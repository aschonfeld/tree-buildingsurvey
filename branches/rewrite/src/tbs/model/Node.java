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
public abstract class Node extends ModelElement implements Cloneable
{	
	private String name;
	private int height;
	private int width;
	private Point anchorPoint;
	private boolean inTree;
	private int maxNameLength = 30;
	protected String imgFileName;

	//connections to and from other ModelElements, respectively
	private List<Node> connectedFrom = new LinkedList<Node>();
	private List<Node> connectedTo = new LinkedList<Node>();

	public Node(int id, String name, Point anchorPoint, int height, int width){
		super(id);
		this.name = name;
		this.height = height;
		this.width = width;
		this.anchorPoint = anchorPoint;
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
		return new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
	}
	
	public Rectangle2D getPaddedRectangle(){
		return new Rectangle2D.Double(getX()-4, getY()-4, getWidth()+8, getHeight()+8);
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
	* Returns X coordinate of Node's location (upper left corner)
	*/
	public int getX() {return anchorPoint.x;}
	
	/**
	* Sets X coordinate of Node's location (upper left corner)
	*/
	public void setX(int x){this.anchorPoint.x = x;}
	
	/**
	* Returns Y coordinatei of Node's location (upper left corner)
	*/
	public int getY() {return anchorPoint.y;}

	/**
	* Sets Y coordinate of Node's location (upper left corner)
	*/
	public void setY(int y){this.anchorPoint.y = y;}
	
	public abstract int getHeight();
	
	public int getDefaultHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public abstract int getWidth();
	
	public int getDefaultWidth() {
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

	/**
	* Returns the name of the image file associated with this object
	*/
	public String getImgFileName()
	{
		return imgFileName;
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
	
	/**
	* Explicitly declares this object to be active (true) or inactive
	* (false). This method should only be called to reflect actual change
	* in status, ie, if an Organism Node is moved out of the active
	* field, setInTree(false) would be the correct way to ensure that the
	* object knows it is not to connect to other objects. .
	*/ 
	public void setInTree(boolean inTree)
	{ 
		this.inTree = inTree;
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
	@Override
	public String dump()
	{
		String ret;
		ret = (this instanceof OrganismNode)?"O:":"E:";
		ret = ret + this.getId()+":";
		ret = ret + this.getName()+":";
		//ret = ret + this.getImgFileName()+":";
		ret = ret + this.getX()+":" + this.getY()+":";
		ret = ret + this.isInTree()+":(";
		for (Node toNode : this.getConnectedTo())
			ret = ret + toNode.getId()+",";
		ret = ret +"):(";
		for (Node fromNode : this.getConnectedFrom())
			ret = ret + fromNode.getId()+",";	
		ret = ret +"):";
		return ret;
	}

//-------------------------------------------------
// ---- Connection handling from here to end ------
//-------------------------------------------------


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

	public void removeConnectionTo(Node n)
	{
		connectedTo.remove(n);
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

	public void removeConnectionFrom(Node n)
	{
		connectedFrom.remove(n);
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
			return ((Node) m).getPaddedRectangle().intersects(getRectangle()); 
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
		return new Point(anchorPoint.x + (getWidth()/2),anchorPoint.y + (getHeight()/2));	
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		Node copy = (Node) super.clone();
		copy.setName(getName());
		copy.setAnchorPoint(anchorPoint);
		copy.setHeight(height);
		copy.setWidth(width);
		return copy;
	}
}	
