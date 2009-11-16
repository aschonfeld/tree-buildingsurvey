//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.*;

/**
* TBSView contains the logic for rendering the information contained in
* the data model.
**/
public class TBSView extends JComponent {

	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0xBB7D0BF0A83E3AF6L;
	
	// This connection follows the mouse
	private Point[] connInProgress;
	private String statusString;

	
	private TBSModel model;
	public TBSView(TBSModel m) {
        model = m;
        connInProgress = null;
    	statusString = null;
	}
	
	/**
	* Calls up a Swing-based string text entry box and returns the
	* submitted String. 
	* Avoiding Swing, so we're not using this. 
	*/
	public String promptUserForString(String message) {
		return (String) JOptionPane.showInputDialog(message);
	}
	
	/**
	* Calls up a Swing-based yes/no/cancel dialog box, returns the user's
	* selection. Avoiding this, since it's Swing. 
	*/
	public int promptUserForYesNoCancel(String message) {
		return JOptionPane.showConfirmDialog(null, message);
	}
	
	/**
	* Displays the button bar.
	*/
	public void renderButtons(Graphics g)
	{
		int width = TBSGraphics.appletWidth;
		if(width < 800) width = 800;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, TBSGraphics.buttonsHeight);
		int leftX = 0;
		int upperY = TBSGraphics.buttonsHeight - TBSGraphics.buttonsYPadding;
		for(TBSButtonType b: TBSGraphics.buttons) {
			g2.setColor(Color.BLACK);
			TBSGraphics.drawCenteredString(g2, b.toString(), leftX, upperY, TBSGraphics.buttonsWidth, 0);
			g2.setColor(Color.BLUE);
			g2.drawRect(leftX, 0,TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
			leftX += TBSGraphics.buttonsWidth;
		}
		int stringStartX = leftX + 20;
		renderStatusString(g2, stringStartX);
	}

	/**
	* draws a modelElement
	*/
	public void renderModelElement(Graphics g, ModelElement me) {
		Graphics2D g2 = (Graphics2D) g;
		int stringWidth = 0;
		int imageWidth = 0;
		int imageStartX = 0;
		if(me instanceof OrganismNode) 
		{
			OrganismNode on = (OrganismNode) me;
			stringWidth = (int) TBSGraphics.getStringBounds(g2, on.getName()).getWidth();
			imageWidth = on.getImage().getWidth();
			// center image and text
			int imageXOffset = (TBSGraphics.organismNodeWidth - imageWidth - stringWidth) / 2;
			imageStartX = on.getLeftX() + imageXOffset;
			g2.setColor(TBSGraphics.organismBoxColor);
			g2.fillRect(on.getLeftX(), on.getUpperY(), TBSGraphics.organismNodeWidth, TBSGraphics.organismNodeHeight);
			g2.drawImage(on.getImage(), imageStartX, on.getUpperY(), null);
			int stringAreaLeftX = imageStartX + imageWidth + TBSGraphics.paddingWidth;
			int stringAreaWidth = stringWidth;
			int stringAreaUpperY = on.getUpperY();
			int stringAreaHeight = TBSGraphics.organismNodeHeight;			
			TBSGraphics.drawCenteredString(g2, on.getName(), stringAreaLeftX, stringAreaUpperY, stringAreaWidth, stringAreaHeight);
		}
		else if (me instanceof EmptyNode)
		{
			EmptyNode en = (EmptyNode) me;
			String name = en.getName();
			int leftX = en.getLeftX();
			int upperY = en.getUpperY();
			if(name == null) name = "";
			// make empty nodes light purple (like Prof. White's node.gif)
			g2.setColor(TBSGraphics.emptyNodeColor);
			g2.fillRect(en.getLeftX(), en.getUpperY(), en.getWidth(), en.getHeight());
			// make bold for greater visibility;
	  		Font f = new Font(TBSGraphics.fontName, TBSGraphics.fontStyle, TBSGraphics.fontSize);
	   		g2.setFont(f);
			if(name.length() > 0) {
				// zero length string gives an error
				Rectangle2D bounds = TBSGraphics.getStringBounds(g2, en.getName());
				int h = (int) bounds.getHeight();
				int w = (int) bounds.getWidth();
				int stringX = leftX + (en.getWidth() / 2) - (w / 2);
				int stringY = upperY - h;
				g2.drawString(name, stringX, stringY);
			}
		}
		if(me.getSelected() && me instanceof Node){
			Node n = (Node) me;
			Graphics selectedBorder = g;
			selectedBorder.setColor(TBSGraphics.selectedNodeBorderColor);
			for (int i = 0; i <= TBSGraphics.selectedNodeBorderThickness; i++)
		    	selectedBorder.drawRect(n.getLeftX()-i, n.getUpperY()-i, n.getWidth() +(2*i), n.getHeight() +(2*i));
		}
	}
	
	/**
	* Establish this connection as the one to update and set. 	
	*/
	public void setConnInProgress(Point[] conn) {
		connInProgress = conn;
	}

	
	/**
	* Redraw the screen.
	*/
	public void refreshGraphics() {
		repaint();	
	}
	
	/**
	* Draw a connection. 
	*/
	public void renderConnections(Graphics2D g2) {
		for(ModelElement me: model.getElements()) {
			if(me instanceof Node) {
				Node fromNode = (Node) me;
				for(Connection c: fromNode.getConnections()) {
					Point[] conn = TBSUtils.getConnectionBounds(c.getFromNode() , 
						c.getToNode());
					//g2.setColor(Color.WHITE);
					drawArrow(g2, conn, c.getSelected());
					//Connection.drawOrTestLine(g2, conn, 0, 0);
				}
			}
		}
		if(connInProgress != null) {
			drawArrow(g2, connInProgress, false);
			//Connection.drawOrTestLine(g2, connInProgress, 0, 0);
		}
		
	}

	/**
	* Draw a 3-pixel wide line between two points.
	*/
	public void draw3PixelWideLine(Graphics g2, int x0, int y0, int x1, int y1, boolean selected) {
		g2.setColor(selected ? TBSGraphics.connectionSelectedColor : TBSGraphics.connectionColor);
		g2.drawLine(x0, y0, x1, y1);
		for(int i0 = -1; i0 <= 1; i0 += 1) {
			for(int i1 = -1; i1 <= 1; i1 += 2) {
				for(int i2 = -1; i2 <= 1; i2 += 1) {
					for(int i3 = -1; i3 <= 1; i3 += 2) {
						g2.drawLine(x0 + i0, y0 + i1, x1 + i2, y1 + i3);
					}
				}
			}
		}
	}
	
	/**
	* Draw the arrowhead at the end of a connection.
	*/
	public void drawArrow(Graphics2D g2, Point[] conn, boolean selected) {
		double arrowLengthInPixels = 10.0;
		double angle0 = 0.75 * Math.PI;
		double angle1 = 2 * Math.PI - angle0;
		double dx = (conn[1].x - conn[0].x);
		double dy = (conn[1].y - conn[0].y);
		double dArrowX0 = Math.round(dx * Math.cos(angle0) + dy * Math.sin(angle0));
		double dArrowY0 = Math.round(dy * Math.cos(angle0) - dx * Math.sin(angle0));
		double dArrowX1 = Math.round(dx * Math.cos(angle1) + dy * Math.sin(angle1));
		double dArrowY1 = Math.round(dy * Math.cos(angle1) - dx * Math.sin(angle1));
		double arrowLength = Math.sqrt(dx * dx + dy * dy);
		dArrowX0 /= arrowLength * (1.0 / arrowLengthInPixels);
		dArrowY0 /= arrowLength * (1.0 / arrowLengthInPixels);
		dArrowX1 /= arrowLength * (1.0 / arrowLengthInPixels);
		dArrowY1 /= arrowLength * (1.0 / arrowLengthInPixels);
		int arrowX0 = (int) Math.round(dArrowX0);
		int arrowY0 = (int) Math.round(dArrowY0);
		int arrowX1 = (int) Math.round(dArrowX1);
		int arrowY1 = (int) Math.round(dArrowY1);
		draw3PixelWideLine(g2, conn[0].x, conn[0].y, conn[1].x, conn[1].y, selected);
		draw3PixelWideLine(g2, conn[1].x, conn[1].y, conn[1].x + arrowX0, conn[1].y + arrowY0, selected);
		draw3PixelWideLine(g2, conn[1].x, conn[1].y, conn[1].x + arrowX1, conn[1].y + arrowY1, selected);
	}
	

	/**
	* Set status string.
	*/
	public void setStatusString(String s) {
		statusString = s;
	}

	/**
	* Draw the statusString. 	
	*/
	public void renderStatusString(Graphics2D g2, int leftX) {
		if(statusString == null) return;
		int upperY = 0;
		int height = TBSGraphics.buttonsHeight;
		TBSGraphics.drawCenteredString(g2, statusString, leftX, upperY, 0, height);
	}

	
	/**
	* How to paint the screen.
	*/
	// this is what the applet calls to refresh the screen
	public void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		refreshGraphics();
		Iterator<ModelElement> itr = model.getElements().iterator();
		while(itr.hasNext()) {
			renderModelElement(g, itr.next());
		}
		renderConnections(g2);
		renderButtons(g2);
		
	}
	
}
