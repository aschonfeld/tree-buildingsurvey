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
	
	private TBSModel model;
	private TBSView view;
	
	private ArrayList<Integer> selectedIndices;
    private boolean mousePressed;
    private boolean mouseDragged;
    private boolean draggingElement;
    private int previousX, previousY;
    
    public TBSController(TBSModel m, TBSView v){
    	model = m;
    	view = v;
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
				ModelElement me = model.getElement(i);
				if(me instanceof Node) {
					// Move Node
					Node node = (Node) me;
					if(node instanceof OrganismNode) {
						OrganismNode on = (OrganismNode) node;
						on.addToTree();
						node.moveTo(x, y);
					}
					// Refresh Node
					model.setElement(i, node);
				}
			}
		}
		view.refreshGraphics();
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
	    int numElements = model.numElements();
	    for (int i = 0; i < numElements; i++) {
		    ModelElement var = model.getElement(i);
		    if(var.contains(x, y)) {
		    	activeIndices.add(i);
		    }
		}
		return activeIndices;
	}		    
	
}
