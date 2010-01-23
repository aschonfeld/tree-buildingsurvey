package tbs.model.history;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.StudentModel;

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

	public void execute(StudentModel model){
		model.removeFromTree(modelElement);
	}
	
	public void undo(StudentModel model) {
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
				System.out.println("Undoing " + modelElement.getClass().getSimpleName() + " delete command.");
				if(modelElement.getId() < model.getElements().size())
					model.getElements().add(modelElement.getId(), modelElement);
				else{
					model.getElements().add(modelElement);
					Collections.sort(model.getElements(), TBSGraphics.elementIdComparator);
				}
				for(Connection c : elementConnections){
					int id;
					id = c.getFrom() == null ? modelElement.getId() : c.getFrom().getId();
					Node from = (Node) model.getElement(model.findIndexById(id));
					id = c.getTo() == null ? modelElement.getId() : c.getTo().getId();
					Node to = (Node) model.getElement(model.findIndexById(id));
					model.addConnection(from,to,c.getId());
				}
			}
		}
	}

	public String toString() {
		if(twoWayConnection != null)
			return "Two-way Connection Delete";
		else{
			if(modelElement instanceof Connection)
				return "Connection Delete";
			else if(modelElement instanceof EmptyNode)
				return "Empty Node Delete";
			else
				return "Organism Node Delete";
		}
	}
	
	
	
	
}
