//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Point;
import java.awt.geom.Area;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.TBSApplet;
import tbs.TBSUtils;
import tbs.graphanalysis.ConvexHull;
import tbs.graphanalysis.Edge;
import tbs.graphanalysis.Graph;
import tbs.graphanalysis.Vertex;
import tbs.model.admin.Student;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.admin.AnalysisPrompt;
import tbs.view.prompt.admin.RadioQuestionReviewPrompt;
import tbs.view.prompt.admin.WrittenQuestionReviewPrompt;

public class AdminModel extends TBSModel
{
	private WrittenQuestionReviewPrompt writtenQuestionReviewPrompt;
	private RadioQuestionReviewPrompt radioQuestionReviewPrompt;
	private AnalysisPrompt analysisPrompt;
	private List<Student> students;
	private List<ConvexHull> hulls;
	private List<String> hullCollisions;
	private Graph graph;

	public AdminModel(TBSApplet applet,	List<OrganismNode> organisms, List<Student> students) {
		super(applet, organisms);
		this.students = students;
		Student student = this.students.get(0);
		setStudent(student);
		String tree = student.getTree();
		if(!TBSUtils.isStringEmpty(tree)){
			loadTree(tree);
			calculateHullCollisions();
			loadGraph();
		}
		writtenQuestionReviewPrompt = new WrittenQuestionReviewPrompt(this);
		/*
		 * Until Professor White says otherwise we will be eliminating the radio
		 * portion of the open-response
		 * radioQuestionReviewPrompt = new RadioQuestionReviewPrompt(this);
		 */
	}

	public void changeSavedTree(int studentIndex){
		/*
		 * Make sure your don't re-calculate the selected student's
		 * information
		 */
		System.out.println("Selected Index:" + studentIndex);
		if(studentIndex != students.indexOf(getStudent())){
			Student student = students.get(studentIndex);
			setStudent(student);
			String tree = student.getTree();
			resetModel();
			if(!TBSUtils.isStringEmpty(tree)){
				loadTree(tree);
				calculateHullCollisions();
				loadGraph();
			}
			writtenQuestionReviewPrompt = null;
			analysisPrompt = null;
		}
	}

	public List<Student> getStudents(){
		return students;
	}
	
	public void viewPrompt(TBSButtonType buttonClicked) {
		if(TBSButtonType.TREE.equals(buttonClicked))
			clearPrompt();
		else{
			Prompt prompt = null;
			if(TBSButtonType.OPEN_RESPONSE.equals(buttonClicked)){
				if(writtenQuestionReviewPrompt == null)
					writtenQuestionReviewPrompt = new WrittenQuestionReviewPrompt(this);
				prompt = writtenQuestionReviewPrompt;
			}else if(TBSButtonType.ANALYSIS.equals(buttonClicked)){
				if(analysisPrompt == null)
					analysisPrompt = new AnalysisPrompt(this);
				prompt = analysisPrompt;
			}
			setPrompt(prompt);
		}
	}
	
	public void calculateHullCollisions(){
		Map<String, List<Point>> typeVertices = new HashMap<String, List<Point>>();
		for(ModelElement m : inTreeElements()){
			if(m instanceof OrganismNode){
				if(typeVertices.containsKey(((OrganismNode) m).getOrganismType()))
					typeVertices.get(((OrganismNode) m).getOrganismType()).add(((Node)m).getAnchorPoint());
				else{
					List<Point> temp = new LinkedList<Point>();
					temp.add(((Node) m).getAnchorPoint());
					typeVertices.put(((OrganismNode) m).getOrganismType(), temp);
				}
			}
		}
		hulls = new LinkedList<ConvexHull>();
		hullCollisions = new LinkedList<String>();
		int i = 0;
		for(Map.Entry<String, List<Point>> e : typeVertices.entrySet()){
			hulls.add(new ConvexHull(2, e.getValue(), e.getKey()));
			i++;
		}
		
		for(int i1=0;i1<hulls.size();i1++){
			for(int i2=hulls.size()-1;i2>i1;i2--){
				Area intersect = new Area(); 
				intersect.add(new Area(hulls.get(i1).getHullShape())); 
				intersect.intersect(new Area(hulls.get(i2).getHullShape())); 
				if (!intersect.isEmpty())
					hullCollisions.add(new StringBuffer(" \u2022 ")
					.append(hulls.get(i1).getHullName())
					.append(" group collides with the ").append(hulls.get(i2).getHullName())
					.append(" group.").toString());
			}
		}
	}

	public List<ConvexHull> getHulls() {return hulls;}
	public List<String> getHullCollisions() {return hullCollisions;}
	
	public void loadGraph(){
		graph = new Graph(getStudent().getName());
		List<Connection> connections = new LinkedList<Connection>();
		for(ModelElement element: inTreeElements()){   //load vertices
			if(element instanceof Node)
				graph.addVertex(element.getId(), ((Node) element).convertToVertex());
			else
				connections.add((Connection) element);
		}
		for(Connection c : connections){
			Vertex v1 = graph.getVertexByID(c.getFrom().getId());
			Vertex v2 = graph.getVertexByID(c.getTo().getId());
			graph.addEdge(new Edge(v1, v2));
		}
	}
	
	public Graph getGraph() {return graph;}
}
