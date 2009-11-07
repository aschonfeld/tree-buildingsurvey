package tbs.model;

import java.util.ArrayList;

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
	ArrayList<Node> toNodes = new ArrayList<Node>();
	protected boolean selected;

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
	
	/**
	* Establish a directional link between this object and another.
	*/
	public void addConnection(Node n)
	{
		if (!inTree) return;  	//can't connect, not in tree
		if (toConnections.indexOf(n) < 0) return; //already connected
		if (n == this)
		{
			System.out.println("Tried to connect node to self");
			return;
		}
		toConnections.add(n); 		 
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
	public void RemoveConnection(Node n)
	{
		if (toConnections.indexOf(n) < 0)   //no connections to undo
		{
			System.out.println("--Bad call to Node.disconnectTo!--");
			return; 
		}

		toConnections.remove(toConnections.indexOf(n));
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
		fromConnections.remove(fromConnections.indexOf(n));
		System.out.println("Removed "+ n.getName() +" from " +getName()+
			" toConnections list.");
	}	

	/**
	* Remove all of this Node's connections; in preparation, perhaps,
	* for deleting it.
	*/
	public void unlink()
	{
		System.out.println("Unlink method called from " + getName());
	}

	public boolean isConnected()
	{
		return !toConnections.isEmpty();
	}
	
	
	public ArrayList<Node> getConnections() {
		return to;
	}
}	
