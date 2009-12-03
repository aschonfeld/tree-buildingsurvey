#!/usr/bin/perl

print "Content-type:text/html\n\n";
print <<EndOfHTML;
<html><head><title>Print Environment</title></head>
<body>
EndOfHTML

print "This is what you typed after query.cgi?<br>\n";
print $ENV{'QUERY_STRING'};

open (FILE, ">>write.txt") || die "Can't open write.txt: $!\n";
#Write the information to the file
print FILE "What you passed should in should be appended in write.txt starting here:\n";
print FILE $ENV{'QUERY_STRING'};
print FILE "\n";
close(FILE);

print "</body></html>";
