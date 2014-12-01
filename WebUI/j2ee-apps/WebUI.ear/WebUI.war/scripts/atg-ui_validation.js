// -------------------------------------------------------------------------------------
// Static validation objects
// -------------------------------------------------------------------------------------

/**
  * Primitive and Composite validation functions:
  * atgValidation_validateRequiredField(testString)
  * atgValidation_validateMinMaxLength(testString, minLength, maxLength)
  * atgValidation_validateNoSpecialCharacters(testString)
  * atgValidation_validateAlphaCharacters(testString)
  * atgValidation_validateAlphaNumericCharacters(testString)
  * atgValidation_validateNumericCharacters(testNumber)
  * atgValidation_validateNumericRange(testNumber, lowRange, hiRange)
  * atgValidation_validateEmailAddress(testString)
  * atgValidation_validateURL(testString)
  * atgValidation_validateDate(testString)
  */

// Static final object for constants
constants = {
  generalErrorMessageId: "page_message",
  alertMessageSeparator: "\n",
  generalMessageSeparator: "<br/>",
  inlineMessageSeparator: ", ",
  defaultSubstitutionToken: "###"
}

validationCode = {
  MESSAGE_GENERAL:"There were errors with your submission. The following fields need to be corrected:",
  MESSAGE_SPECIFIC:"",
  SUCCESS:"Validation successful",
  ERROR_GENERAL:"General validation error",
  ERROR_NULL_STRING: "You must enter a value in this field",
  ERROR_EMPTY_STRING: "You must enter a value in this field",
  ERROR_MIN_LENGTH: "You must enter a value that contains at least " + constants.defaultSubstitutionToken + " characters",
  ERROR_MAX_LENGTH: "You must enter a value that contains no more than " + constants.defaultSubstitutionToken + " characters",
  ERROR_SPECIAL_CHARACTERS: "Please do not enter special characters in this field",
  ERROR_ALPHA_CHARACTERS: "Please enter only alphabetic characters in this field",
  ERROR_ALPHA_NUMERIC_CHARACTERS: "Please enter only alphanumeric characters in this field",
  
  ERROR_NUMERIC_CHARACTERS: "This field may only contain numeric characters",
  ERROR_NUMERIC_RANGE_LOW: "You must enter a number that is greater or equal to " + constants.defaultSubstitutionToken,
  ERROR_NUMERIC_RANGE_HIGH: "You must enter a number that is less than or equal to " + constants.defaultSubstitutionToken,
  
  ERROR_EMAIL_ADDRESS_FORMAT: "The e-mail address is not in the correct format. Please try again. E-mail addresses must be formatted as name@domainname.extension",
  ERROR_URL_FORMAT: "The URL is not in the correct format. Please try again and using  www.domain.extension or http://www.domainname.extension as the format",
  ERROR_DATE_FORMAT: "Please check that your date is in the format " + constants.defaultSubstitutionToken,
  ERROR_DATE_RANGE_DAY: "Please check your day value and enter a number from 1 to 31",
  ERROR_DATE_RANGE_MONTH: "Please check your month value and enter a number from 1 to 12",
  ERROR_DATE_RANGE_YEAR: "Please check your year value and enter a number between 1900 and 2050",
  ERROR_DATE_DAYS_IN_MONTH: "Please check your date and enter a day value that occurs in the selected month"
}

// This object is required to keep track of any validation objects created on the screen
// Its contents should be initialized and hidden at the start of every validation
currentValidationObjectArray= new Array();

// Define the global success variable that will be used to determine whether the validation function succeeds or
// fails overall
var isSuccess = true;


// -------------------------------------------------------------------------------------
// Primitive validation functions
// -------------------------------------------------------------------------------------

// Validates a required field and returns a validation code. The optional allowWhitespace
// attribute (when set to false) will ensure that whitespace prevents successful validation
function atgValidation_validateRequiredField(testString, allowWhitespace) {
  if (testString == null)
    return validationCode.ERROR_NULL_STRING;
    
  if (testString.length == 0)
    return validationCode.ERROR_EMPTY_STRING;

  if ((allowWhitespace != null) && (allowWhitespace == false)) {
    // Test for the presence of whitespace
    var onlyWhitespace = true;
    for (charCount = 0; charCount < testString.length; charCount++) {   
        charLetter = testString.charAt(charCount);
        if (charLetter != " ") {
        	onlyWhitespace = false;
        }
    }
    
    if (onlyWhitespace == true)
      return validationCode.ERROR_EMPTY_STRING;
  }
  
  return validationCode.SUCCESS;
}

// Validates a required field and returns a validation code
function atgValidation_validateMinMaxLength(testString, minLength, maxLength) {
  if (testString == null)
    return validationCode.ERROR_NULL_STRING;
    
  
  if (minLength != null) {
    if (testString.length < minLength)
      return atgCommon_substituteToken(validationCode.ERROR_MIN_LENGTH, minLength, constants.defaultSubstitutionToken);
  }
  
  if (maxLength != null) { 
    if (testString.length > maxLength)
      return atgCommon_substituteToken(validationCode.ERROR_MAX_LENGTH, maxLength, constants.defaultSubstitutionToken);
  }
  
   return validationCode.SUCCESS;
}

// Validates a string for the presence of any special characters
function atgValidation_validateNoSpecialCharacters(testString) {
  var invalidCharacters = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_SPECIAL_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only alphabetic characters are specified
function atgValidation_validateAlphaCharacters(testString) {
  var alphaCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (alphaCharacters.indexOf(charLetter,0) == -1) {
      	return validationCode.ERROR_ALPHA_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only alphanumeric characters are specified
function atgValidation_validateAlphaNumericCharacters(testString) {
  var alphaNumericCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (alphaNumericCharacters.indexOf(charLetter,0) == -1) {
      	return validationCode.ERROR_ALPHA_NUMERIC_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only numeric characters are specified
function atgValidation_validateNumericCharacters(testNumber) {
  for (charCount = 0; charCount < testNumber.length; charCount++) {   
      charLetter = testNumber.charAt(charCount);
      if (((charLetter < "0") || (charLetter > "9"))) {
      	return validationCode.ERROR_NUMERIC_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates an integer to ensure that it is within the specified range
function atgValidation_validateNumericRange(testNumber, lowRange, hiRange) {
  if (lowRange != null) {
    if (testNumber < lowRange) {
      return atgCommon_substituteToken(validationCode.ERROR_NUMERIC_RANGE_LOW, lowRange, constants.defaultSubstitutionToken);
      
    }
  }
  
  if (hiRange != null) {
    if (testNumber > hiRange) {
      return atgCommon_substituteToken(validationCode.ERROR_NUMERIC_RANGE_HIGH, hiRange, constants.defaultSubstitutionToken);
    }
  }
    
  return validationCode.SUCCESS;
}

// -------------------------------------------------------------------------------------
// Composite validation functions
// -------------------------------------------------------------------------------------

// Validates an e-mail address by ensuring that an @ symbol and a . are specified. This
// function will eventually be moved into a regular expression to better match the input
function atgValidation_validateEmailAddress(testString) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;
  
  var invalidCharacters = "!#$%^&*()+=[]\\\';,/{}|\":<>?";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
      }
  }

  // Test that there is at least one '@' symbol and one or more '.' symbols - and make sure that the last '.' 
  // symbol is after the '@' symbol
  if ((testString.indexOf("@") < 0) || (testString.indexOf(".") < 2) || (testString.indexOf("@") > testString.lastIndexOf(".")))
    return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
   
  // Test that there is only one '@' symbol in the string
  if (testString.indexOf("@") != testString.lastIndexOf("@"))
    return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
  
  return validationCode.SUCCESS;
}

// Validates a URL by ensuring that an no special characters (other than those permitted by URLs)
// are used. In addition the function ensures that the a . is always specified. This
// function will eventually be moved into a regular expression to better match the input
function atgValidation_validateURL(testString) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;
  
  var invalidCharacters = "!#$^*()+[]\\\',{}|\"<>";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_URL_FORMAT;
      }
  }

  return validationCode.SUCCESS;
}

// Validates the String to ensure that it is a correctly formatted date
function atgValidation_validateDate(testString, dateFormat, optionalDateInputId, returnUpdatedDate) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;
  
  // Reformat the date to conform to the i18n date format
  if ((dateFormat == null) || (dateFormat == "")) {
    dateFormat = "MM/DD/YY";
  }

  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(testString, dateFormat);
  if (i18nDateArray == null)
    return atgCommon_substituteToken(validationCode.ERROR_DATE_FORMAT, dateFormat, constants.defaultSubstitutionToken);
  
  var theMonth = i18nDateArray[0];
	var theDay = i18nDateArray[1];
	var theYear = i18nDateArray[2];

	// Numeric character validation
	var numericValidationMonth = atgValidation_validateNumericCharacters(theMonth);
	var numericValidationDay = atgValidation_validateNumericCharacters(theDay);
	var numericValidationYear = atgValidation_validateNumericCharacters(theYear);

	if ((numericValidationMonth != validationCode.SUCCESS) || (numericValidationDay != validationCode.SUCCESS)
	  || (numericValidationYear != validationCode.SUCCESS))
	  return validationCode.ERROR_NUMERIC_CHARACTERS;
	
  // Explicitly convert any numeric strings to integers
	theMonth = new Number(theMonth);
	theDay = new Number(theDay);
	theYear = new Number(theYear);
	
	// Range checking validation
	var rangeValidationMonth = atgValidation_validateNumericRange(theMonth, 1, 12);
	var rangeValidationDay = atgValidation_validateNumericRange(theDay, 1, 31);
	var rangeValidationYear = atgValidation_validateNumericRange(theYear, 1900, 2050);
	
	if (rangeValidationMonth != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_MONTH;
	  
	if (rangeValidationDay != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_DAY;
	
	if (rangeValidationYear != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_YEAR;
	
  // Range check days of month with respect to current month
  var maxDaysInMonth = atgCalendar_getDaysInMonth(theMonth-1,theYear);
	if (theDay > maxDaysInMonth)
	  return validationCode.ERROR_DATE_DAYS_IN_MONTH;
 
  // If this point has been reached, the validation is successful
  var newDateString = atgCalendar_getI18nDateString((theMonth + "/" + theDay + "/" + theYear), dateFormat);
  
  // Update the content of the date field with the updated date
  if (optionalDateInputId != null) {
    dateInputObject = atgCalendar_findElementById(optionalDateInputId);
    if (dateInputObject != null)
      dateInputObject.value = atgCalendar_getI18nDateString(newDateString, dateFormat);
  }

  // Determine whether to return the validation code or the validation object
  if (returnUpdatedDate == true) {
    var validationObject = new Object();
    validationObject.isValidDate = validationCode.SUCCESS;
    validationObject.openDate = newDateString;
    return validationObject;
  }
  else 
    return validationCode.SUCCESS;
}


// -------------------------------------------------------------------------------------
// Page validation functions
// -------------------------------------------------------------------------------------

// Initializes the validation array by setting the contents of all message objects to empty strings
// and hiding them if they are visible
function atgValidation_initValidationObjects() {
  
  // Reinitialize the success variable
  isSuccess = true;
  
  for (var i = 0; i < currentValidationObjectArray.length; i++) {
    var validationId = currentValidationObjectArray[i];
    
    var validationObject = document.getElementById(validationId);
    if (validationObject != null) {
      validationObject.innerHTML = "";
      validationObject.style.display="none";
    }
  }  
  
  // After cleaning up the array, initialize it to an empty array
  currentValidationObjectArray = new Array();
  
  // Set the default Substitution Token
  if (validationCode.DEFAULT_SUBSTITUTION_TOKEN != null)
    constants.defaultSubstitutionToken = validationCode.DEFAULT_SUBSTITUTION_TOKEN;
}


// -------------------------------------------------------------------------------------
// Error reporting functions
// -------------------------------------------------------------------------------------

// Tests the validationResult and reports any errors if necessary
function atgValidation_testValidationResult(fieldId, fieldName, validationResult) {
  if (validationResult != validationCode.SUCCESS) {
    atgValidation_showError(fieldId, fieldName, validationResult);
    isSuccess = false;
  }
}

// Displays an error message above the label of the respective field
function atgValidation_showError(fieldId, fieldName, errorMessage) {
  var fieldMessageObject = document.getElementById(fieldId + "_message");
  var generalMessageObject = document.getElementById(constants.generalErrorMessageId);

  var separatorType = constants.inlineMessageSeparator;
  if ((fieldMessageObject == null) && (generalMessageObject == null))
      separatorType = constants.alertMessageSeparator;
  
  var generalErrorMessage = validationCode.MESSAGE_SPECIFIC + " <b>" + fieldName + "</b>";
  var fieldErrorMessage = validationResult;
  var combinedErrorMessage = generalErrorMessage + separatorType + fieldErrorMessage;
    
  /*
   Error decision logic:
   Ideally, the field name will be written to the general message object, while the 
   error message will be written to the field message object. If the general message object is absent,
   only the error message will be written to the field message object. If the error message object is
   absent, both messages will be written to the general message object. If both objects are absent, 
   the combined message is displayed as an alert.
  */
  if ((fieldMessageObject != null) && (generalMessageObject != null)) {
    atgValidation_setGeneralErrorMessage(generalMessageObject);
    atgValidation_appendErrorMessage(generalMessageObject, generalErrorMessage, separatorType);
    atgValidation_appendErrorMessage(fieldMessageObject, fieldErrorMessage, separatorType);
    currentValidationObjectArray.push(generalMessageObject.id);
    currentValidationObjectArray.push(fieldMessageObject.id);
  }
  else if (generalMessageObject != null) {
    atgValidation_setGeneralErrorMessage(generalMessageObject);
    atgValidation_appendErrorMessage(generalMessageObject, combinedErrorMessage, separatorType);
    currentValidationObjectArray.push(generalMessageObject.id);
  }
  else if (fieldMessageObject != null) {
    atgValidation_appendErrorMessage(fieldMessageObject, fieldErrorMessage, separatorType);
    currentValidationObjectArray.push(fieldMessageObject.id);
  }
  else {
    alert(combinedErrorMessage);
  }
}

// Appends the error message to the DOM object and makes that object visible if hidden
function atgValidation_appendErrorMessage(domObject, errorMessage, separatorType) {
  var messageContents = "";
  if (domObject.innerHTML != "") {
    messageContents = domObject.innerHTML;
    
    var testLength = validationCode.MESSAGE_GENERAL + constants.generalMessageSeparator;
    if (messageContents.length != testLength.length-1) {
      messageContents += separatorType;
    }
  }

  domObject.innerHTML = messageContents + errorMessage;
  if (domObject.style.display == "none")
    domObject.style.display = "block";
}

// Sets the general message area with the validationCode.MESSAGE_GENERAL string if it is empty
function atgValidation_setGeneralErrorMessage(domObject) {
  if (domObject.innerHTML == "") {
    domObject.innerHTML = validationCode.MESSAGE_GENERAL + constants.generalMessageSeparator;
  }
}

// -------------------------------------------------------------------------------------
// General functions
// -------------------------------------------------------------------------------------

// Escapes single and double quotes to allow the string concatenation to successfully validate without
// abnormally terminating the validation string
function atgValidation_escapeQuotes(testValue) {
  var regExp = /\'/g;
  testValue = testValue.replace(regExp, "\\\'");

  regExp = /\"/g;
  testValue = testValue.replace(regExp, "\\\"");

  regExp = /\r/g;
  testValue = testValue.replace(regExp, "\\r");
  
  regExp = /\n/g;
  testValue = testValue.replace(regExp, "\\n");

  return testValue;
}

// Substitutes an array of tokens within a String to return the new String
function atgCommon_substituteTokenArray(originalString, substitutionStringArray, substitutionTokenArray) {
  var newString = originalString;
  
  // Loop through the array and update the String accordingly
  for (var i=0; i< substitutionTokenArray.length; i++) {
    newString = atgCommon_substituteToken(newString, substitutionStringArray[i], substitutionTokenArray[i]);
  }
  
  return newString;
}

// Substitutes a token within a String to return the new String
function atgCommon_substituteToken(originalString, substitutionString, substitutionToken) {
  var newString = null;
  var regExp = eval("/"+substitutionToken+"/gi");
  newString = originalString.replace(regExp, substitutionString);
  return newString;
}

// Returns the DOM object for an element specified by its id
function atgCommon_findElementById(elementId) {
  return document.getElementById(elementId); 
}
