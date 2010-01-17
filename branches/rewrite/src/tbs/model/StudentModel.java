//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.TreeMap;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.controller.StudentController;
import tbs.controller.TBSController;
import tbs.model.history.Add;
import tbs.model.history.Command;
import tbs.model.history.Delete;
import tbs.model.history.Drag;
import tbs.model.history.Link;
import tbs.model.history.Unlink;
import tbs.properties.PropertyType;
import tbs.view.OpenQuestionButtonType;
import tbs.view.StudentView;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;
import tbs.view.TextEntryBox;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.HelpPrompt;
import tbs.view.prompt.student.OpenQuestionPrompt;
import tbs.view.prompt.student.WelcomePrompt;

public class StudentModel implements TBSModel
{
	private StudentView view;
	private Boolean admin;
	private StudentController controller;
	private List<TBSButtonType> buttons;
	private List<ModelElement> modelElements;
	private ModelElement selectedModelElement;
	private List<ModelElement> selectedTwoWay;
	private EmptyNode immortalEmptyNode;
	private Stack<Command> history;
	private int MESerialNumber=0;
	private TBSApplet applet;
	private Prompt prompt;
	private OpenQuestionPrompt openResponsePrompt;
	private HelpPrompt helpPrompt;
	private TextEntryBox textEntryBox;
	private Map<PropertyType,Properties> propertiesMap;
	private Map<TBSButtonType, Boolean> buttonStates;
	private String name;
	private String questionOne;
	private String questionTwo;
	private String questionThree;
	private Boolean hasArrows;
	public StudentModel(TBSApplet app, String savedTree, Graphics2D g2,
			TreeMap<String, BufferedImage> organismNameToImage,
			Boolean hasArrows, Map<PropertyType, Properties> propertiesMap) {
		applet = app;
		this.propertiesMap = propertiesMap;
		buttons = TBSButtonType.getButtons(false);
		modelElements = new LinkedList<ModelElement>();
		selectedModelElement = null;
		selectedTwoWay = null;
		createButtons(g2); // call before creating model elements
		createModelElements(g2, organismNameToImage);
		buttonStates = new HashMap<TBSButtonType, Boolean>();
		for(TBSButtonType b : buttons)
			buttonStates.put(b, b.isActiveWhenCreated());
		if(!"".equals(savedTree)){
			loadTree(savedTree);
			if(inTreeElements().size() > 1){
				buttonStates.put(TBSButtonType.LINK, true);
				buttonStates.put(TBSButtonType.DELETE, true);
				buttonStates.put(TBSButtonType.CLEAR, true);
				if(hasEmptyNodes())
					buttonStates.put(TBSButtonType.LABEL, false);
			}
		}
		view = new StudentView(this);
		this.admin = false;
		this.hasArrows = hasArrows;
		controller = new StudentController(this, view);
		history = new Stack<Command>();
		prompt = new WelcomePrompt(this);
		helpPrompt = new HelpPrompt(this);
		openResponsePrompt = new OpenQuestionPrompt(this);
	}
	
	public void changeSavedTree(String savedTree){
		if(!"".equals(savedTree)){
			loadTree(savedTree);
			if(inTreeElements().size() > 1){
				buttonStates.put(TBSButtonType.LINK, true);
				buttonStates.put(TBSButtonType.DELETE, true);
				buttonStates.put(TBSButtonType.CLEAR, true);
				if(hasEmptyNodes())
					buttonStates.put(TBSButtonType.LABEL, false);
			}
		}
		history = new Stack<Command>();		
	}

	public void setModelElements(List<ModelElement> newList){
		modelElements = newList;
	}
	
	public void resetModel(){
		while(modelElements.size() > TBSGraphics.numOfOrganisms+1)
			removeFromTree(modelElements.get(modelElements.size()-1));
		List<Node> inTreeElements = inTreeElements();
		for(Node n : inTreeElements){
			removeFromTree(n);
		}
		history = new Stack<Command>();
		buttonStates.put(TBSButtonType.UNDO, false);
	}
	
	/**
	* Returns a handle for the View associated with this Model
   */
	public TBSView getView() {
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
	
	public int findIndexById(Integer id){
		if(id <= TBSGraphics.numOfOrganisms)
			return (id);
		/*
		 * As you can see we can begin searching the List of ModelElement
		 * objects from where the number of loaded organisms ends since these
		 * items (with the exception of the immortalEmptyNode can be removed
		 * and thus have their serialId changed/out of order.
		 */
		for(int i=(TBSGraphics.numOfOrganisms-1);i<modelElements.size();i++){
			if(modelElements.get(i).getId().equals(id))
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
	
	public ModelElement getSelectedModelElement() {
		return selectedModelElement;
	}

	public void setSelectedModelElement(ModelElement selectedModelElement) {
		this.selectedModelElement = selectedModelElement;
	}

	public List<ModelElement> getSelectedTwoWay() {
		return selectedTwoWay;
	}

	public void setSelectedTwoWay(List<ModelElement> selectedTwoWay) {
		this.selectedTwoWay = selectedTwoWay;
	}

	/**
	* Assigns value me to the ith member of the list. 
	*/
	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}

	public EmptyNode getImmortalEmptyNode() {
		return immortalEmptyNode;
	}
	
	public Stack<Command> getHistory() {
		return history;
	}

	public void setHistory(Stack<Command> history) {
		this.history = history;
	}
	
	public void addActionToHistory(Command c){
		if(history.isEmpty())
			buttonStates.put(TBSButtonType.UNDO, true);
		history.push(c);
		System.out.println("Added action(" + c.toString() + ") to history.");
	}
	
	public Command removeActionFromHistory(){
		Command c = history.pop();
		if(c instanceof Unlink)
			buttonStates.put(TBSButtonType.UNLINK, true);
		if(history.isEmpty())
			buttonStates.put(TBSButtonType.UNDO, false);
		return c;
	}
	
	public void createButtons(Graphics2D g2)
	{
		Dimension buttonDimensions = TBSGraphics.get2DStringBounds(g2,buttons);
		TBSGraphics.buttonsWidth = buttonDimensions.width + 
				TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonDimensions.height + 
				TBSGraphics.buttonsYPadding * 2;
		
		buttonDimensions = TBSGraphics.get2DStringBounds(g2,
				Arrays.asList(OpenQuestionButtonType.values()));
		TBSGraphics.questionButtonsWidth = buttonDimensions.width + 
				TBSGraphics.buttonsXPadding * 2;
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics2D g2, 
				TreeMap<String, BufferedImage> organismNameToImage) {
		EmptyNode.g2 = g2;
		int currentY = TBSGraphics.buttonsHeight + 10;
		Dimension stringDimensions = TBSGraphics.get2DStringBounds(g2, organismNameToImage.keySet());
		Dimension imageDimensions = TBSGraphics.get2DImageBounds(g2, organismNameToImage.values());
		TBSGraphics.organismNodeWidth = stringDimensions.width + imageDimensions.width + 
				TBSGraphics.paddingWidth * 2;
		if(stringDimensions.height > imageDimensions.height)
			TBSGraphics.organismNodeHeight = stringDimensions.height;
		else
			TBSGraphics.organismNodeHeight = imageDimensions.height;
		for(Map.Entry<String, BufferedImage> e : organismNameToImage.entrySet()) {
			addElement(new OrganismNode( getSerial(), e.getKey(), 
				new Point(0, currentY), e.getValue()));
			currentY += TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing;
		}

		//create left-side empty node
		TBSGraphics.immortalNodeLabelWidth = (int) TBSGraphics.getStringBounds(g2, TBSGraphics.immortalNodeLabel).getWidth();
		TBSGraphics.emptyNodeLeftX = (TBSGraphics.organismNodeWidth - (TBSGraphics.emptyNodeWidth + TBSGraphics.immortalNodeLabelWidth)) / 2;
		TBSGraphics.emptyNodeUpperY = currentY + ((TBSGraphics.organismNodeHeight - TBSGraphics.emptyNodeHeight)/2);
		/*
		 * If you use this line it will make the positioning of the immortal node
		 * like the default positioning of an organism node
		 */		
		//TBSGraphics.emptyNodeUpperY = currentY + TBSGraphics.ySpacing;
		immortalEmptyNode = new EmptyNode(getSerial());
		addElement(immortalEmptyNode);
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
	
	public void addToTree(Node n)
	{
		Node newNode;
		if (n.equals(immortalEmptyNode)) {
			newNode = new EmptyNode(getSerial(), n.getAnchorPoint());
			modelElements.add(newNode);
			n.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
			buttonStates.put(TBSButtonType.LABEL, true);
		} else {
			n.setInTree(true);
			newNode = n;
		}
		if(history.peek() instanceof Drag)
			removeActionFromHistory();
		try{
			addActionToHistory(new Add((Node) newNode.clone()));
		}catch(CloneNotSupportedException c){
			System.out.println("Unable to add action to history.");
		}
		buttonStates.put(TBSButtonType.DELETE, true);
		if(inTreeElements().size() > 1)
			buttonStates.put(TBSButtonType.LINK, true);
		buttonStates.put(TBSButtonType.CLEAR, true);
	}
	
	public void addConnection(Node from, Node to){
		addConnection(from, to, -1);
	}
	
	public void addConnection(Node from, Node to, int id)
	{
		Connection newConn = new Connection(id == -1 ? getSerial() : id, from, to);
		if(id == -1)
			modelElements.add(newConn);
		else
			modelElements.add(id, newConn);
		from.addConnectionTo(to);
		to.addConnectionFrom(from);
		if(!controller.getButtonClicked().equals(TBSButtonType.UNDO)){
			try{
				addActionToHistory(new Link((Connection) newConn.clone()));
			}catch(CloneNotSupportedException c){
				System.out.println("Unable to add action to history.");
			}
		}
		buttonStates.put(TBSButtonType.UNLINK, true);
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
		if(controller.getButtonClicked().equals(TBSButtonType.UNLINK)){
			addActionToHistory(unlink);
		}
		return connections;
	}
	
	/**
	* Unlink had to live in Model when connections were
	* one-way. Now, this simply calls the Node-based two-way unlink.
	*/
	public void unlink(Node n)
	{
		List<Connection> connections = getConnectionsByNode(n);
		if(!history.isEmpty()){
		Command comm = history.peek();
		if(comm instanceof Delete)
			((Delete) comm).setElementConnections(connections);
		}
		for(Connection c : connections){
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			modelElements.remove(c);
		}
	}
	
	public void removeFromTree(ModelElement m){
		if(m == null)
			return;
		if(m.equals(immortalEmptyNode)){
			immortalEmptyNode.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX,
					TBSGraphics.emptyNodeUpperY));
			return;
		}
		if(m instanceof Node){
			Node n = (Node) m;
			Command c = history.peek();
			try{
			if(controller.getButtonClicked().equals(TBSButtonType.DELETE) 
					|| c instanceof Drag){
				if(c instanceof Drag){
					Node copy = (Node) n.clone();
					copy.setAnchorPoint(((Drag) c).getPointBefore());
					removeActionFromHistory();
					System.out.println("Invalid drag move removed from history.");
					addActionToHistory(new Delete(copy));
				}else
					addActionToHistory(new Delete((Node) n.clone()));
			}
			}catch(CloneNotSupportedException e){
				System.out.println("Unable to add action to history.");
			}
			unlink(n);
			if(n instanceof OrganismNode){
				n.setInTree(false);
				((OrganismNode) n).resetPosition();
				updateButtonStatesAfterRemove();
				return;
			}
		}else{
			Connection c = (Connection) m;
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			if(controller.getButtonClicked().equals(TBSButtonType.DELETE)){
				try{
					if(selectedTwoWay != null){
						Command command = history.peek();
						if(command instanceof Delete && ((Delete) command).getTwoWayConnection() != null)
							((Delete) command).addConnection((Connection) c.clone());
						else{
							addActionToHistory(new Delete());
							((Delete) history.peek()).addConnection((Connection) c.clone());
						}
					}else{
						addActionToHistory(new Delete((Connection) c.clone()));
					}
				}catch(CloneNotSupportedException e){
					System.out.println("Unable to add action to history.");
				}
			}
		}
		modelElements.remove(m);
		updateButtonStatesAfterRemove();
	}
	
	public void updateButtonStatesAfterRemove(){
		if(!hasConnections())
			buttonStates.put(TBSButtonType.UNLINK, false);
		List<Node> inTree = inTreeElements();
		if(inTree.size() == 0){
			buttonStates.put(TBSButtonType.LINK, false);
			buttonStates.put(TBSButtonType.DELETE, false);
			buttonStates.put(TBSButtonType.LABEL, false);
			buttonStates.put(TBSButtonType.CLEAR, false);
		}else if(inTree.size() < 2)
			buttonStates.put(TBSButtonType.LINK, false);
		else if(!hasEmptyNodes())
			buttonStates.put(TBSButtonType.LABEL, false);
	}

	/**
	* Take a list of strings extracted from a file by
	* the perl script contained within the website, and recreate the stored tree.
	* Two passes: first pass recreates nodes, second makes connections. 
	*/
	public void loadTree(String tree)
	{
		List<ModelElement> savedTree = new LinkedList<ModelElement>();
		EmptyNode savedImmortalEmptyNode = null;
		String[] treeItems = tree.split("#");
		for(String item : treeItems)
		{
			String data[] = item.split(":");
			if (data[0].equals("O"))
				savedTree.add(loadOrganismNode(data));
			else if (data[0].equals("E")){
				ModelElement temp = loadEmptyNode(data);
				if(!((EmptyNode) temp).isInTree())
					savedImmortalEmptyNode = (EmptyNode) temp;
				savedTree.add(temp);
			}else if (data[0].equals("C"))
				savedTree.add(loadConnection(data));
			else
			{
				System.out.println("Problem in loadTree");
				break;
			}
		}
	
		// Sort the local list
		Collections.sort(savedTree, TBSGraphics.elementIdComparator);
		modelElements = savedTree;
		immortalEmptyNode = savedImmortalEmptyNode;
		System.out.println("loadTree: end");
	}

	/**
	 * Load an OrganismNode. Might be possible to combine this with
	 * loadEmptyNode(). 
	 * This does not create any new OrganismNodes; it simply resets the
	 * old ones and sets their values where we want them. This saves
	 * reloading the image files, but it means we have to always use the
	 * same set of organisms. 
	 */
	public ModelElement loadOrganismNode(String[] data)
	{
		int id = Integer.parseInt(data[1]);
		ModelElement me = getElementBySN(id);
		OrganismNode node = (OrganismNode) me;
		int x = Integer.parseInt(data[3]);
		int y = Integer.parseInt(data[4]);
		Point pt = new Point(x,y);
		node.setAnchorPoint(pt);
		node.setInTree(Boolean.parseBoolean(data[5]));
		return (ModelElement) node;
	}

	/**
	 * Load an EmptyNode. Might be possible to combine this with
	 * loadOrganismNode().
	 */
	public ModelElement loadEmptyNode(String[] data)
	{
		int id = Integer.parseInt(data[1]);
		String name = data[2];
		int x = Integer.parseInt(data[3]);
		int y = Integer.parseInt(data[4]);
		Point pt = new Point(x,y);
		Boolean in = (data[5].equals("true")?true:false);
		if(!in)
			pt = new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY);
		EmptyNode node = new EmptyNode(id, pt);
		node.rename(name);
		node.setInTree(in);
		return (ModelElement) node;
	}

	public ModelElement loadConnection(String[] data)
	{
		int id = Integer.parseInt(data[1]);
		int from = Integer.parseInt(data[2]);
		int to = Integer.parseInt(data[3]);
		Node fromNode = (Node) getElementBySN(from);
		Node toNode = (Node) getElementBySN(to);
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
	
	public void viewOpenResponse(OpenQuestionButtonType currentQuestion) {
		openResponsePrompt.setCurrentQuestion(currentQuestion);
		openResponsePrompt.setFinished(false);
		this.prompt = openResponsePrompt;
		view.refreshGraphics();
	}
	
	public void viewPrompt(Prompt prompt){
		this.prompt = prompt;
		view.refreshGraphics();
	}
	
	public void helpUser() {
		this.prompt = helpPrompt;
		view.refreshGraphics();
	}

	public String getQuestion(OpenQuestionButtonType question){
		switch(question){
		case ONE:
			return questionOne;
		case TWO:
			return questionTwo;
		case THREE:
			return questionThree;
		}
		return "";
	}
	
	public void setQuestion(String input, OpenQuestionButtonType question){
		String formattedInput = input == null ? "" : input.trim();
		switch(question){
		case ONE:
			questionOne = formattedInput;
			return;
		case TWO:
			questionTwo = formattedInput;
			return;
		case THREE:
			questionThree = formattedInput;
			return;
		}
	}
	
	public String getName(){
		if(name != null && name != ""){
			String[] splitName = name.split(",");
			StringBuffer nameBuffer = new StringBuffer();
			for(int i=(splitName.length-1);i>=0;i--)
				nameBuffer.append(splitName[i]).append(" ");
			return nameBuffer.toString().trim();
		}else
			return "";
	}
	
	public void setName(String name){
		this.name = name;
	}

	public Map<TBSButtonType, Boolean> getButtonStates() {
		return buttonStates;
	}
	
	public Boolean isButtonActive(TBSButtonType button){
		return buttonStates.get(button);
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
	
	public Boolean hasArrows() {
		return hasArrows;
	}

	public void setHasArrows(Boolean hasArrows) {
		this.hasArrows = hasArrows;
	}
}
