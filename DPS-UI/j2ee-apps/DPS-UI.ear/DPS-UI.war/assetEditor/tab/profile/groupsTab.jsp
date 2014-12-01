 <%--
  Groups tab for  assets.

  @param  view          A request scoped, MappedView item for this view
  @param  formHandler   The form handler

  @version $Id $$Change $
  @updated $DateTime $$Author $
  --%>

<!-- Begin /AssetManager/assetEditor/tab/groupsTab.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"    uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"            %>

<dspel:page>
  <c:set var="debug" value="false"/>

  <dspel:importbean var="config"
                    bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="view" param="view"/>
  <dspel:getvalueof var="formHandler" param="formHandler"/>

  <c:set var="repitem" value="${formHandler.repositoryItem}"/>
  <dspel:tomap var="item" value="${repitem}"/>
  <c:if test="${debug}">
    [groupsTab.jsp]: view=<c:out value="${view}"/><br />
    [groupsTab.jsp]: formHandler=<c:out value="${formHandler}"/><br />
    [groupsTab.jsp]: item=<c:out value="${repitem}"/><br />
  </c:if>

  <br />
  <fieldset>
  <legend><span><fmt:message key="userEditor.userSegments.fieldSetLabel"/></span></legend>

  <p>
  <input type="checkbox" id="hideFalseRowsCheckbox" onclick="toggleHideFalseRows()"/> <fmt:message key="userEditor.hideSegmentsUserNotMemberOf"/>
  </p>
  <table class="data" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <th><fmt:message key="userEditor.segmentHeader"/></th>
        <th><fmt:message key="userEditor.membershipHeader"/></th>
      </tr>
    </thead>

    <tbody>
      <asset-ui:getGroupPropertyNames id="props" repositoryItem="${repitem}"/>
      <c:forEach var="prop" items="${props}" varStatus="status">
        <dspel:droplet name="/atg/dynamo/droplet/BeanProperty">
          <dspel:param name="bean" value="${item}"/>
          <dspel:param name="propertyName" value="${prop}"/>
          <dspel:oparam name="output">
            <dspel:getvalueof var="propval" param="propertyValue"/>
            <c:set var="rowname" value="${propval}TableRow"/>

            <c:choose>
              <c:when test="${ status.index % 2 != 0 }">
                <c:set var="rowClass" value="alt"/>
              </c:when>
              <c:otherwise>
                <c:set var="rowClass" value=""/>
              </c:otherwise>
            </c:choose>

            <tr class="<c:out value='${rowClass}'/>" id="<c:out value='${rowname}'/>">
              <td><c:out value="${prop}"/></td>
              <td>
                <c:out value="${propval}"/>
              </td>
            </tr>
          </dspel:oparam>
        </dspel:droplet>
      </c:forEach>
    </tbody>
  </table>
</fieldset>
  <P>


  <script type="text/javascript" >

    //
    // show or hide the false rows
    //
    function toggleHideFalseRows() {
      var hideEm = document.getElementById("hideFalseRowsCheckbox").checked;
      var allrows = document.getElementsByTagName("tr");
      for (var ii=0; ii<allrows.length; ii++) {
        if (allrows[ii].id === ("falseTableRow")) {
          if(hideEm)
            allrows[ii].style.display="none";
          else
            allrows[ii].style.display="";
        }
      }
    }
  </script>

</dspel:page>

<!-- End /AssetManager/assetEditor/tab/groupsTab.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/profile/groupsTab.jsp#2 $$Change: 651448 $--%>
