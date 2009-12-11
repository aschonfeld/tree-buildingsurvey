package tbs.view;

public enum TBSQuestionButtonType {

	ONE("1","questionOne"),
	TWO("2", "questionTwo"),
	THREE("3", "questionThree.one");
	
	private String text;
	
	private String questionKey;
	
	private TBSQuestionButtonType(String text, String questionKey){
		this.text = text;
		this.questionKey = questionKey;
	}
	
	public String getText(){
		return text;	
	}
	
	public String getQuestionKey(){
		return questionKey;	
	}
	
	@Override
	public String toString(){
		return text;
	}
}
