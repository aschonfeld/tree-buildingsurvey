package tbs.view.prompt;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


public abstract class Prompt {
	
	private boolean finished;
	
	public Prompt(){
		finished = false;
	}
	
	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	public abstract void paintComponent(Graphics2D g2);
	
	public abstract boolean isOverButton(MouseEvent e);
	
	public abstract void mousePressed(MouseEvent e);
	
	public abstract void keyPressed(KeyEvent e);
	
	public abstract void keyTyped(KeyEvent e);
	
}
