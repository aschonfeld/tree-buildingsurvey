package tbs.model.history;

import java.util.LinkedList;
import java.util.List;

import tbs.model.Connection;
import tbs.model.Node;
import tbs.model.StudentModel;

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

	public void execute(StudentModel model) {
		if(node != null){
			for(Connection c : connections){
				c.getFrom().getConnectedTo().remove(c.getTo());
				c.getTo().getConnectedFrom().remove(c.getFrom());
				model.getElements().remove(c);
			} 
		}else{
			Connection c = connections.get(0);
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			model.getElements().remove(c);
		}
	}

	public void undo(StudentModel model) {
		System.out.println("Undoing unlink command.");
		int index;
		if(node != null){
			model.getElements().addAll(connections);
			index = model.findIndexByElement(node);
			if(index >= 0)
				model.setElement(index, node);
		}else{
			for(Connection c : connections){
				model.addElement(c);
				index = model.findIndexByElement(c.getFrom());
				((Node) model.getElement(index)).addConnectionTo(c.getTo());
				index = model.findIndexByElement(c.getTo());
				((Node) model.getElement(index)).addConnectionFrom(c.getFrom());
			}
		}
	}

	public String toString() {
		if(node != null)
			return "Node Unlink";
		else
			return "Connection Unlink";
	}

}
