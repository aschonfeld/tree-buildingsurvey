package tbs.controller;
//TBSController v0.3

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;
import tbs.view.TBSView;

public class TBSController implements MouseListener, MouseMotionListener, ActionListener {
	
	private TBSModel model;
	private TBSView view;
	
	private ArrayList<Integer> selectedIndices;
	private int previousX, previousY;
	private Node draggedNode;   

 
    public TBSController(TBSModel m, TBSView v){
    	model = m;
    	view = v;
    }
    
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	// Check for double click
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		String message = new String();
		String label = new String();
		if(e.getClickCount() == 2) {
			if(mouseIsOver(x, y).size() == 0) {
				// user clicked on empty space, create empty node
				message = new String("Do you want to label this node?");
				int userSelection = view.promptUserForYesNoCancel(message);
				switch (userSelection) {
				case JOptionPane.YES_OPTION:
					label = view.promptUserForString("Please enter a label for this node");
					if ((label == null) || !(label instanceof String)) label = " ";
					model.addElement(new EmptyNode(model, x, y, label ));
					break;
				case JOptionPane.NO_OPTION:
					model.addElement(new EmptyNode(model, x, y, "") );
					break;
				case JOptionPane.CANCEL_OPTION:
					// do nothing
				}
			} else {
				// user clicked on node, ask if wants to delete
				// remove top most node (in case nodes are stacked)
				ArrayList<Integer> a = mouseIsOver(x,y);
				int topIndex = a.get(a.size() - 1);
				ModelElement me = model.getElement(topIndex);
				if(me instanceof Node) {
					Node n = (Node) me;
					message = "Delete this node?";
					if(view.promptUserForYesNoCancel("Delete this node?") == JOptionPane.YES_OPTION) {
						n.removeFromTree();
					}					
				}
			}
		}
	}
	
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
					draggedNode = node;
					node.move(deltaX, deltaY);
					model.setElement(i, node);
				}
			}
		}
		view.refreshGraphics();
		// update our data
		previousX = x;
		previousY = y;
		
 	}
	
	
	public void mouseReleased(MouseEvent e) 
	{
		//Auto-add/delete: 
		if (draggedNode != null)
		{
			if (draggedNode.getLeftX() < TBSView.LINE_OF_DEATH )
				draggedNode.removeFromTree();
			if (draggedNode.getLeftX() > TBSView.LINE_OF_DEATH )
				draggedNode.addToTree(); 
				//is it more efficient to check isInTree in this
				//case or not to check? It shouldn't affect
				//performance, but it's an interesting question.
			draggedNode=null;
		}
		selectedIndices = new ArrayList<Integer>(); // clear selected items
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
