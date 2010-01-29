//TBSController: mousehandling and button handling for TBS


package tbs.controller;

import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import tbs.model.ModelElement;
import tbs.view.TBSButtonType;

/**
* TBSController contains the methods used to manipulate the information
* contained in the data model. There are two variations of the
* TBSController. The StudentController is used when a student is taking
* the Tree-Building Survey, and contains methods used in constructing a
* tree and answering the survey questions. The AdminController is used
* in examining students' work and viewing statistical patterns in the
* trees produced.  
**/
public abstract class TBSController implements MouseListener, MouseMotionListener, KeyListener
{
	public TBSController(){}

/**
* getButtonClicked returns the {@link TBSButtonType} of the button
* selected by the user.
*/	
	public abstract TBSButtonType getButtonClicked();

/**
* elementMouseIsOver returns the {@link ModelElement} containing the
* current mouse location. 
*/	
	public abstract ModelElement elementMouseIsOver(int x, int y);
    
/**
* NOT YET DOCUMENTED
*/
	public abstract void handleMouseButtonPressed(int x, int y);
    
/**
* NOT YET DOCUMENTED
*/
    public abstract void handleMousePressed(int x, int y);    
}
