//TBS Version 0.4
//TBSGraphics: Constants and low-level methods for graphics handling
package tbs;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

import tbs.view.TBSButtonType;

/**
* This class encapsulates the constants used in graphics handling for
* the TBS applet, and contains some grunt-work methods for setting up
* objects in the Node hierarchy.
* This class has only static methods and perhaps should be declared
* static. In any case, there is no constructor as it is not intended to
* be instantiated.
*/

public class TBSGraphics {
	
	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0xE6D7FA0516CC8DB2L;

	/**
	* Applet height: perhaps should be set to size of window eventually. 
	*/	
	public static int appletWidth;

	/**
	* Applet width: perhaps this should be set to size of window.
	*/
	public static int appletHeight;
	
	/** 
	* The LINE_OF_DEATH is the vertical line separating the active
	* elements of the model from the inactive; when a @ModelElement is
	* moved across it, it is either placed in the tree or removed,
	* depending on whether it lands in the active or the inactive
	* portion. 
	*/
	public static int LINE_OF_DEATH = 180;
	

	/**
	* The fixed width of all OrganismNodes. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int organismNodeWidth = 0;

	/**
	* The fixed height of all OrganismNodes. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int organismNodeHeight = 0;

	/**
	* The fixed width of all OrganismNode label strings. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int maxOrganismStringWidth = 0;

	/**
	* The fixed height of all OrganismNode label strings. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int maxOrganismStringHeight = 0;

	/**
	* The fixed width of all OrganismNode images. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int maxOrganismImageWidth = 0;

	/**
	* The fixed height of all OrganismNode images. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int maxOrganismImageHeight = 0;
	
	/**
	 * Default label for unused empty nodes
	 */
	public static String emptyNodeDefaultLabel = "Empty Node";
	
	/**
	 * Label for inTree empty nodes
	 */
	public static String emptyNodeLabel = "Empty Node #%d";
	
	/**
	* The fixed width of all EmptyNodes. 
	*/ 
	public static int emptyNodeWidth = 20;

	/**
	* The fixed height of all EmptyNodes. 
	*/ 
	public static int emptyNodeHeight = 20;


	/**
	* The initial x-coordinate of all EmptyNodes. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int emptyNodeLeftX;

	/**
	* The initial y-coordinate of all EmptyNodes. Value is calculated in
	* TBSModel.createModelElements (CHECK THIS)
	*/ 
	public static int emptyNodeUpperY;
	public static int emptyNodeYLabelOffset = 5;
	
	/**
	* The initial length of all arrowheads.
	*/ 
	public static double arrowLength = 0.1;
	
	/**
	* Minimum number of pixels around the right and left of an organism's name
	*/
	public static int paddingWidth = 5;
	
	/**
	* Space between bottom and top of images [in the left-hand column] 
	*/
	public static int ySpacing = 1;
	
	// Font Properties
	/**
	* Value used for font throught the applet. Currently set to "default"
	*/
	public static String fontName = "default"; // Use default font

	/**
	* Value used for font style throught the applet. Currently set to
	* "bold"
	*/
	public static int fontStyle = Font.BOLD;
	/**
	* Value used for font size throught the applet. Currently set to 16.
	*/
	public static int fontSize = 16;
	
	/**
	* Color of text strings labeling OrganismNodes. Currently set to
	* black.
	*/
	public static Color organismStringColor = Color.BLACK;
	
	/**
	* Background color of organismNodes. Currently set to white.
	*/
	public static Color organismBoxColor = Color.WHITE;
	public static Color connectionColor = new Color(0.5f, 1.0f, 0.5f);
	public static Color emptyNodeColor = new Color(0.5f, 0.5f, 1.0f);
	
	/**
	 * Styling of selected elements
	 */
	public static Color connectionSelectedColor = Color.GREEN;
	public static Color selectedNodeBorderColor = Color.GREEN;
	public static int selectedNodeBorderThickness = 3;
	
	/**
	* Vertical spacing of buttons.
	*/
	public static int buttonsYPadding = 5;

	/**
	* Horizontal spacing of buttons.
	*/ 
	public static int buttonsXPadding = 10;

	/**
	* Height of buttons. Set in  [TBSModel.???]
	*/
	public static int buttonsHeight = 0;

	/**
	* Width of buttons. Set in [TBSModel.???]
	*/ 
	public static int buttonsWidth = 0;

	/**
	* ArrayList holding names of the buttons used
	*/
	public static ArrayList<TBSButtonType> buttons;
	
	/**
	* Returns correct Font for TBS text.
	*/
	public static Font getFont(Graphics2D g2) {
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		Font f = new Font(fontName, fontStyle, fontSize);
		g2.setFont(f);
		return f;
	}
	
	/**
	* Returns the @Rectangle2D surrounding a piece of text
	*/
	public static Rectangle2D getStringBounds(Graphics2D g2, String name) {
		Font f = TBSGraphics.getFont(g2);
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(name, f, frc);
   		return layout.getBounds();
	}
	
	public static Point getMaxBounds(ArrayList<Point> points) {
		Point max = new Point(0,0);
		for(Point p: points) {
			int width = (int) p.x;
			int height = (int) p.y;
			if(width > max.x) max.x = width;
			if(height > max.y) max.y = height;
		}
		return max;	
	}
	
	public static Point get2DStringBounds(Graphics2D g2, 
			Collection<?> strings) 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(Object s: strings) 
		{
			Rectangle2D bounds = getStringBounds(g2, s.toString());
			points.add(new Point((int) bounds.getWidth(), 
				(int) bounds.getHeight()));
		}
		return getMaxBounds(points);
	}
	
	public static Point get2DImageBounds(Graphics2D g2, 
			Collection<BufferedImage> images) 
	{
		ArrayList<Point> points = new ArrayList<Point>();
		for(BufferedImage i: images) 
		{
			points.add(new Point(i.getWidth(), i.getHeight()));
		}
		return getMaxBounds(points);
	}
	
	public static Point get2DBounds(Point p0, Point p1) 
	{
		if(p1.x > p0.x) p0.x = p1.x;
		if(p1.y > p0.y) p0.y = p1.y;
		return p0;
	}


	/**
	* Paints a string centered in the rectangle defined.
	*/	
	public static void drawCenteredString(Graphics2D g2, String s, 
				int leftX, int upperY, int width, int height) 
	{
		// RenderingHints tell
		g2.setColor(Color.black);
   		Font f = TBSGraphics.getFont(g2);
   		FontRenderContext frc = g2.getFontRenderContext();
		TextLayout layout = new TextLayout(s, f, frc);
		Rectangle2D bounds = layout.getBounds();
		int stringHeight = (int) bounds.getHeight();
		int stringWidth = (int) bounds.getWidth();
		float y = upperY + height - (height - stringHeight) / 2;
   		float x = leftX + (width - stringWidth) / 2;
   		// if width or height is 0, do not center along that axis
   		if(width == 0) x = leftX;
   		if(height == 0) y = upperY;
   		layout.draw(g2, x, y);
	}

	
	
}
