<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

	<title>Grids - if content doesn't need scrollbars, don't show them</title>
	<script type="text/javascript" src="/dojo-1/dojo/dojo.js.uncompressed.js" djConfig="isDebug: true, parseOnLoad: true"></script>
	

    <link rel="stylesheet" href="/WebUI/css/cssModules/foundation.css" type="text/css" media="all" />
    <link rel="stylesheet" href="/WebUI/css/cssFramework.css" type="text/css" media="all" />
  <!--[if IE]>
    <link rel="stylesheet" href="/WebUI/css/cssFramework_IE6.css" type="text/css" media="all" />
  <![endif]-->

    <link type="text/css" href="/dojo-1/dojo/resources/dojo.css" rel="stylesheet" />
    <link type="text/css" href="/dojo-1/dijit/themes/dijit.css" rel="stylesheet" />
    <link type="text/css" href="/dojo-1/dijit/themes/atg/atg.css" rel="stylesheet" />

		<script type="text/javascript" src="/WebUI/atg.js"></script>

		
		<script type="text/javascript" src="/dojo-1/dojo-fixes.js"></script>
	
	  		<script type="text/javascript">
    			dojo.require("dojox.grid.Grid");
    			dojo.require("dojox.grid._data.model");
    			dojo.require("dojo.parser");
    		</script>
    		<script type="text/javascript">

        var model;
    		// example sample data and code
  
        	// some sample data
        	// global var "data"
        	data = [ 
        		[ "normal", false, "new", 'But are not followed by two hexadecimal', 29.91],
        		[ "important", false, "new", 'Because a % sign always indicates', 9.33],
        		[ "important", false, "read", 'Signs can be selectively', 19.34],
        		[ "note", false, "read", 'However the reserved characters', 15.63 ]
        	];
        	var rows = 2000;
        	for(var i=0, l=data.length; i<rows-l; i++){
        		data.push(data[i%l].slice(0));
        	}

        	// global var "model"
        	model = new dojox.grid.data.Table(null, data);
        	
        	data2 = [ 
        		[ "normal", false, "new", 'But are not followed by two hexadecimal', 29.91],
        		[ "important", false, "new", 'Because a % sign always indicates', 9.33],
        		[ "important", false, "read", 'Signs can be selectively', 19.34],
        		[ "note", false, "read", 'However the reserved characters', 15.63 ]
        	];
        	
        	var rows = 5;
        	for(var i=0, l=data2.length; i<rows-l; i++){
        		data2.push(data2[i%l].slice(0));
        	}

        	// global var "model"
        	model2 = new dojox.grid.data.Table(null, data2);


    			dojo.addOnLoad(function(){
    				// a grid view is a group of columns
    				var view1 = {
    					cells: [
    						[
    							{name: 'Column 0'}, 
    							{name: 'Column 1'}, 
    							{name: 'Column 2'}, 
    							{name: 'Column 3', width: "150px"}, 
    							{name: 'Column 4'}
    						]
    					]
    				};
    				// a grid layout is an array of views.
    				var layout = [ view1 ];

    				var grid = new dojox.Grid({
    					"id": "grid",
    					"model": model,
    					"structure": layout
    				});
    				dojo.byId("gridContainer").appendChild(grid.domNode);
    				grid.render();
    				
    				var grid1 = new dojox.Grid({
    					"id": "grid",
    					"model": model,
    					"structure": layout    				
    					});
    				dojo.byId("gridContainer1").appendChild(grid1.domNode);
    				grid1.render();
    				
    				
    				var grid2 = new dojox.Grid({
    					"id": "grid2",
    					"model": model2,
    					"structure": layout,
    					"autoHeight": true
    				});
    				dojo.byId("gridContainer2").appendChild(grid2.domNode);
    				grid2.render();
    				
    				
    				var grid3 = new dojox.Grid({
    					"id": "grid2",
    					"model": model2,
    					"structure": layout,
    					"autoHeight": true
    				});
    				dojo.byId("gridContainer3").appendChild(grid3.domNode);
    				grid3.render();
    				

    				dojo.byId('gridContainer').style.display = "block";

    				dojo.byId('gridContainer2').style.display = "block";



            var nl = dojo.query(".dojoxGrid-scrollbox");
            dojo.forEach(nl, function (n) { 
              console.debug(n);
              //n.style.overflow = "auto"; 
            });


    			});



    		</script>
    		
    		<style type="text/css" media="screen">
    		  
/*          .atg .dojoxGrid-scrollbox{
            border: 1px solid black;
          }*/
    		  
    		  .atg .dojoxGrid-scrollbox{
    		    overflow: auto;
    		    
    		  }
    		  
    		  .dojoxGrid{
    		    border: solid 1px black;
    		  }
    		  
    		  
    		  h2{
    		    margin-top: 30px;
    		  }
    		  
    		</style>
    		
    		
    	</head>
    	<body class="atg">
    		<h1 class="heading">Grid Scrolling Test Page</h1>

        <div style="width: 700px; margin-left: 100px">

        <h2>More Content Than Space - scroll</h2>
        <div style="">
    		<div id="gridContainer" style="height: 300px"></div>
        </div>
        
        <h2>Narrow and more content Than Space - Scroll x & y</h2>
        <div style="">
      		<div id="gridContainer1" style="height: 300px"></div>
      	</div>
        
          <h2>Less Content Than Space - no Scroll</h2>
        <div style="">
      		<div id="gridContainer2"></div>
      	</div>


        <h2>Narrow and less content Than Space - no Scroll</h2>
        <div style="width: 400px">
      		<div id="gridContainer3"></div>
      	</div>

        </div>

    	</body>

</html>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/atgFunctionsTests/grid/scrollbarless/index.jsp#2 $$Change: 651448 $--%>
