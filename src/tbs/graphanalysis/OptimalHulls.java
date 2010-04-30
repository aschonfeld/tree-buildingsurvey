package tbs.graphanalysis;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Timer;

import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.model.AdminModel;
import tbs.model.OrganismNode;
import tbs.view.dropdown.SubDropDown;

public class OptimalHulls extends SubDropDown {

	private List<ConvexHull> hulls;
	private Point originalCentroid;
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

	public OptimalHulls(HullCollision collision) {
		level = collision.getLevel();
		hulls = collision.getHulls();
		commaSepGroups = TBSUtils.commaSeparatedString(hulls);
		removedHullNodes = new HashMap<String, List<OrganismNode>>();
		remainingHullNodes = new HashMap<String, List<OrganismNode>>();
		optimalHullPoints = new HashMap<String, List<Point>>();
		inProgressHulls = new HashMap<String, ConvexHull>();
		originalCentroid = collision.getCentroid();
		iterationWait = new Timer(1000, iterate);
		fullOptimization();
	}

	public void fullOptimization() {
		if (iterationWait.isRunning())
			iterationWait.stop();
		initOptimization();
		while (collisionExists)
			iterateOptimization();
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
		centroidInProgress = originalCentroid;
		collision = new LinkedList<Point>();
		collisionExists = true;
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
			centroidInProgress = tempHC.getCentroid();
			collision = tempHC.getCollisionPoints();
		}
	}

	private void createText() {
		StringBuffer textBuff = new StringBuffer(
				"\"Optimal Groups\" is optimization of the current student's arrangement of organisms so that groupings no longer collide.");
		textBuff
				.append(" This particular optimization required the removal of ");
		String sep = "";
		for (Map.Entry<String, List<OrganismNode>> removed : removedHullNodes
				.entrySet()) {
			textBuff.append(sep).append(removed.getValue().size()).append(" ")
					.append(removed.getKey());
			if (removed.getValue().size() > 1)
				textBuff.append("s");
			sep = ", ";
		}
		textBuff.append(" in order to eliminate group collisions.");
		text = textBuff.toString();
	}

	public void render(Graphics2D g2, int xOffset, int yOffset, AdminModel model) {
		if (collisionExists) {
			Polygon collisionShape = new Polygon();
			for (Point p : collision)
				collisionShape.addPoint(p.x - xOffset, p.y - yOffset);
			g2.setColor(new Color(255, 36, 0, 160));
			g2.fill(collisionShape);
		}
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
		iterationWait.start();
	}
}
