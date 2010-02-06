//TBS version 0.4
//Model: creates and maintains the logical structure underlying TBS

package tbs.model;

import java.util.List;

import tbs.TBSApplet;
import tbs.TBSUtils;
import tbs.model.admin.Student;
import tbs.view.prompt.admin.AnalysisPrompt;
import tbs.view.prompt.admin.RadioQuestionReviewPrompt;
import tbs.view.prompt.admin.WrittenQuestionReviewPrompt;

public class AdminModel extends TBSModel
{
	private WrittenQuestionReviewPrompt writtenQuestionReviewPrompt;
	private RadioQuestionReviewPrompt radioQuestionReviewPrompt;
	private AnalysisPrompt analysisPrompt;
	private List<Student> students;

	public AdminModel(TBSApplet applet,	List<OrganismNode> organisms, List<Student> students) {
		super(applet, organisms);
		this.students = students;
		Student student = this.students.get(0);
		setStudent(student);
		String tree = student.getTree();
		if(!TBSUtils.isStringEmpty(tree))
			loadTree(tree);
		writtenQuestionReviewPrompt = new WrittenQuestionReviewPrompt(this);
		/*
		 * Until Professor White says otherwise we will be eliminating the radio
		 * portion of the open-response
		 * radioQuestionReviewPrompt = new RadioQuestionReviewPrompt(this);
		 */
	}

	public void changeSavedTree(int studentIndex){
		/*
		 * Make sure your don't re-calculate the selected student's
		 * information
		 */
		System.out.println("Selected Index:" + studentIndex);
		if(studentIndex != students.indexOf(getStudent())){
			Student student = students.get(studentIndex);
			setStudent(student);
			String tree = student.getTree();
			resetModel();
			if(!TBSUtils.isStringEmpty(tree))
				loadTree(tree);
			writtenQuestionReviewPrompt = null;
			analysisPrompt = null;
		}
	}

	public List<Student> getStudents(){
		return students;
	}

	public void questionReview() {
		if(writtenQuestionReviewPrompt == null)
			writtenQuestionReviewPrompt = new WrittenQuestionReviewPrompt(this);
		else
			writtenQuestionReviewPrompt.setFinished(false);
		setPrompt(writtenQuestionReviewPrompt);
		getView().refreshGraphics();
	}

	public void analyze(){
		if(analysisPrompt == null)
			analysisPrompt = new AnalysisPrompt(this);
		else
			analysisPrompt.setFinished(false);
		setPrompt(analysisPrompt);
		getView().refreshGraphics();
	}
}
