package admin;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import admin.StudentDataColumns.ColumnDataHandler;

import java.awt.*;
import java.util.ArrayList;

public class StudentDataTable extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 8614152295188968068L;
	static JTextArea output;
    static JList list; 
    static JTable table;
    static JScrollPane tablePane;
    static String newline = "\n";
    static ListSelectionModel listSelectionModel;
    static StudentDataTableModel studentDataTableModel;
    public static StudentDataColumns studentDataColumns;
    static AdminApplication parent;
    static HumanScoring humanScoring;

    public StudentDataTable(AdminApplication parent) {
        super("TBS Student Data");
        this.parent = parent;
        humanScoring = new HumanScoring(parent);
        studentDataColumns = new StudentDataColumns();
        sharedTableInit();
        tablePane.setSize(new Dimension(928, 762));
        add(tablePane);
        setSize(new Dimension(928, 762));
        setJMenuBar(parent.actionHandler.getDataMenuBar(studentDataColumns));
    }
    
    public class StudentDataTableModel extends AbstractTableModel {
    	
        private String[] columnNames;
        private Object[][] data;
        
        StudentDataTableModel() {
        	loadTableData();
        }
        
        private void loadTableData() {
        	int rows = parent.graphs.size();
            int columns = 0;
            for(ColumnDataHandler cdh: studentDataColumns.columnDataHandlers) {
            	if(!cdh.isVisible()) continue;
            	columns++;
            }
            columnNames = new String[columns];
            int column = 0;
            for(ColumnDataHandler cdh: studentDataColumns.columnDataHandlers) {
            	if(!cdh.isVisible()) continue;
            	columnNames[column] = cdh.getName();
            	column++;
            }
            data = new Object[rows][columns];
            int row = 0;
            for(Graph graph : parent.graphs) {
            	column = 0;
            	for(ColumnDataHandler cdh: studentDataColumns.columnDataHandlers) {
            		if(!cdh.isVisible()) continue;
            		data[row][column] = cdh.getData(graph);
            		column++;
            	}
            	row++;
            }
        }
        		
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return parent.graphs.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
        	return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
        	if(col == 1) {
        		// row must be selected in order for cell to be editable
        		if(row != parent.getCurrentGraphIndex()) return false;
        		if(data[row][col] == Graph.GraphType.Test) return false;
        		return true;
        	}
        	return false;
        }

        public void setValueAt(Object value, int row, int col) {
        	if(col == 1) {
        		humanScoring.saveCategory(parent.graphs.get(row), (Graph.GraphType) value);
        		data[row][col] = value;
        	}
            fireTableCellUpdated(row, col);
        }
    }
    
    public void setUpGraphTypeColumn(JTable table, TableColumn graphTypeColumn) {
    		JComboBox comboBox = new JComboBox();
    		for(Graph.GraphType type: Graph.GraphType.values()) {
    			if(type.isSelectableType()) comboBox.addItem(type);
    		}
    		graphTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
    }

    public void refreshTable() {
    	Dimension size = this.getSize();
    	Dimension tableSize = tablePane.getSize();
    	remove(tablePane);
    	sharedTableInit();
        tablePane.setSize(tableSize);
        add(tablePane);
        setSize(size);
    }
    
    // contains code common to contstructor and refreshTable
    public void sharedTableInit() {
        studentDataTableModel = new StudentDataTableModel();
        table = new JTable(studentDataTableModel);
        listSelectionModel = table.getSelectionModel();
        listSelectionModel.addListSelectionListener(new SharedListSelectionHandler());
        table.setSelectionModel(listSelectionModel);
        table.setAutoCreateRowSorter(true);
        tablePane = new JScrollPane(table);
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setUpGraphTypeColumn(table, table.getColumnModel().getColumn(1));
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
