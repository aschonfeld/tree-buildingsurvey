//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Properties;

import javax.swing.JScrollBar;
import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.graphanalysis.ConvexHull;
import tbs.graphanalysis.HullCollision;
import tbs.model.AdminModel;
import tbs.model.admin.Student;
import tbs.properties.PropertyLoader;

/**
 * TBSView contains the logic for rendering the information contained in
 * the data model.
 **/
public class AdminView extends TBSView {

	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0xBB7D0BF0A83E3AF6L; 

	private AdminModel model;
  
	private boolean hasStudentScroll = false;
	private JScrollBar studentBar;
	private int studentYOffset = 0;
	
	private Timer dropDownTimer;
	private boolean displayDropDownMenu = false;
	private ActionListener dropDownHider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			dropDownTimer.stop();
		}
	};
	
	private Timer hullTimer;
	private boolean displayHullMenu = false;
	private ActionListener hullHider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			hullTimer.stop();
		}
	};
	
	private Timer collisionTimer;
	private boolean displayCollisionMenu = false;
	private ActionListener collisionHider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			hullTimer.stop();
		}
	};
	
	public AdminView(Graphics2D g2, AdminModel m) {
		super(true, m);
		model = m;
		int studentBarMax = TBSGraphics.studentNodeHeight * model.getStudents().size();
		studentBarMax += (model.getStudents().size()-1) * TBSGraphics.ySpacing;
		if(studentBarMax > model.getApplet().getHeight()){
			studentBar = new JScrollBar(JScrollBar.VERTICAL, 0, model.getApplet().getHeight(), 0, studentBarMax);
			studentBar.setBlockIncrement(TBSGraphics.studentNodeHeight + TBSGraphics.ySpacing);
			add(studentBar, BorderLayout.WEST);
			hasStudentScroll = true;
		}else{
			studentBar = new JScrollBar();
		}
		hullTimer = new Timer(1000, hullHider);
		collisionTimer = new Timer(1000, collisionHider);
		dropDownTimer = new Timer(1000, dropDownHider);
		positionButtons(g2);
		positionModelElements(g2);
	}

	public boolean hasStudentScroll() {
		return hasStudentScroll;
	}

	public JScrollBar getStudentBar() {
		return studentBar;
	}

	public int getStudentYOffset() {
		return studentYOffset;
	}

	// sets the start of viewable tree area
	public void setStudentYOffset(int yo) {
		studentYOffset = yo;
	}
	
	public boolean isDropDownMenuDisplayed(){
		if(displayDropDownMenu)
			return true;
		return dropDownTimer.isRunning();
	}
	
	public void setDisplayDropDownMenu(Boolean displayDropDownMenu){
		if(!this.displayDropDownMenu && this.displayDropDownMenu == displayDropDownMenu)
			return;
		this.displayDropDownMenu = displayDropDownMenu;
		if(!this.displayDropDownMenu){
			if(dropDownTimer.isRunning())
				dropDownTimer.restart();
			else
				dropDownTimer.start();
		}else
			dropDownTimer.stop();
	}
	
	public boolean isHullMenuDisplayed(){
		if(displayHullMenu)
			return true;
		return hullTimer.isRunning();
	}
	
	public void setDisplayHullMenu(Boolean displayHullMenu){
		if(!this.displayHullMenu && this.displayHullMenu == displayHullMenu)
			return;
		this.displayHullMenu = displayHullMenu;
		if(!this.displayHullMenu){
			if(hullTimer.isRunning())
				hullTimer.restart();
			else
				hullTimer.start();
		}else{
			hullTimer.stop();
			setDisplayDropDownMenu(true);
			collisionTimer.stop();
			displayCollisionMenu = false;
		}
	}
	
	public boolean isCollisionMenuDisplayed(){
		if(displayCollisionMenu)
			return true;
		return collisionTimer.isRunning();
	}
	
	public void setDisplayCollisionMenu(Boolean displayCollisionMenu){
		if(!this.displayCollisionMenu && this.displayCollisionMenu == displayCollisionMenu)
			return;
		this.displayCollisionMenu = displayCollisionMenu;
		if(!this.displayCollisionMenu){
			if(collisionTimer.isRunning())
				collisionTimer.restart();
			else
				collisionTimer.start();
		}else{
			collisionTimer.stop();
			setDisplayDropDownMenu(true);
			hullTimer.stop();
			displayHullMenu = false;
		}
	}
	
	public void closeDropDowns(){
		hullTimer.stop();
		displayHullMenu = false;
		collisionTimer.stop();
		displayCollisionMenu = false;
		dropDownTimer.stop();
		displayDropDownMenu = false;
	}

	/**
	 * Displays the button bar.
	 */
	public void renderButtons(Graphics2D g2)
	{
		if(!getScreenPrintMode()){
			renderGroupSelection(g2);
			renderCollisionSelection(g2);
			TBSButtonType buttonClicked = model.getController().getButtonClicked();
			if(buttonClicked == null || model.getPrompt() == null)
				buttonClicked = TBSButtonType.TREE;
			int characterWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
			int studentWidth = characterWidth + getVerticalBar().getWidth() + (hasStudentScroll ? studentBar.getWidth() : 0);

			TBSGraphics.questionButtonsStart = (model.getApplet().getWidth() - studentWidth)/2 + (studentWidth-getVerticalBar().getWidth())
			- ((TBSGraphics.buttonsWidth*getButtons().size())/2);
			Rectangle buttonRect = new Rectangle(TBSGraphics.questionButtonsStart,0,TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
			int upperY = TBSGraphics.buttonsHeight - TBSGraphics.padding.height;
			for(TBSButtonType b: getButtons()) {
				if(b.equals(buttonClicked))
					TBSGraphics.renderButtonBackground(g2, buttonRect, true);
				else
					TBSGraphics.renderButtonBackground(g2, buttonRect, false);
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, b.toString(),
						buttonRect.x, upperY, buttonRect.width, 0);
				buttonRect.setLocation(buttonRect.x + TBSGraphics.buttonsWidth, buttonRect.y);
			}

			//Drop Down Menu Button
			buttonRect = new Rectangle(model.getApplet().getWidth()-(TBSGraphics.groupsButtonWidth + getVerticalBar().getWidth()),
					0,TBSGraphics.groupsButtonWidth, TBSGraphics.buttonsHeight);
			TBSGraphics.renderButtonBackground(g2, buttonRect, false);
			g2.setColor(Color.gray);
			g2.draw(buttonRect);
			TBSGraphics.drawCenteredString(g2, "Menu",
					buttonRect.x, upperY, buttonRect.width, 0);
			
			if(isDropDownMenuDisplayed()){
				//Print Button
				upperY += TBSGraphics.buttonsHeight;
				buttonRect.setLocation(buttonRect.x, buttonRect.y + TBSGraphics.buttonsHeight);
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, "Print",
						buttonRect.x, upperY, buttonRect.width, 0);
				
				//Show All Tooltips Button
				upperY += TBSGraphics.buttonsHeight;
				buttonRect.setLocation(buttonRect.x, buttonRect.y + TBSGraphics.buttonsHeight);
				TBSGraphics.renderButtonBackground(g2, buttonRect, false);
				g2.setColor(Color.gray);
				g2.draw(buttonRect);
				TBSGraphics.drawCenteredString(g2, "Names" + (getDisplayAllTooltips() ? " \u2713" : ""),
						buttonRect.x, upperY, buttonRect.width, 0);
				
				//Group Hulls Button
				if(model.getHulls(true).size() > 0){
					upperY += TBSGraphics.buttonsHeight;
					buttonRect.setLocation(buttonRect.x, buttonRect.y + TBSGraphics.buttonsHeight);
					TBSGraphics.renderButtonBackground(g2, buttonRect, false);
					g2.setColor(Color.gray);
					g2.draw(buttonRect);
					TBSGraphics.drawCenteredString(g2, "\u25C0 Groups (" + model.getHulls(true).size() + ")",
							buttonRect.x, upperY, buttonRect.width, 0);
					
					//Hull Collisions Button
					if(model.getHullCollisions(true).size() > 0){
						upperY += TBSGraphics.buttonsHeight;
						buttonRect.setLocation(buttonRect.x, buttonRect.y + TBSGraphics.buttonsHeight);
						TBSGraphics.renderButtonBackground(g2, buttonRect, false);
						g2.setColor(Color.gray);
						g2.draw(buttonRect);
						TBSGraphics.drawCenteredString(g2, "\u25C0 Collisions (" + model.getHullCollisions(true).size() + ")",
								buttonRect.x, upperY, buttonRect.width, 0);
					}
				}
			}
		}
	}
	
	public void renderElements(Graphics2D g2) {
		/*
		 * Uncomment this line of code to start logging of 
		 * model integrity
		 * model.checkElementsIntegrity();
		 */
		renderUnselectedModelElements(g2);
		if(getMaxX() > (model.getApplet().getWidth() - getVerticalBar().getWidth())){
			getHorizontalBar().setVisibleAmount(model.getApplet().getWidth() - getVerticalBar().getWidth());
			getHorizontalBar().setMaximum(getMaxX());
			if(!getScreenPrintMode())
				getHorizontalBar().setVisible(true);
				
		}else
			getHorizontalBar().setVisible(false);
		renderTooltip(g2);
		if(getScreenPrintMode())
			renderScreenPrintText(g2);
	}

	public void renderStudents(Graphics2D g2){
		if(!getScreenPrintMode()){
			String selectedStudentName = model.getStudent().getName();
			int x,y,width;
			int characterWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth;
			width = TBSGraphics.maxStudentNameWidth - TBSGraphics.padding.width;
			for(Student student : model.getStudents()){
				if(student.getName().equals(selectedStudentName))
					g2.setColor(TBSGraphics.selectedStudentColor);
				else
					g2.setColor(Color.WHITE);
				x = student.getAnchorPoint().x + (hasStudentScroll ? studentBar.getWidth() : 0);
				y = student.getAnchorPoint().y - studentYOffset;
				g2.fillRect(x, y,
						characterWidth, TBSGraphics.studentNodeHeight);
				String studentIndicators = "";
				int indicatorsWidth = TBSGraphics.arrowWidth + TBSGraphics.checkWidth;
				if(student.hasArrows())
					studentIndicators += " \u2192";
				String lastUpdate = student.getLastUpdate();
				if(!TBSUtils.isStringEmpty(lastUpdate))
					studentIndicators += " \u2713";
				if(studentIndicators.length() > 0)
					TBSGraphics.drawCenteredString(g2, studentIndicators,
							x + width, y, indicatorsWidth + TBSGraphics.padding.width,
							TBSGraphics.studentNodeHeight,
							Color.BLACK);
				y += TBSGraphics.padding.width;
				for(String nameString : student.getNodeName()){
					TBSGraphics.drawCenteredString(g2, nameString,
							x + TBSGraphics.padding.width, y,width, TBSGraphics.textHeight,
							Color.BLACK);
					y += TBSGraphics.textHeight;
				}
			}
		}
	}
	
	public void renderGroupSelection(Graphics2D g2){
		if(!getScreenPrintMode()){
			if(model.getPrompt() == null || model.getPrompt().renderElements()){
				List<ConvexHull> hulls = model.getHulls(true);
				Dimension buttonDimensions = TBSGraphics.get2DStringBounds(g2,hulls);
				int hullHeaderEnd = model.getApplet().getWidth()-(TBSGraphics.groupsButtonWidth + getVerticalBar().getWidth());
				TBSGraphics.hullButtonWidth = buttonDimensions.width + TBSGraphics.padding.width * 2;
				TBSGraphics.hullButtonHeight = buttonDimensions.height + TBSGraphics.padding.height * 2;
				Rectangle hullButton = new Rectangle(hullHeaderEnd - TBSGraphics.hullButtonWidth,
						TBSGraphics.buttonsHeight*3, TBSGraphics.hullButtonWidth, TBSGraphics.hullButtonHeight);
				int index=0;
				Color hullColor;
				for(ConvexHull ch : hulls){
					hullColor = TBSGraphics.hullColors[index];
					if(ch.getDisplayHull()){
						//Render Hull
						g2.setStroke(new BasicStroke(3));
						g2.setColor(hullColor);
						ch.render(g2, getXOffset(), getYOffset());
						g2.setStroke(new BasicStroke());
					}
					//Render Button
					if(isHullMenuDisplayed()){
						g2.setColor(hullColor);
						g2.fill(hullButton);
						TBSGraphics.drawCenteredString(g2, ch.toString(),
								hullButton.x, hullButton.y, hullButton.width, hullButton.height);
						g2.draw(hullButton);
					}
					g2.setColor(Color.BLACK);
					hullButton.setLocation(hullButton.x, hullButton.y + TBSGraphics.hullButtonHeight);
					index++;
				}
			}
		}
	}
	
	public void renderCollisionSelection(Graphics2D g2){
		if(!getScreenPrintMode()){
			if(model.getPrompt() == null || model.getPrompt().renderElements()){
				List<HullCollision> collisions = model.getHullCollisions(true);
				Dimension buttonDimensions = TBSGraphics.get2DStringBounds(g2,collisions);
				int hullHeaderEnd = model.getApplet().getWidth()-(TBSGraphics.groupsButtonWidth + getVerticalBar().getWidth());
				TBSGraphics.collisionButtonWidth = buttonDimensions.width + TBSGraphics.padding.width * 2;
				TBSGraphics.collisionButtonHeight = buttonDimensions.height + TBSGraphics.padding.height * 2;
				Rectangle collisionButton = new Rectangle(hullHeaderEnd - TBSGraphics.collisionButtonWidth,
						TBSGraphics.buttonsHeight*4, TBSGraphics.collisionButtonWidth, TBSGraphics.collisionButtonHeight);
				int index=0;
				Color hullColor;
				for(HullCollision hc : collisions){
					hullColor = TBSGraphics.hullColors[index];
					//Render Collision
					if(hc.getDisplayCollision())
						hc.render(g2, getXOffset(), getYOffset());
					//Render Button
					if(isCollisionMenuDisplayed()){
						g2.setColor(hullColor);
						g2.fill(collisionButton);
						TBSGraphics.drawCenteredString(g2, hc.toString(),
								collisionButton.x, collisionButton.y, collisionButton.width, collisionButton.height);
						g2.draw(collisionButton);
					}
					g2.setColor(Color.BLACK);
					collisionButton.setLocation(collisionButton.x, collisionButton.y + TBSGraphics.collisionButtonHeight);
					index++;
				}
			}
		}
	}

	/**
	 * Draw the statusString. 	
	 */
	public void renderScreenString(Graphics2D g2) {
		if(!getScreenPrintMode()){
			TBSButtonType buttonClicked = model.getController().getButtonClicked();
			int yStep = TBSGraphics.buttonsHeight;

			if(buttonClicked == null || model.getPrompt() == null)
				buttonClicked = TBSButtonType.TREE;

			Properties adminProps = PropertyLoader.getProperties("admin");
			StringBuffer screenString = new StringBuffer(String.format(adminProps.getProperty(buttonClicked.name()), model.getStudent().getName()));
			if(TBSButtonType.TREE.equals(buttonClicked)){
				String lastUpdate = model.getStudent().getLastUpdate();
				if(lastUpdate != null && lastUpdate.length() > 0)
					screenString.append("(Last Update: ").append(lastUpdate).append(")");
			}
			int studentWidth = TBSGraphics.maxStudentNameWidth + TBSGraphics.checkWidth + TBSGraphics.arrowWidth + 
			+ getVerticalBar().getWidth() + (hasStudentScroll ? studentBar.getWidth() : 0);
			int width = model.getApplet().getWidth() - studentWidth;
			int x = (model.getApplet().getWidth() - studentWidth)/2 + (studentWidth-getVerticalBar().getWidth());

			List<String> lines = TBSGraphics.breakStringByLineWidth(g2, screenString.toString(), width);
			int yVal = model.getApplet().getHeight() - (TBSGraphics.buttonsHeight * (lines.size()+1));
			for(String line : lines) {
				Dimension d = TBSGraphics.getStringBounds(g2, line);
				TBSGraphics.drawCenteredString(g2, line, x-(d.width/2), yVal, d.width, yStep, TBSGraphics.emptyNodeColor);
				yVal += yStep;
			}
		}
	}
	
	private void renderScreenPrintText(Graphics2D g2){
		int width = getWidth();
		if(getHorizontalBar().isVisible())
			width *= getHorizontalBar().getMaximum()/getHorizontalBar().getVisibleAmount();
		
		TBSButtonType buttonClicked = model.getController().getButtonClicked();
		if(buttonClicked == null || model.getPrompt() == null)
			buttonClicked = TBSButtonType.TREE;
		Properties adminProps = PropertyLoader.getProperties("admin");
		StringBuffer screenString = new StringBuffer(String.format(adminProps.getProperty(buttonClicked.name()), model.getStudent().getName()));
		if(TBSButtonType.TREE.equals(buttonClicked)){
			String lastUpdate = model.getStudent().getLastUpdate();
			if(lastUpdate != null && lastUpdate.length() > 0)
				screenString.append("(Last Update: ").append(lastUpdate).append(")");
		}
		List<String> lines = TBSGraphics.breakStringByLineWidth(g2, screenString.toString(), width);
		int yVal = TBSGraphics.padding.height;
		for(String line : lines) {
			TBSGraphics.drawCenteredString(g2, line, TBSGraphics.padding.width, yVal, width,
					TBSGraphics.textHeight + TBSGraphics.padding.height, Color.BLACK);
			yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		}
		
		yVal = getMaxY() == 0 ? (3 * TBSGraphics.textHeight) : (getMaxY() + TBSGraphics.textHeight);
		Properties questionProps = PropertyLoader.getProperties("questions");
		
		TBSGraphics.drawCenteredString(g2, "Written Questions", TBSGraphics.padding.width, yVal, width,
				TBSGraphics.textHeight + 4, Color.BLACK);
		yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		for(OpenQuestionButtonType writtenQuestion : OpenQuestionButtonType.getWrittenButtons()){
			for(String s : TBSGraphics.breakStringByLineWidth(g2,
					(writtenQuestion.ordinal() + 1) + ") " + 
					questionProps.getProperty(writtenQuestion.getQuestionKey()),
					width - TBSGraphics.padding.width * 2)){
				TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
						TBSGraphics.textHeight + 4, Color.BLACK);
				yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			}
			yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			String writtenAnswer = model.getStudent().getResponse(writtenQuestion).getText();
			if(TBSUtils.isStringEmpty(writtenAnswer))
				writtenAnswer = "NO REPONSE";
			for(String s : TBSGraphics.breakStringByLineWidth(g2,writtenAnswer,
					width - TBSGraphics.padding.width * 2)){
				TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
						TBSGraphics.textHeight + 4, Color.BLACK);
				yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			}
			yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		}
		TBSGraphics.drawCenteredString(g2, "Tree Analysis", TBSGraphics.padding.width, yVal, width,
				TBSGraphics.textHeight + 4, Color.BLACK);
		yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		for(String s : TBSGraphics.breakStringByLineWidth(g2,
				"1) All organism nodes are" + (model.getGraph().allOrganismsTerminal() ? " " : " not ") + "terminal.",
				width - TBSGraphics.padding.width * 2)){
			TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
					TBSGraphics.textHeight + 4, Color.BLACK);
			yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		}
			
		for(String s : TBSGraphics.breakStringByLineWidth(g2,
				"2) All organism nodes are" + (model.outOfTreeElements().isEmpty() ? " " : " not ") + "included.",
				width - TBSGraphics.padding.width * 2)){
			TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
					TBSGraphics.textHeight + 4, Color.BLACK);
			yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
		}
		List<String> collisionText = TBSUtils.collisonText(model);
		if(collisionText.isEmpty()){
			for(String s : TBSGraphics.breakStringByLineWidth(g2,
					"3) None of the groups of organisms collide with another group of organisms.",
					width - TBSGraphics.padding.width * 2)){
				TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
						TBSGraphics.textHeight + 4, Color.BLACK);
				yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			}
		}else{
			for(String s : TBSGraphics.breakStringByLineWidth(g2,
					"3) There were the following collisions between organism groups:",
					width - TBSGraphics.padding.width * 2)){
				TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
						TBSGraphics.textHeight + 4, Color.BLACK);
				yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			}
			for(String s : collisionText){
				TBSGraphics.drawCenteredString(g2, s, TBSGraphics.padding.width, yVal, 0,
						TBSGraphics.textHeight + 4, Color.BLACK);
				yVal += TBSGraphics.textHeight + TBSGraphics.padding.height;
			}
		}
	}
}