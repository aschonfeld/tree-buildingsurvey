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
	
	public abstract class ModelElement 
	{
		
		public boolean collidesWith(Node n);
		public int getLeftX() {return 0;}
		public int getUpperY() {return 0;}
		public int getLength() {return 0;}
		public int getWidth() {return 0;}
	}
	
	//represents connector node:freely created and deleted by student
	public class EmptyNode extends ModelElement 
	{	
		public EmptyNode(int leftX, int upperY)
		{
		}	
	}
	
	//represents organism node: fixed quantity, cannot be created or
	//deleted
	public class OrganismNode extends ModelElement 
	{
		
	 	public OrganismNode(int leftX, int upperY, Image img) 
		{
		}
		
	}
	
}

