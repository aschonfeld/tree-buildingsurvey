import java.util.Observable;
import java.awt.*;

public class Thing extends Observable
{
	String name;
	Point loc;
	Dimension size;

	public Thing(String s, Point p)
	{
		name = s;
		loc = p;
	}
	
	public void setName(String s)
	{
		name = s;
		setChanged();
	}	
	
	public String getName()
	{
		return name;
	}
	
	public void setLocation(Point p)
	{
		loc = p;
		setChanged();
		notifyObservers();
	}

	public Point getLocation()
	{
		return loc;
	}

	public void setSize(Dimension d)
	{
		size = d;
		setChanged();
	}

	public Dimension getSize()
	{
		return size;
	}
	
}
