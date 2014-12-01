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
		dojo.require("dojox.layout.ContentPane");
      // Register the Embedded Assistance widget namespace
      dojo.registerModulePath("atg.widget", "/WebUI/dijit");
      dojo.require("atg.widget.assistance.Base");
      dojo.require("atg.widget.assistance.Popup");
      dojo.require("atg.widget.assistance.Inline");
      
    </script>
    
    <link rel="stylesheet" href="/WebUI/dijit/assistance/templates/base.css" type="text/css" media="all" />
  	<link rel="stylesheet" href="/WebUI/dijit/assistance/templates/inline.css" type="text/css" media="all" />
  	<link rel="stylesheet" href="/WebUI/dijit/assistance/templates/popup.css" type="text/css" media="all" />
  	

    <script type="text/javascript" charset="utf-8">

  		dojo.addOnLoad(function(){

  		 });		

  	</script>
	  
		<script type="text/javascript" src="help/helpArray.js"></script>
		<script type="text/javascript" src="help/helpContent.js"></script>

		<script type="text/javascript" src="help/csc_helpArray.js"></script>
		<script type="text/javascript" src="help/csc_helpContent.js"></script>
		
		
		
		
    <script type="text/javascript" charset="utf-8">
	
		 // Hook the contextHelp creation to the page load

		dojo.addOnLoad(function(){

      if(!dijit.registry.byClass('atg.widget.assistance.Base')){
        dijit.registry.byClass('atg.widget.assistance.Base')[0].initialize();
      }else{
        new atg.widget.assistance.Base();
      }
		 });		

	</script>
	
  	<link rel="stylesheet" href="../../css framework/styles/cssModules/foundation.css" type="text/css" media="all" />
		<link rel="stylesheet" href="../../css framework/styles/cssFramework.css" type="text/css" media="all" />
  	<!--[if IE]>
  	<link rel="stylesheet" href="../css framework/styles/cssFramework_IE6.css" type="text/css" media="all" />
  	<![endif]-->

		<style type="text/css" media="screen">
			.holder div{
				border: 1px solid black;
				padding: 1%;
				margin: 1%;
				float: left;
				width: 29%;
				background-color: #FFF;
			}
			.holder{
				border: 1px solid black;
				padding: 1%;
				margin: 1%;
				background-color: #B4B4B4;
				width: 95%;
				height: 1%;
				overflow: hidden;
				padding-bottom: 300px;
				
			}

			
		</style>

	
	<title>ATG Widget Library - Context Help Demo Page</title>
</head>

<body>
<div class="holder">
	

	<h1 id="atg_arm_contentBrowserTitle">Context Help Demo Page</h1>

	<p id="atg_arm_linkedDocumentsTitle">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
	

	<table class="atg_dataTable" style="width: 50%">
		<tr>
			<th>Header</th>
			<th>Header</th>
			<th>Header</th>
			<th>Header</th>
			<th>Header</th>
			
		</tr>
		<tr>
			<td>Data</td>
			<td>Data</td>
			<td>Data</td>
			<td><span>Data</span></td>
			<td>Data</td>
		</tr>
		<tr>
			<td>Data</td>
			<td>Data</td>
			<td>Data</td>
			<td>Data</td>
			<td>Data</td>
		</tr>
	</table>
	
	<hr />


	<div dojoType="dojox.layout.ContentPane" id="ajaxTest" executeScripts="true">
	  <h3  id="atg_arm_addAttachment">Test Header</h3>
	  <button onclick="dijit.byId('ajaxTest').setHref('loadedFile.html')">Click me to Load New Content</button>
    
<p id="id25">Bunch of Cotent that we will replace.... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
	</div>

		<div style="padding: 10px; width: 29%">
		<h3 id="id24">Content Inside an Relatively positioned div</h3>
		<p id="id25">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
		</div>


		<div style="position: relative; padding: 10px; width: 29%; height: 150px; overflow: auto">
		<h3 id="id26">Content Inside an Relatively positioned div, with a scroll</h3>
		<p id="id27">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
		</div>
			
			
			<div style="position: relative; padding: 10px; width: 29%">
			<h3 id="id28">Content Inside an Relatively positioned div, with a scroll on the subcontent</h3>
			<p id="id29" style="height: 150px; overflow: auto">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
			</div>
			
			
				<div style="position: fixed; padding: 10px; width: 30%; right: 10%; bottom: 30px">
				<h3 id="id30">Content Inside an Fixed positioned div</h3>
				<p id="id31">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>
				</div>
			
		

</div>


</body>
</html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/dijit/tests/assistance/index.jsp#2 $$Change: 651448 $--%>
