package tbs.view.prompt.student;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Properties;

import tbs.TBSGraphics;
import tbs.model.TBSModel;
import tbs.properties.PropertyLoader;
import tbs.view.prompt.Prompt;

/**
 * Displays a welcome message on starting up the applet.
 */
public class ResizeWarningPrompt extends Prompt {

	TBSModel model;
	Properties resizeProps;

	public ResizeWarningPrompt(TBSModel model, int width) {
		super(false, false, new Dimension(width < 300 ? width : 400, 0), model);
		setRenderClose(false);
		this.model = model;
		resizeProps = PropertyLoader.getProperties("resizewarning");
	}

	/**
	 * This Prompt does not handle keyboard input
	 */
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * This Prompt does not handle keyboard input
	 */
	public void keyTyped(KeyEvent e) {
	}

	/**
	 * Checks whether any mouse clicks occurred within the start button or the
	 * close button; if so, calls {@link setFinished} to close this Prompt.
	 */
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Instructions for rendering this Prompt
	 */
	public void paintComponent(Graphics2D g2) {
		setGraphics(g2);
		List<String> introduction = TBSGraphics.breakStringByLineWidth(g2,
				resizeProps.getProperty("warning"), getWidth()
						- TBSGraphics.padding.width * 2);

		calculateValues(introduction.size() + 2, false);
		drawBox();
		drawHeader("Warning");
		incrementStringY();
		drawText(introduction);
	}
}
