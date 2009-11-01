//TBS Version 0.3


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

public class TBSApplet extends JApplet {
	
	private TBSModel model;
	private TBSView view;
	private TBSController controller;

	public void init() {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
            	model = new TBSModel(getGraphics(), loadOrganismsFromDirectory("images"));
                add(model.getView());
                model.getView().addMouseListener(model.getController());
                model.getView().addMouseMotionListener(model.getController());
            }
        });
    }
    
   	public TreeMap<String, BufferedImage> loadOrganismsFromDirectory(String directoryName) {
		TreeMap<String, BufferedImage> organismNameToImage = new TreeMap();
		try {
			// read names of organisms and image file names from list.txt in "/images"
			URL fileURL=new URL(getCodeBase(),"images/list.txt");     
			URLConnection conn=(URLConnection) fileURL.openConnection();    
			conn.setRequestProperty("REFERER",getDocumentBase().toString());    
			InputStream is=conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			String[] parseLine = null;
			String organismName = null;
			String organismImageFilename = null;
			String organismImageFullPath = null;
			BufferedImage img = null;
            while ((line = reader.readLine()) != null) {
	            // load image from files, and map organism name to image
                parseLine = line.split(",");
                organismName = parseLine[0];
                organismImageFilename = parseLine[1];
                // System.out.println(organismName + " " + organismImageFilename);
                organismImageFullPath = ("images/" + organismImageFilename);
				URL imageURL=new URL(getCodeBase(), organismImageFullPath);     
				URLConnection imageconn=(URLConnection) imageURL.openConnection();    
				imageconn.setRequestProperty("REFERER",getDocumentBase().toString());    
				InputStream imageis=imageconn.getInputStream();
				img = ImageIO.read(imageis);
				organismNameToImage.put(organismName, img);
				imageis.close();
            }
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return organismNameToImage;
	}
    
}

