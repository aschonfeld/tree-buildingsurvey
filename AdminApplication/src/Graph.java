import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;

public class Graph implements Renderable {
	
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private TreeMap<Integer, Vertex> idToVertex;
	private boolean directional = true;
	
	Graph() {
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		idToVertex = new TreeMap<Integer, Vertex>();
	}
	
	public void addVertex(int id, Vertex v) {
		//System.out.println("ADDED VERTEX " + v);
		vertices.add(v);
		idToVertex.put(new Integer(id), v);
	}
	
	public void addEdge(Edge e) {
		//System.out.println("ADDED EDGE " + e);
		edges.add(e);
		e.getV1().addTo(e.getV2());
		e.getV2().addFrom(e.getV1());
	}
	
	public boolean containsCycle() {
		for(Vertex v: vertices) v.setMark(Vertex.Mark.WHITE);
		for(Vertex v: vertices) {
			if(v.getMark() == Vertex.Mark.WHITE) {
				if(visit(v)) return true;
			}
		}
		return false;
	}

	private boolean visit(Vertex v) {
		v.setMark(Vertex.Mark.GREY);
		for(Vertex v2: v.getAdjVertices()) {
			if (v2.getMark() == Vertex.Mark.GREY) {
				return true;
			} else if(v2.getMark() == Vertex.Mark.WHITE) {
				if (visit(v2)) return true;
			}
		}
		v.setMark(Vertex.Mark.BLACK);
		return false;
	}

	public Vertex getVertexByID(int id) {
		return idToVertex.get(new Integer(id));
	}
	
	public String getInfo() {
		StringBuffer sb = new StringBuffer();
		for(Vertex v: vertices) {
			sb.append("START:" + v + "\n");
			for(Vertex from: v.getFrom()) {
				sb.append("    FROM: " + from.toString() + "\n");
			}
			for(Vertex to: v.getTo()) {
				sb.append("    TO: " + to.toString() + "\n");
			}
		}
		return sb.toString();
	}
	
	public void render(Graphics g, Point offset) {
		for(Vertex v: vertices) {
			v.render(g, offset);
		}
		for(Edge e: edges) {
			e.render(g, offset);
		}
	}


	public boolean allOrganismsTerminal()	
	{
		for(Vertex v: vertices) {
			if(v.getType() == Vertex.Type.ORGANISM) {
				if(!v.isTerminal(directional)) return false;
			}
		}
		return true;
	}
	
	public boolean hasSingleCommonAncestor()
	{
		return true;
	}

	public boolean groupsAreLabelled()
	{
		return true;
	}

	public boolean includesAllOrganisms()
	{
		return true;
	}


	public boolean hasBranches()
	{
		return true;
	}

	public int hierarchy()
	{
		return 2;
	}

	public float groupingVertebrates()
	{
		return 2/3;
	}

	public float groupingInvertebrates()
	{
		return 1/2;
	}

	public float groupingMammals()
	{		
		return 4/5;
	}

	public float groupingNonmammals()
	{
		return 7/8;
	}

	

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for(Vertex v: vertices) {
			sb.append(v.toString() + "\n");
		}
		for(Edge e: edges) {
			sb.append(e.toString() + "\n");
		}
		return sb.toString();
	}
	
}
