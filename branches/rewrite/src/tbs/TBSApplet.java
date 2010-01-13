//TBSApplet: the runnable frame class of TBS

package tbs;



import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JApplet;

import tbs.model.AdminModel;
import tbs.model.StudentModel;
import tbs.model.TBSModel;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;

/**
* TBSApplet is the frame in which the Tree-Building System runs. Its
* function is to initialize the major classes of the applet and to load
* in the initial data.
*/
public class TBSApplet extends JApplet {
	
	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0x03046F6687102247L;
	
	private TBSModel model;
	private TBSApplet app = this;

	/**
	* INIT instantiates TBSGraphics and TBSModel, and calls the
	* loadOrganismsFromDirectory method in order to populate the initial
	* set of OrganismNodes.
	*/
	public void init() {

 	EventQueue.invokeLater(new Runnable() {
 		public void run() {
 			TBSGraphics.appletWidth = getWidth();
 			TBSGraphics.appletHeight = getHeight();
 			Graphics2D g2 = (Graphics2D) getGraphics();
 			RenderingHints rh = new RenderingHints(
 			RenderingHints.KEY_TEXT_ANTIALIASING,
 			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
 			g2.setRenderingHints(rh);
 			g2.setFont(TBSGraphics.font);
 			Map<PropertyType, Properties> propertiesMap = new TreeMap<PropertyType, Properties>();
 			for(PropertyType pt : PropertyType.values()){
 				if(pt.isLoadedToModel())
 					propertiesMap.put(pt, loadPropertyFile(pt));
 			}
 			TreeMap<String, BufferedImage> organisms = loadOrganismsFromDirectory();
 			String adminStr = getParameter("Admin");
 			boolean admin = Boolean.parseBoolean(adminStr);
 			boolean arrows = true;
 			if(!admin){
 				String hasArrows = getParameter("Arrows");
 				if(hasArrows == null || hasArrows == "")
 					arrows = true;
 				else
 					arrows = Boolean.parseBoolean(hasArrows);
 				String savedTree = getParameter("SavedTree");
 				if(savedTree == null)
 					savedTree = "";
 				else
 					savedTree = savedTree.trim();
 				String q1 = getParameter("quest1");
 				String q2 = getParameter("quest2");
 				String q3 = getParameter("quest3");
 				String name = getParameter("StudentName");
 				model = new StudentModel(app, savedTree, g2, organisms, arrows, propertiesMap);
 	 			model.setQuestion(q1, OpenQuestionButtonType.ONE);
 	 			model.setQuestion(q2, OpenQuestionButtonType.TWO);
 	 			model.setQuestion(q3, OpenQuestionButtonType.THREE);
 	 			model.setName(name);
 			}else{
 				String numOfStudents = getParameter("StudentCount");
 				int studentCt = Integer.parseInt(numOfStudents);
 				List<String> students = new LinkedList<String>();
 				String student;
 				for(int i=1; i<=studentCt; i++){
 					student = getParameter("student"+i);
 					students.add(student);
 				}
 				model = new AdminModel(app, g2, organisms, students, propertiesMap);
 			}
 			add(model.getView());
 			model.getView().addMouseListener(model.getController());
 			model.getView().addMouseMotionListener(model.getController());
 			model.getView().addKeyListener(model.getController());
 		}});
 	}
 

	/**
	* Initialize ModelElements with names and images of Organisms for
	* this task.
	* The initial data for this program is a flat comma-delimited text file 
	* containing each organism's name, name of the image file containing
	* its icon, and the class to which it is assigned for scoring
	* purposes. LoadImageOrganisms reads and parses this text file to
	* populate the ModelElements list with the organisms the student is
	* to use in building his or her tree.
	*/
 	public TreeMap<String, BufferedImage> loadOrganismsFromDirectory() 
	{
		TreeMap<String, BufferedImage> organismNameToImage = 
			new TreeMap<String, BufferedImage>();
		Properties props;
		String imageFilename = "";
		try 
		{
			props = loadPropertyFile(PropertyType.ORGANISMS);
			if(props.size() == 0)
				return new TreeMap<String, BufferedImage>();
			TBSGraphics.numOfOrganisms = props.size();
			BufferedImage img = null;
			String name, value;
			String[] splitValue;
			for(Map.Entry<Object,Object> entry : props.entrySet()){
				name = entry.getKey().toString().replace("_", " ");
				value = entry.getValue().toString();
				splitValue = value.split(",");
				imageFilename = "images/" + splitValue[0];
				URL imageURL = this.getClass().getResource(imageFilename);
				URLConnection imageconn=
					(URLConnection) imageURL.openConnection(); 
				imageconn.setRequestProperty("REFERER",
						getDocumentBase().toString()); 
				InputStream imageis=imageconn.getInputStream();
				img = ImageIO.read(imageis);
				organismNameToImage.put(name, img);
				imageis.close();
			}
 		} catch (Exception e){
 			System.out.println("Error loading image " + imageFilename + ": " + e);
 			return organismNameToImage;
 		} 
 		return organismNameToImage;
	}


	/**
	* Read a file and return a list of Strings, one String per Node.
	*/
	public List<String> loadTreeFile(String fileName)
	{
		URL fileURL = null;	
		URLConnection conn = null;
		InputStream is= null;
		BufferedReader reader = null;
 		ArrayList<String> lines = new ArrayList<String>();
		String line = "";
	try{
		fileURL=new URL(getCodeBase(), fileName);
		System.out.println("Got a file");
		 conn = (URLConnection) fileURL.openConnection();
		conn.setRequestProperty("REFERER", getDocumentBase().toString());
		 is = conn.getInputStream();
		reader= new BufferedReader(new
			InputStreamReader(is));
		line = null;
		while ((line = reader.readLine()) != null)
		{
		 	lines.add(line);
		}
	is.close();	
	}//end try block
	

	catch (Exception e)
	{
		System.out.println("loadTreeFile: "+e);
		e.printStackTrace();

	}
	return lines;
	}//end loadTreeFile
	
	public String getTree(){ return model.exportTree(); }
	
	public String getQ1(){return model.getQuestion(OpenQuestionButtonType.ONE);}
	
	public String getQ2(){return model.getQuestion(OpenQuestionButtonType.TWO);}
	
	public String getQ3(){
		return model.getQuestion(OpenQuestionButtonType.THREE);
	}
	
	public Properties loadPropertyFile(PropertyType pt){
		Properties props = new Properties();
		String filename = "/tbs/properties/"+pt.getFilename();
		try{
			props.load(this.getClass().getResource(filename).openStream());
			return props;
		}catch(Exception e){
			System.out.println("Unable to load " + filename + ": " + e);
			return new Properties();
		}
	}
}

