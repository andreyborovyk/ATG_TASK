<%--
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/navigator.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>


<dspel:page>

  <dspel:getvalueof var="imap" param="imap"/>

  <c:set var="imapBundleName" value="${imap.attributes.resourceBundle.value}"/>
  <c:set var="appname" value="${imap.attributes.application.value}"/>
  <fmt:setBundle var="imapbundle" basename="${imapBundleName}"/>
  <fmt:message bundle="${imapbundle}" key="${appname}" var="application"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>
  <table class="navigator"><tr><td>
    <fmt:message bundle="${bundle}" key="navigator.this_asset_can_be_edited_in.text">
        <fmt:param value="${application}"/>
    </fmt:message>
  </td></tr></table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/navigator.jsp#2 $$Change: 651448 $--%>
