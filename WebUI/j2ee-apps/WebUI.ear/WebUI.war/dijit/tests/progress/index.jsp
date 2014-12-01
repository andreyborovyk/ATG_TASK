<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

	<title>ATG - Progress Bar Widget Tests</title>

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
      dojo.registerModulePath("atg.widget", "/WebUI/dijit");
      dojo.require("atg.widget.progress.base");
    </script>
    
    <link rel="stylesheet" href="/WebUI/dijit/progress/templates/progress.css" type="text/css" media="screen" title="no title" charset="utf-8" />

    <!-- test area specific code -->

    <script type="text/javascript" src="../testExtras/base.js"></script>
    <link rel="stylesheet" href="../testExtras/style.css" type="text/css" media="screen" title="no title" charset="utf-8">

    <script type="text/javascript" charset="utf-8">

  		dojo.addOnLoad(function(){

  		 });		

  	</script>



  	<style type="text/css" media="screen">
  	  body{
  	    font-family: arial;
  	    padding: 20px;
  	  }
  	  .progressContainer{
  	    margin-bottom: 20px;
  	  }
  	  dt{
  	    font-weight: bold;
  	  }
  	  h3{
  	    padding-top: 10px;
  	    margin-top: 15px;
  	    border-top: 2px solid #C6C6C6;
  	  }
  	</style>

  </head>

  <body class="atg">
    <h1>ATG Widget Library - Progress Bar</h1>
  <pre>
    &lt;div id=&quot;progress1&quot; dojoType=&quot;progress&quot; pingUrl=&quot;ping2.jsp&quot;&gt;&lt;/div&gt;
  </pre>
  <div id="progress1" dojoType="atg.widget.progressBase" pingUrl="ping2.jsp">test</div>


  <h3>Progress Bar UI Parameters</h3>
  <dl>
    <dt><a href="colors.html">barColor</a></dt>
    <dd>String</dd>
    <dd>Default: Blue</dd>
    <dd>Possible Options: blue, yellow, red, green</dd>

    <dt><a href="updated.html">percentProgress</a></dt>
    <dd>Integer (whole number percentage)</dd>
    <dd>Default: 0</dd>
    <dd>Initial progress bar width as a percentage</dd>

    <dt><a href="pingTime.html">pingDelay</a></dt>
    <dd>Integer (shouldn't really be less that 1000, due to performance issues)</dd>
    <dd>Default: 2000 (2 seconds)</dd>
    <dd>Millisecond value for the delay between pings</dd>

    <dt>pingUrl</dt>
    <dd>String (URI)</dd>
    <dd>Default: ''</dd>
    <dd>URI that the widget should use for the ping update, blank means no live update will occur.</dd>
    <dd><strike>TODO : pingUrl probably needs to be updated moving forward so that the progress widget doesn't manage it's own AJAX calls, but rather using a common topic system it just uses pub/sub to request and handle responses. But as ATG doesn't have a current standard we'll keep it here.</strike></dd>


    <dt><a href="noPing.jsp">topicDriven</a></dt>
    <dd>Boolean</dd>
    <dd>Default: false</dd>
    <dd>Indicates whether the progress bar should do it's own polling or just listen for publishing to topic "/atg/assetManager/multiEdit/progress".</dd>
    <dd>Currently this topic is hard coded into the widget, it should probably be rolled out and passed as a parameter (maybe the value of this parameter) so you can have multiple topics for a series of progress bars if need be).</dd>

    <dt><a href="noPingIframe.jsp">topicDriven - with iFrame</a></dt>
     <dd>Topic Driven demo page, with 1 widget in an iframe</dd>


    <dt><a href="barWidth.html">containerWidth</a></dt>
    <dd>String</dd>
    <dd>Default: '100%'</dd>
    <dd>String representing the container width (should be px or %)</dd>

    <dt><a href="showValues.html">showValue</a></dt>
    <dd>Boolean</dd>
    <dd>Default: true</dd>
    <dd>Boolean that shows determines if the % value is displayed inside the widget</dd>

    <dt><a href="transitions.html">transition</a></dt>
    <dd>String</dd>
    <dd>Default: 'slide'</dd>
    <dd>String, current supported transitions are 'jump' and 'slide'</dd>
    
    <dt><a href="forwardOnly.html">forwardOnly</a></dt>
    <dd>Boolean</dd>
    <dd>Default: False</dd>
    <dd>Boolean that indicates whether the widget should show backwards transitions or not, the default True setting will show forward and backward changes in the progress graphically, setting it to false will only show forward progress.</dd>
    
  </dl>

  <h3>Progress Bar Publish Topics</h3>

  <dl>
    <dt>dojo.event.topic.publish("/atg/message/addNew",errorMessage);</dt>
    <dd>If there is an error the progress bar will publish the error message object to /atg/message/addNew (which I'm adding to the message bar widget now as a subscription topic that will add the messages)</dd>

    <dt>dojo.event.topic.publish("/atg/progress/complete",{'id': this.widgetId });</dt>
    <dd>On completion (100% progress) the progress bar will publish the widget's ID to /atg/progress/complete</dd>

  </dl>

  <h3>Progress Bar Expected Server Response from Ping</h3>

  <pre>
    {
        'value':&lt;INTEGER&gt;,
        'state':'&lt;STRING&gt;',
        'message':'&lt;STRING&gt;'
    }
  </pre>

  <dl>
    <dt>Value - required</dt>
    <dd>This should be a whole number percentage value representing the current progress state</dd>

    <dt>State - optional</dt>
    <dd>This should be a string representing the current state of the progress.  These will usually be used to indicate some sort of negative situation (examples might be 'stalled', 'error')</dd>

    <dt>Message - optional</dt>
    <dd>This would be a string containing additional information regarding the state value that was passed.</dd>

  </dl>
  </body>
  </html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/progress/index.jsp#2 $$Change: 651448 $--%>
