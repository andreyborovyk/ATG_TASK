<%--
  This is the itemView page for commerceRefineConfig items. 
  
    @param  imap          A request scoped, MappedItem
    @param  view          A request scoped, MappedItemView
    @param  formHandler   The form handler

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetChildEdit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="dspel"        uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"          uri="http://java.sun.com/jstl/fmt"                     %>

<!-- Begin commerceRefineConfigEdit.jsp -->

<dspel:page>

<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<fieldset>
<legend><span><fmt:message key="fctSrchIvmFctChildFacets.displayName" bundle="${bundle}"/></span></legend>
<table class="formTable"><tr><td>
  <dspel:include page="property/collectionEditor.jsp">
    <dspel:param name="property"             value="childElements"/>
    <dspel:param name="hideComponentReorder" value="${true}"/>
  </dspel:include>
</td></tr></table>
</fieldset>

</dspel:page>

<!-- End commerceRefineConfigEdit.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetChildEdit.jsp#2 $$Change: 651448 $ --%>
