package tbs.model.history;

import tbs.model.Node;
import tbs.model.TBSModel;

public class Add extends Action{

	private Node node;
 	
	public Add(Node node){
		this.node = node;
	}

	@Override
	public void execute(TBSModel model) {
		int selection = model.findIndexByElement(node);
		if(selection >= 0)
			model.removeFromTree(model.getElements().get(selection));		
	}
	
	
}
