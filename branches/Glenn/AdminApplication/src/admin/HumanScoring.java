package admin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.TreeMap;

public class HumanScoring {
	
	private AdminApplication parent;
	private TreeMap<String, Graph.GraphType> studentNameToGraphType;
	
	HumanScoring(AdminApplication parent) {
		this.parent = parent;
		loadCategories();
	}
	
	public void loadCategories() {
		studentNameToGraphType = new TreeMap<String, Graph.GraphType>();
		String linein;
		String[] tokens;
		RandomAccessFile file;
	    try {
			file = new RandomAccessFile(new String("trees/categories"), "rw");
		} catch (FileNotFoundException nf) {
			System.out.println("HumanScoring.loadCategories(): \"trees/categories\" not found");
			return;
		}
		try {
			linein = file.readLine();
			while(linein != null) {
				tokens = linein.split(":");
				String studentName = tokens[0];
				Graph.GraphType type = Graph.GraphType.valueOf(tokens[1]);
				studentNameToGraphType.put(studentName, type);
				linein = file.readLine();
			}
			file.close();
		} catch (IOException ie) {
			System.out.println("HumanScoring.loadCategories(): error reading file");
			System.exit(0);
		}
		for(Graph graph: parent.graphs) {
			String studentName = graph.getStudentName();
			if(studentNameToGraphType.containsKey(studentName)) {
				graph.setType(studentNameToGraphType.get(studentName));
			}
		}
	}
	
	// append new category value for graph to file
	// if category already scored both will be in file, 
	// however most recent version will be loaded (see loadCategories())
	public void saveCategory(Graph graph, Graph.GraphType type) {
		BufferedWriter bufferedWriter;
	    try {
	    	bufferedWriter = new BufferedWriter(new FileWriter("trees/categories", true));
			bufferedWriter.write(new String(graph.getStudentName() + ":" + type));
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException ie) {
			System.out.println("HumanScoring.saveCategory(): error appending file");
			System.exit(0);
		}
	}
	
}