
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;

public class StudentDataTable extends JPanel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8614152295188968068L;
	JTextArea output;
    JList list; 
    JTable table;
    String newline = "\n";
    ListSelectionModel listSelectionModel;

    public StudentDataTable() {
        super(new BorderLayout());

        String[] columnNames = { "Name", 
        						 "Contains loop", 
        						 "All organism nodes terminal", 
        						 "Includes all organisms",
								 "Single Common Ancestor",
        						 "Grouping Inv",
        						 "Grouping Vert",
        						 "Grouping Mammals",
        						 "Grouping Non-Mammal Vert"};
        
        int rows = AdminApplication.graphs.size();
        Object[][] tableData = new Object[rows][columnNames.length];
        int row = 0;
        for(Graph graph : AdminApplication.graphs) {
				String studentName = graph.getStudentName();
        	tableData[row][0] = studentName;
        	tableData[row][1] = graph.containsCycle();
        	tableData[row][2] = graph.allOrganismsTerminal();
        	tableData[row][3] = graph.includesAllOrganisms();
        	tableData[row][4] = graph.hasSingleCommonAncestor();
        	tableData[row][5] = graph.groupingInvertebrates();
        	tableData[row][6] = graph.groupingVertebrates();
        	tableData[row][7] = graph.groupingMammals();
        	tableData[row][8] = graph.groupingNonmammals();	
        	row++;
        }
        table = new JTable(tableData, columnNames);
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        JScrollPane tablePane = new JScrollPane(table);
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablePane.setPreferredSize(new Dimension(420, 130));
        add(tablePane);
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
                        if(!isAdjusting) AdminApplication.setCurrentGraph(i);
                    }
                }
            }
            //System.out.println(output.toString());
        }
    }
}
