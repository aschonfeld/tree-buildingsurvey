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
import java.util.List;
import java.util.Stack;

import tbs.TBSGraphics;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.StudentModel;
import tbs.model.history.Command;
import tbs.model.history.Drag;
import tbs.model.history.Label;
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
	private Point lastPosition = null;
	private TBSButtonType buttonClicked = TBSButtonType.SELECT;
	private OpenQuestionButtonType questionClicked = null;
	private boolean dragInProgress = false;
	private boolean labelingInProgress = false;
	
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
		if(e.getKeyCode() == KeyEvent.VK_DELETE) {
			if(!labelingInProgress){
				if(model.getSelectedElement() == null) 
					return;
				clearCurrentActions();
				view.setScreenString("You have removed " + model.getSelectedElement().toString());
				ModelUtils.removeElement(model.getSelectedElement(), model, false);
				model.setSelectedElement(null);
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(TBSButtonType.ADD.equals(buttonClicked)){
				buttonClicked = TBSButtonType.SELECT;
			}
		}

		if(labelingInProgress) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				model.getTextEntryBox().finishLabeling();
				labelingInProgress = false;
				((Label) model.getHistory().peek()).setLabelAfter(((Node)model.getSelectedElement()).getName(),
						((Node)model.getSelectedElement()).getWidth());
				System.out.println("Added command(label) to history.");
				model.setSelectedElement(null);
				buttonClicked = TBSButtonType.SELECT;
				view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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

		if(model.getSelectedElement() == null)
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
		if(dragInProgress)
			return;
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
				if(buttonIndex < 1)
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
			if (x <= TBSGraphics.LINE_OF_DEATH){
				if(buttonClicked.ordinal() >= 1 && buttonClicked.ordinal() <= 5)
					c = DragSource.DefaultMoveNoDrop;
			}else{
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
								new Point(o.getX() + (o.getWidth()/2), o.getY()-o.getHeight()));
					}
				}
			}
		}
		view.setAppletCursor(c);
		
		if(model.getSelectedElement() == null)
			return;
		if(model.getSelectedElement() instanceof Node) {
			if(TBSButtonType.LINK.equals(buttonClicked))
				view.setConnInProgress(
		    		new Line2D.Double(((Node) model.getSelectedElement()).getCenter(),
		    				new Point(e.getX(), e.getY() + view.getYOffset())));
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
        previousX = x;
        previousY = y;
		// if mouse is in button bar
		if(y < TBSGraphics.buttonsHeight)  {
			if(x >= TBSGraphics.questionButtonsStart)
				handleMouseQuestionPressed(x, y);
			handleMouseButtonPressed(x, y);
		} else if (x > TBSGraphics.LINE_OF_DEATH)
			handleMousePressed(x, y);
		else {
			if(buttonClicked.ordinal() == 0){
				int index = indexMouseIsOver(x,y);
				if(index >= 0){
					model.setSelectedElement(model.getElement(index));
					view.setScreenString(getStatus(TBSButtonType.SELECT));
				}else{
					model.setSelectedElement(null);
					if(model.inTreeElements().isEmpty())
						view.setScreenString(null);
					else
						view.setScreenString(getStatus(TBSButtonType.SELECT));
				}
			}
		}
	}
	
	/**
	* Handle mouseDragged events: adjust position of selected Node and
	* refresh screen image.
	*/
	public void mouseDragged(MouseEvent e){
		printMouseEvent(e);
		if(labelingInProgress || model.getPrompt() != null)
			return;
		dragInProgress = true;
		int x = e.getX();
		int y = e.getY();
		int deltaX = x - previousX;
		int deltaY = y - previousY;
		if(model.getSelectedElement() == null)
			return;
		if(model.getSelectedElement() instanceof Node) {
			// Move Node
			Node node = (Node) model.getSelectedElement();
			if(lastPosition == null){
				lastPosition = node.getAnchorPoint();
				model.addActionToHistory(new Drag(node.getId(), node.getAnchorPoint()));
			}
			node.setBeingDragged(true);
			if(node.isInTree()) {
				node.move(deltaX, deltaY);
			} else {
				// if organism node being added to tree snap to mouse location
				node.moveTo(x, y);
			}
		}
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
		if(dragInProgress){
			//Auto-add/delete:
			if(model.getSelectedElement() == null)
				return;
			if (model.getSelectedElement() instanceof Node)
			{
				Node draggedNode = (Node) model.getSelectedElement();
				//Node dragged to point out of bounds
				modifyOutOfBounds(draggedNode);
				List<Node> inTreeElements = model.inTreeElements();
				if(!draggedNode.isInTree())
					draggedNode.setY(draggedNode.getY() + view.getYOffset());
				for(Node inTreeElement : inTreeElements){
					if(!inTreeElement.equals(draggedNode) && inTreeElement.collidesWith(draggedNode)){
						draggedNode.setAnchorPoint(lastPosition);
						break;
					}
				}
				if ((draggedNode.getX()-draggedNode.getWidth()) <= TBSGraphics.LINE_OF_DEATH )
					ModelUtils.removeElement(draggedNode, model, false);
				else{
					if (!draggedNode.isInTree() && (draggedNode.getX()-draggedNode.getWidth()) > TBSGraphics.LINE_OF_DEATH )
						ModelUtils.addNode(draggedNode, model, false); 
					else 
						((Drag) model.getHistory().peek()).setPointAfter(draggedNode.getAnchorPoint());
				}
				lastPosition = null;
				draggedNode.setBeingDragged(false);
				model.setSelectedElement(null);
			}
			dragInProgress = false;
		}
	}

	/**
	* Return the {@link TBSButtonType} of the button most recently
	* clicked. WHERE USED?
	*/	
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}
	

	private int indexMouseIsOver(int x, int y) {
	    int maxIndex = -1;
	    int i = 0;
	    int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH) 
	    	yOffset = view.getYOffset(); 
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y + yOffset))
		    	maxIndex = i;
		    i++;
		}
		return maxIndex;
	}
    
	/**
	* Returns the identity of the node at (x,y).
	*/
    public Node elementMouseIsHoveringOver(int x, int y){
    	int yOffset = 0;
	    if(x > TBSGraphics.LINE_OF_DEATH)
	    	yOffset = view.getYOffset(); 	    
	    for (Node n : model.inTreeElements()) {
		    if(n.contains(x, y + yOffset))
		    	return n;
		}
	    return null;
    }
   
	/**
	* returns the identity of the ModelElement at (x,y)
	*/ 
    public ModelElement elementMouseIsOver(int x, int y) {
    	int index = indexMouseIsOver(x,y);
    	if(index > 0)
    		return model.getElement(index);
	    return null;
	}   
    
    

	// Handles attempts to place nodes outside of the applet's area. 
	
    private void modifyOutOfBounds(Node n){
    	if((n.getX()+n.getWidth()) > view.getWidth() - view.getVerticalBar().getWidth())
    		n.setX(view.getWidth() - n.getWidth() - view.getVerticalBar().getWidth());
    	if(n.isInTree()){
    		if (n.getY() <= view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
    			n.setY(view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    		if((n.getY() + n.getHeight()) > view.getHeight() + view.getYOffset())
    			n.setY(view.getHeight()-n.getHeight() + view.getYOffset());
    	} else {
    		if(n.getY() <= TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding)
    			n.setY(TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    		if((n.getY() + n.getHeight()) > view.getHeight())
    			n.setY(view.getHeight()-n.getHeight());   		
    	}
    }
    
    // creates a connection if conditions are correct
    // returns true if connection created
    private boolean creatingConnection(Node n) {
    	if(n.isInTree()){
    		if(n != model.getSelectedElement()) {
    			if(model.getSelectedElement() instanceof Node) {
    				ModelUtils.addConnection((Node) model.getSelectedElement(), n, model, false);
    				view.setConnInProgress(null);
    				return true;
    			}
    		}
    	}
    	view.setConnInProgress(null);
        return false;
    }
    
    public void creatingLabel(EmptyNode en) {
    	if(!labelingInProgress) {
    		labelingInProgress = true;
    		en.setBeingLabeled(true);
    		model.setTextEntryBox(new TextEntryBox(en));
    		model.addActionToHistory(new Label(en.getId(), en.getName(), en.getWidth()));
    		model.setSelectedElement(en);
    	}
    }

    /**
	* Called by () to return to a null state: no connections in progress
	* and no nodes being labeled. 
	* This method just calls {@ link cancelConnection} and {@link cancelLabel}; 
	* it has no logic of its own.
	*/    
    public void clearCurrentActions() {
    	view.setConnInProgress(null);
    	labelingInProgress = false;
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
		if(!model.isButtonActive(buttonClicked)){
			buttonClicked = TBSButtonType.SELECT;
			return;
		}
		if(!TBSButtonType.UNDO.equals(buttonClicked) && !buttonClicked.isConfirmation()){
			if(TBSButtonType.SELECT.equals(buttonClicked)){
				if(model.inTreeElements().isEmpty())
					view.setScreenString(null);
				else
					view.setScreenString(getStatus(TBSButtonType.SELECT));
			}
			view.setScreenString(getStatus(buttonClicked));
		}
		if(buttonClicked.isConfirmation())
			view.setScreenString(null);
		if(model.getSelectedElement() != null){
			switch (buttonClicked) {
				case DELETE:
					clearCurrentActions();
					ModelUtils.removeElement(model.getSelectedElement(), model, false);
					buttonClicked = TBSButtonType.SELECT;
					break;
				case LINK:
					if(model.getSelectedElement() instanceof Node)
						return;
				case UNLINK:
					ModelUtils.unlinkElement(model.getSelectedElement(), model);
					break;
				case LABEL:
					if(model.getSelectedElement() instanceof EmptyNode){
						creatingLabel((EmptyNode) model.getSelectedElement());
						return;
					}
					break;
			}
		}
		switch(buttonClicked){
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
		model.setSelectedElement(null);
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
    

	/**
	* Calls up a dialog box for the question selected by the user. 
	* @param x x coordinate of mouse click
	* @param y y coordinate of mouse click
	* {@see viewOpenResponse}
	*/
    private void handleMouseQuestionPressed(int x, int y) {
    	int buttonIndex = (x - TBSGraphics.questionButtonsStart)/TBSGraphics.questionButtonsWidth;
		if(buttonIndex > 0)
			return;
		questionClicked = OpenQuestionButtonType.values()[buttonIndex];
		System.out.println(questionClicked.toString());
		model.viewPrompt(questionClicked);
		model.setSelectedElement(null);
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
    		model.setSelectedElement(null);
    		if(!buttonClicked.equals(TBSButtonType.ADD)){
    			buttonClicked = TBSButtonType.SELECT;
    			clearCurrentActions();
    			if(model.inTreeElements().isEmpty())
					view.setScreenString(null);
				else
					view.setScreenString(getStatus(TBSButtonType.SELECT));
    			return;
    		}
    	}
		
		if(!TBSButtonType.SELECT.equals(buttonClicked)){
			switch (buttonClicked) {
				case ADD:
					if(x > view.getWidth() - view.getVerticalBar().getWidth()) 
						x = view.getWidth() - view.getVerticalBar().getWidth();
					EmptyNode newNode = new EmptyNode(model.getSerial());
					newNode.setAnchorPoint(new Point(x, y + view.getYOffset()));
					newNode.setInTree(true);
					modifyOutOfBounds(newNode);
					for(Node n : model.inTreeElements()){
						// make sure not putting it on top of another item
						if(n.collidesWith(newNode)){
							newNode = null;
							break;
						}
					}
					if(newNode != null)
						ModelUtils.addNode(newNode, model, false);
					break;
				case DELETE:
					ModelUtils.removeElement(clickedElement, model, false);
					buttonClicked = TBSButtonType.SELECT;
					return;
				case LINK:
					if(clickedElement instanceof Node) {
						if(creatingConnection((Node) clickedElement)) {
							// do not automatically start a new connection
							clickedElement = null;
						}
						break;
					}
					break;
				case UNLINK:
					ModelUtils.unlinkElement(clickedElement, model);
					break;
				case LABEL:
					if(clickedElement instanceof EmptyNode)
						creatingLabel((EmptyNode) clickedElement);
					break;
			}
		}
		
    	// default action unless return
    	if(clickedElement instanceof Node) {
    		if(((Node) clickedElement).isInTree()) // organism node is in tree, selectable
    			model.setSelectedElement(clickedElement);
		} else // default set selectedElement = clickedElement
			model.setSelectedElement(clickedElement);
	}		
   

 
    public String getStatus(TBSButtonType button){
    	StringBuffer statusKey = new StringBuffer(buttonClicked.name());
		if(buttonClicked.isItemSelectionBased()){
			if(model.getSelectedElement() == null)
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
