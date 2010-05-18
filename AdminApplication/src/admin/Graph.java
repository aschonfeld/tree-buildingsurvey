package admin;

//explicit list needed since some dumbass put List in both awt and util
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import admin.VertexInfo.VertexType;

public class Graph implements Renderable {

	private ArrayList<Vertex> vertices;
	private ArrayList<Vertex> organisms;
	private ArrayList<Edge> edges;
	private List<ConvexHull> hulls;
	private List<HullCollision> hullCollisions;
	private OptimalHulls defaultOptimization;
	private TreeMap<Integer, Vertex> idToVertex;
	private boolean directional;
	private boolean allOrgsInTree;
	private boolean hasBranches;
	private Boolean hasLoopResult = null;
	private boolean hasHullCollisions;
	private String studentName;
	private ArrayList<String> answers;
	private boolean labelled;
	private int graphDirection = 0;
	// length of shortest path from x->y = path[x][y]
	// includes empty nodes, does not contain unused organisms
	private int path[][] = null;
	// standard matrix for all graphs with the same common organisms
	// does not include empty nodes, contains unused organisms
	private int uniformPath[][] = null;
	private String[] pathIndexNames = null;
	private int unconnected = 9999;
	private int maxPathLength = 0;
	private GraphType type = GraphType.Unscored;

	public enum GraphType {
		Perfect, PerfectAlmost, Tree, Web, Islands, InsufficientLinks, ILGoodGrouping, ILBadGrouping, Garbage,
		// NOTE: Type "Test" is for non-student trees
		// Only student trees should be used for machine learning
		Test, Unscored;

		public boolean isSelectableType() {
			if ((this == GraphType.Test) || (this == GraphType.Unscored))
				return false;
			return true;
		}
	}

	public Graph(String studentName) {
		this.studentName = studentName;
		vertices = new ArrayList<Vertex>();
		organisms = new ArrayList<Vertex>();
		edges = new ArrayList<Edge>();
		idToVertex = new TreeMap<Integer, Vertex>();
		answers = new ArrayList<String>();
		hulls = new LinkedList<ConvexHull>();
		hullCollisions = new LinkedList<HullCollision>();
		directional = true;
		allOrgsInTree = false;
		hasBranches = false;
		hasHullCollisions = false;
		labelled = false;
	}

	public GraphType getType() {
		return type;
	}

	public void setType(GraphType type) {
		if (this.type == GraphType.Test)
			return;
		this.type = type;
	}

	public void setDirectional(boolean directional) {
		this.directional = directional;
	}

	public void setAnswers(ArrayList<String> answers) {
		this.answers = answers;
	}

	public ArrayList<String> getAnswers() {
		return answers;
	}

	/*************************
	 * Return test parameters *
	 *************************/

	public boolean allOrganismsTerminal() {
		for (Vertex v : organisms) {
			if (!v.isTerminal(directional))
				return false;
		}
		return true;
	}

	public boolean hasSingleCommonAncestor() {
		int commonAncestors = 0;
		for (Vertex v : vertices) {
			ArrayList<Vertex> checklist = new ArrayList<Vertex>();
			checklist.addAll(vertices);
			checklist.remove(v);
			if ((v.getParents().size() == 0)
					&& (v.getDescendants().containsAll(checklist))) {
				commonAncestors++;
			}
		}
		return (commonAncestors == 1);
	}

	public boolean groupsAreLabelled() {
		return labelled;
	}

	public boolean includesAllOrganisms() {
		int organismCounter = 0;
		for (Vertex v : vertices) {
			if (v.getType() == VertexInfo.VertexType.ORGANISM)
				organismCounter++;
		}
		if (organismCounter == 20) {
			allOrgsInTree = true;
		} else {
			allOrgsInTree = false;
		}
		return allOrgsInTree;
	}

	public boolean hasBranches() {
		checkForBranches();
		return hasBranches;
	}

	public int hierarchy() {
		return 2;
	}

	// higher scores are better
	// grouping < 1.0 means worse than random, grouping > 1.0 means better than
	// random
	public float groupingVertebrates() {
		calulateOrganismPathLengths();
		return calcAverage(InvToVert) / calcAverage(VertToVert);
	}

	public float groupingInvertebrates() {
		calulateOrganismPathLengths();
		return calcAverage(InvToVert) / calcAverage(InvToInv);
	}

	public float groupingMammals() {
		calulateOrganismPathLengths();
		return calcAverage(MammalToNMV) / calcAverage(MammalToMammal);
	}

	public float groupingNonmammals() {
		calulateOrganismPathLengths();
		return calcAverage(MammalToNMV) / calcAverage(NMVToNMV);
	}

	/*********************
	 * Check for branches *
	 *********************/

	public void checkForBranches() {
		for (Vertex v : vertices) {
			if (v.getFromVertices().size() > 1 || v.getToVertices().size() > 1)
				hasBranches = true;
		}
	}

	/******************************************************
	 * Alternate Grouping detection * Find least node containing 50%+ of group
	 * under test * Return percentage of group under this node *
	 ******************************************************/

	/******************
	 * Cycle detection *
	 * There is a bug affecting a small number of graphs *
	 ******************/
	
	public boolean hasLoop() {
		if(hasLoopResult == null) hasLoopResult = new Boolean(containsCycle());
		return hasLoopResult;
	}
	
	public boolean containsCycle() {
		for (Vertex v : vertices)
			v.setMark(Vertex.Mark.WHITE);
		for (Vertex v : vertices) {
			if (v.getMark() == Vertex.Mark.WHITE) {
				if (visit(v)) {
					//v.setError(true);
					return true;
				}
			}
		}
		return false;
	}

	private boolean visit(Vertex v) {
		v.setMark(Vertex.Mark.GREY);
		for (Vertex v2 : v.getAdjVertices()) {
			if (v2.getMark() == Vertex.Mark.GREY) {
				return true;
			} else if (v2.getMark() == Vertex.Mark.WHITE) {
				if (visit(v2)) {
					//v.setError(true);
					return true;
				}
			}
		}
		v.setMark(Vertex.Mark.BLACK);
		return false;
	}

	/***************************************
	 * Construct ancestor/descendant lists * (for single common ancestor) *
	 ***************************************/
	public void initRelations() {
		buildDescendantList();
		buildAncestorList();
		setGraphDirection();
	}

	public void buildDescendantList(Vertex v) {
		if (v.visited)
			return;
		v.visited = true;
		v.addDescendants(v.getToVertices());
		for (Vertex c : v.getToVertices()) {
			buildDescendantList(c);
			v.addDescendants(c.getDescendants());
		}
		for (Vertex p : v.getFromVertices()) {
			buildDescendantList(p);
		}
	}

	public void buildDescendantList() {
		// Doesn't matter where we start, so start at first vertex in the list
		buildDescendantList(vertices.get(0));
	}

	public void buildAncestorList(Vertex v) {
		if (v.visited)
			return;
		v.visited = true;
		v.addAncestors(v.getFromVertices());
		for (Vertex c : v.getFromVertices()) {
			buildAncestorList(c);
			v.addAncestors(c.getDescendants());
		}
		for (Vertex p : v.getToVertices()) {
			buildAncestorList(p);
		}
	}

	public void buildAncestorList() {
		// Doesn't matter where we start, so start at first vertex in the list
		buildAncestorList(vertices.get(0));
	}

	/*****************************************************
	 * Convex Hull Calculations * (for checking geometric arrangement of
	 * organisms) *
	 *****************************************************/
	public void loadHulls() {
		Map<String, List<Vertex>> organismGroups = new HashMap<String, List<Vertex>>();
		for (Vertex v : vertices) {
			if (VertexType.ORGANISM.equals(v.getType())) {
				if (organismGroups.containsKey(v.getInfo().getTypes().get(1)))
					organismGroups.get(v.getInfo().getTypes().get(1)).add(v);
				else {
					List<Vertex> temp = new LinkedList<Vertex>();
					temp.add(v);
					organismGroups.put(v.getInfo().getTypes().get(1), temp);
				}
			}
		}
		hulls = new LinkedList<ConvexHull>();
		for (Map.Entry<String, List<Vertex>> e : organismGroups.entrySet())
			hulls.add(new ConvexHull(e.getValue(), e.getKey()));
		
		hullCollisions = Common.hullCollisions(1, hulls);
		hasHullCollisions = !hullCollisions.isEmpty();
		defaultOptimization = null;
		if(!hasHullCollisions)
			defaultOptimization = new OptimalHulls(hulls);
	}

	public List<ConvexHull> getHulls(Boolean all) {
		if (!all)
			return hulls;
		List<ConvexHull> allHulls = new LinkedList<ConvexHull>();
		for (ConvexHull hull : hulls) {
			allHulls.add(hull);
			allHulls.addAll(hull.getChildren());
		}
		return allHulls;
	}

	public List<HullCollision> getHullCollisions(Boolean all) {
		if (!all)
			return hullCollisions;
		List<HullCollision> allCollisions = new LinkedList<HullCollision>();
		allCollisions.addAll(hullCollisions);
		for (ConvexHull hull : hulls)
			allCollisions.addAll(hull.getChildCollisions());
		return allCollisions;
	}

	public List<OptimalHulls> getOptimalHulls(Boolean all) {
		List<OptimalHulls> optimalHulls = new LinkedList<OptimalHulls>();
		if(!hullCollisions.isEmpty()){
			for (HullCollision hc : hullCollisions)
				optimalHulls.add(hc.getOptimalHulls());
			if (all) {
				for (ConvexHull hull : hulls) {
					for (HullCollision hc : hull.getChildCollisions())
						optimalHulls.add(hc.getOptimalHulls());
				}
			}
		}else
			optimalHulls.add(defaultOptimization);
		return optimalHulls;
	}

	public void displaySubDropDownItem(SubDropDownType type, int index) {
		Displayable selection = null;
		switch (type) {
		case HULL:
			selection = getHulls(true).get(index);
			break;
		case COLLISION:
			selection = getHullCollisions(true).get(index);
			break;
		case OPTIMAL_HULL:
			selection = getOptimalHulls(true).get(index);
			break;
		}
		boolean previousDisplay = selection.getDisplay();
		// Close previous selections
		for (SubDropDownType sdd : SubDropDownType.values()) {
			if (sdd.equals(type)) {
				if (!type.getViewMultiple())
					deselectItems(sdd);
			} else
				deselectItems(sdd);
		}
		selection.setDisplay(!previousDisplay);
	}

	public void deselectItems(SubDropDownType type) {
		List<Displayable> items = new LinkedList<Displayable>();
		switch (type) {
		case HULL:
			items.addAll(getHulls(true));
			break;
		case COLLISION:
			items.addAll(getHullCollisions(true));
			break;
		case OPTIMAL_HULL:
			items.addAll(getOptimalHulls(true));
			break;
		}
		for (Displayable item : items)
			item.setDisplay(false);
	}

	public void deselectAllItems() {
		for (SubDropDownType sdd : SubDropDownType.values())
			deselectItems(sdd);
	}

	public Boolean getHasHullCollisions() {
		return hasHullCollisions;
	}

	/***************************************
	 * Shortest Path Calculations * (for checking grouping of organisms) *
	 ****************************************/
	private class PathPair {
		public int numPaths;
		public int pathSums;

		PathPair(int numPaths, int pathSums) {
			this.numPaths = numPaths;
			this.pathSums = pathSums;
		}
	}

	private int minOrgPath = unconnected;
	private int maxOrgPath = 0;
	private PathPair AveragePath = null;
	private PathPair InvToInv = null;
	private PathPair InvToVert = null;
	private PathPair VertToVert = null;
	private PathPair MammalToMammal = null;
	private PathPair MammalToNMV = null;
	private PathPair NMVToNMV = null;

	// calculate shortest and average path length between distinct organisms

	private void runFloydWarshall() {
		int numVertices = vertices.size();
		path = new int[numVertices][numVertices];
		pathIndexNames = new String[numVertices];
		for (int x = 0; x < numVertices; x++) {
			for (int y = 0; y < numVertices; y++) {
				if (y == x) {
					path[x][x] = 0;
					continue;
				}
				// if use Integer.MAX_VALUE, addition will cause an overflow
				path[x][y] = unconnected;
			}
		}
		int nameIndex = 0;
		for (Vertex from : vertices) {
			pathIndexNames[nameIndex] = from.getName();
			for (Vertex to : from.getAdjVertices(false)) {
				// System.out.print(from.getIndex() + "|" + to.getIndex() +
				// " ");
				path[from.getIndex()][to.getIndex()] = 1;
			}
			nameIndex++;
		}
		for (int k = 0; k < numVertices; k++) {
			for (int i = 0; i < numVertices; i++) {
				for (int j = 0; j < numVertices; j++) {
					path[i][j] = Math.min(path[i][j], path[i][k] + path[k][j]);
				}
			}
		}
		for (int i = 0; i < numVertices; i++) {
			for (int j = 0; j < numVertices; j++) {
				// System.out.println(i + "|" + j + "=" + path[i][j]);
			}
		}
		setUnconnectedPathLengths();
		calculateUniformPathArray();
	}

	private void calculateUniformPathArray() {
		ArrayList<Vertex> commonVertices = AdminApplication.getCommonVertices();
		uniformPath = new int[commonVertices.size()][commonVertices.size()];
		for (int i = 0; i < commonVertices.size(); i++) {
			for (int j = 0; j < commonVertices.size(); j++) {
				uniformPath[i][j] = maxPathLength;
			}
		}
		for (int row = 0; row < pathIndexNames.length; row++) {
			if (vertices.get(row).getType() != VertexType.ORGANISM)
				continue;
			int uniformRow = AdminApplication
					.getVertexIndexByName(pathIndexNames[row]);
			for (int col = 0; col < pathIndexNames.length; col++) {
				if (vertices.get(col).getType() != VertexType.ORGANISM)
					continue;
				int uniformCol = AdminApplication
						.getVertexIndexByName(pathIndexNames[col]);
				uniformPath[uniformRow][uniformCol] = path[row][col];
			}
		}
	}

	// unconnected path length = max connected path length + 1
	private void setUnconnectedPathLengths() {
		maxPathLength = 0;
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) {
				if (path[i][j] < unconnected) {
					if (path[i][j] > maxPathLength)
						maxPathLength = path[i][j];
				}
			}
		}
		maxPathLength++;
		for (int i = 0; i < vertices.size(); i++) {
			for (int j = 0; j < vertices.size(); j++) {
				if (path[i][j] == unconnected) {
					path[i][j] = maxPathLength;
				}
			}
		}
	}

	public void calulateOrganismPathLengths() {
		if (InvToInv != null)
			return;
		InvToInv = new PathPair(0, 0);
		InvToVert = new PathPair(0, 0);
		VertToVert = new PathPair(0, 0);
		MammalToMammal = new PathPair(0, 0);
		MammalToNMV = new PathPair(0, 0);
		NMVToNMV = new PathPair(0, 0);
		AveragePath = new PathPair(0, 0);
		int[][] paths = getShortestPaths();
		for (int fromIndex = 0; fromIndex < vertices.size(); fromIndex++) {
			Vertex from = vertices.get(fromIndex);
			if (from.getType() != VertexInfo.VertexType.ORGANISM)
				continue;
			for (int toIndex = 0; toIndex < vertices.size(); toIndex++) {
				Vertex to = vertices.get(toIndex);
				if (to.getType() != VertexInfo.VertexType.ORGANISM)
					continue;
				if (toIndex == fromIndex)
					continue;
				int pathLength = paths[fromIndex][toIndex];
				if (pathLength < unconnected) {
					if (pathLength > maxOrgPath)
						maxOrgPath = pathLength;
					if (pathLength < minOrgPath)
						minOrgPath = pathLength;
					AveragePath.numPaths++;
					AveragePath.pathSums += pathLength;
					doAnalysis(from, to, pathLength);
				}
			}
		}
	}

	// Should be renamed to clarify what sort of analysis is being done
	public void doAnalysis(Vertex from, Vertex to, int pathLength) {
		for (PathPair p : getPathPairs(from, to)) {
			p.numPaths++;
			p.pathSums += pathLength;
		}
	}

	public ArrayList<PathPair> getPathPairs(Vertex from, Vertex to) {
		ArrayList<PathPair> returnVal = new ArrayList<PathPair>();
		if (testPair(from, to, "Invert", "Invert"))
			returnVal.add(InvToInv);
		if (testPair(from, to, "Invert", "Vert"))
			returnVal.add(InvToVert);
		if (testPair(from, to, "Vert", "Vert"))
			returnVal.add(VertToVert);
		if (testPair(from, to, "Mammal", "Mammal"))
			returnVal.add(MammalToMammal);
		if (testPair(from, to, "NMV", "Mammal"))
			returnVal.add(MammalToNMV);
		if (testPair(from, to, "NMV", "NMV"))
			returnVal.add(NMVToNMV);
		return returnVal;
	}

	public boolean testPair(Vertex v1, Vertex v2, String type1, String type2) {
		if (v1.getInfo().getTypes().containsValue(type1)) {
			if (v2.getInfo().getTypes().containsValue(type2)) {
				return true;
			}
		}
		if (v2.getInfo().getTypes().containsValue(type1)) {
			if (v1.getInfo().getTypes().containsValue(type2)) {
				return true;
			}
		}
		return false;
	}

	public int minOrgPathLength() {
		calulateOrganismPathLengths();
		return minOrgPath;
	}

	public float averageOrgPathLength() {
		calulateOrganismPathLengths();
		return calcAverage(AveragePath);
	}

	public int maxOrgPathLength() {
		calulateOrganismPathLengths();
		return minOrgPath;
	}

	public float calcAverage(PathPair p) {
		if (p.numPaths == 0)
			return maxPathLength;
		float returnVal = (float) p.pathSums;
		returnVal /= (float) p.numPaths;
		return returnVal;
	}

	/************
	 * Rendering *
	 ************/

	public void render(Graphics g, Point offset) {
		Point upperLeft = getUpperLeft();
		offset.x += upperLeft.x;
		offset.y += upperLeft.y;
		for (Vertex v : vertices) {
			v.render(g, offset);
			if (AdminApplication.showNames
					&& VertexType.ORGANISM.equals(v.getInfo().getVertexType())) {
				int xVal = (v.upperLeft.x - offset.x)
						+ (v.getInfo().getImage().getWidth() / 2);
				int yVal = v.upperLeft.y - (offset.y + Common.ySpacing);
				g.setFont(Common.tooltipFont);
				String tooltipString = v.getInfo().getName()
						+ " - "
						+ Common.commaSeparatedString(v.getInfo().getTypes()
								.values());
				xVal -= Common.getStringBounds((Graphics2D) g, tooltipString).width / 2;
				Common.drawCenteredString((Graphics2D) g, tooltipString, xVal,
						yVal, 0, 0, Common.tooltipColor, Common.tooltipFont);
				g.setFont(Common.font);
			}
		}
		for (Edge e : edges) {
			e.setDirectional(directional);
			e.render(g, offset);
		}
		for (ConvexHull hull : getHulls(true)) {
			if (hull.getDisplay())
				hull.render(g, offset);
		}
		for (HullCollision collision : getHullCollisions(true)) {
			if (collision.getDisplay())
				collision.render(g, offset);
		}
		for (OptimalHulls optimalHull : getOptimalHulls(true)) {
			if (optimalHull.getDisplay())
				optimalHull.render(g, offset);
		}
	}

	/*********************
	 * Getters / toString *
	 *********************/

	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (Vertex v : vertices) {
			sb.append(v.toString() + "\n");
		}
		for (Edge e : edges) {
			sb.append(e.toString() + "\n");
		}
		return sb.toString();
	}

	public String getStudentName() {
		return studentName;
	}

	public Vertex getVertexByID(int id) {
		return idToVertex.get(new Integer(id));
	}

	public String getInfo() {
		StringBuffer sb = new StringBuffer();
		for (Vertex v : vertices) {
			sb.append("START:" + v + "\n");
			for (Vertex from : v.getFrom()) {
				sb.append("    FROM: " + from.toString() + "\n");
			}
			for (Vertex to : v.getTo()) {
				sb.append("    TO: " + to.toString() + "\n");
			}
		}
		return sb.toString();
	}

	public Point getUpperLeft() {
		int minX = 1000;
		int minY = 1000;
		for (Vertex v : vertices) {
			int x = v.getUpperLeft().x;
			int y = v.getUpperLeft().y;
			if (x < minX)
				minX = x;
			if (y < minY)
				minY = y;
		}
		return new Point(minX - 5, minY - 40);
	}

	public Point getLowerRight(Graphics g) {
		int maxX = 0;
		int maxY = 0;
		for (Vertex v : vertices) {
			int x = v.getLowerRight(g).x;
			int y = v.getLowerRight(g).y;
			if (x > maxX)
				maxX = x;
			if (y > maxY)
				maxY = y;
		}
		maxX -= getUpperLeft().x;
		maxY -= getUpperLeft().y;
		return new Point(maxX, maxY);
	}

	public int[][] getShortestPaths() {
		if (path == null) {
			runFloydWarshall();
		}
		return path;
	}

	public int[][] getUniformShortestPaths() {
		if (uniformPath == null) {
			runFloydWarshall();
		}
		return uniformPath;
	}

	public String[] getPathIndexNames() {
		if (path == null) {
			runFloydWarshall();
		}
		return pathIndexNames;
	}

	/*****************************
	 * Graph construction methods *
	 *****************************/
	public void addVertex(int id, Vertex v) {
		v.setIndex(vertices.size());
		vertices.add(v);
		if (v.getType() == VertexInfo.VertexType.ORGANISM) {
			organisms.add(v);
		} else if (v.getType() == VertexInfo.VertexType.EMPTY) {
			if (v.hasName())
				labelled = true;
		}
		idToVertex.put(new Integer(id), v);
	}

	public void setGraphDirection() {
		// partial- this only works if all organisms are terminal
		// and assumes that consistency there will be reflected elsewhere
		// This will have to be repaired, but will work for trial purposes.

		// Direction is 1 if arrows point from root to leaf, -1 if from
		// leaves to root, 0 if undefined.

		if (allOrganismsTerminal()) {
			for (Vertex v : organisms) {
				graphDirection += v.direction();
			}
			if (Math.abs(graphDirection) == organisms.size()
					&& organisms.size() != 0) {
				graphDirection /= organisms.size();
				if (graphDirection < 0) {
					for (Vertex v : vertices)
						v.invertGraph();
					System.out.println("Inverted " + studentName);
				}

			} else {
				graphDirection = 0; // we haven't got a direction yet
			}
		}

	}

	public void addEdge(Edge e) {
		if ((e.getV1() == null) || (e.getV2() == null)) {
			System.out.println("Error loading tree: " + studentName);
			return;
		}
		edges.add(e);
		e.getV1().addTo(e.getV2());
		e.getV2().addFrom(e.getV1());
	}

}

/*
 * Deprecated, will probably be removed soon public void printReport() {
 * System.out.println("------------ \nNext Graph:\n------------");
 * System.out.println("All Organisms Terminal: " + allOrganismsTerminal());
 * 
 * System.out.println("Tree has single common ancestor: " +
 * hasSingleCommonAncestor());
 * 
 * System.out.println("Tree includes all Organisms: " + includesAllOrganisms());
 * 
 * // System.out.println("Tree has branches: " + hasBranches()); //
 * System.out.println("Groups are Labelled: " + groupsAreLabelled());
 * 
 * // System.out.println("Degree of hierarchy: " + hierarchy());
 * 
 * // System.out.println("Vertebrates grouped: "+ groupingVertebrates());
 * 
 * // System.out.println("Invertebrates grouped: " + //
 * groupingInvertebrates());
 * 
 * 
 * // System.out.println("Mammals grouped: " + groupingMammals());
 * 
 * // System.out.println("Non-mammals grouped " + groupingNonmammals());
 * System.out.println("----------------"); }
 */

