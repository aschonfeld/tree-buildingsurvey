//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.event.AdjustmentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import tbs.model.ModelElement;
import tbs.view.TBSButtonType;

/**
* TBSController contains the methods allowing the user to manipulate the
* data stored in the data model.
**/
public abstract class TBSController implements MouseListener, MouseMotionListener, KeyListener, AdjustmentListener
{
	public TBSController(){
		
	}
	
	public abstract TBSButtonType getButtonClicked();
	
	public abstract ModelElement elementMouseIsOver(int x, int y);
    
	public abstract void handleMouseButtonPressed(int x, int y);
    
    public abstract void handleMousePressed(int x, int y);    
}
