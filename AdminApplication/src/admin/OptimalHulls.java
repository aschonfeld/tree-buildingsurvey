package admin;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

public class OptimalHulls extends Displayable implements Renderable {

	private List<ConvexHull> hulls;
	private Point originalCentroid = null;
	private int level;
	private String commaSepGroups;
	private String text;

	private Map<String, List<Point>> optimalHullPoints;
	private Map<String, List<Vertex>> removedHullNodes;
	private Map<String, List<Vertex>> remainingHullNodes;
	private Map<String, ConvexHull> inProgressHulls;
	private Point centroidInProgress;

	private Timer iterationWait;
	private ActionListener iterate = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			iterationWait.stop();
			iterateOptimization();
			if (collisionExists)
				iterationWait.start();
		}
	};

	private List<Point> collision;
	private boolean collisionExists;
	private boolean optimizationComplete;


	public OptimalHulls(){
		removedHullNodes = new HashMap<String, List<Vertex>>();
		remainingHullNodes = new HashMap<String, List<Vertex>>();
		optimalHullPoints = new HashMap<String, List<Point>>();
		inProgressHulls = new HashMap<String, ConvexHull>();
		iterationWait = new Timer(1000, iterate);
	}
	
	public OptimalHulls(HullCollision collision) {
		this();
		level = collision.getLevel();
		hulls = collision.getHulls();
		commaSepGroups = Common.commaSeparatedString(hulls);
		if(!collision.getCentroids().isEmpty())
			originalCentroid = collision.getCentroids().get(0);
		initOptimization();
	}
	
	/**
	 * This is the default constructor to be used when there are no collisions
	 * between convex hulls in the graph, this will primarily display all hulls
	 * to demonstrate to the user that there are no collisions and these are, in
	 * actuality, "Optimal Hulls".
	 * 
	 * @param hulls, List of organism {@link ConvexHull} objects
	 */
	public OptimalHulls(List<ConvexHull> hulls) {
		this();
		level = 1;
		this.hulls = hulls;
		commaSepGroups = Common.commaSeparatedString(hulls);
		collisionExists = false;
		optimizationComplete = true;
		createText();
	}

	public void fullOptimization() {
		if (iterationWait.isRunning())
			iterationWait.stop();
		initOptimization();
		while (collisionExists)
			iterateOptimization();
		completeOptimizations();
		optimizationComplete = true;
		createText();
	}

	private void initOptimization() {
		String key;
		List<Vertex> remaining;
		List<Point> optimal;
		for (ConvexHull hull : hulls) {
			key = hull.getHullName();
			removedHullNodes.put(key, new LinkedList<Vertex>());
			remaining = new LinkedList<Vertex>();
			remaining.addAll(hull.getNodes());
			remainingHullNodes.put(key, remaining);
			optimal = new LinkedList<Point>();
			optimal.addAll(hull.getHull());
			optimalHullPoints.put(key, optimal);
			inProgressHulls.put(key, new ConvexHull(remaining, ""));
		}
		collisionExists = false;
		if(originalCentroid != null){
			centroidInProgress = originalCentroid;
			collision = new LinkedList<Point>();
			collisionExists = true;
		}
		optimizationComplete = false;
	}

	private void iterateOptimization() {
		int closest = 0;
		String closestKey = "";
		List<Point> points;
		Double smallestDist = null;
		for (Map.Entry<String, List<Point>> optimal : optimalHullPoints
				.entrySet()) {
			points = optimal.getValue();
			for (int i = 0; i < points.size(); i++) {
				Double temp = centroidInProgress.distance(points.get(i));
				if (smallestDist == null || temp < smallestDist) {
					smallestDist = temp;
					closest = i;
					closestKey = optimal.getKey();
				}
			}
		}
		Point p = optimalHullPoints.get(closestKey).remove(closest);
		for (Vertex v : remainingHullNodes.get(closestKey)) {
			if (v.getRectangle().contains(p)) {
				removedHullNodes.get(closestKey).add(v);
				break;
			}
		}
		remainingHullNodes.get(closestKey).remove(
				removedHullNodes.get(closestKey).get(
						removedHullNodes.get(closestKey).size() - 1));
		inProgressHulls.put(closestKey, new ConvexHull(remainingHullNodes
				.get(closestKey), ""));
		optimalHullPoints.put(closestKey, inProgressHulls.get(closestKey)
				.getHull());

		List<ConvexHull> temp = new LinkedList<ConvexHull>();
		temp.addAll(inProgressHulls.values());
		collisionExists = Common.collide(temp);
		if (collisionExists) {
			HullCollision tempHC = new HullCollision(0, temp);
			centroidInProgress = tempHC.getCentroids().get(0);
			collision = tempHC.getUnion();
		}
	}
	
	private void completeOptimizations(){
		Map<String, List<Point>> smallHulls = new HashMap<String, List<Point>>();
		List<Polygon> largeHulls = new LinkedList<Polygon>();
		for(Map.Entry<String, List<Point>> e : optimalHullPoints.entrySet()){
			if(e.getValue().size() < 3)
				smallHulls.put(e.getKey(), e.getValue());
			else{
				Polygon lhPoly = new Polygon();
				for(Point lhP : e.getValue())
					lhPoly.addPoint(lhP.x, lhP.y);
				largeHulls.add(lhPoly);
			}
		}
		Map<String, List<Integer>> pointsToRemove = new HashMap<String, List<Integer>>();
		int currentIndex = 0;
		for(Map.Entry<String, List<Point>> e : smallHulls.entrySet()){
			boolean remove;
			currentIndex = 0;
			for(Point shP : e.getValue()){
				remove = false;
				for(Polygon largeHull : largeHulls){
					if(largeHull.contains(shP)){
						remove = true;
						break;
					}
				}
				if(remove){
					if(pointsToRemove.containsKey(e.getKey()))
						pointsToRemove.get(e.getKey()).add(currentIndex);
					else{
						List<Integer> tempPTR = new LinkedList<Integer>();
						tempPTR.add(currentIndex);
						pointsToRemove.put(e.getKey(), tempPTR);
					}
					continue;
				}
				currentIndex++;
			}
		}
		Point p;
		for(Map.Entry<String, List<Integer>> e : pointsToRemove.entrySet()){
			for(Integer pointToRemove : e.getValue()){
				p = optimalHullPoints.get(e.getKey()).remove((int) pointToRemove);
				for (Vertex v : remainingHullNodes.get(e.getKey())) {
					if (v.getRectangle().contains(p)) {
						removedHullNodes.get(e.getKey()).add(v);
						break;
					}
				}
				remainingHullNodes.get(e.getKey()).remove(
						removedHullNodes.get(e.getKey()).get(
								removedHullNodes.get(e.getKey()).size() - 1));
				inProgressHulls.put(e.getKey(), new ConvexHull(remainingHullNodes
						.get(e.getKey()), ""));
				optimalHullPoints.put(e.getKey(), inProgressHulls.get(e.getKey())
						.getHull());
			}
		}
	}

	private void createText() {
		StringBuffer textBuff = new StringBuffer(
				"\"Optimal Groups\" is optimization of the current student's arrangement of organisms so that groupings no longer collide.");
		textBuff
				.append(" This particular optimization required the removal of ");
		String sep = "";
		if(!removedHullNodes.isEmpty()){
			for (Map.Entry<String, List<Vertex>> removed : removedHullNodes
					.entrySet()) {
				textBuff.append(sep).append(removed.getValue().size()).append(" ")
				.append(removed.getKey());
				if (removed.getValue().size() > 1)
					textBuff.append("s");
				sep = ", ";
			}
		}else{
			for(ConvexHull ch : hulls){
				textBuff.append(sep).append("0 ").append(ch.getHullName()).append("s");
				sep = ", ";
			}
		}
		textBuff.append(" in order to eliminate group collisions.");
		text = textBuff.toString();
	}

	public void render(Graphics g, Point offset) {
		Graphics2D g2 = (Graphics2D) g;
		if (collisionExists) {
			g2.setColor(new Color(255, 36, 0, 160));
			Polygon collisionShape = new Polygon();
			for (Point p : collision)
				collisionShape.addPoint(p.x - offset.x, p.y - offset.y);
			g2.fill(collisionShape);
		}else{
			if(!optimizationComplete){
					completeOptimizations();
					optimizationComplete = true;
			}
		}
		if(optimizationComplete && removedHullNodes.isEmpty()){
			for(ConvexHull ch : hulls)
				ch.render(g2, offset);
		}else{
			Map<String, Polygon> hullShapes = new HashMap<String, Polygon>();
			for (Map.Entry<String, List<Point>> optimal : optimalHullPoints
					.entrySet()) {
				Polygon shape = new Polygon();
				for (Point p : optimal.getValue())
					shape.addPoint(p.x - offset.x, p.y - offset.y);
				hullShapes.put(optimal.getKey(), shape);
			}
			g2.setStroke(new BasicStroke(3));
			for (Map.Entry<String, Polygon> hullShape : hullShapes.entrySet()) {
				g2.setColor(AdminApplication.getGroupColor(hullShape.getKey()));
				g2.draw(hullShape.getValue());
			}
			g2.setStroke(new BasicStroke());
			for (Map.Entry<String, List<Vertex>> removed : removedHullNodes
					.entrySet())
				renderRemoved(g2, removed.getValue(), offset);
		}
	}

	public void renderRemoved(Graphics2D g2, List<Vertex> nodes, Point offset) {
		Rectangle bounds;
		for (Vertex v : nodes) {
			bounds = v.getRectangle();
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.RED);
			g2.draw(new Rectangle2D.Double(bounds.x - (1.5 + offset.x),
					bounds.y - (1.5 + offset.y), bounds.width + 3,
					bounds.height + 3));
			g2.setStroke(new BasicStroke());
			if (!AdminApplication.showNames) {
				int xVal = (bounds.x - offset.x)
						+ (v.getInfo().getImage().getWidth() / 2);
				int yVal = bounds.y - (offset.y + Common.ySpacing);
				g2.setFont(Common.tooltipFont);
				xVal -= Common.getStringBounds(g2, v.getInfo().getTypes().get(
						level)).width / 2;
				Common.drawCenteredString(g2,
						v.getInfo().getTypes().get(level), xVal, yVal, 0, 0,
						Common.tooltipColor, Common.tooltipFont);
				g2.setFont(Common.font);
			}
		}
	}

	public String toString() {
		return commaSepGroups + (getDisplay() ? " \u2713" : "");
	}

	public String getText() {
		return text;
	}

	public void startOptimization() {
		initOptimization();
		if(collisionExists)
			iterationWait.start();
	}
}
