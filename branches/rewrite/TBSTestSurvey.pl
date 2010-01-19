#!/usr/bin/perl

# set some constants
$admin_pw = "lab09acce55";
$too_late_month = 12;
$too_late_day = 12;
#$name_of_survey_field_in_assignments_txt = "Diversity of Life Survey (15)";

use DBI;
use CGI;
#use GradeDB;
use treeDB;

$googleCode_url = "http://code.google.com/p/tree-buildingsurvey/issues/list";
$script_url = "http://localhost:8080/PhylogenySurveyWeb/cgi-bin/TBSTestSurvey.pl";
#$script_url = "http://cluster.bio.whe.umb.edu/cgi-bin/Test/TBSTestSurvey.pl";
$jar_loc = "http://localhost:8080/PhylogenySurveyWeb/TBSRun.jar";
#$jar_loc = "http://cluster.bio.whe.umb.edu/Test/TBSRun.jar";


@date = localtime(time);
$too_late = 0;
if ($date[4] > $too_late_month) {
     $too_late = 1;
} elsif (($date[4] == $too_late_month) && ($date[3] > $too_late_day)) {
     $too_late = 1;
}

# if we enter without params, give the student login page
#  otherwise, process survey data
if ($ENV{'CONTENT_LENGTH'} == 0) {
	&login_page;
} else {
	&load_survey;
}

exit 1;

#------------------

sub login_page {
	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Login to the TBS Survey</title>\n";
	print "<SCRIPT type=\"text/javascript\">\n";
	print "function getAdminValue() {\n";
	print " document.form.Browser.value = navigator.userAgent;\n";
	print " if(document.form.AdminCB.checked) {\n";
	print "    document.form.AdminValue.value = \"true\";\n";
	print "  }\n";
	print "  return true;\n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor=\"#808000\">\n";
	
	if ($too_late == 1) {
	     print "<center><table><tr><td bgcolor=red><font color=black><font size=+3>\n";
	     print "<center>It is too late to complete the survey.</font><br>\n";
	     print "Sorry.</font></center></td></tr></table></center>\n";	
	}
	
	#print "<form action=\"$script_url\" method=\"POST\">\n";
	print "<form action=\"$script_url\" method=\"POST\" onsubmit=\"return getAdminValue();\" ";
  	print "name=\"form\">\n";
  	
	#$dbh = GradeDB::connect();
	$dbh = treeDB::connect();
	%name_grade_hash = ();
	#$sth = $dbh->prepare("SELECT * FROM students ORDER BY name");
	$sth = $dbh->prepare("SELECT name FROM student_testdata ORDER BY name");
    $sth->execute();
    while (@result = $sth->fetchrow_array()) {
        $name_grade_hash{$result[0]} = "";
    }
    $sth->finish();
	
	#$statement = "SELECT number FROM assignments WHERE name=\"$name_of_survey_field_in_assignments_txt\"";
    #$sth = $dbh->prepare($statement);
    #$sth->execute();
    #@result = $sth->fetchrow_array();
    #$sth->finish();
    #$index = "grade".$result[0];
    
    #foreach $name (sort keys %name_grade_hash) {
    #     $sth = $dbh->prepare("SELECT $index from students WHERE name = \"$name\"");
    #     $sth->execute();
    #     @result = $sth->fetchrow_array();
    #     $name_grade_hash{$name} = $result[0];
    #     $sth->finish();
    #}
	
	$dbh->disconnect();

	print "<font size=+3>Login to the diversity of life survey site</font><br>\n";
	print "Choose your name from this list:<br>\n";
    print "<select name=\"Name\" size=6>\n";
    #$count = 0;
    foreach $name (sort keys %name_grade_hash) {
         #$score = "";
         #if ($name_grade_hash{$name} ne "") {
         #     $score = "(".$name_grade_hash{$name}.")";
         #     $count++;
         #}
         #print "<option value=\"$name\">$name $score</option>\n";
         print "<option value=\"$name\">$name</option>\n";
    }
    print "</select><br>\n";
    #print "Enter your 8-digit UMS ID # (leave off the UMS):\n";
    #print "<input type=\"password\" name=\"Passwd\" size=20><br>\n";
    print "Admin:  \n";
    print "<input type=\"checkbox\" name=\"AdminCB\"><br>\n";
    print "<input type=\"submit\" value=\"Login\">\n";
    print "<input type=\"hidden\" name=\"AdminValue\" value=\"false\">\n";
    print "<input type=\"hidden\" name=\"Browser\" value=\"\">\n";
	print "</form>\n";
	print "<hr>\n";
	#print "$count surveys entered.<br>\n";
	
	print "</body></html>\n";
}

sub load_survey {

	$query = CGI->new();
	$name = $query->param('Name');
	#$password = $query->param('Passwd');
	$password = "pass";
	$admin = $query->param('AdminValue');
	#$dbh = GradeDB::connect();
	#$statement = "SELECT password FROM students WHERE name=\"$name\"";
	#$sth = $dbh->prepare($statement);
	#$sth->execute();
	#@result = $sth->fetchrow_array();
	#$sth->finish();
	#$pw = $result[0];
	     
	#if(&decrypt_pw($pw,$password) != 1){
	if($admin eq "true"){
		&load_admin_survey;
	}else{
		&load_student_survey;
	}
	#}else{
	#   print "Content-type: text/html\n\n";
	#   print "<html><head>\n";
	#   print "<title>Diversity of Life Survey - Login Failed</title>\n";"
	#	print "<body bgcolor=#FF8080>\n";
  	#   print "<br><font color=green><b>Error: Password incorrect 
   	#     	       for $name.</b></font><br>";
   	#   print "<a href=\"$script_url\">Click here to return to login screen</a>.\n";
   	#	print "</body></html>\n";
   	#	exit 1;
	#}
	#$dbh->disconnect();
}

sub load_student_survey {
	#$query = CGI->new();
	#$name = $query->param('Name');
	#$password = $query->param('Passwd');
	$password = "pass";
	$arrows = "true";
	$name = $query->param('Name');
	$Q1 = $query->param('Q1');
	$Q2 = $query->param('Q2');
	$Q3 = $query->param('Q3');
	$treeXML = $query->param('treeXML');
	$lastUpdate = $query->param('lastUpdate');
	$browser = $query->param('Browser');

	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Diversity of Life Survey for $name</title>\n";
	
	
	if ($too_late == 1) {
		print "<body bgcolor=#FF8080>\n";
  	    print "<br><font color=green><b>Sorry, it is too late to complete the survey.</b></font><br>";
   		print "</body></html>\n";
   		exit 1;
	}
	
	#$dbh = GradeDB::connect();
	#$statement = "SELECT password FROM students WHERE name=\"$name\"";
	#$sth = $dbh->prepare($statement);
	#$sth->execute();
	#@result = $sth->fetchrow_array();
	#$sth->finish();
	#$pw = $result[0];
	     
	#if(&decrypt_pw($pw,$password) != 1){
	if(1 != 1){
	    print "<body bgcolor=#FF8080>\n";
  	    print "<br><font color=green><b>Error: Password incorrect 
   	     	  for $name.</b></font><br>";
   	    print "<a href=\"$script_url\">Click here to return to login screen</a>.\n";
   		print "</body></html>\n";
   		exit 1;
	}
	#$dbh->disconnect();
	
    #see if there's already an entry for this student
	$dbh = treeDB::connect();
	$statement = "SELECT count(*) from student_testdata WHERE name=?";
	$sth = $dbh->prepare($statement);
	$sth->execute($name);
	$rowcount = $sth->fetchrow();
	$sth->finish();

	#see if they're entering data
	if ($treeXML ne "") {
	
	    if ($rowcount == 0) {
	        $statement = "INSERT INTO student_testdata (Q1, Q2, Q3, tree, date, name) VALUES (?,?,?,?,NOW(),?)";
	    }  else {
	        $statement = "UPDATE student_testdata SET Q1 = ?, Q2 = ?, Q3 = ?, tree = ?, date = NOW() WHERE name = ?";
	    }
	    $sth = $dbh->prepare($statement);
	    $sth->execute($Q1, $Q2, $Q3, $treeXML, $name);
	    $sth->finish();
	    print "<body bgcolor=#FF8080>\n";
  	    print "<br><font color=green><b>$name, thank you for your survey submission.</b></font><br>";
   	    print "<a href=\"$script_url\">Click here to return to login screen</a>.\n";
   		print "</body></html>\n";
   		exit 1;
	} else {
	    # if there's already data there, get it
	    if ($rowcount != 0) {
	        $sth = $dbh->prepare("SELECT Q1, Q2, Q3, tree, date FROM student_testdata WHERE name=?");
	        $sth->execute($name);
	        ($Q1, $Q2, $Q3, $treeXML, $lastUpdate) = $sth->fetchrow_array();
	        $sth->finish();
	    }
	}
	
	$dbh->disconnect();
	
	if (($Q1 eq "") || ($Q2 eq "") || ($Q3 =~ /0/)) {
	     $complete = 0;
	} else {
	     $complete = 1;
	}
	
	print "<SCRIPT language=\"JavaScript\">\n";
	print "function isComplete() {\n";
	print " var xml = document.TreeApplet.getTree();\n";
	print " var q1 = document.TreeApplet.getQ1(); \n";
	print " var q2 = document.TreeApplet.getQ2(); \n";
	print " var q3 = document.TreeApplet.getQ3(); \n";
	print "    if(q1 == null || q1 == \"\"){ \n";
	print "     	alert(\"Please complete question 1!\");\n";
	print "         return false; \n";
	print "    } \n";
	print "    if(q2 == null || q2 == \"\"){ \n";
	print "     	alert(\"Please complete question 2!\");\n";
	print "         return false; \n";
	print "    } \n";
	print "    if(q3 == null || q3 == \"\"  || q3 == \"0,0,0,0,0,0,0,0,0,0,0,0,0\"){ \n";
	print "     	alert(\"Please complete question 3!\");\n";
	print "         return false; \n";
	print "    } \n";
	print "    document.forms[0].treeXML.value = xml;\n";
	print "    document.forms[0].Q1.value = q1;\n";
	print "    document.forms[0].Q2.value = q2;\n";
	print "    document.forms[0].Q3.value = q3;\n";
	print "    return true; \n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor = \"lightblue\" style=\"border: 0;padding: 0;margin:0;\">\n"; 
	
	if ($complete != 0) {
	     #enter the grade
	     #$dbh = GradeDB::connect();
		 #$statement = "SELECT number FROM assignments WHERE name=\"$name_of_survey_field_in_assignments_txt\"";
         #$sth = $dbh->prepare($statement);
         #$sth->execute();
         #@result = $sth->fetchrow_array();
         #$sth->finish();
         #$index = "grade".$result[0];
         #$statement = "UPDATE students SET $index = \"15\" WHERE name=\"$name\""; 
	     #$dbh->disconnect();
	}
	print "<form action=\"$script_url\" method=\"POST\" name=\"form\" style=\"border: 0;padding: 0;margin:0;\">\n";
  	print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;\"><tr><td> \n";
	print "<applet code=\"tbs.TBSApplet.class\" ";
	print "archive=\"$jar_loc\" ";
	print "width=1175 height=590 name=\"TreeApplet\"> \n";
	print "<param name=\"Student\" value=\"$name+=$lastUpdate+=$treeXML+=$Q1+=$Q2+=$Q3+=$arrows+=\"> \n";
  	print "<param name=\"Admin\" value=\"false\"> \n";
  	print "<param name=\"Browser\" value=\"$browser\"> \n";
  	print "          You have to enable Java on your machine !</applet> \n";
  	print "</td><td height=\"100%\">\n";
    print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;height:100%;\"><tr><td valign=\"top\"><center>\n";
    print "<input type=\"button\" value=\"Logout\" onclick=\"window.navigate('$script_url');\">\n";
    print "</center></td></tr>\n";
    print "<tr><td><center>\n";
    print "<font size=+1>Diversity of Life<br> Survey<br> for<br> $name</font><br>\n";
    if ($complete == 0) {
	     print "<table bgcolor=red><tr><td style=\"font-weight:bold;\"><center>\n";
	     print "Your survey is not complete!</center></td></tr><tr><td><center>\n";
	     print "You will not receive<br> any credit unless<br> you answer all the questions.<br>\n";
	     print "Thanks!</center></td></tr></table>\n";
	} else {
	     print "<table bgcolor=green><tr><td style=\"font-weight:bold;\"><center>\n";
	     print "Your survey is complete!</center></td></tr><tr><td><center>\n";
	     print "You have received<br> 15 points<br> for the<br> &quot;Diversity Of Life Survey&quot;<br>\n";
	     print "Thanks!</center></td></tr></table>\n";	
	}
    print "<input type=\"hidden\" name=\"Name\" value=\"$name\">\n";
    print "<input type=\"hidden\" name=\"Passwd\" value=\"$password\">\n";
    print "<input type=\"hidden\" name=\"lastUpdate\" value=\"$lastUpdate\">\n";
    print "<input type=\"hidden\" name=\"treeXML\" value=\"$treeXML\">\n";
    print "<input type=\"hidden\" name=\"Q1\" value=\"$Q1\">\n";
    print "<input type=\"hidden\" name=\"Q2\" value=\"$Q2\">\n";
    print "<input type=\"hidden\" name=\"Q3\" value=\"$Q3\">\n";
    print "<input type=\"hidden\" name=\"Browser\" value=\"$browser\">\n";
    print "<input type=\"submit\" value=\"Submit Survey\" onclick=\"return isComplete();\">\n";
    print "</center></td></tr> \n";
    print "<tfoot><tr><td>\n";
    print "<center>For any issues<br> with this site<br> click here<br> \n";
    print "<input type=\"button\" value=\"Site Issues\" onclick=\"window.navigate('$googleCode_url');\"></center> \n";
    print "</td></tr></tfoot>\n";
    print "</table>\n";
    print "</td></tr></table>\n";
  	print "</form>\n";
    print "</body></html>\n";
}

sub load_admin_survey {

	$query = CGI->new();
	$name = $query->param('Name');
	#$password = $query->param('Passwd');
	$password = "pass";
	$arrows = "true";
	$browser = $query->param('Browser');

	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Diversity of Life Survey - Administrator Version</title>\n";
	
	
	if ($password eq $admin_pw) {
	     $admin_mode = 1;
	} else {
	     $admin_mode = 0;
	}

	print "</head>\n";
	print "<body bgcolor = \"lightblue\" style=\"border: 0;padding-bottom: 0;margin-bottom: 0;\">\n"; 
	
	print "<form name=\"form\" style=\"border: 0;padding-bottom: 0;margin-bottom: 0;\">\n";
  	print "<table style=\"border-collapse: collapse;padding-bottom: 0;margin-bottom: 0;\"><tr><td> \n";
	print "<applet code=\"tbs.TBSApplet.class\" ";
	print "archive=\"$jar_loc\" ";
	print "width=1175 height=590 name=\"TreeApplet\"> \n";
	#$dbh = GradeDB::connect();
	$dbh = treeDB::connect();
	#$sth = $dbh->prepare("SELECT * FROM students ORDER BY name");
	$sth = $dbh->prepare("SELECT * FROM student_testdata ORDER BY name");
    $sth->execute();
    $count = 0;
	while (@data = $sth->fetchrow_array()) {
		$count++;
		my $student_name = $data[0];
        my $last_update = $data[1];
		my $tree = $data[2];
		my $Q1 = $data[3];
		my $Q2 = $data[4];
		my $Q3 = $data[5];
		print "<param name=\"Student$count\" value=\"$student_name+=$last_update+=$tree+=$Q1+=$Q2+=$Q3+=$arrows+=\"> \n";
    }
    $sth->finish();
	$dbh->disconnect();
	print "<param name=\"Admin\" value=\"true\"> \n";
	print "<param name=\"StudentCount\" value=\"$count\"> \n";
	print "<param name=\"Browser\" value=\"$browser\"> \n";
  	print "          You have to enable Java on your machine !</applet> \n";
  	print "</td></tr></table>\n";
  	print "</form>\n";
    print "</body></html>\n";
}

sub decrypt_pw {

	my $cryptpw = $_[0];
	my $pw = $_[1];
	my $isok = 0;
	
	if(crypt($pw, time.$$) eq $cryptpw && $pw ne "" && $cryptpw ne ""){
		$isok = 1;
	}
	$isok;
}