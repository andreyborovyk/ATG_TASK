<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="dsp"            uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>
  
<tr>
  <td colspan="2"><fmt:message key="propertyWeightingWidget.propertiesSubtotal"/>:</td>
  <td colspan="2">
    <div class="propPercent">0%</div>
    <div class="propBar">[Bar Here]</div>
  </td>
</tr>

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/javascript/dijit/propPriority/templates/ppWeightTotal.jsp#2 $$Change: 651448 $--%>
