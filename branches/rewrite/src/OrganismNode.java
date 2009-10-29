//TBS version 0.2

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;
	
public class OrganismNode extends Node
{
	private BufferedImage img;
	private int defaultLeftX;
	private int defaultUpperY;
	private Rectangle2D stringBounds;

	public OrganismNode(BufferedImage i, String n, Rectangle2D
sb, int x, int y, int w, int h)
	{

		super(n, x, y, w, h, false);
		defaultLeftX=x;
		defaultUpperY=y;
		stringBounds = sb;
	}
		
	public boolean collidesWith(ModelElement e)
	{
		return false;
	}

	public boolean isOrganismNode()
	{
		return true;
	}

	public BufferedImage getImage() 
	{
		return img;
	}

	public Rectangle2D getStringBounds()
	{
		return stringBounds;
	}
	public void remove()
	{
		setPosition(defaultLeftX, defaultUpperY);
		setInTree(false);
		unlink();
	}


	//stub. when implemented, will disconnect this node from
	//(what? one selected node? all connected nodes? how are
	//connections represented? decisions to be made)
	public void unlink()
	{
		System.out.println("Unlinked this");
	}

	public void link(ModelElement me)
	{
		System.out.println("Linked this to " + me.toString());

	}

}
