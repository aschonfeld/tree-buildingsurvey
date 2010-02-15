import java.awt.Graphics;
import java.awt.Point;
import java.util.*;

public class Graph implements Renderable {
	
	private ArrayList<Vertex> vertices;
	private ArrayList<Edge> edges;
	private TreeMap<Integer, Vertex> idToVertex;
	private boolean directional = true;
	private boolean allOrgsInTree;
	private String studentName;	

	Graph(String studentName) {
		this.studentName=studentName;
		vertices = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		idToVertex = new TreeMap<Integer, Vertex>();
	}
	
	public void addVertex(int id, Vertex v) {
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
			if(v.getType() == Vertex.Type.ORGANISM) {
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
            if(v.getType() == Vertex.Type.ORGANISM) organismCounter++;
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
