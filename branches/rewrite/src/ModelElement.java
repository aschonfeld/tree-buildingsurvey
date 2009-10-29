//package TBS;

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
	
public class ModelElement {
		
	public boolean collidesWith(ModelElement e){return false;}
	public boolean isOrganismNode(){return this instanceof TBSModel.OrganismNode;}
	public int getLeftX(){return 0;}
	public int getUpperY(){return 0;}
	public int getLength(){return 0;}
	public int getWidth(){return 0;}
}
