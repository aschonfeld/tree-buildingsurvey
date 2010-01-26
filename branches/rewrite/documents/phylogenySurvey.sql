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
insert into student_data values ('Lincoln,Barbara Elizabeth',null,'','','','');
insert into student_data values ('Steinberg,David J',null,'','','','');
insert into student_data values ('Ramos,Saimom Andre',null,'','','','');
insert into student_data values ('Petersen,Elizabeth Nancy',null,'','','','');
insert into student_data values ('Ceccarini,Amber Teren',null,'','','','');
insert into student_data values ('Farhangmehr,Taraneh',null,'','','','');
insert into student_data values ('Perry,Lea June',null,'','','','');
insert into student_data values ('Munir,Kiara JNai',null,'','','','');
insert into student_data values ('AragonMejia,Monica A',null,'','','','');
insert into student_data values ('Naseer,Nida',null,'','','','');
insert into student_data values ('Olejnik,Adam E.',null,'','','','');
insert into student_data values ('OBrien,Anya Elizabeth',null,'','','','');
insert into student_data values ('Hunter,Jennifer A',null,'','','','');
insert into student_data values ('Julian,Amy S',null,'','','','');
insert into student_data values ('Bash,Mollie',null,'','','','');
insert into student_data values ('Maloney,Michael Fintan',null,'','','','');
insert into student_data values ('Toner,Deanna Christine',null,'','','','');
insert into student_data values ('Chang,Iju',null,'','','','');
insert into student_data values ('McMullin,Kelly Marie',null,'','','','');
insert into student_data values ('Yang,Heather',null,'','','','');
insert into student_data values ('Le,Dong H',null,'','','','');
insert into student_data values ('Ly,Henry K',null,'','','','');
insert into student_data values ('Inglis,Nicole',null,'','','','');
insert into student_data values ('Benjamino,Jacquelynn M',null,'','','','');
insert into student_data values ('Maziarz,Robert D',null,'','','','');
insert into student_data values ('Beatrice,Breana Ashley',null,'','','','');
insert into student_data values ('Headley,Astley',null,'','','','');
insert into student_data values ('Tannous,Anastasia Ann',null,'','','','');
insert into student_data values ('Demos,Maria Georgia',null,'','','','');
insert into student_data values ('Yacsavilca,Kenji M',null,'','','','');
insert into student_data values ('Diaz Antigua,Luigina C',null,'','','','');
insert into student_data values ('Sim,Hyun Ji',null,'','','','');
insert into student_data values ('Karanja,Tabitha W',null,'','','','');
insert into student_data values ('Eustis,Meredith S',null,'','','','');
insert into student_data values ('Louis,Judith',null,'','','','');
insert into student_data values ('Yanson,Ron Pritz Sampuang',null,'','','','');
insert into student_data values ('Chan,Robert Tzimun',null,'','','','');
insert into student_data values ('Paul,Marpha',null,'','','','');
insert into student_data values ('Buckler,James',null,'','','','');
insert into student_data values ('Della Croce,Maria Nicole',null,'','','','');
insert into student_data values ('Tanner,Angela Leslie',null,'','','','');
insert into student_data values ('Spade,Jacquelyn Lee',null,'','','','');
insert into student_data values ('Wolf,Shawn',null,'','','','');
insert into student_data values ('Alzaim,Hanan',null,'','','','');
insert into student_data values ('Guerrier,Mirphael Z',null,'','','','');
insert into student_data values ('LeBrun,Michelle',null,'','','','');
insert into student_data values ('Makosky,Amanda M',null,'','','','');
insert into student_data values ('DeCosta,Kendra Mary',null,'','','','');
insert into student_data values ('Mello,Laura L',null,'','','','');
insert into student_data values ('Haidar,Shadi Karam',null,'','','','');
insert into student_data values ('Kim, Jeong-Hyun',null,'','','','');
insert into student_data values ('Arodaki,Firas',null,'','','','');
insert into student_data values ('Fossa,Alan John',null,'','','','');
insert into student_data values ('Gray,Kashaine',null,'','','','');
insert into student_data values ('Scaduto,John Christopher',null,'','','','');
insert into student_data values ('Zarella,Gina M',null,'','','','');
insert into student_data values ('Courtemanche,Aimee L',null,'','','','');
insert into student_data values ('Germain,Ramses J',null,'','','','');
insert into student_data values ('Fuertes,Yinette S',null,'','','','');
insert into student_data values ('Thomas,Ann Zachariah',null,'','','','');
insert into student_data values ('Samaha,Fadi Elie',null,'','','','');
insert into student_data values ('Alami,Aula',null,'','','','');
insert into student_data values ('Marino,Vincent Jack',null,'','','','');
insert into student_data values ('Lerman,Zachary Jarrad',null,'','','','');
insert into student_data values ('Gonzalez,Ediozel R',null,'','','','');
insert into student_data values ('Nicolas,Laurie Dianga',null,'','','','');
insert into student_data values ('Coulter,Allison M',null,'','','','');
insert into student_data values ('MacDonald,Kyle Douglas',null,'','','','');
insert into student_data values ('Kindeur,Johanna',null,'','','','');
insert into student_data values ('Ahmed,Subeia',null,'','','','');
insert into student_data values ('Simpson,Kyle E',null,'','','','');
insert into student_data values ('Hughes,Matthew Robert',null,'','','','');
insert into student_data values ('Iraheta,Brenda V',null,'','','','');
insert into student_data values ("D'Onofrio,Lauren R",null,'','','','');
insert into student_data values ('Balcha,Elias A',null,'','','','');
insert into student_data values ('Lam,Ngoc Kim',null,'','','','');
insert into student_data values ('Santos,Jenelyn',null,'','','','');
insert into student_data values ('Rateau,Vicky',null,'','','','');
insert into student_data values ('Roberson,Monette',null,'','','','');
insert into student_data values ('Fenelon,Cassandra R',null,'','','','');
insert into student_data values ('Guastalli,Holly B',null,'','','','');
insert into student_data values ('Chacon,Claudia L',null,'','','','');
insert into student_data values ('Chung,Anna L',null,'','','','');
insert into student_data values ('Tierney,Sonja Anne',null,'','','','');
insert into student_data values ('Phan,Thanh D',null,'','','','');
insert into student_data values ('Connolly,Elizabeth Lauren',null,'','','','');
insert into student_data values ('Linsky,Ben Nathan',null,'','','','');
insert into student_data values ('McCann,Patrick',null,'','','','');
insert into student_data values ('McKinnon,Amber Irene',null,'','','','');
insert into student_data values ('Alexander,Jonathan A',null,'','','','');
insert into student_data values ('Khalifa,Nazim Bhikhu',null,'','','','');
insert into student_data values ('Ammer,Fadlseed A',null,'','','','');
insert into student_data values ('Huynh,Caitlyn',null,'','','','');
insert into student_data values ('Kelty,Nathan James',null,'','','','');
insert into student_data values ('Sanchez,Aryany C',null,'','','','');
insert into student_data values ('Delgado,Stephanie',null,'','','','');
insert into student_data values ('Cudmore,Jami Alison',null,'','','','');
insert into student_data values ('Zhao,Cuiyun',null,'','','','');
insert into student_data values ('Henkin,Matthew E',null,'','','','');
insert into student_data values ('Andrade,Matthew',null,'','','','');
insert into student_data values ('Kovaleva,Marina',null,'','','','');
insert into student_data values ('Okwara,Noreen Chioma',null,'','','','');
insert into student_data values ('Win,Ma Kyu K',null,'','','','');
insert into student_data values ('Abikan,Abdulgafar Abdulkadir',null,'','','','');
insert into student_data values ('Mawejje,Josephine Bukirwa',null,'','','','');
insert into student_data values ('Mejia,Kady R',null,'','','','');
insert into student_data values ('Tobin,Nora J',null,'','','','');
insert into student_data values ('Eglington,Luke Glenn',null,'','','','');
insert into student_data values ('Wosny,Patricia Maria',null,'','','','');
insert into student_data values ('Beale,Shekeria S',null,'','','','');
insert into student_data values ('Woodward,Christina Angelina',null,'','','','');
insert into student_data values ('Jimma,Mahlet',null,'','','','');
insert into student_data values ('Burns,Latisha R',null,'','','','');
insert into student_data values ('Mejia,Yanel D',null,'','','','');
insert into student_data values ('Le-Nguyen,Minhgiao Ngoc',null,'','','','');
insert into student_data values ('Rahman,John Sheikh',null,'','','','');
insert into student_data values ('Britt,Marion P',null,'','','','');
insert into student_data values ('Pham,Thuy T',null,'','','','');
insert into student_data values ('Chamseddine,Abdul-hafiz O',null,'','','','');
insert into student_data values ('Piza,Elizabeth Anne',null,'','','','');
insert into student_data values ('Zhu,Liya',null,'','','','');
insert into student_data values ('Travis,Kevin Patrick',null,'','','','');
insert into student_data values ('Tang,Jeanie',null,'','','','');
insert into student_data values ('Tran,Tran',null,'','','','');
insert into student_data values ('Norton,Ryan M',null,'','','','');
insert into student_data values ('Phillips,Kari L',null,'','','','');
insert into student_data values ('Vu,Linh M',null,'','','','');
insert into student_data values ('Evans,Matthew Jacob',null,'','','','');
insert into student_data values ('Alvarado,Janell K',null,'','','','');
insert into student_data values ('Wood,Brandon Charles',null,'','','','');
insert into student_data values ('Reed,Eric Raymond',null,'','','','');
insert into student_data values ('Eschuk,Danielle D',null,'','','','');
insert into student_data values ('Nakashian,Gregory H',null,'','','','');
insert into student_data values ('Farinloye,Samuel K',null,'','','','');
insert into student_data values ('Cabral,Jennifer B',null,'','','','');
insert into student_data values ('Guerrero,Ivan',null,'','','','');
insert into student_data values ('Cost,Robert James',null,'','','','');
insert into student_data values ('Clark, Andrew',null,'','','','');
insert into student_data values ('Blanchard,Victoria Astrid',null,'','','','');
insert into student_data values ('Kokalari,Aiola',null,'','','','');
insert into student_data values ('Mach,Henry',null,'','','','');
insert into student_data values ('Dorime,Michelson',null,'','','','');
insert into student_data values ('Tassinari,Anna M',null,'','','','');
insert into student_data values ('Marinacci,Lucas X',null,'','','','');
insert into student_data values ('Ruan,Allie Yi',null,'','','','');
insert into student_data values ('Pelham,James M',null,'','','','');
insert into student_data values ('Clark,John James',null,'','','','');
insert into student_data values ('Oulbacha,Asmae',null,'','','','');
insert into student_data values ('Alami,Maisa',null,'','','','');
insert into student_data values ('Nguyen,Mylinh',null,'','','','');
insert into student_data values ('Bailey,Michiko',null,'','','','');
insert into student_data values ('Nguyen,Nhu Da',null,'','','','');
insert into student_data values ('Onwuka,UJa Ude',null,'','','','');
insert into student_data values ('White,Samantha Elizabeth',null,'','','','');
insert into student_data values ('Guillaume,Faradjin Esther',null,'','','','');
insert into student_data values ('McCarthy-Bates,Melissa Michell',null,'','','','');
insert into student_data values ('Dubey,Himanshu',null,'','','','');
insert into student_data values ('Ogle,Janelle L',null,'','','','');
insert into student_data values ('Nasser,Sahar Abdulla',null,'','','','');
insert into student_data values ('Maganzini,David',null,'','','','');
insert into student_data values ('Bautista,Jazmine Rose',null,'','','','');
insert into student_data values ('Medina-Rivera,Katy L',null,'','','','');
insert into student_data values ('Defendre,Marie C',null,'','','','');
insert into student_data values ('Gittins Stone,Daniel Ian',null,'','','','');

create table student_testdata (name varchar(50) null,
date datetime null,
tree mediumblob null,
Q1 blob null,
Q2 blob null,
Q3 varchar(50) not null default '0,0,0,0,0,0,0,0,0,0,0,0,0');

insert into student_testdata (name, date, tree, Q1, Q2, Q3)
select * from student_data where name in ('White,Brian',
'GUEST','Schonfeld,Andrew','Kiparsky,Jon',
'Thelen,Glenn','Bolker,Ethan','Young,Aimee','Skurtu,Tara',
'Nassali,Vivian','Makosky,Amanda');
insert into student_testdata values ('Kiparsky,Carol',null,'','','','');

select * from student_data where name in ('White,Brian',
'GUEST','Schonfeld,Andrew','Kiparsky,Jon',
'Thelen,Glenn','Bolker,Ethan','Young,Aimee','Skurtu,Tara',
'Nassali,Vivian','Makosky,Amanda');

-- insert student test data
insert into students(name,section,password) values ('Lincoln,Barbara Elizabeth','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Steinberg,David J','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ramos,Saimom Andre','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Petersen,Elizabeth Nancy','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ceccarini,Amber Teren','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Farhangmehr,Taraneh','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Perry,Lea June','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Munir,Kiara JNai','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('AragonMejia,Monica A','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Naseer,Nida','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Olejnik,Adam E.','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('OBrien,Anya Elizabeth','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Hunter,Jennifer A','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Julian,Amy S','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Bash,Mollie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Maloney,Michael Fintan','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Toner,Deanna Christine','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Chang,Iju','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('McMullin,Kelly Marie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Yang,Heather','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Le,Dong H','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ly,Henry K','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Inglis,Nicole','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Benjamino,Jacquelynn M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Maziarz,Robert D','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Beatrice,Breana Ashley','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Headley,Astley','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tannous,Anastasia Ann','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Demos,Maria Georgia','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Yacsavilca,Kenji M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Diaz Antigua,Luigina C','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Sim,Hyun Ji','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Karanja,Tabitha W','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Eustis,Meredith S','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Louis,Judith','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Yanson,Ron Pritz Sampuang','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Chan,Robert Tzimun','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Paul,Marpha','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Buckler,James','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Della Croce,Maria Nicole','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tanner,Angela Leslie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Spade,Jacquelyn Lee','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Wolf,Shawn','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Alzaim,Hanan','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Guerrier,Mirphael Z','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('LeBrun,Michelle','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Makosky,Amanda M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('DeCosta,Kendra Mary','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Mello,Laura L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Haidar,Shadi Karam','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Kim, Jeong-Hyun','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Arodaki,Firas','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Fossa,Alan John','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Gray,Kashaine','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Scaduto,John Christopher','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Zarella,Gina M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Courtemanche,Aimee L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Germain,Ramses J','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Fuertes,Yinette S','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Thomas,Ann Zachariah','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Samaha,Fadi Elie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Alami,Aula','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Marino,Vincent Jack','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Lerman,Zachary Jarrad','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Gonzalez,Ediozel R','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Nicolas,Laurie Dianga','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Coulter,Allison M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('MacDonald,Kyle Douglas','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Kindeur,Johanna','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ahmed,Subeia','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Simpson,Kyle E','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Hughes,Matthew Robert','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Iraheta,Brenda V','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ("D'Onofrio,Lauren R",'section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Balcha,Elias A','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Lam,Ngoc Kim','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Santos,Jenelyn','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Rateau,Vicky','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Roberson,Monette','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Fenelon,Cassandra R','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Guastalli,Holly B','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Chacon,Claudia L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Chung,Anna L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tierney,Sonja Anne','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Phan,Thanh D','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Connolly,Elizabeth Lauren','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Linsky,Ben Nathan','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('McCann,Patrick','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('McKinnon,Amber Irene','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Alexander,Jonathan A','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Khalifa,Nazim Bhikhu','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ammer,Fadlseed A','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Huynh,Caitlyn','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Kelty,Nathan James','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Sanchez,Aryany C','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Delgado,Stephanie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Cudmore,Jami Alison','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Zhao,Cuiyun','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Henkin,Matthew E','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Andrade,Matthew','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Kovaleva,Marina','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Okwara,Noreen Chioma','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Win,Ma Kyu K','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Abikan,Abdulgafar Abdulkadir','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Mawejje,Josephine Bukirwa','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Mejia,Kady R','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tobin,Nora J','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Eglington,Luke Glenn','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Wosny,Patricia Maria','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Beale,Shekeria S','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Woodward,Christina Angelina','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Jimma,Mahlet','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Burns,Latisha R','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Mejia,Yanel D','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Le-Nguyen,Minhgiao Ngoc','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Rahman,John Sheikh','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Britt,Marion P','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Pham,Thuy T','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Chamseddine,Abdul-hafiz O','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Piza,Elizabeth Anne','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Zhu,Liya','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Travis,Kevin Patrick','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tang,Jeanie','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tran,Tran','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Norton,Ryan M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Phillips,Kari L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Vu,Linh M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Evans,Matthew Jacob','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Alvarado,Janell K','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Wood,Brandon Charles','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Reed,Eric Raymond','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Eschuk,Danielle D','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Nakashian,Gregory H','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Farinloye,Samuel K','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Cabral,Jennifer B','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Guerrero,Ivan','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Cost,Robert James','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Clark, Andrew','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Blanchard,Victoria Astrid','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Kokalari,Aiola','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Mach,Henry','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Dorime,Michelson','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Tassinari,Anna M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Marinacci,Lucas X','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ruan,Allie Yi','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Pelham,James M','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Clark,John James','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Oulbacha,Asmae','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Alami,Maisa','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Nguyen,Mylinh','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Bailey,Michiko','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Nguyen,Nhu Da','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Onwuka,UJa Ude','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('White,Samantha Elizabeth','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Guillaume,Faradjin Esther','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('McCarthy-Bates,Melissa Michell','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Dubey,Himanshu','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Ogle,Janelle L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Nasser,Sahar Abdulla','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Maganzini,David','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Bautista,Jazmine Rose','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Medina-Rivera,Katy L','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Defendre,Marie C','section 02','12O69mG8XyfSc');
insert into students(name,section,password) values ('Gittins Stone,Daniel Ian','section 02','12O69mG8XyfSc');


-- create user, treesurvey
use mysql;
insert into user (Host,User,Password) VALUES ('localhost','treesurvey',PASSWORD('tr335urvey'));
grant all privileges on grades.* to treesurvey@localhost;
grant all privileges on trees.* to treesurvey@localhost;
flush privileges;
