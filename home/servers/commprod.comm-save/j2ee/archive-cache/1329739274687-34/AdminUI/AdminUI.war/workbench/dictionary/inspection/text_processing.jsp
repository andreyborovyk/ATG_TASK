<%--
"Term Lookup" dictionary inspection tab

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/text_processing.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTextProcessingFormHandler"/>
  <admin-ui:initializeFormHandler handler="${handler}" />
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/scrolling.js"></script>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent" class="scrollingAreaContent">
      <br/>
      <d:form action="text_processing.jsp" method="post">
        <d:input type="hidden" bean="TermDictionaryInspectionTextProcessingFormHandler.successURL" value="${dictionaryPath}/inspection/text_processing.jsp" name="successURL"/>

        <d:include page="active_project_tpo.jsp">
          <d:param value="/atg/searchadmin/workbenchui/formhandlers/TermDictionaryInspectionTextProcessingFormHandler" name="handlerName"/>
          <d:param value="embedded_assistant.text_processing_tpo" name="hoverKey"/>
        </d:include>
        <div class="inputPanel" id="inputPanel">
          <table class="form" cellspacing="0" cellpadding="0">
            <tbody>
              <tr>
                <td class="label">
                  <fmt:message key="dictionary_inspection.text_processing.text"/>
                </td>
                <td>
                  <d:textarea iclass="textAreaField short unchanged" id="textInput" name="text" rows="3"
                              bean="TermDictionaryInspectionTextProcessingFormHandler.text" />
                  <fmt:message key="dictionary_inspection.text_processing.process" var="processButtonLabel"/>
                  <d:input type="submit" bean="TermDictionaryInspectionTextProcessingFormHandler.textProcessing"
                    value="${processButtonLabel}" id="textProcessingSubmit" name="textProcessing" iclass="formsubmitter"/>
                  <span class="ea"><tags:ea key="embedded_assistant.text_processing_text"/></span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </d:form>
      <%-- inspect response section --%>
      <c:if test="${handler.searchResponse != null}">
        <h3><fmt:message key="dictionary_inspection.text_processing.results"/></h3>

        <table class="scrollableContainerTable" cellspacing="0" cellpadding="0">
          <tr><td id="titleColumnCell">
            <div class="scrollableTableContainer" id="scrollableTableContainer">
              <div class="scrollableTableAnywidth" id="scrollableTableAnywidth">
                <table class="data" cellspacing="0" cellpadding="0" id="scrollableTable">
                  <tr>
                    <th><fmt:message key="dictionary_inspection.text_processing.text_title"/></th>
                    <c:forEach items="${handler.searchResponse.words}" var="word">
                      <td>${fn:escapeXml(word.form)}</td>
                    </c:forEach>
                  </tr>
                  <tr>
                    <th><fmt:message key="dictionary_inspection.text_processing.stem"/></th>
                    <c:forEach items="${handler.searchResponse.words}" var="word">
                      <td>
                        <c:choose>
                          <c:when test="${empty word.stem or word.stem == 'n/a'}">
                            <fmt:message key="dictionary_inspection.text_processing.na"/>
                          </c:when>
                          <c:otherwise>
                            ${fn:escapeXml(word.stem)}
                          </c:otherwise>
                        </c:choose>
                      </td>
                    </c:forEach>
                  </tr>
                  <tr class="alt">
                    <th><fmt:message key="dictionary_inspection.text_processing.source"/></th>
                    <c:forEach items="${handler.searchResponse.words}" var="word">
                      <td>
                        <c:choose>
                          <c:when test="${empty word.source or word.source == 'n/a'}">
                            <fmt:message key="dictionary_inspection.text_processing.na"/>
                          </c:when>
                          <c:when test="${word.source == '*new*'}">
                            <fmt:message key="dictionary_inspection.text_processing.source.new"/>
                          </c:when>
                          <c:otherwise>
                            ${fn:escapeXml(word.source)}
                          </c:otherwise>
                        </c:choose>
                      </td>
                    </c:forEach>
                  </tr>
                </table>
              </div>
            </div>
          </td>
          <td id="contentCell">
            <div class="scrollingArea" id="scrollingArea">
              <div class="scrollingAreaWindow" id="scrollingAreaWindow">
                <div class="scrollingAreaContent" id="scrollingAreaContent"></div></div></div></td></tr>
        </table>
      </c:if>
    </div>
  </div>
  <script type="text/javascript"> 
    //refreshes right pane after changes in selection of active search project
    function refreshRightPane(obj) {
      loadRightPanel('${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp');
    }
    function customLoad() {
      tableScrolling.init();
    }
    function focusFormElement() {
      document.getElementById("textInput").focus();
    }
    disableButtonWhenFieldIsEmpty("textProcessingSubmit", "textInput");
    
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictInspectNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/inspection/text_processing.jsp#2 $$Change: 651448 $--%>
