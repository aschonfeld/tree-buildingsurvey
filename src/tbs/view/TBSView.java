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
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.Connection;
import tbs.model.EmptyNode;
import tbs.model.ModelElement;
import tbs.model.ModelUtils;
import tbs.model.Node;
import tbs.model.OrganismNode;
import tbs.model.TBSModel;
import tbs.view.prompt.Prompt;

public abstract class TBSView extends JComponent implements Printable{

	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = -5734020097117125930L;

	private TBSModel model;
	private JScrollBar verticalBar;
	private int yOffset = 0; // start of viewable tree area
	private JScrollBar horizontalBar;
	private int xOffset = 0;
	private List<TBSButtonType> buttons;
	private Cursor cursor;

	//Tooltip information
	private Boolean displayAllTooltips;
	private Boolean screenPrintMode;
	private String tooltipString;
	private Point tooltipLocation;
	private Timer timer;
	private ActionListener hider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			timer.stop();
			tooltipString = null;
		}
	};

	public TBSView(boolean admin, TBSModel model){
		this.model = model;
		buttons = TBSButtonType.getButtons(admin);
		setLayout(new BorderLayout());
		verticalBar = new JScrollBar(JScrollBar.VERTICAL, 0, 100, 0, 200);
		add(verticalBar, BorderLayout.EAST);
		horizontalBar = new JScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
		horizontalBar.setVisible(false);
		add(horizontalBar, BorderLayout.SOUTH);
		timer = new Timer(1000, hider);
		displayAllTooltips = false;
		cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
		screenPrintMode = false;
	}
	
	public Cursor getAppletCursor(){
		return cursor;
	}
	
	public void setAppletCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public List<TBSButtonType> getButtons() {return buttons;}

	public JScrollBar getVerticalBar() {
		return verticalBar;
	}

	public int getYOffset() {
		return screenPrintMode ? 0 : yOffset;
	}

	// sets the start of viewable tree area
	public void setYOffset(int yo) {
		yOffset = yo;
	}
	
	public JScrollBar getHorizontalBar() {
		return horizontalBar;
	}

	public int getXOffset() {
		return screenPrintMode ? 0 : xOffset;
	}

	// sets the start of viewable tree area
	public void setXOffset(int xo) {
		xOffset = xo;
	}

	public void updateTooltip(String name, Point location){
		tooltipString = name;
		tooltipLocation = location;
	}

	public boolean isTooltipRunning(){
		return timer.isRunning();
	}
	
	public Boolean getDisplayAllTooltips() {
		return displayAllTooltips;
	}

	public void setDisplayAllTooltips(Boolean displayAllTooltips) {
		this.displayAllTooltips = displayAllTooltips;
	}
	
	public void toggleDisplayAllTooltips(){
		this.displayAllTooltips = !displayAllTooltips;
	}
	
	public Boolean getScreenPrintMode(){
		return screenPrintMode;
	}

	/**
	 * Redraw the screen.
	 */
	public void refreshGraphics() {
		repaint();  
	}
	
	public abstract void renderButtons(Graphics2D g2);
	
	public abstract void renderStudents(Graphics2D g2);
	
	public abstract void renderElements(Graphics2D g2);
	
	public abstract void renderScreenString(Graphics2D g2);
	
	/**
	 * draws a modelElement
	 */
	public void renderUnselectedModelElements(Graphics2D g2) {
		for(ModelElement me : model.getElements()){
			if(me instanceof OrganismNode){
				renderOrganismNodeInfo(g2, (OrganismNode) me);
				if(!((Node) me).isBeingDragged() &&  me.isInTree())
					renderOrganismNode(g2, (OrganismNode) me);
			}else if (me instanceof EmptyNode){
				if(me.isInTree())
					renderEmptyNode(g2, (EmptyNode) me);
			}else{
				Connection c = (Connection) me;
				if (!c.getFrom().collidesWith(c.getTo()))
					renderConnection(g2, TBSUtils.getConnectionBounds(c.getFrom(), c.getTo()), TBSGraphics.connectionColor);
			}
		}
	}

	public void renderOrganismNode(Graphics2D g2, OrganismNode on) {
		g2.drawImage(on.getImage(), on.getX() - getXOffset(), on.getY() - getYOffset(), null);
		if(displayAllTooltips){
			int xVal = (on.getX() + (on.getWidth()/2)) - getXOffset();
			int yVal = (on.getY()-on.getHeight()) - getYOffset();
			g2.setFont(TBSGraphics.tooltipFont);
			xVal -= TBSGraphics.getStringBounds(g2, on.getName()).width/2;
			TBSGraphics.drawCenteredString(g2, on.getName(), xVal, yVal, 0,
					TBSGraphics.buttonsHeight, TBSGraphics.tooltipColor, TBSGraphics.tooltipFont);
			g2.setFont(TBSGraphics.font);
		}
	}
	
	public void renderOrganismNodeInfo(Graphics2D g2, OrganismNode on) {
		Color stringColor = on.isInTree() || on.isBeingDragged() ? TBSGraphics.organismBoxColor : TBSGraphics.organismStringColor;
		g2.setColor(on.isInTree() || on.isBeingDragged() ? TBSGraphics.organismStringColor : TBSGraphics.organismBoxColor);
		g2.fillRect(on.getDefaultPoint().x, on.getDefaultPoint().y, on.getWidth(), on.getHeight());
		TBSGraphics.drawCenteredString(g2, on.getName(), on.getStringAreaLeftX(), on.getDefaultPoint().y, on.getStringWidth(), TBSGraphics.organismNodeHeight, stringColor);
		g2.drawImage(on.getImage(), on.getImageStartX(), on.getDefaultPoint().y, null);
	}
	
	public void renderEmptyNode(Graphics2D g2, EmptyNode en){
		if(!en.isBeingLabeled()){
			g2.setColor(TBSGraphics.emptyNodeColor);
			g2.fill(new Rectangle(en.getX() - getXOffset(), en.getY() - getYOffset(), en.getWidth(), en.getHeight()));
			TBSGraphics.drawCenteredString(g2, en.getName(), en.getX() - getXOffset(),
					en.getY() - getYOffset(), en.getWidth(), en.getHeight());
		}
	}

	
	public void renderSelectedModelElements(Graphics2D g2){
		ModelElement selected = model.getSelectedElement();
		if(selected != null){
			g2.setStroke(new BasicStroke(3));
			if(selected instanceof Node)
				renderSelectedNode(g2, (Node) selected);
			else{
				for(Connection c : ModelUtils.getConnectionsByNodes(((Connection) selected).getFrom(), ((Connection) selected).getTo(), model))
					renderConnection(g2, TBSUtils.getConnectionBounds(c.getFrom(), c.getTo()), TBSGraphics.connectionSelectedColor);	
			}
			g2.setStroke(new BasicStroke());
		}
		
	}
	
	public void renderSelectedNode(Graphics2D g2, Node n){
		if(n.isBeingLabeled() || (!n.isInTree() && !n.isBeingDragged()))
			return; // do not draw green box around node being labeled or in reservoir
		g2.setColor(TBSGraphics.selectedNodeBorderColor);
		g2.draw(new Rectangle2D.Double(n.getX()-(1.5+getXOffset()), n.getY()-(1.5+getYOffset()), n.getWidth() + 3, n.getHeight() + 3));
		if(n.isBeingDragged()){
			if(n instanceof OrganismNode)
				g2.drawImage(((OrganismNode) n).getImage(), n.getX()-getXOffset(), n.getY()-getYOffset(), null);
			else
				renderEmptyNode(g2, (EmptyNode) n);	
		}
	}
	
	public void renderConnection(Graphics2D g2, Line2D line, Color color){
		Line2D temp = new Line2D.Double(line.getX1() - getXOffset(), line.getY1() - getYOffset(), line.getX2() - getXOffset(), line.getY2() - getYOffset());
		g2.setStroke(new BasicStroke(3));
		g2.setColor(color);
		g2.draw(temp);
		if(model.getStudent().hasArrows()){
			g2.draw(TBSUtils.getArrowHead(temp, 0.75 * Math.PI));
			g2.draw(TBSUtils.getArrowHead(temp, 1.25 * Math.PI));
		}
		g2.setStroke(new BasicStroke());
	}
	
	public void renderTooltip(Graphics2D g2){
		if(!displayAllTooltips && tooltipString != null){
			int xVal = tooltipLocation.x;
			xVal -= getXOffset();
			int yVal = tooltipLocation.y;
			yVal -= getYOffset();
			g2.setFont(TBSGraphics.tooltipFont);
			xVal -= TBSGraphics.getStringBounds(g2, tooltipString).width/2;
			TBSGraphics.drawCenteredString(g2, tooltipString, xVal, yVal, 0,
					TBSGraphics.buttonsHeight, TBSGraphics.tooltipColor, TBSGraphics.tooltipFont);
			g2.setFont(TBSGraphics.font);
			if(!timer.isRunning())
				timer.start();
		}
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
	public void paintComponent(Graphics g){
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
		if(prompt != null){
			if(prompt.renderButtonsAndString()){
				renderButtons(g2);
				renderScreenString(g2);
			}
			if(prompt.renderElements())
				renderElements(g2);
			prompt.paintComponent(g2);
		}else{
			renderScreenString(g2);
			renderElements(g2);
			renderButtons(g2);
			renderStudents(g2);
		}
		setCursor(getAppletCursor());
	}

	public int print(Graphics g, PageFormat pageFormat, int pageIndex)
	throws PrinterException {
		if (pageIndex > 0) {
			return(NO_SUCH_PAGE);
		} else {
			// make pic
			int previousHeight = getHeight();
			int previousWidth = getWidth();
			int width = getWidth();
			if(horizontalBar.isVisible())
				width *= horizontalBar.getMaximum()/horizontalBar.getVisibleAmount();
			int height = getHeight() * (verticalBar.getMaximum()/verticalBar.getVisibleAmount());
			BufferedImage fullSizeImage = new BufferedImage(width, height, 
					BufferedImage.TYPE_INT_RGB);
			TBSGraphics.setColorsForPrinting();
			screenPrintMode = true;
			horizontalBar.setVisible(false);
			verticalBar.setVisible(false);
			setSize(width, height);
			paint(fullSizeImage.getGraphics());

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
			TBSGraphics.setColorsForDisplay();
			setSize(previousWidth, previousHeight);
			verticalBar.setVisible(true);
			screenPrintMode = false;
			return(PAGE_EXISTS);
		}
	}
}
