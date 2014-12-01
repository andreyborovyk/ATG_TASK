//
///////////////////////////////////////////////////////////////////////
 
var priceListMap = new Array();
var inheritCellList = new Array();
var volumeInfoCellList = new Array();
var currentPriceList;
var maxPageCount;

function modifiedTable(uniqueId) {
  //mark the table as modified
  var modCB = document.getElementById("modifiedTable");
  modCB.checked = true;

  // mark this particular cell as modified
  var cellModCB = document.getElementById("modified_" + uniqueId);
  if (cellModCB) cellModCB.checked = true;

  // mark the parent asset as modified
  markAssetModified();
}

function userPriceKeyPress(e) {

  // get the target out of the event
  if (!e){ var e = window.event;}

  if (e.target){ targ = e.target;}
  else if (e.srcElement){ targ = e.srcElement;}

  var priceStr = targ.value;

  // get the info from the id
  var currentTD = targ.parentNode;
  var id = currentTD.id;
  var type = id.split("_")[0]; 
  var cellNumber = id.substr(id.indexOf("_") + 1); 
  var rowId = cellNumber.split(".")[0];
  var index = cellNumber.split(".")[1];

  // for each first level child of this price list, update its inherit label
  updateChildLabels(rowId, index, priceStr);

  // set the modified flag for the table
  modifiedTable(rowId + "." + index);
}

function updateLabel(rowId, index, priceStr) {

 // get the div which surrounds the inherit label
 var iValue = document.getElementById("iValue_" + rowId + "." + index);

 // set the inherited price
 if (iValue != null) {

   iValue.innerHTML = priceStr;
 
   inheritCB = document.getElementById("inherit_" + rowId + "." + index).getElementsByTagName('INPUT')[0];
   volCB = document.getElementById("volume_" + rowId + "." + index).getElementsByTagName('INPUT')[0];
    
   // if this price is inherited, also update its children
   if (inheritCB.checked == true) {

     // if its not a link, it's a list price
     if(priceStr.search("<a")<0){
       volCB.checked = false;
       volCB.defaultChecked = false;
     }
     // otherwise, it's a volume price
     else {
       volCB.checked = true;
       volCB.defaultChecked = true;
     }

     // update the first level children's labels
     updateChildLabels(rowId, index, priceStr);
   }

 }  

}


function priceBoxClicked(e) {

  if (!e){ var e = window.event;}

  if (e.target){ targ = e.target;}
  else if (e.srcElement){ targ = e.srcElement;}
	
  var currentTD = targ.parentNode;
  var id = currentTD.id;
  priceBoxWithIdClicked(id);
}

function priceBoxWithIdClicked(id) {

  var type = id.split("_")[0]; 
  var uniqueId = id.substr(id.indexOf("_") + 1); 
  var rowId = uniqueId.split(".")[0];
  var index = uniqueId.split(".")[1];

  // div that contains the inherited value || this could be a Volume Price link or a number
  var iValue = document.getElementById("iValue_"+uniqueId);      	
  // div that contains the input field for the user entered price	
  var userPrice = document.getElementById("userPrice_"+uniqueId);  
  // div that conatins the link to the user defined volume price table     	
  var volPricing = document.getElementById("volPricing_"+uniqueId);	
    
  var inheritCB = null;
  var volCB = null;
    
  var valueTD = document.getElementById("value_"+uniqueId);

  switch(type){
    case "inherit":
      inheritCB = document.getElementById("inherit_"+uniqueId).getElementsByTagName('INPUT')[0];
      volCB = document.getElementById("volume_"+uniqueId).getElementsByTagName('INPUT')[0];
    	
      // both checkboxes are checked
      if(inheritCB.checked == true && volCB.checked == true){
					  
        if(iValue.innerHTML.search("<a")<0){
    		
          volCB.checked = false;
          volCB.defaultChecked = false;
        }
		
	updateChildLabels(rowId, index,iValue.innerHTML);
		
      // inherit is checked, volume is not
      } else if(inheritCB.checked == true && volCB.checked == false){	
                                
        // if the inherited price is a volume price, check volume button.
        if(iValue.innerHTML.search("<a")>-1){
    		
          volCB.checked = true;
          volCB.defaultChecked = true;
    			
         }
	updateChildLabels(rowId, index,iValue.innerHTML);		
      
      } else if (inheritCB.checked == false && volCB.checked == true) {
	updateChildLabels(rowId, index,volPricing.innerHTML);			
      } else if (inheritCB.checked == false && volCB.checked == false) {        
        var userPriceInput = userPrice.getElementsByTagName('INPUT')[0];
	updateChildLabels(rowId, index,userPriceInput.value);	
      }

      break;

    case "volume":
    	
      inheritCB = document.getElementById("inherit_"+uniqueId).getElementsByTagName('INPUT')[0];
      volCB = document.getElementById("volume_"+uniqueId).getElementsByTagName('INPUT')[0];
    		
      // both checkboxes are checked
      if(inheritCB.checked == true && volCB.checked == true){
					      		
        inheritCB.checked = false;
        inheritCB.defaultChecked = false;
		
      } else if(inheritCB.checked == true && volCB.checked == false){	
        // both checkboxes are checked					      		
    	inheritCB.checked = false;
    	inheritCB.defaultChecked = false;
		
      }

      if (volCB.checked == true) {

        //   add "bulkPrice" to the volPriceInfo
        //   var volPriceInfo = document.getElementById("volPriceInfo_" + uniqueId);
        //   volPriceInfo.value = "bulkPrice";

	updateChildLabels(rowId, index,volPricing.innerHTML);			
      } else  {        
        var userPriceInput = userPrice.getElementsByTagName('INPUT')[0];
	updateChildLabels(rowId, index,userPriceInput.value);	
      }
    	
      break;
    		
    default:
        alert("type is unknown");
  }

  // both checkboxes are checked
  if(inheritCB.checked == true && volCB.checked == true){
    	
    iValue.style.display = "block";
    userPrice.style.display = "none";
    volPricing.style.display = "none";

  } 
  // inherit is checked, volume is not
  else if(inheritCB.checked == true && volCB.checked == false){	
    iValue.style.display = "block";
    userPrice.style.display = "none";
    volPricing.style.display = "none";
    
  }
  // volume is checked, inherit is not
  else if(inheritCB.checked == false && volCB.checked == true){	
    	    	
    iValue.style.display = "none";
    userPrice.style.display = "none";
    volPricing.style.display = "block";
    
  }
  // neither checkbox is checked
  else if(inheritCB.checked == false && volCB.checked == false){ 
    
    iValue.style.display = "none";
    userPrice.style.display = "block";
    volPricing.style.display = "none";
    
  } 
  
  // set modified flag on table
  modifiedTable(uniqueId);
}

function showVolumeDialog(e, priceId, uniqueId, aclwritable) {

  // get the target from the event
  var targ;
  if (!e){ var e = window.event;}
  if (e.target){ targ = e.target;}
  else if (e.srcElement){ targ = e.srcElement;}

  // if the parent element is the inherit div, the dialog is readonly
  var parentdiv = targ.parentNode;
  var parentID =  parentdiv.id;
  // if the value displayed is an inherited value, the dialog is read only
  var readOnly =  (parentID.search("iValue") >= 0);
  // if the pricelist's acl is non-writable, the dialog is read only
  if (!aclwritable) readOnly = true;

  // get the dialog frame	
  var frame = parent.document.getElementById("volumeDialogFrame");
  // get the volume price info field in order to pass the vol info string into dialog
  var volPriceInfo = document.getElementById("volPriceInfo_" + uniqueId);

  // create the dialog url
  var frameURL = "/Merchandising2/assetEditor/volumeDialog.jsp?priceId=" + priceId + "&uniqueId="+ uniqueId + "&readOnly=" + readOnly;
  if (volPriceInfo != null)  {
    frameURL = frameURL + "&volPriceInfo=" + volPriceInfo.value;
  }   else {
    frameURL = frameURL + "&volPriceInfo=" + volumeInfoCellList[uniqueId];
  }  
  frame.src = frameURL;

  // popup the dialog
  parent.showIframe("volumeDialog");

}


function submitPriceListTable() {

  // if they have typed in a page number - GOTO that page.
  processGotoPageNumber();

  var form = document.getElementById("assetForm");
  form.submit();
}

function stepPageNumber(increase) {

  var currentPageNumber = 1;
  var pageNumberInput = document.getElementById("currentPageInput");
  if (pageNumberInput)
    currentPageNumber = parseInt(pageNumberInput.value);

  if (increase)
    currentPageNumber = currentPageNumber + 1;
  else currentPageNumber = currentPageNumber - 1;

  if (currentPageNumber > 0)
    pageNumberInput.value = currentPageNumber;

  var form = document.getElementById("assetForm");
  form.submit();

}

function gotoPageNumber(pageNumber) {

  if (maxPageCount > 0 && pageNumber > maxPageCount) pageNumber = maxPageCount;
  if (pageNumber <= 0) pageNumber = 1;

  var pageNumberInput = document.getElementById("currentPageInput");
  pageNumberInput.value = pageNumber;

  var form = document.getElementById("assetForm");
  form.submit();

}

function processGotoPageNumber() {

  var gotoPageInput = document.getElementById("gotoPageInput");
  var pageNumberInput = document.getElementById("currentPageInput");

  if (gotoPageInput != null) {
    var gotoPageValue = gotoPageInput.value;
    var gotoPageInt = parseInt(gotoPageValue);
    if (maxPageCount > 0 && gotoPageInt > maxPageCount) gotoPageInt = maxPageCount;
    if (gotoPageInt <= 0) gotoPageInt = 1;
    pageNumberInput.value = gotoPageInt;
    gotoPageInput.value= "";
  }
}

function handleEmptyPrice(e) {
  // get the target out of the event
  if (!e){ var e = window.event;}

  if (e.target){ targ = e.target;}
  else if (e.srcElement){ targ = e.srcElement;}

  var priceStr = targ.value;

  if (priceStr == null || priceStr.length == 0) {
    // get the info from the id
    var currentTD = targ.parentNode;
    var id = currentTD.id;
    var type = id.split("_")[0];
    var cellNumber = id.substr(id.indexOf("_") + 1); 
    var rowId = cellNumber.split(".")[0];
    var index = cellNumber.split(".")[1];

    inheritCB = document.getElementById("inherit_" + rowId + "." + index).getElementsByTagName('INPUT')[0];
    if (!inheritCB.checked) {
        var id = "inherit_" + rowId + "." + index;
        inheritCB.checked = true;
        priceBoxWithIdClicked(id);
    }
  }
}

//----------------------------------------------------------------------------//
// End of priceListScripts.js
//----------------------------------------------------------------------------//
