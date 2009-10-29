//TBSModel v0.02

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

public class TBSModel 
{
	private ArrayList<ModelElement> modelElements;
	
	public TBSModel() {
		modelElements = new ArrayList<ModelElement>();
	}
	
	public void addElement(ModelElement m) {
		modelElements.add(m);
	}
	
	public int numElements() {
		return modelElements.size();
	}
	
	public ModelElement getElement(int i) {
		return modelElements.get(i);
	}
	
	public ArrayList<ModelElement> getElements() {
		return modelElements;
	}

	public void setElement(int i, ModelElement me) {
		modelElements.set(i, me);
	}
	
	public interface ModelElement {
		
		public boolean collidesWith(ModelElement e);
		public boolean isOverMe(int x, int y);
	}

	public interface Node extends ModelElement 
	{	
		public int getLeftX();
		public int getUpperY();
		public int getWidth();
		public int getHeight();
		public void move(int deltaX, int deltaY);
		public void moveTo(int x, int y);
		public boolean isInTree();
		public void addToTree();
	}
	
	//represents connector node:freely created and deleted by student
	public static class EmptyNode implements Node 
	{	
		int leftX;
		int upperY;
		int width;
		int height;
		
		EmptyNode(int x, int y) {
			leftX = x;
			upperY = y;
			width = 5;
			height = 5;
		}
		
		public void addToTree() {
			return;
		}
		
		public boolean isInTree() {return true;}
		
		
		public boolean isOverMe (int x, int y) {
			if((x > leftX) && (x < (leftX + width))) {
				if((y > upperY) && (y < (upperY + height))) {
					return true;
				}
			}
			return false;
		}
		
		public int getLeftX() {
			return leftX;
		}
		
		public int getUpperY() {
			return upperY;
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public void move(int deltaX, int deltaY) {
			leftX += deltaX;
			upperY += deltaY;
		}		
		
		public void moveTo(int x, int y) {
			leftX = x;
			upperY = y;
		}
		
		public boolean collidesWith(ModelElement e) {return false;};
				
	}
	
	public static class OrganismNode implements Node
	{
		private boolean inTree;
		private BufferedImage img;
		private String name;
		private Rectangle2D stringBounds;
		private int defaultLeftX;
		private int leftX;
		private int defaultUpperY;
		private int upperY;
		private int width;
		private int height;
		
		public OrganismNode(BufferedImage i, String n, Rectangle2D sb, int x, int y, int w, int h) {
			img = i;
			name = n;
			stringBounds = sb;
			defaultLeftX = x;
			leftX = 0;
			defaultUpperY = y;
			upperY = 0;
			width = w;
			height = h;
			inTree = false;
		}
				
		public boolean isInTree() {return inTree;}
		
		public void addToTree() {
			inTree = true;
		}
		
		public boolean isOverMe (int x, int y) {
			int activeX;
			int activeY;
			if(inTree) {
				activeX = leftX;
				activeY = upperY;
			} else {
				activeX = defaultLeftX;
				activeY = defaultUpperY;				
			}
			int rightX = activeX + width;
			int lowerY = activeY + height;
			//System.out.println(leftX + " " + x + " " + rightX + " " + upperY + " " + y + " " + lowerY);
			if((x > activeX) && (x < rightX)) {
				if((y > activeY) && (y < lowerY)) {
					return true;
				}
			}
			return false;
		}
		
		public int getLeftX() {
			if (inTree) {
				return leftX;
			} else {
				return defaultLeftX;
			}
		}
		
		public int getUpperY() {
			if (inTree) {
				return upperY;
			} else {
				return defaultUpperY;
			}
		}
		
		public int getWidth() {
			return width;
		}
		
		public int getHeight() {
			return height;
		}
		
		public BufferedImage getImage() {return img;}
		
		public String getName() {return name;}
		
		public Rectangle2D getStringBounds() {return stringBounds;}
		
		public boolean collidesWith(ModelElement m) {return false;}
		
		public void move(int deltaX, int deltaY) {
			leftX += deltaX;
			upperY += deltaY;
		}		
		
		public void moveTo(int x, int y) {
			leftX = x;
			upperY = y;
		}
		
		public void removeFromTree() {
			inTree = false;
		}
		
	}
	
}

