<%--
Index Audit page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_audit.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof var="projectId" param="projectId" />
  <d:getvalueof var="compareToIndexId" param="compareToIndexId" />
  <d:getvalueof var="showChangesOnly" param="showChangesOnly" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <fmt:setBundle var="l10nContext" basename="atg.searchadmin.WebAppResources" />
      <admin-beans:getIndexAuditData var="elements" varCurrentIndex="currentIndex" varCompareToIndex="compareToIndex"
          projectId="${projectId}" compareToIndexId="${compareToIndexId}" showChangesOnly="${showChangesOnly}"
          bundle="${l10nContext.resourceBundle}" />
  
      <c:if test="${currentIndex == null}">
        <p><fmt:message key="project_index_audit.description" /></p>
        <p><fmt:message key="project_index_review.unavailable" /></p>
      </c:if>
      <c:if test="${currentIndex != null}">
        <fmt:message var="timeFormat" key="timeFormat" />
        <br/>
        <table cellspacing="0" cellpadding="0" width="95%">
          <tr>
            <c:if test="${compareToIndex != null}">
              <td valign="top">
                <table class="form" cellspacing="0" cellpadding="0">
                  <tr>
                    <td class="label" nowrap=""><fmt:message key="project_index_audit.compare_to_index.label" /></td>
                    <td nowrap="">
                      <fmt:formatDate value="${compareToIndex.endTime}" pattern="${timeFormat}" />
                      (<fmt:message key="synchronization.task.${compareToIndex.taskType.enum}" />)
                      <fmt:message var="selectTitle" key="project_index_audit.compare_to_index.select.title" />
                      <c:url var="popupUrl" value="/searchadmin/project_index_audit_select.jsp">
                        <c:param name="projectId" value="${projectId}"/>
                        <c:param name="compareToIndexId" value="${compareToIndexId}"/>
                        <c:param name="showChangesOnly" value="${showChangesOnly}"/>
                      </c:url>
                      [<a href="${popupUrl}" title="${selectTitle}" onclick="return showPopUp(this.href);">
                        <fmt:message key="project_index_audit.compare_to_index.select"/>
                      </a>]
                    </td>
                  </tr>
                  <tr>
                    <td class="label" nowrap=""><fmt:message key="project_index_audit.environment.label" /></td>
                    <td nowrap="">
                      <tags:join items="${compareToIndex.searchEnvironmentSelections}" var="env" delimiter=", ">
                        <c:out value="${env.envName}" />
                      </tags:join>
                    </td>
                  </tr>
                </table>
              </td>
              <td width="20">&nbsp;</td>
            </c:if>
            <td valign="top">
              <table class="form" cellspacing="0" cellpadding="0">
                <tr>
                  <td class="label" nowrap=""><fmt:message key="project_index_audit.current_index.label" /></td>
                  <td nowrap="">
                    <fmt:formatDate value="${currentIndex.endTime}" pattern="${timeFormat}" />
                    (<fmt:message key="synchronization.task.${currentIndex.taskType.enum}" />)
                  </td>
                </tr>
                <tr>
                  <td class="label" nowrap=""><fmt:message key="project_index_audit.environment.label" /></td>
                  <td nowrap="">
                    <tags:join items="${currentIndex.searchEnvironmentSelections}" var="env" delimiter=", ">
                      <c:out value="${env.envName}" />
                    </tags:join>
                  </td>
                </tr>
              </table>
            </td>
            <td valign="top">
              <span class="ea"><tags:ea key="embedded_assistant.project_index_audit" /></span>
            </td>      
          </tr>
        </table>

        <c:choose>
          <c:when test="${compareToIndex != null}">
            <table cellpadding="0" cellspacing="0">
              <tr>
                <td><fmt:message key="project_index_audit.show.label" /></td>
                <td width="10">&nbsp;</td>
                <c:url var="otherShowUrl" value="/searchadmin/project_index_audit.jsp">
                  <c:param name="projectId" value="${projectId}"/>
                  <c:param name="compareToIndexId" value="${compareToIndexId}"/>
                  <c:param name="showChangesOnly" value="${showChangesOnly == 'false'}"/>
                </c:url>
                <c:set var="toggleLeftClassPrefix" value="toggle_left_${showChangesOnly != 'false' ? 'down' : 'up'}" />
                <c:set var="toggleRightClassPrefix" value="toggle_right_${showChangesOnly != 'false' ? 'up' : 'down'}" />
                <td class="${toggleLeftClassPrefix}_l">&nbsp;</td>
                <td class="${toggleLeftClassPrefix}_m"><c:choose>
                  <c:when test="${showChangesOnly != 'false'}"><fmt:message key="project_index_audit.show.changes_only" /></c:when>
                  <c:otherwise><a href="${otherShowUrl}" onclick="return loadRightPanel(this.href);">
                    <fmt:message key="project_index_audit.show.changes_only" /></a></c:otherwise>
                </c:choose></td>
                <%-- the following line consists of 2 cells w/out spacing - it's a fix for removing a bad space between cells in FF3.6 --%>
                <td class="${toggleLeftClassPrefix}_r">&nbsp;</td><td class="${toggleRightClassPrefix}_l">&nbsp;</td>
                <td class="${toggleRightClassPrefix}_m"><c:choose>
                  <c:when test="${showChangesOnly == 'false'}"><fmt:message key="project_index_audit.show.all_elements" /></c:when>
                  <c:otherwise><a href="${otherShowUrl}" onclick="return loadRightPanel(this.href);">
                    <fmt:message key="project_index_audit.show.all_elements" /></a></c:otherwise>
                </c:choose></td>
                <td class="${toggleRightClassPrefix}_r">&nbsp;</td>
              </tr>
            </table>
            <br/>
          </c:when>
          <c:otherwise>
            <fmt:message key="project_index_audit.compare_to_index.unavailable" />
          </c:otherwise>
        </c:choose>
  
        <table class="data" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="project_index_audit.table.index_element" /></th>
              <c:if test="${compareToIndex != null}">
                <th width="15%" nowrap="" style="text-align:right">
                  <fmt:message key="project_index_audit.table.count_in_index">
                    <fmt:param><fmt:formatDate value="${compareToIndex.endTime}" pattern="${timeFormat}" /></fmt:param>
                  </fmt:message>
                </th>
              </c:if>
              <th width="15%" nowrap="" style="text-align:right">
                <fmt:message key="project_index_audit.table.count_in_current">
                  <fmt:param><fmt:formatDate value="${currentIndex.endTime}" pattern="${timeFormat}" /></fmt:param>
                </fmt:message>
              </th>
              <c:if test="${compareToIndex != null}">
                <th colspan="3" style="text-align:center"><fmt:message key="project_index_audit.table.change" /></th>
              </c:if>
            </tr>
          </thead>
          <tbody>
            <c:if test="${compareToIndex != null and empty elements}">
              <tr>
                <td colspan="6"><fmt:message key="project_index_audit.table.empty" /></td>
              </tr>
            </c:if>
            <c:forEach items="${elements}" var="group">
              <tr class="group">
                <td colspan="${compareToIndex != null ? 6 : 2}"><fmt:message key="project_index_audit.group.${group.name}" /></td>
              </tr>
              <c:forEach items="${group.factors}" var="factor" varStatus="factorStatus">
                <tr class="${factorStatus.index % 2 == 0 ? '' : 'alt'}">
                  <td class="grouped">${factor.name}</td>
                  <c:if test="${compareToIndex != null}">
                    <td align="right">
                      <c:choose>
                        <c:when test="${factor.countInIndex >= 0}">
                          ${factor.countInIndex}
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="project_index_audit.table.not_exist" />
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </c:if>
                  <td align="right">
                    <c:choose>
                      <c:when test="${factor.countInCurrentIndex >= 0}">
                        ${factor.countInCurrentIndex}
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="project_index_audit.table.not_exist" />
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <c:if test="${compareToIndex != null}">
                    <td class="iconCell">
                      <c:choose>
                        <c:when test="${factor.na}">
                          <fmt:message key="project_index_audit.table.na" />
                        </c:when>
                        <c:when test="${factor.new}">
                          <fmt:message key="project_index_audit.table.new" />
                        </c:when>
                        <c:when test="${factor.countInIndex == factor.countInCurrentIndex}">
                          <fmt:message key="project_index_audit.table.equals" />
                        </c:when>
                        <c:otherwise>
                          <fmt:formatNumber value="${factor.percent}" type="percent" />
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td class="iconCell">
                      <c:choose>
                        <c:when test="${factor.less}">
                          <span class="icon propertyDecrease" title='<fmt:message key="project_index_audit.table.decrease.title" />' />
                        </c:when>
                        <c:when test="${factor.more}">
                          <span class="icon propertyIncrease" title='<fmt:message key="project_index_audit.table.increase.title" />' />
                        </c:when>
                      </c:choose>
                    </td>
                    <td class="iconCell">
                      <c:choose>
                        <c:when test="${factor.na}">
                          <span class="icon propertyAlert" title='<fmt:message key="project_index_audit.table.alert.na" />' />
                        </c:when>
                        <c:when test="${factor.less and factor.countInCurrentIndex == 0}">
                          <span class="icon propertyAlert" title='<fmt:message key="project_index_audit.table.alert.dropped_to_zero" />' />
                        </c:when>
                      </c:choose>
                    </td>
                  </c:if>
                </tr>
              </c:forEach>
            </c:forEach>
          </tbody>
        </table>
        <br/>
      </c:if>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_audit.jsp#2 $$Change: 651448 $--%>
