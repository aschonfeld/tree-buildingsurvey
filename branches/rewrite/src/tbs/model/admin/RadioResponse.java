package tbs.model.admin;

import java.util.LinkedList;
import java.util.List;

import tbs.TBSGraphics;
import tbs.view.prompt.buttons.OpenQuestionPromptButtonType;

public class RadioResponse extends Response{

	private List<OpenQuestionPromptButtonType> radioAnswers;
	
	public RadioResponse(String input){
		super();
		initRadio(input);
	}
	
	public String getText() {
		String radioAnswersToString = "";
		for(OpenQuestionPromptButtonType answer : radioAnswers)
			radioAnswersToString += "" + answer.ordinal()+",";
		return radioAnswersToString.substring(0, radioAnswersToString.length()-1);
	}
	
	public List<OpenQuestionPromptButtonType> getRadioAnswers(){
		return radioAnswers;
	}
	
	private void initRadio(String input){
		System.out.println("Radio response:" + input);
		radioAnswers = new LinkedList<OpenQuestionPromptButtonType>();
		String answerText = input == null ? "" : input.trim();
		int numRadios = TBSGraphics.numberOfRadioQuestions;//Default number of radio questions
		if(answerText == null || answerText.length() == 0){
			for(int i=0;i<numRadios;i++)
				radioAnswers.add(OpenQuestionPromptButtonType.SUBMIT);
		}else{
			for(String answer : answerText.split(",")){
				if(!"0".equals(answer))
					setCompleted(true);
				radioAnswers.add(convertStringToRadioAnswer(answer));
			}
			if(radioAnswers.size() < numRadios){
				for(int i=radioAnswers.size();i<=numRadios;i++)
					radioAnswers.add(OpenQuestionPromptButtonType.SUBMIT);
			}else if(radioAnswers.size() > numRadios){
				while(radioAnswers.size() != numRadios)
					radioAnswers.remove(radioAnswers.size()-1);
			}
		}
	}
	
	public void updateText(int index, OpenQuestionPromptButtonType answer){
		radioAnswers.set(index, answer);
		setCompleted(true);
	}

	private OpenQuestionPromptButtonType convertStringToRadioAnswer(String numberString){
		int answerNum = 0;
		try{
			answerNum = Integer.parseInt(numberString);
		}catch(NumberFormatException e){
			System.out.println("RadioResponse:Number format exception for answer text: " + numberString);
			return OpenQuestionPromptButtonType.SUBMIT;
		}
		return OpenQuestionPromptButtonType.values()[answerNum];
	}

	public void updateText(String input) {}	

}
