package nilc.wsd.mxpost;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;

public class Mxpost {

	/**
	 * Este método usa o mxpost ({@link http://www.nilc.icmc.usp.br/nilc/tools/nilctaggers.html})
	 * para anotar um arquivo de entrada, {@link input}, para um arquivo de saída, {@link out}, 
	 * usando um o tag-set passado como argumento 
	 * 
	 * @param input arquivo de entrada
	 * @param out arquivo de saída
	 * @param tag_set arquivo do tag-set
	 * 
	 * @throws IOException Caso haja problemas de leitura ou escrita dos arquivos (input, out ou tag_set ) 
	 */
	public static void tagger_file( File input, File out, File tag_set ) throws IOException{
		
		InputStream input_strem = System.in;
		OutputStream out_strem = System.out;
		
		//Alterando a entrada e saída padrão do sistema, para que o mxpost possa rodar normalmente
		System.setIn( new FileInputStream( input ) );
		System.setOut( new PrintStream( out ));
				
		String tagset[] = { tag_set.getAbsolutePath() };
		tagger.TestTagger.main(  tagset );
		
		//Retornando a entrada e saída padrão do programa
		System.setIn( input_strem );
		System.setOut( (PrintStream) out_strem );
	}

	/**
	 * Este método usa o mxpost ({@link http://www.nilc.icmc.usp.br/nilc/tools/nilctaggers.html})
	 * para anotar uma string de entrada
	 * 
	 * @param input A string a ser etiquetada
	 * @return Uma stirng contendo as palavras com respectivas etiquetas morfossintáticas
	 * 
	 * @throws IOException
	 */
	public static String tagger( String input) throws IOException {
	
		//Arquivo onde será gravado o texto a ser etiquetado
		File input_file = new File("input.txt");
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter( 
						new FileOutputStream( input_file ), "utf-8")
				);
		
		//Escrevendo no arquivo
		writer.write( input );
		writer.close();
		
		//Arquivo de saída da etiquetação
		File out_file = new File("output.txt");
		
		//Etiquetando o arquivo
		tagger_file( input_file, out_file, new File("lib/port") );
		
		//Lendo o arquivo e retornando a string
		BufferedReader reader = new BufferedReader( 
			new InputStreamReader(
					new FileInputStream( out_file), "utf-8" )
				);
		
		StringBuilder out_string = new StringBuilder();
		String line = "";
		while( ( line = reader.readLine() ) != null )
			out_string.append( line );
		
		return out_string.toString();
	}
	
	/**
	 * Este método usa o mxpost ({@link http://www.nilc.icmc.usp.br/nilc/tools/nilctaggers.html})
	 * para etiquetar morfossintática uma lista de tokens
	 * 
	 * @param tokens Um {@link ArrayList} de tokens a serem etiquetados
	 * @return Uma stirng contendo as palavras com respectivas etiquetas morfossintáticas
	 * 
	 * @throws IOException
	 */
	public static String tagger( ArrayList<String> tokens) throws IOException {
		
		//Arquivo onde será gravado o texto a ser etiquetado
		File input_file = new File("input.mxpost");
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter( 
						new FileOutputStream( input_file ), "utf-8")
				);
		
		//Escrevendo no arquivo
		for( String t : tokens)
			writer.write( t +" ");
		writer.close();
		
		//Arquivo de saída da etiquetação
		File out_file = new File("output.mxpost");
		
		//Etiquetando o arquivo
		tagger_file( input_file, out_file, new File("lib/port") );
		
		//Lendo o arquivo e retornando a string
		BufferedReader reader = new BufferedReader( 
			new InputStreamReader(
					new FileInputStream( out_file), "utf-8" )
				);
		
		StringBuilder out_string = new StringBuilder();
		String line = "";
		while( ( line = reader.readLine() ) != null ){
			System.out.println( line );
			out_string.append( line );
		}
		
		return out_string.toString();
	}
}
