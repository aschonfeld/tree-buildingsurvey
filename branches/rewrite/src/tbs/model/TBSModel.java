//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.TreeMap;

import tbs.TBSGraphics;
import tbs.controller.TBSController;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;

public class TBSModel 
{
	private TBSView view;
	private TBSController controller;
	private List<ModelElement> modelElements;
	private EmptyNode immortalEmptyNode;


	public TBSModel(Graphics g, TreeMap<String, BufferedImage> organismNameToImage) {
		modelElements = new ArrayList<ModelElement>();
		createButtons(g); // call before creating model elements
		createModelElements(g, organismNameToImage);
		view = new TBSView(this);
		controller = new TBSController(this, view);
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
	* Adds a ModelElement to the ArrayList of items this Model knows
	* about.
	*/
	public void addElement(ModelElement m) {
		modelElements.add(m);
	}

	/**
	* Deletes an item from the ArrayList of ModelElements. Limited to
	* EmptyNodes.
   */
	public void delete(EmptyNode en)
	{
		modelElements.remove(en);		
	}
	
	public void delete(Connection conn)
	{
		modelElements.remove(conn);		
	}	
	
	/**
	* returns the ith ModelElement in the list.
	*/
	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}
	

	/**
	* returns the complete ArrayList of Model Elements.
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

	public EmptyNode getImmortalEmptyNode() {
		return immortalEmptyNode;
	}
	
	public void createButtons(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		TBSGraphics.getFont(g2);
		TBSGraphics.buttons = new ArrayList<TBSButtonType>();
		for(TBSButtonType b : TBSButtonType.values())
			TBSGraphics.buttons.add(b);
		Point buttonBounds = TBSGraphics.get2DStringBounds(g2, 
				TBSGraphics.buttons);
		TBSGraphics.buttonsWidth = buttonBounds.x + 
				TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonBounds.y + 
				TBSGraphics.buttonsYPadding * 2;
	}
	
	// called during setup to create organism nodes
	protected void createModelElements(Graphics g, 
				TreeMap<String, BufferedImage> organismNameToImage) {
		Graphics2D g2 = (Graphics2D) g;
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
			addElement(new OrganismNode(this, img, organismName, 
				currentX, currentY, TBSGraphics.organismNodeWidth, 
				TBSGraphics.organismNodeHeight));
			currentY += TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing;
		}

		//create left-side empty node

		TBSGraphics.emptyNodeLeftX = TBSGraphics.organismNodeWidth / 2 - 
					TBSGraphics.emptyNodeWidth / 2;
		TBSGraphics.emptyNodeUpperY = currentY + TBSGraphics.organismNodeHeight + 
					TBSGraphics.emptyNodeYLabelOffset; 
		immortalEmptyNode = new EmptyNode(this);
		addElement(immortalEmptyNode);
	}	

	
	/**
	* PrintConnections() prints out a list of all connections in each
	* model element. 
	* Connection to a Node (toConnection) is indicated by ->
	* Trace connection from a Node (fromConnection) indicated by <-
	*/
	public void printConnections()
	{
		Node n;
		ListIterator<ModelElement> li = modelElements.listIterator();
		while (li.hasNext())
		{
			n = (Node)li.next();
			
			if (n.isConnected())
			{
				ListIterator<Connection> it = n.getConnections().listIterator();

				while (it.hasNext())
				{
					System.out.println(n.getName()+" -> "+
						it.next().getToNode().getName());
				}
			}
			if (!n.getFromConnections().isEmpty())
			{
				ListIterator<Node> it =
					n.getFromConnections().listIterator();
				while (it.hasNext())
				{
					System.out.println(n.getName() +" <- " +
						it.next().getName());
				}
			}
		}
	}


	/**
	* Deprecated. Unlink had to live in Model when connections were
	* one-way. Now, this simply calls the Node-based two-way unlink.
	*/
	public void unlink(Node source)
	{
		source.unlink();

/*		Node target;
		Connection conn;

		for (ModelElement me:modelElements)
		{
			target = (Node)me;
		
			if (source.connectedTo(target))
				source.removeConnection(source.getConn(target));
			if (target.connectedTo(source))
				target.removeConnection(target.getConn(source));
			
		}
*/		
	}

	/**
	* Deprecated. Old version of unlink. Now a reference to
	* Node.unlink().
	*/
	public void clearConnections(Node n) 
	{
		n.unlink();
	}
	
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
}
