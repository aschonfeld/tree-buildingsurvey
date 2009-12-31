package tbs.model.history;

import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.TBSModel;

public class Label extends Command{

	private Integer nodeId;
	
	private String labelBefore;
	
	private String labelAfter;
 	
	public Label(Integer nodeId, String labelBefore){
		this.nodeId = nodeId;
		this.labelBefore = labelBefore;
	}

	public String getLabelAfter() {
		return labelAfter;
	}

	public void setLabelAfter(String labelAfter) {
		this.labelAfter = labelAfter;
	}

	public String getLabelBefore() {
		return labelBefore;
	}

	@Override
	public void execute(TBSModel model){
		int index = model.findIndexById(nodeId);
		if(index >= 0){
			ModelElement selection = model.getElement(index);
			if(selection instanceof EmptyNode)
				((EmptyNode) selection).rename(labelAfter);
		}
	}
	
	@Override
	public void undo(TBSModel model) {
		System.out.println("Undoing label command.");
		int index = model.findIndexById(nodeId);
		if(index >= 0){
			ModelElement selection = model.getElement(index);
			if(selection instanceof EmptyNode)
				((EmptyNode) selection).rename(labelBefore);	
		}
	}

	@Override
	public String toString() {
		return "Label";
	}
	
	
}
