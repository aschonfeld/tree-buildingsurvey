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

public class TBSModel {
	
	public interface ModelElement {
		
		 public boolean collidesWith(Node n);
	}
	
	public interface Node extends ModelElement {
		
		public int getLeftX();
		public int getUpperY();
		public int getLength();
		public int getWidth();
	}
	
	public class OrganismNode implements Node {
		
		OrganismNode(int leftX, int upperY, Image img) {}
		
		public boolean collidesWith(Node n) {return false;};
		public int getLeftX() {return 0;}
		public int getUpperY() {return 0;}
		public int getLength() {return 0;}
		public int getWidth() {return 0;}
	}
	
}

