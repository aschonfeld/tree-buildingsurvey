package TBS;

import javax.swing.*;
import javax.swing.event.*;
import javax.imageio.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import java.lang.*;
import java.io.*;

public class TBSApplet extends JApplet {
	
	private TBSView view;
	private TBSController controller;
	private TreeMap<String, BufferedImage> organismNameToImage;
	private ImageObserver observer;
	
	
	public void init() {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
    
    public void loadOrganismsFromDirectory(String directoryName) {
		organismNameToImage = new TreeMap();
		File dir = new File(directoryName);
		File[] files = dir.listFiles();
    	String[] children = dir.list();
    	String filename = null;
    	BufferedImage img = null;
    	if (children == null) {
    		// Either dir does not exist or is not a directory
    	} else {
    		for (int i=0; i< children.length; i++) {
            // Get filename of file or directory
            filename = children[i];
            // System.out.println(filename);
            try {
            	// create new BufferedImage from filename
            	// note: "Image" class is too general, using Buffered Image
            	img = ImageIO.read(files[i]);
        	} catch (IOException e) {
	        	//System.err.println("Error trying to load image from: " + filename + "in directory /images/");
	        	System.exit(0);
        	}
            // use part of filename before first "." as name of image, NOTE: must use "\\." not "."
            String[] parseFilename = filename.split("\\.");
            // insert into TreeMap
			organismNameToImage.put(parseFilename[0], img);
    		}
        }
	}
        	    		
	public void createAndShowGUI() {
		//Make sure we have nice window decorations.
        //JFrame.setDefaultLookAndFeelDecorated(true);

        //Create and set up the window.
        //JFrame frame = new JFrame("Tree Building Survey");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up the drawing area.
        loadOrganismsFromDirectory("images");
        view = new TBSView(organismNameToImage);
        view.setBackground(Color.black);
        view.setPreferredSize(new Dimension(928, 762));
        view.setOpaque(true); //content panes must be opaque
     
        // add the mouse handler
        controller = new TBSController();
        view.addMouseListener(controller);
        view.addMouseMotionListener(controller);
        add(view);
        
        // Create and set up the content pane.
        // frame.setContentPane(view);

        //Display the window.
        //frame.pack();
        //frame.setVisible(true);
    }
    
}
