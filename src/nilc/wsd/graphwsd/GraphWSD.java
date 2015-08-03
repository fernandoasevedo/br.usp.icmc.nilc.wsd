package nilc.wsd.graphwsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import nilc.wsd.graphwsd.build.Build;
import nilc.wsd.graphwsd.build.edgeBuild.EdgeBuilder;
import nilc.wsd.graphwsd.graph.Edge;
import nilc.wsd.graphwsd.graph.Graph;
import nilc.wsd.graphwsd.graph.Vertice;
import nilc.wsd.mxpost.Mxpost;
import nilc.wsd.util.Lemmar;
import nlputil.Tokenizer;
import nlputil.stoplist.StopListEn;
import nlputil.stoplist.StopListPtBR;
import nlputil.translate.Translator;
import nlputil.wordnet.WordNet;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;

/**
 * Classe que encapsula métodos para aplicação de um algorimto de desambiguação 
 * por meio de Redes de Coocorrência lexical, direcioando ao cenário multidocumento.
 * 
 * Este método representa o contexto das palavras por meio de uma rede complexa e emprega
 * uma adaptação do algoritmo de Lesk (vide Clase LeskUtil para mais informações) no processo 
 * de dessambiguação.
 * 
 * O resultado da avaliação deste trabalho são apresentados em:
 * Nóbrega, F.A.A. and Pardo, T.A.S. (2013). Desambiguação Lexical de Sentido com uso
 * de Informação Multidocumento por meio de Redes de Co-ocorrência. In the Proceedings
 * of the 9th Brazilian Symposium in Information and Human Language Technology – STIL, 
 * pp. 138-147. October 21-23, Fortaleza/Brazi
 * 
 * @author fernandoasevedo
 *
 */
public class GraphWSD {
	
	private Graph<Word, Integer, Integer, Integer> graph; /* Rede de coocorrência lexical */
	private Translator translate; /* Tradutor */	
	private StopListEn en_stoplist; /* Lista de stopwords para o inglês */
	private StopListPtBR br_stoplist; /* Lista de stopwords para o português */
	
	
	public GraphWSD( Translator translate ){
		this.translate = translate;
		this.en_stoplist = new StopListEn();
		this.br_stoplist = new StopListPtBR();
		
		//Carregando o cache de tradução
		translate.loadSavedCache();
	}
	
	
	/**
	 * Algoritmo de pré-processamento do texto, que consiste em:
	 * <ul>
	 * 	<li>Tokenização</li>
	 *  <li>Etiquetagem morfossintática</li>
	 *  <li>Remoção de stop-words e palavras cujo etiquetas não são aceitas na WordNet</li>
	 *  <li>Recuperação de informação para construção de rótulos das palavras (tradução, synsets)</li>
	 * </ul>
	 * 
	 * @param input Texto de entrada do algorimto
	 * @return Uma {@link ArrayList} contendo {@link Words} que encapsulam as palavras pré-processadas
	 * @throws Exception Este método faz uso de alguns recursos em arquivo. Assim, podem ser lançadas exceções na manipulação dos mesmos
	 */
	public ArrayList<Word> preProcessing( String input ) throws Exception{
		
	
		//Tokenização da entrada
		ArrayList<String> tokens = Tokenizer.tokenizer( input );
		StringBuilder buffer = new StringBuilder();
		for( String t : tokens ){
			buffer.append( t ); buffer.append(" ");
		}
		
		//Etiquetagem morfossintica das palavras
		String input_tag[] = Mxpost.tagger( buffer.toString() ).split(" ");
		ArrayList<Word> words = new ArrayList<Word>();
		SynsetType synset_type;
		
		//Processameto das palavras conforme etiqueta morfossintática
		for( String w_t : input_tag ) {			
			String w[] = w_t.split("_");		

			Word new_word = null;
			//Verifica se esta etiqueta é valida
			if( ( synset_type = WordNet.mapSynset( w[ 1 ] ) )!= null ){
				
				if( !br_stoplist.isStopWord( w[ 0 ] ) && Character.isLetter( w[ 0 ].charAt( 0 ) ) ){

					String processed_word = Lemmar.lemma(w[ 0 ], w[ 1 ] ).toLowerCase();

					new_word = new Word( w[ 0 ], processed_word, w[ 1 ], true );

					//Inserindo informações para os rótulos
					//new_word.translates = translate.translateWord(processed_word );					
					new_word.translates = this.translate.translateWord( processed_word, w[ 1 ]);
					for( String t : new_word.translates )
						for ( Synset s : WordNet.getSynonyms( t , synset_type) )
							//LeskUtil.buildLabel( new_word , s);
							new_word.synsets.add( s );
				}
			}
			
			if( new_word == null )
				new_word = new Word( w[ 0 ],w[ 0 ].toLowerCase(), w[ 1 ], false );
			
			
			words.add(  new_word );
		}

		return words;
	}
	
		
	/**
	 * Constrói um grafo cujo arestas são mapeadas conforme um construtor de arestas passado como parâmetro
	 * 
	 * @param input um {@link ArrayList}, onde cada posição há um texto de entrada. Estes testes são tratados 
	 * dentro de uma mesma coleção 
	 * @param window_size tamanho da janela que será usada no texto. No grafo, isso pode ser interpretado como k-vizinhos
	 * @param edge_builder um {@link EdgeBuilder} que representa como as arestas serão criadas
	 * @throws Exception Pode-se lançar exeções na etapa de pré-processamento do texto de entrada
	 */
	public ArrayList<Word> disambiguateWithWindow( ArrayList<String> input, int window_size , EdgeBuilder edge_builder ) throws Exception{
		
		
		ArrayList<Word> disambiguate_word = new ArrayList<Word>();
		
		this.graph = new Graph<Word, Integer, Integer, Integer>();
		ArrayList<Word> words = new ArrayList<Word>();
		for( String s : input ){
			words.addAll( preProcessing( input.get( 0 ) ) );
			words.add( null ); //marcador de fim de aquivo
		}
				
		//Construção da rede de coocorrência lexical
		Build.buildWindowGraph( graph, words , window_size , edge_builder);
		
		WeightEdgeComparator comparator = new WeightEdgeComparator();
		Word neighbors[] = new Word[ window_size ];
		ArrayList<String> vizinhos = new ArrayList<String>();
		
		
		//Desambiguação das palavras
		for( Vertice<Word, Integer> v : graph.getVertices() ){
			
			disambiguate_word.add( v.getValue() );
			
			if( v.getValue().use_word ) {
			
				//Apenas substantivos comuns são desambiguados
				if( v.getValue().tag.compareTo("N") == 0){
				
					vizinhos.clear();
					LinkedList<Edge> edges = v.edges();					
					Collections.sort( edges ,  comparator );
					
					int count = 0;
					for( Edge<Integer, Integer> e : edges ){
	
						if( count < window_size ){
							if( e.getV().equals( v ) ) neighbors[ count ] =  (Word) e.getU().getValue();
							else neighbors[ count ] =  (Word) e.getV().getValue();
						}					
						vizinhos.add( ((Word) e.getU().getValue()).processed_word );
					}
					
					//Desambiguação da palavra
					v.getValue().synset = leskWsd( v.getValue(), neighbors );					
					
				}
			}		
		}		 
		
		return disambiguate_word;
	}
	
	
	private String print( Word words[] ){
		
		String ouput = "";
		for( Word w : words )
			if( w == null ) break;
			else ouput+= " " +w.processed_word;		
			
			
		return ouput;
	}
	
	/**
	 * Método de desambiguação 
	 * 
	 * @param target Palavra a ser desambiguada
	 * @param context Contexto da palavra, ou seja, palavras que mais coocorreram no grafo
	 * @return Um sysnet que melhor foi ranqueado pelo algoritmo
	 */
	public Synset leskWsd(Word target, Word[] context ) {
		
		if( target.synsets.isEmpty() )
			return null;
		
		int values[] = new int[ target.synsets.size() ];
		
		int max_value=0, synset_index = 0;
		
		ArrayList<String> synset_label; 
		for( int index=0; index < values.length; index++ ){

			//Rótulo do synset da palavra alvo
			synset_label = Tokenizer.tokenizer( target.synsets.get( index ).getDefinition() );
			this.en_stoplist.removeStopWord( synset_label );
			
			int sum = 0;
			for( Word context_word : context )
				if( context_word != null )
					sum+= LeskUtil.comumWords( synset_label.toString(), context_word.translates.toString() );
			
			if( sum > max_value ){
				max_value = sum;
				synset_index = index;
			}
		}
		
		return target.synsets.get( synset_index );
	}

	
	private class WeightEdgeComparator implements Comparator<Edge>{
		@Override
		public int compare(Edge e1, Edge e2) {
			
			//TODO e ser forem iguais?
			return (Integer)e2.getRelation() - (Integer) e1.getRelation();
		}
		
	}
}
 