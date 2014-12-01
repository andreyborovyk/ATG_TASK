<%--
  The inner edit page.  If there are tabs, this would be the contents of a single tab.

 This page includes the view's jsp page (specified in the viewmapping system).


  The following request-scoped variables are expected to be set:

  @param  multiEditMode True if during the multi edit phase of a multi edit operation
  @param  multiEditOperation name of the multi edit operation

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAssetView.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>


<dspel:page>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <dspel:importbean var="config"
                  bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="multiEditSessionInfo" value="${config.sessionInfo.multiEditSessionInfo}"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="imap" param="imap"/>

  <%-- OPER <c:out value="${multiEditOperation}"/> --%>

  <c:choose>
    <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_LIST}">

      <c:set var="listEditFormHandlerPath" value="/atg/web/assetmanager/multiedit/ListEditFormHandler" scope="request"/>

      <%-- Import list edit --%>
      <dspel:importbean var="listEditFormHandler" bean="${listEditFormHandlerPath}"/>

      <c:set target="${listEditFormHandler}" property="singleFormHandler" value="${formHandler}"/>

      <c:forEach items="${listEditFormHandler.itemFormHandlerPaths}" var="currentFH" varStatus="status">


        <c:set var="formHandlerPath" value="${currentFH}" scope="request"/>
        <dspel:importbean var="formHandler" bean="${formHandlerPath}" scope="request" />
        <c:set var="uniqueAssetID" value="${formHandler.itemDescriptorName}${formHandler.repositoryId}" scope="request"/>

        <%-- uniqueAssetID will be used as part of a JavaScript identifier, so it
             must contain only valid characters.  (TBD: Move the following code
             into a tag.) --%>
        <%
          String attrName = "uniqueAssetID";
          ServletRequest req = pageContext.getRequest();
          String id = (String) req.getAttribute(attrName);
          StringBuffer buf = new StringBuffer();
          if (java.util.regex.Pattern.matches("^[0-9].*", id))
            buf.append('_');
          buf.append(id.replaceAll("[^\\$0-9A-Za-z]", "_"));
          req.setAttribute(attrName, buf.toString());
        %>

        <c:set var="aURI" value="${listEditFormHandler.assetURIs[status.index]}"/>
        <a name="anchor_<c:out value='${aURI}'/>"></a>

          <c:choose>
            <c:when test="${status.count % 2 == 0}">
              <fieldset class="altGroup">
            </c:when>
            <c:otherwise>
              <fieldset>
            </c:otherwise>
          </c:choose>

          <legend><span><c:out value="${formHandler.repositoryItem.itemDisplayName}"/></span></legend>

          <dspel:include otherContext="${view.contextRoot}" page="${view.uri}">
            <dspel:param name="imap" value="${imap}"/>
            <dspel:param name="multiEditMode" value="${multiEditMode}"/>
            <dspel:param name="multiEditGroupIndex" value="${multiEditSessionInfo.multiEditPropertyGroupIndex}"/>
          </dspel:include>
        </fieldset>

      </c:forEach>

      <c:set var="formHandlerPath" value="${imap.formHandler.path}" scope="request"/>
      <dspel:importbean var="formHandler" bean="${formHandlerPath}" scope="request"/>

    </c:when>

    <c:otherwise>

      <dspel:include otherContext="${view.contextRoot}" page="${view.uri}">
        <dspel:param name="imap" value="${imap}"/>
        <dspel:param name="multiEditMode" value="${multiEditMode}"/>
        <dspel:param name="multiEditGroupIndex" value="${multiEditSessionInfo.multiEditPropertyGroupIndex}"/>
      </dspel:include>

      <c:if test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_APPLY_TO_ALL}">
        <div style="height:100px">
        <!-- padding so the drop down can be accessed easily -->
        </div>
      </c:if>

    </c:otherwise>

  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/editAssetView.jsp#2 $$Change: 651448 $--%>
