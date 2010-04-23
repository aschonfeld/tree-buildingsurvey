//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.dnd.DragSource;
import java.awt.event.ComponentEvent;
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
import tbs.properties.PropertyLoader;
import tbs.view.OpenQuestionButtonType;
import tbs.view.StudentView;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.ResizeWarningPrompt;
import tbs.view.prompt.student.TextEntryBox;
import tbs.view.prompt.student.WelcomePrompt;
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
	private OpenQuestionButtonType questionClicked = null;
	private boolean dragInProgress = false;
  
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
	* Constructor
	*/
	public StudentController(StudentModel model, StudentView view) {
      super(model, view, TBSButtonType.SELECT);
    	this.model = model;
    	this.view = view;
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
    	Prompt p = model.getPrompt();
		if(p != null) {
			p.keyPressed(e);
      		if(p instanceof TextEntryBox && e.getKeyCode() == KeyEvent.VK_ENTER){
        		setButtonClicked(TBSButtonType.SELECT);
        		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      		}
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
			if(model.getSelectedElement() != null){ 
				view.setScreenString("You have removed " + model.getSelectedElement().toString());
				ModelUtils.removeElement(model.getSelectedElement(), model, false);
				view.setConnInProgress(null);
				model.setSelectedElement(null);
			}
		}
		
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			if(TBSButtonType.ADD.equals(getButtonClicked()))
				setButtonClicked(TBSButtonType.SELECT);
		}
	}

	/**
	 * Not implemented
	 */		
	public void keyReleased(KeyEvent e) {}

	/**
	 * keyTyped events are passed to the currently active Prompt or to the
	 * node being labeled, if any. If no active prompt and no node being
	 * labeled, the event is discarded. 
	 */
	public void keyTyped(KeyEvent e) {
		printKeyEvent(e);
		if(model.getPrompt() != null)
			model.getPrompt().keyTyped(e);
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
		if(TBSButtonType.PRINT.equals(getButtonClicked()))
			setButtonClicked(TBSButtonType.SELECT);
		Prompt prompt = model.getPrompt();
		if(prompt != null){
			view.setAppletCursor(prompt.getCursor(e));
			return;
		}
		int x,y,buttonIndex;
		x = e.getX();
		y = e.getY();
		Cursor c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		if(y < TBSGraphics.buttonsHeight) {
			if(x >= model.getApplet().getWidth()-(TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth()))
				c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			else if(x >= TBSGraphics.questionButtonsStart){
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
		} else if(!view.isTooltipRunning() || getButtonClicked().isCursorVariant()) {
			if (x <= TBSGraphics.LINE_OF_DEATH){
				if(getButtonClicked().ordinal() >= 1 && getButtonClicked().ordinal() <= 5)
					c = DragSource.DefaultMoveNoDrop;
			}else{
				Node n = elementMouseIsHoveringOver(x,y);
				if(n != null){
					if(TBSButtonType.UNLINK.equals(getButtonClicked())){
						if(n.getConnectedTo().size() == 0 &&
								n.getConnectedFrom().size() == 0)
							c = DragSource.DefaultMoveNoDrop;
					}
					if(n instanceof OrganismNode){
						if(TBSButtonType.LABEL.equals(getButtonClicked()))
							c = DragSource.DefaultMoveNoDrop;
						if(!view.getDisplayAllTooltips()){
							OrganismNode o = (OrganismNode) n;
							view.updateTooltip(o.getName(),
									new Point(o.getX() + (o.getWidth()/2), o.getY()-o.getHeight()));
						}
					}
				}
			}
		}
		view.setAppletCursor(c);
		
		if(model.getSelectedElement() == null)
			return;
		if(model.getSelectedElement() instanceof Node) {
			if(TBSButtonType.LINK.equals(getButtonClicked()))
				view.setConnInProgress(
		    		new Line2D.Double(
		    				((Node) model.getSelectedElement()).getCenter(),
		    				new Point(x + view.getXOffset(), y + view.getYOffset())));
		}
	}
	

	/**
	 * More mousehandling
	 */
	public void mouseClicked(MouseEvent e) {}

	/**
	* Handle mousePressed events: if the mouse is over an object, select it.
	* ALSO: This is where you get the result of a prompt
	*/
	public void mousePressed(MouseEvent e){
		printMouseEvent(e);
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
							setButtonClicked(TBSButtonType.SELECT);
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
			if(x >= model.getApplet().getWidth()-(TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth()))
				view.toggleDisplayAllTooltips();
			else if(x >= TBSGraphics.questionButtonsStart)
				handleMouseQuestionPressed(x, y);
			handleMouseButtonPressed(x, y);
		} else if (x > TBSGraphics.LINE_OF_DEATH)
			handleMousePressed(x, y);
		else {
			if(getButtonClicked().ordinal() == 0){
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
		if(getButtonClicked() != null){
			if(!getButtonClicked().isMode())
				setButtonClicked(TBSButtonType.SELECT);
			if(!model.isButtonActive(getButtonClicked()))
				setButtonClicked(TBSButtonType.SELECT);
		}		
	}
	
	/**
	* Handle mouseDragged events: adjust position of selected Node and
	* refresh screen image.
	*/
	public void mouseDragged(MouseEvent e){
		printMouseEvent(e);
		if(model.getPrompt() != null)
			return;
		int x = e.getX();
		int y = e.getY();
		int deltaX = x - previousX;
		int deltaY = y - previousY;
		if(model.getSelectedElement() == null)
			return;
		if(model.getSelectedElement() instanceof Node) {
			dragInProgress = true;
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
				node.moveTo(x+view.getXOffset(), y+view.getYOffset());
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
				List<ModelElement> inTreeElements = model.inTreeElements();
				for(ModelElement inTreeElement : inTreeElements){
				  if(!inTreeElement.equals(draggedNode)){
				    if(inTreeElement instanceof Node && inTreeElement.collidesWith(draggedNode)){
				      draggedNode.setAnchorPoint(lastPosition);
				      break;
				    }
				  }
				}
				if ((draggedNode.getX()-draggedNode.getWidth()) <= TBSGraphics.LINE_OF_DEATH ){
					view.setScreenString("You have removed " + draggedNode.toString());
					ModelUtils.removeElement(draggedNode, model, false);
				}else{
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

	/**
 	* This method provides some of the logic for directing mouse clicks.
 	* {@see handleMousePressed}
	*/
    public void handleMouseButtonPressed(int x, int y) {
    	view.setConnInProgress(null);
    	int buttonIndex = x / TBSGraphics.buttonsWidth;
    	if(buttonIndex >= view.getButtons().size())
    		return;
    	setButtonClicked(view.getButtons().get(buttonIndex));
    	System.out.println(getButtonClicked().toString());
    	if(!model.isButtonActive(getButtonClicked())){
    		setButtonClicked(TBSButtonType.SELECT);
    		return;
    	}
    	if(!TBSButtonType.UNDO.equals(getButtonClicked()) && !getButtonClicked().isConfirmation()){
			if(TBSButtonType.SELECT.equals(getButtonClicked())){
				if(model.inTreeElements().isEmpty())
					view.setScreenString(null);
				else
					view.setScreenString(getStatus(TBSButtonType.SELECT));
			}
			view.setScreenString(getStatus(getButtonClicked()));
		}
		if(getButtonClicked().isConfirmation())
			view.setScreenString(null);
		if(model.getSelectedElement() != null){
			switch (getButtonClicked()) {
				case DELETE:
					view.setScreenString("You have removed " + model.getSelectedElement().toString());
					ModelUtils.removeElement(model.getSelectedElement(), model, false);
          setButtonClicked(TBSButtonType.SELECT);
					break;
				case LINK:
					if(model.getSelectedElement() instanceof Node)
						return;
				case UNLINK:
					ModelUtils.unlinkElement(model.getSelectedElement(), model);
					break;
				case LABEL:
					if(model.getSelectedElement() instanceof EmptyNode){
					  	ModelUtils.labelEmptyNode(null, model);
						return;
					}
					break;
			}
		}
		switch(getButtonClicked()){
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
    		if(!TBSButtonType.ADD.equals(getButtonClicked())){
    			setButtonClicked(TBSButtonType.SELECT);
    			view.setConnInProgress(null);
    			if(model.inTreeElements().isEmpty())
    				view.setScreenString(null);
    			else
    				view.setScreenString(getStatus(TBSButtonType.SELECT));
    			return;
    		}
    	}
		
		if(!TBSButtonType.SELECT.equals(getButtonClicked())){
			switch (getButtonClicked()) {
				case ADD:
					if(x > view.getWidth() - view.getVerticalBar().getWidth()) 
						x = view.getWidth() - view.getVerticalBar().getWidth();
					EmptyNode newNode = new EmptyNode(model.getSerial());
					newNode.setAnchorPoint(new Point(x, y + view.getYOffset()));
					newNode.setInTree(true);
          			modifyOutOfBounds(newNode);
					for(ModelElement me : model.inTreeElements()){
						// make sure not putting it on top of another item
						if(me.collidesWith(newNode)){
							newNode = null;
							break;
						}
					}
					if(newNode != null)
						ModelUtils.addNode(newNode, model, false);
					break;
				case DELETE:
					view.setScreenString("You have removed " + clickedElement.toString());
					ModelUtils.removeElement(clickedElement, model, false);
          setButtonClicked(TBSButtonType.SELECT);
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
            			ModelUtils.labelEmptyNode((EmptyNode) clickedElement, model);
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
    	StringBuffer statusKey = new StringBuffer(getButtonClicked().name());
		if(getButtonClicked().isItemSelectionBased()){
			if(model.getSelectedElement() == null)
				statusKey.append("1");
			else
				statusKey.append("2");
		}
		statusKey.append("_");
		Boolean buttonState = model.getButtonStates().get(getButtonClicked());
		statusKey.append(buttonState.toString());
		return PropertyLoader.getProperties("status").getProperty(statusKey.toString());
	}    
    
    //Handles attempts to place nodes outside of the applet's area. 
    public void modifyOutOfBounds(Node n){
    	if((n.getX()+n.getWidth()) > view.getWidth() - view.getVerticalBar().getWidth())
    		n.setX(view.getWidth() - n.getWidth() - view.getVerticalBar().getWidth());
    	if (n.getY() <= view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.padding.height)
    		n.setY(view.getYOffset() + TBSGraphics.buttonsHeight + TBSGraphics.padding.height);
    	if((n.getY() + n.getHeight()) > view.getHeight() + view.getYOffset())
    		n.setY((view.getHeight()+ view.getYOffset())-n.getHeight());
    }

	public void componentHidden(ComponentEvent ce) {}
	public void componentMoved(ComponentEvent ce) {}
	public void componentResized(ComponentEvent ce) {
		if(view.getWidth() <= 945 || view.getHeight() <= 575){
			model.setPrompt(new ResizeWarningPrompt(model, view.getWidth()));
		}else{
			if(model.getPrompt() instanceof ResizeWarningPrompt){
				model.clearPrompt();
				if(!model.getWelcomePromptShown())
					model.setPrompt(new WelcomePrompt(model));
			}
		}
	}

	public void componentShown(ComponentEvent ce) {}
}
