package nilc.wsd.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Classe que encapsula métodos para registrar resultados de experimentos em arquivo
 * 
 * @author fernandoasevedo
 *
 */
public class ResultPrinter {
	private static File file_result;
	private static BufferedWriter writer;
	
	/**
	 * Cria um novo arquivo para armazenar resultados
	 * @param result_name uma {@link String} contendo o nome do arquivo que será criado
	 * @throws IOException
	 */
	public static void createNewResultFile( String result_name ) throws IOException{
		file_result = new File("result" + File.separator+ result_name + ".csv") ;
		file_result.createNewFile();
	}

	/**
	 * Abre o arquivo onde será registrado os resultados
	 * @throws IOException
	 */
	public static void openResultFile() throws IOException{
		
		System.out.println( ">" + file_result.getName() );		
		writer = new BufferedWriter( 
				new FileWriter( file_result , true )
				);
	}

	/**
	 * Adiciona uma linha ao arquivo aberto anteriormente
	 * 
	 * @param line representa o conteúdo que será adicionado
	 * @throws IOException
	 */
	public static void addLine( String line ) throws IOException {		
		System.out.println( line );		
		writer.write( line );
		writer.newLine();
	}
	
	
	/**
	 * Salva os dados registrados em disco e fecha o arquivo
	 * @throws IOException
	 */
	public static void saveResult() throws IOException{		
		System.out.println(  file_result.getName() +"<<<\n\n");
		writer.flush();
		writer.close();
		
	}
}
