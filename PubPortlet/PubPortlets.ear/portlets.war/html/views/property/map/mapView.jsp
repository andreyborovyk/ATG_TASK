<%--
  Map Viewer
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<%--
  Default read-only view for arrays
  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

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

      <c:if test="${ ! empty arrayValues }">

        <table cellpadding='0' cellspacing='0' border='0' width='100%'
          class="map">

          <tr>
            <th align='<c:out value="${mpv.attributes.horziontalKeyColumnTitleAlign}"/>'>
              <c:choose>
                <c:when test="${ ! empty mpv.attributes.keyColumnTitle }">
                  <c:out value="${mpv.attributes.keyColumnTitle}"/>
                </c:when>
                <c:otherwise>
                  Key
                </c:otherwise>
              </c:choose>
            </th>
            <th align='<c:out value="${mpv.attributes.horziontalValueColumnTitleAlign}"/>'>
              <c:choose>
                <c:when test="${ ! empty mpv.attributes.valueColumnTitle }">
                  <c:out value="${mpv.attributes.valueColumnTitle}"/>
                </c:when>
                <c:otherwise>
                  Value
                </c:otherwise>
              </c:choose>
            </th>
          </tr>

          <c:forEach items="${arrayValues}" var="arrayValue" varStatus="status">

            <tr>
              <c:choose>
                <c:when test="${ status.index % 2 == 0 }">
                  <c:set var="tagClass" value="alternateRowHighlight"/>
                </c:when>
                <c:otherwise>
                  <c:set var="tagClass" value=""/>
                </c:otherwise>
              </c:choose>

              <%-- Key --%>
              <td class='<c:out value="${tagClass}"/>' 
                align='<c:out value="${mpv.attributes.horziontalKeyColumnAlign}"/>'>

                <%-- Show optional key pre-label --%>
                <c:out value="${mpv.attributes.labelBeforeKey}" escapeXml="false"/>

                <c:choose>
                  <c:when test="${mpv.view.displayName eq 'Diff'}">
                    <c:out value="${arrayValue.key}"/>
                    <%-- 
                      Note: set the componentProperty name for the current
                      collection member, for example, ".arrayValue.key"
                    --%>
                   <c:set target="${mpv}" property="componentPropertyName"
                     value=".${arrayValue.key}"/>
                  </c:when>
                  <c:otherwise>
                    <dspel:valueof
                      bean="${mpv.formHandlerProperty}.keys[${status.index}]"/>
                    <%-- 
                      Note: set the componentProperty name for the current
                      collection member, for example, ".value[0]"
                    --%>
                    <c:set target="${mpv}" property="componentPropertyName"
                      value=".value[${status.index}]"/>
                  </c:otherwise>
                </c:choose>

                <%-- Show optional key post-label --%>
                <c:out value="${mpv.attributes.labelAfterKey}" escapeXml="false"/>
        
                &nbsp;&nbsp;

              </td>

              <%-- Value --%>
              <td class='<c:out value="${tagClass}"/>' 
                align='<c:out value="${mpv.attributes.horziontalValueColumnAlign}"/>'>

                <%-- Show optional value pre-label --%>
                <c:out value="${mpv.attributes.labelBeforeValue}" escapeXml="false"/>

                <dspel:include page="${mpv.componentUri}"/>

                <%-- Show optional value post-label --%>
                <c:out value="${mpv.attributes.labelAfterValue}" escapeXml="false"/>

              </td>
           
            </tr>
          </c:forEach>

        </table>

      </c:if> <%-- --%>

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

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/jsp-src/views/property/map/mapView.jsp#2 $$Change: 651448 $--%>
