package tbs.model;

//OrganismNode: represents "organisms" manipulated by user

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class OrganismNode extends Node
{
	private boolean inTree;
	private BufferedImage img;
	private int defaultLeftX;
	private int defaultUpperY;
	private TBSModel model;
		
	public OrganismNode(TBSModel m, BufferedImage i, String n, int x, int y, int w, int h) 
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
		model.clearConnections(this);
		System.out.println("Removed from tree: inTree = " +inTree);
	}
		
	public BufferedImage getImage() {return img;}
	
	public boolean collidesWith(ModelElement m) {
		List<Corners> cornersContained = new LinkedList<Corners>();
		if(m.contains(leftX, upperY+height))
			cornersContained.add(Corners.A);
		if(m.contains(leftX+width, upperY+height))
			cornersContained.add(Corners.B);
		if(m.contains(leftX+width, upperY))
			cornersContained.add(Corners.C);
		if(m.contains(leftX, upperY))
			cornersContained.add(Corners.D);
		
		if(cornersContained.size() == 4){//Organism placed right on top of another Organism
			//Default: move vertically up or down depending on space left
			
		}
		return cornersContained.size() > 0;
	}
	
	private enum Corners { A, B, C, D };


		
}
