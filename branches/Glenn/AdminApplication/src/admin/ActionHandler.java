package admin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import admin.StudentDataColumns.ColumnDataHandler;

public class ActionHandler extends JPanel {    
	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0x60655B840361BFA4L;
	
	public Action exitAction;
	public Action printAction;
	public Action exportAction;
	public AdminApplication parent;
	
	
	public class ExitAction extends AbstractAction {

		private static final long serialVersionUID = 1740545338294704279L;

		public ExitAction() {
			super("Exit");
		}

		//@0verride
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}
	
	public class PrintAction extends AbstractAction {

		private static final long serialVersionUID = 1740545322294704279L;

		public PrintAction() {
			super("Print Graph");
		}

		//@0verride
		public void actionPerformed(ActionEvent arg0) {
			parent.printGraphInfo();
			PrinterJob printJob = PrinterJob.getPrinterJob();
			printJob.setPrintable(parent.treeView);
			if (printJob.printDialog()){
				try { 
					printJob.print();
				} catch(PrinterException pe) {
					System.out.println("Error printing: " + pe);
				}
			}
		}
	}
	
	public class NamesAction extends AbstractAction {

		private static final long serialVersionUID = 3382645405034163126L;
		public NamesAction() {
			super();
		}

		//@0verride
		public void actionPerformed(ActionEvent e) {
			parent.toggleShowNames();
			JMenuItem item = (JMenuItem)e.getSource();
			item.setText("Display Names" + (parent.showNames ? " \u2713" : ""));
		}
	}
	
	public class HullAction extends AbstractAction {

		private static final long serialVersionUID = 3382645405034163126L;
		private int hullIndex;
		public HullAction(int hullIndex) {
			super();
			this.hullIndex = hullIndex;
		}

		//@0verride
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem)e.getSource();
			item.setText(parent.getCurrentGraph().displaySubDropDownItem(SubDropDownType.HULL,
					hullIndex));
		}
	}
	
	public class CollisionAction extends AbstractAction {

		private static final long serialVersionUID = 3382645405034163126L;
		private int collisionIndex;
		public CollisionAction(int collisionIndex) {
			super();
			this.collisionIndex = collisionIndex;
		}

		//@0verride
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem)e.getSource();
			item.setText(parent.getCurrentGraph().displaySubDropDownItem(SubDropDownType.COLLISION,
					collisionIndex));
		}
	}
	
	public class OptimalAction extends AbstractAction {

		private static final long serialVersionUID = 3382645405034163126L;
		private int optimalIndex;
		public OptimalAction(int optimalIndex) {
			super();
			this.optimalIndex = optimalIndex;
		}

		//@0verride
		public void actionPerformed(ActionEvent e) {
			JMenuItem item = (JMenuItem)e.getSource();
			item.setText(parent.getCurrentGraph().displaySubDropDownItem(SubDropDownType.OPTIMAL_HULL,
					optimalIndex));
		}
	}
	
	public class ColumnDisplayAction extends AbstractAction {
		
		private static final long serialVersionUID = -251547965416261110L;
		private ColumnDataHandler cdh;
		private int index;
		public ColumnDisplayAction(ColumnDataHandler cdh, int index) {
			super(cdh.getName() + (cdh.isVisible() ? " \u2713" : ""));
			this.cdh = cdh;
			this.index = index;
		}

		//@0verride
		public void actionPerformed(ActionEvent e) {
			cdh.toggleVisible();
			JMenuItem item = (JMenuItem) e.getSource();
			item.setText(cdh.getName() + (cdh.isVisible() ? " \u2713" : ""));
			parent.parent.studentDataTableFrame.refreshTable();
		}
	}
	
	
	public class ExportAction extends AbstractAction {

		private static final long serialVersionUID = -5425238186912620684L;
		
		private JFileChooser fc;


		public ExportAction() {
			super("Export Data");
			fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}

		//@0verride
		public void actionPerformed(ActionEvent arg0) {
			int returnVal = fc.showSaveDialog(parent);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				parent.createExportFile(file.getPath());
				System.out.println("Exporting data to: " + file.getPath());
			} else 
				System.out.println("Export Command cancelled by user.");
		}
	}
		
    public ActionHandler() {
        exitAction = new ExitAction();
        printAction = new PrintAction();
        exportAction = new ExportAction();
    }
    
    public void setParent(AdminApplication parent) {
    	this.parent = parent;
    }
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu fileMenu;
        JMenuItem printItem;
        JMenuItem exitItem;
        
        //Create the menu bar.
        menuBar = new JMenuBar();
        fileMenu = new JMenu("Menu");
        menuBar.add(fileMenu);

        //a group of JMenuItems
        printItem = new JMenuItem(printAction);
        exitItem = new JMenuItem(exitAction);
        fileMenu.add(printItem);
        JMenuItem names = new JMenuItem("Display Names" + (parent.showNames ? " \u2713" : ""));
        names.addActionListener(new NamesAction());
        fileMenu.add(names);
        Graph tempGraph = parent.getCurrentGraph();
        List<ConvexHull> groups = tempGraph.getHulls(true);
        if(!groups.isEmpty()){
        	final JMenuItem colorEditorItem = new JMenuItem("Group Colors");
        	final JDialog colorEditor = getColorEditor();
        	ActionListener colorEditorListener = new ActionListener() {
    			public void actionPerformed(ActionEvent actionEvent) {
    				colorEditor.setVisible(true);
    			}
    		};
    		colorEditorItem.addActionListener(colorEditorListener);
    		fileMenu.add(colorEditorItem);
        	JMenu groupMenu = new JMenu("Groups");
        	for(int i=0;i<groups.size();i++){
        		ConvexHull tempCH = groups.get(i);
        		JMenuItem menuItem = new JMenuItem(tempCH.toString());
        		menuItem.addActionListener(new HullAction(i));
        		groupMenu.add(menuItem);
        	}
        	fileMenu.add(groupMenu);
        	List<HullCollision> collisions = tempGraph.getHullCollisions(true);
        	if(!collisions.isEmpty()){
        		JMenu collisionMenu = new JMenu("Group Collisions");
        		for(int i=0;i<collisions.size();i++){
        			HullCollision tempHC = collisions.get(i);
        			JMenuItem menuItem = new JMenuItem(tempHC.toString());
        			menuItem.addActionListener(new CollisionAction(i));
        			collisionMenu.add(menuItem);
        		}
        		fileMenu.add(collisionMenu);
        		List<OptimalHulls> optimalHulls = tempGraph.getOptimalHulls(true);
        		JMenu optimalMenu = new JMenu("Optimal Groups");
        		for(int i=0;i<optimalHulls.size();i++){
        			OptimalHulls tempOH = optimalHulls.get(i);
        			JMenuItem menuItem = new JMenuItem(tempOH.toString());
        			menuItem.addActionListener(new OptimalAction(i));
        			optimalMenu.add(menuItem);
        		}
        		fileMenu.add(optimalMenu);
        	}
        	JMenuItem deselect = new JMenuItem("Clear Selections");
    		ActionListener deselectListener = new ActionListener() {
    			public void actionPerformed(ActionEvent actionEvent) {
    				parent.getCurrentGraph().deselectAllItems();
    			}
    		};
    		deselect.addActionListener(deselectListener);
    		fileMenu.add(deselect);
        }
        fileMenu.add(exitItem);
        

       return menuBar;
    }
    
    public JMenuBar getDataMenuBar(StudentDataColumns studentDataColumns) {
    	JMenuBar menuBar;
    	JMenu fileMenu;
    	JMenu showColumnsMenu;
    	JMenuItem exportItem;
    	JMenuItem exitItem;
    	
    	//Create the menu bar.
    	menuBar = new JMenuBar();
    	fileMenu = new JMenu("Menu");
    	showColumnsMenu = new JMenu("Show Columns");
    	int index = 0;
    	for(ColumnDataHandler cdh: studentDataColumns.columnDataHandlers) {
    		if(cdh.isAlwaysVisible()) continue;
    		showColumnsMenu.add(new ColumnDisplayAction(cdh, index));
    		index++;
    	}
    	menuBar.add(fileMenu);
    	menuBar.add(showColumnsMenu);
    	//a group of JMenuItems
    	exportItem = new JMenuItem(exportAction);
    	exitItem = new JMenuItem(exitAction);
    	fileMenu.add(exportItem);
    	fileMenu.add(exitItem);


    	return menuBar;
    }
    
    public JDialog getColorEditor(){
    	JDialog colorDialog = new JDialog(parent, "Edit Group Colors", true);
    	colorDialog.setSize(500, 550);
    	JPanel colorEditor = new JPanel(new BorderLayout());

    	//Set up the banner at the top of the window
    	final JLabel groupLabel = new JLabel();
    	groupLabel.setForeground(Color.WHITE);
    	groupLabel.setBackground(Color.blue);
    	groupLabel.setOpaque(true);
    	groupLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    	groupLabel.setPreferredSize(new Dimension(100, 65));
        
        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.add(groupLabel, BorderLayout.CENTER);
        groupPanel.setBorder(BorderFactory.createTitledBorder("Selected Group"));

        Set<String> groups = AdminApplication.getColorChooser().keySet();
    	final JComboBox groupSelection = new JComboBox();
    	groupSelection.setSize(100, 40);
    	String selectedGroup = null;
    	for (String group : groups){
    		groupSelection.addItem(group);
    		if(selectedGroup == null)
    			selectedGroup = group;
    	}
    	groupSelection.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			String selectedGroup = ((JComboBox) e.getSource()).getSelectedItem().toString();
    			groupLabel.setText(selectedGroup);
    			groupLabel.setBackground(AdminApplication.getColorChooser().get(selectedGroup));
    		}
    	});
    	groupLabel.setText(selectedGroup);
    	groupLabel.setHorizontalAlignment(JLabel.CENTER);
    	groupLabel.setBackground(AdminApplication.getColorChooser().get(selectedGroup));
    	
        //Set up color chooser for setting text color
        final JColorChooser tcc = new JColorChooser(colorEditor.getForeground());
        ChangeListener colorEditorListener = new ChangeListener() {
        	public void stateChanged(ChangeEvent e) {
        		
                Color newColor = tcc.getColor();
                groupLabel.setBackground(newColor);
                Set<String> groups = AdminApplication.getColorChooser().keySet();
                int index = 0;
                int selectedIndex = groupSelection.getSelectedIndex();
                String selectedGroup = "";
                for(String group : groups){
                	if(index == selectedIndex){
                		selectedGroup = group;
                		break;
                	}
                	index++;
                }
                AdminApplication.getColorChooser().put(selectedGroup, newColor);
            }
		};
        tcc.getSelectionModel().addChangeListener(colorEditorListener);
        tcc.setBorder(BorderFactory.createTitledBorder("Choose Group Color"));

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(groupSelection);
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Group"));
        
        colorEditor.add(selectionPanel, BorderLayout.PAGE_START);
        colorEditor.add(groupPanel, BorderLayout.CENTER);
        colorEditor.add(tcc, BorderLayout.PAGE_END);
        colorDialog.getContentPane().add(colorEditor);
        return colorDialog;
    }
}