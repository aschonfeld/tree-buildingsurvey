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
	private TreeMap<String, ModelElement> elements;
	
	//represents connector node:freely created and deleted by student
	public abstract class EmptyNode extends ModelElement 
	{	
		// public EmptyNode(int leftX, int upperY) {}	
	}
	
	
	public abstract class Node extends ModelElement 
	{	
		//public Node(int leftX, int upperY) { }	
	}
	
	//represents organism node: fixed quantity, cannot be created or
	//deleted
	public class OrganismNode extends ModelElement 
	{
		public boolean collidesWith(Node n){return false;};
	 	public OrganismNode(int leftX, int upperY, Image img) 
		{
		}
		public int getLeftX() {return 0;}
		public int getUpperY() {return 0;}
		public int getLength() {return 0;}
		public int getWidth() {return 0;}		
	}

	public TreeMap<String, ModelElement> getElements() {
		return elements;
	}

	public void setElements(TreeMap<String, ModelElement> elements) {
		this.elements = elements;
	}
	
}

