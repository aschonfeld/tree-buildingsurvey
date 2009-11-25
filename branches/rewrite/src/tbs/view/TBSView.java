//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.TBSModel;

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
	private Line2D connInProgress;
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
		TBSButtonType buttonClicked = model.getController().getButtonClicked();
		if(buttonClicked == null) buttonClicked = TBSButtonType.SELECT;
		int width = TBSGraphics.appletWidth;
		int minWidth = TBSGraphics.buttonsWidth * TBSButtonType.values().length;
		if(width < minWidth) width = minWidth;
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, TBSGraphics.buttonsHeight);
		int leftX = 0;
		int upperY = TBSGraphics.buttonsHeight - TBSGraphics.buttonsYPadding;
		for(TBSButtonType b: TBSButtonType.values()) {
			if(b == buttonClicked) {
				Color start = new Color(0.2f, 0.8f, 0.2f);
				Color end = new Color(1.0f, 1.0f, 1.0f);
				renderButtonBackground(g2, leftX, start, end);
			} else {
				Color start = new Color(0.45f, 0.55f, 0.65f);
				Color end = new Color(1.0f, 1.0f, 1.0f);
				renderButtonBackground(g2, leftX, start, end);
			}
			g2.setColor(Color.BLACK);
			TBSGraphics.drawCenteredString(g2, b.toString(), leftX, upperY, TBSGraphics.buttonsWidth, 0);
			g2.setColor(Color.gray);
			g2.drawRect(leftX, 0,TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
			leftX += TBSGraphics.buttonsWidth;
		}
		int stringStartX = leftX + 20;
		renderStatusString(g2, stringStartX);
	}
	
	public void renderButtonBackground(Graphics2D g2, int leftX, Color start, Color end) {
		float redDiff = end.getRed() - start.getRed();
		float greenDiff = end.getGreen() - start.getGreen();
		float blueDiff = end.getBlue() - start.getBlue();
		for(int y = 0; y <= TBSGraphics.buttonsHeight / 3; y++) {
			float fy = (float) y;
			float fh = (float) TBSGraphics.buttonsHeight / 3;
			float fdiff = 0.6f + 0.4f * fy / fh;
			float red = start.getRed() + redDiff * fdiff;
			float green = start.getGreen() + greenDiff * fdiff;
			float blue = start.getBlue() + blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + TBSGraphics.buttonsWidth, y);
		}
		for(int y = TBSGraphics.buttonsHeight / 3; y < TBSGraphics.buttonsHeight; y++) {
			float fy = (float) y - (TBSGraphics.buttonsHeight / 3);
			float fh = (float) 2.0f * (TBSGraphics.buttonsHeight / 3);
			float fdiff = fy / fh;
			float red = end.getRed() - redDiff * fdiff;
			float green = end.getGreen() - greenDiff * fdiff;
			float blue = end.getBlue() - blueDiff * fdiff;
			red /= 255.0f;
			green /= 255.0f;
			blue /= 255.0f;
			g2.setColor(new Color(red, green, blue));
			g2.drawLine(leftX, y , leftX + TBSGraphics.buttonsWidth, y);
		}
	}

	/**
	* draws a modelElement
	*/
	public void renderModelElement(Graphics g, ModelElement me) {
		Graphics2D g2 = (Graphics2D) g;
		if(me instanceof OrganismNode) 
		{
			renderOrganismNode(g2, (OrganismNode) me);
		}
		else if (me instanceof EmptyNode)
		{
			EmptyNode en = (EmptyNode) me;
			String name = en.getName();
			if(name == null) name = "";
			// make empty nodes light purple (like Prof. White's node.gif)
			g2.setColor(TBSGraphics.emptyNodeColor);
			g2.fill(en.getRectangle());
			// make bold for greater visibility;
	  		Font f = new Font(TBSGraphics.fontName, TBSGraphics.fontStyle, TBSGraphics.fontSize);
	   		g2.setFont(f);
			if(name.length() > 0) {
				// zero length string gives an error
				int h = (int) en.getHeight();
				int w = (int) en.getWidth();
				TBSGraphics.drawCenteredString(g2, name, en.getX(), en.getY(), w, h, Color.black);
			}
		}else if(me instanceof Connection){
			Connection c = (Connection) me;
			Line2D conn = TBSUtils.getConnectionBounds(c.getFrom() , 
					c.getTo());
				g2.setColor(me.equals(model.getSelectedModelElement()) ? TBSGraphics.connectionSelectedColor : TBSGraphics.connectionColor);
				g2.setStroke(new BasicStroke(3));
				g2.draw(conn);
				g2.draw(getArrowHead(conn, 0.75 * Math.PI));
				g2.draw(getArrowHead(conn, 1.25 * Math.PI));
		}
	}
	
	public void renderOrganismNode(Graphics2D g2, OrganismNode on) {
		Color stringColor = TBSGraphics.organismStringColor;
		Color boxColor = TBSGraphics.organismBoxColor;
		int stringWidth = 0;
		int imageWidth = 0;
		int imageStartX = 0;
		stringWidth = (int) TBSGraphics.getStringBounds(g2, on.getName()).getWidth();
		imageWidth = on.getImage().getWidth();
		// center image and text
		int imageXOffset = (TBSGraphics.organismNodeWidth - imageWidth - stringWidth) / 2;
		imageStartX = on.getDefaultPoint().x + imageXOffset;
		if(on.isInTree()) {
			stringColor = TBSGraphics.organismBoxColor;
			boxColor = TBSGraphics.organismStringColor;
			g2.drawImage(on.getImage(), on.getX(), on.getY(), null);
		} else {
			// organism is being dragged for possible addition to tree
			if(on.getX() > 0) {
				g2.drawImage(on.getImage(), on.getX(), on.getY(), null);
				return;
			}
		}
		int stringAreaLeftX = imageStartX + imageWidth + TBSGraphics.paddingWidth;
		int stringAreaWidth = stringWidth;
		int stringAreaUpperY = on.getDefaultPoint().y;
		int stringAreaHeight = TBSGraphics.organismNodeHeight;
		g2.setColor(boxColor);
		g2.fillRect(on.getDefaultPoint().x, on.getDefaultPoint().y, on.getDefaultWidth(), on.getDefaultHeight());
		TBSGraphics.drawCenteredString(g2, on.getName(), stringAreaLeftX, stringAreaUpperY, stringAreaWidth, stringAreaHeight, stringColor);
		g2.drawImage(on.getImage(), imageStartX, on.getDefaultPoint().y, null);
	}
	
	public void renderSelectedModelElement(Graphics g, ModelElement me){
		if(me == null)
			return;
		Graphics2D selectedGraphics = (Graphics2D) g;
		if(me instanceof Node){
			Node n = (Node) me;
			selectedGraphics.setColor(TBSGraphics.selectedNodeBorderColor);
			selectedGraphics.setStroke(new BasicStroke(TBSGraphics.selectedNodeBorderThickness));
			selectedGraphics.draw(new Rectangle2D.Double(n.getX()-1.5,
					n.getY()-1.5,
					n.getWidth() + TBSGraphics.selectedNodeBorderThickness,
					n.getHeight() + TBSGraphics.selectedNodeBorderThickness));
		}else{
			Connection c = (Connection) me;
			Line2D conn = TBSUtils.getConnectionBounds(c.getFrom() , 
					c.getTo());
			selectedGraphics.setColor(TBSGraphics.connectionSelectedColor);
			selectedGraphics.setStroke(new BasicStroke(3));
			selectedGraphics.draw(conn);
			selectedGraphics.draw(getArrowHead(conn, 0.75 * Math.PI));
			selectedGraphics.draw(getArrowHead(conn, 1.25 * Math.PI));
		}
			
	}
	
	/**
	* Establish this connection as the one to update and set. 	
	*/
	public void setConnInProgress(Line2D conn) {
		connInProgress = conn;
	}

	
	/**
	* Redraw the screen.
	*/
	public void refreshGraphics() {
		repaint();	
	}
	
	/**
	* Draw the arrowhead at the end of a connection.
	*/
	public Line2D getArrowHead(Line2D conn, double angle) {
		double dx = TBSUtils.dx(conn);
		double dy = TBSUtils.dy(conn);
		double dArrowX = Math.round(dx * Math.cos(angle) + dy * Math.sin(angle));
		double dArrowY = Math.round(dy * Math.cos(angle) - dx * Math.sin(angle));
		double arrowLength = Math.sqrt(dx * dx + dy * dy);
		dArrowX /= arrowLength * TBSGraphics.arrowLength;
		dArrowY /= arrowLength * TBSGraphics.arrowLength;
		int arrowX = (int) Math.round(dArrowX);
		int arrowY = (int) Math.round(dArrowY);
		return new Line2D.Double(
				conn.getP2().getX(),
				conn.getP2().getY(),
				conn.getP2().getX() + arrowX,
				conn.getP2().getY() + arrowY);
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
		g2.setColor(Color.black);
		g2.fillRect(0, 0, getWidth(), getHeight());
		refreshGraphics();
		for(ModelElement m : model.getElements())
			renderModelElement(g, m);
		renderSelectedModelElement(g,model.getSelectedModelElement());
		if(connInProgress != null){
			g2.setColor(TBSGraphics.connectionColor);
			g2.setStroke(new BasicStroke(3));
			g2.draw(connInProgress);
			g2.draw(getArrowHead(connInProgress, 0.75 * Math.PI));
			g2.draw(getArrowHead(connInProgress, 1.25 * Math.PI));
		}
		g2.setStroke(new BasicStroke());
		renderButtons(g2);
		
	}
	
}
