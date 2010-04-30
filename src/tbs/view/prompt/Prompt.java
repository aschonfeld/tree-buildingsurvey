package tbs.view.prompt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.AdminModel;
import tbs.model.TBSModel;
import tbs.view.AdminView;

/**
 * Prompts are used to display information of various sorts on the screen; they
 * are essentially text boxes capable of bearing various sorts of buttons.
 */
public abstract class Prompt {

	private Graphics2D g2;
	private Point anchorPoint;
	private Dimension promptSize;
	private boolean renderClose;
	private Rectangle closeButton;
	private boolean renderMinimize;
	private Rectangle minimizeButton;
	private Rectangle bottomButtons;
	private int buttonHeight = TBSGraphics.textHeight
			+ TBSGraphics.padding.height;
	private int stringY = 0;

	private TBSModel model;
	private boolean finished;
	private boolean renderButtonsAndString;
	private boolean renderElements;

	private boolean minimizationInProgress = false;
	private boolean maximizationInProgress = false;
	private int resizeIndex;
	private int xIncr, yIncr, wIncr, hIncr;
	private boolean minimizedState = false;
	private String minimizedTitle;

	public Prompt(boolean renderButtonsAndString, boolean renderElements,
			Dimension promptSize, TBSModel model) {
		finished = false;
		this.renderButtonsAndString = renderButtonsAndString;
		this.renderElements = renderElements;
		this.promptSize = promptSize;
		anchorPoint = new Point();
		closeButton = new Rectangle();
		minimizeButton = new Rectangle();
		bottomButtons = new Rectangle();
		this.model = model;
		renderClose = true;
		renderMinimize = false;
	}

	public Graphics2D getGraphics() {
		return g2;
	}

	public void setGraphics(Graphics2D g2) {
		this.g2 = g2;
	}

	public Dimension getPromptSize() {
		return promptSize;
	}

	public int getWidth() {
		return promptSize.width;
	}

	public int getUnpaddedWidth() {
		return promptSize.width - (TBSGraphics.padding.width * 2);
	}

	public int getHeight() {
		return promptSize.height;
	}

	public void setAnchorPoint(Point anchorPoint) {
		this.anchorPoint = anchorPoint;
	}

	public int getX() {
		return anchorPoint.x;
	}

	public int getY() {
		return anchorPoint.y;
	}

	public Rectangle getCloseButton() {
		return closeButton;
	}

	public void setCloseButton(Rectangle closeButton) {
		this.closeButton = closeButton;
	}

	public Rectangle getMinimizeButton() {
		return minimizeButton;
	}

	public void setMinimizeButton(Rectangle minimizeButton) {
		this.minimizeButton = minimizeButton;
	}

	public Rectangle getBottomButtons() {
		return bottomButtons;
	}

	public int getStringY() {
		return stringY;
	}

	public void setStringY(int stringY) {
		this.stringY = stringY;
	}

	public void incrementStringY() {
		stringY += buttonHeight;
	}

	public void incrementStringY(int value) {
		stringY += value;
	}

	public void incrementStringYMulti(int value) {
		stringY += buttonHeight * value;
	}

	public void setBottomButtons(Rectangle bottomButtons) {
		this.bottomButtons = bottomButtons;
	}

	public boolean renderButtonsAndString() {
		return renderButtonsAndString;
	}

	public boolean renderElements() {
		return renderElements;
	}

	public void setRenderClose(boolean renderClose) {
		this.renderClose = renderClose;
	}

	public void setRenderMinimize(boolean renderMinimize) {
		this.renderMinimize = renderMinimize;
	}

	public boolean getMinimizedState() {
		return minimizedState;
	}

	public void setMinimizedTitle(String minimizedTitle) {
		this.minimizedTitle = minimizedTitle;
	}

	/**
	 * Returns true if Prompt is ready to close
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Sets a flag; if input value is true, Prompt will close itself
	 */
	public void setFinished(boolean finished) {
		this.finished = finished;
		minimizedState = false;
	}

	/**
	 * Contains instructions for painting this Prompt to the screen
	 */
	public abstract void paintComponent(Graphics2D g2);

	/**
	 * Returns true if {@link MouseEvent} e has x,y coordinates within one of
	 * this Prompt's buttons
	 */
	public boolean isOverButton(MouseEvent e) {
		if (minimizedState)
			return closeButton.contains(e.getPoint())
					|| minimizeButton.contains(e.getPoint());
		else
			return bottomButtons.contains(e.getPoint())
					|| (closeButton.contains(e.getPoint()) || minimizeButton
							.contains(e.getPoint()));
	}

	public Cursor getCursor(MouseEvent e) {
		if (isOverButton(e))
			return Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
		else
			return Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
	}

	/**
	 * Deals with mouse button clicks
	 */
	public abstract void mousePressed(MouseEvent e);

	/**
	 * Deals with keyboard input
	 */
	public abstract void keyPressed(KeyEvent e);

	/**
	 * Deals with other keyboard input
	 */
	public abstract void keyTyped(KeyEvent e);

	public void calculateValues(int lineCount, boolean hasBottomButtons) {
		calculateValues(lineCount, 0, hasBottomButtons);
	}

	/**
	 * Determines values of several interesting numbers, including the center
	 * point of the applet, an anchor point for the top left corner of the
	 * Prompt and locations of the close and selector buttons.
	 */
	public void calculateValues(int lineCount, int extraHeight,
			boolean hasBottomButtons) {
		promptSize.setSize(promptSize.width,
				(TBSGraphics.textHeight * lineCount)
						+ (TBSGraphics.padding.height * (lineCount + 1))
						+ extraHeight);

		int centerX;
		if (model instanceof AdminModel) {
			AdminView view = (AdminView) model.getView();
			int scrollWidth = view.hasStudentScroll() ? view.getStudentBar()
					.getWidth() : 0;
			int studentButtonWidth = TBSGraphics.maxStudentNameWidth
					+ TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
			int adminWidth = model.getApplet().getWidth()
					- (view.getVerticalBar().getWidth() + scrollWidth + studentButtonWidth);
			centerX = (adminWidth / 2) + scrollWidth + studentButtonWidth;
		} else
			centerX = model.getApplet().getWidth() / 2;

		int centerY = model.getApplet().getHeight() / 2;
		anchorPoint = new Point(centerX - (promptSize.width / 2), centerY
				- (promptSize.height / 2));

		if (hasBottomButtons)
			setBottomButtons(new Rectangle(anchorPoint.x, anchorPoint.y
					+ (promptSize.height - buttonHeight), promptSize.width,
					buttonHeight));
		if (renderClose) {
			Rectangle temp = new Rectangle((anchorPoint.x + promptSize.width)
					- buttonHeight, anchorPoint.y, buttonHeight, buttonHeight);
			closeButton = temp;
			if (renderMinimize) {
				minimizeButton = new Rectangle(temp.x - buttonHeight, temp.y,
						buttonHeight, buttonHeight);
				xIncr = (model.getApplet().getWidth() - anchorPoint.x) / 5;
				yIncr = anchorPoint.y / 5;
				wIncr = promptSize.width / 5;
				hIncr = promptSize.height / 5;
			}
		}
		stringY = anchorPoint.y;
	}

	public void drawBox() {
		Rectangle box = new Rectangle(anchorPoint.x - 2, anchorPoint.y - 2,
				promptSize.width + 4, promptSize.height + 4);
		g2.setColor(Color.lightGray);
		g2.fill(box);
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.x - 1.5, box.y - 1.5, box.width + 3,
				box.height + 3));
		g2.setStroke(new BasicStroke());
		if (renderClose)
			drawUtilityButton(closeButton, 1);
		if (renderMinimize)
			drawUtilityButton(minimizeButton, 2);

	}

	/**
	 * Passes s, x, and y to drawString(String s, int x, int y, boolean
	 * isSelected) with "false" as the final value.
	 */
	public void drawString(String s, int x, int y) {
		drawString(s, x, y, false);
	}

	/**
	 * Calls the drawCenteredString method from {@link TBSGraphics} to put a
	 * string on the screen. Default color for selected text is the same as used
	 * for EmptyNode, but this can be changed.
	 */
	public void drawString(String s, int x, int y, boolean isSelected) {
		if (s != null && s.length() > 0)
			TBSGraphics.drawCenteredString(g2, s, x, y, 0,
					TBSGraphics.textHeight + 4,
					isSelected ? TBSGraphics.selectedPromptTextColor
							: Color.BLACK);
	}

	public void drawText(List<String> lines) {
		drawText(lines, false);
	}

	public void drawText(List<String> lines, boolean selected) {
		int startX = anchorPoint.x + TBSGraphics.padding.width;
		for (String line : lines) {
			drawString(line, startX, stringY, selected);
			incrementStringY();
		}
	}

	public void drawHeader(String s) {
		TBSGraphics.drawCenteredString(g2, s, anchorPoint.x
				+ TBSGraphics.padding.width, stringY, promptSize.width
				- (TBSGraphics.padding.width * 2), buttonHeight,
				TBSGraphics.selectedPromptTextColor);
	}

	public void drawButtons(Object[] buttons) {
		drawButtons(buttons, null);
	}

	/**
	 * Draws the close button and selector buttons. Buttons, as elsewhere in
	 * TBS, are not objects, but are simply painted on the screen and checked by
	 * contains() methods.
	 */
	public void drawButtons(Object[] buttons, String selected) {
		if (buttons.length > 0) {
			Rectangle buttonRect = new Rectangle(bottomButtons.x,
					bottomButtons.y, bottomButtons.width / buttons.length,
					bottomButtons.height);
			for (Object button : buttons) {
				TBSGraphics.renderButtonBackground(g2, buttonRect, button
						.toString().equals(selected));
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, button.toString(),
						buttonRect.x, buttonRect.y + (buttonRect.height - 2),
						buttonRect.width, 0);
				buttonRect.setLocation(buttonRect.x + buttonRect.width,
						buttonRect.y);
			}
		}
	}

	public void drawButtons(Rectangle buttonArea, Object[] buttons,
			String selected) {
		if (buttons.length > 0) {
			Rectangle buttonRect = new Rectangle(buttonArea.x, buttonArea.y,
					buttonArea.width / buttons.length, buttonArea.height);
			for (Object button : buttons) {
				TBSGraphics.renderButtonBackground(g2, buttonRect, button
						.toString().equals(selected));
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, button.toString(),
						buttonRect.x, buttonRect.y, buttonRect.width,
						buttonRect.height);
				buttonRect.setLocation(buttonRect.x + buttonRect.width,
						buttonRect.y);
			}
		}
	}

	public void drawUtilityButton(Rectangle utilityButton, int utilityType) {
		TBSGraphics.renderButtonBackground(g2, utilityButton, false);
		g2.setColor(Color.BLACK);
		g2.setStroke(TBSGraphics.closeButtonStroke);
		g2.draw(utilityButton);
		switch (utilityType) {
		case 1:
			drawCloseButtonGraphic(utilityButton);
			break;
		case 2:
			drawMinimizeButtonGraphic(utilityButton);
			break;
		default:
			break;
		}
		g2.setStroke(new BasicStroke());
	}

	private void drawCloseButtonGraphic(Rectangle rect) {
		int x, y, w, h;
		x = rect.x + 1;
		y = rect.y + 1;
		w = rect.width - 1;
		h = rect.height - 1;
		g2.draw(new Line2D.Double(x, y, x + w, y + h));
		g2.draw(new Line2D.Double(x, y + h, x + w, y));
	}

	private void drawMinimizeButtonGraphic(Rectangle rect) {
		int x = 0, y = 0, w = 0;
		Rectangle minimizedRect = new Rectangle();
		x = rect.x + (rect.width / 4);
		w = rect.width / 2;
		if (!minimizedState)
			y = rect.y + (3 * (rect.height / 4));
		else {
			y = rect.y + (rect.height / 4);
			minimizedRect.setLocation(x - 1, y - 1);
			minimizedRect.setSize(w + 2, w + 2);
		}
		g2.draw(new Line2D.Double(x, y, x + w, y));
		g2.setStroke(new BasicStroke());
		g2.draw(minimizedRect);
	}

	public void startMinimization() {
		minimizationInProgress = true;
		minimizedState = true;
		resizeIndex = 1;
		renderElements = true;
	}

	public void startMaximization() {
		maximizationInProgress = true;
		resizeIndex = 4;
		renderElements = false;
	}

	public void drawResizedBox() {
		Point pt = new Point(anchorPoint.x + (resizeIndex * xIncr),
				anchorPoint.y + (resizeIndex * yIncr));
		Dimension dim = new Dimension(promptSize.width - (resizeIndex * wIncr),
				promptSize.height - (resizeIndex * hIncr));
		Rectangle box = new Rectangle(pt.x - 2, pt.y - 2, dim.width + 4,
				dim.height + 4);
		g2.setColor(Color.lightGray);
		g2.fill(box);
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		g2.draw(new Rectangle2D.Double(box.x - 1.5, box.y - 1.5, box.width + 3,
				box.getHeight() + 3));
		g2.setStroke(new BasicStroke());
		if (minimizationInProgress) {
			if (resizeIndex < 4)
				resizeIndex++;
			else
				minimizationInProgress = false;
		} else {
			if (resizeIndex > 1)
				resizeIndex--;
			else {
				maximizationInProgress = false;
				minimizedState = false;
			}
		}
	}

	public void drawMinimized() {
		if (minimizationInProgress || maximizationInProgress)
			drawResizedBox();
		else {
			Dimension textDim = TBSGraphics.getStringBounds(g2, minimizedTitle);
			Dimension dim = new Dimension(textDim.width
					+ (2 * (buttonHeight + TBSGraphics.padding.width)) + 3,
					buttonHeight + 3);
			Point pt = new Point(model.getApplet().getWidth()
					- (dim.width + 1 + model.getView().getVerticalBar()
							.getWidth()), model.getApplet().getHeight()
					- (buttonHeight + 5));

			// draw box & border
			Rectangle box = new Rectangle(pt.x, pt.y, dim.width, dim.height);
			g2.setColor(Color.lightGray);
			g2.fill(box);
			g2.setColor(Color.white);
			g2.setStroke(new BasicStroke(3));
			g2.draw(new Rectangle2D.Double(box.x - 1.5, box.y - 1.5,
					box.width + 3, box.height + 3));
			g2.setStroke(new BasicStroke());
			TBSGraphics.drawCenteredString(g2, minimizedTitle, pt.x
					+ TBSGraphics.padding.width, pt.y, 0, buttonHeight + 3);
			minimizeButton.setLocation(pt.x
					+ (textDim.width + (2 * TBSGraphics.padding.width)),
					pt.y + 1);
			drawUtilityButton(minimizeButton, 2);
			closeButton.setLocation(minimizeButton.x + buttonHeight,
					minimizeButton.y);
			drawUtilityButton(closeButton, 1);
		}
	}

	public int getSelectedButtonIndex(int mouseX, int buttonCount) {
		return ((mouseX - bottomButtons.x) * buttonCount) / promptSize.width;
	}

	public int getSelectedButtonIndex(int mouseX, int buttonCount,
			Rectangle buttonArea) {
		return ((mouseX - buttonArea.x) * buttonCount) / promptSize.width;
	}
}
