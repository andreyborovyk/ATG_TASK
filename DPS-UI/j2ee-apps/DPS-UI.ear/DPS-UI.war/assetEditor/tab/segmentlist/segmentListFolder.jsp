<%--
  General info tab for segment list folder assets.
  
  The following request-scoped variables are expected to be set:

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id //product/DPS-UI/main/src/web-apps/DPS-UI/assetEditor/tab/segmentlist/segmentListFolder.jsp  $$Change $
  @updated $DateTime $$Author amitj $
  --%>

<%@ taglib prefix="c"            uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"        uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"          uri="http://java.sun.com/jstl/fmt"                     %>

<!-- Begin DPS-UI's /assetEditor/tab/segmentlist/segmentListFolder.jsp -->

<dspel:page>
                 
<fmt:setBundle basename="${view.attributes.resourceBundle}"/>

<%-- get the formHandler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>

<fieldset>
  <legend><fmt:message key="${view.displayName}"/></legend>
  <table class="formTable">
    <tr>
      <c:set var="propertyName" value="displayName"/>
      <td class="formLabel">
        <fmt:message key="segmentListFolder.displayName"/>:
      </td>
      <td>
        <dspel:valueof bean="${formHandlerPath}.value.${propertyName}"/>
      </td>
    </tr>
  </table>
</fieldset>

</dspel:page>

<!-- End DPS-UI's /assetEditor/tab/segmentList/segmentListFolder.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/segmentlist/segmentListFolder.jsp#2 $$Change: 651448 $--%>
