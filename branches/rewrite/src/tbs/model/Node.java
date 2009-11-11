//TBS version 0.4
//Node: superclass for OrganismNode and EmptyNode

package tbs.model;

import java.util.ArrayList;
import java.util.ListIterator;
import tbs.TBSUtils;


/**
* This class is the superclass for OrganismNode and EmptyNode, and
* contains their common elements. 
*/
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

	
	/**
	* Returns true if the point indicated is within the object's
	* boundaries. 
	*/
	public boolean contains(int x, int y) {
		if(TBSUtils.isInRange(x,leftX,leftX + width) &&
			TBSUtils.isInRange(y, upperY, upperY + height))
				return true;
		return false;
	}
	
	/**
	* Adjusts the object's position by the indicated amount
	*/
	public void move(int deltaX, int deltaY) {
		leftX += deltaX;
		upperY += deltaY;
	}		
	
	/**
	* Sets the object's position to the indicated point. 
	*/
	public void moveTo(int x, int y) {
		leftX = x;
		upperY = y;
	}	
	
	/**
	* Asks this node to be gone, by the means appropriate to its type.
	*/
	public abstract void removeFromTree();


	/**
	* Called when dragging from the left-side column, this asks the
	* object to do what is needed to place itself "in the tree", that is,
	* ready to be connected to other objects.
	*/
	public abstract void addToTree();
		

	/**
	* Returns true if the node thinks it should accept connections and
	* selected status. 
	*/
	public boolean isInTree()
	{ 
		return inTree;
	}

//-------------------------------------------------
// ---- Connection handling from here to end ------
//-------------------------------------------------


	/**
	* Returns true if this node is connected by a forward connection
	* (toConnection) to the Node submitted as argument. 
	* Will return false if this node is connected to by n, and has a
	* fromConnection to n. 
	*/
	public boolean connectedTo(Node n)
	{
		ListIterator<Connection> li = toConnections.listIterator();
		while (li.hasNext())
		{	if (li.next().getToNode()==n)
			{	return true;}
		}
		return false;
	}


	/**
	* Returns the Connection between this node and Node n, or null if no
	* connection exists. 
	*/
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

		n.connectFrom(this);
		
		System.out.println("AddConnection: connected" + getName() + " to " +
				n.getName());

	} // end of addConnection


	

	/**
	* Add n to this Node's list of objects connecting to it.
	*/
	public void connectFrom(Node n)
	{
		
		if (fromConnections.contains(n))
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
	}	

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
		for (Node n : fromConnections)
		{
			n.removeConnection(this);
		}
		fromConnections.clear();
		
		for (Connection c: toConnections)
		{
			c.getToNode().disconnectFrom(this);    
		}
		toConnections.clear();

	}		


	/**
	* Returns true if this object has forward connections (toConnections)
	* to any objects in the model. 
	* Will return false if this object is connected to, but does not
	* connect to any objects (ie, is a terminal node) or if it is
	* completely isolated in the model.
	*/
	public boolean isConnected()
	{
		return !toConnections.isEmpty();
	}
	
	/**
	* Returns the ArrayList of Connections for this Node. 
	*/
	public ArrayList<Connection> getConnections() 
	{
		return toConnections;
	}
		

	/**
	* Returns the ArrayList of Nodes which connect to this Node. 
	*/
	public ArrayList<Node> getFromConnections()
	{
		return fromConnections;
	}
}	
