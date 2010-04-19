//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Point;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.controller.TBSController;
import tbs.model.admin.Student;
import tbs.view.TBSView;
import tbs.view.prompt.Prompt;


public abstract class TBSModel 
{

	/**
	*	Reference to the TBSApplet
	*/
	private TBSApplet applet;

	/**
	* 	Reference to the view object
	*/
	private TBSView view;

	/**
	*	Reference to the controller
	*/
	private TBSController controller;

	/**
	*	Prompts are the objects used to display information to the student
	*	and to elicit answers to the post-survey questions
	*/
	private Prompt prompt;
	
	/**
	*	Student - MUST DO
	*/
	private Student student;

	/**
	*	At any given moment, one model element is selected, meaning it can
	*	be the object of button click "verbs", such as "link" or "delete". 
	*/
	private ModelElement selectedElement;
	
	/**
	*	(StudentModel) The list of objects being manipulated by the student.
	*/	
	private List<ModelElement> elements;

	/**
	*	Each model element has a unique serial number; MESerialNumber
	*	keeps track of the most recently assigned number, ensuring
	*	uniqueness.
	*/
	private int MESerialNumber;

	/**
	* 	Are there elements in the tree?
	*/
	private boolean elementsInTree;

	/**
	*	Are there emptyNodes in the tree?
	*/
	private boolean emptyNodesInTree;

	/**
	*	Has the student connected objects in the tree?
	*/
	private boolean connectionsInTree;


	/**
	*	The constructor for TBSModel simply sets the local reference to
	*	TBSApplet and initializes certain variables to null/false values.
	*/
	public TBSModel(TBSApplet applet, List<OrganismNode> organisms){
		this.applet = applet;
		selectedElement = null;
		elements = new LinkedList<ModelElement>();
		elements.addAll(organisms);
		refreshSerial();
		elementsInTree = false;
		emptyNodesInTree = false;
		connectionsInTree = false;
	}

	/**
	 * Returns a handle for the Controller associated with this Model.
	 */
	public TBSController getController() {
		return controller;
	}

	/**
	*	Associates this Model with a Conroller.
	*/
	public void setController( TBSController controller ) {
		this.controller = controller;
	}

	/**
	*	Returns the Student object MUST REVIEW 
	*/
	public Student getStudent() {
		return student;
	}

	/**
	*	Associates this Model with a Student 	
	*/
	public void setStudent( Student selectedStudent ) {
		this.student = selectedStudent;
	}



	/**
	 * Returns a handle for the View associated with this Model
	 */
	public TBSView getView() {
		return view;
	}

	/**
	*	Associates this Model with a View
	*/
	public void setView( TBSView view ) {
		this.view = view;
	}

	/**
	 * Returns a handle for the Applet.
	 */
	public TBSApplet getApplet() {
		return applet;
	}


	/**
	*	Puts the applet in a "prompt" state - this can have various
	*	meanings, depending on the particular Prompt used.	
	*/
	public void promptUser(Prompt prompt) {
		this.prompt = prompt;
		view.refreshGraphics();
	}


	/**
	*	Puts the applet in a "prompt" state - this can have various
	*	meanings, depending on the particular Prompt used.	HOW DIFFERENT
	*	FROM promptUser?
	*/
	public void setPrompt(Prompt prompt){
		prompt.setFinished(false);
		this.prompt = prompt;
		view.refreshGraphics();
	}

	/**
	*	Return the current prompt object; usually this will be null.
	*/
	public Prompt getPrompt() {
		return prompt;
	}

	/**
	*	Set the current Prompt to null, putting the applet in normal
	*	interaction mode.
	*/
	public void clearPrompt() {
		this.prompt = null;
	}

	/**
	 * Returns a serial number for a model element. Serial numbers start
	 * at 0 and simply increment; they are unique within a tree, but not
	 * across trees.
	 */
	public int getSerial()
	{
		int sn = MESerialNumber;
		System.out.println(MESerialNumber);
		MESerialNumber++;
		return sn;
	}

	/**
	*	Manually set MESerialNumber to some value. Generally a bad idea to
	*	mess with this, since duplicate serial numbers will produce all
	*	sorts of strange and difficult bugs.
	*/
	public void setMESerialNumber( int serialNumber ) {
		MESerialNumber = serialNumber;
	}


	/**
	*	Set serialNumber to one place past the end of the current elements
	*	list. 	
	*/
	public void refreshSerial(){
		MESerialNumber = elements.size();
	}
	
	/**
	*	Returns the identity of the currently selected element. 
	*/
	public ModelElement getSelectedElement() {
		return selectedElement;
	}


	/**
	*	Select an element (de-selecting the previously-selected element)
	*/
	public void setSelectedElement(ModelElement selectedModelElement) {
		this.selectedElement = selectedModelElement;
	}

	/**
	 * returns the complete List of Model Elements.
	 */
	public List<ModelElement> getElements() {
		return elements;
	}

	/**
	*	Populate the model with a list of elements.
	*/
	public void setElements( List<ModelElement> elements ) {
		this.elements = elements;
	}

	/**
	*	Returns the number of elements in the model.
	*/
	public int elementCount(){
		return elements.size();
	}

	/**
	 * returns the ModelElement and the indicated point in the list.
	 */
	public ModelElement getElement(int index){
		return elements.get(index);
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
	
	/**
	* 	Removes ModelElement m from the model. The element is not retained
	* 	by the Model, but see the undo routines.
	*/
	public void removeElement(ModelElement m){
		elements.remove(m);
	}

	public void removeElement(int index){
		elements.remove(index);
	}

	/**
	*	Remove all non-OrganismNode elements from the model, deleting all
	*	connections. Return all OrganismNodes to the inactive column.
	*	Reset state variables, and otherwise return the tree to its
	*	initial state.
	*/
	public void resetModel(){
		List<ModelElement> modelElements = getElements();
		while(modelElements.size() > TBSGraphics.numOfOrganisms+1)
			ModelUtils.removeElement(modelElements.get(modelElements.size()-1), this, true);
		List<ModelElement> inTreeElements = inTreeElements();
		for(ModelElement me : inTreeElements){
      if(me instanceof Node)
        ModelUtils.removeElement(me, this, true);
    }
		refreshSerial();
		elementsInTree = false;
		emptyNodesInTree = false;
		connectionsInTree = false;
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

	/**
	* Returns true if there are any connections in the tree. 
	* Consulted to determine whether "unlink" should be available. 
	*/
	public boolean hasConnections(){
		if(connectionsInTree){
			for(int i=TBSGraphics.numOfOrganisms;i<elements.size(); i++){
				if(elements.get(i) instanceof Connection){
					return true;
				}
			}
			connectionsInTree = false;
		}
		return false;
	}

	/**
	* Returns true if the tree has any empty nodes in it. 
	* This is checked to determine whether the Label button should be
	* available.
	*/
	public boolean hasEmptyNodes(){
		if(emptyNodesInTree){
			for(int i=(TBSGraphics.numOfOrganisms-1);i<elements.size();i++){
				if(elements.get(i) instanceof EmptyNode)
					return true;
			}
			emptyNodesInTree = false;
		}
		return false;
	}

  /**
	 * Returns the list of active elements
	 */
	public List<ModelElement> inTreeElements(){
	  List<ModelElement> inTreeElements = new LinkedList<ModelElement>();
	  boolean tempElementsInTree = false;
	  if(elementsInTree){
	    for(ModelElement m : elements){
	      if(m.isInTree()){
	        if(m instanceof Node)
            tempElementsInTree = true;
	        inTreeElements.add(m);
	      }
	    }
	  }
	  elementsInTree = tempElementsInTree;
	  return inTreeElements;
	}
  
  /**
   * Returns the list of inactive elements
   */
  public List<ModelElement> outOfTreeElements(){
    List<ModelElement> outOfTreeElements = new LinkedList<ModelElement>();
    if(elementsInTree){
      for(ModelElement m : elements){
        if(!m.isInTree())
          outOfTreeElements.add(m);
      }
    }
    return outOfTreeElements;
  }

	public boolean isElementsInTree() {
		return elementsInTree;
	}

	public void setElementsInTree(boolean elementsInTree) {
		this.elementsInTree = elementsInTree;
	}

	public boolean isEmptyNodesInTree() {
		return emptyNodesInTree;
	}

	public void setEmptyNodesInTree(boolean emptyNodesInTree) {
		this.emptyNodesInTree = emptyNodesInTree;
	}

	public boolean isConnectionsInTree() {
		return connectionsInTree;
	}

	public void setConnectionsInTree(boolean connectionsInTree) {
		this.connectionsInTree = connectionsInTree;
	}

	/**
	 * Take a list of strings extracted from a file by
	 * the perl script contained within the website, and recreate the stored 
	 * tree.
	 * Two passes: first pass recreates nodes, second makes connections. 
	 */
	public void loadTree(String tree)
	{
		List<ModelElement> savedTree = getElements();
		List<String[]> savedConnections = new LinkedList<String[]>();
		String[] treeItems = tree.split("#");
		try{
			for(String item : treeItems)
			{
				String data[] = item.split(":");
				if (data[0].equals("O"))
					loadOrganismNode(data, savedTree);
				else if (data[0].equals("E"))
					loadEmptyNode(data, savedTree);
				else if (data[0].equals("C")){
					savedConnections.add(data);
					connectionsInTree = true;
				}else{
					System.out.println("Problem in loadTree");
					break;
				}
			}
			Collections.sort(savedTree, TBSGraphics.elementIdComparator);
			refreshSerial();
			for(String[] savedConnection : savedConnections)
				loadConnection(savedConnection, savedTree);
			// Sort the local list
			setElements(savedTree);
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
			elementsInTree = true;
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
			emptyNodesInTree = true;
		}
	}
	
	/**
	*	Sets up connections in model based on information suppled via the
	*	perl script that loads the applet. 
	*/

	public void loadConnection(String[] data, List<ModelElement> tempTree) throws NumberFormatException {
		int from=0,to=0;
		try{
			from = Integer.parseInt(data[2]);
			to = Integer.parseInt(data[3]);
		}catch(NumberFormatException e){
			System.out.println(new StringBuffer("TBSModel:loadConnection:Error parsing connection data (id:")
			.append(data[1]).append(",from id:").append(data[2]).append("to id:").append(data[3]).append(")").toString());
			throw e;
		}
		int fromIndex = findIndexById(from, tempTree);
		int toIndex = findIndexById(to, tempTree);

		if(fromIndex > 0 && toIndex > 0){
			Node fromNode = (Node) tempTree.get(fromIndex);
			Node toNode = (Node) tempTree.get(toIndex);
			tempTree.add(new Connection(getSerial(), fromNode, toNode));
		}
	}

	/**
	 * PrintConnections() prints out a list of all connections in each
	 * model element. 
	 * Connection to a Node (toConnection) is indicated by ->
	 * Trace connection from a Node (fromConnection) indicated by <-
	 * Written for testing connections; functionality may not have
	 * survived rewrite of connections methodology.
	 *
 	 * NOT USED CURRENTLY - REVIEW FOR VALIDITY, RETAIN FOR TESTING
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

	/**
	* Construct and return the string representing this tree.
	*/
	public String exportTree(){
		StringBuffer export = new StringBuffer();
		for(ModelElement m : inTreeElements())
			export.append(m.dump()).append("#");
		return export.toString();
	}
	

	/**
	* 	Error-checking method, reviews list of elements and looks for
	* 	duplicate ID numbers, connections to non-existent objects. 
	*/
	public void checkElementsIntegrity(){
		Set<Integer> ids = new HashSet<Integer>();
		for(ModelElement element : elements){
			if(!ids.add(element.getId()))
				System.out.println("ID(" + element.getId() + ") OCCURS TWICE!!");
			if(element instanceof Node){
				for(Node n : ((Node) element).getConnectedTo()){
					if(findIndexById(n.getId()) < 0)
						System.out.println("Node(" + element.getId() + ") connected to Node(" + n.getId() + ") that doesn't exist!");
				}
				for(Node n : ((Node) element).getConnectedFrom()){
					if(findIndexById(n.getId()) < 0)
						System.out.println("Node(" + element.getId() + ") connected from Node(" + n.getId() + ") that doesn't exist!");
				}
			}
		}
	}
}
