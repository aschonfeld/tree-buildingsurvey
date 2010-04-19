//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.TBSApplet;
import tbs.TBSUtils;
import tbs.graphanalysis.ConvexHull;
import tbs.graphanalysis.Edge;
import tbs.graphanalysis.Graph;
import tbs.graphanalysis.HullCollision;
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
	private List<HullCollision> hullCollisions;
	private Graph graph;

	public AdminModel(TBSApplet applet,	List<OrganismNode> organisms, List<Student> students) {
		super(applet, organisms);
		this.students = students;
		Student student = this.students.get(0);
		setStudent(student);
		String tree = student.getTree();
		hulls = new LinkedList<ConvexHull>();
		hullCollisions = new LinkedList<HullCollision>();
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
			hulls = new LinkedList<ConvexHull>();
			hullCollisions = new LinkedList<HullCollision>();
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
		Map<String, List<OrganismNode>> organismGroups = new HashMap<String, List<OrganismNode>>();
		for(ModelElement m : inTreeElements()){
			if(m instanceof OrganismNode){
				if(organismGroups.containsKey(((OrganismNode) m).getTypes().get(1)))
					organismGroups.get(((OrganismNode) m).getTypes().get(1)).add((OrganismNode) m);
				else{
					List<OrganismNode> temp = new LinkedList<OrganismNode>();
					temp.add((OrganismNode) m);
					organismGroups.put(((OrganismNode) m).getTypes().get(1), temp);
				}
			}
		}
		hulls = new LinkedList<ConvexHull>();
		for(Map.Entry<String, List<OrganismNode>> e : organismGroups.entrySet())
			hulls.add(new ConvexHull(e.getValue(), e.getKey()));
		hullCollisions = TBSUtils.hullCollisions(hulls);
	}

	public List<ConvexHull> getHulls(Boolean all) {
		if(!all)
			return hulls;
		List<ConvexHull> allHulls = new LinkedList<ConvexHull>();
		for(ConvexHull hull : hulls){
			allHulls.add(hull);
			allHulls.addAll(hull.getChildren());
		}
		return allHulls;
	}
	
	public List<HullCollision> getHullCollisions(Boolean all) {
		if(!all)
			return hullCollisions;
		List<HullCollision> allCollisions = new LinkedList<HullCollision>();
		allCollisions.addAll(hullCollisions);
		for(ConvexHull hull : hulls)
			allCollisions.addAll(hull.getChildCollisions());
		return allCollisions;
	}
	
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
