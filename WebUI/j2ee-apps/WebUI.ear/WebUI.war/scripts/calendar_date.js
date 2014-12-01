// -------------------------------------------------------------------------------------
// Calendar date calculations
// -------------------------------------------------------------------------------------

// Returns the day of the week for the first day of a given month
function atgCalendar_getFirstDayOfWeek(newDate) 
{
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var firstDay = new Date(newYear,newMonth,1);
	return firstDay.getDay();
}

// Returns the day of the week for the last day of a given month
function atgCalendar_getLastDayOfWeek(newDate) 
{
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var numDays = atgCalendar_getDaysInMonth(newMonth, newYear);
	var lastDay = new Date(newYear,newMonth,numDays);
	return lastDay.getDay();
}

// Returns the day of the month from a given date object
function atgCalendar_getSelectedDay(newDate)
{
	return newDate.getDate();
}

// Returns the name of the month from a given date object
function atgCalendar_getSelectedMonth(newDate)
{
	var newMonth = newDate.getMonth();
	return calendarI18n.monthNames[newMonth];
}

// Returns the four-digit year from a given date object
function atgCalendar_getSelectedYear(newDate)
{
	return newDate.getFullYear();
}

// Returns the name of the day of the week given a day of the month integer
function atgCalendar_getDayOfWeek(dayNum)
{
	return calendarI18n.dayNames[dayNum];
}

// Returns the number of days in a month for a given date object
function atgCalendar_getDaysInMonth(theMonth,theYear)
{
	// Compensate for the month array starting at zero
	theMonth ++;
	
	if (theMonth == 2)
	{
		// Test for leap year
		if (((theYear % 4 == 0) && (theYear % 100 != 0)) || (theYear % 400 == 0)) {
			daysInMonth[1] = 29;
		}
		else
			daysInMonth[1] = 28;
	}

	return daysInMonth[theMonth-1];
}

// Returns a date object representing the day after a given date object
function atgCalendar_getNextDay(newDate)
{
	newDate.setDate(newDate.getDate()+1);
	return newDate;
}

// Returns a date object representing the day before a given date object
function atgCalendar_getPreviousDay(newDate)
{
	newDate.setDate(newDate.getDate()-1);
	return newDate;
}

// Returns a serialized date String of the form MM/DD/YYYY for the day after a given String date
function atgCalendar_getNextDayAsString(newDateString, i18nSep)
{
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	var newDate = new Date(newDateString);
	newDate.setDate(newDate.getDate()+1);
	var newDateString = newDate.getMonth() + 1 + splitChar + newDate.getDate() + splitChar + newDate.getFullYear();
	return newDateString;
}

// Returns a serialized date String of the form MM/DD/YYYY for the day before a given String date
function atgCalendar_getPreviousDayAsString(newDateString, i18nSep)
{
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	var newDate = new Date(newDateString);
	newDate.setDate(newDate.getDate()-1);
	var newDateString = newDate.getMonth() + 1 + splitChar + newDate.getDate() + SplitChar + newDate.getFullYear();
	return newDateString;
}

// Returns a short form of a specified date
function atgCalendar_formatShortDate(theDate, i18nSep)
{
	if (theDate == "")
		return "";

  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	return (theDate.getMonth() + 1) + splitChar + theDate.getDate() + splitChar + theDate.getFullYear();
}

// Returns the long form of a specified date
function atgCalendar_formatLongDate(theDate)
{
	if (theDate == "")
		return "";
		
	var weekday = atgCalendar_getDayOfWeek(theDate.getDay());
	var monthName = atgCalendar_getSelectedMonth(theDate);
	return weekday + ", " + monthName + " " + theDate.getDate() + ", " + theDate.getFullYear();
}

// Formats a four-digit year representation of the current date
function atgCalendar_formatYear(yearString)
{
	theYear = new Number(yearString);
	if (isNaN(yearString))
	  return yearString;
	
	if ((theYear > 20) && (theYear < 100)) {
		theYear = 1900 + new Number(theYear);
	}
	else if ((theYear > 0) && (theYear < 21)) {
		theYear = 2000 + new Number(theYear);
	}
		
	return theYear;
}

// Given an i18n format and an i18n date string, this function returns a
// correctly-formatted date object
function atgCalendar_getI18nDateObject(i18nDateString, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep);
  if (i18nDateArray == null)
    return null;
  
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var dateObject = new Date(i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2]);
  return dateObject;
}

// Given an i18n format and an i18n date string, this function returns a 
// correctly-formatted date string
function atgCalendar_getI18nDateString(i18nDateString, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep);
  if (i18nDateArray == null)
    return null;
  
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var serializedDate = i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2];
  return serializedDate;
}


// Given an i18n format and an i18n date object, this function returns a 
// correctly-formatted date string
function atgCalendar_getI18nDateStringFromObject(i18nDateObject, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateObject(i18nDateObject, i18nFormat);
  if (i18nDateArray == null)
    return null;
  
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var serializedDate = i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2];
  return serializedDate;
}


// Given an i18n format and an i18n date string, this function returns 
// an array of date values
function atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep) {
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var formatArray = i18nFormat.split(splitChar);
  var dateArray = i18nDateString.split(splitChar);
  var i18nDateArray = new Array(-1,-1,-1);
  
  if ((formatArray.length < 3) || (dateArray.length < 3))
    return null;
  
  // Calculate the position of the date format
  for (var i=0; i< formatArray.length; i++) {
    if ((formatArray[i].indexOf("M") != -1) || (formatArray[i].indexOf("m") != -1)) {
      i18nDateArray[0] = dateArray[i];
    }
    else if ((formatArray[i].indexOf("D") != -1) || (formatArray[i].indexOf("d") != -1)) {
      i18nDateArray[1] = dateArray[i];
    }
    else if ((formatArray[i].indexOf("Y") != -1) || (formatArray[i].indexOf("y") != -1)) {
      i18nDateArray[2] = dateArray[i];
    }
  }
  
  var yearReference = i18nDateArray[2];
  i18nDateArray[2] = atgCalendar_formatYear(i18nDateArray[2]);
  
  return i18nDateArray;
}


// Given an i18n format and an i18n date object, this function returns 
// an array of date values
function atgCalendar_getI18nDateArrayFromDateObject(i18nDateObject, i18nFormat, i18nSep) {
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var formatArray = i18nFormat.split(splitChar);
  var i18nDateArray = new Array(-1,-1,-1);
  
  if ((formatArray.length < 3) || (i18nDateObject == null))
    return null;
  
  // Calculate the position of the date format
  for (var i=0; i< formatArray.length; i++) {
    if ((formatArray[i].indexOf("M") != -1) || (formatArray[i].indexOf("m") != -1)) {
      i18nDateArray[i] = i18nDateObject.getMonth()+1;
    }
    else if ((formatArray[i].indexOf("D") != -1) || (formatArray[i].indexOf("d") != -1)) {
      i18nDateArray[i] = i18nDateObject.getDate();
    }
    else if ((formatArray[i].indexOf("Y") != -1) || (formatArray[i].indexOf("y") != -1)) {
      i18nDateArray[i] = i18nDateObject.getFullYear();
    }
  } 
  
  return i18nDateArray;
}

