<%--
  This is the itemView page for commerceRefineConfig items. 
  
    @param  imap          A request scoped, MappedItem
    @param  view          A request scoped, MappedItemView
    @param  formHandler   The form handler

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/commerceRefineConfigEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"            uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"        uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"          uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="biz"          uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="asset-ui"     uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="dcsui-search" uri="http://www.atg.com/taglibs/dcsui-srch"            %>

<!-- Begin commerceRefineConfigEdit.jsp -->

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<%-- get the formHandler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>
<dspel:getvalueof var="imap" param="imap"/>

<c:if test="${debug}">
  <p>Editing my refineConfig
  <p>item name: <dspel:valueof bean="${formHandlerPath}.itemDescriptorName"/>
  <br>itemId: <dspel:valueof bean="${formHandlerPath}.repositoryId"/>
  <p>item name: <c:out value="${imap.itemName}" />
  <br>imap: <c:out value="${imap.class.name}" />
  <br>view: <c:out value="${view.class.name}" />
  <br>refineConfig: <dspel:valueof bean="${formHandlerPath}.value.refineConfig.id" /><br>
  <br>formHandlerPath: <c:out value="${formHandlerPath}" /><br>
</c:if>

<dspel:getvalueof var="isGlobal" bean="${formHandlerPath}.value.isGlobal"/>
<fmt:message var="facetsHeader" key="facets" bundle="${bundle}"/>
<c:if test="${isGlobal == 'true'}">
  <fmt:message var="facetsHeader" key="globalFacets" bundle="${bundle}"/>
</c:if>

<fieldset>
<legend><span><c:out value="${facetsHeader}"/></span></legend>
<table class="formTable"><tr><td>
  <dspel:include page="property/collectionEditor.jsp">
    <dspel:param name="property"             value="refineElements"/>
    <dspel:param name="hideComponentReorder" value="${true}"/>
  </dspel:include>
</td></tr></table>
</fieldset>

<%-- Not for 2006.3
<fieldset>
<legend><span>Search Result Sorting</span></legend>
<table class="formTable">
  <tr>
    <td class="formLabel">
      Sorting Properties: 
    </td>
    <td>
      <dspel:include page="property/collectionEditor.jsp">
        <dspel:param name="property"        value="refineConfig"/>
        <dspel:param name="subProperty"     value="sortOptions"/>
        <dspel:param name="subPropertyType" value="sortOption"/>
      </dspel:include>
    </td>
  </tr>
</table>
</fieldset>
--%> 

</dspel:page>

<!-- End commerceRefineConfigEdit.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/commerceRefineConfigEdit.jsp#2 $$Change: 651448 $ --%>
