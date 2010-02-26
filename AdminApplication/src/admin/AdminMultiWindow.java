
package admin;

import java.awt.event.WindowAdapter;

public class AdminMultiWindow extends WindowAdapter {

	public AdminApplication adminApplicationFrame;
	public StudentDataTable studentDataTableFrame;
	
	public AdminMultiWindow() {
		adminApplicationFrame = new AdminApplication();
	    adminApplicationFrame.setVisible(true);
	    studentDataTableFrame = new StudentDataTable();
	    studentDataTableFrame.setVisible(true);
	}
}
