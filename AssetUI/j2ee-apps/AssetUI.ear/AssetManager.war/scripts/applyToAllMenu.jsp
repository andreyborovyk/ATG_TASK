<%--
  Page header fragment that includes applyToAllMenu.js and initializes
  localized strings used by that script file.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/applyToAllMenu.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <script type="text/javascript"
          src="<c:out value='${config.contextRoot}'/>/scripts/applyToAllMenu.js">
  </script>

  <script type="text/javascript">
    atg.assetmanager.ApplyToAllMenuStrings.changeLabel      = "<fmt:message key='ataMenu.update'/>";
    atg.assetmanager.ApplyToAllMenuStrings.clearLabel       = "<fmt:message key='ataMenu.makeNull'/>";
    atg.assetmanager.ApplyToAllMenuStrings.clearDescription = "<fmt:message key='ataMenu.valuesNullMessage'/>";
    atg.assetmanager.ApplyToAllMenuStrings.keepLabel        = "<fmt:message key='ataMenu.keepExisting'/>";
    atg.assetmanager.ApplyToAllMenuStrings.keepDescription  = "<fmt:message key='ataMenu.keepValuesMessage'/>";
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/applyToAllMenu.jsp#2 $$Change: 651448 $ --%>
