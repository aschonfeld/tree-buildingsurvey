import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Area;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Graph implements Renderable {
	
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private TreeMap<Integer, Vertex> idToVertex;
	private boolean directional = true;
	private boolean allOrgsInTree;
	private String studentName;
	private int path[][] = null; // length of shortest path from x->y = path[x][y]
	private String[] pathIndexNames = null;
	private int unconnected = 99;

	Graph(String studentName) {
		this.studentName=studentName;
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		idToVertex = new TreeMap<Integer, Vertex>();
	}
	
	public void addVertex(int id, Vertex v) {
		v.setIndex(vertices.size());
		vertices.add(v);
		idToVertex.put(new Integer(id), v);
		//System.out.println("ADDED VERTEX " + v);
	}
	
	public void addEdge(Edge e) {
		//System.out.println("ADDED EDGE " + e);
		if((e.getV1() == null) || (e.getV2() == null)) {
			System.out.println("Error loading tree: " + studentName);
			return;
		}
		edges.add(e);
		e.getV1().addTo(e.getV2());
		e.getV2().addFrom(e.getV1());
	}
	
	public boolean containsCycle() 
	{
		for(Vertex v: vertices) v.setMark(Vertex.Mark.WHITE);
		for(Vertex v: vertices) 
			if(v.getMark() == Vertex.Mark.WHITE) 
				if(visit(v)) return true;
		return false;
	}


	public void buildDescendantList(Vertex v)
	{
		if (v.visited) return;
		v.visited = true;
		v.addDescendants(v.getToVertices());
		for (Vertex c: v.getToVertices())
		{
			buildDescendantList(c);
			v.addDescendants(c.getDescendants());
		}
		for (Vertex p:v.getFromVertices())
		{
			buildDescendantList(p);
		}
	}

	public void buildDescendantList()
	{
		//Doesn't matter where we start, so start at first vertex in the list
		buildDescendantList(vertices.get(0));
	}

	public void buildAncestorList(Vertex v)
	{
		if (v.visited) return;
		v.visited = true;
		v.addAncestors(v.getFromVertices());
		for (Vertex c: v.getFromVertices())
		{
			buildAncestorList(c);
			v.addAncestors(c.getDescendants());
		}
		for (Vertex p:v.getToVertices())
		{
			buildAncestorList(p);
		}
	}

	public void buildAncestorList()
	{
		//Doesn't matter where we start, so start at first vertex in the list
		buildAncestorList(vertices.get(0));
	}


	public void showDescendants()
	{
	
		for (Vertex v:vertices)
		{
			System.out.print(v.getName()+": ");
			for (Vertex d:v.getDescendants())
				System.out.print(d.getName()+": ");
			System.out.println();
		}


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
	
	public void render(Graphics g, Point offset)
	{
		for(Vertex v: vertices) {
			v.render(g, offset);
		}
		for(Edge e: edges) {
			e.render(g, offset);
		}
	}

	public void	printReport()
	{
		System.out.println("------------ \nNext Graph:\n------------");
		System.out.println("All Organisms Terminal: " +
			allOrganismsTerminal());

		System.out.println("Tree has single common ancestor: " +
			 hasSingleCommonAncestor());

		System.out.println("Tree includes all Organisms: " +
			includesAllOrganisms());

//		System.out.println("Tree has branches: " + hasBranches());
//		System.out.println("Groups are Labelled: " + groupsAreLabelled());

//		System.out.println("Degree of hierarchy: " + hierarchy());
			
//		System.out.println("Vertebrates grouped: "+ groupingVertebrates());
			
//		System.out.println("Invertebrates grouped: " +
//			groupingInvertebrates());

		
//		System.out.println("Mammals grouped: " + groupingMammals());
		
//		System.out.println("Non-mammals grouped " + groupingNonmammals());
		System.out.println("----------------");	
	}


	public boolean allOrganismsTerminal()	
	{
		for(Vertex v: vertices) {
			if(v.getType() == VertexInfo.VertexType.ORGANISM) {
				if(!v.isTerminal(directional)) return false;
			}
		}
		return true;
	}
	
	public boolean hasSingleCommonAncestor()
	{
		int commonAncestors=0;
		for (Vertex v: vertices)
		{
			ArrayList <Vertex> checklist = new ArrayList<Vertex>();
			checklist.addAll(vertices);
			checklist.remove(v);
			if ( (v.getFromVertices().size() == 0)  &&
				  (v.getDescendants().containsAll(checklist)) )
			{		
				commonAncestors++;
			}
		}
		return (commonAncestors==1);
	}

	public boolean groupsAreLabelled()
	{
		return true;
	}

    public boolean includesAllOrganisms() {
        int organismCounter = 0;
        for(Vertex v: vertices) {
            if(v.getType() == VertexInfo.VertexType.ORGANISM) organismCounter++;
        }
        if(organismCounter == 20) {
            allOrgsInTree =true;
        } else {
            allOrgsInTree =false;
        }
        return allOrgsInTree;
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

	public String getStudentName()
	{
		return studentName;
	}
	
	public int[][] getShortestPaths() {
		if(path == null) {
			runFloydWarshall();
		}
		return path;
	}
	
	public String[] getPathIndexNames() {
		if(path == null) {
			runFloydWarshall();
		}	
		return pathIndexNames;
	}
	
	private void runFloydWarshall() {
		int numVertices = vertices.size();
		path = new int[numVertices][numVertices];
		pathIndexNames = new String[numVertices];
		for(int x = 0; x < numVertices; x++) {
			for(int y = 0; y < numVertices; y++) {
				if(y == x) {
					path[x][x] = 0;
					continue;
				}
				// if use Integer.MAX_VALUE, addition will cause an overflow
				path[x][y] = unconnected;
			}
		}
		int nameIndex = 0;
		for(Vertex from: vertices) {
			pathIndexNames[nameIndex] = from.getName();
			for(Vertex to: from.getAdjVertices(false)) {
				//System.out.print(from.getIndex() + "|" + to.getIndex() + " ");
				path[from.getIndex()][to.getIndex()] = 1;
			}
			nameIndex++;
		}
		for(int k = 0; k < numVertices; k++) {
			for(int i = 0; i < numVertices; i++) {
				for(int j = 0; j < numVertices; j++) {
					path[i][j] = Math.min(path[i][j], path[i][k] + path[k][j]);
				}
			}
		}
		for(int i = 0; i < numVertices; i++) {
			for(int j = 0; j < numVertices; j++) {
				//System.out.println(i + "|" + j + "=" + path[i][j]);
			}
		}
	}
	
	public boolean checkConvexHullCollision(){
		Map<String, List<Vertex>> typeVertices = new HashMap<String, List<Vertex>>();
		for(Vertex v : vertices){
			if(VertexInfo.VertexType.ORGANISM.equals(v.getType())){
				if(typeVertices.containsKey(v.getInfo().getType()))
					typeVertices.get(v.getInfo().getType()).add(v);
				else{
					List<Vertex> temp = new LinkedList<Vertex>();
					temp.add(v);
					typeVertices.put(v.getInfo().getType(), temp);
				}
			}
		}
		List<ConvexHull> hulls = new LinkedList<ConvexHull>();
		for(Map.Entry<String, List<Vertex>> e : typeVertices.entrySet())
			hulls.add(new ConvexHull(2, e.getValue()));
		for(int i1=0;i1<hulls.size();i1++){
			for(int i2=hulls.size()-1;i2>i1;i2--){
				Area intersect = new Area(); 
				intersect.add(new Area(hulls.get(i1).hullShape)); 
				intersect.intersect(new Area(hulls.get(i2).hullShape)); 
				if (!intersect.isEmpty())
					return true;
			}
		}
		return false;
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
