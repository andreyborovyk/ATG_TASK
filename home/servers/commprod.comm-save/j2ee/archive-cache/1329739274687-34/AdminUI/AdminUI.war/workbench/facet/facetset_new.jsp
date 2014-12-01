<%--
  JSP provides creation of the facet set

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_new.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>


<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageFacetSetFormHandler"/>

  <%-- URL definitions --%>
  <c:url value="${facetPath}/facetset_new.jsp" var="errorURL"/>
  <c:url value="${facetPath}/facetset.jsp" var="successURL"/>

  <d:include src="${facetPath}/facetset_navigation.jsp" />
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${formActionUrl}" method="POST">
      <div id="paneContent">
        <div id="content1">
          <%-- General tab --%>
          <br/>
          <h3>
            <fmt:message key="facetset_basics.title"/>
          </h3>
          <table class="form" cellspacing="0" cellpadding="0">
            <tr>
              <td class="label">
                <span id="facetSetNameAlert">
                  <span class="required"><fmt:message key="facetset_new.required_field"/></span>
                </span>
                <label for="facetSetName">
                  <span id="facetSetNameAlert"></span><fmt:message key="facetset_basics.name"/>
                </label>
              </td>
              <td>
                <d:input type="text" iclass="textField" bean="ManageFacetSetFormHandler.facetSetName"
                         name="facetSetName" id="facetSetName"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="facetSetDescription">
                  <span id="facetSetDescriptionAlert"></span><fmt:message key="facetset_basics.description"/>
                </label>
              </td>
              <td>
                <d:input type="text" iclass="textField" bean="ManageFacetSetFormHandler.facetSetDescription"
                         name="facetSetDescription" id="facetSetDescription"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <label for="facetSetMappings">
                  <span id="facetSetMappingsAlert"></span><fmt:message key="facetset_basics.mappings"/>
                </label>
              </td>
              <td style="padding:0">
                <d:getvalueof bean="ManageFacetSetFormHandler.facetSetMappings" var="mappings"/>
                
                <%-- Bach-add mappings table --%>
                <table id="mappingFacet" cellspacing="0" cellpadding="0" width="100%">
                  <c:forEach items="${mappings}" var="mapping">
                    <tr>
                      <td>
                        <d:input bean="ManageFacetSetFormHandler.facetSetMappings" name="topicName" type="text" value="${mapping}"
                                 iclass="textField" onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
                      </td>
                    </tr>
                  </c:forEach>
                </table>
              </td>
            </tr>
          </table>
            <script type="text/javascript">
              initTable(document.getElementById("mappingFacet"));
            </script>
        </div>
        <div id="content2" style="display:none">
          <d:include src="facetsets_search_projects.jsp"/>
        </div>
      </div>

      <div id="paneFooter">
        <fmt:message var="createButton"              key='facetset_new.button.create'/>
        <fmt:message var="cancelButton"              key='facetset_new.button.cancel'/>
        <fmt:message var="createButtonTooltip"       key='facetset_new.button.tooltip.create'/>
        <fmt:message var="cancelButtonTooltip"       key='facetset_new.button.tooltip.cancel'/>

        <d:input type="hidden" bean="ManageFacetSetFormHandler.successURL" value="${successURL}"  name="successURL"/>
        <d:input type="hidden" bean="ManageFacetSetFormHandler.errorURL"   value="${errorURL}" name="errorURL"/>

        <d:input type="submit" bean="ManageFacetSetFormHandler.create" value="${createButton}" iclass="formsubmitter"
                 title="${createButtonTooltip}" name="create" onclick="return checkForm()"/>

        <%-- Cancel button --%>
        <d:input type="submit" bean="ManageFacetSetFormHandler.cancel" value="${cancelButton}" iclass="formsubmitter"
                 title="${cancelButtonTooltip}" />
      </div>
      <admin-validator:validate beanName="ManageFacetSetFormHandler"/>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_new.jsp#2 $$Change: 651448 $--%>
