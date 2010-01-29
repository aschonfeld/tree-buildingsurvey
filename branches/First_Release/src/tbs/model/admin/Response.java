package tbs.model.admin;

import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public abstract class Response {

	private boolean completed;
	
	public Response(){
		completed = false;
	}
	
	public boolean isCompleted(){return completed;}
	public void setCompleted(boolean completed){this.completed = completed;}
	public abstract String getText();
	public abstract void updateText(String input);
	public abstract void updateText(int index, OpenQuestionPromptButtonType answer);
}
