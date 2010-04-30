package tbs.model.history;

import java.util.LinkedList;
import java.util.List;

import tbs.model.Connection;
import tbs.model.ModelElement;
import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.StudentModel;

public class Unlink extends Command {

	private List<Connection> connections;

	public Unlink(ModelElement element, StudentModel model) {
		this.connections = new LinkedList<Connection>();
		if (element instanceof Node)
			connections.addAll(ModelUtils.getConnectionsByNode((Node) element,
					model));
		else {
			Connection c = (Connection) element;
			connections.addAll(ModelUtils.getConnectionsByNodes(c.getTo(), c
					.getFrom(), model));
		}
	}

	public void addConnections(List<Connection> connections) {
		for (Connection c : connections) {
			try {
				this.connections.add((Connection) c.clone());
			} catch (CloneNotSupportedException e) {
				System.out.println("Unable to create connection clone.");
			}
		}
	}

	public void execute(StudentModel model) {
		for (Connection c : connections) {
			c.getFrom().getConnectedTo().remove(c.getTo());
			c.getTo().getConnectedFrom().remove(c.getFrom());
			model.getElements().remove(c);
		}
	}

	public void undo(StudentModel model) {
		System.out.println("Undoing unlink command.");
		for (Connection c : connections) {
			int id;
			id = c.getFrom().getId();
			Node from = (Node) model.getElement(model.findIndexById(id));
			id = c.getTo().getId();
			Node to = (Node) model.getElement(model.findIndexById(id));
			ModelUtils.addConnection(from, to, c.getId(), model, true);
		}
	}

	public String toString() {
		return "Unlinking Connections";
	}

}
