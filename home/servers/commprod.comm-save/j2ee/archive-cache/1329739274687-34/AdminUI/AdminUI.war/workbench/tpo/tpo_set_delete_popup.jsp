<%--
  TPO delete JSP

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_delete_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $ 
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="tpoSetId" var="tpoSetId" scope="page" />
  <d:getvalueof param="level" var="level" scope="page" />

  <c:if test="${level eq 'sao'}">
    <c:url var="successUrl" value="${dictionaryPath}/sao/sao_sets_browse.jsp"/>
  </c:if>
  <c:if test="${level ne 'sao'}">
    <c:url var="successUrl" value="${tpoPath}/tpo_browse.jsp"/>
  </c:if>
  
  <c:url var="errorURL" value="tpo_set_delete_popup.jsp">
    <c:param name="tpoSetId" value="${tpoSetId}" />
  </c:url>
  <d:form action="${errorURL}" method="post">

    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTPOSetFormHandler.successURL"
             value="${successUrl}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTPOSetFormHandler.errorURL"
             value="${errorURL}"/>
    <d:input type="hidden" bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTPOSetFormHandler.tpoSetId"
             value="${tpoSetId}"/>

    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="tpo_set_delete.question1">
          <fmt:param>
            <strong>
              <c:if test="${level ne 'sao'}">
                <fmt:message key="tpo_set_delete.question2">
                  <fmt:param>
                    <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${tpoSetId}" var="tpoSet"/>
                    <c:out value="${tpoSet.name}"/>
                  </fmt:param>
                </fmt:message>
              </c:if>
              <c:if test="${level eq 'sao'}">
                <fmt:message key="tpo_set_delete.question2.sao">
                  <fmt:param>
                    <tpo:textProcessingOptionsSetFindByPrimaryKey textProcessingOptionsSetId="${tpoSetId}" var="tpoSet"/>
                    <c:out value="${tpoSet.name}"/>
                  </fmt:param>
                </fmt:message>
              </c:if>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>

        <c:if test="${level eq 'content'}">
          <fmt:message key="tpo_set_delete.used_in_contents"/>
        </c:if>
        <c:if test="${level eq 'index'}">
          <fmt:message key="tpo_set_delete.used_in_search_projects"/>
        </c:if> 
      <ul>
        <c:if test="${level eq 'content'}">
          <admin-beans:getContentByUsedTPOItem var="contents" tpoId="${tpoSetId}"/>
          <c:choose>
            <c:when test="${empty contents}">
              <li>
                <fmt:message key="tpo_set_delete.used_in_search_projects.none"/>
              </li>
            </c:when>
            <c:otherwise>
              <c:forEach items="${contents}" var="currentContent">
                <li>
                  <c:out value="${currentContent}"/>
                </li>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </c:if>

        <c:if test="${level eq 'index'}">
          <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${tpoSetId}" itemType="project_tpo"/>
          <c:choose>
            <c:when test="${empty projects}">
              <li>
                <fmt:message key="tpo_set_delete.used_in_search_projects.none"/>
              </li>
            </c:when>
            <c:otherwise>
              <c:forEach items="${projects}" var="currentProject">
                <li>
                  <c:out value="${currentProject.name}"/>
                </li>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </c:if>
      </ul>
    </div>
    <div class="footer" id="popupFooter">
      <fmt:message key="tpo_set_delete.delete.button" var="deleteButtonTitle"/>
      <fmt:message key="tpo_set_delete.delete.button.tooltip" var="deleteButtonToolTip"/>
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/DeleteTPOSetFormHandler.delete" type="submit"
               value="${deleteButtonTitle}" iclass="formsubmitter"
               title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='tpo_set_delete.cancel.button'/>"
             onclick="closePopUp()" title="<fmt:message key='tpo_set_delete.cancel.button.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_delete_popup.jsp#2 $$Change: 651448 $--%>
