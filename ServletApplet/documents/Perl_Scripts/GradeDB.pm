package GradeDB;

use strict;
use DBI;

my $host_name = "localhost";
my $db_name = "grades";
my $dsn = "DBI:mysql:host=$host_name;database=$db_name";

#connect to mysql server, using hardwired name and password

sub connect
{
    return (DBI->connect ($dsn, "treesurvey", "tr335urvey",
    			  {PrintError => 0, RaiseError => 1}));
}

1;