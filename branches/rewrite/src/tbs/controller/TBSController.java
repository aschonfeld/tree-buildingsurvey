//TBS version .04
//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;
import java.util.ArrayList;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.Connection;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;
import tbs.view.TBSView;

public class TBSController 
		implements MouseListener, MouseMotionListener, KeyListener
{
	
	private TBSModel model;
	private TBSView view;
	private int previousX, previousY, selectedIndex;
	private Node draggedNode = null;
	private Node selectedNode = null;
	private Point lastPosition = null;
	
	public TBSController(TBSModel m, TBSView v) {
    	model = m;
    	view = v;
		draggedNode=null;
		selectedNode=null;
    }
    
	public void keyPressed(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	
	public void mouseMoved(MouseEvent e){
		if(selectedNode != null) {
			Point[] conn = new Point[]{TBSUtils.getNodeCenter(selectedNode), new Point(e.getX(), e.getY())};
			view.setConnInProgress(conn);
		}
	}
	
	// Check for double click
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if(e.getClickCount() == 1) {
			if(y < TBSGraphics.buttonsHeight)  {
				int buttonIndex = x / TBSGraphics.buttonsWidth;
				if(buttonIndex < TBSGraphics.buttons.size()) {
					System.out.println(TBSGraphics.buttons.get(buttonIndex));
				}
			} else {
				creatingConnection(x, y);
			}
		}	
		if(e.getClickCount() == 1) {
			ModelElement me = elementMouseIsOver(x, y);
			if (me != null) {
				if(me instanceof Connection) {
					Connection c = (Connection) me;
					c.removeFromTree();
				}
			}
		}
	}
	
	public void mousePressed(MouseEvent e){
        int x = e.getX();
        int y = e.getY();
        selectedIndex = indexMouseIsOver(x, y);
        previousX = x;
        previousY = y;
	}
	
	public void mouseDragged(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int deltaX = x - previousX;
		int deltaY = y - previousY;
		cancelConnection();
		if(selectedIndex < 0) return;
		ModelElement selected = model.getElement(selectedIndex);
		if(selected instanceof Node) {
			// Move Node
			Node node = (Node) selected;
			if(lastPosition == null)
			   lastPosition = new Point(node.getLeftX(), node.getUpperY());
			draggedNode = node;
			node.move(deltaX, deltaY);
			model.setElement(selectedIndex, node);
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
			List<Node> inTreeElements = model.inTreeElements();
			for(Node n : inTreeElements){
				if(!n.equals(draggedNode) && draggedNode.collidesWith(n)){
					draggedNode.setLeftX(lastPosition.x);
					draggedNode.setUpperY(lastPosition.y);
					break;
				}
			}
			lastPosition = null;
			if (draggedNode.getLeftX() < TBSGraphics.LINE_OF_DEATH )
				draggedNode.removeFromTree();
			if (draggedNode.getLeftX() > TBSGraphics.LINE_OF_DEATH )
				draggedNode.addToTree(); 
				//is it more efficient to check isInTree in this
				//case or not to check? It shouldn't affect
				//performance, but it's an interesting question.
			draggedNode=null;
		}
		//selectedIndices = new ArrayList<Integer>(); // clear selected items
	}
	
    private int indexMouseIsOver(int x, int y) {
	    int maxIndex = -1;
	    int i = 0;
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y)) maxIndex = i;
		    i++;
		}
		return maxIndex;
	}
    
    private ModelElement elementMouseIsOver(int x, int y) {
	    ModelElement topElement = null;
	    for (ModelElement me : model.getElements()) {
		    if(me.contains(x, y)) topElement = me;
		}
		return topElement;
	}   
    
    
    private void modifyOutOfBounds(Node n){
    	if(n.getLeftX() < 0 )
    		n.setLeftX(0);
    	if((n.getLeftX()+n.getWidth()) > view.getWidth())
    		n.setLeftX(view.getWidth()-n.getWidth());
    	if(n.getUpperY() <= 0)
    		n.setUpperY(TBSGraphics.buttonsHeight + TBSGraphics.buttonsYPadding);
    	if((n.getUpperY()) > view.getHeight())
    		n.setUpperY(view.getHeight()-n.getHeight());
    }
    
    private void cancelConnection() {
		view.setConnInProgress(null);
		selectedNode = null;
    }
    
    // handles code for starting a connection and making a connection
	private void creatingConnection(int mouseX, int mouseY) {
		ModelElement me = elementMouseIsOver(mouseX, mouseY);
		if(me == null) {
			cancelConnection();
			return;
		}
		if (!(me instanceof Node)) {
			cancelConnection();
			return;
		}
		Node n = (Node) me;
		if (!n.isInTree()) {
			cancelConnection();
			return;
		}
		if(selectedNode == null) {
			// selected node is from node
			selectedNode = n;
			
		} else {
			// n is to node
			if(n != selectedNode) {
				selectedNode.addConnection(n);
				model.addElement(new Connection(model, selectedNode, n));
				cancelConnection();
			}
		}
	}
	
}
