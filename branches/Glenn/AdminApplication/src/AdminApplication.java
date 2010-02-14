import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

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
	private static StudentDataTable table = null;
	private static JSplitPane splitPane;
	public static TreeMap<String, Graph> studentNameToTree = null;
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
        treeView.setBackground(Color.black);
        table = new StudentDataTable();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, table, treeView);
        add(splitPane);
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
    
    public String getStudent(int index) {
    	for(String s : studentNameToTree.keySet()) {
    		if (index==currentGraphIndex) return s;
    		index++;
    	}
    	return "NOT FOUND";
    }
    
    public Graph getGraph(int index) {
    	if(getStudent(index).equals("NOT FOUND")) return new Graph();
    	return studentNameToTree.get(getStudent(index));
    }
    
    public int getNumStudents() {
    	return studentNameToTree.size();
    }
    
    public static void setCurrentGraph(int index) {
    	currentGraphIndex = index;
		currentGraphIndex %= studentNameToTree.values().size();
    	treeView.paintComponent();
		getCurrentGraph().printReport();  	
    }
    
    public void nextGraph() {
    	currentGraphIndex++;
		currentGraphIndex %= studentNameToTree.values().size();
    	treeView.paintComponent();
		getCurrentGraph().printReport();
    }
    
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
    	int index = 0;
    	String studentName = "";
    	for(String s : studentNameToTree.keySet()) {
    		studentName = s;
    		if (index==currentGraphIndex) break;
    		index++;
    	}
		return studentNameToTree.get(studentName);
	}
 
    public void drawCurrentGraph(Graphics g) {
    	Graphics2D g2 = (Graphics2D) g;
    	int index = 0;
    	String studentName = "";
    	for(String s : studentNameToTree.keySet()) {
    		studentName = s;
    		if (index==currentGraphIndex) break;
    		index++;
    	}
    	g.setColor(Color.white);
    	g.drawString(studentName, 0, 50);
    	Graph graph = studentNameToTree.get(studentName);
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
        		Graph graph = new Graph();
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
        		studentNameToTree.put(studentName, graph);
        		linein = reader.readLine();
        	}
        	reader.close();
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
}
