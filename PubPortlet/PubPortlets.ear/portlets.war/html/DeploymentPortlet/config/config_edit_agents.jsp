<!-- BEGIN FILE config_edit_agents.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<style>
/* Merch UI addition */
.multiButton {
	margin-top:5px;
	margin-bottom: 5px;
}

.button {
  display:inline-block !important;
  margin:0 !important;
  padding:3px 8px 3px 8px !important;
  width:auto !important;
  background:#fff url(http://sika:8840/atg/images/bg_button.gif) repeat-x left bottom !important;
  border:1px solid !important;
  border-color:#C2C6CA #A1A4A7 #A1A4A7 #C2C6CA !important;
  font:small-caption !important;
  text-decoration:none !important;
  text-align:center !important;
  white-space:nowrap;
  }
.buttonSmall {
  display:inline-block !important;
  margin:0 !important;
  padding:2px 5px 2px 5px !important;
  background:#fff url(../images/bg_button.gif) repeat-x left bottom !important;
  border:1px solid !important;
  border-color:#C2C6CA #A1A4A7 #A1A4A7 #C2C6CA !important;
  font:small-caption !important;
  font-weight:normal !important;
  text-decoration:none !important;
  text-align:center !important;
  white-space:nowrap;
  }
.buttonSmall:link,
.button:link,
.buttonSmall:visited, 
.button:visited {
  display:inline !important;
  color:#000 !important;
  }
.buttonSmall:hover,
.button:hover,
.buttonSmall:active,
.button:active {
  display:inline !important;
  background:#fff url(../images/bg_buttonOn.gif) repeat-x left bottom !important;
  }
</style>     

<dspel:page>

  <fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>

  <portlet:defineObjects/>

  <script language="javascript" src="<c:url value='/html/scripts/listbox.js'/>" type="text/javascript">
  </script>

  <dspel:importbean var="versionManager" bean="/atg/epub/version/VersionManagerService"/>
  <dspel:importbean var="deploymentServer" bean="/atg/epub/DeploymentServer"/>

  <c:if test="${!topologyEditFormHandler.formError}">
    <dspel:setvalue bean="${topologyFormHandlerName}.editAgentIDs" value="${editAgentIDs}"/>
    <dspel:setvalue bean="${topologyFormHandlerName}.populateBeanWithAgentData" value="foo"/>
  </c:if>

<script language="javascript" type="text/javascript">
    // An associative array of all available destinations.
    var availableDestinations = new Array();
 
    

    <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
      <%-- repositories --%>
      availableDestinations[<c:out value="${editAgentID}"/>] = new Array();

      <c:if test="${ ! deploymentServer.useDafDeployment }">
        <c:forEach var="versionedRepository" items="${versionManager.versionedRepositoriesOnlySet}">
          <pws:ensureVersionedRepository var="realVersionedRepository" 
            repositoryName="${versionedRepository.repositoryName}"/>
          <c:if test="${!empty realVersionedRepository.versionTypeNames && !empty realVersionedRepository.deployableTypeNames}">
            availableDestinations[<c:out value="${editAgentID}"/>][ "<c:out value="${realVersionedRepository.absoluteName}"/>" ] =
              { used:false, displayName:"<c:out value="${realVersionedRepository.repositoryName}"/>" };
          </c:if>
        </c:forEach>
      </c:if>


      <%-- virtual file systems --%>
      <c:forEach var="versionedRepository" items="${versionManager.versionedVirtualFileSystemsSet}">
        availableDestinations[<c:out value="${editAgentID}"/>][ "<c:out value="${versionedRepository.absoluteName}"/>" ] =
          { used:false, displayName:"<c:out value="${versionedRepository.absoluteName}"/>" };
      </c:forEach>
    </c:forEach>


    function selectAndSubmit()
    {
      <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
        saveDelimitedMapping(<c:out value="${editAgentID}"/>);
        setSelection( '<c:out value="includeDestinations_${editAgentID}"/>', true );
      </c:forEach>
      submitForm( 'editAgentsForm' );
    }

    <%-- virtual file system mapping functions --%>
    var keyDelimiter     = "$$";
    var mappingDelimiter = "||";
    var delimitedMapping = new Array();
    var vfsMapping = new Array();
    var sourceEdits = new Array();

    <%-- should only be one agent id --%>
    <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
      delimitedMapping[<c:out value="${editAgentID}"/>] =  "<dspel:valueof bean='${topologyFormHandlerName}.agentToDelimitedDestinationMap.${editAgentID}'/>";

      vfsMapping[<c:out value="${editAgentID}"/>] = createMapFromDelimited(delimitedMapping[<c:out value="${editAgentID}"/>]);
        
      sourceEdits[<c:out value="${editAgentID}"/>] = new Array();
    </c:forEach>


    // destination mapping labels
    var destMapSourceLabel = "<fmt:message key="destination-map-source-label" bundle="${depBundle}"/>";
    var destMapDestLabel = "<fmt:message key="destination-map-dest-label" bundle="${depBundle}"/>";


      function createMapFromDelimited(delimited)
      {
        var mapping = delimited.split(mappingDelimiter);
        var map = new Array();
        for (var i=0; i<mapping.length; i++) {
          var keyValue = mapping[i].split(keyDelimiter);
          if (keyValue != null && keyValue != "")
            map[keyValue[0]] = keyValue[1];
        }
        return map;
      }

      function createDelimitedFromMap(map)
      {
        var delimited = new String();
        for (key in map) {
          if (map[key] != null && map[key] != "") {
            delimited += key + keyDelimiter + map[key] + mappingDelimiter;
          }
        }
        return delimited;
      }

      function drawVFSMapping(agentId)
      {
        var mappingDiv = document.getElementById("vfsMapping_"+agentId);
        var mappingHeaderDiv = document.getElementById("vfsMappingHeader_"+agentId);
        var mappingHTML = new String();
        var mappingHeaderHTML = new String();

        var included = document.getElementById("includeDestinations_"+agentId);
        
        if (included != null && included.options.length > 0) {

          mappingHeaderHTML += '<table cellpadding="0" cellspacing="0" border="0"><tr><td style="width: 325px;"><p><label><span class="tableInfo destinationLabel">';
          mappingHeaderHTML += destMapSourceLabel;
          mappingHeaderHTML += '</span></label></p></td><td><p><label><span class="tableInfo destinationLabel">';
          mappingHeaderHTML += destMapDestLabel;
          mappingHeaderHTML += '</span></label></p></td></tr></table>';

          mappingHTML = '<table cellpadding="0" cellspacing="0" border="0" class="dataTable">';
          for (i=0; i < included.options.length; i++) {
              var source = included.options[i].text;
              var dest = null;
              if (vfsMapping[agentId][source] != null && vfsMapping[agentId][source] != "") 
                dest = vfsMapping[agentId][source];
              else
                dest = source;
              


              mappingHTML += '<tr><td class="leftAligned" valign="top" style="width: 325px;"><span class="tableInfo">';
              mappingHTML += source;
              mappingHTML += '</span></td><td class="leftAligned"><span class="tableInfo">';
              if (sourceEdits[agentId][source] == 1) {
                mappingHTML += '<input type="text" id="vfsDest_' + agentId + '" value="' + dest + '" onChange="saveValue( \'' + agentId + '\', \'' + source + '\');" size="30" maxlength="254"/>';
                mappingHTML += '</span></td><td class="rightAligned">';
                mappingHTML += '<input type="button" class="buttonSmall" value="stop editing" align="right" onClick="stopEditMapping( \'' + agentId + '\', \'' + source + '\')"/></td></tr>';
              } else {
                mappingHTML += dest
                mappingHTML += '</span></td><td class="rightAligned" width="10%">';
                mappingHTML += '<input type="button" class="buttonSmall" value="edit" align="right" onClick="editMapping( \'' + agentId + '\', \'' + source + '\')"/></td></tr>';
              }
          }
          mappingHTML += '</table>';
        }
        
        mappingDiv.innerHTML = mappingHTML;
        mappingHeaderDiv.innerHTML = mappingHeaderHTML;
      }

      function editMapping(agentId, source)
      {
        sourceEdits[agentId][source] = 1;
        
        <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
          drawVFSMapping(<c:out value="${editAgentID}"/>);
        </c:forEach>
      }

      function stopEditMapping(agentId, source)
      {
        saveValue(agentId, source);
        sourceEdits[agentId][source] = 0;
        
        <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
          drawVFSMapping(<c:out value="${editAgentID}"/>);
        </c:forEach>
      }

      function saveValue(agentId, source)
      {
        var elem = document.getElementById("vfsDest_"+agentId);
        vfsMapping[agentId][source] = elem.value;
      }

      function saveDelimitedMapping(agentId)
      {
        var delimited = createDelimitedFromMap(vfsMapping[agentId]);
        var delimitedField = document.getElementById("destinationMap_"+agentId);
        delimitedField.value = delimited;
      }

  </script>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0" width="100%">
      <tr>
        <td class="blankSpace">&nbsp;</td>
        <td>
          <h2>
            <fmt:message key="editing-agents-for-site" bundle="${depBundle}"> 
              <fmt:param value='${targetDef.displayName}'/>
            </fmt:message>
          </h2>
        </td>

        <td width="100%" class="error rightAlign">
          <dspel:include page="../includes/formErrors.jsp"/>
        </td>		

      </tr>
    </table>
  </div> <!-- contentActions -->
			
  <div id="adminDeployment">

    <table cellpadding="0" cellspacing="0" border="0" class="dataTableNoGrid">

      <portlet:renderURL var="agentsURL">
        <portlet:param name="atg_admin_menu_group" value="deployment"/>
        <portlet:param name="atg_admin_menu_1" value="configuration"/>
        <portlet:param name="goto_config_details_tabs" value="true"/>
        <portlet:param name="config_tab_name" value="agents"/>
        <portlet:param name="add_or_edit_agent" value='none'/>                      
        <portlet:param name="done_edit_agents" value='true'/>                      
        <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("TARGET_DEF_ID")+""%>'/>
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

      <c:set var="disableFormElements" value="${targetDef.primary}"/>

      <dspel:form action="${thisPageURL}" id="editAgentsForm" name="editAgentsForm" 
        formid="editAgentsForm" method="post">

        <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${agentsURL}"/>
        <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${thisPageURL}"/>
        <dspel:input type="hidden" bean="${topologyFormHandlerName}.targetID" value="${TARGET_DEF_ID}"/>
        <dspel:input type="hidden" bean="${topologyFormHandlerName}.updateMultipleAgents" priority="-10" value="foo"/>

        <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
          <tr>
            <th class="leftAligned" style="width: 20%">
              <span class="tableHeader">
                <fmt:message key="agent-name" bundle="${depBundle}"/>
              </span>
            </th>
            <th class="centerAligned" style="width: 50%">
              <span class="tableHeader">
                <fmt:message key="transport-url" bundle="${depBundle}"/>
              </span>
            </th>
            <th class="centerAligned" style="width: 2%">
              <span class="tableHeader">
                <fmt:message key="essential" bundle="${depBundle}"/>
              </span>
            </th>
          </tr>

          <c:choose>
            <c:when test="${loopStatus.count % 2 == 0}">
              <c:set var="trclass" value="alternateRowHighlight"/>
            </c:when>
            <c:otherwise>
              <c:set var="trclass" value=""/>
            </c:otherwise>
          </c:choose>

          <tr class="<c:out value="${trclass}"/>"/>

            <c:set var="beanFullDotName" value="${topologyFormHandlerName}.editAgentIDs"/> 

            <dspel:input bean="${beanFullDotName}" disabled="${disableFormElements}" 
              value="${editAgentID}" type="hidden"/>

            <c:set var="beanFullDotName" 
              value="${topologyFormHandlerName}.agentToDisplayName.${editAgentID}"/> 

            <td class="leftAligned">
              <p>
                <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" 
                  style="margin-right: 6px; vertical-align: middle;" width="16" height="16" border="0" />

                <dspel:input type="text" disabled="${disableFormElements}" bean="${beanFullDotName}" 
                  iclass="formElement formElementInputText" style="margin-left: 0px;" />
              </p>
            </td>

            <c:set var="beanFullDotName" 
              value="${topologyFormHandlerName}.agentToTransportURL.${editAgentID}"/> 

            <td class="centerAligned">
              <p>
                <dspel:input disabled="${disableFormElements}" bean="${beanFullDotName}" 
                  type="text" size="60" iclass="formElement" style="width:300px"/>
              </p>
            </td>

            <c:set var="beanFullDotName" 
              value="${topologyFormHandlerName}.agentToAgentEssential.${editAgentID}"/> 
            <td class="centerAligned">
              <p>
                <dspel:input bean="${beanFullDotName}" disabled="${disableFormElements}" type="checkbox" iclass="checkbox"/>
              </p>
            </td>

          </tr>

          <tr class="<c:out value="${trclass}"/>">
            <td class="wrapNoBorder">&nbsp;</td>
            <td colspan="2">
              <span class="tableInfo">&nbsp;</span>
            </td>	
          </tr>

          <tr class="<c:out value="${trclass}"/>">
            <td colspan="3">
              <table cellpadding="0" cellspacing="0" border="0">
                <tr>
                  <td>
                    <p>
                      <label> <%-- Available Destinations --%>
                        <span class="tableInfo destinationLabel">
                          <fmt:message key="available-destinations" bundle="${depBundle}"/>:
                        </span>
                      </label>
                    </p>
                  </td>	
                  <td>&nbsp;</td>
                  <td>
                    <p>
                      <label> <%-- Include Destinations --%>
                        <span class="tableInfo destinationLabel">
                          <fmt:message key="include-destinations" bundle="${depBundle}"/>: 
                        </span>
                      </label>
                    </p>
                  </td>
                </tr>
                <tr>
                  <td>
                    <select id="<c:out value="availableDestinations_${editAgentID}"/>" multiple="true" size="8"
                      class="formElement" style="width: 250px;"
                      <c:if test="${disableFormElements}">disabled</c:if> >
                    </select>
                  </td>

                  <td align="center">
                    <%-- Button Column --%>
                    <p class="multiButton">
                      <%-- Add All Button --%>
                      <input type="button" class="buttonSmall" value=">>" 
                        id="addAllButton" name="addAllButton"
                        onClick="moveAllFromTo('<c:out value="availableDestinations_${editAgentID}"/>','<c:out value="includeDestinations_${editAgentID}"/>');drawVFSMapping(<c:out value="${editAgentID}"/>)"/>
                    </p>
                    <%-- Add Selected Button --%>
                    <input type="button" class="buttonSmall" 
                      onClick="move('<c:out value="availableDestinations_${editAgentID}"/>','<c:out value="includeDestinations_${editAgentID}"/>');drawVFSMapping(<c:out value="${editAgentID}"/>)"
                      value=">" id="addSelectedButton" name="addSelectedButton"/>
                    <br />
                    <%-- Remove Selected Button --%>
                    <input type="button" class="buttonSmall" 
                      onClick="move('<c:out value="includeDestinations_${editAgentID}"/>','<c:out value="availableDestinations_${editAgentID}"/>');drawVFSMapping(<c:out value="${editAgentID}"/>)"
                      value="<" id="removeSelectedButton" name="removeSelectedButton"/>
                    <p class="multiButton">
                      <%-- Remove All Button --%>
                      <input type="button" class="buttonSmall"
                        onClick="moveAllFromTo('<c:out value="includeDestinations_${editAgentID}"/>','<c:out value="availableDestinations_${editAgentID}"/>');drawVFSMapping(<c:out value="${editAgentID}"/>)"
                        value="<<" id="removeAllButton" name="removeAllButton"/>
                    </p>
                  </td>

                  <td>

                    <c:set var="beanFullDotName" 
                      value="${topologyFormHandlerName}.agentToIncludeAssetDestinations.${editAgentID}"/> 

                    <dspel:select id="includeDestinations_${editAgentID}" multiple="true" size="8" style="width: 250px;"
                      bean="${beanFullDotName}" disabled="${disableFormElements}" iclass="formElement">

                      <dspel:getvalueof var="currentDestinations" bean="${beanFullDotName}"/>

                      <c:forEach var="versionedRepository" items="${currentDestinations}">
                        <script language="JavaScript" type="text/javascript">
                          availableDestinations[<c:out value="${editAgentID}"/>]["<c:out value="${versionedRepository}"/>"].used = true;
                        </script>
                        <dspel:option value="${versionedRepository}">
                          <c:out value="${versionedRepository}"/>
                        </dspel:option>
                      </c:forEach>

                    </dspel:select>
                  </td>
                </tr>

              </table>	
            </td>
          </tr>

          <tr class="<c:out value="${trclass}"/>"/>
             <td colspan="3">
               <c:set var="beanFullDotName" value="${topologyFormHandlerName}.agentToDelimitedDestinationMap.${editAgentID}"/> 
               <dspel:input id="destinationMap_${editAgentID}" type="hidden" bean="${beanFullDotName}" value=""/>

               <div id="<c:out value="vfsMappingHeader_${editAgentID}"/>">
                 <%-- this is populated dynamically --%>
               </div>
             </td>
          </tr>
          <tr class="<c:out value="${trclass}"/>"/>
             <td colspan="3">
               <div id="<c:out value="vfsMapping_${editAgentID}"/>">
                 <%-- this is populated dynamically --%>
               </div>
             </td>
          </tr>

          <tr class="<c:out value="${trclass}"/>">
            <td class="wrapBorder">&nbsp;</td>
            <td class="wrapBorder" colspan="5">
              <span class="tableInfo">&nbsp;</span>
            </td>	
          </tr>

        </c:forEach>	
      </dspel:form>
    </table>
		
  </div> <!-- adminDeployment -->

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>	

        <td class="blankSpace" width="100%">&nbsp;</td>

        <c:if test="${!disableFormElements}">
          <td class="buttonImage">
            <a href="javascript:selectAndSubmit();" class="mainContentButton save" 
              onmouseover="status='';return true;">
              <fmt:message key="save-changes" bundle="${depBundle}"/>
            </a>
          </td>		
          <td class="buttonImage">
            <a href="<c:out value='${agentsURL}'/>" class="mainContentButton delete" 
              onmouseover="status='';return true;">
              <fmt:message key="cancel" bundle="${depBundle}"/>
            </a>
          </td>	
        </c:if>

        <c:if test="${disableFormElements}">
          <td class="buttonImage">
            <a href="<c:out value='${agentsURL}'/>" class="mainContentButton go" 
              onmouseover="status='';return true;">
              <fmt:message key="back" bundle="${depBundle}"/>
            </a>
          </td>		
        </c:if>

        <td class="blankSpace"></td>
      </tr>
    </table>

    <script language="Javascript" type="text/javascript">
      registerOnLoad( function() {
        // This fucntion is passed to condPopulateListBox to
        // cause elements that aren't already used to not be
        // added to the listbox
        function destinationTest( element ) { 
          return ! element.used; 
        }
        // On load, populate the availableDestinations list
        // with all destinations not already in the include
        // destinations list
        <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
        condPopulateListBox( '<c:out value="availableDestinations_${editAgentID}"/>', availableDestinations[<c:out value="${editAgentID}"/>], 
          destinationTest );
        // Deselect stuff in list boxes
        setSelection( '<c:out value="availableDestinations_${editAgentID}"/>', false );
        setSelection( '<c:out value="includeDestinations_${editAgentID}"/>', false );
        </c:forEach>
      } );

      <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
      drawVFSMapping(<c:out value="${editAgentID}"/>);
      </c:forEach>
    </script>

  </div> <!-- contentActions -->

</dspel:page>
  
<!-- END FILE config_edit_agents.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_edit_agents.jsp#2 $$Change: 651448 $--%>
