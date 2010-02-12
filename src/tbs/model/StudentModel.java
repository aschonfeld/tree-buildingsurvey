//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import tbs.TBSApplet;
import tbs.TBSGraphics;
import tbs.TBSUtils;
import tbs.controller.StudentControllerTest;
import tbs.model.admin.Student;
import tbs.model.history.Command;
import tbs.model.history.Unlink;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.prompt.Prompt;
import tbs.view.prompt.student.HelpPrompt;
import tbs.view.prompt.student.RadioQuestionPrompt;
import tbs.view.prompt.student.TextEntryBox;
import tbs.view.prompt.student.WrittenQuestionPrompt;
import tbs.view.prompt.student.YesNoPrompt;

public class StudentModel extends TBSModel
{
	private Stack<Command> history;
	private WrittenQuestionPrompt writtenQuestionPrompt;
	private RadioQuestionPrompt radioQuestionPrompt;
	private HelpPrompt helpPrompt;
	private TextEntryBox textEntryBox;
	private Map<TBSButtonType, Boolean> buttonStates;
	private StudentControllerTest sct;

	public StudentModel(TBSApplet applet, List<OrganismNode> organisms,
			String studentString) {
		super(applet, organisms);
		addElement(new EmptyNode(getSerial()));//Immortal Empty Node
		buttonStates = new HashMap<TBSButtonType, Boolean>();
		for(TBSButtonType b : TBSButtonType.getButtons(false))
			buttonStates.put(b, b.isActiveWhenCreated());
		Student student = new Student(studentString, 1);
		setStudent(student);
		String tree = student.getTree();
		if(!TBSUtils.isStringEmpty(tree)){
			loadTree(tree);
			int inTreeElementCount = inTreeElements().size();
			if(inTreeElementCount > 0){
				buttonStates.put(TBSButtonType.DELETE, true);
				buttonStates.put(TBSButtonType.CLEAR, true);
				if(inTreeElementCount > 1){
					buttonStates.put(TBSButtonType.LINK, true);
					if(hasEmptyNodes())
						buttonStates.put(TBSButtonType.LABEL, true);
					if(hasConnections())
						buttonStates.put(TBSButtonType.UNLINK, true);
				}
			}
		}
		helpPrompt = new HelpPrompt(this);
		writtenQuestionPrompt = new WrittenQuestionPrompt(this);
    	textEntryBox = new TextEntryBox(this);
		history = new Stack<Command>();
		/*
		 * Until Professor White says otherwise we will be eliminating the radio
		 * portion of the open-response
		 * radioQuestionPrompt = new RadioQuestionPrompt(this);
		 */
		sct = null;
	}

	public Stack<Command> getHistory() {
		return history;
	}

	public void setHistory(Stack<Command> history) {
		this.history = history;
	}

	public void addActionToHistory(Command c){
		if(history.isEmpty())
			buttonStates.put(TBSButtonType.UNDO, true);
		history.push(c);
		System.out.println(new StringBuffer("Added action(").append(c.toString())
				.append(") to history.").toString());
	}

	public Command removeActionFromHistory(){
		Command c = history.pop();
		if(c instanceof Unlink)
			buttonStates.put(TBSButtonType.UNLINK, true);
		if(history.isEmpty() || history.size() == 0)
			buttonStates.put(TBSButtonType.UNDO, false);
		return c;
	}

	public void updateButtonStatesAfterRemove(){
		if(!hasConnections())
			buttonStates.put(TBSButtonType.UNLINK, false);
		List<ModelElement> inTree = inTreeElements();
		if(inTree.isEmpty()){
			buttonStates.put(TBSButtonType.LINK, false);
			buttonStates.put(TBSButtonType.DELETE, false);
			buttonStates.put(TBSButtonType.LABEL, false);
			buttonStates.put(TBSButtonType.CLEAR, false);
		}else{
			if(inTree.size() < 2)
				buttonStates.put(TBSButtonType.LINK, false);
			if(!hasEmptyNodes())
				buttonStates.put(TBSButtonType.LABEL, false);
		}
	}

	public void viewPrompt(OpenQuestionButtonType currentQuestion) {
		if(currentQuestion.isRadio()){
			radioQuestionPrompt.setCurrentQuestion(currentQuestion);
			setPrompt(radioQuestionPrompt);
		}else{
			writtenQuestionPrompt.setCurrentQuestion(currentQuestion);
			setPrompt(writtenQuestionPrompt);
		}
	}

	public void viewPrompt(TBSButtonType button) {
    	Prompt p;
    	if(TBSButtonType.LABEL.equals(button)){
      		textEntryBox.initLabeling();
      		textEntryBox.setFinished(false);
      		p = textEntryBox;
    	}else
      		p = new YesNoPrompt(this, TBSButtonType.CLEAR);
		setPrompt(p);
	}

	public void helpUser() {
		setPrompt(helpPrompt);
	}

	public Map<TBSButtonType, Boolean> getButtonStates() {
		return buttonStates;
	}

	public Boolean isButtonActive(TBSButtonType button){
		return buttonStates.get(button);
	}
  
	public StudentControllerTest getStudentControllerTest() {
		return sct;
	}

	public void setStudentControllerTest(StudentControllerTest sct) {
		this.sct = sct;
	}

	public List<String> incompletedItems(){
		List<String> incompletedItems = new LinkedList<String>();
		if(inTreeElements().isEmpty())
			incompletedItems.add("the tree");
		for(OpenQuestionButtonType q : OpenQuestionButtonType.values()){
			if(!getStudent().getResponse(q).isCompleted())
				incompletedItems.add(q.getAdminText());
		}
		return incompletedItems;
	}

	public String unusedOrganisms(){
		StringBuffer unusedString = new StringBuffer();
		StringBuffer unusedStartString = new StringBuffer();
		for(int i=0;i<TBSGraphics.numOfOrganisms;i++){
			OrganismNode o = (OrganismNode) getElement(i);
			if(!o.isInTree())
				unusedString.append("\t").append(o.getName()).append("\n");
		}
		if(unusedString.length() > 0)
			return unusedStartString.append(unusedString).append("\n").toString();
		return "";
	}

	public String surveyStatus(){
		StringBuffer statusString = new StringBuffer("");
		List<String> incompletedItems = incompletedItems();
		if(incompletedItems.isEmpty()){
			return "";
		}else{
			if(incompletedItems.size() == 1){
				statusString.append("Currently you still need to complete ");
				statusString.append(incompletedItems.remove(0)).append(". ");
			}else if(incompletedItems.size() <= OpenQuestionButtonType.values().length+1){
				statusString.append("Currently you still need to complete ");
				statusString.append(incompletedItems.remove(0));
				String statusEnd = incompletedItems.remove(incompletedItems.size()-1);
				for(String s : incompletedItems)
					statusString.append(", ").append(s);
				statusString.append(" & " + statusEnd + ". ");
			}
		}
		return statusString.toString();
	}

}
