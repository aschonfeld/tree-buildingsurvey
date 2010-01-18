package tbs.model.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import tbs.TBSGraphics;
import tbs.view.OpenQuestionButtonType;

public class Student {

	private String name;
	private String lastUpdate;
	private Date lastUpdateTimestamp;
	private SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, d MMMM yyyy h:mm a");
	private String tree;
	private Map<OpenQuestionButtonType, Response> openResponses;
	private Boolean arrows;
	private int height;
	private int width;
	private Point anchorPoint;
	
	public Student(Graphics2D g2, String studentDataString){
		openResponses = new HashMap<OpenQuestionButtonType, Response>();
		if(studentDataString == null || studentDataString == ""){
			lastUpdate = "";
			tree = "";
			arrows = true;
			name = "";
			openResponses.put(OpenQuestionButtonType.ONE, new WrittenResponse(""));
			openResponses.put(OpenQuestionButtonType.TWO, new WrittenResponse(""));
			openResponses.put(OpenQuestionButtonType.THREE, new RadioResponse(""));
			return;
		}
		String[] studentData = studentDataString.split("\\+=");
		setName(studentData[0]);
		setLastUpdate(studentData[1]);
		tree = studentData[2];
		openResponses.put(OpenQuestionButtonType.ONE, new WrittenResponse(studentData[3]));
		openResponses.put(OpenQuestionButtonType.TWO, new WrittenResponse(studentData[4]));
		openResponses.put(OpenQuestionButtonType.THREE, new RadioResponse(studentData[5]));
		String arrowsString = studentData[6];
		if(arrowsString == null || arrowsString == "")
			arrows = true;
		else
			arrows = Boolean.parseBoolean(arrowsString);
		Dimension d = TBSGraphics.getStringBounds(g2, name);
		width = d.width;
		height = d.height;
	}
	
	public String toString(){
		return name;
	}
	
	public void setName(String nameInput){
		if(nameInput != null && nameInput != ""){
			String[] splitName = nameInput.split(",");
			StringBuffer nameBuffer = new StringBuffer();
			for(int i=(splitName.length-1);i>=0;i--)
				nameBuffer.append(splitName[i]).append(" ");
			name = nameBuffer.toString().trim();
		}else
			name = "";
	}

	public String getName() {
		return name;
	}

	public String getLastUpdate() {
		if(lastUpdate == null || lastUpdate == "")
			return lastUpdate;
		return displayFormat.format(lastUpdateTimestamp);
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
		if(lastUpdate != null && lastUpdate != ""){
			try {
				lastUpdateTimestamp = parseFormat.parse(lastUpdate);
			} catch (ParseException e) {
				System.out.println("Student:setLastUpdate:Error parsing date string(" + lastUpdate +")");
			}
		}
	}
	public String getTree() {
		return tree;
	}

	public Response getResponse(OpenQuestionButtonType responseType) {
		return openResponses.get(responseType);
	}
	
	public Map<OpenQuestionButtonType, Response> getResponses(){
		return openResponses;
	}

	public Boolean hasArrows() {
		return arrows;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Point getAnchorPoint() {
		return anchorPoint;
	}

	public void setAnchorPoint(Point anchorPoint) {
		this.anchorPoint = anchorPoint;
	}
	
}
