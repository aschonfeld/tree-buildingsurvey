//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.List;

import tbs.TBSGraphics;
import tbs.model.StudentModel;
import tbs.model.admin.Student;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.WrittenQuestionPrompt;

/**
 * StudentView represents the model for a test subject, displaying controls for
 * building and manipulating a tree.
 **/
public class StudentView extends TBSView {

	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0xBB7D0BF0A83E3AF6L;

	private StudentModel model;

	// This connection follows the mouse
	private Line2D connInProgress;
	private String screenString;

	public StudentView(Graphics2D g2, StudentModel m) {
		super(false, m);
		model = m;
		connInProgress = null;
		screenString = null;
		positionButtons(g2);
		positionModelElements(g2);
	}

	/**
	 * Displays the button bar.
	 */
	public void renderButtons(Graphics2D g2) {
		if (!getScreenPrintMode()) {
			TBSButtonType buttonClicked = model.getController()
					.getButtonClicked();
			if (buttonClicked == null)
				buttonClicked = TBSButtonType.SELECT;
			Rectangle buttonRect = new Rectangle(0, 0,
					TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
			int upperY = TBSGraphics.buttonsHeight - TBSGraphics.padding.height;
			for (TBSButtonType b : getButtons()) {
				if (model.isButtonActive(b))
					TBSGraphics.renderButtonBackground(g2, buttonRect, b
							.equals(buttonClicked));
				else {
					g2.setColor(TBSGraphics.buttonInactiveBgColor);
					g2.fill(buttonRect);
				}
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				if (!model.isButtonActive(b))
					TBSGraphics.drawCenteredString(g2, b.toString(),
							buttonRect.x, upperY, buttonRect.width, 0,
							TBSGraphics.buttonInactiveFntColor);
				else
					TBSGraphics.drawCenteredString(g2, b.toString(),
							buttonRect.x, upperY, buttonRect.width, 0);
				buttonRect.setLocation(buttonRect.x + TBSGraphics.buttonsWidth,
						buttonRect.y);
			}

			buttonRect.setLocation(buttonRect.x
					+ TBSGraphics.spaceBeforeQuestionButtons, buttonRect.y);
			TBSGraphics.questionButtonsStart = buttonRect.x;
			buttonRect.setSize(new Dimension(TBSGraphics.questionButtonsWidth,
					buttonRect.height));

			Prompt prompt = model.getPrompt();
			Student student = model.getStudent();
			String buttonString;
			TBSGraphics.renderButtonBackground(g2, buttonRect, (prompt != null)
					&& prompt instanceof WrittenQuestionPrompt);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			buttonString = "Questions";
			if (student.getResponse(OpenQuestionButtonType.ONE).isCompleted()
					&& student.getResponse(OpenQuestionButtonType.TWO)
							.isCompleted())
				buttonString += " \u2713";
			TBSGraphics.drawCenteredString(g2, buttonString, buttonRect.x,
					upperY, buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x
					+ TBSGraphics.questionButtonsWidth, buttonRect.y);

			// Show All Names Button
			buttonRect = new Rectangle(model.getApplet().getWidth()
					- (TBSGraphics.namesButtonWidth + getVerticalBar()
							.getWidth()), 0, TBSGraphics.namesButtonWidth,
					TBSGraphics.buttonsHeight);
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, "Names"
					+ (getDisplayAllTooltips() ? " \u2713" : ""), buttonRect.x,
					upperY, buttonRect.width, 0);
		}
	}

	public void renderElements(Graphics2D g2) {
		/*
		 * Uncomment this line of code to start logging of model integrity
		 * model.checkElementsIntegrity();
		 */
		renderUnselectedModelElements(g2);
		// Immortal Branch Node
		int stringAreaLeftX = TBSGraphics.emptyNodeLeftX
				+ TBSGraphics.emptyNodeWidth + TBSGraphics.padding.width;
		TBSGraphics.drawCenteredString(g2, TBSGraphics.immortalNodeLabel,
				stringAreaLeftX, TBSGraphics.emptyNodeUpperY,
				TBSGraphics.immortalNodeLabelWidth,
				TBSGraphics.emptyNodeHeight, TBSGraphics.emptyNodeColor);
		g2.fill(new Rectangle(TBSGraphics.emptyNodeLeftX,
				TBSGraphics.emptyNodeUpperY, TBSGraphics.emptyNodeWidth,
				TBSGraphics.emptyNodeHeight));
		renderSelectedModelElements(g2);
		if (connInProgress != null)
			renderConnection(g2, connInProgress, TBSGraphics.connectionColor);
		renderTooltip(g2);
		if (model.getStudentControllerTest() != null)
			model.getStudentControllerTest().renderVirtualCursor(g2);
	}

	public void renderStudents(Graphics2D g2) {
	}

	/**
	 * Establish this connection as the one to update and set.
	 */
	public void setConnInProgress(Line2D conn) {
		connInProgress = conn;
	}

	/**
	 * Set status string.
	 */
	public void setScreenString(String s) {
		screenString = s;
	}

	public String getScreenString() {
		return screenString;
	}

	/**
	 * Draw the statusString.
	 */
	public void renderScreenString(Graphics2D g2) {
		if (screenString != null && screenString.length() > 0) {
			int xVal = TBSGraphics.LINE_OF_DEATH + 20;
			int yVal = TBSGraphics.buttonsHeight;
			int width = model.getApplet().getWidth()
					- (xVal + TBSGraphics.buttonsWidth);
			List<String> lines = TBSGraphics.breakStringByLineWidth(g2,
					screenString, width);
			for (String line : lines) {
				TBSGraphics.drawCenteredString(g2, line, xVal, yVal, 0,
						TBSGraphics.buttonsHeight, TBSGraphics.tooltipColor);
				yVal += TBSGraphics.buttonsHeight;
			}
		}
	}
}
