//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import tbs.TBSGraphics;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.StudentModel;
import tbs.model.history.Add;
import tbs.model.history.Command;
import tbs.model.history.Drag;
import tbs.model.history.Label;
import tbs.model.history.Unlink;
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.StudentView;
import tbs.view.TBSButtonType;
import tbs.view.TextEntryBox;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.YesNoPrompt;

/**
* StudentController contains the methods used by the student in
* contructing a TBS tree. 
**/
public class StudentController extends TBSController
{
	
	private StudentModel model;
	private StudentView view;
	private int previousX, previousY; 
	private int selectedIndex;
	private Node draggedNode;
	private ModelElement selectedElement;
	private Point lastPosition = null;
	private String statusString = null;
	private TBSButtonType buttonClicked = TBSButtonType.SELECT;
	private OpenQuestionButtonType questionClicked = null;
	private boolean labelingInProgress = false;
	private boolean itemDeleted = false;
	
	public void printMouseEvent(MouseEvent e) {
		if(e.getID() == MouseEvent.MOUSE_CLICKED) {
			//System.out.println(e.toString());
		}
	}
	
	/**
	* Used in automated testing: prints KeyEvent to the console
	* (See {@see StudentControllerTest})
	*/
	public void printKeyEvent(KeyEvent e) {
		//System.out.println(e.toString());
	}
	
	/**
	* Returns true if a label is being edited. Provided for automated
   * testing. 
	* (See {@see StudentControllerTest})
	*/
	public boolean getLabelingInProgress() {
		return labelingInProgress;
	}
	
	/**
	* Constructor
	*/
	public StudentController(StudentModel m, StudentView v) {
    	model = m;
    	view = v;
    	view.getVerticalBar().addAdjustmentListener(new AdjustmentListener() {
 			public void adjustmentValueChanged(AdjustmentEvent e) {
 				view.setYOffset((e.getValue() * view.getHeight()) / 100);
 			}
 		});
 		draggedNode=null;
    }
	
	/**
	* Handles keyPressed events. This method handles several contexts in
	* which keyboard entry is significant. If there is an active Prompt
	* object, the event is passed to that prompt's keyPressed() method.
	* If there is a Node being labelled, then keystrokes are forwarded to
	* the keyPressed() method of {@link getTextEntryBox}, where they are
	* handled. 
	* Otherwise, the F1 key starts the test-bot and Delete triggers a Delete
	* event (much like pressing the Delete button). 
	*/
	public void keyPressed(KeyEvent e) {
		printKeyEvent(e);
		if(model.getPrompt() != null) {
			model.getPrompt().keyPressed(e);
			return;
		}
		/*
		if(e.getKeyCode() == KeyEvent.VK_F1) {
			if(model.getStudentControllerTest() == null) {
				model.setStudentControllerTest(new StudentControllerTest(this, model.getView()));
			} else {
				model.getStudentControllerTest().toggleTest();
			}
		}
		*/
		if(statusString == null)
			statusString = new String();
		
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(!labelingInProgress){
				buttonClicked = TBSButtonType.DELETE;
				handleDelete();
				buttonClicked = TBSButtonType.SELECT;
			}
		}

		if(labelingInProgress) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				model.getTextEntryBox().finishLabeling();
				cancelLabel();
				((Label) model.getHistory().peek()).setLabelAfter(((Node)selectedElement).getName(),
						((Node)selectedElement).getWidth());
				System.out.println("Added command(label) to history.");
				setSelectedElement(null);
				buttonClicked = TBSButtonType.SELECT;
			}else
				model.getTextEntryBox().keyPressed(e);
		}
	}

/**
* Not implemented
*/		
	public void keyReleased(KeyEvent e) {}

/**
* keyTyped events are passed to the currently active Prompt or to the
* node being labelled, if any. If no active prompt and no node being
* labelled, the event is discarded. 
*/
	public void keyTyped(KeyEvent e) {
		printKeyEvent(e);
		if(model.getPrompt() != null) {
			model.getPrompt().keyTyped(e);
			return;
		}
		
		if(selectedElement == null)
			return;
		
		if(labelingInProgress)
			model.getTextEntryBox().keyTyped(e);
	}
		
/**
* mouseEntered events are not used in TBS
*/		
	public void mouseEntered(MouseEvent e){}

/**
* mouseExited events are not used in TBS
*/		
	public void mouseExited(MouseEvent e){}
	
/**
* NOT YET DOCUMENTED
*/
	public void mouseMoved(MouseEvent e){
		printMouseEvent(e);
		if(buttonClicked != null && TBSButtonType.PRINT.equals(buttonClicked))
			buttonClicked = TBSButtonType.SELECT;
		Prompt prompt = model.getPrompt();
		if(prompt != null){
			if(prompt.isOverButton(e))
				view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			else
				view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			return;
		}
		int x,y,buttonIndex;
		x = e.getX();
		y = e.getY();
		Cursor c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		if(labelingInProgress)
			c = DragSource.DefaultMoveNoDrop;
		else if(y < TBSGraphics.buttonsHeight) {
			if(x >= TBSGraphics.questionButtonsStart){
				buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.questionButtonsWidth;
				if(buttonIndex < OpenQuestionButtonType.values().length)
					c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			} else {
				buttonIndex = x / TBSGraphics.buttonsWidth;
				if(buttonIndex < view.getButtons().size()) {
					TBSButtonType temp = view.getButtons().get(buttonIndex);
					if(model.isButtonActive(temp))
						c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
					else
						c = DragSource.DefaultMoveNoDrop;
				}
			}
		} else if(!view.isTooltipRunning() || buttonClicked.isCursorVariant()) {
			Node n = elementMouseIsHoveringOver(x,y);
			if(n != null){
				if(TBSButtonType.UNLINK.equals(buttonClicked)){
					if(n.getConnectedTo().size() == 0 &&
							n.getConnectedFrom().size() == 0)
						c = DragSource.DefaultMoveNoDrop;
				}
				if(n instanceof OrganismNode){
					if(TBSButtonType.LABEL.equals(buttonClicked))
						c = DragSource.DefaultMoveNoDrop;
					OrganismNode o = (OrganismNode) n;
					view.updateTooltip(o.getName(),
							new Point(o.getX() + (o.getWidth()/2), o.getY()));
				}
			}
		}
		view.setAppletCursor(c);
		
		if(selectedElement == null)
			return;
		if(selectedElement instanceof Node) {
			if(TBSButtonType.LINK.equals(buttonClicked))
				view.setConnInProgress(
		    		new Line2D.Double(((Node) selectedElement).getCenter(),
		    				new Point(e.getX(), e.getY() + view.getYOffset())));
			view.setScreenString(getStatus(TBSButtonType.LINK));
		}
	}
	

/**
* More mousehandling
*/
	public void mouseClicked(MouseEvent e) {
		printMouseEvent(e);
		if(model.getPrompt() != null)
			return;
		if(buttonClicked != null){
			if(!buttonClicked.isMode())
				buttonClicked = TBSButtonType.SELECT;
			if(!model.isButtonActive(buttonClicked))
				buttonClicked = TBSButtonType.SELECT;
			if(TBSButtonType.DELETE.equals(buttonClicked) && itemDeleted){
				buttonClicked = TBSButtonType.SELECT;
				itemDeleted = false;
			}
		}
		
	}

	/**
	* Handle mousePressed events: if the mouse is over an object, select it.
	* ALSO: This is where you get the result of a prompt
	*/
	public void mousePressed(MouseEvent e){
		printMouseEvent(e);
		if(labelingInProgress)
			return;
		Prompt prompt = model.getPrompt();

		if(prompt != null) {
			prompt.mousePressed(e);
			if(prompt.isFinished()) {
				// Get result of prompt here
				if(prompt instanceof YesNoPrompt){
					YesNoPrompt temp = (YesNoPrompt) prompt;
					switch(temp.getPromptType()){
						case CLEAR:
							if(temp.getResponse().getValue()){
								view.setScreenString(getStatus(temp.getPromptType()));
								model.resetModel();
								model.setHistory(new Stack<Command>());
								model.getButtonStates().put(TBSButtonType.UNDO, false);
								buttonClicked = TBSButtonType.SELECT;
							}
							break;
					}
					model.clearPrompt();
				}else
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
		printMouseEvent(e);
		if(labelingInProgress)
			return;
		if(model.getPrompt() != null)
			return;
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
				lastPosition = node.getAnchorPoint();
				model.addActionToHistory(new Drag(node.getId(), node.getAnchorPoint()));
			}
			node.setBeingDragged(true);
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
		printMouseEvent(e);
		if(labelingInProgress)
			return;
		if(model.getPrompt() != null)
			return;
		//Auto-add/delete: 
		if (draggedNode != null)
		{
			//Node dragged to point out of bounds
			modifyOutOfBounds(draggedNode);
			List<Node> inTreeElements = model.inTreeElements();
			if(!draggedNode.isInTree())
				draggedNode.setY(draggedNode.getY() + view.getYOffset());
			for(Node n : inTreeElements){
				if(!n.equals(draggedNode) && draggedNode.collidesWith(n)){
					draggedNode.setAnchorPoint(lastPosition);
					break;
				}
			}
			if ((draggedNode.getX()-draggedNode.getWidth()) <= TBSGraphics.LINE_OF_DEATH ){
				model.removeFromTree(draggedNode);
			}else{
				if (!draggedNode.isInTree() && (draggedNode.getX()-draggedNode.getWidth()) > TBSGraphics.LINE_OF_DEATH )
					model.addToTree(draggedNode); 
				else 
					((Drag) model.getHistory().peek()).setPointAfter(draggedNode.getAnchorPoint());
			}
			lastPosition = null;
			draggedNode.setBeingDragged(false);
			draggedNode=null;
		}
	}

	/**
	* Return the {@link TBSButtonType} of the button most recently
	* clicked. WHERE USED?
	*/	
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}
	

	/**
	* Returns draggedNode. WHERE USED?
	*/
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
    
	/**
	* Returns the identity of the node at (x,y).
	*/
    public Node elementMouseIsHoveringOver(int x, int y){
    	Node nodeHovered = null;
	    int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH) yOffset = view.getYOffset(); 	    
	    for (Node n : model.inTreeElements()) {
		    if(n.contains(x, y + yOffset)){
		    	nodeHovered = n;
		    }
		}
	    
	    return nodeHovered;
    }
   
	/**
	* returns the identity of the ModelElement at (x,y)
	*/ 
    public ModelElement elementMouseIsOver(int x, int y) {
    	ModelElement topElement = null;
	    List<ModelElement> selectedTwoWay = new LinkedList<ModelElement>();
	    int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH) 
	    	yOffset = view.getYOffset(); 	    
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y - yOffset)){
		    	topElement = me;
		    	if(me instanceof Connection)
		    		selectedTwoWay.add(me);
		    }
		}
	    if(selectedTwoWay.size() > 1)
	    	model.setSelectedTwoWay(selectedTwoWay);
	    else
	    	model.setSelectedTwoWay(null);
	    	
	    return topElement;
	}   
    
    

	// Handles attempts to place nodes outside of the applet's area. 
	
    private void modifyOutOfBounds(Node n){
    	if((n.getX()+n.getWidth()) > 
				view.getWidth() - view.getVerticalBar().getWidth())
  		{
	  		n.setX(view.getWidth() - n.getWidth() - 
				view.getVerticalBar().getWidth());
		} 
   	if(n.isInTree()) 
		{
    		if (n.getY() <= view.getYOffset() + 
				TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
    		{	
				n.setY(view.getYOffset() + TBSGraphics.buttonsHeight + 
					TBSGraphics.buttonsYPadding);
			}
    		if((n.getY() + n.getHeight()) > view.getHeight() + view.getYOffset())
    			n.setY(view.getHeight()-n.getHeight() + view.getYOffset());
    		
    	} else 
		{
    		if(n.getY() <= TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
			{
    			n.setY(TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
			}

    		if((n.getY() + n.getHeight()) > view.getHeight())
			{
    			n.setY(view.getHeight()-n.getHeight());   		
			}
    	}
    }
    

    private void cancelConnection() {
		view.setConnInProgress(null);
		view.setScreenString(null);
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
    	view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    public void creatingLabel(EmptyNode en) {
    	if(!labelingInProgress) {
    		labelingInProgress = true;
    		en.setBeingLabeled(true);
    		model.setTextEntryBox(new TextEntryBox(en));
    		model.addActionToHistory(new Label(en.getId(), en.getName(), en.getWidth()));
    		selectedElement = en;
    	}
    }

    
    private void setSelectedElement(ModelElement me) {
    	unselectPrevious();
    	model.setSelectedElement(me);
        selectedElement = me;
    }

    // unselect previously selected element, otherwise will keep green box

    private void unselectPrevious(){
    	model.setSelectedElement(null);
    	selectedElement = null;
    }
 
    public void handleDelete() {
		if(selectedElement == null) 
			return;
		view.setScreenString(null);
		clearCurrentActions();
		if(model.getSelectedTwoWay() != null){
			for(ModelElement tw : model.getSelectedTwoWay())
				model.removeFromTree(tw);
			model.setSelectedTwoWay(null);
		}else
			model.removeFromTree(selectedElement);
		unselectPrevious();
		itemDeleted = true;
    }

	/**
	* Called by () to return to a null state: no connections in progress
	* and no nodes being labelled. 
	* This method just calls {@ link cancelConnection} and {@link cancelLabel}; 
	* it has nologic of its own.
	*/    
    public void clearCurrentActions() {
		cancelConnection();
		cancelLabel();
    }
   

	/**
 	* This method provides some of the logic for directing mouse clicks.
 	* {@see handleMousePressed}
	*/
    public void handleMouseButtonPressed(int x, int y) {
    	clearCurrentActions();
		int buttonIndex = x / TBSGraphics.buttonsWidth;
		if(buttonIndex >= view.getButtons().size())
			return;
		buttonClicked = view.getButtons().get(buttonIndex);
		System.out.println(buttonClicked.toString());
		if(!buttonClicked.isConfirmation())
			view.setScreenString(getStatus(buttonClicked));
		if(!model.isButtonActive(buttonClicked)){
			buttonClicked = TBSButtonType.SELECT;
			return;
		}
		switch (buttonClicked) {
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
					if(model.getSelectedTwoWay() != null){
						Unlink u = new Unlink();
						for(ModelElement tw : model.getSelectedTwoWay()){
							Connection c = (Connection) tw;
							u.addConnection((Connection) c.clone());
							model.removeFromTree(c);
						}
						model.addActionToHistory(u);
						model.setSelectedTwoWay(null);
					}else{
						Connection c = (Connection) selectedElement;
						model.addActionToHistory(new Unlink((Connection) c.clone()));
						model.removeFromTree(c);
					}
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
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(view);
			if (printJob.printDialog()){
				try { 
					printJob.print();
				} catch(PrinterException pe) {
					System.out.println("Error printing: " + pe);
				}
			}
			break;
		case UNDO:
			if(!model.getHistory().isEmpty()){
				view.setScreenString(String.format(getStatus(TBSButtonType.UNDO),
						model.getHistory().peek().toString()));
				model.removeActionFromHistory().undo(model);
			}
			break;
		case CLEAR:
			model.viewPrompt(TBSButtonType.CLEAR);
			break;
		case HELP:
			model.helpUser();
			break;
		}
		setSelectedElement(null);
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    

	/**
	* Calls up a dialog box for the question selected by the user. 
	* @param x x coordinate of mouse click
	* @param y y coordinate of mouse click
	* {@see viewOpenResponse}
	*/
    private void handleMouseQuestionPressed(int x, int y) {
    	int buttonIndex = 
			(x - TBSGraphics.questionButtonsStart) / 
			TBSGraphics.questionButtonsWidth;
		if(buttonIndex >= OpenQuestionButtonType.values().length) return;
		questionClicked = OpenQuestionButtonType.values()[buttonIndex];
		System.out.println(questionClicked.toString());
		model.viewPrompt(questionClicked);
		setSelectedElement(null);
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

	/**
	* Handles the rest of the mouse-clicking behavior. 
	* {@see handleMouseButtonPressed}
	*/    
    public void handleMousePressed(int x, int y) {
    	ModelElement clickedElement = elementMouseIsOver(x, y);
    	// clicking on empty space always cancels connection
		if(clickedElement == null) {
			unselectPrevious();
			if(!buttonClicked.equals(TBSButtonType.ADD) 
					&& !buttonClicked.equals(TBSButtonType.DELETE))
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
				Node newNode = new EmptyNode(model.getSerial());
				newNode.setAnchorPoint(new Point(x, y + view.getYOffset()));
				newNode.setInTree(true);
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
		case DELETE:
			if(clickedElement == null){
				buttonClicked = TBSButtonType.SELECT;
				break;
			}
			selectedElement = clickedElement;
			handleDelete();
			return;
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
			try{
				if(clickedElement instanceof Node){
					model.unlink((Node) clickedElement);
				}else{
					if(model.getSelectedTwoWay() != null){
						Unlink u = new Unlink();
						for(ModelElement tw : model.getSelectedTwoWay()){
							Connection c = (Connection) tw;
							u.addConnection((Connection) c.clone());
							model.removeFromTree(c);
						}
						model.addActionToHistory(u);
						model.setSelectedTwoWay(null);
						
					}else{
						Connection c = (Connection) clickedElement;
						model.addActionToHistory(new Unlink((Connection) c.clone()));
						model.removeFromTree(c);
					}
					clickedElement = null;
				}
			}catch(CloneNotSupportedException c){
				System.out.println("Unable to add action to history.");
			}
			break;
		case LABEL:
			if(clickedElement == null)
				break;
			if(clickedElement instanceof EmptyNode)
				creatingLabel((EmptyNode) clickedElement);
			break;
		}
    	// default action unless return
    	if(clickedElement instanceof Node) {
    		if(((Node) clickedElement).isInTree()) // organism node is in tree, selectable
    			setSelectedElement(clickedElement);
		} else // default set selectedElement = clickedElement
    		setSelectedElement(clickedElement);
	}		
   

 
    public String getStatus(TBSButtonType button){
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
		return PropertyLoader.getProperties("status").getProperty(statusKey.toString());
	}    
}
