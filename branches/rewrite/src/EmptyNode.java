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
	
public class EmptyNode extends Node {

	EmptyNode(int x, int y, String n) {
		leftX = x;
		upperY = y;
		width = 5;
		height = 5;
		name = n;
	}
	
	public boolean collidesWith(ModelElement e) {return false;};

	public void addToTree()
	{
		System.out.println ("Empty Node adding itself to the tree");

		//This is here to facilitate auto-add/auto-delete
		//Please don't delete.
		// If we fill in some code here, we can probably auto-add empty nodes from the ever-full
		// well, one of Bolker's requests from early on. Or if we leave it as is, everything's
		// fine too. ?
	}
	
	public void removeFromTree()
	{

		System.out.println("Deleting this empty node, called " + name);

		// this should call something like "TBSModel.remove(this)"
		// We want this in order to allow the "remove" button to function
		// for all nodes
		// Null node for now because I need to be able to call removeFromTree
	}
}
