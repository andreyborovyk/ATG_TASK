function showIframe(elementID) {


if(elementID == 0){
	alert("You must choose an action from the menu");
	return;
}

	if(parent.document.iframeOpen){
	
		if(parent.document.currentID != elementID){
			parent.document.getElementById(parent.document.currentID).style.display = "none";
			parent.document.getElementById(elementID).style.display = "block";
			parent.document.iframeOpen = true;
		}else{
			parent.document.getElementById(elementID).style.display = "none";
			parent.document.iframeOpen = false;
		}
	}else{
		parent.document.getElementById(elementID).style.display = "block";
		parent.document.iframeOpen = true;

	}
		
	parent.document.currentID = elementID;

}




var child_suffix = "Child";

function toggleElement (element){
	var id = element.id;
	var childElement = document.getElementById (id + child_suffix);
	if(childElement != null){
		if (childElement.className != "closed" + child_suffix){
			childElement.className = "closed" + child_suffix;
			element.className = "closed";
			}
			else{
			element.className = "open";
			childElement.className ="open" + child_suffix;
			}
	}
}//toggleElement 



// Toggle Asset Browser size 

function maximize() {
	var assetBrowser = parent.document.getElementById('iFrame');
	var assetBrowserResize = document.getElementById('assetBrowserSize');
	var dropShadow = parent.document.getElementById('browserIframe');
	var dropShadowBottom = parent.document.getElementById('browserBottom');
	var browserContent = document.getElementsByTagName('body')[0];
	
	if (assetBrowser.offsetWidth < 400) {
		assetBrowser.style.width = "492px";
		dropShadow.setAttribute('id','browserIframeLarge');
		dropShadowBottom.setAttribute('id','browserBottomLarge');
		browserContent.setAttribute('id','assetBrowserLarge');
		assetBrowserResize.className="minimizeButton";
	} else {
		assetBrowser.style.width = "392px";
		parent.document.getElementById('browserIframeLarge').setAttribute('id','browserIframe');
		parent.document.getElementById('browserBottomLarge').setAttribute('id','browserBottom');
		browserContent.setAttribute('id','assetBrowser');
		assetBrowserResize.className="maximizeButton";
	}
}
		

// Help Box Toggle

function displayToggle(boxid, onclass, offclass){

	if(document.getElementById){
		document.getElementById(boxid).className =(document.getElementById(boxid).className == offclass)? onclass : offclass;

	}
	
	//var theBox = document.getElementById("box");
	//var someText = document.createTextNode(" this string");
	
	//var theOriginaHeight = theBox.style.height;
	//var theNewHeight = "10px"
	
	//theBox.style.height = theNewHeight;
	//theBox.style.height = theOriginaHeight;

}