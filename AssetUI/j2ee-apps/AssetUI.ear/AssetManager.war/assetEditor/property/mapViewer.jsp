<%--
  Default property viewer for map values.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/mapViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Display the property name. --%>
  <c:if test="${propertyView.attributes.fullWidth == 'true'}">
    <div class="formLabelCE">
      <c:out value="${propertyView.propertyDescriptor.displayName}"/>
    </div>
  </c:if>

  <%-- Get the map entries --%>
  <dspel:getvalueof var="entries" bean="${propertyView.formHandlerProperty}"/>

  <%-- Get the number of entries --%>
  <web-ui:collectionPropertySize var="numEntries" collection="${entries}"/>

  <%-- Display the entries in a table if any entries exist --%>
  <c:if test="${numEntries > 0}">

    <table cellpadding="0" cellspacing="0" border="0" width="100%" class="atg_dataTable">

      <%-- Table header --%>
      <tr>
        <th align="<c:out value='${propertyView.attributes.horziontalKeyColumnTitleAlign}'/>">
          <fmt:message key="collectionEditor.mapKey"/>
        </th>
        <th align="<c:out value='${propertyView.attributes.horziontalValueColumnTitleAlign}'/>">
          <fmt:message key="collectionEditor.mapValue"/>
        </th>
      </tr>

      <c:forEach var="entry" items="${entries}" varStatus="loop">
          <c:choose>
            <c:when test="${loop.count % 2 == 0}">
              <tr class="altRow">
            </c:when>
            <c:otherwise>
              <tr>
            </c:otherwise>
          </c:choose>

          <%-- Display the entry key --%>
          <td align="<c:out value='${propertyView.attributes.horziontalKeyColumnAlign}'/>">
            <dspel:valueof bean="${propertyView.formHandlerProperty}.keys[${loop.index}]"/>
            &nbsp;&nbsp;
          </td>

          <%-- Display the entry value using the component viewer for the type of
               entries contained in this map.  Note that we set a special property
               named "componentPropertyName" on the property view.  This is designated
               for use by collection container pages to indicate the form handler
               sub-property that contains the collection component value.  For a
               map collection, this is ".value[index]".  So, the full form handler
               property would be referred to using something like:
               "SomeFormHandler.someMapProperty.value[index]". --%>
          <td align="<c:out value='${propertyView.attributes.horziontalValueColumnAlign}'/>">
            <c:set target="${propertyView}" property="componentPropertyName"
                                            value=".value[${loop.index}]"/>
            <dspel:include otherContext="${propertyView.componentApplication}"
                           page="${propertyView.componentUri}"/>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/mapViewer.jsp#2 $$Change: 651448 $--%>
