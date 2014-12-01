// -------------------------------------------------------------------------------------
// Global definitions
// -------------------------------------------------------------------------------------
var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var calendarInstance = null;
var today = new Date();
var restoreDateClassName = "";
var restoreDateId = "";
var calendarStyles = null;
var calendarI18n = null;

// -------------------------------------------------------------------------------------
// Calendar object functions
// -------------------------------------------------------------------------------------

// Associates a single calendar instance with multiple anchors, given that the calendar instance is
// residing inside an iFrame
function atgCalendar_associateCalendarIFrame(newAnchorId, newDateInputId, calendarInstanceId) {
  //TODO: 
}

// Associates a single calendar instance with multiple anchors, by dynamically switching the date input id
// and anchor id at run time
function atgCalendar_associateCalendar(newAnchorId, newDateInputId, calendarInstanceId) {
  eval(calendarInstanceId).anchorId = newAnchorId;
  eval(calendarInstanceId).dateInputId = newDateInputId;
  atgCalendar_openCalendar(eval(calendarInstanceId));
}

// Calls the openCalendar function, but providing the id of the iFrame so that the objects may be
// correctly initialized. The name of the calendar instance object is assumed to be unchanged in the iFrame
function atgCalendar_openCalendarIFrame(iFrameId) {
  var iFrameObject = atgCalendar_findElementById(iFrameId);
  if (iFrameObject != null) {
    iFrameObject.contentWindow.atgCalendar_openIFrame(iFrameId);
  }
}

// Opens the calendar by accepting allowing a user-specified date to override the openDate.
// If neither dates are specified, today's date is selected.
function atgCalendar_openCalendar(calendarInstanceInput) {
  calendarInstance = calendarInstanceInput;
	calendarStyles = eval(calendarInstance.stylesId);
	calendarI18n = eval(calendarInstance.i18nId);
	
	var openDate = calendarInstance.openDate;
	var dateInputObject = null;
	if (calendarInstance.dateInputId != null)
		dateInputObject = atgCalendar_findElementById(calendarInstance.dateInputId);

	// The date input will always override the open date attribute
	if ((dateInputObject != null) && (dateInputObject.value != ""))
		openDate = dateInputObject.value;

  if ((openDate == null) || (openDate == "null")) {
    calendarInstance.selectedDate = new Date();
    
    if (calendarInstance.setDateOnCalendarOpen == true) {
  	  atgCalendar_setSelectedDate(calendarInstance.selectedDate);  
  	}

    calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
  	atgCalendar_changeDate(calendarInstance);
  	atgCalendar_showCalendar();
  }
  else {
    var returnUpdatedDate = true;
    var validationResult = atgValidation_validateDate(openDate, calendarI18n.dateFormat, calendarInstance.dateInputId, returnUpdatedDate);
    var isValid = false;
    var dateObject = null;
    
    // Test the response for an Object or boolean value
    if (validationResult.isValidDate == null) {
      if (validationResult == validationCode.SUCCESS) {
        isValid = true;
        dateObject = new Date(openDate);
      }
    }
    else if (validationResult.isValidDate == validationCode.SUCCESS) {
      dateObject = new Date(validationResult.openDate);
      if (validationResult.openDate != openDate) {
        dateInputObject.value = validationResult.openDate;
      }
      isValid = true;
    }
    
    if (isValid) {
      calendarInstance.selectedDate = dateObject;
   	  atgCalendar_setSelectedDate(calendarInstance.selectedDate);  
      calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
      atgCalendar_changeDate(calendarInstance);
    	atgCalendar_showCalendar();
    }
    else {
      calendarInstance.selectedDate = new Date();
      calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
      
      if (calendarInstance.clearDateOnError == true) {
        dateInputObject.value = "";
        atgCalendar_changeDate(calendarInstance);
        atgCalendar_showCalendar();
      }
      else {
        atgCalendar_alert(validationResult);
      }
    }
  }
}	

// Sets the current date back to the input control
function atgCalendar_setSelectedDate(optionalDateObject) {
	var dateInputObject = atgCalendar_findElementById(calendarInstance.dateInputId);
	if (dateInputObject != null) {
	  var i18nFormat = calendarI18n.dateFormat;
	  var dateObject = calendarInstance.selectedDate;
	  
	  if (optionalDateObject != null)
	    dateObject = optionalDateObject;
	  
	  dateInputObject.value = atgCalendar_getI18nDateStringFromObject(dateObject, i18nFormat);
	}
	
	return;
}

// Changes the date in the calendar
function atgCalendar_changeDate(calendarInstance) {
	var theDate = calendarInstance.displayDate;
	if ((theDate == null) || (theDate == ""))
		theDate = new Date();

  var containerObject = atgCalendar_findElementById(calendarInstance.containerId);
  var nodeType = containerObject.tagName;

  if (nodeType == "DIV")
    containerObject.innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
  else {
    var iframeObject = containerObject.contentWindow.document;
    var iframeBodyObject = iframeObject.body;
    iframeBodyObject.innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
  }
	
	if ((theDate.getFullYear() != today.getFullYear())
		||(theDate.getMonth() != today.getMonth())
		||(theDate.getDate() != today.getDate()))
	{
		atgCalendar_highlightDate(theDate.getDate());
	}
}

// Displays the previous month in the calendar
function atgCalendar_showPreviousMonth(calendarInstance) {
	var displayDate = calendarInstance.displayDate;
	if (displayDate == null)
		displayDate = calendarInstance.selectedDate;
	
	var currentMonth = displayDate.getMonth();
	var currentYear = displayDate.getFullYear();
	
	if (currentMonth == 1)
	{
		currentMonth = 12;
		currentYear --;
		displayDate.setFullYear(currentYear);
	}
	else
		currentMonth --;
	
	var daysInNewMonth = atgCalendar_getDaysInMonth(currentMonth,currentYear);
	if (displayDate.getDate() > daysInNewMonth) {
	  displayDate.setDate(daysInNewMonth);
	}
	
	displayDate.setMonth(currentMonth);
	calendarInstance.displayDate = displayDate;
	
	atgCalendar_findElementById(calendarInstance.containerId).innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
	atgCalendar_resizeIFrameContainer();
	atgCalendar_highlightSelectedDate();
}

// Displays the next month in the calendar
function atgCalendar_showNextMonth() {
	var displayDate = calendarInstance.displayDate;
	if (displayDate == null)
		displayDate = calendarInstance.selectedDate;
	
	var currentMonth = displayDate.getMonth();
	var currentYear = displayDate.getFullYear();
	
	if (currentMonth == 12)
	{
		currentMonth = 1;
		currentYear ++;
		displayDate.setFullYear(currentYear);
	}
	else
		currentMonth ++;
	
	var daysInNewMonth = atgCalendar_getDaysInMonth(currentMonth,currentYear);
	if (displayDate.getDate() > daysInNewMonth) {
	  displayDate.setDate(daysInNewMonth);
	}
	
	displayDate.setMonth(currentMonth);
	calendarInstance.displayDate = displayDate;

	atgCalendar_findElementById(calendarInstance.containerId).innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
	atgCalendar_resizeIFrameContainer();
	atgCalendar_highlightSelectedDate();
}

// Sets a new date in the calendar
function atgCalendar_setNewDate(newDate) {
	calendarInstance.selectedDate = newDate;
	calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
	atgCalendar_changeDate(calendarInstance);
	
	if (calendarInstance.closeAfterSelection)
		atgCalendar_hideCalendar();
}

// Sets a specified date for the current month in the calendar
function atgCalendar_setDateInMonth(dateInMonth) {
	atgCalendar_highlightDate(dateInMonth);
		
	if (calendarInstance.closeAfterSelection)
		atgCalendar_hideCalendar();
}

// Highlights a specified date in the calendar
function atgCalendar_highlightDate(theDate) {
	if ((restoreDateId != "") && (restoreDateClassName != ""))
	{
		var restoreObject = atgCalendar_findElementById(restoreDateId);
		if (restoreObject != null)
		{
			if ((calendarInstance.displayDate.getFullYear() == calendarInstance.selectedDate.getFullYear()) &&
				(calendarInstance.displayDate.getMonth() == calendarInstance.selectedDate.getMonth()))
			{
				restoreObject.className = restoreDateClassName;
			}
		}
	}
	restoreDateId = calendarInstance.calendarInstanceId + "_" + theDate;

	calendarInstance.displayDate.setDate(theDate);
	calendarInstance.selectedDate = new Date(calendarInstance.displayDate);

	var dateObject = atgCalendar_findElementById(restoreDateId);
	
	if (dateObject != null)
	{
		restoreDateClassName = dateObject.className;
		dateObject.className = calendarStyles.NumericsSelected;
	}
	
}

// Highlights the currently-selected date
function atgCalendar_highlightSelectedDate() {
	if ((calendarInstance.displayDate.getFullYear() == calendarInstance.selectedDate.getFullYear()) &&
		(calendarInstance.displayDate.getMonth() == calendarInstance.selectedDate.getMonth()))
	{
		var dateObject = atgCalendar_findElementById(restoreDateId);
		if (dateObject != null)
			dateObject.className = calendarStyles.NumericsSelected;
	}
}

// Positions and displays the calendar
function atgCalendar_showCalendar() {
	if ((calendarInstance.curtainId != null) && (calendarInstance.curtainId != ""))
	{
		var curtainObject = atgCalendar_findElementById(calendarInstance.curtainId);
		if (curtainObject != null) {
			
			var windowWidth = document.body.offsetWidth;
    	var windowHeight = document.body.offsetHeight;
    	var scrollWidth = document.body.scrollWidth;
    	var scrollHeight = document.body.scrollHeight;
    	
    	if (calendarInstance.iFrameId != null) {
    	  windowWidth = parent.document.body.offsetWidth;
    	  windowHeight = parent.document.body.offsetHeight;
    	  scrollWidth = parent.document.body.scrollWidth;
    	  scrollHeight = parent.document.body.scrollHeight;
    	}
			
			atgCalendar_debug("Curtain object before: Width="+curtainObject.style.width+", Height="+curtainObject.style.height);
			
			if (scrollWidth > windowWidth)
			  curtainObject.style.width = scrollWidth;
			else
			  curtainObject.style.width = windowWidth;
			  
      if (scrollHeight > windowHeight)
			  curtainObject.style.height = scrollHeight;
			else
			  curtainObject.style.height = windowHeight;
			
			atgCalendar_debug("Curtain object after: Width="+curtainObject.style.width+", Height="+curtainObject.style.height);
			curtainObject.style.display = "block";
		}
	}
	
	var anchorObject = atgCalendar_findElementById(calendarInstance.anchorId);
	var originalCalendarObject = atgCalendar_findElementById(calendarInstance.containerId);
  var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
  
  var isIFrame = false;
  if (calendarInstance.iFrameId != null) {
    calendarObject.style.display = "block";
    isIFrame = true;
	  calendarObject = atgCalendar_findElementById(calendarInstance.iFrameId);
	  calendarObject.style.display="block";
	}
	
	var domLeft = getOffsetLeft(calendarInstance.anchorId);
	var domTop = getOffsetTop(calendarInstance.anchorId);
	var domWidth = calendarObject.offsetWidth;
	var domHeight = calendarObject.offsetHeight;
	
	atgCalendar_debug("Calendar object: Width="+domWidth+", Height="+domHeight);
	atgCalendar_debug("Ideal positioning object: L="+domLeft+", R="+(domLeft+domWidth)+", T="+domTop+", B="+(domTop+domHeight));
	
	var horizontalOffset = calendarInstance.horizontalOffset;
	var verticalOffset = calendarInstance.verticalOffset;
	
	if (horizontalOffset == null)
		horizontalOffset = 0;
		
	if (verticalOffset == null)
		verticalOffset = 0;
	
	if ((calendarInstance.displayRightOfAnchor != null) && (calendarInstance.displayRightOfAnchor == false)) {
		calendarObject.style.left = (domLeft - domWidth) + "px";
	}
	else {
		calendarObject.style.left = (domLeft + new Number(horizontalOffset)) + "px";
	}
		
	if ((calendarInstance.displayBelowAnchor != null) && (calendarInstance.displayBelowAnchor == false)) {
		calendarObject.style.top = (domTop - domHeight) + "px";
	}
	else {
		calendarObject.style.top = (domTop + new Number(verticalOffset)) + "px";
	}

	// Test for overlap on the screen. If there is overlap, and the autoFlip attribute is set to true,
	// flip the calendar around one or both axes
	if (calendarInstance.autoFlip == true) {
		var positionObject = {
  			isIFrame: isIFrame,
  			left: parseInt(calendarObject.style.left),
  			top: parseInt(calendarObject.style.top),
  			width: domWidth,
  			height: domHeight
  		}

		atgCalendar_debug("Position object: L="+positionObject.left+", R="+(positionObject.left+positionObject.width)+", T="+positionObject.top+", B="+(positionObject.top+positionObject.height));
		var overlapObject = atgCalendar_getOverlapObject(positionObject);
		atgCalendar_debug("Overlap object: L="+overlapObject.leftOverlap+", R="+overlapObject.rightOverlap+", T="+overlapObject.topOverlap+", B="+overlapObject.bottomOverlap);

	  if (overlapObject.leftOverlap > 0) 
			calendarObject.style.left = (domLeft + new Number(horizontalOffset)) + "px";
		else if (overlapObject.rightOverlap > 0)
			calendarObject.style.left = (domLeft - domWidth) + "px";
		
		if (overlapObject.topOverlap > 0) 
			calendarObject.style.top = (domTop + new Number(verticalOffset)) + "px";
		else if (overlapObject.bottomOverlap > 0)
			calendarObject.style.top = (domTop - domHeight) + "px";
	}

	if (isIFrame == true) {
  	atgCalendar_resizeIFrameContainer();
  	originalCalendarObject.style.display = "block";
  }
  else {
    calendarObject.className = calendarStyles.CalendarDivShow;
	  calendarObject.style.display = "block";
  }
}

// Hides the calendar
function atgCalendar_hideCalendar() {
	if (calendarStyles != null)
		atgCalendar_findElementById(calendarInstance.containerId).className = calendarStyles.CalendarDivHide;
	atgCalendar_findElementById(calendarInstance.containerId).style.display="none";
	
	if ((calendarInstance.curtainId != null) && (calendarInstance.curtainId != ""))
	{
		var curtainObject = atgCalendar_findElementById(calendarInstance.curtainId);
		if (curtainObject != null)
			curtainObject.style.display = "none";
	}
	
	if (calendarInstance.iFrameId != null) {
    var calendarIFrame = atgCalendar_findElementById(calendarInstance.iFrameId);
    calendarIFrame.style.display = "none";
  }
}

// Resizes the iFrame container based on the size of the Div container
function atgCalendar_resizeIFrameContainer() {
  if (calendarInstance.iFrameId != null) {
	  var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
	  var domWidth = calendarObject.offsetWidth;
	  var domHeight = calendarObject.offsetHeight;
	  
	  calendarIFrame = atgCalendar_findElementById(calendarInstance.iFrameId);
	  calendarIFrame.width = domWidth;
	  calendarIFrame.height = domHeight;
	 }
}

// Returns true if the calendar is current visible
function atgCalendar_isCalendarVisible() {
	var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
	if (calendarObject == null)
		return false;
	
	if (calendarObject.style.display == "block")
		return true;
	else
		return false;
}

// Returns an object describing the vertical and horizontal overlap of the object with respect
// to the dimensions of the DOM
function atgCalendar_getOverlapObject(positionObject) {
	var overlapObject = {
		leftOverlap: 0,
		rightOverlap: 0,
		topOverlap: 0,
		bottomOverlap: 0
	}
	
	var right = (new Number(positionObject.left)) + (new Number(positionObject.width));
	var bottom = (new Number(positionObject.top)) + (new Number(positionObject.height));
	var windowWidth = null;
	var windowHeight = null;
	
	if (positionObject.isIFrame) {
	  windowWidth = parent.document.body.offsetWidth;
	  windowHeight = parent.document.body.offsetHeight;
	}
	else {
	  windowWidth = document.body.offsetWidth;
	  windowHeight = document.body.offsetHeight;
	}
	
	var testWidth = self.innerWidth;
	var testHeight = self.innerHeight;
	
	if (testWidth > windowWidth) 
	  windowWidth = testWidth;
	if (testHeight > windowHeight) 
	  windowHeight = testHeight;
	
	atgCalendar_debug("Window width = " + windowWidth + ", Window height = " + windowHeight);
	
	overlapObject.leftOverlap = 0 - positionObject.left;
	overlapObject.rightOverlap = right - windowWidth;
	overlapObject.topOverlap = 0 - positionObject.top;
	overlapObject.bottomOverlap = bottom - windowHeight;

	return overlapObject;
}

// Returns the true offsetLeft position of an anchor
function getOffsetLeft(anchorId) {
	var anchorObject = atgCalendar_findElementById(anchorId);
	var curleft = 0;
	if (anchorObject.offsetParent)
	{
		while (anchorObject.offsetParent)
		{
			curleft += anchorObject.offsetLeft
			anchorObject = anchorObject.offsetParent;
		}
	}
	else if (anchorObject.x)
		curleft += anchorObject.x;
	return curleft;
}

// Returns the true offsetTop position of an anchor
function getOffsetTop(anchorId) {
	var anchorObject = atgCalendar_findElementById(anchorId);
	var curtop = 0;
	if (anchorObject.offsetParent)
	{
		while (anchorObject.offsetParent)
		{
			curtop += anchorObject.offsetTop
			anchorObject = anchorObject.offsetParent;
		}
	}
	else if (anchorObject.y)
		curtop += anchorObject.y;
	return curtop;
}

// Returns the element given the id by looking in the current DOM or any ancestor DOMs
function atgCalendar_findElementById(elementId) {
  var domObject = document.getElementById(elementId);
  if (domObject == null) {
    domObject = parent.document.getElementById(elementId);
  }
  
  return domObject;
}

// Simple alert debugging
function atgCalendar_debug(debugString) {
  //alert(debugString);
}

// Simple alert function
function atgCalendar_alert(alertString) {
  alert(alertString);
}
