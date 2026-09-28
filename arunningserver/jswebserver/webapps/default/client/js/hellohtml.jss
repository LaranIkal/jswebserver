
var searchDict = { 
"taskStatus":3, 
"description":9,
};
function dataSearch(e) {
  var input, filter, table, tr, td, i, txtValue, type;
  
  const self = this;
  const dataSet =  e.currentTarget.dataset;
  type = dataSet.type;
  const element = e.currentTarget;

  cleanup(type);
  searchTable(element.value, type)
}
function searchTable(value,type){
    let filter,table, tr,td,txtValue, column;

    column = searchDict[type];
    filter = value.toUpperCase();
    
    table = document.getElementById("myTable");
    tr = table.getElementsByTagName("tr");
    for (i = 0; i < tr.length; i++) {
      td = tr[i].getElementsByTagName("td")[column];
      if (td) {
        txtValue = td.textContent || td.innerText;
        if (txtValue.toUpperCase().indexOf(filter) > -1) {
          tr[i].style.display = "";
        } else {
          tr[i].style.display = "none";
        }
      }
    }
}
function cleanup(type) {
  var tr = document.getElementById("searchTable");
  var input;
  for (var i = 0; i < tr.children.length;i++) {
    input = tr.children[i].getElementsByTagName("input")[0];
    if (input) {
      if(input.dataset.type == type){
        continue;
      }else {
        input.value = "";
      }
    }
  }
}
//Pad given value to the left with "0"
function AddZero(num) {
    return (num >= 0 && num < 10) ? "0" + num : num + "";
}

window.onload = function() {
  var now = new Date();
  var strDateTime = [[AddZero(now.getDate()), 
      AddZero(now.getMonth() + 1), 
      now.getFullYear()].join("/"), 
      [AddZero(now.getHours()), 
      AddZero(now.getMinutes())].join(":"), 
      now.getHours() >= 12 ? "PM" : "AM"].join(" ")


  // clientDateTime YearMonthDayHourMinutes
  var clientDateTime = now.getFullYear() +
                          AddZero(now.getMonth() + 1) +
                          AddZero(now.getDate()) +
      AddZero(now.getHours()) +
      AddZero(now.getMinutes())

  var OSName="Unknown OS"
  if (navigator.appVersion.indexOf("Win")!=-1) OSName="Windows"
  if (navigator.appVersion.indexOf("Mac")!=-1) OSName="MacOS"
  if (navigator.appVersion.indexOf("X11")!=-1) OSName="UNIX"
  if (navigator.appVersion.indexOf("Linux")!=-1) OSName="Linux"

  var myPixelSrc = "<img src=\"/sample/Stats&vara=" + navigator.product + "&varb=" + OSName + "&varc=" + clientDateTime + "\" width=\"1\" height=\"1\">"
  document.getElementById("StatTime").innerHTML = "" + myPixelSrc
  
  document.getElementById("refreshDateTime").innerHTML = "" + strDateTime
  
}

