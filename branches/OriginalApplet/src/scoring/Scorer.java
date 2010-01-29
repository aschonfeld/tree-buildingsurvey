package scoring;

import java.util.Iterator;

import phylogenySurvey.Link;
import phylogenySurvey.OrganismLabel;
import phylogenySurvey.SurveyData;

public class Scorer {

	private static Scorer instance;
	private SurveyData data;

	private Scorer() {

	}

	public static Scorer getInstance() {
		if (instance == null) {
			instance = new Scorer();
		}
		return instance;
	}

	public String score(SurveyData data) {
		this.data = data;
		StringBuffer scoreBuffer = new StringBuffer();
		scoreBuffer.append("<html>\n");
		scoreBuffer.append(checkNumberOfConnections());

		scoreBuffer.append("</html>");
		System.out.println(scoreBuffer.toString());
		return scoreBuffer.toString();
	}

	/*
	 * go through each organism and see how many links connect to it
	 *   all should have one and only one
	 *   if any = 0, they're not connected
	 *   if any > 1, they're not at the end of a branch
	 */
	private String checkNumberOfConnections() {
		boolean allConnected = true;
		boolean allAtEndOfBranch = true;
		
		Iterator<OrganismLabel> orgIt = data.getOrganismLabels().iterator();
		while (orgIt.hasNext()) {
			OrganismLabel org = orgIt.next();
			int links = countLinks(org);
			if (links == 0) allConnected = false;
			if (links > 1) allAtEndOfBranch = false;
		}
		
		StringBuffer resultBuf = new StringBuffer();
		if (allConnected) {
			resultBuf.append("All Connected to Tree<br>\n");
		} else {
			resultBuf.append("<font color=red>Not all connected to Tree</font><br>\n");
		}
		if (allAtEndOfBranch) {
			resultBuf.append("All at ends of branches<br>\n");
		} else {
			resultBuf.append("<font color=red>Not all at ends of branches</font><br>\n");
		}
		
		return resultBuf.toString();
	}
	
	/* 
	 * see how many links connect to this organismLabel
	 */
	private int countLinks(OrganismLabel org) {
		int count = 0;
		Iterator<Link>linkIt = data.getLinks().iterator();
		while (linkIt.hasNext()) {
			Link link = linkIt.next();
			if ((link.getOneLabel() == org) || (link.getOtherLabel() == org)) {
				count++;
			}
		}
		return count;
	}
}
