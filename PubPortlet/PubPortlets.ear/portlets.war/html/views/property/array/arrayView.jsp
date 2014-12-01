<%--
  Default read-only view for arrays
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<table border="0" cellpadding="0" cellspacing="3">
  <tr>

    <%--
     Display the property view title...
     Use the property display name by default.  Check 'title' attribute
     for override...
     --%>
    <c:set var="pvTitle" value="${mpv.propertyDescriptor.displayName}"/>
    <c:if test="${mpv.attributes.title != null}">
      <c:set var="pvTitle" value="${mpv.attributes.title}"/>
    </c:if>
    <td class="formLabel">
      <c:out value="<label for=\"${mpv.propertyDescriptor.name}\">" escapeXml="false"/>
      <c:out value="${pvTitle}" escapeXml="false"/>:</label>
    </td>

    <td class="formText formPadding">

    <%-- Open paragraph and show the optional lead-in text --%>
    <c:if test="${mpv.attributes.textAboveField != null}">
      <c:out value="${mpv.attributes.textAboveField}" escapeXml="false"/><br />
    </c:if>

    <%-- Show the array data --%>
    <dspel:getvalueof var="arrayValues" vartype="Object[]" 
      bean="${mpv.formHandlerProperty}">

    <dspel:getvalueof var="listTypeName" 
      bean="${mpv.formHandlerProperty}.class.name"/>

      <table cellpadding='0' cellspacing='0' border='0' width='100%'
        style='margin-left: 0px;'>

        <c:forEach items="${arrayValues}" var="arrayValue" varStatus="status">
          
          <c:set var="componentObject" value="${arrayValue}" scope="request"/>

          <%-- 
            Note: set the componentProperty name for the current
            collection member, for example, "[0]".  Or if it's a RepositoryFormHandler,
            the componentProperty should be ".value[0]".
          --%>
          <c:choose>
            <c:when test="${listTypeName == 'atg.adapter.gsa.ChangeAwareList'}">
              <c:set target="${mpv}" property="componentPropertyName"
                value="[${status.index}]"/>
            </c:when>
            <c:when test="${listTypeName == 'atg.adapter.gsa.ChangeAwareSet'}">
              <c:set target="${mpv}" property="componentPropertyName"
                value="[${status.index}]"/>
            </c:when>
            <c:when test="${listTypeName == 'atg.repository.servlet.RepositoryFormList'}">
              <c:set target="${mpv}" property="componentPropertyName"
                value=".value[${status.index}]"/>
            </c:when>
            <c:otherwise>
              <c:set target="${mpv}" property="componentPropertyName"
                value="[${status.index}]"/>
            </c:otherwise>
          </c:choose>

          <tr>
            <c:choose>
              <c:when test="${ status.index % 2 == 0 }">
                <c:set var="tagClass" value="alternateRowHighlight"/>
              </c:when>
              <c:otherwise>
                <c:set var="tagClass" value=""/>
              </c:otherwise>
            </c:choose>

            <td class='<c:out value="${tagClass}"/>'>

              <%-- Show optional pre-label --%>
              <c:out value="${mpv.attributes.labelBeforeField}" escapeXml="false"/>

              <dspel:include page="${mpv.componentUri}"/>

              <%-- Show optional post-label --%>
              <c:out value="${mpv.attributes.labelAfterField}" escapeXml="false"/>

            </td>
          </tr>
        </c:forEach>

      </table>

    </dspel:getvalueof>

    <%-- Show option trailing text --%>
    <c:if test="${mpv.attributes.textBelowField != null}">
      <br />
      <c:out value="${mpv.attributes.textBelowField}" escapeXml="false"/>
    </c:if>

    <%-- Close out field --%>
    </td>

  </tr>
</table>

<%-- Clear the componentObject variable. --%>
<c:set var="componentObject" value="${null}"/>
</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/array/arrayView.jsp#2 $$Change: 651448 $--%>
