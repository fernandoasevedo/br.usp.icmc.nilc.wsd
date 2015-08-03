package nilc.wsd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import nlputil.translate.bing.BingAuth;
import nlputil.translate.bing.BingTranslator;

import edu.smu.tspell.wordnet.Synset;
import nilc.wsd.graphwsd.GraphWSD;
import nilc.wsd.graphwsd.Word;
import nilc.wsd.graphwsd.build.edgeBuild.WeightEdge;


public class Main {

	public static void main(String[] args) throws Exception {
	
		Scanner aux = new Scanner( System.in );
		String l;
		//while(( l = aux.nextLine()) != null )
		//	System.out.println( l );
		
		//Carregando arquivo de configuração, que possui as chaves de acesso às apis utilizadas (Bing, WordReference)
		HashMap<String, String> conf = readConf("config.conf");
		
		
		/*Carregando parâmetros enviados */
		/*Parâmetros inicializados são os melhores valores, segundo experimentos de 
		 * Nóbrega e Pardo (2013).     Desambiguação Lexical de Sentido com uso de Informação Multidocumento 
		 * por meio de Redes de Co-ocorrência.  In: 9th Brazilian Symposium in Information and Human Language
		 * Technology, 2013, Fortaleza.   Proceedings of the 9th Brazilian Symposium in Information and Human 
		 * Language Technology, 2013. p. 138-147.
		 */		
		int window_size = 3;
		File input_file = null;
		boolean multidocument_mode = false;
		String wordnet_dir = ( 
				"WordNet-3.0"+
				File.separator+"dict" );
		
		for( int i =0; i < args.length; i++ ){
						
			if( args[ i ].compareToIgnoreCase("-in") == 0 ){
				input_file  = new File( args[ ++i ] );
				if( !input_file.exists() )
					throw new IllegalArgumentException("Input file " + input_file.getName() + " not exists ");		
					
				continue;
			}
						
			if( args[ i ].compareToIgnoreCase("-wordnet") == 0 ){
				wordnet_dir = args[ ++i ];
				continue;
			}
				
			if( args[ i ].compareToIgnoreCase("-multi") == 0 )
				multidocument_mode = true;		
		}	

		
		System.setProperty("wordnet.database.dir", wordnet_dir );
		
		
		/* Verificando parâmetros enviados pelo usuário */
		
		if(  input_file == null )
			throw new IllegalArgumentException("Input file not found.\nUse -in for indicate the file");		

	
		GraphWSD wsd = new GraphWSD( 
				new BingTranslator(
						"pt",
						"en",
						BingAuth.accessToken( conf.get("client_id"), conf.get("client_secret"), BingAuth.default_scop )));
				
		WeightEdge<Word, Integer, Integer, Integer> edge_builder = new WeightEdge<Word, Integer, Integer, Integer>();
		
		
		String content_file = readFile( input_file );
		
		if(  multidocument_mode  ){			
			//Chama o método de DLS
			ArrayList<String> input = new ArrayList<String>();
			input.add( content_file );
			save( wsd.disambiguateWithWindow( input, window_size , edge_builder ) );			
			
		}else{
			
			ArrayList<Word> words = wsd.preProcessing( content_file );
			
			/*Percorrendo as palavras*/
			ArrayList<Word> window = new ArrayList<Word>();
			for( int i=0; i < words.size(); i++ ){
				Word w = words.get( i );
				
				if( w.use_word ){
					
					/*Reconhecendo a janela da palavra*/
					int left = i ;
					int right = i;
					while( window.size() < window_size - 1 ){
												
						if( left < 0 && right >= window.size()  )
							break;
						
						left--;
						Word left_word = getWord( words, left );
						if( left_word != null )
							window.add( left_word );
												
						right++;
						Word right_word = getWord( words, right );
						if( right_word != null )
							window.add( right_word );
					}					
					
				}
				
				if( window.size() > window_size -1)
					window.remove( window.size() - 1 );				
				
				w.synset = wsd.leskWsd( w , window.toArray( new Word[ window.size() ] ) );
			}
			
			save( words);
		}
		
	}
	
	private static Word getWord( ArrayList<Word> words, int index ){
		
		Word out = null;
		if( index >= 0 && index < words.size() ){
			out = words.get( index );
			
			if( !out.use_word )
				out = null;
		}
		
		return out;
	}
	
	
	private static void save( ArrayList<Word> words ){		
		for( Word w : words ){			
			System.out.print( w  + " ");
		}
	}
	
	/**
	 * Método auxiliar utilizado para carregar arquivos em uma {@link String}
	 * 
	 * @param file o arquivo ({@link File}) a ser abert 
	 * @return Uma {@link String} contendo todas as linhas do arquivo 
	 * @throws IOException Lança-se exceções em caso de problemas ao abrir o arquivo passado como parâmetro
	 */
	public static String readFile( File file ) throws IOException{
		
		BufferedReader reader = new BufferedReader( new FileReader( new File( file.getAbsolutePath()) ) );
		StringBuilder builder = new StringBuilder();
		String line;
		while( (line = reader.readLine() ) != null ){
			builder.append( line );
			builder.append( System.lineSeparator() );
		}
		
		reader.close();
		return builder.toString();
	}

	public static String printSynset( ArrayList<Synset> synset ){
		
		StringBuilder builder = new StringBuilder();
		for( Synset s : synset ){
			builder.append("[ ");		
			for( String word : s.getWordForms() ){
				builder.append(word);
				builder.append(" ");
			}
			builder.append("] ");	
		}
		
		return builder.toString();
		
	}
	
	public static HashMap<String, String> readConf( String conf_file_name ) throws IOException{
		
		
		String conf_content = readFile( new File( conf_file_name ) );
		String lines[] = conf_content.split("\n");
		HashMap<String, String> conf = new HashMap<String, String>();
		for(String l : lines ){
			String split[] = l.split("\t");
			conf.put( split[ 0 ], split[ 1 ]);
		}
		
		return conf;
		
	}
}
