package admin;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;


public class TreeView extends JComponent implements Printable{
	
	private AdminApplication parent;
	private boolean screenPrintMode = false;
	
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
		g2.setColor(Common.backgroundColor);
		g2.fillRect(0, 0, getWidth(), getHeight() + (screenPrintMode ? 200 : 0));
		if(parent == null) return;
		parent.drawCurrentGraph(g,screenPrintMode);
		return;
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	throws PrinterException {
		if (pageIndex > 0) {
			return(NO_SUCH_PAGE);
		} else {
			int previousWidth = getWidth(), previousHeight = getHeight();
			int width = previousWidth, height = previousHeight+600;
			// make pic
			if(pageFormat.getImageableWidth() > width)
				width = (int) pageFormat.getImageableWidth();
			if(pageFormat.getImageableHeight() > height)
				height = (int) pageFormat.getImageableHeight();
			BufferedImage fullSizeImage = new BufferedImage(
					width, height, BufferedImage.TYPE_INT_RGB);
			Common.setColorsForPrinting();
			screenPrintMode = true;
			setSize(width, height);
			paint(fullSizeImage.getGraphics());
			screenPrintMode = false;
			// scale to fit
			double wRatio = width/pageFormat.getImageableWidth();
			double hRatio = height/pageFormat.getImageableHeight();
			int actualWidth;
			int actualHeight;
			if (wRatio > hRatio) {
				actualWidth = (int)(width/wRatio);
				actualHeight = (int)(height/wRatio);
			} else {
				actualWidth = (int)(width/hRatio);
				actualHeight = (int)(height/hRatio);
			}

			// print it
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(
					RenderingHints.KEY_FRACTIONALMETRICS, 
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2.drawImage(fullSizeImage, 
					(int)pageFormat.getImageableX(), 
					(int)pageFormat.getImageableY(), 
					actualWidth, 
					actualHeight, 
					null);
			fullSizeImage = null;
			Common.setColorsForDisplay();
			setSize(previousWidth, previousHeight);
			return(PAGE_EXISTS);
		}
	}
}
