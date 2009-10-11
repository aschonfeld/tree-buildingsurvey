package phylogenySurvey;

/**
 * This object encompasses, rather than extending, the Node class. 
 * Node has a location (see getCenter() method) but inherits no direct
 * way to set that location. I don't know yet whether this has anything
 * to do with the rationale for this class. Note: no setters for
 * location.  
 */
public class NodeWithLocation {
	
	private Node node;
	private int x;
	private int y;
	
/**
 * Takes a Node object and a location (as x, y coordinates, not as a
 * Point)
 */
	public NodeWithLocation(Node node, int x, int y) {
		this.node = node;
		this.x = x;
		this.y = y;
	}

/**
 * Returns the Node associated with this object.
 */
	public Node getNode() {
		return node;
	}

/**
 * Returns this object's X coordinate
 */
	public int getX() {
		return x;
	}

/**
 * Returns this object's Y coordinate
 */
	public int getY() {
		return y;
	}

}
