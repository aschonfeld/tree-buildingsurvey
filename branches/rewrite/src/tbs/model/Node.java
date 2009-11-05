package tbs.model;

//TBS version 0.3
//Node: superclass for OrganismNode and EmptyNode


	
public abstract class Node extends ModelElement 
{	
	//Warning - "protected" means public, as these will all be part of
	//the same package. Is this what you want? If so, should declare
	//public, for clarity. -jpk

	protected String name;
	protected int leftX;
	protected int upperY;
	protected int width;
	protected int height;
	protected boolean inTree;
	
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
		
}
