package tbs.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import tbs.TBSGraphics;
import tbs.controller.TBSController;
import tbs.view.TBSView;

public class TBSModel 
{
	private TBSView view;
	private TBSController controller;
	private ArrayList<ModelElement> modelElements;
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

	/**
	* returns the ith ModelElement in the list.
	*/
	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}
	

	/**
	* returns the complete ArrayList of Model Elements.
	*/
	public ArrayList<ModelElement> getElements() {
		return modelElements;
	}

	/**
	* Assigns value me to the ith member of the list. 
	*/
	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}


	public void clearConnections(Node n) {
		n.unlink();
	}


/* Unnecessary to iterate the model to do this
		for(ModelElement me: modelElements) {
			if(me instanceof Node) {
				Node n2 = (Node) me;
				if (n.getConnections().contains(n2)) {
					n.removeConnection(n2);
				}
				if (n2.getConnections().contains(n)) {
					n2.removeConnection(n);
				}
			}
		}
	}
*/	
	public EmptyNode getImmortalEmptyNode() {
		return immortalEmptyNode;
	}
	
	public void createButtons(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		TBSGraphics.getFont(g2);
		TBSGraphics.buttons = new ArrayList<String>();
		TBSGraphics.buttons.add("link");
		TBSGraphics.buttons.add("unlink");
		TBSGraphics.buttons.add("label");
		TBSGraphics.buttons.add("delete");
		TBSGraphics.buttons.add("split");
		TBSGraphics.buttons.add("print");
		TBSGraphics.buttons.add("undo");
		TBSGraphics.buttons.add("save");
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
	
}
