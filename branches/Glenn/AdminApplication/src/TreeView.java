import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;


public class TreeView extends JComponent {
	
	private AdminApplication parent;
	/**
	 * 
	 */
	private static final long serialVersionUID = -4503784957634793464L;

	TreeView() {
	}
	
	public void setParent(AdminApplication parent) {
		this.parent = parent;
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
		Graphics2D g2 = (Graphics2D) g;
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		g2.setFont(Common.font);
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if(parent == null) return;
		parent.drawCurrentGraph(g);
		return;
	}
}
