<%--
JSP, used to show default SAO options from Search Analysis Options set.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_set_details.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="saoSetId" param="saoSetId"/>
  <d:getvalueof var="searchAnalysisOptions" bean="/atg/searchadmin/repository/service/SearchAnalysisOptionsService"/>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.saoOptions" var="options"/>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectDefaultSAOSet" var="activeProjectDefaultSAOSet"/>
  <c:url value="${dictionaryPath}/sao/sao_sets_browse.jsp" var="backURL"/>

  <%--Set TPO bundle--%>
  <admin-beans:setTPOResourceBundle var="bundle" useSAO="true" />

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <d:form action="${backURL}" method="POST">
        <p></p>
        <table class="form" cellpadding="0" cellspacing="2">
          <tbody>
          <%-- SAO set general info --%>
          <tr class="underlined">
            <td class="label">
              <nobr><b><fmt:message key="sao_set.edit.name"/></b></nobr>
            </td>
            <td>
              <b><c:out value="${activeProjectDefaultSAOSet.name}" /></b>
              <span class="ea"><tags:ea key="embedded_assistant.sao_set_details" /></span>
            </td>
          </tr>
          <%-- SAO option fields --%>
          <c:set var="isSearchParams" value="true"/>
          <c:forEach items="${options}" var="optionDef" varStatus="cursor">
            <c:if test="${optionDef.category==searchAnalysisOptions.searchParamsCategoryName && isSearchParams}">
              <tr class="underlined">
                <td class="label">
                  <h3><fmt:message key="sao_set.search_parameters.label"/></h3>
                </td>
                <td>&nbsp;</td>
              </tr>
              <c:set var="isSearchParams" value="false"/>
            </c:if>
            <tr <c:if test="${not cursor.last}">class="underlined"</c:if>>
              <td class="label">
                <nobr><fmt:message key="${optionDef.name}.label" bundle="${bundle}" /></nobr>
              </td>
              <td>
                <c:set var="optionValues" value="${activeProjectDefaultSAOSet.options[optionDef.name].values}"/>
                <c:set var="eaSet" value="false"/>
                <c:choose>
                  <c:when test="${empty optionValues}">
                    <fmt:message key="sao_set.details.not_available"/>
                  </c:when>
                  <c:when test="${optionDef.display eq 'combinedropdown'}">
                    <c:out value="${fn:join(optionValues, '; ')}" />
                  </c:when>
                  <c:when test="${fn:length(optionValues) > 1}">
                    <ul>
                    <c:forEach items="${optionValues}" var="optionValue" varStatus="status">
                      <li><c:out value="${optionValue}" />
                        <c:if test="${status.first}">
                          <span class="ea"><tags:ea key="embedded_assistant.sao.${optionDef.name}"/></span>
                          <c:set var="eaSet" value="false"/>
                        </c:if>
                      </li>
                    </c:forEach>
                    </ul>
                  </c:when>
                  <c:when test="${empty optionValues[0]}">
                    <fmt:message key="sao_set.details.not_available"/>
                  </c:when>
                  <c:when test="${(optionDef.name == searchAnalysisOptions.languageOptionName) ||
                      (optionDef.name == searchAnalysisOptions.targetLanguageOptionName)}">
                    <fmt:message key="${optionDef.name}.values.${optionValues[0]}" bundle="${bundle}"/>
                  </c:when>
                  <c:otherwise>
                    <c:out value="${optionValues[0]}"/>
                  </c:otherwise>
                </c:choose>
                <c:if test="${not eaSet}">
                  <span class="ea"><tags:ea key="embedded_assistant.sao.${optionDef.name}"/></span>
                </c:if>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </d:form>
    </div>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictInspectNode"}, {id:"saoSetsNode"},
                     {id:"${saoSetId}", treeNodeType:"saoDefault"}];
    top.syncTree();
    function refreshRightPane(obj) {
      loadRightPanel('${backURL}');
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/sao/sao_set_details.jsp#2 $$Change: 651448 $--%>
