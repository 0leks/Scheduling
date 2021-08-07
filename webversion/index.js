
var employeePreferences = {};

function loadFileAsText(){
    var fileToLoad = document.getElementById("fileToLoad2").files[0];
  
    var fileReader = new FileReader();
    fileReader.onload = function(fileLoadedEvent){
        var textFromFileLoaded = fileLoadedEvent.target.result;
        // console.log(textFromFileLoaded);
        employeePreferences = loadPreferences(textFromFileLoaded);
        populatePreferences(employeePreferences);
        //document.getElementById("inputTextToSave").value = textFromFileLoaded;
    };
  
    fileReader.readAsText(fileToLoad, "UTF-8");
}

function populatePreferences(preferences) {
    console.log("Populating preferences!");
    var prefTable = document.getElementById("availabilityTable");
    for(var key in preferences) {
        var value = preferences[key];

        console.log(key, value);
        var row = document.createElement("tr");  
        var header = document.createElement("th");
        var nameText = document.createTextNode(key);
        header.appendChild(nameText);
        row.appendChild(header);
        prefTable.appendChild(row);

        elem = row.appendChild(document.createElement("td"));
        elem.appendChild(document.createTextNode(value["Monday"]));
        
        elem = row.appendChild(document.createElement("td"));
        elem.appendChild(document.createTextNode(value["Tuesday"]));
        
        elem = row.appendChild(document.createElement("td"));
        elem.appendChild(document.createTextNode(value["Wednesday"]));
        
        elem = row.appendChild(document.createElement("td"));
        elem.appendChild(document.createTextNode(value["Thursday"]));
        
        elem = row.appendChild(document.createElement("td"));
        elem.appendChild(document.createTextNode(value["Friday"]));
    }

}

function loadPreferences(preferencesText) {
    var employees = {};
    console.log(preferencesText);
    splitByPerson = preferencesText.split('\n');
    console.log(splitByPerson);
    splitByPerson.forEach(function (item, index) {
        splitByDay = item.split(' ');
        if(splitByDay.length == 10) {
            name = splitByDay[9];
            splitByDay.splice(9, 1);
            console.log(name, splitByDay);
            employees[name] = {};
            employees[name]["Monday"] = splitByDay[0] == "Y";
            employees[name]["Tuesday"] = splitByDay[2] == "Y";
            employees[name]["Wednesday"] = splitByDay[4] == "Y";
            employees[name]["Thursday"] = splitByDay[6] == "Y";
            employees[name]["Friday"] = splitByDay[8] == "Y";
        }
        console.log(item, index);
        console.log(item.split(' '));
    });
    console.log(employees);
    return employees;
}

function generateSchedule() {

}

$(document).ready(function() {
    // stuff to do on ready
});