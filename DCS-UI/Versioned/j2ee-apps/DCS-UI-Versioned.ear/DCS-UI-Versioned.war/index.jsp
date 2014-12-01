<%--
  Merchandising index page.  This just redirects to the AssetManager, with the
  correct configuration manager.
  
  @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/index.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/commerce/web/Configuration"/>

  <c:redirect context="/AssetManager" url="/assetManager.jsp">
    <c:param name="taskConfiguration" value="/atg/commerce/web/assetmanager/ConfigurationManager"/>
  </c:redirect>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/Versioned/src/web-apps/DCS-UI-Versioned/index.jsp#2 $$Change: 651448 $ --%>
