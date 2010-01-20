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

/**
* Student Controller Randomly Simulates Student Behavior in Tree Creation Mode
**/
public class StudentControllerTest
{
	
	Stack<SystemEvent> eventStack;
	
	private StudentController sc;
	private Component sourceComponent;
	private int mouseModifiers = 0;
	private int keyModifiers = 0;
	private int mouseX;
	private int mouseY;
	private int minX = 0;
	private int maxX = 500;
	private int minY = 100;
	private int maxY = 500;
	private int minDelta = 3;
	private int maxDelta = 10;
	private Random randomGenerator;
	private String actionString = "NONE";
	
	private Timer timer;
	private ActionListener fireNextEvent = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			nextEvent();
		}
	};
	
	private enum UserActionType {
		MOUSE_CLICKED,
		MOUSE_MOVED,
		MOUSE_DRAGGED,
	}
	
	private enum SystemEventType {
		MOUSE_PRESSED,
		MOUSE_RELEASED,
		MOUSE_CLICKED,
		MOUSE_MOVED,
		MOUSE_DRAGGED,
		NO_OP;
	}
	
	private class SystemEvent {
		
		private SystemEventType type;
		private Point p;
		
		SystemEvent(SystemEventType type, Point p) {
			this.type = type;
			this.p = p;
		}
		
		SystemEventType getType() {return type;}
		int getX() {return p.x;}
		int getY() {return p.y;}
		Point getPoint() {return p;}
	}
	
	public StudentControllerTest(StudentController sc, Component sourceComponent) {
		this.sc = sc;
		this.sourceComponent = sourceComponent;
		mouseX = 300;
		mouseY = 300;
		eventStack = new Stack<SystemEvent>();
		timer = new Timer(10, fireNextEvent);
 		timer.start();
 		randomGenerator = new Random(System.currentTimeMillis());
    }
	
	private void nextEvent() {
		if (eventStack.empty()) {
			nextUserAction();
		} else {
			executeNextSystemEvent();
		}
	}
	
	void executeNextSystemEvent() {
		SystemEvent e = eventStack.pop();
		switch(e.getType()) {
		case MOUSE_PRESSED:
			mousePressed(e.getX(), e.getY());
			break;
		case MOUSE_RELEASED:
			mouseReleased(e.getX(), e.getY());
			break;
		case MOUSE_CLICKED:
			mouseClicked(e.getX(), e.getY());
			break;
		case MOUSE_MOVED:
			mouseMoved(e.getX(), e.getY());
			break;
		case MOUSE_DRAGGED:
			mouseDragged(e.getX(), e.getY());
			break;
		case NO_OP:
			break;
		}
	}
	
	private void nextUserAction() {
		double randVal = Math.random() * 100.0;
		if(randVal < 30.0) {
			generateMouseMoved();
			return;
		}
		generateMouseDragged();
	}
	
	private void generateMouseMoved() {
		ArrayList<Point> coordinates = getMouseCoordinates();
		for(int index = coordinates.size() - 1; index > 0; index--) {
			Point current = coordinates.get(index - 1);
			eventStack.push(new SystemEvent(SystemEventType.MOUSE_MOVED, current));
		}
	}
	
	private void generateMouseDragged() {
		Point newPoint = getRandomCoordinate();
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_RELEASED, newPoint));
		ArrayList<Point> coordinates = getMouseCoordinates(newPoint);
		for(int index = coordinates.size() - 1; index > 0; index--) {
			Point current = coordinates.get(index - 1);
			eventStack.push(new SystemEvent(SystemEventType.MOUSE_DRAGGED, current));
		}
		eventStack.push(new SystemEvent(SystemEventType.MOUSE_PRESSED, new Point(mouseX, mouseY)));
	}
	
	private Point getRandomCoordinate() {
		int newX = minX + randomGenerator.nextInt(maxX - minX);
		int newY = minY + randomGenerator.nextInt(maxY - minY);
		return new Point(newX, newY);
	}
	
	private ArrayList<Point> getMouseCoordinates() {
		return getMouseCoordinates(getRandomCoordinate());
	}
	
	private ArrayList<Point> getMouseCoordinates(Point newPoint) {
		int newX = newPoint.x;
		int newY = newPoint.y;
		ArrayList<Point> returnVal = new ArrayList<Point>();
		double x = mouseX;
		double y = mouseY;
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
		//System.out.println("NUMMOVES " + numMoves);
		//System.out.println("STARTX " + mouseX + "STARTY " + mouseY);
		//System.out.println("DELTAX " + deltaXStep + "DELTAY " + deltaYStep);
		//System.out.println("!ENDX " + newX + "!ENDY " + newY);
		for(double move = 0.0; move < numMoves; move += 1.0) {
			x += deltaXStep;
			y += deltaYStep;
			intX = (int) Math.round(x);
			intY = (int) Math.round(y);
			//System.out.println(intX + " " + intY);
			returnVal.add(new Point(intX, intY));
		}
		//System.out.println("ENDX " + newX + "ENDY " + newY);
		if((intX != newX) || (intY != newY)) {
			returnVal.add(new Point(newX, newY));
		}
		//mouseX = newX;
		//mouseY = newY;
		return returnVal;
	}
	
	private void keyPressed(KeyEvent e) {
		char keyChar = 'p';
		int keyCode = 0;
		KeyEvent event = new KeyEvent(
				sourceComponent, 
				KeyEvent.KEY_PRESSED, 
				System.currentTimeMillis(), 
				keyModifiers, 
				keyCode, 
				keyChar);
		sc.keyPressed(event);
	}

	private void keyTyped(KeyEvent e) {
		char keyChar = 'p';
		int keyCode = 0;
		KeyEvent event = new KeyEvent(
				sourceComponent, 
				KeyEvent.KEY_PRESSED, 
				System.currentTimeMillis(), 
				keyModifiers, 
				keyCode, 
				keyChar);
		sc.keyTyped(event);
	}
	
	private void mouseMoved(int x, int y){
		int mouseClickCount = 0;
		actionString = "MOVE";
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_CLICKED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				x, 
				y, 
				mouseClickCount, 
				false);
		mouseX = x;
		mouseY = y;
		sc.mouseMoved(event);
	}
	
	// No need to use since mousePressed is used instead
	private void mouseClicked(int x, int y) {
		mousePressed(x,y);
		mouseReleased(x,y);
		int mouseClickCount = 1;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_CLICKED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				x, 
				y, 
				mouseClickCount, 
				false);
		sc.mouseClicked(event);
	}
	
	private void mousePressed(int x, int y){
		int mouseClickCount = 1;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_PRESSED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				x, 
				y, 
				mouseClickCount, 
				false);
		sc.mousePressed(event);
	}
	
	private void mouseDragged(int x, int y){
		actionString = "DRAG";
		int mouseClickCount = 0;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_DRAGGED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				x, 
				y, 
				mouseClickCount, 
				false);
		mouseX = x;
		mouseY = y;
		sc.mouseDragged(event);
 	}
	
	private void mouseReleased(int x, int y)
	{
		int mouseClickCount = 1;
		MouseEvent event = new MouseEvent(
				sourceComponent, 
				MouseEvent.MOUSE_RELEASED, 
				System.currentTimeMillis(), 
				mouseModifiers, 
				x, 
				y, 
				mouseClickCount, 
				false);
		sc.mouseReleased(event);
	}
	
	public void renderVirtualCursor(Graphics2D g2) {
		g2.setColor(Color.RED);
		g2.fillRect(mouseX - 4, mouseY - 4, 8, 8);
		g2.drawString(actionString, (float) mouseX + 4, (float) mouseY + 4);
	}
	
}