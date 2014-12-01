/*
	Copyright (c) 2004-2007, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/book/dojo-book-0-9/introduction/licensing
*/

/*
	This is a compiled version of Dojo, built for deployment and not for
	development. To get an editable version, please visit:

		http://dojotoolkit.org

	for documentation and information on getting the source.
*/

if(!dojo._hasResource["dijit.layout.LayoutContainer"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.layout.LayoutContainer"] = true;
dojo.provide("dijit.layout.LayoutContainer");



dojo.declare(
	"dijit.layout.LayoutContainer",
	dijit.layout._LayoutWidget,
{
	// summary
	//	Provides Delphi-style panel layout semantics.
	//
	// details
	//	A LayoutContainer is a box with a specified size (like style="width: 500px; height: 500px;"),
	//	that contains children widgets marked with "layoutAlign" of "left", "right", "bottom", "top", and "client".
	//	It takes it's children marked as left/top/bottom/right, and lays them out along the edges of the box,
	//	and then it takes the child marked "client" and puts it into the remaining space in the middle.
	//
	//  Left/right positioning is similar to CSS's "float: left" and "float: right",
	//	and top/bottom positioning would be similar to "float: top" and "float: bottom", if there were such
	//	CSS.
	//
	//	Note that there can only be one client element, but there can be multiple left, right, top,
	//	or bottom elements.
	//
	// usage
	//	<style>
	//		html, body{ height: 100%; width: 100%; }
	//	</style>
	//	<div dojoType="dijit.layout.LayoutContainer" style="width: 100%; height: 100%">
	//		<div dojoType="dijit.layout.ContentPane" layoutAlign="top">header text</div>
	//		<div dojoType="dijit.layout.ContentPane" layoutAlign="left" style="width: 200px;">table of contents</div>
	//		<div dojoType="dijit.layout.ContentPane" layoutAlign="client">client area</div>
	//	</div>
	//
	//	Lays out each child in the natural order the children occur in.
	//	Basically each child is laid out into the "remaining space", where "remaining space" is initially
	//	the content area of this widget, but is reduced to a smaller rectangle each time a child is added.
	//	

	layout: function(){
		dijit.layout.layoutChildren(this.domNode, this._contentBox, this.getChildren());
	},

	addChild: function(/*Widget*/ child, /*Integer?*/ insertIndex){
		dijit._Container.prototype.addChild.apply(this, arguments);
		if(this._started){
			dijit.layout.layoutChildren(this.domNode, this._contentBox, this.getChildren());
		}
	},

	removeChild: function(/*Widget*/ widget){
		dijit._Container.prototype.removeChild.apply(this, arguments);
		if(this._started){
			dijit.layout.layoutChildren(this.domNode, this._contentBox, this.getChildren());
		}
	}
});

// This argument can be specified for the children of a LayoutContainer.
// Since any widget can be specified as a LayoutContainer child, mix it
// into the base widget class.  (This is a hack, but it's effective.)
dojo.extend(dijit._Widget, {
	// layoutAlign: String
	//		"none", "left", "right", "bottom", "top", and "client".
	//		See the LayoutContainer description for details on this parameter.
	layoutAlign: 'none'
});

}

if(!dojo._hasResource["dojo.i18n"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.i18n"] = true;
dojo.provide("dojo.i18n");

dojo.i18n.getLocalization = function(/*String*/packageName, /*String*/bundleName, /*String?*/locale){
	//	summary:
	//		Returns an Object containing the localization for a given resource
	//		bundle in a package, matching the specified locale.
	//	description:
	//		Returns a hash containing name/value pairs in its prototypesuch
	//		that values can be easily overridden.  Throws an exception if the
	//		bundle is not found.  Bundle must have already been loaded by
	//		dojo.requireLocalization() or by a build optimization step.  NOTE:
	//		try not to call this method as part of an object property
	//		definition (var foo = { bar: dojo.i18n.getLocalization() }).  In
	//		some loading situations, the bundle may not be available in time
	//		for the object definition.  Instead, call this method inside a
	//		function that is run after all modules load or the page loads (like
	//		in dojo.adOnLoad()), or in a widget lifecycle method.
	//	packageName:
	//		package which is associated with this resource
	//	bundleName:
	//		the base filename of the resource bundle (without the ".js" suffix)
	//	locale:
	//		the variant to load (optional).  By default, the locale defined by
	//		the host environment: dojo.locale

	locale = dojo.i18n.normalizeLocale(locale);

	// look for nearest locale match
	var elements = locale.split('-');
	var module = [packageName,"nls",bundleName].join('.');
	var bundle = dojo._loadedModules[module];
	if(bundle){
		var localization;
		for(var i = elements.length; i > 0; i--){
			var loc = elements.slice(0, i).join('_');
			if(bundle[loc]){
				localization = bundle[loc];
				break;
			}
		}
		if(!localization){
			localization = bundle.ROOT;
		}

		// make a singleton prototype so that the caller won't accidentally change the values globally
		if(localization){
			var clazz = function(){};
			clazz.prototype = localization;
			return new clazz(); // Object
		}
	}

	throw new Error("Bundle not found: " + bundleName + " in " + packageName+" , locale=" + locale);
};

dojo.i18n.normalizeLocale = function(/*String?*/locale){
	//	summary:
	//		Returns canonical form of locale, as used by Dojo.
	//
	//  description:
	//		All variants are case-insensitive and are separated by '-' as specified in RFC 3066.
	//		If no locale is specified, the dojo.locale is returned.  dojo.locale is defined by
	//		the user agent's locale unless overridden by djConfig.

	var result = locale ? locale.toLowerCase() : dojo.locale;
	if(result == "root"){
		result = "ROOT";
	}
	return result; // String
};

dojo.i18n._requireLocalization = function(/*String*/moduleName, /*String*/bundleName, /*String?*/locale, /*String?*/availableFlatLocales){
	//	summary:
	//		See dojo.requireLocalization()
	//	description:
	// 		Called by the bootstrap, but factored out so that it is only
	// 		included in the build when needed.

	var targetLocale = dojo.i18n.normalizeLocale(locale);
 	var bundlePackage = [moduleName, "nls", bundleName].join(".");
	// NOTE: 
	//		When loading these resources, the packaging does not match what is
	//		on disk.  This is an implementation detail, as this is just a
	//		private data structure to hold the loaded resources.  e.g.
	//		tests/hello/nls/en-us/salutations.js is loaded as the object
	//		tests.hello.nls.salutations.en_us={...} The structure on disk is
	//		intended to be most convenient for developers and translators, but
	//		in memory it is more logical and efficient to store in a different
	//		order.  Locales cannot use dashes, since the resulting path will
	//		not evaluate as valid JS, so we translate them to underscores.
	
	//Find the best-match locale to load if we have available flat locales.
	var bestLocale = "";
	if(availableFlatLocales){
		var flatLocales = availableFlatLocales.split(",");
		for(var i = 0; i < flatLocales.length; i++){
			//Locale must match from start of string.
			if(targetLocale.indexOf(flatLocales[i]) == 0){
				if(flatLocales[i].length > bestLocale.length){
					bestLocale = flatLocales[i];
				}
			}
		}
		if(!bestLocale){
			bestLocale = "ROOT";
		}		
	}

	//See if the desired locale is already loaded.
	var tempLocale = availableFlatLocales ? bestLocale : targetLocale;
	var bundle = dojo._loadedModules[bundlePackage];
	var localizedBundle = null;
	if(bundle){
		if(djConfig.localizationComplete && bundle._built){return;}
		var jsLoc = tempLocale.replace(/-/g, '_');
		var translationPackage = bundlePackage+"."+jsLoc;
		localizedBundle = dojo._loadedModules[translationPackage];
	}

	if(!localizedBundle){
		bundle = dojo["provide"](bundlePackage);
		var syms = dojo._getModuleSymbols(moduleName);
		var modpath = syms.concat("nls").join("/");
		var parent;

		dojo.i18n._searchLocalePath(tempLocale, availableFlatLocales, function(loc){
			var jsLoc = loc.replace(/-/g, '_');
			var translationPackage = bundlePackage + "." + jsLoc;
			var loaded = false;
			if(!dojo._loadedModules[translationPackage]){
				// Mark loaded whether it's found or not, so that further load attempts will not be made
				dojo["provide"](translationPackage);
				var module = [modpath];
				if(loc != "ROOT"){module.push(loc);}
				module.push(bundleName);
				var filespec = module.join("/") + '.js';
				loaded = dojo._loadPath(filespec, null, function(hash){
					// Use singleton with prototype to point to parent bundle, then mix-in result from loadPath
					var clazz = function(){};
					clazz.prototype = parent;
					bundle[jsLoc] = new clazz();
					for(var j in hash){ bundle[jsLoc][j] = hash[j]; }
				});
			}else{
				loaded = true;
			}
			if(loaded && bundle[jsLoc]){
				parent = bundle[jsLoc];
			}else{
				bundle[jsLoc] = parent;
			}
			
			if(availableFlatLocales){
				//Stop the locale path searching if we know the availableFlatLocales, since
				//the first call to this function will load the only bundle that is needed.
				return true;
			}
		});
	}

	//Save the best locale bundle as the target locale bundle when we know the
	//the available bundles.
	if(availableFlatLocales && targetLocale != bestLocale){
		bundle[targetLocale.replace(/-/g, '_')] = bundle[bestLocale.replace(/-/g, '_')];
	}
};

(function(){
	// If other locales are used, dojo.requireLocalization should load them as
	// well, by default. 
	// 
	// Override dojo.requireLocalization to do load the default bundle, then
	// iterate through the extraLocale list and load those translations as
	// well, unless a particular locale was requested.

	var extra = djConfig.extraLocale;
	if(extra){
		if(!extra instanceof Array){
			extra = [extra];
		}

		var req = dojo.i18n._requireLocalization;
		dojo.i18n._requireLocalization = function(m, b, locale, availableFlatLocales){
			req(m,b,locale, availableFlatLocales);
			if(locale){return;}
			for(var i=0; i<extra.length; i++){
				req(m,b,extra[i], availableFlatLocales);
			}
		};
	}
})();

dojo.i18n._searchLocalePath = function(/*String*/locale, /*Boolean*/down, /*Function*/searchFunc){
	//	summary:
	//		A helper method to assist in searching for locale-based resources.
	//		Will iterate through the variants of a particular locale, either up
	//		or down, executing a callback function.  For example, "en-us" and
	//		true will try "en-us" followed by "en" and finally "ROOT".

	locale = dojo.i18n.normalizeLocale(locale);

	var elements = locale.split('-');
	var searchlist = [];
	for(var i = elements.length; i > 0; i--){
		searchlist.push(elements.slice(0, i).join('-'));
	}
	searchlist.push(false);
	if(down){searchlist.reverse();}

	for(var j = searchlist.length - 1; j >= 0; j--){
		var loc = searchlist[j] || "ROOT";
		var stop = searchFunc(loc);
		if(stop){ break; }
	}
};

dojo.i18n._preloadLocalizations = function(/*String*/bundlePrefix, /*Array*/localesGenerated){
	//	summary:
	//		Load built, flattened resource bundles, if available for all
	//		locales used in the page. Only called by built layer files.

	function preload(locale){
		locale = dojo.i18n.normalizeLocale(locale);
		dojo.i18n._searchLocalePath(locale, true, function(loc){
			for(var i=0; i<localesGenerated.length;i++){
				if(localesGenerated[i] == loc){
					dojo["require"](bundlePrefix+"_"+loc);
					return true; // Boolean
				}
			}
			return false; // Boolean
		});
	}
	preload();
	var extra = djConfig.extraLocale||[];
	for(var i=0; i<extra.length; i++){
		preload(extra[i]);
	}
};

}

if(!dojo._hasResource["dijit.layout.ContentPane"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.layout.ContentPane"] = true;
dojo.provide("dijit.layout.ContentPane");








dojo.declare(
	"dijit.layout.ContentPane",
	dijit._Widget,
{
	// summary:
	//		A widget that acts as a Container for other widgets, and includes a ajax interface
	// description:
	//		A widget that can be used as a standalone widget
	//		or as a baseclass for other widgets
	//		Handles replacement of document fragment using either external uri or javascript
	//		generated markup or DOM content, instantiating widgets within that content.
	//		Don't confuse it with an iframe, it only needs/wants document fragments.
	//		It's useful as a child of LayoutContainer, SplitContainer, or TabContainer.
	//		But note that those classes can contain any widget as a child.
	// example:
	//		Some quick samples:
	//		To change the innerHTML use .setContent('<b>new content</b>')
	//
	//		Or you can send it a NodeList, .setContent(dojo.query('div [class=selected]', userSelection))
	//		please note that the nodes in NodeList will copied, not moved
	//
	//		To do a ajax update use .setHref('url')
	//
	// href: String
	//		The href of the content that displays now.
	//		Set this at construction if you want to load data externally when the
	//		pane is shown.  (Set preload=true to load it immediately.)
	//		Changing href after creation doesn't have any effect; see setHref();
	href: "",

	// extractContent: Boolean
	//	Extract visible content from inside of <body> .... </body>
	extractContent: false,

	// parseOnLoad: Boolean
	//	parse content and create the widgets, if any
	parseOnLoad:	true,

	// preventCache: Boolean
	//		Cache content retreived externally
	preventCache:	false,

	// preload: Boolean
	//	Force load of data even if pane is hidden.
	preload: false,

	// refreshOnShow: Boolean
	//		Refresh (re-download) content when pane goes from hidden to shown
	refreshOnShow: false,

	// loadingMessage: String
	//	Message that shows while downloading
	loadingMessage: "<span class='dijitContentPaneLoading'>${loadingState}</span>", 

	// errorMessage: String
	//	Message that shows if an error occurs
	errorMessage: "<span class='dijitContentPaneError'>${errorState}</span>", 

	// isLoaded: Boolean
	//	Tells loading status see onLoad|onUnload for event hooks
	isLoaded: false,

	// class: String
	//	Class name to apply to ContentPane dom nodes
	"class": "dijitContentPane",

	postCreate: function(){
		// remove the title attribute so it doesn't show up when i hover
		// over a node
		this.domNode.title = "";

		if(this.preload){
			this._loadCheck();
		}

		var messages = dojo.i18n.getLocalization("dijit", "loading", this.lang);
		this.loadingMessage = dojo.string.substitute(this.loadingMessage, messages);
		this.errorMessage = dojo.string.substitute(this.errorMessage, messages);

		// for programatically created ContentPane (with <span> tag), need to muck w/CSS
		// or it's as though overflow:visible is set
		dojo.addClass(this.domNode, this["class"]);
	},

	startup: function(){
		if(this._started){ return; }
		this._checkIfSingleChild();
		if(this._singleChild){
			this._singleChild.startup();
		}
		this._loadCheck();
		this._started = true;
	},

	_checkIfSingleChild: function(){
		// summary:
		// 	Test if we have exactly one widget as a child, and if so assume that we are a container for that widget,
		//	and should propogate startup() and resize() calls to it.
		var childNodes = dojo.query(">", this.containerNode || this.domNode),
			childWidgets = childNodes.filter("[widgetId]");

		if(childNodes.length == 1 && childWidgets.length == 1){
			this.isContainer = true;
			this._singleChild = dijit.byNode(childWidgets[0]);
		}else{
			delete this.isContainer;
			delete this._singleChild;
		}
	},

	refresh: function(){
		// summary:
		//	Force a refresh (re-download) of content, be sure to turn off cache

		// we return result of _prepareLoad here to avoid code dup. in dojox.layout.ContentPane
		return this._prepareLoad(true);
	},

	setHref: function(/*String|Uri*/ href){
		// summary:
		//		Reset the (external defined) content of this pane and replace with new url
		//		Note: It delays the download until widget is shown if preload is false
		//	href:
		//		url to the page you want to get, must be within the same domain as your mainpage
		this.href = href;

		// we return result of _prepareLoad here to avoid code dup. in dojox.layout.ContentPane
		return this._prepareLoad();
	},

	setContent: function(/*String|DomNode|Nodelist*/data){
		// summary:
		//		Replaces old content with data content, include style classes from old content
		//	data:
		//		the new Content may be String, DomNode or NodeList
		//
		//		if data is a NodeList (or an array of nodes) nodes are copied
		//		so you can import nodes from another document implicitly

		// clear href so we cant run refresh and clear content
		// refresh should only work if we downloaded the content
		if(!this._isDownloaded){
			this.href = "";
			this._onUnloadHandler();
		}

		this._setContent(data || "");

		this._isDownloaded = false; // must be set after _setContent(..), pathadjust in dojox.layout.ContentPane

		if(this.parseOnLoad){
			this._createSubWidgets();
		}

		this._checkIfSingleChild();
		if(this._singleChild && this._singleChild.resize){
			this._singleChild.resize(this._contentBox);
		}

		this._onLoadHandler();
	},

	cancel: function(){
		// summary:
		//		Cancels a inflight download of content
		if(this._xhrDfd && (this._xhrDfd.fired == -1)){
			this._xhrDfd.cancel();
		}
		delete this._xhrDfd; // garbage collect
	},

	destroy: function(){
		// if we have multiple controllers destroying us, bail after the first
		if(this._beingDestroyed){
			return;
		}
		// make sure we call onUnload
		this._onUnloadHandler();
		this._beingDestroyed = true;
		this.inherited("destroy",arguments);
	},

	resize: function(size){
		dojo.marginBox(this.domNode, size);

		// Compute content box size in case we [later] need to size child
		// If either height or width wasn't specified by the user, then query node for it.
		// But note that setting the margin box and then immediately querying dimensions may return
		// inaccurate results, so try not to depend on it.
		var node = this.containerNode || this.domNode,
			mb = dojo.mixin(dojo.marginBox(node), size||{});

		this._contentBox = dijit.layout.marginBox2contentBox(node, mb);

		// If we have a single widget child then size it to fit snugly within my borders
		if(this._singleChild && this._singleChild.resize){
			this._singleChild.resize(this._contentBox);
		}
	},

	_prepareLoad: function(forceLoad){
		// sets up for a xhrLoad, load is deferred until widget onShow
		// cancels a inflight download
		this.cancel();
		this.isLoaded = false;
		this._loadCheck(forceLoad);
	},

	_loadCheck: function(forceLoad){
		// call this when you change onShow (onSelected) status when selected in parent container
		// it's used as a trigger for href download when this.domNode.display != 'none'

		// sequence:
		// if no href -> bail
		// forceLoad -> always load
		// this.preload -> load when download not in progress, domNode display doesn't matter
		// this.refreshOnShow -> load when download in progress bails, domNode display !='none' AND
		//						this.open !== false (undefined is ok), isLoaded doesn't matter
		// else -> load when download not in progress, if this.open !== false (undefined is ok) AND
		//						domNode display != 'none', isLoaded must be false

		var displayState = ((this.open !== false) && (this.domNode.style.display != 'none'));

		if(this.href &&	
			(forceLoad ||
				(this.preload && !this._xhrDfd) ||
				(this.refreshOnShow && displayState && !this._xhrDfd) ||
				(!this.isLoaded && displayState && !this._xhrDfd)
			)
		){
			this._downloadExternalContent();
		}
	},

	_downloadExternalContent: function(){
		this._onUnloadHandler();

		// display loading message
		this._setContent(
			this.onDownloadStart.call(this)
		);

		var self = this;
		var getArgs = {
			preventCache: (this.preventCache || this.refreshOnShow),
			url: this.href,
			handleAs: "text"
		};
		if(dojo.isObject(this.ioArgs)){
			dojo.mixin(getArgs, this.ioArgs);
		}

		var hand = this._xhrDfd = (this.ioMethod || dojo.xhrGet)(getArgs);

		hand.addCallback(function(html){
			try{
				self.onDownloadEnd.call(self);
				self._isDownloaded = true;
				self.setContent.call(self, html); // onload event is called from here
			}catch(err){
				self._onError.call(self, 'Content', err); // onContentError
			}
			delete self._xhrDfd;
			return html;
		});

		hand.addErrback(function(err){
			if(!hand.cancelled){
				// show error message in the pane
				self._onError.call(self, 'Download', err); // onDownloadError
			}
			delete self._xhrDfd;
			return err;
		});
	},

	_onLoadHandler: function(){
		this.isLoaded = true;
		try{
			this.onLoad.call(this);
		}catch(e){
			console.error('Error '+this.widgetId+' running custom onLoad code');
		}
	},

	_onUnloadHandler: function(){
		this.isLoaded = false;
		this.cancel();
		try{
			this.onUnload.call(this);
		}catch(e){
			console.error('Error '+this.widgetId+' running custom onUnload code');
		}
	},

	_setContent: function(cont){
		this.destroyDescendants();

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
	},

	_onError: function(type, err, consoleText){
		// shows user the string that is returned by on[type]Error
		// overide on[type]Error and return your own string to customize
		var errText = this['on' + type + 'Error'].call(this, err);
		if(consoleText){
			console.error(consoleText, err);
		}else if(errText){// a empty string won't change current content
			this._setContent.call(this, errText);
		}
	},

	_createSubWidgets: function(){
		// summary: scan my contents and create subwidgets
		var rootNode = this.containerNode || this.domNode;
		try{
			dojo.parser.parse(rootNode, true);
		}catch(e){
			this._onError('Content', e, "Couldn't create widgets in "+this.id
				+(this.href ? " from "+this.href : ""));
		}
	},

	// EVENT's, should be overide-able
	onLoad: function(e){
		// summary:
		//		Event hook, is called after everything is loaded and widgetified
	},

	onUnload: function(e){
		// summary:
		//		Event hook, is called before old content is cleared
	},

	onDownloadStart: function(){
		// summary:
		//		called before download starts
		//		the string returned by this function will be the html
		//		that tells the user we are loading something
		//		override with your own function if you want to change text
		return this.loadingMessage;
	},

	onContentError: function(/*Error*/ error){
		// summary:
		//		called on DOM faults, require fault etc in content
		//		default is to display errormessage inside pane
	},

	onDownloadError: function(/*Error*/ error){
		// summary:
		//		Called when download error occurs, default is to display
		//		errormessage inside pane. Overide function to change that.
		//		The string returned by this function will be the html
		//		that tells the user a error happend
		return this.errorMessage;
	},

	onDownloadEnd: function(){
		// summary:
		//		called when download is finished
	}
});

}

if(!dojo._hasResource["dojo.cookie"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.cookie"] = true;
dojo.provide("dojo.cookie");

/*=====
dojo.__cookieProps = function(kwArgs){
	//	expires: Date|Number?
	//		If a number, seen as the number of days from today. If a date, the
	//		date past which the cookie is invalid. If expires is in the past,
	//		the cookie will be deleted If expires is left out or is 0, the
	//		cookie will expire when the browser closes.
	//	path: String?
	//		The path to use for the cookie.
	//	domain: String?
	//		The domain to use for the cookie.
	//	secure: Boolean?
	//		Whether to only send the cookie on secure connections
}
=====*/


dojo.cookie = function(/*String*/name, /*String?*/value, /*dojo.__cookieProps?*/props){
	//	summary: 
	//		Get or set a cookie.
	//	description:
	// 		If you pass in one argument, the the value of the cookie is returned
	//
	// 		If you pass in two arguments, the cookie value is set to the second
	// 		argument.
	//
	// 		If you pass in three arguments, the cookie value is set to the
	// 		second argument, and the options on the third argument are used for
	// 		extended properties on the cookie
	//	name:
	//		The name of the cookie
	//	value:
	//		Optional. The value for the cookie.
	//	props: 
	//		Optional additional properties for the cookie
	//	example:
	//		set a cookie with the JSON-serialized contents of an object which
	//		will expire 5 days from now:
	//	|	dojo.cookie("configObj", dojo.toJson(config), { expires: 5 });
	//	
	//	example:
	//		de-serialize a cookie back into a JavaScript object:
	//	|	var config = dojo.fromJson(dojo.cookie("configObj"));
	//	
	//	example:
	//		delete a cookie:
	//	|	dojo.cookie("configObj", null);
	var c = document.cookie;
	if(arguments.length == 1){
		var idx = c.lastIndexOf(name+'=');
		if(idx == -1){ return null; }
		var start = idx+name.length+1;
		var end = c.indexOf(';', idx+name.length+1);
		if(end == -1){ end = c.length; }
		return decodeURIComponent(c.substring(start, end)); 
	}else{
		props = props || {};
		value = encodeURIComponent(value);
		if(typeof(props.expires) == "number"){ 
			var d = new Date();
			d.setTime(d.getTime()+(props.expires*24*60*60*1000));
			props.expires = d;
		}
		document.cookie = name + "=" + value 
			+ (props.expires ? "; expires=" + props.expires.toUTCString() : "")
			+ (props.path ? "; path=" + props.path : "")
			+ (props.domain ? "; domain=" + props.domain : "")
			+ (props.secure ? "; secure" : "");
		return null;
	}
};

}

if(!dojo._hasResource["dijit.layout.SplitContainer"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.layout.SplitContainer"] = true;
dojo.provide("dijit.layout.SplitContainer");

//
// FIXME: make it prettier
// FIXME: active dragging upwards doesn't always shift other bars (direction calculation is wrong in this case)
//




dojo.declare("dijit.layout.SplitContainer",
	dijit.layout._LayoutWidget,
	{
	// summary: 
	//	A Container widget with sizing handles in-between each child
	// description:
	//		Contains multiple children widgets, all of which are displayed side by side
	//		(either horizontally or vertically); there's a bar between each of the children,
	//		and you can adjust the relative size of each child by dragging the bars.
	//
	//		You must specify a size (width and height) for the SplitContainer.
	//
	// activeSizing: Boolean
	//		If true, the children's size changes as you drag the bar;
	//		otherwise, the sizes don't change until you drop the bar (by mouse-up)
	activeSizing: false,

	// sizerWidth: Integer
	//		Size in pixels of the bar between each child
	sizerWidth: 7, // FIXME: this should be a CSS attribute (at 7 because css wants it to be 7 until we fix to css)

	// orientation: String
	//		either 'horizontal' or vertical; indicates whether the children are
	//		arranged side-by-side or up/down.
	orientation: 'horizontal',

	// persist: Boolean
	//		Save splitter positions in a cookie
	persist: true,

	postMixInProperties: function(){
		this.inherited("postMixInProperties",arguments);
		this.isHorizontal = (this.orientation == 'horizontal');
	},

	postCreate: function(){
		this.inherited("postCreate",arguments);
		this.sizers = [];
		dojo.addClass(this.domNode, "dijitSplitContainer");
		// overflow has to be explicitly hidden for splitContainers using gekko (trac #1435)
		// to keep other combined css classes from inadvertantly making the overflow visible
		if(dojo.isMozilla){
			this.domNode.style.overflow = '-moz-scrollbars-none'; // hidden doesn't work
		}

		// create the fake dragger
		if(typeof this.sizerWidth == "object"){
			try{ //FIXME: do this without a try/catch
				this.sizerWidth = parseInt(this.sizerWidth.toString());
			}catch(e){ this.sizerWidth = 7; }
		}
		var sizer = this.virtualSizer = document.createElement('div');
		sizer.style.position = 'relative';

		// #1681: work around the dreaded 'quirky percentages in IE' layout bug
		// If the splitcontainer's dimensions are specified in percentages, it
		// will be resized when the virtualsizer is displayed in _showSizingLine
		// (typically expanding its bounds unnecessarily). This happens because
		// we use position: relative for .dijitSplitContainer.
		// The workaround: instead of changing the display style attribute,
		// switch to changing the zIndex (bring to front/move to back)

		sizer.style.zIndex = 10;
		sizer.className = this.isHorizontal ? 'dijitSplitContainerVirtualSizerH' : 'dijitSplitContainerVirtualSizerV';
		this.domNode.appendChild(sizer);
		dojo.setSelectable(sizer, false);
	},

	startup: function(){
		if(this._started){ return; }
		dojo.forEach(this.getChildren(), function(child, i, children){
			// attach the children and create the draggers
			this._injectChild(child);

			if(i < children.length-1){
				this._addSizer();
			}
		}, this);

		if(this.persist){
			this._restoreState();
		}
		this.inherited("startup",arguments); 
		this._started = true;
	},

	_injectChild: function(child){
		child.domNode.style.position = "absolute";
		dojo.addClass(child.domNode, "dijitSplitPane");
	},

	_addSizer: function(){
		var i = this.sizers.length;

		// TODO: use a template for this!!!
		var sizer = this.sizers[i] = document.createElement('div');
		sizer.className = this.isHorizontal ? 'dijitSplitContainerSizerH' : 'dijitSplitContainerSizerV';

		// add the thumb div
		var thumb = document.createElement('div');
		thumb.className = 'thumb';
		sizer.appendChild(thumb);

		// FIXME: are you serious? why aren't we using mover start/stop combo?
		var self = this;
		var handler = (function(){ var sizer_i = i; return function(e){ self.beginSizing(e, sizer_i); } })();
		dojo.connect(sizer, "onmousedown", handler);

		this.domNode.appendChild(sizer);
		dojo.setSelectable(sizer, false);
	},

	removeChild: function(widget){
		// summary: Remove sizer, but only if widget is really our child and
		// we have at least one sizer to throw away
		if(this.sizers.length && dojo.indexOf(this.getChildren(), widget) != -1){
			var i = this.sizers.length - 1;
			dojo._destroyElement(this.sizers[i]);
			this.sizers.length--;
		}

		// Remove widget and repaint
		this.inherited("removeChild",arguments); 
		if(this._started){
			this.layout();
		}
	},

	addChild: function(/*Widget*/ child, /*Integer?*/ insertIndex){
		// summary: Add a child widget to the container
		// child: a widget to add
		// insertIndex: postion in the "stack" to add the child widget
		
		this.inherited("addChild",arguments); 

		if(this._started){
			// Do the stuff that startup() does for each widget
			this._injectChild(child);
			var children = this.getChildren();
			if(children.length > 1){
				this._addSizer();
			}

			// and then reposition (ie, shrink) every pane to make room for the new guy
			this.layout();
		}
	},

	layout: function(){
		// summary:
		//		Do layout of panels

		// base class defines this._contentBox on initial creation and also
		// on resize
		this.paneWidth = this._contentBox.w;
		this.paneHeight = this._contentBox.h;

		var children = this.getChildren();
		if(!children.length){ return; }

		//
		// calculate space
		//

		var space = this.isHorizontal ? this.paneWidth : this.paneHeight;
		if(children.length > 1){
			space -= this.sizerWidth * (children.length - 1);
		}

		//
		// calculate total of SizeShare values
		//
		var outOf = 0;
		dojo.forEach(children, function(child){
			outOf += child.sizeShare;
		});

		//
		// work out actual pixels per sizeshare unit
		//
		var pixPerUnit = space / outOf;

		//
		// set the SizeActual member of each pane
		//
		var totalSize = 0;
		dojo.forEach(children.slice(0, children.length - 1), function(child){
			var size = Math.round(pixPerUnit * child.sizeShare);
			child.sizeActual = size;
			totalSize += size;
		});

		children[children.length-1].sizeActual = space - totalSize;

		//
		// make sure the sizes are ok
		//
		this._checkSizes();

		//
		// now loop, positioning each pane and letting children resize themselves
		//

		var pos = 0;
		var size = children[0].sizeActual;
		this._movePanel(children[0], pos, size);
		children[0].position = pos;
		pos += size;

		// if we don't have any sizers, our layout method hasn't been called yet
		// so bail until we are called..TODO: REVISIT: need to change the startup
		// algorithm to guaranteed the ordering of calls to layout method
		if(!this.sizers){
			return;
		}

		dojo.some(children.slice(1), function(child, i){
			// error-checking
			if(!this.sizers[i]){
				return true;
			}
			// first we position the sizing handle before this pane
			this._moveSlider(this.sizers[i], pos, this.sizerWidth);
			this.sizers[i].position = pos;
			pos += this.sizerWidth;

			size = child.sizeActual;
			this._movePanel(child, pos, size);
			child.position = pos;
			pos += size;
		}, this);
	},

	_movePanel: function(panel, pos, size){
		if(this.isHorizontal){
			panel.domNode.style.left = pos + 'px';	// TODO: resize() takes l and t parameters too, don't need to set manually
			panel.domNode.style.top = 0;
			var box = {w: size, h: this.paneHeight};
			if(panel.resize){
				panel.resize(box);
			}else{
				dojo.marginBox(panel.domNode, box);
			}
		}else{
			panel.domNode.style.left = 0;	// TODO: resize() takes l and t parameters too, don't need to set manually
			panel.domNode.style.top = pos + 'px';
			var box = {w: this.paneWidth, h: size};
			if(panel.resize){
				panel.resize(box);
			}else{
				dojo.marginBox(panel.domNode, box);
			}
		}
	},

	_moveSlider: function(slider, pos, size){
		if(this.isHorizontal){
			slider.style.left = pos + 'px';
			slider.style.top = 0;
			dojo.marginBox(slider, { w: size, h: this.paneHeight });
		}else{
			slider.style.left = 0;
			slider.style.top = pos + 'px';
			dojo.marginBox(slider, { w: this.paneWidth, h: size });
		}
	},

	_growPane: function(growth, pane){
		if(growth > 0){
			if(pane.sizeActual > pane.sizeMin){
				if((pane.sizeActual - pane.sizeMin) > growth){

					// stick all the growth in this pane
					pane.sizeActual = pane.sizeActual - growth;
					growth = 0;
				}else{
					// put as much growth in here as we can
					growth -= pane.sizeActual - pane.sizeMin;
					pane.sizeActual = pane.sizeMin;
				}
			}
		}
		return growth;
	},

	_checkSizes: function(){

		var totalMinSize = 0;
		var totalSize = 0;
		var children = this.getChildren();

		dojo.forEach(children, function(child){
			totalSize += child.sizeActual;
			totalMinSize += child.sizeMin;
		});

		// only make adjustments if we have enough space for all the minimums

		if(totalMinSize <= totalSize){

			var growth = 0;

			dojo.forEach(children, function(child){
				if(child.sizeActual < child.sizeMin){
					growth += child.sizeMin - child.sizeActual;
					child.sizeActual = child.sizeMin;
				}
			});

			if(growth > 0){
				var list = this.isDraggingLeft ? children.reverse() : children;
				dojo.forEach(list, function(child){
					growth = this._growPane(growth, child);
				}, this);
			}
		}else{
			dojo.forEach(children, function(child){
				child.sizeActual = Math.round(totalSize * (child.sizeMin / totalMinSize));
			});
		}
	},

	beginSizing: function(e, i){
		var children = this.getChildren();
		this.paneBefore = children[i];
		this.paneAfter = children[i+1];

		this.isSizing = true;
		this.sizingSplitter = this.sizers[i];

		if(!this.cover){
			this.cover = dojo.doc.createElement('div');
			this.domNode.appendChild(this.cover);
			var s = this.cover.style;
			s.position = 'absolute';
			s.zIndex = 1;
			s.top = 0;
			s.left = 0;
			s.width = "100%";
			s.height = "100%";
		}else{
			this.cover.style.zIndex = 1;
		}
		this.sizingSplitter.style.zIndex = 2;

		// TODO: REVISIT - we want MARGIN_BOX and core hasn't exposed that yet (but can't we use it anyway if we pay attention? we do elsewhere.)
		this.originPos = dojo.coords(children[0].domNode, true);
		if(this.isHorizontal){
			var client = (e.layerX ? e.layerX : e.offsetX);
			var screen = e.pageX;
			this.originPos = this.originPos.x;
		}else{
			var client = (e.layerY ? e.layerY : e.offsetY);
			var screen = e.pageY;
			this.originPos = this.originPos.y;
		}
		this.startPoint = this.lastPoint = screen;
		this.screenToClientOffset = screen - client;
		this.dragOffset = this.lastPoint - this.paneBefore.sizeActual - this.originPos - this.paneBefore.position;

		if(!this.activeSizing){
			this._showSizingLine();
		}

		//					
		// attach mouse events
		//
		this._connects = [];
		this._connects.push(dojo.connect(document.documentElement, "onmousemove", this, "changeSizing"));
		this._connects.push(dojo.connect(document.documentElement, "onmouseup", this, "endSizing"));

		dojo.stopEvent(e);
	},

	changeSizing: function(e){
		if(!this.isSizing){ return; }
		this.lastPoint = this.isHorizontal ? e.pageX : e.pageY;
		this.movePoint();
		if(this.activeSizing){
			this._updateSize();
		}else{
			this._moveSizingLine();
		}
		dojo.stopEvent(e);
	},

	endSizing: function(e){
		if(!this.isSizing){ return; }
		if(this.cover){
			this.cover.style.zIndex = -1;
		}
		if(!this.activeSizing){
			this._hideSizingLine();
		}

		this._updateSize();

		this.isSizing = false;

		if(this.persist){
			this._saveState(this);
		}

		dojo.forEach(this._connects,dojo.disconnect); 
	},

	movePoint: function(){

		// make sure lastPoint is a legal point to drag to
		var p = this.lastPoint - this.screenToClientOffset;

		var a = p - this.dragOffset;
		a = this.legaliseSplitPoint(a);
		p = a + this.dragOffset;

		this.lastPoint = p + this.screenToClientOffset;
	},

	legaliseSplitPoint: function(a){

		a += this.sizingSplitter.position;

		this.isDraggingLeft = !!(a > 0);

		if(!this.activeSizing){
			var min = this.paneBefore.position + this.paneBefore.sizeMin;
			if(a < min){
				a = min;
			}

			var max = this.paneAfter.position + (this.paneAfter.sizeActual - (this.sizerWidth + this.paneAfter.sizeMin));
			if(a > max){
				a = max;
			}
		}

		a -= this.sizingSplitter.position;

		this._checkSizes();

		return a;
	},

	_updateSize: function(){
	//FIXME: sometimes this.lastPoint is NaN
		var pos = this.lastPoint - this.dragOffset - this.originPos;

		var start_region = this.paneBefore.position;
		var end_region   = this.paneAfter.position + this.paneAfter.sizeActual;

		this.paneBefore.sizeActual = pos - start_region;
		this.paneAfter.position	= pos + this.sizerWidth;
		this.paneAfter.sizeActual  = end_region - this.paneAfter.position;

		dojo.forEach(this.getChildren(), function(child){
			child.sizeShare = child.sizeActual;
		});

		if(this._started){
			this.layout();
		}
	},

	_showSizingLine: function(){

		this._moveSizingLine();

		dojo.marginBox(this.virtualSizer,
			this.isHorizontal ? { w: this.sizerWidth, h: this.paneHeight } : { w: this.paneWidth, h: this.sizerWidth });

		this.virtualSizer.style.display = 'block';
	},

	_hideSizingLine: function(){
		this.virtualSizer.style.display = 'none';
	},

	_moveSizingLine: function(){
		var pos = (this.lastPoint - this.startPoint) + this.sizingSplitter.position;
		dojo.style(this.virtualSizer,(this.isHorizontal ? "left" : "top"),pos+"px");
		// this.virtualSizer.style[ this.isHorizontal ? "left" : "top" ] = pos + 'px'; // FIXME: remove this line if the previous is better
	},

	_getCookieName: function(i){
		return this.id + "_" + i;
	},

	_restoreState: function(){
		dojo.forEach(this.getChildren(), function(child, i){
			var cookieName = this._getCookieName(i);
			var cookieValue = dojo.cookie(cookieName);
			if(cookieValue){
				var pos = parseInt(cookieValue);
				if(typeof pos == "number"){
					child.sizeShare = pos;
				}
			}
		}, this);
	},

	_saveState: function(){
		dojo.forEach(this.getChildren(), function(child, i){
			dojo.cookie(this._getCookieName(i), child.sizeShare);
		}, this);
	}
});

// These arguments can be specified for the children of a SplitContainer.
// Since any widget can be specified as a SplitContainer child, mix them
// into the base widget class.  (This is a hack, but it's effective.)
dojo.extend(dijit._Widget, {
	// sizeMin: Integer
	//	Minimum size (width or height) of a child of a SplitContainer.
	//	The value is relative to other children's sizeShare properties.
	sizeMin: 10,

	// sizeShare: Integer
	//	Size (width or height) of a child of a SplitContainer.
	//	The value is relative to other children's sizeShare properties.
	//	For example, if there are two children and each has sizeShare=10, then
	//	each takes up 50% of the available space.
	sizeShare: 10
});

}

if(!dojo._hasResource["dijit.Menu"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Menu"] = true;
dojo.provide("dijit.Menu");





dojo.declare(
	"dijit.Menu",
	[dijit._Widget, dijit._Templated, dijit._KeyNavContainer],
{
	constructor: function() {
		this._bindings = [];
	},

	templateString:
			'<table class="dijit dijitMenu dijitReset dijitMenuTable" waiRole="menu" dojoAttachEvent="onkeypress:_onKeyPress">' +
				'<tbody class="dijitReset" dojoAttachPoint="containerNode"></tbody>'+
			'</table>',

	// targetNodeIds: String[]
	//	Array of dom node ids of nodes to attach to.
	//	Fill this with nodeIds upon widget creation and it becomes context menu for those nodes.
	targetNodeIds: [],

	// contextMenuForWindow: Boolean
	//	if true, right clicking anywhere on the window will cause this context menu to open;
	//	if false, must specify targetNodeIds
	contextMenuForWindow: false,

	// parentMenu: Widget
	// pointer to menu that displayed me
	parentMenu: null,

	// popupDelay: Integer
	//	number of milliseconds before hovering (without clicking) causes the popup to automatically open
	popupDelay: 500,

	// _contextMenuWithMouse: Boolean
	//	used to record mouse and keyboard events to determine if a context
	//	menu is being opened with the keyboard or the mouse
	_contextMenuWithMouse: false,

	postCreate: function(){
		if(this.contextMenuForWindow){
			this.bindDomNode(dojo.body());
		}else{
			dojo.forEach(this.targetNodeIds, this.bindDomNode, this);
		}
		this.connectKeyNavHandlers([dojo.keys.UP_ARROW], [dojo.keys.DOWN_ARROW]);
	},

	startup: function(){
		dojo.forEach(this.getChildren(), function(child){ child.startup(); });
		this.startupKeyNavChildren();
	},

	onExecute: function(){
		// summary: attach point for notification about when a menu item has been executed
	},

	onCancel: function(/*Boolean*/ closeAll){
		// summary: attach point for notification about when the user cancels the current menu
	},

	_moveToPopup: function(/*Event*/ evt){
		if(this.focusedChild && this.focusedChild.popup && !this.focusedChild.disabled){
			this.focusedChild._onClick(evt);
		}
	},

	_onKeyPress: function(/*Event*/ evt){
		// summary
		//	Handle keyboard based menu navigation.
		if(evt.ctrlKey || evt.altKey){ return; }

		switch(evt.keyCode){
			case dojo.keys.RIGHT_ARROW:
				this._moveToPopup(evt);
				dojo.stopEvent(evt);
				break;
			case dojo.keys.LEFT_ARROW:
				if(this.parentMenu){
					this.onCancel(false);
				}else{
					dojo.stopEvent(evt);
				}
				break;
		}
	},

	onItemHover: function(/*MenuItem*/ item){
		this.focusChild(item);

		if(this.focusedChild.popup && !this.focusedChild.disabled && !this.hover_timer){
			this.hover_timer = setTimeout(dojo.hitch(this, "_openPopup"), this.popupDelay);
		}
	},

	_onChildBlur: function(item){
		// Close all popups that are open and descendants of this menu
		dijit.popup.close(item.popup);
		item._blur();
		this._stopPopupTimer();
	},

	onItemUnhover: function(/*MenuItem*/ item){
	},

	_stopPopupTimer: function(){
		if(this.hover_timer){
			clearTimeout(this.hover_timer);
			this.hover_timer = null;
		}
	},

	_getTopMenu: function(){
		for(var top=this; top.parentMenu; top=top.parentMenu);
		return top;
	},

	onItemClick: function(/*Widget*/ item){
		// summary: user defined function to handle clicks on an item
		// summary: internal function for clicks
		if(item.disabled){ return false; }

		if(item.popup){
			if(!this.is_open){
				this._openPopup();
			}
		}else{
			// before calling user defined handler, close hierarchy of menus
			// and restore focus to place it was when menu was opened
			this.onExecute();

			// user defined handler for click
			item.onClick();
		}
	},

	// thanks burstlib!
	_iframeContentWindow: function(/* HTMLIFrameElement */iframe_el) {
		//	summary
		//	returns the window reference of the passed iframe
		var win = dijit.getDocumentWindow(dijit.Menu._iframeContentDocument(iframe_el)) ||
			// Moz. TODO: is this available when defaultView isn't?
			dijit.Menu._iframeContentDocument(iframe_el)['__parent__'] ||
			(iframe_el.name && document.frames[iframe_el.name]) || null;
		return win;	//	Window
	},

	_iframeContentDocument: function(/* HTMLIFrameElement */iframe_el){
		//	summary
		//	returns a reference to the document object inside iframe_el
		var doc = iframe_el.contentDocument // W3
			|| (iframe_el.contentWindow && iframe_el.contentWindow.document) // IE
			|| (iframe_el.name && document.frames[iframe_el.name] && document.frames[iframe_el.name].document)
			|| null;
		return doc;	//	HTMLDocument
	},

	bindDomNode: function(/*String|DomNode*/ node){
		// summary: attach menu to given node
		node = dojo.byId(node);

		//TODO: this is to support context popups in Editor.  Maybe this shouldn't be in dijit.Menu
		var win = dijit.getDocumentWindow(node.ownerDocument);
		if(node.tagName.toLowerCase()=="iframe"){
			win = this._iframeContentWindow(node);
			node = dojo.withGlobal(win, dojo.body);
		}

		// to capture these events at the top level,
		// attach to document, not body
		var cn = (node == dojo.body() ? dojo.doc : node);

		node[this.id] = this._bindings.push([
			dojo.connect(cn, "oncontextmenu", this, "_openMyself"),
			dojo.connect(cn, "onkeydown", this, "_contextKey"),
			dojo.connect(cn, "onmousedown", this, "_contextMouse")
		]);
	},

	unBindDomNode: function(/*String|DomNode*/ nodeName){
		// summary: detach menu from given node
		var node = dojo.byId(nodeName);
		var bid = node[this.id]-1, b = this._bindings[bid];
		dojo.forEach(b, dojo.disconnect);
		delete this._bindings[bid];
	},

	_contextKey: function(e){
		this._contextMenuWithMouse = false;
		if (e.keyCode == dojo.keys.F10) {
			dojo.stopEvent(e);
			if (e.shiftKey && e.type=="keydown") {
				// FF: copying the wrong property from e will cause the system
				// context menu to appear in spite of stopEvent. Don't know
				// exactly which properties cause this effect.
				var _e = { target: e.target, pageX: e.pageX, pageY: e.pageY };
				_e.preventDefault = _e.stopPropagation = function(){};
				// IE: without the delay, focus work in "open" causes the system
				// context menu to appear in spite of stopEvent.
				window.setTimeout(dojo.hitch(this, function(){ this._openMyself(_e); }), 1);
			}
		}
	},

	_contextMouse: function(e){
		this._contextMenuWithMouse = true;
	},

	_openMyself: function(/*Event*/ e){
		// summary:
		//		Internal function for opening myself when the user
		//		does a right-click or something similar

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
		}else{
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
	},

	onOpen: function(/*Event*/ e){
		// summary
		//		Open menu relative to the mouse
		this.isShowingNow = true;
	},

	onClose: function(){
		// summary: callback when this menu is closed
		this._stopPopupTimer();
		this.parentMenu = null;
		this.isShowingNow = false;
		this.currentPopup = null;
		if(this.focusedChild){
			this._onChildBlur(this.focusedChild);
			this.focusedChild = null;
		}
	},

	_openPopup: function(){
		// summary: open the popup to the side of the current menu item
		this._stopPopupTimer();
		var from_item = this.focusedChild;
		var popup = from_item.popup;

		if(popup.isShowingNow){ return; }
		popup.parentMenu = this;
		var self = this;
		dijit.popup.open({
			parent: this,
			popup: popup,
			around: from_item.arrowCell,
			orient: this.isLeftToRight() ? {'TR': 'TL', 'TL': 'TR'} : {'TL': 'TR', 'TR': 'TL'},
			onCancel: function(){
				// called when the child menu is canceled
				dijit.popup.close(popup);
				from_item.focus();	// put focus back on my node
				self.currentPopup = null;
			}
		});

		this.currentPopup = popup;

		if(popup.focus){
			popup.focus();
		}
	}
}
);

dojo.declare(
	"dijit.MenuItem",
	[dijit._Widget, dijit._Templated, dijit._Contained],
{
	// summary
	//	A line item in a Menu2

	// Make 3 columns
	//   icon, label, and expand arrow (BiDi-dependent) indicating sub-menu
	templateString:
		 '<tr class="dijitReset dijitMenuItem"'
		+'dojoAttachEvent="onmouseenter:_onHover,onmouseleave:_onUnhover,ondijitclick:_onClick">'
		+'<td class="dijitReset"><div class="dijitMenuItemIcon ${iconClass}" dojoAttachPoint="iconNode" ></div></td>'
		+'<td tabIndex="-1" class="dijitReset dijitMenuItemLabel" dojoAttachPoint="containerNode" waiRole="menuitem"></td>'
		+'<td class="dijitReset" dojoAttachPoint="arrowCell">'
			+'<div class="dijitMenuExpand" dojoAttachPoint="expand" style="display:none">'
			+'<span class="dijitInline dijitArrowNode dijitMenuExpandInner">+</span>'
			+'</div>'
		+'</td>'
		+'</tr>',

	// label: String
	//	menu text
	label: '',

	// iconClass: String
	//	class to apply to div in button to make it display an icon
	iconClass: "",

	// disabled: Boolean
	//  if true, the menu item is disabled
	//  if false, the menu item is enabled
	disabled: false,

	postCreate: function(){
		dojo.setSelectable(this.domNode, false);
		this.setDisabled(this.disabled);
		if(this.label){
			this.containerNode.innerHTML=this.label;
		}
	},

	_onHover: function(){
		// summary: callback when mouse is moved onto menu item
		this.getParent().onItemHover(this);
	},

	_onUnhover: function(){
		// summary: callback when mouse is moved off of menu item
		// if we are unhovering the currently selected item
		// then unselect it
		this.getParent().onItemUnhover(this);
	},

	_onClick: function(evt){
		this.getParent().onItemClick(this);
		dojo.stopEvent(evt);
	},

	onClick: function() {
		// summary
		//	User defined function to handle clicks
	},

	focus: function(){
		dojo.addClass(this.domNode, 'dijitMenuItemHover');
		try{
			dijit.focus(this.containerNode);
		}catch(e){
			// this throws on IE (at least) in some scenarios
		}
	},

	_blur: function(){
		dojo.removeClass(this.domNode, 'dijitMenuItemHover');
	},

	setDisabled: function(/*Boolean*/ value){
		// summary: enable or disable this menu item
		this.disabled = value;
		dojo[value ? "addClass" : "removeClass"](this.domNode, 'dijitMenuItemDisabled');
		dijit.setWaiState(this.containerNode, 'disabled', value ? 'true' : 'false');
	}
});

dojo.declare(
	"dijit.PopupMenuItem",
	dijit.MenuItem,
{
	_fillContent: function(){
		// my inner HTML contains both the menu item text and a popup widget, like
		// <div dojoType="dijit.PopupMenuItem">
		//		<span>pick me</span>
		//		<popup> ... </popup>
		// </div>
		// the first part holds the menu item text and the second part is the popup
		if(this.srcNodeRef){
			var nodes = dojo.query("*", this.srcNodeRef);
			dijit.PopupMenuItem.superclass._fillContent.call(this, nodes[0]);

			// save pointer to srcNode so we can grab the drop down widget after it's instantiated
			this.dropDownContainer = this.srcNodeRef;
		}
	},

	startup: function(){
		// we didn't copy the dropdown widget from the this.srcNodeRef, so it's in no-man's
		// land now.  move it to document.body.
		if(!this.popup){
			var node = dojo.query("[widgetId]", this.dropDownContainer)[0];
			this.popup = dijit.byNode(node);
		}
		dojo.body().appendChild(this.popup.domNode);

		this.popup.domNode.style.display="none";
		dojo.addClass(this.expand, "dijitMenuExpandEnabled");
		dojo.style(this.expand, "display", "");
		dijit.setWaiState(this.containerNode, "haspopup", "true");
	}
});

dojo.declare(
	"dijit.MenuSeparator",
	[dijit._Widget, dijit._Templated, dijit._Contained],
{
	// summary
	//	A line between two menu items

	templateString: '<tr class="dijitMenuSeparator"><td colspan=3>'
			+'<div class="dijitMenuSeparatorTop"></div>'
			+'<div class="dijitMenuSeparatorBottom"></div>'
			+'</td></tr>',

	postCreate: function(){
		dojo.setSelectable(this.domNode, false);
	},
	
	isFocusable: function(){
		// summary:
		//		over ride to always return false
		return false;
	}
});

}

if(!dojo._hasResource["dijit.form.TextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.TextBox"] = true;
dojo.provide("dijit.form.TextBox");



dojo.declare(
	"dijit.form.TextBox",
	dijit.form._FormWidget,
	{
		// summary:
		//		A generic textbox field.
		//		Serves as a base class to derive more specialized functionality in subclasses.

		//	trim: Boolean
		//		Removes leading and trailing whitespace if true.  Default is false.
		trim: false,

		//	uppercase: Boolean
		//		Converts all characters to uppercase if true.  Default is false.
		uppercase: false,

		//	lowercase: Boolean
		//		Converts all characters to lowercase if true.  Default is false.
		lowercase: false,

		//	propercase: Boolean
		//		Converts the first character of each word to uppercase if true.
		propercase: false,

		// maxLength: String
		//		HTML INPUT tag maxLength declaration.
		maxLength: "",

		templateString:"<input class=\"dojoTextBox\" dojoAttachPoint='textbox,focusNode' name=\"${name}\"\r\n\tdojoAttachEvent='onmouseenter:_onMouse,onmouseleave:_onMouse,onfocus:_onMouse,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress'\r\n\tautocomplete=\"off\" type=\"${type}\"\r\n\t/>\r\n",
		baseClass: "dijitTextBox",

		attributeMap: dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),
			{maxLength:"focusNode"}),

		getDisplayedValue: function(){
			return this.filter(this.textbox.value);
		},

		getValue: function(){
			return this.parse(this.getDisplayedValue(), this.constraints);
		},

		setValue: function(value, /*Boolean, optional*/ priorityChange, /*String, optional*/ formattedValue){
			var filteredValue = this.filter(value);
			if((typeof filteredValue == typeof value) && (formattedValue == null || formattedValue == undefined)){
				formattedValue = this.format(filteredValue, this.constraints);
			}
			if(formattedValue != null && formattedValue != undefined){
				this.textbox.value = formattedValue;
			}
			dijit.form.TextBox.superclass.setValue.call(this, filteredValue, priorityChange);
		},

		setDisplayedValue: function(/*String*/value){
			this.textbox.value = value;
			this.setValue(this.getValue(), true);
		},

		forWaiValuenow: function(){
			return this.getDisplayedValue();
		},

		format: function(/* String */ value, /* Object */ constraints){
			// summary: Replacable function to convert a value to a properly formatted string
			return ((value == null || value == undefined) ? "" : (value.toString ? value.toString() : value));
		},

		parse: function(/* String */ value, /* Object */ constraints){
			// summary: Replacable function to convert a formatted string to a value
			return value;
		},

		postCreate: function(){
			// setting the value here is needed since value="" in the template causes "undefined"
			// and setting in the DOM (instead of the JS object) helps with form reset actions
			this.textbox.setAttribute("value", this.getDisplayedValue());
			this.inherited('postCreate', arguments);

			if(this.srcNodeRef){
				dojo.style(this.textbox, "cssText", this.style);
				this.textbox.className += " " + this["class"];
			}
			this._layoutHack();
		},

		_layoutHack: function(){
			// summary: work around table sizing bugs on FF2 by forcing redraw
			if(dojo.isFF == 2 && this.domNode.tagName=="TABLE"){
				var node=this.domNode;
				var old = node.style.opacity;
				node.style.opacity = "0.999";
				setTimeout(function(){
					node.style.opacity = old;
				}, 0);
			}			
		},

		filter: function(val){
			// summary: Apply various filters to textbox value
			if(val == undefined || val == null){ return ""; }
			else if(typeof val != "string"){ return val; }
			if(this.trim){
				val = dojo.trim(val);
			}
			if(this.uppercase){
				val = val.toUpperCase();
			}
			if(this.lowercase){
				val = val.toLowerCase();
			}
			if(this.propercase){
				val = val.replace(/[^\s]+/g, function(word){
					return word.substring(0,1).toUpperCase() + word.substring(1);
				});
			}
			return val;
		},

		// event handlers, you can over-ride these in your own subclasses
		_onBlur: function(){
			this.setValue(this.getValue(), (this.isValid ? this.isValid() : true));
		},

		onkeyup: function(){
			// TODO: it would be nice to massage the value (ie: automatic uppercase, etc) as the user types
			// but this messes up the cursor position if you are typing into the middle of a word, and
			// also trimming doesn't work correctly (it prevents spaces between words too!)
			// this.setValue(this.getValue());
		}
	}
);

}

if(!dojo._hasResource["dijit.Tooltip"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Tooltip"] = true;
dojo.provide("dijit.Tooltip");




dojo.declare(
	"dijit._MasterTooltip",
	[dijit._Widget, dijit._Templated],
	{
		// summary
		//		Internal widget that holds the actual tooltip markup,
		//		which occurs once per page.
		//		Called by Tooltip widgets which are just containers to hold
		//		the markup

		// duration: Integer
		//		Milliseconds to fade in/fade out
		duration: 200,

		templateString:"<div class=\"dijitTooltip dijitTooltipLeft\" id=\"dojoTooltip\">\r\n\t<div class=\"dijitTooltipContainer dijitTooltipContents\" dojoAttachPoint=\"containerNode\" waiRole='alert'></div>\r\n\t<div class=\"dijitTooltipConnector\"></div>\r\n</div>\r\n",

		postCreate: function(){
			dojo.body().appendChild(this.domNode);

			this.bgIframe = new dijit.BackgroundIframe(this.domNode);

			// Setup fade-in and fade-out functions.
			this.fadeIn = dojo.fadeIn({ node: this.domNode, duration: this.duration, onEnd: dojo.hitch(this, "_onShow") });
			this.fadeOut = dojo.fadeOut({ node: this.domNode, duration: this.duration, onEnd: dojo.hitch(this, "_onHide") });

		},

		show: function(/*String*/ innerHTML, /*DomNode*/ aroundNode){
			// summary:
			//	Display tooltip w/specified contents to right specified node
			//	(To left if there's no space on the right, or if LTR==right)

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
			var align = this.isLeftToRight() ? {'BR': 'BL', 'BL': 'BR'} : {'BL': 'BR', 'BR': 'BL'};
			var pos = dijit.placeOnScreenAroundElement(this.domNode, aroundNode, align);
			this.domNode.className="dijitTooltip dijitTooltip" + (pos.corner=='BL' ? "Right" : "Left");//FIXME: might overwrite class

			// show it
			dojo.style(this.domNode, "opacity", 0);
			this.fadeIn.play();
			this.isShowingNow = true;
			this.aroundNode = aroundNode;
		},

		_onShow: function(){
			if(dojo.isIE){
				// the arrow won't show up on a node w/an opacity filter
				this.domNode.style.filter="";
			}
		},

		hide: function(aroundNode){
			// summary: hide the tooltip
			if(!this.aroundNode || this.aroundNode !== aroundNode){
				return;
			}
			if(this._onDeck){
				// this hide request is for a show() that hasn't even started yet;
				// just cancel the pending show()
				this._onDeck=null;
				return;
			}
			this.fadeIn.stop();
			this.isShowingNow = false;
			this.aroundNode = null;
			this.fadeOut.play();
		},

		_onHide: function(){
			this.domNode.style.cssText="";	// to position offscreen again
			if(this._onDeck){
				// a show request has been queued up; do it now
				this.show.apply(this, this._onDeck);
				this._onDeck=null;
			}
		}

	}
);

dijit.showTooltip = function(/*String*/ innerHTML, /*DomNode*/ aroundNode){
	// summary:
	//	Display tooltip w/specified contents to right specified node
	//	(To left if there's no space on the right, or if LTR==right)
	if(!dijit._masterTT){ dijit._masterTT = new dijit._MasterTooltip(); }
	return dijit._masterTT.show(innerHTML, aroundNode);
};

dijit.hideTooltip = function(aroundNode){
	// summary: hide the tooltip
	if(!dijit._masterTT){ dijit._masterTT = new dijit._MasterTooltip(); }
	return dijit._masterTT.hide(aroundNode);
};

dojo.declare(
	"dijit.Tooltip",
	dijit._Widget,
	{
		// summary
		//		Pops up a tooltip (a help message) when you hover over a node.

		// label: String
		//		Text to display in the tooltip.
		//		Specified as innerHTML when creating the widget from markup.
		label: "",

		// showDelay: Integer
		//		Number of milliseconds to wait after hovering over/focusing on the object, before
		//		the tooltip is displayed.
		showDelay: 400,

		// connectId: String[]
		//		Id(s) of domNodes to attach the tooltip to.
		//		When user hovers over any of the specified dom nodes, the tooltip will appear.
		connectId: [],

		postCreate: function(){
			if(this.srcNodeRef){
				this.srcNodeRef.style.display = "none";
			}

			this._connectNodes = [];
			
			dojo.forEach(this.connectId, function(id) {
				var node = dojo.byId(id);
				if (node) {
					this._connectNodes.push(node);
					dojo.forEach(["onMouseOver", "onMouseOut", "onFocus", "onBlur", "onHover", "onUnHover"], function(event){
						this.connect(node, event.toLowerCase(), "_"+event);
					}, this);
					if(dojo.isIE){
						// BiDi workaround
						node.style.zoom = 1;
					}
				}
			}, this);
		},

		_onMouseOver: function(/*Event*/ e){
			this._onHover(e);
		},

		_onMouseOut: function(/*Event*/ e){
			if(dojo.isDescendant(e.relatedTarget, e.target)){
				// false event; just moved from target to target child; ignore.
				return;
			}
			this._onUnHover(e);
		},

		_onFocus: function(/*Event*/ e){
			this._focus = true;
			this._onHover(e);
		},
		
		_onBlur: function(/*Event*/ e){
			this._focus = false;
			this._onUnHover(e);
		},

		_onHover: function(/*Event*/ e){
			if(!this._showTimer){
				var target = e.target;
				this._showTimer = setTimeout(dojo.hitch(this, function(){this.open(target)}), this.showDelay);
			}
		},

		_onUnHover: function(/*Event*/ e){
			// keep a tooltip open if the associated element has focus
			if(this._focus){ return; }
			if(this._showTimer){
				clearTimeout(this._showTimer);
				delete this._showTimer;
			}
			this.close();
		},

		open: function(/*DomNode*/ target){
 			// summary: display the tooltip; usually not called directly.
			target = target || this._connectNodes[0];
			if(!target){ return; }

			if(this._showTimer){
				clearTimeout(this._showTimer);
				delete this._showTimer;
			}
			dijit.showTooltip(this.label || this.domNode.innerHTML, target);
			
			this._connectNode = target;
		},

		close: function(){
			// summary: hide the tooltip; usually not called directly.
			dijit.hideTooltip(this._connectNode);
			delete this._connectNode;
			if(this._showTimer){
				clearTimeout(this._showTimer);
				delete this._showTimer;
			}
		},

		uninitialize: function(){
			this.close();
		}
	}
);

}

if(!dojo._hasResource["dijit.form.ValidationTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.ValidationTextBox"] = true;
dojo.provide("dijit.form.ValidationTextBox");








dojo.declare(
	"dijit.form.ValidationTextBox",
	dijit.form.TextBox,
	{
		// summary:
		//		A subclass of TextBox.
		//		Over-ride isValid in subclasses to perform specific kinds of validation.

		templateString:"<table style=\"display: -moz-inline-stack;\" class=\"dijit dijitReset dijitInlineTable\" cellspacing=\"0\" cellpadding=\"0\"\r\n\tid=\"widget_${id}\" name=\"${name}\"\r\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\r\n\t><tr class=\"dijitReset\"\r\n\t\t><td class=\"dijitReset dijitInputField\" width=\"100%\"\r\n\t\t\t><input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete=\"off\"\r\n\t\t\ttype='${type}' name='${name}'\r\n\t\t/></td\r\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\r\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div><div class='dijitValidationIconText'>&Chi;</div\r\n\t\t></td\r\n\t></tr\r\n></table>\r\n",
		baseClass: "dijitTextBox",

		// default values for new subclass properties
		// required: Boolean
		//		Can be true or false, default is false.
		required: false,
		// promptMessage: String
		//		Hint string
		promptMessage: "",
		// invalidMessage: String
		// 		The message to display if value is invalid.
		invalidMessage: "$_unset_$", // read from the message file if not overridden
		// constraints: Object
		//		user-defined object needed to pass parameters to the validator functions
		constraints: {},
		// regExp: String
		//		regular expression string used to validate the input
		//		Do not specify both regExp and regExpGen
		regExp: ".*",
		// regExpGen: Function
		//		user replaceable function used to generate regExp when dependent on constraints
		//		Do not specify both regExp and regExpGen
		regExpGen: function(constraints){ return this.regExp; },
		
		// state: String
		//		Shows current state (ie, validation result) of input (Normal, Warning, or Error)
		state: "",

		setValue: function(){
			this.inherited('setValue', arguments);
			this.validate(false);
		},

		validator: function(value,constraints){
			// summary: user replaceable function used to validate the text input against the regular expression.
			return (new RegExp("^(" + this.regExpGen(constraints) + ")"+(this.required?"":"?")+"$")).test(value) &&
				(!this.required || !this._isEmpty(value)) &&
				(this._isEmpty(value) || this.parse(value, constraints) !== null);
		},

		isValid: function(/* Boolean*/ isFocused){
			// summary: Need to over-ride with your own validation code in subclasses
			return this.validator(this.textbox.value, this.constraints);
		},

		_isEmpty: function(value){
			// summary: Checks for whitespace
			return /^\s*$/.test(value); // Boolean
		},

		getErrorMessage: function(/* Boolean*/ isFocused){
			// summary: return an error message to show if appropriate
			return this.invalidMessage;
		},

		getPromptMessage: function(/* Boolean*/ isFocused){
			// summary: return a hint to show if appropriate
			return this.promptMessage;
		},

		validate: function(/* Boolean*/ isFocused){
			// summary:
			//		Called by oninit, onblur, and onkeypress.
			// description:
			//		Show missing or invalid messages if appropriate, and highlight textbox field.
			var message = "";
			var isValid = this.isValid(isFocused);
			var isEmpty = this._isEmpty(this.textbox.value);
			this.state = (isValid || (!this._hasBeenBlurred && isEmpty)) ? "" : "Error";
			this._setStateClass();
			dijit.setWaiState(this.focusNode, "invalid", (isValid? "false" : "true"));
			if(isFocused){
				if(isEmpty){
					message = this.getPromptMessage(true);
				}
				if(!message && !isValid){
					message = this.getErrorMessage(true);
				}
			}
			this._displayMessage(message);
		},

		// currently displayed message
		_message: "",

		_displayMessage: function(/*String*/ message){
			if(this._message == message){ return; }
			this._message = message;
			this.displayMessage(message);
		},

		displayMessage: function(/*String*/ message){
			// summary:
			//		User overridable method to display validation errors/hints.
			//		By default uses a tooltip.
			if(message){
				dijit.showTooltip(message, this.domNode);
			}else{
				dijit.hideTooltip(this.domNode);
			}
		},

		_hasBeenBlurred: false,

		_onBlur: function(evt){
			this._hasBeenBlurred = true;
			this.validate(false);
			this.inherited('_onBlur', arguments);
		},

		onfocus: function(evt){
			// TODO: change to _onFocus?
			this.validate(true);
			this._onMouse(evt);	// update CSS classes
		},

		onkeyup: function(evt){
			this.onfocus(evt);
		},

		//////////// INITIALIZATION METHODS ///////////////////////////////////////
		constructor: function(){
			this.constraints = {};
		},

		postMixInProperties: function(){
			this.inherited('postMixInProperties', arguments);
			this.constraints.locale=this.lang;
			this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
			if(this.invalidMessage == "$_unset_$"){ this.invalidMessage = this.messages.invalidMessage; }
			var p = this.regExpGen(this.constraints);
			this.regExp = p;
			// make value a string for all types so that form reset works well
		}
	}
);

dojo.declare(
	"dijit.form.MappedTextBox",
	dijit.form.ValidationTextBox,
	{
		// summary:
		//		A subclass of ValidationTextBox.
		//		Provides a hidden input field and a serialize method to override

		serialize: function(val, /*Object?*/options){
			// summary: user replaceable function used to convert the getValue() result to a String
			return (val.toString ? val.toString() : "");
		},

		toString: function(){
			// summary: display the widget as a printable string using the widget's value
			var val = this.filter(this.getValue());
			return (val!=null) ? ((typeof val == "string") ? val : this.serialize(val, this.constraints)) : "";
		},

		validate: function(){
			this.valueNode.value = this.toString();
			this.inherited('validate', arguments);
		},

		postCreate: function(){
			var textbox = this.textbox;
			var valueNode = (this.valueNode = document.createElement("input"));
			valueNode.setAttribute("type", textbox.type);
			valueNode.setAttribute("value", this.toString());
			dojo.style(valueNode, "display", "none");
			valueNode.name = this.textbox.name;
			this.textbox.name = "_" + this.textbox.name + "_displayed_";
			this.textbox.removeAttribute("name");
			dojo.place(valueNode, textbox, "after");

			this.inherited('postCreate', arguments);
		}
	}
);

dojo.declare(
	"dijit.form.RangeBoundTextBox",
	dijit.form.MappedTextBox,
	{
		// summary:
		//		A subclass of MappedTextBox.
		//		Tests for a value out-of-range
		/*===== contraints object:
		// min: Number
		//		Minimum signed value.  Default is -Infinity
		min: undefined,
		// max: Number
		//		Maximum signed value.  Default is +Infinity
		max: undefined,
		=====*/

		// rangeMessage: String
		//		The message to display if value is out-of-range
		rangeMessage: "",

		compare: function(val1, val2){
			// summary: compare 2 values
			return val1 - val2;
		},

		rangeCheck: function(/* Number */ primitive, /* Object */ constraints){
			// summary: user replaceable function used to validate the range of the numeric input value
			var isMin = (typeof constraints.min != "undefined");
			var isMax = (typeof constraints.max != "undefined");
			if(isMin || isMax){
				return (!isMin || this.compare(primitive,constraints.min) >= 0) &&
					(!isMax || this.compare(primitive,constraints.max) <= 0);
			}else{ return true; }
		},

		isInRange: function(/* Boolean*/ isFocused){
			// summary: Need to over-ride with your own validation code in subclasses
			return this.rangeCheck(this.getValue(), this.constraints);
		},

		isValid: function(/* Boolean*/ isFocused){
			return this.inherited('isValid', arguments) &&
				((this._isEmpty(this.textbox.value) && !this.required) || this.isInRange(isFocused));
		},

		getErrorMessage: function(/* Boolean*/ isFocused){
			if(dijit.form.RangeBoundTextBox.superclass.isValid.call(this, false) && !this.isInRange(isFocused)){ return this.rangeMessage; }
			else{ return this.inherited('getErrorMessage', arguments); }
		},

		postMixInProperties: function(){
			this.inherited('postMixInProperties', arguments);
			if(!this.rangeMessage){
				this.messages = dojo.i18n.getLocalization("dijit.form", "validate", this.lang);
				this.rangeMessage = this.messages.rangeMessage;
			}
		},

		postCreate: function(){
			this.inherited('postCreate', arguments);
			if(typeof this.constraints.min != "undefined"){
				dijit.setWaiState(this.focusNode, "valuemin", this.constraints.min);
			}
			if(typeof this.constraints.max != "undefined"){
				dijit.setWaiState(this.focusNode, "valuemax", this.constraints.max);
			}
		}
	}
);

}

if(!dojo._hasResource["dojo.data.util.filter"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.data.util.filter"] = true;
dojo.provide("dojo.data.util.filter");

dojo.data.util.filter.patternToRegExp = function(/*String*/pattern, /*boolean?*/ ignoreCase){
	//	summary:  
	//		Helper function to convert a simple pattern to a regular expression for matching.
	//	description:
	//		Returns a regular expression object that conforms to the defined conversion rules.
	//		For example:  
	//			ca*   -> /^ca.*$/
	//			*ca*  -> /^.*ca.*$/
	//			*c\*a*  -> /^.*c\*a.*$/
	//			*c\*a?*  -> /^.*c\*a..*$/
	//			and so on.
	//
	//	pattern: string
	//		A simple matching pattern to convert that follows basic rules:
	//			* Means match anything, so ca* means match anything starting with ca
	//			? Means match single character.  So, b?b will match to bob and bab, and so on.
	//      	\ is an escape character.  So for example, \* means do not treat * as a match, but literal character *.
	//				To use a \ as a character in the string, it must be escaped.  So in the pattern it should be 
	//				represented by \\ to be treated as an ordinary \ character instead of an escape.
	//
	//	ignoreCase:
	//		An optional flag to indicate if the pattern matching should be treated as case-sensitive or not when comparing
	//		By default, it is assumed case sensitive.

	var rxp = "^";
	var c = null;
	for(var i = 0; i < pattern.length; i++){
		c = pattern.charAt(i);
		switch (c) {
			case '\\':
				rxp += c;
				i++;
				rxp += pattern.charAt(i);
				break;
			case '*':
				rxp += ".*"; break;
			case '?':
				rxp += "."; break;
			case '$':
			case '^':
			case '/':
			case '+':
			case '.':
			case '|':
			case '(':
			case ')':
			case '{':
			case '}':
			case '[':
			case ']':
				rxp += "\\"; //fallthrough
			default:
				rxp += c;
		}
	}
	rxp += "$";
	if(ignoreCase){
		return new RegExp(rxp,"i"); //RegExp
	}else{
		return new RegExp(rxp); //RegExp
	}
	
};

}

if(!dojo._hasResource["dojo.data.util.sorter"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.data.util.sorter"] = true;
dojo.provide("dojo.data.util.sorter");

dojo.data.util.sorter.basicComparator = function(	/*anything*/ a, 
													/*anything*/ b){
	//	summary:  
	//		Basic comparision function that compares if an item is greater or less than another item
	//	description:  
	//		returns 1 if a > b, -1 if a < b, 0 if equal.
	//		undefined values are treated as larger values so that they're pushed to the end of the list.

	var ret = 0;
	if(a > b || typeof a === "undefined" || a === null){
		ret = 1;
	}else if(a < b || typeof b === "undefined" || b === null){
		ret = -1;
	}
	return ret; //int, {-1,0,1}
};

dojo.data.util.sorter.createSortFunction = function(	/* attributes array */sortSpec,
														/*dojo.data.core.Read*/ store){
	//	summary:  
	//		Helper function to generate the sorting function based off the list of sort attributes.
	//	description:  
	//		The sort function creation will look for a property on the store called 'comparatorMap'.  If it exists
	//		it will look in the mapping for comparisons function for the attributes.  If one is found, it will
	//		use it instead of the basic comparator, which is typically used for strings, ints, booleans, and dates.
	//		Returns the sorting function for this particular list of attributes and sorting directions.
	//
	//	sortSpec: array
	//		A JS object that array that defines out what attribute names to sort on and whether it should be descenting or asending.
	//		The objects should be formatted as follows:
	//		{
	//			attribute: "attributeName-string" || attribute,
	//			descending: true|false;   // Default is false.
	//		}
	//	store: object
	//		The datastore object to look up item values from.
	//
	var sortFunctions=[];   

	function createSortFunction(attr, dir){
		return function(itemA, itemB){
			var a = store.getValue(itemA, attr);
			var b = store.getValue(itemB, attr);
			//See if we have a override for an attribute comparison.
			var comparator = null;
			if(store.comparatorMap){
				if(typeof attr !== "string"){
					 attr = store.getIdentity(attr);
				}
				comparator = store.comparatorMap[attr]||dojo.data.util.sorter.basicComparator;
			}
			comparator = comparator||dojo.data.util.sorter.basicComparator; 
			return dir * comparator(a,b); //int
		};
	}

	for(var i = 0; i < sortSpec.length; i++){
		sortAttribute = sortSpec[i];
		if(sortAttribute.attribute){
			var direction = (sortAttribute.descending) ? -1 : 1;
			sortFunctions.push(createSortFunction(sortAttribute.attribute, direction));
		}
	}

	return function(rowA, rowB){
		var i=0;
		while(i < sortFunctions.length){
			var ret = sortFunctions[i++](rowA, rowB);
			if(ret !== 0){
				return ret;//int
			}
		}
		return 0; //int  
	};  //  Function
};

}

if(!dojo._hasResource["dojo.data.util.simpleFetch"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.data.util.simpleFetch"] = true;
dojo.provide("dojo.data.util.simpleFetch");


dojo.data.util.simpleFetch.fetch = function(/* Object? */ request){
	//	summary:
	//		The simpleFetch mixin is designed to serve as a set of function(s) that can
	//		be mixed into other datastore implementations to accelerate their development.  
	//		The simpleFetch mixin should work well for any datastore that can respond to a _fetchItems() 
	//		call by returning an array of all the found items that matched the query.  The simpleFetch mixin
	//		is not designed to work for datastores that respond to a fetch() call by incrementally
	//		loading items, or sequentially loading partial batches of the result
	//		set.  For datastores that mixin simpleFetch, simpleFetch 
	//		implements a fetch method that automatically handles eight of the fetch()
	//		arguments -- onBegin, onItem, onComplete, onError, start, count, sort and scope
	//		The class mixing in simpleFetch should not implement fetch(),
	//		but should instead implement a _fetchItems() method.  The _fetchItems() 
	//		method takes three arguments, the keywordArgs object that was passed 
	//		to fetch(), a callback function to be called when the result array is
	//		available, and an error callback to be called if something goes wrong.
	//		The _fetchItems() method should ignore any keywordArgs parameters for
	//		start, count, onBegin, onItem, onComplete, onError, sort, and scope.  
	//		The _fetchItems() method needs to correctly handle any other keywordArgs
	//		parameters, including the query parameter and any optional parameters 
	//		(such as includeChildren).  The _fetchItems() method should create an array of 
	//		result items and pass it to the fetchHandler along with the original request object 
	//		-- or, the _fetchItems() method may, if it wants to, create an new request object 
	//		with other specifics about the request that are specific to the datastore and pass 
	//		that as the request object to the handler.
	//
	//		For more information on this specific function, see dojo.data.api.Read.fetch()
	request = request || {};
	if(!request.store){
		request.store = this;
	}
	var self = this;

	var _errorHandler = function(errorData, requestObject){
		if(requestObject.onError){
			var scope = requestObject.scope || dojo.global;
			requestObject.onError.call(scope, errorData, requestObject);
		}
	};

	var _fetchHandler = function(items, requestObject){
		var oldAbortFunction = requestObject.abort || null;
		var aborted = false;

		var startIndex = requestObject.start?requestObject.start:0;
		var endIndex   = requestObject.count?(startIndex + requestObject.count):items.length;

		requestObject.abort = function(){
			aborted = true;
			if(oldAbortFunction){
				oldAbortFunction.call(requestObject);
			}
		};

		var scope = requestObject.scope || dojo.global;
		if(!requestObject.store){
			requestObject.store = self;
		}
		if(requestObject.onBegin){
			requestObject.onBegin.call(scope, items.length, requestObject);
		}
		if(requestObject.sort){
			items.sort(dojo.data.util.sorter.createSortFunction(requestObject.sort, self));
		}
		if(requestObject.onItem){
			for(var i = startIndex; (i < items.length) && (i < endIndex); ++i){
				var item = items[i];
				if(!aborted){
					requestObject.onItem.call(scope, item, requestObject);
				}
			}
		}
		if(requestObject.onComplete && !aborted){
			var subset = null;
			if (!requestObject.onItem) {
				subset = items.slice(startIndex, endIndex);
			}
			requestObject.onComplete.call(scope, subset, requestObject);   
		}
	};
	this._fetchItems(request, _fetchHandler, _errorHandler);
	return request;	// Object
};

}

if(!dojo._hasResource["dojo.data.ItemFileReadStore"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.data.ItemFileReadStore"] = true;
dojo.provide("dojo.data.ItemFileReadStore");





dojo.declare("dojo.data.ItemFileReadStore", null,{
	//	summary:
	//		The ItemFileReadStore implements the dojo.data.api.Read API and reads
	//		data from JSON files that have contents in this format --
	//		{ items: [
	//			{ name:'Kermit', color:'green', age:12, friends:['Gonzo', {_reference:{name:'Fozzie Bear'}}]},
	//			{ name:'Fozzie Bear', wears:['hat', 'tie']},
	//			{ name:'Miss Piggy', pets:'Foo-Foo'}
	//		]}
	//		Note that it can also contain an 'identifer' property that specified which attribute on the items 
	//		in the array of items that acts as the unique identifier for that item.
	//
	constructor: function(/* Object */ keywordParameters){
		//	summary: constructor
		//	keywordParameters: {url: String}
		//	keywordParameters: {data: jsonObject}
		//	keywordParameters: {typeMap: object)
		//		The structure of the typeMap object is as follows:
		//		{
		//			type0: function || object,
		//			type1: function || object,
		//			...
		//			typeN: function || object
		//		}
		//		Where if it is a function, it is assumed to be an object constructor that takes the 
		//		value of _value as the initialization parameters.  If it is an object, then it is assumed
		//		to be an object of general form:
		//		{
		//			type: function, //constructor.
		//			deserialize:	function(value) //The function that parses the value and constructs the object defined by type appropriately.
		//		}
	
		this._arrayOfAllItems = [];
		this._arrayOfTopLevelItems = [];
		this._loadFinished = false;
		this._jsonFileUrl = keywordParameters.url;
		this._jsonData = keywordParameters.data;
		this._datatypeMap = keywordParameters.typeMap || {};
		if(!this._datatypeMap['Date']){
			//If no default mapping for dates, then set this as default.
			//We use the dojo.date.stamp here because the ISO format is the 'dojo way'
			//of generically representing dates.
			this._datatypeMap['Date'] = {
											type: Date,
											deserialize: function(value){
												return dojo.date.stamp.fromISOString(value);
											}
										};
		}
		this._features = {'dojo.data.api.Read':true, 'dojo.data.api.Identity':true};
		this._itemsByIdentity = null;
		this._storeRefPropName = "_S";  // Default name for the store reference to attach to every item.
		this._itemNumPropName = "_0"; // Default Item Id for isItem to attach to every item.
		this._rootItemPropName = "_RI"; // Default Item Id for isItem to attach to every item.
		this._loadInProgress = false;	//Got to track the initial load to prevent duelling loads of the dataset.
		this._queuedFetches = [];
	},
	
	url: "",	// use "" rather than undefined for the benefit of the parser (#3539)

	_assertIsItem: function(/* item */ item){
		//	summary:
		//		This function tests whether the item passed in is indeed an item in the store.
		//	item: 
		//		The item to test for being contained by the store.
		if(!this.isItem(item)){ 
			throw new Error("dojo.data.ItemFileReadStore: Invalid item argument.");
		}
	},

	_assertIsAttribute: function(/* attribute-name-string */ attribute){
		//	summary:
		//		This function tests whether the item passed in is indeed a valid 'attribute' like type for the store.
		//	attribute: 
		//		The attribute to test for being contained by the store.
		if(typeof attribute !== "string"){ 
			throw new Error("dojo.data.ItemFileReadStore: Invalid attribute argument.");
		}
	},

	getValue: function(	/* item */ item, 
						/* attribute-name-string */ attribute, 
						/* value? */ defaultValue){
		//	summary: 
		//		See dojo.data.api.Read.getValue()
		var values = this.getValues(item, attribute);
		return (values.length > 0)?values[0]:defaultValue; // mixed
	},

	getValues: function(/* item */ item, 
						/* attribute-name-string */ attribute){
		//	summary: 
		//		See dojo.data.api.Read.getValues()

		this._assertIsItem(item);
		this._assertIsAttribute(attribute);
		return item[attribute] || []; // Array
	},

	getAttributes: function(/* item */ item){
		//	summary: 
		//		See dojo.data.api.Read.getAttributes()
		this._assertIsItem(item);
		var attributes = [];
		for(var key in item){
			// Save off only the real item attributes, not the special id marks for O(1) isItem.
			if((key !== this._storeRefPropName) && (key !== this._itemNumPropName) && (key !== this._rootItemPropName)){
				attributes.push(key);
			}
		}
		return attributes; // Array
	},

	hasAttribute: function(	/* item */ item,
							/* attribute-name-string */ attribute) {
		//	summary: 
		//		See dojo.data.api.Read.hasAttribute()
		return this.getValues(item, attribute).length > 0;
	},

	containsValue: function(/* item */ item, 
							/* attribute-name-string */ attribute, 
							/* anything */ value){
		//	summary: 
		//		See dojo.data.api.Read.containsValue()
		var regexp = undefined;
		if(typeof value === "string"){
			regexp = dojo.data.util.filter.patternToRegExp(value, false);
		}
		return this._containsValue(item, attribute, value, regexp); //boolean.
	},

	_containsValue: function(	/* item */ item, 
								/* attribute-name-string */ attribute, 
								/* anything */ value,
								/* RegExp?*/ regexp){
		//	summary: 
		//		Internal function for looking at the values contained by the item.
		//	description: 
		//		Internal function for looking at the values contained by the item.  This 
		//		function allows for denoting if the comparison should be case sensitive for
		//		strings or not (for handling filtering cases where string case should not matter)
		//	
		//	item:
		//		The data item to examine for attribute values.
		//	attribute:
		//		The attribute to inspect.
		//	value:	
		//		The value to match.
		//	regexp:
		//		Optional regular expression generated off value if value was of string type to handle wildcarding.
		//		If present and attribute values are string, then it can be used for comparison instead of 'value'
		return dojo.some(this.getValues(item, attribute), function(possibleValue){
			if(possibleValue !== null && !dojo.isObject(possibleValue) && regexp){
				if(possibleValue.toString().match(regexp)){
					return true; // Boolean
				}
			}else if(value === possibleValue){
				return true; // Boolean
			}
		});
	},

	isItem: function(/* anything */ something){
		//	summary: 
		//		See dojo.data.api.Read.isItem()
		if(something && something[this._storeRefPropName] === this){
			if(this._arrayOfAllItems[something[this._itemNumPropName]] === something){
				return true;
			}
		}
		return false; // Boolean
	},

	isItemLoaded: function(/* anything */ something){
		//	summary: 
		//		See dojo.data.api.Read.isItemLoaded()
		return this.isItem(something); //boolean
	},

	loadItem: function(/* object */ keywordArgs){
		//	summary: 
		//		See dojo.data.api.Read.loadItem()
		this._assertIsItem(keywordArgs.item);
	},

	getFeatures: function(){
		//	summary: 
		//		See dojo.data.api.Read.getFeatures()
		return this._features; //Object
	},

	getLabel: function(/* item */ item){
		//	summary: 
		//		See dojo.data.api.Read.getLabel()
		if(this._labelAttr && this.isItem(item)){
			return this.getValue(item,this._labelAttr); //String
		}
		return undefined; //undefined
	},

	getLabelAttributes: function(/* item */ item){
		//	summary: 
		//		See dojo.data.api.Read.getLabelAttributes()
		if(this._labelAttr){
			return [this._labelAttr]; //array
		}
		return null; //null
	},

	_fetchItems: function(	/* Object */ keywordArgs, 
							/* Function */ findCallback, 
							/* Function */ errorCallback){
		//	summary: 
		//		See dojo.data.util.simpleFetch.fetch()
		var self = this;
		var filter = function(requestArgs, arrayOfItems){
			var items = [];
			if(requestArgs.query){
				var ignoreCase = requestArgs.queryOptions ? requestArgs.queryOptions.ignoreCase : false; 

				//See if there are any string values that can be regexp parsed first to avoid multiple regexp gens on the
				//same value for each item examined.  Much more efficient.
				var regexpList = {};
				for(var key in requestArgs.query){
					var value = requestArgs.query[key];
					if(typeof value === "string"){
						regexpList[key] = dojo.data.util.filter.patternToRegExp(value, ignoreCase);
					}
				}

				for(var i = 0; i < arrayOfItems.length; ++i){
					var match = true;
					var candidateItem = arrayOfItems[i];
					if(candidateItem === null){
						match = false;
					}else{
						for(var key in requestArgs.query) {
							var value = requestArgs.query[key];
							if (!self._containsValue(candidateItem, key, value, regexpList[key])){
								match = false;
							}
						}
					}
					if(match){
						items.push(candidateItem);
					}
				}
				findCallback(items, requestArgs);
			}else{
				// We want a copy to pass back in case the parent wishes to sort the array. 
				// We shouldn't allow resort of the internal list, so that multiple callers 
				// can get lists and sort without affecting each other.  We also need to
				// filter out any null values that have been left as a result of deleteItem()
				// calls in ItemFileWriteStore.
				for(var i = 0; i < arrayOfItems.length; ++i){
					var item = arrayOfItems[i];
					if(item !== null){
						items.push(item);
					}
				}
				findCallback(items, requestArgs);
			}
		};

		if(this._loadFinished){
			filter(keywordArgs, this._getItemsArray(keywordArgs.queryOptions));
		}else{

			if(this._jsonFileUrl){
				//If fetches come in before the loading has finished, but while
				//a load is in progress, we have to defer the fetching to be 
				//invoked in the callback.
				if(this._loadInProgress){
					this._queuedFetches.push({args: keywordArgs, filter: filter});
				}else{
					this._loadInProgress = true;
					var getArgs = {
							url: self._jsonFileUrl, 
							handleAs: "json-comment-optional"
						};
					var getHandler = dojo.xhrGet(getArgs);
					getHandler.addCallback(function(data){
						try{
							self._getItemsFromLoadedData(data);
							self._loadFinished = true;
							self._loadInProgress = false;
							
							filter(keywordArgs, self._getItemsArray(keywordArgs.queryOptions));
							self._handleQueuedFetches();
						}catch(e){
							self._loadFinished = true;
							self._loadInProgress = false;
							errorCallback(e, keywordArgs);
						}
					});
					getHandler.addErrback(function(error){
						self._loadInProgress = false;
						errorCallback(error, keywordArgs);
					});
				}
			}else if(this._jsonData){
				try{
					this._loadFinished = true;
					this._getItemsFromLoadedData(this._jsonData);
					this._jsonData = null;
					filter(keywordArgs, this._getItemsArray(keywordArgs.queryOptions));
				}catch(e){
					errorCallback(e, keywordArgs);
				}
			}else{
				errorCallback(new Error("dojo.data.ItemFileReadStore: No JSON source data was provided as either URL or a nested Javascript object."), keywordArgs);
			}
		}
	},

	_handleQueuedFetches: function(){
		//	summary: 
		//		Internal function to execute delayed request in the store.
		//Execute any deferred fetches now.
		if (this._queuedFetches.length > 0) {
			for(var i = 0; i < this._queuedFetches.length; i++){
				var fData = this._queuedFetches[i];
				var delayedQuery = fData.args;
				var delayedFilter = fData.filter;
				if(delayedFilter){
					delayedFilter(delayedQuery, this._getItemsArray(delayedQuery.queryOptions)); 
				}else{
					this.fetchItemByIdentity(delayedQuery);
				}
			}
			this._queuedFetches = [];
		}
	},

	_getItemsArray: function(/*object?*/queryOptions){
		//	summary: 
		//		Internal function to determine which list of items to search over.
		//	queryOptions: The query options parameter, if any.
		if(queryOptions && queryOptions.deep) {
			return this._arrayOfAllItems; 
		}
		return this._arrayOfTopLevelItems;
	},

	close: function(/*dojo.data.api.Request || keywordArgs || null */ request){
		 //	summary: 
		 //		See dojo.data.api.Read.close()
	},

	_getItemsFromLoadedData: function(/* Object */ dataObject){
		//	summary:
		//		Function to parse the loaded data into item format and build the internal items array.
		//	description:
		//		Function to parse the loaded data into item format and build the internal items array.
		//
		//	dataObject:
		//		The JS data object containing the raw data to convery into item format.
		//
		// 	returns: array
		//		Array of items in store item format.
		
		// First, we define a couple little utility functions...
		
		function valueIsAnItem(/* anything */ aValue){
			// summary:
			//		Given any sort of value that could be in the raw json data,
			//		return true if we should interpret the value as being an
			//		item itself, rather than a literal value or a reference.
			// example:
			// 	|	false == valueIsAnItem("Kermit");
			// 	|	false == valueIsAnItem(42);
			// 	|	false == valueIsAnItem(new Date());
			// 	|	false == valueIsAnItem({_type:'Date', _value:'May 14, 1802'});
			// 	|	false == valueIsAnItem({_reference:'Kermit'});
			// 	|	true == valueIsAnItem({name:'Kermit', color:'green'});
			// 	|	true == valueIsAnItem({iggy:'pop'});
			// 	|	true == valueIsAnItem({foo:42});
			var isItem = (
				(aValue != null) &&
				(typeof aValue == "object") &&
				(!dojo.isArray(aValue)) &&
				(!dojo.isFunction(aValue)) &&
				(aValue.constructor == Object) &&
				(typeof aValue._reference == "undefined") && 
				(typeof aValue._type == "undefined") && 
				(typeof aValue._value == "undefined")
			);
			return isItem;
		}
		
		var self = this;
		function addItemAndSubItemsToArrayOfAllItems(/* Item */ anItem){
			self._arrayOfAllItems.push(anItem);
			for(var attribute in anItem){
				var valueForAttribute = anItem[attribute];
				if(valueForAttribute){
					if(dojo.isArray(valueForAttribute)){
						var valueArray = valueForAttribute;
						for(var k = 0; k < valueArray.length; ++k){
							var singleValue = valueArray[k];
							if(valueIsAnItem(singleValue)){
								addItemAndSubItemsToArrayOfAllItems(singleValue);
							}
						}
					}else{
						if(valueIsAnItem(valueForAttribute)){
							addItemAndSubItemsToArrayOfAllItems(valueForAttribute);
						}
					}
				}
			}
		}

		this._labelAttr = dataObject.label;

		// We need to do some transformations to convert the data structure
		// that we read from the file into a format that will be convenient
		// to work with in memory.

		// Step 1: Walk through the object hierarchy and build a list of all items
		var i;
		var item;
		this._arrayOfAllItems = [];
		this._arrayOfTopLevelItems = dataObject.items;

		for(i = 0; i < this._arrayOfTopLevelItems.length; ++i){
			item = this._arrayOfTopLevelItems[i];
			addItemAndSubItemsToArrayOfAllItems(item);
			item[this._rootItemPropName]=true;
		}

		// Step 2: Walk through all the attribute values of all the items, 
		// and replace single values with arrays.  For example, we change this:
		//		{ name:'Miss Piggy', pets:'Foo-Foo'}
		// into this:
		//		{ name:['Miss Piggy'], pets:['Foo-Foo']}
		// 
		// We also store the attribute names so we can validate our store  
		// reference and item id special properties for the O(1) isItem
		var allAttributeNames = {};
		var key;

		for(i = 0; i < this._arrayOfAllItems.length; ++i){
			item = this._arrayOfAllItems[i];
			for(key in item){
				if (key !== this._rootItemPropName)
				{
					var value = item[key];
					if(value !== null){
						if(!dojo.isArray(value)){
							item[key] = [value];
						}
					}else{
						item[key] = [null];
					}
				}
				allAttributeNames[key]=key;
			}
		}

		// Step 3: Build unique property names to use for the _storeRefPropName and _itemNumPropName
		// This should go really fast, it will generally never even run the loop.
		while(allAttributeNames[this._storeRefPropName]){
			this._storeRefPropName += "_";
		}
		while(allAttributeNames[this._itemNumPropName]){
			this._itemNumPropName += "_";
		}

		// Step 4: Some data files specify an optional 'identifier', which is 
		// the name of an attribute that holds the identity of each item. 
		// If this data file specified an identifier attribute, then build a 
		// hash table of items keyed by the identity of the items.
		var arrayOfValues;

		var identifier = dataObject.identifier;
		if(identifier){
			this._itemsByIdentity = {};
			this._features['dojo.data.api.Identity'] = identifier;
			for(i = 0; i < this._arrayOfAllItems.length; ++i){
				item = this._arrayOfAllItems[i];
				arrayOfValues = item[identifier];
				var identity = arrayOfValues[0];
				if(!this._itemsByIdentity[identity]){
					this._itemsByIdentity[identity] = item;
				}else{
					if(this._jsonFileUrl){
						throw new Error("dojo.data.ItemFileReadStore:  The json data as specified by: [" + this._jsonFileUrl + "] is malformed.  Items within the list have identifier: [" + identifier + "].  Value collided: [" + identity + "]");
					}else if(this._jsonData){
						throw new Error("dojo.data.ItemFileReadStore:  The json data provided by the creation arguments is malformed.  Items within the list have identifier: [" + identifier + "].  Value collided: [" + identity + "]");
					}
				}
			}
		}else{
			this._features['dojo.data.api.Identity'] = Number;
		}

		// Step 5: Walk through all the items, and set each item's properties 
		// for _storeRefPropName and _itemNumPropName, so that store.isItem() will return true.
		for(i = 0; i < this._arrayOfAllItems.length; ++i){
			item = this._arrayOfAllItems[i];
			item[this._storeRefPropName] = this;
			item[this._itemNumPropName] = i;
		}

		// Step 6: We walk through all the attribute values of all the items,
		// looking for type/value literals and item-references.
		//
		// We replace item-references with pointers to items.  For example, we change:
		//		{ name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
		// into this:
		//		{ name:['Kermit'], friends:[miss_piggy] } 
		// (where miss_piggy is the object representing the 'Miss Piggy' item).
		//
		// We replace type/value pairs with typed-literals.  For example, we change:
		//		{ name:['Nelson Mandela'], born:[{_type:'Date', _value:'July 18, 1918'}] }
		// into this:
		//		{ name:['Kermit'], born:(new Date('July 18, 1918')) } 
		//
		// We also generate the associate map for all items for the O(1) isItem function.
		for(i = 0; i < this._arrayOfAllItems.length; ++i){
			item = this._arrayOfAllItems[i]; // example: { name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
			for(key in item){
				arrayOfValues = item[key]; // example: [{_reference:{name:'Miss Piggy'}}]
				for(var j = 0; j < arrayOfValues.length; ++j) {
					value = arrayOfValues[j]; // example: {_reference:{name:'Miss Piggy'}}
					if(value !== null && typeof value == "object"){
						if(value._type && value._value){
							var type = value._type; // examples: 'Date', 'Color', or 'ComplexNumber'
							var mappingObj = this._datatypeMap[type]; // examples: Date, dojo.Color, foo.math.ComplexNumber, {type: dojo.Color, deserialize(value){ return new dojo.Color(value)}}
							if(!mappingObj){ 
								throw new Error("dojo.data.ItemFileReadStore: in the typeMap constructor arg, no object class was specified for the datatype '" + type + "'");
							}else if(dojo.isFunction(mappingObj)){
								arrayOfValues[j] = new mappingObj(value._value);
							}else if(dojo.isFunction(mappingObj.deserialize)){
								arrayOfValues[j] = mappingObj.deserialize(value._value);
							}else{
								throw new Error("dojo.data.ItemFileReadStore: Value provided in typeMap was neither a constructor, nor a an object with a deserialize function");
							}
						}
						if(value._reference){
							var referenceDescription = value._reference; // example: {name:'Miss Piggy'}
							if(dojo.isString(referenceDescription)){
								// example: 'Miss Piggy'
								// from an item like: { name:['Kermit'], friends:[{_reference:'Miss Piggy'}]}
								arrayOfValues[j] = this._itemsByIdentity[referenceDescription];
							}else{
								// example: {name:'Miss Piggy'}
								// from an item like: { name:['Kermit'], friends:[{_reference:{name:'Miss Piggy'}}] }
								for(var k = 0; k < this._arrayOfAllItems.length; ++k){
									var candidateItem = this._arrayOfAllItems[k];
									var found = true;
									for(var refKey in referenceDescription){
										if(candidateItem[refKey] != referenceDescription[refKey]){ 
											found = false; 
										}
									}
									if(found){ 
										arrayOfValues[j] = candidateItem; 
									}
								}
							}
						}
					}
				}
			}
		}
	},

	getIdentity: function(/* item */ item){
		//	summary: 
		//		See dojo.data.api.Identity.getIdentity()
		var identifier = this._features['dojo.data.api.Identity'];
		if(identifier === Number){
			return item[this._itemNumPropName]; // Number
		}else{
			var arrayOfValues = item[identifier];
			if(arrayOfValues){
				return arrayOfValues[0]; // Object || String
			}
		}
		return null; // null
	},

	fetchItemByIdentity: function(/* Object */ keywordArgs){
		//	summary: 
		//		See dojo.data.api.Identity.fetchItemByIdentity()

		// Hasn't loaded yet, we have to trigger the load.
		if(!this._loadFinished){
			var self = this;
			if(this._jsonFileUrl){

				if(this._loadInProgress){
					this._queuedFetches.push({args: keywordArgs});
				}else{
					this._loadInProgress = true;
					var getArgs = {
							url: self._jsonFileUrl, 
							handleAs: "json-comment-optional"
					};
					var getHandler = dojo.xhrGet(getArgs);
					getHandler.addCallback(function(data){
						var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
						try{
							self._getItemsFromLoadedData(data);
							self._loadFinished = true;
							self._loadInProgress = false;
							var item = self._getItemByIdentity(keywordArgs.identity);
							if(keywordArgs.onItem){
								keywordArgs.onItem.call(scope, item);
							}
							self._handleQueuedFetches();
						}catch(error){
							self._loadInProgress = false;
							if(keywordArgs.onError){
								keywordArgs.onError.call(scope, error);
							}
						}
					});
					getHandler.addErrback(function(error){
						self._loadInProgress = false;
						if(keywordArgs.onError){
							var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
							keywordArgs.onError.call(scope, error);
						}
					});
				}

			}else if(this._jsonData){
				// Passed in data, no need to xhr.
				self._getItemsFromLoadedData(self._jsonData);
				self._jsonData = null;
				self._loadFinished = true;
				var item = self._getItemByIdentity(keywordArgs.identity);
				if(keywordArgs.onItem){
					var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
					keywordArgs.onItem.call(scope, item);
				}
			} 
		}else{
			// Already loaded.  We can just look it up and call back.
			var item = this._getItemByIdentity(keywordArgs.identity);
			if(keywordArgs.onItem){
				var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
				keywordArgs.onItem.call(scope, item);
			}
		}
	},

	_getItemByIdentity: function(/* Object */ identity){
		//	summary:
		//		Internal function to look an item up by its identity map.
		var item = null;
		if(this._itemsByIdentity){
			item = this._itemsByIdentity[identity];
		}else{
			item = this._arrayOfAllItems[identity];
		}
		if(item === undefined){
			item = null;
		}
		return item; // Object
	},

	getIdentityAttributes: function(/* item */ item){
		//	summary: 
		//		See dojo.data.api.Identity.getIdentifierAttributes()
		 
		var identifier = this._features['dojo.data.api.Identity'];
		if(identifier === Number){
			// If (identifier === Number) it means getIdentity() just returns
			// an integer item-number for each item.  The dojo.data.api.Identity
			// spec says we need to return null if the identity is not composed 
			// of attributes 
			return null; // null
		}else{
			return [identifier]; // Array
		}
	},
	
	_forceLoad: function(){
		//	summary: 
		//		Internal function to force a load of the store if it hasn't occurred yet.  This is required
		//		for specific functions to work properly.  
		var self = this;
		if(this._jsonFileUrl){
				var getArgs = {
					url: self._jsonFileUrl, 
					handleAs: "json-comment-optional",
					sync: true
				};
			var getHandler = dojo.xhrGet(getArgs);
			getHandler.addCallback(function(data){
				try{
					//Check to be sure there wasn't another load going on concurrently 
					//So we don't clobber data that comes in on it.  If there is a load going on
					//then do not save this data.  It will potentially clobber current data.
					//We mainly wanted to sync/wait here.
					//TODO:  Revisit the loading scheme of this store to improve multi-initial
					//request handling.
					if (self._loadInProgress !== true && !self._loadFinished) {
						self._getItemsFromLoadedData(data);
						self._loadFinished = true;
					}
				}catch(e){
					console.log(e);
					throw e;
				}
			});
			getHandler.addErrback(function(error){
				throw error;
			});
		}else if(this._jsonData){
			self._getItemsFromLoadedData(self._jsonData);
			self._jsonData = null;
			self._loadFinished = true;
		} 
	}
});
//Mix in the simple fetch implementation to this class.
dojo.extend(dojo.data.ItemFileReadStore,dojo.data.util.simpleFetch);

}

if(!dojo._hasResource["dijit.form.ComboBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.ComboBox"] = true;
dojo.provide("dijit.form.ComboBox");





dojo.declare(
	"dijit.form.ComboBoxMixin",
	null,
	{
		// summary:
		//		Auto-completing text box, and base class for FilteringSelect widget.
		//
		//		The drop down box's values are populated from an class called
		//		a data provider, which returns a list of values based on the characters
		//		that the user has typed into the input box.
		//
		//		Some of the options to the ComboBox are actually arguments to the data
		//		provider.
		//
		//		You can assume that all the form widgets (and thus anything that mixes
		//		in ComboBoxMixin) will inherit from _FormWidget and thus the "this"
		//		reference will also "be a" _FormWidget.

		// item: Object
		//		This is the item returned by the dojo.data.store implementation that
		//		provides the data for this cobobox, it's the currently selected item.
		item: null,

		// pageSize: Integer
		//		Argument to data provider.
		//		Specifies number of search results per page (before hitting "next" button)
		pageSize: Infinity,

		// store: Object
		//		Reference to data provider object used by this ComboBox
		store: null,

		// query: Object
		//		A query that can be passed to 'store' to initially filter the items,
		//		before doing further filtering based on searchAttr and the key.
		query: {},

		// autoComplete: Boolean
		//		If you type in a partial string, and then tab out of the <input> box,
		//		automatically copy the first entry displayed in the drop down list to
		//		the <input> field
		autoComplete: true,

		// searchDelay: Integer
		//		Delay in milliseconds between when user types something and we start
		//		searching based on that value
		searchDelay: 100,

		// searchAttr: String
		//		Searches pattern match against this field
		searchAttr: "name",

		// ignoreCase: Boolean
		//		Set true if the ComboBox should ignore case when matching possible items
		ignoreCase: true,

		// hasDownArrow: Boolean
		//		Set this textbox to have a down arrow button.
		//		Defaults to true.
		hasDownArrow:true,

		// _hasFocus: Boolean
		//		Represents focus state of the textbox
		// TODO: get rid of this; it's unnecessary (but currently referenced in FilteringSelect)
		_hasFocus:false,

		templateString:"<table class=\"dijit dijitReset dijitInlineTable dijitLeft\" cellspacing=\"0\" cellpadding=\"0\"\r\n\tid=\"widget_${id}\" name=\"${name}\" dojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse\" waiRole=\"presentation\"\r\n\t><tr class=\"dijitReset\"\r\n\t\t><td class='dijitReset dijitStretch dijitInputField' width=\"100%\"\r\n\t\t\t><input type=\"text\" autocomplete=\"off\" name=\"${name}\"\r\n\t\t\tdojoAttachEvent=\"onkeypress, onkeyup, onfocus, compositionend\"\r\n\t\t\tdojoAttachPoint=\"textbox,focusNode\" waiRole=\"combobox\"\r\n\t\t/></td\r\n\t\t><td class=\"dijitReset dijitValidationIconField\" width=\"0%\"\r\n\t\t\t><div dojoAttachPoint='iconNode' class='dijitValidationIcon'></div\r\n\t\t\t><div class='dijitValidationIconText'>&Chi;</div\r\n\t\t></td\r\n\t\t><td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton' width=\"0%\"\r\n\t\t\tdojoAttachPoint=\"downArrowNode\"\r\n\t\t\tdojoAttachEvent=\"onmousedown:_onArrowMouseDown,onmouseup:_onMouse,onmouseenter:_onMouse,onmouseleave:_onMouse\"\r\n\t\t\t><div class=\"dijitDownArrowButtonInner\" waiRole=\"presentation\"\r\n\t\t\t\t><div class=\"dijitDownArrowButtonChar\">&#9660;</div\r\n\t\t\t></div\r\n\t\t></td\t\r\n\t></tr\r\n></table>\r\n",

		baseClass:"dijitComboBox",

		_lastDisplayedValue: "",

		getValue:function(){
			// don't get the textbox value but rather the previously set hidden value
			return dijit.form.TextBox.superclass.getValue.apply(this, arguments);
		},

		setDisplayedValue:function(/*String*/ value){
			this._lastDisplayedValue = value;
			this.setValue(value, true);
		},

		_getCaretPos: function(/*DomNode*/ element){
			// khtml 3.5.2 has selection* methods as does webkit nightlies from 2005-06-22
			if(typeof(element.selectionStart)=="number"){
				// FIXME: this is totally borked on Moz < 1.3. Any recourse?
				return element.selectionStart;
			}else if(dojo.isIE){
				// in the case of a mouse click in a popup being handled,
				// then the document.selection is not the textarea, but the popup
				// var r = document.selection.createRange();
				// hack to get IE 6 to play nice. What a POS browser.
				var tr = document.selection.createRange().duplicate();
				var ntr = element.createTextRange();
				tr.move("character",0);
				ntr.move("character",0);
				try{
					// If control doesnt have focus, you get an exception.
					// Seems to happen on reverse-tab, but can also happen on tab (seems to be a race condition - only happens sometimes).
					// There appears to be no workaround for this - googled for quite a while.
					ntr.setEndPoint("EndToEnd", tr);
					return String(ntr.text).replace(/\r/g,"").length;
				}catch(e){
					return 0; // If focus has shifted, 0 is fine for caret pos.
				}
			}
		},

		_setCaretPos: function(/*DomNode*/ element, /*Number*/ location){
			location = parseInt(location);
			this._setSelectedRange(element, location, location);
		},

		_setSelectedRange: function(/*DomNode*/ element, /*Number*/ start, /*Number*/ end){
			if(!end){
				end = element.value.length;
			}  // NOTE: Strange - should be able to put caret at start of text?
			// Mozilla
			// parts borrowed from http://www.faqts.com/knowledge_base/view.phtml/aid/13562/fid/130
			if(element.setSelectionRange){
				dijit.focus(element);
				element.setSelectionRange(start, end);
			}else if(element.createTextRange){ // IE
				var range = element.createTextRange();
				with(range){
					collapse(true);
					moveEnd('character', end);
					moveStart('character', start);
					select();
				}
			}else{ //otherwise try the event-creation hack (our own invention)
				// do we need these?
				element.value = element.value;
				element.blur();
				dijit.focus(element);
				// figure out how far back to go
				var dist = parseInt(element.value.length)-end;
				var tchar = String.fromCharCode(37);
				var tcc = tchar.charCodeAt(0);
				for(var x = 0; x < dist; x++){
					var te = document.createEvent("KeyEvents");
					te.initKeyEvent("keypress", true, true, null, false, false, false, false, tcc, tcc);
					element.dispatchEvent(te);
				}
			}
		},

		onkeypress: function(/*Event*/ evt){
			// summary: handles keyboard events

			//except for pasting case - ctrl + v(118)
			if(evt.altKey || (evt.ctrlKey && evt.charCode != 118)){
				return;
			}
			var doSearch = false;
			this.item = null; // #4872
			if(this._isShowingNow){this._popupWidget.handleKey(evt);}
			switch(evt.keyCode){
				case dojo.keys.PAGE_DOWN:
				case dojo.keys.DOWN_ARROW:
					if(!this._isShowingNow||this._prev_key_esc){
						this._arrowPressed();
						doSearch=true;
					}else{
						this._announceOption(this._popupWidget.getHighlightedOption());
					}
					dojo.stopEvent(evt);
					this._prev_key_backspace = false;
					this._prev_key_esc = false;
					break;

				case dojo.keys.PAGE_UP:
				case dojo.keys.UP_ARROW:
					if(this._isShowingNow){
						this._announceOption(this._popupWidget.getHighlightedOption());
					}
					dojo.stopEvent(evt);
					this._prev_key_backspace = false;
					this._prev_key_esc = false;
					break;

				case dojo.keys.ENTER:
					// prevent submitting form if user presses enter
					// also prevent accepting the value if either Next or Previous are selected
					var highlighted;
					if(this._isShowingNow&&(highlighted=this._popupWidget.getHighlightedOption())){
						// only stop event on prev/next
						if(highlighted==this._popupWidget.nextButton){
							this._nextSearch(1);
							dojo.stopEvent(evt);
							break;
						}
						else if(highlighted==this._popupWidget.previousButton){
							this._nextSearch(-1);
							dojo.stopEvent(evt);
							break;
						}
					}else{
						this.setDisplayedValue(this.getDisplayedValue());
					}
					// default case:
					// prevent submit, but allow event to bubble
					evt.preventDefault();
					// fall through

				case dojo.keys.TAB:
					var newvalue=this.getDisplayedValue();
					// #4617: if the user had More Choices selected fall into the _onBlur handler
					if(this._popupWidget &&
						(newvalue == this._popupWidget._messages["previousMessage"] ||
							newvalue == this._popupWidget._messages["nextMessage"])){
						break;
					}
					if(this._isShowingNow){
						this._prev_key_backspace = false;
						this._prev_key_esc = false;
						if(this._popupWidget.getHighlightedOption()){
							this._popupWidget.setValue({target:this._popupWidget.getHighlightedOption()}, true);
						}
						this._hideResultList();
					}
					break;

				case dojo.keys.SPACE:
					this._prev_key_backspace = false;
					this._prev_key_esc = false;
					if(this._isShowingNow && this._popupWidget.getHighlightedOption()){
						dojo.stopEvent(evt);
						this._selectOption();
						this._hideResultList();
					}else{
						doSearch = true;
					}
					break;

				case dojo.keys.ESCAPE:
					this._prev_key_backspace = false;
					this._prev_key_esc = true;
					this._hideResultList();
					if(this._lastDisplayedValue != this.getDisplayedValue()){
						this.setDisplayedValue(this._lastDisplayedValue);
						dojo.stopEvent(evt);
					}else{
						this.setValue(this.getValue(), false);
					}
					break;

				case dojo.keys.DELETE:
				case dojo.keys.BACKSPACE:
					this._prev_key_esc = false;
					this._prev_key_backspace = true;
					doSearch = true;
					break;

				case dojo.keys.RIGHT_ARROW: // fall through

				case dojo.keys.LEFT_ARROW: // fall through
					this._prev_key_backspace = false;
					this._prev_key_esc = false;
					break;

				default:// non char keys (F1-F12 etc..)  shouldn't open list
					this._prev_key_backspace = false;
					this._prev_key_esc = false;
					if(dojo.isIE || evt.charCode != 0){
						doSearch=true;
					}
			}
			if(this.searchTimer){
				clearTimeout(this.searchTimer);
			}
			if(doSearch){
				// need to wait a tad before start search so that the event bubbles through DOM and we have value visible
				this.searchTimer = setTimeout(dojo.hitch(this, this._startSearchFromInput), this.searchDelay);
			}
		},

		_autoCompleteText: function(/*String*/ text){
			// summary:
			// Fill in the textbox with the first item from the drop down list, and
			// highlight the characters that were auto-completed.   For example, if user
			// typed "CA" and the drop down list appeared, the textbox would be changed to
			// "California" and "ifornia" would be highlighted.

			// IE7: clear selection so next highlight works all the time
			this._setSelectedRange(this.focusNode, this.focusNode.value.length, this.focusNode.value.length);
			// does text autoComplete the value in the textbox?
			// #3744: escape regexp so the user's input isn't treated as a regular expression.
			// Example: If the user typed "(" then the regexp would throw "unterminated parenthetical."
			// Also see #2558 for the autocompletion bug this regular expression fixes.
			if(new RegExp("^"+escape(this.focusNode.value), this.ignoreCase ? "i" : "").test(escape(text))){
				var cpos = this._getCaretPos(this.focusNode);
				// only try to extend if we added the last character at the end of the input
				if((cpos+1) > this.focusNode.value.length){
					// only add to input node as we would overwrite Capitalisation of chars
					// actually, that is ok
					this.focusNode.value = text;//.substr(cpos);
					// visually highlight the autocompleted characters
					this._setSelectedRange(this.focusNode, cpos, this.focusNode.value.length);
					dijit.setWaiState(this.focusNode, "valuenow", text);
				}
			}else{
				// text does not autoComplete; replace the whole value and highlight
				this.focusNode.value = text;
				this._setSelectedRange(this.focusNode, 0, this.focusNode.value.length);
				dijit.setWaiState(this.focusNode, "valuenow", text);
			}
		},

		_openResultList: function(/*Object*/ results, /*Object*/ dataObject){
			if(this.disabled || dataObject.query[this.searchAttr] != this._lastQuery){
				return;
			}
			this._popupWidget.clearResultList();
			if(!results.length){
				this._hideResultList();
				return;
			}

			// Fill in the textbox with the first item from the drop down list, and
			// highlight the characters that were auto-completed.   For example, if user
			// typed "CA" and the drop down list appeared, the textbox would be changed to
			// "California" and "ifornia" would be highlighted.

			var zerothvalue=new String(this.store.getValue(results[0], this.searchAttr));
			if(zerothvalue && this.autoComplete && !this._prev_key_backspace &&
			// when the user clicks the arrow button to show the full list,
			// startSearch looks for "*".
			// it does not make sense to autocomplete
			// if they are just previewing the options available.
				(dataObject.query[this.searchAttr] != "*")){
				this._autoCompleteText(zerothvalue);
				// announce the autocompleted value
				dijit.setWaiState(this.focusNode || this.domNode, "valuenow", zerothvalue);
			}
			this._popupWidget.createOptions(results, dataObject, dojo.hitch(this, this._getMenuLabelFromItem));

			// show our list (only if we have content, else nothing)
			this._showResultList();

			// #4091: tell the screen reader that the paging callback finished by shouting the next choice
			if(dataObject.direction){
				if(dataObject.direction==1){
					this._popupWidget.highlightFirstOption();
				}else if(dataObject.direction==-1){
					this._popupWidget.highlightLastOption();
				}
				this._announceOption(this._popupWidget.getHighlightedOption());
			}
		},

		_showResultList: function(){
			this._hideResultList();
			var items = this._popupWidget.getItems(),
				visibleCount = Math.min(items.length,this.maxListLength);
			this._arrowPressed();
			// hide the tooltip
			this._displayMessage("");
			
			// Position the list and if it's too big to fit on the screen then
			// size it to the maximum possible height
			// Our dear friend IE doesnt take max-height so we need to calculate that on our own every time
			// TODO: want to redo this, see http://trac.dojotoolkit.org/ticket/3272, http://trac.dojotoolkit.org/ticket/4108
			with(this._popupWidget.domNode.style){
				// natural size of the list has changed, so erase old width/height settings,
				// which were hardcoded in a previous call to this function (via dojo.marginBox() call) 
				width="";
				height="";
			}
			var best=this.open();
			// #3212: only set auto scroll bars if necessary
			// prevents issues with scroll bars appearing when they shouldn't when node is made wider (fractional pixels cause this)
			var popupbox=dojo.marginBox(this._popupWidget.domNode);
			this._popupWidget.domNode.style.overflow=((best.h==popupbox.h)&&(best.w==popupbox.w))?"hidden":"auto";
			// #4134: borrow TextArea scrollbar test so content isn't covered by scrollbar and horizontal scrollbar doesn't appear
			var newwidth=best.w;
			if(best.h<this._popupWidget.domNode.scrollHeight){newwidth+=16;}
			dojo.marginBox(this._popupWidget.domNode, {h:best.h,w:Math.max(newwidth,this.domNode.offsetWidth)});
		},

		_hideResultList: function(){
			if(this._isShowingNow){
				dijit.popup.close(this._popupWidget);
				this._arrowIdle();
				this._isShowingNow=false;
			}
		},

		_onBlur: function(){
			// summary: called magically when focus has shifted away from this widget and it's dropdown
			this._hasFocus=false;
			this._hasBeenBlurred = true;
			this._hideResultList();
			this._arrowIdle();
			// if the user clicks away from the textbox OR tabs away, set the value to the textbox value
			// #4617: if value is now more choices or previous choices, revert the value
			var newvalue=this.getDisplayedValue();
			if(this._popupWidget&&(newvalue==this._popupWidget._messages["previousMessage"]||newvalue==this._popupWidget._messages["nextMessage"])){
				this.setValue(this._lastValueReported, true);
			}else{
				this.setDisplayedValue(newvalue);
			}
		},

		onfocus:function(/*Event*/ evt){
			this._hasFocus=true;
			
			// update styling to reflect that we are focused
			this._onMouse(evt);
		},

		_announceOption: function(/*Node*/ node){
			// summary:
			//	a11y code that puts the highlighted option in the textbox
			//	This way screen readers will know what is happening in the menu

			if(node==null){return;}
			// pull the text value from the item attached to the DOM node
			var newValue;
			if(node==this._popupWidget.nextButton||node==this._popupWidget.previousButton){
				newValue=node.innerHTML;
			}else{
				newValue=this.store.getValue(node.item, this.searchAttr);
			}
			// get the text that the user manually entered (cut off autocompleted text)
			this.focusNode.value=this.focusNode.value.substring(0, this._getCaretPos(this.focusNode));
			// autocomplete the rest of the option to announce change
			this._autoCompleteText(newValue);
		},

		_selectOption: function(/*Event*/ evt){
			var tgt = null;
			if(!evt){
				evt ={ target: this._popupWidget.getHighlightedOption()};
			}
				// what if nothing is highlighted yet?
			if(!evt.target){
				// handle autocompletion where the the user has hit ENTER or TAB
				this.setDisplayedValue(this.getDisplayedValue());
				return;
			// otherwise the user has accepted the autocompleted value
			}else{
				tgt = evt.target;
			}
			if(!evt.noHide){
				this._hideResultList();
				this._setCaretPos(this.focusNode, this.store.getValue(tgt.item, this.searchAttr).length);
			}
			this._doSelect(tgt);
		},

		_doSelect: function(tgt){
			this.item = tgt.item;
			this.setValue(this.store.getValue(tgt.item, this.searchAttr), true);
		},

		_onArrowMouseDown: function(evt){
			// summary: callback when arrow is clicked
			if(this.disabled){
				return;
			}
			dojo.stopEvent(evt);
			this.focus();
			if(this._isShowingNow){
				this._hideResultList();
			}else{
				// forces full population of results, if they click
				// on the arrow it means they want to see more options
				this._startSearch("");
			}
		},

		_startSearchFromInput: function(){
			this._startSearch(this.focusNode.value);
		},

		_startSearch: function(/*String*/ key){
			if(!this._popupWidget){
				this._popupWidget = new dijit.form._ComboBoxMenu({
					onChange: dojo.hitch(this, this._selectOption)
				});
			}
			// create a new query to prevent accidentally querying for a hidden value from FilteringSelect's keyField
			var query=this.query;
			this._lastQuery=query[this.searchAttr]=key+"*";
			var dataObject=this.store.fetch({queryOptions:{ignoreCase:this.ignoreCase, deep:true}, query: query, onComplete:dojo.hitch(this, "_openResultList"), start:0, count:this.pageSize});
			function nextSearch(dataObject, direction){
				dataObject.start+=dataObject.count*direction;
				// #4091: tell callback the direction of the paging so the screen reader knows which menu option to shout
				dataObject.direction=direction;
				dataObject.store.fetch(dataObject);
			}
			this._nextSearch=this._popupWidget.onPage=dojo.hitch(this, nextSearch, dataObject);
		},

		_getValueField:function(){
			return this.searchAttr;
		},

		/////////////// Event handlers /////////////////////

		_arrowPressed: function(){
			if(!this.disabled&&this.hasDownArrow){
				dojo.addClass(this.downArrowNode, "dijitArrowButtonActive");
			}
		},

		_arrowIdle: function(){
			if(!this.disabled&&this.hasDownArrow){
				dojo.removeClass(this.downArrowNode, "dojoArrowButtonPushed");
			}
		},

		compositionend: function(/*Event*/ evt){
			// summary: When inputting characters using an input method, such as Asian
			// languages, it will generate this event instead of onKeyDown event
			// Note: this event is only triggered in FF (not in IE)
			this.onkeypress({charCode:-1});
		},

		//////////// INITIALIZATION METHODS ///////////////////////////////////////
		constructor: function(){
			this.query={};
		},

		postMixInProperties: function(){
			if(!this.hasDownArrow){
				this.baseClass = "dijitTextBox";
			}
			if(!this.store){
				// if user didn't specify store, then assume there are option tags
				var items = this.srcNodeRef ? dojo.query("> option", this.srcNodeRef).map(function(node){
					node.style.display="none";
					return { value: node.getAttribute("value"), name: String(node.innerHTML) };
				}) : {};
				this.store = new dojo.data.ItemFileReadStore({data: {identifier:this._getValueField(), items:items}});

				// if there is no value set and there is an option list,
				// set the value to the first value to be consistent with native Select
				if(items && items.length && !this.value){
					// For <select>, IE does not let you set the value attribute of the srcNodeRef (and thus dojo.mixin does not copy it).
					// IE does understand selectedIndex though, which is automatically set by the selected attribute of an option tag
					this.value = items[this.srcNodeRef.selectedIndex != -1 ? this.srcNodeRef.selectedIndex : 0]
						[this._getValueField()];
				}
			}
		},

		uninitialize:function(){
			if(this._popupWidget){
				this._hideResultList();
				this._popupWidget.destroy()
			};
		},

		_getMenuLabelFromItem:function(/*Item*/ item){
			return {html:false, label:this.store.getValue(item, this.searchAttr)};
		},

		open:function(){
			this._isShowingNow=true;
			return dijit.popup.open({
				popup: this._popupWidget,
				around: this.domNode,
				parent: this
			});
		}
	}
);

dojo.declare(
	"dijit.form._ComboBoxMenu",
	[dijit._Widget, dijit._Templated],

	{
		// summary:
		//	Focus-less div based menu for internal use in ComboBox

		templateString:"<div class='dijitMenu' dojoAttachEvent='onmousedown,onmouseup,onmouseover,onmouseout' tabIndex='-1' style='overflow:\"auto\";'>"
				+"<div class='dijitMenuItem dijitMenuPreviousButton' dojoAttachPoint='previousButton'></div>"
				+"<div class='dijitMenuItem dijitMenuNextButton' dojoAttachPoint='nextButton'></div>"
			+"</div>",
		_messages:null,

		postMixInProperties:function(){
			this._messages = dojo.i18n.getLocalization("dijit.form", "ComboBox", this.lang);
			this.inherited("postMixInProperties", arguments);
		},

		setValue:function(/*Object*/ value){
			this.value=value;
			this.onChange(value);
		},

		onChange:function(/*Object*/ value){},
		onPage:function(/*Number*/ direction){},

		postCreate:function(){
			// fill in template with i18n messages
			this.previousButton.innerHTML=this._messages["previousMessage"];
			this.nextButton.innerHTML=this._messages["nextMessage"];
			this.inherited("postCreate", arguments);
		},

		onClose:function(){
			this._blurOptionNode();
		},

		_createOption:function(/*Object*/ item, labelFunc){
			// summary: creates an option to appear on the popup menu
			// subclassed by FilteringSelect

			var labelObject=labelFunc(item);
			var menuitem = document.createElement("div");
			if(labelObject.html){menuitem.innerHTML=labelObject.label;}
			else{menuitem.appendChild(document.createTextNode(labelObject.label));}
			// #3250: in blank options, assign a normal height
			if(menuitem.innerHTML==""){
				menuitem.innerHTML="&nbsp;"
			}
			menuitem.item=item;
			return menuitem;
		},

		createOptions:function(results, dataObject, labelFunc){
			//this._dataObject=dataObject;
			//this._dataObject.onComplete=dojo.hitch(comboBox, comboBox._openResultList);
			// display "Previous . . ." button
			this.previousButton.style.display=dataObject.start==0?"none":"";
			// create options using _createOption function defined by parent ComboBox (or FilteringSelect) class
			// #2309: iterate over cache nondestructively
			var _this=this;
			dojo.forEach(results, function(item){
				var menuitem=_this._createOption(item, labelFunc);
				menuitem.className = "dijitMenuItem";
				_this.domNode.insertBefore(menuitem, _this.nextButton);
			});
			// display "Next . . ." button
			this.nextButton.style.display=dataObject.count==results.length?"":"none";
		},

		clearResultList:function(){
			// keep the previous and next buttons of course
			while(this.domNode.childNodes.length>2){
				this.domNode.removeChild(this.domNode.childNodes[this.domNode.childNodes.length-2]);
			}
		},

		// these functions are called in showResultList
		getItems:function(){
			return this.domNode.childNodes;
		},

		getListLength:function(){
			return this.domNode.childNodes.length-2;
		},

		onmousedown:function(/*Event*/ evt){
			dojo.stopEvent(evt);
		},

		onmouseup:function(/*Event*/ evt){
			if(evt.target === this.domNode){
				return;
			}else if(evt.target==this.previousButton){
				this.onPage(-1);
			}else if(evt.target==this.nextButton){
				this.onPage(1);
			}else{
				var tgt=evt.target;
				// while the clicked node is inside the div
				while(!tgt.item){
					// recurse to the top
					tgt=tgt.parentNode;
				}
				this.setValue({target:tgt}, true);
			}
		},

		onmouseover:function(/*Event*/ evt){
			if(evt.target === this.domNode){ return; }
			var tgt=evt.target;
			if(!(tgt==this.previousButton||tgt==this.nextButton)){
				// while the clicked node is inside the div
				while(!tgt.item){
					// recurse to the top
					tgt=tgt.parentNode;
				}
			}
			this._focusOptionNode(tgt);
		},

		onmouseout:function(/*Event*/ evt){
			if(evt.target === this.domNode){ return; }
			this._blurOptionNode();
		},

		_focusOptionNode:function(/*DomNode*/ node){
			// summary:
			//	does the actual highlight
			if(this._highlighted_option != node){
				this._blurOptionNode();
				this._highlighted_option = node;
				dojo.addClass(this._highlighted_option, "dijitMenuItemHover");
			}
		},

		_blurOptionNode:function(){
			// summary:
			//	removes highlight on highlighted option
			if(this._highlighted_option){
				dojo.removeClass(this._highlighted_option, "dijitMenuItemHover");
				this._highlighted_option = null;
			}
		},

		_highlightNextOption:function(){
			// because each press of a button clears the menu,
			// the highlighted option sometimes becomes detached from the menu!
			// test to see if the option has a parent to see if this is the case.
			if(!this.getHighlightedOption()){
				this._focusOptionNode(this.domNode.firstChild.style.display=="none"?this.domNode.firstChild.nextSibling:this.domNode.firstChild);
			}else if(this._highlighted_option.nextSibling&&this._highlighted_option.nextSibling.style.display!="none"){
				this._focusOptionNode(this._highlighted_option.nextSibling);
			}
			// scrollIntoView is called outside of _focusOptionNode because in IE putting it inside causes the menu to scroll up on mouseover
			dijit.scrollIntoView(this._highlighted_option);
		},

		highlightFirstOption:function(){
			// highlight the non-Previous choices option
			this._focusOptionNode(this.domNode.firstChild.nextSibling);
			dijit.scrollIntoView(this._highlighted_option);
		},

		highlightLastOption:function(){
			// highlight the noon-More choices option
			this._focusOptionNode(this.domNode.lastChild.previousSibling);
			dijit.scrollIntoView(this._highlighted_option);
		},

		_highlightPrevOption:function(){
			// if nothing selected, highlight last option
			// makes sense if you select Previous and try to keep scrolling up the list
			if(!this.getHighlightedOption()){
				this._focusOptionNode(this.domNode.lastChild.style.display=="none"?this.domNode.lastChild.previousSibling:this.domNode.lastChild);
			}else if(this._highlighted_option.previousSibling&&this._highlighted_option.previousSibling.style.display!="none"){
				this._focusOptionNode(this._highlighted_option.previousSibling);
			}
			dijit.scrollIntoView(this._highlighted_option);
		},

		_page:function(/*Boolean*/ up){
			var scrollamount=0;
			var oldscroll=this.domNode.scrollTop;
			var height=parseInt(dojo.getComputedStyle(this.domNode).height);
			// if no item is highlighted, highlight the first option
			if(!this.getHighlightedOption()){this._highlightNextOption();}
			while(scrollamount<height){
				if(up){
					// stop at option 1
					if(!this.getHighlightedOption().previousSibling||this._highlighted_option.previousSibling.style.display=="none"){break;}
					this._highlightPrevOption();
				}else{
					// stop at last option
					if(!this.getHighlightedOption().nextSibling||this._highlighted_option.nextSibling.style.display=="none"){break;}
					this._highlightNextOption();
				}
				// going backwards
				var newscroll=this.domNode.scrollTop;
				scrollamount+=(newscroll-oldscroll)*(up ? -1:1);
				oldscroll=newscroll;
			}
		},

		pageUp:function(){
			this._page(true);
		},

		pageDown:function(){
			this._page(false);
		},

		getHighlightedOption:function(){
			// summary:
			//	Returns the highlighted option.
			return this._highlighted_option&&this._highlighted_option.parentNode ? this._highlighted_option : null;
		},

		handleKey:function(evt){
			switch(evt.keyCode){
				case dojo.keys.DOWN_ARROW:
					this._highlightNextOption();
					break;
				case dojo.keys.PAGE_DOWN:
					this.pageDown();
					break;	
				case dojo.keys.UP_ARROW:
					this._highlightPrevOption();
					break;
				case dojo.keys.PAGE_UP:
					this.pageUp();
					break;	
			}
		}
	}
);

dojo.declare(
	"dijit.form.ComboBox",
	[dijit.form.ValidationTextBox, dijit.form.ComboBoxMixin],
	{
		postMixInProperties: function(){
			dijit.form.ComboBoxMixin.prototype.postMixInProperties.apply(this, arguments);
			dijit.form.ValidationTextBox.prototype.postMixInProperties.apply(this, arguments);
		}
	}
);

}

if(!dojo._hasResource["dijit.form.FilteringSelect"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.FilteringSelect"] = true;
dojo.provide("dijit.form.FilteringSelect");



dojo.declare(
	"dijit.form.FilteringSelect",
	[dijit.form.MappedTextBox, dijit.form.ComboBoxMixin],
	{
		/*
		 * summary
		 *	Enhanced version of HTML's <select> tag.
		 *
		 *	Similar features:
		 *	  - There is a drop down list of possible values.
		 *	- You can only enter a value from the drop down list.  (You can't enter an arbitrary value.)
		 *	- The value submitted with the form is the hidden value (ex: CA),
		 *	  not the displayed value a.k.a. label (ex: California)
		 *
		 *	Enhancements over plain HTML version:
		 *	- If you type in some text then it will filter down the list of possible values in the drop down list.
		 *	- List can be specified either as a static list or via a javascript function (that can get the list from a server)
		 */

		// searchAttr: String
		//		Searches pattern match against this field

		// labelAttr: String
		//		Optional.  The text that actually appears in the drop down.
		//		If not specified, the searchAttr text is used instead.
		labelAttr: "",

		// labelType: String
		//		"html" or "text"
		labelType: "text",

		_isvalid:true,

		isValid:function(){
			return this._isvalid;
		},

		_callbackSetLabel: function(/*Array*/ result, /*Object*/ dataObject, /*Boolean, optional*/ priorityChange){
			// summary
			//	Callback function that dynamically sets the label of the ComboBox

			// setValue does a synchronous lookup,
			// so it calls _callbackSetLabel directly,
			// and so does not pass dataObject
			// dataObject==null means do not test the lastQuery, just continue
			if(dataObject&&dataObject.query[this.searchAttr]!=this._lastQuery){return;}
			if(!result.length){
				//#3268: do nothing on bad input
				//this._setValue("", "");
				//#3285: change CSS to indicate error
				if(!this._hasFocus){ this.valueNode.value=""; }
				dijit.form.TextBox.superclass.setValue.call(this, undefined, !this._hasFocus);
				this._isvalid=false;
				this.validate(this._hasFocus);
			}else{
				this._setValueFromItem(result[0], priorityChange);
			}
		},

		_openResultList: function(/*Object*/ results, /*Object*/ dataObject){
			// #3285: tap into search callback to see if user's query resembles a match
			if(dataObject.query[this.searchAttr]!=this._lastQuery){return;}
			this._isvalid=results.length!=0;
			this.validate(true);
			dijit.form.ComboBoxMixin.prototype._openResultList.apply(this, arguments);
		},

		getValue:function(){
			// don't get the textbox value but rather the previously set hidden value
			return this.valueNode.value;
		},

		_getValueField:function(){
			// used for option tag selects
			return "value";
		},

		_setValue:function(/*String*/ value, /*String*/ displayedValue, /*Boolean, optional*/ priorityChange){
			this.valueNode.value = value;
			dijit.form.FilteringSelect.superclass.setValue.call(this, value, priorityChange, displayedValue);
			this._lastDisplayedValue = displayedValue;
		},

		setValue: function(/*String*/ value, /*Boolean, optional*/ priorityChange){
			// summary
			//	Sets the value of the select.
			//	Also sets the label to the corresponding value by reverse lookup.

			//#3347: fetchItemByIdentity if no keyAttr specified
			var self=this;
			var handleFetchByIdentity = function(item, priorityChange){
				if(item){
					if(self.store.isItemLoaded(item)){
						self._callbackSetLabel([item], undefined, priorityChange);
					}else{
						self.store.loadItem({item:item, onItem: function(result, dataObject){self._callbackSetLabel(result, dataObject, priorityChange)}});
					}
				}else{
					self._isvalid=false;
					// prevent errors from Tooltip not being created yet
					self.validate(false);
				}
			}
			this.store.fetchItemByIdentity({identity: value, onItem: function(item){handleFetchByIdentity(item, priorityChange)}});
		},

		_setValueFromItem: function(/*item*/ item, /*Boolean, optional*/ priorityChange){
			// summary
			//	Set the displayed valued in the input box, based on a selected item.
			//	Users shouldn't call this function; they should be calling setDisplayedValue() instead
			this._isvalid=true;
			this._setValue(this.store.getIdentity(item), this.labelFunc(item, this.store), priorityChange);
		},

		labelFunc: function(/*item*/ item, /*dojo.data.store*/ store){
			// summary: Event handler called when the label changes
			// returns the label that the ComboBox should display
			return store.getValue(item, this.searchAttr);
		},

		onkeyup: function(/*Event*/ evt){
			// summary: internal function
			// FilteringSelect needs to wait for the complete label before committing to a reverse lookup
			//this.setDisplayedValue(this.textbox.value);
		},

		_doSelect: function(/*Event*/ tgt){
			// summary:
			//	ComboBox's menu callback function
			//	FilteringSelect overrides this to set both the visible and hidden value from the information stored in the menu
			this.item = tgt.item;
			this._setValueFromItem(tgt.item, true);
		},

		setDisplayedValue:function(/*String*/ label){
			// summary:
			//	Set textbox to display label
			//	Also performs reverse lookup to set the hidden value
			//	Used in InlineEditBox

			if(this.store){
				var query={};
				this._lastQuery=query[this.searchAttr]=label;
				// if the label is not valid, the callback will never set it,
				// so the last valid value will get the warning textbox
				// set the textbox value now so that the impending warning will make sense to the user
				this.textbox.value=label;
				this._lastDisplayedValue=label;
				this.store.fetch({query:query, queryOptions:{ignoreCase:this.ignoreCase, deep:true}, onComplete: dojo.hitch(this, this._callbackSetLabel)});
			}
		},

		_getMenuLabelFromItem:function(/*Item*/ item){
			// internal function to help ComboBoxMenu figure out what to display
			if(this.labelAttr){return {html:this.labelType=="html", label:this.store.getValue(item, this.labelAttr)};}
			else{
				// because this function is called by ComboBoxMenu, this.inherited tries to find the superclass of ComboBoxMenu
				return dijit.form.ComboBoxMixin.prototype._getMenuLabelFromItem.apply(this, arguments);
			}
		},

		postMixInProperties: function(){
			dijit.form.ComboBoxMixin.prototype.postMixInProperties.apply(this, arguments);
			dijit.form.MappedTextBox.prototype.postMixInProperties.apply(this, arguments);
		}
	}
);

}

({
		previousMessage: "Previous choices",
		nextMessage: "More choices"
})

if(!dojo._hasResource["dojo.regexp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.regexp"] = true;
dojo.provide("dojo.regexp");

dojo.regexp.escapeString = function(/*String*/str, /*String?*/except){
	//	summary:
	//		Adds escape sequences for special characters in regular expressions
	// except:
	//		a String with special characters to be left unescaped

//	return str.replace(/([\f\b\n\t\r[\^$|?*+(){}])/gm, "\\$1"); // string
	return str.replace(/([\.$?*!=:|{}\(\)\[\]\\\/^])/g, function(ch){
		if(except && except.indexOf(ch) != -1){
			return ch;
		}
		return "\\" + ch;
	}); // String
}

dojo.regexp.buildGroupRE = function(/*Object|Array*/arr, /*Function*/re, /*Boolean?*/nonCapture){
	//	summary:
	//		Builds a regular expression that groups subexpressions
	//	description:
	//		A utility function used by some of the RE generators. The
	//		subexpressions are constructed by the function, re, in the second
	//		parameter.  re builds one subexpression for each elem in the array
	//		a, in the first parameter. Returns a string for a regular
	//		expression that groups all the subexpressions.
	// arr:
	//		A single value or an array of values.
	// re:
	//		A function. Takes one parameter and converts it to a regular
	//		expression. 
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. Defaults to false

	// case 1: a is a single value.
	if(!(arr instanceof Array)){
		return re(arr); // String
	}

	// case 2: a is an array
	var b = [];
	for(var i = 0; i < arr.length; i++){
		// convert each elem to a RE
		b.push(re(arr[i]));
	}

	 // join the REs as alternatives in a RE group.
	return dojo.regexp.group(b.join("|"), nonCapture); // String
}

dojo.regexp.group = function(/*String*/expression, /*Boolean?*/nonCapture){
	// summary:
	//		adds group match to expression
	// nonCapture:
	//		If true, uses non-capturing match, otherwise matches are retained
	//		by regular expression. 
	return "(" + (nonCapture ? "?:":"") + expression + ")"; // String
}

}

if(!dojo._hasResource["dojo.number"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.number"] = true;
dojo.provide("dojo.number");







/*=====
dojo.number.__formatOptions = function(kwArgs){
	//	pattern: String?
	//		override formatting pattern with this string (see
	//		dojo.number._applyPattern)
	//	type: String?
	//		choose a format type based on the locale from the following:
	//		decimal, scientific, percent, currency. decimal by default.
	//	places: Number?
	//		fixed number of decimal places to show.  This overrides any
	//		information in the provided pattern.
	//	round: NUmber?
	//		5 rounds to nearest .5; 0 rounds to nearest whole (default). -1
	//		means don't round.
	//	currency: String?
	//		iso4217 currency code
	//	symbol: String?
	//		localized currency symbol
	//	locale: String?
	//		override the locale used to determine formatting rules
}
=====*/

dojo.number.format = function(/*Number*/value, /*dojo.number.__formatOptions?*/options){
	// summary:
	//		Format a Number as a String, using locale-specific settings
	// description:
	//		Create a string from a Number using a known localized pattern.
	//		Formatting patterns appropriate to the locale are chosen from the
	//		CLDR http://unicode.org/cldr as well as the appropriate symbols and
	//		delimiters.  See http://www.unicode.org/reports/tr35/#Number_Elements
	// value:
	//		the number to be formatted.  If not a valid JavaScript number,
	//		return null.

	options = dojo.mixin({}, options || {});
	var locale = dojo.i18n.normalizeLocale(options.locale);
	var bundle = dojo.i18n.getLocalization("dojo.cldr", "number", locale);
	options.customs = bundle;
	var pattern = options.pattern || bundle[(options.type || "decimal") + "Format"];
	if(isNaN(value)){ return null; } // null
	return dojo.number._applyPattern(value, pattern, options); // String
};

//dojo.number._numberPatternRE = /(?:[#0]*,?)*[#0](?:\.0*#*)?/; // not precise, but good enough
dojo.number._numberPatternRE = /[#0,]*[#0](?:\.0*#*)?/; // not precise, but good enough

dojo.number._applyPattern = function(/*Number*/value, /*String*/pattern, /*dojo.number.__formatOptions?*/options){
	// summary:
	//		Apply pattern to format value as a string using options. Gives no
	//		consideration to local customs.
	// value:
	//		the number to be formatted.
	// pattern:
	//		a pattern string as described in
	//		http://www.unicode.org/reports/tr35/#Number_Format_Patterns
	// options: dojo.number.__formatOptions?
	//		_applyPattern is usually called via dojo.number.format() which
	//		populates an extra property in the options parameter, "customs".
	//		The customs object specifies group and decimal parameters if set.

	//TODO: support escapes
	options = options || {};
	var group = options.customs.group;
	var decimal = options.customs.decimal;

	var patternList = pattern.split(';');
	var positivePattern = patternList[0];
	pattern = patternList[(value < 0) ? 1 : 0] || ("-" + positivePattern);

	//TODO: only test against unescaped
	if(pattern.indexOf('%') != -1){
		value *= 100;
	}else if(pattern.indexOf('\u2030') != -1){
		value *= 1000; // per mille
	}else if(pattern.indexOf('\u00a4') != -1){
		group = options.customs.currencyGroup || group;//mixins instead?
		decimal = options.customs.currencyDecimal || decimal;// Should these be mixins instead?
		pattern = pattern.replace(/\u00a4{1,3}/, function(match){
			var prop = ["symbol", "currency", "displayName"][match.length-1];
			return options[prop] || options.currency || "";
		});
	}else if(pattern.indexOf('E') != -1){
		throw new Error("exponential notation not supported");
	}
	
	//TODO: support @ sig figs?
	var numberPatternRE = dojo.number._numberPatternRE;
	var numberPattern = positivePattern.match(numberPatternRE);
	if(!numberPattern){
		throw new Error("unable to find a number expression in pattern: "+pattern);
	}
	return pattern.replace(numberPatternRE,
		dojo.number._formatAbsolute(value, numberPattern[0], {decimal: decimal, group: group, places: options.places}));
}

dojo.number.round = function(/*Number*/value, /*Number*/places, /*Number?*/multiple){
	//	summary:
	//		Rounds the number at the given number of places
	//	value:
	//		the number to round
	//	places:
	//		the number of decimal places where rounding takes place
	//	multiple:
	//		rounds next place to nearest multiple

	var pieces = String(value).split(".");
	var length = (pieces[1] && pieces[1].length) || 0;
	if(length > places){
		var factor = Math.pow(10, places);
		if(multiple > 0){factor *= 10/multiple;places++;} //FIXME
		value = Math.round(value * factor)/factor;

		// truncate to remove any residual floating point values
		pieces = String(value).split(".");
		length = (pieces[1] && pieces[1].length) || 0;
		if(length > places){
			pieces[1] = pieces[1].substr(0, places);
			value = Number(pieces.join("."));
		}
	}
	return value; //Number
}

/*=====
dojo.number.__formatAbsoluteOptions = function(kwArgs){
	//	decimal: String?
	//		the decimal separator
	//	group: String?
	//		the group separator
	//	places: Integer?
	//		number of decimal places
	//	round: Number?
	//		5 rounds to nearest .5; 0 rounds to nearest whole (default). -1
	//		means don't round.
}
=====*/

dojo.number._formatAbsolute = function(/*Number*/value, /*String*/pattern, /*dojo.number.__formatAbsoluteOptions?*/options){
	// summary: 
	//		Apply numeric pattern to absolute value using options. Gives no
	//		consideration to local customs.
	// value:
	//		the number to be formatted, ignores sign
	// pattern:
	//		the number portion of a pattern (e.g. #,##0.00)
	options = options || {};
	if(options.places === true){options.places=0;}
	if(options.places === Infinity){options.places=6;} // avoid a loop; pick a limit

	var patternParts = pattern.split(".");
	var maxPlaces = (options.places >= 0) ? options.places : (patternParts[1] && patternParts[1].length) || 0;
	if(!(options.round < 0)){
		value = dojo.number.round(value, maxPlaces, options.round);
	}

	var valueParts = String(Math.abs(value)).split(".");
	var fractional = valueParts[1] || "";
	if(options.places){
		valueParts[1] = dojo.string.pad(fractional.substr(0, options.places), options.places, '0', true);
	}else if(patternParts[1] && options.places !== 0){
		// Pad fractional with trailing zeros
		var pad = patternParts[1].lastIndexOf("0") + 1;
		if(pad > fractional.length){
			valueParts[1] = dojo.string.pad(fractional, pad, '0', true);
		}

		// Truncate fractional
		var places = patternParts[1].length;
		if(places < fractional.length){
			valueParts[1] = fractional.substr(0, places);
		}
	}else{
		if(valueParts[1]){ valueParts.pop(); }
	}

	// Pad whole with leading zeros
	var patternDigits = patternParts[0].replace(',', '');
	pad = patternDigits.indexOf("0");
	if(pad != -1){
		pad = patternDigits.length - pad;
		if(pad > valueParts[0].length){
			valueParts[0] = dojo.string.pad(valueParts[0], pad);
		}

		// Truncate whole
		if(patternDigits.indexOf("#") == -1){
			valueParts[0] = valueParts[0].substr(valueParts[0].length - pad);
		}
	}

	// Add group separators
	var index = patternParts[0].lastIndexOf(',');
	var groupSize, groupSize2;
	if(index != -1){
		groupSize = patternParts[0].length - index - 1;
		var remainder = patternParts[0].substr(0, index);
		index = remainder.lastIndexOf(',');
		if(index != -1){
			groupSize2 = remainder.length - index - 1;
		}
	}
	var pieces = [];
	for(var whole = valueParts[0]; whole;){
		var off = whole.length - groupSize;
		pieces.push((off > 0) ? whole.substr(off) : whole);
		whole = (off > 0) ? whole.slice(0, off) : "";
		if(groupSize2){
			groupSize = groupSize2;
			delete groupSize2;
		}
	}
	valueParts[0] = pieces.reverse().join(options.group || ",");

	return valueParts.join(options.decimal || ".");
};

/*=====
dojo.number.__regexpOptions = function(kwArgs){
	//	pattern: String?
	//		override pattern with this string.  Default is provided based on
	//		locale.
	//	type: String?
	//		choose a format type based on the locale from the following:
	//		decimal, scientific, percent, currency. decimal by default.
	//	locale: String?
	//		override the locale used to determine formatting rules
	//	strict: Boolean?
	//		strict parsing, false by default
	//	places: Number|String?
	//		number of decimal places to accept: Infinity, a positive number, or
	//		a range "n,m".  By default, defined by pattern.
}
=====*/
dojo.number.regexp = function(/*dojo.number.__regexpOptions?*/options){
	//	summary:
	//		Builds the regular needed to parse a number
	//	description:
	//		Returns regular expression with positive and negative match, group
	//		and decimal separators
	return dojo.number._parseInfo(options).regexp; // String
}

dojo.number._parseInfo = function(/*Object?*/options){
	options = options || {};
	var locale = dojo.i18n.normalizeLocale(options.locale);
	var bundle = dojo.i18n.getLocalization("dojo.cldr", "number", locale);
	var pattern = options.pattern || bundle[(options.type || "decimal") + "Format"];
//TODO: memoize?
	var group = bundle.group;
	var decimal = bundle.decimal;
	var factor = 1;

	if(pattern.indexOf('%') != -1){
		factor /= 100;
	}else if(pattern.indexOf('\u2030') != -1){
		factor /= 1000; // per mille
	}else{
		var isCurrency = pattern.indexOf('\u00a4') != -1;
		if(isCurrency){
			group = bundle.currencyGroup || group;
			decimal = bundle.currencyDecimal || decimal;
		}
	}

	//TODO: handle quoted escapes
	var patternList = pattern.split(';');
	if(patternList.length == 1){
		patternList.push("-" + patternList[0]);
	}

	var re = dojo.regexp.buildGroupRE(patternList, function(pattern){
		pattern = "(?:"+dojo.regexp.escapeString(pattern, '.')+")";
		return pattern.replace(dojo.number._numberPatternRE, function(format){
			var flags = {
				signed: false,
				separator: options.strict ? group : [group,""],
				fractional: options.fractional,
				decimal: decimal,
				exponent: false};
			var parts = format.split('.');
			var places = options.places;
			if(parts.length == 1 || places === 0){flags.fractional = false;}
			else{
				if(typeof places == "undefined"){ places = parts[1].lastIndexOf('0')+1; }
				if(places && options.fractional == undefined){flags.fractional = true;} // required fractional, unless otherwise specified
				if(!options.places && (places < parts[1].length)){ places += "," + parts[1].length; }
				flags.places = places;
			}
			var groups = parts[0].split(',');
			if(groups.length>1){
				flags.groupSize = groups.pop().length;
				if(groups.length>1){
					flags.groupSize2 = groups.pop().length;
				}
			}
			return "("+dojo.number._realNumberRegexp(flags)+")";
		});
	}, true);

	if(isCurrency){
		// substitute the currency symbol for the placeholder in the pattern
		re = re.replace(/(\s*)(\u00a4{1,3})(\s*)/g, function(match, before, target, after){
			var prop = ["symbol", "currency", "displayName"][target.length-1];
			var symbol = dojo.regexp.escapeString(options[prop] || options.currency || "");
			before = before ? "\\s" : "";
			after = after ? "\\s" : "";
			if(!options.strict){
				if(before){before += "*";}
				if(after){after += "*";}
				return "(?:"+before+symbol+after+")?";
			}
			return before+symbol+after;
		});
	}

//TODO: substitute localized sign/percent/permille/etc.?

	// normalize whitespace and return
	return {regexp: re.replace(/[\xa0 ]/g, "[\\s\\xa0]"), group: group, decimal: decimal, factor: factor}; // Object
}

/*=====
dojo.number.__parseOptions = function(kwArgs){
	//	pattern: String
	//		override pattern with this string.  Default is provided based on
	//		locale.
	//	type: String?
	//		choose a format type based on the locale from the following:
	//		decimal, scientific, percent, currency. decimal by default.
	//	locale: String
	//		override the locale used to determine formatting rules
	//	strict: Boolean?
	//		strict parsing, false by default
	//	currency: Object
	//		object with currency information
}
=====*/
dojo.number.parse = function(/*String*/expression, /*dojo.number.__parseOptions?*/options){
	// summary:
	//		Convert a properly formatted string to a primitive Number, using
	//		locale-specific settings.
	// description:
	//		Create a Number from a string using a known localized pattern.
	//		Formatting patterns are chosen appropriate to the locale.
	//		Formatting patterns are implemented using the syntax described at
	//		*URL*
	// expression:
	//		A string representation of a Number
	var info = dojo.number._parseInfo(options);
	var results = (new RegExp("^"+info.regexp+"$")).exec(expression);
	if(!results){
		return NaN; //NaN
	}
	var absoluteMatch = results[1]; // match for the positive expression
	if(!results[1]){
		if(!results[2]){
			return NaN; //NaN
		}
		// matched the negative pattern
		absoluteMatch =results[2];
		info.factor *= -1;
	}

	// Transform it to something Javascript can parse as a number.  Normalize
	// decimal point and strip out group separators or alternate forms of whitespace
	absoluteMatch = absoluteMatch.
		replace(new RegExp("["+info.group + "\\s\\xa0"+"]", "g"), "").
		replace(info.decimal, ".");
	// Adjust for negative sign, percent, etc. as necessary
	return Number(absoluteMatch) * info.factor; //Number
};

/*=====
dojo.number.__realNumberRegexpFlags = function(kwArgs){
	//	places: Number?
	//		The integer number of decimal places or a range given as "n,m".  If
	//		not given, the decimal part is optional and the number of places is
	//		unlimited.
	//	decimal: String?
	//		A string for the character used as the decimal point.  Default
	//		is ".".
	//	fractional: Boolean|Array?
	//		Whether decimal places are allowed.  Can be true, false, or [true,
	//		false].  Default is [true, false]
	//	exponent: Boolean|Array?
	//		Express in exponential notation.  Can be true, false, or [true,
	//		false]. Default is [true, false], (i.e. will match if the
	//		exponential part is present are not).
	//	eSigned: Boolean|Array?
	//		The leading plus-or-minus sign on the exponent.  Can be true,
	//		false, or [true, false].  Default is [true, false], (i.e. will
	//		match if it is signed or unsigned).  flags in regexp.integer can be
	//		applied.
}
=====*/

dojo.number._realNumberRegexp = function(/*dojo.number.__realNumberRegexpFlags?*/flags){
	// summary:
	//		Builds a regular expression to match a real number in exponential
	//		notation
	// flags:
	//		An object

	// assign default values to missing paramters
	flags = flags || {};
	if(typeof flags.places == "undefined"){ flags.places = Infinity; }
	if(typeof flags.decimal != "string"){ flags.decimal = "."; }
	if(typeof flags.fractional == "undefined" || /^0/.test(flags.places)){ flags.fractional = [true, false]; }
	if(typeof flags.exponent == "undefined"){ flags.exponent = [true, false]; }
	if(typeof flags.eSigned == "undefined"){ flags.eSigned = [true, false]; }

	// integer RE
	var integerRE = dojo.number._integerRegexp(flags);

	// decimal RE
	var decimalRE = dojo.regexp.buildGroupRE(flags.fractional,
		function(q){
			var re = "";
			if(q && (flags.places!==0)){
				re = "\\" + flags.decimal;
				if(flags.places == Infinity){ 
					re = "(?:" + re + "\\d+)?"; 
				}else{
					re += "\\d{" + flags.places + "}"; 
				}
			}
			return re;
		},
		true
	);

	// exponent RE
	var exponentRE = dojo.regexp.buildGroupRE(flags.exponent,
		function(q){ 
			if(q){ return "([eE]" + dojo.number._integerRegexp({ signed: flags.eSigned}) + ")"; }
			return ""; 
		}
	);

	// real number RE
	var realRE = integerRE + decimalRE;
	// allow for decimals without integers, e.g. .25
	if(decimalRE){realRE = "(?:(?:"+ realRE + ")|(?:" + decimalRE + "))";}
	return realRE + exponentRE; // String
};

/*=====
dojo.number.__integerRegexpFlags = function(kwArgs){
	//	signed: Boolean?
	//		The leading plus-or-minus sign. Can be true, false, or [true,
	//		false]. Default is [true, false], (i.e. will match if it is signed
	//		or unsigned).
	//	separator: String?
	//		The character used as the thousands separator. Default is no
	//		separator. For more than one symbol use an array, e.g. [",", ""],
	//		makes ',' optional.
	//	groupSize: Number?
	//		group size between separators
	//	flags.groupSize2: Number?
	//		second grouping (for India)
}
=====*/

dojo.number._integerRegexp = function(/*dojo.number.__integerRegexpFlags?*/flags){
	// summary: 
	//		Builds a regular expression that matches an integer
	// flags: 
	//		An object

	// assign default values to missing paramters
	flags = flags || {};
	if(typeof flags.signed == "undefined"){ flags.signed = [true, false]; }
	if(typeof flags.separator == "undefined"){
		flags.separator = "";
	}else if(typeof flags.groupSize == "undefined"){
		flags.groupSize = 3;
	}
	// build sign RE
	var signRE = dojo.regexp.buildGroupRE(flags.signed,
		function(q) { return q ? "[-+]" : ""; },
		true
	);

	// number RE
	var numberRE = dojo.regexp.buildGroupRE(flags.separator,
		function(sep){
			if(!sep){
				return "(?:0|[1-9]\\d*)";
			}

			sep = dojo.regexp.escapeString(sep);
			if(sep == " "){ sep = "\\s"; }
			else if(sep == "\xa0"){ sep = "\\s\\xa0"; }

			var grp = flags.groupSize, grp2 = flags.groupSize2;
			if(grp2){
				var grp2RE = "(?:0|[1-9]\\d{0," + (grp2-1) + "}(?:[" + sep + "]\\d{" + grp2 + "})*[" + sep + "]\\d{" + grp + "})";
				return ((grp-grp2) > 0) ? "(?:" + grp2RE + "|(?:0|[1-9]\\d{0," + (grp-1) + "}))" : grp2RE;
			}
			return "(?:0|[1-9]\\d{0," + (grp-1) + "}(?:[" + sep + "]\\d{" + grp + "})*)";
		},
		true
	);

	// integer RE
	return signRE + numberRE; // String
}

}

if(!dojo._hasResource["dijit.form.NumberTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.NumberTextBox"] = true;
dojo.provide("dijit.form.NumberTextBox");




dojo.declare(
	"dijit.form.NumberTextBoxMixin",
	null,
	{
		// summary:
		//		A mixin for all number textboxes
		regExpGen: dojo.number.regexp,

		format: function(/*Number*/ value, /*Object*/ constraints){
			if(isNaN(value)){ return ""; }
			return dojo.number.format(value, constraints);
		},

		parse: dojo.number.parse,

		filter: function(/*Number*/ value){
			if(typeof value == "string"){ return this.inherited('filter', arguments); }
			return (isNaN(value) ? '' : value);
		},

		value: NaN
	}
);

dojo.declare(
	"dijit.form.NumberTextBox",
	[dijit.form.RangeBoundTextBox,dijit.form.NumberTextBoxMixin],
	{
		// summary:
		//		A validating, serializable, range-bound text box.
		// constraints object: min, max, places
	}
);

}

if(!dojo._hasResource["dijit._editor.selection"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.selection"] = true;
dojo.provide("dijit._editor.selection");

// FIXME:
//		all of these methods branch internally for IE. This is probably
//		sub-optimal in terms of runtime performance. We should investigate the
//		size difference for differentiating at definition time.

dojo.mixin(dijit._editor.selection, {
	getType: function(){
		// summary: Get the selection type (like document.select.type in IE).
		if(dojo.doc["selection"]){ //IE
			return dojo.doc.selection.type.toLowerCase();
		}else{
			var stype = "text";

			// Check if the actual selection is a CONTROL (IMG, TABLE, HR, etc...).
			var oSel;
			try{
				oSel = dojo.global.getSelection();
			}catch(e){ /*squelch*/ }

			if(oSel && oSel.rangeCount==1){
				var oRange = oSel.getRangeAt(0);
				if(	(oRange.startContainer == oRange.endContainer) &&
					((oRange.endOffset - oRange.startOffset) == 1) &&
					(oRange.startContainer.nodeType != 3 /* text node*/)
				){
					stype = "control";
				}
			}
			return stype;
		}
	},

	getSelectedText: function(){
		// summary:
		//		Return the text (no html tags) included in the current selection or null if no text is selected
		if(dojo.doc["selection"]){ //IE
			if(dijit._editor.selection.getType() == 'control'){
				return null;
			}
			return dojo.doc.selection.createRange().text;
		}else{
			var selection = dojo.global.getSelection();
			if(selection){
				return selection.toString();
			}
		}
	},

	getSelectedHtml: function(){
		// summary:
		//		Return the html of the current selection or null if unavailable
		if(dojo.doc["selection"]){ //IE
			if(dijit._editor.selection.getType() == 'control'){
				return null;
			}
			return dojo.doc.selection.createRange().htmlText;
		}else{
			var selection = dojo.global.getSelection();
			if(selection && selection.rangeCount){
				var frag = selection.getRangeAt(0).cloneContents();
				var div = document.createElement("div");
				div.appendChild(frag);
				return div.innerHTML;
			}
			return null;
		}
	},

	getSelectedElement: function(){
		// summary:
		//		Retrieves the selected element (if any), just in the case that
		//		a single element (object like and image or a table) is
		//		selected.
		if(this.getType() == "control"){
			if(dojo.doc["selection"]){ //IE
				var range = dojo.doc.selection.createRange();
				if(range && range.item){
					return dojo.doc.selection.createRange().item(0);
				}
			}else{
				var selection = dojo.global.getSelection();
				return selection.anchorNode.childNodes[ selection.anchorOffset ];
			}
		}
	},

	getParentElement: function(){
		// summary:
		//		Get the parent element of the current selection
		if(this.getType() == "control"){
			var p = this.getSelectedElement();
			if(p){ return p.parentNode; }
		}else{
			if(dojo.doc["selection"]){ //IE
				return dojo.doc.selection.createRange().parentElement();
			}else{
				var selection = dojo.global.getSelection();
				if(selection){
					var node = selection.anchorNode;

					while(node && (node.nodeType != 1)){ // not an element
						node = node.parentNode;
					}

					return node;
				}
			}
		}
	},

	hasAncestorElement: function(/*String*/tagName /* ... */){
		// summary:
		// 		Check whether current selection has a  parent element which is
		// 		of type tagName (or one of the other specified tagName)
		return (this.getAncestorElement.apply(this, arguments) != null);
	},

	getAncestorElement: function(/*String*/tagName /* ... */){
		// summary:
		//		Return the parent element of the current selection which is of
		//		type tagName (or one of the other specified tagName)

		var node = this.getSelectedElement() || this.getParentElement();
		return this.getParentOfType(node, arguments);
	},

	isTag: function(/*DomNode*/node, /*Array*/tags){
		if(node && node.tagName){
			var _nlc = node.tagName.toLowerCase();
			for(var i=0; i<tags.length; i++){
				var _tlc = String(tags[i]).toLowerCase();
				if(_nlc == _tlc){
					return _tlc;
				}
			}
		}
		return "";
	},

	getParentOfType: function(/*DomNode*/node, /*Array*/tags){
		while(node){
			if(this.isTag(node, tags).length){
				return node;
			}
			node = node.parentNode;
		}
		return null;
	},

	remove: function(){
		// summary: delete current selection
		var _s = dojo.doc.selection;
		if(_s){ //IE
			if(_s.type.toLowerCase() != "none"){
				_s.clear();
			}
			return _s;
		}else{
			_s = dojo.global.getSelection();
			_s.deleteFromDocument();
			return _s;
		}
	},

	selectElementChildren: function(/*DomNode*/element,/*Boolean?*/nochangefocus){
		// summary:
		//		clear previous selection and select the content of the node
		//		(excluding the node itself)
		var _window = dojo.global;
		var _document = dojo.doc;
		element = dojo.byId(element);
		if(_document.selection && dojo.body().createTextRange){ // IE
			var range = element.ownerDocument.body.createTextRange();
			range.moveToElementText(element);
			if(!nochangefocus){
				range.select();
			}
		}else if(_window["getSelection"]){
			var selection = _window.getSelection();
			if(selection["setBaseAndExtent"]){ // Safari
				selection.setBaseAndExtent(element, 0, element, element.innerText.length - 1);
			}else if(selection["selectAllChildren"]){ // Mozilla
				selection.selectAllChildren(element);
			}
		}
	},

	selectElement: function(/*DomNode*/element,/*Boolean?*/nochangefocus){
		// summary:
		//		clear previous selection and select element (including all its children)
		var _document = dojo.doc;
		element = dojo.byId(element);
		if(_document.selection && dojo.body().createTextRange){ // IE
			try{
				var range = dojo.body().createControlRange();
				range.addElement(element);
				if(!nochangefocus){
					range.select();
				}
			}catch(e){
				this.selectElementChildren(element,nochangefocus);
			}
		}else if(dojo.global["getSelection"]){
			var selection = dojo.global.getSelection();
			// FIXME: does this work on Safari?
			if(selection["removeAllRanges"]){ // Mozilla
				var range = _document.createRange() ;
				range.selectNode(element) ;
				selection.removeAllRanges() ;
				selection.addRange(range) ;
			}
		}
	}
});

}

if(!dojo._hasResource["dijit._editor.RichText"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.RichText"] = true;
dojo.provide("dijit._editor.RichText");






// used to restore content when user leaves this page then comes back
// but do not try doing document.write if we are using xd loading.
// document.write will only work if RichText.js is included in the dojo.js
// file. If it is included in dojo.js and you want to allow rich text saving
// for back/forward actions, then set djConfig.allowXdRichTextSave = true.
if(!djConfig["useXDomain"] || djConfig["allowXdRichTextSave"]){
	if(dojo._postLoad){
		(function(){
			var savetextarea = dojo.doc.createElement('textarea');
			savetextarea.id = "dijit._editor.RichText.savedContent";
			var s = savetextarea.style;
			s.display='none';
			s.position='absolute';
			s.top="-100px";
			s.left="-100px"
			s.height="3px";
			s.width="3px";
			dojo.body().appendChild(savetextarea);
		})();
	}else{
		//dojo.body() is not available before onLoad is fired
		try {
			dojo.doc.write('<textarea id="dijit._editor.RichText.savedContent" ' +
				'style="display:none;position:absolute;top:-100px;left:-100px;height:3px;width:3px;overflow:hidden;"></textarea>');
		}catch(e){ }
	}
}
dojo.declare("dijit._editor.RichText", [ dijit._Widget ], {
	constructor: function(){
		// summary:
		//		dijit._editor.RichText is the core of the WYSIWYG editor in dojo, which
		//		provides the basic editing features. It also encapsulates the differences
		//		of different js engines for various browsers
		//
		// contentPreFilters: Array
		//		pre content filter function register array.
		//		these filters will be executed before the actual
		//		editing area get the html content
		this.contentPreFilters = [];

		// contentPostFilters: Array
		//		post content filter function register array.
		//		these will be used on the resulting html
		//		from contentDomPostFilters. The resuling
		//		content is the final html (returned by getValue())
		this.contentPostFilters = [];

		// contentDomPreFilters: Array
		//		pre content dom filter function register array.
		//		these filters are applied after the result from
		//		contentPreFilters are set to the editing area
		this.contentDomPreFilters = [];

		// contentDomPostFilters: Array
		//		post content dom filter function register array.
		//		these filters are executed on the editing area dom
		//		the result from these will be passed to contentPostFilters
		this.contentDomPostFilters = [];

		// editingAreaStyleSheets: Array
		//		array to store all the stylesheets applied to the editing area
		this.editingAreaStyleSheets=[];

		this._keyHandlers = {};
		this.contentPreFilters.push(dojo.hitch(this, "_preFixUrlAttributes"));
		if(dojo.isMoz){
			this.contentPreFilters.push(this._fixContentForMoz);
		}
		//this.contentDomPostFilters.push(this._postDomFixUrlAttributes);

		this.onLoadDeferred = new dojo.Deferred();
	},

	// inheritWidth: Boolean
	//		whether to inherit the parent's width or simply use 100%
	inheritWidth: false,

	// focusOnLoad: Boolean
	//		whether focusing into this instance of richtext when page onload
	focusOnLoad: false,

	// name: String
	//		If a save name is specified the content is saved and restored when the user
	//		leave this page can come back, or if the editor is not properly closed after
	//		editing has started.
	name: "",

	// styleSheets: String
	//		semicolon (";") separated list of css files for the editing area
	styleSheets: "",

	// _content: String
	//		temporary content storage
	_content: "",

	// height: String
	//		set height to fix the editor at a specific height, with scrolling.
	//		By default, this is 300px. If you want to have the editor always
	//		resizes to accommodate the content, use AlwaysShowToolbar plugin
	//		and set height=""
	height: "300px",

	// minHeight: String
	//		The minimum height that the editor should have
	minHeight: "1em",
	
	// isClosed: Boolean
	isClosed: true,

	// isLoaded: Boolean
	isLoaded: false,

	// _SEPARATOR: String
	//		used to concat contents from multiple textareas into a single string
	_SEPARATOR: "@@**%%__RICHTEXTBOUNDRY__%%**@@",

	// onLoadDeferred: dojo.Deferred
	//		deferred which is fired when the editor finishes loading
	onLoadDeferred: null,

	postCreate: function(){
		// summary: init
		dojo.publish("dijit._editor.RichText::init", [this]);
		this.open();
		this.setupDefaultShortcuts();
	},

	setupDefaultShortcuts: function(){
		// summary: add some default key handlers
		// description:
		// 		Overwrite this to setup your own handlers. The default
		// 		implementation does not use Editor commands, but directly
		//		executes the builtin commands within the underlying browser
		//		support.
		var ctrl = this.KEY_CTRL;
		var exec = function(cmd, arg){
			return arguments.length == 1 ? function(){ this.execCommand(cmd); } :
				function(){ this.execCommand(cmd, arg); }
		}
		this.addKeyHandler("b", ctrl, exec("bold"));
		this.addKeyHandler("i", ctrl, exec("italic"));
		this.addKeyHandler("u", ctrl, exec("underline"));
		this.addKeyHandler("a", ctrl, exec("selectall"));
		this.addKeyHandler("s", ctrl, function () { this.save(true); });

		this.addKeyHandler("1", ctrl, exec("formatblock", "h1"));
		this.addKeyHandler("2", ctrl, exec("formatblock", "h2"));
		this.addKeyHandler("3", ctrl, exec("formatblock", "h3"));
		this.addKeyHandler("4", ctrl, exec("formatblock", "h4"));

		this.addKeyHandler("\\", ctrl, exec("insertunorderedlist"));
		if(!dojo.isIE){
			this.addKeyHandler("Z", ctrl, exec("redo"));
		}
	},

	// events: Array
	//		 events which should be connected to the underlying editing area
	events: ["onKeyPress", "onKeyDown", "onKeyUp", "onClick"],

	// events: Array
	//		 events which should be connected to the underlying editing
	//		 area, events in this array will be addListener with
	//		 capture=true
	captureEvents: [],

	_editorCommandsLocalized: false,
	_localizeEditorCommands: function(){
		if(this._editorCommandsLocalized){
			return;
		}
		this._editorCommandsLocalized = true;

		//in IE, names for blockformat is locale dependent, so we cache the values here

		//if the normal way fails, we try the hard way to get the list

		//do not use _cacheLocalBlockFormatNames here, as it will
		//trigger security warning in IE7

		//in the array below, ul can not come directly after ol,
		//otherwise the queryCommandValue returns Normal for it
		var formats = ['p', 'pre', 'address', 'h1', 'h2', 'h3', 'h4', 'h5', 'h6', 'ol', 'div', 'ul'];
		var localhtml = "", format, i=0;
		while((format=formats[i++])){
			if(format.charAt(1) != 'l'){
				localhtml += "<"+format+"><span>content</span></"+format+">";
			}else{
				localhtml += "<"+format+"><li>content</li></"+format+">";
			}
		}
		//queryCommandValue returns empty if we hide editNode, so move it out of screen temporary
		var div=document.createElement('div');
		div.style.position = "absolute";
		div.style.left = "-2000px";
		div.style.top = "-2000px";
		document.body.appendChild(div);
		div.innerHTML = localhtml;
		var node = div.firstChild;
		while(node){
			dijit._editor.selection.selectElement(node.firstChild);
			dojo.withGlobal(this.window, "selectElement", dijit._editor.selection, [node.firstChild]);
			var nativename = node.tagName.toLowerCase();
			this._local2NativeFormatNames[nativename] = document.queryCommandValue("formatblock");//this.queryCommandValue("formatblock");
			this._native2LocalFormatNames[this._local2NativeFormatNames[nativename]] = nativename;
			node = node.nextSibling;
		}
		document.body.removeChild(div);
	},

	open: function(/*DomNode?*/element){
		// summary:
		//		Transforms the node referenced in this.domNode into a rich text editing
		//		node. This will result in the creation and replacement with an <iframe>
		//		if designMode(FF)/contentEditable(IE) is used.

		if((!this.onLoadDeferred)||(this.onLoadDeferred.fired >= 0)){
			this.onLoadDeferred = new dojo.Deferred();
		}

		if(!this.isClosed){ this.close(); }
		dojo.publish("dijit._editor.RichText::open", [ this ]);

		this._content = "";
		if((arguments.length == 1)&&(element["nodeName"])){ this.domNode = element; } // else unchanged

		if(	(this.domNode["nodeName"])&&
			(this.domNode.nodeName.toLowerCase() == "textarea")){
			// if we were created from a textarea, then we need to create a
			// new editing harness node.
			this.textarea = this.domNode;
			this.name=this.textarea.name;
			var html = this._preFilterContent(this.textarea.value);
			this.domNode = dojo.doc.createElement("div");
			this.domNode.setAttribute('widgetId',this.id);
			this.textarea.removeAttribute('widgetId');
			this.domNode.cssText = this.textarea.cssText;
			this.domNode.className += " "+this.textarea.className;
			dojo.place(this.domNode, this.textarea, "before");
			var tmpFunc = dojo.hitch(this, function(){
				//some browsers refuse to submit display=none textarea, so
				//move the textarea out of screen instead
				with(this.textarea.style){
					display = "block";
					position = "absolute";
					left = top = "-1000px";

					if(dojo.isIE){ //nasty IE bug: abnormal formatting if overflow is not hidden
						this.__overflow = overflow;
						overflow = "hidden";
					}
				}
			});
			if(dojo.isIE){
				setTimeout(tmpFunc, 10);
			}else{
				tmpFunc();
			}

			// this.domNode.innerHTML = html;

//				if(this.textarea.form){
//					// FIXME: port: this used to be before advice!!!
//					dojo.connect(this.textarea.form, "onsubmit", this, function(){
//						// FIXME: should we be calling close() here instead?
//						this.textarea.value = this.getValue();
//					});
//				}
		}else{
			var html = this._preFilterContent(this.getNodeChildrenHtml(this.domNode));
			this.domNode.innerHTML = '';
		}
		if(html == ""){ html = "&nbsp;"; }

		var content = dojo.contentBox(this.domNode);
		// var content = dojo.contentBox(this.srcNodeRef);
		this._oldHeight = content.h;
		this._oldWidth = content.w;

		this.savedContent = html;

		// If we're a list item we have to put in a blank line to force the
		// bullet to nicely align at the top of text
		if(	(this.domNode["nodeName"]) &&
			(this.domNode.nodeName == "LI") ){
			this.domNode.innerHTML = " <br>";
		}

		this.editingArea = dojo.doc.createElement("div");
		this.domNode.appendChild(this.editingArea);

		if(this.name != "" && (!djConfig["useXDomain"] || djConfig["allowXdRichTextSave"])){
			var saveTextarea = dojo.byId("dijit._editor.RichText.savedContent");
			if(saveTextarea.value != ""){
				var datas = saveTextarea.value.split(this._SEPARATOR), i=0, dat;
				while((dat=datas[i++])){
					var data = dat.split(":");
					if(data[0] == this.name){
						html = data[1];
						datas.splice(i, 1);
						break;
					}
				}
			}

			// FIXME: need to do something different for Opera/Safari
			dojo.connect(window, "onbeforeunload", this, "_saveContent");
			// dojo.connect(window, "onunload", this, "_saveContent");
		}

		this.isClosed = false;
		// Safari's selections go all out of whack if we do it inline,
		// so for now IE is our only hero
		//if (typeof document.body.contentEditable != "undefined") {
		if(dojo.isIE || dojo.isSafari || dojo.isOpera){ // contentEditable, easy
			var ifr = this.iframe = dojo.doc.createElement('iframe');
			ifr.src = 'javascript:void(0)';
			this.editorObject = ifr;
			ifr.style.border = "none";
			ifr.style.width = "100%";
			ifr.frameBorder = 0;
//			ifr.style.scrolling = this.height ? "auto" : "vertical";
			this.editingArea.appendChild(ifr);
			this.window = ifr.contentWindow;
			this.document = this.window.document;
			this.document.open();
			this.document.write(this._getIframeDocTxt(html));
			this.document.close();

			if(dojo.isIE >= 7){
				if(this.height){
					ifr.style.height = this.height;
				}
				if(this.minHeight){
					ifr.style.minHeight = this.minHeight;
				}
			}else{
				ifr.style.height = this.height ? this.height : this.minHeight;
			}

			if(dojo.isIE){
				this._localizeEditorCommands();
			}

			this.onLoad();
		}else{ // designMode in iframe
			this._drawIframe(html);
		}

		// TODO: this is a guess at the default line-height, kinda works
		if(this.domNode.nodeName == "LI"){ this.domNode.lastChild.style.marginTop = "-1.2em"; }
		this.domNode.className += " RichTextEditable";
	},

	//static cache variables shared among all instance of this class
	_local2NativeFormatNames: {},
	_native2LocalFormatNames: {},
	_localizedIframeTitles: null,

	_getIframeDocTxt: function(/* String */ html){
		var _cs = dojo.getComputedStyle(this.domNode);
		if(!this.height && !dojo.isMoz){
			html="<div>"+html+"</div>";
		}
		var font = [ _cs.fontWeight, _cs.fontSize, _cs.fontFamily ].join(" ");

		// line height is tricky - applying a units value will mess things up.
		// if we can't get a non-units value, bail out.
		var lineHeight = _cs.lineHeight;
		if(lineHeight.indexOf("px") >= 0){
			lineHeight = parseFloat(lineHeight)/parseFloat(_cs.fontSize);
			// console.debug(lineHeight);
		}else if(lineHeight.indexOf("em")>=0){
			lineHeight = parseFloat(lineHeight);
		}else{
			lineHeight = "1.0";
		}
		return [
			this.isLeftToRight() ? "<html><head>" : "<html dir='rtl'><head>",
			(dojo.isMoz ? "<title>" + this._localizedIframeTitles.iframeEditTitle + "</title>" : ""),
			"<style>",
			"body,html {",
			"	background:transparent;",
			"	padding: 0;",
			"	margin: 0;",
			"}",
			// TODO: left positioning will cause contents to disappear out of view
			//	   if it gets too wide for the visible area
			"body{",
			"	top:0px; left:0px; right:0px;",
				((this.height||dojo.isOpera) ? "" : "position: fixed;"),
			"	font:", font, ";",
			// FIXME: IE 6 won't understand min-height?
			"	min-height:", this.minHeight, ";",
			"	line-height:", lineHeight,
			"}",
			"p{ margin: 1em 0 !important; }",
			(this.height ?
				"" : "body,html{overflow-y:hidden;/*for IE*/} body > div {overflow-x:auto;/*for FF to show vertical scrollbar*/}"
			),
			"li > ul:-moz-first-node, li > ol:-moz-first-node{ padding-top: 1.2em; } ",
			"li{ min-height:1.2em; }",
			"</style>",
			this._applyEditingAreaStyleSheets(),
			"</head><body>"+html+"</body></html>"
		].join(""); // String
	},

	_drawIframe: function(/*String*/html){
		// summary:
		//		Draws an iFrame using the existing one if one exists.
		//		Used by Mozilla, Safari, and Opera

		if(!this.iframe){
			var ifr = this.iframe = dojo.doc.createElement("iframe");
			// this.iframe.src = "about:blank";
			// document.body.appendChild(this.iframe);
			// console.debug(this.iframe.contentDocument.open());
			// dojo.body().appendChild(this.iframe);
			var ifrs = ifr.style;
			// ifrs.border = "1px solid black";
			ifrs.border = "none";
			ifrs.lineHeight = "0"; // squash line height
			ifrs.verticalAlign = "bottom";
//			ifrs.scrolling = this.height ? "auto" : "vertical";
			this.editorObject = this.iframe;
			// get screen reader text for mozilla here, too
			this._localizedIframeTitles = dojo.i18n.getLocalization("dijit", "Textarea");
			// need to find any associated label element and update iframe document title
			var label=dojo.query('label[for="'+this.id+'"]');
			if(label.length){
				this._localizedIframeTitles.iframeEditTitle = label[0].innerHTML + " " + this._localizedIframeTitles.iframeEditTitle;
			}
		}
		// opera likes this to be outside the with block
		//	this.iframe.src = "javascript:void(0)";//dojo.uri.dojoUri("src/widget/templates/richtextframe.html") + ((dojo.doc.domain != currentDomain) ? ("#"+dojo.doc.domain) : "");
		this.iframe.style.width = this.inheritWidth ? this._oldWidth : "100%";

		if(this.height){
			this.iframe.style.height = this.height;
		}else{
			this.iframe.height = this._oldHeight;
		}

		if(this.textarea){
			var tmpContent = this.srcNodeRef;
		}else{
			var tmpContent = dojo.doc.createElement('div');
			tmpContent.style.display="none";
			tmpContent.innerHTML = html;
			//append tmpContent to under the current domNode so that the margin
			//calculation below is correct
			this.editingArea.appendChild(tmpContent);
		}

		this.editingArea.appendChild(this.iframe);

		//do we want to show the content before the editing area finish loading here?
		//if external style sheets are used for the editing area, the appearance now
		//and after loading of the editing area won't be the same (and padding/margin
		//calculation above may not be accurate)
		//	tmpContent.style.display = "none";
		//	this.editingArea.appendChild(this.iframe);

		var _iframeInitialized = false;
		// console.debug(this.iframe);
		// var contentDoc = this.iframe.contentWindow.document;


		// note that on Safari lower than 420+, we have to get the iframe
		// by ID in order to get something w/ a contentDocument property

		var contentDoc = this.iframe.contentDocument;
		contentDoc.open();
		contentDoc.write(this._getIframeDocTxt(html));
		contentDoc.close();

		// now we wait for onload. Janky hack!
		var ifrFunc = dojo.hitch(this, function(){
			if(!_iframeInitialized){
				_iframeInitialized = true;
			}else{ return; }
			if(!this.editNode){
				try{
					if(this.iframe.contentWindow){
						this.window = this.iframe.contentWindow;
						this.document = this.iframe.contentWindow.document
					}else if(this.iframe.contentDocument){
						// for opera
						this.window = this.iframe.contentDocument.window;
						this.document = this.iframe.contentDocument;
					}
					if(!this.document.body){
						throw 'Error';
					}
				}catch(e){
					setTimeout(ifrFunc,500);
					_iframeInitialized = false;
					return;
				}

				dojo._destroyElement(tmpContent);
				this.document.designMode = "on";
				//	try{
				//	this.document.designMode = "on";
				// }catch(e){
				//	this._tryDesignModeOnClick=true;
				// }

				this.onLoad();
			}else{
				dojo._destroyElement(tmpContent);
				this.editNode.innerHTML = html;
				this.onDisplayChanged();
			}
			this._preDomFilterContent(this.editNode);
		});

		ifrFunc();
	},

	_applyEditingAreaStyleSheets: function(){
		// summary:
		//		apply the specified css files in styleSheets
		var files = [];
		if(this.styleSheets){
			files = this.styleSheets.split(';');
			this.styleSheets = '';
		}

		//empty this.editingAreaStyleSheets here, as it will be filled in addStyleSheet
		files = files.concat(this.editingAreaStyleSheets);
		this.editingAreaStyleSheets = [];

		var text='', i=0, url;
		while((url=files[i++])){
			var abstring = (new dojo._Url(dojo.global.location, url)).toString();
			this.editingAreaStyleSheets.push(abstring);
			text += '<link rel="stylesheet" type="text/css" href="'+abstring+'"/>'
		}
		return text;
	},

	addStyleSheet: function(/*dojo._Url*/uri){
		// summary:
		//		add an external stylesheet for the editing area
		// uri:	a dojo.uri.Uri pointing to the url of the external css file
		var url=uri.toString();

		//if uri is relative, then convert it to absolute so that it can be resolved correctly in iframe
		if(url.charAt(0) == '.' || (url.charAt(0) != '/' && !uri.host)){
			url = (new dojo._Url(dojo.global.location, url)).toString();
		}

		if(dojo.indexOf(this.editingAreaStyleSheets, url) > -1){
			console.debug("dijit._editor.RichText.addStyleSheet: Style sheet "+url+" is already applied to the editing area!");
			return;
		}

		this.editingAreaStyleSheets.push(url);
		if(this.document.createStyleSheet){ //IE
			this.document.createStyleSheet(url);
		}else{ //other browser
			var head = this.document.getElementsByTagName("head")[0];
			var stylesheet = this.document.createElement("link");
			with(stylesheet){
				rel="stylesheet";
				type="text/css";
				href=url;
			}
			head.appendChild(stylesheet);
		}
	},

	removeStyleSheet: function(/*dojo._Url*/uri){
		// summary:
		//		remove an external stylesheet for the editing area
		var url=uri.toString();
		//if uri is relative, then convert it to absolute so that it can be resolved correctly in iframe
		if(url.charAt(0) == '.' || (url.charAt(0) != '/' && !uri.host)){
			url = (new dojo._Url(dojo.global.location, url)).toString();
		}
		var index = dojo.indexOf(this.editingAreaStyleSheets, url);
		if(index == -1){
			console.debug("dijit._editor.RichText.removeStyleSheet: Style sheet "+url+" is not applied to the editing area so it can not be removed!");
			return;
		}
		delete this.editingAreaStyleSheets[index];
		dojo.withGlobal(this.window,'query', dojo, ['link:[href="'+url+'"]']).orphan()
	},

	disabled: false,
	_mozSettingProps: ['styleWithCSS','insertBrOnReturn'],
	setDisabled: function(/*Boolean*/ disabled){
		if(dojo.isIE || dojo.isSafari || dojo.isOpera){
			this.editNode.contentEditable=!disabled;
		}else{ //moz
			if(disabled){
				this._mozSettings=[false,this.blockNodeForEnter==='BR'];
			}
			this.document.designMode=(disabled?'off':'on');
			if(!disabled){
				dojo.forEach(this._mozSettingProps, function(s,i){
					this.document.execCommand(s,false,this._mozSettings[i]);
				},this);
			}
//			this.document.execCommand('contentReadOnly', false, disabled);
//				if(disabled){
//					this.blur(); //to remove the blinking caret
//				}
//				
		}
		this.disabled=disabled;
	},

/* Event handlers
 *****************/

	_isResized: function(){ return false; },

	onLoad: function(/* Event */ e){
		// summary: handler after the content of the document finishes loading
		this.isLoaded = true;
		if(this.height || dojo.isMoz){
			this.editNode=this.document.body;
		}else{
			this.editNode=this.document.body.firstChild;
		}
		this.editNode.contentEditable = true; //should do no harm in FF
		this._preDomFilterContent(this.editNode);

		var events=this.events.concat(this.captureEvents),i=0,et;
		while((et=events[i++])){
			this.connect(this.document, et.toLowerCase(), et);
		}
		if(!dojo.isIE){
			try{ // sanity check for Mozilla
//					this.document.execCommand("useCSS", false, true); // old moz call
				this.document.execCommand("styleWithCSS", false, false); // new moz call
				//this.document.execCommand("insertBrOnReturn", false, false); // new moz call
			}catch(e2){ }
			// FIXME: when scrollbars appear/disappear this needs to be fired
		}else{ // IE contentEditable
			// give the node Layout on IE
			this.editNode.style.zoom = 1.0;
		}

		if(this.focusOnLoad){
			this.focus();
		}

		this.onDisplayChanged(e);
		if(this.onLoadDeferred){
			this.onLoadDeferred.callback(true);
		}
	},

	onKeyDown: function(/* Event */ e){
		// summary: Fired on keydown

//		 console.info("onkeydown:", e.keyCode);

		// we need this event at the moment to get the events from control keys
		// such as the backspace. It might be possible to add this to Dojo, so that
		// keyPress events can be emulated by the keyDown and keyUp detection.
		if(dojo.isIE){
			if(e.keyCode === dojo.keys.BACKSPACE && this.document.selection.type === "Control"){
				// IE has a bug where if a non-text object is selected in the editor,
		  // hitting backspace would act as if the browser's back button was
		  // clicked instead of deleting the object. see #1069
				dojo.stopEvent(e);
				this.execCommand("delete");
			}else if(	(65 <= e.keyCode&&e.keyCode <= 90) ||
				(e.keyCode>=37&&e.keyCode<=40) // FIXME: get this from connect() instead!
			){ //arrow keys
				e.charCode = e.keyCode;
				this.onKeyPress(e);
			}
		}
		else if (dojo.isMoz){
			if(e.keyCode == dojo.keys.TAB && !e.shiftKey && !e.ctrlKey && !e.altKey && this.iframe){
				// update iframe document title for screen reader
				this.iframe.contentDocument.title = this._localizedIframeTitles.iframeFocusTitle;
				
				// Place focus on the iframe. A subsequent tab or shift tab will put focus
				// on the correct control.
				this.iframe.focus();  // this.focus(); won't work
				dojo.stopEvent(e);
			}else if (e.keyCode == dojo.keys.TAB && e.shiftKey){
				// if there is a toolbar, set focus to it, otherwise ignore
				if (this.toolbar){
					this.toolbar.focus();
				}
				dojo.stopEvent(e);
			}
		}
	},

	onKeyUp: function(e){
		// summary: Fired on keyup
		return;
	},

	KEY_CTRL: 1,
	KEY_SHIFT: 2,

	onKeyPress: function(e){
		// summary: Fired on keypress

//		 console.info("onkeypress:", e.keyCode);

		// handle the various key events
		var modifiers = e.ctrlKey ? this.KEY_CTRL : 0 | e.shiftKey?this.KEY_SHIFT : 0;

		var key = e.keyChar||e.keyCode;
		if(this._keyHandlers[key]){
			// console.debug("char:", e.key);
			var handlers = this._keyHandlers[key], i = 0, h;
			while((h = handlers[i++])){
				if(modifiers == h.modifiers){
					if(!h.handler.apply(this,arguments)){
						e.preventDefault();
					}
					break;
				}
			}
		}

		// function call after the character has been inserted
		setTimeout(dojo.hitch(this, function(){
			this.onKeyPressed(e);
		}), 1);
	},

	addKeyHandler: function(/*String*/key, /*Int*/modifiers, /*Function*/handler){
		// summary: add a handler for a keyboard shortcut
		if(!dojo.isArray(this._keyHandlers[key])){ this._keyHandlers[key] = []; }
		this._keyHandlers[key].push({
			modifiers: modifiers || 0,
			handler: handler
		});
	},

	onKeyPressed: function(/*Event*/e){
		this.onDisplayChanged(/*e*/); // can't pass in e
	},

	onClick: function(/*Event*/e){
//			console.debug('onClick',this._tryDesignModeOnClick);
//			if(this._tryDesignModeOnClick){
//				try{
//					this.document.designMode='on';
//					this._tryDesignModeOnClick=false;
//				}catch(e){}
//			}
		this.onDisplayChanged(e); },
	_onBlur: function(e){
		var _c=this.getValue(true);
		if(_c!=this.savedContent){
			this.onChange(_c);
			this.savedContent=_c;
		}
		if (dojo.isMoz && this.iframe){
			this.iframe.contentDocument.title = this._localizedIframeTitles.iframeEditTitle;
		} 
//			console.info('_onBlur')
	},
	_initialFocus: true,
	_onFocus: function(/*Event*/e){
//			console.info('_onFocus')
		// summary: Fired on focus
		if( (dojo.isMoz)&&(this._initialFocus) ){
			this._initialFocus = false;
			if(this.editNode.innerHTML.replace(/^\s+|\s+$/g, "") == "&nbsp;"){
				this.placeCursorAtStart();
//					this.execCommand("selectall");
//					this.window.getSelection().collapseToStart();
			}
		}
	},

	blur: function(){
		// summary: remove focus from this instance
		if(this.iframe){
			this.window.blur();
		}else if(this.editNode){
			this.editNode.blur();
		}
	},

	focus: function(){
		// summary: move focus to this instance
		if(this.iframe && !dojo.isIE){
			dijit.focus(this.iframe);
		}else if(this.editNode && this.editNode.focus){
			// editNode may be hidden in display:none div, lets just punt in this case
			dijit.focus(this.editNode);
		}else{
			console.debug("Have no idea how to focus into the editor!");
		}
	},

//		_lastUpdate: 0,
	updateInterval: 200,
	_updateTimer: null,
	onDisplayChanged: function(/*Event*/e){
		// summary:
		//		This event will be fired everytime the display context
		//		changes and the result needs to be reflected in the UI.
		// description:
		//		If you don't want to have update too often,
		//		onNormalizedDisplayChanged should be used instead

//			var _t=new Date();
		if(!this._updateTimer){
//				this._lastUpdate=_t;
			if(this._updateTimer){
				clearTimeout(this._updateTimer);
			}
			this._updateTimer=setTimeout(dojo.hitch(this,this.onNormalizedDisplayChanged),this.updateInterval);
		}
	},
	onNormalizedDisplayChanged: function(){
		// summary:
		//		This event is fired every updateInterval ms or more
		// description:
		//		If something needs to happen immidiately after a
		//		user change, please use onDisplayChanged instead
		this._updateTimer=null;
	},
	onChange: function(newContent){
		// summary:
		//		this is fired if and only if the editor loses focus and
		//		the content is changed

//			console.log('onChange',newContent);
	},
	_normalizeCommand: function(/*String*/cmd){
		// summary:
		//		Used as the advice function by dojo.connect to map our
		//		normalized set of commands to those supported by the target
		//		browser

		var command = cmd.toLowerCase();
		if(command == "formatblock"){
			if(dojo.isSafari){ command = "heading"; }
		}else if(command == "hilitecolor" && !dojo.isMoz){
			command = "backcolor";
		}

		return command;
	},

	queryCommandAvailable: function(/*String*/command){
		// summary:
		//		Tests whether a command is supported by the host. Clients SHOULD check
		//		whether a command is supported before attempting to use it, behaviour
		//		for unsupported commands is undefined.
		// command: The command to test for
		var ie = 1;
		var mozilla = 1 << 1;
		var safari = 1 << 2;
		var opera = 1 << 3;
		var safari420 = 1 << 4;

		var gt420 = dojo.isSafari;

		function isSupportedBy(browsers){
			return {
				ie: Boolean(browsers & ie),
				mozilla: Boolean(browsers & mozilla),
				safari: Boolean(browsers & safari),
				safari420: Boolean(browsers & safari420),
				opera: Boolean(browsers & opera)
			}
		}

		var supportedBy = null;

		switch(command.toLowerCase()){
			case "bold": case "italic": case "underline":
			case "subscript": case "superscript":
			case "fontname": case "fontsize":
			case "forecolor": case "hilitecolor":
			case "justifycenter": case "justifyfull": case "justifyleft":
			case "justifyright": case "delete": case "selectall":
				supportedBy = isSupportedBy(mozilla | ie | safari | opera);
				break;

			case "createlink": case "unlink": case "removeformat":
			case "inserthorizontalrule": case "insertimage":
			case "insertorderedlist": case "insertunorderedlist":
			case "indent": case "outdent": case "formatblock":
			case "inserthtml": case "undo": case "redo": case "strikethrough":
				supportedBy = isSupportedBy(mozilla | ie | opera | safari420);
				break;

			case "blockdirltr": case "blockdirrtl":
			case "dirltr": case "dirrtl":
			case "inlinedirltr": case "inlinedirrtl":
				supportedBy = isSupportedBy(ie);
				break;
			case "cut": case "copy": case "paste":
				supportedBy = isSupportedBy( ie | mozilla | safari420);
				break;

			case "inserttable":
				supportedBy = isSupportedBy(mozilla | ie);
				break;

			case "insertcell": case "insertcol": case "insertrow":
			case "deletecells": case "deletecols": case "deleterows":
			case "mergecells": case "splitcell":
				supportedBy = isSupportedBy(ie | mozilla);
				break;

			default: return false;
		}

		return (dojo.isIE && supportedBy.ie) ||
			(dojo.isMoz && supportedBy.mozilla) ||
			(dojo.isSafari && supportedBy.safari) ||
			(gt420 && supportedBy.safari420) ||
			(dojo.isOpera && supportedBy.opera);  // Boolean return true if the command is supported, false otherwise
	},

	execCommand: function(/*String*/command, argument){
		// summary: Executes a command in the Rich Text area
		// command: The command to execute
		// argument: An optional argument to the command
		var returnValue;

		//focus() is required for IE to work
		//In addition, focus() makes sure after the execution of
		//the command, the editor receives the focus as expected
		this.focus();

		command = this._normalizeCommand(command);
		if(argument != undefined){
			if(command == "heading"){
				throw new Error("unimplemented");
			}else if((command == "formatblock") && dojo.isIE){
				argument = '<'+argument+'>';
			}
		}
		if(command == "inserthtml"){
			//TODO: we shall probably call _preDomFilterContent here as well
			argument=this._preFilterContent(argument);
			if(dojo.isIE){
				var insertRange = this.document.selection.createRange();
				insertRange.pasteHTML(argument);
				insertRange.select();
				//insertRange.collapse(true);
				returnValue=true;
			}else if(dojo.isMoz && !argument.length){
				//mozilla can not inserthtml an empty html to delete current selection
				//so we delete the selection instead in this case
				dojo.withGlobal(this.window,'remove',dijit._editor.selection); // FIXME
				returnValue=true;
			}else{
				returnValue=this.document.execCommand(command, false, argument);
			}
		}else if(
			(command == "unlink")&&
			(this.queryCommandEnabled("unlink"))&&
			(dojo.isMoz || dojo.isSafari)
		){
			// fix up unlink in Mozilla to unlink the link and not just the selection

			// grab selection
			// Mozilla gets upset if we just store the range so we have to
			// get the basic properties and recreate to save the selection
			var selection = this.window.getSelection();
			//	var selectionRange = selection.getRangeAt(0);
			//	var selectionStartContainer = selectionRange.startContainer;
			//	var selectionStartOffset = selectionRange.startOffset;
			//	var selectionEndContainer = selectionRange.endContainer;
			//	var selectionEndOffset = selectionRange.endOffset;

			// select our link and unlink
			var a = dojo.withGlobal(this.window, "getAncestorElement",dijit._editor.selection, ['a']);
			dojo.withGlobal(this.window, "selectElement", dijit._editor.selection, [a]);

			returnValue=this.document.execCommand("unlink", false, null);
		}else if((command == "hilitecolor")&&(dojo.isMoz)){
//				// mozilla doesn't support hilitecolor properly when useCSS is
//				// set to false (bugzilla #279330)

			this.document.execCommand("styleWithCSS", false, true);
			returnValue = this.document.execCommand(command, false, argument);
			this.document.execCommand("styleWithCSS", false, false);

		}else if((dojo.isIE)&&( (command == "backcolor")||(command == "forecolor") )){
			// Tested under IE 6 XP2, no problem here, comment out
			// IE weirdly collapses ranges when we exec these commands, so prevent it
//				var tr = this.document.selection.createRange();
			argument = arguments.length > 1 ? argument : null;
			returnValue = this.document.execCommand(command, false, argument);

			// timeout is workaround for weird IE behavior were the text
			// selection gets correctly re-created, but subsequent input
			// apparently isn't bound to it
//				setTimeout(function(){tr.select();}, 1);
		}else{
			argument = arguments.length > 1 ? argument : null;
//				if(dojo.isMoz){
//					this.document = this.iframe.contentWindow.document
//				}

			if(argument || command!="createlink"){
				returnValue = this.document.execCommand(command, false, argument);
			}
		}

		this.onDisplayChanged();
		return returnValue;
	},

	queryCommandEnabled: function(/*String*/command){
		// summary: check whether a command is enabled or not
		command = this._normalizeCommand(command);
		if(dojo.isMoz || dojo.isSafari){
			if(command == "unlink"){ // mozilla returns true always
				// console.debug(dojo.withGlobal(this.window, "hasAncestorElement",dijit._editor.selection, ['a']));
				return dojo.withGlobal(this.window, "hasAncestorElement",dijit._editor.selection, ['a']);
			}else if (command == "inserttable"){
				return true;
			}
		}
		//see #4109
		if(dojo.isSafari)
			if(command == "copy"){
				command="cut";
			}else if(command == "paste"){
				return true;
		}

		// return this.document.queryCommandEnabled(command);
		var elem = (dojo.isIE) ? this.document.selection.createRange() : this.document;
		return elem.queryCommandEnabled(command);
	},

	queryCommandState: function(command){
		// summary: check the state of a given command
		command = this._normalizeCommand(command);
		return this.document.queryCommandState(command);
	},

	queryCommandValue: function(command){
		// summary: check the value of a given command
		command = this._normalizeCommand(command);
		if(dojo.isIE && command == "formatblock"){
			return this._local2NativeFormatNames[this.document.queryCommandValue(command)];
		}
		return this.document.queryCommandValue(command);
	},

	// Misc.

	placeCursorAtStart: function(){
		// summary:
		//		place the cursor at the start of the editing area
		this.focus();

		//see comments in placeCursorAtEnd
		var isvalid=false;
		if(dojo.isMoz){
			var first=this.editNode.firstChild;
			while(first){
				if(first.nodeType == 3){
					if(first.nodeValue.replace(/^\s+|\s+$/g, "").length>0){
						isvalid=true;
						dojo.withGlobal(this.window, "selectElement", dijit._editor.selection, [first]);
						break;
					}
				}else if(first.nodeType == 1){
					isvalid=true;
					dojo.withGlobal(this.window, "selectElementChildren",dijit._editor.selection, [first]);
					break;
				}
				first = first.nextSibling;
			}
		}else{
			isvalid=true;
			dojo.withGlobal(this.window, "selectElementChildren",dijit._editor.selection, [this.editNode]);
		}
		if(isvalid){
			dojo.withGlobal(this.window, "collapse", dijit._editor.selection, [true]);
		}
	},

	placeCursorAtEnd: function(){
		// summary:
		//		place the cursor at the end of the editing area
		this.focus();

		//In mozilla, if last child is not a text node, we have to use selectElementChildren on this.editNode.lastChild
		//otherwise the cursor would be placed at the end of the closing tag of this.editNode.lastChild
		var isvalid=false;
		if(dojo.isMoz){
			var last=this.editNode.lastChild;
			while(last){
				if(last.nodeType == 3){
					if(last.nodeValue.replace(/^\s+|\s+$/g, "").length>0){
						isvalid=true;
						dojo.withGlobal(this.window, "selectElement",dijit._editor.selection, [last]);
						break;
					}
				}else if(last.nodeType == 1){
					isvalid=true;
					if(last.lastChild){
						dojo.withGlobal(this.window, "selectElement",dijit._editor.selection, [last.lastChild]);
					}else{
						dojo.withGlobal(this.window, "selectElement",dijit._editor.selection, [last]);
					}
					break;
				}
				last = last.previousSibling;
			}
		}else{
			isvalid=true;
			dojo.withGlobal(this.window, "selectElementChildren",dijit._editor.selection, [this.editNode]);
		}
		if(isvalid){
			dojo.withGlobal(this.window, "collapse", dijit._editor.selection, [false]);
		}
	},

	getValue: function(/*Boolean?*/nonDestructive){
		// summary:
		//		return the current content of the editing area (post filters are applied)
		if(this.textarea){
			if(this.isClosed || !this.isLoaded){
				return this.textarea.value;
			}
		}

		return this._postFilterContent(null, nonDestructive);
	},

	setValue: function(/*String*/html){
		// summary:
		//		this function set the content. No undo history is preserved
		if(this.textarea && (this.isClosed || !this.isLoaded)){
			this.textarea.value=html;
		}else{
			html = this._preFilterContent(html);
			if(this.isClosed){
				this.domNode.innerHTML = html;
				this._preDomFilterContent(this.domNode);
			}else{
				this.editNode.innerHTML = html;
				this._preDomFilterContent(this.editNode);
			}
		}
	},

	replaceValue: function(/*String*/html){
		// summary:
		//		this function set the content while trying to maintain the undo stack
		//		(now only works fine with Moz, this is identical to setValue in all
		//		other browsers)
		if(this.isClosed){
			this.setValue(html);
		}else if(this.window && this.window.getSelection && !dojo.isMoz){ // Safari
			// look ma! it's a totally f'd browser!
			this.setValue(html);
		}else if(this.window && this.window.getSelection){ // Moz
			html = this._preFilterContent(html);
			this.execCommand("selectall");
			if(dojo.isMoz && !html){ html = "&nbsp;" }
			this.execCommand("inserthtml", html);
			this._preDomFilterContent(this.editNode);
		}else if(this.document && this.document.selection){//IE
			//In IE, when the first element is not a text node, say
			//an <a> tag, when replacing the content of the editing
			//area, the <a> tag will be around all the content
			//so for now, use setValue for IE too
			this.setValue(html);
		}
	},

	_preFilterContent: function(/*String*/html){
		// summary:
		//		filter the input before setting the content of the editing area
		var ec = html;
		dojo.forEach(this.contentPreFilters, function(ef){ if(ef){ ec = ef(ec); } });
		return ec;
	},
	_preDomFilterContent: function(/*DomNode*/dom){
		// summary:
		//		filter the input
		dom = dom || this.editNode;
		dojo.forEach(this.contentDomPreFilters, function(ef){
			if(ef && dojo.isFunction(ef)){
				ef(dom);
			}
		}, this);
	},

	_postFilterContent: function(/*DomNode|DomNode[]?*/dom,/*Boolean?*/nonDestructive){
		// summary:
		//		filter the output after getting the content of the editing area
		dom = dom || this.editNode;
		if(this.contentDomPostFilters.length){
			if(nonDestructive && dom['cloneNode']){
				dom = dom.cloneNode(true);
			}
			dojo.forEach(this.contentDomPostFilters, function(ef){
				dom = ef(dom);
			});
		}
		var ec = this.getNodeChildrenHtml(dom);
		if(!ec.replace(/^(?:\s|\xA0)+/g, "").replace(/(?:\s|\xA0)+$/g,"").length){ ec = ""; }

		//	if(dojo.isIE){
		//		//removing appended <P>&nbsp;</P> for IE
		//		ec = ec.replace(/(?:<p>&nbsp;</p>[\n\r]*)+$/i,"");
		//	}
		dojo.forEach(this.contentPostFilters, function(ef){
			ec = ef(ec);
		});

		return ec;
	},

	_saveContent: function(/*Event*/e){
		// summary:
		//		Saves the content in an onunload event if the editor has not been closed
		var saveTextarea = dojo.byId("dijit._editor.RichText.savedContent");
		saveTextarea.value += this._SEPARATOR + this.name + ":" + this.getValue();
	},


	escapeXml: function(/*String*/str, /*Boolean*/noSingleQuotes){
		//summary:
		//		Adds escape sequences for special characters in XML: &<>"'
		//		Optionally skips escapes for single quotes
		str = str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
		if(!noSingleQuotes){
			str = str.replace(/'/gm, "&#39;");
		}
		return str; // string
	},

	getNodeHtml: function(/* DomNode */node){
		switch(node.nodeType){
			case 1: //element node
				var output = '<'+node.tagName.toLowerCase();
				if(dojo.isMoz){
					if(node.getAttribute('type')=='_moz'){
						node.removeAttribute('type');
					}
					if(node.getAttribute('_moz_dirty') != undefined){
						node.removeAttribute('_moz_dirty');
					}
				}
				//store the list of attributes and sort it to have the
				//attributes appear in the dictionary order
				var attrarray = [];
				if(dojo.isIE){
					var s = node.outerHTML;
					s = s.substr(0,s.indexOf('>'));
					s = s.replace(/(?:['"])[^"']*\1/g, '');//to make the following regexp safe
					var reg = /([^\s=]+)=/g;
					var m, key;
					while((m = reg.exec(s)) != undefined){
						key=m[1];
						if(key.substr(0,3) != '_dj'){
							if(key == 'src' || key == 'href'){
								if(node.getAttribute('_djrealurl')){
									attrarray.push([key,node.getAttribute('_djrealurl')]);
									continue;
								}
							}
							if(key == 'class'){
								attrarray.push([key,node.className]);
							}else{
								attrarray.push([key,node.getAttribute(key)]);
							}
						}
					}
				}else{
					var attr, i=0, attrs = node.attributes;
					while((attr=attrs[i++])){
						//ignore all attributes starting with _dj which are
						//internal temporary attributes used by the editor
						if(attr.name.substr(0,3) != '_dj' /*&&
							(attr.specified == undefined || attr.specified)*/){
							var v = attr.value;
							if(attr.name == 'src' || attr.name == 'href'){
								if(node.getAttribute('_djrealurl')){
									v = node.getAttribute('_djrealurl');
								}
							}
							attrarray.push([attr.name,v]);
						}
					}
				}
				attrarray.sort(function(a,b){
					return a[0]<b[0]?-1:(a[0]==b[0]?0:1);
				});
				i=0;
				while((attr=attrarray[i++])){
					output += ' '+attr[0]+'="'+attr[1]+'"';
				}
				if(node.childNodes.length){
					output += '>' + this.getNodeChildrenHtml(node)+'</'+node.tagName.toLowerCase()+'>';
				}else{
					output += ' />';
				}
				break;
			case 3: //text
				// FIXME:
				var output = this.escapeXml(node.nodeValue,true);
				break;
			case 8: //comment
				// FIXME:
				var output = '<!--'+this.escapeXml(node.nodeValue,true)+'-->';
				break;
			default:
				var output = "Element not recognized - Type: " + node.nodeType + " Name: " + node.nodeName;
		}
		return output;
	},

	getNodeChildrenHtml: function(/* DomNode */dom){
		// summary: Returns the html content of a DomNode and children
		var out = "";
		if(!dom){ return out; }
		var nodes = dom["childNodes"]||dom;
		var i=0;
		var node;
		while((node=nodes[i++])){
			out += this.getNodeHtml(node);
		}
		return out; // String
	},

	close: function(/*Boolean*/save, /*Boolean*/force){
		// summary:
		//		Kills the editor and optionally writes back the modified contents to the
		//		element from which it originated.
		// save:
		//		Whether or not to save the changes. If false, the changes are discarded.
		// force:
		if(this.isClosed){return false; }

		if(!arguments.length){ save = true; }
		this._content = this.getValue();
		var changed = (this.savedContent != this._content);

		// line height is squashed for iframes
		// FIXME: why was this here? if (this.iframe){ this.domNode.style.lineHeight = null; }

		if(this.interval){ clearInterval(this.interval); }

		if(this.textarea){
			with(this.textarea.style){
				position = "";
				left = top = "";
				if(dojo.isIE){
					overflow = this.__overflow;
					this.__overflow = null;
				}
			}
			if(save){
				this.textarea.value = this._content;
			}else{
				this.textarea.value = this.savedContent;
			}
			dojo._destroyElement(this.domNode);
			this.domNode = this.textarea;
		}else{
			if(save){
				//why we treat moz differently? comment out to fix #1061
//					if(dojo.isMoz){
//						var nc = dojo.doc.createElement("span");
//						this.domNode.appendChild(nc);
//						nc.innerHTML = this.editNode.innerHTML;
//					}else{
//						this.domNode.innerHTML = this._content;
//					}
				this.domNode.innerHTML = this._content;
			}else{
				this.domNode.innerHTML = this.savedContent;
			}
		}

		dojo.removeClass(this.domNode, "RichTextEditable");
		this.isClosed = true;
		this.isLoaded = false;
		// FIXME: is this always the right thing to do?
		delete this.editNode;

		if(this.window && this.window._frameElement){
			this.window._frameElement = null;
		}

		this.window = null;
		this.document = null;
		this.editingArea = null;
		this.editorObject = null;

		return changed; // Boolean: whether the content has been modified
	},

	destroyRendering: function(){
		// summary: stub	
	}, 

	destroy: function(){
		this.destroyRendering();
		if(!this.isClosed){ this.close(false); }
		this.inherited("destroy",arguments);
		//dijit._editor.RichText.superclass.destroy.call(this);
	},

	_fixContentForMoz: function(/* String */ html){
		// summary:
		//		Moz can not handle strong/em tags correctly, convert them to b/i
		html = html.replace(/<(\/)?strong([ \>])/gi, '<$1b$2' );
		html = html.replace(/<(\/)?em([ \>])/gi, '<$1i$2' );
		return html; // String
	},

	_srcInImgRegex	: /(?:(<img(?=\s).*?\ssrc=)("|')(.*?)\2)|(?:(<img\s.*?src=)([^"'][^ >]+))/gi ,
	_hrefInARegex	: /(?:(<a(?=\s).*?\shref=)("|')(.*?)\2)|(?:(<a\s.*?href=)([^"'][^ >]+))/gi ,

	_preFixUrlAttributes: function(/* String */ html){
		html = html.replace(this._hrefInARegex, '$1$4$2$3$5$2 _djrealurl=$2$3$5$2') ;
		html = html.replace(this._srcInImgRegex, '$1$4$2$3$5$2 _djrealurl=$2$3$5$2') ;
		return html; // String
	}
});

}

if(!dojo._hasResource["dijit.Toolbar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Toolbar"] = true;
dojo.provide("dijit.Toolbar");





dojo.declare(
	"dijit.Toolbar",
	[dijit._Widget, dijit._Templated, dijit._KeyNavContainer],
{
	templateString:
		'<div class="dijit dijitToolbar" waiRole="toolbar" tabIndex="${tabIndex}" dojoAttachPoint="containerNode">' +
//			'<table style="table-layout: fixed" class="dijitReset dijitToolbarTable">' + // factor out style
//				'<tr class="dijitReset" dojoAttachPoint="containerNode"></tr>'+
//			'</table>' +
		'</div>',

	tabIndex: "0",

	postCreate: function(){
		this.connectKeyNavHandlers(
			this.isLeftToRight() ? [dojo.keys.LEFT_ARROW] : [dojo.keys.RIGHT_ARROW],
			this.isLeftToRight() ? [dojo.keys.RIGHT_ARROW] : [dojo.keys.LEFT_ARROW]
		);
	},

	startup: function(){
		this.startupKeyNavChildren();
	}
}
);

// Combine with dijit.MenuSeparator??
dojo.declare(
	"dijit.ToolbarSeparator",
	[ dijit._Widget, dijit._Templated ],
{
	// summary
	//	A line between two menu items
	templateString: '<div class="dijitToolbarSeparator dijitInline"></div>',
	postCreate: function(){ dojo.setSelectable(this.domNode, false); },
	isFocusable: function(){ return false; }
});

}

if(!dojo._hasResource["dijit.form.Button"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.Button"] = true;
dojo.provide("dijit.form.Button");




dojo.declare("dijit.form.Button", dijit.form._FormWidget, {
/*
 * usage
 *	<button dojoType="button" onClick="...">Hello world</button>
 *
 *  var button1 = new dijit.form.Button({label: "hello world", onClick: foo});
 *	dojo.body().appendChild(button1.domNode);
 */
	// summary
	//	Basically the same thing as a normal HTML button, but with special styling.

	// label: String
	//	text to display in button
	label: "",

	// showLabel: Boolean
	//	whether or not to display the text label in button
	showLabel: true,

	// iconClass: String
	//	class to apply to div in button to make it display an icon
	iconClass: "",

	type: "button",
	baseClass: "dijitButton",
	templateString:"<div class=\"dijit dijitLeft dijitInline dijitButton\"\r\n\tdojoAttachEvent=\"onclick:_onButtonClick,onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\"\r\n\t><div class='dijitRight'\r\n\t\t><button class=\"dijitStretch dijitButtonNode dijitButtonContents\" dojoAttachPoint=\"focusNode,titleNode\"\r\n\t\t\ttype=\"${type}\" waiRole=\"button\" waiState=\"labelledby-${id}_label\"\r\n\t\t\t><span class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\" \r\n \t\t\t\t><span class=\"dijitToggleButtonIconChar\">&#10003</span \r\n\t\t\t></span\r\n\t\t\t><span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span\r\n\t\t></button\r\n\t></div\r\n></div>\r\n",

	// TODO: set button's title to this.containerNode.innerText

	_onClick: function(/*Event*/ e){
		// summary: internal function to handle click actions
		if(this.disabled){ return false; }
		this._clicked(); // widget click actions
		return this.onClick(e); // user click actions
	},

	_onButtonClick: function(/*Event*/ e){
		// summary: callback when the user mouse clicks the button portion
		dojo.stopEvent(e);
		var okToSubmit = this._onClick(e) !== false; // returning nothing is same as true

		// for some reason type=submit buttons don't automatically submit the form; do it manually
		if(this.type=="submit" && okToSubmit){
			for(var node=this.domNode; node; node=node.parentNode){
				var widget=dijit.byNode(node);
				if(widget && widget._onSubmit){
					widget._onSubmit(e);
					break;
				}
				if(node.tagName.toLowerCase() == "form"){
					if(!node.onsubmit || node.onsubmit()){ node.submit(); }
					break;
				}
			}
		}
	},

	postCreate: function(){
		// summary:
		//	get label and set as title on button icon if necessary
		if (this.showLabel == false){
			var labelText = "";
			this.label = this.containerNode.innerHTML;
			labelText = dojo.trim(this.containerNode.innerText || this.containerNode.textContent);
			// set title attrib on iconNode
			this.titleNode.title=labelText;
			dojo.addClass(this.containerNode,"dijitDisplayNone");
		}
		this.inherited(arguments);
	},

	onClick: function(/*Event*/ e){
		// summary: user callback for when button is clicked
		//      if type="submit", return value != false to perform submit
		return true;
	},

	_clicked: function(/*Event*/ e){
		// summary: internal replaceable function for when the button is clicked
	},

	setLabel: function(/*String*/ content){
		// summary: reset the label (text) of the button; takes an HTML string
		this.containerNode.innerHTML = this.label = content;
		if(dojo.isMozilla){ // Firefox has re-render issues with tables
			var oldDisplay = dojo.getComputedStyle(this.domNode).display;
			this.domNode.style.display="none";
			var _this = this;
			setTimeout(function(){_this.domNode.style.display=oldDisplay;},1);
		}
		if (this.showLabel == false){
				this.titleNode.title=dojo.trim(this.containerNode.innerText || this.containerNode.textContent);
		}
	}		
});

/*
 * usage
 *	<button dojoType="DropDownButton" label="Hello world"><div dojotype=dijit.Menu>...</div></button>
 *
 *  var button1 = new dijit.form.DropDownButton({ label: "hi", dropDown: new dijit.Menu(...) });
 *	dojo.body().appendChild(button1);
 */
dojo.declare("dijit.form.DropDownButton", [dijit.form.Button, dijit._Container], {
	// summary
	//		push the button and a menu shows up

	baseClass : "dijitDropDownButton",

	templateString:"<div class=\"dijit dijitLeft dijitInline\"\r\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse,onclick:_onDropDownClick,onkeydown:_onDropDownKeydown,onblur:_onDropDownBlur,onkeypress:_onKey\"\r\n\t><div class='dijitRight'>\r\n\t<button class=\"dijitStretch dijitButtonNode dijitButtonContents\" type=\"${type}\"\r\n\t\tdojoAttachPoint=\"focusNode,titleNode\" waiRole=\"button\" waiState=\"haspopup-true,labelledby-${id}_label\"\r\n\t\t><div class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\"></div\r\n\t\t><span class=\"dijitButtonText\" \tdojoAttachPoint=\"containerNode,popupStateNode\"\r\n\t\tid=\"${id}_label\">${label}</span\r\n\t\t><span class='dijitA11yDownArrow'>&#9660;</span>\r\n\t</button>\r\n</div></div>\r\n",

	_fillContent: function(){
		// my inner HTML contains both the button contents and a drop down widget, like
		// <DropDownButton>  <span>push me</span>  <Menu> ... </Menu> </DropDownButton>
		// The first node is assumed to be the button content. The widget is the popup.
		if(this.srcNodeRef){ // programatically created buttons might not define srcNodeRef
			//FIXME: figure out how to filter out the widget and use all remaining nodes as button
			//	content, not just nodes[0]
			var nodes = dojo.query("*", this.srcNodeRef);
			dijit.form.DropDownButton.superclass._fillContent.call(this, nodes[0]);

			// save pointer to srcNode so we can grab the drop down widget after it's instantiated
			this.dropDownContainer = this.srcNodeRef;
		}
	},

	startup: function(){
		// the child widget from srcNodeRef is the dropdown widget.  Insert it in the page DOM,
		// make it invisible, and store a reference to pass to the popup code.
		if(!this.dropDown){
			var dropDownNode = dojo.query("[widgetId]", this.dropDownContainer)[0];
			this.dropDown = dijit.byNode(dropDownNode);
			delete this.dropDownContainer;
		}
		dojo.body().appendChild(this.dropDown.domNode);
		this.dropDown.domNode.style.display="none";
	},

	_onArrowClick: function(/*Event*/ e){
		// summary: callback when the user mouse clicks on menu popup node
		if(this.disabled){ return; }
		this._toggleDropDown();
	},

	_onDropDownClick: function(/*Event*/ e){
		// on Firefox 2 on the Mac it is possible to fire onclick
		// by pressing enter down on a second element and transferring
		// focus to the DropDownButton;
		// we want to prevent opening our menu in this situation
		// and only do so if we have seen a keydown on this button;
		// e.detail != 0 means that we were fired by mouse
		var isMacFFlessThan3 = dojo.isFF && dojo.isFF < 3
			&& navigator.appVersion.indexOf("Macintosh") != -1;
		if(!isMacFFlessThan3 || e.detail != 0 || this._seenKeydown){
			this._onArrowClick(e);
		}
		this._seenKeydown = false;
	},

	_onDropDownKeydown: function(/*Event*/ e){
		this._seenKeydown = true;
	},

	_onDropDownBlur: function(/*Event*/ e){
		this._seenKeydown = false;
	},

	_onKey: function(/*Event*/ e){
		// summary: callback when the user presses a key on menu popup node
		if(this.disabled){ return; }
		if(e.keyCode == dojo.keys.DOWN_ARROW){
			if(!this.dropDown || this.dropDown.domNode.style.display=="none"){
				dojo.stopEvent(e);
				return this._toggleDropDown();
			}
		}
	},

	_onBlur: function(){
		// summary: called magically when focus has shifted away from this widget and it's dropdown
		this._closeDropDown();
		// don't focus on button.  the user has explicitly focused on something else.
	},

	_toggleDropDown: function(){
		// summary: toggle the drop-down widget; if it is up, close it, if not, open it
		if(this.disabled){ return; }
		dijit.focus(this.popupStateNode);
		var dropDown = this.dropDown;
		if(!dropDown){ return false; }
		if(!dropDown.isShowingNow){
			// If there's an href, then load that first, so we don't get a flicker
			if(dropDown.href && !dropDown.isLoaded){
				var self = this;
				var handler = dojo.connect(dropDown, "onLoad", function(){
					dojo.disconnect(handler);
					self._openDropDown();
				});
				dropDown._loadCheck(true);
				return;
			}else{
				this._openDropDown();
			}
		}else{
			this._closeDropDown();
		}
	},

	_openDropDown: function(){
		var dropDown = this.dropDown;
		var oldWidth=dropDown.domNode.style.width;
		var self = this;

		dijit.popup.open({
			parent: this,
			popup: dropDown,
			around: this.domNode,
			orient: this.isLeftToRight() ? {'BL':'TL', 'BR':'TR', 'TL':'BL', 'TR':'BR'}
				: {'BR':'TR', 'BL':'TL', 'TR':'BR', 'TL':'BL'},
			onExecute: function(){
				self._closeDropDown(true);
			},
			onCancel: function(){
				self._closeDropDown(true);
			},
			onClose: function(){
				dropDown.domNode.style.width = oldWidth;
				self.popupStateNode.removeAttribute("popupActive");
				this._opened = false;
			}
		});
		if(this.domNode.offsetWidth > dropDown.domNode.offsetWidth){
			var adjustNode = null;
			if(!this.isLeftToRight()){
				adjustNode = dropDown.domNode.parentNode;
				var oldRight = adjustNode.offsetLeft + adjustNode.offsetWidth;
			}
			// make menu at least as wide as the button
			dojo.marginBox(dropDown.domNode, {w: this.domNode.offsetWidth});
			if(adjustNode){
				adjustNode.style.left = oldRight - this.domNode.offsetWidth + "px";
			}
		}
		this.popupStateNode.setAttribute("popupActive", "true");
		this._opened=true;
		if(dropDown.focus){
			dropDown.focus();
		}
		// TODO: set this.checked and call setStateClass(), to affect button look while drop down is shown
	},
	
	_closeDropDown: function(/*Boolean*/ focus){
		if(this._opened){
			dijit.popup.close(this.dropDown);
			if(focus){ this.focus(); }
			this._opened = false;			
		}
	}
});

/*
 * usage
 *	<button dojoType="ComboButton" onClick="..."><span>Hello world</span><div dojoType=dijit.Menu>...</div></button>
 *
 *  var button1 = new dijit.form.ComboButton({label: "hello world", onClick: foo, dropDown: "myMenu"});
 *	dojo.body().appendChild(button1.domNode);
 */
dojo.declare("dijit.form.ComboButton", dijit.form.DropDownButton, {
	// summary
	//		left side is normal button, right side displays menu
	templateString:"<table class='dijit dijitReset dijitInline dijitLeft'\r\n\tcellspacing='0' cellpadding='0'\r\n\tdojoAttachEvent=\"onmouseenter:_onMouse,onmouseleave:_onMouse,onmousedown:_onMouse\">\r\n\t<tr>\r\n\t\t<td\tclass=\"dijitStretch dijitButtonContents dijitButtonNode\"\r\n\t\t\ttabIndex=\"${tabIndex}\"\r\n\t\t\tdojoAttachEvent=\"ondijitclick:_onButtonClick\"  dojoAttachPoint=\"titleNode\"\r\n\t\t\twaiRole=\"button\" waiState=\"labelledby-${id}_label\">\r\n\t\t\t<div class=\"dijitInline ${iconClass}\" dojoAttachPoint=\"iconNode\"></div>\r\n\t\t\t<span class=\"dijitButtonText\" id=\"${id}_label\" dojoAttachPoint=\"containerNode\">${label}</span>\r\n\t\t</td>\r\n\t\t<td class='dijitReset dijitRight dijitButtonNode dijitDownArrowButton'\r\n\t\t\tdojoAttachPoint=\"popupStateNode,focusNode\"\r\n\t\t\tdojoAttachEvent=\"ondijitclick:_onArrowClick, onkeypress:_onKey\"\r\n\t\t\tstateModifier=\"DownArrow\"\r\n\t\t\ttitle=\"${optionsTitle}\" name=\"${name}\"\r\n\t\t\twaiRole=\"button\" waiState=\"haspopup-true\"\r\n\t\t><div waiRole=\"presentation\">&#9660;</div>\r\n\t</td></tr>\r\n</table>\r\n",

	attributeMap: dojo.mixin(dojo.clone(dijit.form._FormWidget.prototype.attributeMap),
		{id:"", name:""}),

	// optionsTitle: String
	//  text that describes the options menu (accessibility)
	optionsTitle: "",

	baseClass: "dijitComboButton",

	_focusedNode: null,

	postCreate: function(){
		this.inherited(arguments);
		this._focalNodes = [this.titleNode, this.popupStateNode];
		dojo.forEach(this._focalNodes, dojo.hitch(this, function(node){
			if(dojo.isIE){
				this.connect(node, "onactivate", this._onNodeFocus);
			}else{
				this.connect(node, "onfocus", this._onNodeFocus);
			}
		}));
	},

	focusFocalNode: function(node){
		// summary: Focus the focal node node.
		this._focusedNode = node;
		dijit.focus(node);
	},

	hasNextFocalNode: function(){
		// summary: Returns true if this widget has no node currently
		//		focused or if there is a node following the focused one.
		//		False is returned if the last node has focus.
		return this._focusedNode !== this.getFocalNodes()[1];
	},

	focusNext: function(){
		// summary: Focus the focal node following the current node with focus
		//		or the first one if no node currently has focus.
		this._focusedNode = this.getFocalNodes()[this._focusedNode ? 1 : 0];
		dijit.focus(this._focusedNode);
	},

	hasPrevFocalNode: function(){
		// summary: Returns true if this widget has no node currently
		//		focused or if there is a node before the focused one.
		//		False is returned if the first node has focus.
		return this._focusedNode !== this.getFocalNodes()[0];
	},

	focusPrev: function(){
		// summary: Focus the focal node before the current node with focus
		//		or the last one if no node currently has focus.
		this._focusedNode = this.getFocalNodes()[this._focusedNode ? 0 : 1];
		dijit.focus(this._focusedNode);
	},

	getFocalNodes: function(){
		// summary: Returns an array of focal nodes for this widget.
		return this._focalNodes;
	},

	_onNodeFocus: function(evt){
		this._focusedNode = evt.currentTarget;
	},

	_onBlur: function(evt){
		this.inherited(arguments);
		this._focusedNode = null;
	}
});

dojo.declare("dijit.form.ToggleButton", dijit.form.Button, {
	// summary
	//	A button that can be in two states (checked or not).
	//	Can be base class for things like tabs or checkbox or radio buttons

	baseClass: "dijitToggleButton",

	// checked: Boolean
	//		Corresponds to the native HTML <input> element's attribute.
	//		In markup, specified as "checked='checked'" or just "checked".
	//		True if the button is depressed, or the checkbox is checked,
	//		or the radio button is selected, etc.
	checked: false,

	_clicked: function(/*Event*/ evt){
		this.setChecked(!this.checked);
	},

	setChecked: function(/*Boolean*/ checked){
		// summary
		//	Programatically deselect the button
		this.checked = checked;
		dijit.setWaiState(this.focusNode || this.domNode, "pressed", this.checked);
		this._setStateClass();		
		this.onChange(checked);
	}
});

}

if(!dojo._hasResource["dijit._editor._Plugin"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor._Plugin"] = true;
dojo.provide("dijit._editor._Plugin");




dojo.declare("dijit._editor._Plugin", null, {
	// summary
	//		This represents a "plugin" to the editor, which is basically
	//		a single button on the Toolbar and some associated code
	constructor: function(/*Object?*/args, /*DomNode?*/node){
		if(args){
			dojo.mixin(this, args);
		}
	},

	editor: null,
	iconClassPrefix: "dijitEditorIcon",
	button: null,
	queryCommand: null,
	command: "",
	commandArg: null,
	useDefaultCommand: true,
	buttonClass: dijit.form.Button,
	updateInterval: 200, // only allow updates every two tenths of a second
	_initButton: function(){
		if(this.command.length){
			var label = this.editor.commands[this.command];
			var className = "dijitEditorIcon "+this.iconClassPrefix + this.command.charAt(0).toUpperCase() + this.command.substr(1);
			if(!this.button){
				var props = {
					label: label,
					showLabel: false,
					iconClass: className,
					dropDown: this.dropDown
				};
				this.button = new this.buttonClass(props);
			}
		}
	},
	updateState: function(){
		var _e = this.editor;
		var _c = this.command;
		if(!_e){ return; }
		if(!_e.isLoaded){ return; }
		if(!_c.length){ return; }
		if(this.button){
			try{
				var enabled = _e.queryCommandEnabled(_c);
				this.button.setDisabled(!enabled);
				if(this.button.setChecked){
					this.button.setChecked(_e.queryCommandState(_c));
				}
			}catch(e){
				console.debug(e);
			}
		}
	},
	setEditor: function(/*Widget*/editor){
		// FIXME: detatch from previous editor!!
		this.editor = editor;

		// FIXME: prevent creating this if we don't need to (i.e., editor can't handle our command)
		this._initButton();

		// FIXME: wire up editor to button here!
		if(	(this.command.length) &&
			(!this.editor.queryCommandAvailable(this.command))
		){
			// console.debug("hiding:", this.command);
			if(this.button){
				this.button.domNode.style.display = "none";
			}
		}
		if(this.button && this.useDefaultCommand){
			dojo.connect(this.button, "onClick",
				dojo.hitch(this.editor, "execCommand", this.command, this.commandArg)
			);
		}
		dojo.connect(this.editor, "onNormalizedDisplayChanged", this, "updateState");
	},
	setToolbar: function(/*Widget*/toolbar){
		if(this.button){
			toolbar.addChild(this.button);
		}
		// console.debug("adding", this.button, "to:", toolbar);
	}
});

}

if(!dojo._hasResource["dijit.Editor"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Editor"] = true;
dojo.provide("dijit.Editor");







dojo.declare(
	"dijit.Editor",
	dijit._editor.RichText,
	{
	// summary: A rich-text Editing widget

		// plugins: Array
		//		a list of plugin names (as strings) or instances (as objects)
		//		for this widget.
		plugins: null,

		// extraPlugins: Array
		//		a list of extra plugin names which will be appended to plugins array
		extraPlugins: null,

		constructor: function(){
			this.plugins=["undo","redo","|","cut","copy","paste","|","bold","italic","underline","strikethrough","|",
			"insertOrderedList","insertUnorderedList","indent","outdent","|","justifyLeft","justifyRight","justifyCenter","justifyFull"/*"createLink"*/];

			this._plugins=[];
			this._editInterval = this.editActionInterval * 1000;
		},

		postCreate: function(){
			//for custom undo/redo
			if(this.customUndo){
				dojo['require']("dijit._editor.range");
				this._steps=this._steps.slice(0);
				this._undoedSteps=this._undoedSteps.slice(0);
//				this.addKeyHandler('z',this.KEY_CTRL,this.undo);
//				this.addKeyHandler('y',this.KEY_CTRL,this.redo);
			}
			if(dojo.isArray(this.extraPlugins)){
				this.plugins=this.plugins.concat(this.extraPlugins);
			}

//			try{
			dijit.Editor.superclass.postCreate.apply(this, arguments);

			this.commands = dojo.i18n.getLocalization("dijit._editor", "commands", this.lang);

			if(!this.toolbar){
				// if we haven't been assigned a toolbar, create one
				var toolbarNode = dojo.doc.createElement("div");
				dojo.place(toolbarNode, this.editingArea, "before");
				this.toolbar = new dijit.Toolbar({}, toolbarNode);
			}

			dojo.forEach(this.plugins, this.addPlugin, this);
			this.onNormalizedDisplayChanged(); //update toolbar button status
//			}catch(e){ console.debug(e); }
		},
		destroy: function(){
			dojo.forEach(this._plugins, function(p){
				if(p.destroy){
					p.destroy();
				}
			});
			this._plugins=[];
			this.toolbar.destroy(); delete this.toolbar;
			this.inherited('destroy',arguments);
		},
		addPlugin: function(/*String||Object*/plugin, /*Integer?*/index){
			//	summary:
			//		takes a plugin name as a string or a plugin instance and
			//		adds it to the toolbar and associates it with this editor
			//		instance. The resulting plugin is added to the Editor's
			//		plugins array. If index is passed, it's placed in the plugins
			//		array at that index. No big magic, but a nice helper for
			//		passing in plugin names via markup.
			//	plugin: String, args object or plugin instance. Required.
			//	args: This object will be passed to the plugin constructor.
			//	index:	
			//		Integer, optional. Used when creating an instance from
			//		something already in this.plugins. Ensures that the new
			//		instance is assigned to this.plugins at that index.
			var args=dojo.isString(plugin)?{name:plugin}:plugin;
			if(!args.setEditor){
				var o={"args":args,"plugin":null,"editor":this};
				dojo.publish("dijit.Editor.getPlugin",[o]);
				if(!o.plugin){
					var pc = dojo.getObject(args.name);
					if(pc){
						o.plugin=new pc(args);
					}
				}
				if(!o.plugin){
					console.debug('Cannot find plugin',plugin);
					return;
				}
				plugin=o.plugin;
			}
			if(arguments.length > 1){
				this._plugins[index] = plugin;
			}else{
				this._plugins.push(plugin);
			}
			plugin.setEditor(this);
			if(dojo.isFunction(plugin.setToolbar)){
				plugin.setToolbar(this.toolbar);
			}
		},
		/* beginning of custom undo/redo support */

		// customUndo: Boolean
		//		Whether we shall use custom undo/redo support instead of the native
		//		browser support. By default, we only enable customUndo for IE, as it
		//		has broken native undo/redo support. Note: the implementation does
		//		support other browsers which have W3C DOM2 Range API.
		customUndo: dojo.isIE,

		//	editActionInterval: Integer
		//		When using customUndo, not every keystroke will be saved as a step.
		//		Instead typing (including delete) will be grouped together: after
		//		a user stop typing for editActionInterval seconds, a step will be
		//		saved; if a user resume typing within editActionInterval seconds,
		//		the timeout will be restarted. By default, editActionInterval is 3
		//		seconds.
		editActionInterval: 3,
		beginEditing: function(cmd){
			if(!this._inEditing){
				this._inEditing=true;
				this._beginEditing(cmd);
			}
			if(this.editActionInterval>0){
				if(this._editTimer){
					clearTimeout(this._editTimer);
				}
				this._editTimer = setTimeout(dojo.hitch(this, this.endEditing), this._editInterval);
			}
		},
		_steps:[],
		_undoedSteps:[],
		execCommand: function(cmd){
			if(this.customUndo && (cmd=='undo' || cmd=='redo')){
				return this[cmd]();
			}else{
				try{
					if(this.customUndo){
						this.endEditing();
						this._beginEditing();
					}
					var r = this.inherited('execCommand',arguments);
					if(this.customUndo){
						this._endEditing();
					}
					return r;
				}catch(e){
					if(dojo.isMoz && /copy|cut|paste/.test(cmd)){
						// Warn user of platform limitation.  Cannot programmatically access keyboard. See ticket #4136
						var sub = dojo.string.substitute,
							accel = {cut:'X', copy:'C', paste:'V'},
							isMac = navigator.userAgent.indexOf("Macintosh") != -1;
						alert(sub(this.commands.systemShortcutFF,
							[this.commands[cmd], sub(this.commands[isMac ? 'appleKey' : 'ctrlKey'], [accel[cmd]])]));
					}
					return false;
				}
			}
		},
		queryCommandEnabled: function(cmd){
			if(this.customUndo && (cmd=='undo' || cmd=='redo')){
				return cmd=='undo'?(this._steps.length>1):(this._undoedSteps.length>0);
			}else{
				return this.inherited('queryCommandEnabled',arguments);
			}
		},
		_changeToStep: function(from,to){
			this.setValue(to.text);
			var b=to.bookmark;
			if(!b){ return; }
			if(dojo.isIE){
				if(dojo.isArray(b)){//IE CONTROL
					var tmp=[];
					dojo.forEach(b,function(n){
						tmp.push(dijit.range.getNode(n,this.editNode));
					},this);
					b=tmp;
				}
			}else{//w3c range
				var r=dijit.range.create();
				r.setStart(dijit.range.getNode(b.startContainer,this.editNode),b.startOffset);
				r.setEnd(dijit.range.getNode(b.endContainer,this.editNode),b.endOffset);
				b=r;
			}
			dojo.withGlobal(this.window,'moveToBookmark',dijit,[b]);
		},
		undo: function(){
//			console.log('undo');
			this.endEditing(true);
			var s=this._steps.pop();
			if(this._steps.length>0){
				this.focus();
				this._changeToStep(s,this._steps[this._steps.length-1]);
				this._undoedSteps.push(s);
				this.onDisplayChanged();
				return true;
			}
			return false;
		},
		redo: function(){
//			console.log('redo');
			this.endEditing(true);
			var s=this._undoedSteps.pop();
			if(s && this._steps.length>0){
				this.focus();
				this._changeToStep(this._steps[this._steps.length-1],s);
				this._steps.push(s);
				this.onDisplayChanged();
				return true;
			}
			return false;
		},
		endEditing: function(ignore_caret){
			if(this._editTimer){
				clearTimeout(this._editTimer);
			}
			if(this._inEditing){
				this._endEditing(ignore_caret);
				this._inEditing=false;
			}
		},
		_getBookmark: function(){
			var b=dojo.withGlobal(this.window,dijit.getBookmark);
			if(dojo.isIE){
				if(dojo.isArray(b)){//CONTROL
					var tmp=[];
					dojo.forEach(b,function(n){
						tmp.push(dijit.range.getIndex(n,this.editNode).o);
					},this);
					b=tmp;
				}
			}else{//w3c range
				var tmp=dijit.range.getIndex(b.startContainer,this.editNode).o
				b={startContainer:tmp,
					startOffset:b.startOffset,
					endContainer:b.endContainer===b.startContainer?tmp:dijit.range.getIndex(b.endContainer,this.editNode).o,
					endOffset:b.endOffset};
			}
			return b;
		},
		_beginEditing: function(cmd){
			if(this._steps.length===0){
				this._steps.push({'text':this.savedContent,'bookmark':this._getBookmark()});
			}
		},
		_endEditing: function(ignore_caret){
			var v=this.getValue(true);

			this._undoedSteps=[];//clear undoed steps
			this._steps.push({'text':v,'bookmark':this._getBookmark()});
		},
		onKeyDown: function(e){
			if(!this.customUndo){
				this.inherited('onKeyDown',arguments);
				return;
			}
			var k=e.keyCode,ks=dojo.keys;
			if(e.ctrlKey){
				if(k===90||k===122){ //z
					dojo.stopEvent(e);
					this.undo();
					return;
				}else if(k===89||k===121){ //y
					dojo.stopEvent(e);
					this.redo();
					return;
				}
			}
			this.inherited('onKeyDown',arguments);

			switch(k){
					case ks.ENTER:
						this.beginEditing();
						break;
					case ks.BACKSPACE:
					case ks.DELETE:
						this.beginEditing();
						break;
					case 88: //x
					case 86: //v
						if(e.ctrlKey && !e.altKey && !e.metaKey){
							this.endEditing();//end current typing step if any
							if(e.keyCode == 88){
								this.beginEditing('cut');
								//use timeout to trigger after the cut is complete
								setTimeout(dojo.hitch(this, this.endEditing), 1);
							}else{
								this.beginEditing('paste');
								//use timeout to trigger after the paste is complete
								setTimeout(dojo.hitch(this, this.endEditing), 1);
							}
							break;
						}
						//pass through
					default:
						if(!e.ctrlKey && !e.altKey && !e.metaKey && (e.keyCode<dojo.keys.F1 || e.keyCode>dojo.keys.F15)){
							this.beginEditing();
							break;
						}
						//pass through
					case ks.ALT:
						this.endEditing();
						break;
					case ks.UP_ARROW:
					case ks.DOWN_ARROW:
					case ks.LEFT_ARROW:
					case ks.RIGHT_ARROW:
					case ks.HOME:
					case ks.END:
					case ks.PAGE_UP:
					case ks.PAGE_DOWN:
						this.endEditing(true);
						break;
					//maybe ctrl+backspace/delete, so don't endEditing when ctrl is pressed
					case ks.CTRL:
					case ks.SHIFT:
					case ks.TAB:
						break;
				}	
		},
		_onBlur: function(){
			this.inherited('_onBlur',arguments);
			this.endEditing(true);
		},
		onClick: function(){
			this.endEditing(true);
			this.inherited('onClick',arguments);
		}
		/* end of custom undo/redo support */
	}
);

/* the following code is to registered a handler to get default plugins */
dojo.subscribe("dijit.Editor.getPlugin",null,function(o){
	if(o.plugin){ return; }
	var args=o.args, p;
	var _p = dijit._editor._Plugin;
	var name=args.name;
	switch(name){
		case "undo": case "redo": case "cut": case "copy": case "paste": case "insertOrderedList":
		case "insertUnorderedList": case "indent": case "outdent": case "justifyCenter":
		case "justifyFull": case "justifyLeft": case "justifyRight": case "delete":
		case "selectAll": case "removeFormat":
			p = new _p({ command: name });
			break;

		case "bold": case "italic": case "underline": case "strikethrough":
		case "subscript": case "superscript":
			p = new _p({ buttonClass: dijit.form.ToggleButton, command: name });
			break;
		case "|":
			p = new _p({ button: new dijit.ToolbarSeparator() });
			break;
		case "createLink":
//					dojo['require']('dijit._editor.plugins.LinkDialog');
			p = new dijit._editor.plugins.LinkDialog({ command: name });
			break;
		case "foreColor": case "hiliteColor":
			p = new dijit._editor.plugins.TextColor({ command: name });
			break;
		case "fontName": case "fontSize": case "formatBlock":
			p = new dijit._editor.plugins.FontChoice({ command: name });
	}
//	console.log('name',name,p);
	o.plugin=p;
});

}

if(!dojo._hasResource["dojo.dnd.common"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.common"] = true;
dojo.provide("dojo.dnd.common");

dojo.dnd._copyKey = navigator.appVersion.indexOf("Macintosh") < 0 ? "ctrlKey" : "metaKey";

dojo.dnd.getCopyKeyState = function(e) {
	// summary: abstracts away the difference between selection on Mac and PC,
	//	and returns the state of the "copy" key to be pressed.
	// e: Event: mouse event
	return e[dojo.dnd._copyKey];	// Boolean
};

dojo.dnd._uniqueId = 0;
dojo.dnd.getUniqueId = function(){
	// summary: returns a unique string for use with any DOM element
	var id;
	do{
		id = "dojoUnique" + (++dojo.dnd._uniqueId);
	}while(dojo.byId(id));
	return id;
};

dojo.dnd._empty = {};

dojo.dnd.isFormElement = function(/*Event*/ e){
	// summary: returns true, if user clicked on a form element
	var t = e.target;
	if(t.nodeType == 3 /*TEXT_NODE*/){
		t = t.parentNode;
	}
	return " button textarea input select option ".indexOf(" " + t.tagName.toLowerCase() + " ") >= 0;	// Boolean
};

}

if(!dojo._hasResource["dojo.dnd.autoscroll"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.autoscroll"] = true;
dojo.provide("dojo.dnd.autoscroll");

dojo.dnd.getViewport = function(){
	// summary: returns a viewport size (visible part of the window)

	// FIXME: need more docs!!
	var d = dojo.doc, dd = d.documentElement, w = window, b = dojo.body();
	if(dojo.isMozilla){
		return {w: dd.clientWidth, h: w.innerHeight};	// Object
	}else if(!dojo.isOpera && w.innerWidth){
		return {w: w.innerWidth, h: w.innerHeight};		// Object
	}else if (!dojo.isOpera && dd && dd.clientWidth){
		return {w: dd.clientWidth, h: dd.clientHeight};	// Object
	}else if (b.clientWidth){
		return {w: b.clientWidth, h: b.clientHeight};	// Object
	}
	return null;	// Object
};

dojo.dnd.V_TRIGGER_AUTOSCROLL = 32;
dojo.dnd.H_TRIGGER_AUTOSCROLL = 32;

dojo.dnd.V_AUTOSCROLL_VALUE = 16;
dojo.dnd.H_AUTOSCROLL_VALUE = 16;

dojo.dnd.autoScroll = function(e){
	// summary:
	//		a handler for onmousemove event, which scrolls the window, if
	//		necesary
	// e: Event:
	//		onmousemove event

	// FIXME: needs more docs!
	var v = dojo.dnd.getViewport(), dx = 0, dy = 0;
	if(e.clientX < dojo.dnd.H_TRIGGER_AUTOSCROLL){
		dx = -dojo.dnd.H_AUTOSCROLL_VALUE;
	}else if(e.clientX > v.w - dojo.dnd.H_TRIGGER_AUTOSCROLL){
		dx = dojo.dnd.H_AUTOSCROLL_VALUE;
	}
	if(e.clientY < dojo.dnd.V_TRIGGER_AUTOSCROLL){
		dy = -dojo.dnd.V_AUTOSCROLL_VALUE;
	}else if(e.clientY > v.h - dojo.dnd.V_TRIGGER_AUTOSCROLL){
		dy = dojo.dnd.V_AUTOSCROLL_VALUE;
	}
	window.scrollBy(dx, dy);
};

dojo.dnd._validNodes = {"div": 1, "p": 1, "td": 1};
dojo.dnd._validOverflow = {"auto": 1, "scroll": 1};

dojo.dnd.autoScrollNodes = function(e){
	// summary:
	//		a handler for onmousemove event, which scrolls the first avaialble
	//		Dom element, it falls back to dojo.dnd.autoScroll()
	// e: Event:
	//		onmousemove event

	// FIXME: needs more docs!
	for(var n = e.target; n;){
		if(n.nodeType == 1 && (n.tagName.toLowerCase() in dojo.dnd._validNodes)){
			var s = dojo.getComputedStyle(n);
			if(s.overflow.toLowerCase() in dojo.dnd._validOverflow){
				var b = dojo._getContentBox(n, s), t = dojo._abs(n, true);
				// console.debug(b.l, b.t, t.x, t.y, n.scrollLeft, n.scrollTop);
				b.l += t.x + n.scrollLeft;
				b.t += t.y + n.scrollTop;
				var w = Math.min(dojo.dnd.H_TRIGGER_AUTOSCROLL, b.w / 2), 
					h = Math.min(dojo.dnd.V_TRIGGER_AUTOSCROLL, b.h / 2),
					rx = e.pageX - b.l, ry = e.pageY - b.t, dx = 0, dy = 0;
				if(rx > 0 && rx < b.w){
					if(rx < w){
						dx = -dojo.dnd.H_AUTOSCROLL_VALUE;
					}else if(rx > b.w - w){
						dx = dojo.dnd.H_AUTOSCROLL_VALUE;
					}
				}
				//console.debug("ry =", ry, "b.h =", b.h, "h =", h);
				if(ry > 0 && ry < b.h){
					if(ry < h){
						dy = -dojo.dnd.V_AUTOSCROLL_VALUE;
					}else if(ry > b.h - h){
						dy = dojo.dnd.V_AUTOSCROLL_VALUE;
					}
				}
				var oldLeft = n.scrollLeft, oldTop = n.scrollTop;
				n.scrollLeft = n.scrollLeft + dx;
				n.scrollTop  = n.scrollTop  + dy;
				// if(dx || dy){ console.debug(oldLeft + ", " + oldTop + "\n" + dx + ", " + dy + "\n" + n.scrollLeft + ", " + n.scrollTop); }
				if(oldLeft != n.scrollLeft || oldTop != n.scrollTop){ return; }
			}
		}
		try{
			n = n.parentNode;
		}catch(x){
			n = null;
		}
	}
	dojo.dnd.autoScroll(e);
};

}

if(!dojo._hasResource["dojo.dnd.Mover"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Mover"] = true;
dojo.provide("dojo.dnd.Mover");




dojo.declare("dojo.dnd.Mover", null, {
	constructor: function(node, e, host){
		// summary: an object, which makes a node follow the mouse, 
		//	used as a default mover, and as a base class for custom movers
		// node: Node: a node (or node's id) to be moved
		// e: Event: a mouse event, which started the move;
		//	only pageX and pageY properties are used
		// host: Object?: object which implements the functionality of the move,
		//	 and defines proper events (onMoveStart and onMoveStop)
		this.node = dojo.byId(node);
		this.marginBox = {l: e.pageX, t: e.pageY};
		this.mouseButton = e.button;
		var h = this.host = host, d = node.ownerDocument, 
			firstEvent = dojo.connect(d, "onmousemove", this, "onFirstMove");
		this.events = [
			dojo.connect(d, "onmousemove", this, "onMouseMove"),
			dojo.connect(d, "onmouseup",   this, "onMouseUp"),
			// cancel text selection and text dragging
			dojo.connect(d, "ondragstart",   dojo, "stopEvent"),
			dojo.connect(d, "onselectstart", dojo, "stopEvent"),
			firstEvent
		];
		// notify that the move has started
		if(h && h.onMoveStart){
			h.onMoveStart(this);
		}
	},
	// mouse event processors
	onMouseMove: function(e){
		// summary: event processor for onmousemove
		// e: Event: mouse event
		dojo.dnd.autoScroll(e);
		var m = this.marginBox;
		this.host.onMove(this, {l: m.l + e.pageX, t: m.t + e.pageY});
	},
	onMouseUp: function(e){
		if(this.mouseButton == e.button){
			this.destroy();
		}
	},
	// utilities
	onFirstMove: function(){
		// summary: makes the node absolute; it is meant to be called only once
		this.node.style.position = "absolute";	// enforcing the absolute mode
		var m = dojo.marginBox(this.node);
		m.l -= this.marginBox.l;
		m.t -= this.marginBox.t;
		this.marginBox = m;
		this.host.onFirstMove(this);
		dojo.disconnect(this.events.pop());
	},
	destroy: function(){
		// summary: stops the move, deletes all references, so the object can be garbage-collected
		dojo.forEach(this.events, dojo.disconnect);
		// undo global settings
		var h = this.host;
		if(h && h.onMoveStop){
			h.onMoveStop(this);
		}
		// destroy objects
		this.events = this.node = null;
	}
});

}

if(!dojo._hasResource["dojo.dnd.Moveable"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.Moveable"] = true;
dojo.provide("dojo.dnd.Moveable");



dojo.declare("dojo.dnd.Moveable", null, {
	// object attributes (for markup)
	handle: "",
	delay: 0,
	skip: false,
	
	constructor: function(node, params){
		// summary: an object, which makes a node moveable
		// node: Node: a node (or node's id) to be moved
		// params: Object: an optional object with additional parameters;
		//	following parameters are recognized:
		//		handle: Node: a node (or node's id), which is used as a mouse handle
		//			if omitted, the node itself is used as a handle
		//		delay: Number: delay move by this number of pixels
		//		skip: Boolean: skip move of form elements
		//		mover: Object: a constructor of custom Mover
		this.node = dojo.byId(node);
		if(!params){ params = {}; }
		this.handle = params.handle ? dojo.byId(params.handle) : null;
		if(!this.handle){ this.handle = this.node; }
		this.delay = params.delay > 0 ? params.delay : 0;
		this.skip  = params.skip;
		this.mover = params.mover ? params.mover : dojo.dnd.Mover;
		this.events = [
			dojo.connect(this.handle, "onmousedown", this, "onMouseDown"),
			// cancel text selection and text dragging
			dojo.connect(this.handle, "ondragstart",   this, "onSelectStart"),
			dojo.connect(this.handle, "onselectstart", this, "onSelectStart")
		];
	},

	// markup methods
	markupFactory: function(params, node){
		return new dojo.dnd.Moveable(node, params);
	},

	// methods
	destroy: function(){
		// summary: stops watching for possible move, deletes all references, so the object can be garbage-collected
		dojo.forEach(this.events, dojo.disconnect);
		this.events = this.node = this.handle = null;
	},
	
	// mouse event processors
	onMouseDown: function(e){
		// summary: event processor for onmousedown, creates a Mover for the node
		// e: Event: mouse event
		if(this.skip && dojo.dnd.isFormElement(e)){ return; }
		if(this.delay){
			this.events.push(dojo.connect(this.handle, "onmousemove", this, "onMouseMove"));
			this.events.push(dojo.connect(this.handle, "onmouseup", this, "onMouseUp"));
			this._lastX = e.pageX;
			this._lastY = e.pageY;
		}else{
			new this.mover(this.node, e, this);
		}
		dojo.stopEvent(e);
	},
	onMouseMove: function(e){
		// summary: event processor for onmousemove, used only for delayed drags
		// e: Event: mouse event
		if(Math.abs(e.pageX - this._lastX) > this.delay || Math.abs(e.pageY - this._lastY) > this.delay){
			this.onMouseUp(e);
			new this.mover(this.node, e, this);
		}
		dojo.stopEvent(e);
	},
	onMouseUp: function(e){
		// summary: event processor for onmouseup, used only for delayed delayed drags
		// e: Event: mouse event
		dojo.disconnect(this.events.pop());
		dojo.disconnect(this.events.pop());
	},
	onSelectStart: function(e){
		// summary: event processor for onselectevent and ondragevent
		// e: Event: mouse event
		if(!this.skip || !dojo.dnd.isFormElement(e)){
			dojo.stopEvent(e);
		}
	},
	
	// local events
	onMoveStart: function(/* dojo.dnd.Mover */ mover){
		// summary: called before every move operation
		dojo.publish("/dnd/move/start", [mover]);
		dojo.addClass(dojo.body(), "dojoMove"); 
		dojo.addClass(this.node, "dojoMoveItem"); 
	},
	onMoveStop: function(/* dojo.dnd.Mover */ mover){
		// summary: called after every move operation
		dojo.publish("/dnd/move/stop", [mover]);
		dojo.removeClass(dojo.body(), "dojoMove");
		dojo.removeClass(this.node, "dojoMoveItem");
	},
	onFirstMove: function(/* dojo.dnd.Mover */ mover){
		// summary: called during the very first move notification,
		//	can be used to initialize coordinates, can be overwritten.
		
		// default implementation does nothing
	},
	onMove: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called during every move notification,
		//	should actually move the node, can be overwritten.
		this.onMoving(mover, leftTop);
		dojo.marginBox(mover.node, leftTop);
		this.onMoved(mover, leftTop);
	},
	onMoving: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called before every incremental move,
		//	can be overwritten.
		
		// default implementation does nothing
	},
	onMoved: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called after every incremental move,
		//	can be overwritten.
		
		// default implementation does nothing
	}
});

}

if(!dojo._hasResource["dojo.dnd.move"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.dnd.move"] = true;
dojo.provide("dojo.dnd.move");




dojo.declare("dojo.dnd.move.constrainedMoveable", dojo.dnd.Moveable, {
	// object attributes (for markup)
	constraints: function(){},
	within: false,
	
	// markup methods
	markupFactory: function(params, node){
		return new dojo.dnd.move.constrainedMoveable(node, params);
	},

	constructor: function(node, params){
		// summary: an object, which makes a node moveable
		// node: Node: a node (or node's id) to be moved
		// params: Object: an optional object with additional parameters;
		//	following parameters are recognized:
		//		constraints: Function: a function, which calculates a constraint box,
		//			it is called in a context of the moveable object.
		//		within: Boolean: restrict move within boundaries.
		//	the rest is passed to the base class
		if(!params){ params = {}; }
		this.constraints = params.constraints;
		this.within = params.within;
	},
	onFirstMove: function(/* dojo.dnd.Mover */ mover){
		// summary: called during the very first move notification,
		//	can be used to initialize coordinates, can be overwritten.
		var c = this.constraintBox = this.constraints.call(this, mover), m = mover.marginBox;
		c.r = c.l + c.w - (this.within ? m.w : 0);
		c.b = c.t + c.h - (this.within ? m.h : 0);
	},
	onMove: function(/* dojo.dnd.Mover */ mover, /* Object */ leftTop){
		// summary: called during every move notification,
		//	should actually move the node, can be overwritten.
		var c = this.constraintBox;
		leftTop.l = leftTop.l < c.l ? c.l : c.r < leftTop.l ? c.r : leftTop.l;
		leftTop.t = leftTop.t < c.t ? c.t : c.b < leftTop.t ? c.b : leftTop.t;
		dojo.marginBox(mover.node, leftTop);
	}
});

dojo.declare("dojo.dnd.move.boxConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {
	// object attributes (for markup)
	box: {},
	
	// markup methods
	markupFactory: function(params, node){
		return new dojo.dnd.move.boxConstrainedMoveable(node, params);
	},

	constructor: function(node, params){
		// summary: an object, which makes a node moveable
		// node: Node: a node (or node's id) to be moved
		// params: Object: an optional object with additional parameters;
		//	following parameters are recognized:
		//		box: Object: a constraint box
		//	the rest is passed to the base class
		var box = params && params.box;
		this.constraints = function(){ return box; };
	}
});

dojo.declare("dojo.dnd.move.parentConstrainedMoveable", dojo.dnd.move.constrainedMoveable, {
	// object attributes (for markup)
	area: "content",

	// markup methods
	markupFactory: function(params, node){
		return new dojo.dnd.move.parentConstrainedMoveable(node, params);
	},

	constructor: function(node, params){
		// summary: an object, which makes a node moveable
		// node: Node: a node (or node's id) to be moved
		// params: Object: an optional object with additional parameters;
		//	following parameters are recognized:
		//		area: String: a parent's area to restrict the move,
		//			can be "margin", "border", "padding", or "content".
		//	the rest is passed to the base class
		var area = params && params.area;
		this.constraints = function(){
			var n = this.node.parentNode, 
				s = dojo.getComputedStyle(n), 
				mb = dojo._getMarginBox(n, s);
			if(area == "margin"){
				return mb;	// Object
			}
			var t = dojo._getMarginExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			if(area == "border"){
				return mb;	// Object
			}
			t = dojo._getBorderExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			if(area == "padding"){
				return mb;	// Object
			}
			t = dojo._getPadExtents(n, s);
			mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
			return mb;	// Object
		};
	}
});

// WARNING: below are obsolete objects, instead of custom movers use custom moveables (above)

dojo.dnd.move.constrainedMover = function(fun, within){
	// summary: returns a constrained version of dojo.dnd.Mover
	// description: this function produces n object, which will put a constraint on 
	//	the margin box of dragged object in absolute coordinates
	// fun: Function: called on drag, and returns a constraint box
	// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
	//	otherwise the constraint is applied to the left-top corner
	var mover = function(node, e, notifier){
		dojo.dnd.Mover.call(this, node, e, notifier);
	};
	dojo.extend(mover, dojo.dnd.Mover.prototype);
	dojo.extend(mover, {
		onMouseMove: function(e){
			// summary: event processor for onmousemove
			// e: Event: mouse event
			dojo.dnd.autoScroll(e);
			var m = this.marginBox, c = this.constraintBox,
				l = m.l + e.pageX, t = m.t + e.pageY;
			l = l < c.l ? c.l : c.r < l ? c.r : l;
			t = t < c.t ? c.t : c.b < t ? c.b : t;
			this.host.onMove(this, {l: l, t: t});
		},
		onFirstMove: function(){
			// summary: called once to initialize things; it is meant to be called only once
			dojo.dnd.Mover.prototype.onFirstMove.call(this);
			var c = this.constraintBox = fun.call(this), m = this.marginBox;
			c.r = c.l + c.w - (within ? m.w : 0);
			c.b = c.t + c.h - (within ? m.h : 0);
		}
	});
	return mover;	// Object
};

dojo.dnd.move.boxConstrainedMover = function(box, within){
	// summary: a specialization of dojo.dnd.constrainedMover, which constrains to the specified box
	// box: Object: a constraint box (l, t, w, h)
	// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
	//	otherwise the constraint is applied to the left-top corner
	return dojo.dnd.move.constrainedMover(function(){ return box; }, within);	// Object
};

dojo.dnd.move.parentConstrainedMover = function(area, within){
	// summary: a specialization of dojo.dnd.constrainedMover, which constrains to the parent node
	// area: String: "margin" to constrain within the parent's margin box, "border" for the border box,
	//	"padding" for the padding box, and "content" for the content box; "content" is the default value.
	// within: Boolean: if true, constraints the whole dragged object withtin the rectangle, 
	//	otherwise the constraint is applied to the left-top corner
	var fun = function(){
		var n = this.node.parentNode, 
			s = dojo.getComputedStyle(n), 
			mb = dojo._getMarginBox(n, s);
		if(area == "margin"){
			return mb;	// Object
		}
		var t = dojo._getMarginExtents(n, s);
		mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
		if(area == "border"){
			return mb;	// Object
		}
		t = dojo._getBorderExtents(n, s);
		mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
		if(area == "padding"){
			return mb;	// Object
		}
		t = dojo._getPadExtents(n, s);
		mb.l += t.l, mb.t += t.t, mb.w -= t.w, mb.h -= t.h;
		return mb;	// Object
	};
	return dojo.dnd.move.constrainedMover(fun, within);	// Object
};

// patching functions one level up for compatibility

dojo.dnd.constrainedMover = dojo.dnd.move.constrainedMover;
dojo.dnd.boxConstrainedMover = dojo.dnd.move.boxConstrainedMover;
dojo.dnd.parentConstrainedMover = dojo.dnd.move.parentConstrainedMover;

}

if(!dojo._hasResource["dojo.fx"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.fx"] = true;
dojo.provide("dojo.fx");
dojo.provide("dojo.fx.Toggler");

dojo.fx.chain = function(/*dojo._Animation[]*/ animations){
	// summary: Chain a list of dojo._Animation s to run in sequence
	// example:
	//	|	dojo.fx.chain([
	//	|		dojo.fadeIn({ node:node }),
	//	|		dojo.fadeOut({ node:otherNode })
	//	|	]).play();
	//
	var first = animations.shift();
	var previous = first;
	dojo.forEach(animations, function(current){
		dojo.connect(previous, "onEnd", current, "play");
		previous = current;
	});
	return first; // dojo._Animation
};

dojo.fx.combine = function(/*dojo._Animation[]*/ animations){
	// summary: Combine a list of dojo._Animation s to run in parallel
	// example:
	//	|	dojo.fx.combine([
	//	|		dojo.fadeIn({ node:node }),
	//	|		dojo.fadeOut({ node:otherNode })
	//	|	]).play();
	var ctr = new dojo._Animation({ curve: [0, 1] });
	if(!animations.length){ return ctr; }
	// animations.sort(function(a, b){ return a.duration-b.duration; });
	ctr.duration = animations[0].duration;
	dojo.forEach(animations, function(current){
		dojo.forEach([ "play", "pause", "stop" ],
			function(e){
				if(current[e]){
					dojo.connect(ctr, e, current, e);
				}
			}
		);
	});
	return ctr; // dojo._Animation
};

dojo.declare("dojo.fx.Toggler", null, {
	// summary:
	//		class constructor for an animation toggler. It accepts a packed
	//		set of arguments about what type of animation to use in each
	//		direction, duration, etc.
	//
	// example:
	//	|	var t = new dojo.fx.Toggler({
	//	|		node: "nodeId",
	//	|		showDuration: 500,
	//	|		// hideDuration will default to "200"
	//	|		showFunc: dojo.wipeIn, 
	//	|		// hideFunc will default to "fadeOut"
	//	|	});
	//	|	t.show(100); // delay showing for 100ms
	//	|	// ...time passes...
	//	|	t.hide();

	// FIXME: need a policy for where the toggler should "be" the next
	// time show/hide are called if we're stopped somewhere in the
	// middle.

	constructor: function(args){
		var _t = this;

		dojo.mixin(_t, args);
		_t.node = args.node;
		_t._showArgs = dojo.mixin({}, args);
		_t._showArgs.node = _t.node;
		_t._showArgs.duration = _t.showDuration;
		_t.showAnim = _t.showFunc(_t._showArgs);

		_t._hideArgs = dojo.mixin({}, args);
		_t._hideArgs.node = _t.node;
		_t._hideArgs.duration = _t.hideDuration;
		_t.hideAnim = _t.hideFunc(_t._hideArgs);

		dojo.connect(_t.showAnim, "beforeBegin", dojo.hitch(_t.hideAnim, "stop", true));
		dojo.connect(_t.hideAnim, "beforeBegin", dojo.hitch(_t.showAnim, "stop", true));
	},

	// node: DomNode
	//	the node to toggle
	node: null,

	// showFunc: Function
	//	The function that returns the dojo._Animation to show the node
	showFunc: dojo.fadeIn,

	// hideFunc: Function	
	//	The function that returns the dojo._Animation to hide the node
	hideFunc: dojo.fadeOut,

	// showDuration:
	//	Time in milliseconds to run the show Animation
	showDuration: 200,

	// hideDuration:
	//	Time in milliseconds to run the hide Animation
	hideDuration: 200,

	/*=====
	_showArgs: null,
	_showAnim: null,

	_hideArgs: null,
	_hideAnim: null,

	_isShowing: false,
	_isHiding: false,
	=====*/

	show: function(delay){
		// summary: Toggle the node to showing
		return this.showAnim.play(delay || 0);
	},

	hide: function(delay){
		// summary: Toggle the node to hidden
		return this.hideAnim.play(delay || 0);
	}
});

dojo.fx.wipeIn = function(/*Object*/ args){
	// summary
	//		Returns an animation that will expand the
	//		node defined in 'args' object from it's current height to
	//		it's natural height (with no scrollbar).
	//		Node must have no margin/border/padding.
	args.node = dojo.byId(args.node);
	var node = args.node, s = node.style;

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			height: {
				// wrapped in functions so we wait till the last second to query (in case value has changed)
				start: function(){
					// start at current [computed] height, but use 1px rather than 0
					// because 0 causes IE to display the whole panel
					s.overflow="hidden";
					if(s.visibility=="hidden"||s.display=="none"){
						s.height="1px";
						s.display="";
						s.visibility="";
						return 1;
					}else{
						var height = dojo.style(node, "height");
						return Math.max(height, 1);
					}
				},
				end: function(){
					return node.scrollHeight;
				}
			}
		}
	}, args));

	dojo.connect(anim, "onEnd", function(){ 
		s.height = "auto";
	});

	return anim; // dojo._Animation
}

dojo.fx.wipeOut = function(/*Object*/ args){
	// summary
	//		Returns an animation that will shrink node defined in "args"
	//		from it's current height to 1px, and then hide it.
	var node = args.node = dojo.byId(args.node);
	var s = node.style;

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			height: {
				end: 1 // 0 causes IE to display the whole panel
			}
		}
	}, args));

	dojo.connect(anim, "beforeBegin", function(){
		s.overflow = "hidden";
		s.display = "";
	});
	dojo.connect(anim, "onEnd", function(){
		s.height = "auto";
		s.display = "none";
	});

	return anim; // dojo._Animation
}

dojo.fx.slideTo = function(/*Object?*/ args){
	// summary
	//		Returns an animation that will slide "node" 
	//		defined in args Object from its current position to
	//		the position defined by (args.left, args.top).
	// example:
	//	|	dojo.fx.slideTo({ node: node, left:"40", top:"50", unit:"px" }).play()

	var node = (args.node = dojo.byId(args.node));
	
	var top = null;
	var left = null;
	
	var init = (function(n){
		return function(){
			var cs = dojo.getComputedStyle(n);
			var pos = cs.position;
			top = (pos == 'absolute' ? n.offsetTop : parseInt(cs.top) || 0);
			left = (pos == 'absolute' ? n.offsetLeft : parseInt(cs.left) || 0);
			if(pos != 'absolute' && pos != 'relative'){
				var ret = dojo.coords(n, true);
				top = ret.y;
				left = ret.x;
				n.style.position="absolute";
				n.style.top=top+"px";
				n.style.left=left+"px";
			}
		};
	})(node);
	init();

	var anim = dojo.animateProperty(dojo.mixin({
		properties: {
			top: { end: args.top||0 },
			left: { end: args.left||0 }
		}
	}, args));
	dojo.connect(anim, "beforeBegin", anim, init);

	return anim; // dojo._Animation
}

}

if(!dojo._hasResource["dijit.form.Form"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.form.Form"] = true;
dojo.provide("dijit.form.Form");




dojo.declare("dijit.form._FormMixin", null,
	{
		/*
		summary:
			Widget corresponding to <form> tag, for validation and serialization

		usage:
			<form dojoType="dijit.form.Form" id="myForm">
				Name: <input type="text" name="name" />
			</form>
			myObj={name: "John Doe"};
			dijit.byId('myForm').setValues(myObj);

			myObj=dijit.byId('myForm').getValues();
		TODO:
		* Repeater
		* better handling for arrays.  Often form elements have names with [] like
		* people[3].sex (for a list of people [{name: Bill, sex: M}, ...])

		*/

		// HTML <FORM> attributes

		action: "",
		method: "",
		enctype: "",
		name: "",
		"accept-charset": "",
		accept: "",
		target: "",

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),
			{action: "", method: "", enctype: "", "accept-charset": "", accept: "", target: ""}),

		// execute: Function
		//	User defined function to do stuff when the user hits the submit button
		execute: function(/*Object*/ formContents){},

		// onCancel: Function
		//	Callback when user has canceled dialog, to notify container
		//	(user shouldn't override)
		onCancel: function(){},

		// onExecute: Function
		//	Callback when user is about to execute dialog, to notify container
		//	(user shouldn't override)
		onExecute: function(){},

		templateString: "<form dojoAttachPoint='containerNode' dojoAttachEvent='onsubmit:_onSubmit' name='${name}' enctype='multipart/form-data'></form>",

		_onSubmit: function(/*event*/e) {
			// summary: callback when user hits submit button
			dojo.stopEvent(e);
			this.onExecute();	// notify container that we are about to execute
			this.execute(this.getValues());
		},

		submit: function() {
			// summary: programatically submit form
			this.containerNode.submit();
		},

		setValues: function(/*object*/obj) {
			// summary: fill in form values from a JSON structure

			// generate map from name --> [list of widgets with that name]
			var map = {};
			dojo.forEach(this.getDescendants(), function(widget){
				if(!widget.name){ return; }
				var entry = map[widget.name] || (map[widget.name] = [] );
				entry.push(widget);
			});

			// call setValue() or setChecked() for each widget, according to obj
			for(var name in map){
				var widgets = map[name],						// array of widgets w/this name
					values = dojo.getObject(name, false, obj);	// list of values for those widgets
				if(!dojo.isArray(values)){
					values = [ values ];
				}
				if(widgets[0].setChecked){
					// for checkbox/radio, values is a list of which widgets should be checked
					dojo.forEach(widgets, function(w, i){
						w.setChecked(dojo.indexOf(values, w.value) != -1);
					});
				}else{
					// otherwise, values is a list of values to be assigned sequentially to each widget
					dojo.forEach(widgets, function(w, i){
						w.setValue(values[i]);
					});					
				}
			}

			/***
			 * 	TODO: code for plain input boxes (this shouldn't run for inputs that are part of widgets

			dojo.forEach(this.containerNode.elements, function(element){
				if (element.name == ''){return};	// like "continue"	
				var namePath = element.name.split(".");
				var myObj=obj;
				var name=namePath[namePath.length-1];
				for(var j=1,len2=namePath.length;j<len2;++j) {
					var p=namePath[j - 1];
					// repeater support block
					var nameA=p.split("[");
					if (nameA.length > 1) {
						if(typeof(myObj[nameA[0]]) == "undefined") {
							myObj[nameA[0]]=[ ];
						} // if

						nameIndex=parseInt(nameA[1]);
						if(typeof(myObj[nameA[0]][nameIndex]) == "undefined") {
							myObj[nameA[0]][nameIndex]={};
						}
						myObj=myObj[nameA[0]][nameIndex];
						continue;
					} // repeater support ends

					if(typeof(myObj[p]) == "undefined") {
						myObj=undefined;
						break;
					};
					myObj=myObj[p];
				}

				if (typeof(myObj) == "undefined") {
					return;		// like "continue"
				}
				if (typeof(myObj[name]) == "undefined" && this.ignoreNullValues) {
					return;		// like "continue"
				}

				// TODO: widget values (just call setValue() on the widget)

				switch(element.type) {
					case "checkbox":
						element.checked = (name in myObj) &&
							dojo.some(myObj[name], function(val){ return val==element.value; });
						break;
					case "radio":
						element.checked = (name in myObj) && myObj[name]==element.value;
						break;
					case "select-multiple":
						element.selectedIndex=-1;
						dojo.forEach(element.options, function(option){
							option.selected = dojo.some(myObj[name], function(val){ return option.value == val; });
						});
						break;
					case "select-one":
						element.selectedIndex="0";
						dojo.forEach(element.options, function(option){
							option.selected = option.value == myObj[name];
						});
						break;
					case "hidden":
					case "text":
					case "textarea":
					case "password":
						element.value = myObj[name] || "";
						break;
				}
	  		});
	  		*/
		},

		getValues: function() {
			// summary: generate JSON structure from form values

			// get widget values
			var obj = {};
			dojo.forEach(this.getDescendants(), function(widget){
				var value = widget.getValue ? widget.getValue() : widget.value;
				var name = widget.name;
				if(!name){ return; }

				// Store widget's value(s) as a scalar, except for checkboxes which are automatically arrays
				if(widget.setChecked){
					if(/Radio/.test(widget.declaredClass)){
						// radio button
						if(widget.checked){
							dojo.setObject(name, value, obj);
						}
					}else{
						// checkbox/toggle button
						var ary=dojo.getObject(name, false, obj);
						if(!ary){
							ary=[];
							dojo.setObject(name, ary, obj);
						}
						if(widget.checked){
							ary.push(value);
						}
					}
				}else{
					// plain input
					dojo.setObject(name, value, obj);
				}
			});

			/***
			 * code for plain input boxes (see also dojo.formToObject, can we use that instead of this code?
			 * but it doesn't understand [] notation, presumably)
			var obj = { };
			dojo.forEach(this.containerNode.elements, function(elm){
				if (!elm.name)	{
					return;		// like "continue"
				}
				var namePath = elm.name.split(".");
				var myObj=obj;
				var name=namePath[namePath.length-1];
				for(var j=1,len2=namePath.length;j<len2;++j) {
					var nameIndex = null;
					var p=namePath[j - 1];
					var nameA=p.split("[");
					if (nameA.length > 1) {
						if(typeof(myObj[nameA[0]]) == "undefined") {
							myObj[nameA[0]]=[ ];
						} // if
						nameIndex=parseInt(nameA[1]);
						if(typeof(myObj[nameA[0]][nameIndex]) == "undefined") {
							myObj[nameA[0]][nameIndex]={};
						}
					} else if(typeof(myObj[nameA[0]]) == "undefined") {
						myObj[nameA[0]]={}
					} // if

					if (nameA.length == 1) {
						myObj=myObj[nameA[0]];
					} else {
						myObj=myObj[nameA[0]][nameIndex];
					} // if
				} // for

				if ((elm.type != "select-multiple" && elm.type != "checkbox" && elm.type != "radio") || (elm.type=="radio" && elm.checked)) {
					if(name == name.split("[")[0]) {
						myObj[name]=elm.value;
					} else {
						// can not set value when there is no name
					}
				} else if (elm.type == "checkbox" && elm.checked) {
					if(typeof(myObj[name]) == 'undefined') {
						myObj[name]=[ ];
					}
					myObj[name].push(elm.value);
				} else if (elm.type == "select-multiple") {
					if(typeof(myObj[name]) == 'undefined') {
						myObj[name]=[ ];
					}
					for (var jdx=0,len3=elm.options.length; jdx<len3; ++jdx) {
						if (elm.options[jdx].selected) {
							myObj[name].push(elm.options[jdx].value);
						}
					}
				} // if
				name=undefined;
			}); // forEach
			***/
			return obj;
		},

	 	isValid: function() {
	 		// TODO: ComboBox might need time to process a recently input value.  This should be async?
	 		// make sure that every widget that has a validator function returns true
	 		return dojo.every(this.getDescendants(), function(widget){
	 			return !widget.isValid || widget.isValid();
	 		});
		}
	});

dojo.declare(
	"dijit.form.Form",
	[dijit._Widget, dijit._Templated, dijit.form._FormMixin],
	null
);

}

if(!dojo._hasResource["dijit.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Dialog"] = true;
dojo.provide("dijit.Dialog");









dojo.declare(
	"dijit.DialogUnderlay",
	[dijit._Widget, dijit._Templated],
	{
		// summary: the thing that grays out the screen behind the dialog

		// Template has two divs; outer div is used for fade-in/fade-out, and also to hold background iframe.
		// Inner div has opacity specified in CSS file.
		templateString: "<div class=dijitDialogUnderlayWrapper id='${id}_underlay'><div class=dijitDialogUnderlay dojoAttachPoint='node'></div></div>",

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.bgIframe = new dijit.BackgroundIframe(this.domNode);
		},

		layout: function(){
			// summary
			//		Sets the background to the size of the viewport (rather than the size
			//		of the document) since we need to cover the whole browser window, even
			//		if the document is only a few lines long.

			var viewport = dijit.getViewport();
			var is = this.node.style,
				os = this.domNode.style;

			os.top = viewport.t + "px";
			os.left = viewport.l + "px";
			is.width = viewport.w + "px";
			is.height = viewport.h + "px";

			// process twice since the scroll bar may have been removed
			// by the previous resizing
			var viewport2 = dijit.getViewport();
			if(viewport.w != viewport2.w){ is.width = viewport2.w + "px"; }
			if(viewport.h != viewport2.h){ is.height = viewport2.h + "px"; }
		},

		show: function(){
			this.domNode.style.display = "block";
			this.layout();
			if(this.bgIframe.iframe){
				this.bgIframe.iframe.style.display = "block";
			}
			this._resizeHandler = this.connect(window, "onresize", "layout");
		},

		hide: function(){
			this.domNode.style.display = "none";
			if(this.bgIframe.iframe){
				this.bgIframe.iframe.style.display = "none";
			}
			this.disconnect(this._resizeHandler);
		},

		uninitialize: function(){
			if(this.bgIframe){
				this.bgIframe.destroy();
			}
		}
	}
);

dojo.declare(
	"dijit.Dialog",
	[dijit.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a modal dialog window, blocking access to the screen
		//		and also graying out the screen Dialog is extended from
		//		ContentPane so it supports all the same parameters (href, etc.)

		templateString: null,
		templateString:"<div class=\"dijitDialog\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dijitDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">\r\n\t<span dojoAttachPoint=\"titleNode\" class=\"dijitDialogTitle\">${title}</span>\r\n\t<span dojoAttachPoint=\"closeButtonNode\" class=\"dijitDialogCloseIcon\" dojoAttachEvent=\"onclick: hide\">\r\n\t\t<span dojoAttachPoint=\"closeText\" class=\"closeText\">x</span>\r\n\t</span>\r\n\t</div>\r\n\t\t<div dojoAttachPoint=\"containerNode\" class=\"dijitDialogPaneContent\"></div>\r\n\t<span dojoAttachPoint=\"tabEnd\" dojoAttachEvent=\"onfocus:_cycleFocus\" tabindex=\"0\"></span>\r\n</div>\r\n",

		// open: Boolean
		//		is True or False depending on state of dialog
		open: false,

		// duration: Integer
		//		The time in milliseconds it takes the dialog to fade in and out
		duration: 400,

		_lastFocusItem:null,

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap),
			{title: "titleBar"}),

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.inherited("postCreate",arguments);
			this.domNode.style.display="none";
			this.connect(this, "onExecute", "hide");
			this.connect(this, "onCancel", "hide");
		},

		onLoad: function(){
			// summary: 
			//		when href is specified we need to reposition the dialog after the data is loaded
			this._position();
			this.inherited("onLoad",arguments);
		},

		_setup: function(){
			// summary:
			//		stuff we need to do before showing the Dialog for the first
			//		time (but we defer it until right beforehand, for
			//		performance reasons)

			this._modalconnects = [];

			if(this.titleBar){
				this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
			}

			this._underlay = new dijit.DialogUnderlay();

			var node = this.domNode;
			this._fadeIn = dojo.fx.combine(
				[dojo.fadeIn({
					node: node,
					duration: this.duration
				 }),
				 dojo.fadeIn({
					node: this._underlay.domNode,
					duration: this.duration,
					onBegin: dojo.hitch(this._underlay, "show")
				 })
				]
			);

			this._fadeOut = dojo.fx.combine(
				[dojo.fadeOut({
					node: node,
					duration: this.duration,
					onEnd: function(){
						node.style.display="none";
					}
				 }),
				 dojo.fadeOut({
					node: this._underlay.domNode,
					duration: this.duration,
					onEnd: dojo.hitch(this._underlay, "hide")
				 })
				]
			);
		},

		uninitialize: function(){
			if(this._underlay){
				this._underlay.destroy();
			}
		},

		_position: function(){
			// summary: position modal dialog in center of screen
			
			if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
			var viewport = dijit.getViewport();
			var mb = dojo.marginBox(this.domNode);

			var style = this.domNode.style;
			style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
			style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary:  called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to titleBar

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.titleBar.focus();
		},

		_onKey: function(/*Event*/ evt){
			if(evt.keyCode){
				var node = evt.target;
				// see if we are shift-tabbing from titleBar
				if(node == this.titleBar && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
					if(this._lastFocusItem){
						this._lastFocusItem.focus(); // send focus to last item in dialog if known
					}
					dojo.stopEvent(evt);
				}else{
					// see if the key is for the dialog
					while(node){
						if(node == this.domNode){
							if(evt.keyCode == dojo.keys.ESCAPE){
								this.hide(); 
							}else{
								return; // just let it go
							}
						}
						node = node.parentNode;
					}
					// this key is for the disabled document window
					if(evt.keyCode != dojo.keys.TAB){ // allow tabbing into the dialog for a11y
						dojo.stopEvent(evt);
					// opera won't tab to a div
					}else if (!dojo.isOpera){
						try{
							this.titleBar.focus();
						}catch(e){/*squelch*/}
					}
				}
			}
		},

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

			this._fadeIn.play();

			this._savedFocus = dijit.getFocus(this);

			// set timeout to allow the browser to render dialog
			setTimeout(dojo.hitch(this, function(){
				dijit.focus(this.titleBar);
			}), 50);
		},

		hide: function(){
			// summary
			//		Hide the dialog

			// if we haven't been initialized yet then we aren't showing and we can just return		
			if(!this._alreadyInitialized){
				return;
			}

			if(this._fadeIn.status() == "playing"){
				this._fadeIn.stop();
			}
			this._fadeOut.play();

			if (this._scrollConnected){
				this._scrollConnected = false;
			}
			dojo.forEach(this._modalconnects, dojo.disconnect);
			this._modalconnects = [];

			this.connect(this._fadeOut,"onEnd",dojo.hitch(this,function(){
				dijit.focus(this._savedFocus);
			}));
			this.open = false;
		},

		layout: function() {
			// summary: position the Dialog and the underlay
			if(this.domNode.style.display == "block"){
				this._underlay.layout();
				this._position();
			}
		}
	}
);

dojo.declare(
	"dijit.TooltipDialog",
	[dijit.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a dialog that appears like a Tooltip
		// title: String
		// 		Description of tooltip dialog (required for a11Y)
		title: "",

		_lastFocusItem: null,

		templateString: null,
		templateString:"<div class=\"dijitTooltipDialog\" >\r\n\t<div class=\"dijitTooltipContainer\">\r\n\t\t<div class =\"dijitTooltipContents dijitTooltipFocusNode\" dojoAttachPoint=\"containerNode\" tabindex=\"0\" waiRole=\"dialog\"></div>\r\n\t</div>\r\n\t<span dojoAttachPoint=\"tabEnd\" tabindex=\"0\" dojoAttachEvent=\"focus:_cycleFocus\"></span>\r\n\t<div class=\"dijitTooltipConnector\" ></div>\r\n</div>\r\n",

		postCreate: function(){
			this.inherited("postCreate",arguments);
			this.connect(this.containerNode, "onkeypress", "_onKey");

			// IE doesn't bubble onblur events - use ondeactivate instead
			var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
			this.connect(this.containerNode, ev, "_findLastFocus");
			this.containerNode.title=this.title;
		},

		orient: function(/*Object*/ corner){
			// summary: configure widget to be displayed in given position relative to the button
			this.domNode.className="dijitTooltipDialog " +" dijitTooltipAB"+(corner.charAt(1)=='L'?"Left":"Right")+" dijitTooltip"+(corner.charAt(0)=='T' ? "Below" : "Above");
		},

		onOpen: function(/*Object*/ pos){
			// summary: called when dialog is displayed
			this.orient(pos.corner);
			this._loadCheck(); // lazy load trigger
			this.containerNode.focus();
		},

		_onKey: function(/*Event*/ evt){
			// summary: keep keyboard focus in dialog; close dialog on escape key
			if(evt.keyCode == dojo.keys.ESCAPE){
				this.onCancel();
			}else if(evt.target == this.containerNode && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
				if (this._lastFocusItem){
					this._lastFocusItem.focus();
				}
				dojo.stopEvent(evt);
			}else if(evt.keyCode == dojo.keys.TAB){
				// we want the browser's default tab handling to move focus
				// but we don't want the tab to propagate upwards
				evt.stopPropagation();
			}
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary: called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to containerNode

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.containerNode.focus();
		}
	}	
);


}

if(!dojo._hasResource["dijit._editor.plugins.LinkDialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.plugins.LinkDialog"] = true;
dojo.provide("dijit._editor.plugins.LinkDialog");











dojo.declare("dijit._editor.plugins.DualStateDropDownButton",
	dijit.form.DropDownButton,
	{
		// summary: a DropDownButton but button can be displayed in two states (checked or unchecked)
		setChecked: dijit.form.ToggleButton.prototype.setChecked
	}
);

dojo.declare("dijit._editor.plugins.UrlTextBox",
	dijit.form.ValidationTextBox,
	{
		// summary: the URL input box we use in our dialog

		// regular expression for URLs, generated from dojo.regexp.url()
		regExp: "((https?|ftps?)\\://|)(([0-9a-zA-Z]([-0-9a-zA-Z]{0,61}[0-9a-zA-Z])?\\.)+(arpa|aero|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|xxx|jobs|mobi|post|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|eu|es|et|fi|fj|fk|fm|fo|fr|ga|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kr|kw|ky|kz|la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sk|sl|sm|sn|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|tn|to|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw)|(((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])|(0[xX]0*[\\da-fA-F]?[\\da-fA-F]\\.){3}0[xX]0*[\\da-fA-F]?[\\da-fA-F]|(0+[0-3][0-7][0-7]\\.){3}0+[0-3][0-7][0-7]|(0|[1-9]\\d{0,8}|[1-3]\\d{9}|4[01]\\d{8}|42[0-8]\\d{7}|429[0-3]\\d{6}|4294[0-8]\\d{5}|42949[0-5]\\d{4}|429496[0-6]\\d{3}|4294967[01]\\d{2}|42949672[0-8]\\d|429496729[0-5])|0[xX]0*[\\da-fA-F]{1,8}|([\\da-fA-F]{1,4}\\:){7}[\\da-fA-F]{1,4}|([\\da-fA-F]{1,4}\\:){6}((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])))(\\:(0|[1-9]\\d*))?(/([^?#\\s/]+/)*)?([^?#\\s/]+(\\?[^?#\\s/]*)?(#[A-Za-z][\\w.:-]*)?)?",

		required: true,

		postMixInProperties: function(){
			this.inherited("postMixInProperties", arguments);
			this.invalidMessage = dojo.i18n.getLocalization("dijit._editor", "LinkDialog", this.lang).urlInvalidMessage;
		},

		getValue: function(){
			if(!/^(https?|ftps?)/.test(this.textbox.value)){
				this.textbox.value="http://"+this.textbox.value;
			}
			return this.textbox.value;
		}
	}
);

dojo.declare("dijit._editor.plugins.LinkDialog",
	dijit._editor._Plugin,
	{
		buttonClass: dijit._editor.plugins.DualStateDropDownButton,
		useDefaultCommand: false,
		command: "createLink",
		linkDialogTemplate: [
			"<label for='urlInput'>${url}&nbsp;</label>",
			"<input dojoType=dijit._editor.plugins.UrlTextBox name='urlInput'><br>",
			"<label for='textInput'>${text}&nbsp;</label>",
			"<input dojoType=dijit.form.TextBox name='textInput'>",
			"<br>",
			"<button dojoType=dijit.form.Button type='submit'>${set}</button>"
		].join(""),

		constructor: function(){
			var _this = this;
			var messages = dojo.i18n.getLocalization("dijit._editor", "LinkDialog", this.lang);
			this.dropDown = new dijit.TooltipDialog({
				title: messages.title,
				execute: dojo.hitch(this, "setValue"),
				onOpen: function(){
					_this._onOpenDialog();
					dijit.TooltipDialog.prototype.onOpen.apply(this, arguments);
				},
				onCancel: function(){
					setTimeout(dojo.hitch(_this, "_onCloseDialog"),0);
				},
				onClose: dojo.hitch(this, "_onCloseDialog")
			});
			this.dropDown.setContent(dojo.string.substitute(this.linkDialogTemplate, messages));
			this.dropDown.startup();
		},

		setValue: function(args){
			// summary: callback from the dialog when user hits "set" button
			//TODO: prevent closing popup if the text is empty
			this._onCloseDialog();
			if(dojo.isIE){ //see #4151
				var a = dojo.withGlobal(this.editor.window, "getAncestorElement",dijit._editor.selection, ['a']);
				if(a){
					dojo.withGlobal(this.editor.window, "selectElement",dijit._editor.selection, [a]);
				}
			}
			var attstr='href="'+args.urlInput+'" _djrealurl="'+args.urlInput+'"';
//			console.log(args,this.editor,'<a '+attstr+'>'+args.textInput+'</a>');
			this.editor.execCommand('inserthtml', '<a '+attstr+'>'+args.textInput+'</a>');
//			this.editor.execCommand(this.command, args.urlInput);
 		},

//		_savedSelection: null,
		_onCloseDialog: function(){
			// FIXME: IE is really messed up here!!
			if(dojo.isIE){
				if(this._savedSelection){
					var b=this._savedSelection;
					this._savedSelection=null;
					this.editor.focus();
					var range = this.editor.document.selection.createRange();
					range.moveToBookmark(b);
					range.select();
				}
			}else{this.editor.focus();
			}
		},
		_onOpenDialog: function(){
			var a = dojo.withGlobal(this.editor.window, "getAncestorElement",dijit._editor.selection, ['a']);
			var url='',text='';
			if(a){
				url=a.getAttribute('_djrealurl');
				text=a.textContent||a.innerText;
				dojo.withGlobal(this.editor.window, "selectElement",dijit._editor.selection, [a,true]);
			}else{
				text=dojo.withGlobal(this.editor.window, dijit._editor.selection.getSelectedText);
			}
			// FIXME: IE is *really* b0rken
			if(dojo.isIE){
				var range = this.editor.document.selection.createRange();
				this._savedSelection = range.getBookmark();
			}
			this.dropDown.setValues({'urlInput':url,'textInput':text});
			//dijit.focus(this.urlInput);
		},

		updateState: function(){
			// summary: change shading on button if we are over a link (or not)

			var _e = this.editor;
			if(!_e){ return; }
			if(!_e.isLoaded){ return; }
			if(this.button){
				try{
					// display button differently if there is an existing link associated with the current selection
					var hasA = dojo.withGlobal(this.editor.window, "hasAncestorElement",dijit._editor.selection, ['a']);
					this.button.setChecked(hasA);
				}catch(e){
					console.debug(e); //FIXME: probably shouldn't squelch an exception here
				}
			}
		}
	}
);

}

({
	title: "Link URL",
	url: "URL:",
	text: "Text:",
	set: "Set",
	urlInvalidMessage: "Invalid URL.  Enter a full URL like 'http://www.dojotoolkit.org'"	
})

if(!dojo._hasResource["dojo.colors"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojo.colors"] = true;
dojo.provide("dojo.colors");

(function(){
	// this is a standard convertion prescribed by the CSS3 Color Module
	var hue2rgb = function(m1, m2, h){
		if(h < 0){ ++h; }
		if(h > 1){ --h; }
		var h6 = 6 * h;
		if(h6 < 1){ return m1 + (m2 - m1) * h6; }
		if(2 * h < 1){ return m2; }
		if(3 * h < 2){ return m1 + (m2 - m1) * (2 / 3 - h) * 6; }
		return m1;
	};
	
	dojo.colorFromRgb = function(/*String*/ color, /*dojo.Color?*/ obj){
		// summary:
		//		get rgb(a) array from css-style color declarations
		// description:
		//		this function can handle all 4 CSS3 Color Module formats: rgb,
		//		rgba, hsl, hsla, including rgb(a) with percentage values.
		var m = color.toLowerCase().match(/^(rgba?|hsla?)\(([\s\.\-,%0-9]+)\)/);
		if(m){
			var c = m[2].split(/\s*,\s*/), l = c.length, t = m[1];
			if((t == "rgb" && l == 3) || (t == "rgba" && l == 4)){
				var r = c[0];
				if(r.charAt(r.length - 1) == "%"){
					// 3 rgb percentage values
					var a = dojo.map(c, function(x){
						return parseFloat(x) * 2.56;
					});
					if(l == 4){ a[3] = c[3]; }
					return dojo.colorFromArray(a, obj);	// dojo.Color
				}
				return dojo.colorFromArray(c, obj);	// dojo.Color
			}
			if((t == "hsl" && l == 3) || (t == "hsla" && l == 4)){
				// normalize hsl values
				var H = ((parseFloat(c[0]) % 360) + 360) % 360 / 360,
					S = parseFloat(c[1]) / 100,
					L = parseFloat(c[2]) / 100,
					// calculate rgb according to the algorithm 
					// recommended by the CSS3 Color Module 
					m2 = L <= 0.5 ? L * (S + 1) : L + S - L * S, 
					m1 = 2 * L - m2,
					a = [hue2rgb(m1, m2, H + 1 / 3) * 256,
						hue2rgb(m1, m2, H) * 256, hue2rgb(m1, m2, H - 1 / 3) * 256, 1];
				if(l == 4){ a[3] = c[3]; }
				return dojo.colorFromArray(a, obj);	// dojo.Color
			}
		}
		return null;	// dojo.Color
	};
	
	var confine = function(c, low, high){
		// summary:
		//		sanitize a color component by making sure it is a number,
		//		and clamping it to valid values
		c = Number(c);
		return isNaN(c) ? high : c < low ? low : c > high ? high : c;	// Number
	};
	
	dojo.Color.prototype.sanitize = function(){
		// summary: makes sure that the object has correct attributes
		var t = this;
		t.r = Math.round(confine(t.r, 0, 255));
		t.g = Math.round(confine(t.g, 0, 255));
		t.b = Math.round(confine(t.b, 0, 255));
		t.a = confine(t.a, 0, 1);
		return this;	// dojo.Color
	};
})();


dojo.colors.makeGrey = function(/*Number*/ g, /*Number?*/ a){
	// summary: creates a greyscale color with an optional alpha
	return dojo.colorFromArray([g, g, g, a]);
};


// mixin all CSS3 named colors not already in _base, along with SVG 1.0 variant spellings
dojo.Color.named = dojo.mixin({
	aliceblue:	[240,248,255],
	antiquewhite:	[250,235,215],
	aquamarine:	[127,255,212],
	azure:	[240,255,255],
	beige:	[245,245,220],
	bisque:	[255,228,196],
	blanchedalmond:	[255,235,205],
	blueviolet:	[138,43,226],
	brown:	[165,42,42],
	burlywood:	[222,184,135],
	cadetblue:	[95,158,160],
	chartreuse:	[127,255,0],
	chocolate:	[210,105,30],
	coral:	[255,127,80],
	cornflowerblue:	[100,149,237],
	cornsilk:	[255,248,220],
	crimson:	[220,20,60],
	cyan:	[0,255,255],
	darkblue:	[0,0,139],
	darkcyan:	[0,139,139],
	darkgoldenrod:	[184,134,11],
	darkgray:	[169,169,169],
	darkgreen:	[0,100,0],
	darkgrey:	[169,169,169],
	darkkhaki:	[189,183,107],
	darkmagenta:	[139,0,139],
	darkolivegreen:	[85,107,47],
	darkorange:	[255,140,0],
	darkorchid:	[153,50,204],
	darkred:	[139,0,0],
	darksalmon:	[233,150,122],
	darkseagreen:	[143,188,143],
	darkslateblue:	[72,61,139],
	darkslategray:	[47,79,79],
	darkslategrey:	[47,79,79],
	darkturquoise:	[0,206,209],
	darkviolet:	[148,0,211],
	deeppink:	[255,20,147],
	deepskyblue:	[0,191,255],
	dimgray:	[105,105,105],
	dimgrey:	[105,105,105],
	dodgerblue:	[30,144,255],
	firebrick:	[178,34,34],
	floralwhite:	[255,250,240],
	forestgreen:	[34,139,34],
	gainsboro:	[220,220,220],
	ghostwhite:	[248,248,255],
	gold:	[255,215,0],
	goldenrod:	[218,165,32],
	greenyellow:	[173,255,47],
	grey:	[128,128,128],
	honeydew:	[240,255,240],
	hotpink:	[255,105,180],
	indianred:	[205,92,92],
	indigo:	[75,0,130],
	ivory:	[255,255,240],
	khaki:	[240,230,140],
	lavender:	[230,230,250],
	lavenderblush:	[255,240,245],
	lawngreen:	[124,252,0],
	lemonchiffon:	[255,250,205],
	lightblue:	[173,216,230],
	lightcoral:	[240,128,128],
	lightcyan:	[224,255,255],
	lightgoldenrodyellow:	[250,250,210],
	lightgray:	[211,211,211],
	lightgreen:	[144,238,144],
	lightgrey:	[211,211,211],
	lightpink:	[255,182,193],
	lightsalmon:	[255,160,122],
	lightseagreen:	[32,178,170],
	lightskyblue:	[135,206,250],
	lightslategray:	[119,136,153],
	lightslategrey:	[119,136,153],
	lightsteelblue:	[176,196,222],
	lightyellow:	[255,255,224],
	limegreen:	[50,205,50],
	linen:	[250,240,230],
	magenta:	[255,0,255],
	mediumaquamarine:	[102,205,170],
	mediumblue:	[0,0,205],
	mediumorchid:	[186,85,211],
	mediumpurple:	[147,112,219],
	mediumseagreen:	[60,179,113],
	mediumslateblue:	[123,104,238],
	mediumspringgreen:	[0,250,154],
	mediumturquoise:	[72,209,204],
	mediumvioletred:	[199,21,133],
	midnightblue:	[25,25,112],
	mintcream:	[245,255,250],
	mistyrose:	[255,228,225],
	moccasin:	[255,228,181],
	navajowhite:	[255,222,173],
	oldlace:	[253,245,230],
	olivedrab:	[107,142,35],
	orange:	[255,165,0],
	orangered:	[255,69,0],
	orchid:	[218,112,214],
	palegoldenrod:	[238,232,170],
	palegreen:	[152,251,152],
	paleturquoise:	[175,238,238],
	palevioletred:	[219,112,147],
	papayawhip:	[255,239,213],
	peachpuff:	[255,218,185],
	peru:	[205,133,63],
	pink:	[255,192,203],
	plum:	[221,160,221],
	powderblue:	[176,224,230],
	rosybrown:	[188,143,143],
	royalblue:	[65,105,225],
	saddlebrown:	[139,69,19],
	salmon:	[250,128,114],
	sandybrown:	[244,164,96],
	seagreen:	[46,139,87],
	seashell:	[255,245,238],
	sienna:	[160,82,45],
	skyblue:	[135,206,235],
	slateblue:	[106,90,205],
	slategray:	[112,128,144],
	slategrey:	[112,128,144],
	snow:	[255,250,250],
	springgreen:	[0,255,127],
	steelblue:	[70,130,180],
	tan:	[210,180,140],
	thistle:	[216,191,216],
	tomato:	[255,99,71],
	transparent: [0, 0, 0, 0],
	turquoise:	[64,224,208],
	violet:	[238,130,238],
	wheat:	[245,222,179],
	whitesmoke:	[245,245,245],
	yellowgreen:	[154,205,50]
}, dojo.Color.named);

}

if(!dojo._hasResource["dijit.ColorPalette"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.ColorPalette"] = true;
dojo.provide("dijit.ColorPalette");







dojo.declare(
		"dijit.ColorPalette",
		[dijit._Widget, dijit._Templated],
{
	// summary
	//		Grid showing various colors, so the user can pick a certain color

	// defaultTimeout: Number
	//		number of milliseconds before a held key or button becomes typematic
	defaultTimeout: 500,

	// timeoutChangeRate: Number
	//		fraction of time used to change the typematic timer between events
	//		1.0 means that each typematic event fires at defaultTimeout intervals
	//		< 1.0 means that each typematic event fires at an increasing faster rate
	timeoutChangeRate: 0.90,

	// palette: String
	//		Size of grid, either "7x10" or "3x4".
	palette: "7x10",

	//_value: String
	//		The value of the selected color.
	value: null,

	//_currentFocus: Integer
	//		Index of the currently focused color.
	_currentFocus: 0,

	// _xDim: Integer
	//		This is the number of colors horizontally across.
	_xDim: null,

	// _yDim: Integer
	///		This is the number of colors vertically down.
	_yDim: null,

	// _palettes: Map
	// 		This represents the value of the colors.
	//		The first level is a hashmap of the different arrays available
	//		The next two dimensions represent the columns and rows of colors.
	_palettes: {

		"7x10":	[["white", "seashell", "cornsilk", "lemonchiffon","lightyellow", "palegreen", "paleturquoise", "lightcyan",	"lavender", "plum"],
				["lightgray", "pink", "bisque", "moccasin", "khaki", "lightgreen", "lightseagreen", "lightskyblue", "cornflowerblue", "violet"],
				["silver", "lightcoral", "sandybrown", "orange", "palegoldenrod", "chartreuse", "mediumturquoise", 	"skyblue", "mediumslateblue","orchid"],
				["gray", "red", "orangered", "darkorange", "yellow", "limegreen", 	"darkseagreen", "royalblue", "slateblue", "mediumorchid"],
				["dimgray", "crimson", 	"chocolate", "coral", "gold", "forestgreen", "seagreen", "blue", "blueviolet", "darkorchid"],
				["darkslategray","firebrick","saddlebrown", "sienna", "olive", "green", "darkcyan", "mediumblue","darkslateblue", "darkmagenta" ],
				["black", "darkred", "maroon", "brown", "darkolivegreen", "darkgreen", "midnightblue", "navy", "indigo", 	"purple"]],

		"3x4": [["white", "lime", "green", "blue"],
			["silver", "yellow", "fuchsia", "navy"],
			["gray", "red", "purple", "black"]]	

	},

	// _imagePaths: Map
	//		This is stores the path to the palette images
	_imagePaths: {
		"7x10": dojo.moduleUrl("dijit", "templates/colors7x10.png"),
		"3x4": dojo.moduleUrl("dijit", "templates/colors3x4.png")
	},

	// _paletteCoords: Map
	//		This is a map that is used to calculate the coordinates of the
	//		images that make up the palette.
	_paletteCoords: {
		"leftOffset": 4, "topOffset": 4,
		"cWidth": 20, "cHeight": 20
		
	},

	// templatePath: String
	//		Path to the template of this widget.
	templateString:"<div class=\"dijitInline dijitColorPalette\">\r\n\t<div class=\"dijitColorPaletteInner\" dojoAttachPoint=\"divNode\" waiRole=\"grid\" tabIndex=\"-1\">\r\n\t\t<img class=\"dijitColorPaletteUnder\" dojoAttachPoint=\"imageNode\" waiRole=\"presentation\">\r\n\t</div>\t\r\n</div>\r\n",

	// _paletteDims: Object
	//		Size of the supported palettes for alignment purposes.
	_paletteDims: {
		"7x10": {"width": "206px", "height": "145px"},
		"3x4": {"width": "86px", "height": "64px"}
	},


	postCreate: function(){
		// A name has to be given to the colorMap, this needs to be unique per Palette.
		dojo.mixin(this.divNode.style, this._paletteDims[this.palette]);
		this.imageNode.setAttribute("src", this._imagePaths[this.palette]);
		var choices = this._palettes[this.palette];	
		this.domNode.style.position = "relative";
		this._highlightNodes = [];	
		this.colorNames = dojo.i18n.getLocalization("dojo", "colors", this.lang);
		var url= dojo.moduleUrl("dijit", "templates/blank.gif");
		var colorObject = new dojo.Color(),
		    coords = this._paletteCoords;
		for(var row=0; row < choices.length; row++){
			for(var col=0; col < choices[row].length; col++) {
                var highlightNode = document.createElement("img");
                highlightNode.src = url;
                dojo.addClass(highlightNode, "dijitPaletteImg");
                var color = choices[row][col],
                        colorValue = colorObject.setColor(dojo.Color.named[color]);
                highlightNode.alt = this.colorNames[color];
                highlightNode.color = colorValue.toHex();
                var highlightStyle = highlightNode.style;
                highlightStyle.color = highlightStyle.backgroundColor = highlightNode.color;
                dojo.forEach(["Dijitclick", "MouseOut", "MouseOver", "Blur", "Focus"], function(handler) {
                    this.connect(highlightNode, "on" + handler.toLowerCase(), "_onColor" + handler);
                }, this);
                this.divNode.appendChild(highlightNode);
                highlightStyle.top = coords.topOffset + (row * coords.cHeight) + "px";
                highlightStyle.left = coords.leftOffset + (col * coords.cWidth) + "px";
                highlightNode.setAttribute("tabIndex", "-1");
                highlightNode.title = this.colorNames[color];
                dijit.setWaiRole(highlightNode, "gridcell");
                highlightNode.index = this._highlightNodes.length;
                this._highlightNodes.push(highlightNode);
            }
		}
		this._highlightNodes[this._currentFocus].tabIndex = 0;
		this._xDim = choices[0].length;
		this._yDim = choices.length;

		// Now set all events
		// The palette itself is navigated to with the tab key on the keyboard
		// Keyboard navigation within the Palette is with the arrow keys
		// Spacebar selects the color.
		// For the up key the index is changed by negative the x dimension.		

		var keyIncrementMap = {
			UP_ARROW: -this._xDim,
			// The down key the index is increase by the x dimension.	
			DOWN_ARROW: this._xDim,
			// Right and left move the index by 1.
			RIGHT_ARROW: 1,
			LEFT_ARROW: -1
		};
		for(var key in keyIncrementMap){
			this._connects.push(dijit.typematic.addKeyListener(this.domNode,
				{keyCode:dojo.keys[key], ctrlKey:false, altKey:false, shiftKey:false},
				this,
				function(){
					var increment = keyIncrementMap[key];
					return function(count){ this._navigateByKey(increment, count); };
				}(),
				this.timeoutChangeRate, this.defaultTimeout));
		}
	},

	focus: function(){
		// summary:
		//		Focus this ColorPalette.
		dijit.focus(this._highlightNodes[this._currentFocus]);
	},

	onChange: function(color){
		// summary:
		//		Callback when a color is selected.
		// color: String
		//		Hex value corresponding to color.
//		console.debug("Color selected is: "+color);
	},

	_onColorDijitclick: function(/*Event*/ evt){
		// summary:
		//		Handler for click, enter key & space key. Selects the color.
		// evt:
		//		The event.
		var target = evt.currentTarget;
		if (this._currentFocus != target.index){
			this._currentFocus = target.index;
			dijit.focus(target);
		}
		this._selectColor(target);
		dojo.stopEvent(evt);
	},

	_onColorMouseOut: function(/*Event*/ evt){
		// summary:
		//		Handler for onMouseOut. Removes highlight.
		// evt:
		//		The mouse event.
		dojo.removeClass(evt.currentTarget, "dijitPaletteImgHighlight");
	},

	_onColorMouseOver: function(/*Event*/ evt){
		// summary:
		//		Handler for onMouseOver. Highlights the color.
		// evt:
		//		The mouse event.
		var target = evt.currentTarget;
		target.tabIndex = 0;
		target.focus();
	},

	_onColorBlur: function(/*Event*/ evt){
		// summary:
		//		Handler for onBlur. Removes highlight and sets
		//		the first color as the palette's tab point.
		// evt:
		//		The blur event.
		dojo.removeClass(evt.currentTarget, "dijitPaletteImgHighlight");
		evt.currentTarget.tabIndex = -1;
		this._currentFocus = 0;
		this._highlightNodes[0].tabIndex = 0;
	},

	_onColorFocus: function(/*Event*/ evt){
		// summary:
		//		Handler for onFocus. Highlights the color.
		// evt:
		//		The focus event.
		if(this._currentFocus != evt.currentTarget.index){
			this._highlightNodes[this._currentFocus].tabIndex = -1;
		}
		this._currentFocus = evt.currentTarget.index;
		dojo.addClass(evt.currentTarget, "dijitPaletteImgHighlight");

	},

	_selectColor: function(selectNode){	
		// summary:
		// 		This selects a color. It triggers the onChange event
		// area:
		//		The area node that covers the color being selected.
		this.onChange(this.value = selectNode.color);
	},

	_navigateByKey: function(increment, typeCount){
		// summary:we
		// 	  	This is the callback for typematic.
		// 		It changes the focus and the highlighed color.
		// increment:
		// 		How much the key is navigated.
		// typeCount:
		//		How many times typematic has fired.

		// typecount == -1 means the key is released.
		if(typeCount == -1){ return; }

		var newFocusIndex = this._currentFocus + increment;
		if(newFocusIndex < this._highlightNodes.length && newFocusIndex > -1)
		{
			var focusNode = this._highlightNodes[newFocusIndex];
			focusNode.tabIndex = 0;
			focusNode.focus();
		}
	}
});

}

if(!dojo._hasResource["dijit._editor.plugins.TextColor"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.plugins.TextColor"] = true;
dojo.provide("dijit._editor.plugins.TextColor");




dojo.declare("dijit._editor.plugins.TextColor",
	dijit._editor._Plugin,
	{
		buttonClass: dijit.form.DropDownButton,

//TODO: set initial focus/selection state?

		constructor: function(){
			this.dropDown = new dijit.ColorPalette();
			dojo.connect(this.dropDown, "onChange", this, function(color){
				this.editor.execCommand(this.command, color);
			});
		}
	}
);

}

if(!dojo._hasResource["dijit._editor.plugins.FontChoice"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit._editor.plugins.FontChoice"] = true;
dojo.provide("dijit._editor.plugins.FontChoice");








dojo.declare("dijit._editor.plugins.FontChoice",
	dijit._editor._Plugin,
	{
		_uniqueId: 0,

		buttonClass: dijit.form.FilteringSelect,

		_initButton: function(){
			this.inherited("_initButton", arguments);

			//TODO: do we need nls for font names?  provide css font lists? or otherwise make this more configurable?
			var names = {
				fontName: ["serif", "sans-serif", "monospaced", "cursive", "fantasy"],
				fontSize: [1,2,3,4,5,6,7],
				formatBlock: ["p", "h1", "h2", "h3", "pre"] }[this.command];
			var strings = dojo.i18n.getLocalization("dijit._editor", "FontChoice");
			var items = dojo.map(names, function(x){ return { name: strings[x], value: x }; });
			items.push({name:"", value:""}); // FilteringSelect doesn't like unmatched blank strings
			this.button.store = new dojo.data.ItemFileReadStore(
				{ data: { identifier: "value",
					items: items }
				});
			this.button.setValue("");

			dojo.connect(this.button, "onChange", this, function(choice){
				this.editor.execCommand(this.command, choice);
			});
		},

		updateState: function(){
			this.inherited("updateState", arguments);
			var _e = this.editor;
			var _c = this.command;
			if(!_e || !_e.isLoaded || !_c.length){ return; }
			if(this.button){
				var value = _e.queryCommandValue(_c);
				this.button.setValue(value);
			}
		},

		setToolbar: function(){
			this.inherited("setToolbar", arguments);

			var forRef = this.button;
			if(!forRef.id){ forRef.id = "dijitEditorButton-"+this.command+(this._uniqueId++); } //TODO: is this necessary?  FilteringSelects always seem to have an id?
			var label = dojo.doc.createElement("label");
			label.setAttribute("for", forRef.id);
			var strings = dojo.i18n.getLocalization("dijit._editor", "FontChoice");
			label.appendChild(dojo.doc.createTextNode(strings[this.command]));
			dojo.place(label, this.button.domNode, "before");
		}
	}
);

}

({
	fontSize: "Size",
	fontName: "Font",
	formatBlock: "Format",

	serif: "serif",
	"sans-serif": "sans-serif",
	monospaced: "monospaced",
	cursive: "cursive",
	fantasy: "fantasy",

	p: "Paragraph",
	h1: "Heading",
	h2: "Subheading",
	h3: "Sub-subheading",
	pre: "Pre-formatted",

	1: "xx-small",
	2: "x-small",
	3: "small",
	4: "medium",
	5: "large",
	6: "x-large",
	7: "xx-large"
})

if(!dojo._hasResource["dijit.Tree"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.Tree"] = true;
dojo.provide("dijit.Tree");








dojo.declare(
	"dijit._TreeNode",
	[dijit._Widget, dijit._Templated, dijit._Container, dijit._Contained],
{
	// summary
	//		Single node within a tree

	// item: dojo.data.Item
	//		the dojo.data entry this tree represents
	item: null,	

	isTreeNode: true,

	// label: String
	//		Text of this tree node
	label: "",
	
	isExpandable: null, // show expando node
	
	isExpanded: false,

	// state: String
	//		dynamic loading-related stuff.
	//		When an empty folder node appears, it is "UNCHECKED" first,
	//		then after dojo.data query it becomes "LOADING" and, finally "LOADED"	
	state: "UNCHECKED",
	
	templateString:"<div class=\"dijitTreeNode dijitTreeExpandLeaf dijitTreeChildrenNo\" waiRole=\"presentation\"\r\n\t><span dojoAttachPoint=\"expandoNode\" class=\"dijitTreeExpando\" waiRole=\"presentation\"\r\n\t></span\r\n\t><span dojoAttachPoint=\"expandoNodeText\" class=\"dijitExpandoText\" waiRole=\"presentation\"\r\n\t></span\r\n\t>\r\n\t<div dojoAttachPoint=\"contentNode\" class=\"dijitTreeContent\" waiRole=\"presentation\">\r\n\t\t<div dojoAttachPoint=\"iconNode\" class=\"dijitInline dijitTreeIcon\" waiRole=\"presentation\"></div>\r\n\t\t<span dojoAttachPoint=\"labelNode\" class=\"dijitTreeLabel\" wairole=\"treeitem\" tabindex=\"-1\"></span>\r\n\t</div>\r\n</div>\r\n",		

	postCreate: function(){
		// set label, escaping special characters
		this.setLabelNode(this.label);

		// set expand icon for leaf 	
		this._setExpando();

		// set icon and label class based on item
		this._updateItemClasses(this.item);

		if(this.isExpandable){
			dijit.setWaiState(this.labelNode, "expanded", this.isExpanded);
		}
	},

	markProcessing: function(){
		// summary: visually denote that tree is loading data, etc.
		this.state = "LOADING";
		this._setExpando(true);	
	},

	unmarkProcessing: function(){
		// summary: clear markup from markProcessing() call
		this._setExpando(false);	
	},

	_updateItemClasses: function(item){
		// summary: set appropriate CSS classes for item (used to allow for item updates to change respective CSS)
		this.iconNode.className = "dijitInline dijitTreeIcon " + this.tree.getIconClass(item);
		this.labelNode.className = "dijitTreeLabel " + this.tree.getLabelClass(item);
	},
	
	_updateLayout: function(){
		// summary: set appropriate CSS classes for this.domNode
		var parent = this.getParent();
		if(parent && parent.isTree && parent._hideRoot){
			/* if we are hiding the root node then make every first level child look like a root node */
			dojo.addClass(this.domNode, "dijitTreeIsRoot");
		}else{
			dojo.toggleClass(this.domNode, "dijitTreeIsLast", !this.getNextSibling());
		}
	},

	_setExpando: function(/*Boolean*/ processing){
		// summary: set the right image for the expando node

		// apply the appropriate class to the expando node
		var styles = ["dijitTreeExpandoLoading", "dijitTreeExpandoOpened",
			"dijitTreeExpandoClosed", "dijitTreeExpandoLeaf"];
		var idx = processing ? 0 : (this.isExpandable ?	(this.isExpanded ? 1 : 2) : 3);
		dojo.forEach(styles,
			function(s){
				dojo.removeClass(this.expandoNode, s);
			}, this
		);
		dojo.addClass(this.expandoNode, styles[idx]);

		// provide a non-image based indicator for images-off mode
		this.expandoNodeText.innerHTML =
			processing ? "*" :
				(this.isExpandable ?
					(this.isExpanded ? "-" : "+") : "*");
	},	

	expand: function(){
		// summary: show my children
		if(this.isExpanded){ return; }
		// cancel in progress collapse operation
		if(this._wipeOut.status() == "playing"){
			this._wipeOut.stop();
		}

		this.isExpanded = true;
		dijit.setWaiState(this.labelNode, "expanded", "true");
		dijit.setWaiRole(this.containerNode, "group");

		this._setExpando();

		this._wipeIn.play();
	},

	collapse: function(){					
		if(!this.isExpanded){ return; }

		// cancel in progress expand operation
		if(this._wipeIn.status() == "playing"){
			this._wipeIn.stop();
		}

		this.isExpanded = false;
		dijit.setWaiState(this.labelNode, "expanded", "false");
		this._setExpando();

		this._wipeOut.play();
	},

	setLabelNode: function(label){
		this.labelNode.innerHTML="";
		this.labelNode.appendChild(document.createTextNode(label));
	},

	_setChildren: function(/* Object[] */ childrenArray){
		// summary:
		//		Sets the children of this node.
		//		Sets this.isExpandable based on whether or not there are children
		// 		Takes array of objects like: {label: ...} (_TreeNode options basically)
		//		See parameters of _TreeNode for details.

		this.destroyDescendants();

		this.state = "LOADED";
		var nodeMap= {};
		if(childrenArray && childrenArray.length > 0){
			this.isExpandable = true;
			if(!this.containerNode){ // maybe this node was unfolderized and still has container
				this.containerNode = this.tree.containerNodeTemplate.cloneNode(true);
				this.domNode.appendChild(this.containerNode);
			}

			// Create _TreeNode widget for each specified tree node
			dojo.forEach(childrenArray, function(childParams){
				var child = new dijit._TreeNode(dojo.mixin({
					tree: this.tree,
					label: this.tree.getLabel(childParams.item)
				}, childParams));
				this.addChild(child);
				var identity = this.tree.store.getIdentity(childParams.item);
				nodeMap[identity] = child;
				if(this.tree.persist){
					if(this.tree._openedItemIds[identity]){
						this.tree._expandNode(child);
					}
				}
			}, this);

			// note that updateLayout() needs to be called on each child after
			// _all_ the children exist
			dojo.forEach(this.getChildren(), function(child, idx){
				child._updateLayout();
			});
		}else{
			this.isExpandable=false;
		}

		if(this._setExpando){
			// change expando to/form dot or + icon, as appropriate
			this._setExpando(false);
		}

		if(this.isTree && this._hideRoot){
			// put first child in tab index if one exists.
			var fc = this.getChildren()[0];
			var tabnode = fc ? fc.labelNode : this.domNode;
			tabnode.setAttribute("tabIndex", "0");
		}

		// create animations for showing/hiding the children (if children exist)
		if(this.containerNode && !this._wipeIn){
			this._wipeIn = dojo.fx.wipeIn({node: this.containerNode, duration: 150});
			this._wipeOut = dojo.fx.wipeOut({node: this.containerNode, duration: 150});
		}

		return nodeMap;
	},

	_addChildren: function(/* object[] */ childrenArray){
		// summary:
		//		adds the children to this node.
		// 		Takes array of objects like: {label: ...}  (_TreeNode options basically)

		//		See parameters of _TreeNode for details.
		var nodeMap = {};
		if(childrenArray && childrenArray.length > 0){
			dojo.forEach(childrenArray, function(childParams){
				var child = new dijit._TreeNode(
					dojo.mixin({
						tree: this.tree,
						label: this.tree.getLabel(childParams.item)
					}, childParams)
				);
				this.addChild(child);
				nodeMap[this.tree.store.getIdentity(childParams.item)] = child;
			}, this);

			dojo.forEach(this.getChildren(), function(child, idx){
				child._updateLayout();
			});
		}

		return nodeMap;
	},

	deleteNode: function(/* treeNode */ node){
		node.destroy();

		var children = this.getChildren();		
		if(children.length == 0){
			this.isExpandable = false;
			this.collapse();
		}

		dojo.forEach(children, function(child){
				child._updateLayout();
		});
	},

	makeExpandable: function(){
		//summary
		//		if this node wasn't already showing the expando node,
		//		turn it into one and call _setExpando()
		this.isExpandable = true;
		this._setExpando(false);
	}
});

dojo.declare(
	"dijit.Tree",
	dijit._TreeNode,
{
	// summary
	//	This widget displays hierarchical data from a store.  A query is specified
	//	to get the "top level children" from a data store, and then those items are
	//	queried for their children and so on (but lazily, as the user clicks the expand node).
	//
	//	Thus in the default mode of operation this widget is technically a forest, not a tree,
	//	in that there can be multiple "top level children".  However, if you specify label,
	//	then a special top level node (not corresponding to any item in the datastore) is
	//	created, to father all the top level children.

	// store: String||dojo.data.Store
	//	The store to get data to display in the tree
	store: null,

	// query: String
	//	query to get top level node(s) of tree (ex: {type:'continent'})
	query: null,

	// childrenAttr: String
	//		one ore more attributes that holds children of a tree node
	childrenAttr: ["children"],

	templateString:"<div class=\"dijitTreeContainer\" style=\"\" waiRole=\"tree\"\r\n\tdojoAttachEvent=\"onclick:_onClick,onkeypress:_onKeyPress\">\r\n\t<div class=\"dijitTreeNode  dijitTreeIsRoot dijitTreeExpandLeaf dijitTreeChildrenNo\" waiRole=\"presentation\"\r\n\t\tdojoAttachPoint=\"rowNode\"\r\n\t\t><span dojoAttachPoint=\"expandoNode\" class=\"dijitTreeExpando\" waiRole=\"presentation\"\r\n\t\t></span\r\n\t\t><span dojoAttachPoint=\"expandoNodeText\" class=\"dijitExpandoText\" waiRole=\"presentation\"\r\n\t\t></span\r\n\t\t>\r\n\t\t<div dojoAttachPoint=\"contentNode\" class=\"dijitTreeContent\" waiRole=\"presentation\">\r\n\t\t\t<div dojoAttachPoint=\"iconNode\" class=\"dijitInline dijitTreeIcon\" waiRole=\"presentation\"></div>\r\n\t\t\t<span dojoAttachPoint=\"labelNode\" class=\"dijitTreeLabel\" wairole=\"treeitem\" tabindex=\"0\"></span>\r\n\t\t</div>\r\n\t</div>\r\n</div>\r\n",		

	isExpandable: true,

	isTree: true,

	// persist: Boolean
	//	enables/disables use of cookies for state saving.
	persist: true,
	
	// dndController: String
	//	class name to use as as the dnd controller
	dndController: null,

	//parameters to pull off of the tree and pass on to the dndController as its params
	dndParams: ["onDndDrop","itemCreator","onDndCancel","checkAcceptance", "checkItemAcceptance"],

	//declare the above items so they can be pulled from the tree's markup
	onDndDrop:null,
	itemCreator:null,
	onDndCancel:null,
	checkAcceptance:null,	
	checkItemAcceptance:null,

	_publish: function(/*String*/ topicName, /*Object*/ message){
		// summary:
		//		Publish a message for this widget/topic
		dojo.publish(this.id, [dojo.mixin({tree: this, event: topicName}, message||{})]);
	},

	postMixInProperties: function(){
		this.tree = this;
		this.lastFocused = this.labelNode;

		this._itemNodeMap={};

		this._hideRoot = !this.label;

		if(!this.store.getFeatures()['dojo.data.api.Identity']){
			throw new Error("dijit.tree requires access to a store supporting the dojo.data Identity api");			
		}

		if(!this.cookieName){
			this.cookieName = this.id + "SaveStateCookie";
		}

		// if the store supports Notification, subscribe to the notification events
		if(this.store.getFeatures()['dojo.data.api.Notification']){
			this.connect(this.store, "onNew", "_onNewItem");
			this.connect(this.store, "onDelete", "_onDeleteItem");
			this.connect(this.store, "onSet", "_onSetItem");
		}
	},

	postCreate: function(){
		// load in which nodes should be opened automatically
		if(this.persist){
			var cookie = dojo.cookie(this.cookieName);
			this._openedItemIds = {};
			if(cookie){
				dojo.forEach(cookie.split(','), function(item){
					this._openedItemIds[item] = true;
				}, this);
			}
		}
		
		// make template for container node (we will clone this and insert it into
		// any nodes that have children)
		var div = document.createElement('div');
		div.style.display = 'none';
		div.className = "dijitTreeContainer";	
		dijit.setWaiRole(div, "presentation");
		this.containerNodeTemplate = div;

		if(this._hideRoot){
			this.rowNode.style.display="none";
		}

		this.inherited("postCreate", arguments);

		// load top level children
		this._expandNode(this);

		if(this.dndController){
			if(dojo.isString(this.dndController)){
				this.dndController= dojo.getObject(this.dndController);
			}	
			var params={};
			for (var i=0; i<this.dndParams.length;i++){
				if(this[this.dndParams[i]]){
					params[this.dndParams[i]]=this[this.dndParams[i]];
				}
			}
			this.dndController= new this.dndController(this, params);
		}

		this.connect(this.domNode,
			dojo.isIE ? "onactivate" : "onfocus",
			"_onTreeFocus");
	},

	////////////// Data store related functions //////////////////////

	mayHaveChildren: function(/*dojo.data.Item*/ item){
		// summary
		//		User overridable function to tell if an item has or may have children.
		//		Controls whether or not +/- expando icon is shown.
		//		(For efficiency reasons we may not want to check if an element has
		//		children until user clicks the expando node)

		return dojo.some(this.childrenAttr, function(attr){
			return this.store.hasAttribute(item, attr);
		}, this);
	},

	getItemChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
		// summary
		// 		User overridable function that return array of child items of given parent item,
		//		or if parentItem==null then return top items in tree
		var store = this.store;
		if(parentItem == null){
			// get top level nodes
			store.fetch({ query: this.query, onComplete: onComplete});
		}else{
			// get children of specified node
			var childItems = [];
			for (var i=0; i<this.childrenAttr.length; i++){	
				childItems= childItems.concat(store.getValues(parentItem, this.childrenAttr[i]));
			}
			// count how many items need to be loaded
			var _waitCount = 0;
			dojo.forEach(childItems, function(item){ if(!store.isItemLoaded(item)){ _waitCount++; } });

			if(_waitCount == 0){
				// all items are already loaded.  proceed..
				onComplete(childItems);
			}else{
				// still waiting for some or all of the items to load
				function onItem(item){
					if(--_waitCount == 0){
						// all nodes have been loaded, send them to the tree
						onComplete(childItems);
					}
				}
				dojo.forEach(childItems, function(item){
					if(!store.isItemLoaded(item)){
						store.loadItem({item: item, onItem: onItem});
					}
				});
			}
		}
	},

	getItemParentIdentity: function(/*dojo.data.Item*/ item, /*Object*/ parentInfo){
		// summary
		//		User overridable function, to return id of parent (or null if top level).
		//		It's called with args from dojo.store.onNew
		return this.store.getIdentity(parentInfo.item);		// String
	},

	getLabel: function(/*dojo.data.Item*/ item){
		// summary: user overridable function to get the label for a tree node (given the item)
		return this.store.getLabel(item);	// String
	},

	getIconClass: function(/*dojo.data.Item*/ item){
		// summary: user overridable function to return CSS class name to display icon
	},

	getLabelClass: function(/*dojo.data.Item*/ item){
		// summary: user overridable function to return CSS class name to display label
	},

	_onLoadAllItems: function(/*_TreeNode*/ node, /*dojo.data.Item[]*/ items){
		// sumary: callback when all the children of a given node have been loaded
		var childParams=dojo.map(items, function(item){
			return {
				item: item,
				isExpandable: this.mayHaveChildren(item)
			};
		}, this);

		dojo.mixin(this._itemNodeMap,node._setChildren(childParams));

		this._expandNode(node);
	},

	/////////// Keyboard and Mouse handlers ////////////////////

	_onKeyPress: function(/*Event*/ e){
		// summary: translates keypress events into commands for the controller
		if(e.altKey){ return; }
		var treeNode = dijit.getEnclosingWidget(e.target);
		if(!treeNode){ return; }

		// Note: On IE e.keyCode is not 0 for printables so check e.charCode.
		// In dojo charCode is universally 0 for non-printables.
		if(e.charCode){  // handle printables (letter navigation)
			// Check for key navigation.
			var navKey = e.charCode;
			if(!e.altKey && !e.ctrlKey && !e.shiftKey && !e.metaKey){
				navKey = (String.fromCharCode(navKey)).toLowerCase();
				this._onLetterKeyNav( { node: treeNode, key: navKey } );
				dojo.stopEvent(e);
			}
		}else{  // handle non-printables (arrow keys)
			var map = this._keyHandlerMap;
			if(!map){
				// setup table mapping keys to events
				map = {};
				map[dojo.keys.ENTER]="_onEnterKey";
				map[dojo.keys.LEFT_ARROW]="_onLeftArrow";
				map[dojo.keys.RIGHT_ARROW]="_onRightArrow";
				map[dojo.keys.UP_ARROW]="_onUpArrow";
				map[dojo.keys.DOWN_ARROW]="_onDownArrow";
				map[dojo.keys.HOME]="_onHomeKey";
				map[dojo.keys.END]="_onEndKey";
				this._keyHandlerMap = map;
			}
			if(this._keyHandlerMap[e.keyCode]){
				this[this._keyHandlerMap[e.keyCode]]( { node: treeNode, item: treeNode.item } );	
				dojo.stopEvent(e);
			}
		}
	},

	_onEnterKey: function(/*Object*/ message){
		this._publish("execute", { item: message.item, node: message.node} );
		this.onClick(message.item, message.node);
	},

	_onDownArrow: function(/*Object*/ message){
		// summary: down arrow pressed; get next visible node, set focus there
		var returnNode = this._navToNextNode(message.node);
		if(returnNode && returnNode.isTreeNode){
			returnNode.tree.focusNode(returnNode);
			return returnNode;
		}	
	},

	_onUpArrow: function(/*Object*/ message){
		// summary: up arrow pressed; move to previous visible node

		var nodeWidget = message.node;
		var returnWidget = nodeWidget;

		// if younger siblings		
		var previousSibling = nodeWidget.getPreviousSibling();
		if(previousSibling){
			nodeWidget = previousSibling;
			// if the previous nodeWidget is expanded, dive in deep
			while(nodeWidget.isExpandable && nodeWidget.isExpanded && nodeWidget.hasChildren()){
				returnWidget = nodeWidget;
				// move to the last child
				var children = nodeWidget.getChildren();
				nodeWidget = children[children.length-1];
			}
		}else{
			// if this is the first child, return the parent
			// unless the parent is the root of a tree with a hidden root
			var parent = nodeWidget.getParent();
			if(!(this._hideRoot && parent === this)){
				nodeWidget = parent;
			}
		}

		if(nodeWidget && nodeWidget.isTreeNode){
			returnWidget = nodeWidget;
		}

		if(returnWidget && returnWidget.isTreeNode){
			returnWidget.tree.focusNode(returnWidget);
			return returnWidget;
		}
	},

	_onRightArrow: function(/*Object*/ message){
		// summary: right arrow pressed; go to child node
		var nodeWidget = message.node;
		var returnWidget = nodeWidget;

		// if not expanded, expand, else move to 1st child
		if(nodeWidget.isExpandable && !nodeWidget.isExpanded){
			this._expandNode(nodeWidget);
		}else if(nodeWidget.hasChildren()){
			nodeWidget = nodeWidget.getChildren()[0];
		}

		if(nodeWidget && nodeWidget.isTreeNode){
			returnWidget = nodeWidget;
		}

		if(returnWidget && returnWidget.isTreeNode){
			returnWidget.tree.focusNode(returnWidget);
			return returnWidget;
		}
	},

	_onLeftArrow: function(/*Object*/ message){
		// summary: left arrow pressed; go to parent

		var node = message.node;
		var returnWidget = node;

		// if not collapsed, collapse, else move to parent
		if(node.isExpandable && node.isExpanded){
			this._collapseNode(node);
		}else{
			node = node.getParent();
		}
		if(node && node.isTreeNode){
			returnWidget = node;
		}

		if(returnWidget && returnWidget.isTreeNode){
			returnWidget.tree.focusNode(returnWidget);
			return returnWidget;
		}
	},

	_onHomeKey: function(){
		// summary: home pressed; get first visible node, set focus there
		var returnNode = this._navToRootOrFirstNode();
		if(returnNode){
			returnNode.tree.focusNode(returnNode);
			return returnNode;
		}
	},

	_onEndKey: function(/*Object*/ message){
		// summary: end pressed; go to last visible node

		var returnWidget = message.node.tree;

		var lastChild = returnWidget;
		while(lastChild.isExpanded){
			var c = lastChild.getChildren();
			lastChild = c[c.length - 1];
			if(lastChild.isTreeNode){
				returnWidget = lastChild;
			}
		}

		if(returnWidget && returnWidget.isTreeNode){
			returnWidget.tree.focusNode(returnWidget);
			return returnWidget;
		}
	},

	_onLetterKeyNav: function(message){
		// summary: letter key pressed; search for node starting with first char = key
		var node = startNode = message.node;
		var key = message.key;
		do{
			node = this._navToNextNode(node);
			//check for last node, jump to first node if necessary
			if(!node){
				node = this._navToRootOrFirstNode();
			}
		}while(node !== startNode && (node.label.charAt(0).toLowerCase() != key));
		if(node && node.isTreeNode){
			// no need to set focus if back where we started
			if(node !== startNode){
				node.tree.focusNode(node);
			}
			return node;
		}
	},

	_onClick: function(/*Event*/ e){
		// summary: translates click events into commands for the controller to process
		var domElement = e.target;

		// find node
		var nodeWidget = dijit.getEnclosingWidget(domElement);	
		if(!nodeWidget || !nodeWidget.isTreeNode){
			return;
		}

		if(domElement == nodeWidget.expandoNode ||
			 domElement == nodeWidget.expandoNodeText){
			// expando node was clicked
			if(nodeWidget.isExpandable){
				this._onExpandoClick({node:nodeWidget});
			}
		}else{
			this._publish("execute", { item: nodeWidget.item, node: nodeWidget} );
			this.onClick(nodeWidget.item, nodeWidget);
			this.focusNode(nodeWidget);
		}
		dojo.stopEvent(e);
	},

	_onExpandoClick: function(/*Object*/ message){
		// summary: user clicked the +/- icon; expand or collapse my children.
		var node = message.node;
		if(node.isExpanded){
			this._collapseNode(node);
		}else{
			this._expandNode(node);
		}
	},

	onClick: function(/* dojo.data */ item, /*TreeNode*/ node){
		// summary: user overridable function for executing a tree item
	},

	_navToNextNode: function(node){
		// summary: get next visible node
		var returnNode;
		// if this is an expanded node, get the first child
		if(node.isExpandable && node.isExpanded && node.hasChildren()){
			returnNode = node.getChildren()[0];			
		}else{
			// find a parent node with a sibling
			while(node && node.isTreeNode){
				returnNode = node.getNextSibling();
				if(returnNode){
					break;
				}
				node = node.getParent();
			}	
		}
		return returnNode;
	},

	_navToRootOrFirstNode: function(){
		// summary: get first visible node
		if(!this._hideRoot){
			return this;
		}else{
			var returnNode = this.getChildren()[0];
			if(returnNode && returnNode.isTreeNode){
				return returnNode;
			}
		}
	},

	_collapseNode: function(/*_TreeNode*/ node){
		// summary: called when the user has requested to collapse the node

		if(node.isExpandable){
			if(node.state == "LOADING"){
				// ignore clicks while we are in the process of loading data
				return;
			}
			if(this.lastFocused){
				// are we collapsing a descendant with focus?
				if(dojo.isDescendant(this.lastFocused.domNode, node.domNode)){
					this.focusNode(node);
				}else{
					// clicking the expando node might have erased focus from
					// the current item; restore it
					this.focusNode(this.lastFocused);
				}
			}
			node.collapse();
			if(this.persist && node.item){
				delete this._openedItemIds[this.store.getIdentity(node.item)];
				this._saveState();
			}
		}
	},

	_expandNode: function(/*_TreeNode*/ node){
		// summary: called when the user has requested to expand the node

		// clicking the expando node might have erased focus from the current item; restore it
		var t = node.tree;
		if(t.lastFocused){ t.focusNode(t.lastFocused); }

		if(!node.isExpandable){
			return;
		}

		var store = this.store;
		var getValue = this.store.getValue;

		switch(node.state){
			case "LOADING":
				// ignore clicks while we are in the process of loading data
				return;

			case "UNCHECKED":
				// need to load all the children, and then expand
				node.markProcessing();
				var _this = this;
				var onComplete = function(childItems){
					node.unmarkProcessing();
					_this._onLoadAllItems(node, childItems);
				};
				this.getItemChildren(node.item, onComplete);
				break;

			default:
				// data is already loaded; just proceed
				if(node.expand){	// top level Tree doesn't have expand() method
					node.expand();
					if(this.persist && node.item){
						this._openedItemIds[this.store.getIdentity(node.item)] = true;
						this._saveState();
					}
				}
				break;
		}
	},

	////////////////// Miscellaneous functions ////////////////

	blurNode: function(){
		// summary
		//	Removes focus from the currently focused node (which must be visible).
		//	Usually not called directly (just call focusNode() on another node instead)
		var node = this.lastFocused;
		if(!node){ return; }
		var labelNode = node.labelNode;
		dojo.removeClass(labelNode, "dijitTreeLabelFocused");
		labelNode.setAttribute("tabIndex", "-1");
		this.lastFocused = null;
	},

	focusNode: function(/* _tree.Node */ node){
		// summary
		//	Focus on the specified node (which must be visible)

		// set focus so that the label will be voiced using screen readers
		node.labelNode.focus();
	},

	_onBlur: function(){
		// summary:
		// 		We've moved away from the whole tree.  The currently "focused" node
		//		(see focusNode above) should remain as the lastFocused node so we can
		//		tab back into the tree.  Just change CSS to get rid of the dotted border
		//		until that time
		if(this.lastFocused){
			var labelNode = this.lastFocused.labelNode;
			dojo.removeClass(labelNode, "dijitTreeLabelFocused");	
		}
	},

	_onTreeFocus: function(evt){
		var node = dijit.getEnclosingWidget(evt.target);
		if(node != this.lastFocused){
			this.blurNode();
		}
		var labelNode = node.labelNode;
		// set tabIndex so that the tab key can find this node
		labelNode.setAttribute("tabIndex", "0");
		dojo.addClass(labelNode, "dijitTreeLabelFocused");
		this.lastFocused = node;
	},

	//////////////// Events from data store //////////////////////////


	_onNewItem: function(/*Object*/ item, parentInfo){
		//summary: callback when new item has been added to the store.

		var loadNewItem;	// should new item be displayed in tree?

		if(parentInfo){
			var parent = this._itemNodeMap[this.getItemParentIdentity(item, parentInfo)];
			
			// If new item's parent item not in tree view yet, can safely ignore.
			// Also, if a query of specified parent wouldn't return this item, then ignore.
			if(!parent ||
				dojo.indexOf(this.childrenAttr, parentInfo.attribute) == -1){
				return;
			}
		}

		var childParams = {
			item: item,
			isExpandable: this.mayHaveChildren(item)
		};
		if(parent){
			if(!parent.isExpandable){
				parent.makeExpandable();
			}
			if(parent.state=="LOADED" || parent.isExpanded){
				var childrenMap=parent._addChildren([childParams]);
			}
		}else{
			// top level node
			var childrenMap=this._addChildren([childParams]);		
		}

		if(childrenMap){
			dojo.mixin(this._itemNodeMap, childrenMap);
			//this._itemNodeMap[this.store.getIdentity(item)]=child;
		}
	},
	
	_onDeleteItem: function(/*Object*/ item){
		//summary: delete event from the store
		//since the object has just been deleted, we need to
		//use the name directly
		var identity = this.store.getIdentity(item);
		var node = this._itemNodeMap[identity];

		if(node){
			var parent = node.getParent();
			parent.deleteNode(node);
			this._itemNodeMap[identity]=null;
		}
	},

	_onSetItem: function(/*Object*/ item){
		//summary: set data event  on an item in the store
		var identity = this.store.getIdentity(item);
		node = this._itemNodeMap[identity];

		if(node){
			node.setLabelNode(this.getLabel(item));
			node._updateItemClasses(item);
		}
	},
	
	_saveState: function(){
		//summary: create and save a cookie with the currently expanded nodes identifiers
		if(!this.persist){
			return;
		}
		var ary = [];
		for(var id in this._openedItemIds){
			ary.push(id);
		}
		dojo.cookie(this.cookieName, ary.join(","));
	}
});

}

if(!dojo._hasResource["dijit.TitlePane"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dijit.TitlePane"] = true;
dojo.provide("dijit.TitlePane");






dojo.declare(
	"dijit.TitlePane",
	[dijit.layout.ContentPane, dijit._Templated],
{
	// summary
	//		A pane with a title on top, that can be opened or collapsed.
	//
	// title: String
	//		Title of the pane
	title: "",

	// open: Boolean
	//		Whether pane is opened or closed.
	open: true,

	// duration: Integer
	//		Time in milliseconds to fade in/fade out
	duration: 250,

	// baseClass: String
	//	the root className to use for the various states of this widget
	baseClass: "dijitTitlePane",

	templateString:"<div class=\"dijitTitlePane\">\r\n\t<div dojoAttachEvent=\"onclick:toggle,onkeypress: _onTitleKey,onfocus:_handleFocus,onblur:_handleFocus\" tabindex=\"0\"\r\n\t\t\twaiRole=\"button\" class=\"dijitTitlePaneTitle\" dojoAttachPoint=\"focusNode\">\r\n\t\t<div dojoAttachPoint=\"arrowNode\" class=\"dijitInline dijitArrowNode\"><span dojoAttachPoint=\"arrowNodeInner\" class=\"dijitArrowNodeInner\"></span></div>\r\n\t\t<div dojoAttachPoint=\"titleNode\" class=\"dijitTitlePaneTextNode\"></div>\r\n\t</div>\r\n\t<div class=\"dijitTitlePaneContentOuter\" dojoAttachPoint=\"hideNode\">\r\n\t\t<div class=\"dijitReset\" dojoAttachPoint=\"wipeNode\">\r\n\t\t\t<div class=\"dijitTitlePaneContentInner\" dojoAttachPoint=\"containerNode\" waiRole=\"region\" tabindex=\"-1\">\r\n\t\t\t\t<!-- nested divs because wipeIn()/wipeOut() doesn't work right on node w/padding etc.  Put padding on inner div. -->\r\n\t\t\t</div>\r\n\t\t</div>\r\n\t</div>\r\n</div>\r\n",

	postCreate: function(){
		this.setTitle(this.title);
		if(!this.open){
			this.hideNode.style.display = this.wipeNode.style.display = "none";
		}
		this._setCss();
		dojo.setSelectable(this.titleNode, false);
		this.inherited("postCreate",arguments);
		dijit.setWaiState(this.containerNode, "labelledby", this.titleNode.id);
		dijit.setWaiState(this.focusNode, "haspopup", "true");

		// setup open/close animations
		var hideNode = this.hideNode, wipeNode = this.wipeNode;
		this._wipeIn = dojo.fx.wipeIn({
			node: this.wipeNode,
			duration: this.duration,
			beforeBegin: function(){
				hideNode.style.display="";
			}
		});
		this._wipeOut = dojo.fx.wipeOut({
			node: this.wipeNode,
			duration: this.duration,
			onEnd: function(){
				hideNode.style.display="none";
			}
		});
	},

	setContent: function(content){
		// summary
		// 		Typically called when an href is loaded.  Our job is to make the animation smooth
		if(this._wipeOut.status() == "playing"){
			// we are currently *closing* the pane, so just let that continue
			this.inherited("setContent",arguments);
		}else{
			if(this._wipeIn.status() == "playing"){
				this._wipeIn.stop();
			}

			// freeze container at current height so that adding new content doesn't make it jump
			dojo.marginBox(this.wipeNode, {h: dojo.marginBox(this.wipeNode).h});

			// add the new content (erasing the old content, if any)
			this.inherited("setContent",arguments);

			// call _wipeIn.play() to animate from current height to new height
			this._wipeIn.play();
		}
	},

	toggle: function(){
		// summary: switches between opened and closed state
		dojo.forEach([this._wipeIn, this._wipeOut], function(animation){
			if(animation.status() == "playing"){
				animation.stop();
			}
		});

		this[this.open ? "_wipeOut" : "_wipeIn"].play();
		this.open =! this.open;

		// load content (if this is the first time we are opening the TitlePane
		// and content is specified as an href, or we have setHref when hidden)
		this._loadCheck();

		this._setCss();
	},

	_setCss: function(){
		// summary: set the open/close css state for the TitlePane
		var classes = ["dijitClosed", "dijitOpen"];
		var boolIndex = this.open;
		dojo.removeClass(this.focusNode, classes[!boolIndex+0]);
		this.focusNode.className += " " + classes[boolIndex+0];

		// provide a character based indicator for images-off mode
		this.arrowNodeInner.innerHTML = this.open ? "-" : "+";
	},

	_onTitleKey: function(/*Event*/ e){
		// summary: callback when user hits a key
		if(e.keyCode == dojo.keys.ENTER || e.charCode == dojo.keys.SPACE){
			this.toggle();
		}
		else if(e.keyCode == dojo.keys.DOWN_ARROW){
			if(this.open){
				this.containerNode.focus();
				e.preventDefault();
			}
	 	}
	},
	
	_handleFocus: function(/*Event*/ e){
		// summary: handle blur and focus for this widget
		
		// add/removeClass is safe to call without hasClass in this case
		dojo[(e.type=="focus" ? "addClass" : "removeClass")](this.focusNode,this.baseClass+"Focused");
	},

	setTitle: function(/*String*/ title){
		// summary: sets the text of the title
		this.titleNode.innerHTML=title;
	}
});

}


dojo.i18n._preloadLocalizations("dijit.nls.dijit-atg-apps", ["es-es", "es", "hu", "it-it", "de", "pt-br", "pl", "fr-fr", "zh-cn", "pt", "en-us", "zh", "ru", "xx", "fr", "zh-tw", "it", "cs", "en-gb", "de-de", "ja-jp", "ko-kr", "ko", "en", "ROOT", "ja"]);
