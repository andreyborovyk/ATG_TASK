<%--
Index summary overview statistics  page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview_content.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="partitionId" var="partitionId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <admin-beans:getIndexingOverview var="overview" projectId="${projectId}"
      partitionId="${partitionId}" syncTaskId="${syncTaskId}" />
  <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${syncTaskId}" projectId="${projectId}" />
  <table class="indexReviewTable" cellspacing="0" cellpadding="0">
    <tr>
      <td>
        <admin-beans:getLogicalPartitionById var="partition" id="${partitionId}"/>
        <h2>${partition.name}</h2>
      </td>
      <td align="right">
        <fmt:message key="index_verification_overview.current_index" />
        <fmt:message var="timeFormat" key="timeFormat" />
        <fmt:formatDate value="${syncTaskHistoryInfo.currentEndDate}" pattern="${timeFormat}" />
        (<fmt:message key="synchronization.task.${syncTaskHistoryInfo.syncTaskType}" />)
      </td>
    </tr>
  </table>

  <table class="indexReviewTable" cellspacing="0" cellpadding="0">
    <tr>
      <td colspan="3">
        <h2><fmt:message key="index_verification_overview.content.items"/></h2>
      </td>
    </tr>
    <tr valign="top">
      <td class="tdPadding">
        <table class="data"  cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.by.type"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key="index_verification_overview.table.by.type.unstructured"/></td>
              <td>${overview.documents}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.by.type.structured"/></td>
              <td>${overview.structured}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.by.type.total"/></td>
              <td>${overview.total}</td>
            </tr>
          </tbody>
        </table>
      </td>
      <td class="tdPadding">
        <table class="data" >
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.by.language"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <c:forEach var="lang" items="${overview.languages}" varStatus="cursor">
              <tr <c:if test="${cursor.index % 2 == 1}">class="alt"</c:if>>
                <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${lang.key}"/>
                <td>${localizedLanguage}</td>
                <td>${lang.value}</td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </td>
      <td class="tdPadding">
        <table class="data"  cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.by.format"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key="index_verification_overview.table.by.format.text"/></td>
              <td>${overview.textDocs}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.by.format.html"/></td>
              <td>${overview.htmlDocs}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.by.format.rich"/></td>
              <td>${overview.otherDocs}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.by.format.pdf"/></td>
              <td>${overview.pdfDocs}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.by.format.xhtml"/></td>
              <td>${overview.xhtmlDocs}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.by.format.xml"/></td>
              <td>${overview.xmlDocs}</td>
            </tr>
          </tbody>
          </table>
        </td>
      </tr>
    </table>
    <table class="indexReviewTable" cellspacing="0" cellpadding="0">
      <tr>
        <td colspan="3">
          <h2><fmt:message key="index_verification_overview.index_details"/></h2>
        </td>
      </tr>
      <tr valign="top">
        <td class="tdPadding">
          <table class="data" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.index.content"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.measures.size.text"/></td>
              <td><fmt:formatNumber value="${overview.textSize / 1000000}" maxFractionDigits="1"/> <fmt:message key="index_verification_overview.table.mchar"/></td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.content.statement.count"/></td>
              <td>${overview.sentenceCount}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.content.word.count"/></td>
              <td>${overview.wordCount}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.content.unique.term.count"/></td>
              <td>${overview.uniqueTerms}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.content.index.term.count"/></td>
              <td>${overview.indexTerms}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.content.total.term.count"/></td>
              <td>${overview.terms}</td>
            </tr>
          </tbody>
          </table>
        </td>
        <td class="tdPadding">
          <table class="data"  cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.index.elements"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.elements.document.sets"/></td>
              <td>${overview.totalDocsets}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.elements.topics"/></td>
              <td>${overview.totalTopics}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.elements.meta.sets"/></td>
              <td>${overview.totalMetasets}</td>
            </tr>
            <tr  class="alt">
              <td><fmt:message key="index_verification_overview.table.index.elements.collections"/></td>
              <td>${overview.totalCollections}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.elements.properties"/></td>
              <td>${overview.totalProperties}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.elements.query.rules"/></td>
              <td>${overview.totalQueryRules}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.elements.refinement.configs"/></td>
              <td>${overview.totalRefinements}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.elements.ranking.configs"/></td>
              <td>${overview.totalRanks}</td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.elements.custom.terms"/></td>
              <td>${overview.custom}</td>
            </tr>
          </tbody>
          </table>
        </td>
        <td class="tdPadding">
          <table class="data"  cellspacing="0" cellpadding="0" >
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.index.measures"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.measures.partitions"/></td>
              <td>${overview.numPartitions}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.measures.actual"/></td>
              <td>${overview.totalMB} <fmt:message key="index_verification_overview.table.MB"/></td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.measures.storage"/></td>
              <td>${overview.storageMB} <fmt:message key="index_verification_overview.table.MB"/></td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.measures.size.content"/></td>
              <td><fmt:formatNumber value="${overview.docSize / (1024.0 * 1024.0)}" maxFractionDigits="1"/> <fmt:message key="index_verification_overview.table.MB"/></td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.measures.size.text"/></td>
              <td><fmt:formatNumber value="${overview.textSize / 1000000}" maxFractionDigits="1"/> <fmt:message key="index_verification_overview.table.mchar"/></td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.measures.content.ratio"/></td>
              <td><fmt:formatNumber value="${overview.docRatio}" maxFractionDigits="1"/></td>
            </tr>
            <tr>
              <td><fmt:message key="index_verification_overview.table.index.measures.text.ratio"/></td>
              <td><fmt:formatNumber value="${overview.textRatio}" maxFractionDigits="1"/></td>
            </tr>
            <tr class="alt">
              <td><fmt:message key="index_verification_overview.table.index.measures.reserved"/></td>
              <td>${overview.maxMB} <fmt:message key="index_verification_overview.table.MB"/></td>
            </tr>
          </tbody>
        </table>
      </td>
    </tr>
  </table>

  <common:syncTaskFindByPrimaryKey var="syncTask" syncTaskId="${syncTaskId}" />
  <c:set var="index" value="${0}" />
  <c:forEach items="${syncTask.contentSourceSetSelections}" var="content">
    <c:if test="${content.parentLogicalPartition.id == partitionId}">
      <admin-beans:getSuppressedFilesData var="suppressedData" contentId="${content.id}"
        varUnspecifiedKey="unspecifiedKey" varBlankKey="blankKey" />
      <c:if test="${not empty suppressedData}">
        <c:if test="${index == 0}">
          <table class="indexReviewTable" cellspacing="0" cellpadding="0">
            <tr>
              <td><h2><fmt:message key="index_verification_overview.suppressed"/></h2></td>
            </tr>
            <tr>
              <td width="33%">&nbsp;</td>
              <td width="33%">&nbsp;</td>
              <td width="33%">&nbsp;</td>
        </c:if>
        <c:if test="${index % 3 == 0}">
          </tr>
          <tr valign="top">
        </c:if>
        <td class="tdPadding">
          <h2><c:out value="${content.name}" /></h2>
          <table class="data" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="index_verification_overview.table.suppressed.extension"/></th>
              <th><fmt:message key="index_verification_overview.table.count"/></th>
            </tr>
          </thead>
          <tbody>
            <c:set var="even" value="false"/>
            <c:forEach var="suppressedType" items="${suppressedData}">
              <c:if test="${suppressedType.key != unspecifiedKey}">
                <c:forEach var="suppressedItem" items="${suppressedType.value}">
                  <tr <c:if test="${even}">class="alt"</c:if>>
                    <td>
                      <c:if test="${suppressedType.key == blankKey}">
                        <fmt:message key="index_verification_overview.table.suppressed.blank"/>
                      </c:if>
                      <c:if test="${suppressedType.key != blankKey}">
                        ${suppressedItem.key}
                      </c:if>
                    </td>
                    <td>${suppressedItem.value}</td>
                  </tr>
                  <c:set var="even" value="${!even}"/>
                </c:forEach>
              </c:if>
            </c:forEach>
            <c:if test="${not empty suppressedData[unspecifiedKey]}">
              <tr <c:if test="${even}">class="alt"</c:if>>
                <td>
                  <d:a href="${pageContext.request.contextPath}/searchadmin/view_unspecified_suppressed_items.jsp"
                       onclick="return openSeparateWindow(this.href, '_blank', 500, 400)">
                    <d:param name="contentId" value="${content.id}" />
                    <d:param name="contentName" value="${content.name}" />
                    <fmt:message key="index_verification_overview.table.suppressed.unspecified"/>
                  </d:a>
                </td>
                <c:set var="unspecifiedCount" value="${0}" />
                <c:forEach var="suppressedItem" items="${suppressedData[unspecifiedKey]}">
                  <c:set var="unspecifiedCount" value="${unspecifiedCount + suppressedItem.value}" />
                </c:forEach>
                <td>${unspecifiedCount}</td>
              </tr>
            </c:if>
          </tbody>
          </table>
        </td>
        <c:set var="index" value="${index + 1}" />
      </c:if>
    </c:if>
  </c:forEach>
  <c:if test="${index > 0}">
    </tr>
  </table>
  </c:if>
  <h2><strong><fmt:message key="index_verification_overview.partition.command.history"/></strong></h2>
  <c:forEach var="hist" items="${overview.history}">
    <c:out value="${hist}"/><br/>
  </c:forEach>
  <br/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview_content.jsp#1 $$Change: 651360 $--%>
  