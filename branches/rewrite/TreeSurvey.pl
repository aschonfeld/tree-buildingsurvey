#!/usr/bin/perl

use DBI;
use CGI;
use GradeDB;
use treeDB;

# set some constants
$googleCode_url = "http://code.google.com/p/tree-buildingsurvey/issues/list";
$script_url = "http://$ENV{'HTTP_HOST'}$ENV{'REQUEST_URI'}";
$jar_loc = "http://$ENV{'HTTP_HOST'}/TBSRun.jar";
$admin_pw = "lab09acce55";
$too_late_month = 12;
$too_late_day = 12;
$name_of_survey_field_in_assignments_txt = "Diversity of Life Survey (15)";
$student_info = "test_students";
$survey_info = "student_testdata";
$assignment_index = "";


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
	$invalidLogin = $_[0];
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
	print "function checkLogin() {\n";
	print " if(!document.form.AdminCB.checked) {\n";
	print " 	var user = document.form.Name.value;\n";
	print " 	if(user == \"\") {\n";
	print "    		alert(\"You must select a username!\");\n";
	print "    		return false;\n";
	print "  	}\n";
	print " }\n";
	print " var pass = document.form.Passwd.value;\n";
	print " if(pass == \"\") {\n";
	print "    alert(\"You must enter a password!\");\n";
	print "    return false;\n";
	print "  }\n";
	print "  return true;\n";
	print "}\n";
	print "function updateView() {\n";
	print " if(document.layers) {\n";
	print "        if(document.InvalidLogin) {\n";
	print "				document.InvalidLogin.display = 'none';\n";
	print "        }\n";
	print "  } else {\n";
	print "        if(document.all.InvalidLogin) {\n";
	print "				document.all.InvalidLogin.style.display = 'none';\n";
	print "        }\n";
	print " }\n";
	print " if(document.form.AdminCB.checked) {\n";
	print "    if(document.layers){\n";
	print "        document.NameSelection.display = 'none';\n";
	print "        document.AdminPassText.display = 'block';\n";
	print "        document.StudentPassText.display = 'none';\n";
	print "    } else {\n";
	print "        document.all.NameSelection.style.display = 'none';\n";
	print "        document.all.AdminPassText.style.display = 'block';\n";
	print "        document.all.StudentPassText.style.display = 'none';\n";
	print "    }\n";
	print " } else {\n";
	print "    if(document.layers){\n";
	print "        document.NameSelection.display = 'block';\n";
	print "        document.AdminPassText.display = 'none';\n";
	print "        document.StudentPassText.display = 'block';\n";
	print "    } else {\n";
	print "        document.all.NameSelection.style.display = 'block';\n";
	print "        document.all.AdminPassText.style.display = 'none';\n";
	print "        document.all.StudentPassText.style.display = 'block';\n";
	print "    }\n";
	print " }\n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor=\"#808000\">\n";
	
	print "<form action=\"$script_url\" method=\"POST\" onsubmit=\"return getAdminValue();\" ";
  	print "name=\"form\">\n";
  	
	$dbh = GradeDB::connect();
	%name_grade_hash = ();
	$sth = $dbh->prepare("SELECT * FROM $student_info ORDER BY name");
	$sth->execute();
    while (@result = $sth->fetchrow_array()) {
        $name_grade_hash{$result[0]} = "";
    }
    $sth->finish();
	$dbh->disconnect();
	print "<font size=+3>Login to the diversity of life survey site</font><br>\n";
	print "<br>Administrator?  \n";
    print "<input type=\"checkbox\" name=\"AdminCB\" onclick=\"return updateView();\"><br><br>\n";
	print "<div id=\"NameSelection\">\n";
	print "Choose your name from this list:<br>\n";
	if ($too_late == 1) {
		print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;\"><tr><td>\n";
	}
    print "<select name=\"Name\" size=10 style=\"width:200px;\">\n";
    foreach $name (sort keys %name_grade_hash) {
         print "<option value=\"$name\">$name</option>\n";
    }
    print "</select>\n";
    if ($too_late == 1) {
    	 print "</td><td height=100%>\n";
    	 print "<table bgcolor=yellow height=100% style=\"width:200px;\"><tr><td style=\"font-weight:bold;\"><center>\n";
		 print "The submission deadline has passed!</center></td></tr><tr><td><center>\n";
		 print "You can submit a survey but you will not recieve 15 points.<br>\n";
		 print "Thanks!</center></td></tr></table>\n";
		 print "</td></tr></table>\n";
    }
    print "</div>\n";
    print "<div id=\"StudentPassText\">\n";
    #print "Enter your 8-digit UMS ID # (leave off the UMS):\n";
    print "Enter your password (this is a test version, just enter in 'pass'):\n";
    print "</div>\n";
    print "<div id=\"AdminPassText\" style=\"display:none\">\n";
    print "Enter administrator password:\n";
    print "</div>\n";
    print "<input type=\"password\" name=\"Passwd\" size=20><br>\n";
    if($invalidLogin eq 'true'){
    	print "<div id=\"InvalidLogin\" style=\"display:block\">\n";
  		print "Invalid Password!<br>\n";
  		print "</div>\n";
  	}
  	print "<br>\n";
  	print "<input type=\"submit\" value=\"Login\" onclick=\"return checkLogin();\">\n";
  	print "<input type=\"hidden\" name=\"AdminValue\" value=\"false\">\n";
    print "<input type=\"hidden\" name=\"Browser\" value=\"\">\n";
    print "</form>\n";
	print "<hr>\n";
	print "</body></html>\n";
}

sub load_survey {

	$query = CGI->new();
	$name = $query->param('Name');
	$password = $query->param('Passwd');
	$admin = $query->param('AdminValue');
	
	if($admin eq 'false'){
		$dbh = GradeDB::connect();
		
		if($assignment_index eq ''){
			$statement = "SELECT number FROM assignments WHERE name=\"$name_of_survey_field_in_assignments_txt\"";
			$sth = $dbh->prepare($statement);
			$sth->execute();
			@result = $sth->fetchrow_array();
			$assignment_index = "grade".$result[0];
		}
		
		$statement = "SELECT password,section FROM $student_info WHERE name=\"$name\"";
		$sth = $dbh->prepare($statement);
		$sth->execute();
		@result = $sth->fetchrow_array();
		$sth->finish();
		$pw = $result[0];
		$dbh->disconnect();
		
		if(&decrypt_pw($pw,$password) == 1){
			&load_student_survey;
		}else{
		   &login_page('true');
		}
	}else{
		if($admin_pw eq $password){
			&load_admin_survey;
		}else{
			&login_page('true');
		}
	}
}

sub load_student_survey {
	
	$password = $query->param('Passwd');
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
	        $statement = "INSERT INTO $survey_info (Q1, Q2, Q3, tree, date, name) VALUES (?,?,?,?,NOW(),?)";
	    }  else {
	        $statement = "UPDATE $survey_info SET Q1 = ?, Q2 = ?, Q3 = ?, tree = ?, date = NOW() WHERE name = ?";
	    }
	    $sth = $dbh->prepare($statement);
	    $sth->execute($Q1, $Q2, $Q3, $treeXML, $name);
	    $sth->finish();
	    print "<body bgcolor=\"lightblue\">\n";
  	    print "<br><center><font size=+1>$name thank you for your survey submission.</font><br>";
  	    if ($too_late != 1) {
  	    	print "<br><center><font size=+1>You have recieved 15 points.</font><br>";
  	    }
  	    print "<form action=\"$script_url\" method=\"POST\" name=\"form\">\n";
  	    print "<table><tr><td>\n";
  	    print "<input type=\"button\" value=\"Return To Login\" onclick=\"window.location = '$script_url';\">\n";
  	    print "</td><td>\n";
  	    print "<input type=\"submit\" value=\"Return To Survey\">\n";
  	    print "</td></tr></table>\n";
   	    print "<input type=\"hidden\" name=\"AdminValue\" value=\"false\">\n";
    	print "<input type=\"hidden\" name=\"Name\" value=\"$name\">\n";
    	print "<input type=\"hidden\" name=\"Passwd\" value=\"$password\">\n";
    	print "<input type=\"hidden\" name=\"Browser\" value=\"$browser\">\n";
    	print "</form></center>\n";
   		print "</body></html>\n";
   		exit 1;
	} else {
	    # if there's already data there, get it
	    if ($rowcount != 0) {
	        $sth = $dbh->prepare("SELECT Q1, Q2, Q3, tree, date FROM $survey_info WHERE name=?");
	        $sth->execute($name);
	        ($Q1, $Q2, $Q3, $treeXML, $lastUpdate) = $sth->fetchrow_array();
	        $sth->finish();
	    }
	}
	
	$dbh->disconnect();
	
	$dbh = GradeDB::connect();
	$statement = "SELECT section,$assignment_index from $student_info WHERE name=?";
	$sth = $dbh->prepare($statement);
	$sth->execute($name);
	($section,$grade) = $sth->fetchrow();
	$sth->finish();
	
	#If Professor White requests the radio question be put back into
	#the open-response section, then reinstitute this line of code and
	#comment out the one underneath it.
	#if (($Q1 eq "") || ($Q2 eq "") || ($Q3 =~ /0/)) {
	
	if($grade eq 15){
		$complete  = 1;
	}
	else{
		if (($Q1 eq "") || ($Q2 eq "")) {
		     $complete = 0;
		} else {
		     $complete = 1;
		}
	}
	print "<SCRIPT language=\"JavaScript\">\n";
	print "function isComplete() {\n";
	print " var qip = document.TreeApplet.questionInProgress();\n";
	print " if(qip != ''){\n";
	print " 	if(confirm(\"Is it ok to save your changes to question \" + qip + \"?\")){\n";
	print "			document.TreeApplet.acceptQuestionInProgress();\n";
	print "		}\n";
	print " }\n";
	print " var status = document.TreeApplet.getStatus();\n";
	print " document.forms[0].treeXML.value = document.TreeApplet.getTree();\n";
	print " document.forms[0].Q1.value = document.TreeApplet.getQ1(); \n";
	print " document.forms[0].Q2.value = document.TreeApplet.getQ2(); \n";
	#print " document.forms[0].Q3.value = document.TreeApplet.getQ3(); \n";
	print " if(status != \"\"){ \n";
	print "   return confirm(status + \" Is it ok to save?\");\n";
	print " } \n";
	print " return true; \n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor=\"lightblue\" style=\"border: 0;padding: 0;margin:0;\">\n"; 
	
	if ($complete != 0) {
	     #enter the grade
	     $dbh = GradeDB::connect();
		 $statement = "UPDATE $student_info SET $assignment_index = \"15\" WHERE name=\"$name\"";
         $sth = $dbh->prepare($statement);
         $sth->execute();
	     $dbh->disconnect();
	}
	print "<form action=\"$script_url\" method=\"POST\" name=\"form\" style=\"border: 0;padding: 0;margin:0;\">\n";
  	print "<table width=\"100%\" height=\"100%\" style=\"border-collapse: collapse;padding: 0;margin: 0;\"> \n";
  	print "<tr>\n";
  	print "<td width=\"85%\">\n";
	print "<applet code=\"tbs.TBSApplet.class\" archive=\"$jar_loc\" width=\"100%\" height=\"100%\" name=\"TreeApplet\"> \n";
	print "<param name=\"Student\" value=\"$name+=$lastUpdate+=$treeXML+=$Q1+=$Q2+=$Q3+=$section+=\"> \n";
  	print "<param name=\"Admin\" value=\"false\"> \n";
  	print "<param name=\"Browser\" value=\"$browser\"> \n";
  	print "You have to enable Java on your machine! \n";
  	print "</applet>\n";
  	print "</td>\n";
  	print "<td width=\"15%\" height=\"100%\" align=\"center\">\n";
  	print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;height:100%;\">\n";
  	print "<tr><td valign=\"top\"><center>\n";
    print "<input type=\"button\" value=\"Logout\" onclick=\"window.location = '$script_url';\">\n";
    print "</center></td></tr>\n";
    print "<tr><td><center>\n";
    print "<font size=+1>Diversity of Life<br> Survey<br> for<br> $name</font><br>\n";
    if ($too_late == 1) {
    	print "<table bgcolor=yellow><tr><td style=\"font-weight:bold;\"><center>\n";
		print "The submission deadline<br> has passed!</center></td></tr><tr><td><center>\n";
		print "You can submit a survey<br>but you will not<br> recieve 15 points.<br>\n";
		print "Thanks!</center></td></tr></table>\n";
	}else{
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
	}
    print "<input type=\"hidden\" name=\"AdminValue\" value=\"$admin\">\n";
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
    print "<tfoot><tr><td valign=\"bottom\">\n";
    print "<center>For any issues<br> with this site<br> click here<br> \n";
    print "<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('$googleCode_url','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"></center> \n";
    print "</td></tr></tfoot>\n";
    print "</table>\n";
    print "</td></tr></table>\n";
  	print "</form>\n";
    print "</body></html>\n";
}

sub load_admin_survey {

	$query = CGI->new();
	$name = $query->param('Name');
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
	print "<body bgcolor = \"lightblue\" style=\"border: 0;padding: 0;margin:0;\">\n"; 
	print "<form name=\"form\" style=\"border: 0;padding: 0;margin:0;\">\n";
  	print "<table width=\"100%\" height=\"100%\" style=\"border-collapse: collapse;padding: 0;margin: 0;\">\n";
  	print "<tr><td width=\"85%\"> \n";
	print "<applet code=\"tbs.TBSApplet.class\" archive=\"$jar_loc\" width=\"100%\" height=\"100%\" name=\"TreeApplet\"> \n";
	$dbh = GradeDB::connect();
	%name_section_hash = ();
	$sth = $dbh->prepare("SELECT name, section FROM $student_info ORDER BY name");
    $sth->execute();
    while (@result = $sth->fetchrow_array()) {
        $name_section_hash{$result[0]} = $result[1];
    }
    $sth->finish();
    
	$dbh = treeDB::connect();
	$sth = $dbh->prepare("SELECT * FROM $survey_info ORDER BY name");
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
		my $section = $name_section_hash{$student_name};
		print "<param name=\"Student$count\" value=\"$student_name+=$last_update+=$tree+=$Q1+=$Q2+=$Q3+=$section+=\"> \n";
    }
    $sth->finish();
	
	print "<param name=\"Admin\" value=\"true\"> \n";
	print "<param name=\"StudentCount\" value=\"$count\"> \n";
	print "<param name=\"Browser\" value=\"$browser\"> \n";
  	print "You have to enable Java on your machine!\n";
  	print "</applet> \n";
  	print "</td>\n";
  	print "<td width=\"15%\" height=\"100%\" align=\"center\">\n";
  	print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;height:100%;\">\n";
  	print "<tr><td valign=\"top\"><center>\n";
  	print "<input type=\"button\" value=\"Logout\" onclick=\"window.location = '$script_url';\">\n";
  	print "</center></td></tr>\n";
  	print "<tr><td><center>\n";
  	print "<font size=+1>Diversity of Life<br> Survey<br> Administrator Version</font><br>\n";
  	print "</center></td></tr>\n";
  	print "<tfoot><tr><td valign=\"bottom\">\n";
  	print "<center>For any issues<br> with this site<br> click here<br>\n";
  	print "<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('$googleCode_url','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"></center>\n";
  	print "</td></tr></tfoot>\n";
  	print "</table>\n";
  	print "</td>\n";
  	print "</tr></table>\n";
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