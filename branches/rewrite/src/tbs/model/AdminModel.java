//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import javax.swing.JComponent;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.controller.AdminController;
import tbs.controller.TBSController;
import tbs.model.admin.Student;
import tbs.model.history.Unlink;
import tbs.properties.PropertyType;
import tbs.view.AdminView;
import tbs.view.TBSButtonType;
import tbs.view.TextEntryBox;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.admin.AnalysisPrompt;
import tbs.view.prompt.admin.OpenQuestionReviewPrompt;

public class AdminModel implements TBSModel
{
	private AdminView view;
	private Boolean admin;
	private TBSController controller;
	private List<TBSButtonType> buttons;
	private List<ModelElement> modelElements;
	private int MESerialNumber=0;
	private TBSApplet applet;
	private Prompt prompt;
	private OpenQuestionReviewPrompt openQuestionReviewPrompt;
	private AnalysisPrompt analysisPrompt;
	private TextEntryBox textEntryBox;
	private Map<PropertyType, Properties> propertiesMap;
	private List<Student> students;
	private Student selectedStudent;
	
	public AdminModel(TBSApplet app, Graphics2D g2,
			TreeMap<String, BufferedImage> organismNameToImage,
			List<String> students, Map<PropertyType, Properties> propertiesMap) {
		applet = app;
		this.propertiesMap = propertiesMap;
		buttons = TBSButtonType.getButtons(true);
		modelElements = new LinkedList<ModelElement>();
		createButtons(g2); // call before creating model elements
		createModelElements(g2, organismNameToImage);
		createStudents(g2, students);
		selectedStudent = this.students.get(0);
		if(!"".equals(selectedStudent.getTree()))
			loadTree(selectedStudent.getTree());
		openQuestionReviewPrompt = new OpenQuestionReviewPrompt(this);
		view = new AdminView(this);
		this.admin = true;
		controller = new AdminController(this, view);
	}
	
	public void changeSavedTree(int studentIndex){
		selectedStudent = students.get(studentIndex);
		if(!"".equals(selectedStudent.getTree()))
			loadTree(selectedStudent.getTree());
		else
			resetModel();
		openQuestionReviewPrompt = null;
		analysisPrompt = null;
	}
	
	public void resetModel(){
		while(modelElements.size() > TBSGraphics.numOfOrganisms+1)
			removeFromTree(modelElements.get(modelElements.size()-1));
		List<Node> inTreeElements = inTreeElements();
		for(Node n : inTreeElements){
			removeFromTree(n);
		}
	}
	
	public void removeFromTree(ModelElement m){
		if(m == null)
			return;
		if(m instanceof Node){
			Node n = (Node) m;
			unlink(n);
			if(n instanceof OrganismNode){
				n.setInTree(false);
				((OrganismNode) n).resetPosition();
				return;
			}
		}else{
			Connection c = (Connection) m;
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
		}
		modelElements.remove(m);
	}
	
	/**
	* Unlink had to live in Model when connections were
	* one-way. Now, this simply calls the Node-based two-way unlink.
	*/
	public void unlink(Node n)
	{
		List<Connection> connections = getConnectionsByNode(n);
		for(Connection c : connections){
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			modelElements.remove(c);
		}
	}
	
	public void setModelElements(List<ModelElement> newList){
		modelElements = newList;
	}
	
	public List<Student> getStudents(){
		return students;
	}
	
	/**
	* Returns a handle for the View associated with this Model
   */
	public JComponent getView() {
		return view;
	}
	
	/**
	* Returns a handle for the Controller associated with this Model.
	*/
	public TBSController getController() {
		return controller;
	}
	
	/**
	* Returns a handle for the Applet.
	*/
	public TBSApplet getApplet() {
		return applet;
	}	
	
	/**
	* Returns a serial number for a model element. Serial numbers start
	* at 0 and simply increment; they are unique within a tree, but not
	* outside it.
	*/
	public int getSerial()
	{
		int sn = MESerialNumber;
		System.out.println(MESerialNumber);
		MESerialNumber++;
		return sn;
	}

	/**
	* Adds a ModelElement to the ArrayList of items this Model knows
	* about.
	*/
	public void addElement(ModelElement m) {
		modelElements.add(m);
	}	
	
	/**
	* returns the ith ModelElement in the list.
	*/
	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}

	public void removeElement(int i) {
		modelElements.remove(i);
	}
	
	public int findIndexByElement(ModelElement m){
		/*
		 * For OrganismNodes we can just use serialId
		 * because they are the first loaded into the List and never actually removed from the
		 * List.
		 */		
		if(m instanceof OrganismNode)
			return (m.getId());
		else
			return modelElements.indexOf(m);
	}
	
	//findIndexById method that is called when a saved tree is not being loaded
	public int findIndexById(Integer id){
		return findIndexById(id, null);
	}
	
	public int findIndexById(Integer id, List<ModelElement> parsedElements){
		if(id <= TBSGraphics.numOfOrganisms)
			return (id);
		/*
		 * As you can see we can begin searching the List of ModelElement
		 * objects from where the number of loaded organisms ends since these
		 * items (with the exception of the immortalEmptyNode can be removed
		 * and thus have their serialId changed/out of order.
		 */
		List<ModelElement> elements;
		if(parsedElements != null)
			elements = parsedElements;
		else
			elements = modelElements;
		for(int i=(TBSGraphics.numOfOrganisms-1);i<elements.size();i++){
			if(elements.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}	

	/**
	* Returns the ModelElement with a given serial number
	* This method relies on the fact that objects are added in serial#
	* order, and remain sorted, although they may be deleted. If this
	* assumption ceases to be true, this method will fail. 
	*/
	public ModelElement getElementBySN(int sn)
	{
		ModelElement me;
		int checknum = sn;
		List<ModelElement> model = modelElements;
		do 
		{
			 me = (ModelElement)model.get(checknum);
			if (me.getId() == sn)
				return me;
			checknum--;
		} while (checknum >= me.getId());
		return null;
	}
	
	/**
	* returns the complete List of Model Elements.
	*/
	public List<ModelElement> getElements() {
		return modelElements;
	}

	/**
	* Assigns value me to the ith member of the list. 
	*/
	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}

	public void createButtons(Graphics2D g2)
	{
		Dimension buttonDimensions = TBSGraphics.get2DStringBounds(g2,buttons);
		TBSGraphics.buttonsWidth = buttonDimensions.width + 
				TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonDimensions.height + 
				TBSGraphics.buttonsYPadding * 2;
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics2D g2, 
				TreeMap<String, BufferedImage> organismNameToImage) {
		EmptyNode.g2 = g2;
		for(Map.Entry<String, BufferedImage> e : organismNameToImage.entrySet()) {
			addElement(new OrganismNode( getSerial(), e.getKey(), 
				new Point(), e.getValue()));
		}

		TBSGraphics.emptyNodeLeftX = 0;
		TBSGraphics.emptyNodeUpperY = 0;
	}
	
	protected void createStudents(Graphics2D g2, List<String>  studentStringArrays) {
		int currentY = 0;
		students = new LinkedList<Student>();
		for(String studentStringArray : studentStringArrays){
			Student temp = new Student(g2, studentStringArray);
			students.add(temp);
			if(temp.getWidth() > TBSGraphics.studentNodeWidth) 
				TBSGraphics.studentNodeWidth = temp.getWidth();
			if(temp.getHeight() > TBSGraphics.studentNodeHeight) 
				TBSGraphics.studentNodeHeight = temp.getHeight();
		}
		Dimension checkDimension = TBSGraphics.getStringBounds(g2, " \u2713");
		TBSGraphics.studentNodeWidth += checkDimension.width;
		TBSGraphics.studentNodeWidth += TBSGraphics.paddingWidth * 2;
		TBSGraphics.studentNodeHeight += TBSGraphics.paddingWidth * 2;
		for(Student s : students) {
			s.setAnchorPoint(new Point(0, currentY));
			currentY += TBSGraphics.studentNodeHeight + TBSGraphics.ySpacing;
		}
	}	

	/**
	* PrintConnections() prints out a list of all connections in each
	* model element. 
	* Connection to a Node (toConnection) is indicated by ->
	* Trace connection from a Node (fromConnection) indicated by <-
	* Written for testing connections; functionality may not have
	* survived rewrite of connections methodology.
	*/
	public void printConnections()
	{
		Node n;
		for(ModelElement m : modelElements){
			if(m instanceof Node){
				n = (Node) m;
				for(Node to : n.getConnectedTo())
					System.out.println(n.getName()+" -> "+to.getName());
				for(Node from : n.getConnectedFrom())
					System.out.println(from.getName()+" -> "+n.getName());
			}
		}
	}

	public boolean hasConnections(){
		for(int i=TBSGraphics.numOfOrganisms;i<modelElements.size(); i++){
			if(modelElements.get(i) instanceof Connection)
				return true;
		}
		return false;
	}
	
	public boolean hasEmptyNodes(){
		for(int i=(TBSGraphics.numOfOrganisms-1);i<modelElements.size();i++){
			if(modelElements.get(i) instanceof EmptyNode)
				return true;
		}
		return false;
	}
	
	/**
	* Returns the list of active elements
	*/	
	public List<Node> inTreeElements(){
		List<Node> inTreeElements = new LinkedList<Node>();
		for(ModelElement m : modelElements){
			if(m instanceof Node){
				Node n = (Node) m;
				if(n.isInTree())
					inTreeElements.add(n);
			}
		}
		return inTreeElements;
	}
	
	public Properties getProperties(PropertyType pt) {
		return propertiesMap.get(pt);
	}

	public List<Connection> getConnectionsByNode(Node n){
		Unlink unlink = new Unlink();
		unlink.setNode(n);
		List<Connection> connections = new LinkedList<Connection>();
		Connection c;
		for (ModelElement me: modelElements)
		{
			if(me instanceof Connection){
				c = (Connection) me;
				if(c.hasNode(n)){
					connections.add(c);
					try{
						unlink.addConnection((Connection) c.clone());
					}catch(CloneNotSupportedException e){
						System.out.println("Unable to create connection clone.");
					}
				}
			}
		}
		return connections;
	}

	/**
	* Take a list of strings extracted from a file by
	* the perl script contained within the website, and recreate the stored tree.
	* Two passes: first pass recreates nodes, second makes connections. 
	*/
	public void loadTree(String tree)
	{
		List<ModelElement> savedTree = new LinkedList<ModelElement>();
		String[] treeItems = tree.split("#");
		try{
			for(String item : treeItems)
			{
				String data[] = item.split(":");
				if (data[0].equals("O"))
					savedTree.add(loadOrganismNode(data));
				else if (data[0].equals("E")){
					ModelElement temp = loadEmptyNode(data);
					if(((EmptyNode) temp).isInTree())
						savedTree.add(temp);
				}else if (data[0].equals("C"))
					savedTree.add(loadConnection(data, savedTree));
				else
				{
					System.out.println("Problem in loadTree");
					break;
				}
			}

			// Sort the local list
			Collections.sort(savedTree, TBSGraphics.elementIdComparator);
			modelElements = savedTree;
			MESerialNumber = savedTree.size()+1;
			System.out.println("loadTree: end");
		}catch(NumberFormatException e){
			System.out.println("There was an error parsing saved tree for " + selectedStudent.getName() + ". " + 
			"This tree has been reset.");
		}
	}

	/**
	 * Load an OrganismNode. Might be possible to combine this with
	 * loadEmptyNode(). 
	 * This does not create any new OrganismNodes; it simply resets the
	 * old ones and sets their values where we want them. This saves
	 * reloading the image files, but it means we have to always use the
	 * same set of organisms. 
	 */
	public ModelElement loadOrganismNode(String[] data) throws NumberFormatException {
		int id=0,x=0,y=0;
		try{
			id = Integer.parseInt(data[1]);
			x = Integer.parseInt(data[3]);
			y = Integer.parseInt(data[4]);
		}catch(NumberFormatException e){
			System.out.println("StudentModel:loadOrganismNode:Error parsing organism data (id:" + data[1] +
					",x:" + data[3] + "y:" + data[4]+")");
			throw e;
		}
		ModelElement me = getElementBySN(id);
		OrganismNode node = (OrganismNode) me;
		Point pt = new Point(x,y);
		node.setAnchorPoint(pt);
		node.setInTree(Boolean.parseBoolean(data[5]));
		return (ModelElement) node;
	}

	/**
	 * Load an EmptyNode. Might be possible to combine this with
	 * loadOrganismNode().
	 */
	public ModelElement loadEmptyNode(String[] data) throws NumberFormatException {
		String name = data[2];
		int id=0,x=0,y=0;
		try{
			id = Integer.parseInt(data[1]);
			x = Integer.parseInt(data[3]);
			y = Integer.parseInt(data[4]);
		}catch(NumberFormatException e){
			System.out.println("StudentModel:loadEmptyNode:Error parsing empty node (" +
					name + ") data (id:" + data[1] +
					",x:" + data[3] + "y:" + data[4]+")");
			throw e;
		}
		Point pt = new Point(x,y);
		Boolean in = (data[5].equals("true")?true:false);
		if(!in)
			pt = new Point();
		EmptyNode node = new EmptyNode(id, pt);
		node.rename(name);
		node.setInTree(in);
		return (ModelElement) node;
	}

	public ModelElement loadConnection(String[] data, List<ModelElement> parsedElements) 
	throws NumberFormatException {
		int id=0,from=0,to=0;
		try{
			id = Integer.parseInt(data[1]);
			from = Integer.parseInt(data[2]);
			to = Integer.parseInt(data[3]);
		}catch(NumberFormatException e){
			System.out.println("StudentModel:loadConnection:Error parsing connection data (id:" + data[1] +
					",from id:" + data[2] + "to id:" + data[3]+")");
			throw e;
		}
		int fromIndex = findIndexById(from, parsedElements);
		int toIndex = findIndexById(to, parsedElements);

		Node fromNode = (Node) parsedElements.get(fromIndex);
		Node toNode = (Node) parsedElements.get(toIndex);
		Connection conn = new Connection(id, fromNode, toNode);
		return (ModelElement) conn;
	}

	public String exportTree(){
		StringBuilder export = new StringBuilder();
		for(ModelElement m : modelElements)
			export.append(m.dump() + "#");
		return export.toString();

	}
	
	public Prompt getPrompt() {
		return prompt;
	}
	
	public void clearPrompt() {
		this.prompt = null;
	}
	
	public void promptUser(Prompt prompt) {
		this.prompt = prompt;
		view.refreshGraphics();
	}
	
	public void questionReview() {
		if(openQuestionReviewPrompt == null)
			openQuestionReviewPrompt = new OpenQuestionReviewPrompt(this);
		this.prompt = openQuestionReviewPrompt;
		view.refreshGraphics();
	}
	
	public void analyze(){
		if(analysisPrompt == null)
			analysisPrompt = new AnalysisPrompt(this);
		this.prompt = analysisPrompt;
		view.refreshGraphics();
	}

	public TextEntryBox getTextEntryBox() {
		return textEntryBox;
	}

	public void setTextEntryBox(TextEntryBox textEntryBox) {
		this.textEntryBox = textEntryBox;
	}
	
	public Boolean isAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}

	public List<TBSButtonType> getButtons() {
		return buttons;
	}

	public Boolean isButtonActive(TBSButtonType b) {return false;}

	public Student getStudent() {return selectedStudent;}

}
