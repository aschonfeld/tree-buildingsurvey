package tbs.model.history;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.StudentModel;
import tbs.view.TBSButtonType;

public class Delete extends Command{

	private ModelElement modelElement;
	
	private List<Connection> elementConnections;
 	
	public Delete(ModelElement element, StudentModel model) throws CloneNotSupportedException{
		elementConnections = new LinkedList<Connection>();
		if(element instanceof Node){
			if(element instanceof EmptyNode){
				if(!((Node) element).isInTree()){
					model.removeActionFromHistory();
					System.out.println("Invalid drag move removed from history.");
				}
			}
			this.modelElement = (Node)((Node)element).clone();
			((Node)modelElement).setBeingDragged(false);
			if(!model.getHistory().isEmpty() && model.getHistory().peek() instanceof Drag){
				((Node)modelElement).setAnchorPoint(((Drag) model.getHistory().peek()).getPointBefore());
				model.removeActionFromHistory();
				System.out.println("Invalid drag move removed from history.");            
			}
			elementConnections = ModelUtils.getConnectionsByNode((Node) element, model);
		}else
			elementConnections = ModelUtils.getConnectionsByNodes(((Connection)element).getTo(), ((Connection)element).getFrom(), model);
	}
	
	public void execute(StudentModel model){
		ModelUtils.removeElement(modelElement, model, true);
	}
	
	public void undo(StudentModel model) {
		if (modelElement != null){
			System.out.println("Undoing " + modelElement.getClass().getSimpleName() + " delete command.");
			if(modelElement instanceof OrganismNode){
				int id = model.findIndexById(modelElement.getId());
				model.getElements().set(id, modelElement);
			}else{
				if(modelElement.getId() < model.getElements().size())
					model.getElements().add(modelElement.getId(), modelElement);
				else{
					model.getElements().add(modelElement);
					model.getButtonStates().put(TBSButtonType.LABEL, true);
					Collections.sort(model.getElements(), TBSGraphics.elementIdComparator);
				}
				model.setEmptyNodesInTree(true);
				model.getButtonStates().put(TBSButtonType.DELETE, true);
				model.getButtonStates().put(TBSButtonType.CLEAR, true);
				if(model.inTreeElements().size() > 1)
					model.getButtonStates().put(TBSButtonType.LINK, true);
			}
			model.setElementsInTree(true);
		}
		if(!elementConnections.isEmpty()){
			for(Connection c : elementConnections){
				int id;
				id = c.getFrom() == null ? modelElement.getId() : c.getFrom().getId();
				Node from = (Node) model.getElement(model.findIndexById(id));
				id = c.getTo() == null ? modelElement.getId() : c.getTo().getId();
				Node to = (Node) model.getElement(model.findIndexById(id));
				ModelUtils.addConnection(from,to,c.getId(), model, true);
			}
			model.setConnectionsInTree(true);
			model.getButtonStates().put(TBSButtonType.LINK, true);
			model.getButtonStates().put(TBSButtonType.UNLINK, true);
		}
		model.getButtonStates().put(TBSButtonType.DELETE, true);
		model.getButtonStates().put(TBSButtonType.CLEAR, true);
	}

	public String toString() {
		if(modelElement != null){
			if(modelElement instanceof EmptyNode)
				return "Empty Node Delete";
			else
				return "Organism Node Delete";
		}else if(!elementConnections.isEmpty())
			return "Connection Delete";
		return "";
	}
	
	
	
	
}
