<%--
  Default view-mode view for RepositoryItem properties
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/simpleView.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<%@ include file="../includes/fieldAttributes.jspf" %>

<table border="0" cellpadding="0" cellspacing="3">
<tr>
  
  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
  <c:if test="${!empty title}">
    <c:set var="pvTitle" value="${title}"/>
  </c:if>
  <td class="formLabel">
    <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/><c:out value="${pvTitle}" escapeXml="false"/>:</label>
  </td>

  <td class="formText formPadding">

  <%-- Open paragraph and show the optional lead-in text --%>
  <c:if test="${!empty textAboveField}">
    <c:out value="${textAboveField}" escapeXml="false"/><br />
  </c:if>

  <%-- Show optional pre-label --%>
  <c:if test="${!empty labelBeforeField}">
    <c:out value="${labelBeforeField}" escapeXml="false"/>&nbsp;
  </c:if>

  <c:set var="itemDescriptor" value="${mpv.repositoryItemDescriptor}"/>

  <dspel:getvalueof var="assetID"
    bean="${mpv.formHandlerProperty}.repositoryId"/>

  <pws:createVersionManagerURI var="assetURI"
    componentName="${itemDescriptor.repository.repositoryName}"
    itemDescriptorName="${itemDescriptor.itemDescriptorName}"
    itemId="${assetID}"/>

  <%-- Create link to asset if referenced asset exists --%>
  <c:if test="${ ! empty assetID }">
    <a href='#' onclick='pushContext( "<c:out value='${assetURI}'/>" );return false;'>
      <dspel:getvalueof var="displayName"
         bean="${mpv.formHandlerProperty}.itemDisplayName"/>
      <c:if test="${ empty displayName }">
        <dspel:getvalueof var="displayName"
          bean="${mpv.formHandlerProperty}.${itemDescriptor.itemDisplayNameProperty}"/>
      </c:if>
      <c:if test="${ empty displayName }">
        <c:set var="displayName" value="${mpv.propertyDescriptor.displayName}"/>
      </c:if>
      <c:out value="${displayName}"/>
    </a>
  </c:if>

  <%-- Show optional post-label --%>
  <c:if test="${!empty labelAfterField}">
    &nbsp;<c:out value="${labelAfterField}" escapeXml="false"/>
  </c:if>

  <%-- Show option trailing text --%>
  <c:if test="${!empty textBelowField}">
    <br />
    <c:out value="${textBelowField}" escapeXml="false"/>
  </c:if>

  <%-- Close out field --%>
  </td>

</tr>
</table>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/item/simpleView.jsp#2 $$Change: 651448 $--%>
