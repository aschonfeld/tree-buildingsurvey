package phylogenySurvey;

public class NodeWithLocation {
	
	private Node node;
	private int x;
	private int y;
	
	public NodeWithLocation(Node node, int x, int y) {
		this.node = node;
		this.x = x;
		this.y = y;
	}

	public Node getNode() {
		return node;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

}
