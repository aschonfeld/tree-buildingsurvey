//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;
import tbs.model.history.Add;
import tbs.model.history.Drag;
import tbs.model.history.Label;
import tbs.model.history.Unlink;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;

/**
* TBSController contains the methods allowing the user to manipulate the
* data stored in the data model.
**/
public class TBSController 
		implements MouseListener, MouseMotionListener, KeyListener, AdjustmentListener
{
	
	private TBSModel model;
	private TBSView view;
	private int previousX, previousY; 
	private int selectedIndex;
	private Node draggedNode;
	private ModelElement selectedElement;
	private Point lastPosition = null;
	private String statusString = null;
	private TBSButtonType buttonClicked = TBSButtonType.SELECT;
	
	private boolean labelingInProgress = false;
	
	public TBSController(TBSModel m, TBSView v) {
    	model = m;
    	view = v;
 		view.getVerticalBar().addAdjustmentListener(this);
		draggedNode=null;
    }
	
	public void adjustmentValueChanged(AdjustmentEvent e) {
		view.setYOffset((e.getValue() * view.getHeight()) / 100);
	}
    
	public void keyPressed(KeyEvent e) {
		if(statusString == null) statusString = new String();
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(!labelingInProgress){
				buttonClicked = TBSButtonType.DELETE;
				handleDelete();
				buttonClicked = TBSButtonType.SELECT;
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(labelingInProgress) {
				cancelLabel();
				((Label) model.getHistory().peek()).setLabelAfter(((Node)selectedElement).getName());
				System.out.println("Added command(label) to history.");
				setSelectedElement(null);
				buttonClicked = TBSButtonType.SELECT;
			}
		}
		
	}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();
		if(selectedElement == null) return;
		if(labelingInProgress) {
			if(selectedElement instanceof EmptyNode) {
				EmptyNode en = (EmptyNode) selectedElement;
				String name =en.getName();
				if(c == '\b'){
					if(name.length() > 0)
						en.rename(name.substring(0,name.length()-1));
				}else
					en.rename(name + c);
			}
		}
	}
		
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		if(selectedElement == null)
			return;
		if(selectedElement instanceof Node) {
			if(TBSButtonType.LINK.equals(buttonClicked))
				view.setConnInProgress(
		    		new Line2D.Double(((Node) selectedElement).getCenter(),
		    				new Point(e.getX(), e.getY() + view.getYOffset())));
		}
	}
	
	// No need to use since mousePressed is used instead
	public void mouseClicked(MouseEvent e) {
		if(buttonClicked != null && !buttonClicked.getIsMode())
			buttonClicked = TBSButtonType.SELECT;
	}
	
	/**
	* Handle mousePressed events: if the mouse is over an object, select
	* it.
	*/
	public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        selectedIndex = indexMouseIsOver(x, y);
        previousX = x;
        previousY = y;
		view.requestFocusInWindow();
		// if mouse is in button bar
		if(y < TBSGraphics.buttonsHeight)  {
			handleMouseButtonPressed(x, y);
		} else if (x > TBSGraphics.LINE_OF_DEATH) {
			handleMousePressed(x, y);
		} else {
			clearCurrentActions();
			setSelectedElement(null);
		}
	}
	
	/**
	* Handle mouseDragged events: adjust position of selected Node and
	* refresh screen image.
	*/
	public void mouseDragged(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int deltaX = x - previousX;
		int deltaY = y - previousY;
		//cancelConnection();
		if(selectedIndex < 0) return;
		ModelElement selected = model.getElement(selectedIndex);
		if(selected instanceof Node) {
			// Move Node
			Node node = (Node) selected;
			if(lastPosition == null){
				lastPosition = new Point(node.getX(), node.getY());
				model.getHistory().push(new Drag(node.getId(), node.getAnchorPoint()));
				System.out.println("Added action(drag) to history.");
			}
			draggedNode = node;
			if(node.isInTree()) {
				node.move(deltaX, deltaY);
			} else {
				// if organism node being added to tree snap to mouse location
				node.moveTo(x, y);
			}
			model.setElement(selectedIndex, node);
		}
		view.refreshGraphics();
		unselectPrevious();
		cancelConnection();
		// update our data
		previousX = x;
		previousY = y;
		
 	}
	

	/**
	* Handle mouseReleased events. Drop the object being dragged and
	* correct its location if necessary. 
	*/	
	public void mouseReleased(MouseEvent e) 
	{
		//Auto-add/delete: 
		if (draggedNode != null)
		{
			//Node dragged to point out of bounds
			modifyOutOfBounds(draggedNode);
			List<Node> inTreeElements = model.inTreeElements();
			if(!draggedNode.isInTree()) draggedNode.setY(draggedNode.getY() + view.getYOffset());
			for(Node n : inTreeElements){
				if(!n.equals(draggedNode) && draggedNode.collidesWith(n)){
					draggedNode.setX(lastPosition.x);
					draggedNode.setY(lastPosition.y);
					break;
				}
			}
			
			if (draggedNode.getX() < TBSGraphics.LINE_OF_DEATH ){
				if(lastPosition.x < TBSGraphics.LINE_OF_DEATH && (model.getHistory().peek() instanceof Drag)){
					model.getHistory().pop();
					System.out.println("Invalid drag move removed from history.");
				}
				model.removeFromTree(draggedNode);
			}else{
				if (!draggedNode.isInTree() && draggedNode.getX() > TBSGraphics.LINE_OF_DEATH ) {
					//draggedNode.setY(draggedNode.getY() + view.getYOffset());
					model.addToTree(draggedNode); 
				} else {
					((Drag) model.getHistory().peek()).setPointAfter(draggedNode.getAnchorPoint());
				}
			}
			lastPosition = null;
			draggedNode=null;
		}
	}
	
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}
	
	public Node getDraggedNode(){
		return draggedNode;
	}
	
    private int indexMouseIsOver(int x, int y) {
	    int maxIndex = -1;
	    int i = 0;
	    int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH) yOffset = view.getYOffset(); 
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y + yOffset)) maxIndex = i;
		    i++;
		}
		return maxIndex;
	}
    
    private ModelElement elementMouseIsOver(int x, int y) {
    	ModelElement topElement = null;
	    List<ModelElement> selectedTwoWay = new LinkedList<ModelElement>();
	    int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH) yOffset = view.getYOffset(); 	    
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y + yOffset)){
		    	topElement = me;
		    	if(me instanceof Connection)
		    		selectedTwoWay.add(me);
		    }
		}
	    if(selectedTwoWay.size() > 1)
	    	model.setSelectedTwoWay(selectedTwoWay);
	    	
	    return topElement;
	}   
    
    
    private void modifyOutOfBounds(Node n){
    	if(n.getX() < 0 )
    		n.setX(0);
    	if((n.getX()+n.getWidth()) > view.getWidth() - view.getVerticalBar().getWidth())
    		n.setX(view.getWidth() - n.getWidth() - view.getVerticalBar().getWidth());
    	if(n.isInTree()) {
    		if(n.getY() <= view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
    			n.setY(view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    		if((n.getY()) > view.getHeight() + view.getYOffset())
    			n.setY(view.getHeight()-n.getHeight() + view.getYOffset());
    	} else {
    		if(n.getY() <= TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
    			n.setY(TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    		if((n.getY()) > view.getHeight())
    			n.setY(view.getHeight()-n.getHeight());   		
    	}
    }
    
    private void cancelConnection() {
		view.setConnInProgress(null);
    }
    
    // creates a connection if conditions are correct
    // returns true if connection created
    private boolean creatingConnection(Node n) {
        if(model.inTreeElements().size() > 1){
        	if(n.isInTree()){
        		if(n != selectedElement) {
        			if(selectedElement instanceof Node) {
        				model.addConnection((Node) selectedElement, n);
        				cancelConnection();
        				return true;
        			}
                }
            }
        }
        cancelConnection();
        return false;
    }
    
    public void cancelLabel() {
    	labelingInProgress = false;
    }
    
    public void creatingLabel(EmptyNode en) {
    	if(!labelingInProgress) {
    		labelingInProgress = true;
    		model.getHistory().push(new Label(en.getId(), en.getName()));
    		en.rename(en.getName());
    		selectedElement = en;
    	}
    }
    
    private void setSelectedElement(ModelElement me) {
    	unselectPrevious();
    	model.setSelectedModelElement(me);
        selectedElement = me;
    }

    // unselect previously selected element, otherwise will keep green box
    private void unselectPrevious(){
    	model.setSelectedModelElement(null);
        selectedElement = null;
    }
 
    public void handleDelete() {
		if(selectedElement == null) return;
		clearCurrentActions();
		if(model.getSelectedTwoWay() != null){
			for(ModelElement tw : model.getSelectedTwoWay())
				model.removeFromTree(tw);
			model.setSelectedTwoWay(null);
		}else
			model.removeFromTree(selectedElement);
		unselectPrevious();
    }
    
    public void clearCurrentActions() {
		cancelConnection();
		cancelLabel();
    }
    
    private void handleMouseButtonPressed(int x, int y) {
    	clearCurrentActions();
		int buttonIndex = x / TBSGraphics.buttonsWidth;
		if(buttonIndex >= TBSButtonType.values().length) return;
		buttonClicked = TBSButtonType.values()[buttonIndex];
		System.out.println(buttonClicked.toString());
		switch (buttonClicked) {
		case SELECT:
			break;
		case ADD:
			break;
		case DELETE:
			handleDelete();
			break;
		case LINK:
			if(selectedElement == null) 
				break;
			else if(selectedElement instanceof Node)
				return;
			else
				break;
		case UNLINK:
			if(selectedElement == null) 
				break;
			try{
				if(selectedElement instanceof Node){
					model.unlink((Node) selectedElement);
				}else{
					Connection c = (Connection) selectedElement;
					model.getHistory().push(new Unlink((Connection) c.clone()));
					System.out.println("Added action(unlink) to history.");
					model.removeFromTree(c);
				}
			}catch(CloneNotSupportedException c){
				System.out.println("Unable to add action to history.");
			}
			break;
		case LABEL:
			if(selectedElement instanceof EmptyNode){
				creatingLabel((EmptyNode) selectedElement);
				return;
			}
			break;
		case PRINT:
			model.loadTree();
			break;
		case UNDO:
			if(!model.getHistory().isEmpty())
				model.getHistory().pop().undo(model);
			break;
		case SAVE: 	//Dumps tree data to console for testing
						//This should not happen in a release unless it is
						//explicitly highlighted as a temporary demonstration
						//of the save format
			for (ModelElement me : model.getElements())
			{
				if (me instanceof Node)
				{
					Node n = (Node)me;
					System.out.println(n.dump());
				}
			}
			break;
			
		case CLEAR:
			model.resetModel();
			break;
		}
		setSelectedElement(null);
    }
    
    private void handleMousePressed(int x, int y) {
    	ModelElement clickedElement = elementMouseIsOver(x, y);
    	// clicking on empty space always cancels connection
		if(clickedElement == null) {
			unselectPrevious();
			if(!buttonClicked.equals(TBSButtonType.ADD))
				buttonClicked = TBSButtonType.SELECT;
		}
		clearCurrentActions();
    	switch (buttonClicked) {
		case SELECT:
			break;
		case ADD:
			if(clickedElement == null) {
				if(x > view.getWidth() - view.getVerticalBar().getWidth()) 
					x = view.getWidth() - view.getVerticalBar().getWidth();
				Node newNode = new EmptyNode(model.getSerial(), new Point(x, y + view.getYOffset()));
				for(Node n : model.inTreeElements()){
					// make sure not putting it on top of another item
					if(n.collidesWith(newNode)){
						newNode = null;
						break;
					}
					// make sure it's not under the scroll bar
					
				}
				if(newNode != null){
					model.addElement(newNode);
					try{
						model.getHistory().push(new Add((Node) newNode.clone()));
						System.out.println("Added action(add) to history.");
					}catch(CloneNotSupportedException c){
						System.out.println("Unable to add action to history.");
					}
				}
			}
			break;
		case LINK:
			if(clickedElement == null) 
				break;
			if(clickedElement instanceof Node) {
				if(creatingConnection((Node) clickedElement)) {
					// do not automatically start a new connection
					clickedElement = null;
				}
				break;
			}
			break;
		case UNLINK:
			if(clickedElement == null)
				break;
			if(clickedElement instanceof Node)
				model.unlink((Node) clickedElement);
			else
				model.removeFromTree(clickedElement);
			break;
		case LABEL:
			if(clickedElement == null)
				break;
			if(clickedElement instanceof EmptyNode)
				creatingLabel((EmptyNode) clickedElement);
			break;
		case PRINT:
			break;
		case SAVE:
			break;
		}
    	// default action unless return
    	if(clickedElement instanceof Node) {
    		if(((Node) clickedElement).isInTree()) // organism node is in tree, selectable
    			setSelectedElement(clickedElement);
    		else // organism node is not in tree, just unselect previous
    			unselectPrevious();
		} else // default set selectedElement = clickedElement
    		setSelectedElement(clickedElement);	
	}			




}
