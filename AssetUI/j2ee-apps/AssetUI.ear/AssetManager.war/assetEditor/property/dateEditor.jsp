<%--
  Default property editor for date values.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/dateEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <%-- This just uses the timestamp editor with different parameters --%>
  <dspel:include page="timestampEditor.jsp">
    <dspel:param name="omitTime" value="true"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/dateEditor.jsp#2 $$Change: 651448 $--%>
