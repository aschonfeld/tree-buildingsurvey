package tbs.controller;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.TBSModel;
import tbs.view.TBSView;

public class TBSController 
		implements MouseListener, MouseMotionListener, ActionListener 
{
	
	private TBSModel model;
	private TBSView view;

	private ArrayList<Integer> selectedIndices;
	private int previousX, previousY;
	private Node draggedNode = null;
	private Node selectedNode = null;
	
   public TBSController(TBSModel m, TBSView v)
	{
    	model = m;
    	view = v;
		draggedNode=null;
		selectedNode=null;
    }
    
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		if(selectedNode != null) {
			Node dummyNode = (Node) new OrganismNode(model, null, null, e.getX(), e.getY(), 3, 3);
			Point[] conn = TBSUtils.computeConnectionBounds(selectedNode , dummyNode);
			view.setConnInProgress(conn);
		}
	}
	
	// Check for double click
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		//if mouse is in button space, call button actions
		if(y < TBSGraphics.buttonsHeight) 
		{
			int buttonIndex = x / TBSGraphics.buttonsWidth;
			if(buttonIndex < TBSGraphics.buttons.size()) 
			{
				System.out.println(TBSGraphics.buttons.get(buttonIndex));
			}
		} else {
			if(mouseIsOver(x, y).size() != 0) {
				ArrayList<Integer> a = mouseIsOver(x,y);
				int topIndex = a.get(a.size() - 1);
				ModelElement me = model.getElement(topIndex);
				if(me instanceof Node) {
					if((selectedNode) == null) {
						Node n = (Node) me;
						// don't allow connection from node not in tree
						if(n.isInTree()) selectedNode = n;
					} else {
						// fromNode already selected, make connection
						selectedNode.addConnection((Node) me);
						selectedNode = null;
						view.setConnInProgress(null);
					}
				}
			}
		}	
	
		if(e.getClickCount() == 2) {
			// no double clicking for now
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
		for(Integer index : selectedIndices) {
			int i = index.intValue();
			ModelElement selected = model.getElement(i);
			if(selected instanceof Node) {
				// Move Node
				Node node = (Node) selected;
				draggedNode = node;
				node.move(deltaX, deltaY);
				model.setElement(i, node);
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
			//Node dragged to point out of bounds
			modifyOutOfBounds(draggedNode);
			for(ModelElement me : model.getElements())
			{
				if(me instanceof Node) 
				{
					Node curr = (Node) me;
					if(!curr.equals(draggedNode) && curr.isInTree())
					{
						if(draggedNode.collidesWith(curr))
							draggedNode.removeFromTree();
					}
				}
			}
			if (draggedNode.getLeftX() < TBSGraphics.LINE_OF_DEATH )
				draggedNode.removeFromTree();
			if (draggedNode.getLeftX() > TBSGraphics.LINE_OF_DEATH )
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
	    int i = 0;
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y)) 
		    	activeIndices.add(i);
		    i++;
		}
		return activeIndices;
	}	
    
    private void modifyOutOfBounds(Node n){
    	if(n.getLeftX() < 0 )
    		n.setLeftX(0);
    	if((n.getLeftX()+n.getWidth()) > view.getWidth())
    		n.setLeftX(view.getWidth()-n.getWidth());
    	if(n.getUpperY() < 0)
    		n.setUpperY(0);
    	if((n.getUpperY()+n.getHeight()) > view.getHeight())
    		n.setUpperY(view.getHeight()-n.getHeight());
    }
	
}
