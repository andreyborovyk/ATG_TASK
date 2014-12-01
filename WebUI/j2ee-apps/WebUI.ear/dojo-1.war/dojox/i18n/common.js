if(!dojo._hasResource["dojox.i18n.common"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.i18n.common"] = true;
/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojox.i18n.common");
dojo.require("dojo.i18n");

dojo.i18n.isLTR = function(/*String?*/locale){
//	summary:
//		Is the language read left-to-right?  Most exceptions are for middle eastern languages.
//
//	locale: a string representing the locale.  By default, the locale defined by the
//		host environment: dojo.locale

	var lang = dojo.i18n.normalizeLocale(locale).split('-')[0];
	var RTL = {ar:true,fa:true,he:true,ur:true,yi:true};
	return !RTL[lang]; // Boolean
};

}
