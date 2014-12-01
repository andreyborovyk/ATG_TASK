<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="dsp"            uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<table id="atg_smerch_propWeightType">
  <tr>
    <th><fmt:message key="propertyWeightingWidget.properties"/></th>
    <th><fmt:message key="propertyWeightingWidget.textRelevance"/></th>
    <th><fmt:message key="propertyWeightingWidget.textPropertyMatch"/></th>
    <th><fmt:message key="propertyWeightingWidget.total"/></th>
  </tr>
  
  <tr>
    <td><input type="text" name="some_name" value="" /> %</td>
    <td><input type="text" name="some_name" value="" /> %</td>
    <td><input type="text" name="some_name" value="" /> %</td>
    <td>100 %</td>
  </tr>
</table>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/javascript/dijit/propPriority/templates/ppWeightType.jsp#2 $$Change: 651448 $--%>
