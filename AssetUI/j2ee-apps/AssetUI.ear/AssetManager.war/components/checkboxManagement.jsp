<%--
  checkboxManagment.jsp
  Fragment for managing checkbox state in list views.
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>
  <c:set var="sessionInfoName" value="${config.sessionInfoPath}"/>

  <c:url var="checkURL" value="/components/listAction.jsp">
    <c:param name="listManager"      value="${sessionInfoName}"/>
  </c:url>
  <c:url var="checkAllURL" value="/components/listAction.jsp">
    <c:param name="listManager"      value="${sessionInfoName}"/>
    <c:param name="action"           value="addAll"/>
  </c:url>
  <c:url var="uncheckAllURL" value="/components/listAction.jsp">
    <c:param name="listManager"      value="${sessionInfoName}"/>
    <c:param name="action"           value="removeAll"/>
  </c:url>


  <script type="text/javascript"
    src="<c:out value='${config.contextRoot}'/>/scripts/checkboxManagement.js">
  </script>

  <script type="text/javascript" > 
    //
    // initialize the numChecks var from the appropriate server side component  
    //
    <c:set var="checkedItemCount" value="${sessionInfo.checkedItemCount}"/>

    atg.assetmanager.checkboxes.initialize({
      numChecks:              <c:out value="${checkedItemCount}"/>,
      clearChecksOnTabChange: true,
      checkURL:               "<c:out value='${checkURL}' escapeXml='false'/>",
      checkAllURL:            "<c:out value='${checkAllURL}' escapeXml='false'/>",
      uncheckAllURL:          "<c:out value='${uncheckAllURL}' escapeXml='false'/>"
    });

  </script>

  <%-- This is a hidden div that allows list elements to send information
       back to the server --%>
  <div style="display: none">
    <dspel:iframe id="actionFrame" page="/components/listAction.jsp?listManager=${sessionInfoName}"/>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/checkboxManagement.jsp#2 $$Change: 651448 $--%>
