<%--
  Default property viewer for list values.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/listViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Display the property name. --%>
  <c:if test="${propertyView.attributes.fullWidth == 'true'}">
    <div class="formLabelCE">
      <c:out value="${propertyView.propertyDescriptor.displayName}"/>
    </div>
  </c:if>

  <%-- Get the list items --%>
  <dspel:getvalueof var="items" bean="${propertyView.formHandlerProperty}"/>

  <%-- Get the number of items --%>
  <web-ui:collectionPropertySize var="numItems" collection="${items}"/>

  <%-- Display the items in a table if any items exist --%>
  <c:if test="${numItems > 0}">

    <table cellpadding="0" cellspacing="0" border="0" width="100%" class="atg_dataTable">

      <c:forEach var="item" items="${items}" varStatus="loop">
          <c:choose>
            <c:when test="${loop.count % 2 == 0}">
              <tr class="altRow">
            </c:when>
            <c:otherwise>
              <tr>
            </c:otherwise>
          </c:choose>

          <%-- Display the item value using the component viewer for the type of
               items contained in this list.  Note that we set a special property
               named "componentPropertyName" on the property view.  This is designated
               for use by collection container pages to indicate the form handler
               sub-property that contains the collection component value.  For a
               list collection, this is ".value[index]".  So, the full form handler
               property would be referred to using something like:
               "SomeFormHandler.someMapProperty.value[index]". --%>
          <td>
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
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/listViewer.jsp#2 $$Change: 651448 $--%>
