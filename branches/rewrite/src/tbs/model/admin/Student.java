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
			createNewStudent();
			return;
		}
		String[] studentData = studentDataString.split("\\+");
		if(studentData == null || studentData.length == 0){
			createNewStudent();
			return;
		}
		setName(studentData[0].trim());
		setLastUpdate(studentData[1].substring(1).trim());
		tree = studentData[2].substring(1).trim();
		int splitIndex = 3;
		for(OpenQuestionButtonType response : OpenQuestionButtonType.values()){
			if(response.isRadio())
				openResponses.put(response, new RadioResponse(studentData[splitIndex].substring(1).trim(),response.getRadioQuestionCount()));
			else
				openResponses.put(response, new WrittenResponse(studentData[splitIndex].substring(1).trim()));
			splitIndex++;
		}
		String arrowsString = studentData[6].substring(1).trim();
		arrowsString = arrowsString.trim();
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
		if(nameInput != null && nameInput.length() > 0){
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
		if(lastUpdate != null && lastUpdate.length() != 0 && lastUpdateTimestamp != null)
			return displayFormat.format(lastUpdateTimestamp);
		return lastUpdate;
	}

	public void setLastUpdate(String lastUpdate) {
		this.lastUpdate = lastUpdate;
		if(lastUpdate != null && lastUpdate.length() > 0){
			try {
				lastUpdateTimestamp = parseFormat.parse(lastUpdate);
			} catch (ParseException e) {
				System.out.println("Student:setLastUpdate:Error parsing date string(" + lastUpdate +")");
				lastUpdateTimestamp = null;
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
	
	private void createNewStudent(){
		lastUpdate = "";
		tree = "";
		arrows = true;
		name = "";
		for(OpenQuestionButtonType button : OpenQuestionButtonType.values()){
			if(button.isRadio())
				openResponses.put(button, new RadioResponse("", button.getRadioQuestionCount()));
			else
				openResponses.put(button, new WrittenResponse(""));
		}
	}
	
}
