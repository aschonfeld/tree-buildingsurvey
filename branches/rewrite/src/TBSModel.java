//TBSModel v0.02

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.font.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TBSModel 
{
	private TreeMap<String, ModelElement> elements;
	
	
	

	public TreeMap<String, ModelElement> getElements() {
		return elements;
	}

	public void setElements(TreeMap<String, ModelElement> elements) {
		this.elements = elements;
	}
	
}

