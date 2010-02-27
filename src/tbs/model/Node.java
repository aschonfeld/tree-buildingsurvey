//TBS version 0.4
//Node: superclass for OrganismNode and EmptyNode

package tbs.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import tbs.graphanalysis.Vertex;



/**
* This class is the superclass for OrganismNode and EmptyNode, and
* contains their common elements. 
*/
public abstract class Node extends ModelElement implements Cloneable
{	
	private String name;
	private Point anchorPoint;
	private Boolean inTree;
	private boolean beingDragged;
	
	//connections to and from other ModelElements, respectively
	private List<Node> connectedFrom = new LinkedList<Node>();
	private List<Node> connectedTo = new LinkedList<Node>();

	public Node(int id, String name){
		super(id);
		this.name = name;
		anchorPoint = new Point();
		inTree = false;
		beingDragged = false;
	}
	
	/**
	* Node locations are given by their upper left corner; this method
	* returns that value as a Point.
	*/
	public Point getAnchorPoint(){
		if(inTree || beingDragged)
			return anchorPoint;
		else
			return getDefaultPoint();
	}
	
	public abstract Point getDefaultPoint();

	/**
	* Returns the rectangle defined by this Node.
	*/	
	public Rectangle getRectangle() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}
	
	/**
	*	Returns the rectangle defined by this Node with 4 pixels of padding on each side	
	*/
	public Rectangle getPaddedRectangle() {
		return new Rectangle(getX()-4, getY()-4, getWidth()+8, getHeight()+8);
	}
	
	/**
	* Node locations are given by their upper left corner; this method
	* returns that value as a Point.
	*/
	public void setAnchorPoint(Point anchorPoint){
		this.anchorPoint = anchorPoint;
	}
	
	/**
	* Returns X coordinate of Node's location (upper left corner)
	*/
	public int getX() {
		if(inTree || beingDragged)
			return anchorPoint.x;
		else
			return getDefaultPoint().x;
	}
	
	/**
	* Sets X coordinate of Node's location (upper left corner)
	*/
	public void setX(int x){this.anchorPoint.x = x;}
	
	/**
	* Returns Y coordinate of Node's location (upper left corner)
	*/
	public int getY() {
		if(inTree || beingDragged)
			return anchorPoint.y;
		else
			return getDefaultPoint().y;
	}

	/**
	* Sets Y coordinate of Node's location (upper left corner)
	*/
	public void setY(int y){this.anchorPoint.y = y;}
	
	public abstract int getHeight();
	
	public abstract int getWidth();
	
	/**
	* Returns this Node's name.
	*/
	public String getName() {return name;}
	
	public void setName(String name) {this.name = name;}	
	
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
	public Boolean isInTree(){ 
		return inTree;
	}
	
	/**
	* Explicitly declares this object to be active (true) or inactive
	* (false). This method should only be called to reflect actual change
	* in status, ie, if an Organism Node is moved out of the active
	* field, setInTree(false) would be the correct way to ensure that the
	* object knows it is not to connect to other objects.
	*/ 
	public void setInTree(Boolean inTree){ 
		this.inTree = inTree;
	}
	
	public boolean isBeingDragged(){ 
		return beingDragged;
	}
	
	public void setBeingDragged(boolean beingDragged){ 
		this.beingDragged = beingDragged;
	}
	

	/**
	*	creates a string describing all the parameter of this object for
	*	saving/scoring and possibly undo
	*	The string's format is colon-delimited text fields, as follows:
	*	Serial number:Name:x location:y location:inTree:[toConnections]:
	*	[fromConnections]. Connection lists are comma-delimited lists of
	*	serial numbers surrounded by parentheses - this could be
	*	improved.
	*	Make suggestions. 
	*/
	public StringBuffer dump()
	{
		StringBuffer ret;
		ret =new StringBuffer( (this instanceof OrganismNode)?"O:":"E:");
		ret.append(this.getId()).append(":");
		ret.append(this.getName()).append(":");
		ret.append(this.getX()).append(":");
		ret.append(this.getY()).append(":");
		ret.append(this.isInTree()).append(":(");
		for (Node toNode : this.getConnectedTo())
			ret.append(toNode.getId()).append(",");
		ret.append("):(");
		for (Node fromNode : this.getConnectedFrom())
			ret.append(fromNode.getId()).append(",");	
		ret.append("):");
		return ret;
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
	* Establish a directional link between this object and another.
	*/
	public void addConnectionTo(Node n){
		if (this.connectedTo.contains(n)){
			System.out.println("AddConnection error: already connected");
			return;
		}else
			connectedTo.add(n);
		
		System.out.println(new StringBuffer("AddConnection: connected").append(getName())
				.append(" to ").append(n.getName()).toString());
	}

	public void removeConnectionTo(Node n){
		connectedTo.remove(n);
	}

	/**
	* Add n to this Node's list of objects connecting to it.
	*/
	public void addConnectionFrom(Node n){
		if (connectedFrom.contains(n)){	
			System.out.println("connectFrom error: already connected");
			 return;
		}else
			connectedFrom.add(n); 		 

		System.out.println("Connected to " +getName()+ " from " +
				n.getName());
	}

	public void removeConnectionFrom(Node n){
		connectedFrom.remove(n);
	}

	/**
	* Returns true if this Node overlaps another ModelElement. Should be
	* able to deal with Connections, but I haven't checked. 
	*/	
	public boolean collidesWith(ModelElement m) {		
		if(m instanceof Node)
			return ((Node) m).getPaddedRectangle().intersects(getRectangle()); 
		return false;
	}
	
	/**
	* Returns true if the point indicated is within the object's
	* boundaries. 
	*/
	public boolean contains(int x, int y) {
		return this.getRectangle().contains(new Point(x,y));
	}
	
	public Point getCenter() {
		return new Point(getX() + (getWidth()/2),getY() + (getHeight()/2));	
	}
	
	public Object clone() throws CloneNotSupportedException {
		Node copy = (Node) super.clone();
		copy.setName(getName());
		copy.setAnchorPoint(anchorPoint);
		return copy;
	}
	
	public abstract boolean isBeingLabeled();
	public abstract void setBeingLabeled(boolean beingLabeled);
	
	public abstract Vertex convertToVertex();
}	
