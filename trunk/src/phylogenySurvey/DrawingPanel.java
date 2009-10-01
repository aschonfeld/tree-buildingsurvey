package phylogenySurvey;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.util.Iterator;

import javax.swing.JPanel;

public class DrawingPanel extends JPanel implements Printable {
	
	private SurveyUI surveyUI;
	
	public DrawingPanel(SurveyUI surveyUI) {
		this.surveyUI = surveyUI;
	}
	
	public void paint(Graphics g) {
		super.paint(g);

		g.setColor(Color.BLUE);
		Iterator<Link> it = surveyUI.getSurveyData().getLinks().iterator();
		while (it.hasNext()) {
			Link link = it.next();
			g.drawLine(link.getOneLabel().getCenter().x, link.getOneLabel().getCenter().y, 
					link.getOtherLabel().getCenter().x, link.getOtherLabel().getCenter().y);
		}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
		if (pageIndex > 0) {
			return(NO_SUCH_PAGE);
		} else {
			// make pic
			BufferedImage fullSizeImage = new BufferedImage(
					getWidth(), 
					getHeight(), 
					BufferedImage.TYPE_INT_RGB);
			paint(fullSizeImage.getGraphics());
			
			// scale to fit
			double wRatio = getWidth()/pageFormat.getImageableWidth();
			double hRatio = getHeight()/pageFormat.getImageableHeight();
			int actualWidth;
			int actualHeight;
			if (wRatio > hRatio) {
				actualWidth = (int)(getWidth()/wRatio);
				actualHeight = (int)(getHeight()/wRatio);
			} else {
				actualWidth = (int)(getWidth()/hRatio);
				actualHeight = (int)(getHeight()/hRatio);
			}

			// print it
			Graphics2D g2d = (Graphics2D)g;
			g2d.setRenderingHint(
					RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
			g2d.setRenderingHint(
					RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(
					RenderingHints.KEY_FRACTIONALMETRICS, 
					RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g2d.drawImage(fullSizeImage, 
					(int)pageFormat.getImageableX(), 
					(int)pageFormat.getImageableY(), 
					actualWidth, 
					actualHeight, 
					null);
			fullSizeImage = null;
			return(PAGE_EXISTS);
		}
	}

}
