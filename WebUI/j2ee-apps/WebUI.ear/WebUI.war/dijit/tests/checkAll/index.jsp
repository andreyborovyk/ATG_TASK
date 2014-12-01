<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

	<script type="text/javascript" src="/dojo-1/dojo/dojo.js.uncompressed.js"
		djConfig="isDebug: true, parseOnLoad: true"></script>
		


  <link rel="stylesheet" href="/WebUI/css/cssModules/foundation.css" type="text/css" media="all" />
  <link rel="stylesheet" href="/WebUI/css/cssFramework.css" type="text/css" media="all" />
<!--[if IE]>
  <link rel="stylesheet" href="/WebUI/css/cssFramework_IE6.css" type="text/css" media="all" />
<![endif]-->

  <link type="text/css" href="/dojo-1/dojo/resources/dojo.css" rel="stylesheet" />
  <link type="text/css" href="/dojo-1/dijit/themes/dijit.css" rel="stylesheet" />
  <link type="text/css" href="/dojo-1/dijit/themes/atg/atg.css" rel="stylesheet" />

	<script type="text/javascript" charset="utf-8">

	  dojo.require("dijit._Widget");
	  dojo.require("dijit._Templated");
		dojo.require("dojo.parser");	// scan page for widgets and instantiate them

    // Register the  widget namespace
    // Register the Embedded Assistance widget namespace
    dojo.registerModulePath("atg.widget", "/WebUI/dijit");
    dojo.require("atg.widget.checkAll.checkAll");
  </script>
  
  <script type="text/javascript" src="/dojo-1/dojo-fixes.js"></script>
  
  
  
  <script type="text/javascript" src="../testExtras/base.js"></script>
  <link rel="stylesheet" href="../testExtras/style.css" type="text/css" media="screen" title="no title" charset="utf-8">

  <style type="text/css" media="screen">
    .testcase{
      height: 200px;
      width: 25%;
      padding: 1%;
      float: left;
      margin: 2%;
      border: 2px solid #B1B1B1;
    }
    
    h3{
      margin: 0;
      padding: 0;
    }
  </style>

	<title>ATG - CheckAll Widget Tests</title>
	
</head>

<body class="atg" style="padding: 30px">

<h1>atg.widget.checkAll</h1>

<div class="testcase">
<h3>Basic Unordered List Demo</h3>

<ul>
  <li class="checkAll"><label><input type="checkbox" dojotype="atg.widget.checkAll"/>Check All</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox"/>My checkbox</label></li>
</ul>
</div>

<div class="testcase">
<h3>Basic Unordered List Demo - everything checked at start</h3>

<ul>
  <li class="checkAll"><label><input type="checkbox" dojotype="atg.widget.checkAll"/>Check All</label></li>
  <li><label><input type="checkbox" CHECKED/>My checkbox</label></li>
  <li><label><input type="checkbox" CHECKED/>My checkbox</label></li>
  <li><label><input type="checkbox" CHECKED/>My checkbox</label></li>
  <li><label><input type="checkbox" CHECKED/>My checkbox</label></li>
  <li><label><input type="checkbox" CHECKED/>My checkbox</label></li>
</ul>
</div>


<div class="testcase">
<h3>Show Excluded by Class name</h3>

<ul>
  <li class="checkAll"><label><input type="checkbox" dojotype="atg.widget.checkAll"/>Check All</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" class="noCheckAll" />I'm excluded by my classname</label></li>
</ul>
</div>
<div class="testcase">
<h3>In a table - just change the parentTag value to table</h3>
<table>
  <tr><td class="checkAll"><label><input type="checkbox" dojotype="atg.widget.checkAll" parentTag="table"/>Check All</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
  <tr><td><label><input type="checkbox" />My checkbox</label></td></tr>
</table>
</div>
<div class="testcase">
<h3>In a parent Div - check all not in direct list node</h3>
<div>
    <p><label><input type="checkbox" dojotype="atg.widget.checkAll" parentTag="div"/>Check All</label></p>
  <ul style="overflow: auto; height: 100px; width: 100px; border: 1px solid red">
    <li><label><input type="checkbox" />My checkbox</label></li>
    <li><label><input type="checkbox" />My checkbox</label></li>
    <li><label><input type="checkbox" />My checkbox</label></li>
    <li><label><input type="checkbox" />My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
    <li><label><input type="checkbox"/>My checkbox</label></li>
  </ul>
</div>
</div>
<div class="testcase">
<h3>CheckAll as a link - not currently supported this will fail</h3>

<ul>
  <li class="checkAll"><a href="#" dojotype="atg.widget.checkAll"/>Check All</a></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox" />My checkbox</label></li>
  <li><label><input type="checkbox"/>My checkbox</label></li>
</ul>
</div>

</body>
</html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/checkAll/index.jsp#2 $$Change: 651448 $--%>
