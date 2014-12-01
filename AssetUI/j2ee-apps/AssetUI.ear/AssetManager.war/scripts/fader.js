/* NEW ITEM COLOR FADE FUNCTIONS */
// Original code from TJ McGee, media-hive 2005
// edited Jon Sykes media-hive April 11th 2005

// to find by className rather than unique ID would be cool
// so will use this function to do that, it'll generate an array
// containing references to all elements with a className
// it'll even recurse through all the frames/ iframes in a page

//########################################################//
// This is a specific function for this situation
// when getting a CSS set color some browsers return #XXXXXXX hex values
// some return rbg(XXX, XXX, XXX) as the values
// We use parseColor() to convert them both to just the basic 6 character string
// 

function parseColor(browserColor)
{
  // if it doesn't start with a # it's not a Hex, treat as an RGB
  // should maybe add some sort of error handling here, although chances are slim
  if (browserColor.substring(0, 1) == "r")
  {

    // get the  individual RGB values from the Firefox way of getting the browser values
    // which is to return a string with a value of rgb(xxx, xxx, xxx) always in the format
    // so we can just split the string up the same each time, thankfully
    var rrr = browserColor.substring(4, 7);
    var ggg = browserColor.substring(9, 12);
    var bbb = browserColor.substring(14, 17);
    
    // next three section basically do the R, G. B to Hex value conversion
    rrr=parseInt(rrr);
    ggg=parseInt(ggg);
    bbb=parseInt(bbb);
    
    rrrHex=rrr.toString(16).toUpperCase();
    gggHex=ggg.toString(16).toUpperCase();
    bbbHex=bbb.toString(16).toUpperCase();
    
    if (rrrHex.length == 1){ rrrHex='0'+rrrHex;}
    if (gggHex.length == 1){ gggHex='0'+gggHex;}
    if (bbbHex.length == 1){ bbbHex='0'+bbbHex;}
    
    // splice them together with the #
    rgbVal= rrrHex+gggHex+bbbHex;
    
    return rgbVal;
  
  // this could be tweaked, but for now if the background is transparent or not a valid hex
  // we'll assume it's going to end up white.
  }else if(browserColor.substring(0, 1) == "#"){
  
  
  //return "FFFFFF";
  
  return browserColor.substring(1, 7); 
   
  }else{
  // OK, it starts with a # and it has 7 characters, it must be a hex, take the # off the front
//alert(browserColor);
  return "FFFFFF";
  }

}

//#################################################################################//
// Fader class
// Fades for theStartColor to theEndColor in a theNumberSteps
// It fade theProperty (most often this will be background color or font color
// theElement should be a reference to the element that this behavior has been applied


function fader(theStartColor, theEndColor, theNumberSteps, theProperty, theElement) {

  //instantiate the variables in the object
  
	this.startColor = theStartColor;
	this.endColor = theEndColor;
	this.numberSteps = theNumberSteps;
	this.jsprementor = 0;
	this.property = theProperty;
  this.timer = new Timer(this);
  this.theElement = theElement;
  
  // Declare Object Methods	
	this.hexToDecimal = FaderHexToDecimal;
	this.decToHex = FaderDecToHex;
	this.getPartialColor = FaderGetPartialColor;
	this.getColorArray = FaderGetColorArray;
	this.changeColor = FaderChangeColor;
	this.highlightStyle = FaderHighlightStyle;
	this.highlightStyleClear = FaderHighlightStyleClear;
	
  // Set up the color array that controls the fade
	this.colorArray = this.getColorArray();

}

// converts a single r, g or b value from hex to decimal
function FaderHexToDecimal (value) {
	var colorValue = parseInt("0x" + value, 16);	
	return colorValue;
}

// converts a single r, g or b value from a decimal to a hex value
function FaderDecToHex (value){
	var value = Math.round(value);
	var colorValue = value.toString(16);
		
	if(colorValue.length<2){
		colorValue = "0" + colorValue;
	}
	return colorValue;
}



// Generates the partial color array (steps from startColor to end color)
function FaderGetPartialColor (startColor, redStep, numberSteps) {
	
	var thisArray = new Array();
	for(var i=0; i<this.numberSteps; i++){
		var currentColor =  startColor - redStep*i;
		currentColor = this.decToHex(currentColor);
		thisArray[i] = currentColor.toUpperCase();
	}
	return thisArray;
}

// Generates the partial color array (steps from startColor to end color)
function FaderGetColorArray (){
	
	var startColor = this.startColor;
	var endColor = this.endColor;
	var numberSteps = this.numberSteps;
	
	var startRed = this.hexToDecimal(startColor.substring(0,2));	
	var startGreen = this.hexToDecimal(startColor.substring(2,4));
	var startBlue = this.hexToDecimal(startColor.substring(4,6));
	
	var finishRed  = this.hexToDecimal(endColor.substring(0,2));
	var finishGreen  = this.hexToDecimal(endColor.substring(2,4));
	var finishBlue  = this.hexToDecimal(endColor.substring(4,6));
	
	var redRange = startRed - finishRed;
	var greenRange = startGreen - finishGreen;
	var blueRange = startBlue - finishBlue;
	
	var redStep = redRange/(numberSteps-1);
	var greenStep = greenRange/(numberSteps-1);
	var blueStep = blueRange/(numberSteps-1);
	
	var redArray = new Array(numberSteps);
	var greenArray = new Array(numberSteps);
	var blueArray = new Array(numberSteps);
	var colorArray = new Array(numberSteps);
	
	redArray = this.getPartialColor(startRed, redStep, numberSteps);
	greenArray = this.getPartialColor(startGreen, greenStep, numberSteps);
	blueArray = this.getPartialColor(startBlue, blueStep, numberSteps);
	
	for(var k=0; k<this.numberSteps; k++){
		currentColor = redArray[k] + "" + greenArray[k] + "" + blueArray[k];
		colorArray[k] = currentColor;
	}
	return colorArray;
}


// step to the next color in the array
function FaderChangeColor () {
    // work through the color array setting the property
    this.theElement.style[this.property] = "#" + this.colorArray[this.jsprementor];		
    this.jsprementor++;	 
}



// the color change trigger
function FaderHighlightStyle() {
	// wait for 2000 miliseconds before starting the fade
	this.timer.setTimeout("highlightStyleClear", 2000);
	this.changeColor();

}



function FaderHighlightStyleClear() {

	this.changeColor();
	if(this.jsprementor < this.colorArray.length)
	{
	  // fade interval is 100 miliseconds (so there will be 10 changes per second
	  // with 20 steps and a 2000 milisecond delay the whole fade will be done in
	  // 4 seconds from page load.
	  this.timer.setTimeout("highlightStyleClear", 100);
	}
}
