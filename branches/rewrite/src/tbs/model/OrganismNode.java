package tbs.model;

//TBS version 0.3
//OrganismNode: represents "organisms" manipulated by user

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class OrganismNode extends Node
{
	private boolean inTree;
	private BufferedImage img;
	private Rectangle2D stringBounds;
	private int defaultLeftX;
	private int defaultUpperY;
		
	public OrganismNode(BufferedImage i, String n, Rectangle2D sb, 
					int x, int y, int w, int h) 
	{
		img = i;
		name = n;
		stringBounds = sb;
		defaultLeftX = x;
		leftX = x;
		defaultUpperY = y;
		upperY = y;
		width = w;
		height = h;
		inTree = false;
	}
				
	public boolean isInTree() {return inTree;}
		
	public void addToTree() 
	{
		inTree = true;
		
		System.out.println("Added to tree: inTree = " +inTree);
	}
	
	public void removeFromTree() {
		inTree = false;
		leftX = defaultLeftX;
		upperY = defaultUpperY;

		System.out.println("Removed from tree: inTree = " +inTree);
	}
		
	public BufferedImage getImage() {return img;}
		
	public Rectangle2D getStringBounds() {return stringBounds;}
		
	public boolean collidesWith(ModelElement m) {return false;}
		
}
