//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.StudentModel;
import tbs.model.admin.Student;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.WelcomePrompt;
import tbs.view.prompt.student.WrittenQuestionPrompt;

/**
 * StudentView represents the model for a test subject, displaying controls for building and
 * manipulating a tree.
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
		super(false);
		model = m;
		connInProgress = null;
		screenString = null; 
		setLayout(new BorderLayout());
		add(getVerticalBar(), BorderLayout.EAST);
		positionButtons(g2);
		positionModelElements(g2);

	}

	/**
	 * Displays the button bar.
	 */
	public void renderButtons(Graphics g)
	{
		TBSButtonType buttonClicked = model.getController().getButtonClicked();
		if(buttonClicked == null)
			buttonClicked = TBSButtonType.SELECT;
		Graphics2D g2 = (Graphics2D) g;
		Rectangle buttonRect = new Rectangle(0,0,TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
		int upperY = TBSGraphics.buttonsHeight - TBSGraphics.buttonsYPadding;
		for(TBSButtonType b: getButtons()) {
			if(b.equals(buttonClicked) ||
					(!buttonClicked.isMode() && b.equals(TBSButtonType.SELECT)))
				TBSGraphics.renderButtonBackground(g2, buttonRect, true);
			else
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			if(!model.isButtonActive(b)){
				g2.setColor(Color.RED);
				g2.setStroke(new BasicStroke(3));
				g2.draw(new Line2D.Double(buttonRect.x, buttonRect.y,
						buttonRect.x + buttonRect.width, buttonRect.y + buttonRect.height));
				g2.draw(new Line2D.Double(buttonRect.x, buttonRect.y+buttonRect.height,
						buttonRect.x + buttonRect.width, buttonRect.y));
				g2.setStroke(new BasicStroke());
			}
			TBSGraphics.drawCenteredString(g2, b.toString(),
					buttonRect.x, upperY, buttonRect.width, 0);
			buttonRect.setLocation(buttonRect.x + TBSGraphics.buttonsWidth, buttonRect.y);
		}

		buttonRect.setLocation(buttonRect.x + TBSGraphics.spaceBeforeQuestionButtons, buttonRect.y);
		TBSGraphics.questionButtonsStart = buttonRect.x;
		buttonRect.setSize(new Dimension(TBSGraphics.questionButtonsWidth, buttonRect.height));

		Prompt prompt = model.getPrompt();
		Student student = model.getStudent();
		String buttonString;
		if((prompt != null) && prompt instanceof WrittenQuestionPrompt)
			TBSGraphics.renderButtonBackground(g2, buttonRect, true);
		else
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
		g2.setColor(Color.gray);
		g2.draw(buttonRect);
		buttonString = "Questions";
		if(student.getResponse(OpenQuestionButtonType.ONE).isCompleted() && 
				student.getResponse(OpenQuestionButtonType.TWO).isCompleted())
			buttonString += " \u2713";
		TBSGraphics.drawCenteredString(g2, buttonString,
				buttonRect.x, upperY, buttonRect.width, 0);
		buttonRect.setLocation(buttonRect.x + TBSGraphics.questionButtonsWidth, buttonRect.y);

	}

	/**
	 * draws a modelElement
	 */
	public void renderUnselectedModelElements(Graphics2D g2, List<ModelElement> elements) {
		for(ModelElement me : elements){
			if(me instanceof OrganismNode) 
				renderOrganismNode(g2, (OrganismNode) me);
			else if (me instanceof EmptyNode)
			{
				EmptyNode en = (EmptyNode) me;
				String name = en.getName();
				if(name == null)
					name = "";
				// make empty nodes light purple (like Prof. White's node.gif)
				g2.setColor(TBSGraphics.emptyNodeColor);
				if(en.isBeingLabeled())
					model.getTextEntryBox().renderTextEntryBox(g2, getYOffset());
				else{
					if(en.isInTree()){
						Rectangle yAdjust = en.getRectangle();
						yAdjust.setLocation(yAdjust.x, yAdjust.y - getYOffset());
						g2.fill(yAdjust);
						TBSGraphics.drawCenteredString(g2, name, en.getX(),
								en.getY() - getYOffset(), en.getWidth(), en.getHeight());
					}else{
						int stringAreaLeftX = TBSGraphics.emptyNodeLeftX + TBSGraphics.emptyNodeWidth + TBSGraphics.paddingWidth;
						TBSGraphics.drawCenteredString(g2, TBSGraphics.immortalNodeLabel,
								stringAreaLeftX, TBSGraphics.emptyNodeUpperY,
								TBSGraphics.immortalNodeLabelWidth, TBSGraphics.emptyNodeHeight,
								TBSGraphics.emptyNodeColor);
						g2.fill(en.getRectangle());
					}
				}

			}else if(me instanceof Connection){
				Connection c = (Connection) me;
				if (!c.getFrom().collidesWith(c.getTo()))
				{
					Line2D conn = TBSUtils.getConnectionBounds(c.getFrom(), 
							c.getTo());
					conn = TBSUtils.scrollAdjust(conn, getYOffset());
					g2.setColor(TBSGraphics.connectionColor);
					g2.setStroke(new BasicStroke(3));
					g2.draw(conn);
					if(model.getStudent().hasArrows()){
						g2.draw(TBSUtils.getArrowHead(conn, 0.75 * Math.PI));
						g2.draw(TBSUtils.getArrowHead(conn, 1.25 * Math.PI));
					}
					g2.setStroke(new BasicStroke());
				}
			}
		}
	}

	public void renderOrganismNode(Graphics2D g2, OrganismNode on) {
		Color stringColor = on.isInTree() || on.isBeingDragged() ? TBSGraphics.organismBoxColor : TBSGraphics.organismStringColor;
		int stringWidth = 0;
		int imageWidth = 0;
		int imageStartX = 0;
		stringWidth = (int) TBSGraphics.getStringBounds(g2, on.getName()).getWidth();
		imageWidth = on.getImage().getWidth();
		// center image and text
		int imageXOffset = (TBSGraphics.organismNodeWidth - imageWidth - stringWidth) / 2;
		imageStartX = on.getDefaultPoint().x + imageXOffset;
		int stringAreaLeftX = imageStartX + imageWidth + TBSGraphics.paddingWidth;
		int stringAreaWidth = stringWidth;
		int stringAreaUpperY = on.getDefaultPoint().y;
		int stringAreaHeight = TBSGraphics.organismNodeHeight;
		g2.setColor(on.isInTree() || on.isBeingDragged() ? TBSGraphics.organismStringColor : TBSGraphics.organismBoxColor);
		if(!on.isInTree() && !on.isBeingDragged())
			g2.fillRect(on.getDefaultPoint().x, on.getDefaultPoint().y, on.getWidth(), on.getHeight());
		TBSGraphics.drawCenteredString(g2, on.getName(), stringAreaLeftX, stringAreaUpperY, stringAreaWidth, stringAreaHeight, stringColor);
		g2.drawImage(on.getImage(), imageStartX, on.getDefaultPoint().y, null);
		if(!on.isBeingDragged()){
			if(on.isInTree())
				g2.drawImage(on.getImage(), on.getX(), on.getY() - getYOffset(), null);
			else {
				// organism is being dragged for possible addition to tree
				if(on.getAnchorPoint().x > 0)
					g2.drawImage(on.getImage(), on.getX(), on.getY(), null);
			}
		}
	}

	public void renderSelectedModelElement(Graphics2D g2, ModelElement me){
		g2.setStroke(new BasicStroke(3));
		if(me instanceof Node){
			if(((Node) me).isBeingLabeled())
				return; // do not draw green box around node being labeled
			Node n = (Node) me;
			double y = n.getY() - 1.5;
			if(n.isInTree()) y -= getYOffset();
			g2.setColor(TBSGraphics.selectedNodeBorderColor);
			g2.draw(new Rectangle2D.Double(n.getX()-1.5,
					y,
					n.getWidth() + 3,
					n.getHeight() + 3));
			if(n instanceof OrganismNode){
				OrganismNode on = (OrganismNode) n;
				if(on.isInTree())
					g2.drawImage(on.getImage(), on.getX(), on.getY() - getYOffset(), null);
				else {
					// organism is being dragged for possible addition to tree
					if(on.getX() > 0) {
						g2.drawImage(on.getImage(), on.getX(), on.getY(), null);
						return;
					}
				}
			}
		}else{
			Connection c = (Connection) me;
			Line2D conn = TBSUtils.getConnectionBounds(c.getFrom() , 
					c.getTo());
			conn = TBSUtils.scrollAdjust(conn, getYOffset());
			g2.setColor(TBSGraphics.connectionSelectedColor);
			g2.draw(conn);
			if(model.getStudent().hasArrows()){
				g2.draw(TBSUtils.getArrowHead(conn, 0.75 * Math.PI));
				g2.draw(TBSUtils.getArrowHead(conn, 1.25 * Math.PI));
			}
		}	
		g2.setStroke(new BasicStroke());

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
		int xVal = TBSGraphics.LINE_OF_DEATH + 20;
		int yVal = TBSGraphics.buttonsHeight;
		int yStep = TBSGraphics.buttonsHeight;
		if(screenString == null) 
			return;
		int width = model.getApplet().getWidth() - (xVal + TBSGraphics.buttonsWidth);
		List<String> lines = TBSGraphics.breakStringByLineWidth(g2, screenString, width);
		for(String line : lines) {
			TBSGraphics.drawCenteredString(g2, line, xVal, yVal, 0, yStep, Color.CYAN);
			yVal += yStep;
		}
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
		g2.setFont(TBSGraphics.font);
		Prompt prompt = model.getPrompt();
		g2.setColor(Color.black);
		g2.fillRect(0, 0, model.getApplet().getWidth(), model.getApplet().getHeight());
		refreshGraphics();
		if(!(prompt instanceof WelcomePrompt)){
			renderButtons(g2);
			renderScreenString(g2);
		}
		if(prompt == null){
			renderUnselectedModelElements(g2, model.getElements());

			ModelElement selected = model.getSelectedElement();
			if(selected != null){
				if(selected instanceof Connection){
					for(Connection c : ModelUtils.getConnectionsByNodes(((Connection)selected).getFrom(), ((Connection)selected).getTo(), model))
						renderSelectedModelElement(g2,c);
				}else
					renderSelectedModelElement(g2,selected);
			}
			if(connInProgress != null){
				g2.setColor(TBSGraphics.connectionColor);
				g2.setStroke(new BasicStroke(3));
				g2.draw(TBSUtils.scrollAdjust(connInProgress, getYOffset()));
				if(model.getStudent().hasArrows()){
					g2.draw(TBSUtils.getArrowHead(TBSUtils.scrollAdjust(connInProgress, getYOffset()), 0.75 * Math.PI));
					g2.draw(TBSUtils.getArrowHead(TBSUtils.scrollAdjust(connInProgress, getYOffset()), 1.25 * Math.PI));
				}
				g2.setStroke(new BasicStroke());
			}
			renderTooltip(g2);
		}else
			prompt.paintComponent(g2);
		setCursor(getAppletCursor());
		if(model.getStudentControllerTest() != null) {
			model.getStudentControllerTest().renderVirtualCursor(g2);
		}
	}

	private void positionModelElements(Graphics2D g2) {
		TBSGraphics.organismNodeWidth = TBSGraphics.maxOrganismStringWidth + TBSGraphics.maxOrganismImageWidth + 
		TBSGraphics.paddingWidth * 2;
		if(TBSGraphics.maxOrganismStringHeight  > TBSGraphics.maxOrganismImageHeight)
			TBSGraphics.organismNodeHeight = TBSGraphics.maxOrganismStringHeight;
		else
			TBSGraphics.organismNodeHeight = TBSGraphics.maxOrganismImageHeight;
		
		//create left-side empty node
		TBSGraphics.immortalNodeLabelWidth = (int) TBSGraphics.getStringBounds(g2, TBSGraphics.immortalNodeLabel).getWidth();
		TBSGraphics.emptyNodeLeftX = (TBSGraphics.organismNodeWidth - (TBSGraphics.emptyNodeWidth + TBSGraphics.immortalNodeLabelWidth)) / 2;
		int emptyY = (TBSGraphics.buttonsHeight + 10) + (TBSGraphics.numOfOrganisms * (TBSGraphics.organismNodeHeight + TBSGraphics.ySpacing));
		TBSGraphics.emptyNodeUpperY = emptyY + ((TBSGraphics.organismNodeHeight - TBSGraphics.emptyNodeHeight)/2);
	}

	private void positionButtons(Graphics2D g2)
	{
		Dimension buttonDimensions = TBSGraphics.get2DStringBounds(g2,TBSButtonType.getButtons(false));
		TBSGraphics.buttonsWidth = buttonDimensions.width + 
		TBSGraphics.buttonsXPadding * 2;
		TBSGraphics.buttonsHeight = buttonDimensions.height + 
		TBSGraphics.buttonsYPadding * 2;

		buttonDimensions = TBSGraphics.getStringBounds(g2,"Questions");
		TBSGraphics.questionButtonsWidth = buttonDimensions.width + TBSGraphics.checkWidth +
			TBSGraphics.buttonsXPadding * 2;
	}
}