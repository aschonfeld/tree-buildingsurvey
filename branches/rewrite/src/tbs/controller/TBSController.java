//TBS version .04
//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;
import tbs.view.TBSButtonType;
import tbs.view.TBSView;

/**
* TBSController contains the methods allowing the user to manipulate the
* data stored in the data model.
**/
public class TBSController 
		implements MouseListener, MouseMotionListener, KeyListener
{
	
	private TBSModel model;
	private TBSView view;
	private int previousX, previousY, selectedIndex;
	private Node draggedNode;
	private ModelElement selectedElement;
	private Point lastPosition = null;
	private String statusString = null;
	private TBSButtonType buttonClicked = TBSButtonType.SELECT;
	private boolean labelingInProgress = false;
	
	public TBSController(TBSModel m, TBSView v) {
    	model = m;
    	view = v;
		draggedNode=null;
		buttonClicked=null;
    }
    
	public void keyPressed(KeyEvent e) {
		if(statusString == null) statusString = new String();
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			handleDelete();
		}
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			if(labelingInProgress) {
				cancelLabel();
				setSelectedElement(null);
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
				en.setName(en.getName() + c);
			}
		}
	}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		if(selectedElement == null)
			return;
		if(selectedElement instanceof Node) {
			if(TBSButtonType.CONNECT.equals(buttonClicked))
				view.setConnInProgress(
		    		new Line2D.Double(((Node) selectedElement).getCenter(),
		    				new Point(e.getX(), e.getY())));
		}
	}
	
	// Handle mouseClicked events. Check position of mouse pointer and
	// respond accordingly. This is complicated and still unstable; 
	// will cover in more detail as it settles down. 
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		// get keyboard focus when user clicks in window
		view.requestFocusInWindow();
		if(e.getClickCount() == 1) {
			// if mouse is in button bar
			if(y < TBSGraphics.buttonsHeight)  {
				handleButtonClicked(x, y);
			} else if (x > TBSGraphics.LINE_OF_DEATH) {
				handleMouseClicked(x, y);
			} else {
				clearCurrentActions();
				setSelectedElement(null);
			}
		}
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
			}
			draggedNode = node;
			node.move(deltaX, deltaY);
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
			for(Node n : inTreeElements){
				if(!n.equals(draggedNode) && draggedNode.collidesWith(n)){
					draggedNode.setX(lastPosition.x);
					draggedNode.setY(lastPosition.y);
					break;
				}
			}
			lastPosition = null;
			if (draggedNode.getX() < TBSGraphics.LINE_OF_DEATH )
				model.removeFromTree(draggedNode);
			if (draggedNode.getX() > TBSGraphics.LINE_OF_DEATH )
				model.addToTree(draggedNode); 
				//is it more efficient to check isInTree in this
				//case or not to check? It shouldn't affect
				//performance, but it's an interesting question.
			draggedNode=null;
		}
		//selectedIndices = new ArrayList<Integer>(); // clear selected items
	}
	
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}
	
    private int indexMouseIsOver(int x, int y) {
	    int maxIndex = -1;
	    int i = 0;
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y)) maxIndex = i;
		    i++;
		}
		return maxIndex;
	}
    
    private ModelElement elementMouseIsOver(int x, int y) {
	    ModelElement topElement = null;
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y))
		    	topElement = me;
		}
	    return topElement;
	}   
    
    
    private void modifyOutOfBounds(Node n){
    	if(n.getX() < 0 )
    		n.setX(0);
    	if((n.getX()+n.getWidth()) > view.getWidth())
    		n.setX(view.getWidth()-n.getWidth());
    	if(n.getY() <= 0)
    		n.setY(TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    	if((n.getY()) > view.getHeight())
    		n.setY(view.getHeight()-n.getHeight());
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
    		en.setName("");
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
		model.removeFromTree(selectedElement);
		unselectPrevious();
    }
    
    public void clearCurrentActions() {
		cancelConnection();
		cancelLabel();
    }
    
    private void handleButtonClicked(int x, int y) {
    	clearCurrentActions();
		int buttonIndex = x / TBSGraphics.buttonsWidth;
		if(buttonIndex >= TBSGraphics.buttons.size()) return;
		buttonClicked = TBSGraphics.buttons.get(buttonIndex);
		System.out.println(buttonClicked.toString());
		switch (buttonClicked) {
		case SELECT:
			break;
		case ADD:
			break;
		case DELETE:
			handleDelete();
			break;
		case CONNECT:
			if(selectedElement == null) 
				break;
		case DISCONNECT:
			if(selectedElement == null) 
				break;
			if(selectedElement instanceof Node)
				model.unlink((Node) selectedElement);
			else
				model.removeFromTree(selectedElement);
			break;
		case LABEL:
		case PRINT:
		case UNDO:
		case SAVE:
		}
		setSelectedElement(null);
    }
    
    private void handleMouseClicked(int x, int y) {
    	if(buttonClicked == null)
    		buttonClicked = TBSButtonType.SELECT;
		ModelElement clickedElement = elementMouseIsOver(x, y);
		// clicking on empty space always cancels connection
		if(clickedElement == null) {
			unselectPrevious();
		}
		clearCurrentActions();
    	switch (buttonClicked) {
		case SELECT:
			break;
		case ADD:
			if(clickedElement == null) {
				Node newNode = new EmptyNode(model.getSerial(), x, y);
				for(Node n : model.inTreeElements()){
					// make sure not putting it on top of another item
					if(n.collidesWith(newNode)){
						newNode = null;
						break;
					}
				}
				if(newNode != null) model.addElement(newNode);
			}
			break;
		case DELETE:
			if(clickedElement == null)
				break;
			selectedElement = clickedElement;
			handleDelete();
			return;
		case CONNECT:
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
		case DISCONNECT:
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
		case UNDO:
		case SAVE:
		}
    	// default action unless return
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
