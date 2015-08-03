package test;

import static org.junit.Assert.*;

import java.util.ArrayList;

import nilc.wsd.graphwsd.build.Build;
import nilc.wsd.graphwsd.graph.Graph;

import org.junit.Before;
import org.junit.Test;

public class BuildTest {

	private Build build;
	private ArrayList<String> first;
	private Graph<String, Integer, Integer, Integer> graph;
	
	@Before
	public void before(){
		build = new Build();
		first = new ArrayList<String>();
		first.add("a");
		first.add("b");
		first.add("c");
		first.add("d");
	}
	
	@Test
	public void testBuildSequenceGraph() {
		System.out.println( "\ntestBuildSequenceGraph" );
		System.out.println( first );
		//graph = build.buildSequenceGraph( first );		
		System.out.println( graph );
		
	}

	@Test
	public void testBuildWindowGraph() {
				
		System.out.println( "\ntestBuildWindowGraph [ 1 ] " );
		System.out.println( first );
		//graph = build.buildWindowGraph( first, 1 );		
		System.out.println( graph );
		
		System.out.println( "\ntestBuildWindowGraph [ 2 ] " );
		System.out.println( first );
		//graph = build.buildWindowGraph( first,2 );		
		System.out.println( graph );
		
		System.out.println( "\ntestBuildWindowGraph [ 3 ] " );
		System.out.println( first );
		//graph = build.buildWindowGraph( first, 3);		
		System.out.println( graph );
	}

}
