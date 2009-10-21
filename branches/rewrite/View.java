//with Thing.java, this is a demo of the Observer/Observable relation
//NOT part of the tbs package!

import java.awt.*;
import java.util.*;

public class View implements Observer
{
	Thing thingy = new Thing("Thingy", new Point(3,4));		

	public static void main(String args[])
	{
		System.out.println("Starting");
		View v = new View();
		System.out.println("Called View()");
	}	

	public View()
	{
		thingy.addObserver(this);
		for (int i = 0; i<5; i++)
		{
			Point p = thingy.getLocation();
			p.x++;
			p.y++;
			thingy.setLocation(p);
			
		}
	}

	public void update(Observable o, Object arg)
	{
		Thing t = (Thing) o;
		System.out.println("Location of "+ t.getName()+" is" 
			+ t.getLocation()+ ". "); 
	}

}
