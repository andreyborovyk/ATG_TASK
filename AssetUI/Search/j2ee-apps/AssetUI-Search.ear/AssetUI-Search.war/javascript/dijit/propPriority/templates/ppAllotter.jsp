  <%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
  <%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
  <%@ taglib prefix="dsp"            uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

<table id="allotterTable" class="atg_dataTable" cellspacing="0" cellpadding="0" dojoattachpoint="mainWeights">
  <thead>
    <tr>
      <th scope="col"><fmt:message key="propertyWeightingWidget.propertyPrioritization"/></th>
      <th scope="col"><fmt:message key="propertyWeightingWidget.searchTermRelevance"/></th>
      <th scope="col"><fmt:message key="propertyWeightingWidget.searchTermPropertyMatch"/></th>
      <th scope="col"><fmt:message key="propertyWeightingWidget.total"/></th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>
        <input id="propertyWeight" dojoattachpoint="propertiesWeightView" />%
      </td>
      <td>
        <input id="textRelevanceAllWeight" dojoattachpoint="overallTextRelevanceView" />%
      </td>
      <td>
        <input id="textRelevanceTypeWeight" dojoattachpoint="textRelevanceByFieldView" />%
      </td>
      <td>
        <span dojoattachpoint="mainWeightTotal"></span>
      </td>    
    </tr>
  </tbody>
</table>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/javascript/dijit/propPriority/templates/ppAllotter.jsp#2 $$Change: 651448 $--%>
