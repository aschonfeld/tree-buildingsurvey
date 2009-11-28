package tbs.model.history;

import tbs.model.Node;
import tbs.model.TBSModel;

public class Add extends Command{

	private Node node;
 	
	public Add(Node node){
		this.node = node;
	}

	@Override
	public void execute(TBSModel model){
		model.addToTree(node);	
	}
	
	@Override
	public void undo(TBSModel model) {
		System.out.println("Undoing add command.");
		int index = model.findIndexByElement(node);
		if(index >= 0)
			model.removeFromTree(model.getElement(index));		
	}
	
	
}
