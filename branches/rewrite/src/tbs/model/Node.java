//TBS version 0.4
//Node: superclass for OrganismNode and EmptyNode

package tbs.model;

import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;



/**
* This class is the superclass for OrganismNode and EmptyNode, and
* contains their common elements. 
*/
public abstract class Node extends ModelElement 
{	
	private String name;
	private int height;
	private int width;
	private Point anchorPoint;
	private boolean inTree;
	public int serial;
	private int maxNameLength = 30;

	//connections to and from other ModelElements, respectively
	private List<Node> connectedFrom = new LinkedList<Node>();
	private List<Node> connectedTo = new LinkedList<Node>();

	public Node(String name, int x, int y, int height, int width){
		this.name = name;
		this.height = height;
		this.width = width;
		this.anchorPoint = new Point(x,y);
		this.inTree = false;
	}
	/**
	* Node locations are given by their upper left corner; this method
	* returns that value as a Point.
	*/
	public Point getAnchorPoint()
	{
		return anchorPoint;	
	}
	
	public Rectangle2D getRectangle(){
		return new Rectangle2D.Double(getX(), getY(), width, height);
	}
	
	/**
	* Node locations are given by their upper left corner; this method
	* returns that value as a Point.
	*/
	public void setAnchorPoint(Point anchorPoint)
	{
		this.anchorPoint = anchorPoint;
	}
	
	/**
	* Returns X coordinate Node's location (upper left corner)
	*/
	public int getX() {return anchorPoint.x;}
	
	/**
	* Sets X coordinate Node's location (upper left corner)
	*/
	public void setX(int x){this.anchorPoint.x = x;}
	
	/**
	* Returns Y coordinate Node's location (upper left corner)
	*/
	public int getY() {return anchorPoint.y;}

	/**
	* Sets Y coordinate Node's location (upper left corner)
	*/
	public void setY(int y){this.anchorPoint.y = y;}
	
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	/**
	* Returns this Node's name.
	*/
	public String getName() {return name;}
	
	public void setName(String s) {
		if(this instanceof EmptyNode) {
			if(s.length() < maxNameLength) {
				name = s;
			}
		}
	}
	
	public List<Node> getConnectedFrom() {
		return connectedFrom;
	}

	public void setConnectedFrom(List<Node> connectedFrom) {
		this.connectedFrom = connectedFrom;
	}

	public List<Node> getConnectedTo() {
		return connectedTo;
	}

	public void setConnectedTo(List<Node> connectedTo) {
		this.connectedTo = connectedTo;
	}	
	
	
	/**
	* Adjusts the object's position by the indicated amount
	*/
	public void move(int deltaX, int deltaY) {
		anchorPoint = new Point(anchorPoint.x + deltaX, anchorPoint.y + deltaY);
	}		
	
	/**
	* Sets the object's position to the indicated point. 
	*/
	public void moveTo(int x, int y) {
		anchorPoint = new Point(x, y);
	}	
	
	/**
	* Returns true if the node thinks it should accept connections and
	* selected status. 
	*/
	public boolean isInTree()
	{ 
		return inTree;
	}
	
	public void setInTree(boolean inTree){ this.inTree = inTree;}

//-------------------------------------------------
// ---- Connection handling from here to end ------
//-------------------------------------------------


	/**
	* Establish a directional link between this object and another.
	*/
	public void addConnectionTo(Node n)
	{
		if (this.connectedTo.contains(n)){
			System.out.println("AddConnection error: already connected");
			return;
		}else
			connectedTo.add(n);
		
		System.out.println("AddConnection: connected" + getName() + " to " +
				n.getName());
	}

	/**
	* Add n to this Node's list of objects connecting to it.
	*/
	public void addConnectionFrom(Node n)
	{
		if (connectedFrom.contains(n)){	
			System.out.println("connectFrom error: already connected");
			 return;
		}else
			connectedFrom.add(n); 		 

		System.out.println("Connected to " +getName()+ " from " +
				n.getName());
	}
	
	public void unlink(){
		this.connectedTo.clear();
		this.connectedFrom.clear();
	}
	
	/**
	* Returns true if this Node overlaps another ModelElement. Should be
	* able to deal with Connections, but I haven't checked. 
	*/	
	@Override
	public boolean collidesWith(ModelElement m) {		
		if(m instanceof Node)
			return ((Node) m).getRectangle().intersects(getRectangle()); 
		return false;
	}
	
	/**
	* Returns true if the point indicated is within the object's
	* boundaries. 
	*/
	@Override
	public boolean contains(int x, int y) {
		return this.getRectangle().contains(new Point(x,y));
	}
	
	public Point getCenter(){
		return new Point(anchorPoint.x + (width/2),anchorPoint.y + (height/2));	
	}
}	
