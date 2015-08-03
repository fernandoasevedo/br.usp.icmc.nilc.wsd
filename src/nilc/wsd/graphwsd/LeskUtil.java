package nilc.wsd.graphwsd;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.smu.tspell.wordnet.Synset;

import nlputil.Tokenizer;
import nlputil.stoplist.StopListEn;

/**
 * Classe que encapsula métodos para execução do algoritmo de Lesk, adaptado do trablaho de 
 * Banerjee, S. (2002). Adapting the lesk algorithm for word sense disambiguation to wordnet.
 * Master’s thesis, Department of Computer Science University of Minnesota
 * 
 * Os resultados da avaliação deste método forma apresentados em:
 * Nóbrega, F.A.A. and Pardo, T.A.S. (2012). Explorando Métodos de Uso Geral para Desambiguação Lexical
 * de Sentidos para a Língua Portuguesa. In Anais do 9o Encontro Nacional de Inteligência Artificial -
 * ENIA, pp. 1-12. October 20-25, Curitiba/PR, Brazil.
 *  
 * @author fernandoasevedo
 *
 */
public class LeskUtil {
	
	public static StopListEn stop_list_en = new StopListEn();
	
	/**
	 * Cria os rótulos para uma palavra w. Este rótulo consiste nas palavras 
	 * presentes na definição do synset e nos exemplos do synset. Extraindo-se
	 * stopwords e sinais de pontuação
	 * 
	 * @param w uma {@link Word} que representa a palavra que receberá o rótulo
	 * @param synset o synset que está sendo utilizado
	 */
	public static void buildLabel(Word w, Synset synset) {
		
		ArrayList<String> target = Tokenizer.tokenizer( synset.getDefinition() );
		
		//Montando o label com a glosa
		for( int i=0; i < target.size(); i++ )
			if( isWord( target.get( i ) ) && 
					!stop_list_en.isStopWord( target.get( i ) ) )
				w.glosa_label.add( target.get( i ).toLowerCase() );
		
		//Montando o rótulo com os exemplos
		String samples[] = synset.getUsageExamples();
		for( int i=0; i < samples.length; i++ ){
			
			target = Tokenizer.tokenizer( samples[ i ] );
			
			for( int j=0; j < target.size(); j++ )
				if( isWord( target.get( j ) ) && 
					!stop_list_en.isStopWord( target.get( j ) ) )
					w.sample_label.add( target.get( j ).toLowerCase() );
		}
	}
	
	/**
	 * @param word uma {@link String} que será verificada
	 * @return true, casa word é uma palavra. False, caso word não seja uma palavra
	 */
	public static boolean isWord(String word ) {

		for( int i=0; i < word.length(); i++)
			if( !Character.isLetter( word.charAt( i ) ) && word.charAt( i ) != '-' )
				return false;
		
		return true;
	}
	
	/**
	 * Calcula o número de palavras sobrepostas entre duas strings
	 * 
	 * @param s1 String 1
	 * @param s2 String 2
	 * 
	 * @return O número de palavras presentes em s1 que estão contidas em s2
	 */
	public static int comumWords( String s1, String s2 ){
		
		int count = 0;
		 
		//Tokeniza-se as strings
		ArrayList<String> tokens1 = Tokenizer.tokenizer( s1 );
		ArrayList<String> tokens2 = Tokenizer.tokenizer( s2 );
		
		HashSet<String> hash = new HashSet<String>();
		for( String t : tokens1 ){
			
			//normalização
			t = t.toLowerCase();
			
			//Extração de stopwords
			if ( !stop_list_en.isStopWord( t ) || isWord( t ) )
				if ( !hash.contains( t ) )
					hash.add( t );
		}
		
		for( String t: tokens2 ){
			t = t.toLowerCase();
			if ( hash.remove( t ) )
				count++;
		}
		
		return count;
	}
	
	/**
	 * Calcula o número de palavras sobrepostas entre duas listas de strings
	 * 
	 * @param w1 String 1
	 * @param w2 String 2
	 * 
	 * @return O número de palavras presentes em s1 que estão contidas em s2
	 */
	private int countCommunWords(List<String> w1, List<String> w2) {
		
		int count = 0;
		for( int i =0; i < w1.size(); i++ )			
			if( w1.indexOf( w1.get( i ) ) == i ) //verifica se é a primeira ocorrência da palava w1_i
				if( w2.contains( w1.get( i ).toLowerCase() ) )
					count++;
		
		return count;		
	}
	

}
