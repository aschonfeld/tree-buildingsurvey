//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.awt.Point;
import java.util.Collections;
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
import tbs.model.history.Add;
import tbs.model.history.Command;
import tbs.model.history.Delete;
import tbs.model.history.Drag;
import tbs.model.history.Link;
import tbs.model.history.Unlink;
import tbs.view.OpenQuestionButtonType;
import tbs.view.TBSButtonType;
import tbs.view.TextEntryBox;
import tbs.view.prompt.student.HelpPrompt;
import tbs.view.prompt.student.RadioQuestionPrompt;
import tbs.view.prompt.student.WrittenQuestionPrompt;
import tbs.view.prompt.student.YesNoPrompt;

public class StudentModel extends TBSModel
{
	private ModelElement selectedElement;
	private List<ModelElement> selectedTwoWay;
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
		selectedElement = null;
		selectedTwoWay = null;
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
		history = new Stack<Command>();
		/*
		 * Until Professor White says otherwise we will be eliminating the radio
		 * portion of the open-response
		 * radioQuestionPrompt = new RadioQuestionPrompt(this);
		 */
		sct = null;
	}

	public ModelElement getSelectedElement() {
		return selectedElement;
	}

	public void setSelectedElement(ModelElement selectedModelElement) {
		this.selectedElement = selectedModelElement;
	}

	public List<ModelElement> getSelectedTwoWay() {
		return selectedTwoWay;
	}

	public void setSelectedTwoWay(List<ModelElement> selectedTwoWay) {
		this.selectedTwoWay = selectedTwoWay;
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

	public void addToTree(Node n)
	{
		Node newNode;
		if (n instanceof EmptyNode && !n.isInTree()) {//Immortal Empty Node
			newNode = new EmptyNode(getSerial());
			newNode.setAnchorPoint(n.getAnchorPoint());
			newNode.setInTree(true);
			addElement(newNode);
			n.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
			buttonStates.put(TBSButtonType.LABEL, true);
		} else {
			n.setInTree(true);
			newNode = n;
		}
		if(history.peek() instanceof Drag)
			removeActionFromHistory();
		try{
			addActionToHistory(new Add((Node) newNode.clone()));
		}catch(CloneNotSupportedException c){
			System.out.println("Unable to add action to history.");
		}
		buttonStates.put(TBSButtonType.DELETE, true);
		if(inTreeElements().size() > 1)
			buttonStates.put(TBSButtonType.LINK, true);
		buttonStates.put(TBSButtonType.CLEAR, true);
	}

	public void addConnection(Node from, Node to){
		addConnection(from, to, -1);
	}

	public void addConnection(Node from, Node to, int id)
	{
		Connection newConn = new Connection(id == -1 ? getSerial() : id, from, to);
		if(id == -1)
			addElement(newConn);
		else{
			if(id < elementCount())
				addElement(id, newConn);
			else{
				addElement(newConn);
				Collections.sort(getElements(), TBSGraphics.elementIdComparator);
			}
		}
		from.addConnectionTo(to);
		to.addConnectionFrom(from);
		if(!getController().getButtonClicked().equals(TBSButtonType.UNDO)){
			try{
				addActionToHistory(new Link((Connection) newConn.clone()));
			}catch(CloneNotSupportedException c){
				System.out.println("Unable to add action to history.");
			}
		}
		buttonStates.put(TBSButtonType.UNLINK, true);
	}

	/**
	 * Unlink had to live in Model when connections were
	 * one-way. Now, this simply calls the Node-based two-way unlink.
	 */
	public void unlink(Node n)
	{
		List<Connection> connections = getConnectionsByNode(n);
    if(getController().getButtonClicked().equals(TBSButtonType.UNLINK)){
      Unlink unlink = new Unlink();
      unlink.addConnections(connections);
      addActionToHistory(unlink);
    }
		if(!history.isEmpty()){
			Command comm = history.peek();
			if(comm instanceof Delete)
				((Delete) comm).setElementConnections(connections);
		}
		for(Connection c : connections)
			removeConnection(c);
		updateButtonStatesAfterRemove();
	}

	public void removeFromTree(ModelElement m){
		if(m == null)
			return;
		if(m instanceof Node){
			Node n = (Node) m;
			if(n instanceof EmptyNode && !n.isInTree()){//Immortal Empty Node
				removeActionFromHistory();
				System.out.println("Invalid drag move removed from history.");
				n.setAnchorPoint(new Point(TBSGraphics.emptyNodeLeftX, TBSGraphics.emptyNodeUpperY));
				return;
			}
			Command c = null;
			if(!history.isEmpty())
				c = history.peek();
			try{
				if(getController().getButtonClicked().equals(TBSButtonType.DELETE)){
					if(c != null && c instanceof Drag){
						Node copy = (Node) n.clone();
						copy.setAnchorPoint(((Drag) c).getPointBefore());
						removeActionFromHistory();
						System.out.println("Invalid drag move removed from history.");
						addActionToHistory(new Delete(copy));
					}else
						addActionToHistory(new Delete((Node) n.clone()));
				}
			}catch(CloneNotSupportedException e){
				System.out.println("Unable to add action to history.");
			}
			unlink(n);
			if(n instanceof OrganismNode)
				((OrganismNode) n).reset();
			else
				removeElement(m);
		}else{
			Connection c = (Connection) m;
			if(getController().getButtonClicked().equals(TBSButtonType.DELETE)){
				try{
					if(selectedTwoWay != null){
						Command command = history.peek();
						if(command instanceof Delete && ((Delete) command).getTwoWayConnection() != null)
							((Delete) command).addConnection((Connection) c.clone());
						else{
							addActionToHistory(new Delete());
							((Delete) history.peek()).addConnection((Connection) c.clone());
						}
					}else{
						addActionToHistory(new Delete((Connection) c.clone()));
					}
				}catch(CloneNotSupportedException e){
					System.out.println("Unable to add action to history.");
				}
			}
			removeConnection(c);
		}
		updateButtonStatesAfterRemove();
	}

	public void updateButtonStatesAfterRemove(){
		if(!hasConnections())
			buttonStates.put(TBSButtonType.UNLINK, false);
		List<Node> inTree = inTreeElements();
		if(inTree.size() == 0){
			buttonStates.put(TBSButtonType.LINK, false);
			buttonStates.put(TBSButtonType.DELETE, false);
			buttonStates.put(TBSButtonType.LABEL, false);
			buttonStates.put(TBSButtonType.CLEAR, false);
		}else if(inTree.size() < 2)
			buttonStates.put(TBSButtonType.LINK, false);
		else if(!hasEmptyNodes())
			buttonStates.put(TBSButtonType.LABEL, false);
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
		setPrompt(new YesNoPrompt(this, TBSButtonType.CLEAR));
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

	public TextEntryBox getTextEntryBox() {
		return textEntryBox;
	}

	public void setTextEntryBox(TextEntryBox textEntryBox) {
		this.textEntryBox = textEntryBox;
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
