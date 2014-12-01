<%--
Displays pop-up window with info about an item from Content Inspection results table.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_tabs.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="documentUrl" var="documentUrl"/>
  <c:set var="searchEnvironmentName"><tags:i18GetParam paramName="searchEnvironmentName"/></c:set>
  <tags:separateWindow titleName="${documentUrl}">
    <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/common.js"></script>

    <admin-beans:getContentItem varResponse="response" varSortedMap="sortedProperties" varFormattedHtml="responseHtml"
        searchEnvironmentName="${searchEnvironmentName}" documentUrl="${documentUrl}" />
    <c:set var="isStructured" value="${response.itemInspect.type eq 'Structured'}" />
    <c:set var="docFormat" value="${fn:toLowerCase(response.itemInspect.format)}"/>
    <div id="subNav">
      <ul>
        <li class="current">
          <a href="#" onclick="return switchContent(1,'content');">
            <fmt:message key="content_inspection_tabs.tab.basic"/>
          </a>
        </li>
        <li>
          <a href="#" onclick="return switchContent(2,'content');">
            <fmt:message key="content_inspection_tabs.tab.metadata"/>
          </a>
        </li>
        <li>
          <a href="#" onclick="return switchContent(3,'content');">
            <fmt:message key="content_inspection_tabs.tab.text"/>
          </a>
        </li>
        <c:if test="${docFormat eq 'pdf'}">
          <c:set var="validPdfUrl" value="${fn:startsWith(documentUrl, 'http://')}" />
          <c:if test="${not validPdfUrl}">
            <c:set var="docFormat" value="notValidUrl"/>
          </c:if>
          <c:if test="${validPdfUrl}">
            <li>
              <c:url var="retrieverUrl" value="/documentRetriever" >
                <c:param name="docUrl" value="${documentUrl}"/>
              </c:url>
              <a href="#" onclick="setContent('${retrieverUrl}')">
                <fmt:message key="content_inspection_tabs.tab.preview"/>
              </a>
            </li>
          </c:if>
        </c:if>
        <c:if test="${docFormat ne 'pdf'}">
          <li>
            <a href="#" onclick="return switchContent(4,'content');">
              <fmt:message key="content_inspection_tabs.tab.preview"/>
            </a>
          </li>
        </c:if>
        <c:if test="${isStructured}" >
          <li>
            <a href="#" onclick="return switchContent(5,'content');">
              <fmt:message key="content_inspection_tabs.tab.structured"/>
            </a>
          </li>
        </c:if>
      </ul>
    </div>

    <div id="popupContent">

    <c:if test="${not empty response}">
      <c:set value="${response.itemInspect}" var="document"/>
      <div id="content1" class="content_tab">
        <strong><fmt:message key='content_inspection_tabs.tab.basic'/></strong>
        <table class="data" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th width="20%"><fmt:message key='content_inspection_tabs.tab.basic.property'/></th>
              <th width="80%"><fmt:message key='content_inspection_tabs.tab.basic.value'/></th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td><fmt:message key='content_inspection_tabs.tab.basic.id'/></td>
              <td>${document.id}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key='content_inspection_tabs.tab.basic.url'/></td>
              <td>${document.url}</td>
            </tr>
            <tr>
              <td><fmt:message key='content_inspection_tabs.tab.basic.title'/></td>
              <td>${document.title}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key='content_inspection_tabs.tab.basic.summary'/></td>
              <td>${document.summary}</td>
            </tr>
            <tr>
              <td><fmt:message key='content_inspection_tabs.tab.basic.type'/></td>
              <td>${document.type}</td>
            </tr>
            <tr class="alt">
              <td><fmt:message key='content_inspection_tabs.tab.basic.format'/></td>
              <td>${document.format}</td>
            </tr>
            <tr>
              <td><fmt:message key='content_inspection_tabs.tab.basic.language'/></td>
              <td>
                <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${document.language}"/>
                <c:out value="${localizedLanguage}"/>
              </td>
            </tr>
            <tr class="alt">
              <td><fmt:message key='content_inspection_tabs.tab.basic.phys_directory'/></td>
              <td>${document.docset}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div id="content2" class="content_tab" style="display: none">
        <c:if test="${not empty document.properties}">
          <strong><fmt:message key='content_inspection_tabs.tab.metadata.meta_prop'/></strong>
          <table class="data" cellspacing="0" cellpadding="0">
            <thead>
              <tr>
                <th><fmt:message key='content_inspection_tabs.tab.metadata.meta_sets.table.type'/></th>
                <th><fmt:message key='content_inspection_tabs.tab.metadata.meta_sets.table.property'/></th>
                <th><fmt:message key='content_inspection_tabs.tab.metadata.meta_sets.table.value'/></th>
              </tr>
            </thead>
            <tbody>
              <c:set var="metaPropertyIndex" value="${0}" />
              <c:forEach items="${sortedProperties}" var="resultMap">
                <c:forEach items="${resultMap.value}" var="property">
                  <tr class="${metaPropertyIndex % 2 == 0 ? '' : 'alt'}">
                    <td>${property.type}</td>
                    <td>${property.name}</td>
                    <td>
                      <c:if test="${property.type ne 'date'}">
                        ${property.value}
                      </c:if>
                      <c:if test="${property.type eq 'date'}">
                        <jsp:useBean id="dateValue" class="java.util.Date" scope="request" />
                        <jsp:setProperty property="time" name="dateValue" value="${property.value * 1000}"/>
                        <fmt:message var="timeFormat" key="timeFormat"/>
                        <fmt:formatDate value="${dateValue}" pattern="${timeFormat}"/>
                      </c:if>
                    </td>
                  </tr>
                  <c:set var="metaPropertyIndex" value="${metaPropertyIndex + 1}" />
                </c:forEach>
              </c:forEach>
            </tbody>
          </table>
        </c:if>

        <c:if test="${not empty document.metaSets}">
          <p><fieldset>
            <legend><fmt:message key='content_inspection_tabs.tab.metadata.meta_sets'/></legend>
            <div class="fieldset_content">
            <c:forEach items="${document.metaSets}" var="metaSet">
              ${metaSet}<br/>
            </c:forEach>
            </div>
          </fieldset></p>
        </c:if>

        <c:if test="${not empty document.metaIdxs}">
          <p><fieldset>
            <legend><fmt:message key='content_inspection_tabs.tab.metadata.meta_index'/></legend>
            <div class="fieldset_content">
            <c:forEach items="${document.metaIdxs}" var="metaIdx">
              ${metaIdx}<br/>
            </c:forEach>
            </div>
          </fieldset></p>
        </c:if>

        <c:if test="${not empty document.topicSets}">
          <p><fieldset>
            <legend><fmt:message key='content_inspection_tabs.tab.metadata.topic_sets'/></legend>
            <div class="fieldset_content">
            <c:forEach items="${document.topicSets}" var="topicSet">
              ${topicSet}<br/>
            </c:forEach>
            </div>
          </fieldset></p>
        </c:if>
      </div>
      <div id="content3" class="content_tab" style="display: none">
        <c:if test="${not empty document.sentences}">
          <admin-beans:getContentSentences documentSentences="${document.sentences}" varItems="mapItems"/>
          <table class="data" cellspacing="0" cellpadding="0">
            <thead>
              <th style="width:30%"><fmt:message key='content_inspection_tabs.tab.text.table.property_security'/></th>
              <th><fmt:message key='content_inspection_tabs.tab.text.table.text'/></th>
            </thead>
            <tbody>
              <c:forEach items="${mapItems}" var="sentence" varStatus="factorStatus">
                <c:forEach items="${sentence.value}" var="sentenceText" varStatus="status">
                  <tr class="${factorStatus.index % 2 == 0 ? '' : 'alt'}">
                    <c:if test="${status.first}">
                      <td rowspan="${fn:length(sentence.value)}">
                        <c:forEach items="${sentence.key}" var="feature">
                          <c:if test="${not fn:endsWith(feature, ':everyone')}">
                            ${feature}<br/>
                          </c:if>
                        </c:forEach>
                      </td>
                    </c:if>
                    <td>
                      <c:out value="${sentenceText}"/>
                    </td>
                  </tr>
                </c:forEach>
              </c:forEach>
            </tbody>
          </table>
        </c:if>
      </div>

      <%-- content 4 --%>
      <c:choose>
        <c:when test="${docFormat eq 'notValidUrl'}">
          <%-- pdf document without external URL --%>
          <div id="content4" class="content_tab" >
            <div>
              <p><fmt:message key="project_content_inspection_preview.error.message"/></p>
            </div>
          </div>
        </c:when>
        <c:when test="${docFormat eq 'pdf'}">
          <div id="content4">
            <iframe src="" frameborder="0" scrolling="no" width="100%" height="100%"
                    id="docFrame" name="docFrame"></iframe>
          </div>
        </c:when>
        <c:otherwise>
          <div id="content4" style="display: none">
            <c:url var="structuredUrl" value="/searchadmin/project_content_inspection_preview.jsp">
              <c:param name="env" value="${searchEnvironmentName}" />
              <c:param name="documentUrl" value="${documentUrl}" />
            </c:url>
            <iframe src="${structuredUrl}" frameborder="0" scrolling="yes" width="100%" height="100"
                    id="docFrame" name="docFrame"></iframe></div>
        </c:otherwise>
      </c:choose>

      <c:if test="${isStructured}">
        <div id="content5" class="content_tab" style="display: none"> 
          <fieldset>
            <legend><fmt:message key='content_inspection_tabs.tab.legend.structured'/></legend>
            <div class="content_tab_html">
              ${responseHtml}
            </div>
          </fieldset>
        </div>
      </c:if>

    </c:if>

    </div>

    <script>
      function setContent(documentUrl) {
        if (this.innerHeight) {
          //FF version - need to refresh each time
          document.getElementById('docFrame').setAttribute('src',documentUrl);
        } else {
          //IE version - refresh first time only
          if (document.getElementById('docFrame').getAttribute('src') == "") {
            document.getElementById('docFrame').setAttribute('src',documentUrl);
          }
        }
        return switchContent(4,'content');
      }
      function popupResize() {
        var height = window.innerHeight;//Firefox
        if (document.body.clientHeight) {
          height = document.body.clientHeight;//IE
        }
        var popupContent = document.getElementById("popupContent");
        if (popupContent.offsetLeft > 0) {
          popupContent.style.cssFloat = "left";
        }
        popupContent.style.height = (height - popupContent.offsetTop) + "px";
        if (document.getElementById("docFrame") != null) {
          document.getElementById("docFrame").style.height = popupContent.style.height;
        }
      }
      window.onresize = popupResize;
      popupResize();
      switchContent(1,'content');
    </script>
  </tags:separateWindow>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_content_inspection_tabs.jsp#1 $$Change: 651360 $--%>
