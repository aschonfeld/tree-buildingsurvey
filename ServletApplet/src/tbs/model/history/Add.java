package tbs.model.history;

import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.StudentModel;

public class Add extends Command{

	private Node node;
 	
	public Add(Node node, StudentModel model){
		this.node = node;
		if(!model.getHistory().isEmpty() && model.getHistory().peek() instanceof Drag)
			model.removeActionFromHistory();
	}

	public void execute(StudentModel model){
		ModelUtils.addNode(node, model, false);	
	}
	
	public void undo(StudentModel model) {
		System.out.println("Undoing add command.");
		int index = model.findIndexByElement(node);
		if(index >= 0)
			ModelUtils.removeElement(model.getElement(index), model, true);
	}

	public String toString() {
		return "Add";
	}
	
	
}
