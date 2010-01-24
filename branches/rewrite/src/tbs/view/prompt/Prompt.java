package tbs.view.prompt;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
* Prompts are used to display information of various sorts on the
* screen; they are essentially text boxes capable of bearing various
* sorts of buttons.
*/
public abstract class Prompt {
	
	private boolean finished;
	
	
	public Prompt(){
		finished = false;
	}

	/**
	* Returns true if Prompt is ready to close
	*/
	public boolean isFinished() {
		return finished;
	}

	/**
	* Sets a flag; if inpute value is true, Prompt will close itself
	*/
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	/**
	* Contains instructions for painting this Prompt to the screen
	*/
	public abstract void paintComponent(Graphics2D g2);
	
	/**
	* Returns true if {@link MouseEvent} e has x,y coordinates within one
	* of this Prompt's buttons
	*/
	public abstract boolean isOverButton(MouseEvent e);
	
	/**
	* Deals with mouse button clicks
	*/
	public abstract void mousePressed(MouseEvent e);
	
	/**
	* Deals with keyboard input
	*/
	public abstract void keyPressed(KeyEvent e);
	
	/**
	* Deals with other keyboard input
	*/
	public abstract void keyTyped(KeyEvent e);
	
}
