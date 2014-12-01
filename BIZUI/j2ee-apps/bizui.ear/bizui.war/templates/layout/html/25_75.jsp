<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

  
<% 
  Community cm = (Community)request.getAttribute(Attribute.COMMUNITY);

  Page pg = (Page)request.getAttribute(Attribute.PAGE); 
  String gearModeName = "content";
  String gearBackgroundColor = "333333";
  String gearTextColor = "000000";
  Map regionMap = null;
 
  if(pg != null) {
    // ColorPalette 
    ColorPalette colorPalette = pg.getColorPalette();
    if(colorPalette != null) {
      gearBackgroundColor = colorPalette.getGearBackgroundColor();
      gearTextColor = colorPalette.getGearTextColor();
    }
    // RegionMap
    regionMap = pg.getRegions(); 
 }  
 String regionName = null;
%>

<%
  Gear [] gears = null;
%>
  <div id="mainContent" class="noPadding">
 	<table id="mainTable" cellpadding="0" cellspacing="0" width="100%">
	<tr>

	<td id="portletLeft">

<% 
  regionName = "25_75_Left";
  gears = null; 
  if(regionMap != null) {
    Region region = (Region)regionMap.get(regionName);
    if(region != null)
     gears = region.getGears();
  pageContext.setAttribute("gears", gears);
  }
%>

<%@ include file="BizUIRegion.jspf" %>
</td>
<td valign="top" id="adminRight" class="padding">
 
<% 
  regionName = "25_75_Right";
  gears = null; 
  if(regionMap != null) {
    Region region = (Region)regionMap.get(regionName);
    if(region != null)
     gears = region.getGears();
  pageContext.setAttribute("gears", gears);
  }
%>
<%@ include file="BizUIRegion.jspf" %> 
</td>
</tr>
</table>
</div>

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/layout/html/25_75.jsp#2 $$Change: 651448 $--%>
