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
	
		
  <script type="text/javascript" charset="utf-8">
	
		dojo.addOnLoad(function(){

		 });		

     function doSomething(val){

       obj = {
         'value':val,
         'state':'Good',
         'message':''
       };
       
       dojo.publish("/atg/assetManager/multiEdit/progress", [obj]);
       frames['testIframeName'].window.dojo.publish("/atg/assetManager/multiEdit/progress", [obj]);
     }

	</script>
	
	<style type="text/css" media="screen">
	  .progressContainer{
	    margin-bottom: 20px;
	  }
	</style>
	
	<link rel="stylesheet" href="/WebUI/dijit/progress/templates/progress.css" type="text/css" media="screen" title="no title" charset="utf-8" />
	<link rel="stylesheet" href="/WebUI/dijit/progress/templates/clock.css" type="text/css" media="screen" title="no title" charset="utf-8">
	
	
	<title>ATG Widget Library - Progress Bar Test (No Ping URL - Topic Based Updates)</title>
</head>

<body>
<h1>ATG Widget Library - Progress Bar Test (No Ping URL - Topic Based Updates)</h1>

<h3>Default</h3>
<pre>
  function doSomething(val){
     obj = {
       &#x27;value&#x27;:val,
       &#x27;state&#x27;:&#x27;Good&#x27;,
       &#x27;message&#x27;:&#x27;&#x27;
     };
     
     dojo.publish(&quot;/atg/assetManager/multiEdit/progress&quot;, [obj]);
     frames[&#x27;testIframeName&#x27;].window.dojo.publish(&quot;/atg/assetManager/multiEdit/progress&quot;, [obj]);
   }
</pre>

<div id="progress2" topicDriven="true" dojoType="atg.widget.progressClock"></div>


<iframe src="noPingIframeContent.jsp" height="200px" width="500px" name="testIframeName"></iframe>

<div style="padding-top: 20px">
<a href="#" onclick='doSomething(10)'>Progress to 10</a>
<a href="#" onclick='doSomething(20)'>Progress to 20</a>
<a href="#" onclick='doSomething(30)'>Progress to 30</a>
<a href="#" onclick='doSomething(40)'>Progress to 40</a>
<a href="#" onclick='doSomething(50)'>Progress to 50</a>
<a href="#" onclick='doSomething(60)'>Progress to 60</a>
<a href="#" onclick='doSomething(70)'>Progress to 70</a>
<a href="#" onclick='doSomething(80)'>Progress to 80</a>
<a href="#" onclick='doSomething(90)'>Progress to 90</a>
<a href="#" onclick='doSomething(100)'>Progress to 100</a>
</div>

</body>
</html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/progress/noPingIframe.jsp#2 $$Change: 651448 $--%>
