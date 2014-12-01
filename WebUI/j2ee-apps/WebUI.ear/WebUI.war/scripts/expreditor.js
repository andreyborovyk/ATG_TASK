// -------------------------------------------------------------------------------------
// Javascript used in the expression editor
// @version $Id$$Change$
// -------------------------------------------------------------------------------------

/**
 * The current state of the expression editor
 **/
expreditorState = {
  locked: false
}

var expreditorLocked = false;

/**
 * Lock the expression editor
 **/
function lock(){
  //alert('lock()');
  expreditorState.locked = true;
}

/**
 * Unlock the expression editor
 **/
function unlock(){
  //alert('unlock()');
  expreditorState.locked = false;
}

/**
 * Check to see if the expression editor is locked
 **/
function isLocked(){
  //alert('isLocked: ' + expreditorState.locked);
  return expreditorState.locked;
}

/**
 * Updates a choice expression
 **/
function choiceExprUpdated(controlId, terminalIdentifier) {
  if(isLocked())
    return false;
  else
    lock();
  // Set the new choice 
  var terminalChoiceIndex = document.getElementById(controlId).value;
  var expressionSubmitForm = document.forms['expressionSubmitForm'];
  expressionSubmitForm.terminalChoiceIndex.value = terminalChoiceIndex;
  // Set the previous terminal identifier 
  expressionSubmitForm.terminalIdentifier.value = terminalIdentifier;
  // Submit the choice selection 
  expressionSubmitForm.submitChoose.click();
  return true;
}

/**
 * Function called whenever a literal is updated 
 **/
function literalExprUpdated(controlId, terminalIdentifier) {
  if(isLocked())
    return false;
  else
    lock();
  // Set the new literal value 
  var literalTextValue = document.getElementById(controlId).value;
  var expressionSubmitForm = document.forms['expressionSubmitForm'];
  expressionSubmitForm.literalTextValue.value = literalTextValue;
  // Set the previous terminal identifier 
  expressionSubmitForm.terminalIdentifier.value = terminalIdentifier;
  // Submit the choice selection 
  expressionSubmitForm.submitLiteral.click();
}

/**
 * Function called whenever a button is clicked 
 **/
function buttonExprClicked(controlId, buttonIdentifier) {
  if(isLocked())
    return false;
  else
    lock();
  // Notify server that the button was clicked 
  var expressionSubmitForm = document.forms['expressionSubmitForm'];
  // Set the previous terminal identifier 
  expressionSubmitForm.terminalIdentifier.value = buttonIdentifier;
  // Submit the choice selection 
  expressionSubmitForm.submitButton.click();
}
