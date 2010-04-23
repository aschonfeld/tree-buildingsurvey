package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.util.Comparator;
import java.util.List;

import tbs.model.AdminModel;

public class HullCollision {

	private int level;
	private String hull1;
	private List<Point> hull1Points;
	private String hull2;
	private List<Point> hull2Points;
	private String analysisText;
	private Boolean displayCollision;
	
	public static Comparator<Point> pointComparator = new Comparator<Point>() {
		public int compare( Point p1, Point p2 ) {
			return (new Double(Math.abs(Math.atan2(p1.y, p1.x)))).compareTo(new Double(Math.abs(Math.atan2(p2.y, p2.x))));
		}
	};
	
	public HullCollision(int level, ConvexHull hull1, ConvexHull hull2){
		this.level = level;
		this.hull1 = hull1.getHullName();
		this.hull1Points = hull1.getHull();
		this.hull2 = hull2.getHullName();
		this.hull2Points = hull2.getHull();
		analysisText = new StringBuffer(" \u2022 ").append(hull1)
		.append(" group collides with the ")
		.append(hull2).append(" group.").toString();
		displayCollision = false;
	}
	
	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model){
		Polygon hull1Shape = new Polygon(), hull2Shape = new Polygon();
		
		for(Point p : hull1Points)
			hull1Shape.addPoint(p.x - xOffset, p.y - yOffset);
		
		for(Point p : hull2Points)
			hull2Shape.addPoint(p.x - xOffset, p.y - yOffset);
		
		Area intersect = new Area(hull1Shape); 
		intersect.intersect(new Area(hull2Shape)); 
		g2.setColor(Color.RED);
		g2.fill(intersect);
		g2.setStroke(new BasicStroke(3));
		g2.setColor(model.getGroupColor(hull1));
		g2.draw(hull1Shape);
		g2.setColor(model.getGroupColor(hull2));
		g2.draw(hull2Shape);
		g2.setStroke(new BasicStroke());
	}
	
	public String getAnalysisText(){
		return analysisText;
	}
	
	public Boolean getDisplayCollision() {return displayCollision;}
	public void setDisplayCollision(Boolean displayCollision) {this.displayCollision = displayCollision;}
	
	public void toggleCollision(){this.displayCollision = !displayCollision;}
	public String toString(){return hull1 + " - " + hull2 + (displayCollision ? " \u2713" : "");}
	
}
