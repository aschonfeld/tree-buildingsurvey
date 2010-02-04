import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.TreeMap;

public class Graph implements Renderable {
	
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private TreeMap<Integer, Vertex> idToVertex;
	
	Graph() {
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		idToVertex = new TreeMap<Integer, Vertex>();
	}
	
	public void addVertex(int id, Vertex v) {
		System.out.println("ADDED VERTEX " + v);
		vertices.add(v);
		idToVertex.put(new Integer(id), v);
	}
	
	public void addEdge(Edge e) {
		System.out.println("ADDED EDGE " + e);
		edges.add(e);
		e.getV1().addEdge(e.getV2());
	}
	
	public Vertex getVertexByID(int id) {
		return idToVertex.get(new Integer(id));
	}
	
	public void render(Graphics g, Point offset) {
		for(Vertex v: vertices) {
			v.render(g, offset);
		}
		for(Edge e: edges) {
			e.render(g, offset);
		}
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
