package TBS;

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
	
public interface ModelElement {
		
	public abstract boolean collidesWith(ModelElement e);
	public abstract boolean isOrganismNode();
}
