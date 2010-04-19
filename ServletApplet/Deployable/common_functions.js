function getAdminValue() {
	document.form.Browser.value = navigator.userAgent;
	if(document.form.AdminCB.checked) {
		document.form.AdminValue.value = "true";
	}
	return true;
}
function checkLogin() {
	if(!document.form.AdminCB.checked) {
		var user = document.form.Name.value;
		if(user == '') {
				alert("You must select a username!");
						return false;
	}
}
var pass = document.form.Passwd.value;
if(pass == '') {
		alert("You must enter a password!");
				return false;
	}
	return true;
}
function updateView() {
	if(document.layers) {
		if(document.InvalidLogin) {
			document.InvalidLogin.display = 'none';
		}
	} else {
		if(document.all.InvalidLogin) {
			document.all.InvalidLogin.style.display = 'none';
		}
	}
	if(document.form.AdminCB.checked) {
		if(document.layers){
			document.NameSelection.display = 'none';
			document.AdminPassText.display = 'block';
			document.StudentPassText.display = 'none';
		} else {
			document.all.NameSelection.style.display = 'none';
			document.all.AdminPassText.style.display = 'block';
			document.all.StudentPassText.style.display = 'none';
		}
	} else {
		if(document.layers){
			document.NameSelection.display = 'block';
			document.AdminPassText.display = 'none';
			document.StudentPassText.display = 'block';
		} else {
			document.all.NameSelection.style.display = 'block';
			document.all.AdminPassText.style.display = 'none';
			document.all.StudentPassText.style.display = 'block';
		}
	}
}
function isComplete() {
	var qip = document.TreeApplet.questionInProgress();
	if(qip != ''){
		if(confirm("Is it ok to save your changes to question " + qip + "?")){
			document.TreeApplet.acceptQuestionInProgress();
		}
	}
	var status = document.TreeApplet.getStatus();
	document.forms[0].treeXML.value = document.TreeApplet.getTree();
	document.forms[0].Q1.value = document.TreeApplet.getQ1();
	document.forms[0].Q2.value = document.TreeApplet.getQ2();
	if(status != ''){
		return confirm(status + " Is it ok to save?");
	}
	return true;
}
	