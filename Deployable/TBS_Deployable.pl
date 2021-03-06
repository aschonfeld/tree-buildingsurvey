#!/usr/bin/perl

use CGI;
require "ctime.pl";
require "common_functions.pl";


# set some constants
$googleCode_url = "http://code.google.com/p/tree-buildingsurvey/issues/list";
$script_url = "http://$ENV{'HTTP_HOST'}$ENV{'REQUEST_URI'}";
$jar_loc = "http://$ENV{'HTTP_HOST'}/Test/TBSRun.jar";
#$jar_loc = "http://localhost:8080/PhylogenySurveyWeb/TBSRun.jar";
$js_loc = "http://$ENV{'HTTP_HOST'}/common_functions.js";
#$js_loc = "http://localhost:8080/PhylogenySurveyWeb/common_functions.js";
$too_late_month = 12;
$too_late_day = 12;
$survey_points = "10";
$revno = 801;
#$student_index="C:/Workspace/PhylogenySurveyWeb/WebContent/WEB-INF/cgi/students";
$student_index="/var/www/cgi-bin/students";
#$dummy_index="C:/Workspace/PhylogenySurveyWeb/WebContent/WEB-INF/cgi/dummy";
$dummy_index="/var/www/cgi-bin/dummy";
$prof_name="Dr. Blue";
$complete = 0;

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
	print "<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"$js_loc\">\n";
	print "</SCRIPT>\n";
	print "</head>\n";
	print "<body bgcolor=\"#808000\">\n";
	
	print "<form action=\"$script_url\" method=\"POST\" onsubmit=\"return getAdminValue();\" ";
  	print "name=\"form\">\n";
  	

	################################################
	# Read students file, convert to %name_id_hash #
	################################################
    open(STUDENTS, "$student_index") || die "Can't open students";
	#tie @result, 'Tie::File', STUDENTS or die "Can't tie file";


    while (<STUDENTS>){  
	    @this_student=split(/,/, $_);
	    $name_id_hash{@this_student[0]}=@this_student[1];
    }

	close STUDENTS || die "Can't close students file";

	print "<font size=+3>Login to the diversity of life survey site</font><br>\n";
	print "<br>Administrator?  \n";
    print "<input type=\"checkbox\" name=\"AdminCB\" onclick=\"return updateView();\"><br>\n";
    print "<div id=\"NameSelection\">\n";
	print "Choose your name from this list:<br>\n";
	if ($too_late == 1) {
		print "<table style=\"border-collapse: collapse;padding: 0;margin: 0;\"><tr><td>\n";
	}
    print "<select name=\"Name\" size=10 style=\"width:200px;\">\n";
    foreach $name (sort keys %name_id_hash) {
         print "<option value=\"$name\">$name</option>\n";
    }
    print "</select>\n";
    if ($too_late == 1) {
    	 print "</td><td height=100%>\n";
    	 print "<table bgcolor=yellow height=100% style=\"width:200px;\"><tr><td style=\"font-weight:bold;\"><center>\n";
		 print "The submission deadline has passed!</center></td></tr><tr><td><center>\n";
		 print "You can submit a survey but you will not recieve course credit.<br>\n";
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
	
    # read in student file and shove it into an array
	$query = CGI->new();
	$name = $query->param('Name');
	$password = $query->param('Passwd');
	$admin = $query->param('AdminValue');
	
    open(STUDENT_FILE, $dummy_index) || die "Can't open dummy";
    @this_student_data=<STUDENT_FILE>;
    close STUDENT_FILE || die "Can't close dummy";
    
    #load values into variables

   ($name, $section, $pw)=split(/,/, trim(@this_student_data[0]));
    if($admin eq 'true') {
		if($admin_pw eq $password){
			&load_admin_survey;
		}else{
			&login_page('true');
		}
	} else {
		#We might eventually add encryption to the password values stored in the
		#student data files
		#if(&decrypt_pw($pw,$password) == 1){
		if($pw eq $password) {
			&load_student_survey;
		} else {
			&login_page('true');
		}
	}
	
}

sub load_student_survey {
	
	$name = $query->param('Name');
	$password = $query->param('Passwd');
	$Q1 = $query->param('Q1');
	$Q2 = $query->param('Q2');
	$Q3 = "";
	$treeXML = $query->param('treeXML');
	$lastUpdate = $query->param('lastUpdate');
	$browser = $query->param('Browser');	

	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Diversity of Life Survey for $name</title>\n";
	
	@temp = split(/,/,trim(@this_student_data[1]));
	if(@temp[0] eq $survey_points){
		$complete  = 1;
	} else {
		if (($Q1 eq "") || ($Q2 eq "")) {
			$complete = 0;
		} else {
			$complete = 1;
		}
	}

    if ($treeXML ne "") {
    	print "</head>\n";
       	print "<body bgcolor=\"lightblue\">\n";
       	
    	#Save student data
    	@writeArray[0]=@this_student_data[0];
		if ($complete != 0) {
			push (@writeArray,"10,".&ctime(time));
		} else {
			push (@writeArray,"0,".&ctime(time));
		}
		if($Q1 eq ''){$Q1 = "NULL";}
		push (@writeArray,"$Q1\n");
		if($Q2 eq ''){$Q2 = "NULL";}
		push (@writeArray,"$Q2\n");
		if($treeXML eq ''){$treeXML = "NULL";}
		push (@writeArray,"$treeXML\n");
		
		open (F, ">$dummy_index") || die "Can't open dummy";
		flock F, 2; #Exclusive lock
		print F @writeArray;
		flock F, 8; #Unlock
    	close F || die "Can't close dummy";
    	
    	print "<br><center><font size=+1>$name thank you for your survey submission.</font><br>";
	   	if ($too_late != 1) {
			print "<br><center><font size=+1>You have recieved credit for completing the survey.</font><br>";
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
	}else {
		######################################################################
		# Open student's data file, extract question strings and tree, as well
		# as last update and browser values
		# lastUpdate, browser, grade will be on line 2, comma-sep
		# Q1-QN are on lines 3ff, followed by the tree. Save format tbd
		######################################################################
		
		# we've already loaded the student file into @array, so:
		$lastUpdate = @temp[1];
		$Q1 = trim(@this_student_data[2]);
		$Q2 = trim(@this_student_data[3]);
		$treeXML = trim(@this_student_data[4]);
	}
	
	
	print "<SCRIPT LANGUAGE=\"JavaScript\" SRC=\"$js_loc\">\n";
	print "</SCRIPT>\n";
	print "</head>\n";
	print "<body bgcolor=\"lightblue\" style=\"border: 0;padding: 0;margin:0;\">\n"; 
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
		print "You can submit a survey<br>but you will not<br> recieve course credit.<br>\n";
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
			print "You have received<br> course credit<br> for the<br> &quot;Diversity Of Life Survey&quot;<br>\n";
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
    print "<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('$googleCode_url','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"><br> \n";
    print "TBSAPPLET REVNO $revno</center>\n";
    print "</td></tr></tfoot>\n";
    print "</table>\n";
    print "</td></tr></table>\n";
  	print "</form>\n";
    print "</body></html>\n";
}

sub load_admin_survey {

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
    
    #$dbh = GradeDB::connect();
	#%name_section_hash = ();
	#$sth = $dbh->prepare("SELECT name, section FROM $student_info ORDER BY name");
    #$sth->execute();
    #while (@result = $sth->fetchrow_array()) {
    #    $name_section_hash{$result[0]} = $result[1];
    #}
    #$sth->finish();
    
	#$dbh = treeDB::connect();
	#$sth = $dbh->prepare("SELECT * FROM $survey_info ORDER BY name");
    #$sth->execute();
    #$count = 0;
	#while (@data = $sth->fetchrow_array()) {
	#	$count++;
	#	my $student_name = $data[0];
    #    my $last_update = $data[1];
	#	my $tree = $data[2];
	#	my $Q1 = $data[3];
	#	my $Q2 = $data[4];
	#	my $Q3 = $data[5];
	#	my $section = $name_section_hash{$student_name};
	#	print "<param name=\"Student$count\" value=\"$student_name+=$last_update+=$tree+=$Q1+=$Q2+=$Q3+=$section+=\"> \n";
    #}
    #$sth->finish();
    
	print "<param name=\"Student\" value=\"+=+=+=+=+=+=+=\"> \n";
	print "<param name=\"Admin\" value=\"true\"> \n";
	#print "<param name=\"StudentCount\" value=\"$count\"> \n";
	print "<param name=\"StudentCount\" value=\"1\"> \n";
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
  	print "<input type=\"button\" value=\"Site Issues\" onclick=\"window.open('$googleCode_url','','fullscreen=yes,toolbar=yes,menubar=yes,status=yes,scrollbars=yes,directories=yes,resizable=yes');\"><br>\n";
  	print "TBSAPPLET REVNO $revno</center>\n";
  	print "</td></tr></tfoot>\n";
  	print "</table>\n";
  	print "</td>\n";
  	print "</tr></table>\n";
  	print "</form>\n";
    print "</body></html>\n";
}