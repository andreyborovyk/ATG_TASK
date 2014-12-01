<%--
  This is the summary table body

  @param  imap   ItemMapping
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/summaryTable.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="bundleName" value="${config.resourceBundle}"/>
  <fmt:setBundle var="bundle" basename="${bundleName}"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="imap" param="imap"/>
  
  <%-- Get the summary table droplet --%>
  <c:set var="summaryTableDropletPath" 
         value="${requestScope.atgItemViewMapping.attributes.summaryTableDropletPath}"/>
         
  <c:if test="${empty summaryTableDropletPath}">
    <c:set var="summaryTableDropletPath" 
           value="/atg/web/assetmanager/editor/SummaryTableDroplet"/>
  </c:if>

  <dspel:droplet name="${summaryTableDropletPath}">
    <%-- Input Parameters --%>
    <dspel:param name="mappedItem" value="${imap}"/>

    <%-- outputStart Open Parameter --%>
    <dspel:oparam name="outputStart">
      <%-- start table --%>
      <table class="atg_dataTable atg_summaryTable">
    </dspel:oparam>

    <%-- tableHeaderRow Open Parameter --%>
    <dspel:oparam name="tableHeaderRow">
      <tr>
        <%-- render table header cells --%>
        <%-- iterate through each configured column --%>
        <dspel:droplet name="/atg/dynamo/droplet/ForEach">
          <dspel:param name="array" param="headerCellJSP"/>

          <dspel:oparam name="output">
            <dspel:getvalueof var="cellInfo" param="element"/>
            <dspel:include otherContext="${cellInfo.contextRoot}" page="${cellInfo.page}">
              <dspel:param name="view" value="${view}"/>
            </dspel:include>
          </dspel:oparam>
        </dspel:droplet>
      </tr>
    </dspel:oparam>

    <%-- tableBodyRow Open Parameter --%>
    <dspel:oparam name="tableBodyRow">
      <dspel:getvalueof var="rowId" param="rowId"/>
        <c:choose>
          <c:when test="${rowId % 2 == 0}">
            <tr>
          </c:when>
          <c:otherwise>
            <tr class="atg_altRow">
          </c:otherwise>
        </c:choose>

        <%-- render table body cells --%>
        <%-- iterate through each configured column --%>
        <dspel:droplet name="/atg/dynamo/droplet/ForEach">
          <dspel:param name="array" param="bodyCellJSP"/>

          <dspel:oparam name="output">
            <td>
              <dspel:getvalueof var="cellInfo" param="element"/>
              <dspel:include otherContext="${cellInfo.contextRoot}" page="${cellInfo.page}">
                <dspel:param name="ivm"   param="mappedItemView"/>
                <dspel:param name="tabId" param="tabId"/>
              </dspel:include>
            </td>
          </dspel:oparam>
        </dspel:droplet>
      </tr>
    </dspel:oparam>

    <%-- tableFooterRow Open Parameter --%>
    <dspel:oparam name="tableFooterRow">
      <tr>
        <%-- render table header cells --%>
        <%-- iterate through each configured column --%>
        <dspel:droplet name="/atg/dynamo/droplet/ForEach">
          <dspel:param name="array" param="footerCellJSP"/>

          <dspel:oparam name="output">
            <td>
              <dspel:getvalueof var="cellInfo" param="element"/>
              <dspel:include otherContext="${cellInfo.contextRoot}" page="${cellInfo.page}">
                <dspel:param name="view" value="${view}"/>
              </dspel:include>
            </td>
          </dspel:oparam>
        </dspel:droplet>
      </tr>
    </dspel:oparam>

    <%-- outputEnd Open Parameter --%>
    <dspel:oparam name="outputEnd">
      <%-- end table --%>
      </table>
    </dspel:oparam>

    <%-- error Open Parameter --%>
    <dspel:oparam name="error">
      <p> <dspel:valueof param="element"/>
    </dspel:oparam>
  </dspel:droplet>
  <br>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/summaryTable.jsp#2 $$Change: 651448 $--%>
