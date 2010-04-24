
package tbs.view.prompt.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;

import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.properties.PropertyLoader;
import tbs.view.dropdown.DropDownMenu;
import tbs.view.dropdown.DropDownRowRenderer;
import tbs.view.prompt.Prompt;

public class ColorEditorPrompt extends Prompt
{

	//Information to be used by all prompt types
	private AdminModel model;
	private Properties chooserProps;
	
	private ActionListener hider = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			cursorIsOn = !cursorIsOn;
		}
	};
	private Timer timer = new Timer(500, hider);
	private List<Integer> pressedKeys;
	private boolean cursorIsOn = true;
	private int cursorIndex;
	private int cursorWidth = 2;
	private Color offColor = Color.GRAY;
	private Color onColor = Color.BLACK;
	
	//Prompt sizing information
	private String selectedGroup;
	private List<String> headerText;
	
	private TreeMap<Integer, RGBEntryBox> rgbEntryBoxes;
	private Integer rgbSelection;
	private Rectangle customColor;
	
	private DropDownMenu<String> groupDD;
	private DropDownMenu<Color> colorDD;
	
	public ColorEditorPrompt(AdminModel model) {
		super(true, true, new Dimension(400,0), model);
		this.model = model;
		setMinimizedTitle("Group Colors");
		chooserProps = PropertyLoader.getProperties("colorchooser");
		headerText = new LinkedList<String>();
		selectedGroup = "";
		
		rgbEntryBoxes = new TreeMap<Integer, RGBEntryBox>();
		rgbEntryBoxes.put(1, new RGBEntryBox("R"));
		rgbEntryBoxes.put(2, new RGBEntryBox("G"));
		rgbEntryBoxes.put(3, new RGBEntryBox("B"));
		rgbSelection = 0;
		customColor = new Rectangle();
		
		
		pressedKeys = new LinkedList<Integer>();
		pressedKeys.add(KeyEvent.VK_DELETE);
		pressedKeys.add(KeyEvent.VK_RIGHT);
		pressedKeys.add(KeyEvent.VK_LEFT);
		DropDownRowRenderer groupRowRenderer = new DropDownRowRenderer() {
			public void renderRow(Object[] data, Rectangle row, Graphics2D g2) {
				TBSGraphics.drawCenteredString(g2, data[0].toString(), row.x, row.y,
						row.width, row.height);
			}
		};
		groupDD = new DropDownMenu<String>(model.getColorChooser().keySet(), groupRowRenderer);
		DropDownRowRenderer colorRowRenderer = new DropDownRowRenderer() {
			public void renderRow(Object[] data, Rectangle row, Graphics2D g2) {
				Rectangle temp = new Rectangle(row.x+2, row.y+2, row.width-4, row.height-4);
				g2.setColor((Color)data[0]);
				g2.fill(temp);
				g2.setColor(Color.BLACK);
			}
		};
		colorDD = new DropDownMenu<Color>(Arrays.asList(TBSGraphics.defualtGroupColors), colorRowRenderer);
		colorDD.setRenderSelection(false);
	}


	public void mousePressed(MouseEvent e) {
		cursorIsOn = false;
		rgbSelection = null;
		if(getCloseButton().contains(e.getPoint()) || getBottomButtons().contains(e.getPoint())){
			setFinished(true);
			timer.stop();
		}else{
			if(groupDD.isMouseOver(e)){
				groupDD.mousePressed(e);
				selectedGroup = groupDD.getSelection();
			}else if(colorDD.isMouseOver(e)){
				colorDD.mousePressed(e);
				if(selectedGroup != null && colorDD.getSelection() != null){
					model.getColorChooser().put(selectedGroup, colorDD.getSelection());
					colorDD.clearSelection();
				}
			}else if(customColor.contains(e.getPoint())){
				if(selectedGroup != null){
					Color color = new Color(rgbEntryBoxes.get(1).getValue(),
							rgbEntryBoxes.get(2).getValue(),
							rgbEntryBoxes.get(3).getValue());
					model.getColorChooser().put(selectedGroup, color);
				}
			}else{
				for(Map.Entry<Integer, RGBEntryBox> rgbEntryBox : rgbEntryBoxes.entrySet()){
					if(rgbEntryBox.getValue().getArea().contains(e.getPoint())){
						rgbSelection = rgbEntryBox.getKey();
						cursorIsOn = true;
						cursorIndex = 0;
						timer.start();
					}
				}
			}	
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(rgbSelection == null)
			return;
		if(!pressedKeys.contains(e.getKeyCode()))
			return;
		int len = rgbEntryBoxes.get(rgbSelection).getStringValue().length();
		if(e.getKeyCode() == KeyEvent.VK_DELETE){
			if(cursorIndex < len){
				StringBuffer temp = new StringBuffer(rgbEntryBoxes.get(rgbSelection).getStringValue());
				temp.deleteCharAt(cursorIndex);
				rgbEntryBoxes.get(rgbSelection).setStringValue(temp.toString());
			}
		}else if(e.getKeyCode() == KeyEvent.VK_LEFT){
			if(cursorIndex > 0)
				cursorIndex--;
		}else if(e.getKeyCode() == KeyEvent.VK_RIGHT){
			if(cursorIndex < len)
				cursorIndex++;
		}
		System.out.println("KeyPressed: " + cursorIndex);
	}

	public void keyTyped(KeyEvent e){
		if(rgbSelection == null)
			return;
		if(pressedKeys.contains(e.getKeyCode()))
			return;
		char c = e.getKeyChar();
		StringBuffer temp = new StringBuffer(rgbEntryBoxes.get(rgbSelection).getStringValue());
		if(c == '\b'){
			if(cursorIndex > 0){
				temp.deleteCharAt(cursorIndex-1);
				cursorIndex--;
			}
		}else{
			if(temp.length() <= 3){
				Matcher m = TBSGraphics.colorChooserPattern.matcher("" + c);
				if(m.find()){
					temp.insert(cursorIndex, c);
					cursorIndex++;
				}
			}
		}
		rgbEntryBoxes.get(rgbSelection).setStringValue(temp.toString());
	}

	public void paintComponent(Graphics2D g2) 
	{
		setGraphics(g2);
		if(getMinimizedState()){
			drawMinimized();
		}else{
			List<String> text;
			if(headerText.isEmpty()){
				text = TBSGraphics.breakStringByLineWidth(g2, chooserProps.getProperty("header"),getUnpaddedWidth());
				headerText =  text;
			}else
				text = headerText;
			
			calculateValues(headerText.size() + 12, true);
			drawBox();
			drawButtons(new String[]{"Exit"});
			drawHeader("Edit Group Color Associations");
			incrementStringY();
			drawText(text);
			incrementStringY();
			drawHeader("Select Group");
			incrementStringY();
			groupDD.setY(getStringY());
			incrementStringYMulti(2);
			TBSGraphics.drawCenteredString(g2, "Default Colors",
					getX(), getStringY(), getWidth()/2,
					TBSGraphics.textHeight + TBSGraphics.padding.height,
					TBSGraphics.selectedPromptTextColor);
			TBSGraphics.drawCenteredString(g2, "Default Colors",
					getX()+(getWidth()/2), getStringY(), getWidth()/2,
					TBSGraphics.textHeight + TBSGraphics.padding.height,
					TBSGraphics.selectedPromptTextColor);
			incrementStringY();
			colorDD.setY(getStringY());
			drawEntryBoxes();
			incrementStringYMulti(2);
			drawColorSelection();
			groupDD.render(g2, getX(), getWidth(), model.getColorChooser().keySet());
			colorDD.render(g2, getX(), getWidth()/2, new LinkedList<String>());
		}
	}
	
	public void drawEntryBoxes(){
		int labelWidth = TBSGraphics.get2DStringBounds(getGraphics(), rgbEntryBoxes.keySet()).width;
		int entryWidth = TBSGraphics.RGBEntryBoxWidth + (2 * TBSGraphics.emptyNodePadding);
		int entryHeight = TBSGraphics.textHeight + TBSGraphics.padding.height;
		int x = ((getX() + (getWidth()/2)) + (getWidth()/4)) - ((((labelWidth+8+entryWidth)*3)+20+entryWidth)/2);
		Rectangle temp = null;
		for(Map.Entry<Integer, RGBEntryBox> rgbEntryBox : rgbEntryBoxes.entrySet()){
			TBSGraphics.drawCenteredString(getGraphics(),
					rgbEntryBox.getValue().getLabel(), x, getStringY(), labelWidth+8, entryHeight);
			x += (labelWidth + 8);
			temp = new Rectangle(x, getStringY(), entryWidth, entryHeight);
			getGraphics().setColor(Color.GRAY);
			getGraphics().fill(temp);
			getGraphics().setColor(Color.BLACK);
			if(rgbEntryBox.getKey().equals(rgbSelection))
				drawEntryInProgress(temp, rgbEntryBox.getValue().getStringValue());
			else
				TBSGraphics.drawCenteredString(getGraphics(),
						rgbEntryBox.getValue().getStringValue(), temp.x,
						temp.y, temp.width, temp.height);
			getGraphics().setColor(Color.BLACK);
			getGraphics().draw(temp);
			rgbEntryBox.getValue().setArea(temp);
			x += temp.width;
		}
		TBSGraphics.drawCenteredString(getGraphics(), "=", x, temp.y, 20, temp.height + TBSGraphics.padding.height);
		customColor = new Rectangle(x+20, temp.y, TBSGraphics.textHeight + TBSGraphics.padding.height, temp.height);
		Color color = new Color(rgbEntryBoxes.get(1).getValue(),
				rgbEntryBoxes.get(2).getValue(),
				rgbEntryBoxes.get(3).getValue());
		getGraphics().setColor(color);
		getGraphics().fill(customColor);
		getGraphics().setColor(Color.BLACK);
		getGraphics().draw(customColor);
	}
	
	public void drawEntryInProgress(Rectangle rect, String entry){
		Dimension strDim = TBSGraphics.getStringBounds(getGraphics(), entry);
		int cursorHeight = strDim.height;
		boolean cursorWithinName = cursorIndex < entry.length();
		String beforeCursor = cursorWithinName ? entry.substring(0, cursorIndex) : entry;
		int cursorX = rect.x + ((rect.width/2) - (strDim.width/2));
		int cursorY = rect.y + ((rect.height/2) - (cursorHeight/2));
		if(!TBSUtils.isStringEmpty(beforeCursor)){
			strDim = TBSGraphics.getStringBounds(getGraphics(), beforeCursor);
			TBSGraphics.drawCenteredString(getGraphics(), beforeCursor, cursorX, rect.y,
					0, rect.height);
			cursorX += strDim.width+2; 
		}else
			cursorX += 2;
		if(cursorWithinName){
			String afterCursor = entry.substring(cursorIndex);
			if(!TBSUtils.isStringEmpty(afterCursor)){
				strDim = TBSGraphics.getStringBounds(getGraphics(), afterCursor);
				TBSGraphics.drawCenteredString(getGraphics(), afterCursor, cursorX + cursorWidth, rect.y,
						0, rect.height);
			}
		}
		renderCursor(getGraphics(), new Point(cursorX, cursorY), new Point(cursorWidth, cursorHeight));
	}
	
	public void drawColorSelection() {
		if(!TBSUtils.isStringEmpty(selectedGroup)){
			int groupWidth = TBSGraphics.getStringBounds(getGraphics(),selectedGroup + " = ").width + 4;
			int questionX = (getX() + getWidth()/2) - (groupWidth/2+(TBSGraphics.textHeight + TBSGraphics.padding.height/2));
			drawString(selectedGroup + " = ", questionX, getStringY());
			questionX += groupWidth;
			Rectangle colorRect = new Rectangle(questionX, getStringY(),
					TBSGraphics.textHeight + TBSGraphics.padding.height,
					TBSGraphics.textHeight + TBSGraphics.padding.height);
			getGraphics().setColor(model.getGroupColor(selectedGroup));
			getGraphics().fill(colorRect);
			getGraphics().setColor(Color.gray);
			getGraphics().draw(colorRect);
			incrementStringY();
		}
	}
	
	public void renderCursor(Graphics2D g2, Point upperLeft, Point size) {
		if(cursorIsOn) 
			g2.setColor(onColor);
		else
			g2.setColor(offColor);
		g2.fillRect(upperLeft.x, upperLeft.y, size.x, size.y);
	}
	
	/*
	 * Must leave this code for isOverButton() here because of the addition of
	 * radio question selection buttons, whereas all the other prompts only
	 * contain close button and/or bottom buttons.
	 */
	public boolean isOverButton(MouseEvent e){
		if(getBottomButtons().contains(e.getPoint()))
			return true;
		if(getCloseButton().contains(e.getPoint()))
			return true;
		if(customColor.contains(e.getPoint()))
			return true;
		for(Map.Entry<Integer, RGBEntryBox> rgbEntryBox : rgbEntryBoxes.entrySet()){
			if(rgbEntryBox.getValue().getArea().contains(e.getPoint()))
				return true;
		}
		if(colorDD.isMouseOver(e))
			return true;
		return groupDD.isMouseOver(e);
	}
	
	public class RGBEntryBox {
		
		private String label;
		private Rectangle area;
		private int value;
		private String stringValue;
		
		private RGBEntryBox(String label){
			this.label = label;
			area = new Rectangle();
			value = 0;
			stringValue = "000";
		}

		public String getLabel() {
			return label;
		}
		
		public Rectangle getArea() {
			return area;
		}

		public void setArea(Rectangle area) {
			this.area = area;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getStringValue() {
			return stringValue;
		}

		public void setStringValue(String stringValue) {
			if(!TBSUtils.isStringEmpty(stringValue)){
				this.stringValue = stringValue;
				Integer temp = null;
				try{
					temp = Integer.parseInt(stringValue);
				}catch(NumberFormatException e){}
				if(temp != null){
					if(temp < 0){
						value = 0;
						this.stringValue = "000";
					}else if(temp > 255){
						value = 255;
						this.stringValue = "255";
					}else
						value = temp;
				}
			}else{
				stringValue = "000";
				value = 0;
			}
		}
	}
}

