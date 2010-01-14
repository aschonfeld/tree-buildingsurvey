//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.Connection;
import tbs.model.ModelElement;
import tbs.model.OrganismNode;
import tbs.view.AdminView;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.prompt.OpenQuestionPrompt;
import tbs.view.prompt.Prompt;

/**
* TBSController contains the methods allowing the user to manipulate the
* data stored in the data model.
**/
public class AdminController extends TBSController
{
	
	private AdminModel model;
	private AdminView view;
	private TBSButtonType buttonClicked = TBSButtonType.TREE;
	
	
	public AdminController(AdminModel m, AdminView v) {
    	model = m;
    	view = v;
 		view.getVerticalBar().addAdjustmentListener(new AdjustmentListener() {
 			public void adjustmentValueChanged(AdjustmentEvent e) {
 				view.setYOffset((e.getValue() * view.getHeight()) / 100);
 			}
 		});
 		view.getStudentBar().addAdjustmentListener(new AdjustmentListener() {
 			public void adjustmentValueChanged(AdjustmentEvent e) {
 				view.setStudentYOffset((e.getValue() * view.getHeight()) / 100);
 			}
 		});
    }
	
//	@Override
	public void handleMousePressed(int x, int y) {
		handleStudentPressed(x, y);		
	}

//	@Override
	public void keyPressed(KeyEvent arg0) {}

//	@Override
	public void keyReleased(KeyEvent arg0) {}

//	@Override
	public void keyTyped(KeyEvent arg0) {}
		
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
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
		if (x > view.getStudentBar().getWidth() && x < TBSGraphics.studentNodeWidth){
			int studentIndex = (y + view.getStudentYOffset()) / TBSGraphics.studentNodeHeight;
			if(studentIndex < model.getStudents().size())
				c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		} else if(y < TBSGraphics.buttonsHeight)  {
			if(x >= TBSGraphics.questionButtonsStart){
				buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.buttonsWidth;
				if(buttonIndex < OpenQuestionButtonType.values().length)
					c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			}
		} else if(!view.isTooltipRunning()){
			ModelElement m = elementMouseIsOver(x,y);
			if(m != null && m instanceof OrganismNode){
				OrganismNode o = (OrganismNode) m;
				if(o.isInTree()){
					view.updateTooltip(o.getName(),
							new Point(o.getAnchorPoint().x + (o.getWidth()/2), o.getAnchorPoint().y));
				}
			}
		}
		view.setAppletCursor(c);
	}
	
	// No need to use since mousePressed is used instead
	public void mouseClicked(MouseEvent e) {}
	
	/**
	* Handle mousePressed events: if the mouse is over an object, select it.
	* ALSO: This is where you get the result of a prompt
	*/
	public void mousePressed(MouseEvent e){
		Prompt prompt = model.getPrompt();
		if(prompt != null) {
			prompt.mousePressed(e);
			if(prompt.isFinished()) {
				// Get result of prompt here
				OpenQuestionPrompt temp = (OpenQuestionPrompt) prompt;
				String input = temp.getUserInput();
				OpenQuestionButtonType q = temp.getCurrentQuestion();
				model.setQuestion(input, q);
				if(q.ordinal() < OpenQuestionButtonType.THREE.ordinal())
					model.promptUser(new OpenQuestionPrompt(model, OpenQuestionButtonType.values()[q.ordinal()+1]));
				else
					model.clearPrompt();
			}
			return;
		}
		view.requestFocusInWindow();
        int x = e.getX();
        int y = e.getY();
        // if mouse is in button bar
        if (x > view.getStudentBar().getWidth() && x < TBSGraphics.studentNodeWidth)
			handleStudentPressed(x, y);
		else if(y < TBSGraphics.buttonsHeight) {
			if(x >= TBSGraphics.questionButtonsStart)
				handleMouseButtonPressed(x, y);
		}
	}
	
	/**
	* Handle mouseDragged events: adjust position of selected Node and
	* refresh screen image.
	*/
	public void mouseDragged(MouseEvent e){}
	

	/**
	* Handle mouseReleased events. Drop the object being dragged and
	* correct its location if necessary. 
	*/	
	public void mouseReleased(MouseEvent e){}
	
	public TBSButtonType getButtonClicked() {
		return buttonClicked;
	}
	
	public ModelElement elementMouseIsOver(int x, int y) {
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
	    	
	    return topElement;
	}   

	@Override
    public void handleMouseButtonPressed(int x, int y) {
    	int buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.buttonsWidth;
		if(buttonIndex >= model.getButtons().length)
			return;
		buttonClicked = model.getButtons()[buttonIndex];
		System.out.println(buttonClicked.toString());
		switch (buttonClicked) {
		case TREE:
			break;
		case OPEN_RESPONSE:
			model.promptUser(new OpenQuestionPrompt(model, OpenQuestionButtonType.ONE));
			break;
		case ANALYSIS:
			model.promptUser(new OpenQuestionPrompt(model, OpenQuestionButtonType.ONE));
			break;
		}
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }	
    
    private void handleStudentPressed(int x, int y) {
    	int studentIndex = (y + view.getStudentYOffset()) / TBSGraphics.studentNodeHeight;
		if(studentIndex >= model.getStudents().size())
			return;
		model.changeSavedTree(studentIndex);
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
