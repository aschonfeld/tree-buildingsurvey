package tbs.model.admin;

import java.awt.Point;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.view.OpenQuestionButtonType;

public class Student {

	private int index;
	private String name;
	private String databaseName;
	private List<String> nodeName;
	private String lastUpdate;
	private Date lastUpdateTimestamp;
	private SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat displayFormat = new SimpleDateFormat("EEE, d MMMM yyyy");
	private String tree;
	private Map<OpenQuestionButtonType, Response> openResponses;
	private String section;
	private Boolean arrows;
	private Point anchorPoint;

	public Student(String studentDataString, int index){
		openResponses = new HashMap<OpenQuestionButtonType, Response>();
		if(TBSUtils.isStringEmpty(studentDataString)){
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
		setTree(studentData[2].substring(1).trim());
		int splitIndex = 3;
		for(OpenQuestionButtonType response : OpenQuestionButtonType.values()){
			if(response.isRadio())
				openResponses.put(response, new RadioResponse(studentData[splitIndex].substring(1).trim(),response.getRadioQuestionCount()));
			else
				openResponses.put(response, new WrittenResponse(studentData[splitIndex].substring(1).trim()));
			splitIndex++;
		}
		section = studentData[6].substring(1).trim();
		if(TBSUtils.isStringEmpty(section))
			arrows = true;
		else{
			String[] sectionSplit = section.split(" ");
			if(sectionSplit.length < 2)
				arrows = true;
			else{
				try{
					int sectionNum = Integer.parseInt(sectionSplit[1]);
					if(sectionNum%2 == 0)
						arrows = true;
					else
						arrows = false;
				}catch(NumberFormatException e){
					System.out.println("Error parsing section number (" + section + ") defaulting arrows to true");
					arrows = true;
				}
			}
		}
		this.index = index;
		nodeName = new LinkedList<String>();
	}
	
	public Student(String[] studentData, int index){
		openResponses = new HashMap<OpenQuestionButtonType, Response>();
		if(studentData == null || studentData.length == 0){
			createNewStudent();
			return;
		}
		databaseName = studentData[0];
		setName(studentData[0]);
		setLastUpdate(studentData[1]);
		setTree(studentData[2]);
		int splitIndex = 3;
		for(OpenQuestionButtonType response : OpenQuestionButtonType.values()){
			if(response.isRadio())
				openResponses.put(response, new RadioResponse(studentData[splitIndex],response.getRadioQuestionCount()));
			else
				openResponses.put(response, new WrittenResponse(studentData[splitIndex]));
			splitIndex++;
		}
		section = studentData[6];
		if(TBSUtils.isStringEmpty(section))
			arrows = true;
		else{
			String[] sectionSplit = section.split(" ");
			if(sectionSplit.length < 2)
				arrows = true;
			else{
				try{
					int sectionNum = Integer.parseInt(sectionSplit[1]);
					if(sectionNum%2 == 0)
						arrows = true;
					else
						arrows = false;
				}catch(NumberFormatException e){
					System.out.println("Error parsing section number (" + section + ") defaulting arrows to true");
					arrows = true;
				}
			}
		}
		this.index = index;
		nodeName = new LinkedList<String>();
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
	
	public String getDatabaseName() {
		return databaseName;
	}

	public String getLastUpdate() {
		if(!TBSUtils.isStringEmpty(lastUpdate) && lastUpdateTimestamp != null)
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

	public void setTree(String treeInput){
		if(TBSUtils.isStringEmpty(treeInput)){
			tree = "";
			return;
		}
		//This code eliminates duplicate records caused from previous problematic code
		List<String> parsedElements = new LinkedList<String>();
		StringBuffer adjTreeString = new StringBuffer();
		for(String element : treeInput.split("#")){
			if(!parsedElements.contains(element)){
				parsedElements.add(element);
				adjTreeString.append(element).append("#");
			}
		}
		tree = adjTreeString.toString();
	}

	public Response getResponse(OpenQuestionButtonType responseType) {
		return openResponses.get(responseType);
	}

	public Map<OpenQuestionButtonType, Response> getResponses(){
		return openResponses;
	}

	public String getSection() {
		return section;
	}

	public Boolean hasArrows() {
		return arrows;
	}

	public Point getAnchorPoint() {
		if(anchorPoint == null)
			anchorPoint = new Point(0,index * (TBSGraphics.studentNodeHeight + TBSGraphics.ySpacing));
		return anchorPoint;
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

	public List<String> getNodeName() {
		return nodeName;
	}
	
	public void setNodeName(List<String> nodeName){
		this.nodeName = nodeName;
	}

}
