#! /usr/bin/perl


$str = `svnversion`;
split(/:/,$str);
chomp($revno = $_[1]);
$revno =~ s/M//;
$revno++;

open(SCRIPT, "TBS.pl") ||
	die "Coudn't open TBSTestSurvey.pl";

open (OUTFILE, ">TBSTestSurvey.pl");
while (<SCRIPT>)
{
	s/= REVNO/= $revno/;
	print OUTFILE $_;
}


