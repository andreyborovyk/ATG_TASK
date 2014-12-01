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

  <!-- test area specific code -->

  <script type="text/javascript" src="/WebUI/atg.js"></script>

  <style type="text/css" media="screen">
    
    dl{
      margin: 20px;
    }
    
    dd, dt{
      padding: 10px;
    }
    
  </style>

	<title>ATG - atg.js Tests</title>
	
</head>

<body class="atg">

<h2>truncateMid</h2>
<div>Truncation function that removes the central part of a string then truncates the start and end portions.  Especially useful in URI truncation where the start and end are the most important parts to a user</p>
<dl>
  <dt>String: http://design.atg.com:81/team/131/truncating-a-url, Abbr: null</dt>
  <dd>
<script type="text/javascript" charset="utf-8">
document.write(atg.truncateMid("http://design.atg.com:81/team/131/truncating-a-url"));
</script> 
  </dd>
  
    <dt>String: http://design.atg.com:81/team/131/truncating-a-url, Abbr: false</dt>
    <dd>
  <script type="text/javascript" charset="utf-8">
  document.write(atg.truncateMid("http://design.atg.com:81/team/131/truncating-a-url", false));
  </script> 
    </dd>
    
      <dt>String: http://design.atg.com:81, Abbr: null</dt>
      <dd>
    <script type="text/javascript" charset="utf-8">
    document.write(atg.truncateMid("http://design.atg.com:81"));
    </script> 
      </dd>




<h2>Sparkle</h2>


<div id="triggers">
  <dl>
    <h3>Tests</h3>
    <dt>Text Block</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id2'))">Pulsar</dd>
    
    <dt>Table Header</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id3'))">Pulsar</dd>

  
    <dt>Table Cell</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id4'))">Pulsar</dd>
    
    <dt>Text Input</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('form2'))">Pulsar</dd>
    
    <dt>Select Input</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id10'))">Pulsar</dd>

    
    <dt>Floating DIV</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id28'))">Pulsar</dd>
    
    <dt>Header on dark BG</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id30'))">Pulsar</dd>

    <dt>Item on dark BG</dt>

    <dd onclick="atg.sparkle.pulsar(dojo.byId('id16'))">Pulsar</dd>

  </dl>
  
</div>


  	<h1 id="id1">Highlight Examples</h1>

    

  	<p id="id2">Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>


  	<table class="atg_dataTable" style="width: 50%">

  		<tr>
  			<th id="id3">Header</th>
  			<th>Header</th>
  			<th>Header</th>
  		</tr>
  		<tr>
  			<td>Data</td>

  			<td>Data</td>
  			<td><span  id="id4">Data</span></td>
  		</tr>
  		<tr>
  			<td>Data</td>
  			<td>Data</td>
  			<td>Data</td>

  		</tr>
  		<tr>
  			<td>Data</td>
  			<td>Data</td>
  			<td>Data</td>
  		</tr>
  	</table>

  	<hr />


    <form name="formName">
    <dl class="atg_dataForm">
      <dt><label for="form1" id="id5">Form Label:</label></dt>
      <dd><input type="text" name="inputName" id="form1" /></dd>
      <dt><label for="form2">Another Form Label:</label></dt>

      <dd><input type="text" name="inputName" id="form2" /></dd>
      <dt><label for="form3">Label:</label></dt>
      <dd><input type="text" name="inputName" id="form3" /></dd>
      <dt><label for="form4">Form Label:</label></dt>
      <dd><input type="text" name="inputName" id="id6" /></dd>
      <dt id="id7">Simple Boolean:</dt>
      <dd>

        <ul class="simpleBoolean">
          <li><label id="id8"><input type="radio" name="inputName" /> True</label></li>
          <li><label><input type="radio" name="inputName" id="id9"/> False</label></li>
        </ul>
      </dd>
      <dt><label for="selectBox">Select Option:</label></dt>

      <dd>
        <select name="selectName" id="id10">
          <option label="option 1">Option Number 1</option>
          <option label="option 2">Option Number 2</option>
          <option label="option 3">Option Number 3</option>
        </select>
      </dd>

    
      <dt><label for="selectBoxMulti">Multi Select Option:</label></dt>
      <dd>
        <select name="selectName" class="multiSelect" multiple="multiple" id="id11">
          <option label="option 1">Option Number 1</option>
          <option label="option 2">Option Number 2</option>
          <option label="option 3">Option Number 3</option>
        </select>

      </dd>
      <dt><label for="formList">Vertical Form List:</label></dt>
      <dd>
        <ul class="formVerticalList selectionBullets" id="formLabel">
      		<li><label id="id12"><input type="checkbox" class="checkboxBullet" />Inherit global facets</label></li>
      		<li><label><input type="checkbox" class="checkboxBullet" id="id13"/>Inherit catalog facets</label></li>
      		<li><label><input type="checkbox" class="checkboxBullet" />Inherit catalog facets a really long item that will wrap. Inherit catalog facets a really long item that will wrap. Inherit catalog facets a really long item that will wrap.</label></li>

      		<li><label><input type="checkbox" class="checkboxBullet" />Allow subcatalogs and subcategories to inherit</label></li>
      	</ul>
      </dd>
    
      <dt></dt>
      <dd id="id14"><input type="submit" value="Submit" id="id15"/> <input type="reset" value="Cancel" /></dd>
    </dl>
    </form>


  				<div style="position: fixed; padding: 10px; width: 30%; right: 10%; bottom: 30px; background: #545454; color: #fff" id="id28">
  				<h3 id="id30">Content Inside an Fixed positioned div</h3>
  				<ul>
        		<li id="id16">Lorem ipsum dolor sit amet<li>
        		<li>consectetur adipisicing elit</li>
        		<li>sed do eiusmod</li> 
        	</ul>

        	<ol>
        		<li>Duis aute irure dolor</li> 
        		<li id="id18">Lorem ipsum dolor sit amet</li>
        		<li>quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat</li>
        	</ol>
  				</div>


  </div>



</body>
</html>

</dsp:page>

<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/index.jsp#2 $$Change: 651448 $--%>
