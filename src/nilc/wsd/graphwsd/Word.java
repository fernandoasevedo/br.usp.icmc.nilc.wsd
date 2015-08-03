package nilc.wsd.graphwsd;

import java.util.ArrayList;

import edu.smu.tspell.wordnet.Synset;

/**
 * Classe que abstrai um token dos textos ou strings a serem processeadas 
 * Estes tokens podem ser desambiguados, utilizados como contexto, ou descartados no processo de desambiguação, porém mantidos para formatação 
 * do texto 
 * 
 * @author fernandoasevedo
 *
 */
public class Word {
	
	protected String orginal_word; /* palavra original */
	protected String processed_word; /* palavra processada (etiquetada, lematizada)*/
	protected String tag; /* etiqueta morfossintática */
	public boolean use_word; /*Se true, o token é utilizado no processo de desambiguação */
	public Synset synset; /* Synset selecioando como correto */
	public ArrayList<Synset> synsets; /* Lista de possíveis synsets */
	public ArrayList<String> translates; /* Lista de possíves traduções */
	public ArrayList<String> glosa_label; /* Um conjunto de palavras estraídas das glosas dos possíves sysnets */ 
	public ArrayList<String> sample_label; /* Um conjunto de palavras extraídas dos exemplos dos possíveis synsets */
	
	public Word( String origal_word, String word, String tag, boolean use){
		this.orginal_word = origal_word;
		this.processed_word = word;
		this.tag = tag;
		this.use_word = use;
		this.synset = null;
		this.synsets = new ArrayList<Synset>();
		this.translates = new ArrayList<String>();
		this.glosa_label = new ArrayList<String>();
		this.sample_label = new ArrayList<String>();
		
	}

	public String getWord() {
		return this.processed_word;
	}
	
	@Override
	public String toString() {
		
		StringBuilder list_translates = new StringBuilder();		
		if( this.synset != null )
			for( String s : this.synset.getWordForms() ){
				list_translates.append( s );
				list_translates.append(',');
			}		
		else
			for(String t : this.translates ){
				list_translates.append( t );
				list_translates.append(',');			
			}
		
		if( list_translates.length() > 1)
			list_translates.deleteCharAt( list_translates.length() -1 );
	
			
		StringBuilder response = new StringBuilder();
		response.append( this.orginal_word );
		response.append( "<" );
		response.append( this.tag );
		response.append( "_" );
		response.append( ( this.synset == null ? "": synset.hashCode() ) );
		response.append(  "_" );
		response.append(  list_translates );
		response.append( ">" );
		
		return response.toString();
				
	}
}

