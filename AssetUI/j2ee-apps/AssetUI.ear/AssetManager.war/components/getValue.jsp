<%--
  This page can be invoked using XMLHttpRequest to get the value of a Nucleus
  component property.  The value is echoed back to the caller.
  
  Parameters:
    @param  bean   The Nucleus path of the bean property to get
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/getValue.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <dspel:valueof bean="${param.bean}"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/getValue.jsp#2 $$Change: 651448 $--%>
