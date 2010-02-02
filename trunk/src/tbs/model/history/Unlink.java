package tbs.model.history;

import java.util.LinkedList;
import java.util.List;

import tbs.model.Connection;
import tbs.model.Node;
import tbs.model.StudentModel;

public class Unlink extends Command{

	private List<Connection> connections;
	
	public Unlink(){
		this.connections = new LinkedList<Connection>();
	}
	
	public Unlink(Connection c){
		this();
		this.connections.add(c);
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
  
	public void addConnections(List<Connection> connections){
	  for(Connection c : connections){
	    try{
	      connections.add((Connection) c.clone());
	    }catch(CloneNotSupportedException e){
	      System.out.println("Unable to create connection clone.");
	    }
	  }
	}

	public void execute(StudentModel model) {
		for(Connection c : connections){
				c.getFrom().getConnectedTo().remove(c.getTo());
				c.getTo().getConnectedFrom().remove(c.getFrom());
				model.getElements().remove(c); 
		}
	}

	public void undo(StudentModel model) {
		System.out.println("Undoing unlink command.");
		for(Connection c : connections){
				int id;
				id = c.getFrom().getId();
				Node from = (Node) model.getElement(model.findIndexById(id));
				id = c.getTo().getId();
				Node to = (Node) model.getElement(model.findIndexById(id));
				model.addConnection(from,to,c.getId());
		}
	}

	public String toString() {
		return "Unlinking Connections";
	}

}
