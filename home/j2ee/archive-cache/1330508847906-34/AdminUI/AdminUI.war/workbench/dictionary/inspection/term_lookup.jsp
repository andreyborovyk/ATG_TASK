<%--
"Term Lookup" dictionary inspection tab

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/term_lookup.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermLookupFormHandler"/>
  <admin-ui:initializeFormHandler handler="${handler}" />
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject" var="activeSearchProject"/>
  <c:choose>
    <c:when test="${not empty activeSearchProject.activeProjectId}">
      <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
           executeScripts="true" cacheContent="false" scriptSeparation="false">
        <d:form action="term_lookup.jsp" method="post">
          <div id="paneContent">
            <br/>
            <d:include page="active_project_tpo.jsp">
              <d:param value="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTermLookupFormHandler" name="handlerName"/>
              <d:param value="embedded_assistant.term_lookup_tpo" name="hoverKey"/>
            </d:include>
            <div class="inputPanel">
              <table class="form" cellspacing="0" cellpadding="0">
                <tbody>
                <tr>
                  <td class="label centered">
                    <fmt:message key="dictionary_inspection.term_lookup.term" />
                  </td>
                  <td>
                    <d:input type="text" iclass="textField short unchanged" id="termInput" name="term"
                             bean="TermDictionaryInspectionTermLookupFormHandler.text" />
                    <fmt:message key="dictionary_inspection.term_lookup.lookup" var="lookupButtonLabel"/>
                    <d:input type="submit" bean="TermDictionaryInspectionTermLookupFormHandler.lookup"
                      value="${lookupButtonLabel}" id="lookupSubmit" name="lookup" iclass="formsubmitter"/>
                    <span class="ea"><tags:ea key="embedded_assistant.term_lookup_term"/></span>
                  </td>
                </tr>
                </tbody>
              </table>
            </div>
            <c:url var="successUrl" value="${dictionaryPath}/inspection/term_lookup.jsp"/>
            <d:input type="hidden" bean="TermDictionaryInspectionTermLookupFormHandler.successURL" value="${successUrl}" name="successURL"/>
            <d:input type="hidden" bean="TermDictionaryInspectionTermLookupFormHandler.needInitialization" value="false"/>
        
            <%-- inspect response section --%>
            <c:if test="${handler.searchResponse != null}">
              <table class="output">
                <tr class="title">
                  <td>
                    <fmt:message key="dictionary_inspection.term_lookup.results">
                      <fmt:param value="${fn:length(handler.searchResponse.lookups)}"/>
                      <fmt:param value="${fn:escapeXml(handler.text)}"/>
                    </fmt:message>
                  </td>
                </tr>
                <tr>
                  <td>
                    <a href="#" onclick="document.getElementById('rightPanelContent').scrollTop = document.getElementById('lookupsTable').offsetTop; return false;">
                      <fmt:message key="dictionary_inspection.term_lookup.matches">
                        <fmt:param value="${fn:length(handler.searchResponse.matches)}"/>
                      </fmt:message>
                    </a>
                  </td>
                </tr>
              </table>

              <c:forEach items="${handler.searchResponse.lookups}" var="lookup" varStatus="current">
                <div class="seperatorThin"></div>
                <table class="output">
                  <tr class="title">
                    <c:if test="${fn:length(handler.searchResponse.lookups) > 1}">
                      <td>
                        ${current.index + 1}.
                      </td>
                    </c:if>
                    <td><fmt:message key="dictionary_inspection.term_lookup.stem"/></td>
                    <td width="30">&nbsp;</td>
                    <td><fmt:message key="dictionary_inspection.term_lookup.language"/></td>
                  </tr>
                  <tr>
                    <c:if test="${fn:length(handler.searchResponse.lookups) > 1}">
                      <td rowspan="9"></td>
                    </c:if>
                    <td>
                      <c:set var="totalOccur" value="${0}"/>
                      <c:forEach items="${lookup.forms}" var="form" varStatus="formStatus">
                        <c:set var="totalOccur" value="${totalOccur + form.frequency}"/>
                      </c:forEach>
                      <fmt:message key="dictionary_inspection.term_lookup.occurrences">
                        <fmt:param value="${lookup.stem}"/>
                        <fmt:param value="${lookup.docfreq}"/>
                        <fmt:param value="${totalOccur}"/>
                      </fmt:message>
                    </td>
                    <td width="30">&nbsp;</td>
                    <td>
                      <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${handler.searchResponse.language}"/>
                      ${localizedLanguage}
                    </td>
                  </tr>

                  <tr class="title">
                    <td><fmt:message key="dictionary_inspection.term_lookup.pos"/></td>
                    <td width="30">&nbsp;</td>
                    <td><fmt:message key="dictionary_inspection.term_lookup.term_source"/></td>
                  </tr>
                  <tr>
                    <td><c:out value="${fn:replace(lookup.pos, ',', ', ')}" /></td>
                    <td width="30">&nbsp;</td>
                    <td><c:out value="${fn:replace(lookup.source, ',', ', ')}" /></td>
                    <td>
                      <c:if test="${lookup.source eq 'core'}">
                        <c:url var="exportUrl" value="${dictionaryPath}/inspection/term_export_popup.jsp">
                          <c:param name="termName" value="${lookup.stem}"/>
                          <c:param name="termPos" value="${lookup.pos}"/>
                          <c:param name="termLanguage" value="${handler.searchResponse.language}"/>
                        </c:url>
                        <fmt:message key="dictionary_inspection.term_lookup.export_term_icon.title" var="exportTitle"/>
                        <a class="icon termLookup_copyToDict" title="${exportTitle}" href="${exportUrl}"
                           onclick="return showPopUp(this.href);">exp</a>
                      </c:if>
                    </td>
                  </tr>
              
                  <tr class="title">
                    <td colspan="3"><fmt:message key="dictionary_inspection.term_lookup.other_forms"/></td>
                  </tr>
                  <tr>
                    <td colspan="3">
                      <c:choose>
                        <c:when test="${fn:length(lookup.forms) > 0}">
                          <c:forEach items="${lookup.forms}" var="form" varStatus="formStatus">
                            <fmt:message key="dictionary_inspection.term_lookup.form_occurrences">
                              <fmt:param value="${form.form}"/>
                              <fmt:param value="${form.frequency}"/>
                            </fmt:message>
                            <c:if test="${not formStatus.last}"><br/></c:if>
                          </c:forEach>
                        </c:when>
                        <c:otherwise>
                          <fmt:message key="dictionary_inspection.term_lookup.no_items"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>

                  <tr class="title">
                    <td colspan="3"><fmt:message key="dictionary_inspection.term_lookup.expansion_synonyms"/></td>
                  </tr>
                  <tr>
                    <td colspan="3">
                      <c:if test="${fn:length(lookup.expansions) == 0}">
                        <fmt:message key="dictionary_inspection.term_lookup.no_expansions"/>
                      </c:if>
                    </td>
                  </tr>
                </table>

                <c:if test="${fn:length(lookup.expansions) > 0}">
                  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table" var="expansion" 
                                  items="${lookup.expansions}">
                    <admin-ui:column title="dictionary_inspection.term_lookup.expansion_table.terms" name="term" type="text">
                      <a href="#" onclick="synonymSelected('${fn:escapeXml(expansion.stem)}','${expansion.language}');">
                        ${fn:escapeXml(expansion.stem)}
                      </a>
                    </admin-ui:column>
                    <admin-ui:column title="dictionary_inspection.term_lookup.expansion_table.pos" name="pos"
                      type="text">${expansion.type}</admin-ui:column>
                      <admin-ui:column title="dictionary_inspection.term_lookup.expansion_table.language" name="language" type="text">
                        <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${expansion.language}"/>
                          ${localizedLanguage}
                    </admin-ui:column>
                    <admin-ui:column title="dictionary_inspection.term_lookup.expansion_table.adaptor" name="source"
                      type="text">${expansion.adaptor}</admin-ui:column>
                  </admin-ui:table>
                </c:if>

              </c:forEach>

              <div class="seperatorThin"></div>
              <table class="output" id="lookupsTable">
                <c:if test="${empty handler.searchResponse.lookups}">
                  <tr>
                    <td>&nbsp;</td>
                  </tr>
                  <tr>
                    <td><fmt:message key="dictionary_inspection.term_lookup.no_terms"/></td>
                  </tr>
                  <c:if test="${not empty handler.searchResponse.suggestion}">
                    <tr>
                      <td>
                        <fmt:message key="dictionary_inspection.term_lookup.did_you_mean">
                          <fmt:param>
                            <a href="#" onclick="return matchSelected(this)">${fn:escapeXml(handler.searchResponse.suggestion)}</a>
                          </fmt:param>
                        </fmt:message>
                      </td>
                    </tr>
                  </c:if>
                </c:if>
                <tr class="title">
                  <td><fmt:message key="dictionary_inspection.term_lookup.partial_matches"/></td>
                </tr>
                <c:choose>
                  <c:when test="${fn:length(handler.searchResponse.matches) > 0}">
                    <c:forEach items="${handler.searchResponse.matches}" var="match">
                      <tr>
                        <td><a href="#" onclick="return matchSelected(this)">${fn:escapeXml(match.stem)}</a></td>
                      </tr>
                    </c:forEach>
                  </c:when>
                  <c:otherwise>
                    <tr>
                      <td><fmt:message key="dictionary_inspection.term_lookup.no_items"/></td>
                    </tr>
                  </c:otherwise>
                </c:choose>
              </table>
              <br/>
            </c:if>
          </div>
        </d:form>
      </div>
      <script type="text/javascript"> 
        //refreshes right pane after changes in selection of active search project
        function refreshRightPane(obj) {
          loadRightPanel('${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp');
        }
        function matchSelected(term){
          document.getElementById('termInput').value = term.innerHTML;
          document.getElementById('lookupSubmit').click();
          return false;
        }
        function synonymSelected(term, language){
          document.getElementById('termInput').value = term;
          document.getElementById('languageInput').value = language;
          document.getElementById('lookupSubmit').click();
        }
        function focusFormElement() {
          document.getElementById("termInput").focus();
        }
        document.getElementById("termInput").onkeydown = function(e) {
          e = e || window.event;
          if (e.keyCode == 13) {
            document.getElementById('lookupSubmit').click(); 
            return false;
        }
        };
        disableButtonWhenFieldIsEmpty("lookupSubmit", "termInput");

        //dojo tree refresh
        top.hierarchy = [{id:"rootDictInspectNode"}];
        top.syncTree();
      </script>
    </c:when>
    <c:otherwise>
      <script>loadingStatus.setRedirectUrl("${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp");</script>
    </c:otherwise>
  </c:choose>

  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/term_lookup.jsp#1 $$Change: 651360 $--%>
