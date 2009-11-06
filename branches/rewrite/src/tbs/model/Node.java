package tbs.model;

import java.util.ArrayList;

//TBS version 0.3
//Node: superclass for OrganismNode and EmptyNode


	
public abstract class Node extends ModelElement 
{	
	//Warning - "protected" means public, as these will all be part of
	//the same package. Is this what you want? If so, should declare
	//public, for clarity. -jpk

	String name;
	int leftX;
	int upperY;
	int width;
	int height;
	boolean inTree;
	ArrayList<Node> toNodes = new ArrayList<Node>();
	
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
	
	public void addConnection(Node toNode) {
		if(!isInTree()) return;
		if(!toNode.isInTree()) return;
		if(!toNodes.contains(toNode) && (toNode != this)) {
			toNodes.add(toNode);
		}
	}
	
	public void removeConnection(Node toNode) {
		toNodes.remove(toNode);
	}
	
	public ArrayList<Node> getConnections() {
		return toNodes;
	}
	
}
