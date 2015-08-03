package nilc.wsd.graphwsd.build;

import java.util.ArrayList;

import nilc.wsd.graphwsd.Word;
import nilc.wsd.graphwsd.build.edgeBuild.EdgeBuilder;
import nilc.wsd.graphwsd.graph.Graph;
import nilc.wsd.graphwsd.graph.Vertice;

/**
 * Classe que encapsula métodos para construção de Redes de Coocorrência lexical 
 * 
 * @author fernandoasevedo
 *
 */
public class Build {

	/**
	 * Constrói grafos de coocorrência lexical sequências, ou seja, sem analisar janela de palavras, 
	 * as arestas são criadas sempre de palavras vizinhas, da esquerda para a direita
	 * 
	 * @param words
	 */
	public static Graph<Word, Integer, Integer, Integer> buildSequenceGraph( ArrayList<Word> words  ){
		
		Graph<Word, Integer, Integer, Integer> graph = new Graph();
		String last =null;
		Vertice<Word, Integer> actual = null;
		
		for( Word w : words ){
			
			//Verifica se o vertice com a palavra w já foi adicionado no grafo
			if( ( actual = graph.get( w.getWord() ) ) == null ){
				graph.addVertice( w.getWord(),  w );
				actual = graph.get( w.getWord() ); 
			}
			
			//cria aresta entre vertice atual e o ultimo vertice adiconado
			if ( last != null )
				graph.addEdge( last, w.getWord() , false, 1);	
				
			last = w.getWord();
		}
		
		return graph;
	}
	
	/**
	 * Constrói um grafo de co-ocorrência entre palavras, adiciando arestas entre palavras dentro de uma 
	 * mesma janela (também pode ser interpretado como grafo de k-vizinhos, onde k é o tamnaho da janela)
	 * 
	 * @param words um {@link ArrayList} contendo as palavras que comporão o grafo
	 * @param windows_size o tamnaho da janela, ou valor do k na interpretação de grafo de k vizinhos
	 */
	public static void buildWindowGraph(
			Graph<Word, Integer, Integer, Integer> graph,
			ArrayList<Word> words, int window_size ){
		
		ArrayList<String> window = new ArrayList<String>( window_size ); 
		Vertice<Word, Integer> actual = null;
		
		for( Word w : words ){
			
			//Verifica se o vertice com a palavra w já foi adicionado no grafo
			if( ( actual = graph.get( w.getWord() ) ) == null ){
				graph.addVertice( w.getWord() , w);
				actual = graph.get( w.getWord() ); 
			}
			
			//cria aresta entre vertice atual e o ultimo vertice adiconado
			for( String v : window )
				graph.addEdge( v, w.getWord(), false, 1);	

			if( window.size() == window_size )
				window.remove( 0 );
			
			window.add( w.getWord() );
		}
				
	}

	public static void buildWindowGraph(
			Graph<Word, Integer, Integer, Integer> graph,
			ArrayList<Word> words, int window_size, EdgeBuilder edge_builder) {
				
		ArrayList<String> window = new ArrayList<String>( window_size );
		Vertice<Word, Integer> actual = null;
		
		int text_count = 1;
		
		for( Word w : words ){
			 
			
			if( w == null ){
				text_count++;
				
			}else{
				
				if( w.use_word ){
				
					//Verifica se o vertice com a palavra w já foi adicionado no grafo
					if( ( actual = graph.get( w.getWord() ) ) == null ){
						graph.addVertice( w.getWord() , w);
						actual = graph.get( w.getWord() ); 
					}
					
					//Cria aresta entre os vertice presentes na janela e o vertice atual
					for( String v : window )				
						edge_builder.edgeBuilde( v,  w.getWord(), graph );
		
					//Retira o vertice que não irá pertecer à próxima janela
					if( window.size() == window_size )
						window.remove( 0 );
					
					//Adiciona o novo vértice da janela
					window.add( w.getWord() );
				}
			}
		}
		
	}
}
