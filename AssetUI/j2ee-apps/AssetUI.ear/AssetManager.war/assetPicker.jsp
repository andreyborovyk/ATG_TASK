<%--
  Asset picker wrapper. This page exists so that the Merchandising UI
  can use the AccessControlServlet to make sure the user is logged in
  before wandering into the real asset picker.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetPicker.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  
  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
    </head>
    <body>
      <dspel:include otherContext="${config.assetUIRoot}" page="/assetPicker/assetPicker.jsp"/>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetPicker.jsp#2 $$Change: 651448 $--%>
