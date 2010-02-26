package admin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import admin.dao.AdminJdbcDao;

public class AdminApplication extends JFrame {
	private static final long serialVersionUID = 8731793917007308006L;
	private static ActionHandler actionHandler = null;
	private static TreeView treeView = null;
	private static TreeController treeController = null;
	private static StudentDataTable studentTable = null;
	private static JSplitPane mainSplitPane;
	private static JScrollPane treePane;
	public static TreeMap<String, Graph> studentNameToTree = null;
	public static ArrayList<Graph> graphs;
	private static ArrayList<Vertex> commonVertices = null; //organism nodes
	private static ArrayList<VertexInfo> commonImages = null; //this simplifies things
	private static int currentGraphIndex = 0;
	
	AdminApplication() {
    	super("AdminApplication");
		actionHandler = new ActionHandler();
		treeView = new TreeView();
		treeController = new TreeController();
		initCommonVertices();
		loadTreesFromDirectory();
		try{
			loadTreesFromDB();
		}catch(Exception e){
			System.out.println("Error loading students from database: " + e);
			System.out.println("Database Connection could not be made.  Loading Student tree from local file.");
			loadTreesFromParamTags();
		}
		treeMapToArrayList();
		treeView.setBackground(Color.black);
		treePane = new JScrollPane(treeView);
		studentTable = new StudentDataTable();
		mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
				studentTable, treePane);
		add(mainSplitPane);
		addMouseListener(treeController);
		setPreferredSize(new Dimension(928, 762));
		setJMenuBar(actionHandler.createMenuBar());
	}
    
    	/**
		* Create the GUI and show it.  For thread safety, this method 
		*should be invoked from the event-dispatching thread.
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
   
    public static void setCurrentGraph(int index) {
    	currentGraphIndex = index;
		currentGraphIndex %= graphs.size();
	    //leftSplitPane.remove(pathTable);
	    //pathTable = new ShortestPathTable();
	    //leftSplitPane.add(pathTable);
	    //leftSplitPane.revalidate();
	    treeView.paintComponent();
    }

    public void printGraphInfo() {
    	System.out.println(graphs.get(currentGraphIndex).getInfo());
    }
    
    public void checkHullCollisions() {
    	System.out.println(graphs.get(currentGraphIndex).checkConvexHullCollision());
    }
   
	public static Graph getCurrentGraph(){
		return graphs.get(currentGraphIndex);
	}
 

    public void drawCurrentGraph(Graphics g) {
    	Graph graph = getCurrentGraph();	
		String studentName = graph.getStudentName();
    	g.setColor(Color.white);
    	g.drawString(studentName, 5, 20);
    	Point size = graph.getLowerRight(g);
    	Dimension newSize = new Dimension(size.x, size.y);
    	treeView.setPreferredSize(newSize);
    	treeView.scrollRectToVisible(new Rectangle(0, 0, size.x, size.y));
    	treeView.revalidate();
    	treeView.repaint();
    	//System.out.println(size);
    	graph.render(g, new Point(0,0));
    }
    
    private static void initCommonVertices() {
    	TreeMap<String, VertexInfo> organismNameToImage = 
				loadVerticesFromDirectory();
		commonVertices = new ArrayList<Vertex>();
		commonImages = new ArrayList<VertexInfo>();
    	for(Map.Entry<String, VertexInfo> e : organismNameToImage.entrySet()) {
            commonVertices.add(new Vertex(e.getValue(), new Point(0,0)));
            commonImages.add(e.getValue());
    	}
    }

    public static TreeMap<String, VertexInfo> loadVerticesFromDirectory() {
        TreeMap<String, VertexInfo> organismNameToImage = 
				new TreeMap<String, VertexInfo>();
        try {
        	// read names of organisms and image file names from list.txt
        	BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream("images/list.txt")));
        	String line = null;
        	String[] parseLine = null;
        	String name = null;
        	StringBuffer imgFname = null;
        	String type = null;
        	BufferedImage img = null;
        	while ((line = reader.readLine()) != null) {
        		// load image from files, and map organism name to image
        		parseLine = line.split(",");
        		name = parseLine[0];
        		imgFname = new StringBuffer("images/").append(parseLine[1]);
        		type = parseLine[2];
        		// System.out.println(organismName + " " + organismImageFilename);
        		InputStream imageis= new FileInputStream(imgFname.toString());
        		img = ImageIO.read(imageis);
        		organismNameToImage.put(name, new VertexInfo(name, type, img));
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
        			if("O".equals(type)) 
					{
        				if(inTree) 
						{
        					graph.addVertex(
     							id, new Vertex(commonImages.get(id), new Point(x, y)));
        				}
        			}
        			if("E".equals(type)) 
					{
        				// only empty nodes in tree (exclude immortalNode)
        				if(inTree) 
						{
        					graph.addVertex(id, new Vertex(new
								VertexInfo(elementName), new Point(x, y)));
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
			FileWriter ryt=new FileWriter("c:\\studentData.sql");
			BufferedWriter out=new BufferedWriter(ryt);	
			StringBuffer sqlStatement;
			String filePath = new String("trees/studentTrees");
			BufferedReader reader = new BufferedReader(new 
					InputStreamReader(new FileInputStream(filePath)));
			String linein = reader.readLine();
			while(linein != null) {
				sqlStatement = new StringBuffer("insert into student_testdata values(");
				String[] paramParse = linein.split("\" value=\"");
				String studentData = paramParse[1];
				//studentData.replaceAll("/", "");  
				//System.out.println(studentData);
				String[] studentDataItems = 
					studentData.split(Pattern.quote("+="));
				String studentName = studentDataItems[0];
				sqlStatement.append("'").append(studentName.replace("'", "''").replace("\"", "")).append("',null,");
				String treeData = studentDataItems[2];
				sqlStatement.append("'").append(treeData.replace("'", "''")).append("',");
				String question1 = studentDataItems[3];
				sqlStatement.append("'").append(question1.replace("'", "''").replace("\"", "")).append("',");
				String question2 = studentDataItems[4];
				sqlStatement.append("'").append(question2.replace("'", "''").replace("\"", "")).append("',");
				sqlStatement.append("'');");
				String section = studentDataItems[6].substring(8,10);
				String[] treeItems = treeData.split("#"); // remove '=' at start
				Graph graph = new Graph(studentName);
				for(String elements: treeItems){   //load vertices
					String[] attributes = elements.split(":");
					if(attributes.length < 6) continue;
					String type = attributes[0];
					int id = Integer.parseInt(attributes[1]);
					String elementName = attributes[2];
					int x = Integer.parseInt(attributes[3]);
					int y = Integer.parseInt(attributes[4]);
					boolean inTree = Boolean.parseBoolean(attributes[5]);
					if("O".equals(type)) 
					{
						if(inTree) 
						{
							graph.addVertex(id, new Vertex(commonImages.get(id), 
									new Point(x, y)));
						}
					}
					if("E".equals(type)) 
					{
						// only empty nodes in tree (exclude immortalNode)
						if(inTree) 
						{
							graph.addVertex(id, new Vertex(
									new VertexInfo(elementName), new Point(x, y)));
						}	
					}
				}
				for(String elements: treeItems) 
				{
					// load connections
					String[] attributes = elements.split(":");
					String type = attributes[0];
					if(!"C".equals(type)) continue;
					int id1 = Integer.parseInt(attributes[2]);
					int id2 = Integer.parseInt(attributes[3]);
					//System.out.println(id1 + " " + id2);
					Vertex v1 = graph.getVertexByID(id1);
					Vertex v2 = graph.getVertexByID(id2);
					graph.addEdge(new Edge(v1, v2));
				}
				studentNameToTree.put(studentName, graph);
				out.write(sqlStatement.append("\n").toString());
				linein = reader.readLine();
			}
			reader.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadTreesFromDB() throws Exception {
		AdminJdbcDao dao = new AdminJdbcDao();
		List<String[]> studentsData = dao.loadStudents();
		for(String[] studentData : studentsData) {
				String studentName = studentData[0];
				String treeData = studentData[2];
				String[] treeItems = treeData.split("#"); // remove '=' at start
				Graph graph = new Graph(studentName);
				for(String elements: treeItems){   //load vertices
					String[] attributes = elements.split(":");
					if(attributes.length < 6) continue;
					String type = attributes[0];
					int id = Integer.parseInt(attributes[1]);
					String elementName = attributes[2];
					int x = Integer.parseInt(attributes[3]);
					int y = Integer.parseInt(attributes[4]);
					boolean inTree = Boolean.parseBoolean(attributes[5]);
					if("O".equals(type)) 
					{
						if(inTree) 
						{
							graph.addVertex(id, new Vertex(commonImages.get(id), 
									new Point(x, y)));
						}
					}
					if("E".equals(type)) 
					{
						// only empty nodes in tree (exclude immortalNode)
						if(inTree) 
						{
							graph.addVertex(id, new Vertex(
									new VertexInfo(elementName), new Point(x, y)));
						}	
					}
				}
				for(String elements: treeItems) 
				{
					// load connections
					String[] attributes = elements.split(":");
					String type = attributes[0];
					if(!"C".equals(type)) continue;
					int id1 = Integer.parseInt(attributes[2]);
					int id2 = Integer.parseInt(attributes[3]);
					//System.out.println(id1 + " " + id2);
					Vertex v1 = graph.getVertexByID(id1);
					Vertex v2 = graph.getVertexByID(id2);
					graph.addEdge(new Edge(v1, v2));
				}
				studentNameToTree.put(studentName, graph);
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
			Graph thisGraph = studentNameToTree.remove(key);

			//might as well build these lists now and get it done
			
			thisGraph.initRelations();

			//put it in the arraylist

			graphs.add(thisGraph);
		}
	}
    
}
