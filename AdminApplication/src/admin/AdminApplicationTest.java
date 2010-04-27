package admin;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;


public class AdminApplicationTest extends TestCase {

	public AdminApplication app;
	public Graph g1,g2,g3,g4,g5,g6;


	@Before
	public void setUp() throws Exception {
		
		 app = new AdminApplication();
	}



	@Test public void testOrganismsTerminal()
	{
		g1 = app.studentNameToTree.get("Organisms_Terminal_True1");
		g2 = app.studentNameToTree.get("Organisms_Terminal_True2");

		g3 = app.studentNameToTree.get("Organisms_Terminal_False1");
		g4 = app.studentNameToTree.get("Organisms_Terminal_False2");

		assertTrue(g1.allOrganismsTerminal());
		assertTrue(g2.allOrganismsTerminal());
		assertFalse(g3.allOrganismsTerminal());
		assertFalse(g4.allOrganismsTerminal());
	}

	public void testSingleCommonAncestor()
	{
		g1 = app.studentNameToTree.get("Single_Common_Ancestor_True1");
		g2 = app.studentNameToTree.get("Single_Common_Ancestor_True2");
		g3 = app.studentNameToTree.get("Single_Common_Ancestor_True2");

		g4 = app.studentNameToTree.get("Single_Common_Ancestor_False1");
		g5 = app.studentNameToTree.get("Single_Common_Ancestor_False2");

		assertTrue(g1.hasSingleCommonAncestor());
		assertTrue(g2.hasSingleCommonAncestor());
		assertTrue(g3.hasSingleCommonAncestor());
		assertFalse(g4.hasSingleCommonAncestor());
		assertFalse(g5.hasSingleCommonAncestor());
	}

	public void testGroupsAreLabelled()
	{
		g1 = app.studentNameToTree.get("Groups_Labelled_True1");
		
		g2 = app.studentNameToTree.get("Groups_Labelled_False2");
		assertTrue(g1.groupsAreLabelled());
		assertFalse(g2.groupsAreLabelled());

	}

	public void testIncludesAllOrganisms()
	{
		g1 = app.studentNameToTree.get("All_Organisms_Included_True1");
		g2 = app.studentNameToTree.get("All_Organisms_Included_True2");
		g3 = app.studentNameToTree.get("All_Organisms_Included_True3");;
		g4 = app.studentNameToTree.get("All_Organisms_Included_False4");
		
		assertTrue(g1.includesAllOrganisms());
		assertTrue(g2.includesAllOrganisms());
		assertTrue(g3.includesAllOrganisms());
		assertFalse(g4.includesAllOrganisms());
	}

	public void testHasBranches()
	{
		g1 = app.studentNameToTree.get("Tree_Branches_True1");
		g2 = app.studentNameToTree.get("Tree_Branches_True2");
		g3 = app.studentNameToTree.get("Tree_Branches_True3");;
		g4 = app.studentNameToTree.get("Tree_Branches_False4");
		g5 = app.studentNameToTree.get("Tree_Branches_False5");
		g6 = app.studentNameToTree.get("Tree_Branches_False1");

		assertTrue(g1.hasBranches());
		assertTrue(g2.hasBranches());
		assertTrue(g3.hasBranches());
		assertFalse(g4.hasBranches());
		assertFalse(g5.hasBranches());
		assertFalse(g6.hasBranches());

	}

	public void testGroupingVertInvert()
	{
		g1 = app.studentNameToTree.get("Grouping_Vert_100%_Invert_100%_1");
		g2 = app.studentNameToTree.get("Grouping_Vert_100%_Invert_100%_2");
		g3 = app.studentNameToTree.get("Grouping_Vert_100%_Invert_100%_3");;
		g4 = app.studentNameToTree.get("Grouping_Vert_10_Invert_8_1");
		g5 = app.studentNameToTree.get("Grouping_Vert_10_Invert_8_2");

		assertEquals(g1.groupingVertebrates(), 1, 0.0001);
		assertEquals(g1.groupingInvertebrates(), 1, 0.0001);
		assertEquals(g2.groupingVertebrates(), 1, 0.0001);
		assertEquals(g2.groupingInvertebrates(), 1, 0.0001);
		assertEquals(g3.groupingVertebrates(), 1, 0.0001);
		assertEquals(g3.groupingInvertebrates(), 1, 0.0001);
		assertEquals(g4.groupingVertebrates(), 0.9090, 0.0001);
		assertEquals(g4.groupingInvertebrates(), 0.8888, 0.0001);
		assertEquals(g5.groupingVertebrates(), 0.9090, 0.0001);
		assertEquals(g5.groupingInvertebrates(), 0.8888, 0.0001);
	
	}

	public void testGroupingMammalNonMammal()
	{
	}
	public static void main(String args[])
	{
		org.junit.runner.JUnitCore.main("AdminApplicationTest");
	}
}
