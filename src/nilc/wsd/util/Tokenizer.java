package nilc.wsd.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class Tokenizer {
	private static String buffer;
	private static int type;
	
	/**
	 * Tokeniza um arquivo de origem (in) para um arquivo de destino (out)
	 * 
	 * @param in um {@link File} que será tokenizado
	 * @param out um {@link File} onde será armazenado o resultado 
	 * @throws IOException 
	 */
	public static void tokenizer( File in, File out) throws IOException{
		
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream( in ), "utf-8" ) );
		
		BufferedWriter writer = new BufferedWriter(
				new OutputStreamWriter( 
						new FileOutputStream( out ), "utf-8"));
		
		//Tokeniza-se uma linha por vez do arquivo
		String line;
		ArrayList<String> tokens;
		while( ( line = reader.readLine() ) != null ){
			
			tokens = tokenizer( line );
			
			for( String t : tokens )
				writer.write( t + " ");
			writer.write("\n");
		}
		
		reader.close();
		writer.close();
	}
	
	/**
	 * Tokeniza a {@link String} text, passada como parâmetro, e 
	 * retonra uma {@link ArrayList} com estes tokens 
	 * @param text a {@link String} que será tokenizada
	 * @return um {@link ArrayList} com os tokens
	 */
	
	/**
	 * Tokeniza a {@link String} text, passada como parâmetro, e 
	 * retonra uma {@link ArrayList} com estes tokens 
	 * @param text a {@link String} que será tokenizada
	 * @return um {@link ArrayList} com os tokens
	 */
	public static ArrayList<String> tokenizer( String text ) {
		
		ArrayList<String> tokens = new ArrayList<String>();
		
		int index = 0;
		type = 0;
		char c;
		buffer = "";
		
		while( index < text.length() ){
			
			c = text.charAt( index );
			
			if( Character.isLetter( c ) ){
				
				//Verificando se pode colocar uma letra no buffer
				if( type != 0 && type != 1 && type != 2 )
					add( tokens );

				if ( type == 0 ) type = 1;
				
				buffer+= "" + c;
				index++;
				continue;
			}
			
			if( c == '-' && type == 1){
				
				if ( nextIsLetter( text, index +1) ){
					buffer+= "" + c;
					type = 2;
				}else{
				
					add( tokens );
				
					buffer = "-";
					add( tokens );				
				}
				
				index++;				
				continue;
			}
			
			if( Character.isDigit( c ) ){
				//Verificando se pode colocar um dígito no buffer
				if( type != 0 && type != 3 && type != 4)
					add( tokens );
			
				if ( type == 0 ) type = 3;
				
				buffer+= "" + c;
				index++;				
				continue;
			}
			
			if ( c == '.' || c == ','){
				
				if ( nextIsDigit( text, index + 1 ) ){
					buffer+= "" + c;
					type = 4;
				}else{
					
					add( tokens );
					
					buffer = "" + c; 
					add( tokens );				
				}
				
				index++;
				continue;
			}
			
			if ( !Character.isSpaceChar( c ) && c != '\t' && c != '\n'){
				add( tokens );				
				buffer = "" + c;				
			}
			
			add( tokens );			
			index++;
		}
		
		return tokens;
	}

	/** 
	 * @param text {@link String} que será verificada
	 * @param index posição de text que será verificada
	 * @return true, caso o caracter da posição index seja um digito
	 */
	private static boolean nextIsDigit(String text, int index) {
		
		if( index < text.length() )
			return Character.isDigit( text.charAt( index) );
		
		return false;
	}

	/**
	 * Adiciona o tokem armazenada no buffer no {@link ArrayList}
	 * tokens, passado como parâmetro
	 */
	private static void add(  ArrayList<String> tokens) {
		
		type = 0;
		if ( !buffer.isEmpty())
			tokens.add( buffer );
		
		buffer = "";
	}

	/** 
	 * @param text {@link String} que será verificada
	 * @param index posição de text que será verificada
	 * @return true, caso o caracter da posição index seja uma letra
	 */
	private static boolean nextIsLetter(String text, int index) {
		
		if( index < text.length() )
			return Character.isLetter( text.charAt( index) );
		
		return false;
		
	}
		
	
	
}
