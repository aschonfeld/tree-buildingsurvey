#!/usr/bin/perl

# set some constants
$admin_pw = "lab09acce55";
$too_late_month = 1;
$too_late_day = 12;
$name_of_survey_field_in_assignments_txt = "Diversity of Life Survey (15)";

use DBI;
use CGI;
use GradeDB;
use treeDB;

$script_url = "https://www.securebio.umb.edu/cgi-bin/TreeSurvey.pl";

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
	print "<title>Login to the Phylogeny Survey</title>\n";
	print "</head>\n";
	print "<body bgcolor=\"#808000\">\n";
	
	if ($too_late == 1) {
	     print "<center><table><tr><td bgcolor=red><font color=black><font size=+3>\n";
	     print "<center>It is too late to complete the survey.</font><br>\n";
	     print "Sorry.</font></center></td></tr></table></center>\n";	
	}
	
	print "<form action=\"$script_url\" method=\"POST\">\n";

	$dbh = GradeDB::connect();
	%name_grade_hash = ();
	$sth = $dbh->prepare("SELECT * FROM students ORDER BY name");
    $sth->execute();
    while (@result = $sth->fetchrow_array()) {
        $name_grade_hash{$result[0]} = "";
    }
    $sth->finish();
	
	$statement = "SELECT number FROM assignments WHERE name=\"$name_of_survey_field_in_assignments_txt\"";
    $sth = $dbh->prepare($statement);
    $sth->execute();
    @result = $sth->fetchrow_array();
    $sth->finish();
    $index = "grade".$result[0];
    
    foreach $name (sort keys %name_grade_hash) {
         $sth = $dbh->prepare("SELECT $index from students WHERE name = \"$name\"");
         $sth->execute();
         @result = $sth->fetchrow_array();
         $name_grade_hash{$name} = $result[0];
         $sth->finish();
    }
	
	$dbh->disconnect();

	print "<font size=+3>Login to the diversity of life survey site</font><br>\n";
	print "Choose your name from this list:<br>\n";
    print "<select name=\"Name\" size=12>\n";
    $count = 0;
    foreach $name (sort keys %name_grade_hash) {
         $score = "";
         if ($name_grade_hash{$name} ne "") {
              $score = "(".$name_grade_hash{$name}.")";
              $count++;
         }
         print "<option value=\"$name\">$name $score</option>\n";
    }
    print "</select><br>\n";
    print "Enter your 8-digit UMS ID # (leave off the UMS):\n";
    print "<input type=\"password\" name=\"Passwd\" size=20><br>\n";
    print "  <input type=\"submit\" value=\"Login\">\n";
    
	print "</form>\n";
	print "<hr>\n";
	print "$count surveys entered.<br>\n";
	
	print "</body></html>\n";
}

sub load_survey {

	$query = CGI->new();
	$name = $query->param('Name');
	$password = $query->param('Passwd');
	$Q1 = $query->param('Q1');
	$Q2 = $query->param('Q2');
	$treeXML = $query->param('treeXML');
	
	#read in the parts of Q3
	$Q3 = "";
	for ($i = 0; $i < 12; $i++) {
	     $part = $query->param("Q3-$i");
	     if ($part eq "") {
	          $part = "0";
	     }
	     $Q3 = $Q3.$part.",";
	}
	$last = $query->param("Q3-12");
	if ($last eq "") {
	     $last = "0";
	}
	$Q3 = $Q3.$last;
	

	print "Content-type: text/html\n\n";
	print "<html><head>\n";
	print "<title>Diversity of Life Survey for $name</title>\n";
	
	
	if ($password eq $admin_pw) {
	     $admin_mode = 1;
	} else {
	     $admin_mode = 0;
	     
	     if ($too_late == 1) {
	        print "<body bgcolor=#FF8080>\n";
  	     	print "<br><font color=green><b>Sorry, it is too late to complete the survey.</b></font><br>";
   		    print "</body></html>\n";
   		    exit 1;
	     }
	
	     $dbh = GradeDB::connect();
	     $statement = "SELECT password FROM students
 	          WHERE name=\"$name\"";
	     $sth = $dbh->prepare($statement);
	     $sth->execute();
	     @result = $sth->fetchrow_array();
	     $sth->finish();
	     $pw = $result[0];

	     if(&decrypt_pw($pw,$password) != 1){
	        print "<body bgcolor=#FF8080>\n";
  	     	print "<br><font color=green><b>Error: Password incorrect 
   	     	       for $name.</b></font><br>";
   	     	print "<a href=\"$script_url\">Click here to return to login screen</a>.\n";
   		    print "</body></html>\n";
   		    exit 1;
	     }
	     $dbh->disconnect();
	}
	
    #see if there's already an entry for this student
	$dbh = treeDB::connect();
	$statement = "SELECT count(*) from student_data WHERE name=?";
	$sth = $dbh->prepare($statement);
	$sth->execute($name);
	$rowcount = $sth->fetchrow();
	$sth->finish();

	#see if they're entering data
	if ($treeXML ne "") {
	
	    if ($rowcount == 0) {
	        $statement = "INSERT INTO student_data (Q1, Q2, Q3, tree, date, name) VALUES (?,?,?,?,NOW(),?)";
	    }  else {
	        $statement = "UPDATE student_data SET Q1 = ?, Q2 = ?, Q3 = ?, tree = ?, date = NOW() WHERE name = ?";
	    }
	    $sth = $dbh->prepare($statement);
	    $sth->execute($Q1, $Q2, $Q3, $treeXML, $name);
	    $sth->finish();
	    $dbh->disconnect();
	} else {
	    # if there's already data there, get it
	    if ($rowcount != 0) {
	        $sth = $dbh->prepare("SELECT Q1, Q2, Q3, tree FROM student_data WHERE name=?");
	        $sth->execute($name);
	        ($Q1, $Q2, $Q3, $treeXML) = $sth->fetchrow_array();
	        $sth->finish();
	    }
	}
	
	$dbh->disconnect();
	
	if (($Q1 eq "") || ($Q2 eq "") || ($Q3 =~ /0/)) {
	     $complete = 0;
	} else {
	     $complete = 1;
	}
	
	&setup_arrays;
	
    $version = $WhichVersion{$name};
	
	@Q3parts = split /,/, $Q3;
	
	print "<SCRIPT language=\"JavaScript\">\n";
	print "function getTreeData() {\n";
	print "    var xml = document.TreeApplet.getTreeXML();\n";
	print "    document.forms[0].treeXML.value = xml;\n";
	print "    return true;\n";
	print "}\n";
	print "function setTreeData() {\n";
	print "    document.TreeApplet.setTreeXML(\"$treeXML\");\n";
	print "}\n";
	print "</script>\n";
	print "</head>\n";
	print "<body bgcolor = \"lightblue\" onload=\"setTreeData()\">\n"; 
	
	print "<center><font size=+2>Diversity of Life Survey for $name</font></center><br>\n";
	if ($complete == 0) {
	     print "<center><table><tr><td bgcolor=red><font color=black><font size=+3>\n";
	     print "<center>Your survey is not complete</font><br>\n";
	     print "you will not receive any credit unless you answer all the questions.  <br>\n";
	     print "Thanks!</font></center></td></tr></table></center>\n";
	} else {
	     print "<center><table><tr><td bgcolor=green><font color=black><font size=+3>\n";
	     print "<center>Your survey is complete!</font><br>\n";
	     print "You have received 15 points for the &quot;Diversity Of Life Survey&quot;<br>\n";
	     print "Thanks!</font></center></td></tr></table></center>\n";	
	     #enter the grade
	     $dbh = GradeDB::connect();
		 $statement = "SELECT number FROM assignments WHERE name=\"$name_of_survey_field_in_assignments_txt\"";
         $sth = $dbh->prepare($statement);
         $sth->execute();
         @result = $sth->fetchrow_array();
         $sth->finish();
         $index = "grade".$result[0];
         $statement = "UPDATE students SET $index = \"15\" WHERE name=\"$name\""; 
         $rows = $dbh->do($statement);
	     $dbh->disconnect();
	}
	print "This survey is designed to see how well you understand the diversity of living \n";
	print "organisms.  There is no right or wrong answer; you will receive full credit for \n";
	print "whatever you write.  We are most interested in your understanding of these \n";
	print "important biological issues.  <font color=red>Please do not consult any outside \n";
	print "sources (textbook ,www, other people, etc,) when completing this survey!!</font><hr>\n";
	print "Assume that you are working for a natural history museum like the Harvard Museum of \n";
	print "Natural History, only smaller.  Your museum has specimens of the following 20 \n";
	print "types of organisms in its collection.  Your task is to design a tree that will help \n";
	print "orient visitors to the collection.  \n";
	print "Your tree should include all the groups of organisms listed below and communicate the \n";
	print "ways they are evolutionarily related to one another.<br><br><br>\n";
	print "Using the program in the window\n";
	print "below, draw a tree diagram to show the relationships between these organisms.\n";
	print "Please include additional text and graphics that you think will help visitors \n";
	print "understand how you have organized these groups of organisms. There is no right or \n";
	print "wrong answer to this task, but it is important that you are able to explain the logic \n";
	print "behind your approach.<br><br>\n";

	print "<b>Instructions:</b><br>\n";
	print "<ul>\n";
  	print "<li>Drag Organisms to where you want them</li>\n";
  	print "<li>Shift-click on the workspace to add a node</li>\n";
  	print "<li>Click on an item (organism or node) to select it. It's border will turn red</li>\n";
  	print "<li>When you have two items (organism and/or node) selected, you can link or unlink them</li>\n";
  	print "<li>Links will remain connected when you move items.</li>\n";
  	print "<li>Click &quot;Label&quot; to add a text label.</li>\n";
  	print "<li>Click &quot;Delete&quot; to delete a test label or node.</li>\n";
  	print "<li>Select two objects connected by a link and click &quot;Split&quot; \n";
  	print "to add a new node in the middle of the link.</li>\n";
	print "</ul>\n";
  	print "<form action=\"$script_url\" method=\"POST\" onsubmit=\"return getTreeData();\" ";
  	print "name=\"form\">\n";
	print "<applet code=\"phylogenySurvey.SurveyApplet.class\" \n";
	print "archive=\"https://www.securebio.umb.edu/phylogenySurvey.jar\" \n";
	print "width=1020 height=1020 name=\"TreeApplet\">\n";
  	print "          You have to enable Java on your machine !</applet>\n";
    print "<br><br>\n";
    print "<hr>\n";
    print "<b>After you have developed your tree please answer the following questions.</b><br>\n";
    print "<b>1)</b> Explain in words how you went about organizing these organisms. Use one \n";
    print "or two specific examples and describe why you put them where you did.<br>\n";
    print "<textarea name=\"Q1\" rows=10 cols=80>$Q1</textarea><br><br>\n";
    print "<b>2)</b> How did you decide if organisms were closely related to one another\n";
    print "or not closely related? Use one or two specific examples from your work to explain \n";
    print "your reasoning.<br>\n";
    print "<textarea name=\"Q2\" rows=10 cols=80>$Q2</textarea><br><br>\n";
    print "<b>3) Common Ancestors:</b><br>\n";
    print "Choose the option that best expresses <u>how well you agree with</u> in the following";
    print " statements.  Use these options:<br>\n";
    print "<table border = 1>\n";
    print "<tr>\n";
    print "<td>&nbsp;</td>\n";
    print "<td><font color=red><b>Strongly Disagree</font></b></td>\n";
    print "<td><font color=red>Disagree</font></td>\n";
    print "<td>I\'m not sure</td>\n";
    print "<td><font color=green>Agree</font></td>\n";
    print "<td><font color=green><b> Strongly Agree</font></b></td>\n";
    print "</tr>\n";
    for ($i = 0; $i < 13; $i++){
         print "<tr>\n";
         print "<td>";
         if ($version eq "A") {
              print $VersionA[$i];
         } else {
              print $VersionB[$i];
         }
         print "</td>\n";
         for ($j = 1; $j < 6; $j++ ) {
              print "<td><input type=\"radio\" name=\"Q3-$i\" value=\"$j\"";
              if ($Q3parts[$i] == $j) {
                   print " CHECKED ";
              }
              print ">\n";
         }
         print "</tr>\n";
    }
    print "</table>\n";
    print "<input type=\"hidden\" name=\"Name\" value=\"$name\">\n";
    print "<input type=\"hidden\" name=\"Passwd\" value=\"$password\">\n";
    print "<input type=\"hidden\" name=\"treeXML\" value=\"$treeXML\">\n";
    @time = localtime(time);
    if ($admin_mode == 0) {
         print "<input type=\"submit\">\n";
    }
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

sub setup_arrays {
	#set up for the 13 questions
	@VersionA = (	"Cucumbers and humans have a common ancestor.",
					"Tuna fish and pumpkins have a common ancestor.",
					"Earthworms, mosquitoes, and humans all have a common ancestor.",
					"Earthworms, leeches, and snails all have a common ancestor.",
					"Beetles, ants, and mosquitoes all have a common ancestor.",
					"Crocodiles, rodents, and turtles all have a common ancestor.",
					"Salamanders, lizards, and crocodiles all have a common ancestor.",
					"Dolphins and humans have a common ancestor.",
					"Rats, whales, and zebras all have a common ancestor.",
					"Rodents and dogs have a common ancestor.",
					"Monkeys, baboons, and humans all have a common ancestor.",
					"Dogs, coyotes, and wolves all have a common ancestor.",
					"Gorillas, monkeys, and chimpanzees all have a common ancestor.");
	@VersionB = reverse @VersionA;
	
	%WhichVersion = ('Adan,Ambaro M' => 'A',
					'Aden,Abdirahman A' => 'A',
					'Alami,Aula' => 'A',
					'Albert,Bryan J' => 'A',
					'Antonioli,Alyssa M' => 'A',
					'Arshy,Zarin Tasnim' => 'A',
					'Badin,Georgy F' => 'A',
					'Balcha,Elias A' => 'A',
					'Barrett,Ashley Elizabeth' => 'A',
					'Bash,Mollie' => 'A',
					'Bautista,Jazmine Rose' => 'A',
					'Belair,Daniel R' => 'A',
					'Buckler,James' => 'A',
					'Cadorette,Alissa Marie' => 'A',
					'Chahine,Joseph Elias' => 'A',
					'Chirapha,Andrew' => 'A',
					'Chirapha,Andrew' => 'A',
					'Ciulla,Paul Steven' => 'A',
					'Clark,John James' => 'A',
					'Coulter,Allison M' => 'A',
					'Courtemanche,Aimee L' => 'A',
					'Cruz,Xenia' => 'A',
					'Dahl,Meghan E' => 'A',
					'Daniels,Kristen Lee' => 'A',
					'DeCosta,Kendra Mary' => 'A',
					'Delboni,Marcela Merces' => 'A',
					'Dilbarian,Tyler Richard' => 'A',
					'Dubey,Himanshu' => 'A',
					'Ellis,Peter Truesdell' => 'A',
					'Errico,Nicole M' => 'A',
					'Eschuk,Danielle D' => 'A',
					'Fernald,Jenny L' => 'A',
					'Foley,Meissa K' => 'A',
					'Forsythe,Lauren M' => 'A',
					'Forziati,Rachel Ann' => 'A',
					'Freehoff,Tabitha Ann' => 'A',
					'Fuertes,Yinette S' => 'A',
					'Gittins Stone,Daniel Ian' => 'A',
					'Golden,Shekia L' => 'A',
					'Gonzalez,Ediozel R' => 'A',
					'Gonzalez,Ediozel R' => 'A',
					'Goss,Tiffany N' => 'A',
					'Gouthro,Sarah K' => 'A',
					'Greene,Rachel A' => 'A',
					'Guadagno,Cassiopeia Ann' => 'A',
					'Guastalli,Holly B' => 'A',
					'Guerrier,Mirphael Z' => 'A',
					'Guillaume,Faradjin Esther' => 'A',
					'Guir,Cindy L' => 'A',
					'Halliday,Jaime Leigh' => 'A',
					'Hunter,Jennifer A' => 'A',
					'Huynh,Julee Pham' => 'A',
					'Huynh,Julian Quach' => 'A',
					'Ibacache,Camila F' => 'A',
					'Iraheta,Brenda V' => 'A',
					'Irving,Matthew Sean' => 'A',
					'Jean,Dorothy' => 'A',
					'Joyce,Sydney Morgan' => 'A',
					'Karakouzian,Alyson Paisley' => 'A',
					'Keffer-Fries,Courtney Rose' => 'A',
					'Khalifa,Nazim Bhikhu' => 'A',
					'King,Nicholas J' => 'A',
					'Kirkpatrick,Brianne Murphy' => 'A',
					'Kovaleva,Marina' => 'A',
					'Kryukov,Dmitriy S' => 'A',
					'Kwak,Gun Hoon' => 'A',
					'Lam,Ngoc Kim' => 'A',
					'Lam,Raymond' => 'A',
					'Le-Nguyen,Minhgiao Ngoc' => 'A',
					'Liu,Yi' => 'A',
					'Lucchetti,Vanessa Marie' => 'A',
					'Ly,Cindy T' => 'A',
					'MacDonald,Kyle Douglas' => 'A',
					'MacKinnon,Kalli S' => 'A',
					'Makodzeba,Yana' => 'A',
					'Makosky,Amanda M' => 'A',
					'Maloney,Michael Fintan' => 'A',
					'Martin,Danielle D' => 'A',
					'Martin,Eric Adel' => 'A',
					'Mawejje,Josephine Bukirwa' => 'A',
					'McCann,Patrick' => 'A',
					'McKinnon,Amber Irene' => 'A',
					'McNamee,Benjamin G' => 'A',
					'Medina-Rivera,Katy L' => 'A',
					'Mejia,Kady R' => 'A',
					'Mejia,Yanel D' => 'A',
					'Mello,Brendan C' => 'A',
					'Meyer,Tanthalas J' => 'A',
					'Morris,Melissa J' => 'A',
					'Munir,Kiara J\'Nai' => 'A',
					'Murphy,Shannon Terase' => 'A',
					'Nguyen,Jennie Thi' => 'A',
					'Nguyen,Nhu Da' => 'A',
					'Nguyen,Quyen N' => 'A',
					'Nguyen,Tuyen' => 'A',
					'Nguyen,Vu T' => 'A',
					'Nova,Daniel E' => 'A',
					'Nune,Herta N/a' => 'A',
					'Oh,Saeyoung William' => 'A',
					'Okundaye,Alberta A' => 'A',
					'Okwara,Noreen Chioma' => 'A',
					'Perez,Dinelia Iris' => 'A',
					'Perry,Debra Julie' => 'A',
					'Perry,Lea June' => 'A',
					'Pham,Thi Anh' => 'A',
					'Philben,Heather M' => 'A',
					'Phillips,Kari L' => 'A',
					'Rahman,John Sheikh' => 'A',
					'Ramos,Saimom Andre' => 'A',
					'Rateau,Vicky' => 'A',
					'Reed,Eric Raymond' => 'A',
					'Rodgers,Andrea Elizabeth' => 'A',
					'Romaisa,Romaisa' => 'A',
					'Ruggiero,Kayla K' => 'A',
					'Samaha,Fadi Elie' => 'A',
					'Santos,Jenelyn' => 'A',
					'Sayer,Shannon Marie' => 'A',
					'Scaduto,John Christopher' => 'A',
					'Schlesinger,Elizabeth B' => 'A',
					'Segenevich,Marianne Igorevna' => 'A',
					'Shahin,Tarek Emad' => 'A',
					'Shaikh,Seema Nazir' => 'A',
					'Smith,Robert A' => 'A',
					'Therenciel,Yvenie' => 'A',
					'Thompson,Kristen Silun' => 'A',
					'Tobin,Nora J' => 'A',
					'Toribio,Edelyn' => 'A',
					'Tran,Tran' => 'A',
					'Trifkovic,Vedran' => 'A',
					'Tse,Rita' => 'A',
					'Tucci,Jordan Michael' => 'A',
					'Vallas,Nomiki' => 'A',
					'Vu,Trong T' => 'A',
					'Wendle,Brigid' => 'A',
					'Weyer,Francois M.' => 'A',
					'White,Samantha Elizabeth' => 'A',
					'Wirth,Paul David' => 'A',
					'Wolf,Shawn' => 'A',
					'Wu,Ting' => 'A',
					'Yang,Heather' => 'A',
					'Yang,Yuna' => 'A',
					'Zaidi,Mohammad S' => 'A',
					'Zhu,Liya' => 'A',
					'Abdi,Safiya Mohammed' => 'B',
					'Alami,Maisa' => 'B',
					'Alexander,Jonathan A' => 'B',
					'Almasi,Ayatt' => 'B',
					'Almstrom,Justine M' => 'B',
					'Alrasheed,Obaid Abdullah' => 'B',
					'Alvarado, Janell' => 'B',
					'Alvero,Edward Jose G' => 'B',
					'Alzaim,Hanan' => 'B',
					'Anderson,Ashley Mae' => 'B',
					'Arruda,Christin M' => 'B',
					'Ativie,Alfred I' => 'B',
					'Atiyat,Tariq Ziyad' => 'B',
					'Bell,Alison M' => 'B',
					'Bell,Heather J' => 'B',
					'Bibby,Tanya Jo' => 'B',
					'Bittrolff,Molly Ray' => 'B',
					'Bowen,Allison Kayla' => 'B',
					'Boyle,Alanna L' => 'B',
					'Brigham,Ashley Elizabeth' => 'B',
					'Britt,Marion P' => 'B',
					'Buonomo,Kimberly A' => 'B',
					'Cabral,Daniel J' => 'B',
					'Cabral,Jennifer B' => 'B',
					'Chang,Iju' => 'B',
					'Chung,Anna L' => 'B',
					'Clemons,Mario Anton' => 'B',
					'Connolly,Elizabeth Lauren' => 'B',
					'Cost,Robert James' => 'B',
					'Cothias,Samantha Rebbecca' => 'B',
					'Cudmore,Jami Alison' => 'B',
					'Dang,Tam T' => 'B',
					'Della Croce,Maria Nicole' => 'B',
					'Demesyeux,Stacy' => 'B',
					'Demos,Maria Georgia' => 'B',
					'Dorime,Michelson' => 'B',
					'Dovner,Lindsay M' => 'B',
					'Drew,Priscilla A' => 'B',
					'Dunn,Katie Ann' => 'B',
					'Dunphy,Shane' => 'B',
					'Dusute,Ryan Steve' => 'B',
					'Dyer,Matthew k' => 'B',
					'Edouard,Emmanuela' => 'B',
					'Evans,Matthew Jacob' => 'B',
					'Evans,Matthew Jacob' => 'B',
					'Fenelon,Cassandra R' => 'B',
					'Freeman,Gregory E' => 'B',
					'Germain,Ramses J' => 'B',
					'Gerniglia,Danielle B' => 'B',
					'Gillis,Caitlin Louise' => 'B',
					'Gray,Kashaine' => 'B',
					'Grush,Jacob' => 'B',
					'Guerrero,Ivan' => 'B',
					'Haidar,Shadi Karam' => 'B',
					'Hannon,Kathryn Beth' => 'B',
					'Hebb,Tyler Joseph' => 'B',
					'Heinz,Meaghan Elizabeth' => 'B',
					'Hickey,Claire Veronica' => 'B',
					'Hughes,Matthew Robert' => 'B',
					'Hutchinson,Devin Alex' => 'B',
					'Huynh,Caitlyn Thi' => 'B',
					'Jimma,Mahlet' => 'B',
					'Johnson,Erika A' => 'B',
					'Julian,Amy S' => 'B',
					'Karanja,Tabitha W' => 'B',
					'Kim,Andrew Jin Young' => 'B',
					'Kindeur,Johanna' => 'B',
					'Koesler,Bryan Daniel' => 'B',
					'LaRochelle,Deanna Lee' => 'B',
					'Le,Thuy T' => 'B',
					'LeBrun,Michelle' => 'B',
					'Lerman,Zachary Jarrad' => 'B',
					'Lindenbaum,Sarah Anne' => 'B',
					'Linsky,Ben Nathan' => 'B',
					'Lujares,Allyson' => 'B',
					'Ly,Henry K' => 'B',
					'Mach,Henry' => 'B',
					'Malone,Dennis James' => 'B',
					'Marino,Vincent Jack' => 'B',
					'Martel,Kayla Marie' => 'B',
					'Mbengam,Babell' => 'B',
					'Mello,Laura L' => 'B',
					'Moon,Jinhee' => 'B',
					'Moreira,Caroline F' => 'B',
					'Moreira,Jessica B' => 'B',
					'Nakashian,Gregory H' => 'B',
					'Nalule,Janet Babirye' => 'B',
					'Naseer,Nida' => 'B',
					'Nguyen,Dzu' => 'B',
					'Nguyen,Glen' => 'B',
					'Nguyen,Le' => 'B',
					'Nguyen,Mylinh' => 'B',
					'Nicolas,Laurie Dianga' => 'B',
					'Norton,Ryan M' => 'B',
					'Ogle,Janelle L' => 'B',
					'Ojastro,Angela Luzano' => 'B',
					'Okhihan,Osevbouhen Toritseju' => 'B',
					'Olejnik,Adam E.' => 'B',
					'Patel,Shivani A' => 'B',
					'Paul,Marpha' => 'B',
					'Petersen,Elizabeth Nancy' => 'B',
					'Phan,Thanh D' => 'B',
					'Pisaturo,Erica Joan' => 'B',
					'Piza,Elizabeth Anne' => 'B',
					'Roach,Caitlin Elizabeth' => 'B',
					'Ross,Kelli M' => 'B',
					'Sawyer,Adam N' => 'B',
					'Seng,Tony' => 'B',
					'Shain,Stephanie Rebecca' => 'B',
					'Simpson,Kyle E' => 'B',
					'Stefantsiv,Viktoriya' => 'B',
					'Steinberg,David J' => 'B',
					'Suncar,Bianny J' => 'B',
					'Tassinari,Anna M' => 'B',
					'Thomas,Ann Zachariah' => 'B',
					'Tierney,Sonja Anne' => 'B',
					'Tran,Quy Nguyen' => 'B',
					'Tran,Tan' => 'B',
					'Travis,Kevin Patrick' => 'B',
					'Vansonnenberg,Polly' => 'B',
					'Victor,Vallery' => 'B',
					'Wong,Patrick' => 'B',
					'Wong,Suet Ching' => 'B',
					'Woodbine,Deandra T.' => 'B',
					'Yacsavilca,Kenji M' => 'B',
					'Zamborlini,Filipe Gomes' => 'B',
					'Zarella,Gina M' => 'B',
					'Zhao,Cuiyun' => 'B');
}
