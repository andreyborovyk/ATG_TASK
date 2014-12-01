<%--
  Toolbar button confirmation dialog for asset manager UI.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/toolbarConfirm.jsp#2 $$Change: 651448 $
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
  <dspel:getvalueof var="buttonName" param="buttonName"/>
  <dspel:getvalueof var="confirmationGeneratorName" param="confirmationGeneratorName"/>

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
        function doAction(buttonName) {
          document.body.style.cursor = "wait";
          var elem = document.getElementById("okButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          elem = document.getElementById("cancelButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          parent.document.getElementById(buttonName).click();
        }
      </script>

    </head>

    <body id="assetBrowser">
    <h1><fmt:message key="confirm.dialog.title"/></h1>
<%--
DEBUG: confirmationGeneratorName = <br />
<c:out value="${confirmationGeneratorName}"/><br />
DEBUG: sessionInfoName = <br />
<c:out value="${sessionInfoName}"/><br />
DEBUG: buttonName = <c:out value="${buttonName}"/><br />
--%>
    <c:catch var="exc">
      <asset-ui:getConfirmationMessage 
         var="confMsg"
         messageGeneratorComponentName="${confirmationGeneratorName}"/>
    </c:catch>
    <c:if test="${not empty exc}">
      <web-ui:getSecurityExceptionMessage 
         var="errorMsg" 
         exception="${exc}"/>
        <c:if test="${not empty errorMsg}" >
          <script type="text/javascript">
            parent.messages.addError("<c:out value='${errorMsg}'/>");
          </script>
        </c:if>  
    </c:if>         

      <div id="confirmContentBody">
        <div id="confirmScrollContainer">
          <p class="confirmMessage">
            <c:out value="${confMsg.message}"/>   <br />
          </p>
            <UL>
            <c:forEach var="msg" 
                       items="${confMsg.regularMessages}">
              <LI><c:out value="${msg}"/>
            </c:forEach>
            </UL>
            <br />
          <p class="specialMessages">
            <c:forEach var="msg" 
                       items="${confMsg.specialMessages}">
              <c:out value="${msg}"/><br />
            </c:forEach>
          </p>
        </div>
      </div>

      <div id="assetBrowserFooterRight">
        <%-- Omit OK button if no access --%>
        <c:if test="${empty exc}">
          <a id="okButton" href="javascript:doAction('<c:out value="${buttonName}"/>')"
             class="button" title="<fmt:message key='common.ok.title'/>">
            <span>
              <fmt:message key="common.ok"/>
            </span>
          </a>
        </c:if>
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
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/toolbarConfirm.jsp#2 $$Change: 651448 $--%>
