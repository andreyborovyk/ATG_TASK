<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

 
	<script type="text/javascript" src="/dojo-1/dojo/dojo.js" djConfig="isDebug:true, parseOnLoad: true"></script>
	
	<script type="text/javascript" charset="utf-8">
	  
	  dojo.require("dijit._Widget");
	  dojo.require("dijit._Templated");
		dojo.require("dojo.parser");	// scan page for widgets and instantiate them
	  
    // Register the Embedded Assistance widget namespace
    dojo.registerModulePath("atg.widget", "/WebUI/dijit");
    dojo.require("atg.widget.progress.base");
  </script>
  <script type="text/javascript" charset="utf-8" src="/WebUI/dijit/progress/clockwise.js"></script>
	
	
	<style type="text/css" media="screen">
	  .progressContainer{
	    margin-bottom: 20px;
	  }
	</style>
	
	<link rel="stylesheet" href="/WebUI/dijit/progress/templates/progress.css" type="text/css" media="screen" title="no title" charset="utf-8" />
	<link rel="stylesheet" href="/WebUI/dijit/progress/templates/clock.css" type="text/css" media="screen" title="no title" charset="utf-8">
	
	
</head>

<body>
<h1>Progress Bar Inside an Iframe</h1>


<div id="progress1" topicDriven="true" dojoType="atg.widget.progressBase"></div>


</body>
</html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/progress/noPingIframeContent.jsp#2 $$Change: 651448 $--%>
