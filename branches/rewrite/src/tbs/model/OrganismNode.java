//TBS version 0.4
//OrganismNode: represents "organisms" manipulated by user

package tbs.model;

import java.awt.image.BufferedImage;

import tbs.TBSGraphics;

public class OrganismNode extends Node
{
	private BufferedImage img;
	private int defaultLeftX;
	private int defaultUpperY;
	
		
	public OrganismNode(BufferedImage i, String name, int x, int y, int serial) 
	{
		super(name, x, y, TBSGraphics.organismNodeHeight, TBSGraphics.organismNodeWidth);
		img = i;
		defaultLeftX = x;
		defaultUpperY = y;
		this.serial = serial;
	}
				
	public void resetPosition(){
		setX(defaultLeftX);
		setY(defaultUpperY);
	}
		
	public BufferedImage getImage() {return img;}		
}
