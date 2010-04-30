package tbs.model.admin;

import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class WrittenResponse extends Response {

	private String text;

	public WrittenResponse(String input) {
		initWritten(input);
	}

	public String getText() {
		return text;
	}

	public void updateText(String input) {
		text = input;
		if (input != null && input.length() > 0)
			setCompleted(true);
		else
			setCompleted(false);
	}

	private void initWritten(String input) {
		if (input != null && input.length() > 0) {
			text = input;
			setCompleted(true);
		} else
			text = "";
	}

	public void updateText(int index, OpenQuestionPromptButtonType answer) {
	}

}
