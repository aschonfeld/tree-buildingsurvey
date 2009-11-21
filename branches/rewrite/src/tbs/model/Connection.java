//TBS version 0.4
//represents a connection between two node objects
//implemented to make connections selectable

package tbs.model;

import java.awt.Point;

import tbs.TBSUtils;

public class Connection extends ModelElement
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
	}
	
	/**
	* Returns the target node of this connection
	*/
	public Node getTo()
	{
		return to;
	}
		
	/**
	* Returns the origin node of this connection
	*/
	public Node getFrom()
	{
		return from;
	}
	
	public boolean hasNode(Node n){
		return to.equals(n) || from.equals(n);
	}

	/**
	* Returns true for a point on the line, as determined algebraically
	*/
	@Override
	public boolean contains(int x, int y)
	{
		double dist = Math.abs(TBSUtils.getConnectionBounds(from, to).ptSegDist(new Point(x, y)));
		return dist < 1.5;

	}

	@Override
	public boolean collidesWith(ModelElement e) {return false;}
}
