<%--
  Thresholds table.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_thresholds_table.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Getting Stop Word Threshold value --%>
  <d:getvalueof param="stopWordThreshold" var="stopWordThreshold"/>
  <%-- Getting Term Retrieval Threshold value --%>
  <d:getvalueof param="termRetrievalThreshold" var="termRetrievalThreshold"/>

  <table class="data simple" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <th>
          <fmt:message key="term_weight.general.thresholds.table.term_weight"/>
        </th>
        <th>
          <fmt:message key="term_weight.general.thresholds.table.effect"/>
        </th>
        <th>
          <fmt:message key="term_weight.general.thresholds.table.description"/>
        </th>
      </tr>
    </thead>
    <tbody>
      <tr>
        <td>
          <div class="termRetrievalThresholdValue" style="display:inline"><c:out value="${termRetrievalThreshold}"/></div>
          &nbsp;
          <fmt:message key="term_weight.general.thresholds.table.term_weight.fully"/>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.effect.fully"/>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.description.fully"/>
        </td>
      </tr>
      <tr>
        <td>
          <div class="stopWordThresholdValue" style="display:inline"><c:out value="${stopWordThreshold}"/></div>
          &nbsp;
          <fmt:message key="term_weight.general.thresholds.table.term_weight.partially"/>
          &nbsp;
          <div class="termRetrievalThresholdValue" style="display:inline"><c:out value="${termRetrievalThreshold}"/></div>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.effect.partially"/>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.description.partially"/>
        </td>
      </tr>
      <tr>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.term_weight.stop"/>
          &nbsp;
          <div class="stopWordThresholdValue" style="display:inline"><c:out value="${stopWordThreshold}"/></div>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.effect.stop"/>
        </td>
        <td>
          <fmt:message key="term_weight.general.thresholds.table.description.stop"/>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_thresholds_table.jsp#2 $$Change: 651448 $--%>
