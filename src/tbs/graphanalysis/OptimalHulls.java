package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.OrganismNode;
import tbs.view.dropdown.SubDropDown;

public class OptimalHulls extends SubDropDown {

	private String hull1;
	private String hull2;
	private int level;
	private String text;
	
	private List<Point> optimalHull1Points;
	private List<OrganismNode> removedHull1Nodes;
	private List<Point> optimalHull2Points;
	private List<OrganismNode> removedHull2Nodes;
	
	public OptimalHulls(HullCollision collision){
		removedHull1Nodes = new LinkedList<OrganismNode>();
		removedHull2Nodes = new LinkedList<OrganismNode>();
		ConvexHull h1 = collision.getHull1();
		ConvexHull h2 = collision.getHull2();
		level = collision.getLevel();
		hull1 = h1.getHullName();
		hull2 = h2.getHullName();
		List<Point> hull1Points = new LinkedList<Point>();
		hull1Points.addAll(h1.getHull());
		List<Point> hull2Points = new LinkedList<Point>();
		hull2Points.addAll(h2.getHull());
		List<OrganismNode> hull1Nodes = new LinkedList<OrganismNode>();
		hull1Nodes.addAll(h1.getNodes());
		List<OrganismNode> hull2Nodes = new LinkedList<OrganismNode>();
		hull2Nodes.addAll(h2.getNodes());
		ConvexHull tempHull1 = new ConvexHull(hull1Nodes, "");
		ConvexHull tempHull2 = new ConvexHull(hull2Nodes, "");
		Point collisionCentroid = collision.getCentroid();
		boolean collisionExists = true;
		while(collisionExists){
			int closest=0;
			boolean isH1 = true;
			double smallestDist = collisionCentroid.distance(hull1Points.get(0));
			for(int i=1;i<hull1Points.size();i++){
				double temp = collisionCentroid.distance(hull1Points.get(i));
				if(temp < smallestDist){
					smallestDist = temp;
					closest = i;
				}
			}
			for(int i=0;i<hull2Points.size();i++){
				double temp = collisionCentroid.distance(hull2Points.get(i));
				if(temp < smallestDist){
					smallestDist = temp;
					closest = i;
					isH1 = false;
				}
			}
			if(isH1){
				Point p = hull1Points.remove(closest);
				for(OrganismNode o : hull1Nodes){
					if(o.getRectangle().contains(p)){
						removedHull1Nodes.add(o);
						break;
					}
				}
				hull1Nodes.remove(removedHull1Nodes.get(removedHull1Nodes.size()-1));
				tempHull1 = new ConvexHull(hull1Nodes, "");
				hull1Points = tempHull1.getHull();
			}else{
				Point p = hull2Points.remove(closest);
				for(OrganismNode o : hull2Nodes){
					if(o.getRectangle().contains(p)){
						removedHull2Nodes.add(o);
						break;
					}
				}
				hull2Nodes.remove(removedHull2Nodes.get(removedHull2Nodes.size()-1));
				tempHull2 = new ConvexHull(hull2Nodes, "");
				hull2Points = tempHull2.getHull();
			}
			collisionExists = TBSUtils.collide(tempHull1.getHullShape(), tempHull2.getHullShape());
			if(collisionExists){
				HullCollision tempHC = new HullCollision(0, tempHull1, tempHull2);
				collisionCentroid = tempHC.getCentroid();
			}
		}
		optimalHull1Points = hull1Points;
		optimalHull2Points = hull2Points;
		StringBuffer textBuff = new StringBuffer("\"Optimal Groups\" is optimization of the current student's arrangement of organisms so that groupings no longer collide.");
		textBuff.append(" This particular optimization required the removal of ");
		textBuff.append(removedHull1Nodes.size()).append(" ").append(hull1);
		if(removedHull1Nodes.size() > 1)
			textBuff.append("s");
		textBuff.append(" and ");
		textBuff.append(removedHull2Nodes.size()).append(" ").append(hull2);
		if(removedHull2Nodes.size() > 1)
			textBuff.append("s");
		textBuff.append(" in order to eliminate group collisions.");
		text = textBuff.toString();
	}
	
	
	
	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model){
		Polygon hull1Shape = new Polygon(), hull2Shape = new Polygon();

		for(Point p : optimalHull1Points)
			hull1Shape.addPoint(p.x - xOffset, p.y - yOffset);

		for(Point p : optimalHull2Points)
			hull2Shape.addPoint(p.x - xOffset, p.y - yOffset);

		g2.setStroke(new BasicStroke(3));
		g2.setColor(model.getGroupColor(hull1));
		g2.draw(hull1Shape);
		g2.setColor(model.getGroupColor(hull2));
		g2.draw(hull2Shape);
		renderRemoved(g2, removedHull1Nodes, xOffset, yOffset);
		renderRemoved(g2, removedHull2Nodes, xOffset, yOffset);
	}
	
	public void renderRemoved(Graphics2D g2, List<OrganismNode> nodes, int xOffset, int yOffset){
		for(OrganismNode o : nodes){
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.RED);
			g2.draw(new Rectangle2D.Double(o.getX()-(1.5+xOffset), o.getY()-(1.5+yOffset), o.getWidth() + 3, o.getHeight() + 3));
			g2.setStroke(new BasicStroke());
			int xVal = (o.getX() + (o.getWidth()/2)) - xOffset;
			int yVal = (o.getY()-o.getHeight()) - yOffset;
			g2.setFont(TBSGraphics.tooltipFont);
			xVal -= TBSGraphics.getStringBounds(g2, o.getTypes().get(level)).width/2;
			TBSGraphics.drawCenteredString(g2, o.getTypes().get(level), xVal, yVal, 0,
					TBSGraphics.buttonsHeight, TBSGraphics.tooltipColor, TBSGraphics.tooltipFont);
			g2.setFont(TBSGraphics.font);
		}
	}
	
	public String toString(){return hull1 + " - " + hull2 + (getDisplay() ? " \u2713" : "");}
	public String getText(){return text;}	
}
