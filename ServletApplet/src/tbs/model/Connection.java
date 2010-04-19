//TBS version 0.4
//represents a connection between two node objects
//implemented to make connections selectable

package tbs.model;

import java.awt.Point;

import tbs.TBSUtils;

public class Connection extends ModelElement implements Cloneable
{

	private Node to;
	private Node from;
	
	/**
	* Connection registers a connection between two nodes
	*/
	public Connection(int id, Node from, Node to)
	{
		super(id);
		this.from=from;
		this.to=to;
		from.addConnectionTo(to);
		to.addConnectionFrom(from);
	}
	
	/**
	* Returns the target node of this connection
	*/
	public Node getTo(){ return to; }
	
	public void setTo(Node to){ this.to=to; }
		
	/**
	* Returns the origin node of this connection
	*/
	public Node getFrom(){ return from; }

	/**
	* Sets source node of this connection
	*/	
	public void setFrom(Node from){ this.from=from; }
	
	/**
	* Returns true if this connection involves the specified node.
	*/
	public boolean hasNode(Node n){
		return to.equals(n) || from.equals(n);
	}

	/**
	* Returns true for a point on the line, as determined algebraically
	*/
	public boolean contains(int x, int y)
	{
		double dist = Math.abs(TBSUtils.getConnectionBounds(from, to).ptSegDist(new Point(x, y)));
		return dist < 1.5;

	}

	public boolean collidesWith(ModelElement e) {return false;}
	
	public Object clone() throws CloneNotSupportedException {
		Connection copy = (Connection) super.clone();
		copy.setTo((Node) to.clone());
		copy.setFrom((Node) from.clone());
		return copy;
	}

	public StringBuffer dump() {
		return new StringBuffer("C:")
			.append(this.getId()).append(":")
			.append(this.from.getId()).append(":")
			.append(this.to.getId());
	}
	
	/*
	 * This is a default method that is used by setScreenString in the 
	 * Controller & also some logging to get information about a
	 * ModelElement object.  Even though the element is a ModelElement
	 * when toString is called it will refer to the resulting subclass,
	 * in this case Connection. 
	 */
	public String toString(){
		return "Connection from " + getFrom().getName() + " to " + getTo().getName();
	}

	public Boolean isInTree() {return true;}
	public void setInTree( Boolean inTree ) {}
}
