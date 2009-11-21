package tbs.model.history;

import tbs.model.Node;
import tbs.model.TBSModel;

public class Drag extends Action{

	private Node node;
 	
	public Drag(Node node){
		this.node = node;
	}
	
	public Node getNode(){return node;}

	@Override
	public void execute(TBSModel model) {
		int selection = model.findIndexByElement(node);
		if(selection >= 0)
			((Node) model.getElements().get(selection)).setAnchorPoint(node.getAnchorPoint());		
	}
	
	
}
