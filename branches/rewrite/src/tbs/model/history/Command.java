package tbs.model.history;

import tbs.model.TBSModel;


public abstract class Command {
	
	public abstract void execute(TBSModel model);

	public abstract void undo(TBSModel model);

}
