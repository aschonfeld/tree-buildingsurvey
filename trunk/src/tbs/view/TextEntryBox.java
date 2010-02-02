
package tbs.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.EmptyNode;

public class TextEntryBox {

	private Timer timer;
	private ActionListener hider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cursorIsOn = !cursorIsOn;
		}
	};
	private EmptyNode node;
	private Graphics2D g2 = null;
	private String name;
	private int cursorIndex;
	private int leftX;
	private int width;
	private int height;
	private Color offColor = Color.white;
	private Color onColor = Color.darkGray;
	private Color stringColor = Color.black;
	private Color backgroundColor = Color.white;
	private Color borderColor = Color.yellow;
	private int cursorWidth = 2;
	private boolean cursorIsOn = true;
	private List<Integer> pressedKeys;
	
	public TextEntryBox(EmptyNode node) {
		this.node = node;
		name= node.getName();
		if(TBSUtils.isStringEmpty(name))
			cursorIndex = 0;
		else
			cursorIndex = name.length();	
		leftX = node.getX();
		width = node.getWidth();
		height = node.getHeight();
		pressedKeys = new LinkedList<Integer>();
		pressedKeys.add(KeyEvent.VK_DELETE);
		pressedKeys.add(KeyEvent.VK_RIGHT);
		pressedKeys.add(KeyEvent.VK_LEFT);
		timer = new Timer(500, hider);
 		timer.start();
	}
	
	public void keyPressed(KeyEvent e) {
		if(!pressedKeys.contains(e.getKeyCode()))
			return;
		int len = name.length();
		if(e.getKeyCode() == KeyEvent.VK_DELETE){
			if(cursorIndex < len){
				StringBuffer temp = new StringBuffer(name);
				temp.deleteCharAt(cursorIndex);
				name = temp.toString();
			}
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			if(cursorIndex > 0)
				cursorIndex--;
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			if(cursorIndex < len)
				cursorIndex++;
		}
			
	}
	
	public void keyTyped(KeyEvent e){
		if(pressedKeys.contains(e.getKeyCode()))
			return;
		char c = e.getKeyChar();
		StringBuffer temp = new StringBuffer(name);
		if(c == '\b'){
			if(cursorIndex > 0){
				temp.deleteCharAt(cursorIndex-1);
				cursorIndex--;
			}
		}else{
			Matcher m = TBSGraphics.emptyNodePattern.matcher("" + c);
			if(m.find()){
				temp.insert(cursorIndex, c);
				cursorIndex++;
			}
		}
		name = temp.toString();
	}
	
	public void finishLabeling(){
		if(name.length()==0 || name.length() > TBSGraphics.maxNameLength)
			node.setAlteredWidth(-1);
		else{
			Dimension stringBounds = TBSGraphics.getStringBounds(g2, name);
			int testWidth = stringBounds.width + 2 * TBSGraphics.emptyNodePadding;
			if (testWidth > TBSGraphics.emptyNodeWidth)
				node.setAlteredWidth(testWidth);
			else
				node.setAlteredWidth(-1);
			node.setName(name);
		}
		node.setBeingLabeled(false);
		timer.stop();
	}
	
	public void renderCursor(Graphics2D g2, Point upperLeft, Point size) {
		if(cursorIsOn) 
			g2.setColor(onColor);
		else
			g2.setColor(offColor);
		g2.fillRect(upperLeft.x, upperLeft.y, size.x, size.y);
	}
	
	public void renderTextEntryBox(Graphics2D g2, int yOffset) 
	{
		this.g2 = g2;
		int upperY = node.getY() - yOffset;
		Dimension d;
		TextLayout layout;
		if(!TBSUtils.isStringEmpty(name)){
			layout = new TextLayout(name, g2.getFont(), g2.getFontRenderContext());
			Rectangle2D bounds = layout.getBounds();
			d = new Dimension((int) bounds.getWidth(), (int) bounds.getHeight());
			width = d.width + 2 * TBSGraphics.emptyNodePadding;
			if(width < TBSGraphics.emptyNodeWidth)
				width = TBSGraphics.emptyNodeWidth;
		}else{
			g2.setColor(backgroundColor);
			g2.fillRect(leftX, upperY, width, height);
			renderCursor(g2, new Point(leftX, upperY), new Point(cursorWidth, height));
			g2.setColor(borderColor);
			g2.drawRect(leftX - 1, upperY, width, height);
			return;
		}
			
		int x, y;
		// draw background
		g2.setColor(backgroundColor);
		g2.fillRect(leftX, upperY, width, height);
		// draw string starts here
		g2.setColor(stringColor);
		// calculate dimensions of String s
		x = leftX + TBSGraphics.emptyNodePadding;
		y = upperY + height - TBSGraphics.emptyNodePadding;
		boolean cursorWithinName = cursorIndex < name.length();
		String beforeCursor = cursorWithinName ? name.substring(0, cursorIndex) : name;
		int cursorX = x;
		if(!TBSUtils.isStringEmpty(beforeCursor)){
			layout = new TextLayout(beforeCursor, g2.getFont(), g2.getFontRenderContext());
			layout.draw(g2, x, y);
			cursorX += ((int) layout.getBounds().getWidth() + 2);
		}
		else
			cursorX += 2;
		if(cursorWithinName){
			String afterCursor = name.substring(cursorIndex);
			if(!TBSUtils.isStringEmpty(afterCursor)){
				layout = new TextLayout(afterCursor, g2.getFont(), g2.getFontRenderContext());
				layout.draw(g2, cursorX + cursorWidth, y);
			}
		}
		
		renderCursor(g2, new Point(cursorX, upperY), new Point(cursorWidth, height));
		
		g2.setColor(borderColor);
		int boxWidth = (x + d.width + 2 + cursorWidth) - leftX;
		if(width > boxWidth)
			boxWidth = width;
		g2.drawRect(leftX, upperY, boxWidth, height);
	}
	
}
