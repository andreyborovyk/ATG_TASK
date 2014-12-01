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

if(!dojo._hasResource["dojox.collections._base"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.collections._base"] = true;
dojo.provide("dojox.collections._base");

dojox.collections.DictionaryEntry=function(/* string */k, /* object */v){
	//	summary
	//	return an object of type dojox.collections.DictionaryEntry
	this.key=k;
	this.value=v;
	this.valueOf=function(){ 
		return this.value; 	//	object
	};
	this.toString=function(){ 
		return String(this.value);	//	string 
	};
}

/*	Iterators
 *	The collections.Iterators (Iterator and DictionaryIterator) are built to
 *	work with the Collections included in this module.  However, they *can*
 *	be used with arrays and objects, respectively, should one choose to do so.
 */
dojox.collections.Iterator=function(/* array */arr){
	//	summary
	//	return an object of type dojox.collections.Iterator
	var a=arr;
	var position=0;
	this.element=a[position]||null;
	this.atEnd=function(){
		//	summary
		//	Test to see if the internal cursor has reached the end of the internal collection.
		return (position>=a.length);	//	bool
	};
	this.get=function(){
		//	summary
		//	Get the next member in the collection.
		if(this.atEnd()){
			return null;		//	object
		}
		this.element=a[position++];
		return this.element;	//	object
	};
	this.map=function(/* function */fn, /* object? */scope){
		//	summary
		//	Functional iteration with optional scope.
		return dojo.map(a, fn, scope);
	};
	this.reset=function(){
		//	summary
		//	reset the internal cursor.
		position=0;
		this.element=a[position];
	};
}

/*	Notes:
 *	The DictionaryIterator no longer supports a key and value property;
 *	the reality is that you can use this to iterate over a JS object
 *	being used as a hashtable.
 */
dojox.collections.DictionaryIterator=function(/* object */obj){
	//	summary
	//	return an object of type dojox.collections.DictionaryIterator
	var a=[];	//	Create an indexing array
	var testObject={};
	for(var p in obj){
		if(!testObject[p]){
			a.push(obj[p]);	//	fill it up
		}
	}
	var position=0;
	this.element=a[position]||null;
	this.atEnd=function(){
		//	summary
		//	Test to see if the internal cursor has reached the end of the internal collection.
		return (position>=a.length);	//	bool
	};
	this.get=function(){
		//	summary
		//	Get the next member in the collection.
		if(this.atEnd()){
			return null;		//	object
		}
		this.element=a[position++];
		return this.element;	//	object
	};
	this.map=function(/* function */fn, /* object? */scope){
		//	summary
		//	Functional iteration with optional scope.
		return dojo.map(a, fn, scope);
	};
	this.reset=function() { 
		//	summary
		//	reset the internal cursor.
		position=0; 
		this.element=a[position];
	};
};

}

if(!dojo._hasResource["dojox.collections.SortedList"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.collections.SortedList"] = true;
dojo.provide("dojox.collections.SortedList");


dojox.collections.SortedList=function(/* object? */ dictionary){
	//	summary
	//	creates a collection that acts like a dictionary but is also internally sorted.
	//	Note that the act of adding any elements forces an internal resort, making this object potentially slow.
	var _this=this;
	var items={};
	var q=[];
	var sorter=function(a,b){
		if (a.key > b.key) return 1;
		if (a.key < b.key) return -1;
		return 0;
	};
	var build=function(){
		q=[];
		var e=_this.getIterator();
		while (!e.atEnd()){
			q.push(e.get());
		}
		q.sort(sorter);
	};
	var testObject={};

	this.count=q.length;
	this.add=function(/* string */ k,/* object */v){
		//	summary
		//	add the passed value to the dictionary at location k
		if (!items[k]) {
			items[k]=new dojox.collections.DictionaryEntry(k,v);
			this.count=q.push(items[k]);
			q.sort(sorter);
		}
	};
	this.clear=function(){
		//	summary
		//	clear the internal collections
		items={};
		q=[];
		this.count=q.length;
	};
	this.clone=function(){
		//	summary
		//	create a clone of this sorted list
		return new dojox.collections.SortedList(this);	//	dojox.collections.SortedList
	};
	this.contains=this.containsKey=function(/* string */ k){
		//	summary
		//	Check to see if the list has a location k
		if(testObject[k]){
			return false;			//	bool
		}
		return (items[k]!=null);	//	bool
	};
	this.containsValue=function(/* object */ o){
		//	summary
		//	Check to see if this list contains the passed object
		var e=this.getIterator();
		while (!e.atEnd()){
			var item=e.get();
			if(item.value==o){ 
				return true;	//	bool
			}
		}
		return false;	//	bool
	};
	this.copyTo=function(/* array */ arr, /* int */ i){
		//	summary
		//	copy the contents of the list into array arr at index i
		var e=this.getIterator();
		var idx=i;
		while(!e.atEnd()){
			arr.splice(idx,0,e.get());
			idx++;
		}
	};
	this.entry=function(/* string */ k){
		//	summary
		//	return the object at location k
		return items[k];	//	dojox.collections.DictionaryEntry
	};
	this.forEach=function(/* function */ fn, /* object? */ scope){
		//	summary
		//	functional iterator, following the mozilla spec.
		dojo.forEach(q, fn, scope);
	};
	this.getByIndex=function(/* int */ i){
		//	summary
		//	return the item at index i
		return q[i].valueOf();	//	object
	};
	this.getIterator=function(){
		//	summary
		//	get an iterator for this object
		return new dojox.collections.DictionaryIterator(items);	//	dojox.collections.DictionaryIterator
	};
	this.getKey=function(/* int */ i){
		//	summary
		//	return the key of the item at index i
		return q[i].key;
	};
	this.getKeyList=function(){
		//	summary
		//	return an array of the keys set in this list
		var arr=[];
		var e=this.getIterator();
		while (!e.atEnd()){
			arr.push(e.get().key);
		}
		return arr;	//	array
	};
	this.getValueList=function(){
		//	summary
		//	return an array of values in this list
		var arr=[];
		var e=this.getIterator();
		while (!e.atEnd()){
			arr.push(e.get().value);
		}
		return arr;	//	array
	};
	this.indexOfKey=function(/* string */ k){
		//	summary
		//	return the index of the passed key.
		for (var i=0; i<q.length; i++){
			if (q[i].key==k){
				return i;	//	int
			}
		}
		return -1;	//	int
	};
	this.indexOfValue=function(/* object */ o){
		//	summary
		//	return the first index of object o
		for (var i=0; i<q.length; i++){
			if (q[i].value==o){
				return i;	//	int
			}
		}
		return -1;	//	int
	};
	this.item=function(/* string */ k){
		// 	summary
		//	return the value of the object at location k.
		if(k in items && !testObject[k]){
			return items[k].valueOf();	//	object
		}
		return undefined;	//	object
	};
	this.remove=function(/* string */k){
		// 	summary
		//	remove the item at location k and rebuild the internal collections.
		delete items[k];
		build();
		this.count=q.length;
	};
	this.removeAt=function(/* int */ i){
		//	summary
		//	remove the item at index i, and rebuild the internal collections.
		delete items[q[i].key];
		build();
		this.count=q.length;
	};
	this.replace=function(/* string */ k, /* object */ v){
		//	summary
		//	Replace an existing item if it's there, and add a new one if not.
		if (!items[k]){
			//	we're adding a new object, return false
			this.add(k,v);
			return false; // bool
		}else{
			//	we're replacing an object, return true
			items[k]=new dojox.collections.DictionaryEntry(k,v);
			build();
			return true; // bool
		}
	};
	this.setByIndex=function(/* int */ i, /* object */ o){
		//	summary
		//	set an item by index
		items[q[i].key].value=o;
		build();
		this.count=q.length;
	};
	if (dictionary){
		var e=dictionary.getIterator();
		while (!e.atEnd()){
			var item=e.get();
			q[q.length]=items[item.key]=new dojox.collections.DictionaryEntry(item.key,item.value);
		}
		q.sort(sorter);
	}
}

}

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

if(!dojo._hasResource["dojox.i18n.number"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.i18n.number"] = true;
/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojox.i18n.number");

dojo.experimental("dojox.i18n.number");





/**
* Method to Format and validate a given number
*
* @param Number value
*	The number to be formatted and validated.
* @param Object flags
*   flags.places number of decimal places to show, default is 0 (cannot be Infinity)
*   flags.round true to round the number, false to truncate
* @param String locale
*	The locale used to determine the number format.
* @return String
* 	the formatted number of type String if successful
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
	function splitSubstrings(str, count){
		for(var subs = []; str.length >= count; str = str.substr(0, str.length - count)){
			subs.push(str.substr(-count));
		}
		if (str.length > 0){subs.push(str);}
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
		output = output + flags.decimal + fract;
	}

//TODO: exp

	return output;
};

/**
* Method to convert a properly formatted int string to a primative numeric value.
*
* @param String value
*	The int string to be convertted
* @param string locale
*	The locale used to convert the number string
* @param Object flags
*   flags.validate true to check the string for strict adherence to the locale settings for separator, sign, etc.
*     Default is true
* @return Number
* 	Returns a value of type Number, Number.NaN if not a number, or null if locale is not supported.
**/
dojox.i18n.number.parse = function(value, locale /*optional*/, flags /*optional*/){
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}
	if (typeof flags.validate == "undefined") {flags.validate = true;}

	if (flags.validate && !dojox.i18n.number.isReal(value, locale, flags)) {
		return Number.NaN;
	}

	var numbers = value.split(flags.decimal);
	if (numbers.length > 2){return Number.NaN; }
	var whole = Number(numbers[0].replace(new RegExp("\\" + flags.separator, "g"), ""));
	var fract = (numbers.length == 1) ? 0 : Number(numbers[1]) / Math.pow(10, String(numbers[1]).length); // could also do Number(whole + "." + numbers[1]) if whole != NaN

//TODO: exp

	return whole + fract;
};

/**
  Validates whether a string is in an integer format. 

  @param value  A string.
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object.
    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. sign is optional).
    flags.separator  The character used as the thousands separator.  Default is specified by the locale.
      For more than one symbol use an array, e.g. [",", ""], makes ',' optional.
      The empty array [] makes the default separator optional.   
  @return  true or false.
*/
dojox.i18n.number.isInteger = function(value, locale /*optional*/, flags /*optional*/) {
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	else if (dojo.isArray(flags.separator) && flags.separator.length ===0){
		flags.separator = [formatData[1],""];
	}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}

	var re = new RegExp("^" + dojo.regexp.integer(flags) + "$");
	return re.test(value);
};

/**
  Validates whether a string is a real valued number. 
  Format is the usual exponential notation.

  @param value  A string.
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object.
    flags.places  The integer number of decimal places.
      If not given, the decimal part is optional and the number of places is unlimited.
    flags.decimal  The character used for the decimal point.  The default is specified by the locale.
    flags.exponent  Express in exponential notation.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. the exponential part is optional).
    flags.eSigned  The leading plus-or-minus sign on the exponent.  Can be true, false, 
      or [true, false].  Default is [true, false], (i.e. sign is optional).
    flags in regexp.integer can be applied.
  @return  true or false.
*/
dojox.i18n.number.isReal = function(value, locale /*optional*/, flags /*optional*/) {
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = formatData[1];}
	else if (dojo.isArray(flags.separator) && flags.separator.length ===0){
		flags.separator = [formatData[1],""];
	}
	if (typeof flags.decimal == "undefined") {flags.decimal = formatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = formatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = formatData[4];}

	var re = new RegExp("^" + dojo.regexp.realNumber(flags) + "$");
	return re.test(value);
};

//TODO: hide in a closure?
//TODO: change to use hashes and mixins, rather than arrays
//Q: fallback algorithm/how to structure table:
// does it make sense to look by country code most of the time (wildcard match on
// language, except where it's relevant) and provide default country when only
// a language is given?
(function() {

dojox.i18n.number.FORMAT_TABLE = {
	//0: thousand seperator for monetary, 1: thousand seperator for number, 2: decimal seperator, 3: group size, 4: second group size because of india
	'ar-ae': ["","", ",", 1],
	'ar-bh': ["","",",", 1],
	'ar-dz': ["","",",", 1],
	'ar-eg': ["","", ",", 1],
	'ar-jo': ["","",",", 1],
	'ar-kw': ["","", ",", 1],
	'ar-lb': ["","", ",", 1],
	'ar-ma': ["","", ",", 1],
	'ar-om': ["","", ",", 1],
	'ar-qa': ["","", ",", 1],
	'ar-sa': ["","", ",", 1],
	'ar-sy': ["","", ",", 1],
	'ar-tn': ["","", ",", 1],
	'ar-ye': ["","", ",", 1],
	'cs-cz': [".",".", ",", 3],
	'da-dk': [".",".", ",", 3],
	'de-at': [".",".", ",", 3],
	'de-de': [".",".", ",", 3],
	'de-lu': [".",".", ",", 3],
	//IBM JSL defect 51278. right now we have problem with single quote. //IBM: explain?
	'de-ch': ["'","'", ".", 3], //Q: comma as decimal separator for currency??
	//'de-ch': [".",".", ",", 3],
	'el-gr': [".",".", ",", 3],
	'en-au': [",",",", ".", 3],
	'en-ca': [",",",", ".", 3],
	'en-gb': [",",",", ".", 3],
	'en-hk': [",",",", ".", 3],
	'en-ie': [",",",", ".", 3],
	'en-in': [",",",", ".", 3,2],//india-english, 1,23,456.78
	'en-nz': [",",",", ".", 3],
	'en-us': [",",",", ".", 3],
	'en-za': [",",",", ".", 3],
	
	'es-ar': [".",".", ",", 3],
	'es-bo': [".",".", ",", 3],
	'es-cl': [".",".", ",", 3],
	'es-co': [".",".", ",", 3],
	'es-cr': [".",".", ",", 3],
	'es-do': [".",".", ",", 3],
	'es-ec': [".",".", ",", 3],
	'es-es': [".",".", ",", 3],
	'es-gt': [",",",", ".", 3],
	'es-hn': [",",",", ".", 3],
	'es-mx': [",",",", ".", 3],
	'es-ni': [",",",", ".", 3],
	'es-pa': [",",",", ".", 3],
	'es-pe': [",",",", ".", 3],
	'es-pr': [",",",", ".", 3],
	'es-py': [".",".",",", 3],
	'es-sv': [",", ",",".", 3],
	'es-uy': [".",".",",", 3],
	'es-ve': [".",".", ",", 3],
	
	'fi-fi': [" "," ", ",", 3],
	
	'fr-be': [".",".",",", 3],
	'fr-ca': [" ", " ", ",", 3],
	
	'fr-ch': [" ", " ",".", 3],
	
	'fr-fr': [" "," ", ",", 3],
	'fr-lu': [".",".", ",", 3],
	
	'he-il': [",",",", ".", 3],
	
	'hu-hu': [" ", " ",",", 3],
	
	'it-ch': [" "," ", ".", 3],
	
	'it-it': [".",".", ",", 3],
	'ja-jp': [",",",", ".", 3],
	'ko-kr': [",", ",",".", 3],
	
	'no-no': [".",".", ",", 3],
	
	'nl-be': [" "," ", ",", 3],
	'nl-nl': [".",".", ",", 3],
	'pl-pl': [".", ".",",", 3],
	
	'pt-br': [".",".", ",", 3],
	'pt-pt': [".",".", "$", 3],
	'ru-ru': [" ", " ",",", 3],
	
	'sv-se': ["."," ", ",", 3],
	
	'tr-tr': [".",".", ",", 3],
	
	'zh-cn': [",",",", ".", 3],
	'zh-hk': [",",",",".", 3],
	'zh-tw': [",", ",",".", 3],
	'*': [",",",", ".", 3]
};
})();

dojox.i18n.number._mapToLocalizedFormatData = function(table, locale){
	locale = dojo.i18n.normalizeLocale(locale);
//TODO: most- to least-specific search? search by country code?
//TODO: implement aliases to simplify and shorten tables
	var data = table[locale];
	if (typeof data == 'undefined'){data = table['*'];}
	return data;
}

}

if(!dojo._hasResource["dojox.i18n.currency"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.i18n.currency"] = true;
/*
	Copyright (c) 2004-2006, The Dojo Foundation
	All Rights Reserved.

	Licensed under the Academic Free License version 2.1 or above OR the
	modified BSD license. For more information on Dojo licensing, see:

		http://dojotoolkit.org/community/licensing.shtml
*/

dojo.provide("dojox.i18n.currency");

dojo.experimental("dojox.i18n.currency");





/**
* Method to Format and validate a given number a monetary value
*
* @param Number value
*	The number to be formatted and validated.
* @param String iso the ISO 4217 currency code
* @param Object flags
*   flags.places The number of decimal places to be included in the formatted number
* @param String locale the locale to determine formatting used.  By default, the locale defined by the
*   host environment: dojo.locale
* @return String
* 	the formatted currency of type String if successful; Nan if an
* 	invalid currency is provided or null if an unsupported locale value was provided.
**/
dojox.i18n.currency.format = function(value, iso, flags /*optional*/, locale /*optional*/){
	flags = (typeof flags == "object") ? flags : {};

	var formatData = dojox.i18n.currency._mapToLocalizedFormatData(dojox.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {flags.places = formatData.places;}
	if (typeof flags.places == "undefined") {flags.places = 2;}
	flags.signed = false;

	var result = dojox.i18n.number.format(value, flags, locale);

	var sym = formatData.symbol;
	if (formatData.adjSpace == "symbol"){ 
		if (formatData.placement == "after"){
			sym = " " + sym;// TODO: nbsp?
		}else{
			sym = sym + " ";// TODO: nbsp?
		}
	}

	if (value < 0){
		if (formatData.signPlacement == "before"){
			sym = "-" + sym;
		}else if (formatData.signPlacement == "after"){
			sym = sym + "-";
		}
	}

	var spc = (formatData.adjSpace == "number") ? " " : ""; // TODO: nbsp?
	if (formatData.placement == "after"){
		result = result + spc + sym;
	}else{
		result = sym + spc + result;
	}

	if (value < 0){
		if (formatData.signPlacement == "around"){
			result = "(" + result + ")";
		}else if (formatData.signPlacement == "end"){
			result = result + "-";
		}else if (!formatData.signPlacement || formatData.signPlacement == "begin"){
			result = "-" + result;
		}
	}

	return result;
};

/**
* Method to convert a properly formatted monetary value to a primative numeric value.
*
* @param String value
*	The int string to be convertted
  @param String iso the ISO 4217 currency code
* @param String locale the locale to determine formatting used.  By default, the locale defined by the
*   host environment: dojo.locale
* @param Object flags
*   flags.validate true to check the string for strict adherence to the locale settings for separator, sign, etc.
*     Default is true
* @return Number
* 	Returns a primative numeric value, Number.NaN if unable to convert to a number, or null if an unsupported locale is provided.
**/
dojox.i18n.currency.parse = function(value, iso, locale, flags /*optional*/){
	if (typeof flags.validate == "undefined") {flags.validate = true;}

	if (flags.validate && !dojox.i18n.number.isCurrency(value, iso, locale, flags)) {
		return Number.NaN;
	}

	var sign = (value.indexOf('-') != -1);
	var abs = abs.replace(/\-/, "");

	var formatData = dojox.i18n.currency._mapToLocalizedFormatData(dojox.i18n.currency.FORMAT_TABLE, iso, locale);
	abs = abs.replace(new RegExp("\\" + formatData.symbol), "");
	//TODO: trim?

	var number = dojox.i18n.number.parse(abs, locale, flags);
	if (sign){number = number * -1;}
	return number;
};

/**
  Validates whether a string denotes a monetary value. 

  @param value  A string
  @param iso the ISO 4217 currency code
  @param locale the locale to determine formatting used.  By default, the locale defined by the
    host environment: dojo.locale
  @param flags  An object
    flags.symbol  A currency symbol such as Yen "�", Pound "�", or the Euro sign "�".  
        The default is specified by the iso code.  For more than one symbol use an array, e.g. ["$", ""], makes $ optional.
        The empty array [] makes the default currency symbol optional.
    flags.placement  The symbol can come "before" or "after".  The default is specified by the iso code.
    flags.signed  The leading plus-or-minus sign.  Can be true, false, or [true, false].
      Default is [true, false], (i.e. sign is optional).
    flags.signPlacement  The sign can come "before" or "after" the symbol or "around" the whole expression
    	with parenthesis, such as CAD: (123$).  The default is specified by the iso code.
    flags.separator  The character used as the thousands separator. The default is specified by the locale.
        The empty array [] makes the default separator optional.
    flags.fractional  The appropriate number of decimal places for fractional currency (e.g. cents)
      Can be true, false, or [true, false].  Default is [true, false], (i.e. cents are optional).
    flags.places  The integer number of decimal places.
      If not given, an amount appropriate to the iso code is used.
    flags.fractional  The appropriate number of decimal places for fractional currency (e.g. cents)
      Can be true, false, or [true, false].  Default is [true, false], (i.e. cents are optional).
    flags.decimal  The character used for the decimal point.  The default is specified by the locale.
  @return  true or false.
*/
dojox.i18n.currency.isCurrency = function(value, iso, locale /*optional*/, flags){
	flags = (typeof flags == "object") ? flags : {};

	var numberFormatData = dojox.i18n.number._mapToLocalizedFormatData(dojox.i18n.number.FORMAT_TABLE, locale);
	if (typeof flags.separator == "undefined") {flags.separator = numberFormatData[0];}
	else if (dojo.isArray(flags.separator) && flags.separator.length == 0){flags.separator = [numberFormatData[0],""];}
	if (typeof flags.decimal == "undefined") {flags.decimal = numberFormatData[2];}
	if (typeof flags.groupSize == "undefined") {flags.groupSize = numberFormatData[3];}
	if (typeof flags.groupSize2 == "undefined") {flags.groupSize2 = numberFormatData[4];}

	var formatData = dojox.i18n.currency._mapToLocalizedFormatData(dojox.i18n.currency.FORMAT_TABLE, iso, locale);
	if (typeof flags.places == "undefined") {flags.places = formatData.places;}
	if (typeof flags.places == "undefined") {flags.places = 2;}
	if (typeof flags.symbol == "undefined") {flags.symbol = formatData.symbol;}
	else if (dojo.isArray(flags.symbol) && flags.symbol.length == 0){flags.symbol = [formatData.symbol,""];}
	if (typeof flags.placement == "undefined") {flags.placement = formatData.placement;}
	//TODO more... or mixin?

	var re = new RegExp("^" + dojo.regexp.currency(flags) + "$");
//dojo.debug(value+":"+dojo.regexp.currency(flags)+"="+re.test(value));
	return re.test(value);
};

dojox.i18n.currency._mapToLocalizedFormatData = function(table, iso, locale /*optional*/){
	var formatData = dojox.i18n.currency.FORMAT_TABLE[iso];
	if (!dojo.isArray(formatData)){
		return formatData;
	}

	return dojox.i18n.number._mapToLocalizedFormatData(formatData[0], locale);
};

(function() {
	var arabic = {symbol: "\u062C", placement: "after", htmlSymbol: "?"};
	var euro = {symbol: "\u20AC", placement: "before", adjSpace: "symbol", htmlSymbol: "&euro;"};
	var euroAfter = {symbol: "\u20AC", placement: "after", htmlSymbol: "&euro;"};

//Q: Do European countries still use their old ISO symbols instead of just EUR?
//Q: are signPlacement and currency symbol placement ISO-dependent or are they really locale-dependent?
//TODO: htmlSymbol is for html entities, need images? (IBM: why? why can't we just use unicode everywhere?)
//TODO: hide visibility of this table?
//for html entities, need a image for arabic symbol "BHD" as "DZD", "EGP", "JOD", "KWD" "LBP", "MAD", "OMR", "QAR", "SAR", "SYP", "TND", "AED", "YER"
//Note: html entities not used at the moment
//placement: placement of currency symbol, before or after number
//signPlacement: placement of negative sign, before or after symbol, or begin or end of expression, or around with parentheses
// This table assumes defaults of
//	places: 2, placement: "before", signPlacement: "begin", adjSpace: undefined, htmlSymbol: undefined]
dojox.i18n.currency.FORMAT_TABLE = {
	AED: {symbol: "\u062c", placement: "after"},
	ARS: {symbol: "$", signPlacement: "after"},
	//Old ATS: {symbol: "S", adjSpace: "symbol"},
	ATS: {symbol: "\u20AC", adjSpace: "number", signPlacement: "after", htmlSymbol: "&euro;"}, 	//Austria using "EUR" // neg should read euro + sign + space + number
	AUD: {symbol: "$"},
	BOB: {symbol: "$b"},
	BRL: {symbol: "R$", adjSpace: "symbol"},
	//Old BEF: {symbol: "BF", placement: "after", adjSpace: "symbol"},
	BEF: euroAfter,	//Belgium using "EUR"
	//Old BHD: {symbol: "\u062C", signPlacement: "end", places: 3, htmlSymbol: "?"},
	BHD: arabic,
	//TODO: I'm suspicious that all the other entries have locale-specific data in them, too?
	//Q: which attributes are iso-specific and which are locale specific?
	CAD: [{
			'*' : {symbol: "$"},
			'fr-ca' : {symbol: "$", placement: "after", signPlacement: "around"}
		}],
	CHF: {symbol: "CHF", adjSpace: "symbol", signPlacement: "after"},
	CLP: {symbol: "$"},
	COP: {symbol: "$", signPlacement: "around"},
	CNY: {symbol: "\u00A5", htmlSymbol: "&yen;"},
	//// Costa Rica  - Spanish slashed C. need to find out the html entity image
	CRC: {symbol: "\u20A1", signPlacement: "after", htmlSymbol: "?"},
	// Czech Republic  - Czech //need image for html entities
	CZK: {symbol: "Kc", adjSpace: "symbol", signPlacement: "after"},
	DEM: euroAfter,
	DKK: {symbol: "kr.", adjSpace: "symbol", signPlacement: "after"},
	DOP: {symbol: "$"},
	//for html entities, need a image, bidi, using "rtl", so from the link, symbol is suffix
	//Old DZD: {symbol: "\u062C", signPlacement: "end", places: 3, htmlSymbol: "?"},
	DZD: arabic,
	//Ecuador using "USD"
	ECS: {symbol: "$", signPlacement: "after"},
	EGP: arabic,
	//Old ESP: {symbol: "Pts", placement: "after", adjSpace: "symbol", places: 0},
	ESP: euroAfter,	//spain using "EUR"
	EUR: euro,
	//Old FIM: {symbol: "mk", placement: "after", adjSpace: "symbol"},
	FIM: euroAfter,	//Finland using "EUR"
	//Old FRF: {symbol: "F", placement: "after", adjSpace: "symbol"},
	FRF: euroAfter,	//France using "EUR"
	GBP: {symbol: "\u00A3", htmlSymbol: "&pound;"},
	GRD: {symbol: "\u20AC", signPlacement: "end", htmlSymbol: "&euro;"},
	GTQ: {symbol: "Q", signPlacement: "after"},
	//Hong Kong need "HK$" and "$". Now only support "HK$"
	HKD: {symbol: "HK$"},
	HNL: {symbol: "L.", signPlacement: "end"},
	HUF: {symbol: "Ft", placement: "after", adjSpace: "symbol"},
	//IEP: {symbol: "\u00A3", htmlSymbol: "&pound;"},
	IEP: {symbol: "\u20AC", htmlSymbol: "&euro;"},	//ireland using "EUR" at the front.
	//couldn't know what Israel - Hebrew symbol, some sites use "NIS", bidi, using "rtl", so from the link, symbol is suffix (IBM: huh?)
	//ILS: {symbol: "\u05E9\u0022\u05D7", signPlacement: "end", htmlSymbol: "?"},
	ILS: {symbol: "\u05E9\u0022\u05D7", placement: "after", htmlSymbol: "?"},
	INR: {symbol: "Rs."},
	//ITL: {symbol: "L", adjSpace: "symbol", signPlacement: "after", places: 0},
	ITL: {symbol: "\u20AC", signPlacement: "after", htmlSymbol: "&euro;"},	//Italy using "EUR"
	JOD: arabic,
	JPY: {symbol: "\u00a5", places: 0, htmlSymbol: "&yen;"},
	KRW: {symbol: "\u20A9", places: 0, htmlSymbol: "?"},
	KWD: arabic,
	LBP: arabic,
	//Old LUF: {symbol: "LUF", placement: "after", adjSpace: "symbol"},
	//for Luxembourg,using "EUR"
	LUF: euroAfter,
	MAD: arabic,
	MXN: {symbol: "$", signPlacement: "around"},
	NIO: {symbol: "C$", adjSpace: "symbol", signPlacement: "after"},
	//Old NLG: {symbol: "f", adjSpace: "symbol", signPlacement: "end"},
	//Netherlands, using "EUR"
	NLG: {symbol: "\u20AC", signPlacement: "end", htmlSymbol: "&euro;"},
	NOK: {symbol: "kr", adjSpace: "symbol", signPlacement: "after"},
	NZD: {symbol: "$"},
	OMR: arabic,
	PAB: {symbol: "B/", adjSpace: "symbol", signPlacement: "after"},
	PEN: {symbol: "S/", signPlacement: "after"},
	//couldn't know what the symbol is from ibm link. (IBM: what does this mean?  Is the symbol 'z' wrong?)
	PLN: {symbol: "z", placement: "after"},
	//Old PTE: {symbol: "Esc.", placement: "after", adjSpace: "symbol", places: 0},
	PTE: euroAfter,
	PYG: {symbol: "Gs.", signPlacement: "after"},
	QAR: arabic,
	RUR: {symbol: "rub.", placement: "after"},
	SAR: arabic,
	SEK: {symbol: "kr", placement: "after", adjSpace: "symbol"},
	SGD: {symbol: "$"},
	//// El Salvador - Spanish slashed C. need to find out. (IBM: need to find out what?)
	SVC: {symbol: "\u20A1", signPlacement: "after", adjSpace: "symbol"},
	//for html entities, need a image
	SYP: arabic,
	TND: arabic,
	TRL: {symbol: "TL", placement: "after"},
	TWD: {symbol: "NT$"},
	USD: {symbol: "$"},
	UYU: {symbol: "$U", signplacement: "after", adjSpace: "symbol"},
	VEB: {symbol: "Bs", signplacement: "after", adjSpace: "symbol"},
	YER: arabic,
	ZAR: {symbol: "R", signPlacement: "around"}
};

})();

}

if(!dojo._hasResource["dojox.validate.regexp"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.validate.regexp"] = true;
dojo.provide("dojox.validate.regexp");

 

// *** Regular Expression Generator does not entirely live here ***
// FIXME: is this useful enough to be in /dojox/regexp/_base.js, or
// should it respect namespace and be dojox.validate.regexp?
// some say a generic regexp to match zipcodes and urls would be useful
// others would say it's a spare tire. 
dojox.regexp = { ca: {}, us: {} }; 

dojox.regexp.tld = function(/*Object?*/flags){
	// summary: Builds a RE that matches a top-level domain
	//
	// flags:
	//    flags.allowCC  Include 2 letter country code domains.  Default is true.
	//    flags.allowGeneric  Include the generic domains.  Default is true.
	//    flags.allowInfra  Include infrastructure domains.  Default is true.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.allowCC != "boolean"){ flags.allowCC = true; }
	if(typeof flags.allowInfra != "boolean"){ flags.allowInfra = true; }
	if(typeof flags.allowGeneric != "boolean"){ flags.allowGeneric = true; }

	// Infrastructure top-level domain - only one at present
	var infraRE = "arpa";

	// Generic top-level domains RE.
	var genericRE = 
		"aero|biz|com|coop|edu|gov|info|int|mil|museum|name|net|org|pro|travel|xxx|jobs|mobi|post";
	
	// Country Code top-level domains RE
	var ccRE = 
		"ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|az|ba|bb|bd|be|bf|bg|bh|bi|bj|bm|bn|bo|br|" +
		"bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|cn|co|cr|cu|cv|cx|cy|cz|de|dj|dk|dm|do|dz|" +
		"ec|ee|eg|er|eu|es|et|fi|fj|fk|fm|fo|fr|ga|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|"
		+
		"gy|hk|hm|hn|hr|ht|hu|id|ie|il|im|in|io|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kr|kw|ky|kz|" +
		"la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|mg|mh|mk|ml|mm|mn|mo|mp|mq|mr|ms|mt|mu|mv|mw|mx|" +
		"my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|om|pa|pe|pf|pg|ph|pk|pl|pm|pn|pr|ps|pt|pw|py|qa|" +
		"re|ro|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sk|sl|sm|sn|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tm|" +
		"tn|to|tr|tt|tv|tw|tz|ua|ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw";

	// Build top-level domain RE
	var a = [];
	if(flags.allowInfra){ a.push(infraRE); }
	if(flags.allowGeneric){ a.push(genericRE); }
	if(flags.allowCC){ a.push(ccRE); }

	var tldRE = "";
	if (a.length > 0) {
		tldRE = "(" + a.join("|") + ")";
	}

	return tldRE; // String
}

dojox.regexp.ipAddress = function(/*Object?*/flags){
	// summary: Builds a RE that matches an IP Address
	//
	// description:
	//  Supports 5 formats for IPv4: dotted decimal, dotted hex, dotted octal, decimal and hexadecimal.
	//  Supports 2 formats for Ipv6.
	//
	// flags  An object.  All flags are boolean with default = true.
	//    flags.allowDottedDecimal  Example, 207.142.131.235.  No zero padding.
	//    flags.allowDottedHex  Example, 0x18.0x11.0x9b.0x28.  Case insensitive.  Zero padding allowed.
	//    flags.allowDottedOctal  Example, 0030.0021.0233.0050.  Zero padding allowed.
	//    flags.allowDecimal  Example, 3482223595.  A decimal number between 0-4294967295.
	//    flags.allowHex  Example, 0xCF8E83EB.  Hexadecimal number between 0x0-0xFFFFFFFF.
	//      Case insensitive.  Zero padding allowed.
	//    flags.allowIPv6   IPv6 address written as eight groups of four hexadecimal digits.
	//	FIXME: ipv6 can be written multiple ways IIRC
	//    flags.allowHybrid   IPv6 address written as six groups of four hexadecimal digits
	//      followed by the usual 4 dotted decimal digit notation of IPv4. x:x:x:x:x:x:d.d.d.d

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.allowDottedDecimal != "boolean"){ flags.allowDottedDecimal = true; }
	if(typeof flags.allowDottedHex != "boolean"){ flags.allowDottedHex = true; }
	if(typeof flags.allowDottedOctal != "boolean"){ flags.allowDottedOctal = true; }
	if(typeof flags.allowDecimal != "boolean"){ flags.allowDecimal = true; }
	if(typeof flags.allowHex != "boolean"){ flags.allowHex = true; }
	if(typeof flags.allowIPv6 != "boolean"){ flags.allowIPv6 = true; }
	if(typeof flags.allowHybrid != "boolean"){ flags.allowHybrid = true; }

	// decimal-dotted IP address RE.
	var dottedDecimalRE = 
		// Each number is between 0-255.  Zero padding is not allowed.
		"((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";

	// dotted hex IP address RE.  Each number is between 0x0-0xff.  Zero padding is allowed, e.g. 0x00.
	var dottedHexRE = "(0[xX]0*[\\da-fA-F]?[\\da-fA-F]\\.){3}0[xX]0*[\\da-fA-F]?[\\da-fA-F]";

	// dotted octal IP address RE.  Each number is between 0000-0377.  
	// Zero padding is allowed, but each number must have at least 4 characters.
	var dottedOctalRE = "(0+[0-3][0-7][0-7]\\.){3}0+[0-3][0-7][0-7]";

	// decimal IP address RE.  A decimal number between 0-4294967295.  
	var decimalRE =  "(0|[1-9]\\d{0,8}|[1-3]\\d{9}|4[01]\\d{8}|42[0-8]\\d{7}|429[0-3]\\d{6}|" +
		"4294[0-8]\\d{5}|42949[0-5]\\d{4}|429496[0-6]\\d{3}|4294967[01]\\d{2}|42949672[0-8]\\d|429496729[0-5])";

	// hexadecimal IP address RE. 
	// A hexadecimal number between 0x0-0xFFFFFFFF. Case insensitive.  Zero padding is allowed.
	var hexRE = "0[xX]0*[\\da-fA-F]{1,8}";

	// IPv6 address RE. 
	// The format is written as eight groups of four hexadecimal digits, x:x:x:x:x:x:x:x,
	// where x is between 0000-ffff. Zero padding is optional. Case insensitive. 
	var ipv6RE = "([\\da-fA-F]{1,4}\\:){7}[\\da-fA-F]{1,4}";

	// IPv6/IPv4 Hybrid address RE. 
	// The format is written as six groups of four hexadecimal digits, 
	// followed by the 4 dotted decimal IPv4 format. x:x:x:x:x:x:d.d.d.d
	var hybridRE = "([\\da-fA-F]{1,4}\\:){6}" + 
		"((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5])";

	// Build IP Address RE
	var a = [];
	if(flags.allowDottedDecimal){ a.push(dottedDecimalRE); }
	if(flags.allowDottedHex){ a.push(dottedHexRE); }
	if(flags.allowDottedOctal){ a.push(dottedOctalRE); }
	if(flags.allowDecimal){ a.push(decimalRE); }
	if(flags.allowHex){ a.push(hexRE); }
	if(flags.allowIPv6){ a.push(ipv6RE); }
	if(flags.allowHybrid){ a.push(hybridRE); }

	var ipAddressRE = "";
	if(a.length > 0){
		ipAddressRE = "(" + a.join("|") + ")";
	}
	return ipAddressRE; // String
}

dojox.regexp.host = function(/*Object?*/flags){
	// summary: Builds a RE that matches a host
	// description: A host is a domain name or an IP address, possibly followed by a port number.
	// flags: An object.
	//    flags.allowIP  Allow an IP address for hostname.  Default is true.
	//    flags.allowLocal  Allow the host to be "localhost".  Default is false.
	//    flags.allowPort  Allow a port number to be present.  Default is true.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.allowIP != "boolean"){ flags.allowIP = true; }
	if(typeof flags.allowLocal != "boolean"){ flags.allowLocal = false; }
	if(typeof flags.allowPort != "boolean"){ flags.allowPort = true; }

	// Domain names can not end with a dash.
	var domainNameRE = "([0-9a-zA-Z]([-0-9a-zA-Z]{0,61}[0-9a-zA-Z])?\\.)+" + dojox.regexp.tld(flags);

	// port number RE
	var portRE = ( flags.allowPort ) ? "(\\:" + dojox.regexp.integer({signed: false}) + ")?" : "";

	// build host RE
	var hostNameRE = domainNameRE;
	if(flags.allowIP){ hostNameRE += "|" +  dojox.regexp.ipAddress(flags); }
	if(flags.allowLocal){ hostNameRE += "|localhost"; }

	return "(" + hostNameRE + ")" + portRE; // String
}

dojox.regexp.url = function(/*Object?*/flags){
	// summary: Builds a regular expression that matches a URL
	//
	// flags: An object
	//    flags.scheme  Can be true, false, or [true, false]. 
	//      This means: required, not allowed, or match either one.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.scheme == "undefined"){ flags.scheme = [true, false]; }

	// Scheme RE
	var protocolRE = dojo.regexp.buildGroupRE(flags.scheme,
		function(q){ if(q){ return "(https?|ftps?)\\://"; } return ""; }
	);

	// Path and query and anchor RE
	var pathRE = "(/([^?#\\s/]+/)*)?([^?#\\s/]+(\\?[^?#\\s/]*)?(#[A-Za-z][\\w.:-]*)?)?";

	return protocolRE + dojox.regexp.host(flags) + pathRE;
}

dojox.regexp.emailAddress = function(/*Object?*/flags){

	// summary: Builds a regular expression that matches an email address
	//
	//flags: An object
	//    flags.allowCruft  Allow address like <mailto:foo@yahoo.com>.  Default is false.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if (typeof flags.allowCruft != "boolean") { flags.allowCruft = false; }
	flags.allowPort = false; // invalid in email addresses

	// user name RE - apostrophes are valid if there's not 2 in a row
	var usernameRE = "([\\da-zA-Z]+[-._+&'])*[\\da-zA-Z]+";

	// build emailAddress RE
	var emailAddressRE = usernameRE + "@" + dojox.regexp.host(flags);

	// Allow email addresses with cruft
	if ( flags.allowCruft ) {
		emailAddressRE = "<?(mailto\\:)?" + emailAddressRE + ">?";
	}

	return emailAddressRE; // String
}

dojox.regexp.emailAddressList = function(/*Object?*/flags){
	// summary: Builds a regular expression that matches a list of email addresses.
	//
	// flags: An object.
	//    flags.listSeparator  The character used to separate email addresses.  Default is ";", ",", "\n" or " ".
	//    flags in regexp.emailAddress can be applied.
	//    flags in regexp.host can be applied.
	//    flags in regexp.ipAddress can be applied.
	//    flags in regexp.tld can be applied.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.listSeparator != "string"){ flags.listSeparator = "\\s;,"; }

	// build a RE for an Email Address List
	var emailAddressRE = dojox.regexp.emailAddress(flags);
	var emailAddressListRE = "(" + emailAddressRE + "\\s*[" + flags.listSeparator + "]\\s*)*" + 
		emailAddressRE + "\\s*[" + flags.listSeparator + "]?\\s*";

	return emailAddressListRE; // String
}

dojox.regexp.us.state = function(/*Object?*/flags){
	// summary: A regular expression to match US state and territory abbreviations
	//
	// flags  An object.
	//    flags.allowTerritories  Allow Guam, Puerto Rico, etc.  Default is true.
	//    flags.allowMilitary  Allow military 'states', e.g. Armed Forces Europe (AE).  Default is true.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.allowTerritories != "boolean"){ flags.allowTerritories = true; }
	if(typeof flags.allowMilitary != "boolean"){ flags.allowMilitary = true; }

	// state RE
	var statesRE = 
		"AL|AK|AZ|AR|CA|CO|CT|DE|DC|FL|GA|HI|ID|IL|IN|IA|KS|KY|LA|ME|MD|MA|MI|MN|MS|MO|MT|" + 
		"NE|NV|NH|NJ|NM|NY|NC|ND|OH|OK|OR|PA|RI|SC|SD|TN|TX|UT|VT|VA|WA|WV|WI|WY";

	// territories RE
	var territoriesRE = "AS|FM|GU|MH|MP|PW|PR|VI";

	// military states RE
	var militaryRE = "AA|AE|AP";

	// Build states and territories RE
	if(flags.allowTerritories){ statesRE += "|" + territoriesRE; }
	if(flags.allowMilitary){ statesRE += "|" + militaryRE; }

	return "(" + statesRE + ")"; // String
}

dojox.regexp.ca.postalCode = function(){
	var postalRE =
		"[A-Z][0-9][A-Z] [0-9][A-Z][0-9]";
	return "(" + postalRE + ")";
}

dojox.regexp.ca.province = function(){
	// summary: a regular expression to match Canadian Province Abbreviations
	var stateRE = 
		"AB|BC|MB|NB|NL|NS|NT|NU|ON|PE|QC|SK|YT";
	return "(" + statesRE + ")";
}

dojox.regexp.numberFormat = function(/*Object?*/flags){
	// summary: Builds a regular expression to match any sort of number based format
	// description:
	//  Use this method for phone numbers, social security numbers, zip-codes, etc.
	//  The RE can match one format or one of multiple formats.
	//
	//  Format
	//    #        Stands for a digit, 0-9.
	//    ?        Stands for an optional digit, 0-9 or nothing.
	//    All other characters must appear literally in the expression.
	//
	//  Example   
	//    "(###) ###-####"       ->   (510) 542-9742
	//    "(###) ###-#### x#???" ->   (510) 542-9742 x153
	//    "###-##-####"          ->   506-82-1089       i.e. social security number
	//    "#####-####"           ->   98225-1649        i.e. zip code
	//
	// flags:  An object
	//    flags.format  A string or an Array of strings for multiple formats.

	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	if(typeof flags.format == "undefined"){ flags.format = "###-###-####"; }

	// Converts a number format to RE.
	var digitRE = function(format){
		// escape all special characters, except '?'
		format = dojo.regexp.escapeString(format, "?");

		// Now replace '?' with Regular Expression
		format = format.replace(/\?/g, "\\d?");

		// replace # with Regular Expression
		format = format.replace(/#/g, "\\d");

		return format; // String
	};

	// build RE for multiple number formats
	return dojo.regexp.buildGroupRE(flags.format, digitRE); //String
}

}

if(!dojo._hasResource["dojox.validate._base"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.validate._base"] = true;
dojo.provide("dojox.validate._base");

		// dojo core expressions
		// dojo number expressions
 	// additional expressions

dojox.validate.isText = function(/*String*/value, /*Object?*/flags){
	// summary:
	//	Checks if a string has non whitespace characters. 
	//	Parameters allow you to constrain the length.
	//
	// value: A string
	// flags: {length: Number, minlength: Number, maxlength: Number}
	//    flags.length  If set, checks if there are exactly flags.length number of characters.
	//    flags.minlength  If set, checks if there are at least flags.minlength number of characters.
	//    flags.maxlength  If set, checks if there are at most flags.maxlength number of characters.
	
	flags = (typeof flags == "object") ? flags : {};
	
	// test for text
	if(/^\s*$/.test(value)){ return false; } // Boolean
	
	// length tests
	if(typeof flags.length == "number" && flags.length != value.length){ return false; } // Boolean
	if(typeof flags.minlength == "number" && flags.minlength > value.length){ return false; } // Boolean
	if(typeof flags.maxlength == "number" && flags.maxlength < value.length){ return false; } // Boolean
	
	return true; // Boolean

}

dojox.validate._isInRangeCache = {};
dojox.validate.isInRange = function(/*String*/value, /*Object?*/flags){
	// summary:
	//	Validates whether a string denoting an integer, 
	//	real number, or monetary value is between a max and min. 
	//
	// value: A string
	// flags: {max:Number, min:Number, decimal:String}
	//    flags.max  A number, which the value must be less than or equal to for the validation to be true.
	//    flags.min  A number, which the value must be greater than or equal to for the validation to be true.
	//    flags.decimal  The character used for the decimal point.  Default is ".".
	
    // fixes ticket #2908
    value = dojo.number.parse(value, flags);
	if(isNaN(value)){
		return false; // Boolean
	}
    
	// assign default values to missing paramters
	flags = (typeof flags == "object") ? flags : {};
	var max = (typeof flags.max == "number") ? flags.max : Infinity;
	var min = (typeof flags.min == "number") ? flags.min : -Infinity;
	var dec = (typeof flags.decimal == "string") ? flags.decimal : ".";
	
	var cache = dojox.validate._isInRangeCache;
	var cacheIdx = value+"max"+max+"min"+min+"dec"+dec;
	if(typeof cache[cacheIdx] != "undefined"){
		return cache[cacheIdx];
	}

	if ( value < min || value > max ) { cache[cacheIdx] = false; return false; } // Boolean

	cache[cacheIdx] = true; return true; // Boolean
}

dojox.validate.isNumberFormat = function(/*String*/value, /*Object?*/flags){
	// summary:
	//	Validates any sort of number based format
	//
	// description:
	//	Use it for phone numbers, social security numbers, zip-codes, etc.
	//	The value can be validated against one format or one of multiple formats.
	//
	//  Format
	//    #        Stands for a digit, 0-9.
	//    ?        Stands for an optional digit, 0-9 or nothing.
	//    All other characters must appear literally in the expression.
	//
	//  Example   
	//    "(###) ###-####"       ->   (510) 542-9742
	//    "(###) ###-#### x#???" ->   (510) 542-9742 x153
	//    "###-##-####"          ->   506-82-1089       i.e. social security number
	//    "#####-####"           ->   98225-1649        i.e. zip code
	//
	// value: A string
	// flags: {format:String}
	//    flags.format  A string or an Array of strings for multiple formats.

	var re = new RegExp("^" + dojox.regexp.numberFormat(flags) + "$", "i");
	return re.test(value); // Boolean
}

dojox.validate.isValidLuhn = function(/*String*/value){
	//summary: Compares value against the Luhn algorithm to verify its integrity
	var sum, parity, curDigit;
	if(typeof value!='string'){
		value = String(value);
	}
	value = value.replace(/[- ]/g,''); //ignore dashes and whitespaces
	parity = value.length%2;
	sum=0;
	for(var i=0;i<value.length;i++){
		curDigit = parseInt(value.charAt(i));
		if(i%2==parity){
			curDigit*=2;
		}
		if(curDigit>9){
			curDigit-=9;
		}
		sum+=curDigit;
	}
	return !(sum%10); //Boolean
}

/**
	Procedural API Description

		The main aim is to make input validation expressible in a simple format.
		You define profiles which declare the required and optional fields and any constraints they might have.
		The results are provided as an object that makes it easy to handle missing and invalid input.

	Usage

		var results = dojo.validate.check(form, profile);

	Profile Object

		var profile = {
			// filters change the field value and are applied before validation.
			trim: ["tx1", "tx2"],
			uppercase: ["tx9"],
			lowercase: ["tx5", "tx6", "tx7"],
			ucfirst: ["tx10"],
			digit: ["tx11"],

			// required input fields that are blank will be reported missing.
			// required radio button groups and drop-down lists with no selection will be reported missing.
			// checkbox groups and selectboxes can be required to have more than one value selected.
			// List required fields by name and use this notation to require more than one value: {checkboxgroup: 2}, {selectboxname: 3}.
			required: ["tx7", "tx8", "pw1", "ta1", "rb1", "rb2", "cb3", "s1", {"doubledip":2}, {"tripledip":3}],

			// dependant/conditional fields are required if the target field is present and not blank.
			// At present only textbox, password, and textarea fields are supported.
			dependencies:	{
				cc_exp: "cc_no",	
				cc_type: "cc_no",	
			},

			// Fields can be validated using any boolean valued function.  
			// Use arrays to specify parameters in addition to the field value.
			constraints: {
				field_name1: myValidationFunction,
				field_name2: dojo.validate.isInteger,
				field_name3: [myValidationFunction, additional parameters],
				field_name4: [dojo.validate.isValidDate, "YYYY.MM.DD"],
				field_name5: [dojo.validate.isEmailAddress, false, true],
			},

			// Confirm is a sort of conditional validation.
			// It associates each field in its property list with another field whose value should be equal.
			// If the values are not equal, the field in the property list is reported as Invalid. Unless the target field is blank.
			confirm: {
				email_confirm: "email",	
				pw2: "pw1",	
			}
		};

	Results Object

		isSuccessful(): Returns true if there were no invalid or missing fields, else it returns false.
		hasMissing():  Returns true if the results contain any missing fields.
		getMissing():  Returns a list of required fields that have values missing.
		isMissing(field):  Returns true if the field is required and the value is missing.
		hasInvalid():  Returns true if the results contain fields with invalid data.
		getInvalid():  Returns a list of fields that have invalid values.
		isInvalid(field):  Returns true if the field has an invalid value.

*/

}

if(!dojo._hasResource["dojox.validate.creditCard"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.validate.creditCard"] = true;
dojo.provide("dojox.validate.creditCard");



/*
	Validates Credit Cards using account number rules in conjunction with the Luhn algorigthm
	
 */

dojox.validate.isValidCreditCard = function(/*String|Int*/value, /*String*/ccType){
	//Summary:
	//  checks if type matches the # scheme, and if Luhn checksum is accurate (unless its an Enroute card, the checkSum is skipped)
	
	//Value: Boolean
	if(value&&ccType&&((ccType.toLowerCase()=='er'||dojox.validate.isValidLuhn(value))&&(dojox.validate.isValidCreditCardNumber(value,ccType.toLowerCase())))){
			return true; //Boolean
	}
	return false; //Boolean
}
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
	/* 	FIXME: not sure on all the abbreviations for credit cards,below is what each stands for atleast to my knowledge
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
	if(ccType&&dojo.indexOf(cardinfo,ccType.toLowerCase())){
		return Boolean(value.match(cardinfo[ccType.toLowerCase()])); // boolean
	}else{
		for(var p in cardinfo){
			if(value.match('^'+cardinfo[p]+'$')!=null){
				results.push(p);
			}
		}
		return (results.length)?results.join('|'):false; // string | boolean
	}	
}

dojox.validate.isValidCvv = function(/*String|Int*/value, /*String*/ccType) {
	//Summary:
	//  returns true if the security code (CCV) matches the correct format for supplied ccType
	
	//Value: Boolean
	
	if(typeof value!='string'){
		value=String(value);
	}
	var format;
	switch (ccType.toLowerCase()){
		case 'mc':
		case 'ec':
		case 'vi':
		case 'di':
			format = '###';
			break;
		case 'ax':
			format = '####';
			break;
		default:
			return false; //Boolean
	}
	var flags = {format:format};
	//FIXME? Why does isNumberFormat take an object for flags when its only parameter is either a string or an array inside the object?
	if ((value.length == format.length)&&(dojox.validate.isNumberFormat(value, flags))){
		return true; //Boolean
	}
	return false; //Boolean
}

}

if(!dojo._hasResource["dojox.layout.ContentPane"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.layout.ContentPane"] = true;
dojo.provide("dojox.layout.ContentPane");



(function(){ // private scope, sort of a namespace

	// TODO: should these methods be moved to dojox.html.cssPathAdjust or something?

	// css at-rules must be set before any css declarations according to CSS spec
	// match:
	// @import 'http://dojotoolkit.org/dojo.css';
	// @import 'you/never/thought/' print;
	// @import url("it/would/work") tv, screen;
	// @import url(/did/you/now.css);
	// but not:
	// @namespace dojo "http://dojotoolkit.org/dojo.css"; /* namespace URL should always be a absolute URI */
	// @charset 'utf-8';
	// @media print{ #menuRoot {display:none;} }

		
	// we adjust all paths that dont start on '/' or contains ':'
	//(?![a-z]+:|\/)

	if(dojo.isIE){
		var alphaImageLoader = /(AlphaImageLoader\([^)]*?src=(['"]))(?![a-z]+:|\/)([^\r\n;}]+?)(\2[^)]*\)\s*[;}]?)/g;
	}

	var cssPaths = /(?:(?:@import\s*(['"])(?![a-z]+:|\/)([^\r\n;{]+?)\1)|url\(\s*(['"]?)(?![a-z]+:|\/)([^\r\n;]+?)\3\s*\))([a-z, \s]*[;}]?)/g;

	function adjustCssPaths(cssUrl, cssText){
		//	summary:
		//		adjusts relative paths in cssText to be relative to cssUrl
		//		a path is considered relative if it doesn't start with '/' and not contains ':'
		//	description:
		//		Say we fetch a HTML page from level1/page.html
		//		It has some inline CSS:
		//			@import "css/page.css" tv, screen;
		//			...
		//			background-image: url(images/aplhaimage.png);
		//
		//		as we fetched this HTML and therefore this CSS
		//		from level1/page.html, these paths needs to be adjusted to:
		//			@import 'level1/css/page.css' tv, screen;
		//			...
		//			background-image: url(level1/images/alphaimage.png);
		//		
		//		In IE it will also adjust relative paths in AlphaImageLoader()
		//			filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='images/alphaimage.png');
		//		will be adjusted to:
		//			filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='level1/images/alphaimage.png');
		//
		//		Please note that any relative paths in AlphaImageLoader in external css files wont work, as
		//		the paths in AlphaImageLoader is MUST be declared relative to the HTML page,
		//		not relative to the CSS file that declares it

		if(!cssText || !cssUrl){ return; }

		// support the ImageAlphaFilter if it exists, most people use it in IE 6 for transparent PNGs
		// We are NOT going to kill it in IE 7 just because the PNGs work there. Somebody might have
		// other uses for it.
		// If user want to disable css filter in IE6  he/she should
		// unset filter in a declaration that just IE 6 doesn't understands
		// like * > .myselector { filter:none; }
		if(alphaImageLoader){
			cssText = cssText.replace(alphaImageLoader, function(ignore, pre, delim, url, post){
				return pre + (new dojo._Url(cssUrl, './'+url).toString()) + post;
			});
		}

		return cssText.replace(cssPaths, function(ignore, delimStr, strUrl, delimUrl, urlUrl, media){
			if(strUrl){
				return '@import "' + (new dojo._Url(cssUrl, './'+strUrl).toString()) + '"' + media;
			}else{
				return 'url(' + (new dojo._Url(cssUrl, './'+urlUrl).toString()) + ')' + media;
			}
		});
	}

	// attributepaths one tag can have multiple paths, example:
	// <input src="..." style="url(..)"/> or <a style="url(..)" href="..">
	// <img style='filter:progid...AlphaImageLoader(src="noticeTheSrcHereRunsThroughHtmlSrc")' src="img">
	var htmlAttrPaths = /(<[a-z][a-z0-9]*\s[^>]*)(?:(href|src)=(['"]?)([^>]*?)\3|style=(['"]?)([^>]*?)\5)([^>]*>)/gi;

	function adjustHtmlPaths(htmlUrl, cont){
		var url = htmlUrl || "./";

		return cont.replace(htmlAttrPaths,
			function(tag, start, name, delim, relUrl, delim2, cssText, end){
				return start + (name ?
							(name + '=' + delim + (new dojo._Url(url, relUrl).toString()) + delim)
						: ('style=' + delim2 + adjustCssPaths(url, cssText) + delim2)
				) + end;
			}
		);
	}

	function secureForInnerHtml(cont){
		/********* remove <!DOCTYPE.. and <title>..</title> tag **********/
		// khtml is picky about dom faults, you can't attach a <style> or <title> node as child of body
		// must go into head, so we need to cut out those tags
		return cont.replace(/(?:\s*<!DOCTYPE\s[^>]+>|<title[^>]*>[\s\S]*?<\/title>)/ig, "");
	}

	function snarfStyles(/*String*/cssUrl, /*String*/cont, /*Array*/styles){
		/****************  cut out all <style> and <link rel="stylesheet" href=".."> **************/
		// also return any attributes from this tag (might be a media attribute)
		// if cssUrl is set it will adjust paths accordingly
		styles.attributes = [];

		return cont.replace(/(?:<style([^>]*)>([\s\S]*?)<\/style>|<link\s+(?=[^>]*rel=['"]?stylesheet)([^>]*?href=(['"])([^>]*?)\4[^>\/]*)\/?>)/gi,
			function(ignore, styleAttr, cssText, linkAttr, delim, href){
				// trim attribute
				var i, attr = (styleAttr||linkAttr||"").replace(/^\s*([\s\S]*?)\s*$/i, "$1"); 
				if(cssText){
					i = styles.push(cssUrl ? adjustCssPaths(cssUrl, cssText) : cssText);
				}else{
					i = styles.push('@import "' + href + '";')
					attr = attr.replace(/\s*(?:rel|href)=(['"])?[^\s]*\1\s*/gi, ""); // remove rel=... and href=...
				}
				if(attr){
					attr = attr.split(/\s+/);// split on both "\n", "\t", " " etc
					var atObj = {}, tmp;
					for(var j = 0, e = attr.length; j < e; j++){
						tmp = attr[j].split('=')// split name='value'
						atObj[tmp[0]] = tmp[1].replace(/^\s*['"]?([\s\S]*?)['"]?\s*$/, "$1"); // trim and remove ''
					}
					styles.attributes[i - 1] = atObj;
				}
				return ""; // squelsh the <style> or <link>
			}
		);
	}

	function snarfScripts(cont, byRef){
		// summary
		//		strips out script tags from cont
		// invoke with 
		//	byRef = {errBack:function(){/*add your download error code here*/, downloadRemote: true(default false)}}
		//	byRef will have {code: 'jscode'} when this scope leaves
		byRef.code = "";

		function download(src){
			if(byRef.downloadRemote){
				// console.debug('downloading',src);
				dojo.xhrGet({
					url: src,
					sync: true,
					load: function(code){
						byRef.code += code+";";
					},
					error: byRef.errBack
				});
			}
		}
		
		// match <script>, <script type="text/..., but not <script type="dojo(/method)...
		return cont.replace(/<script\s*(?![^>]*type=['"]?dojo)(?:[^>]*?(?:src=(['"]?)([^>]*?)\1[^>]*)?)*>([\s\S]*?)<\/script>/gi,
			function(ignore, delim, src, code){
				if(src){
					download(src);
				}else{
					byRef.code += code;
				}
				return "";
			}
		);
	}

	function evalInGlobal(code, appendNode){
		// we do our own eval here as dojo.eval doesn't eval in global crossbrowser
		// This work X browser but but it relies on a DOM
		// plus it doesn't return anything, thats unrelevant here but not for dojo core
		appendNode = appendNode || dojo.doc.body;
		var n = appendNode.ownerDocument.createElement('script');
		n.type = "text/javascript";
		appendNode.appendChild(n);
		n.text = code; // DOM 1 says this should work
	}

	/*=====
	dojox.layout.ContentPane.DeferredHandle = {
		// cancel: Function
		cancel: function(){
			// summary: cancel a in flight download
		},

		addOnLoad: function(func){
			// summary: add a callback to the onLoad chain
			// func: Function
		},

		addOnUnload: function(func){
			// summary: add a callback to the onUnload chain
			// func: Function
		}
	}
	=====*/


dojo.declare("dojox.layout.ContentPane", dijit.layout.ContentPane, {
	// summary:
	//		An extended version of dijit.layout.ContentPane
	//		Supports infile scrips and external ones declared by <script src=''
	//		relative path adjustments (content fetched from a different folder)
	//		<style> and <link rel='stylesheet' href='..'> tags,
	//		css paths inside cssText is adjusted (if you set adjustPaths = true)
	//
	//		NOTE that dojo.require in script in the fetched file isn't recommended
	//		Many widgets need to be required at page load to work properly

	// adjustPaths: Boolean
	//		Adjust relative paths in html string content to point to this page
	//		Only usefull if you grab content from a another folder then the current one
	adjustPaths: false,

	// cleanContent: Boolean
	//	summary:
	//		cleans content to make it less likly to generate DOM/JS errors.
	//	description:
	//		usefull if you send contentpane a complete page, instead of a html fragment
	//		scans for 
	//			style nodes, inserts in Document head
	//			title Node, remove
	//			DOCTYPE tag, remove
	//			<!-- *JS code here* -->
	//			<![CDATA[ *JS code here* ]]>
	cleanContent: false,

	// renderStyles: Boolean
	//		trigger/load styles in the content
	renderStyles: false,

	// executeScripts: Boolean
	//		Execute (eval) scripts that is found in the content
	executeScripts: true,

	// scriptHasHooks: Boolean
	//		replace keyword '_container_' in scripts with 'dijit.byId(this.id)'
	// NOTE this name might change in the near future
	scriptHasHooks: false,

	/*======
	// ioMethod: dojo.xhrGet|dojo.xhrPost
	//		reference to the method that should grab the content
	ioMethod: dojo.xhrGet,
	
	// ioArgs: Object
	//		makes it possible to add custom args to xhrGet, like ioArgs.headers['X-myHeader'] = 'true'
	ioArgs: {},

	// onLoadDeferred: dojo.Deferred
	//		callbackchain will start when onLoad occurs
	onLoadDeferred: new dojo.Deferred(),

	// onUnloadDeferred: dojo.Deferred
	//		callbackchain will start when onUnload occurs
	onUnloadDeferred: new dojo.Deferred(),

	setHref: function(url){
		// summary: replace current content with url's content
		return ;// dojox.layout.ContentPane.DeferredHandle
	},

	refresh: function(){
		summary: force a re-download of content
		return ;// dojox.layout.ContentPane.DeferredHandle 
	},

	======*/

	constructor: function(){
		// init per instance properties, initializer doesn't work here because how things is hooked up in dijit._Widget
		this.ioArgs = {};
		this.ioMethod = dojo.xhrGet;
		this.onLoadDeferred = new dojo.Deferred();
		this.onUnloadDeferred = new dojo.Deferred();
	},

	postCreate: function(){
		// override to support loadDeferred
		this._setUpDeferreds();

		dijit.layout.ContentPane.prototype.postCreate.apply(this, arguments);
	},

	onExecError: function(e){
		// summary
		//		event callback, called on script error or on java handler error
		//		overide and return your own html string if you want a some text 
		//		displayed within the ContentPane
	},

	setContent: function(data){
		// summary: set data as new content, sort of like innerHTML
		// data: String|DomNode|NodeList|dojo.NodeList
		if(!this._isDownloaded){
			var defObj = this._setUpDeferreds();
		}

		dijit.layout.ContentPane.prototype.setContent.apply(this, arguments);
		return defObj; // dojox.layout.ContentPane.DeferredHandle
	},

	cancel: function(){
		// summary: cancels a inflight download
		if(this._xhrDfd && this._xhrDfd.fired == -1){
			// we are still in flight, which means we should reset our DeferredHandle
			// otherwise we will trigger onUnLoad chain of the canceled content,
			// the canceled content have never gotten onLoad so it shouldn't get onUnload
			this.onUnloadDeferred = null;
		}
		dijit.layout.ContentPane.prototype.cancel.apply(this, arguments);
	},

	_setUpDeferreds: function(){
		var _t = this, cancel = function(){ _t.cancel();	}
		var onLoad = (_t.onLoadDeferred = new dojo.Deferred());
		var onUnload = (_t._nextUnloadDeferred = new dojo.Deferred());
		return {
			cancel: cancel,
			addOnLoad: function(func){onLoad.addCallback(func);},
			addOnUnload: function(func){onUnload.addCallback(func);}
		};
	},

	_onLoadHandler: function(){
		dijit.layout.ContentPane.prototype._onLoadHandler.apply(this, arguments);
		if(this.onLoadDeferred){
			this.onLoadDeferred.callback(true);
		}
	},

	_onUnloadHandler: function(){
		this.isLoaded = false;
		this.cancel();// need to cancel so we don't get any inflight suprises
		if(this.onUnloadDeferred){
			this.onUnloadDeferred.callback(true);
		}

		dijit.layout.ContentPane.prototype._onUnloadHandler.apply(this, arguments);

		if(this._nextUnloadDeferred){
			this.onUnloadDeferred = this._nextUnloadDeferred;
		}
	},

	_onError: function(type, err){
		dijit.layout.ContentPane.prototype._onError.apply(this, arguments);
		if(this.onLoadDeferred){
			this.onLoadDeferred.errback(err);
		}
	},

	_prepareLoad: function(forceLoad){
		// sets up for a xhrLoad, load is deferred until widget is showing
		var defObj = this._setUpDeferreds();

		dijit.layout.ContentPane.prototype._prepareLoad.apply(this, arguments);

		return defObj;
	},

	_setContent: function(cont){
		// override dijit.layout.ContentPane._setContent, to enable path adjustments
		var styles = [];// init vars
		if(dojo.isString(cont)){
			if(this.adjustPaths && this.href){
				cont = adjustHtmlPaths(this.href, cont);
			}
			if(this.cleanContent){
				cont = secureForInnerHtml(cont);
			}
			if(this.renderStyles || this.cleanContent){
				cont = snarfStyles(this.href, cont, styles);
			}

			// because of a bug in IE, script tags that is first in html hierarchy doesnt make it into the DOM 
			//	when content is innerHTML'ed, so we can't use dojo.query to retrieve scripts from DOM
			if(this.executeScripts){
				var _t = this, code, byRef = {
					downloadRemote: true,
					errBack:function(e){
						_t._onError.call(_t, 'Exec', 'Error downloading remote script in "'+_t.id+'"', e);
					}
				};
				cont = snarfScripts(cont, byRef);
				code = byRef.code;
			}

			// rationale for this block:
			// if containerNode/domNode is a table derivate tag, some browsers dont allow innerHTML on those
			var node = (this.containerNode || this.domNode), pre = post = '', walk = 0;
			switch(name = node.nodeName.toLowerCase()){
				case 'tr':
					pre = '<tr>'; post = '</tr>';
					walk += 1;//fallthrough
				case 'tbody': case 'thead':// children of THEAD is of same type as TBODY
					pre = '<tbody>' + pre; post += '</tbody>';
					walk += 1;// falltrough
				case 'table':
					pre = '<table>' + pre; post += '</table>';
					walk += 1;
					break;
			}
			if(walk){
				var n = node.ownerDocument.createElement('div');
				n.innerHTML = pre + cont + post;
				do{
					n = n.firstChild;
				}while(--walk);
				cont = n.childNodes;
			}
		}

		// render the content
		dijit.layout.ContentPane.prototype._setContent.call(this, cont);

		// clear old stylenodes from the DOM
		if(this._styleNodes && this._styleNodes.length){
			while(this._styleNodes.length){
				dojo._destroyElement(this._styleNodes.pop());
			}
		}
		// render new style nodes
		if(this.renderStyles && styles && styles.length){
			this._renderStyles(styles);
		}

		if(this.executeScripts && code){
			if(this.cleanContent){
				// clean JS from html comments and other crap that browser
				// parser takes care of in a normal page load
				code = code.replace(/(<!--|(?:\/\/)?-->|<!\[CDATA\[|\]\]>)/g, '');
			}
			if(this.scriptHasHooks){
				// replace _container_ with dijit.byId(this.id)
				code = code.replace(/_container_(?!\s*=[^=])/g, "dijit.byId('"+this.id+"')");
			}
			try{
				evalInGlobal(code, (this.containerNode || this.domNode));
			}catch(e){
				this._onError('Exec', 'Error eval script in '+this.id+', '+e.message, e);
			}
		}
	},

	_renderStyles: function(styles){
		// insert css from content into document head
		this._styleNodes = [];
		var st, att, cssText, doc = this.domNode.ownerDocument;
		var head = doc.getElementsByTagName('head')[0];

		for(var i = 0, e = styles.length; i < e; i++){
			cssText = styles[i]; att = styles.attributes[i];
			st = doc.createElement('style');
			st.setAttribute("type", "text/css"); // this is required in CSS spec!

			for(var x in att){
				st.setAttribute(x, att[x])
			}
			
			this._styleNodes.push(st);
			head.appendChild(st); // must insert into DOM before setting cssText

			if(st.styleSheet){ // IE
				st.styleSheet.cssText = cssText;
			}else{ // w3c
				st.appendChild(doc.createTextNode(cssText));
			}
		}
	}
});

})();

}

if(!dojo._hasResource["dojox.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.Dialog"] = true;
dojo.provide("dojox.Dialog");




dojo.declare(
	"dojox.Dialog",
	[dojox.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary: A modal dialog Widget
		//
		// description:
		//	Pops up a modal dialog window, blocking access to the screen
		//	and also graying out the screen Dialog is extended from
		//	ContentPane so it supports all the same parameters (href, etc.)
		//
		// example:
		// |	<div dojoType="dijit.Dialog" href="test.html"></div>
		//
		// example:
		// |	<div id="test">test content</div>
		// |	...
		// |	var foo = new dijit.Dialog({ title: "test dialog" },dojo.byId("test"));
		// |	foo.startup();
		
		templateString: null,
		templateString:"<div class=\"dijitDialog\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dijitDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">\r\n\t<span dojoAttachPoint=\"titleNode\" class=\"dijitDialogTitle\">${title}</span>\r\n\t<span dojoAttachPoint=\"closeButtonNode\" class=\"dijitDialogCloseIcon\" dojoAttachEvent=\"onclick: hide\">\r\n\t\t<span dojoAttachPoint=\"closeText\" class=\"closeText\">x</span>\r\n\t</span>\r\n\t</div>\r\n\t\t<div dojoAttachPoint=\"containerNode\" class=\"dijitDialogPaneContent\"></div>\r\n\t<span dojoAttachPoint=\"tabEnd\" dojoAttachEvent=\"onfocus:_cycleFocus\" tabindex=\"0\"></span>\r\n</div>\r\n",

		// open: Boolean
		//		is True or False depending on state of dialog
		open: false,

		// duration: Integer
		//		The time in milliseconds it takes the dialog to fade in and out
		duration: 100,

		// _lastFocusItem: DomNode
		//		The pointer to which node has focus prior to our dialog
		_lastFocusItem:null,

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap), {title: "titleBar"}),

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.inherited("postCreate",arguments);
			this.domNode.style.display="none";
			this.connect(this, "onExecute", "hide");
			this.connect(this, "onCancel", "hide");
		},

		onLoad: function(){
			// summary: when href is specified we need to reposition the dialog after the data is loaded
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
			if(this._fadeIn.status() == "playing"){
				this._fadeIn.stop();
			}
			if(this._fadeOut.status() == "playing"){
				this._fadeOut.stop();
			}
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
			// summary: handles the keyboard events for accessibility reasons
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
			// summary: Hide the dialog

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
	[dojox.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a dialog that appears like a Tooltip
		//
		// title: String
		// 		Description of tooltip dialog (required for a11Y)
		title: "",

		// _lastFocusItem: DomNode
		//		The domNode that had focus before we took it.
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
			this.domNode.className="dijitTooltipDialog " +"dijitTooltipAB"+(corner.charAt(1)=='L'?"Left":"Right")+"dijitTooltip"+(corner.charAt(0)=='T' ? "Below" : "Above");
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

if(!dojo._hasResource["dojox.grid._grid.lib"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.lib"] = true;
dojo.provide("dojox.grid._grid.lib");

dojo.isNumber = function(v){
	return (typeof v == 'number') || (v instanceof Number);
}

// summary:
//	grid utility library

dojo.mixin(dojox.grid, {
	na: '...',
	nop: function() {
	},
	getTdIndex: function(td){
		return td.cellIndex >=0 ? td.cellIndex : dojo.indexOf(td.parentNode.cells, td);
	},
	getTrIndex: function(tr){
		return tr.rowIndex >=0 ? tr.rowIndex : dojo.indexOf(tr.parentNode.childNodes, tr);
	},
	getTr: function(rowOwner, index){
		return rowOwner && ((rowOwner.rows||0)[index] || rowOwner.childNodes[index]);
	},
	getTd: function(rowOwner, rowIndex, cellIndex){
		return (dojox.grid.getTr(inTable, rowIndex)||0)[cellIndex];
	},
	findTable: function(node){
		for (var n=node; n && n.tagName!='TABLE'; n=n.parentNode);
		return n;
	},
	ascendDom: function(inNode, inWhile){
		for (var n=inNode; n && inWhile(n); n=n.parentNode);
		return n;
	},
	makeNotTagName: function(inTagName){
		var name = inTagName.toUpperCase();
		return function(node){ return node.tagName != name; };
	},
	fire: function(ob, ev, args){
		var fn = ob && ev && ob[ev];
		return fn && (args ? fn.apply(ob, args) : ob[ev]());
	},
	// from lib.js
	setStyleText: function(inNode, inStyleText){
		if(inNode.style.cssText == undefined){
			inNode.setAttribute("style", inStyleText);
		}else{
			inNode.style.cssText = inStyleText;
		}
	},
	getStyleText: function(inNode, inStyleText){
		return (inNode.style.cssText == undefined ? inNode.getAttribute("style") : inNode.style.cssText);
	},
	setStyle: function(inElement, inStyle, inValue){
		if(inElement && inElement.style[inStyle] != inValue){
			inElement.style[inStyle] = inValue;
		}
	},
	setStyleHeightPx: function(inElement, inHeight){
		if(inHeight >= 0){
			dojox.grid.setStyle(inElement, 'height', inHeight + 'px');
		}
	},
	mouseEvents: [ 'mouseover', 'mouseout', /*'mousemove',*/ 'mousedown', 'mouseup', 'click', 'dblclick', 'contextmenu' ],
	keyEvents: [ 'keyup', 'keydown', 'keypress' ],
	funnelEvents: function(inNode, inObject, inMethod, inEvents){
		var evts = (inEvents ? inEvents : dojox.grid.mouseEvents.concat(dojox.grid.keyEvents));
		for (var i=0, l=evts.length; i<l; i++){
			dojo.connect(inNode, 'on' + evts[i], inObject, inMethod);
		}
	},
	removeNode: function(inNode){
		inNode = dojo.byId(inNode);
		inNode && inNode.parentNode && inNode.parentNode.removeChild(inNode);
		return inNode;
	},
	getScrollbarWidth: function(){
		if(this._scrollBarWidth){
			return this._scrollBarWidth;
		}
		this._scrollBarWidth = 18;
		try{
			var e = document.createElement("div");
			e.style.cssText = "top:0;left:0;width:100px;height:100px;overflow:scroll;position:absolute;visibility:hidden;";
			document.body.appendChild(e);
			this._scrollBarWidth = e.offsetWidth - e.clientWidth;
			document.body.removeChild(e);
			delete e;
		}catch (ex){}
		return this._scrollBarWidth;
	},
	// needed? dojo has _getProp
	getRef: function(name, create, context){
		var obj=context||dojo.global, parts=name.split("."), prop=parts.pop();
		for(var i=0, p; obj&&(p=parts[i]); i++){
			obj = (p in obj ? obj[p] : (create ? obj[p]={} : undefined));
		}
		return { obj: obj, prop: prop }; 
	},
	getProp: function(name, create, context){
		with(dojox.grid.getRef(name, create, context)){
			return (obj)&&(prop)&&(prop in obj ? obj[prop] : (create ? obj[prop]={} : undefined));
		}
	},
	indexInParent: function(inNode){
		var i=0, n, p=inNode.parentNode;
		while(n = p.childNodes[i++]){
			if(n == inNode){
				return i - 1;
			}
		}
		return -1;
	},
	cleanNode: function(inNode){
		if(!inNode){
			return;
		}
		var filter = function(inW){
			return inW.domNode && dojo.isDescendant(inW.domNode, inNode, true);
		}
		var ws = dijit.registry.filter(filter);
		for(var i=0, w; (w=ws[i]); i++){
			w.destroy();
		}
		delete ws;
	},
	getTagName: function(inNodeOrId){
		var node = dojo.byId(inNodeOrId);
		return (node && node.tagName ? node.tagName.toLowerCase() : '');
	},
	nodeKids: function(inNode, inTag){
		var result = [];
		var i=0, n;
		while(n = inNode.childNodes[i++]){
			if(dojox.grid.getTagName(n) == inTag){
				result.push(n);
			}
		}
		return result;
	},
	divkids: function(inNode){
		return dojox.grid.nodeKids(inNode, 'div');
	},
	focusSelectNode: function(inNode){
		try{
			dojox.grid.fire(inNode, "focus");
			dojox.grid.fire(inNode, "select");
		}catch(e){// IE sux bad
		}
	},
	whenIdle: function(/*inContext, inMethod, args ...*/){
		setTimeout(dojo.hitch.apply(dojo, arguments), 0);
	},
	arrayCompare: function(inA, inB){
		for(var i=0,l=inA.length; i<l; i++){
			if(inA[i] != inB[i]){return false;}
		}
		return (inA.length == inB.length);
	},
	arrayInsert: function(inArray, inIndex, inValue){
		if(inArray.length <= inIndex){
			inArray[inIndex] = inValue;
		}else{
			inArray.splice(inIndex, 0, inValue);
		}
	},
	arrayRemove: function(inArray, inIndex){
		inArray.splice(inIndex, 1);
	},
	arraySwap: function(inArray, inI, inJ){
		var cache = inArray[inI];
		inArray[inI] = inArray[inJ];
		inArray[inJ] = cache;
	},
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
				dojox.grid.textSizeChanged();
			}
		}
		window.setInterval(job, inInterval||200);
		dojox.grid.initTextSizePoll = dojox.grid.nop;
	},
	textSizeChanged: function() {
	}
});

dojox.grid.jobs = {
	cancel: function(inHandle){
		if(inHandle){
			window.clearTimeout(inHandle);
		}
	},
	jobs: [],
	job: function(inName, inDelay, inJob){
		dojox.grid.jobs.cancelJob(inName);
		var job = function(){
			delete dojox.grid.jobs.jobs[inName];
			inJob();
		}
		dojox.grid.jobs.jobs[inName] = setTimeout(job, inDelay);
	},
	cancelJob: function(inName){
		dojox.grid.jobs.cancel(dojox.grid.jobs.jobs[inName]);
	}
}

}

if(!dojo._hasResource['dojox.grid._grid.scroller']){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource['dojox.grid._grid.scroller'] = true;
dojo.provide('dojox.grid._grid.scroller');

dojo.declare('dojox.grid.scroller.base', null, {
	// summary:
	//	virtual scrollbox, abstract class
	//	Content must in /rows/
	//	Rows are managed in contiguous sets called /pages/
	//	There are a fixed # of rows per page
	//	The minimum rendered unit is a page
	constructor: function(){
		this.pageHeights = [];
		this.stack = [];
	},
	// specified
	rowCount: 0, // total number of rows to manage
	defaultRowHeight: 10, // default height of a row
	keepRows: 100, // maximum number of rows that should exist at one time
	contentNode: null, // node to contain pages
	scrollboxNode: null, // node that controls scrolling
	// calculated
	defaultPageHeight: 0, // default height of a page
	keepPages: 10, // maximum number of pages that should exists at one time
	pageCount: 0,
	windowHeight: 0,
	firstVisibleRow: 0,
	lastVisibleRow: 0,
	// private
	page: 0,
	pageTop: 0,
	// init
	init: function(inRowCount, inKeepRows, inRowsPerPage){
		switch(arguments.length){
			case 3: this.rowsPerPage = inRowsPerPage;
			case 2: this.keepRows = inKeepRows;
			case 1: this.rowCount = inRowCount;
		}
		this.defaultPageHeight = this.defaultRowHeight * this.rowsPerPage;
		//this.defaultPageHeight = this.defaultRowHeight * Math.min(this.rowsPerPage, this.rowCount);
		this.pageCount = Math.ceil(this.rowCount / this.rowsPerPage);
		this.keepPages = Math.max(Math.ceil(this.keepRows / this.rowsPerPage), 2);
		this.invalidate();
		if(this.scrollboxNode){
			this.scrollboxNode.scrollTop = 0;
			this.scroll(0);
			this.scrollboxNode.onscroll = dojo.hitch(this, 'onscroll');
		}
	},
	// updating
	invalidate: function(){
		this.invalidateNodes();
		this.pageHeights = [];
		this.height = (this.pageCount ? (this.pageCount - 1)* this.defaultPageHeight + this.calcLastPageHeight() : 0);
		this.resize();
	},
	updateRowCount: function(inRowCount){
		this.invalidateNodes();
		this.rowCount = inRowCount;
		// update page count, adjust document height
		oldPageCount = this.pageCount;
		this.pageCount = Math.ceil(this.rowCount / this.rowsPerPage);
		if(this.pageCount < oldPageCount){
			for(var i=oldPageCount-1; i>=this.pageCount; i--){
				this.height -= this.getPageHeight(i);
				delete this.pageHeights[i]
			}
		}else if(this.pageCount > oldPageCount){
			this.height += this.defaultPageHeight * (this.pageCount - oldPageCount - 1) + this.calcLastPageHeight();
		}
		this.resize();
	},
	// abstract interface
	pageExists: function(inPageIndex){
	},
	measurePage: function(inPageIndex){
	},
	positionPage: function(inPageIndex, inPos){
	},
	repositionPages: function(inPageIndex){
	},
	installPage: function(inPageIndex){
	},
	preparePage: function(inPageIndex, inPos, inReuseNode){
	},
	renderPage: function(inPageIndex){
	},
	removePage: function(inPageIndex){
	},
	pacify: function(inShouldPacify){
	},
	// pacification
	pacifying: false,
	pacifyTicks: 200,
	setPacifying: function(inPacifying){
		if(this.pacifying != inPacifying){
			this.pacifying = inPacifying;
			this.pacify(this.pacifying);
		}
	},
	startPacify: function(){
		this.startPacifyTicks = new Date().getTime();
	},
	doPacify: function(){
		var result = (new Date().getTime() - this.startPacifyTicks) > this.pacifyTicks;
		this.setPacifying(true);
		this.startPacify();
		return result;
	},
	endPacify: function(){
		this.setPacifying(false);
	},
	// default sizing implementation
	resize: function(){
		if(this.scrollboxNode){
			this.windowHeight = this.scrollboxNode.clientHeight;
		}
		dojox.grid.setStyleHeightPx(this.contentNode, this.height);
	},
	calcLastPageHeight: function(){
		if(!this.pageCount){
			return 0;
		}
		var lastPage = this.pageCount - 1;
		var lastPageHeight = ((this.rowCount % this.rowsPerPage)||(this.rowsPerPage)) * this.defaultRowHeight;
		this.pageHeights[lastPage] = lastPageHeight;
		return lastPageHeight;
	},
	updateContentHeight: function(inDh){
		this.height += inDh;
		this.resize();
	},
	updatePageHeight: function(inPageIndex){
		if(this.pageExists(inPageIndex)){
			var oh = this.getPageHeight(inPageIndex);
			var h = (this.measurePage(inPageIndex))||(oh);
			this.pageHeights[inPageIndex] = h;
			if((h)&&(oh != h)){
				this.updateContentHeight(h - oh)
				this.repositionPages(inPageIndex);
			}
		}
	},
	rowHeightChanged: function(inRowIndex){
		this.updatePageHeight(Math.floor(inRowIndex / this.rowsPerPage));
	},
	// scroller core
	invalidateNodes: function(){
		while(this.stack.length){
			this.destroyPage(this.popPage());
		}
	},
	createPageNode: function(){
		var p = document.createElement('div');
		p.style.position = 'absolute';
		//p.style.width = '100%';
		p.style.left = '0';
		return p;
	},
	getPageHeight: function(inPageIndex){
		var ph = this.pageHeights[inPageIndex];
		return (ph !== undefined ? ph : this.defaultPageHeight);
	},
	// FIXME: this is not a stack, it's a FIFO list
	pushPage: function(inPageIndex){
		return this.stack.push(inPageIndex);
	},
	popPage: function(){
		return this.stack.shift();
	},
	findPage: function(inTop){
		var i = 0, h = 0;
		for(var ph = 0; i<this.pageCount; i++, h += ph){
			ph = this.getPageHeight(i);
			if(h + ph >= inTop){
				break;
			}
		}
		this.page = i;
		this.pageTop = h;
	},
	buildPage: function(inPageIndex, inReuseNode, inPos){
		this.preparePage(inPageIndex, inReuseNode);
		this.positionPage(inPageIndex, inPos);
		// order of operations is key below
		this.installPage(inPageIndex);
		this.renderPage(inPageIndex);
		// order of operations is key above
		this.pushPage(inPageIndex);
	},
	needPage: function(inPageIndex, inPos){
		var h = this.getPageHeight(inPageIndex), oh = h;
		if(!this.pageExists(inPageIndex)){
			this.buildPage(inPageIndex, (this.keepPages)&&(this.stack.length >= this.keepPages), inPos);
			h = this.measurePage(inPageIndex) || h;
			this.pageHeights[inPageIndex] = h;
			if(h && (oh != h)){
				this.updateContentHeight(h - oh)
			}
		}else{
			this.positionPage(inPageIndex, inPos);
		}
		return h;
	},
	onscroll: function(){
		this.scroll(this.scrollboxNode.scrollTop);
	},
	scroll: function(inTop){
		this.startPacify();
		this.findPage(inTop);
		var h = this.height;
		var b = this.getScrollBottom(inTop);
		for(var p=this.page, y=this.pageTop; (p<this.pageCount)&&((b<0)||(y<b)); p++){
			y += this.needPage(p, y);
		}
		this.firstVisibleRow = this.getFirstVisibleRow(this.page, this.pageTop, inTop);
		this.lastVisibleRow = this.getLastVisibleRow(p - 1, y, b);
		// indicates some page size has been updated
		if(h != this.height){
			this.repositionPages(p-1);
		}
		this.endPacify();
	},
	getScrollBottom: function(inTop){
		return (this.windowHeight >= 0 ? inTop + this.windowHeight : -1);
	},
	// events
	processNodeEvent: function(e, inNode){
		var t = e.target;
		while(t && (t != inNode) && t.parentNode && (t.parentNode.parentNode != inNode)){
			t = t.parentNode;
		}
		if(!t || !t.parentNode || (t.parentNode.parentNode != inNode)){
			return false;
		}
		var page = t.parentNode;
		e.topRowIndex = page.pageIndex * this.rowsPerPage;
		e.rowIndex = e.topRowIndex + dojox.grid.indexInParent(t);
		e.rowTarget = t;
		return true;
	},
	processEvent: function(e){
		return this.processNodeEvent(e, this.contentNode);
	},
	dummy: 0
});

dojo.declare('dojox.grid.scroller', dojox.grid.scroller.base, {
	// summary:
	//	virtual scroller class, makes no assumption about shape of items being scrolled
	constructor: function(){
		this.pageNodes = [];
	},
	// virtual rendering interface
	renderRow: function(inRowIndex, inPageNode){
	},
	removeRow: function(inRowIndex){
	},
	// page node operations
	getDefaultNodes: function(){
		return this.pageNodes;
	},
	getDefaultPageNode: function(inPageIndex){
		return this.getDefaultNodes()[inPageIndex];
	},
	positionPageNode: function(inNode, inPos){
		inNode.style.top = inPos + 'px';
	},
	getPageNodePosition: function(inNode){
		return inNode.offsetTop;
	},
	repositionPageNodes: function(inPageIndex, inNodes){
		var last = 0;
		for(var i=0; i<this.stack.length; i++){
			last = Math.max(this.stack[i], last);
		}
		//
		var n = inNodes[inPageIndex];
		var y = (n ? this.getPageNodePosition(n) + this.getPageHeight(inPageIndex) : 0);
		//console.log('detected height change, repositioning from #%d (%d) @ %d ', inPageIndex + 1, last, y, this.pageHeights[0]);
		//
		for(var p=inPageIndex+1; p<=last; p++){
			n = inNodes[p];
			if(n){
				//console.log('#%d @ %d', inPageIndex, y, this.getPageNodePosition(n));
				if(this.getPageNodePosition(n) == y){
					return;
				}
				//console.log('placing page %d at %d', p, y);
				this.positionPage(p, y);
			}
			y += this.getPageHeight(p);
		}
	},
	invalidatePageNode: function(inPageIndex, inNodes){
		var p = inNodes[inPageIndex];
		if(p){
			delete inNodes[inPageIndex];
			this.removePage(inPageIndex, p);
			dojox.grid.cleanNode(p);
			p.innerHTML = '';
		}
		return p;
	},
	preparePageNode: function(inPageIndex, inReusePageIndex, inNodes){
		var p = (inReusePageIndex === null ? this.createPageNode() : this.invalidatePageNode(inReusePageIndex, inNodes));
		p.pageIndex = inPageIndex;
		p.id = 'page-' + inPageIndex;
		inNodes[inPageIndex] = p;
	},
	// implementation for page manager
	pageExists: function(inPageIndex){
		return Boolean(this.getDefaultPageNode(inPageIndex));
	},
	measurePage: function(inPageIndex){
		return this.getDefaultPageNode(inPageIndex).offsetHeight;
	},
	positionPage: function(inPageIndex, inPos){
		this.positionPageNode(this.getDefaultPageNode(inPageIndex), inPos);
	},
	repositionPages: function(inPageIndex){
		this.repositionPageNodes(inPageIndex, this.getDefaultNodes());
	},
	preparePage: function(inPageIndex, inReuseNode){
		this.preparePageNode(inPageIndex, (inReuseNode ? this.popPage() : null), this.getDefaultNodes());
	},
	installPage: function(inPageIndex){
		this.contentNode.appendChild(this.getDefaultPageNode(inPageIndex));
	},
	destroyPage: function(inPageIndex){
		var p = this.invalidatePageNode(inPageIndex, this.getDefaultNodes());
		dojox.grid.removeNode(p);
	},
	// rendering implementation
	renderPage: function(inPageIndex){
		var node = this.pageNodes[inPageIndex];
		for(var i=0, j=inPageIndex*this.rowsPerPage; (i<this.rowsPerPage)&&(j<this.rowCount); i++, j++){
			this.renderRow(j, node);
		}
	},
	removePage: function(inPageIndex){
		for(var i=0, j=inPageIndex*this.rowsPerPage; i<this.rowsPerPage; i++, j++){
			this.removeRow(j);
		}
	},
	// scroll control
	getPageRow: function(inPage){
		return inPage * this.rowsPerPage;
	},
	getLastPageRow: function(inPage){
		return Math.min(this.rowCount, this.getPageRow(inPage + 1)) - 1;
	},
	getFirstVisibleRowNodes: function(inPage, inPageTop, inScrollTop, inNodes){
		var row = this.getPageRow(inPage);
		var rows = dojox.grid.divkids(inNodes[inPage]);
		for(var i=0,l=rows.length; i<l && inPageTop<inScrollTop; i++, row++){
			inPageTop += rows[i].offsetHeight;
		}
		return (row ? row - 1 : row);
	},
	getFirstVisibleRow: function(inPage, inPageTop, inScrollTop){
		if(!this.pageExists(inPage)){
			return 0;
		}
		return this.getFirstVisibleRowNodes(inPage, inPageTop, inScrollTop, this.getDefaultNodes());
	},
	getLastVisibleRowNodes: function(inPage, inBottom, inScrollBottom, inNodes){
		var row = this.getLastPageRow(inPage);
		var rows = dojox.grid.divkids(inNodes[inPage]);
		for(var i=rows.length-1; i>=0 && inBottom>inScrollBottom; i--, row--){
			inBottom -= rows[i].offsetHeight;
		}
		return row + 1;
	},
	getLastVisibleRow: function(inPage, inBottom, inScrollBottom){
		if(!this.pageExists(inPage)){
			return 0;
		}
		return this.getLastVisibleRowNodes(inPage, inBottom, inScrollBottom, this.getDefaultNodes());
	},
	findTopRowForNodes: function(inScrollTop, inNodes){
		var rows = dojox.grid.divkids(inNodes[this.page]);
		for(var i=0,l=rows.length,t=this.pageTop,h; i<l; i++){
			h = rows[i].offsetHeight;
			t += h;
			if(t >= inScrollTop){
				this.offset = h - (t - inScrollTop);
				return i + this.page * this.rowsPerPage;
			}
		}
		return -1;
	},
	findScrollTopForNodes: function(inRow, inNodes){
		var rowPage = Math.floor(inRow / this.rowsPerPage);
		var t = 0;
		for(var i=0; i<rowPage; i++){
			t += this.getPageHeight(i);
		}
		this.pageTop = t;
		this.needPage(rowPage, this.pageTop);
		var rows = dojox.grid.divkids(inNodes[rowPage]);
		var r = inRow - this.rowsPerPage * rowPage;
		for(var i=0,l=rows.length; i<l && i<r; i++){
			t += rows[i].offsetHeight;
		}
		return t;
	},
	findTopRow: function(inScrollTop){
		return this.findTopRowForNodes(inScrollTop, this.getDefaultNodes());
	},
	findScrollTop: function(inRow){
		return this.findScrollTopForNodes(inRow, this.getDefaultNodes());
	},
	dummy: 0
});

dojo.declare('dojox.grid.scroller.columns', dojox.grid.scroller, {
	// summary:
	//	Virtual scroller class that scrolls list of columns. Owned by grid and used internally
	//	for virtual scrolling.
	constructor: function(inContentNodes){
		this.setContentNodes(inContentNodes);
	},
	// nodes
	setContentNodes: function(inNodes){
		this.contentNodes = inNodes;
		this.colCount = (this.contentNodes ? this.contentNodes.length : 0);
		this.pageNodes = [];
		for(var i=0; i<this.colCount; i++){
			this.pageNodes[i] = [];
		}
	},
	getDefaultNodes: function(){
		return this.pageNodes[0] || [];
	},
	scroll: function(inTop) {
		if(this.colCount){
			dojox.grid.scroller.prototype.scroll.call(this, inTop);
		}
	},
	// resize
	resize: function(){
		if(this.scrollboxNode){
			this.windowHeight = this.scrollboxNode.clientHeight;
		}
		for(var i=0; i<this.colCount; i++){
			dojox.grid.setStyleHeightPx(this.contentNodes[i], this.height);
		}
	},
	// implementation for page manager
	positionPage: function(inPageIndex, inPos){
		for(var i=0; i<this.colCount; i++){
			this.positionPageNode(this.pageNodes[i][inPageIndex], inPos);
		}
	},
	preparePage: function(inPageIndex, inReuseNode){
		var p = (inReuseNode ? this.popPage() : null);
		for(var i=0; i<this.colCount; i++){
			this.preparePageNode(inPageIndex, p, this.pageNodes[i]);
		}
	},
	installPage: function(inPageIndex){
		for(var i=0; i<this.colCount; i++){
			this.contentNodes[i].appendChild(this.pageNodes[i][inPageIndex]);
		}
	},
	destroyPage: function(inPageIndex){
		for(var i=0; i<this.colCount; i++){
			dojox.grid.removeNode(this.invalidatePageNode(inPageIndex, this.pageNodes[i]));
		}
	},
	// rendering implementation
	renderPage: function(inPageIndex){
		var nodes = [];
		for(var i=0; i<this.colCount; i++){
			nodes[i] = this.pageNodes[i][inPageIndex];
		}
		//this.renderRows(inPageIndex*this.rowsPerPage, this.rowsPerPage, nodes);
		for(var i=0, j=inPageIndex*this.rowsPerPage; (i<this.rowsPerPage)&&(j<this.rowCount); i++, j++){
			this.renderRow(j, nodes);
		}
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.drag"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.drag"] = true;
dojo.provide("dojox.grid._grid.drag");

// summary:
//	utility functions for dragging as used in grid.
// begin closure
(function(){

var dgdrag = dojox.grid.drag = {};

dgdrag.dragging = false;
dgdrag.hysteresis = 2;

dgdrag.capture = function(inElement) {
	//console.debug('dojox.grid.drag.capture');
	if (inElement.setCapture)
		inElement.setCapture();
	else {
		document.addEventListener("mousemove", inElement.onmousemove, true);
		document.addEventListener("mouseup", inElement.onmouseup, true);
		document.addEventListener("click", inElement.onclick, true);
	}
}

dgdrag.release = function(inElement) {
	//console.debug('dojox.grid.drag.release');
	if(inElement.releaseCapture){
		inElement.releaseCapture();
	}else{
		document.removeEventListener("click", inElement.onclick, true);
		document.removeEventListener("mouseup", inElement.onmouseup, true);
		document.removeEventListener("mousemove", inElement.onmousemove, true);
	}
}

dgdrag.start = function(inElement, inOnDrag, inOnEnd, inEvent, inOnStart){
	if(/*dgdrag.elt ||*/ !inElement || dgdrag.dragging){
		console.debug('failed to start drag: bad input node or already dragging');
		return;
	}
	dgdrag.dragging = true;
	dgdrag.elt = inElement;
	dgdrag.events = {
		drag: inOnDrag || dojox.grid.nop, 
		end: inOnEnd || dojox.grid.nop, 
		start: inOnStart || dojox.grid.nop, 
		oldmove: inElement.onmousemove, 
		oldup: inElement.onmouseup, 
		oldclick: inElement.onclick 
	};
	dgdrag.positionX = (inEvent && ('screenX' in inEvent) ? inEvent.screenX : false);
	dgdrag.positionY = (inEvent && ('screenY' in inEvent) ? inEvent.screenY : false);
	dgdrag.started = (dgdrag.position === false);
	inElement.onmousemove = dgdrag.mousemove;
	inElement.onmouseup = dgdrag.mouseup;
	inElement.onclick = dgdrag.click;
	dgdrag.capture(dgdrag.elt);
}

dgdrag.end = function(){
	//console.debug("dojox.grid.drag.end");
	dgdrag.release(dgdrag.elt);
	dgdrag.elt.onmousemove = dgdrag.events.oldmove;
	dgdrag.elt.onmouseup = dgdrag.events.oldup;
	dgdrag.elt.onclick = dgdrag.events.oldclick;
	dgdrag.elt = null;
	try{
		if(dgdrag.started){
			dgdrag.events.end();
		}
	}finally{
		dgdrag.dragging = false;
	}
}

dgdrag.calcDelta = function(inEvent){
	inEvent.deltaX = inEvent.screenX - dgdrag.positionX;
	inEvent.deltaY = inEvent.screenY - dgdrag.positionY;
}

dgdrag.hasMoved = function(inEvent){
	return Math.abs(inEvent.deltaX) + Math.abs(inEvent.deltaY) > dgdrag.hysteresis;
}

dgdrag.mousemove = function(inEvent){
	inEvent = dojo.fixEvent(inEvent);
	dojo.stopEvent(inEvent);
	dgdrag.calcDelta(inEvent);
	if((!dgdrag.started)&&(dgdrag.hasMoved(inEvent))){
		dgdrag.events.start(inEvent);
		dgdrag.started = true;
	}
	if(dgdrag.started){
		dgdrag.events.drag(inEvent);
	}
}

dgdrag.mouseup = function(inEvent){
	//console.debug("dojox.grid.drag.mouseup");
	dojo.stopEvent(dojo.fixEvent(inEvent));
	dgdrag.end();
}

dgdrag.click = function(inEvent){
	dojo.stopEvent(dojo.fixEvent(inEvent));
	//dgdrag.end();
}

})();
// end closure

}

if(!dojo._hasResource["dojox.grid._grid.builder"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.builder"] = true;
dojo.provide("dojox.grid._grid.builder");


dojo.declare("dojox.grid.Builder", null, {
	// summary:
	//	Base class to produce html for grid content.
	//	Also provide event decoration, providing grid related information inside the event object
	// passed to grid events.
	constructor: function(inView){
		this.view = inView;
		this.grid = inView.grid;
	},
	view: null,
	// boilerplate HTML
	_table: '<table class="dojoxGrid-row-table" border="0" cellspacing="0" cellpadding="0" role="wairole:presentation">',
	// generate starting tags for a cell
	generateCellMarkup: function(inCell, inMoreStyles, inMoreClasses, isHeader){
		var result = [], html;
		if (isHeader){
			html = [ '<th tabIndex="-1" role="wairole:columnheader"' ];
		}else{
			html = [ '<td tabIndex="-1" role="wairole:gridcell"' ];
		}
		inCell.colSpan && html.push(' colspan="', inCell.colSpan, '"');
		inCell.rowSpan && html.push(' rowspan="', inCell.rowSpan, '"');
		html.push(' class="dojoxGrid-cell ');
		inCell.classes && html.push(inCell.classes, ' ');
		inMoreClasses && html.push(inMoreClasses, ' ');
		// result[0] => td opener, style
		result.push(html.join(''));
		// SLOT: result[1] => td classes 
		result.push('');
		html = ['" idx="', inCell.index, '" style="'];
		html.push(inCell.styles, inMoreStyles||'');
		inCell.unitWidth && html.push('width:', inCell.unitWidth, ';');
		// result[2] => markup
		result.push(html.join(''));
		// SLOT: result[3] => td style 
		result.push('');
		html = [ '"' ];
		inCell.attrs && html.push(" ", inCell.attrs);
		html.push('>');
		// result[4] => td postfix
		result.push(html.join(''));
		// SLOT: result[5] => content
		result.push('');
		// result[6] => td closes
		result.push('</td>');
		return result;
	},
	// cell finding
	isCellNode: function(inNode){
		return Boolean(inNode && inNode.getAttribute && inNode.getAttribute("idx"));
	},
	getCellNodeIndex: function(inCellNode){
		return inCellNode ? Number(inCellNode.getAttribute("idx")) : -1;
	},
	getCellNode: function(inRowNode, inCellIndex){
		for(var i=0, row; row=dojox.grid.getTr(inRowNode.firstChild, i); i++){
			for(var j=0, cell; cell=row.cells[j]; j++){
				if(this.getCellNodeIndex(cell) == inCellIndex){
					return cell;
				}
			}
		}
	},
	findCellTarget: function(inSourceNode, inTopNode){
		var n = inSourceNode;
		while(n && !this.isCellNode(n) && (n!=inTopNode)){
			n = n.parentNode;
		}
		return n!=inTopNode ? n : null 
	},
	// event decoration
	baseDecorateEvent: function(e){
		e.dispatch = 'do' + e.type;
		e.grid = this.grid;
		e.sourceView = this.view;
		e.cellNode = this.findCellTarget(e.target, e.rowNode);
		e.cellIndex = this.getCellNodeIndex(e.cellNode);
		e.cell = (e.cellIndex >= 0 ? this.grid.getCell(e.cellIndex) : null);
	},
	// event dispatch
	findTarget: function(inSource, inTag){
		var n = inSource;
		while(n && !(inTag in n) && (n!=this.domNode)){
			n = n.parentNode;
		}
		return (n != this.domNode) ? n : null; 
	},
	findRowTarget: function(inSource){
		return this.findTarget(inSource, dojox.grid.rowIndexTag);
	},
	isIntraNodeEvent: function(e){
		try{
			return (e.cellNode && e.relatedTarget && dojo.isDescendant(e.relatedTarget, e.cellNode));
		}catch(x){
			// e.relatedTarget has permission problem in FF if it's an input: https://bugzilla.mozilla.org/show_bug.cgi?id=208427
			return false;
		}
	},
	isIntraRowEvent: function(e){
		try{
			var row = e.relatedTarget && this.findRowTarget(e.relatedTarget);
			return !row && (e.rowIndex==-1) || row && (e.rowIndex==row.gridRowIndex);			
		}catch(x){
			// e.relatedTarget on INPUT has permission problem in FF: https://bugzilla.mozilla.org/show_bug.cgi?id=208427
			return false;
		}
	},
	dispatchEvent: function(e){
		if(e.dispatch in this){
			return this[e.dispatch](e);
		}
	},
	// dispatched event handlers
	domouseover: function(e){
		if(e.cellNode && (e.cellNode!=this.lastOverCellNode)){
			this.lastOverCellNode = e.cellNode;
			this.grid.onMouseOver(e);
		}
		this.grid.onMouseOverRow(e);
	},
	domouseout: function(e){
		if(e.cellNode && (e.cellNode==this.lastOverCellNode) && !this.isIntraNodeEvent(e, this.lastOverCellNode)){
			this.lastOverCellNode = null;
			this.grid.onMouseOut(e);
			if(!this.isIntraRowEvent(e)){
				this.grid.onMouseOutRow(e);
			}
		}
	}
});

dojo.declare("dojox.grid.contentBuilder", dojox.grid.Builder, {
	// summary:
	//	Produces html for grid data content. Owned by grid and used internally 
	//	for rendering data. Override to implement custom rendering.
	update: function(){
		this.prepareHtml();
	},
	// cache html for rendering data rows
	prepareHtml: function(){
		var defaultGet=this.grid.get, rows=this.view.structure.rows;
		for(var j=0, row; (row=rows[j]); j++){
			for(var i=0, cell; (cell=row[i]); i++){
				cell.get = cell.get || (cell.value == undefined) && defaultGet;
				cell.markup = this.generateCellMarkup(cell, cell.cellStyles, cell.cellClasses, false);
			}
		}
	},
	// time critical: generate html using cache and data source
	generateHtml: function(inDataIndex, inRowIndex){
		var
			html = [ this._table ],
			v = this.view,
			obr = v.onBeforeRow,
			rows = v.structure.rows;
		obr && obr(inRowIndex, rows);
		for(var j=0, row; (row=rows[j]); j++){
			if(row.hidden || row.header){
				continue;
			}
			html.push(!row.invisible ? '<tr>' : '<tr class="dojoxGrid-invisible">');
			for(var i=0, cell, m, cc, cs; (cell=row[i]); i++){
				m = cell.markup, cc = cell.customClasses = [], cs = cell.customStyles = [];
				// content (format can fill in cc and cs as side-effects)
				m[5] = cell.format(inDataIndex);
				// classes
				m[1] = cc.join(' ');
				// styles
				m[3] = cs.join(';');
				// in-place concat
				html.push.apply(html, m);
			}
			html.push('</tr>');
		}
		html.push('</table>');
		return html.join('');
	},
	decorateEvent: function(e){
		e.rowNode = this.findRowTarget(e.target);
		if(!e.rowNode){return false};
		e.rowIndex = e.rowNode[dojox.grid.rowIndexTag];
		this.baseDecorateEvent(e);
		e.cell = this.grid.getCell(e.cellIndex);
		return true;
	}
});

dojo.declare("dojox.grid.headerBuilder", dojox.grid.Builder, {
	// summary:
	//	Produces html for grid header content. Owned by grid and used internally 
	//	for rendering data. Override to implement custom rendering.
	bogusClickTime: 0,
	overResizeWidth: 4,
	minColWidth: 1,
	_table: '<table class="dojoxGrid-row-table" border="0" cellspacing="0" cellpadding="0" role="wairole:presentation"',
	update: function(){
		this.tableMap = new dojox.grid.tableMap(this.view.structure.rows);
	},
	generateHtml: function(inGetValue, inValue){
		var html = [this._table], rows = this.view.structure.rows;
		
		// render header with appropriate width, if possible so that views with flex columns are correct height
		if(this.view.viewWidth){
			html.push([' style="width:', this.view.viewWidth, ';"'].join(''));
		}
		html.push('>');
		dojox.grid.fire(this.view, "onBeforeRow", [-1, rows]);
		for(var j=0, row; (row=rows[j]); j++){
			if(row.hidden){
				continue;
			}
			html.push(!row.invisible ? '<tr>' : '<tr class="dojoxGrid-invisible">');
			for(var i=0, cell, markup; (cell=row[i]); i++){
				cell.customClasses = [];
				cell.customStyles = [];
				markup = this.generateCellMarkup(cell, cell.headerStyles, cell.headerClasses, true);
				// content
				markup[5] = (inValue != undefined ? inValue : inGetValue(cell));
				// styles
				markup[3] = cell.customStyles.join(';');
				// classes
				markup[1] = cell.customClasses.join(' '); //(cell.customClasses ? ' ' + cell.customClasses : '');
				html.push(markup.join(''));
			}
			html.push('</tr>');
		}
		html.push('</table>');
		return html.join('');
	},
	// event helpers
	getCellX: function(e){
		var x = e.layerX;
		if(dojo.isMoz){
			var n = dojox.grid.ascendDom(e.target, dojox.grid.makeNotTagName("th"));
			x -= (n && n.offsetLeft) || 0;
			//x -= getProp(ascendDom(e.target, mkNotTagName("td")), "offsetLeft") || 0;
		}
		var n = dojox.grid.ascendDom(e.target, function(){
			if(!n || n == e.cellNode){
				return false;
			}
			// Mozilla 1.8 (FF 1.5) has a bug that makes offsetLeft = -parent border width
			// when parent has border, overflow: hidden, and is positioned
			// handle this problem here ... not a general solution!
			x += (n.offsetLeft < 0 ? 0 : n.offsetLeft);
			return true;
		});
		return x;
	},
	// event decoration
	decorateEvent: function(e){
		this.baseDecorateEvent(e);
		e.rowIndex = -1;
		e.cellX = this.getCellX(e);
		return true;
	},
	// event handlers
	// resizing
	prepareLeftResize: function(e){
		var i = dojox.grid.getTdIndex(e.cellNode);
		e.cellNode = (i ? e.cellNode.parentNode.cells[i-1] : null);
		e.cellIndex = (e.cellNode ? this.getCellNodeIndex(e.cellNode) : -1);
		return Boolean(e.cellNode);
	},
	canResize: function(e){
		if(!e.cellNode || e.cellNode.colSpan > 1){
			return false;
		}
		var cell = this.grid.getCell(e.cellIndex); 
		return !cell.noresize && !cell.isFlex();
	},
	overLeftResizeArea: function(e){
		return (e.cellIndex>0) && (e.cellX < this.overResizeWidth) && this.prepareLeftResize(e);
	},
	overRightResizeArea: function(e){
		return e.cellNode && (e.cellX >= e.cellNode.offsetWidth - this.overResizeWidth);
	},
	domousemove: function(e){
		//console.log(e.cellIndex, e.cellX, e.cellNode.offsetWidth);
		var c = (this.overRightResizeArea(e) ? 'e-resize' : (this.overLeftResizeArea(e) ? 'w-resize' : ''));
		if(c && !this.canResize(e)){
			c = 'not-allowed';
		}
		e.sourceView.headerNode.style.cursor = c || ''; //'default';
	},
	domousedown: function(e){
		if(!dojox.grid.drag.dragging){
			if((this.overRightResizeArea(e) || this.overLeftResizeArea(e)) && this.canResize(e)){
				this.beginColumnResize(e);
			}
			//else{
			//	this.beginMoveColumn(e);
			//}
		}
	},
	doclick: function(e) {
		if (new Date().getTime() < this.bogusClickTime) {
			dojo.stopEvent(e);
			return true;
		}
	},
	// column resizing
	beginColumnResize: function(e){
		dojo.stopEvent(e);
		var spanners = [], nodes = this.tableMap.findOverlappingNodes(e.cellNode);
		for(var i=0, cell; (cell=nodes[i]); i++){
			spanners.push({ node: cell, index: this.getCellNodeIndex(cell), width: cell.offsetWidth });
			//console.log("spanner: " + this.getCellNodeIndex(cell));
		}
		var drag = {
			view: e.sourceView,
			node: e.cellNode,
			index: e.cellIndex,
			w: e.cellNode.clientWidth,
			spanners: spanners
		};
		//console.log(drag.index, drag.w);
		dojox.grid.drag.start(e.cellNode, dojo.hitch(this, 'doResizeColumn', drag), dojo.hitch(this, 'endResizeColumn', drag), e);
	},
	doResizeColumn: function(inDrag, inEvent){
		var w = inDrag.w + inEvent.deltaX;
		if(w >= this.minColWidth){
			for(var i=0, s, sw; (s=inDrag.spanners[i]); i++){
				sw = s.width + inEvent.deltaX;
				s.node.style.width = sw + 'px';
				inDrag.view.setColWidth(s.index, sw);
				//console.log('setColWidth', '#' + s.index, sw + 'px');
			}
			inDrag.node.style.width = w + 'px';
			inDrag.view.setColWidth(inDrag.index, w);
		}
		if(inDrag.view.flexCells && !inDrag.view.testFlexCells()){
			var t = dojox.grid.findTable(inDrag.node);
			t && (t.style.width = '');
		}
	},
	endResizeColumn: function(inDrag){
		this.bogusClickTime = new Date().getTime() + 30;
		setTimeout(dojo.hitch(inDrag.view, "update"), 50);
	}
});

dojo.declare("dojox.grid.tableMap", null, {
	// summary:
	//	Maps an html table into a structure parsable for information about cell row and col spanning.
	//	Used by headerBuilder
	constructor: function(inRows){
		this.mapRows(inRows);
	},
	map: null,
	// map table topography
	mapRows: function(inRows){
		//console.log('mapRows');
		// # of rows
		var rowCount = inRows.length;
		if(!rowCount){
			return;
		}
		// map which columns and rows fill which cells
		this.map = [ ];
		for(var j=0, row; (row=inRows[j]); j++){
			this.map[j] = [];
		}
		for(var j=0, row; (row=inRows[j]); j++){
			for(var i=0, x=0, cell, colSpan, rowSpan; (cell=row[i]); i++){
				while (this.map[j][x]){x++};
				this.map[j][x] = { c: i, r: j };
				rowSpan = cell.rowSpan || 1;
				colSpan = cell.colSpan || 1;
				for(var y=0; y<rowSpan; y++){
					for(var s=0; s<colSpan; s++){
						this.map[j+y][x+s] = this.map[j][x];
					}
				}
				x += colSpan;
			}
		}
		//this.dumMap();
	},
	dumpMap: function(){
		for(var j=0, row, h=''; (row=this.map[j]); j++,h=''){
			for(var i=0, cell; (cell=row[i]); i++){
				h += cell.r + ',' + cell.c + '   ';
			}
			console.log(h);
		}
	},
	// find node's map coords by it's structure coords
	getMapCoords: function(inRow, inCol){
		for(var j=0, row; (row=this.map[j]); j++){
			for(var i=0, cell; (cell=row[i]); i++){
				if(cell.c==inCol && cell.r == inRow){
					return { j: j, i: i };
				}
				//else{console.log(inRow, inCol, ' : ', i, j, " : ", cell.r, cell.c); };
			}
		}
		return { j: -1, i: -1 };
	},
	// find a node in inNode's table with the given structure coords
	getNode: function(inTable, inRow, inCol){
		var row = inTable && inTable.rows[inRow];
		return row && row.cells[inCol];
	},
	_findOverlappingNodes: function(inTable, inRow, inCol){
		var nodes = [];
		var m = this.getMapCoords(inRow, inCol);
		//console.log("node j: %d, i: %d", m.j, m.i);
		var row = this.map[m.j];
		for(var j=0, row; (row=this.map[j]); j++){
			if(j == m.j){ continue; }
			with(row[m.i]){
				//console.log("overlaps: r: %d, c: %d", r, c);
				var n = this.getNode(inTable, r, c);
				if(n){ nodes.push(n); }
			}
		}
		//console.log(nodes);
		return nodes;
	},
	findOverlappingNodes: function(inNode){
		return this._findOverlappingNodes(dojox.grid.findTable(inNode), dojox.grid.getTrIndex(inNode.parentNode), dojox.grid.getTdIndex(inNode));
	}
});

dojox.grid.rowIndexTag = "gridRowIndex";

}

if(!dojo._hasResource["dojox.grid._grid.view"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.view"] = true;
dojo.provide("dojox.grid._grid.view");




dojo.declare('dojox.GridView', [dijit._Widget, dijit._Templated], {
	// summary:
	//	A collection of grid columns. A grid is comprised of a set of views that stack horizontally.
	//	Grid creates views automatically based on grid's layout structure.
	//	Users should typically not need to access individual views directly.
	defaultWidth: "18em",
	// viewWidth: string
	// width for the view, in valid css unit
	viewWidth: "",
	templateString: '<div class="dojoxGrid-view"><div class="dojoxGrid-header" dojoAttachPoint="headerNode"><div style="width: 9000em"><div dojoAttachPoint="headerContentNode"></div></div></div><input type="checkbox" class="dojoxGrid-hidden-focus" dojoAttachPoint="hiddenFocusNode" /><input type="checkbox" class="dojoxGrid-hidden-focus" /><div class="dojoxGrid-scrollbox" dojoAttachPoint="scrollboxNode"><div class="dojoxGrid-content" dojoAttachPoint="contentNode" hidefocus="hidefocus"></div></div></div>',
	themeable: false,
	classTag: 'dojoxGrid',
	marginBottom: 0,
	rowPad: 2,
	postMixInProperties: function(){
		this.rowNodes = [];
	},
	postCreate: function(){
		dojo.connect(this.scrollboxNode, "onscroll", dojo.hitch(this, "doscroll"));
		dojox.grid.funnelEvents(this.contentNode, this, "doContentEvent", [ 'mouseover', 'mouseout', 'click', 'dblclick', 'contextmenu' ]);
		dojox.grid.funnelEvents(this.headerNode, this, "doHeaderEvent", [ 'dblclick', 'mouseover', 'mouseout', 'mousemove', 'mousedown', 'click', 'contextmenu' ]);
		this.content = new dojox.grid.contentBuilder(this);
		this.header = new dojox.grid.headerBuilder(this);
	},
	destroy: function(){
		dojox.grid.removeNode(this.headerNode);
		this.inherited("destroy", arguments);
	},
	// focus 
	focus: function(){
		if(dojo.isSafari || dojo.isOpera){
			this.hiddenFocusNode.focus();
		}else{
			this.scrollboxNode.focus();
		}
	},
	setStructure: function(inStructure){
		var vs = this.structure = inStructure;
		// FIXME: similar logic is duplicated in layout
		if(vs.width && dojo.isNumber(vs.width)){
			this.viewWidth = vs.width + 'em';
		}else{
			this.viewWidth = vs.width || this.viewWidth; //|| this.defaultWidth;
		}
		this.onBeforeRow = vs.onBeforeRow;
		this.noscroll = vs.noscroll;
		if(this.noscroll){
			this.scrollboxNode.style.overflow = "hidden";
		}
		// bookkeeping
		this.testFlexCells();
		// accomodate new structure
		this.updateStructure();
	},
	testFlexCells: function(){
		// FIXME: cheater, this function does double duty as initializer and tester
		this.flexCells = false;
		for(var j=0, row; (row=this.structure.rows[j]); j++){
			for(var i=0, cell; (cell=row[i]); i++){
				cell.view = this;
				this.flexCells = this.flexCells || cell.isFlex();
			}
		}
		return this.flexCells;
	},
	updateStructure: function(){
		// header builder needs to update table map
		this.header.update();
		// content builder needs to update markup cache
		this.content.update();
	},
	getScrollbarWidth: function(){
		return (this.noscroll ? 0 : dojox.grid.getScrollbarWidth());
	},
	getColumnsWidth: function(){
		return this.headerContentNode.firstChild.offsetWidth;
	},
	getWidth: function(){
		return this.viewWidth || (this.getColumnsWidth()+this.getScrollbarWidth()) +'px';
	},
	getContentWidth: function(){
		return Math.max(0, dojo._getContentBox(this.domNode).w - this.getScrollbarWidth()) + 'px';
	},
	render: function(){
		this.scrollboxNode.style.height = '';
		this.renderHeader();
	},
	renderHeader: function(){
		this.headerContentNode.innerHTML = this.header.generateHtml(this._getHeaderContent);
	},
	// note: not called in 'view' context
	_getHeaderContent: function(inCell){
		var n = inCell.name || inCell.grid.getCellName(inCell);
		if(inCell.index != inCell.grid.getSortIndex()){
			return n;
		}
		return [ '<div class="', inCell.grid.sortInfo > 0 ? 'dojoxGrid-sort-down' : 'dojoxGrid-sort-up', '">', n, '</div>' ].join('');
	},
	resize: function(){
		this.resizeHeight();
		this.resizeWidth();
	},
	hasScrollbar: function(){
		return (this.scrollboxNode.clientHeight != this.scrollboxNode.offsetHeight);
	},
	resizeHeight: function(){
		if(!this.grid.autoHeight){
			var h = this.domNode.clientHeight;
			if(!this.hasScrollbar()){ // no scrollbar is rendered
				h -= dojox.grid.getScrollbarWidth();
			}
			dojox.grid.setStyleHeightPx(this.scrollboxNode, h);
		}
	},
	resizeWidth: function(){
		if(this.flexCells){
			// the view content width
			this.contentWidth = this.getContentWidth();
			this.headerContentNode.firstChild.style.width = this.contentWidth;
		}
		// FIXME: it should be easier to get w from this.scrollboxNode.clientWidth, 
		// but clientWidth seemingly does not include scrollbar width in some cases
		var w = this.scrollboxNode.offsetWidth - this.getScrollbarWidth();
		w = Math.max(w, this.getColumnsWidth()) + 'px';
		with(this.contentNode){
			style.width = '';
			offsetWidth;
			style.width = w;
		}
	},
	setSize: function(w, h){
		with(this.domNode.style){
			if(w){
				width = w;
			}
			height = (h >= 0 ? h + 'px' : '');
		}
		with(this.headerNode.style){
			if(w){
				width = w;
			}
		}
	},
	renderRow: function(inRowIndex, inHeightPx){
		var rowNode = this.createRowNode(inRowIndex);
		this.buildRow(inRowIndex, rowNode, inHeightPx);
		this.grid.edit.restore(this, inRowIndex);
		return rowNode;
	},
	createRowNode: function(inRowIndex){
		var node = document.createElement("div");
		node.className = this.classTag + '-row';
		node[dojox.grid.rowIndexTag] = inRowIndex;
		this.rowNodes[inRowIndex] = node;
		return node;
	},
	buildRow: function(inRowIndex, inRowNode){
		this.buildRowContent(inRowIndex, inRowNode);
		this.styleRow(inRowIndex, inRowNode);
	},
	buildRowContent: function(inRowIndex, inRowNode){
		inRowNode.innerHTML = this.content.generateHtml(inRowIndex, inRowIndex); 
		if(this.flexCells){
			// FIXME: accessing firstChild here breaks encapsulation
			inRowNode.firstChild.style.width = this.contentWidth;
		}
	},
	rowRemoved:function(inRowIndex){
		this.grid.edit.save(this, inRowIndex);
		delete this.rowNodes[inRowIndex];
	},
	getRowNode: function(inRowIndex){
		return this.rowNodes[inRowIndex];
	},
	getCellNode: function(inRowIndex, inCellIndex){
		var row = this.getRowNode(inRowIndex);
		if(row){
			return this.content.getCellNode(row, inCellIndex);
		}
	},
	// styling
	styleRow: function(inRowIndex, inRowNode){
		inRowNode._style = dojox.grid.getStyleText(inRowNode);
		this.styleRowNode(inRowIndex, inRowNode);
	},
	styleRowNode: function(inRowIndex, inRowNode){
		if(inRowNode){
			this.doStyleRowNode(inRowIndex, inRowNode);
		}
	},
	doStyleRowNode: function(inRowIndex, inRowNode){
		this.grid.styleRowNode(inRowIndex, inRowNode);
	},
	// updating
	updateRow: function(inRowIndex, inHeightPx, inPageNode){
		var rowNode = this.getRowNode(inRowIndex);
		if(rowNode){
			rowNode.style.height = '';
			this.buildRow(inRowIndex, rowNode);
		}
		return rowNode;
	},
	updateRowStyles: function(inRowIndex){
		this.styleRowNode(inRowIndex, this.getRowNode(inRowIndex));
	},
	// scrolling
	lastTop: 0,
	doscroll: function(inEvent){
		this.headerNode.scrollLeft = this.scrollboxNode.scrollLeft;
		// 'lastTop' is a semaphore to prevent feedback-loop with setScrollTop below
		var top = this.scrollboxNode.scrollTop;
		if(top != this.lastTop){
			this.grid.scrollTo(top);
		}
	},
	setScrollTop: function(inTop){
		// 'lastTop' is a semaphore to prevent feedback-loop with doScroll above
		this.lastTop = inTop;
		this.scrollboxNode.scrollTop = inTop;
		return this.scrollboxNode.scrollTop;
	},
	// event handlers (direct from DOM)
	doContentEvent: function(e){
		if(this.content.decorateEvent(e)){
			this.grid.onContentEvent(e);
		}
	},
	doHeaderEvent: function(e){
		if(this.header.decorateEvent(e)){
			this.grid.onHeaderEvent(e);
		}
	},
	// event dispatch(from Grid)
	dispatchContentEvent: function(e){
		return this.content.dispatchEvent(e);
	},
	dispatchHeaderEvent: function(e){
		return this.header.dispatchEvent(e);
	},
	// column resizing
	setColWidth: function(inIndex, inWidth){
		this.grid.setCellWidth(inIndex, inWidth + 'px');
	},
	update: function(){
		var left = this.scrollboxNode.scrollLeft;
		this.content.update();
		this.grid.update();
		this.scrollboxNode.scrollLeft = left;
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.views"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.views"] = true;
dojo.provide("dojox.grid._grid.views");

dojo.declare('dojox.grid.views', null, {
	// summary:
	//	A collection of grid views. Owned by grid and used internally for managing grid views.
	//	Grid creates views automatically based on grid's layout structure.
	//	Users should typically not need to access individual views or the views collection directly.
	constructor: function(inGrid){
		this.grid = inGrid;
	},
	defaultWidth: 200,
	views: [],
	// operations
	resize: function(){
		this.onEach("resize");
	},
	render: function(){
		this.onEach("render");
		this.normalizeHeaderNodeHeight();
	},
	// views
	addView: function(inView){
		inView.idx = this.views.length;
		this.views.push(inView);
	},
	destroyViews: function(){
		for (var i=0, v; v=this.views[i]; i++)
			v.destroy();
		this.views = [];
	},
	getContentNodes: function(){
		var nodes = [];
		for(var i=0, v; v=this.views[i]; i++){
			nodes.push(v.contentNode);
		}
		return nodes;
	},
	forEach: function(inCallback){
		for(var i=0, v; v=this.views[i]; i++){
			inCallback(v, i);
		}
	},
	onEach: function(inMethod, inArgs){
		inArgs = inArgs || [];
		for(var i=0, v; v=this.views[i]; i++){
			if(inMethod in v){
				v[inMethod].apply(v, inArgs);
			}
		}
	},
	// layout
	normalizeHeaderNodeHeight: function(){
		var rowNodes = [];
		for(var i=0, v; (v=this.views[i]); i++){
			if(v.headerContentNode.firstChild){
				rowNodes.push(v.headerContentNode)
			};
		}
		this.normalizeRowNodeHeights(rowNodes);
	},
	normalizeRowNodeHeights: function(inRowNodes){
		var h = 0; 
		for(var i=0, n, o; (n=inRowNodes[i]); i++){
			h = Math.max(h, (n.firstChild.clientHeight)||(n.firstChild.offsetHeight));
		}
		h = (h >= 0 ? h : 0);
		//
		var hpx = h + 'px';
		for(var i=0, n; (n=inRowNodes[i]); i++){
			if(n.firstChild.clientHeight!=h){
				n.firstChild.style.height = hpx;
			}
		}
		//
		//console.log('normalizeRowNodeHeights ', h);
		//
		// querying the height here seems to help scroller measure the page on IE
		if(inRowNodes&&inRowNodes[0]){
			inRowNodes[0].parentNode.offsetHeight;
		}
	},
	renormalizeRow: function(inRowIndex){
		var rowNodes = [];
		for(var i=0, v, n; (v=this.views[i])&&(n=v.getRowNode(inRowIndex)); i++){
			n.firstChild.style.height = '';
			rowNodes.push(n);
		}
		this.normalizeRowNodeHeights(rowNodes);
	},
	getViewWidth: function(inIndex){
		return this.views[inIndex].getWidth() || this.defaultWidth;
	},
	measureHeader: function(){
		this.forEach(function(inView){
			inView.headerContentNode.style.height = '';
		});
		var h = 0;
		this.forEach(function(inView){
			//console.log('headerContentNode', inView.headerContentNode.offsetHeight, inView.headerContentNode.offsetWidth);
			h = Math.max(inView.headerNode.offsetHeight, h);
		});
		return h;
	},
	measureContent: function(){
		var h = 0;
		this.forEach(function(inView) {
			h = Math.max(inView.domNode.offsetHeight, h);
		});
		return h;
	},
	findClient: function(inAutoWidth){
		// try to use user defined client
		var c = this.grid.elasticView || -1;
		// attempt to find implicit client
		if(c < 0){
			for(var i=1, v; (v=this.views[i]); i++){
				if(v.viewWidth){
					for(i=1; (v=this.views[i]); i++){
						if(!v.viewWidth){
							c = i;
							break;
						}
					}
					break;
				}
			}
		}
		// client is in the middle by default
		if(c < 0){
			c = Math.floor(this.views.length / 2);
		}
		return c;
	},
	_arrange: function(l, t, w, h){
		var i, v, vw, len = this.views.length;
		// find the client
		var c = (w <= 0 ? len : this.findClient());
		// layout views
		var setPosition = function(v, l, t){
			with(v.domNode.style){
				left = l + 'px';
				top = t + 'px';
			}
			with(v.headerNode.style){
				left = l + 'px';
				top = 0;
			}
		}
		// for views left of the client
		for(i=0; (v=this.views[i])&&(i<c); i++){
			// get width
			vw = this.getViewWidth(i);
			// process boxes
			v.setSize(vw, h);
			setPosition(v, l, t);
			vw = v.domNode.offsetWidth;
			// update position
			l += vw;
		}
		// next view (is the client, i++ == c) 
		i++;
		// start from the right edge
		var r = w;
		// for views right of the client (iterated from the right)
		for(var j=len-1; (v=this.views[j])&&(i<=j); j--){
			// get width
			vw = this.getViewWidth(j);
			// set size
			v.setSize(vw, h);
			// measure in pixels
			vw = v.domNode.offsetWidth;
			// update position
			r -= vw;
			// set position
			setPosition(v, r, t);
		}
		if(c<len){
			v = this.views[c];
			// position the client box between left and right boxes	
			vw = Math.max(1, r-l);
			// set size
			v.setSize(vw + 'px', h);
			setPosition(v, l, t);
		}
		return l;
	},
	arrange: function(l, t, w, h){
		var w = this._arrange(l, t, w, h);
		this.resize();
		return w;
	},
	// rendering
	renderRow: function(inRowIndex, inNodes){
		var rowNodes = [];
		for(var i=0, v, n, rowNode; (v=this.views[i])&&(n=inNodes[i]); i++){
			rowNode = v.renderRow(inRowIndex);
			n.appendChild(rowNode);
			rowNodes.push(rowNode);
		}
		this.normalizeRowNodeHeights(rowNodes);
	},
	rowRemoved: function(inRowIndex){
		this.onEach("rowRemoved", [ inRowIndex ]);
	},
	// updating
	updateRow: function(inRowIndex, inHeight){
		for(var i=0, v; v=this.views[i]; i++){
			v.updateRow(inRowIndex, inHeight);
		}
		this.renormalizeRow(inRowIndex);
	},
	updateRowStyles: function(inRowIndex){
		this.onEach("updateRowStyles", [ inRowIndex ]);
	},
	// scrolling
	setScrollTop: function(inTop){
		var top = inTop;
		for(var i=0, v; v=this.views[i]; i++){
			top = v.setScrollTop(inTop);
		}
		return top;
		//this.onEach("setScrollTop", [ inTop ]);
	},
	getFirstScrollingView: function(){
		for(var i=0, v; (v=this.views[i]); i++){
			if(v.hasScrollbar()){
				return v;
			}
		}
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.cell"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.cell"] = true;
dojo.provide("dojox.grid._grid.cell");

dojo.declare("dojox.grid.cell", null, {
	// summary:
	//	Respresents a grid cell and contains information about column options and methods
	//	for retrieving cell related information.
	//	Each column in a grid layout has a cell object and most events and many methods
	//	provide access to these objects.
	styles: '',
	constructor: function(inProps){
		dojo.mixin(this, inProps);
		if(this.editor){this.editor = new this.editor(this);}
	},
	// data source
	format: function(inRowIndex){
		// summary:
		//	provides the html for a given grid cell.
		// inRowIndex: int
		// grid row index
		// returns: html for a given grid cell
		var f, i=this.grid.edit.info, d=this.get ? this.get(inRowIndex) : this.value;
		if(this.editor && (this.editor.alwaysOn || (i.rowIndex==inRowIndex && i.cell==this))){
			return this.editor.format(d, inRowIndex);
		}else{
			return (f = this.formatter) ? f.call(this, d, inRowIndex) : d;
		}
	},
	// utility
	getNode: function(inRowIndex){
		// summary:
		//	gets the dom node for a given grid cell.
		// inRowIndex: int
		// grid row index
		// returns: dom node for a given grid cell
		return this.view.getCellNode(inRowIndex, this.index);
	},
	isFlex: function(){
		var uw = this.unitWidth;
		return uw && (uw=='auto' || uw.slice(-1)=='%');
	},
	// edit support
	applyEdit: function(inValue, inRowIndex){
		this.grid.edit.applyCellEdit(inValue, this, inRowIndex);
	},
	cancelEdit: function(inRowIndex){
		this.grid.doCancelEdit(inRowIndex);
	},
	_onEditBlur: function(inRowIndex){
		if(this.grid.edit.isEditCell(inRowIndex, this.index)){
			//console.log('editor onblur', e);
			this.grid.edit.apply();
		}
	},
	registerOnBlur: function(inNode, inRowIndex){
		if(this.commitOnBlur){
			dojo.connect(inNode, "onblur", function(e){
				// hack: if editor still thinks this editor is current some ms after it blurs, assume we've focused away from grid
				setTimeout(dojo.hitch(this, "_onEditBlur", inRowIndex), 250);
			});
		}
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.layout"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.layout"] = true;
dojo.provide("dojox.grid._grid.layout");


dojo.declare("dojox.grid.layout", null, {
	// summary:
	//	Controls grid cell layout. Owned by grid and used internally.
	constructor: function(inGrid){
		this.grid = inGrid;
	},
	// flat array of grid cells
	cells: null,
	// structured array of grid cells
	structure: null,
	// default cell width
	defaultWidth: '6em',
	// methods
	setStructure: function(inStructure){
		this.fieldIndex = 0;
		this.cells = [];
		var s = this.structure = [];
		for(var i=0, viewDef, rows; (viewDef=inStructure[i]); i++){
			s.push(this.addViewDef(viewDef));
		}
		this.cellCount = this.cells.length;
	},
	addViewDef: function(inDef){
		this._defaultCellProps = inDef.defaultCell || {};
		return dojo.mixin({}, inDef, {rows: this.addRowsDef(inDef.rows || inDef.cells)});
	},
	addRowsDef: function(inDef){
		var result = [];
		for(var i=0, row; inDef && (row=inDef[i]); i++){
			result.push(this.addRowDef(i, row));
		}
		return result;
	},
	addRowDef: function(inRowIndex, inDef){
		var result = [];
		for(var i=0, def, cell; (def=inDef[i]); i++){
			cell = this.addCellDef(inRowIndex, i, def);
			result.push(cell);
			this.cells.push(cell);
		}
		return result;
	},
	addCellDef: function(inRowIndex, inCellIndex, inDef){
		var w = 0;
		if(inDef.colSpan > 1){
			w = 0;
		}else if(!isNaN(inDef.width)){
			w = inDef.width + "em";
		}else{
			w = inDef.width || this.defaultWidth;
		}
		// fieldIndex progresses linearly from the last indexed field
		// FIXME: support generating fieldIndex based a text field name (probably in Grid)
		var fieldIndex = inDef.field != undefined ? inDef.field : (inDef.get ? -1 : this.fieldIndex);
		if((inDef.field != undefined) || !inDef.get){
			this.fieldIndex = (inDef.field > -1 ? inDef.field : this.fieldIndex) + 1; 
		}
		return new dojox.grid.cell(
			dojo.mixin({}, this._defaultCellProps, inDef, {
				grid: this.grid,
				subrow: inRowIndex,
				layoutIndex: inCellIndex,
				index: this.cells.length,
				fieldIndex: fieldIndex,
				unitWidth: w
			}));
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.rows"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.rows"] = true;
dojo.provide("dojox.grid._grid.rows");

dojo.declare("dojox.grid.rows", null, {
	//	Stores information about grid rows. Owned by grid and used internally.
	constructor: function(inGrid){
		this.grid = inGrid;
	},
	linesToEms: 2,
	defaultRowHeight: 1, // lines
	overRow: -2,
	// metrics
	getHeight: function(inRowIndex){
		return '';
	},
	getDefaultHeightPx: function(){
		// summmary:
		// retrieves the default row height
		// returns: int, default row height
		return 32;
		//return Math.round(this.defaultRowHeight * this.linesToEms * this.grid.contentPixelToEmRatio);
	},
	// styles
	prepareStylingRow: function(inRowIndex, inRowNode){
		return {
			index: inRowIndex, 
			node: inRowNode,
			odd: Boolean(inRowIndex&1),
			selected: this.grid.selection.isSelected(inRowIndex),
			over: this.isOver(inRowIndex),
			customStyles: "",
			customClasses: "dojoxGrid-row"
		}
	},
	styleRowNode: function(inRowIndex, inRowNode){
		var row = this.prepareStylingRow(inRowIndex, inRowNode);
		this.grid.onStyleRow(row);
		this.applyStyles(row);
	},
	applyStyles: function(inRow){
		with(inRow){
			node.className = customClasses;
			var h = node.style.height;
			dojox.grid.setStyleText(node, customStyles + ';' + (node._style||''));
			node.style.height = h;
		}
	},
	updateStyles: function(inRowIndex){
		this.grid.updateRowStyles(inRowIndex);
	},
	// states and events
	setOverRow: function(inRowIndex){
		var last = this.overRow;
		this.overRow = inRowIndex;
		if((last!=this.overRow)&&(last >=0)){
			this.updateStyles(last);
		}
		this.updateStyles(this.overRow);
	},
	isOver: function(inRowIndex){
		return (this.overRow == inRowIndex);
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.focus"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.focus"] = true;
dojo.provide("dojox.grid._grid.focus");

// focus management
dojo.declare("dojox.grid.focus", null, {
	// summary:
	//	Controls grid cell focus. Owned by grid and used internally for focusing.
	//	Note: grid cell actually receives keyboard input only when cell is being edited.
	constructor: function(inGrid){
		this.grid = inGrid;
		this.cell = null;
		this.rowIndex = -1;
		dojo.connect(this.grid.domNode, "onfocus", this, "doFocus");
	},
	tabbingOut: false,
	focusClass: "dojoxGrid-cell-focus",
	focusView: null,
	initFocusView: function(){
		this.focusView = this.grid.views.getFirstScrollingView();
	},
	isFocusCell: function(inCell, inRowIndex){
		// summary:
		//	states if the given cell is focused
		// inCell: object
		//	grid cell object
		// inRowIndex: int
		//	grid row index
		// returns:
		//	true of the given grid cell is focused
		return (this.cell == inCell) && (this.rowIndex == inRowIndex);
	},
	isLastFocusCell: function(){
		return (this.rowIndex == this.grid.rowCount-1) && (this.cell.index == this.grid.layout.cellCount-1);
	},
	isFirstFocusCell: function(){
		return (this.rowIndex == 0) && (this.cell.index == 0);
	},
	isNoFocusCell: function(){
		return (this.rowIndex < 0) || !this.cell;
	},
	_focusifyCellNode: function(inBork){
		var n = this.cell && this.cell.getNode(this.rowIndex);
		if(n){
			dojo.toggleClass(n, this.focusClass, inBork);
			this.scrollIntoView();
			try{
				if(!this.grid.edit.isEditing())
					dojox.grid.fire(n, "focus");
			}catch(e){}
		}
	},
	scrollIntoView: function() {
		if(!this.cell){
			return;
		}
		var 
			c = this.cell,
			s = c.view.scrollboxNode,
			sr = {
				w: s.clientWidth,
				l: s.scrollLeft,
				t: s.scrollTop,
				h: s.clientHeight
			},
			n = c.getNode(this.rowIndex),
			r = c.view.getRowNode(this.rowIndex),
			rt = this.grid.scroller.findScrollTop(this.rowIndex);
		// place cell within horizontal view
		if(n.offsetLeft + n.offsetWidth > sr.l + sr.w){
			s.scrollLeft = n.offsetLeft + n.offsetWidth - sr.w;
		}else if(n.offsetLeft < sr.l){
			s.scrollLeft = n.offsetLeft;
		}
		// place cell within vertical view
		if(rt + r.offsetHeight > sr.t + sr.h){
			this.grid.setScrollTop(rt + r.offsetHeight - sr.h);
		}else if(rt < sr.t){
			this.grid.setScrollTop(rt);
		}
},
	styleRow: function(inRow){
		if(inRow.index == this.rowIndex){
			this._focusifyCellNode(true);
		}
	},
	setFocusIndex: function(inRowIndex, inCellIndex){
		// summary:
		//	focuses the given grid cell
		// inRowIndex: int
		//	grid row index
		// inCellIndex: int
		//	grid cell index
		this.setFocusCell(this.grid.getCell(inCellIndex), inRowIndex);
	},
	setFocusCell: function(inCell, inRowIndex){
		// summary:
		//	focuses the given grid cell
		// inCell: object
		//	grid cell object
		// inRowIndex: int
		//	grid row index
		if(inCell && !this.isFocusCell(inCell, inRowIndex)){
			this.tabbingOut = false;
			this.focusGrid();
			this._focusifyCellNode(false);
			this.cell = inCell;
			this.rowIndex = inRowIndex;
			this._focusifyCellNode(true);
		}
		// even if this cell isFocusCell, the document focus may need to be rejiggered
		// call opera on delay to prevent keypress from altering focus
		if(dojo.isOpera){
			setTimeout(dojo.hitch(this.grid, 'onCellFocus', this.cell, this.rowIndex), 1);
		}else{
			this.grid.onCellFocus(this.cell, this.rowIndex);
		}
	},
	next: function(){
		// summary:
		//	focus next grid cell
		var row=this.rowIndex, col=this.cell.index+1, cc=this.grid.layout.cellCount-1, rc=this.grid.rowCount-1;
		if(col > cc){
			col = 0;
			row++;
		}
		if(row > rc){
			col = cc;
			row = rc;
		}
		this.setFocusIndex(row, col);
	},
	previous: function(){
		// summary:
		//	focus previous grid cell
		var row=(this.rowIndex || 0), col=(this.cell.index || 0) - 1;
		if(col < 0){
			col = this.grid.layout.cellCount-1;
			row--;
		}
		if(row < 0){
			row = 0;
			col = 0;
		}
		this.setFocusIndex(row, col);
	},
	move: function(inRowDelta, inColDelta) {
		// summary:
		//	focus grid cell based on position relative to current focus
		// inRowDelta: int
		// vertical distance from current focus
		// inColDelta: int
		// horizontal distance from current focus
		var
			rc = this.grid.rowCount-1,
			cc = this.grid.layout.cellCount-1,
			r = this.rowIndex,
			i = this.cell.index,
			row = Math.min(rc, Math.max(0, r+inRowDelta)),
			col = Math.min(cc, Math.max(0, i+inColDelta));
		this.setFocusIndex(row, col);
		if(inRowDelta){
			this.grid.updateRow(r);
		}
	},
	previousKey: function(e){
		if(this.isFirstFocusCell()){
			this.tabOut(this.grid.domNode);
		}else{
			dojo.stopEvent(e);
			this.previous();
		}
	},
	nextKey: function(e) {
		if(this.isLastFocusCell()){
			this.tabOut(this.grid.lastFocusNode);
		}else{
			dojo.stopEvent(e);
			this.next();
		}
	},
	tabOut: function(inFocusNode){
		this.tabbingOut = true;
		inFocusNode.focus();
	},
	focusGrid: function(){
		dojox.grid.fire(this.focusView, "focus");
		this._focusifyCellNode(true);
	},
	doFocus: function(e){
		// trap focus only for grid dom node
		if(e && e.target != e.currentTarget){
			return;
		}
		// do not focus for scrolling if grid is about to blur
		if(!this.tabbingOut && this.isNoFocusCell()){
			// establish our virtual-focus, if necessary
			this.setFocusIndex(0, 0);
		}
		this.tabbingOut = false;
	}
});

}

if(!dojo._hasResource['dojox.grid._grid.selection']){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource['dojox.grid._grid.selection'] = true;
dojo.provide('dojox.grid._grid.selection');

dojo.declare("dojox.grid.selection", null, {
	// summary:
	//	Manages row selection for grid. Owned by grid and used internally 
	//	for selection. Override to implement custom selection.
	constructor: function(inGrid){
		this.grid = inGrid;
		this.selected = [];
	},
	multiSelect: true,
	selected: null,
	updating: 0,
	selectedIndex: -1,
	onCanSelect: function(inIndex){
		return this.grid.onCanSelect(inIndex);
	},
	onCanDeselect: function(inIndex){
		return this.grid.onCanDeselect(inIndex);
	},
	onSelected: function(inIndex){
		return this.grid.onSelected(inIndex);
	},
	onDeselected: function(inIndex){
		return this.grid.onDeselected(inIndex);
	},
	//onSetSelected: function(inIndex, inSelect) { };
	onChanging: function(){
	},
	onChanged: function(){
		return this.grid.onSelectionChanged();
	},
	isSelected: function(inIndex){
		return this.selected[inIndex];
	},
	getFirstSelected: function(){
		for(var i=0, l=this.selected.length; i<l; i++){
			if(this.selected[i]){
				return i;
			}
		}
		return -1;
	},
	getNextSelected: function(inPrev){
		for(var i=inPrev+1, l=this.selected.length; i<l; i++){
			if(this.selected[i]){
				return i;
			}
		}
		return -1;
	},
	getSelected: function(){
		var result = [];
		for(var i=0, l=this.selected.length; i<l; i++){
			if(this.selected[i]){
				result.push(i);
			}
		}
		return result;
	},
	getSelectedCount: function(){
		var c = 0;
		for(var i=0; i<this.selected.length; i++){
			if(this.selected[i]){
				c++;
			}
		}
		return c;
	},
	beginUpdate: function(){
		if(this.updating == 0){
			this.onChanging();
		}
		this.updating++;
	},
	endUpdate: function(){
		this.updating--;
		if(this.updating == 0){
			this.onChanged();
		}
	},
	select: function(inIndex){
		this.unselectAll(inIndex);
		this.addToSelection(inIndex);
	},
	addToSelection: function(inIndex){
		inIndex = Number(inIndex);
		if(this.selected[inIndex]){
			this.selectedIndex = inIndex;
		}else{
			if(this.onCanSelect(inIndex) !== false){
				this.selectedIndex = inIndex;
				this.beginUpdate();
				this.selected[inIndex] = true;
				this.grid.onSelected(inIndex);
				//this.onSelected(inIndex);
				//this.onSetSelected(inIndex, true);
				this.endUpdate();
			}
		}
	},
	deselect: function(inIndex){
		inIndex = Number(inIndex);
		if(this.selectedIndex == inIndex){
			this.selectedIndex = -1;
		}
		if(this.selected[inIndex]){
			if(this.onCanDeselect(inIndex) === false){
				return;
			}
			this.beginUpdate();
			delete this.selected[inIndex];
			this.grid.onDeselected(inIndex);
			//this.onDeselected(inIndex);
			//this.onSetSelected(inIndex, false);
			this.endUpdate();
		}
	},
	setSelected: function(inIndex, inSelect){
		this[(inSelect ? 'addToSelection' : 'deselect')](inIndex);
	},
	toggleSelect: function(inIndex){
		this.setSelected(inIndex, !this.selected[inIndex])
	},
	insert: function(inIndex){
		this.selected.splice(inIndex, 0, false);
		if(this.selectedIndex >= inIndex){
			this.selectedIndex++;
		}
	},
	remove: function(inIndex){
		this.selected.splice(inIndex, 1);
		if(this.selectedIndex >= inIndex){
			this.selectedIndex--;
		}
	},
	unselectAll: function(inExcept){
		for(var i in this.selected){
			if((i!=inExcept)&&(this.selected[i]===true)){
				this.deselect(i);
			}
		}
	},
	shiftSelect: function(inFrom, inTo){
		var s = (inFrom >= 0 ? inFrom : inTo), e = inTo;
		if(s > e){
			e = s;
			s = inTo;
		}
		for(var i=s; i<=e; i++){
			this.addToSelection(i);
		}
	},
	clickSelect: function(inIndex, inCtrlKey, inShiftKey){
		this.beginUpdate();
		if(!this.multiSelect){
			this.select(inIndex);
		}else{
			var lastSelected = this.selectedIndex;
			if(!inCtrlKey){
				this.unselectAll(inIndex);
			}
			if(inShiftKey){
				this.shiftSelect(lastSelected, inIndex);
			}else if(inCtrlKey){
				this.toggleSelect(inIndex);
			}else{
				this.addToSelection(inIndex)
			}
		}
		this.endUpdate();
	},
	clickSelectEvent: function(e){
		this.clickSelect(e.rowIndex, e.ctrlKey, e.shiftKey);
	},
	clear: function(){
		this.beginUpdate();
		this.unselectAll();
		this.endUpdate();
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.edit"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.edit"] = true;
dojo.provide("dojox.grid._grid.edit");

dojo.declare("dojox.grid.edit", null, {
	// summary:
	//	Controls grid cell editing process. Owned by grid and used internally for editing.
	constructor: function(inGrid){
		this.grid = inGrid;
		this.connections = [];
		if(dojo.isIE){
			this.connections.push(dojo.connect(document.body, "onfocus", dojo.hitch(this, "_boomerangFocus")));
		}
	},
	info: {},
	destroy: function(){
		dojo.forEach(this.connections, function(c){
			dojo.disconnect(c);
		});
	},
	cellFocus: function(inCell, inRowIndex){
		// summary:
		//	invoke editing when cell is focused
		// inCell: cell object
		//	grid cell object
		// inRowIndex: int
		//	grid row index
		if(this.grid.singleClickEdit || this.isEditRow(inRowIndex)){
			// if same row or quick editing, edit
			this.setEditCell(inCell, inRowIndex);
		}else{
			// otherwise, apply any pending row edits
			this.apply();
		}
		// if dynamic or static editing...
		if(this.isEditing() || (inCell && (inCell.editor||0).alwaysOn)){
			// let the editor focus itself as needed
			this._focusEditor(inCell, inRowIndex);
		}
	},
	rowClick: function(e){
		if(this.isEditing() && !this.isEditRow(e.rowIndex)){
			this.apply();
		}
	},
	styleRow: function(inRow){
		if(inRow.index == this.info.rowIndex){
			inRow.customClasses += ' dojoxGrid-row-editing';
		}
	},
	dispatchEvent: function(e){
		var c = e.cell, ed = c && c.editor;
		return ed && ed.dispatchEvent(e.dispatch, e);
	},
	// Editing
	isEditing: function(){
		// summary:
		//	indicates editing state of the grid.
		// returns:
		//	 true if grid is actively editing
		return this.info.rowIndex !== undefined;
	},
	isEditCell: function(inRowIndex, inCellIndex){
		// summary:
		//	indicates if the given cell is being edited.
		// inRowIndex: int
		//	grid row index
		// inCellIndex: int
		//	grid cell index
		// returns:
		//	 true if given cell is being edited
		return (this.info.rowIndex === inRowIndex) && (this.info.cell.index == inCellIndex);
	},
	isEditRow: function(inRowIndex){
		// summary:
		//	indicates if the given row is being edited.
		// inRowIndex: int
		//	grid row index
		// returns:
		//	 true if given row is being edited
		return this.info.rowIndex === inRowIndex;
	},
	setEditCell: function(inCell, inRowIndex){
		// summary:
		//	set the given cell to be edited
		// inRowIndex: int
		//	grid row index
		// inCell: object
		//	grid cell object
		if(!this.isEditCell(inRowIndex, inCell.index)){
			this.start(inCell, inRowIndex, this.isEditRow(inRowIndex) || inCell.editor);
		}
	},
	_focusEditor: function(inCell, inRowIndex){
		dojox.grid.fire(inCell.editor, "focus", [inRowIndex]);
	},
	focusEditor: function(){
		if(this.isEditing()){
			this._focusEditor(this.info.cell, this.info.rowIndex);
		}
	},
	// implement fix for focus boomerang effect on IE
	_boomerangWindow: 500,
	_shouldCatchBoomerang: function(){
		return this._catchBoomerang > new Date().getTime();
	},
	_boomerangFocus: function(){
		//console.log("_boomerangFocus");
		if(this._shouldCatchBoomerang()){
			// make sure we don't utterly lose focus
			this.grid.focus.focusGrid();
			// let the editor focus itself as needed
			this.focusEditor();
			// only catch once
			this._catchBoomerang = 0;
		}
	},
	_doCatchBoomerang: function(){
		// give ourselves a few ms to boomerang IE focus effects
		if(dojo.isIE){this._catchBoomerang = new Date().getTime() + this._boomerangWindow;}
	},
	// end boomerang fix API
	start: function(inCell, inRowIndex, inEditing){
		this.grid.beginUpdate();
		this.editorApply();
		if(this.isEditing() && !this.isEditRow(inRowIndex)){
			this.applyRowEdit();
			this.grid.updateRow(inRowIndex);
		}
		if(inEditing){
			this.info = { cell: inCell, rowIndex: inRowIndex };
			this.grid.doStartEdit(inCell, inRowIndex); 
			this.grid.updateRow(inRowIndex);
		}else{
			this.info = {};
		}
		this.grid.endUpdate();
		// make sure we don't utterly lose focus
		this.grid.focus.focusGrid();
		// let the editor focus itself as needed
		this._focusEditor(inCell, inRowIndex);
		// give ourselves a few ms to boomerang IE focus effects
		this._doCatchBoomerang();
	},
	_editorDo: function(inMethod){
		var c = this.info.cell
		//c && c.editor && c.editor[inMethod](c, this.info.rowIndex);
		c && c.editor && c.editor[inMethod](this.info.rowIndex);
	},
	editorApply: function(){
		this._editorDo("apply");
	},
	editorCancel: function(){
		this._editorDo("cancel");
	},
	applyCellEdit: function(inValue, inCell, inRowIndex){
		this.grid.doApplyCellEdit(inValue, inRowIndex, inCell.fieldIndex);
	},
	applyRowEdit: function(){
		this.grid.doApplyEdit(this.info.rowIndex);
	},
	apply: function(){
		// summary:
		//	apply a grid edit
		if(this.isEditing()){
			this.grid.beginUpdate();
			this.editorApply();
			this.applyRowEdit();
			this.info = {};
			this.grid.endUpdate();
			this.grid.focus.focusGrid();
			this._doCatchBoomerang();
		}
	},
	cancel: function(){
		// summary:
		//	cancel a grid edit
		if(this.isEditing()){
			this.grid.beginUpdate();
			this.editorCancel();
			this.info = {};
			this.grid.endUpdate();
			this.grid.focus.focusGrid();
			this._doCatchBoomerang();
		}
	},
	save: function(inRowIndex, inView){
		// summary:
		//	save the grid editing state
		// inRowIndex: int
		//	grid row index
		// inView: object
		//	grid view
		var c = this.info.cell;
		if(this.isEditRow(inRowIndex) && (!inView || c.view==inView) && c.editor){
			c.editor.save(c, this.info.rowIndex);
		}
	},
	restore: function(inView, inRowIndex){
		// summary:
		//	restores the grid editing state
		// inRowIndex: int
		//	grid row index
		// inView: object
		//	grid view
		var c = this.info.cell;
		if(this.isEditRow(inRowIndex) && c.view == inView && c.editor){
			c.editor.restore(c, this.info.rowIndex);
		}
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.rowbar"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.rowbar"] = true;
dojo.provide("dojox.grid._grid.rowbar");


dojo.declare('dojox.GridRowView', dojox.GridView, {
	// summary:
	//	Custom grid view. If used in a grid structure, provides a small selectable region for grid rows.
	defaultWidth: "3em",
	noscroll: true,
	padBorderWidth: 2,
	buildRendering: function(){
		this.inherited('buildRendering', arguments);
		this.scrollboxNode.style.overflow = "hidden";
		this.headerNode.style.visibility = "hidden";
	},	
	getWidth: function(){
		return this.viewWidth || this.defaultWidth;
	},
	buildRowContent: function(inRowIndex, inRowNode){
		var w = this.contentNode.offsetWidth - this.padBorderWidth 
		inRowNode.innerHTML = '<table style="width:' + w + 'px;" role="wairole:presentation"><tr><td class="dojoxGrid-rowbar-inner"></td></tr></table>';
	},
	renderHeader: function(){
	},
	resize: function(){
		this.resizeHeight();
	},
	// styling
	doStyleRowNode: function(inRowIndex, inRowNode){
		var n = [ "dojoxGrid-rowbar" ];
		if(this.grid.rows.isOver(inRowIndex)){
			n.push("dojoxGrid-rowbar-over");
		}
		if(this.grid.selection.isSelected(inRowIndex)){
			n.push("dojoxGrid-rowbar-selected");
		}
		inRowNode.className = n.join(" ");
	},
	// event handlers
	domouseover: function(e){
		this.grid.onMouseOverRow(e);
	},
	domouseout: function(e){
		if(!this.isIntraRowEvent(e)){
			this.grid.onMouseOutRow(e);
		}
	}
});

}

if(!dojo._hasResource["dojox.grid._grid.publicEvents"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._grid.publicEvents"] = true;
dojo.provide("dojox.grid._grid.publicEvents");

dojox.grid.publicEvents = {
	// summary:
	//	VirtualGrid mixin that provides default implementations for grid events.
	//	dojo.connect to events to retain default implementation or override them for custom handling.
	
	//cellOverClass: string
	// css class to apply to grid cells over which the cursor is placed.
	cellOverClass: "dojoxGrid-cell-over",
	// top level handlers (more specified handlers below)
	onKeyEvent: function(e){
		this.dispatchKeyEvent(e);
	},
	onContentEvent: function(e){
		this.dispatchContentEvent(e);
	},
	onHeaderEvent: function(e){
		this.dispatchHeaderEvent(e);
	},
	onStyleRow: function(inRow){
		// summary:
		//	Perform row styling on a given row. Called whenever row styling is updated.
		// inRow: object
		// Object containing row state information: selected, true if the row is selcted; over:
		// true of the mouse is over the row; odd: true if the row is odd. Use customClasses and
		// customStyles to control row css classes and styles; both properties are strings.
		with(inRow){
			customClasses += (odd?" dojoxGrid-row-odd":"") + (selected?" dojoxGrid-row-selected":"") + (over?" dojoxGrid-row-over":"");
		}
		this.focus.styleRow(inRow);
		this.edit.styleRow(inRow);
	},
	onKeyDown: function(e){
		// summary:
		// grid key event handler. By default enter begins editing and applies edits, escape cancels and edit,
		// tab, shift-tab, and arrow keys move grid cell focus.
		if(e.altKey || e.ctrlKey || e.metaKey ){
			return;
		}
		switch(e.keyCode){
			case dojo.keys.ESCAPE:
				this.edit.cancel();
				break;
			case dojo.keys.ENTER:
				if (!e.shiftKey) {
					var isEditing = this.edit.isEditing();
					this.edit.apply();
					if(!isEditing){
						this.edit.setEditCell(this.focus.cell, this.focus.rowIndex);
					}
				}
				break;
			case dojo.keys.TAB:
				this.focus[e.shiftKey ? 'previousKey' : 'nextKey'](e);
				break;
			case dojo.keys.LEFT_ARROW:
				if(!this.edit.isEditing()){
				this.focus.move(0, -1);
				}
				break;
			case dojo.keys.RIGHT_ARROW:
				if(!this.edit.isEditing()){
					this.focus.move(0, 1);
				}
				break;
			case dojo.keys.UP_ARROW:
				if(!this.edit.isEditing()){
					this.focus.move(-1, 0);
				}
				break;
			case dojo.keys.DOWN_ARROW:
				if(!this.edit.isEditing()){
					this.focus.move(1, 0);
				}
				break;
		}
	},
	onMouseOver: function(e){
		// summary:
		//	event fired when mouse is over the grid.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		e.rowIndex == -1 ? this.onHeaderCellMouseOver(e) : this.onCellMouseOver(e);
	},
	onMouseOut: function(e){
		// summary:
		//	event fired when mouse moves out of the grid.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		e.rowIndex == -1 ? this.onHeaderCellMouseOut(e) : this.onCellMouseOut(e);
	},
	onMouseOverRow: function(e){
		// summary:
		//	event fired when mouse is over any row (data or header).
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		if(!this.rows.isOver(e.rowIndex)){
			this.rows.setOverRow(e.rowIndex);
			e.rowIndex == -1 ? this.onHeaderMouseOver(e) : this.onRowMouseOver(e);
		}
	},
	onMouseOutRow: function(e){
		// summary:
		//	event fired when mouse moves out of any row (data or header).
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		if(this.rows.isOver(-1)){
			this.onHeaderMouseOut(e);
		}else if(!this.rows.isOver(-2)){
			this.rows.setOverRow(-2);
			this.onRowMouseOut(e);
		}
	},
	// cell events
	onCellMouseOver: function(e){
		// summary:
		//	event fired when mouse is over a cell.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.addClass(e.cellNode, this.cellOverClass);
	},
	onCellMouseOut: function(e){
		// summary:
		//	event fired when mouse moves out of a cell.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.removeClass(e.cellNode, this.cellOverClass);
	},
	onCellClick: function(e){
		// summary:
		//	event fired when a cell is clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.focus.setFocusCell(e.cell, e.rowIndex);
		this.onRowClick(e);
	},
	onCellDblClick: function(e){
		// summary:
		//	event fired when a cell is double-clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.edit.setEditCell(e.cell, e.rowIndex); 
		this.onRowDblClick(e);
	},
	onCellContextMenu: function(e){
		// summary:
		//	event fired when a cell context menu is accessed via mouse right click.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.onRowContextMenu(e);
	},
	onCellFocus: function(inCell, inRowIndex){
		// summary:
		//	event fired when a cell receives focus.
		// inCell: object
		//	cell object containing properties of the grid column.
		// inRowIndex: int
		//	index of the grid row
		this.edit.cellFocus(inCell, inRowIndex);
	},
	// row events
	onRowClick: function(e){
		// summary:
		//	event fired when a row is clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.edit.rowClick(e);
		this.selection.clickSelectEvent(e);
	},
	onRowDblClick: function(e){
		// summary:
		//	event fired when a row is double clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onRowMouseOver: function(e){
		// summary:
		//	event fired when mouse moves over a data row.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onRowMouseOut: function(e){
		// summary:
		//	event fired when mouse moves out of a data row.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onRowContextMenu: function(e){
		// summary:
		//	event fired when a row context menu is accessed via mouse right click.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.stopEvent(e);
	},
	// header events
	onHeaderMouseOver: function(e){
		// summary:
		//	event fired when mouse moves over the grid header.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onHeaderMouseOut: function(e){
		// summary:
		//	event fired when mouse moves out of the grid header.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onHeaderCellMouseOver: function(e){
		// summary:
		//	event fired when mouse moves over a header cell.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.addClass(e.cellNode, this.cellOverClass);
	},
	onHeaderCellMouseOut: function(e){
		// summary:
		//	event fired when mouse moves out of a header cell.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.removeClass(e.cellNode, this.cellOverClass);
	},
	onHeaderClick: function(e){
		// summary:
		//	event fired when the grid header is clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onHeaderCellClick: function(e){
		// summary:
		//	event fired when a header cell is clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.setSortIndex(e.cell.index);
		this.onHeaderClick(e);
	},
	onHeaderDblClick: function(e){
		// summary:
		//	event fired when the grid header is double clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
	},
	onHeaderCellDblClick: function(e){
		// summary:
		//	event fired when a header cell is double clicked.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.onHeaderDblClick(e);
	},
	onHeaderCellContextMenu: function(e){
		// summary:
		//	event fired when a header cell context menu is accessed via mouse right click.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		this.onHeaderContextMenu(e);
	},
	onHeaderContextMenu: function(e){
		// summary:
		//	event fired when the grid header context menu is accessed via mouse right click.
		// e: decorated event object 
		//	contains reference to grid, cell, and rowIndex
		dojo.stopEvent(e);
	},
	// editing
	onStartEdit: function(inCell, inRowIndex){
		// summary:
		//	event fired when editing is started for a given grid cell
		// inCell: object
		//	cell object containing properties of the grid column.
		// inRowIndex: int
		//	index of the grid row
	},
	onApplyCellEdit: function(inValue, inRowIndex, inFieldIndex){
		// summary:
		//	event fired when editing is applied for a given grid cell
		// inValue: string
		//	value from cell editor
		// inRowIndex: int
		//	index of the grid row
		// inFieldIndex: int
		//	index in the grid's data model
	},
	onCancelEdit: function(inRowIndex){
		// summary:
		//	event fired when editing is cancelled for a given grid cell
		// inRowIndex: int
		//	index of the grid row
	},
	onApplyEdit: function(inRowIndex){
		// summary:
		//	event fired when editing is applied for a given grid row
		// inRowIndex: int
		//	index of the grid row
	},
	onCanSelect: function(inRowIndex){
		// summary:
		//	event to determine if a grid row may be selected
		// inRowIndex: int
		//	index of the grid row
		// returns:
		//	true if the row can be selected
		return true // boolean;
	},
	onCanDeselect: function(inRowIndex){
		// summary:
		//	event to determine if a grid row may be deselected
		// inRowIndex: int
		//	index of the grid row
		// returns:
		//	true if the row can be deselected
		return true // boolean;
	},
	onSelected: function(inRowIndex){
		// summary:
		//	event fired when a grid row is selected
		// inRowIndex: int
		//	index of the grid row
		this.updateRowStyles(inRowIndex);
	},
	onDeselected: function(inRowIndex){
		// summary:
		//	event fired when a grid row is deselected
		// inRowIndex: int
		//	index of the grid row
		this.updateRowStyles(inRowIndex);
	},
	onSelectionChanged: function(){
	}
}

}

if(!dojo._hasResource["dojox.grid.VirtualGrid"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid.VirtualGrid"] = true;
dojo.provide("dojox.grid.VirtualGrid");












dojo.declare('dojox.VirtualGrid', 
	[ dijit._Widget, dijit._Templated ], 
{
	// summary:
	// 		A grid widget with virtual scrolling, cell editing, complex rows,
	// 		sorting, fixed columns, sizeable columns, etc.
	//
	//	description:
	//		VirtualGrid provides the full set of grid features without any
	//		direct connection to a data store.
	//
	//		The grid exposes a get function for the grid, or optionally
	//		individual columns, to populate cell contents.
	//
	//		The grid is rendered based on its structure, an object describing
	//		column and cell layout.
	//
	//	example:
	//		A quick sample:
	//		
	//		define a get function
	//	|	function get(inRowIndex){ // called in cell context
	//	|		return [this.index, inRowIndex].join(', ');
	//	|	}
	//		
	//		define the grid structure:
	//	|	var structure = [ // array of view objects
	//	|		{ cells: [// array of rows, a row is an array of cells
	//	|			[
	//	|				{ name: "Alpha", width: 6 }, 
	//	|				{ name: "Beta" }, 
	//	|				{ name: "Gamma", get: get }]
	//	|		]}
	//	|	];
	//		
	//	|	<div id="grid" 
	//	|		rowCount="100" get="get" 
	//	|		structure="structure" 
	//	|		dojoType="dojox.VirtualGrid"></div>

	templateString: '<div class="dojoxGrid" hidefocus="hidefocus" role="wairole:grid"><div class="dojoxGrid-master-header" dojoAttachPoint="headerNode"></div><div class="dojoxGrid-master-view" dojoAttachPoint="viewsNode"></div><span dojoAttachPoint="lastFocusNode" tabindex="0"></span></div>',
	// classTag: string
	// css class applied to the grid's domNode
	classTag: 'dojoxGrid',
	get: function(inRowIndex){
		/* summary: Default data getter. 
			description:
				Provides data to display in a grid cell. Called in grid cell context.
				So this.cell.index is the column index.
			inRowIndex: integer
				row for which to provide data
			returns:
				data to display for a given grid cell.
		*/
	},
	// settings
	// rowCount: int
	//	Number of rows to display. 
	rowCount: 5,
	// keepRows: int
	//	Number of rows to keep in the rendering cache.
	keepRows: 75, 
	// rowsPerPage: int
	//	Number of rows to render at a time.
	rowsPerPage: 25,
	// autoWidth: boolean
	//	If autoWidth is true, grid width is automatically set to fit the data.
	autoWidth: false,
	// autoHeight: boolean
	//	If autoHeight is true, grid height is automatically set to fit the data.
	autoHeight: false,
	// autoRender: boolean
	//	If autoRender is true, grid will render itself after initialization.
	autoRender: true,
	// defaultHeight: string
	//	default height of the grid, measured in any valid css unit.
	defaultHeight: '15em',
	// structure: object or string
	//	View layout defintion. Can be set to a layout object, or to the (string) name of a layout object.
	structure: '',
	// elasticView: int
	//	Override defaults and make the indexed grid view elastic, thus filling available horizontal space.
	elasticView: -1,
	// singleClickEdit: boolean
	//	Single-click starts editing. Default is double-click
	singleClickEdit: false,
	// private
	sortInfo: 0,
	themeable: true,
	// initialization
	buildRendering: function(){
		this.inherited(arguments);
		// reset get from blank function (needed for markup parsing) to null, if not changed
		if(this.get == dojox.VirtualGrid.prototype.get){
			this.get = null;
		}
		if(!this.domNode.getAttribute('tabIndex')){
			this.domNode.tabIndex = "0";
		}
		this.createScroller();
		this.createLayout();
		this.createViews();
		this.createManagers();
		dojox.grid.initTextSizePoll();
		this.connect(dojox.grid, "textSizeChanged", "textSizeChanged");
		dojox.grid.funnelEvents(this.domNode, this, 'doKeyEvent', dojox.grid.keyEvents);
		this.connect(this, "onShow", "renderOnIdle");
	},
	postCreate: function(){
		// replace stock styleChanged with one that triggers an update
		this.styleChanged = this._styleChanged;
		this.setStructure(this.structure);
	},
	destroy: function(){
		this.domNode.onReveal = null;
		this.domNode.onSizeChange = null;
		this.edit.destroy();
		this.views.destroyViews();
		this.inherited(arguments);
	},
	styleChanged: function(){
		this.setStyledClass(this.domNode, '');
	},
	_styleChanged: function(){
		this.styleChanged();
		this.update();
	},
	textSizeChanged: function(){
		setTimeout(dojo.hitch(this, "_textSizeChanged"), 1);
	},
	_textSizeChanged: function(){
		if(this.domNode){
			this.views.forEach(function(v){
				v.content.update();
			});
			this.render();
		}
	},
	sizeChange: function(){
		dojox.grid.jobs.job(this.id + 'SizeChange', 50, dojo.hitch(this, "update"));
	},
	renderOnIdle: function() {
		setTimeout(dojo.hitch(this, "render"), 1);
	},
	// managers
	createManagers: function(){
		// summary:
		//	create grid managers for various tasks including rows, focus, selection, editing
		
		// row manager
		this.rows = new dojox.grid.rows(this);
		// focus manager
		this.focus = new dojox.grid.focus(this);
		// selection manager
		this.selection = new dojox.grid.selection(this);
		// edit manager
		this.edit = new dojox.grid.edit(this);
	},
	// virtual scroller
	createScroller: function(){
		this.scroller = new dojox.grid.scroller.columns();
		this.scroller.renderRow = dojo.hitch(this, "renderRow");
		this.scroller.removeRow = dojo.hitch(this, "rowRemoved");
	},
	// layout
	createLayout: function(){
		this.layout = new dojox.grid.layout(this);
	},
	// views
	createViews: function(){
		this.views = new dojox.grid.views(this);
		this.views.createView = dojo.hitch(this, "createView");
	},
	createView: function(inClass){
		var c = eval(inClass);
		var view = new c({ grid: this });
		this.viewsNode.appendChild(view.domNode);
		this.headerNode.appendChild(view.headerNode);
		this.views.addView(view);
		return view;
	},
	buildViews: function(){
		for(var i=0, vs; (vs=this.layout.structure[i]); i++){
			this.createView(vs.type || "dojox.GridView").setStructure(vs);
		}
		this.scroller.setContentNodes(this.views.getContentNodes());
	},
	setStructure: function(inStructure){
		// summary:
		//		Install a new structure and rebuild the grid.
		// inStructure: Object
		//		Structure object defines the grid layout and provides various
		//		options for grid views and columns
		//	description:
		//		A grid structure is an array of view objects. A view object can
		//		specify a view type (view class), width, noscroll (boolean flag
		//		for view scrolling), and cells. Cells is an array of objects
		//		corresponding to each grid column. The view cells object is an
		//		array of subrows comprising a single row. Each subrow is an
		//		array of column objects. A column object can have a name,
		//		width, value (default), get function to provide data, styles,
		//		and span attributes (rowSpan, colSpan).

		this.views.destroyViews();
		this.structure = inStructure;
		if((this.structure)&&(dojo.isString(this.structure))){
			this.structure=dojox.grid.getProp(this.structure);
		}
		if(!this.structure){
			this.structure=window["layout"];
		}
		if(!this.structure){
			return;
		}
		this.layout.setStructure(this.structure);
		this._structureChanged();
	},
	_structureChanged: function() {
		this.buildViews();
		if(this.autoRender){
			this.render();
		}
	},
	// sizing
	resize: function(){
		// summary:
		//		Update the grid's rendering dimensions and resize it
		
		// FIXME: If grid is not sized explicitly, sometimes bogus scrollbars 
		// can appear in our container, which may require an extra call to 'resize'
		// to sort out.
		
		// if we have set up everything except the DOM, we cannot resize
		if(!this.domNode.parentNode){
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
		this.headerNode.style.height = t + 'px';
		// content extent
		var h = (this.autoHeight ? -1 : Math.max(this.domNode.clientHeight - t, 0) || 0);
		//this.views.arrange(0, 0, 0, h);
		this.views.onEach('setSize', [0, h]);
		this.views.onEach('resizeHeight');
		this.scroller.windowHeight = h; 
	},
	// render 
	render: function(){
		// summary:
		//	Render the grid, headers, and views. Edit and scrolling states are reset. To retain edit and 
		// scrolling states, see Update.

		if(!this.domNode){return;}
		//
		this.update = this.defaultUpdate;
		this.scroller.init(this.rowCount, this.keepRows, this.rowsPerPage);
		this.prerender();
		this.setScrollTop(0);
		this.postrender();
	},
	prerender: function(){
		this.views.render();
		this.resize();
	},
	postrender: function(){
		this.postresize();
		this.focus.initFocusView();
		// make rows unselectable
		dojo.setSelectable(this.domNode, false);
	},
	postresize: function(){
		// views are position absolute, so they do not inflate the parent
		if(this.autoHeight){
			this.viewsNode.style.height = this.views.measureContent() + 'px';
		}
	},
	// private, used internally to render rows
	renderRow: function(inRowIndex, inNodes){
		this.views.renderRow(inRowIndex, inNodes);
	},
	// private, used internally to remove rows
	rowRemoved: function(inRowIndex){
		this.views.rowRemoved(inRowIndex);
	},
	invalidated: null,
	updating: false,
	beginUpdate: function(){
		// summary:
		//	Use to make multiple changes to rows while queueing row updating.
		// NOTE: not currently supporting nested begin/endUpdate calls
		this.invalidated = [];
		this.updating = true;
	},
	endUpdate: function(){
		// summary:
		//	Use after calling beginUpdate to render any changes made to rows.
		this.updating = false;
		var i = this.invalidated;
		if(i.all){
			this.update();
		}else if(i.rowCount != undefined){
			this.updateRowCount(i.rowCount);
		}else{
			for(r in i){
				this.updateRow(Number(r));
			}
		}
		this.invalidated = null;
	},
	// update
	defaultUpdate: function(){
		// note: initial update calls render and subsequently this function.
		if(this.updating){
			this.invalidated.all = true;
			return;
		}
		//this.edit.saveState(inRowIndex);
		this.prerender();
		this.scroller.invalidateNodes();
		this.setScrollTop(this.scrollTop);
		this.postrender();
		//this.edit.restoreState(inRowIndex);
	},
	update: function(){
		// summary:
		//	Update the grid, retaining edit and scrolling states.
		this.render();
	},
	updateRow: function(inRowIndex){
		// summary:
		//	Render a single row.
		// inRowIndex: int
		//	index of the row to render
		inRowIndex = Number(inRowIndex);
		if(this.updating){
			this.invalidated[inRowIndex]=true;
			return;
		}
		this.views.updateRow(inRowIndex, this.rows.getHeight(inRowIndex));
		this.scroller.rowHeightChanged(inRowIndex);
	},
	updateRowCount: function(inRowCount){
		//summary: 
		//	Change the number of rows.
		// inRowCount: int
		//	Number of rows in the grid.
		if(this.updating){
			this.invalidated.rowCount = inRowCount;
			return;
		}
		this.rowCount = inRowCount;
		this.scroller.updateRowCount(inRowCount);
		this.setScrollTop(this.scrollTop);
		this.resize();
	},
	updateRowStyles: function(inRowIndex){
		// summary:
		//	Update the styles for a row after it's state has changed.
		
		this.views.updateRowStyles(inRowIndex);
	},
	rowHeightChanged: function(inRowIndex){
		// summary: 
		//	Update grid when the height of a row has changed. Row height is handled automatically as rows
		//	are rendered. Use this function only to update a row's height outside the normal rendering process.
		// inRowIndex: int
		// index of the row that has changed height
		
		this.views.renormalizeRow(inRowIndex);
		this.scroller.rowHeightChanged(inRowIndex);
	},
	// fastScroll: boolean
	//	flag modifies vertical scrolling behavior. Defaults to true but set to false for slower 
	//	scroll performance but more immediate scrolling feedback
	fastScroll: true,
	delayScroll: false,
	// scrollRedrawThreshold: int
	//	pixel distance a user must scroll vertically to trigger grid scrolling.
	scrollRedrawThreshold: (dojo.isIE ? 100 : 50),
	// scroll methods
	scrollTo: function(inTop){
		// summary:
		//	Vertically scroll the grid to a given pixel position
		// inTop: int
		//	vertical position of the grid in pixels
		if(!this.fastScroll){
			this.setScrollTop(inTop);
			return;
		}
		var delta = Math.abs(this.lastScrollTop - inTop);
		this.lastScrollTop = inTop;
		if(delta > this.scrollRedrawThreshold || this.delayScroll){
			this.delayScroll = true;
			this.scrollTop = inTop;
			this.views.setScrollTop(inTop);
			dojox.grid.jobs.job('dojoxGrid-scroll', 200, dojo.hitch(this, "finishScrollJob"));
		}else{
			this.setScrollTop(inTop);
		}
	},
	finishScrollJob: function(){
		this.delayScroll = false;
		this.setScrollTop(this.scrollTop);
	},
	setScrollTop: function(inTop){
		this.scrollTop = this.views.setScrollTop(inTop);
		this.scroller.scroll(this.scrollTop);
	},
	scrollToRow: function(inRowIndex){
		// summary:
		//	Scroll the grid to a specific row.
		// inRowIndex: int
		// grid row index
		this.setScrollTop(this.scroller.findScrollTop(inRowIndex) + 1);
	},
	// styling (private, used internally to style individual parts of a row)
	styleRowNode: function(inRowIndex, inRowNode){
		if(inRowNode){
			this.rows.styleRowNode(inRowIndex, inRowNode);
		}
	},
	// cells
	getCell: function(inIndex){
		// summary:
		//	retrieves the cell object for a given grid column.
		// inIndex: int
		// grid column index of cell to retrieve
		// returns:
		//	a grid cell
		return this.layout.cells[inIndex];
	},
	setCellWidth: function(inIndex, inUnitWidth) {
		this.getCell(inIndex).unitWidth = inUnitWidth;
	},
	getCellName: function(inCell){
		return "Cell " + inCell.index;
	},
	// sorting
	canSort: function(inSortInfo){
		// summary:
		//	determines if the grid can be sorted
		// inSortInfo: int
		//	Sort information, 1-based index of column on which to sort, positive for an ascending sort
		// and negative for a descending sort
		// returns:
		//	true if grid can be sorted on the given column in the given direction
	},
	sort: function(){
	},
	getSortAsc: function(inSortInfo){
		// summary:
		//	returns true if grid is sorted in an ascending direction.
		inSortInfo = inSortInfo == undefined ? this.sortInfo : inSortInfo;
		return Boolean(inSortInfo > 0);
	},
	getSortIndex: function(inSortInfo){
		// summary:
		//	returns the index of the column on which the grid is sorted
		inSortInfo = inSortInfo == undefined ? this.sortInfo : inSortInfo;
		return Math.abs(inSortInfo) - 1;
	},
	setSortIndex: function(inIndex, inAsc){
		// summary:
		// Sort the grid on a column in a specified direction
		// inIndex: int
		// Column index on which to sort.
		// inAsc: boolean
		// If true, sort the grid in ascending order, otherwise in descending order
		var si = inIndex +1;
		if(inAsc != undefined){
			si *= (inAsc ? 1 : -1);
		} else if(this.getSortIndex() == inIndex){
			si = -this.sortInfo;
		}
		this.setSortInfo(si);
	},
	setSortInfo: function(inSortInfo){
		if(this.canSort(inSortInfo)){
			this.sortInfo = inSortInfo;
			this.sort();
			this.update();
		}
	},
	// DOM event handler
	doKeyEvent: function(e){
		e.dispatch = 'do' + e.type;
		this.onKeyEvent(e);
	},
	// event dispatch
	//: protected
	_dispatch: function(m, e){
		if(m in this){
			return this[m](e);
		}
	},
	dispatchKeyEvent: function(e){
		this._dispatch(e.dispatch, e);
	},
	dispatchContentEvent: function(e){
		this.edit.dispatchEvent(e) || e.sourceView.dispatchContentEvent(e) || this._dispatch(e.dispatch, e);
	},
	dispatchHeaderEvent: function(e){
		e.sourceView.dispatchHeaderEvent(e) || this._dispatch('doheader' + e.type, e);
	},
	dokeydown: function(e){
		this.onKeyDown(e);
	},
	doclick: function(e){
		if(e.cellNode){
			this.onCellClick(e);
		}else{
			this.onRowClick(e);
		}
	},
	dodblclick: function(e){
		if(e.cellNode){
			this.onCellDblClick(e);
		}else{
			this.onRowDblClick(e);
		}
	},
	docontextmenu: function(e){
		if(e.cellNode){
			this.onCellContextMenu(e);
		}else{
			this.onRowContextMenu(e);
		}
	},
	doheaderclick: function(e){
		if(e.cellNode){
			this.onHeaderCellClick(e);
		}else{
			this.onHeaderClick(e);
		}
	},
	doheaderdblclick: function(e){
		if(e.cellNode){
			this.onHeaderCellDblClick(e);
		}else{
			this.onHeaderDblClick(e);
		}
	},
	doheadercontextmenu: function(e){
		if(e.cellNode){
			this.onHeaderCellContextMenu(e);
		}else{
			this.onHeaderContextMenu(e);
		}
	},
	// override to modify editing process
	doStartEdit: function(inCell, inRowIndex){
		this.onStartEdit(inCell, inRowIndex);
	},
	doApplyCellEdit: function(inValue, inRowIndex, inFieldIndex){
		this.onApplyCellEdit(inValue, inRowIndex, inFieldIndex);
	},
	doCancelEdit: function(inRowIndex){
		this.onCancelEdit(inRowIndex);
	},
	doApplyEdit: function(inRowIndex){
		this.onApplyEdit(inRowIndex);
	},
	// row editing
	addRow: function(){
		// summary:
		//	add a row to the grid.
		this.updateRowCount(this.rowCount+1);
	},
	removeSelectedRows: function(){
		// summary:
		//	remove the selected rows from the grid.
		this.updateRowCount(Math.max(0, this.rowCount - this.selection.getSelected().length));
		this.selection.clear();
	}
});

dojo.mixin(dojox.VirtualGrid.prototype, dojox.grid.publicEvents);

}

if(!dojo._hasResource["dojox.grid._data.fields"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._data.fields"] = true;
dojo.provide("dojox.grid._data.fields");

dojo.declare("dojox.grid.data.Mixer", null, {
	// summary:
	//	basic collection class that provides a default value for items
	
	constructor: function(){
		this.defaultValue = {};
		this.values = [];
	},
	count: function(){
		return this.values.length;
	},
	clear: function(){
		this.values = [];
	},
	build: function(inIndex){
		var result = dojo.mixin({owner: this}, this.defaultValue);
		result.key = inIndex;
		this.values[inIndex] = result;
		return result;
	},
	getDefault: function(){
		return this.defaultValue;
	},
	setDefault: function(inField /*[, inField2, ... inFieldN] */){
		for(var i=0, a; (a = arguments[i]); i++){
			dojo.mixin(this.defaultValue, a);
		}
	},
	get: function(inIndex){
		return this.values[inIndex] || this.build(inIndex);
	},
	_set: function(inIndex, inField /*[, inField2, ... inFieldN] */){
		// each field argument can be a single field object of an array of field objects
		var v = this.get(inIndex);
		for(var i=1; i<arguments.length; i++){
			dojo.mixin(v, arguments[i]);
		}
		this.values[inIndex] = v;
	},
	set: function(/* inIndex, inField [, inField2, ... inFieldN] | inArray */){
		if(arguments.length < 1){
			return;
		}
		var a = arguments[0];
		if(!dojo.isArray(a)){
			this._set.apply(this, arguments);
		}else{
			if(a.length && a[0]["default"]){
				this.setDefault(a.shift());
			}
			for(var i=0, l=a.length; i<l; i++){
				this._set(i, a[i]);
			}
		}
	},
	insert: function(inIndex, inProps){
		if (inIndex >= this.values.length){
			this.values[inIndex] = inProps;
		}else{
			this.values.splice(inIndex, 0, inProps);
		}
	},
	remove: function(inIndex){
		this.values.splice(inIndex, 1);
	},
	swap: function(inIndexA, inIndexB){
		dojox.grid.arraySwap(this.values, inIndexA, inIndexB);
	},
	move: function(inFromIndex, inToIndex){
		dojox.grid.arrayMove(this.values, inFromIndex, inToIndex);
	}
});

dojox.grid.data.compare = function(a, b){
	return (a > b ? 1 : (a == b ? 0 : -1));
}

dojo.declare('dojox.grid.data.Field', null, {
	constructor: function(inName){
		this.name = inName;
		this.compare = dojox.grid.data.compare;
	},
	na: dojox.grid.na
});

dojo.declare('dojox.grid.data.Fields', dojox.grid.data.Mixer, {
	constructor: function(inFieldClass){
		var fieldClass = inFieldClass ? inFieldClass : dojox.grid.data.Field;
		this.defaultValue = new fieldClass();
	},
	indexOf: function(inKey){
		for(var i=0; i<this.values.length; i++){
			var v = this.values[i];
			if(v && v.key == inKey){return i;}
		}
		return -1;
	}
});

}

if(!dojo._hasResource['dojox.grid._data.model']){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource['dojox.grid._data.model'] = true;
dojo.provide('dojox.grid._data.model');


dojo.declare("dojox.grid.data.Model", null, {
	// summary:
	//	Base abstract grid data model.
	//	Makes no assumptions about the structure of grid data.
	constructor: function(inFields, inData){
		this.observers = [];
		this.fields = new dojox.grid.data.Fields();
		if(inFields){
			this.fields.set(inFields);
		}
		this.setData(inData);
	},
	count: 0,
	updating: 0,
	// observers 
	observer: function(inObserver, inPrefix){
		this.observers.push({o: inObserver, p: inPrefix||'model' });
	},
	notObserver: function(inObserver){
		for(var i=0, m, o; (o=this.observers[i]); i++){
			if(o.o==inObserver){
				this.observers.splice(i, 1);
				return;
			}
		}
	},
	notify: function(inMsg, inArgs){
		if(!this.isUpdating()){
			var a = inArgs || [];
			for(var i=0, m, o; (o=this.observers[i]); i++){
				m = o.p + inMsg, o = o.o;
				(m in o)&&(o[m].apply(o, a));
			}
		}
	},
	// updates
	clear: function(){
		this.fields.clear();
		this.clearData();
	},
	beginUpdate: function(){
		this.updating++;
	},
	endUpdate: function(){
		if(this.updating){
			this.updating--;
		}
		/*if(this.updating){
			if(!(--this.updating)){
				this.change();
			}
		}
		}*/
	},
	isUpdating: function(){
		return Boolean(this.updating);
	},
	// data
	clearData: function(){
		this.setData(null);
	},
	// observer events
	change: function(){
		this.notify("Change", arguments);
	},
	insertion: function(/* index */){
		this.notify("Insertion", arguments);
		this.notify("Change", arguments);
	},
	removal: function(/* keys */){
		this.notify("Removal", arguments);
		this.notify("Change", arguments);
	},
	// insert
	insert: function(inData /*, index */){
		if(!this._insert.apply(this, arguments)){
			return false;
		}
		this.insertion.apply(this, dojo._toArray(arguments, 1));
		return true;
	},
	// remove
	remove: function(inData /*, index */){
		if(!this._remove.apply(this, arguments)){
			return false;
		}
		this.removal.apply(this, arguments);
		return true;
	},
	// sort
	canSort: function(/* (+|-)column_index+1, ... */){
		return this.sort != null;
	},
	makeComparator: function(inIndices){
		var idx, col, field, result = null;
		for(var i=inIndices.length-1; i>=0; i--){
			idx = inIndices[i];
			col = Math.abs(idx) - 1;
			if(col >= 0){
				field = this.fields.get(col);
				result = this.generateComparator(field.compare, field.key, idx > 0, result);
			}
		}
		return result;
	},
	sort: null,
	dummy: 0
});

dojo.declare("dojox.grid.data.Rows", dojox.grid.data.Model, {
	// observer events
	allChange: function(){
		this.notify("AllChange", arguments);
		this.notify("Change", arguments);
	},
	rowChange: function(){
		this.notify("RowChange", arguments);
	},
	datumChange: function(){
		this.notify("DatumChange", arguments);
	},
	// copyRow: function(inRowIndex); // abstract
	// update
	beginModifyRow: function(inRowIndex){
		if(!this.cache[inRowIndex]){
			this.cache[inRowIndex] = this.copyRow(inRowIndex);
		}
	},
	endModifyRow: function(inRowIndex){
		var cache = this.cache[inRowIndex];
		if(cache){
			var data = this.getRow(inRowIndex);
			if(!dojox.grid.arrayCompare(cache, data)){
				this.update(cache, data, inRowIndex);
			}
			delete this.cache[inRowIndex];
		}
	},
	cancelModifyRow: function(inRowIndex){
		var cache = this.cache[inRowIndex];
		if(cache){
			this.setRow(cache, inRowIndex);
			delete this.cache[inRowIndex];
		}
	},
	generateComparator: function(inCompare, inField, inTrueForAscend, inSubCompare){
		return function(a, b){
			var ineq = inCompare(a[inField], b[inField]);
			return ineq ? (inTrueForAscend ? ineq : -ineq) : inSubCompare && inSubCompare(a, b);
		}
	}
});

dojo.declare("dojox.grid.data.Table", dojox.grid.data.Rows, {
	// summary:
	//	Basic grid data model for static data in the form of an array of rows
	//	that are arrays of cell data
	constructor: function(){
		this.cache = [];
	},
	colCount: 0, // tables introduce cols
	data: null,
	cache: null,
	// morphology
	measure: function(){
		this.count = this.getRowCount();
		this.colCount = this.getColCount();
		this.allChange();
		//this.notify("Measure");
	},
	getRowCount: function(){
		return (this.data ? this.data.length : 0);
	},
	getColCount: function(){
		return (this.data && this.data.length ? this.data[0].length : this.fields.count());
	},
	badIndex: function(inCaller, inDescriptor){
		console.debug('dojox.grid.data.Table: badIndex');
	},
	isGoodIndex: function(inRowIndex, inColIndex){
		return (inRowIndex >= 0 && inRowIndex < this.count && (arguments.length < 2 || (inColIndex >= 0 && inColIndex < this.colCount)));
	},
	// access
	getRow: function(inRowIndex){
		return this.data[inRowIndex];
	},
	copyRow: function(inRowIndex){
		return this.getRow(inRowIndex).slice(0);
	},
	getDatum: function(inRowIndex, inColIndex){
		return this.data[inRowIndex][inColIndex];
	},
	get: function(){
		throw('Plain "get" no longer supported. Use "getRow" or "getDatum".');
	},
	setData: function(inData){
		this.data = (inData || []);
		this.allChange();
	},
	setRow: function(inData, inRowIndex){
		this.data[inRowIndex] = inData;
		this.rowChange(inData, inRowIndex);
		this.change();
	},
	setDatum: function(inDatum, inRowIndex, inColIndex){
		this.data[inRowIndex][inColIndex] = inDatum;
		this.datumChange(inDatum, inRowIndex, inColIndex);
	},
	set: function(){
		throw('Plain "set" no longer supported. Use "setData", "setRow", or "setDatum".');
	},
	setRows: function(inData, inRowIndex){
		for(var i=0, l=inData.length, r=inRowIndex; i<l; i++, r++){
			this.setRow(inData[i], r);
		}
	},
	// update
	update: function(inOldData, inNewData, inRowIndex){
		//delete this.cache[inRowIndex];	
		//this.setRow(inNewData, inRowIndex);
		return true;
	},
	// insert
	_insert: function(inData, inRowIndex){
		dojox.grid.arrayInsert(this.data, inRowIndex, inData);
		this.count++;
		return true;
	},
	// remove
	_remove: function(inKeys){
		for(var i=inKeys.length-1; i>=0; i--){
			dojox.grid.arrayRemove(this.data, inKeys[i]);
		}
		this.count -= inKeys.length;
		return true;
	},
	// sort
	sort: function(/* (+|-)column_index+1, ... */){
		this.data.sort(this.makeComparator(arguments));
	},
	swap: function(inIndexA, inIndexB){
		dojox.grid.arraySwap(this.data, inIndexA, inIndexB);
		this.rowChange(this.getRow(inIndexA), inIndexA);
		this.rowChange(this.getRow(inIndexB), inIndexB);
		this.change();
	},
	dummy: 0
});

dojo.declare("dojox.grid.data.Objects", dojox.grid.data.Table, {
	constructor: function(inFields, inData, inKey){
		if(!inFields){
			this.autoAssignFields();
		}
	},
	autoAssignFields: function(){
		var d = this.data[0], i = 0;
		for(var f in d){
			this.fields.get(i++).key = f;
		}
	},
	getDatum: function(inRowIndex, inColIndex){
		return this.data[inRowIndex][this.fields.get(inColIndex).key];
	}
});

dojo.declare("dojox.grid.data.Dynamic", dojox.grid.data.Table, {
	// summary:
	//	Grid data model for dynamic data such as data retrieved from a server.
	//	Retrieves data automatically when requested and provides notification when data is received
	constructor: function(){
		this.page = [];
		this.pages = [];
	},
	page: null,
	pages: null,
	rowsPerPage: 100,
	requests: 0,
	bop: -1,
	eop: -1,
	// data
	clearData: function(){
		this.pages = [];
		this.bop = this.eop = -1;
		this.setData([]);
	},
	getRowCount: function(){
		return this.count;
	},
	getColCount: function(){
		return this.fields.count();
	},
	setRowCount: function(inCount){
		this.count = inCount;
		this.change();
	},
	// paging
	requestsPending: function(inBoolean){
	},
	rowToPage: function(inRowIndex){
		return (this.rowsPerPage ? Math.floor(inRowIndex / this.rowsPerPage) : inRowIndex);
	},
	pageToRow: function(inPageIndex){
		return (this.rowsPerPage ? this.rowsPerPage * inPageIndex : inPageIndex);
	},
	requestRows: function(inRowIndex, inCount){
		// summary:
		//		stub. Fill in to perform actual data row fetching logic. The
		//		returning logic must provide the data back to the system via
		//		setRow
	},
	rowsProvided: function(inRowIndex, inCount){
		this.requests--;
		if(this.requests == 0){
			this.requestsPending(false);
		}
	},
	requestPage: function(inPageIndex){
		var row = this.pageToRow(inPageIndex);
		var count = Math.min(this.rowsPerPage, this.count - row);
		if(count > 0){
			this.requests++;
			this.requestsPending(true);
			setTimeout(dojo.hitch(this, "requestRows", row, count), 1);
			//this.requestRows(row, count);
		}
	},
	needPage: function(inPageIndex){
		if(!this.pages[inPageIndex]){
			this.pages[inPageIndex] = true;
			this.requestPage(inPageIndex);
		}
	},
	preparePage: function(inRowIndex, inColIndex){
		if(inRowIndex < this.bop || inRowIndex >= this.eop){
			var pageIndex = this.rowToPage(inRowIndex);
			this.needPage(pageIndex);
			this.bop = pageIndex * this.rowsPerPage;
			this.eop = this.bop + (this.rowsPerPage || this.count);
		}
	},
	isRowLoaded: function(inRowIndex){
		return Boolean(this.data[inRowIndex]);
	},
	// removal
	removePages: function(inRowIndexes){
		for(var i=0, r; ((r=inRowIndexes[i]) != undefined); i++){
			this.pages[this.rowToPage(r)] = false;
		}
		this.bop = this.eop =-1;
	},
	remove: function(inRowIndexes){
		this.removePages(inRowIndexes);
		dojox.grid.data.Table.prototype.remove.apply(this, arguments);
	},
	// access
	getRow: function(inRowIndex){
		var row = this.data[inRowIndex];
		if(!row){
			this.preparePage(inRowIndex);
		}
		return row;
	},
	getDatum: function(inRowIndex, inColIndex){
		var row = this.getRow(inRowIndex);
		return (row ? row[inColIndex] : this.fields.get(inColIndex).na);
	},
	setDatum: function(inDatum, inRowIndex, inColIndex){
		var row = this.getRow(inRowIndex);
		if(row){
			row[inColIndex] = inDatum;
			this.datumChange(inDatum, inRowIndex, inColIndex);
		}else{
			console.debug('[' + this.declaredClass + '] dojox.grid.data.dynamic.set: cannot set data on an non-loaded row');
		}
	},
	// sort
	canSort: function(){
		return false;
	}
});

// FIXME: deprecated: (included for backward compatibility only)
dojox.grid.data.table = dojox.grid.data.Table;
dojox.grid.data.dynamic = dojox.grid.data.Dyanamic;

// we treat dojo.data stores as dynamic stores because no matter how they got
// here, they should always fill that contract
dojo.declare("dojox.grid.data.DojoData", dojox.grid.data.Dynamic, {
	//	summary:
	//		A grid data model for dynamic data retreived from a store which
	//		implements the dojo.data API set. Retrieves data automatically when
	//		requested and provides notification when data is received
	//	description:
	//		This store subclasses the Dynamic grid data object in order to
	//		provide paginated data access support, notification and view
	//		updates for stores which support those features, and simple
	//		field/column mapping for all dojo.data stores.
	constructor: function(inFields, inData, args){
		this.count = 1;
		this._rowIdentities = {};
		if(args){
			dojo.mixin(this, args);
		}
		if(this.store){
			// NOTE: we assume Read and Identity APIs for all stores!
			var f = this.store.getFeatures();
			this._canNotify = f['dojo.data.api.Notification'];
			this._canWrite = f['dojo.data.api.Write'];

			if(this._canNotify){
				dojo.connect(this.store, "onSet", this, "_storeDatumChange");
			}
		}
	},
	markupFactory: function(args, node){
		return new dojox.grid.data.DojoData(null, null, args);
	},
	query: { name: "*" }, // default, stupid query
	store: null,
	_canNotify: false,
	_canWrite: false,
	_rowIdentities: {},
	clientSort: false,
	// data
	setData: function(inData){
		this.store = inData;
		this.data = [];
		this.allChange();
	},
	setRowCount: function(inCount){
		//console.debug("inCount:", inCount);
		this.count = inCount;
		this.allChange();
	},
	beginReturn: function(inCount){
		if(this.count != inCount){
			// this.setRowCount(0);
			// this.clear();
			// console.debug(this.count, inCount);
			this.setRowCount(inCount);
		}
	},
	_setupFields: function(dataItem){
		// abort if we already have setup fields
		if(this.fields._nameMaps){
			return;
		}
		// set up field/index mappings
		var m = {};
		//console.debug("setting up fields", m);
		var fields = dojo.map(this.store.getAttributes(dataItem),
			function(item, idx){ 
				m[item] = idx;
				m[idx+".idx"] = item;
				// name == display name, key = property name
				return { name: item, key: item };
			},
			this
		);
		this.fields._nameMaps = m;
		// console.debug("new fields:", fields);
		this.fields.set(fields);
		this.notify("FieldsChange");
	},
	_getRowFromItem: function(item){
		// gets us the row object (and row index) of an item
	},
	processRows: function(items, store){
		// console.debug(arguments);
		if(!items){ return; }
		this._setupFields(items[0]);
		dojo.forEach(items, function(item, idx){
			var row = {}; 
			row.__dojo_data_item = item;
			dojo.forEach(this.fields.values, function(a){
				row[a.name] = this.store.getValue(item, a.name)||"";
			}, this);
			// FIXME: where else do we need to keep this in sync?
			this._rowIdentities[this.store.getIdentity(item)] = store.start+idx;
			this.setRow(row, store.start+idx);
		}, this);
		// FIXME: 
		//	Q: scott, steve, how the hell do we actually get this to update
		//		the visible UI for these rows?
		//	A: the goal is that Grid automatically updates to reflect changes
		//		in model. In this case, setRow -> rowChanged -> (observed by) Grid -> modelRowChange -> updateRow
	},
	// request data 
	requestRows: function(inRowIndex, inCount){
		var row  = inRowIndex || 0;
		var params = { 
			start: row,
			count: this.rowsPerPage,
			query: this.query,
			onBegin: dojo.hitch(this, "beginReturn"),
			//	onItem: dojo.hitch(console, "debug"),
			onComplete: dojo.hitch(this, "processRows") // add to deferred?
		}
		// console.debug("requestRows:", row, this.rowsPerPage);
		this.store.fetch(params);
	},
	getDatum: function(inRowIndex, inColIndex){
		//console.debug("getDatum", inRowIndex, inColIndex);
		var row = this.getRow(inRowIndex);
		var field = this.fields.values[inColIndex];
		return row && field ? row[field.name] : field ? field.na : '?';
		//var idx = row && this.fields._nameMaps[inColIndex+".idx"];
		//return (row ? row[idx] : this.fields.get(inColIndex).na);
	},
	setDatum: function(inDatum, inRowIndex, inColIndex){
		var n = this.fields._nameMaps[inColIndex+".idx"];
		// console.debug("setDatum:", "n:"+n, inDatum, inRowIndex, inColIndex);
		if(n){
			this.data[inRowIndex][n] = inDatum;
			this.datumChange(inDatum, inRowIndex, inColIndex);
		}
	},
	// modification, update and store eventing
	copyRow: function(inRowIndex){
		var row = {};
		var backstop = {};
		var src = this.getRow(inRowIndex);
		for(var x in src){
			if(src[x] != backstop[x]){
				row[x] = src[x];
			}
		}
		return row;
	},
	_attrCompare: function(cache, data){
		dojo.forEach(this.fields.values, function(a){
			if(cache[a.name] != data[a.name]){ return false; }
		}, this);
		return true;
	},
	endModifyRow: function(inRowIndex){
		var cache = this.cache[inRowIndex];
		if(cache){
			var data = this.getRow(inRowIndex);
			if(!this._attrCompare(cache, data)){
				this.update(cache, data, inRowIndex);
			}
			delete this.cache[inRowIndex];
		}
	},
	cancelModifyRow: function(inRowIndex){
		// console.debug("cancelModifyRow", arguments);
		var cache = this.cache[inRowIndex];
		if(cache){
			this.setRow(cache, inRowIndex);
			delete this.cache[inRowIndex];
		}
	},
	_storeDatumChange: function(item, attr, oldVal, newVal){
		// the store has changed some data under us, need to update the display
		var rowId = this._rowIdentities[this.store.getIdentity(item)];
		var row = this.getRow(rowId);
		row[attr] = newVal;
		var colId = this.fields._nameMaps[attr];
		this.notify("DatumChange", [ newVal, rowId, colId ]);
	},
	datumChange: function(value, rowIdx, colIdx){
		if(this._canWrite){
			// we're chaning some data, which means we need to write back
			var row = this.getRow(rowIdx);
			var field = this.fields._nameMaps[colIdx+".idx"];
			this.store.setValue(row.__dojo_data_item, field, value);
			// we don't need to call DatumChange, an eventing store will tell
			// us about the row change events
		}else{
			// we can't write back, so just go ahead and change our local copy
			// of the data
			this.notify("DatumChange", arguments);
		}
	},
	insertion: function(/* index */){
		console.debug("Insertion", arguments);
		this.notify("Insertion", arguments);
		this.notify("Change", arguments);
	},
	removal: function(/* keys */){
		console.debug("Removal", arguments);
		this.notify("Removal", arguments);
		this.notify("Change", arguments);
	},
	// sort
	canSort: function(){
		// Q: Return true and re-issue the queries?
		// A: Return true only. Re-issue the query in 'sort'.
		return this.clientSort;
	}
});

}

if(!dojo._hasResource["dojox.grid._data.editors"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid._data.editors"] = true;
dojo.provide("dojox.grid._data.editors");
dojo.provide("dojox.grid.editors");

dojo.declare("dojox.grid.editors.Base", null, {
	// summary:
	//	base grid editor class. Other grid editors should inherited from this class.
	constructor: function(inCell){
		this.cell = inCell;
	},
	//private
	_valueProp: "value",
	_formatPending: false,
	format: function(inDatum, inRowIndex){
		// summary:
		//	formats the cell for editing
		// inDatum: anything
		//	cell data to edit
		// inRowIndex: int
		//	grid row index
		// returns: string of html to place in grid cell
	},
	//protected
	needFormatNode: function(inDatum, inRowIndex){
		this._formatPending = true;
		dojox.grid.whenIdle(this, "_formatNode", inDatum, inRowIndex);
	},
	cancelFormatNode: function(){
		this._formatPending = false;
	},
	//private
	_formatNode: function(inDatum, inRowIndex){
		if(this._formatPending){
			this._formatPending = false;
			// make cell selectable
			dojo.setSelectable(this.cell.grid.domNode, true);
			this.formatNode(this.getNode(inRowIndex), inDatum, inRowIndex);
		}
	},
	//protected
	getNode: function(inRowIndex){
		return (this.cell.getNode(inRowIndex) || 0).firstChild || 0;
	},
	formatNode: function(inNode, inDatum, inRowIndex){
		// summary:
		//	format the editing dom node. Use when editor is a widget.
		// inNode: dom node
		// dom node for the editor
		// inDatum: anything
		//	cell data to edit
		// inRowIndex: int
		//	grid row index
		if(dojo.isIE){
			// IE sux bad
			dojox.grid.whenIdle(this, "focus", inRowIndex, inNode);
		}else{
			this.focus(inRowIndex, inNode);
		}
	},
	dispatchEvent: function(m, e){
		if(m in this){
			return this[m](e);
		}
	},
	//public
	getValue: function(inRowIndex){
		// summary:
		//	returns value entered into editor
		// inRowIndex: int
		// grid row index
		// returns:
		//	value of editor
		return this.getNode(inRowIndex)[this._valueProp];
	},
	setValue: function(inRowIndex, inValue){
		// summary:
		//	set the value of the grid editor
		// inRowIndex: int
		// grid row index
		// inValue: anything
		//	value of editor
		var n = this.getNode(inRowIndex);
		if(n){
			n[this._valueProp] = inValue
		};
	},
	focus: function(inRowIndex, inNode){
		// summary:
		//	focus the grid editor
		// inRowIndex: int
		// grid row index
		// inNode: dom node
		//	editor node
		dojox.grid.focusSelectNode(inNode || this.getNode(inRowIndex));
	},
	save: function(inRowIndex){
		// summary:
		//	save editor state
		// inRowIndex: int
		// grid row index
		this.value = this.value || this.getValue(inRowIndex);
		//console.log("save", this.value, inCell.index, inRowIndex);
	},
	restore: function(inRowIndex){
		// summary:
		//	restore editor state
		// inRowIndex: int
		// grid row index
		this.setValue(inRowIndex, this.value);
		//console.log("restore", this.value, inCell.index, inRowIndex);
	},
	//protected
	_finish: function(inRowIndex){
		// summary:
		//	called when editing is completed to clean up editor
		// inRowIndex: int
		// grid row index
		dojo.setSelectable(this.cell.grid.domNode, false);
		this.cancelFormatNode(this.cell);
	},
	//public
	apply: function(inRowIndex){
		// summary:
		//	apply edit from cell editor
		// inRowIndex: int
		// grid row index
		this.cell.applyEdit(this.getValue(inRowIndex), inRowIndex);
		this._finish(inRowIndex);
	},
	cancel: function(inRowIndex){
		// summary:
		//	cancel cell edit
		// inRowIndex: int
		// grid row index
		this.cell.cancelEdit(inRowIndex);
		this._finish(inRowIndex);
	}
});
dojox.grid.editors.base = dojox.grid.editors.Base; // back-compat

dojo.declare("dojox.grid.editors.Input", dojox.grid.editors.Base, {
	// summary
	// grid cell editor that provides a standard text input box
	constructor: function(inCell){
		this.keyFilter = this.keyFilter || this.cell.keyFilter;
	},
	// keyFilter: object
	// optional regex for disallowing keypresses
	keyFilter: null,
	format: function(inDatum, inRowIndex){
		this.needFormatNode(inDatum, inRowIndex);
		return '<input class="dojoxGrid-input" type="text" value="' + inDatum + '">';
	},
	formatNode: function(inNode, inDatum, inRowIndex){
		this.inherited(arguments);
		// FIXME: feels too specific for this interface
		this.cell.registerOnBlur(inNode, inRowIndex);
	},
	doKey: function(e){
		if(this.keyFilter){
			var key = String.fromCharCode(e.charCode);
			if(key.search(this.keyFilter) == -1){
				dojo.stopEvent(e);
			}
		}
	},
	_finish: function(inRowIndex){
		this.inherited(arguments);
		var n = this.getNode(inRowIndex);
		try{
			dojox.grid.fire(n, "blur");
		}catch(e){}
	}
});
dojox.grid.editors.input = dojox.grid.editors.Input; // back compat

dojo.declare("dojox.grid.editors.Select", dojox.grid.editors.Input, {
	// summary:
	// grid cell editor that provides a standard select
	// options: text of each item
	// values: value for each item
	// returnIndex: editor returns only the index of the selected option and not the value
	constructor: function(inCell){
		this.options = this.options || this.cell.options;
		this.values = this.values || this.cell.values || this.options;
	},
	format: function(inDatum, inRowIndex){
		this.needFormatNode(inDatum, inRowIndex);
		var h = [ '<select class="dojoxGrid-select">' ];
		for (var i=0, o, v; (o=this.options[i])&&(v=this.values[i]); i++){
			h.push("<option", (inDatum==o ? ' selected' : ''), /*' value="' + v + '"',*/ ">", o, "</option>");
		}
		h.push('</select>');
		return h.join('');
	},
	getValue: function(inRowIndex){
		var n = this.getNode(inRowIndex);
		if(n){
			var i = n.selectedIndex, o = n.options[i];
			return this.cell.returnIndex ? i : o.value || o.innerHTML;
		}
	}
});
dojox.grid.editors.select = dojox.grid.editors.Select; // back compat

dojo.declare("dojox.grid.editors.AlwaysOn", dojox.grid.editors.Input, {
	// summary:
	// grid cell editor that is always on, regardless of grid editing state
	// alwaysOn: boolean
	// flag to use editor to format grid cell regardless of editing state.
	alwaysOn: true,
	_formatNode: function(inDatum, inRowIndex){
		this.formatNode(this.getNode(inRowIndex), inDatum, inRowIndex);
	},
	applyStaticValue: function(inRowIndex){
		var e = this.cell.grid.edit;
		e.applyCellEdit(this.getValue(inRowIndex), this.cell, inRowIndex);
		e.start(this.cell, inRowIndex, true);
	}
});
dojox.grid.editors.alwaysOn = dojox.grid.editors.AlwaysOn; // back-compat

dojo.declare("dojox.grid.editors.Bool", dojox.grid.editors.AlwaysOn, {
	// summary:
	// grid cell editor that provides a standard checkbox that is always on
	_valueProp: "checked",
	format: function(inDatum, inRowIndex){
		return '<input class="dojoxGrid-input" type="checkbox"' + (inDatum ? ' checked="checked"' : '') + ' style="width: auto" />';
	},
	doclick: function(e){
		if(e.target.tagName == 'INPUT'){
			this.applyStaticValue(e.rowIndex);
		}
	}
});
dojox.grid.editors.bool = dojox.grid.editors.Bool; // back-compat

}

if(!dojo._hasResource["dojox.grid.Grid"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.grid.Grid"] = true;
dojo.provide("dojox.grid.Grid");




dojo.declare('dojox.Grid', dojox.VirtualGrid, {
	//	summary:
	//		A grid widget with virtual scrolling, cell editing, complex rows,
	//		sorting, fixed columns, sizeable columns, etc.
	//	description:
	//		Grid is a subclass of VirtualGrid, providing binding to a data
	//		store.
	//	example:
	//		define the grid structure:
	//	|	var structure = [ // array of view objects
	//	|		{ cells: [// array of rows, a row is an array of cells
	//	|			[	{ name: "Alpha", width: 6 }, 
	//	|				{ name: "Beta" }, 
	//	|				{ name: "Gamma", get: formatFunction }
	//	|			]
	//	|		]}
	//	|	];
	//	  	
	//		define a grid data model
	//	|	var model = new dojox.grid.data.table(null, data);
	//	|
	//	|	<div id="grid" model="model" structure="structure" 
	//	|		dojoType="dojox.VirtualGrid"></div>
	//	

	//	model:
	//		string or object grid data model
	model: 'dojox.grid.data.Table',
	// life cycle
	postCreate: function(){
		if(this.model){
			var m = this.model;
			if(dojo.isString(m)){
				m = dojo.getObject(m);
			}
			this.model = (dojo.isFunction(m)) ? new m() : m;
			this._setModel(this.model);
		}
		this.inherited(arguments);
	},
	destroy: function(){
		this.setModel(null);
		this.inherited(arguments);
	},
	// structure
	_structureChanged: function() {
		this.indexCellFields();
		this.inherited(arguments);
	},
	// model
	_setModel: function(inModel){
		// if(!inModel){ return; }
		this.model = inModel;
		if(this.model){
			this.model.observer(this);
			this.model.measure();
			this.indexCellFields();
		}
	},
	setModel: function(inModel){
		// summary:
		//		set the grid's data model
		// inModel:
		//		model object, usually an instance of a dojox.grid.data.Model
		//		subclass
		if(this.model){
			this.model.notObserver(this);
		}
		this._setModel(inModel);
	},
	// data socket (called in cell's context)
	get: function(inRowIndex){
		return this.grid.model.getDatum(inRowIndex, this.fieldIndex);
	},
	// model modifications
	modelAllChange: function(){
		this.rowCount = (this.model ? this.model.getRowCount() : 0);
		this.updateRowCount(this.rowCount);
	},
	modelRowChange: function(inData, inRowIndex){
		this.updateRow(inRowIndex);
	},
	modelDatumChange: function(inDatum, inRowIndex, inFieldIndex){
		this.updateRow(inRowIndex);
	},
	modelFieldsChange: function() {
		this.indexCellFields();
		this.render();
	},
	// model insertion
	modelInsertion: function(inRowIndex){
		this.updateRowCount(this.model.getRowCount());
	},
	// model removal
	modelRemoval: function(inKeys){
		this.updateRowCount(this.model.getRowCount());
	},
	// cells
	getCellName: function(inCell){
		var v = this.model.fields.values, i = inCell.fieldIndex;
		return i>=0 && i<v.length && v[i].name || this.inherited(arguments);
	},
	indexCellFields: function(){
		var cells = this.layout.cells;
		for(var i=0, c; cells && (c=cells[i]); i++){
			if(dojo.isString(c.field)){
				c.fieldIndex = this.model.fields.indexOf(c.field);
			}
		}
	},
	// utility
	refresh: function(){
		// summary:
		//	re-render the grid, getting new data from the model
		this.edit.cancel();
		this.model.measure();
	},
	// sorting
	canSort: function(inSortInfo){
		var f = this.getSortField(inSortInfo);
		// 0 is not a valid sort field
		return f && this.model.canSort(f);
	},
	getSortField: function(inSortInfo){
		// summary:
		//	retrieves the model field on which to sort data.
		// inSortInfo: int
		//	1-based grid column index; positive if sort is ascending, otherwise negative
		var c = this.getCell(this.getSortIndex(inSortInfo));
		// we expect c.fieldIndex == -1 for non model fields
		// that yields a getSortField value of 0, which can be detected as invalid
		return (c.fieldIndex+1) * (this.sortInfo > 0 ? 1 : -1);
	},
	sort: function(){
		this.edit.apply();
		this.model.sort(this.getSortField());
	},
	// row editing
	addRow: function(inRowData, inIndex){
		this.edit.apply();
		var i = inIndex || -1;
		if(i<0){
			i = this.selection.getFirstSelected() || 0;
		}
		if(i<0){
			i = 0;
		}
		this.model.insert(inRowData, i);
		this.model.beginModifyRow(i);
		// begin editing row
		// FIXME: add to edit
		for(var j=0, c; ((c=this.getCell(j)) && !c.editor); j++){}
		if(c&&c.editor){
			this.edit.setEditCell(c, i);
		}
	},
	removeSelectedRows: function(){
		this.edit.apply();
		var s = this.selection.getSelected();
		if(s.length){
			this.model.remove(s);
			this.selection.clear();
		}
	},
	//: protected
	// editing
	canEdit: function(inCell, inRowIndex){
		// summary: 
		//	determines if a given cell may be edited
		//	inCell: grid cell
		// inRowIndex: grid row index
		// returns: true if given cell may be edited
		return (this.model.canModify ? this.model.canModify(inRowIndex) : true);
	},
	doStartEdit: function(inCell, inRowIndex){
		var edit = this.canEdit(inCell, inRowIndex);
		if(edit){
			this.model.beginModifyRow(inRowIndex);
			this.onStartEdit(inCell, inRowIndex);
		}
		return edit;
	},
	doApplyCellEdit: function(inValue, inRowIndex, inFieldIndex){
		this.model.setDatum(inValue, inRowIndex, inFieldIndex);
		this.onApplyCellEdit(inValue, inRowIndex, inFieldIndex);
	},
	doCancelEdit: function(inRowIndex){
		this.model.cancelModifyRow(inRowIndex);
		this.onCancelEdit.apply(this, arguments);
	},
	doApplyEdit: function(inRowIndex){
		this.model.endModifyRow(inRowIndex);
		this.onApplyEdit(inRowIndex);
	},
	// Perform row styling 
	styleRowState: function(inRow){
		if(this.model.getState){
			var states=this.model.getState(inRow.index), c='';
			for(var i=0, ss=["inflight", "error", "inserting"], s; s=ss[i]; i++){
				if(states[s]){
					c = ' dojoxGrid-row-' + s;
					break;
				}
			}
			inRow.customClasses += c;
		}
	},
	onStyleRow: function(inRow){
		this.styleRowState(inRow);
		this.inherited(arguments);
	},
	junk: 0
});

}

if(!dojo._hasResource["dojox.widget.Toaster"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.widget.Toaster"] = true;
dojo.provide("dojox.widget.Toaster");






// This is mostly taken from Jesse Kuhnert's MessageNotifier.
// Modified by Bryan Forbes to support topics and a variable delay.
// Modified by Karl Tiedt to support 0 duration messages that require user interaction and message stacking

dojo.declare("dojox.widget.Toaster", [dijit._Widget, dijit._Templated], {
		// summary
		//		Message that slides in from the corner of the screen, used for notifications
		//		like "new email".
		templateString: '<div dojoAttachPoint="clipNode"><div dojoAttachPoint="containerNode" dojoAttachEvent="onclick:onSelect"><div dojoAttachPoint="contentNode"></div></div></div>',

		// messageTopic: String
		//		Name of topic; anything published to this topic will be displayed as a message.
		//		Message format is either String or an object like
		//		{message: "hello word", type: "error", duration: 500}
		messageTopic: "",

		_uniqueId: 0,
		
		// messageTypes: Enumeration
		//		Possible message types.
		messageTypes: {
			MESSAGE: "message",
			WARNING: "warning",
			ERROR: "error",
			FATAL: "fatal"
		},

		// defaultType: String
		//		If message type isn't specified (see "messageTopic" parameter),
		//		then display message as this type.
		//		Possible values in messageTypes enumeration ("message", "warning", "error", "fatal")
		defaultType: "message",

		// positionDirection: String
		//		Position from which message slides into screen, one of
		//		["br-up", "br-left", "bl-up", "bl-right", "tr-down", "tr-left", "tl-down", "tl-right"]
		positionDirection: "br-up",
		
		// positionDirectionTypes: Array
		//		Possible values for positionDirection parameter
		positionDirectionTypes: ["br-up", "br-left", "bl-up", "bl-right", "tr-down", "tr-left", "tl-down", "tl-right"],

		// duration: Integer
		//		Number of milliseconds to show message
		duration: "2000",

		//separator: String
		//		String used to separate messages if consecutive calls are made to setContent before previous messages go away
		separator: "<hr></hr>",

		postCreate: function(){
			dojox.widget.Toaster.superclass.postCreate.apply(this);
			this.hide();

			this.clipNode.className = "dijitToasterClip";
			this.containerNode.className += " dijitToasterContainer";
			this.contentNode.className = "dijitToasterContent";
			if(this.messageTopic){
				dojo.subscribe(this.messageTopic, this, "_handleMessage");
			}
		},

		_handleMessage: function(/*String|Object*/message){
			if(dojo.isString(message)){
				this.setContent(message);
			}else{
				this.setContent(message.message, message.type, message.duration);
			}
		},

		setContent: function(/*String*/message, /*String*/messageType, /*int?*/duration){
			// summary
			//		sets and displays the given message and show duration
			// message:
			//		the message
			// messageType:
			//		type of message; possible values in messageTypes enumeration ("message", "warning", "error", "fatal")
			// duration:
			//		duration in milliseconds to display message before removing it. Widget has default value.
			duration = duration||this.duration;
			// sync animations so there are no ghosted fades and such
			if(this.slideAnim){
				if(this.slideAnim.status() != "playing"){
					this.slideAnim.stop();
				}
				if(this.slideAnim.status() == "playing" || (this.fadeAnim && this.fadeAnim.status() == "playing")){
					setTimeout(dojo.hitch(this, function(){
						this.setContent(message, messageType);
					}), 50);
					return;
				}
			}

			var capitalize = function(word){
				return word.substring(0,1).toUpperCase() + word.substring(1);
			};

			// determine type of content and apply appropriately
			for(var type in this.messageTypes){
				dojo.removeClass(this.containerNode, "dijitToaster" + capitalize(this.messageTypes[type]));
			}

			dojo.style(this.containerNode, "opacity", 1);

			if(message && this.isVisible){
				message = this.contentNode.innerHTML + this.separator + message;
			}
			this.contentNode.innerHTML = message;

			dojo.addClass(this.containerNode, "dijitToaster" + capitalize(messageType || this.defaultType));

			// now do funky animation of widget appearing from
			// bottom right of page and up
			this.show();
			var nodeSize = dojo.marginBox(this.containerNode);
			
			if(this.isVisible){
				this._placeClip();
			}else{
				var style = this.containerNode.style;
				var pd = this.positionDirection;
				// sets up initial position of container node and slide-out direction
				if(pd.indexOf("-up") >= 0){
					style.left=0+"px";
					style.top=nodeSize.h + 10 + "px";
				}else if(pd.indexOf("-left") >= 0){
					style.left=nodeSize.w + 10 +"px";
					style.top=0+"px";
				}else if(pd.indexOf("-right") >= 0){
					style.left = 0 - nodeSize.w - 10 + "px";
					style.top = 0+"px";
				}else if(pd.indexOf("-down") >= 0){
					style.left = 0+"px";
					style.top = 0 - nodeSize.h - 10 + "px";
				}else{
					throw new Error(this.id + ".positionDirection is invalid: " + pd);
				}

				this.slideAnim = dojo.fx.slideTo({
					node: this.containerNode,
					top: 0, left: 0,
					duration: 450});
				dojo.connect(this.slideAnim, "onEnd", this, function(nodes, anim){
						//we build the fadeAnim here so we dont have to duplicate it later
						// can't do a fadeHide because we're fading the
						// inner node rather than the clipping node
						this.fadeAnim = dojo.fadeOut({
							node: this.containerNode,
							duration: 1000});
						dojo.connect(this.fadeAnim, "onEnd", this, function(evt){
							this.isVisible = false;
							this.hide();
						});
						//if duration == 0 we keep the message displayed until clicked
						//TODO: fix so that if a duration > 0 is displayed when a duration==0 is appended to it, the fadeOut is canceled
						if(duration>0){
							setTimeout(dojo.hitch(this, function(evt){
								// we must hide the iframe in order to fade
								// TODO: figure out how to fade with a BackgroundIframe
								if(this.bgIframe && this.bgIframe.iframe){
									this.bgIframe.iframe.style.display="none";
								}
								this.fadeAnim.play();
							}), duration);
						}else{
							dojo.connect(this, 'onSelect', this, function(evt){
								this.fadeAnim.play();
							});
						}
						this.isVisible = true;
					});
				this.slideAnim.play();
			}
		},

		_placeClip: function(){
			var view = dijit.getViewport();

			var nodeSize = dojo.marginBox(this.containerNode);

			var style = this.clipNode.style;
			// sets up the size of the clipping node
			style.height = nodeSize.h+"px";
			style.width = nodeSize.w+"px";

			// sets up the position of the clipping node
			var pd = this.positionDirection;
			if(pd.match(/^t/)){
				style.top = view.t+"px";
			}else if(pd.match(/^b/)){
				style.top = (view.h - nodeSize.h - 2 + view.t)+"px";
			}
			if(pd.match(/^[tb]r-/)){
				style.left = (view.w - nodeSize.w - 1 - view.l)+"px";
			}else if(pd.match(/^[tb]l-/)){
				style.left = 0 + "px";
			}

			style.clip = "rect(0px, " + nodeSize.w + "px, " + nodeSize.h + "px, 0px)";
			if(dojo.isIE){
				if(!this.bgIframe){
					this.clipNode.id = "__dojoXToaster_"+this._uniqueId++;
					this.bgIframe = new dijit.BackgroundIframe(this.clipNode);
//TODO					this.bgIframe.setZIndex(this.clipNode);
				}
//TODO				this.bgIframe.onResized();
				var iframe = this.bgIframe.iframe;
				iframe && (iframe.style.display="block");
			}
		},

		onSelect: function(/*Event*/e){
			// summary: callback for when user clicks the message
		},

		show: function(){
			// summary: show the Toaster
			dojo.style(this.containerNode, 'display', '');

			this._placeClip();

			if(!this._scrollConnected){
				this._scrollConnected = dojo.connect(window, "onscroll", this, this._placeClip);
			}
		},

		hide: function(){
			// summary: hide the Toaster

			//Q: ALP: I didn't port all the toggler stuff from d.w.HtmlWidget.  Is it needed? Ditto for show.
			dojo.style(this.containerNode, 'display', 'none');

			if(this._scrollConnected){
				dojo.disconnect(this._scrollConnected);
				this._scrollConnected = false;
			}

			dojo.style(this.containerNode, "opacity", 1);
		}
	}
);

}

if(!dojo._hasResource["dojox.layout.ResizeHandle"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.layout.ResizeHandle"] = true;
dojo.provide("dojox.layout.ResizeHandle");
dojo.experimental("dojox.layout.ResizeHandle"); 


 
 

dojo.declare("dojox.layout.ResizeHandle", [dijit._Widget, dijit._Templated], {
	// summary
	//	The handle on the bottom-right corner of FloatingPane or other widgets that allows
	//	the widget to be resized.
	//	Typically not used directly.

	// targetId: String
	//	id of the Widget OR DomNode that I will size
	targetId: '',

	// targetContainer: DomNode
	//	over-ride targetId and attch this handle directly to a reference of a DomNode
	targetContainer: null, 

	// resizeAxis: String
	//	one of: x|y|xy limit resizing to a single axis, default to xy ... 
	resizeAxis: "xy",

	// activeResize: Boolean
	// 	if true, node will size realtime with mouse movement, 
	//	if false, node will create virtual node, and only resize target on mouseUp
	activeResize: false,
	
	// activeResizeClass: String
	//	css class applied to virtual resize node. 
	activeResizeClass: 'dojoxResizeHandleClone',

	// animateSizing: Boolean
	//	only applicable if activeResize = false. onMouseup, animate the node to the
	//	new size
	animateSizing: true,
	
	// animateMethod: String
	// 	one of "chain" or "combine" ... visual effect only. combine will "scale" 
	// 	node to size, "chain" will alter width, then height
	animateMethod: 'chain',

	// animateDuration: Integer
	//	time in MS to run sizing animation. if animateMethod="chain", total animation 
	//	playtime is 2*animateDuration
	animateDuration: 225,

	// minHeight: Integer
	//	smallest height in px resized node can be
	minHeight: 100,

	// minWidth: Integer
	//	smallest width in px resize node can be
	minWidth: 100,

	// resize handle template, fairly easy to override: 
	templateString: '<div dojoAttachPoint="resizeHandle" class="dojoxResizeHandle"><div></div></div>',

	// private propteries and holders
	_isSizing: false,
	_connects: [],
	_activeResizeNode: null,	
	_activeResizeLastEvent: null,
	// defaults to match default resizeAxis. set resizeAxis variable to modify. 
	_resizeX: true,
	_resizeY: true,


	postCreate: function(){
		// summary: setup our one major listener upon creation
		dojo.connect(this.resizeHandle, "onmousedown", this, "_beginSizing");
		if(!this.activeResize){ 
			this._activeResizeNode = document.createElement('div');
			dojo.addClass(this._activeResizeNode,this.activeResizeClass); 
		}else{ this.animateSizing = false; } 	

		if (!this.minSize) { 
			this.minSize = { w: this.minWidth, h: this.minHeight };
		}
		// should we modify the css for the cursor hover to n-resize nw-resize and w-resize?
		this._resizeX = this._resizeY = false; 
		switch (this.resizeAxis.toLowerCase()) {
		case "xy" : 
			this._resizeX = this._resizeY = true; 
			// FIXME: need logic to determine NW or NE class to see
			// based on which [todo] corner is clicked
			dojo.addClass(this.resizeHandle,"dojoxResizeNW"); 
			break; 
		case "x" : 
			this._resizeX = true; 
			dojo.addClass(this.resizeHandle,"dojoxResizeW");
			break;
		case "y" : 
			this._resizeY = true; 
			dojo.addClass(this.resizeHandle,"dojoxResizeN");
			break;
		}
	},

	_beginSizing: function(/*Event*/ e){
		// summary: setup movement listeners and calculate initial size
		
		if (this._isSizing){ return false; }

		this.targetWidget = dijit.byId(this.targetId);

		// FIXME: resizing widgets does weird things, disable virtual resizing for now:
		if (this.targetWidget) { this.activeResize = true; } 

		this.targetDomNode = this.targetWidget ? this.targetWidget.domNode : dojo.byId(this.targetId);
		if (this.targetContainer) { this.targetDomNode = this.targetContainer; } 
		if (!this.targetDomNode){ return; }

		if (!this.activeResize) {
			this.targetDomNode.appendChild(this._activeResizeNode); 
			dojo.fadeIn({ node: this._activeResizeNode, duration:120, 
				beforeBegin: dojo.hitch(this,function(){
					this._activeResizeNode.style.display=''; 
				})
			}).play(); 
		}

		this._isSizing = true;
		this.startPoint  = {'x':e.clientX, 'y':e.clientY};

		// FIXME: this is funky: marginBox adds height, contentBox ignores padding (expected, but foo!)
		var mb = (this.targetWidget) ? dojo.marginBox(this.targetDomNode) : dojo.contentBox(this.targetDomNode);  
		this.startSize  = { 'w':mb.w, 'h':mb.h };

		this._connects = []; 
		this._connects.push(dojo.connect(document,"onmousemove",this,"_updateSizing")); 
		this._connects.push(dojo.connect(document,"onmouseup", this, "_endSizing"));

		e.preventDefault();
	},

	_updateSizing: function(/*Event*/ e){
		// summary: called when moving the ResizeHandle ... determines 
		//	new size based on settings/position and sets styles.

		if(this.activeResize){
			this._changeSizing(e);
		}else{
			var tmp = this._getNewCoords(e);	
			if(tmp === false){ return; }
			dojo.style(this._activeResizeNode,"width",tmp.width+"px");
			dojo.style(this._activeResizeNode,"height",tmp.height+"px"); 
			this._activeResizeNode.style.display=''; 
		}
	},

	_getNewCoords: function(/* Event */ e){
		
		// On IE, if you move the mouse above/to the left of the object being resized,
		// sometimes clientX/Y aren't set, apparently.  Just ignore the event.
		try{
			if(!e.clientX  || !e.clientY){ return false; }
		}catch(e){
			// sometimes you get an exception accessing above fields...
			return false;
		}
		this._activeResizeLastEvent = e; 

		var dx = this.startPoint.x - e.clientX;
		var dy = this.startPoint.y - e.clientY;
		
		var newW = (this._resizeX) ? this.startSize.w - dx : this.startSize.w;
		var newH = (this._resizeY) ? this.startSize.h - dy : this.startSize.h;

		// minimum size check
		if(this.minSize){
			//var mb = dojo.marginBox(this.targetDomNode);
			if(newW < this.minSize.w){
				newW = this.minSize.w;
			}
			if(newH < this.minSize.h){
				newH = this.minSize.h;
			}
		}
		return {width:newW, height:newH};  // Object
	},
	
	_changeSizing: function(/*Event*/ e){
		// summary: apply sizing information based on information in (e) to attached node
		var tmp = this._getNewCoords(e);
		if(tmp===false){ return; }

		if(this.targetWidget && typeof this.targetWidget.resize == "function"){ 
			this.targetWidget.resize({ w: tmp.width, h: tmp.height });
		}else{
			if(this.animateSizing){
				var anim = dojo.fx[this.animateMethod]([
					dojo.animateProperty({
						node: this.targetDomNode,
						properties: { 
							width: { start: this.startSize.w, end: tmp.width, unit:'px' } 
						},	
						duration: this.animateDuration
					}),
					dojo.animateProperty({
						node: this.targetDomNode,
						properties: { 
							height: { start: this.startSize.h, end: tmp.height, unit:'px' }
						},
						duration: this.animateDuration
					})
				]);
				anim.play();
			}else{
				dojo.style(this.targetDomNode,"width",tmp.width+"px"); 
				dojo.style(this.targetDomNode,"height",tmp.height+"px");
			}
		}	
		e.preventDefault();
	},

	_endSizing: function(/*Event*/ e){
		// summary: disconnect listenrs and cleanup sizing
		dojo.forEach(this._connects,function(c){
			dojo.disconnect(c); 
		});
		if(!this.activeResize){
			dojo.fadeOut({ node:this._activeResizeNode, duration:250,
				onEnd: dojo.hitch(this,function(){
					this._activeResizeNode.style.display="none";
				})
			}).play();
			this._changeSizing(e);
		}
		this._isSizing = false;
	}

});

}

