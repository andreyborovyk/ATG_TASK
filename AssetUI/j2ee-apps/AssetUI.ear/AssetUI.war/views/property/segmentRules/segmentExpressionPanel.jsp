<%--
  Page fragment that displays an expression editor for editing segment rules.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/views/property/segmentRules/segmentExpressionPanel.jsp#2 $ $Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<style type="text/css">
  @import url("/WebUI/css/inlineExpreditorStyles.css");
</style>

<%-- Local overrides --%>
<style type="text/css">
  .panelHeader {
    background:#f8f8f8;
  }
  a.terminalLabel:link, a.terminalLabel:visited, a.terminalLabel:active {
    color:#036;
  }
  .panelHeader input {
    margin-bottom:1px;
  }
  a.terminalMenuLabel:link, a.terminalMenuLabel:visited, a.terminalMenuLabel:active {
    color:#036;
  }
  a.terminalMenuLabel:hover {
    font-weight: normal;
    background-color: #000000;
    color: #ffffff;
  }
  a.terminalMenuLabelCurrent:link, a.terminalMenuLabelCurrent:visited, a.terminalMenuLabelCurrent:active {
    color:#036;
    font-weight:bold;
  }
  a.terminalMenuLabelCurrent:hover {
    font-weight: bold;
    background-color: #000000;
    color: #ffffff;
  }
</style>

<%-- We need this to support picking items with the asset picker --%>
<script language="JavaScript" type="text/javascript"
        src="/AssetManager/scripts/common.js">
</script>
<script language="JavaScript" type="text/javascript"
        src="/AssetManager/scripts/assetPickerLauncher.js">
</script>

<script language="JavaScript" type="text/javascript"
        src="/WebUI/scripts/inlineExpreditor.js">
</script>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <!-- Begin /AssetUI/views/property/segmentRules/segmentExpressionPanel.jsp -->
  <c:set var="containerId" value="groupExpreditorContainer"/>

  <div id="<c:out value='${containerId}'/>" class="groupExpreditorContainer">
    <dspel:include otherContext="/WebUI" page="/expreditor/targeting/groupExpressionPanel.jsp">
      <dspel:param name="model" value="${requestScope.formHandler.targetingExpressionService.absoluteName}"/>
      <dspel:param name="container" value="${containerId}"/>
    </dspel:include>
  </div>
</dspel:page>
<!-- End /AssetUI/views/property/segmentRules/segmentExpressionPanel.jsp -->
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/views/property/segmentRules/segmentExpressionPanel.jsp#2 $$Change: 651448 $--%>
