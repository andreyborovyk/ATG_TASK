<%--
  Asset editor page fragment with warning message.
--%>

<%@ taglib prefix="dspel"      uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"        uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>
<dspel:importbean var="config"
                  bean="/atg/commerce/search/web/Configuration"/>
<fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>
                  
<div class="sharedWarning">
  <table>
    <tr>
      <td>
        <img class="sharedWarningIcon" src="/AssetManager/images/icon_alertError.gif"/>
      </td>
      <td>
        <div class="sharedWarningTitle">
          <fmt:message key="sharedTitle" bundle="${bundle}"/>
        </div>
        <div class="sharedWarningText">
          <fmt:message key="sharedText" bundle="${bundle}"/>
        </div>
  	  </td>
    </tr>
  </table>
</div>
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/sharedAssetWarning.jsp#2 $$Change: 651448 $--%>
