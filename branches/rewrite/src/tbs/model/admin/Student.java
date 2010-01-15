package tbs.model.admin;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;

import tbs.TBSGraphics;

public class Student {

	private String name;
	private String lastUpdate;
	private String tree;
	private String q1;
	private String q2;
	private String q3;
	private Boolean hasArrows;
	private int height;
	private int width;
	private Point anchorPoint;
	
	public Student(Graphics2D g2, String studentDataString){
		String[] studentData = studentDataString.split("\\+=");
		name = studentData[0];
		lastUpdate = studentData[1];
		tree = studentData[2];
		q1 = studentData[3];
		q2 = studentData[4];
		q3 = studentData[5];
		String arrows = studentData[6];
		if(arrows == null || arrows == "")
			hasArrows = true;
		else
			hasArrows = Boolean.parseBoolean(arrows);
		Dimension d = TBSGraphics.getStringBounds(g2, name);
		width = d.width;
		height = d.height;
	}
	
	public String toString(){
		return name;
	}

	public String getName() {
		return name;
	}

	public String getLastUpdate() {
		return lastUpdate;
	}

	public String getTree() {
		return tree;
	}

	public String getQ1() {
		return q1;
	}

	public String getQ2() {
		return q2;
	}

	public String getQ3() {
		return q3;
	}

	public Boolean getHasArrows() {
		return hasArrows;
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
