package admin;

import java.awt.event.WindowAdapter;

public class AdminMultiWindow extends WindowAdapter {

	public AdminApplication adminApplicationFrame;
	public StudentDataTable studentDataTableFrame;
	public QuestionDisplay questionDisplayFrame;
	public ShortestPathTable shortestPathTableFrame;

	public AdminMultiWindow() {
		adminApplicationFrame = new AdminApplication();
		adminApplicationFrame.setLocation(0, 0);
		studentDataTableFrame = new StudentDataTable(adminApplicationFrame);
		studentDataTableFrame.setLocation(100, 100);
		questionDisplayFrame = new QuestionDisplay(adminApplicationFrame);
		shortestPathTableFrame = new ShortestPathTable();
	}
}
