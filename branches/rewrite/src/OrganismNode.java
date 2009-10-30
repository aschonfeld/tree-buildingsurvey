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
	private boolean inTree;
	private BufferedImage img;
	private Rectangle2D stringBounds;
	private int defaultLeftX;
	private int defaultUpperY;
		
	public OrganismNode(BufferedImage i, String n, Rectangle2D sb, int x, int y, int w, int h) {
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
		
	public void addToTree() {
		inTree = true;
	}
	
	public void removeFromTree() {
		inTree = false;
		leftX = defaultLeftX;
		upperY = defaultUpperY;
	}
		
	public BufferedImage getImage() {return img;}
		
	public Rectangle2D getStringBounds() {return stringBounds;}
		
	public boolean collidesWith(ModelElement m) {return false;}
		
}
