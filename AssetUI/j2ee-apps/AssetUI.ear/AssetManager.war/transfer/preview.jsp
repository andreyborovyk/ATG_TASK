<%--
  Preview table of data to be exported.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/preview.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>

<dspel:page>
  <c:set var="exportFormHandlerPath" value="/atg/web/assetmanager/transfer/ExportFormHandler"/>
  <dspel:importbean var="exportFormHandler" bean="${exportFormHandlerPath}"/>
  <c:if test="${not empty exportFormHandler.previewData}">
    <table class="data">
      <c:forEach var="previewRow" items="${exportFormHandler.previewData}" varStatus="loopStatus">
        <%-- first row is table header.  make it bold --%>        
        <c:set var="cellStyle" value=""/>
        <c:if test="${loopStatus.index eq 0}">
          <c:set var="cellStyle" value="contentHeaderBold"/>
        </c:if>

        <tr>
          <%-- render a table cell for each previewCell --%>
          <c:forEach var="previewCell" items="${previewRow}">
            <td><div class="<c:out value='${cellStyle}'/>" title="<c:out value='${previewCell.type}'/>"><c:out value="${previewCell.value}"/></div></td>
          </c:forEach>
        </tr>
      </c:forEach>
    </table>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/preview.jsp#2 $$Change: 651448 $ --%>

