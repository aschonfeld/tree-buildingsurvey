//TBS version 0.4
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.image.BufferedImage;

public class OrganismNode extends Node
{
	private boolean inTree;
	private BufferedImage img;
	private int defaultLeftX;
	private int defaultUpperY;
	
		
	public OrganismNode(TBSModel m, BufferedImage i, String n, 
			int x, int y, int w, int h) 
	{
		model = m;
		img = i;
		name = n;
		defaultLeftX = x;
		leftX = x;
		defaultUpperY = y;
		upperY = y;
		width = w;
		height = h;
		inTree = false;
		serial = model.getSerial();
	}
				
	public boolean isInTree() {return inTree;}
	

	/**
	* Ad this object to the active list. All this does is to set a
	* boolean to true, telling the code that this is active.  
	*/	
	public void addToTree() 
	{
		inTree = true;
		
		System.out.println("Added to tree: inTree = " +inTree);
	}

	/**
	* Remove this Organism from the tree: delete its connections and
	* return it to its original place.
	*/	
	public void removeFromTree() {
		this.setSelected(false);
		unlink();
		inTree = false;
		leftX = defaultLeftX;
		upperY = defaultUpperY;
//		model.clearConnections(this);
		System.out.println("Removed from tree: inTree = " +inTree);
	}
		
	public BufferedImage getImage() {return img;}


	/**
	* Returns true if this Node overlaps another ModelElement. Should be
	* able to deal with Connections, but I haven't checked. 
	*/	
	public boolean collidesWith(ModelElement m) {
		if(m.contains(leftX, upperY+height))
			return true;
		if(m.contains(leftX+width, upperY+height))
			return true;
		if(m.contains(leftX+width, upperY))
			return true;
		if(m.contains(leftX, upperY))
			return true;
		return false;
	}
	
		
}
