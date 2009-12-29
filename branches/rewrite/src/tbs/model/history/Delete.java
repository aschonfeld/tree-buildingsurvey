package tbs.model.history;

import java.util.LinkedList;
import java.util.List;

import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.TBSModel;

public class Delete extends Command{

	private ModelElement modelElement = null;
	
	private List<ModelElement> twoWayConnection = null;
	
	private List<Connection> elementConnections = null;
 	
	public Delete(){
		twoWayConnection = new LinkedList<ModelElement>();
		elementConnections = new LinkedList<Connection>();
	}
	
	public Delete(ModelElement modelElement){
		this.modelElement = modelElement;
	}
	
	public List<ModelElement> getTwoWayConnection() {
		return twoWayConnection;
	}

	public void addConnection(ModelElement conn){
		twoWayConnection.add(conn);
	}

	public List<Connection> getElementConnections() {
		return elementConnections;
	}

	public void setElementConnections(List<Connection> elementConnections) {
		this.elementConnections = elementConnections;
	}

	@Override
	public void execute(TBSModel model){
		model.removeFromTree(modelElement);
	}
	
	/*
	 * This method may require maintaining the integrity of connections to the
	 * node that has been deleted so that when the user clicks "Undo" the 
	 * connections reappear.  This will be decided by professor bolker/white.
	 */
	@Override
	public void undo(TBSModel model) {
		if(twoWayConnection != null){
			System.out.println("Undoing two-way connection delete command.");
			for(ModelElement m : twoWayConnection){
				model.addElement(m);
				Connection c = (Connection) m;
				model.setElement(model.findIndexByElement(c.getFrom()), c.getFrom());
				model.setElement(model.findIndexByElement(c.getTo()), c.getTo());
			}
		}else{
			if(modelElement instanceof Connection){
				System.out.println("Undoing connection delete command.");
				model.addElement(modelElement);
				Connection c = (Connection) modelElement;
				model.setElement(model.findIndexByElement(c.getFrom()), c.getFrom());
				model.setElement(model.findIndexByElement(c.getTo()), c.getTo());
			}else{
				if(modelElement instanceof EmptyNode){
					System.out.println("Undoing empty node delete command.");
					model.getElements().add(modelElement.getId(), modelElement);
				}else{
					System.out.println("Undoing organism node delete command.");
					model.setElement(modelElement.getId(), modelElement);
				}
				for(Connection c : elementConnections){
					int id;
					id = c.getFrom() == null ? modelElement.getId() : c.getFrom().getId();
					Node from = (Node) model.getElementBySN(id);
					id = c.getTo() == null ? modelElement.getId() : c.getTo().getId();
					Node to = (Node) model.getElementBySN(id);
					model.addConnection(from,to);
				}
			}
		}
	}
	
	
}
