//TBS version 0.4
//represents a connection between two node objects
//implemented to make connections selectable

package tbs.model;

public class Connection extends ModelElement
{

	private Node toNode;
	private Node fromNode;


	/**
	* Connection registers a connection between two nodes
	*/
	public Connection(Node to, Node from)
	{
		toNode=to;
		fromNode=from;
	}
	
	/**
	* Returns the target node of this connection
	*/
	public Node getToNode()
	{
		return toNode;
	}
		
	/**
	* Returns the origin node of this connection
	*/
	public Node getFromNode()
	{
		return fromNode;
	}

	/**
	* We might use this (in future) to resolve connection/node collisions
	*/
	public boolean collidesWith(ModelElement e)
	{
		return false;
	}

	/**
	* Returns true for a point on the line, as determined algebraically
	*/
	public boolean contains (int x, int y)
	{

		//Glenn: here's your stub
		return false;
	}
}
