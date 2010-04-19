package tbs.controller;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javax.swing.Timer;

import tbs.view.TBSButtonType;

/**
* Student Controller Randomly Simulates Student Behavior in Tree Creation Mode
**/
public class StudentControllerTest
{
	
	Stack<SystemEvent> eventStack;
	
	private StudentController sc;
	private Component sourceComponent;
	private int timerMillis = 10;
	private int mouseModifiers = 0;
	private int keyModifiers = 0;
	private int minX = 0;
	private int maxX = 670; // Right of "Help" Button
	private int minY = 30; // Below Buttons
	private int maxY = 580; // Below ImmortalNode
	private Point mouseLocation = new Point(300, 300);
	//private Point minLocation = new Point(minX, minY);
	//private Point maxLocation = new Point(maxX, maxY);
	private int minDelta = 8;
	private int maxDelta = 10;
	private Random randomGenerator;
	private String actionString = "NONE";
	private ArrayList<Point> returnPoints; 
	private boolean doTest = true;
	private SpecialPoint lastSpecialPoint = SpecialPoint.SELECT;
	private int lineOfDeath = 180;
	
	private Timer timer;
	private ActionListener fireNextEvent = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nextEvent();
		}
	};
	
	private enum SpecialPoint {
	    SELECT (34, 12),
	    ADD (100, 12),
	    DELETE (170, 12),
	    LINK (236, 12),
	    UNLINK (301, 12),
	    LABEL (370, 12),
	    UNDO (506, 12),
	    IMMORTAL_NODE (50, 564);

	    private final int x;
	    private final int y;
	    
	    SpecialPoint(int x, int y) {
	        this.x = x;
	        this.y = y;
	    }
	    
	    public Point getPoint() {
	    	return new Point(x, y);
	    }
	}

	
	private enum SystemEventType {
		MOUSE_PRESSED,
		MOUSE_RELEASED,
		MOUSE_CLICKED,
		MOUSE_MOVED,
		MOUSE_DRAGGED,
		KEY_PRESSED,
		KEY_TYPED,
		NO_OP;
	}
	
	private class SystemEvent {
		
		private SystemEventType type;
		private Point p;
		private char keyChar;
		
		SystemEvent(SystemEventType type, Point p) {
			this.type = type;
			this.p = p;
			this.keyChar = 0;
		}
		
		SystemEvent(SystemEventType type, char keyChar) {
			this.type = type;
			this.p = null;
			this.keyChar = keyChar;
		}
		
		SystemEvent(SystemEventType type) {
			this.type = type;
			this.p = null;
			this.keyChar = 0;
		}
		
		SystemEventType getType() {return type;}
		Point getPoint() {return p;}
		char getKeyChar() {return keyChar;};
	}
	
	public StudentControllerTest(StudentController sc, Component sourceComponent) {
		this.sc = sc;
		this.sourceComponent = sourceComponent;
		eventStack = new Stack<SystemEvent>();
		timer = new Timer(timerMillis, fireNextEvent);
 		timer.start();
 		randomGenerator = new Random(System.currentTimeMillis());
 		returnPoints = new ArrayList<Point>();
 		returnPoints.add(new Point(300, 300));
    }
	
	private void nextEvent() {
		if (eventStack.empty()) {
			if(!doTest) return;
			//insertWait(10);
			nextUserAction();
			//insertWait(10);
		} else {
			executeNextSystemEvent();
		}
	}
	
	void executeNextSystemEvent() {
		SystemEvent e = eventStack.pop();
		switch(e.getType()) {
		case MOUSE_PRESSED:
			mousePressed();
			break;
		case MOUSE_RELEASED:
			mouseReleased();
			break;
		case MOUSE_CLICKED:
			mouseClicked();
			break;
		case MOUSE_MOVED:
			mouseMoved(e.getPoint());
			break;
		case MOUSE_DRAGGED:
			mouseDragged(e.getPoint());
			break;
		case KEY_PRESSED:
			keyPressed(e.getKeyChar());
			break;
		case KEY_TYPED:
			keyTyped(e.getKeyChar());
			break;			
		case NO_OP:
			break;
		}
	}
	
	private void nextUserAction() {
		removeRandomReturnPoint();
		if(TBSButtonType.LABEL.equals(sc.getButtonClicked())) {
			pressKey();
			return;
		}
		double randVal = Math.random() * 100.0;
		if(randVal < 40.0) {
			moveMouseTo();
			return;
		}
		if(randVal < 80.0) {
			dragMouseTo();
			return;
		}
		pressButton();
	}
	
	private void pressKey() {
		double randVal = Math.random() * 100.0;
		if(randVal < 95.0) {
			char keyChar = 'a';
			keyChar += (char) randomGenerator.nextInt(25);
			eventStack.push(new SystemEvent(SystemEventType.KEY_TYPED, keyChar));
			eventStack.push(new SystemEvent(SystemEventType.KEY_PRESSED, keyChar));
			return;
		}
		eventStack.push(new SystemEvent(SystemEventType.KEY_TYPED, '\n'));
		eventStack.push(new SystemEvent(SystemEventType.KEY_PRESSED, '\n'));
	}
	
	private void pressButton() {
		lastSpecialPoint = getRandomButton();
		pressButton(lastSpecialPoint);
    }
	
	private void pressButton(SpecialPoint button) {
		moveMouseTo(button.getPoint());
		clickMouse();
		moveMouseToReverse(mouseLocation);
    }
	
	private Point getRandomReturnPoint() {
		int index = randomGenerator.nextInt(returnPoints.size());
		return returnPoints.get(index);
	}
	
	private void removeRandomReturnPoint() {
		//if(returnPoints.size() < 3) return;
		//int index = randomGenerator.nextInt(returnPoints.size());
		//returnPoints.remove(index);
	}
	
	private SpecialPoint getRandomButton() {
		double randVal = Math.random() * 100.0;
		if  (randVal < 10.0) return SpecialPoint.SELECT;
		if  (randVal < 15.0) return SpecialPoint.ADD;
		if  (randVal < 20.0) return SpecialPoint.DELETE;
		if  (randVal < 60.0) return SpecialPoint.LINK;
		if  (randVal < 85.0) return SpecialPoint.LABEL;
		return SpecialPoint.UNDO;
    }
	
	private void insertWait(int millis) {
		int numDelays = millis / timerMillis;
		for(int delay = 0; delay < numDelays; delay++) {
			eventStack.push(new SystemEvent(SystemEventType.NO_OP));
		}
	}
	
	private void clickMouse() {
		insertWait(100);
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_CLICKED));
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_RELEASED));
		insertWait(200);
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_PRESSED));
		insertWait(100);	
	}
	
	// increased probability of link and label
	private void randomHelper() {
		Point location = getRandomReturnPoint();
		while(location.x < lineOfDeath) location = getRandomReturnPoint();
		moveMouseTo(location);
	}
		
	// move mouse to random point or previous point
	private void moveMouseTo() {
		Point location = getRandomCoordinate();
		double randVal = Math.random() * 100.0;
		if(randVal < 75.0) {
			if(lastSpecialPoint == SpecialPoint.LABEL) {
				randomHelper();
				return;
			}
			if(lastSpecialPoint == SpecialPoint.LINK) {
				randomHelper();
				return;
			}			
			moveMouseTo(location);
			returnPoints.add(location);
		} else {
			location = getRandomReturnPoint(); 
		}
	}
	
	// move mouse to newPoint
	private void moveMouseTo(Point newPoint) {
		ArrayList<Point> coordinates = getMouseCoordinates(newPoint);
		for(int index = coordinates.size() - 1; index > 0; index--) {
			Point current = coordinates.get(index - 1);
			eventStack.push(new SystemEvent(SystemEventType.MOUSE_MOVED, current));
		}
	}
	
	private void moveMouseToReverse(Point newPoint) {
		ArrayList<Point> coordinates = getMouseCoordinates(newPoint);
		for(int index = 0; index < coordinates.size(); index++) {
			Point current = coordinates.get(index);
			eventStack.push(new SystemEvent(SystemEventType.MOUSE_MOVED, current));
		}
	}
	
	private void dragMouseTo() {
		dragMouseTo(getRandomCoordinate());
	}
	
	
	private void dragMouseTo(Point newPoint) {
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_RELEASED));
		ArrayList<Point> coordinates = getMouseCoordinates(newPoint);
		for(int index = coordinates.size() - 1; index > 0; index--) {
			Point current = coordinates.get(index - 1);
			eventStack.push(new SystemEvent(SystemEventType.MOUSE_DRAGGED, current));
		}
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_PRESSED));
	}
	
	private Point getRandomCoordinate() {
		int newX = minX + randomGenerator.nextInt(maxX - minX);
		int newY = minY + randomGenerator.nextInt(maxY - minY);
		return new Point(newX, newY);
	}
		
	private ArrayList<Point> getMouseCoordinates(Point newPoint) {
		int newX = newPoint.x;
		int newY = newPoint.y;
		ArrayList<Point> returnVal = new ArrayList<Point>();
		double x = mouseLocation.x;
		double y = mouseLocation.y;
		int intX = -1;
		int intY = -1;
		double numMoves = 1.0;
		double deltaStep = minDelta + randomGenerator.nextInt(maxDelta - minDelta);
		double deltaX = newX - x;
		double deltaY = newY - y;
		if(Math.abs(deltaX) < deltaStep) deltaX = deltaStep;
		if(Math.abs(deltaY) < deltaStep) deltaY = deltaStep;
		if(Math.abs(deltaX) > Math.abs(deltaY)) {
			numMoves = Math.abs(deltaX) / deltaStep;
		} else {
			numMoves = Math.abs(deltaY) / deltaStep;
		}
		double deltaXStep = deltaX / numMoves;
		double deltaYStep = deltaY / numMoves;
		for(double move = 0.0; move < numMoves; move += 1.0) {
			x += deltaXStep;
			y += deltaYStep;
			intX = (int) Math.round(x);
			intY = (int) Math.round(y);
			returnVal.add(new Point(intX, intY));
		}
		if((intX != newX) || (intY != newY)) {
			returnVal.add(new Point(newX, newY));
		}
		return returnVal;
	}
	
	private void keyPressed(char keyChar) {
		int keyCode = Character.getNumericValue(keyChar);
		KeyEvent event = new KeyEvent(
				sourceComponent, 
				KeyEvent.KEY_PRESSED, 
				System.currentTimeMillis(), 
				keyModifiers, 
				keyCode, 
				keyChar);
		sc.keyPressed(event);
	}

	private void keyTyped(char keyChar) {
		int keyCode = 0;
		KeyEvent event = new KeyEvent(
				sourceComponent, 
				KeyEvent.KEY_TYPED, 
				System.currentTimeMillis(), 
				keyModifiers, 
				keyCode, 
				keyChar);
		sc.keyTyped(event);
	}
	
	private void mouseMoved(Point location){
		int mouseClickCount = 0;
		actionString = "MOVE";
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_CLICKED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				location.x, 
				location.y, 
				mouseClickCount, 
				false);
		mouseLocation = location;
		sc.mouseMoved(event);
	}
	
	private void mouseClicked() {
		int mouseClickCount = 1;
		actionString = "CLICK";
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_CLICKED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				mouseLocation.x, 
				mouseLocation.y, 
				mouseClickCount, 
				false);
		sc.mouseClicked(event);
	}
	
	private void mousePressed(){
		int mouseClickCount = 1;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_PRESSED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				mouseLocation.x, 
				mouseLocation.y, 
				mouseClickCount, 
				false);
		sc.mousePressed(event);
	}
	
	private void mouseDragged(Point location){
		actionString = "DRAG";
		int mouseClickCount = 0;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_DRAGGED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				location.x, 
				location.y, 
				mouseClickCount, 
				false);
		mouseLocation = location;
		sc.mouseDragged(event);
 	}
	
	private void mouseReleased()
	{
		int mouseClickCount = 1;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_RELEASED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				mouseLocation.x, 
				mouseLocation.y,
				mouseClickCount, 
				false);
		sc.mouseReleased(event);
	}
	
	public void renderVirtualCursor(Graphics2D g2) {
		if(!doTest) return;
		g2.setColor(Color.RED);
		g2.fillRect(mouseLocation.x - 4, mouseLocation.y - 4, 8, 8);
		g2.drawString(actionString, (float) mouseLocation.x + 4, (float) mouseLocation.y + 4);
	}
	
	public void toggleTest() {
		doTest = !doTest;
	}
	
}
