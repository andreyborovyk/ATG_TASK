<%--
  Custom property viewer for image data.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <c:set var="propView" value="${view.propertyMappings['url']}"/>
  <dspel:getvalueof id="url" bean="${propView.formHandlerProperty}" />
  <c:if test="${!empty url}">
    <img src='<c:out value="${url}"/>' />
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/viewImage.jsp#2 $$Change: 651448 $--%>
