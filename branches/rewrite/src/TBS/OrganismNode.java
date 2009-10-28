package TBS;

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
	
public class OrganismNode implements ModelElement {
		
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
		}
				
		public boolean isInTree() {return inTree;}
		
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
		
		public boolean isOrganismNode() {return true;}

}
