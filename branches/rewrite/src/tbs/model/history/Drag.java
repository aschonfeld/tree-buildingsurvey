package tbs.model.history;

import java.awt.Point;

import tbs.model.Node;
import tbs.model.TBSModel;

public class Drag extends Command{
	
	private Integer nodeId;
	
	private Point pointBefore;
 	
	private Point pointAfter;
	
	public Drag(Integer nodeId, Point pointBefore){
		this.nodeId = nodeId;
		this.pointBefore = pointBefore;
	}
	
	public Point getPointBefore(){return pointBefore;}

	public Point getPointAfter() {return pointAfter;}

	public void setPointAfter(Point pointAfter) {this.pointAfter = pointAfter;}

	@Override
	public void execute(TBSModel model){
		int index = model.findIndexById(nodeId);
		if(index >= 0)
			((Node) model.getElement(index)).setAnchorPoint(pointAfter);
	}
	
	@Override
	public void undo(TBSModel model) {
		int index = model.findIndexById(nodeId);
		if(index >= 0)
			((Node) model.getElement(index)).setAnchorPoint(pointBefore);		
	}
	
	
}
