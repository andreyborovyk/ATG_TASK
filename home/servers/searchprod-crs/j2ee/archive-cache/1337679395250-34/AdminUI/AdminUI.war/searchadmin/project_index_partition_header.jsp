<%--
JSP, used on project_index.jsp and project_index_partition.jsp, showing content label and sites of the logical partition. 

We have request parameter, called "status", which shows us project stage:
status = 1: Project isn't created
status = 2: Project created, but has no content sets
status = 3: Project created, has at least one content set, but has not been synchronized yet.
status = 4: Project created, has at least one content set, and has been synchronized.
Also "status" parameter can be null, it can be used to show information for project_manage_index.jsp page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_partition_header.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="searchProject" var="project" />
  <d:getvalueof param="partition" var="partition" />
  <d:getvalueof param="status" var="status" />
  <d:getvalueof param="isProduction" var="isProduction" />
  <d:getvalueof param="duplicatedProjects" var="duplicatedProjects" />
  <d:getvalueof param="sortedContentSourceSet" var="sortedContentSourceSet" />

  <c:url value="/searchadmin/content_manage.jsp" var="addContentUrl">
    <c:param name="projectId" value="${project.id}"/>
    <c:param name="logicalPartitionId" value="${partition.id}"/>
    <c:param name="action" value="add"/>
  </c:url>

  <%-- Finding the associated sites --%>
  <d:getvalueof bean="/atg/multisite/SiteManager" var="siteManager"/>
  <c:if test="${siteManager.multisiteEnabled}">
    <d:droplet name="/atg/search/multisite/droplets/FindSiteAssociations">
      <d:param name="contentIds" value="${partition.sharedId}"/>
      <d:param name="contentLabels" value="${partition.contentLabel}"/>
      <d:oparam name="output">
        <d:getvalueof var="sites" param="sites" />
      </d:oparam>
    </d:droplet>
  </c:if>

  <table cellspacing="0" cellpadding="0">
    <tr>
      <td align="left" colspan="2">
        <span class="heading">
          <c:out value="${partition.name}" />
        </span>
        <c:if test="${empty status}">
          <c:if test="${isProduction}">
            <c:url value="/searchadmin/partition_rename_popup.jsp" var="renamePopupUrl">
              <c:param name="partitionId" value="${partition.id}"/>
              <c:param name="projectId" value="${project.id}"/>
            </c:url>
            [<a href="${renamePopupUrl}" onclick="return showPopUp(this.href);">
              <fmt:message key="project_index.edit_content_set"/>
            </a>]
            <c:if test="${not project.noContent and (fn:length(project.index.logicalPartitions) > 1 or not empty sortedContentSourceSet)}">
              <c:choose>
                <c:when test="${empty sites}">
                  <c:url value="/searchadmin/partition_delete_popup.jsp" var="deletePopupUrl">
                    <c:param name="partitionId" value="${partition.id}"/>
                    <c:param name="projectId" value="${project.id}"/>
                  </c:url>
                  [<a href="${deletePopupUrl}" onclick="return showPopUp(this.href);">
                    <fmt:message key="project_index.delete_content_set"/>
                  </a>]
                </c:when>
                <c:otherwise>
                  [ <span title="<fmt:message key='project_index.delete_content_set.disabled'/>"><fmt:message key="project_index.delete_content_set"/></span> ]
                </c:otherwise>
              </c:choose>
            </c:if>
          </c:if>
          <c:if test="${not project.noContent}">
            [<a href="${addContentUrl}" onclick="return loadRightPanel(this.href);">
              <fmt:message key="project_index.buttons.new_content"/>
            </a>]
          </c:if>
        </c:if>
        <c:if test="${!empty partition.description}">
          <br/><c:out value="${partition.description}" />
        </c:if>
      </td>
    </tr>
    <tr>
      <td align="left" width="100%">
        <fmt:message key="project_index.content.label">
          <fmt:param>
            <c:if test="${not empty duplicatedProjects}">
              <fmt:message key="project_index.content_label.duplicate" var="errorTitle">
                <fmt:param value="${duplicatedProjects}"/>
              </fmt:message>
              <span class="error" title="${errorTitle}"/>
            </c:if>
            <c:out value="${partition.contentLabel}" />
          </fmt:param>
        </fmt:message>
      </td>
      <td align="right" nowrap="true">
        <c:if test="${siteManager.multisiteEnabled}">
          <span id="sites_${partition.id}_content" style="display:none;">
            <d:droplet name="/atg/search/multisite/droplets/FindSiteAssociations">
              <d:param name="contentIds" value="${partition.sharedId}"/>
              <d:param name="contentLabels" value="${partition.contentLabel}"/>
              <d:oparam name="output">
                <d:droplet name="/atg/dynamo/droplet/ForEach">
                  <d:param name="array" param="sites"/>
                  <d:oparam name="outputStart">
                    <b><fmt:message key="project_index.associated.sites.title"/></b><br/>
                  </d:oparam>
                  <d:oparam name="output">
                    <br/><d:valueof param="element.name" />
                  </d:oparam>
                </d:droplet>
              </d:oparam>
            </d:droplet>
          </span>
          <fmt:message key="project_index.associated.sites">
            <fmt:param>
              <c:choose>
                <c:when test="${empty sites}">
                  <fmt:message key="project_index.associated.sites.total">
                    <fmt:param value="0" />
                  </fmt:message>
                </c:when>
                <c:otherwise>
                  <a href="#" id="sites_${partition.id}" onclick="return false;"
                     onmouseover="contentSetSitesOver(this)" onmouseout="contentSetSitesOut(this)">
                    <fmt:message key="project_index.associated.sites.total">
                      <fmt:param>${fn:length(sites)}</fmt:param>
                    </fmt:message>
                  </a>
                </c:otherwise>
              </c:choose>
            </fmt:param>
          </fmt:message>
        </c:if>
      </td>
    </tr>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_partition_header.jsp#2 $$Change: 651448 $--%>
