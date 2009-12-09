//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.TBSPrompt;
import tbs.controller.TBSController;
import tbs.model.history.Add;
import tbs.model.history.Command;
import tbs.model.history.Delete;
import tbs.model.history.Drag;
import tbs.model.history.Link;
import tbs.model.history.Unlink;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;

public class TBSModel 
{
	private TBSView view;
	private TBSController controller;
	private List<ModelElement> modelElements;
	private ModelElement selectedModelElement;
	private List<ModelElement> selectedTwoWay;
	private EmptyNode immortalEmptyNode;
	private Stack<Command> history;
	private int MESerialNumber=0;
	private TBSApplet applet;
	private TBSPrompt prompt;

	public TBSModel(TBSApplet app, String savedTree, Graphics g,
			TreeMap<String, BufferedImage> organismNameToImage) {
		modelElements = new LinkedList<ModelElement>();
		selectedModelElement = null;
		selectedTwoWay = null;
		createButtons(g); // call before creating model elements
		createModelElements(g, organismNameToImage);
		if(savedTree != null && savedTree.length() > 0)
			loadTree(savedTree);
		view = new TBSView(this);
		controller = new TBSController(this, view);
		history = new Stack<Command>();
		applet = app;
	}

	public void setModelElements(List<ModelElement> newList)
	{
		modelElements = newList;
	}
	
	public void resetModel(){
		while(modelElements.size() > TBSGraphics.numOfOrganisms+1)
			removeFromTree(modelElements.get(modelElements.size()-1));
		List<Node> inTreeElements = inTreeElements();
		for(Node n : inTreeElements){
			removeFromTree(n);
		}
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
		MESerialNumber ++;
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
		List <ModelElement> model = modelElements;
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
	
	public void createButtons(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		TBSGraphics.getFont(g2);
		Point buttonBounds = TBSGraphics.get2DStringBounds(g2,
				Arrays.asList(TBSButtonType.values()));
		TBSGraphics.buttonsWidth = buttonBounds.x + 
				TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonBounds.y + 
				TBSGraphics.buttonsYPadding * 2;
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics g, 
				TreeMap<String, BufferedImage> organismNameToImage) {
		Graphics2D g2 = (Graphics2D) g;
		EmptyNode.g2 = g2;
		BufferedImage img = null;
		int currentX = 0;
		int currentY = TBSGraphics.buttonsHeight + 10;
		String organismName = "";
		Collection<String> organismNames = organismNameToImage.keySet();
		Collection<BufferedImage> organismImages = organismNameToImage.values();
		Point stringBounds = TBSGraphics.get2DStringBounds(g2, organismNames);
		Point imageBounds = TBSGraphics.get2DImageBounds(g2, organismImages);
		TBSGraphics.organismNodeWidth = stringBounds.x + imageBounds.x + 
				TBSGraphics.paddingWidth * 2;
		TBSGraphics.organismNodeHeight = imageBounds.y;
		int y0 = stringBounds.y;
		int y1 = imageBounds.y;
		if(y0 > y1) {
			TBSGraphics.organismNodeHeight = y0;
		} else {
			TBSGraphics.organismNodeHeight = y1;
		}
		Iterator<String> itr = organismNames.iterator();
		while(itr.hasNext()) {
			organismName = itr.next();
			img = organismNameToImage.get(organismName);
			addElement(new OrganismNode( getSerial(), organismName, 
				new Point(currentX, currentY), img));
			currentY += TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing;
		}

		//create left-side empty node
		TBSGraphics.emptyNodeLeftX = TBSGraphics.organismNodeWidth / 2;
		TBSGraphics.emptyNodeUpperY = currentY + TBSGraphics.organismNodeHeight / 2;
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
		} else {
			n.setInTree(true);
			newNode = n;
		}
		if(history.peek() instanceof Drag)
			history.pop();
		try{
			history.push(new Add((Node) newNode.clone()));
			System.out.println("Added action(add) to history.");
		}catch(CloneNotSupportedException c){
			System.out.println("Unable to add action to history.");
		}
	}
	
	public void addConnection(Node from, Node to)
	{
		Connection newConn = new Connection(getSerial(), from, to);
		modelElements.add(newConn);
		from.addConnectionTo(to);
		to.addConnectionFrom(from);
		try{
			history.push(new Link((Connection) newConn.clone()));
			System.out.println("Added action(link) to history.");
		}catch(CloneNotSupportedException c){
			System.out.println("Unable to add action to history.");
		}
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
			history.push(unlink);
			System.out.println("Added action(unlink) to history.");
		}
		return connections;
	}
	
	/**
	* Unlink had to live in Model when connections were
	* one-way. Now, this simply calls the Node-based two-way unlink.
	*/
	public void unlink(Node n)
	{
		modelElements.removeAll(getConnectionsByNode(n));
		n.unlink();
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
			if(controller.getButtonClicked().equals(TBSButtonType.DELETE)){
				try{
					history.push(new Delete((Node) n.clone()));
					System.out.println("Added action(node delete) to history.");
				}catch(CloneNotSupportedException e){
					System.out.println("Unable to add action to history.");
				}
			}
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
			if(controller.getButtonClicked().equals(TBSButtonType.DELETE)){
				try{
					if(selectedTwoWay != null){
						Command command = history.peek();
						if(command instanceof Delete && ((Delete) command).getTwoWayConnection() != null)
							((Delete) command).addConnection((Connection) c.clone());
						else{
							history.push(new Delete());
							((Delete) history.peek()).addConnection((Connection) c.clone());
							System.out.println("Added action(two-way connection delete) to history.");
						}
					}else{
						history.push(new Delete((Connection) c.clone()));
						System.out.println("Added action(connection delete) to history.");
					}
				}catch(CloneNotSupportedException e){
					System.out.println("Unable to add action to history.");
				}
			}
		}
		modelElements.remove(m);
	}

	/**
	* Take a list of strings extracted from a file by
	* Applet.loadTreeFile(), and recreate the stored tree.
	* Two passes: first pass recreates nodes, second makes connections. 
	*/
	public void loadTree(String tree)
	{
		List<ModelElement> savedTree = new LinkedList<ModelElement>();
		String[] treeItems = tree.split("\n");
		for(String item : treeItems)
		{
			String data[] = item.split(":");
			if (data[0].equals("O"))
				savedTree.add(loadOrganismNode(data));
			else if (data[0].equals("E"))
				savedTree.add(loadEmptyNode(data));
			else if (data[0].equals("C"))
				savedTree.add(loadConnection(data));
			else
			{
				System.out.println("Problem in loadTree");
				break;
			}
		}
		Comparator<ModelElement> elementIdComparator = new Comparator<ModelElement>() {
			public int compare( ModelElement o1, ModelElement o2 ) {
				return o1.getId().compareTo(o2.getId());
			}
		};

		// Sort the local list
		Collections.sort(savedTree, elementIdComparator);
		modelElements = savedTree;
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
			export.append(m.dump() + "\n");
		return export.toString();

	}
	
	public TBSPrompt getPrompt() {
		return prompt;
	}
	
	public void clearPrompt() {
		this.prompt = null;
	}
	
	public void promptUser(TBSPrompt prompt) {
		this.prompt = prompt;
		view.refreshGraphics();
	}
   
}
