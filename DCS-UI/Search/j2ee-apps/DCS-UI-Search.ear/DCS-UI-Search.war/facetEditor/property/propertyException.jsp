<%--
  Page fragment to display property errors if they exist.

  The following request-scoped variables are expected to be set:
  
    @param  formHandler  The form handler for the form that displays this view
    @param  property     The property for which we are checking exceptions
  
  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/propertyException.jsp#2 $$Change $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

<%-- Unpack request-scoped parameters into page parameters --%>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>
<c:set var="propertyName"    value="${param.property}"/>

<c:if test="${not empty formHandler.propertyExceptions[propertyName]}">
  <span class="error" title="<c:out value='${formHandler.propertyExceptions[propertyName].message}'/>">
    !
  </span>
  <span style="color: #ff0000">
    <c:out value="${formHandler.propertyExceptions[propertyName].message}"/>
  </span>
  <br>
</c:if>
  
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/propertyException.jsp#2 $$Change: 651448 $--%>
