Deployment Notes

Prerequisites:  

1) You must go to the google code site, http://code.google.com/p/tree-buildingsurvey/, and download the existing
project one of two ways.
	A) Download Toroise SVN and use the directions listed on the "Source" tab of the google code page to checkout the current project
	B) Go to the "Downloads" tab and download the latest .zip file of the project
2) You must have Java 6(or most recent version) installed on your computer
3) You must have Ant 1.7(or latest version) installed on your computer


Steps for Deployment:
1) go to the directory of your local copy of the project in the command line 

2) run the command "ant jarfile", this creates your jarfile that executes the applet

3) run the command "ant javadoc", this creates the javadoc to be viewed on the web

4) update the file applet.html with the current version notes (new functions, future functions, removed functions, etc.)
	-You can do this by adding a some simple text within a <p> tag above the <applet> tag

5) run the command,
"ant -Dversion=[version number] -Dusername=[server username] -Dpassw=[server password] -Dhostname=[server name] -Dserver_dir= [server directory] deploy",
this will deploy the application on whichever server you would like to house it

6) update the wiki links to the updated version on the google code wiki(this step only required for TBS Dev Group members)
	-"Test Current Tree-Building Applet" link on home page
	-"Test Applets", wiki page (http://code.google.com/p/tree-buildingsurvey/wiki/Applets)
	-wiki table of contents (http://code.google.com/p/tree-buildingsurvey/w/edit/TableOfContents)
	-any changes to README.TXT should be reflected in the wiki page OneStepBuild
	