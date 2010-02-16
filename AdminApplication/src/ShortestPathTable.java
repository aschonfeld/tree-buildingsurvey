
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;

public class ShortestPathTable extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 235810946275382728L;
    public JTable table = null;
    public DefaultTableModel tableModel = null;
    public ArrayList<String> columnNames = null;
    
    public ShortestPathTable() {
    	super(new BorderLayout());
        loadTable();
        table = new JTable(tableModel);
        JScrollPane tablePane = new JScrollPane(table);
        tablePane.setPreferredSize(new Dimension(420, 130));
        add(tablePane);
    }
    
    public void loadTable() {
    	if(tableModel != null) while(tableModel.getRowCount() > 0) tableModel.removeRow(0);
    	Graph g = AdminApplication.getCurrentGraph();
    	int[][] path = g.getShortestPaths();
    	String[] vertexNames = g.getPathIndexNames();
    	int rows = vertexNames.length;
    	int columns = rows + 1;
        columnNames = new ArrayList<String>();
        Object[][] tableData = new Object[rows][columns];
        columnNames.add(new String("PATH[FROM][TO]"));
        for(int row = 0; row < rows; row++) {
        	columnNames.add(vertexNames[row]);
        	tableData[row][0] = vertexNames[row];
        	for(int column = 1; column < columns; column++) {
        		//System.out.println(row + "T|" + column + "=" + path[row][column - 1]);
        		tableData[row][column] = path[row][column - 1];
        	}
        }
        tableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 7173915656049098895L;
			public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
		    public String getColumnName(int col) {
		        return columnNames.get(col);
		    }
		    public int getColumnCount() { 
		    	return columnNames.size(); 
		    }

        };
        for(int row = 0; row < rows; row++) tableModel.addRow(tableData[row]);
    }
    
}
