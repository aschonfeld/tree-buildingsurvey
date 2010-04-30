package tbs.model.history;

import java.awt.Point;

import tbs.model.Node;
import tbs.model.StudentModel;

public class Drag extends Command {

	private Integer nodeId;

	private Point pointBefore;

	private Point pointAfter;

	public Drag(Integer nodeId, Point pointBefore) {
		this.nodeId = nodeId;
		this.pointBefore = pointBefore;
	}

	public Point getPointBefore() {
		return pointBefore;
	}

	public Point getPointAfter() {
		return pointAfter;
	}

	public void setPointAfter(Point pointAfter) {
		this.pointAfter = pointAfter;
	}

	public void execute(StudentModel model) {
		int index = model.findIndexById(nodeId);
		if (index >= 0)
			((Node) model.getElement(index)).setAnchorPoint(pointAfter);
	}

	public void undo(StudentModel model) {
		System.out.println("Undoing drag command.");
		int index = model.findIndexById(nodeId);
		if (index >= 0)
			((Node) model.getElement(index)).setAnchorPoint(pointBefore);
	}

	public String toString() {
		return "Drag";
	}

}
