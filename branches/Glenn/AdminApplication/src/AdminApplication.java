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
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class AdminApplication extends JFrame {
	
	private static ActionHandler actionHandler = null;
	private static TreeView treeView = null;
	private static TreeController treeController = null;
	private static TreeMap<String, Graph> studentNameToTree = null;
	private static ArrayList<Vertex> commonVertices = null; //organism nodes
	private static ArrayList<BufferedImage> commonImages = null; //this simplifies things
	
    AdminApplication() {
    	super("AdminApplication");
    	actionHandler = new ActionHandler();
    	treeView = new TreeView();
    	treeController = new TreeController();
        setSize(1000, 600);
        treeView.setBackground(Color.black);
        add(treeView);
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
    	treeView.setFrame(frame);
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
    
    public void drawCurrentGraph(Graphics g) {
    	if(g == null) return;
    	if(studentNameToTree == null) {
    		initGraphics(g);
    		loadTreesFromDirectory();
        	for(String key: studentNameToTree.keySet()) {
        		System.out.println(key);
        	}
        	for(Graph graph: studentNameToTree.values()) {
        		//System.out.println(graph);
        	}	
    	}
    	for(Graph graph: studentNameToTree.values()) {
    		graph.render(g, new Point(0,0));
    		break;
    	}
    }

    public static TreeMap<String, BufferedImage> loadOrganismsFromDirectory() {
        TreeMap<String, BufferedImage> organismNameToImage = new TreeMap<String, BufferedImage>();
        try {
        	// read names of organisms and image file names from list.txt in "/images"
        	BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("images/list.txt")));
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
        	File treeDirectory = new File("trees");
        	for(File f: treeDirectory.listFiles()) {
        		System.out.println("trees/" + f.getName());
        		String filePath = "trees/" + f.getName();
        		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
        		String linein = reader.readLine();
        		if(Common.isStringEmpty(linein)) continue;
        		String[] studentData = linein.split("\\+");
        		String studentName = studentData[0];
        		System.out.println(studentName);
        		String time = studentData[1].substring(1); // remove '=' at start
        		System.out.println(time);
        		String[] treeItems = studentData[2].substring(1).split("#"); // remove '=' at start
        		Graph graph = new Graph();
        		for(String elements: treeItems) {
        			// load vertices
        			String[] attributes = elements.split(":");
        			if(attributes.length < 6) continue;
        			String type = attributes[0];
        			int id = Integer.parseInt(attributes[1]);
        			String elementName = attributes[2];
        			int x = Integer.parseInt(attributes[3]);
        			int y = Integer.parseInt(attributes[4]);
        			boolean inTree = Boolean.parseBoolean(attributes[5]);
        			System.out.println(type + "|" + id + "|" + elementName + "|" + x + "|" + y + "|" + inTree);
        			if(type.equals("O")) {
        				if(inTree) {
        					graph.addVertex(
        							id, new Vertex(elementName, new Point(x, y), commonImages.get(id)));
        				} else {
        					//graph.addVertex(id, commonVertices.get(id));
        				}
        			}
        			if(type.equals("E")) {
        				// only empty nodes in tree (exclude immortalNode)
        				if(inTree) {
        					graph.addVertex(id, new Vertex(elementName, new Point(x, y)));
        				}
        			}
        		}
        		for(String elements: treeItems) {
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
        		reader.close();
        	}
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    public static void initGraphics(Graphics g) {
    	if(g == null) return;
    	if (commonVertices == null) {
    		commonVertices = new ArrayList<Vertex>();
    		commonImages = new ArrayList<BufferedImage>();
    		initCommonVertices(g, loadOrganismsFromDirectory());
    	}
    }
    
    private static void initCommonVertices(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
    	Graphics2D g2 = (Graphics2D) g;
    	int currentY = 0;
    	Dimension stringDimensions = Common.get2DStringBounds(g2, organismNameToImage.keySet());
    	Dimension imageDimensions = Common.get2DImageBounds(g2, organismNameToImage.values());
    	Common.organismNodeWidth = stringDimensions.width + imageDimensions.width + Common.paddingWidth * 2;
    	if(stringDimensions.height > imageDimensions.height) {
            Common.organismNodeHeight = stringDimensions.height;
    	} else {
            Common.organismNodeHeight = imageDimensions.height;
    	}
    	for(Map.Entry<String, BufferedImage> e : organismNameToImage.entrySet()) {
            commonVertices.add(new Vertex(e.getKey(), new Point(0, currentY), e.getValue()));
            commonImages.add(e.getValue());
            currentY += Common.organismNodeHeight + Common.ySpacing;
    	}
    }

}
