import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.swing.JComponent;


public class TreeView extends JComponent {
	
	AdminApplication frame = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4503784957634793464L;

	TreeView() {
	}
	
	public void setFrame(AdminApplication frame) {
		this.frame = frame;
	}
	
	/**
	* How to paint the screen (using view's graphics)
	*/
	public void paintComponent() {
		paintComponent(getGraphics());
	}

	/**
	* How to paint the screen.
	*/
	// this is what the applet calls to refresh the screen
	public void paintComponent(Graphics g) {
		frame.initGraphics(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		frame.drawCurrentGraph(g);
		return;
	}
}