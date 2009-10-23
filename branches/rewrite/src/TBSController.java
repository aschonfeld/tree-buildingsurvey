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

public class TBSController implements MouseListener, MouseMotionListener, ActionListener {
	
    public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mousePressed(MouseEvent e){}
	public void mouseClicked(MouseEvent e){}
	
	public void mouseDragged(MouseEvent e){
		int xPos = e.getX();
	    int yPos = e.getY();
	    System.out.println("mouseDragged " + xPos + " " + yPos);
	}
	
	public void mouseMoved(MouseEvent e){
		int xPos = e.getX();
	    int yPos = e.getY();
	    System.out.println("mouseMoved " + xPos + " " + yPos);
	}
	
	public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand()); 
    }
	
}
