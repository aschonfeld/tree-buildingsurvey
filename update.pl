#! /usr/bin/perl


$str = `svn update lib`;
split(/ /,$str);
chomp($revno = $_[2]);
$revno =~ s/\.//;
$revno ++;

open(SCRIPT, "TBS.pl") ||
	die "Coudn't open TBSTestSurvey.pl";

open (OUTFILE, ">TBSTestSurvey.pl");
while (<SCRIPT>)
{
	s/= REVNO/= $revno/;
	print OUTFILE $_;
}


