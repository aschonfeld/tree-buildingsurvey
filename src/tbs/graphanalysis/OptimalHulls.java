package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.OrganismNode;
import tbs.properties.PropertyLoader;
import tbs.view.dropdown.SubDropDown;

public class OptimalHulls extends SubDropDown {

	private List<ConvexHull> hulls;
	private Point originalCentroid = null;
	private int level;
	private String commaSepGroups;
	private String text;

	private Map<String, List<Point>> optimalHullPoints;
	private Map<String, List<OrganismNode>> removedHullNodes;
	private Map<String, List<OrganismNode>> remainingHullNodes;
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
	
	private Properties adminProps;

	public OptimalHulls(){
		removedHullNodes = new HashMap<String, List<OrganismNode>>();
		remainingHullNodes = new HashMap<String, List<OrganismNode>>();
		optimalHullPoints = new HashMap<String, List<Point>>();
		inProgressHulls = new HashMap<String, ConvexHull>();
		iterationWait = new Timer(1000, iterate);
		adminProps = PropertyLoader.getProperties("admin");
	}
	
	public OptimalHulls(HullCollision collision) {
		this();
		level = collision.getLevel();
		hulls = collision.getHulls();
		commaSepGroups = TBSUtils.commaSeparatedString(hulls);
		if(!collision.getCentroids().isEmpty())
			originalCentroid = collision.getCentroids().get(0);
		fullOptimization();
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
		commaSepGroups = TBSUtils.commaSeparatedString(hulls);
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
		List<OrganismNode> remaining;
		List<Point> optimal;
		for (ConvexHull hull : hulls) {
			key = hull.getHullName();
			removedHullNodes.put(key, new LinkedList<OrganismNode>());
			remaining = new LinkedList<OrganismNode>();
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
		for (OrganismNode o : remainingHullNodes.get(closestKey)) {
			if (o.getRectangle().contains(p)) {
				removedHullNodes.get(closestKey).add(o);
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
		collisionExists = TBSUtils.collide(temp);
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
				for (OrganismNode o : remainingHullNodes.get(e.getKey())) {
					if (o.getRectangle().contains(p)) {
						removedHullNodes.get(e.getKey()).add(o);
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
		StringBuffer arg = new StringBuffer();
		StringBuffer fullText = new StringBuffer();
		String sep = "";
		if(!removedHullNodes.isEmpty()){
			for (Map.Entry<String, List<OrganismNode>> removed : removedHullNodes
					.entrySet()) {
				arg.append(sep).append(removed.getValue().size()).append(" ")
				.append(removed.getKey());
				if (removed.getValue().size() > 1)
					arg.append("s");
				sep = ", ";
			}
		}else{
			for(ConvexHull ch : hulls){
				arg.append(sep).append("0 ").append(ch.getHullName()).append("s");
				sep = ", ";
			}
		}
		fullText.append(adminProps.getProperty("OPTIMAL_HULL1")).append("  ");
		fullText.append(MessageFormat.format(adminProps.getProperty("OPTIMAL_HULL2"), arg.toString()));
		text = fullText.toString();
	}

	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model) {
		if (collisionExists) {
			g2.setColor(new Color(255, 36, 0, 160));
			Polygon collisionShape = new Polygon();
			for (Point p : collision)
				collisionShape.addPoint(p.x - xOffset, p.y - yOffset);
			g2.fill(collisionShape);
		}else{
			if(!optimizationComplete){
					completeOptimizations();
					optimizationComplete = true;
			}
		}
		if(optimizationComplete && removedHullNodes.isEmpty()){
			for(ConvexHull ch : hulls)
				ch.render(g2, xOffset, yOffset, model);
		}else{
			Map<String, Polygon> hullShapes = new HashMap<String, Polygon>();
			for (Map.Entry<String, List<Point>> optimal : optimalHullPoints
					.entrySet()) {
				Polygon shape = new Polygon();
				for (Point p : optimal.getValue())
					shape.addPoint(p.x - xOffset, p.y - yOffset);
				hullShapes.put(optimal.getKey(), shape);
			}
			g2.setStroke(new BasicStroke(3));
			for (Map.Entry<String, Polygon> hullShape : hullShapes.entrySet()) {
				g2.setColor(model.getGroupColor(hullShape.getKey()));
				g2.draw(hullShape.getValue());
			}
			g2.setStroke(new BasicStroke());
			boolean showNames = model.getView().getDisplayAllTooltips();
			for (Map.Entry<String, List<OrganismNode>> removed : removedHullNodes
					.entrySet())
				renderRemoved(g2, removed.getValue(), xOffset, yOffset, showNames);
		}
	}

	public void renderRemoved(Graphics2D g2, List<OrganismNode> nodes,
			int xOffset, int yOffset, boolean showNames) {
		for (OrganismNode o : nodes) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(Color.RED);
			g2.draw(new Rectangle2D.Double(o.getX() - (1.5 + xOffset), o.getY()
					- (1.5 + yOffset), o.getWidth() + 3, o.getHeight() + 3));
			g2.setStroke(new BasicStroke());
			if (!showNames) {
				int xVal = (o.getX() + (o.getWidth() / 2)) - xOffset;
				int yVal = (o.getY() - o.getHeight()) - yOffset;
				g2.setFont(TBSGraphics.tooltipFont);
				xVal -= TBSGraphics
						.getStringBounds(g2, o.getTypes().get(level)).width / 2;
				TBSGraphics.drawCenteredString(g2, o.getTypes().get(level),
						xVal, yVal, 0, TBSGraphics.buttonsHeight,
						TBSGraphics.tooltipColor, TBSGraphics.tooltipFont);
				g2.setFont(TBSGraphics.font);
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
