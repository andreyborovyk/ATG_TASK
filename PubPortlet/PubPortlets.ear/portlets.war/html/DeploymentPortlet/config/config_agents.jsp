<!-- BEGIN FILE config_agents.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>

  <fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>

  <portlet:defineObjects/>

  <script language="Javascript" type="text/javascript">
    <!--
      function submitDeleteAgentsForm()
      {
        for(j=0;j<document.forms['editAgentsForm'].elements.length;j++) {
          var formObj=document.forms['editAgentsForm'].elements[j];
          if(formObj.name == "edit_agent_ids") {
            var deleteAgentCheckboxId ="deleteAgentIDs-"+formObj.value ;
            if(formObj.checked) 
              document.getElementById(deleteAgentCheckboxId).checked = true;
            else
              document.getElementById(deleteAgentCheckboxId).checked = false;
          }
        }
        if(document.getElementById('deleteAgentsForm')) {
         document.forms['deleteAgentsForm'].submit(); 
        }
      }

      function checkBoxChecked() 
      {
        for(j=0;j<document.forms['editAgentsForm'].elements.length;j++){
          var formObj=document.forms['editAgentsForm'].elements[j];
          if( formObj.name == "edit_agent_ids" ) {
            if( formObj.checked ) 
              return true;
          }
        }
        return false;
      }
    -->
  </script>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
        <td class="blankSpace">&nbsp;</td>
        <td>
          <h2>
            <fmt:message key="viewing-agents-for-site" bundle="${depBundle}"/>
              : <c:out value="${target.name}"/>
          </h2>
        </td>
        <td width="100%" class="error rightAlign">
          <dspel:include page="../includes/formErrors.jsp"/>
        </td>  
        <portlet:renderURL var="agentAddURL"> 
          <portlet:param name="atg_admin_menu_group" value="deployment"/>  
          <portlet:param name="atg_admin_menu_1" value="configuration"/>  
          <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("TARGET_DEF_ID")+""%>'/>  
          <portlet:param name="config_tab_name" value="agents"/>                      
          <portlet:param name="add_or_edit_agent" value='add'/>                      
          <portlet:param name="goto_config_details_tabs" value="true"/>  
          <c:if test="${addSite}">
            <portlet:param name="add_site" value="true"/>  
          </c:if>
        </portlet:renderURL> 
        <portlet:renderURL var="thisPageURL"> 
          <portlet:param name="atg_admin_menu_group" value="deployment"/>  
          <portlet:param name="atg_admin_menu_1" value="configuration"/>  
          <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("TARGET_DEF_ID")+""%>'/>  
          <portlet:param name="config_tab_name" value="agents"/>                      
          <portlet:param name="add_or_edit_agent" value='edit'/>                      
          <portlet:param name="goto_config_details_tabs" value="true"/>  
          <c:if test="${addSite}">
            <portlet:param name="add_site" value="true"/>  
          </c:if>
        </portlet:renderURL> 
        <td class="toolBar_button">
          <c:if test="${!targetDef.primary}">
            <a href="<c:out value='${agentAddURL}'/>" class="add" onmouseover="status='';return true;">
              <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" 
                alt="Add Agent to Site" width="16" height="16" />
              &nbsp;
              <fmt:message key="add-agent-to-site" bundle="${depBundle}"/>
            </a>
          </c:if>
        </td>
      </tr>
    </table>
  </div> <!-- contentActions -->
   
  <div id="adminDeployment">
    <form name="editAgentsform" id="editAgentsForm" action="<c:out value='${thisPageURL}'/>" method="post">
      <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
        <tr>
          <th class="centerAligned" style="width: 4%;">
            <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" 
              alt='<fmt:message key="select-multiple-column" bundle="${depBundle}"/>' width="7" height="8" border="0" />
          </th>
          <th class="leftAligned">
            <span class="tableHeader">
              <fmt:message key="agent-name" bundle="${depBundle}"/>
            </span>
          </th>
          <th class="leftAligned">
            <span class="tableHeader">
              <fmt:message key="transport-url" bundle="${depBundle}"/>
            </span>
          </th>
          <th class="centerAligned">
            <span class="tableHeader">
              <fmt:message key="essential" bundle="${depBundle}"/>
            </span>
          </th>
        </tr>

        <c:if test="${empty targetDef.agents}">  
          <tr> 
            <td colspan="5" class="leftAligned">
              <span class="adminDeployment NoData">
                <fmt:message key="no-agents" bundle="${depBundle}"/>
              </span>
            </td>
          </tr>
        </c:if>

        <c:forEach var="agent" items="${targetDef.agents}" varStatus="loopStatus"> 
          <c:set var="agent_id" value="${agent.ID}"/>
          <c:choose>
            <c:when test="${loopStatus.count % 2 == 0}">
              <c:set var="trclass" value="alternateRowHighlight"/>
            </c:when>
            <c:otherwise>
              <c:set var="trclass" value=""/>
            </c:otherwise>
          </c:choose>

          <tr class='<c:out value="${trclass}"/>' >

            <td class="centerAligned selectLine" valign="middle">
              <c:choose>
                <c:when test="${targetDef.primary}">
                  <input type="checkbox" disabled="true" name="edit_agent_ids" 
                    id="editCheckbox<c:out value='${loopStatus.count}'/>" 
                    value="<c:out value='${agent.ID}'/>" rowspan="3" class="checkbox" />
                </c:when>
                <c:otherwise>
                  <input type="checkbox" id="editCheckbox<c:out value='${loopStatus.count}'/>"
                    name="edit_agent_ids" value="<c:out value='${agent.ID}'/>" class="checkbox" />
                </c:otherwise>
              </c:choose>
            </td>  

            <td class="leftAligned wrapNoBorder" valign="top">
              <span class="tableInfo">
                <portlet:renderURL var="agentEditURL"> 
                  <portlet:param name="atg_admin_menu_group" value="deployment"/>  
                  <portlet:param name="atg_admin_menu_1" value="configuration"/>  
                  <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("TARGET_DEF_ID")+""%>'/>  
                  <portlet:param name="edit_agent_ids" value='<%=pageContext.findAttribute("agent_id")+""%>'/>  
                  <portlet:param name="config_tab_name" value="agents"/>                      
                  <portlet:param name="add_or_edit_agent" value='edit'/>                      
                  <portlet:param name="goto_config_details_tabs" value="true"/>  
                </portlet:renderURL> 

                <a href='<c:out value="${agentEditURL}"/>' onmouseover="status='';return true;">
                  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" 
                    style="margin-right: 6px; vertical-align: middle;" width="16" height="16" border="0" />
                  <c:out value="${agent.displayName}"/>
                </a>
              </span>
            </td>

            <td class="leftAligned">
              <span class="tableInfo">
                <c:if test="${agent.transport.transportType eq 'RMI'}">
                  <c:out value="${agent.transport.rmiURI}"/>
                </c:if>
                <c:if test="${agent.transport.transportType eq 'JNDI'}">
                  <c:out value="${agent.transport.jndiName}"/>
                </c:if>
              </span>
            </td>

            <td class="centerAligned">
              <span class="tableInfo">
                <c:set var="isEssential" value="false"/>
                <c:if test="${!empty agent.principalAssets}">
                  <c:forEach var="princAsset" items="${agent.principalAssets}">
                    <c:if test="${princAsset != 'NONE'}">
                      <c:set var="isEssential" value="true"/>
                    </c:if>
                  </c:forEach>
                </c:if>
                <c:if test="${isEssential}">
                  <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/checkMark1.gif'/>" 
                       alt='<fmt:message key="select-multiple-column" bundle="${depBundle}"/>' width="7" height="8" border="0" />
                </c:if>
              </span>
            </td>
            
          </tr>

          <c:if test="${!agent.includeAssetDestinations['empty'] || !agent.excludeAssetDestinations['empty']}">

            <tr class='<c:out value="${trclass}"/>'>

              <td class="centerAligned" colspan="2">
              </td> 
              
              <td class="leftAligned" colspan="2">
                <table cellpadding="0" cellspacing="0" border="0" class="dataTableNoGrid">
                  <tr>
                    <c:if test="${!agent.includeAssetDestinations['empty']}">
                      <td class="leftAligned wrapNoBorder" width="15%" valign="top">
                        <p>
                          <b>
                            <fmt:message key="destinations" bundle="${depBundle}"/>:
                          </b>
                        </p>
                      </td>
                      <td class="leftAligned wrapNoBorder" valign="top">
                        <span class="tableInfo">
                          <c:forEach var="asset" items="${agent.includeAssetDestinations}" varStatus="st">
                            <c:out value="${asset}"/>
                            <br/>
                          </c:forEach>
                        </span>
                      </td>   
                    </c:if>
                  </tr>
                </table>
              </td>
            </tr>
          </c:if>
        </c:forEach>
      </table>
    </form>
  </div> <!-- adminDeployment -->

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td width="100%" class="blankSpace">
          &nbsp;
        </td>

        <c:if test="${!targetDef.primary}">
          <td class="buttonImage">
            <a href="javascript:if(checkBoxChecked()){showIframe('deleteSelectedAgentsAction');}" 
              class="mainContentButton delete" onmouseover="status='';return true;">
              <fmt:message key="delete-selected-agents" bundle="${depBundle}"/>
            </a>
          </td>
          <td class="buttonImage">
            <a href="javascript:if(checkBoxChecked()){submitForm('editAgentsForm');}" 
              class="mainContentButton go" onmouseover="status='';return true;">
              <fmt:message key="edit-selected-agents" bundle="${depBundle}"/>
            </a>
          </td>  
        </c:if>

        <td width="100%" class="blankSpace">
          &nbsp;
        </td>
      </tr>
    </table>
  </div> <!-- contentActions -->

  <!-- end content -->

  <dspel:form method="post" action="${thisPageURL}" name="deleteAgentsForm" id="deleteAgentsForm">
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${thisPageURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${thisPageURL}"/>
    <c:forEach var="agent" items="${targetDef.agents}" varStatus="loopStatus"> 
      <dspel:input style="visibility: hidden" type="checkbox" 
        bean="${topologyFormHandlerName}.deleteAgentIDs" value="${agent.ID}" 
        id="deleteAgentIDs-${agent.ID}"/>
    </c:forEach>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.deleteMultipleAgents" 
      priority="-10" value="foo"/>
  </dspel:form>

  <div id="deleteSelectedAgentsAction" class="confirmAction">
    <dspel:iframe page="./iframes/action_delete_selected_agents.jsp" scrolling="no"/>
  </div>

</dspel:page>

<!-- END FILE config_agents.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_agents.jsp#2 $$Change: 651448 $--%>
