
package admin;

import java.awt.event.WindowAdapter;

public class AdminMultiWindow extends WindowAdapter {

	public AdminApplication adminApplicationFrame;
	public StudentDataTable studentDataTableFrame;
	public QuestionDisplay questionDisplayFrame;
	//public ShortestPathTable shortestPathTableFrame;
	
	public AdminMultiWindow() {
		adminApplicationFrame = new AdminApplication();
		adminApplicationFrame.setLocation(0, 0);
	    //adminApplicationFrame.setVisible(true);
	    studentDataTableFrame = new StudentDataTable(adminApplicationFrame);
	    studentDataTableFrame.setLocation(100, 100);
	    //studentDataTableFrame.setVisible(true);
	    questionDisplayFrame = new QuestionDisplay(adminApplicationFrame);
	    questionDisplayFrame.setLocation(200, 200);
	    //questionDisplayFrame.setVisible(true);
	    //shortestPathTableFrame = new ShortestPathTable();
	    //shortestPathTableFrame.setLocation(300, 300);
	}
}
