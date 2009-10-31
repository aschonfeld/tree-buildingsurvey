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

	TBSModel model;
	private int initX, initY;


	EmptyNode(TBSModel mod, int x, int y, String n) {
		leftX = x;
		upperY = y;
		initY = x;
		initY = y;
		width = 5;
		height = 5;
		name = n;
		model = mod;
	}
	
	public boolean collidesWith(ModelElement e) {return false;};

	public void addToTree()
	{
		model.addElement(new EmptyNode(model, initX, initY, "EmptyNode"));
	
	}
	
	public void removeFromTree()
	{

		model.delete(this);

	}
}
