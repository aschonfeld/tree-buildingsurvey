package TBS;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TBSView extends JPanel {
	
	private TreeMap<String, BufferedImage> organismNameToImage;
	
	public TBSView(TreeMap organismNameToImageArgument) { // , ImageObserver imageObserverArgument) {
		organismNameToImage = organismNameToImageArgument;
		// imageObserver = imageObserverArgument;
	}
	
	public void render(Graphics g, ImageObserver imageObserver) {
		Graphics2D g2 = (Graphics2D) g;
		int currentX = 0;
		int currentY = 0;
		String organismName = "";
		// draw organism selection panel
		Set<String> organismNames = organismNameToImage.keySet();
		Iterator<String> itr = organismNames.iterator();
		g2.setColor(new Color(255, 255, 255));
		g2.drawRect(0, 0, 200, 200);
		while(itr.hasNext()) {
			organismName = itr.next();
			BufferedImage img = organismNameToImage.get(organismName);
			g2.drawImage(img, currentX, currentY, imageObserver);
			currentY += img.getHeight();
			drawString(g2, organismName, img.getWidth() + 10, currentY);
			//System.out.println(organismName + currentY);
			
			
		}
	}
	
	public void drawString(Graphics2D g2, String name, int x, int y) {
		// ReneringHints tell
		RenderingHints rh = new RenderingHints(
		RenderingHints.KEY_TEXT_ANTIALIASING,
		RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
   		Point2D loc = new Point(x, y);
   		Font f = new Font("Lucida Sans Typewriter", Font.BOLD, 16);
   		g2.setFont(f);
   		FontRenderContext frc = g2.getFontRenderContext();
   		TextLayout layout = new TextLayout(name, f, frc);
   		layout.draw(g2, (float)loc.getX(), (float)loc.getY());
		Rectangle2D bounds = layout.getBounds();
   		bounds.setRect(bounds.getX()+loc.getX(),
                  bounds.getY()+loc.getY(),
                  bounds.getWidth(),
                  bounds.getHeight());
   		g2.draw(bounds);
	}

	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		render(g, null);
    }
}
