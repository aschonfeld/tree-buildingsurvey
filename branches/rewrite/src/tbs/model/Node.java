package tbs.model;

import java.util.*;

//TBS version 0.3
//Node: superclass for OrganismNode and EmptyNode


	
public abstract class Node extends ModelElement 
{	
	String name;
	int leftX;
	int upperY;
	int width;
	int height;
	boolean inTree;
	ArrayList<Connection> toConnections = new ArrayList<Connection>();
	protected boolean selected;
	private Connection conn;

	//connections to and from other ModelElements, respectively
	protected ArrayList<Node> fromConnections = new ArrayList<Node>();

	public int getLeftX() {return leftX;}
	public void setLeftX(int leftX){this.leftX = leftX;}
	public int getUpperY() {return upperY;}
	public void setUpperY(int upperY){this.upperY = upperY;}
	public int getWidth() {return width;}
	public int getHeight() 	{return height;}
	public String getName() {return name;}

	
	
	public boolean contains(int x, int y) {
		if((x > leftX) && (x < (leftX + width))) {
			if((y > upperY) && (y < (upperY + height))) {
				return true;
			}
		}
		return false;
	}
	
	public void move(int deltaX, int deltaY) {
		leftX += deltaX;
		upperY += deltaY;
	}		
		
	public void moveTo(int x, int y) {
		leftX = x;
		upperY = y;
	}	
	
	public abstract void removeFromTree();

	public abstract void addToTree();
		
	public boolean isInTree()
	{ 
		return inTree;
	}
	
	public boolean connectedTo(Node n)
	{
		ListIterator<Connection> li = toConnections.listIterator();
		while (li.hasNext())
			if (li.next().getToNode()==n)
				return true;
		return false;
	}

	/**
	* Establish a directional link between this object and another.
	*/
	public void addConnection(Node n)
	{
		if (!isInTree())
		{
			System.out.println("Returning from addConnection"); 
			return;  	//can't connect, not in tree
		}
		if (connectedTo(n))
		{
			System.out.println("error in addConnection, already connected");
			 return; //already connected
		}
		if (n == this)
		{
			System.out.println("Tried to connect node to self");
			return;
		}
		toConnections.add(new Connection(n,this)); 
		n.connectFrom(this);
		System.out.println("Connected " +getName()+ " to " +
				n.getName());
	}	

	/**
	* Add n to this Node's list of objects connecting to it.
	*/
	public void connectFrom(Node n)
	{
		if (!inTree) return;  	//can't connect, not in tree
		if (fromConnections.indexOf(n) < 0) return; //already connected
		if (n == this)
		{
			System.out.println("Tried to connect node from self");
			return;
		}
		fromConnections.add(n); 		 

		System.out.println("Connected to " +getName()+ " from " +
				n.getName());
	}	

	/**
	* Unlink this Node's connection to the specified object. 
	*/
	public void removeConnection(Node n)
	{

		
		if (!connectedTo(n))		
		{
			System.out.println("--Bad call to Node.disconnectTo!--");
			return; 
		}
	
		ListIterator<Connection> li = toConnections.listIterator();
		while (li.hasNext())
		{
			conn = li.next();
			if (conn.getToNode()==n)
			{	toConnections.remove(conn);
			}
		}
			

		n.disconnectFrom(this);
		System.out.println("Removed "+ n.getName() +" from " +getName()+
			" toConnections list.");
	}


	/**
	* Remove n from list of Nodes connecting to this Node
	*/
	public void disconnectFrom(Node n)
	{
		if (fromConnections.indexOf(n) < 0) //no connection to undo
		{
			
			System.out.println("--Bad call to Node.disconnectFrom!--");
			return; 
		}
		fromConnections.remove(n);
		System.out.println("Removed "+ n.getName() +" from " +getName()+
			" toConnections list.");
	}	

	/**
	* Remove all of this Node's connections; in preparation, perhaps,
	* for deleting it.
	*/
	public void unlink()
	{
		toConnections.clear();
		fromConnections.clear();
	}		

	public boolean isConnected()
	{
		return !toConnections.isEmpty();
	}
	
	
	public ArrayList<Connection> getConnections() 
	{
		return toConnections;
	}
}	
