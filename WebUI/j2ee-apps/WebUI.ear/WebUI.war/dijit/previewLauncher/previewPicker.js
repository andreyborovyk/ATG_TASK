dojo.require('dojox.color');
dojo.require("dijit.Dialog");
dojo.require("dojo.fx");


/*
  Preview Picker Widget v0.1
  
  Created by Sykes on 2007-11-13.
  Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/



dojo.provide("atg.widget.previewPicker");

dojo.declare("atg.widget.previewPicker", [dijit.Dialog], {
    

  // focusElement: String
  //  provide a focusable element or element id if you need to
  //  work around FF's tendency to send focus into outer space on hide
  focusElement: "",

  // bgColor: String
  //  color of viewport when displaying a dialog
  bgColor: "#000000",
  
  // bgOpacity: Number
  //  opacity (0~1) of viewport color (see bgColor attribute)
  bgOpacity: 0.8,

  duration: 1,

  // followScroll: Boolean
  //  if true, readjusts the dialog (and dialog background) when the user moves the scrollbar
  followScroll: true,

  // closeOnBackgroundClick: Boolean
  //  clicking anywhere on the background will close the dialog
  closeOnBackgroundClick: true,
  
  templateString: null,
  
  templatePath: "/WebUI/dijit/previewLauncher/templates/previewPicker.html",
  
  sitesEnabled: true,

  selectedUser: null,
  
  selectedUrl: null,
  
  selectedSite: null,

  users: null,
  
  urls: null,
  
  sites: null,
  
  userListElement: null,
  
  urlListElement: null,
  
  siteListElement: null,
  
  
   
  postCreate: function(){
    dojo.body().appendChild(this.domNode);
    this.inherited("postCreate",arguments);
    this.domNode.style.display="none";
    this.connect(this, "onExecute", "hide");
    this.connect(this, "onCancel", "hide");
    this.connect(window,'onresize',"_position");
    //console.debug(this.domNode);
  },


  onLoad: function(){
    // summary: 
    //    when href is specified we need to reposition the dialog after the data is loaded
    this.topic = dojo.event.topic.subscribe("atg/previewPicker/setChosen", this, "setChosen");
    // setup subscriptions

    this._position();
    this.inherited("onLoad",arguments);
  },
  
        
  hide: function(){
    // summary
    //    Hide the dialog

    // if we haven't been initialized yet then we aren't showing and we can just return   
    if(!this._alreadyInitialized){
      return;
    }

    if(this._fadeIn.status() == "playing"){
      this._fadeIn.stop();
    }
    this._fadeOut.play();

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
        dojo.removeClass(doc.body, "appletKiller");
      }
    );

    if (this._scrollConnected){
      this._scrollConnected = false;
    }
    dojo.forEach(this._modalconnects, dojo.disconnect);
    this._modalconnects = [];

    this.connect(this._fadeOut,"onEnd",dojo.hitch(this,function(){
      dijit.focus(this._savedFocus);
    }));
    this.open = false;

    //dijit.Dialog.prototype.hide.call(this);
  },
  
  
  show: function(){
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
        dojo.addClass(doc.body, "appletKiller");
      }
    );

    this._fadeIn.play();

    this._savedFocus = dijit.getFocus(this);

    // set timeout to allow the browser to render dialog
    setTimeout(dojo.hitch(this, function(){
      dijit.focus(this.titleBar);
    }), 50);

    //dijit.Dialog.prototype.show.call(this);
  },
  
  
  addListEntry: function(obj, prop, type, newUL){
    obj[prop].type = type;

    var newLI = document.createElement("LI");
    var newDIV = document.createElement("DIV");

    newDIV.id = "userId"+obj[prop].id;
    newDIV.obj = obj[prop];
    newDIV.innerHTML = atg.truncateMid(obj[prop].name || obj[prop].url, false);
    if (type != "urls")
      newDIV.title = obj[prop].name
    else
      newDIV.title = obj[prop].url;
    
    _this = this;
    dojo.connect(newDIV, "onclick", function(e){
      _this.resetChosen(e.target);
      _this.setChosen(e.target.obj);
      dojo.addClass(e.target, "selected");
    });

    if(obj[prop].selected === true){
       dojo.addClass(newDIV, "selected");
       this.setChosen(obj[prop]);
       this.oldSelection = newDIV;
    }

    if(obj[prop].as === true){
       dojo.addClass(newDIV, "assetSpecific");
       newDIV.title = "Asset Specific Preview Landing Page - "+newDIV.title;
    }

    newLI.appendChild(newDIV);
    newUL.appendChild(newLI);
  },


  setupList: function(obj, type){ 
      
    newUL = document.createElement("UL");
    newUL.setAttribute("id", type);
    
    for(prop in obj)
      this.addListEntry(obj, prop, type, newUL);
      
    if (type == "users")
      this.userListElement = newUL;
    else if (type == "sites")
      this.siteListElement = newUL;
    else if (type == "urls")
      this.urlListElement = newUL;
      
    if (this.userListElement != null && this.urlListElement != null && (!this.sitesEnabled || this.siteListElement != null) ) {
      this.ppPicker.appendChild(this.userListElement);
      if (this.sitesEnabled == true) {
        this.ppPicker.appendChild(this.siteListElement);
      }
      else {
        // resize the picker frame because there are no sites to choose
        var newPPWidth = (dojo.style(this.domNode, "width") * 2)/3;
        dojo.style(this.domNode, "width", new String(newPPWidth) + "px");
        this._position();
        dojo.byId("siteChosen").innerHTML = "";
        dojo.style(this.ppPicker, "width", new String(newPPWidth) + "px");
      }
      this.ppPicker.appendChild(this.urlListElement);
    }
  },

  
  setChosen: function(obj){
    switch(obj.type){
      case("users"):
        this.chosenUser.innerHTML = atg.truncateMid(obj.name);
        this.chosenUser.id = obj.id;
        this.chosenUser.title = obj.name;
        atg.sparkle(this.chosenUser);
        break;
      case("urls"):
        this.chosenURL.innerHTML = atg.truncateMid(obj.name);
        this.chosenURL.id = obj.id;
        this.chosenURL.title = obj.url;
        atg.sparkle(this.chosenURL);
        break;
      case("sites"):
        if (this.sitesEnabled == true) {
          this.chosenSite.innerHTML = atg.truncateMid(obj.name);
          this.chosenSite.id = obj.id;
          this.chosenSite.title = obj.name;
          atg.sparkle(this.chosenSite);
        }
        break;
    }
    
    if ( this.chosenUser.id != "" && this.chosenURL.id != "" && (!this.sitesEnabled || this.chosenSite.id != "") )
      this.previewLaunchButton.disabled = false;
  },
    
    
  resetChosen: function(node){
    var tagUL = node.parentNode.parentNode;    
    for(child in tagUL.childNodes){
      if(tagUL.childNodes[child].firstChild)
        dojo.removeClass(tagUL.childNodes[child].firstChild, "selected");
    }
  },
  
  
  launchClicked: function(){
    this.selectedUser = this.chosenUser.id;
    this.selectedUrl = this.chosenURL.title;
    if (this.sitesEnabled == true)
      this.selectedSite = this.chosenSite.id;
    this.pickerForm.userId.value = this.selectedUser;
    this.pickerForm.url.value = this.chosenURL.innerHTML;
    if (this.sitesEnabled == true)
      this.pickerForm.siteId.value = this.selectedSite;
    
    this.serverLaunchCheck();

    dojo.removeClass(document.body, "appletKiller");

    var previewButton = dojo.byId("previewButton") || window.frames["rightPaneIframe"].dojo.byId("previewButton");
    var previewAsButton = dojo.byId("previewAsButton") || window.frames["rightPaneIframe"].dojo.byId("previewAsButton");
    if (previewButton != null && previewAsButton != null && previewAsButton.style.visibility=="visible")
      previewButton.style.visibility="visible";

    this.hide();    
  },
  
  
  launchPopup: function(){
    var url = this.selectedUrl + "&atgPreviewId=" + this.selectedUser
                  + (this.sitesEnabled == true ? "&stickySite=setSite&pushSite=" + this.selectedSite : "");
    //var windowName = "ATG Preview.  User:" + this.chosenUser.innerHTML; + ", URL:" + url;
    // can't show the window name because IE does not allow spaces in the name field of the window.open() method.
    
    if (url.substr(0,4) == "http") {
      var startIndex = 0;
      if (url.substr(0,7) == "http://")
        startIndex = 7;        
      else if (url.substr(0,8) == "https://")
        startIndex = 8;
        
      var endIndex = url.indexOf('/', startIndex);
      var hostPort = "http://" + url.substring(startIndex,endIndex);
    
      window.open(
          hostPort + "/WebUI/preview/launched.jsp?atgPreviewId=" + this.selectedUser
                        + (this.sitesEnabled == true ? "&stickySite=setSite&pushSite=" + this.selectedSite : "")
                        + "&url=" + escape(url),
          "_blank",
          "location=1,status=1,scrollbars=1,resizable=1"
      );
    }
    else {
      window.open(
          "/WebUI/preview/launched.jsp?atgPreviewId=" + this.selectedUser
                      + (this.sitesEnabled == true ? "&stickySite=setSite&pushSite=" + this.selectedSite : "")
                      + "&url=" + escape(url),
          "_blank",
          "location=1,status=1,scrollbars=1,resizable=1"
      );
    }    
  },
    

  serverLaunchCheck: function(){
    this.launchPopup();
    var url = this.selectedUrl + "&atgPreviewId=" + this.selectedUser
                  + (this.sitesEnabled == true ? "&stickySite=setSite&pushSite=" + this.selectedSite : "");
    _this = this;
    dojo.xhrPost ({
        url: '/WebUI/preview/setPreviewParams.jsp',
        content: {"atgPreviewId": this.selectedUser, "url": escape(url), "site": this.selectedSite},
        handleAs: 'json',
        preventCache: true,
        form: this.pickerForm,
        load: function (data) {
          if(data.success != true){
            console.error('serverLaunchCheck Error: ', data.error);
            _this.errorHandling(data.error);
          }
        },
        error: function (error) {
          console.error('serverLaunchCheck Error: ', error);
          _this.errorHandling(error);
        }
    });
  },
  
  
  getPreviewUsers: function(){
    _this = this;
    console.log('getPreviewUsers send request');
    dojo.xhrGet ({
        url: '/WebUI/preview/getPreviewUsers.jsp',
        handleAs: 'json',
        preventCache: true,
        load: function (data) {
            console.log('getPreviewUsers request begin');
            if(data.success == true){
              _this.users = data.users;
              _this.setupList(_this.users, 'users');
            }
            else{
              console.error('getPreviewUsers Error: ', data.error);
              _this.errorHandling(data.error);
            }
            console.log('getPreviewUsers request done');
        },
        error: function (error) {
            console.error('getPreviewUsers Error: ', error);
            _this.errorHandling(error);
        }
    });
  },
  
  
  checkForPreviewURLs: function(mode, readOnlyMode, mappingName, assetURI){
    _this = this;
    dojo.xhrGet ({
        url: '/PubPortlets/html/ProjectsPortlet/getPreviewURLs.jsp',
        content: {"mode": mode,
                  "readOnlyMode": readOnlyMode,
                  "mappingName": mappingName,
                  "assetURI": assetURI
                 },
        handleAs: 'json',
        preventCache: true,
        load: function (data) {
            if(data.success == true){
              if (data.urls.length > 0) {
                var previewAsButton = dojo.byId("previewAsButton") || window.frames["rightPaneIframe"].dojo.byId("previewAsButton");
                if (previewAsButton != null)
                  previewAsButton.style.visibility="visible";
              }
              _this.getSelectedUserAndUrl();
            }
            else{
              console.error('checkForPreviewURLs Error: ', data.error);
              _this.errorHandling(data.error);
            }
        },
        error: function (error) {
            console.error('checkForPreviewURLs Error: ', error);
            _this.errorHandling(error);
        }
    });
  },
  

  getPreviewURLs: function(mode, readOnlyMode, mappingName, assetURI){
    _this = this;
    console.log('getPreviewURLs send request');
    dojo.xhrGet ({
        url: '/PubPortlets/html/ProjectsPortlet/getPreviewURLs.jsp',
        content: {"mode": mode,
                  "readOnlyMode": readOnlyMode,
                  "mappingName": mappingName,
                  "assetURI": assetURI
                 },
        handleAs: 'json',
        preventCache: true,
        load: function (data) {
            console.log('getPreviewURLs request begin');
            if(data.success == true){
              _this.urls = data.urls;
              _this.setupList(_this.urls, 'urls');
            }
            else{
              console.error('getPreviewURLs Error: ', data.error);
              _this.errorHandling(data.error);
            }
            console.log('getPreviewURLs request done');
        },
        error: function (error) {
            console.error('getPreviewURLs Error: ', error);
            _this.errorHandling(error);
        }
    });
  },
  

  getPreviewSites: function(){
    _this = this;
    console.log('getPreviewSites send request');
    dojo.xhrGet ({
        url: '/PubPortlets/html/ProjectsPortlet/getPreviewSites.jsp',
        handleAs: 'json',
        preventCache: true,
        load: function (data) {
            console.log('getPreviewSites request begin');
            if(data.success == true){
              if (data.enabled == true){
                _this.sites = data.sites;
                _this.setupList(_this.sites, 'sites');
              }
              else {
                _this.sitesEnabled = false;
              }
            }
            else{
              console.error('getPreviewSites Error: ', data.error);
              _this.errorHandling(data.error);
            }
            console.log('getPreviewSites request done');
        },
        error: function (error) {
            console.error('getPreviewSites Error: ', error);
            _this.errorHandling(error);
        }
    });
  },
  
  getSelectedUserAndUrl: function(){
    _this = this;
    dojo.xhrGet ({
        url: '/WebUI/preview/getSelectedUserAndUrl.jsp',
        handleAs: 'json',
        preventCache: true,
        load: function (data) {
            if(data.success == true){
              if (data.selectedUser != "" && data.selectedUrl != "") {
                _this.selectedUrl = data.selectedUrl;
                _this.selectedUser = data.selectedUser;
                _this.selectedSite = data.selectedSite;
                var previewButton = dojo.byId("previewButton") || window.frames["rightPaneIframe"].dojo.byId("previewButton");
                var previewAsButton = dojo.byId("previewButton") || window.frames["rightPaneIframe"].dojo.byId("previewAsButton");
                if (previewButton != null && previewAsButton != null && previewAsButton.style.visibility=="visible")
                  previewButton.style.visibility="visible";
              }
            }
            else{
              console.error('getSelectedUserAndUrl Error: ', data.error);
              _this.errorHandling(data.error);
            }
        },
        error: function (error) {
            console.error('getSelectedUserAndUrl Error: ', error);
            _this.errorHandling(error);
        }
    });
  },


  errorHandling: function(error){
    /* Need to double check that this is still the correct error messaging topic system */
    var errorMessage ={ "messages":[
                    { "summary":"Preview UI Error",
                      "type":"error",
                      "datetime":(new Date()).getTime(),
                      "details":[
                        { "description": error
                        }
                      ]
                    }
                  ]
                };
    dojo.topic.publish("/atg/message/addNew", errorMessage);
  }  
});
