package nilc.wsd.graphwsd.build.edgeBuild;

import nilc.wsd.graphwsd.graph.Graph;
import nilc.wsd.graphwsd.graph.Vertice;

public interface EdgeBuilder<Value, Tag, Relation, Mark> {

	public void edgeBuilde( String u_id, String v_id, Graph<Value, Tag, Relation, Mark> graph); 
}
