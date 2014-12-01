<%--
Content sets view by content JSP. This page is included into syncronization_manual.jsp page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_content_table.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler" var="synchronizationManualFormHandler"/>
  <c:set value="${synchronizationManualFormHandler.projectId}" var="projectId"/>
  <c:set value="${synchronizationManualFormHandler.contentSetsList}" var="contentSetsList"/>
  <c:set value="${synchronizationManualFormHandler.contentList}" var="contentList"/>
  <admin-beans:getIndexPartitions var="indexPartitions" projectId="${projectId}"/>
  <table class="data" cellspacing="0" cellpadding="0" id="contentTableAll">
    <thead>
      <tr>
        <th>
          <input style="margin-left:2px;" type="checkbox" id="csAllPartitions" class="selectAll"
           onclick="setAllContentCheckboxState(this.checked);disableIndexingButtons();"/>
          <fmt:message key="synchronization_manual.table.header.content.set"/>
        </th>
        <th>
          <fmt:message key="synchronization_manual.table.header.content"/>
        </th>
        <th>
          <fmt:message key="synchronization_manual.table.header.content.location"/>
        </th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${indexPartitions}" var="partition" varStatus="partCursor">
        <c:set var="altClass" value="${partCursor.index % 2 == 1}"/>
        <common:contentSourceFindByLogicalPartition var="cssForPartition" logicalPartition="${partition.id}" />
        <c:set var="isSelectedPart" value="${adminfunctions:isContains(contentSetsList, partition.id)}"/>
        <c:if test="${empty cssForPartition}">
          <tr>
            <td>
              <input type="checkbox" value="${partition.id}" name="contentSetsList" id="${partition.id}"
                <c:if test="${isSelectedPart}">checked="true"</c:if>
                onclick="setAllContentCheckboxStateByPartition(${partition.id}, this.checked);disableIndexingButtons();"/>
              <label for="<c:out value='${partition.id}'/>">
                <c:out value="${partition.name}"/>
              </label>
            </td>
            <td><fmt:message key="synchronization_manual.table.no.content"/></td>
            <td></td>
          </tr>
        </c:if>
        <c:if test="${not empty cssForPartition}">
          <c:forEach items="${cssForPartition}" var="content" varStatus="contentCursor">
            <tr <c:if test="${altClass}">class="alt"</c:if>>
            <c:if test="${contentCursor.first}" >
              <td rowspan="${fn:length(cssForPartition)}">
                <input type="checkbox" value="${partition.id}" name="contentSetsList" id="${partition.id}"
                  <c:if test="${isSelectedPart}">checked="true"</c:if>
                  onclick="setAllContentCheckboxStateByPartition(${partition.id}, this.checked);disableIndexingButtons();"/>
                <label for="<c:out value='${partition.id}'/>">
                  <c:out value="${partition.name}"/>
                </label>
              </td>
            </c:if>
            <td>
              <input type="checkbox" value="${content.id}" name="contentList" id="contentList${partition.id}${content.id}" onclick="disableIndexingButtons();"
                 <c:if test="${adminfunctions:isContains(contentList, content.id)}">checked="true"</c:if>
                 <c:if test="${isSelectedPart}">disabled="true"</c:if>/>
              <fmt:message var="link_tooltip" key='synchronization_manual.edit_content_set.tooltip'/>
              <c:url var="contentManageUrl" value="/searchadmin/content_manage.jsp"/>
              <label for="<c:out value='contentList${partition.id}${content.id}'/>">
                <c:out value="${content.name}"/>
              </label> 
            </td>
            <td>
              <admin-beans:getContentLocation var="contentLocation" contentSource="${content}" />
              <c:out value="${contentLocation}"/>
            </td>
            </tr>
          </c:forEach>
        </c:if>
      </c:forEach>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_content_table.jsp#2 $$Change: 651448 $--%>
