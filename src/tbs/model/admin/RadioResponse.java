package tbs.model.admin;

import java.util.LinkedList;
import java.util.List;

import tbs.TBSUtils;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class RadioResponse extends Response {

	private List<OpenQuestionPromptButtonType> radioAnswers;

	private int questionCount;

	public RadioResponse(String input, int questionCount) {
		super();
		initRadio(input, questionCount);
	}

	public String getText() {
		StringBuffer radioAnswersToString = new StringBuffer();
		for (OpenQuestionPromptButtonType answer : radioAnswers)
			radioAnswersToString.append("").append(answer.ordinal())
					.append(",");
		return radioAnswersToString.substring(0,
				radioAnswersToString.length() - 1);
	}

	public List<OpenQuestionPromptButtonType> getRadioAnswers() {
		return radioAnswers;
	}

	private void initRadio(String input, int questionCount) {
		this.questionCount = questionCount;
		System.out.println("Radio response:" + input);
		radioAnswers = new LinkedList<OpenQuestionPromptButtonType>();
		String answerText = input == null ? "" : input.trim();
		if (TBSUtils.isStringEmpty(answerText)) {
			for (int i = 0; i < questionCount; i++)
				radioAnswers.add(OpenQuestionPromptButtonType.SUBMIT);
		} else {
			for (String answer : answerText.split(",")) {
				if (!"0".equals(answer))
					setCompleted(true);
				radioAnswers.add(convertStringToRadioAnswer(answer));
			}
			if (radioAnswers.size() < questionCount) {
				for (int i = radioAnswers.size(); i <= questionCount; i++)
					radioAnswers.add(OpenQuestionPromptButtonType.SUBMIT);
			} else if (radioAnswers.size() > questionCount) {
				while (radioAnswers.size() != questionCount)
					radioAnswers.remove(radioAnswers.size() - 1);
			}
		}
	}

	public void updateText(int index, OpenQuestionPromptButtonType answer) {
		radioAnswers.set(index, answer);
		setCompleted(true);
	}

	private OpenQuestionPromptButtonType convertStringToRadioAnswer(
			String numberString) {
		int answerNum = 0;
		try {
			answerNum = Integer.parseInt(numberString);
		} catch (NumberFormatException e) {
			System.out
					.println("RadioResponse:Number format exception for answer text: "
							+ numberString);
			return OpenQuestionPromptButtonType.SUBMIT;
		}
		return OpenQuestionPromptButtonType.values()[answerNum];
	}

	public void updateText(String input) {
	}

	public int getQuestionCount() {
		return questionCount;
	}

}
