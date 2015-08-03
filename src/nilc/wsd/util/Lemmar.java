package nilc.wsd.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import lematizador.Main;

public class Lemmar {

	/**
	 * Retorna a forma canônica de uma palavra. Este método utilizada a bibliotca disponível 
	 * em {@link http://www.icmc.usp.br/~taspardo/LematizadorV2a.rar}, que faz uso do UniTex
	 * 
	 * @param word Palavra a ser desambiguada
	 * @param tag Etiqueta morfossintática da palavra
	 * 
	 * @return A forma canônica da palavra, caso exita no dicionṕairo unitex
	 * 
	 * @throws IOException Uma exceção é lançada caso o arquivo do dicionário não seja encontrado. Neste caso, retorna-se a palavra enviada como parâmetro
	 */
	public static String lemma( String word , String tag) throws IOException{
		
		//Salvando a saída padrão do sistema		
		OutputStream out_strem = System.out;
		String lemma = word;
		try{		
			
			//O lematizador utilizado funciona apenas por linha de comando
			//Assim, para utiliza-lo como uma ferramenta, deve-se realizar estes 
			//procedicmentos para alterar a saída padrão (console) do sistema 
			//para um arquivo, permitindo posterior leitura dos resultados
			File out = new File("lib" + File.separator + "word_lemar.txt");						
			System.setOut( new PrintStream( out ));			
			//Chamando o lematizador
			Main.main( new String[]{ word , tag } );			
			BufferedReader reader = new BufferedReader( 
					new InputStreamReader( new FileInputStream( out ), "UTF-8") );
			
			//O lematizador possui o seguinte padrão de saída: palavra/forma_canônica
			lemma = reader.readLine().split("/")[ 1 ];
			
		}catch (IOException e) {
			e.printStackTrace();
			return word;
		}
		
		
		//Retornando a saída padrão do programa
		System.setOut( (PrintStream) out_strem );
			
		return lemma;		
	}
}
