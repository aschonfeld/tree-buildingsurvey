package tbs.model.history;

import java.util.LinkedList;
import java.util.List;

import tbs.model.Connection;
import tbs.model.Node;
import tbs.model.TBSModel;

public class Unlink extends Command{

	private List<Connection> connections;
	
	private Node node;
	
	public Unlink(){
		this.connections = new LinkedList<Connection>();
	}
	
	public Unlink(Connection c){
		this();
		this.connections.add(c);
	}
	
	public Unlink(Node node){
		this();
		this.node = node;
	}
	
	
	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(List<Connection> connections) {
		this.connections = connections;
	}
	
	public void addConnection(Connection c){
		connections.add(c);
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	@Override
	public void execute(TBSModel model) {
		if(node != null){
			model.getElements().removeAll(connections);
			node.unlink();
		}else{
			Connection c = connections.get(0);
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			model.getElements().remove(c);
		}
	}

	@Override
	public void undo(TBSModel model) {
		System.out.println("Undoing unlink command.");
		int index;
		if(node != null){
			model.getElements().addAll(connections);
			index = model.findIndexByElement(node);
			if(index >= 0)
				model.setElement(index, node);
		}else{
			Connection c = connections.get(0);
			model.addElement(c);
			index = model.findIndexByElement(c.getFrom());
			((Node) model.getElement(index)).addConnectionTo(c.getTo());
			index = model.findIndexByElement(c.getTo());
			((Node) model.getElement(index)).addConnectionFrom(c.getFrom());
		}
	}

}
