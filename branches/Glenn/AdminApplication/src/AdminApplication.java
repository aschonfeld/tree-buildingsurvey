import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class AdminApplication extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8731793917007308006L;
	private static ActionHandler actionHandler = null;
	private static TreeView treeView = null;
	private static TreeController treeController = null;
	private static StudentDataTable studentTable = null;
	private static ShortestPathTable pathTable = null;
	private static JSplitPane mainSplitPane;
	private static JSplitPane leftSplitPane;
	public static TreeMap<String, Graph> studentNameToTree = null;
	public static ArrayList<Graph> graphs;
	private static ArrayList<Vertex> commonVertices = null; //organism nodes
	private static ArrayList<BufferedImage> commonImages = null; //this simplifies things
	private static int currentGraphIndex = 0;
	
    AdminApplication() {
		super("AdminApplication");
    	actionHandler = new ActionHandler();
    	treeView = new TreeView();
    	treeController = new TreeController();
    	initCommonVertices();
    	loadTreesFromDirectory();
    	loadTreesFromParamTags();
		treeMapToArrayList();
      treeView.setBackground(Color.black);
      studentTable = new StudentDataTable();
      pathTable = new ShortestPathTable();
      leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, studentTable, pathTable);
      mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplitPane, treeView);
      add(mainSplitPane);
      addMouseListener(treeController);
      setPreferredSize(new Dimension(928, 762));
      setJMenuBar(actionHandler.createMenuBar());
    }
    
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
    	AdminApplication frame = new AdminApplication();
    	treeView.setParent(frame);
    	actionHandler.setParent(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
   

    public int getNumStudents() {
    	return studentNameToTree.size();
    }
    
    public static void setCurrentGraph(int index) {
    	currentGraphIndex = index;
		currentGraphIndex %= graphs.size();
	    leftSplitPane.remove(pathTable);
	    pathTable = new ShortestPathTable();
	    leftSplitPane.add(pathTable);
	    leftSplitPane.revalidate();
	    treeView.paintComponent();
    }
    
    public void nextGraph() {}

    public void printGraphInfo() {
    	Graph currentGraph = null;
    	for(Graph graph: studentNameToTree.values()) {
    		currentGraph = graph;
    		break;
    	}
    	System.out.println(currentGraph.getInfo());
    }
   
	public static Graph getCurrentGraph()
	{
		return graphs.get(currentGraphIndex);
	}
 

    public void drawCurrentGraph(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
    	Graph graph = getCurrentGraph();	
		String studentName = graph.getStudentName();

    	g.setColor(Color.white);
    	g.drawString(studentName, 0, 50);
    	int y = Common.getStringBounds(g2, studentName).height + 
				Common.ySpacing + 50;
    	if(graph.containsCycle()) {
        	g.setColor(Color.red);
        	g.drawString("LOOPS: TRUE", 0, y);		
    	} else {
        	g.setColor(Color.white);
        	g.drawString("LOOPS: FALSE", 0, y);		   		
    	}
    	graph.render(g, new Point(0,0));
    }
    
    private static void initCommonVertices() {
    	TreeMap<String, BufferedImage> organismNameToImage = 
				loadVerticesFromDirectory();
		commonVertices = new ArrayList<Vertex>();
		commonImages = new ArrayList<BufferedImage>();
    	for(Map.Entry<String, BufferedImage> e : organismNameToImage.entrySet()) {
            commonVertices.add(new Vertex(e.getKey(), new Point(0,0), 
					e.getValue()));
            commonImages.add(e.getValue());
    	}
    }

    public static TreeMap<String, BufferedImage> loadVerticesFromDirectory() {
        TreeMap<String, BufferedImage> organismNameToImage = 
				new TreeMap<String, BufferedImage>();
        try {
        	// read names of organisms and image file names from list.txt
        	BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream("images/list.txt")));
        	String line = null;
        	String[] parseLine = null;
        	String organismName = null;
        	String organismImageFilename = null;
        	String organismImageFullPath = null;
        	BufferedImage img = null;
        	while ((line = reader.readLine()) != null) {
        		// load image from files, and map organism name to image
        		parseLine = line.split(",");
        		organismName = parseLine[0];
        		organismImageFilename = parseLine[1];
        		// System.out.println(organismName + " " + organismImageFilename);
        		organismImageFullPath = ("images/" + organismImageFilename);
        		InputStream imageis= new FileInputStream(organismImageFullPath);
        		img = ImageIO.read(imageis);
        		organismNameToImage.put(organismName, img);
        		imageis.close();
        	}
        	reader.close();
        } catch (Exception e) {
        	e.printStackTrace();
        } 
        return organismNameToImage;
    }
    
    public static void loadTreesFromDirectory() {
        studentNameToTree = new TreeMap<String, Graph>();
        try {
        	String filePath = new String("trees/testTrees");
    		BufferedReader reader = new BufferedReader(new 
					InputStreamReader(new FileInputStream(filePath)));
    		String linein = reader.readLine();
        	while(linein != null) {
        		String studentName = linein;
        		System.out.println("STUDENT: " + studentName);
        		linein = reader.readLine();
        		String[] treeItems = linein.split("#"); // remove '=' at start
        		Graph graph = new Graph(studentName);
        		for(String elements: treeItems) 
				{
        			// load vertices
        			String[] attributes = elements.split(":");
        			if(attributes.length < 6) continue;
        			String type = attributes[0];
        			int id = Integer.parseInt(attributes[1]);
        			String elementName = attributes[2];
        			int x = Integer.parseInt(attributes[3]);
        			int y = Integer.parseInt(attributes[4]);
        			boolean inTree = Boolean.parseBoolean(attributes[5]);
        			if(type.equals("O")) 
					{
        				if(inTree) 
						{
        					graph.addVertex(
        							id, new Vertex(elementName, new Point(x, y), 
									commonImages.get(id)));
        				}
        			}
        			if(type.equals("E")) 
					{
        				// only empty nodes in tree (exclude immortalNode)
        				if(inTree) 
						{
        					graph.addVertex(id, new Vertex(elementName, 
							new Point(x, y)));
        				}
        			}
        		}
        		for(String elements: treeItems) 
				{
        			// load connections
        			String[] attributes = elements.split(":");
        			String type = attributes[0];
        			if(!type.equals("C")) continue;
        			int id1 = Integer.parseInt(attributes[2]);
        			int id2 = Integer.parseInt(attributes[3]);
        			System.out.println(id1 + " " + id2);
        			Vertex v1 = graph.getVertexByID(id1);
        			Vertex v2 = graph.getVertexByID(id2);
        			graph.addEdge(new Edge(v1, v2));
        		}
        		studentNameToTree.put(new String("0_TEST_" + studentName), graph);
        		linein = reader.readLine();
        	}
        	reader.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void loadTreesFromParamTags() {
        try {
        	String filePath = new String("trees/studentTrees");
    		BufferedReader reader = new BufferedReader(new 
					InputStreamReader(new FileInputStream(filePath)));
    		String linein = reader.readLine();
        	while(linein != null) {
        		String[] paramParse = linein.split("\" value=\"");
            	String studentData = paramParse[1];
            	//studentData.replaceAll("/", "");  
            	//System.out.println(studentData);
            	String[] studentDataItems = studentData.split(Pattern.quote("+="));
            	String studentName = studentDataItems[0];
            	String treeData = studentDataItems[2];
            	String question1 = studentDataItems[3];
            	String question2 = studentDataItems[4];
            	String section = studentDataItems[6].substring(8,10);
            	String[] treeItems = treeData.split("#"); // remove '=' at start
        		Graph graph = new Graph(studentName);
        		for(String elements: treeItems) 
				{
        			// load vertices
        			String[] attributes = elements.split(":");
        			if(attributes.length < 6) continue;
        			String type = attributes[0];
        			int id = Integer.parseInt(attributes[1]);
        			String elementName = attributes[2];
        			int x = Integer.parseInt(attributes[3]);
        			int y = Integer.parseInt(attributes[4]);
        			boolean inTree = Boolean.parseBoolean(attributes[5]);
        			if(type.equals("O")) 
					{
        				if(inTree) 
						{
        					graph.addVertex(
        							id, new Vertex(elementName, new Point(x, y), 
									commonImages.get(id)));
        				}
        			}
        			if(type.equals("E")) 
					{
        				// only empty nodes in tree (exclude immortalNode)
        				if(inTree) 
						{
        					graph.addVertex(id, new Vertex(elementName, 
							new Point(x, y)));
        				}
        			}
        		}
        		for(String elements: treeItems) 
				{
        			// load connections
        			String[] attributes = elements.split(":");
        			String type = attributes[0];
        			if(!type.equals("C")) continue;
        			int id1 = Integer.parseInt(attributes[2]);
        			int id2 = Integer.parseInt(attributes[3]);
        			//System.out.println(id1 + " " + id2);
        			Vertex v1 = graph.getVertexByID(id1);
        			Vertex v2 = graph.getVertexByID(id2);
        			graph.addEdge(new Edge(v1, v2));
        		}
        		studentNameToTree.put(studentName, graph);
        		linein = reader.readLine();
        	}
        	reader.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	

	//convert treeMap to ArrayList

	private void treeMapToArrayList()
	{
		graphs = new ArrayList<Graph>();

		while (!studentNameToTree.isEmpty())
		{
//			Graph thisGraph= studentNameToTree.pollFirstEntry().getValue();
//			tried this - only good under 1.6 - I'm 1.5 at the moment.		

			String key = studentNameToTree.firstKey();
			Graph thisGraph = studentNameToTree.get(key);
			studentNameToTree.remove(key);

			//might as well build these lists now and get it done

			thisGraph.buildDescendantList();
			thisGraph.buildAncestorList();

			//put it in the arraylist

			graphs.add(thisGraph);
		}
	}
    
}
