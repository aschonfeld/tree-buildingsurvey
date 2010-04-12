//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.admin.Student;
import tbs.view.AdminView;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;

/**
* TBSController contains the methods allowing the user to manipulate the
* data stored in the data model.
**/
public class AdminController extends TBSController
{
	
	private AdminModel model;
	private AdminView view;
	
	
	public AdminController(AdminModel m, AdminView v) {
	  super(m, v, TBSButtonType.TREE);
	  model = m;
	  view = v;
	  view.getStudentBar().addAdjustmentListener(new AdjustmentListener() {
	    public void adjustmentValueChanged(AdjustmentEvent e) {
	      view.setStudentYOffset(e.getValue());
	    }
	  });
	}
	
	public void handleMousePressed(int x, int y) {
		handleStudentPressed(x, y);		
	}

	public void keyPressed(KeyEvent e) {
		List<Student> students = model.getStudents();
		int index = students.indexOf(model.getStudent());
		if(e.getKeyCode() == KeyEvent.VK_DOWN){
			if(index < students.size()-1){
				model.changeSavedTree(index+1);
				view.getHorizontalBar().setValue(0);
				view.setXOffset(0);
				boolean moveBar = ((model.getStudent().getAnchorPoint().y + TBSGraphics.studentNodeHeight + TBSGraphics.ySpacing) - view.getStudentYOffset()) > model.getApplet().getHeight();
				if(moveBar)
					view.getStudentBar().setValue(view.getStudentBar().getValue() + view.getStudentBar().getBlockIncrement());
			}
		}else if(e.getKeyCode() == KeyEvent.VK_UP){
			if(index > 0){
				model.changeSavedTree(index-1);
				view.getHorizontalBar().setValue(0);
				view.setXOffset(0);
				boolean moveBar = (model.getStudent().getAnchorPoint().y - view.getStudentYOffset()) < 0;
				if(moveBar)
					view.getStudentBar().setValue(view.getStudentBar().getValue() - view.getStudentBar().getBlockIncrement());
			}
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		Prompt prompt = model.getPrompt();
		if(prompt != null){
			if(prompt.isOverButton(e)){
				view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				return;
			}
		}
		int x,y,buttonIndex;
		x = e.getX();
		y = e.getY();
		Cursor c = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		int scrollWidth = view.hasStudentScroll() ? view.getStudentBar().getWidth() : 0;
		int studentButtonWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
		
		int hullHeaderEnd = model.getApplet().getWidth()-(TBSGraphics.buttonsWidth/2 + TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth());
        int totalHullButtonsHeight = model.getHulls().size() * TBSGraphics.hullButtonHeight;
        Rectangle hullButtons = new Rectangle();
        if(view.isHullMenuDisplayed())
        	hullButtons = new Rectangle(hullHeaderEnd - TBSGraphics.groupsButtonWidth,
        			TBSGraphics.buttonsHeight, TBSGraphics.hullButtonWidth, totalHullButtonsHeight);
        
		if (x > scrollWidth && x < (studentButtonWidth+scrollWidth)){
			int studentIndex = (y + view.getStudentYOffset()) / (TBSGraphics.studentNodeHeight+TBSGraphics.ySpacing);
			if(studentIndex < model.getStudents().size())
				c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		} else if(y < TBSGraphics.buttonsHeight)  {
			if(x >= (hullHeaderEnd - TBSGraphics.groupsButtonWidth) && x < hullHeaderEnd){
				if(view.isHullMenuDisplayed())
					view.getHullTimer().restart();
				else
					view.getHullTimer().start();
			}else if(x >= model.getApplet().getWidth()-(TBSGraphics.buttonsWidth/2 + TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth()))
				c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			else if(x >= TBSGraphics.questionButtonsStart){
				buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.buttonsWidth;
				if(buttonIndex < OpenQuestionButtonType.values().length)
					c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
			}
		} else if(hullButtons.contains(x, y)){
			if(view.isHullMenuDisplayed())
				view.getHullTimer().restart();
			else
				view.getHullTimer().start();
			c = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		} else if(TBSButtonType.TREE.equals(getButtonClicked()) && !view.getDisplayAllTooltips()){
			if(!view.isTooltipRunning()){
				Node n = elementMouseIsHoveringOver(x,y);
				if(n != null && n instanceof OrganismNode){
					OrganismNode o = (OrganismNode) n;
					view.updateTooltip(o.getName(),
								new Point(o.getX() + (o.getWidth()/2), o.getY()-o.getHeight()));
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
		view.requestFocusInWindow();
		int x = e.getX();
        int y = e.getY();
		if(y < TBSGraphics.buttonsHeight) {
			if(x >= (model.getApplet().getWidth()-(TBSGraphics.buttonsWidth/2 + view.getVerticalBar().getWidth()))){
				PrinterJob printJob = PrinterJob.getPrinterJob();
				printJob.setPrintable(view);
				if (printJob.printDialog()){
					try { 
						printJob.print();
					} catch(PrinterException pe) {
						System.out.println("Error printing: " + pe);
					}
				}
				return;
			}else if(x >= (model.getApplet().getWidth()-(TBSGraphics.buttonsWidth/2 + TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth())))
				view.toggleDisplayAllTooltips();
			else if(x >= TBSGraphics.questionButtonsStart){
				handleMouseButtonPressed(x, y);
				return;
			}
		}
		
		int hullHeaderEnd = model.getApplet().getWidth()-(TBSGraphics.buttonsWidth/2 + TBSGraphics.namesButtonWidth + view.getVerticalBar().getWidth());
		int totalHullButtonsHeight = model.getHulls().size() * TBSGraphics.hullButtonHeight;
		Rectangle hullButtons = new Rectangle();
        if(view.isHullMenuDisplayed())
        	hullButtons = new Rectangle(hullHeaderEnd - TBSGraphics.groupsButtonWidth,
        			TBSGraphics.buttonsHeight, TBSGraphics.hullButtonWidth, totalHullButtonsHeight);
		if(hullButtons.contains(x, y)){
			int hullIndex = (y - TBSGraphics.buttonsHeight) / TBSGraphics.hullButtonHeight;
			model.getHulls().get(hullIndex).toggleHull();
			view.getHullTimer().stop();
			return;
		}
				
		
		Prompt prompt = model.getPrompt();
		if(prompt != null) {
			prompt.mousePressed(e);
			if(prompt.isFinished())
				model.clearPrompt();
			return;
		}
		
        // if mouse is in button bar
		int scrollWidth = view.hasStudentScroll() ? view.getStudentBar().getWidth() : 0;
		int studentButtonWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
        if (x > scrollWidth && x < (studentButtonWidth+scrollWidth))
			handleStudentPressed(x, y);
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

    public void handleMouseButtonPressed(int x, int y) {
    	int buttonIndex = (x - TBSGraphics.questionButtonsStart) / TBSGraphics.buttonsWidth;
		if(buttonIndex >= view.getButtons().size())
			return;
		setButtonClicked(view.getButtons().get(buttonIndex));
		System.out.println(getButtonClicked().toString());
		model.viewPrompt(getButtonClicked());
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }	
    
    private void handleStudentPressed(int x, int y) {
    	int studentIndex = (y + view.getStudentYOffset()) / (TBSGraphics.studentNodeHeight+TBSGraphics.ySpacing);
		if(studentIndex >= model.getStudents().size())
			return;
		model.changeSavedTree(studentIndex);
		view.getHorizontalBar().setValue(0);
		view.setXOffset(0);
		view.setAppletCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
