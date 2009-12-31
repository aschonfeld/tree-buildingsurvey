//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DragSource;
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
import java.util.regex.Matcher;

import tbs.TBSGraphics;
import tbs.TBSPrompt;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.TBSModel;
import tbs.model.history.Add;
import tbs.model.history.Drag;
import tbs.model.history.Label;
import tbs.model.history.Unlink;
import tbs.view.TBSButtonType;
import tbs.view.TBSQuestionButtonType;
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
	private TBSQuestionButtonType questionClicked = null;
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
		if(model.getPrompt() != null) {
			model.getPrompt().keyPressed(e);
			return;
		}
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
		if(model.getPrompt() != null) {
			model.getPrompt().keyTyped(e);
			return;
		}
		char c = e.getKeyChar();
		if(selectedElement == null) return;
		if(labelingInProgress) {
			if(selectedElement instanceof EmptyNode) {
				EmptyNode en = (EmptyNode) selectedElement;
				String name =en.getName();
				if(c == '\b'){
					if(name.length() > 0)
						en.rename(name.substring(0,name.length()-1));
				}else{
					Matcher m = TBSGraphics.emptyNodePattern.matcher("" + c);
					if(m.find())
						en.rename(name + c);
				}
			}
		}
	}
		
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		if(model.getPrompt() != null){
			if(model.getPrompt().isOverButton(e))
				view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			return;
		}
		int x,y,buttonIndex;
		x = e.getX();
		y = e.getY();
		if(!view.isTooltipRunning()){
			ModelElement m = elementMouseIsOver(x,y);
			if(m != null && m instanceof OrganismNode){
				OrganismNode o = (OrganismNode) m;
				if(o.isInTree())
					view.updateTooltip(o.getName(), e.getPoint());
			}
		}
		if(y < TBSGraphics.buttonsHeight)  {
			if(x >= TBSGraphics.questionButtonsStart){
				buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.questionButtonsWidth;
				if(buttonIndex >= TBSQuestionButtonType.values().length)
					view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				else
					view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}else{
				buttonIndex = x / TBSGraphics.buttonsWidth;
				if(buttonIndex >= TBSButtonType.values().length)
					view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				else{
					TBSButtonType temp = TBSButtonType.values()[buttonIndex];
					if(model.isButtonActive(temp))
						view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					else
						view.setAppletCursor(DragSource.DefaultMoveNoDrop);
				}
			}
		}
		else
			view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
		if(model.getPrompt() != null) return;
		if(buttonClicked != null){
			if(!buttonClicked.isMode())
				buttonClicked = TBSButtonType.SELECT;
			if(!model.isButtonActive(buttonClicked))
				buttonClicked = TBSButtonType.SELECT;
		}
		
	}
	
	/**
	* Handle mousePressed events: if the mouse is over an object, select it.
	* ALSO: This is where you get the result of a prompt
	*/
	public void mousePressed(MouseEvent e){
		TBSPrompt prompt = model.getPrompt();
		if(prompt != null) {
			prompt.mousePressed(e);
			if(model.getPrompt().isFinished()) {
				// Get result of prompt here
				model.setQuestion(prompt.getUserInput(), prompt.getCurrentQuestion());
				if(prompt.getCurrentQuestion().ordinal() < TBSQuestionButtonType.THREE.ordinal())
					model.promptUser(new TBSPrompt(model, TBSQuestionButtonType.values()[prompt.getCurrentQuestion().ordinal()+1]));
				else
					model.clearPrompt();
			}
			return;
		}
		view.requestFocusInWindow();
        int x = e.getX();
        int y = e.getY();
        selectedIndex = indexMouseIsOver(x, y);
        previousX = x;
        previousY = y;
		// if mouse is in button bar
		if(y < TBSGraphics.buttonsHeight)  {
			if(x >= TBSGraphics.questionButtonsStart)
				handleMouseQuestionPressed(x, y);
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
		if(model.getPrompt() != null) return;
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
				model.addActionToHistory(new Drag(node.getId(), node.getAnchorPoint()));
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
		if(model.getPrompt() != null) return;
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
					model.removeActionFromHistory();
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
    		model.addActionToHistory(new Label(en.getId(), en.getName()));
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
    	model.setSelectedTwoWay(null);
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
		StringBuffer statusKey = new StringBuffer(buttonClicked.name());
		if(buttonClicked.isItemSelectionBased()){
			if(selectedElement == null)
				statusKey.append("1");
			else
				statusKey.append("2");
		}
		statusKey.append("_");
		Boolean buttonState = model.getButtonStates().get(buttonClicked);
		statusKey.append(buttonState.toString());
		view.setScreenString(model.getStatusProperties().getProperty(statusKey.toString()));
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
					model.addActionToHistory(new Unlink((Connection) c.clone()));
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
			String export = model.exportTree();
			model.resetModel();
			model.loadTree(export);
			break;
		case UNDO:
			if(!model.getHistory().isEmpty())
				model.removeActionFromHistory().undo(model);
			break;
		case SAVE: 	
			//Dumps tree data to console for testing
			//This should not happen in a release unless it is
			//explicitly highlighted as a temporary demonstration
			//of the save format
			System.out.println(model.exportTree());
			break;
		case CLEAR:
			model.resetModel();
			break;
		}
		setSelectedElement(null);
    }
    
    private void handleMouseQuestionPressed(int x, int y) {
    	int buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.questionButtonsWidth;
		if(buttonIndex >= TBSQuestionButtonType.values().length) return;
		questionClicked = TBSQuestionButtonType.values()[buttonIndex];
		System.out.println(questionClicked.toString());
		switch (questionClicked) {
		case ONE:
			model.promptUser(new TBSPrompt(model, TBSQuestionButtonType.ONE));
			break;
		case TWO:
			model.promptUser(new TBSPrompt(model, TBSQuestionButtonType.TWO));
			break;
		case THREE:
			model.promptUser(new TBSPrompt(model, TBSQuestionButtonType.THREE));
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
					model.getButtonStates().put(TBSButtonType.LABEL, true);
					model.getButtonStates().put(TBSButtonType.DELETE, true);
					if(model.inTreeElements().size() > 1)
						model.getButtonStates().put(TBSButtonType.LINK, true);
					model.getButtonStates().put(TBSButtonType.CLEAR, true);
					try{
						model.addActionToHistory(new Add((Node) newNode.clone()));
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
			else{
				try{
					Connection c = (Connection) clickedElement;
					model.addActionToHistory(new Unlink((Connection) c.clone()));
				}catch(CloneNotSupportedException c){
					System.out.println("Unable to add action to history.");
				}
				model.removeFromTree(clickedElement);
				return;
			}
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
