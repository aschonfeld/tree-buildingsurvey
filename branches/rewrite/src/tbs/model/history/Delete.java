package tbs.model.history;

import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.TBSModel;

public class Delete extends Command{

	private ModelElement modelElement;
 	
	public Delete(ModelElement modelElement){
		this.modelElement = modelElement;
	}

	@Override
	public void execute(TBSModel model){
		model.removeFromTree(modelElement);
	}
	
	@Override
	public void undo(TBSModel model) {
		if(modelElement instanceof Connection){
			model.addElement(modelElement);
			Connection c = (Connection) modelElement;
			model.setElement(model.findIndexByElement(c.getFrom()), c.getFrom());
			model.setElement(model.findIndexByElement(c.getTo()), c.getTo());
		}else if(modelElement instanceof EmptyNode){
			model.addElement(modelElement);
		}else
			model.setElement(modelElement.getId(), modelElement);
	}
	
	
}
