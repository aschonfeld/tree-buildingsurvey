package tbs.model.history;

import tbs.model.StudentModel;


public abstract class Command {
	
	public abstract void execute(StudentModel model);

	public abstract void undo(StudentModel model);

	public abstract String toString();
}
