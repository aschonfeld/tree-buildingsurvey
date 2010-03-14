package admin;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class QuestionDisplay extends JFrame {
	
	private static final long serialVersionUID = 6831734957022816424L;
	private AdminApplication parent;
	private JEditorPane editorPane;
	
	private static String question1 = new String(
			"<b><i>Question One</i>: " + Common.questions[0] +	"</b><br><br>"
	);
	private static String question2 = new String(
			"<br><br><b><i>Question Two</i>: " + Common.questions[1] + "</b><br><br>"
	);
	
	QuestionDisplay(AdminApplication parent) {
		super("TBS Student Open Response");
		this.parent = parent;
		initEditorPane();
	}
	
	private void initEditorPane() {
		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		setAnswersText(null);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setSize(new Dimension(400, 400));
        add(scrollPane);
        setPreferredSize(new Dimension(400, 400));
        setAnswersText(parent.graphs.get(0).getAnswers());
	}
	
	public void setAnswersText(ArrayList<String> answers) {
		String answer1 = "<i>No answer given</i>";
		String answer2 = "<i>No answer given</i>";
		if(answers != null) {
			if(answers.size() > 0) {
				answer1 = answers.get(0);
			}
			if(answers.size() > 1) {
				answer2 = answers.get(1);
			}
		}
		String text = new String(question1 + answer1 + question2 + answer2);
		editorPane.setText(text);
		editorPane.revalidate();
	}

}
