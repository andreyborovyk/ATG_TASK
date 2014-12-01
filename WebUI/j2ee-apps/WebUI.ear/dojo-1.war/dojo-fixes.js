dojo.require("dojo.cookie");
dojo.cookie.isSupported = function(){
  //      summary:
         //              Use to determine if the current browser supports cookies or not.
         //
         //              Returns true if user allows cookies.
         //              Returns false if user doesn't allow cookies.

         if(typeof navigator.cookieEnabled != "boolean"){
           this("__djCookieTest__", "CookiesAllowed", { expires: 90 });
           var cookieVal = this("__djCookieTest__");
           navigator.cookieEnabled = (cookieVal == "CookiesAllowed");
           if(navigator.cookieEnabled){
             this("__djCookieTest__", "", 0);
           }
         }
         return navigator.cookieEnabled;
};

dojo.Deferred.prototype._check = function(){
  if(this.fired != -1){
    if(!this.silentlyCancelled){
      // keep dojo's obnoxious and unhelpful alert from popping up all over the place while,
      // at the same time, providing more practical info to actually help debug the problem
      console.debug("Attention required: Deferred object already called", this);
    }
    this.silentlyCancelled = false;
    return;
  }
};

dojo.html = function () {};
dojo.html.getParentByType = function(/* HTMLElement */node, /* string */type) {
        //  summary
        //  Returns the first ancestor of node with tagName type.
        var _document = dojo.doc;
        var parent = dojo.byId(node);
        type = type.toLowerCase();
        while((parent)&&(parent.nodeName.toLowerCase()!=type)){
                if(parent==(_document["body"]||_document["documentElement"])){
                        return null;
                }
                parent = parent.parentNode;
        }
        return parent;  //  HTMLElement
};
dojo.html.getEventTarget = function(/* DOMEvent */evt){
        //  summary
        //  Returns the target of an event
        if(!evt) { evt = dojo.global().event || {} };
        var t = (evt.srcElement ? evt.srcElement : (evt.target ? evt.target : null));
        while((t)&&(t.nodeType!=1)){ t = t.parentNode; }
        return t;  //  HTMLElement
};

dojo.dom = function () {};
dojo.dom.getAncestors = function(/*Node*/node, /*function?*/filterFunction, /*boolean?*/returnFirstHit){
        //  summary:
        //    returns all ancestors matching optional filterFunction; will return
        //    only the first if returnFirstHit
        var ancestors = [];
        var isFunction = (filterFunction && (filterFunction instanceof Function || typeof filterFunction == "function"));
        while(node){
                if(!isFunction || filterFunction(node)){
                        ancestors.push(node);
                }
                if(returnFirstHit && ancestors.length > 0){
                        return ancestors[0];   //  Node
                }

                node = node.parentNode;
        }
        if(returnFirstHit){ return null; }
        return ancestors;  //  array
};
dojo.dom.textContent = function(/*Node*/node, /*string*/text){
        //  summary:
        //    implementation of the DOM Level 3 attribute; scan node for text
        if (arguments.length>1) {
                var _document = dojo.doc();
                dojo.dom.replaceChildren(node, _document.createTextNode(text));
                return text;  //  string
        } else {
                if(node.textContent != undefined){ //FF 1.5
                        return node.textContent;  //  string
                }
                var _result = "";
                if (node == null) { return _result; }
                for (var i = 0; i < node.childNodes.length; i++) {
                        switch (node.childNodes[i].nodeType) {
                                case 1: // ELEMENT_NODE
                                case 5: // ENTITY_REFERENCE_NODE
                                        _result += dojo.dom.textContent(node.childNodes[i]);
                                        break;
                                case 3: // TEXT_NODE
                                case 2: // ATTRIBUTE_NODE
                                case 4: // CDATA_SECTION_NODE
                                        _result += node.childNodes[i].nodeValue;
                                        break;
                                default:
                                        break;
                        }
                }
                return _result;  //  string
        }
};
dojo.dom.replaceChildren = function(/*Element*/node, /*Node*/newChild){
        //  summary:
        //    Removes all children of node and appends newChild. All the existing
        //    children will be destroyed.
        // FIXME: what if newChild is an array-like object?
        var nodes = [];
        if(dojo.render.html.ie){
                for(var i=0;i<node.childNodes.length;i++){
                        nodes.push(node.childNodes[i]);
                }
        }
        dojo.dom.removeChildren(node);
        node.appendChild(newChild);
        for(var i=0;i<nodes.length;i++){
                dojo.dom.destroyNode(nodes[i]);
        }
};

dojo.dom.removeChildren = function(/*Element*/node){
        //  summary:
        //    removes all children from node and returns the count of children removed.
        //    The children nodes are not destroyed. Be sure to call destroyNode on them
        //    after they are not used anymore.
        var count = node.childNodes.length;
        while(node.hasChildNodes()){ dojo.dom.removeNode(node.firstChild); }
        return count; // int
};

dojo.dom.removeNode = function(/*Node*/node){
        // summary:
        //    if node has a parent, removes node from parent and returns a
        //    reference to the removed child.
        //    To prevent IE memory leak, call destroyNode on the returned node when
        //    it is no longer needed.
        //  node:
        //    the node to remove from its parent.

        if(node && node.parentNode){
                // return a ref to the removed child
                return node.parentNode.removeChild(node); //Node
        }
};

/*
dojo.require("dijit.layout.ContentPane");
dojo.extend(dijit.layout.ContentPane, {
        destroy: function(){
                console.log("destroying ContentPane " + this.id);
                // if we have multiple controllers destroying us, bail after the first
                if(this._beingDestroyed){
                        return;
                }
                // make sure we call onUnload
                this._onUnloadHandler();
                this._beingDestroyed = true;
                this.destroyDescendants(); // need to destroy the child widgets, too
                this.inherited("destroy",arguments);
        }

});
*/


dojo.require("dijit.dijit");


attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap), {title: "titleBar"}),


dojo.extend(dijit.WidgetSet, {
        add: function(/*Widget*/ widget){
                if(this._hash[widget.id]){
                        //throw new Error("Tried to register widget with id==" + widget.id + " but that id is already registered");
                        console.log("Registering widget with id==" + widget.id + " but that id is already registered");
                        //this._hash[widget.id].destroy();
                }
                this._hash[widget.id]=widget;
        }
});

dojo.require("dijit.Menu");
dojo.extend(dijit.Menu, {
        _openMyself: function(/*Event*/ e){
                // summary:
                //    Internal function for opening myself when the user
                //    does a right-click or something similar

                dojo.stopEvent(e);

                // Get coordinates.
                // if we are opening the menu with the mouse or on safari open
                // the menu at the mouse cursor
                // (Safari does not have a keyboard command to open the context menu
                // and we don't currently have a reliable way to determine
                // _contextMenuWithMouse on Safari)
                var x,y;
                if(dojo.isSafari || this._contextMenuWithMouse){
                        x=e.pageX;
                        y=e.pageY;
                }
                else if (dojo.isIE) {
                        // otherwise open near e.target
                        var coords = dojo.coords(e.srcElement, true);
                        x = coords.x + 10;
                        y = coords.y + 10;
                }
                else{
                        // otherwise open near e.target
                        var coords = dojo.coords(e.target, true);
                        x = coords.x + 10;
                        y = coords.y + 10;
                }

                var self=this;
                var savedFocus = dijit.getFocus(this);
                function closeAndRestoreFocus(){
                        // user has clicked on a menu or popup
                        dijit.focus(savedFocus);
                        dijit.popup.close(self);
                }
                dijit.popup.open({
                        popup: this,
                        x: x,
                        y: y,
                        onExecute: closeAndRestoreFocus,
                        onCancel: closeAndRestoreFocus,
                        orient: this.isLeftToRight() ? 'L' : 'R'
                });
                this.focus();

                this._onBlur = function(){
                        // Usually the parent closes the child widget but if this is a context
                        // menu then there is no parent
                        dijit.popup.close(this);
                        // don't try to restore focus; user has clicked another part of the screen
                        // and set focus there
                }
        }
});

dojo.require("dojo.string");
dojo.string.endsWithAny = function(/*string*/str /* , ... */){
// summary:
//  Returns true if 'str' ends with any of the arguments[2 -> n]

        for(var i = 1; i < arguments.length; i++) {
                if(dojo.string.endsWith(str, arguments[i])) {
                        return true; // boolean
                }
        }
        return false; // boolean
};

// to save having to replace a zillion instances of this
dojo.debug = console.debug;

dojo.require("dijit.form.ValidationTextBox");
dojo.extend(dijit.form.ValidationTextBox, {
        postCreate: function(){
          this.inherited("postCreate", arguments);
                this.validate(false);
        }
});
/*
  ATG bug Fix: 160180
  Remove the -moz-inline-stack display property from the extended template,
  as it causes a disappearing caret when this UrlTextBox is combined with its parent, the LinkDialog.
*/
dojo.require("dijit._editor.plugins.LinkDialog");
dojo.extend(dijit._editor.plugins.UrlTextBox,  {
  templateString:"<table  class=\"dijit dijitReset dijitInlineTable dijitUrlTextBox\"  cellspacing=\"0\" cellpadding=\"0\"\r\n\tid=\"widget_${id}\" name=\"${name}\"\r\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\r\n\t><tr class=\"dijitReset\"\r\n\t\t><td class=\"dijitReset dijitInputField\" ><input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete=\"off\"\r\n\t\t\ttype='${type}' name='${name}'\r\n\t\t/></td\r\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\r\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div><div class='dijitValidationIconText'>&Chi;</div\r\n\t\t></td\r\n\t></tr\r\n></table>\r\n"
});



/*
  Remove the Grid moz-user-select = false from the Grid
  As it prevents selection of text or input fields in FF2
*/

dojo.require("dojox.grid.VirtualGrid");
dojo.extend(dojox.VirtualGrid, {
  // sizing
  resize: function(){
    // summary:
    //    Update the grid's rendering dimensions and resize it

    // FIXME: If grid is not sized explicitly, sometimes bogus scrollbars
    // can appear in our container, which may require an extra call to 'resize'
    // to sort out.

    // if we have set up everything except the DOM, we cannot resize
    if(!this.domNode || !this.domNode.parentNode){
      return;
    }
    // useful measurement
    var padBorder = dojo._getPadBorderExtents(this.domNode);
    // grid height
    if(this.autoHeight){
      this.domNode.style.height = 'auto';
      this.viewsNode.style.height = '';
    }else if(this.flex > 0){
    }else if(this.domNode.clientHeight <= padBorder.h){
      if(this.domNode.parentNode == document.body){
        this.domNode.style.height = this.defaultHeight;
      }else{
        this.fitTo = "parent";
      }
    }
    if(this.fitTo == "parent"){
      var h = dojo._getContentBox(this.domNode.parentNode).h;
      dojo.marginBox(this.domNode, { h: Math.max(0, h) });
    }
    // header height
    var t = this.views.measureHeader();
    t=t-1;
    if (t<0)  t=0;
    this.headerNode.style.height = t + 'px';
    // content extent
    var l = 1, h = (this.autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
    if(this.autoWidth){
      // grid width set to total width
      this.domNode.style.width = this.views.arrange(l, 0, 0, h) + 'px';
    }else{
      // views fit to our clientWidth
      var w = this.domNode.clientWidth || (this.domNode.offsetWidth - padBorder.w);
      this.views.arrange(l, 0, w, h);
    }
    // virtual scroller height
    this.scroller.windowHeight = h;
    // default row height (FIXME: use running average(?), remove magic #)
    this.scroller.defaultRowHeight = this.rows.getDefaultHeightPx() + 1;
    this.postresize();
  },
  resizeHeight: function(){
    var t = this.views.measureHeader();
    t=t-1;
    if (t<0)  t=0;
    this.headerNode.style.height = t + 'px';
    // content extent
    var h = (this.autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
    //this.views.arrange(0, 0, 0, h);
    this.views.onEach('setSize', [0, h]);
    this.views.onEach('resizeHeight');
    this.scroller.windowHeight = h;
  },
        postrender: function(){
          this.postresize();
          this.focus.initFocusView();
          // make rows unselectable
          //dojo.setSelectable(this.domNode, false);
        },
        destroy: function(){
          // verify that DOM node exists before attempting to access
          if (this.domNode) {
            this.domNode.onReveal = null;
            this.domNode.onSizeChange = null;
          }
          if (this.edit) this.edit.destroy();
          if (this.views) this.views.destroyViews();
          if (this.inherited && dojo.isFunction(this.inherited)) this.inherited("destroy", arguments);
        }
});

/*
  Dijit.dialog does not run internal JS code
  Dojox.dialog is required for that
  Point dijit.dialog at dojox.dialog and issue a warning
*/

dojo.require('dojox.Dialog');
dojo.require('dijit.Dialog');
dojo.declare("dijit.Dialog",dojox.Dialog,{
  postMixInProperties: function(){
    console.warn("ATG : You are using dijit.dialog which does not run included javascript, \r\n      it has been automatically redirected to use dojox.dialog");
  }

});


dojo.extend(dojox.Dialog, {

  // Adding title tag to close icon

  closeHoverText: "",

  templateString:"<div class=\"dijitDialog\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dijitDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">\r\n\t<span dojoAttachPoint=\"titleNode\" class=\"dijitDialogTitle\">${title}</span>\r\n\t<span dojoAttachPoint=\"closeButtonNode\" class=\"dijitDialogCloseIcon\" title=\"${closeHoverText}\" dojoAttachEvent=\"onclick: hide\">\r\n\t\t<span dojoAttachPoint=\"closeText\" class=\"closeText\">x</span>\r\n\t</span>\r\n\t</div>\r\n\t\t<div dojoAttachPoint=\"containerNode\" class=\"dijitDialogPaneContent\"></div>\r\n\t<span dojoAttachPoint=\"tabEnd\" dojoAttachEvent=\"onfocus:_cycleFocus\" tabindex=\"0\"></span>\r\n</div>\r\n",

  // Removed Title tag from dialog title bar.
  attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap))

});

// Dojo Trac #1678
// Dojo Trac #1680


dojo.require("dojox.i18n.number");

dojox.i18n.number.format = {};

/**
* Method to Format and validate a given number
* smallaiy: This method is modified to correct the fractional numbers.
*  if the fractional number is from 00 - 09, then the only one digit is added to the
*  format number. This will ensure number of places required by the flags.places.
*
* @param Number value
* The number to be formatted and validated.
* @param Object flags
*   flags.places number of decimal places to show, default is 0 (cannot be Infinity)
*   flags.round true to round the number, false to truncate
* @param String locale
* The locale used to determine the number format.
* @return String
*   the formatted number of type String if successful
*   or null if an unsupported locale value was provided
**/
dojox.i18n.number.format = function(value, flags /*optional*/, locale /*optional*/){
  flags = (typeof flags == "object") ? flags : {};

  var formatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.number.FORMAT_TABLE, locale);
  if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
  if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
  if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
  if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}
  if (typeof flags.round == "undefined") {flags.round = true;}
  if (typeof flags.signed == "undefined") {flags.signed = true;}

  var output = (flags.signed && (value < 0)) ? "-" : "";
  value = Math.abs(value);
  var whole = String((((flags.places > 0) || !flags.round) ? Math.floor : Math.round)(value));

  // Splits str into substrings of size count, starting from right to left.  Is there a more clever way to do this in JS?
  //This is fixed by Dojo Trac #1680
  function splitSubstrings(str, count){
    var subs = [];
    while (str.length >= count) {
      var s = str.substr(str.length - count, count);
      subs.push(s);
      str = str.substr(0, str.length - count);
    }
    if (str.length > 0) { subs.push(str); }
    return subs.reverse();
  }

  if (flags.groupSize2 && (whole.length > flags.groupSize)){
    var groups = splitSubstrings(whole.substr(0, whole.length - flags.groupSize), flags.groupSize2);
    groups.push(whole.substr(-flags.groupSize));
    output = output + groups.join(flags.separator);
  }else if (flags.groupSize){
    output = output + splitSubstrings(whole, flags.groupSize).join(flags.separator);
  }else{
    output = output + whole;
  }

//TODO: what if flags.places is Infinity?
  if (flags.places > 0){
  //Q: Is it safe to convert to a string and split on ".", or might that be locale dependent?  Use Math for now.
    var fract = value - Math.floor(value);
    fract = (flags.round ? Math.round : Math.floor)(fract * Math.pow(10, flags.places));
    //smallaiy: This change is added to format the fractional numbers properly.
    //if the fractional number is from 00 - 09, then the only one digit is added to the
    //format number. This will ensure number of places required by the flags.places.
    //This is fixed by Dojo Trac #1678
    fract = fract.toString();
    while (fract.length < flags.places) {
      fract = "0" + fract;
    }
    output = output + flags.decimal + fract;
  }

//TODO: exp

  return output;
};

/*

ATG Specific Tooltip Styling JS changes

*/


dojo.require("dijit.Tooltip");
dojo.extend(dijit._MasterTooltip, {

  show: function(/*String*/ innerHTML, /*DomNode*/ aroundNode){
    // summary:
    //  Display tooltip w/specified contents to right specified node
    //  (To left if there's no space on the right, or if LTR==right)

    if(this.aroundNode && this.aroundNode === aroundNode){
      return;
    }

    if(this.fadeOut.status() == "playing"){
      // previous tooltip is being hidden; wait until the hide completes then show new one
      this._onDeck=arguments;
      return;
    }
    this.containerNode.innerHTML=innerHTML;

    // Firefox bug. when innerHTML changes to be shorter than previous
    // one, the node size will not be updated until it moves.
    this.domNode.style.top = (this.domNode.offsetTop + 1) + "px";

    // position the element and change CSS according to position
    var align = this.isLeftToRight() ? {'TR': 'TL', 'TL': 'TR', 'BL': 'BR', 'BR': 'BL'} : {'TL': 'TR', 'TR': 'TL'};
    var pos = dijit.placeOnScreenAroundElement(this.domNode, aroundNode, align);
    console.dir(pos);

    this.domNode.className="dijitTooltip dijitTooltip" + pos.corner;

    if(pos.corner.match('T')){
      console.debug("tooltip created in Top");
      this.domNode.style.top = (parseInt(this.domNode.style.top) - 10)+"px";
    }else if(pos.corner.match('B')){
      console.debug("tooltip created in Bottom");
      this.domNode.style.top = (parseInt(this.domNode.style.top) + 10)+"px";
    }


    // show it
    dojo.style(this.domNode, "opacity", 0);
    this.fadeIn.play();
    this.isShowingNow = true;
    this.aroundNode = aroundNode;
  }

}),


/*
  Make Tooltips Persistent if you rollover the tip
  Allows for the addition of content with links within the tooltip
*/

dojo.require("dijit.Tooltip");
dojo.extend(dijit.Tooltip, {
  postMixInProperties: function(){
    if(!dijit._masterTT){
      dijit._masterTT = new dijit._MasterTooltip();
    }
    dijit._masterTT.connect(dijit._masterTT.domNode,'onmouseover',this.ttPersist);
    dijit._masterTT.connect(dijit._masterTT.domNode,'onmouseout',this.ttFade);
    //dijit._masterTT.connect(window,'onscroll',this.ttFade);
    //console.debug(dijit._masterTT);

  },

  ttPersist: function (evt){
    // console.log("persist");
    this.fadeOut.stop();
    this.fadeIn.play();

  },

  ttFade: function (evt){
    // console.log("fade");
    this.isShowingNow = false;
    this.aroundNode = null;
    this.fadeOut.play();
  }

});



/*
ATG Bug Fix: 151813
Prevents Initilized Dojo.Dialogs from throwing errors in IE's
http://trac.dojotoolkit.org/changeset/11727
*/
dojo.require("dojox.Dialog");
dojo.extend(dojox.Dialog, {
  uninitialize: function(){
    if(this._fadeIn && this._fadeIn.status() == "playing"){
      this._fadeIn.stop();
    }
    if(this._fadeOut && this._fadeOut.status() == "playing"){
      this._fadeOut.stop();
    }
    if(this._underlay){
      this._underlay.destroy();
    }
  }
});

/*
ATG Bug Fix: 150987, 152684
Workspace freezes in IE7 on atgShowLoadingIcon when getting focus on a DIV with display:none;
*/
dojo.require("dojox.Dialog");
dojo.extend(dojox.Dialog, {
  show: function(){
    // summary: display the dialog

    // first time we show the dialog, there's some initialization stuff to do
    if(!this._alreadyInitialized){
      this._setup();
      this._alreadyInitialized=true;
    }

    if(this._fadeOut.status() == "playing"){
      this._fadeOut.stop();
    }

    this._modalconnects.push(dojo.connect(window, "onscroll", this, "layout"));
    this._modalconnects.push(dojo.connect(document.documentElement, "onkeypress", this, "_onKey"));

    // IE doesn't bubble onblur events - use ondeactivate instead
    var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
    this._modalconnects.push(dojo.connect(this.containerNode, ev, this, "_findLastFocus"));

    dojo.style(this.domNode, "opacity", 0);
    this.domNode.style.display="block";
    this.open = true;
    this._loadCheck(); // lazy load trigger

    this._position();

    if (djConfig.usesApplets) {
      // add class to body to hide the applet
      dojo.addClass(document.body, "appletKiller");
      dojo.query("iframe").forEach(

        function(eachIframe) {
          if(eachIframe.contentDocument){
            // Firefox, Opera
            doc = eachIframe.contentDocument;
          }else if(eachIframe.contentWindow){
            // Internet Explorer
            doc = eachIframe.contentWindow.document;
          }else if(eachIframe.document){
            // Others?
            doc = eachIframe.document;
          }
          if (doc.body) {
            dojo.addClass(doc.body, "appletKiller");
          }
        }
      );
    }

    this._fadeIn.play();

    try {
      this._savedFocus = dijit.getFocus(this);
    }
    catch (e) {
      // On IE7 this is a bogus error caused by creating ranges
      // on text in a DIV that is display:none - apparently getFocus does
      // somewhere internally - when this happens just reset the property
      // gracefully without a major page blow-up - the dialog will just
      // behave as though there was no prior focus - we can live with this
      this._savedFocus = null;
      delete this._savedFocus;
    }

    // set timeout to allow the browser to render dialog
    setTimeout(dojo.hitch(this, function(){
      dijit.focus(this.titleBar);
    }), 50);
  },

   hide: function(){
     // summary: Hide the dialog

     // if we haven't been initialized yet then we aren't showing and we can just return
     if(!this._alreadyInitialized){
       return;
     }

     if(this._fadeIn.status() == "playing"){
       this._fadeIn.stop();
     }
     this._fadeOut.play();

     if (djConfig.usesApplets) {
       // remove the applet hiding class from the body tag
       dojo.removeClass(document.body, "appletKiller");
       dojo.query("iframe").forEach(
        function(eachIframe) {
          if(eachIframe.contentDocument){
            // Firefox, Opera
            doc = eachIframe.contentDocument;
          }else if(eachIframe.contentWindow){
            // Internet Explorer
            doc = eachIframe.contentWindow.document;
          }else if(eachIframe.document){
            // Others?
            doc = eachIframe.document;
          }
          if (doc.body) {
            dojo.removeClass(doc.body, "appletKiller");
          }
        }
      );
    }

     if (this._scrollConnected){
       this._scrollConnected = false;
     }
     dojo.forEach(this._modalconnects, dojo.disconnect);
     this._modalconnects = [];

     this.connect(this._fadeOut,"onEnd",dojo.hitch(this,function(){
       dijit.focus(this._savedFocus);
     }));
                this._savedFocus = null;
                delete this._savedFocus;
     this.open = false;
   }


});

//Update to include atg.formManager to prevent pseudo memory leak
dojo.require("dijit.layout.ContentPane");
dojo.extend(dijit.layout.ContentPane, {
  // optionally prevent TabContainer from implicitly making the content pane a tab
  excludeFromTabs: false,
  _setContent: function(cont){
    this.destroyDescendants();

    // FORM Tag switcherooo
    if(dojo.isIE){
      var replacePattern = new RegExp("(<form([^>])*>)", "gmi");
      var matchPattern = new RegExp("(<form)([^>])*action=([^>])*>", "gmi");
      cont = cont.replace(replacePattern, function($0)
      {
        if ($0 != "")
          if ($0.match(matchPattern))
            return $0.replace(/<FORM/i, "<DIV")
          else
            return $0.replace(/<FORM/i, "<DIV action=\"#\"")
       }).replace(/<\/FORM>/gi, "</DIV>");
    }

    try{
      var node = this.containerNode || this.domNode;
      while(node.firstChild){
        dojo._destroyElement(node.firstChild);
      }
      if(typeof cont == "string"){
        // dijit.ContentPane does only minimal fixes,
        // No pathAdjustments, script retrieval, style clean etc
        // some of these should be available in the dojox.layout.ContentPane
        if(this.extractContent){
          match = cont.match(/<body[^>]*>\s*([\s\S]+)\s*<\/body>/im);
          if(match){ cont = match[1]; }
        }
        node.innerHTML = cont;
      }else{
        // domNode or NodeList
        if(cont.nodeType){ // domNode (htmlNode 1 or textNode 3)
          node.appendChild(cont);
        }else{// nodelist or array such as dojo.Nodelist
          dojo.forEach(cont, function(n){
            node.appendChild(n.cloneNode(true));
          });
        }
      }

      if(dojo.isIE){
        var divTags = node.getElementsByTagName('DIV');
        for(var k = 0; k<divTags.length; k++){
          var thisDiv = divTags[k];
          if(thisDiv.getAttribute('action')){
            newFormNode = atg.formManager.createForm();
            newFormNode.innerHTML = thisDiv.innerHTML;

            // copy over all attributes and styles from the oDIV to the recycled form node
           for (var attrName in  thisDiv.attributes)
              if (thisDiv.attributes[attrName] &&
                  thisDiv.attributes[attrName].nodeValue)
                newFormNode.setAttribute(attrName, thisDiv.attributes[attrName].nodeValue);
            for (var currentStyle in thisDiv.style){
              if (currentStyle != "font")
                  newFormNode.style[currentStyle] = thisDiv.style[currentStyle];
            }

            thisDiv.replaceNode(newFormNode);
            thisDiv.outerHTML = "";
            thisDiv.removeNode();
            k--;
          }
        }
      }

    }catch(e){
      // check if a domfault occurs when we are appending this.errorMessage
      // like for instance if domNode is a UL and we try append a DIV
      var errMess = this.onContentError(e);
      try{
        node.innerHTML = errMess;
      }catch(e){
        console.error('Fatal '+this.id+' could not change content due to '+e.message, e);
      }
    }
  }
});

dojo.require("dijit.layout.TabContainer");
dojo.extend(dijit.layout.TabContainer, {
  _setupChild: function(/* Widget */tab){
    if (!tab.excludeFromTabs) {
      dojo.addClass(tab.domNode, "dijitTabPane");
    }
    this.inherited("_setupChild",arguments);
    return tab; // Widget
  }
});

dojo.require('dojox.validate');
dojox.validate.isValidCreditCardNumber = function(/*String|Int*/value,/*String?*/ccType) {
  //Summary:
  //  checks if the # matches the pattern for that card or any card types if none is specified
  //  value == CC #, white spaces and dashes are ignored
  //  ccType is of the values in cardinfo -- if Omitted it it returns a | delimited string of matching card types, or false if no matches found

  //Value: Boolean

  if(typeof value!='string'){
    value = String(value);
  }
  value = value.replace(/[- ]/g,''); //ignore dashes and whitespaces
  /*   FIXME: not sure on all the abbreviations for credit cards,below is what each stands for atleast to my knowledge
    mc: Mastercard
    ec: Eurocard
    vi: Visa
    ax: American Express
    dc: Diners Club
    bl: Carte Blanch
    di: Discover
    jcb: JCB
    er: Enroute
   */
  var results=[];
  var cardinfo = {
    'mc':'5[1-5][0-9]{14}','ec':'5[1-5][0-9]{14}','vi':'4([0-9]{12}|[0-9]{15})',
    'ax':'3[47][0-9]{13}', 'dc':'3(0[0-5][0-9]{11}|[68][0-9]{12})',
    'bl':'3(0[0-5][0-9]{11}|[68][0-9]{12})','di':'6011[0-9]{12}',
    'jcb':'(3[0-9]{15}|(2131|1800)[0-9]{11})','er':'2(014|149)[0-9]{11}'
  };
  if(ccType && cardinfo[ccType.toLowerCase()]){
    return Boolean(value.match(cardinfo[ccType.toLowerCase()])); // boolean
  }else{
    for(var p in cardinfo){
      if(value.match('^'+cardinfo[p]+'$')!=null){
        results.push(p);
      }
    }
    return (results.length)?results.join('|'):false; // string | boolean
  }
};

dojo.require("dojox.grid.Grid");
dojo.require("dojox.grid._grid.lib");
dojo.mixin(dojox.grid, {
  initTextSizePoll: function(inInterval) {
    var f = document.createElement("div");
    with (f.style) {
      top = "0px";
      left = "0px";
      position = "absolute";
      visibility = "hidden";
    }
    f.innerHTML = "TheQuickBrownFoxJumpedOverTheLazyDog";
    document.body.appendChild(f);
    var fw = f.offsetWidth;
    var job = function() {
      if (f.offsetWidth != fw) {
        fw = f.offsetWidth;
        try {
          dojox.grid.textSizeChanged();
        }
        catch (e) {
          // ignore this exception as it may be something
          // that happens when the page is unloaded.
          // See bug 158923 for details. As an alternative
          // we can simply log this.
        }
      }
    }
    window.setInterval(job, inInterval||200);
    dojox.grid.initTextSizePoll = dojox.grid.nop;
  }
});

// Modify hitch to debug error instead of throwing error that may not be caught
//
dojo.hitch = function(/*Object*/scope, /*Function|String*/method /*,...*/){
  // summary:
  //    Returns a function that will only ever execute in the a given scope.
  //    This allows for easy use of object member functions
  //    in callbacks and other places in which the "this" keyword may
  //    otherwise not reference the expected scope.
  //    Any number of default positional arguments may be passed as parameters
  //    beyond "method".
  //    Each of these values will be used to "placehold" (similar to curry)
  //    for the hitched function.
  // scope:
  //    The scope to use when method executes. If method is a string,
  //    scope is also the object containing method.
  // method:
  //    A function to be hitched to scope, or the name of the method in
  //    scope to be hitched.
  // example:
  //  | dojo.hitch(foo, "bar")();
  //    runs foo.bar() in the scope of foo
  // example:
  //  | dojo.hitch(foo, myFunction);
  //    returns a function that runs myFunction in the scope of foo
  if(arguments.length > 2){
    return dojo._hitchArgs.apply(dojo, arguments); // Function
  }
  if(!method){
    method = scope;
    scope = null;
  }
  if(dojo.isString(method)){
    scope = scope || dojo.global;
    if(!scope[method]){ console.debug('dojo.hitch: scope["', method, '"] is null (scope="', scope, '")' )}
    return function(){ return scope[method].apply(scope, arguments || []); }; // Function
  }
  return !scope ? method : function(){ return method.apply(scope, arguments || []); }; // Function
}
