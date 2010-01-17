//TBS Version 0.4
//TBSView: one logic for converting Model to a visual representation

package tbs.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.TBSModel;
import tbs.model.admin.Student;
import tbs.properties.PropertyType;
import tbs.view.prompt.Prompt;

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
	
	// This connection follows the mouse
	private String screenString;
	private JScrollBar verticalBar;
	private int yOffset = 0; // start of viewable tree area
	private JScrollBar studentBar;
	private int studentYOffset;
	private Cursor cursor;
	
	//Tooltip information
	private String tooltipString;
	private Point tooltipLocation;
	private Timer timer;
	private ActionListener hider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			tooltipString = null;
		}
	};
	
	private TBSModel model;
	
	public AdminView(TBSModel m) {
        model = m;
        screenString = null;
    	cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    	verticalBar = new JScrollBar(JScrollBar.VERTICAL, 0, 100, 0, 200);
    	studentBar = new JScrollBar(JScrollBar.VERTICAL, 0, 100, 0, 200);
		setLayout(new BorderLayout());
		add(studentBar, BorderLayout.WEST);
 		add(verticalBar, BorderLayout.EAST);
 		timer = new Timer(1000, hider);
	}
	
	public JScrollBar getVerticalBar() {
		return verticalBar;
	}
	
	public int getYOffset() {
		return yOffset;
	}
	
	// sets the start of viewable tree area
	public void setYOffset(int yo) {
		yOffset = yo;
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
	
	public void setAppletCursor(Cursor cursor) {
		this.cursor = cursor;
	}
	
	public void updateTooltip(String name, Point location){
		tooltipString = name;
		tooltipLocation = location;
	}
	
	public boolean isTooltipRunning(){
		return timer.isRunning();
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
		if(buttonClicked == null || model.getPrompt() == null)
			buttonClicked = TBSButtonType.TREE;
		Graphics2D g2 = (Graphics2D) g;
		Rectangle buttonRect = new Rectangle(TBSGraphics.questionButtonsStart,0,TBSGraphics.buttonsWidth, TBSGraphics.buttonsHeight);
		int upperY = TBSGraphics.buttonsHeight - TBSGraphics.buttonsYPadding;
		for(TBSButtonType b: model.getButtons()) {
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
	}

	/**
	* draws a modelElement
	*/
	public void renderModelElement(Graphics2D g2, ModelElement me) {
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
			Rectangle yAdjust = en.getRectangle();
			yAdjust.setLocation(yAdjust.x, yAdjust.y - yOffset);
			g2.fill(yAdjust);
			TBSGraphics.drawCenteredString(g2, name, en.getX(),
						en.getY() - yOffset, en.getWidth(), en.getHeight());
		}else if(me instanceof Connection){
			Connection c = (Connection) me;
			Line2D conn = TBSUtils.getConnectionBounds(c.getFrom() , 
					c.getTo());
			conn = scrollAdjust(conn);
			g2.setColor(TBSGraphics.connectionColor);
			g2.setStroke(new BasicStroke(3));
			g2.draw(conn);
			if(model.hasArrows()){
				g2.draw(getArrowHead(conn, 0.75 * Math.PI));
				g2.draw(getArrowHead(conn, 1.25 * Math.PI));
			}
			g2.setStroke(new BasicStroke());
		}
	}
	
	public void renderOrganismNode(Graphics2D g2, OrganismNode on) {
		g2.drawImage(on.getImage(), on.getX(), on.getY() - yOffset, null);
	}
	
	public void renderStudents(Graphics2D g2){
		AdminModel adminModel = (AdminModel) model;
		int x,y;
		for(Student student : adminModel.getStudents()){
			if(student.getName().equals(adminModel.getName()))
				g2.setColor(Color.GREEN);
			else
				g2.setColor(Color.WHITE);
			x = student.getAnchorPoint().x + studentBar.getWidth();
			y = student.getAnchorPoint().y - studentYOffset;
			g2.fillRect(x, y,
					TBSGraphics.studentNodeWidth, TBSGraphics.studentNodeHeight);
			TBSGraphics.drawCenteredString(g2, student.getName(),
					x, y,
					TBSGraphics.studentNodeWidth, TBSGraphics.studentNodeHeight,
					Color.BLACK);
		}
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
	
	public Line2D scrollAdjust(Line2D l) {
		double y1 = l.getY1() - yOffset;
		double y2 = l.getY2() - yOffset;
		return new Line2D.Double(l.getX1(), y1, l.getX2(), y2);
	}

	/**
	* Set status string.
	*/
	public void setScreenString(String s) {
		screenString = s;
	}

	/**
	* Draw the statusString. 	
	*/
	public void renderScreenString(Graphics2D g2) {
		TBSButtonType buttonClicked = model.getController().getButtonClicked();
		int yStep = TBSGraphics.buttonsHeight;
        
        if(buttonClicked == null)
        	buttonClicked = TBSButtonType.TREE;
		
        Properties adminProps = model.getProperties(PropertyType.ADMIN);
		screenString = String.format(adminProps.getProperty(buttonClicked.name()), model.getName());
		
		int width = model.getApplet().getWidth();
		List<String> lines = TBSGraphics.breakStringByLineWidth(g2, screenString, width);
		int yVal = model.getApplet().getHeight() - (TBSGraphics.buttonsHeight * (lines.size()+1));
		for(String line : lines) {
			TBSGraphics.drawCenteredString(g2, line, 0, yVal, width, yStep, TBSGraphics.emptyNodeColor);
			yVal += yStep;
		}
	}
	
	public void renderTooltip(Graphics2D g2){
		int xVal = tooltipLocation.x;
        int yVal = tooltipLocation.y;
        yVal += yOffset;
        yVal -= TBSGraphics.organismNodeHeight;
        g2.setFont(TBSGraphics.tooltipFont);
        xVal -= TBSGraphics.getStringBounds(g2, tooltipString).width/2;
		TBSGraphics.drawCenteredString(g2, tooltipString, xVal, yVal, 0,
				TBSGraphics.buttonsHeight, Color.CYAN, TBSGraphics.tooltipFont);
		g2.setFont(TBSGraphics.font);
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
		g2.setFont(TBSGraphics.font);
		Prompt prompt = model.getPrompt();
		g2.setColor(Color.black);
		g2.fillRect(0, 0, model.getApplet().getWidth(), model.getApplet().getHeight());
		refreshGraphics();
		if(prompt == null){
			for(ModelElement m : model.getElements()){
				if(m instanceof Connection)
					renderModelElement(g2, m);
				else if(((Node) m).isInTree())
					renderModelElement(g2, m);
			}
		}else
			prompt.paintComponent(g2);
		renderButtons(g2);
		renderStudents(g2);
		renderScreenString(g2);
		setCursor(cursor);
		if(tooltipString != null){
			renderTooltip(g2);
			if(!timer.isRunning())
				timer.start();
		}
	}	
}
