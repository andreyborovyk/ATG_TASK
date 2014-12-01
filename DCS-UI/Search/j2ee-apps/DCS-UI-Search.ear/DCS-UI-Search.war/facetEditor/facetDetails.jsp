<%--
  The initial facet details page
  
    @param  view          A request scoped, MappedItemView (itemViewMapping)
    @param  formHandler   The form handler


  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetDetails.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>

<!-- Begin facetDetails.jsp -->

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

<%-- get the formHandler from the requestScope --%>
<c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
<c:set var="formHandler"     value="${requestScope.formHandler}"/>

<%-- determine which mode we're in --%>
<c:choose>
  <c:when test="${view.itemMapping.mode == 'AssetManager.edit'}">
    <c:set var="disabled" value="false"/>
  </c:when>
  <c:otherwise>
    <c:set var="disabled" value="true"/>
  </c:otherwise>
</c:choose>

<fieldset class="altGroup">
<legend><span><fmt:message key="facetResults" bundle="${bundle}"/></span></legend>
<table class="formTable">
  <dspel:include page="propertyContainer.jsp">
    <dspel:param name="property" value="range"/>
  </dspel:include>
</table>
</fieldset>


<fieldset>
<legend><span><fmt:message key="presentation" bundle="${bundle}"/></span></legend>
<table class="formTable">
  <dspel:include page="propertyContainer.jsp">
    <dspel:param name="property" value="sort"/>
  </dspel:include>
  <dspel:include page="propertyContainer.jsp">
    <dspel:param name="property" value="order"/>
  </dspel:include>
  <dspel:include page="propertyContainer.jsp">
    <dspel:param name="property" value="exclude"/>
  </dspel:include>
</table>
</fieldset>

<c:if test="${requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
<script type="text/javascript">

var facetDetails_order_add_element;
var facetDetails_exclude_add_element;

function switch_order_exclude_add_buttons() {
  if (document.getElementById('range_false').checked) {
    facetDetails_order_add_element.className = 'buttonSmall';
    facetDetails_exclude_add_element.className = 'buttonSmall';

    facetDetails_order_add_element.onclick = null;
    facetDetails_exclude_add_element.onclick = null;
  } else {
    facetDetails_order_add_element.className = 'buttonSmall disabled';
    facetDetails_exclude_add_element.className = 'buttonSmall disabled';

    facetDetails_order_add_element.onclick = function() {
      return false;
    };
    facetDetails_exclude_add_element.onclick = function() {
      return false;
    };
  }
}

function range_onclick() {
  switch_order_exclude_add_buttons();
  return markAssetModified();
}

registerOnLoad(function() {
  document.getElementById('range_false').onclick = range_onclick;
  document.getElementById('range_free').onclick = range_onclick;

  facetDetails_order_add_element = document.getElementById('facetDetails_order_addSpan').getElementsByTagName('A')[0];
  facetDetails_exclude_add_element = document.getElementById('facetDetails_exclude_addSpan').getElementsByTagName('A')[0];

  switch_order_exclude_add_buttons();
});

</script>
</c:if>

</dspel:page>

<!-- End facetDetails.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/facetDetails.jsp#2 $$Change: 651448 $ --%>
