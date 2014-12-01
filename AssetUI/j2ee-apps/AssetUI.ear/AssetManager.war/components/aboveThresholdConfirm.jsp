<%--
  Confirmation dialog for the toolbar.  In this dialog we inform the user that he 
  is about to operate on a large number of assets and that the operation may take 
  some time to complete, and then we ask "Would you like to continue".
   
  Url params: 
    @param function    The function to invoke if the user chooses to continue.
    @param action      The name of the action being performed (link, delete, addToProject etc). This 
                       will be supplied as a parameter to the function. 

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/aboveThresholdConfirm.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="functionName" param="function"/>
  <dspel:getvalueof var="action" param="action"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">

    <head>
      <title>
        <fmt:message key="common.pageTitle"/>
      </title>
      
      <dspel:include page="head.jsp"/>

      <script type="text/javascript">
        function doAction() {
          var action = "<c:out value='${action}'/>";
          var functionName = "<c:out value='${functionName}'/>";
          document.body.style.cursor = "wait";
          var elem = document.getElementById("okButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          elem = document.getElementById("cancelButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          var funcName = "parent." + functionName;
          eval(funcName + '(action)');
        }
      </script>

    </head>

    <body id="assetBrowser">
    <h1><fmt:message key="confirm.dialog.title"/></h1>


      <div id="confirmContentBody">

        <div id="confirmScrollContainer">        

          <%-- 
           DEBUG: action = <c:out value="${action}"/><br />
           DEBUG: functionName = <c:out value="${functionName}"/><br />
          --%>

          <p class="confirmMessage">
          <c:set var="confirmMsgKey" value="confirm.tooManyItems.${action}"/>
          <fmt:message key="${confirmMsgKey}">
            <fmt:param value="${sessionInfo.currentAssetBrowser.checkedItemCount}"/>
          </fmt:message>
          <c:if test="${action ne 'export'}">
            <fmt:message key="confirm.tooManyItems.thisWillBeSlow"/>
          </c:if>
          </p>
        </div>
      </div>

      <div id="assetBrowserFooterRight">
        <a id="okButton" href="javascript:doAction()"
           class="button" title="<fmt:message key='common.ok.title'/>">
          <span>
            <fmt:message key="common.ok"/>
          </span>
        </a>
        <a id="cancelButton" href="javascript:parent.showIframe('confirm')"
           class="button abTrigger" title="<fmt:message key='common.cancel.title'/>">
          <span>
            <fmt:message key="common.cancel"/>
          </span>
        </a>
      </div>
      <div id="assetBrowserFooterLeft">
      </div>

    </body>
  </html>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/aboveThresholdConfirm.jsp#2 $$Change: 651448 $--%>
