//TBS version 0.2: Node

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
	
public abstract class Node extends ModelElement
{
		
		private boolean inTree;
		private String name;
		private int leftX;
		private int upperY;
		private int width;
		private int height;
		
		
		public Node(String n,  int x, int y, int w, int h, boolean inTree)
		{
			super(n);
			leftX=x;
			upperY=y;
			width = w;
			height = h;
			this.inTree=inTree;
		}
		
		public boolean isInTree() 
		{
			return inTree;
		}
	
		public void setInTree(boolean in)
		{
			inTree=in;
		}
		
		public int getLeftX() 
		{
			return leftX;
		}
		
		public int getUpperY() {
			return upperY;
		}
	

		//for use in moving the element; overloaded for your convenience
		public void setPosition(int newX, int newY)
		{
			leftX=newX;
			upperY=newY;
		}

		public void setPosition(Point p)
		{
			setPosition(p.x, p.y);				
		}	
		
		//returns true if point (x,y) is inside element's borders
		//overloaded as Point, just to be nice

		public boolean contains(int x, int y)
		{
			if ( x > leftX   && x <= (leftX + width)  &&
				  y > upperY  && y <= (upperY + height) )
					return true;
			else 
					return false;
		}
		public boolean contains(Point p)
		{
			return contains(p.x, p.y);
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		
		
		public boolean collidesWith(ModelElement m) {return false;}
		
		public abstract boolean isOrganismNode(); 
		
		public void addToTree(int leftXArg, int upperYArg) {
			leftX = leftXArg;
			upperY = upperYArg;
			inTree = true;
		}
		
		public abstract void remove();
		
		public void unLink()
		{
		}

	
}
