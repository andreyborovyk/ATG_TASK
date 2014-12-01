  var fluoroEdit = function() {
    var namespace = {
      setFromIframe: function(replaceId, iframeId) {

        var domReplace = document.getElementById(replaceId);
        if (!domReplace) {
          alert("Failed to find domReplace with id " + replaceId);
          return;
        }
        var domIframe = document.getElementById(iframeId);
        if (!domIframe) {
          alert("Failed to find domIframe with id " + iframeId); 
        }
	// okay, now we need to grab the inner HTML from the
        // iframe...
        var innerHtml;
	if (frames[iframeId].document.body != null) {
	  innerHtml = frames[iframeId].document.body.innerHTML;
        }
	else {
	  innerHtml = frames[iframeId].document.innerHTML;
	}

        frames[iframeId].onload = null;
	// this.debug("frame = " + frames[iframeId].document.body.innerHTML);
	frames[iframeId].document.body.innerHTML = "";


	// alert("innerHtml: " + innerHtml);
	domReplace.innerHTML = innerHtml;

        // ugly, but wee seem to lose our span line-height, otherwise
        domReplace.style.lineHeight = "150%";

	// re-add the scripts, so they execute, and hope possible
        // out-of-order execution doesn't matter.
	var scripts = domReplace.getElementsByTagName("script")
	var len = scripts.length
	for (var i=0; i < len;i++){
	  var newScript = document.createElement('script');
	  newScript.type = "text/javascript";
//	  alert("executing " + scripts[i].text);
	  newScript.text = scripts[i].text;
	  domReplace.appendChild(newScript);
        }
      },
      setFromContent: function(replaceId, iframeId, url) {
        var domReplace = document.getElementById(replaceId);
        if (!domReplace) {
          alert("Failed to find domReplace with id " + replaceId);
          return;
        }
        var domIframe = document.getElementById(iframeId);
        if (!domIframe) {
          alert("Failed to find domIframe with id " + iframeId); 
        }
	frames[iframeId].location.href = url;
        domIframe.onload = function() {
          namespace.setFromIframe(replaceId, iframeId);
        }
      },

      hadError: false,
      delayedHasError: false,
      propertySpanIds: new Array(),
  
      blurEditProp: null,
      blurInputField: null,

      currentEditProp: null,
      editableProperties: new Array(),
      containingSpanIdToEditableProperty: {},

      onBlurRememberActive: function(inputField) {
        this.blurEditProp = this.currentEditProp;
        this.blurInputField = inputField;
      },

      focusFirstFormElement: function(oForm) {
        for (var i = 0; i < oForm.elements.length; i++) {
          if (oForm.elements[i].type != "hidden") {
            if (oForm.elements[i].disabled == false) {
              oForm.elements[i].focus();
              return true;
            }
          }
        }
	return false;
      },


      toggleVisibility: function() {
        for (var i = 0; i < arguments.length; i++) {
  	 var element = document.getElementById(arguments[i]);
           if (element) {     
    	   if (element.style.display == "none") {
               element.style.display = "inline";
             }
             else {
               element.style.display = "none";
             }
          }
        }
      },
  
      setDisplay: function(elementId, displayString) {
        var element = document.getElementById(elementId);
        if (element) {
          element.style.display = displayString;
        }
      },
  
      addEditableProperty: function(displayId, editId, formId, containingSpanId) {
        var prop = 
          new EditableProperty(displayId, editId, formId, containingSpanId);
        // alert("new prop = " + prop);
        this.editableProperties.push(prop);
        this.containingSpanIdToEditableProperty[containingSpanId] = prop;
      },
  
      editProperty: function(containingSpanId) {
        var prop = this.containingSpanIdToEditableProperty[containingSpanId];
        if (this.currentEditProp) {
          if (!this.currentEditProp.endEdit(true)) {
            return false;
          }
        }
        if (prop) {
          prop.edit();
          this.currentEditProp = prop;
          return true;
        }
        else {
          alert("Property not found: " + containingSpanId);
          return true;
        }
      },
  
      toggleVisibility: function() {
        for (var i = 0; i < arguments.length; i++) {
  	 var element = document.getElementById(arguments[i]);
           if (element) {     
    	   if (element.style.display == "none") {
               element.style.display = "inline";
             }
             else {
               element.style.display = "none";
             }
          }
        }
      },
  
      endCurrentEdit: function(shouldConfirm) {
	if (this.currentEditProp) {
	  return this.currentEditProp.endEdit();
        }
	return true;
      },

      debug: function (message) {
        if (window.console) {
         window.console.log(message);
       }
      },
  
  
      updatePropertySpansFromIframe: function(iframeId, hasError) {
        // alert("endEdit: " + endEdit + ", " + this.currentDisplayId);
	this.blurInputField = null;
	this.blurEditProp = null;
        var iFrame = frames[iframeId];
        // this.debug("iFrame = " + iFrame);
        for (var i = 0; i < this.propertySpanIds.length; i++) {
          var curSpanId = this.propertySpanIds[i];
          var iFrameSpan = iFrame.document.getElementById(curSpanId);
          var mainSpan = document.getElementById(curSpanId);
	  var iFrameHtml = null;
          if (iFrameSpan && mainSpan) {
            iFrameHtml = iFrameSpan.innerHTML;
  	  if (iFrameHtml != mainSpan.innerHTML) {
              mainSpan.innerHTML = iFrameHtml;
            }
          }
        }
        if (!hasError) {
          this.endCurrentEdit(false);
        }
        else {
	  this.currentEditProp.edit();
          this.hadError=true;
        }
      },

      isFormModified: function(oForm) {
	var el, opt, hasDefault, i = 0, j;
	while (el = oForm.elements[i++]) {
	  switch (el.type) {
	    case 'text' :
            case 'textarea' :
            case 'hidden' :
              if (!/^\s*$/.test(el.value) && el.value != el.defaultValue) return true;
                break;
            case 'checkbox' :
            case 'radio' :
              if (el.checked != el.defaultChecked) return true;
                break;
            case 'select-one' :
            case 'select-multiple' :
              j = 0, hasDefault = false;
              while (opt = el.options[j++]) {
                if (opt.defaultSelected) 
                  hasDefault = true;
              }
              j = hasDefault ? 0 : 1;
              while (opt = el.options[j++]) {
                if (opt.selected != opt.defaultSelected) 
                  return true;
              }
              break;
	  }
	}
	return false;
      }
    };

    function EditableProperty(displayId, editId, formId, containingSpanId) {
      this.displayId = displayId;
      this.editId = editId;
      this.formId = formId;
      this.containingSpanId = containingSpanId;

      this.edit = function () {
        if (namespace.hadError) {
          namespace.hadError = false;
          frames["FluoroscopeEditSubmitFrame"].location.href="edititem.jhtml?invokeUpdate=true&editSpanId=" + escape(editSpanId);
          return;
        }
        if ((namespace.currentEditProp != null) &&
            (namespace.currentEditProp != this)) {
	  if (!namespace.endCurrentEdit(true)) {
	    return;
          }
        }
        namespace.setDisplay(this.displayId, "none");
        namespace.setDisplay(this.editId, "inline");
        var oSpan = document.getElementById(editId);
        namespace.focusFirstFormElement(this.getForm());
      };

      this.getForm = function() {
        return document.getElementById(this.formId);
      },
  
      this.endEdit = function(shouldConfirm) {
        // should we make this more specific?
        // end the current edit. Returns true if can end, false otherwise

        var oForm = this.getForm();
        if (shouldConfirm && namespace.isFormModified(oForm)) {
          if(!confirm("Abandon current changes?")) {
	    if (namespace.blurEditProp == this) {
	      namespace.blurInputField.focus();
            }
            return false;
          }
        }
        namespace.setDisplay(this.displayId, "inline");
        namespace.setDisplay(this.editId, "none");
    	oForm.reset();
	if (namespace.currentEditProp == this) {
	  namespace.currentEditProp = null;
        }
        return true;
      }
    }

    return namespace;
  }();
