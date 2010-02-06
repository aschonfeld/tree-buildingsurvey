import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import java.awt.event.ActionEvent;

public class ActionHandler extends JPanel {    
	/**
	 * 8-byte serialization class ID generated by
	 * https://www.fourmilab.ch/hotbits/secure_generate.html
	 */
	private static final long serialVersionUID = 0x60655B840361BFA4L;
	
	public Action exitAction;
	public Action printAction;
	public Action nextAction;
	public AdminApplication parent;
	
	
	public class ExitAction extends AbstractAction {

		private static final long serialVersionUID = 1740545338294704279L;

		public ExitAction() {
			super("Exit");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			System.exit(0);
		}
	}
	
	public class PrintAction extends AbstractAction {

		private static final long serialVersionUID = 1740545322294704279L;

		public PrintAction() {
			super("Print To Console");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			parent.printGraphInfo();
		}
	}
	
	public class NextAction extends AbstractAction {

		private static final long serialVersionUID = 1740545444294704279L;

		public NextAction() {
			super("Next Graph");
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			parent.nextGraph();
		}
	}
	
    public ActionHandler() {
        exitAction = new ExitAction();
        printAction = new PrintAction();
        nextAction = new NextAction();
    }
    
    public void setParent(AdminApplication parent) {
    	this.parent = parent;
    }
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu fileMenu;
        JMenuItem printItem;
        JMenuItem nextItem;
        JMenuItem exitItem;
        
        //Create the menu bar.
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        //a group of JMenuItems
        printItem = new JMenuItem(printAction);
        nextItem = new JMenuItem(nextAction);
        exitItem = new JMenuItem(exitAction);
        fileMenu.add(printItem);
        fileMenu.add(nextItem);
        fileMenu.add(exitItem);
        

       return menuBar;
    }
}
