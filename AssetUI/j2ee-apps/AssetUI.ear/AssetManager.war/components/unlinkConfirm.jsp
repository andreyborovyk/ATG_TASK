<%--
  Task outcome confirmation dialog for asset manager UI.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/unlinkConfirm.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <%-- unpack the param(s) --%>
  <dspel:getvalueof var="buttonName" param="buttonName"/>
  <dspel:getvalueof var="functionName" param="functionName"/>
  <dspel:getvalueof var="functionParamValue" param="functionParamValue"/>

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
        function disableButtons() {
          document.body.style.cursor = "wait";
          var elem = document.getElementById("okButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
          elem = document.getElementById("cancelButton");
          elem.className = "button disabled";
          elem.onclick = function() { return false; };
        }
      </script>

    </head>

    <body id="assetBrowser">
      <h1>
        <c:choose>
          <c:when test="${functionParamValue eq 'moveFromAll'}">
            <fmt:message key='browseTab.button.launchMove'/>
          </c:when>
          <c:otherwise>
            <fmt:message key='browseTab.button.unlink'/>
          </c:otherwise>
        </c:choose>
      </h1>

      <div id="confirmContentBody">
        <div id="confirmScrollContainer">
          <p class="confirmMessage">

            <fmt:message key="confirm.unlink.general"/><p />
            <c:forEach var="assetURI" items="${sessionInfo.checkedItems}">
              <c:catch var="exception">
                <asset-ui:resolveAsset var="thisAsset" uri="${assetURI}"/>
              </c:catch>
              <c:if test="${not empty exception}">
                <web-ui:getSecurityExceptionMessage exception="${exception}" var="errorMsg"/>
                  <c:if test="${not empty errorMsg}" >
                    <script type="text/javascript">
                      parent.messages.addError("<c:out value='${errorMsg}'/>");
                    </script>
                  </c:if>
              </c:if>
              

              <%-- check to see if we have a treeable type --%>
              <web-ui:getTree var="unlinktree" 
                  repository="${thisAsset.containerPath}" 
                  itemType="${thisAsset.type}"
                  treeRegistry="${sessionInfo.treeRegistryPath}"/> 
              <c:choose>  
                <c:when test="${empty unlinktree}">
                  <fmt:message var="cannotUnlinkMsg" key="error.unlink.unsupportedType">
                    <fmt:param value="${thisAsset.typeDisplayName}"/>
                  </fmt:message>
                  <c:out value="${cannotUnlinkMsg}"/>
                  <p />
                </c:when>

                <c:otherwise>
                  <web-ui:getTreeAncestry 
                     var="ancestry"
                     assetURI="${assetURI}"
                     treeRegistry="${sessionInfo.treeRegistryPath}"/> 
                  <%-- Handle case where item has no parent --%>
                  <c:choose>
                    <c:when test="${empty ancestry}" >                    
                        <fmt:message var="cannotUnlinkMsg" key="error.unlink.noParent">
                          <fmt:param value="${thisAsset.displayName}"/>
                        </fmt:message>
                        <c:out value="${cannotUnlinkMsg}"/>
                        <P />
                    </c:when>                      
                    <c:otherwise>
                      <c:forEach var="ancestorPath" items="${ancestry}">
                        <c:forEach var="ancestorNode" items="${ancestorPath}" varStatus="loopstatus">
                          <c:out value="${ancestorNode.label}"/>
                          <c:if test="${not loopstatus.last}">
                            &gt;
                          </c:if> 
                        </c:forEach>
                        <P />
                      </c:forEach>
                    </c:otherwise>
                  </c:choose>                  
                </c:otherwise>
              </c:choose>
            </c:forEach> 
            </p>
          </div>
        </div>
        <div id="assetBrowserFooterRight">

        <%-- Omit OK button if no access --%>
        <c:if test="${empty exception}">
          <c:if test="${not empty buttonName}">
            <a id="okButton" href="javascript:disableButtons();parent.document.getElementById('<c:out value="${buttonName}"/>').click()" 
               class="button" title="<fmt:message key='common.ok.title'/>">
              <span>
                <fmt:message key="common.ok"/>
              </span>
            </a>
          </c:if>
        
          <c:if test="${not empty functionName}">
          <a id="okButton" href="javascript:disableButtons();parent.<c:out value='${functionName}'/>('<c:out value='${functionParamValue}'/>')"
             class="button" title="<fmt:message key='common.ok.title'/>">
              <span>
                <fmt:message key="common.ok"/>
              </span>
            </a>
          </c:if>
        </c:if>

<%--
        <c:choose>
          <c:when test="${not empty buttonName}">
          </c:when>
          <c:when test="${not empty function}">
            <a href="javascript:parent.<c:out value="${function}"/>').click()" class="button" title="<fmt:message key='common.ok.title'/>">
              <span>
                <fmt:message key="common.ok"/>
              </span>
            </a>
          </c:when>
        </c:choose>
--%>        

        <a id="cancelButton" href="javascript:parent.showIframe('confirm')"
           class="button" title="<fmt:message key='common.cancel.title'/>">
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
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/unlinkConfirm.jsp#2 $$Change: 651448 $--%>
