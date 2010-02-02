//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.controller.TBSController;
import tbs.model.admin.Student;
import tbs.view.TBSView;
import tbs.view.prompt.Prompt;


public abstract class TBSModel 
{

	private TBSApplet applet;
	private TBSView view;
	private TBSController controller;
	private Prompt prompt;
	private Student student;
	private List<ModelElement> elements;
	private int MESerialNumber;

	public TBSModel(TBSApplet applet, List<OrganismNode> organisms){
		this.applet = applet;
		elements = new LinkedList<ModelElement>();
		elements.addAll(organisms);
		refreshSerial();
	}

	/**
	 * Returns a handle for the Controller associated with this Model.
	 */
	public TBSController getController() {
		return controller;
	}

	public void setController( TBSController controller ) {
		this.controller = controller;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent( Student selectedStudent ) {
		this.student = selectedStudent;
	}

	/**
	 * Returns a handle for the View associated with this Model
	 */
	public TBSView getView() {
		return view;
	}

	public void setView( TBSView view ) {
		this.view = view;
	}

	/**
	 * Returns a handle for the Applet.
	 */
	public TBSApplet getApplet() {
		return applet;
	}

	public void promptUser(Prompt prompt) {
		this.prompt = prompt;
		view.refreshGraphics();
	}

	public void setPrompt(Prompt prompt){
		prompt.setFinished(false);
		this.prompt = prompt;
		view.refreshGraphics();
	}

	public Prompt getPrompt() {
		return prompt;
	}

	public void clearPrompt() {
		this.prompt = null;
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

	public void setMESerialNumber( int serialNumber ) {
		MESerialNumber = serialNumber;
	}

	public void refreshSerial(){
		MESerialNumber = elements.size();
	}

	/**
	 * returns the complete List of Model Elements.
	 */
	public List<ModelElement> getElements() {
		return elements;
	}

	public void setElements( List<ModelElement> elements ) {
		this.elements = elements;
	}

	public int elementCount(){
		return elements.size();
	}

	/**
	 * returns the ith ModelElement in the list.
	 */
	public ModelElement getElement(int index){
		return elements.get(index);
	}

	/**
	 * Assigns value me to the ith member of the list. 
	 */
	public void setElement(int i, ModelElement m) {
		elements.set(i, m);
	}

	/**
	 * Adds a ModelElement to the ArrayList of items this Model knows
	 * about.
	 */
	public void addElement(ModelElement m){
		elements.add(m);
	}

	/**
	 * Adds a ModelElement to the list of items this Model knows
	 * about at a specific index.
	 */
	public void addElement(int index, ModelElement m){
		elements.add(index, m);
	}

	public void removeElement(ModelElement m){
		elements.remove(m);
	}

	public void removeElement(int index){
		elements.remove(index);
	}
  
  public void removeConnection(Connection c){
    c.getFrom().getConnectedTo().remove(c.getTo());
    c.getTo().getConnectedFrom().remove(c.getFrom());
    removeElement(c);
  }
  
  public abstract void removeFromTree(ModelElement me);
  
  public void resetModel(){
    List<ModelElement> modelElements = getElements();
    while(modelElements.size() > TBSGraphics.numOfOrganisms+1)
      removeFromTree(modelElements.get(modelElements.size()-1));
    List<Node> inTreeElements = inTreeElements();
    for(Node n : inTreeElements)
      removeFromTree(n);
    refreshSerial();
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
			return elements.indexOf(m);
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
		List<ModelElement> tempElements;
		if(parsedElements != null)
			tempElements = parsedElements;
		else
			tempElements = elements;
		for(int i=(TBSGraphics.numOfOrganisms-1);i<elements.size();i++){
			if(tempElements.get(i).getId().equals(id))
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
		List<ModelElement> model = elements;
		do 
		{
			me = (ModelElement)model.get(checknum);
			if (me.getId() == sn)
				return me;
			checknum--;
		} while (checknum >= me.getId());
		return null;
	}

	public boolean hasConnections(){
		for(int i=TBSGraphics.numOfOrganisms;i<elements.size(); i++){
			if(elements.get(i) instanceof Connection)
				return true;
		}
		return false;
	}

	public boolean hasEmptyNodes(){
		for(int i=(TBSGraphics.numOfOrganisms-1);i<elements.size();i++){
			if(elements.get(i) instanceof EmptyNode)
				return true;
		}
		return false;
	}

	/**
	 * Returns the list of active elements
	 */ 
	public List<Node> inTreeElements(){
		List<Node> inTreeElements = new LinkedList<Node>();
		for(ModelElement m : elements){
			if(m instanceof Node){
				Node n = (Node) m;
				if(n.isInTree())
					inTreeElements.add(n);
			}
		}
		return inTreeElements;
	}
  
  public List<Connection> getConnectionsByNode(Node n){
    List<Connection> connections = new LinkedList<Connection>();
    Connection c;
    for (ModelElement me: elements)
    {
      if(me instanceof Connection){
        c = (Connection) me;
        if(c.hasNode(n)){
          connections.add(c);
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
		List<ModelElement> savedTree = getElements();
		String[] treeItems = tree.split("#");
		try{
			for(String item : treeItems)
			{
				String data[] = item.split(":");
				if (data[0].equals("O"))
					loadOrganismNode(data, savedTree);
				else if (data[0].equals("E")){
					loadEmptyNode(data, savedTree);
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
			setElements(savedTree);
			refreshSerial();
			System.out.println("loadTree: end");
		}catch(NumberFormatException e){
			System.out.println(new StringBuffer("There was an error parsing saved tree for ")
			.append(getStudent().getName()).append(". ")
			.append("This tree has been reset.").toString());
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
	public void loadOrganismNode(String[] data, List<ModelElement> tempTree) throws NumberFormatException {
		int id=0,x=0,y=0;
		boolean inTree = Boolean.parseBoolean(data[5]);
		if(inTree){
			try{
				id = Integer.parseInt(data[1]);
				x = Integer.parseInt(data[3]);
				y = Integer.parseInt(data[4]);
			}catch(NumberFormatException e){
				System.out.println(new StringBuffer("StudentModel:loadOrganismNode:Error parsing organism data (id:")
				.append(data[1]).append(",x:").append(data[3]).append("y:").append(data[4]).append(")").toString());
				throw e;
			}
			int elementIndex = findIndexById(id, tempTree);
			OrganismNode node = (OrganismNode) tempTree.get(elementIndex);
			Point pt = new Point(x,y);
			node.setAnchorPoint(pt);
			node.setInTree(inTree);
			tempTree.set(elementIndex, node);
		}
	}

	/**
	 * Load an EmptyNode. Might be possible to combine this with
	 * loadOrganismNode().
	 */
	public void loadEmptyNode(String[] data, List<ModelElement> tempTree) throws NumberFormatException {
		int id=0,x=0,y=0;
		boolean inTree = Boolean.parseBoolean(data[5]);
		if(inTree){
			String name = data[2];
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
			EmptyNode node = new EmptyNode(id);
			Point pt = new Point(x,y);
			node.setAnchorPoint(pt);
			node.setInTree(inTree);
			tempTree.add(node);
		}
	}

	public ModelElement loadConnection(String[] data, List<ModelElement> parsedElements)
	throws NumberFormatException {
		int id=0,from=0,to=0;
		try{
			id = Integer.parseInt(data[1]);
			from = Integer.parseInt(data[2]);
			to = Integer.parseInt(data[3]);
		}catch(NumberFormatException e){
			System.out.println(new StringBuffer("TBSModel:loadConnection:Error parsing connection data (id:")
			.append(data[1]).append(",from id:").append(data[2]).append("to id:").append(data[3]).append(")").toString());
			throw e;
		}
		int fromIndex = findIndexById(from, parsedElements);
		int toIndex = findIndexById(to, parsedElements);

		Node fromNode = (Node) parsedElements.get(fromIndex);
		Node toNode = (Node) parsedElements.get(toIndex);
		Connection conn = new Connection(id, fromNode, toNode);
		return (ModelElement) conn;
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
		for(ModelElement m : getElements()){
			if(m instanceof Node){
				n = (Node) m;
				for(Node to : n.getConnectedTo())
					System.out.println(n.getName()+" -> "+to.getName());
				for(Node from : n.getConnectedFrom())
					System.out.println(from.getName()+" -> "+n.getName());
			}
		}
	}

	public String exportTree(){
		StringBuffer export = new StringBuffer();
		for(ModelElement m : elements)
			export.append(m.dump()).append("#");
		return export.toString();

	}
}
