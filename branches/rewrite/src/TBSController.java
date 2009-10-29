//TBSController v0.01

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.TreeMap;
import java.util.Map.Entry;

public class TBSController 
		implements MouseListener, MouseMotionListener, ActionListener 
	{
	
		private String selectedElement;
		private Boolean draggingElement;
		private TBSModel tbsModel;
		private int pressedX, pressedY, draggedX, draggedY;
   
		private Node node;
 
    	public TBSController(TBSModel tbsModel){
    	this.tbsModel = tbsModel;
    	draggingElement = false;
    }
    
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	
	public void mousePressed(MouseEvent e){
		pressedX = e.getX();
		pressedY = e.getY();
		for(Entry<String, ModelElement> entry : tbsModel.getElements().entrySet()){
			node = (Node)entry.getValue();
			if(node.contains(pressedX,pressedY))
			{
				selectedElement = entry.getKey();
				draggingElement = true;
			}
		}
	}
	
	public void mouseDragged(MouseEvent e){
		if ( draggingElement ) {
			// get the latest mouse position
			draggedX = e.getX();
			draggedY = e.getY();
			
			// displace the box by the distance the mouse moved since the last event
			//TBSModel.getElements().get(selectedElement).setLeftX(draggedX - pressedX);
			//TBSModel.getElements().get(selectedElement).setLeftX(draggedY - pressedY);
			
			// update our data
			pressedX = draggedX;
			pressedY = draggedY;
			
			//Don't know how this will be called, it may be called from the TBSView
			//since this class is instantiated there
			//TBSView.refreshGraphics();
			e.consume();
		}
	}
	
	public void mouseReleased(MouseEvent e) {
		draggingElement = false;
		//TODO: Update logic for if an element has been dragged to an
		//area that already this is already occupied		
		e.consume();
	}
	
	public void actionPerformed(ActionEvent e) {
        //System.out.println(e.getActionCommand()); 
    }
	
}
