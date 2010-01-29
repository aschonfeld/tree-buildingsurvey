package tbs.model.history;

import tbs.model.Node;
import tbs.model.StudentModel;

public class Add extends Command{

	private Node node;
 	
	public Add(Node node){
		this.node = node;
	}

	public void execute(StudentModel model){
		model.addToTree(node);	
	}
	
	public void undo(StudentModel model) {
		System.out.println("Undoing add command.");
		int index = model.findIndexByElement(node);
		if(index >= 0)
			model.removeFromTree(model.getElement(index));		
	}

	public String toString() {
		return "Add";
	}
	
	
}
