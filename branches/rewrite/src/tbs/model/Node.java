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
	protected boolean selected;
	private Connection conn;

	//connections to and from other ModelElements, respectively
	protected ArrayList<Node> fromConnections = new ArrayList<Node>();
	ArrayList<Connection> toConnections = new ArrayList<Connection>();

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

//-------------------------------------------------
// ---- Connection handling from here to end ------
//-------------------------------------------------

	public boolean connectedTo(Node n)
	{
		ListIterator<Connection> li = toConnections.listIterator();
		while (li.hasNext())
		{	if (li.next().getToNode()==n)
			{	return true;}
		}
		return false;
	}

	public Connection  getConn(Node n)
	{
		Connection c;
		ListIterator<Connection> li = toConnections.listIterator();
		while (li.hasNext())
		{

			c = li.next();
			if (c.getToNode()==n)
			{	
				return c;
			}
		}
		return null;
	}

	/**
	* Establish a directional link between this object and another.
	*/
	public void addConnection(Node n)
	{

		if (!this.isInTree() || !n.isInTree())
		{
			System.out.println("AddConnection error: not in tree");
			return;
		}

		if (n == this)
		{
			System.out.println("addconn error: tried to connect node to self");
			return;
		}

		if (this.connectedTo(n))
		{
			System.out.println("AddConnection error: already connected");
			return;
		}

		if (n.connectedTo(this))
		{
			System.out.println("AddConn warning: reverse connection exists.");
			System.out.println("Proceeding with connection.");
		}

		conn= new Connection(n, this);
		toConnections.add(conn);


		//here will go the fromConnection language:
		//fromConnections.add(n);		
		
		System.out.println("AddConnection: connected" + getName() + " to " +
				n.getName());
	}		


	

	/**
	* Add n to this Node's list of objects connecting to it.
	*/
	public void connectFrom(Node n)
	{
/*
		if (!isInTree()) 
		{	
			System.out.println("Not in tree");
			return;  	//can't connect, not in tree
		}
		
		if (fromConnections.indexOf(n) > 0)
		{	
			System.out.println("connectFrom error: already connected");
			 return; //already connected
		}
		if (n == this)
		{
			System.out.println("Tried to connect node from self");
			return;
		}
		fromConnections.add(n); 		 

		System.out.println("Connected to " +getName()+ " from " +
				n.getName());
*/	}	

	/**
	* Delete this Node's connection to the specified object. 
	* Takes either Connection or Node as argument.
	*/
	public void removeConnection(Connection c)
	{
		if (c==null)
		{
			System.out.println("removeConnection: no such connection");
			return;
		}

		toConnections.remove(c);

		//n.disconnectFrom(this);
		System.out.println("Removed connection");
	}


	public void removeConnection(Node n)
	{
		conn = getConn(n);
		removeConnection(conn);
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
	/*	Node n;
		ListIterator<ModelElement> li =
			model.getElements().ListIterator();
		while (li.hasNext())
		{
			n = (Node)li.next();
			if (this.connectedTo(n))
				removeConnection(n);
			if (n.connectedTo(this))
				removeConnection(this);
		}
*/
	/*
		System.out.println("Called unlink on "+ getName());
		toConnections.clear();
		ListIterator<Node> li = fromConnections.listIterator();
		while (li.hasNext())
		{
			li.next().removeConnection(this);;
		}
		fromConnections.clear();
	*/
	}		

	public boolean isConnected()
	{
		return !toConnections.isEmpty();
	}
	
	
	public ArrayList<Connection> getConnections() 
	{
		return toConnections;
	}
		
	public ArrayList<Node> getFromConnections()
	{
		return fromConnections;
	}
}	
