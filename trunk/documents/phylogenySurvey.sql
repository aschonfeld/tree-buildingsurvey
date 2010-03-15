--  you can run this script after logging into mysql command-line by running this command

--  source [location of file]\phylogenySurvey.sql

--  e.g. "c:\cs682\phylogenySurvey.sql"


-- create trees database
create database trees;

-- create grade database
create database grades;
use grades;

-- create assignments table
create table assignments (number int(11) not null primary key default 0,
name varchar(30) null);

-- insert row for "Diversity of Life Survey (15)" project[this will be used by TreeSurvey.pl]
insert into assignments (name) values ("Diversity of Life Survey (15)");
insert into assignments (number, name) values (1, "Genetics Survey (10)");

-- create instructors table
create table instructors (name varchar(30) not null primary key default '',
password varchar(30) null,
class_list varchar(50) null);

-- create students table
use trees;
create table students (name varchar(30) not null primary key default '',
section varchar(20) null,
TA varchar(20) null,
password varchar(30) null);
alter table students add grade0 varchar(10) null after password;
alter table students add grade1 varchar(10) null after grade0;
alter table students add grade2 varchar(10) null after grade1;
alter table students add grade3 varchar(10) null after grade2;
alter table students add grade4 varchar(10) null after grade3;
alter table students add grade5 varchar(10) null after grade4;
alter table students add grade6 varchar(10) null after grade5;
alter table students add grade7 varchar(10) null after grade6;
alter table students add grade8 varchar(10) null after grade7;
alter table students add grade9 varchar(10) null after grade8;
alter table students add grade10 varchar(10) null after grade9;
alter table students add grade11 varchar(10) null after grade10;
alter table students add grade12 varchar(10) null after grade11;
alter table students add grade13 varchar(10) null after grade12;
alter table students add grade14 varchar(10) null after grade13;
alter table students add grade15 varchar(10) null after grade14;
alter table students add grade16 varchar(10) null after grade15;
alter table students add grade17 varchar(10) null after grade16;
alter table students add grade18 varchar(10) null after grade17;
alter table students add grade19 varchar(10) null after grade18;
alter table students add grade20 varchar(10) null after grade19;
alter table students add grade21 varchar(10) null after grade20;
alter table students add grade22 varchar(10) null after grade21;
alter table students add grade23 varchar(10) null after grade22;
alter table students add grade24 varchar(10) null after grade23;
alter table students add grade25 varchar(10) null after grade24;
alter table students add grade26 varchar(10) null after grade25;
alter table students add grade27 varchar(10) null after grade26;
alter table students add grade28 varchar(10) null after grade27;
alter table students add grade29 varchar(10) null after grade28;
alter table students add grade30 varchar(10) null after grade29;
alter table students add grade31 varchar(10) null after grade30;
alter table students add grade32 varchar(10) null after grade31;
alter table students add grade33 varchar(10) null after grade32;
alter table students add grade34 varchar(10) null after grade33;
alter table students add grade35 varchar(10) null after grade34;
alter table students add grade36 varchar(10) null after grade35;
alter table students add grade37 varchar(10) null after grade36;
alter table students add grade38 varchar(10) null after grade37;
alter table students add grade39 varchar(10) null after grade38;
alter table students add grade40 varchar(10) null after grade39;
alter table students add grade41 varchar(10) null after grade40;

-- now start creation of trees database
use trees;

-- create student_data table
create table student_data (name varchar(50) null,
date datetime null,
tree mediumblob null,
Q1 blob null,
Q2 blob null,
Q3 varchar(50) not null default '0,0,0,0,0,0,0,0,0,0,0,0,0');

-- insert student test data
insert into student_data values ('Doe,John',null,'','','','');
insert into student_data values ('Doe,Jane',null,'','','','');


create table student_testdata (name varchar(50) null,
date datetime null,
tree mediumblob null,
Q1 blob null,
Q2 blob null,
Q3 varchar(50) not null default '0,0,0,0,0,0,0,0,0,0,0,0,0');

insert into student_testdata (name, date, tree, Q1, Q2, Q3) select * from student_data;

-- insert student test data
-- '12O69mG8XyfSc' is the encrypted value for 'pass', the way to get an encrypted value
-- is by running the following in PERL, 'crypt("[whatever your password is]", time.$$)'
-- and that will give you your value to be stored into the 'password field of your database
-- table
insert into students(name,section,password) values ('Doe,John','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Doe,Jane','section 02','12O69mG8XyfSc');

-- create user, treesurvey
use mysql;
insert into user (Host,User,Password) VALUES ('localhost','treesurvey',PASSWORD('tr335urvey'));
grant all privileges on grades.* to treesurvey@localhost;
grant all privileges on trees.* to treesurvey@localhost;
flush privileges;
