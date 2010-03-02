package admin;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class StudentDataTable extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8614152295188968068L;
	JTextArea output;
    JList list; 
    JTable table;
    String newline = "\n";
    ListSelectionModel listSelectionModel;
    AdminApplication parent;

    public StudentDataTable(AdminApplication parent) {
        super("TBS Student Data");
        String[] columnNames = { "Name", 
        						 "Branches", 
								 "Labelled",
        						 "All organism nodes terminal", 
        						 "Includes all organisms",
        						 "Hull Collisions",
								 "Single Common Ancestor",
        						 "Grouping Inv",
        						 "Grouping Vert",
        						 "Grouping Mammals",
        						 "Grouping Non-Mammal Vert"};
        this.parent = parent;
        int rows = parent.graphs.size();
        Object[][] tableData = new Object[rows][columnNames.length];
        int row = 0;
        for(Graph graph : parent.graphs) {
				String studentName = graph.getStudentName();
        	tableData[row][0] = studentName;
        	tableData[row][1] = graph.hasBranches();
        	tableData[row][2] = graph.groupsAreLabelled();
        	tableData[row][3] = graph.allOrganismsTerminal();
        	tableData[row][4] = graph.includesAllOrganisms();
        	tableData[row][5] = graph.getHasHullCollisions();
        	tableData[row][6] = graph.hasSingleCommonAncestor();
        	tableData[row][7] = graph.groupingInvertebrates();
        	tableData[row][8] = graph.groupingVertebrates();
        	tableData[row][9] = graph.groupingMammals();
        	tableData[row][10] = graph.groupingNonmammals();
        	row++;
        }
        table = new JTable(tableData, columnNames);
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        JScrollPane tablePane = new JScrollPane(table);
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePane.setSize(new Dimension(928, 762));
        add(tablePane);
        setSize(new Dimension(928, 762));
        setJMenuBar(parent.actionHandler.getDataMenuBar());
    }
    
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
            ListSelectionModel lsm = (ListSelectionModel)e.getSource();
            StringBuffer output = new StringBuffer();
            int firstIndex = e.getFirstIndex();
            int lastIndex = e.getLastIndex();
            boolean isAdjusting = e.getValueIsAdjusting(); 
            output.append("Event for indexes "
                          + firstIndex + " - " + lastIndex
                          + "; isAdjusting is " + isAdjusting
                          + "; selected indexes:");

            if (lsm.isSelectionEmpty()) {
                output.append(" <none>");
            } else {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        output.append(" " + i);
                        if(!isAdjusting) parent.setCurrentGraph(i);
                    }
                }
            }
            //System.out.println(output.toString());
        }
    }
}
