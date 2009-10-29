//TBSController v0.01

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TBSController implements MouseListener, MouseMotionListener, ActionListener {
	
	private TBSModel tbsModel;
	private TBSView tbsView;
	
	private ArrayList<Integer> selectedIndices;
    private boolean mousePressed;
    private boolean mouseDragged;
    private boolean draggingElement;
    private int previousX, previousY;
    
    public TBSController(TBSModel m, TBSView v){
    	tbsModel = m;
    	tbsView = v;
    	mousePressed = false;
    	mouseDragged = false;
    }
    
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	public void mousePressed(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		selectedIndices = new ArrayList<Integer>();
		for(Integer Int: mouseIsOver(x, y)) {
			selectedIndices.add(Int);
			//System.out.println(x + " " + y);
		}
		previousX = x;
		previousY = y;
		//System.out.println("Pressed: " + x + " " + y);
	}
	
	public void mouseDragged(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int deltaX = x - previousX;
		int deltaY = y - previousY;
		if(selectedIndices.size() > 0) {
			for(Integer Int: selectedIndices) {
				int i = Int.intValue();
				//System.out.println(i);
				TBSModel.ModelElement me = tbsModel.getElement(i);
				if(me instanceof TBSModel.Node) {
					// Move Node
					TBSModel.Node tbsNode = (TBSModel.Node) me;
					if(tbsNode.isInTree()) {
						tbsNode.move(deltaX, deltaY);
					} else {
						tbsNode.addToTree();
						tbsNode.moveTo(x, y);
					}
					// Refresh Node
					tbsModel.setElement(i, tbsNode);
				}
			}
		}
		tbsView.refreshGraphics();
		// update our data
		previousX = x;
		previousY = y;
		
 	}
	
	
	public void mouseReleased(MouseEvent e) {
		mousePressed = false;
		mouseDragged = false;
		int x = e.getX();
		int y = e.getY();
		selectedIndices = new ArrayList(); // clear selected items
		//System.out.println("Released: " + x + " " + y);
	}
	
	public void actionPerformed(ActionEvent e) {
        //System.out.println(e.getActionCommand()); 
    }
    
    private ArrayList<Integer> mouseIsOver(int x, int y) {
	    ArrayList<Integer> activeIndices= new ArrayList<Integer>();
	    int numElements = tbsModel.numElements();
	    for (int i = 0; i < numElements; i++) {
		    TBSModel.ModelElement var = tbsModel.getElement(i);
		    if(var.isOverMe(x, y)) {
		    	activeIndices.add(i);
		    }
		}
		return activeIndices;
	}		    
	
}
