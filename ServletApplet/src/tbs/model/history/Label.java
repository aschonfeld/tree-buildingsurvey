package tbs.model.history;

import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.StudentModel;

public class Label extends Command{

	private Integer nodeId;
	
	private String labelBefore;
	private int widthBefore;
	
	private String labelAfter;
	private int widthAfter;
 	
	public Label(Integer nodeId, String labelBefore, int widthBefore){
		this.nodeId = nodeId;
		this.labelBefore = labelBefore;
		this.widthBefore = widthBefore;
	}

	public String getLabelAfter() {
		return labelAfter;
	}

	public void setLabelAfter(String labelAfter, int widthAfter) {
		this.labelAfter = labelAfter;
		this.widthAfter = widthAfter;
	}

	public String getLabelBefore() {
		return labelBefore;
	}

	public void execute(StudentModel model){
		int index = model.findIndexById(nodeId);
		if(index >= 0){
			ModelElement selection = model.getElement(index);
			if(selection instanceof EmptyNode){
				((EmptyNode) selection).setName(labelAfter);
				((EmptyNode) selection).setAlteredWidth(widthAfter);
			}
		}
	}
	
	public void undo(StudentModel model) {
		System.out.println("Undoing label command.");
		int index = model.findIndexById(nodeId);
		if(index >= 0){
			ModelElement selection = model.getElement(index);
			if(selection instanceof EmptyNode){
				((EmptyNode) selection).setName(labelBefore);
				((EmptyNode) selection).setAlteredWidth(widthBefore);
			}
		}
	}

	public String toString() {
		return "Label";
	}
	
	
}
