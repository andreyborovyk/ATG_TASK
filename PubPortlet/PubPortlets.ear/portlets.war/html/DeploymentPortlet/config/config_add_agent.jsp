<!-- BEGIN FILE config_add_agent.jsp -->
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

<dspel:page xml="true">

  <fmt:setBundle var="depBundle" 
    basename="atg.epub.portlet.DeploymentPortlet.Resources"/>

  <portlet:defineObjects/>

  <portlet:renderURL var="agentsURL">
    <portlet:param name="atg_admin_menu_group" value="deployment"/>
    <portlet:param name="atg_admin_menu_1" value="configuration"/>
    <portlet:param name="goto_config_details_tabs" value="true"/>
    <portlet:param name="config_tab_name" value="agents"/>
    <portlet:param name="add_or_edit_agent" value='none'/>                      
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
    <portlet:param name="add_or_edit_agent" value='add'/>                      
    <portlet:param name="goto_config_details_tabs" value="true"/>  
    <c:if test="${addSite}">
      <portlet:param name="add_site" value="true"/>  
    </c:if>
  </portlet:renderURL> 
    
  <portlet:renderURL var="thisPageFailureURL"> 
    <portlet:param name="atg_admin_menu_group" value="deployment"/>  
    <portlet:param name="atg_admin_menu_1" value="configuration"/>  
    <portlet:param name="target_def_id" value='<%=pageContext.findAttribute("TARGET_DEF_ID")+""%>'/>  
    <portlet:param name="config_tab_name" value="agents"/>                      
    <portlet:param name="add_or_edit_agent" value='add'/>                      
    <portlet:param name="goto_config_details_tabs" value="true"/>  
    <portlet:param name="add_agent_form_submitted" value="true"/>  
    <c:if test="${addSite}">
      <portlet:param name="add_site" value="true"/>  
    </c:if>
  </portlet:renderURL> 

  <dspel:importbean var="versionManager" bean="/atg/epub/version/VersionManagerService"/>
  <dspel:importbean var="deploymentServer" bean="/atg/epub/DeploymentServer"/>

  <script language="javascript" src="<c:url value='/html/scripts/listbox.js'/>" type="text/javascript">
  </script>

  <script language="javascript" type="text/javascript">

    // An associative array of all available destinations.
    var availableDestinations = new Array();

    <%-- repositories --%>
    <c:if test="${ ! deploymentServer.useDafDeployment }">
      <c:forEach var="versionedRepository" items="${versionManager.versionedRepositoriesOnlySet}">
        <pws:ensureVersionedRepository var="realVersionedRepository" 
          repositoryName="${versionedRepository.repositoryName}"/>
        <c:if test="${!empty realVersionedRepository.versionTypeNames && !empty realVersionedRepository.deployableTypeNames}">
          availableDestinations[ "<c:out value="${realVersionedRepository.absoluteName}"/>" ] =
            { used:false, displayName:"<c:out value="${realVersionedRepository.absoluteName}"/>" };
        </c:if>
      </c:forEach>
    </c:if>

    <%-- virtual file systems --%>
    <c:forEach var="versionedRepository" items="${versionManager.versionedVirtualFileSystemsSet}">
      availableDestinations[ "<c:out value="${versionedRepository.absoluteName}"/>" ] =
        { used:false, displayName:"<c:out value="${versionedRepository.absoluteName}"/>" };
    </c:forEach>

    function selectAndSubmit()
    {
      saveDelimitedMapping();
      setSelection( 'includeDestinations', true );
      submitForm('addAgentForm');
    }

    <%-- virtual file system mapping functions --%>

      var keyDelimiter     = "$$";
      var mappingDelimiter = "||";
      var delimitedMapping = "";

      // should only be one agent id
      <c:forEach var="editAgentID" items="${editAgentIDs}" varStatus="loopStatus">
      delimitedMapping = "<dspel:valueof bean='${topologyFormHandlerName}.agentToDelimitedDestinationMap.${editAgentID}'/>";
      </c:forEach>

      // destination mapping labels
      var destMapSourceLabel = '<fmt:message key="destination-map-source-label" bundle="${depBundle}"/>';
      var destMapDestLabel = '<fmt:message key="destination-map-dest-label" bundle="${depBundle}"/>';

      var vfsMapping = createMapFromDelimited(delimitedMapping);

      var sourceEdits = new Array();

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
        var mappingDiv = document.getElementById("vfsMapping");
        var mappingHeaderDiv = document.getElementById("vfsMappingHeader");
        var mappingHTML = new String();
        var mappingHeaderHTML = new String();

        var included = document.getElementById("includeDestinations");
          
        //alert('included destinations: ' + included.options.length);

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
              if (vfsMapping[source] != null && vfsMapping[source] != "") 
                dest = vfsMapping[source];
              else
                dest = source;
              mappingHTML += '<tr><td class="leftAligned" valign="top" style="width: 325px;"><span class="tableInfo">';
              mappingHTML += source;
              mappingHTML += '</span></td><td class="leftAligned"><span class="tableInfo">';
              if (sourceEdits[source] == 1) {
                mappingHTML += '<input type="text" id="' + source + '" value="' + dest + '" onChange="saveValue( \'' + source + '\');" size="30" maxlength="254"/>';
                mappingHTML += '</span></td><td class="rightAligned">';
                mappingHTML += '<input type="button" class="buttonSmall" value="stop editing" align="right" onClick="stopEditMapping( \'' + source + '\')"/></td></tr>';
              } else {
                mappingHTML += dest
                mappingHTML += '</span></td><td class="rightAligned" width="10%">';
                mappingHTML += '<input type="button" class="buttonSmall" value="edit" align="right" onClick="editMapping( \'' + source + '\')"/></td></tr>';
              }
          }
          mappingHTML += '</table>';
        }
        //alert('mapping html:' + mappingHTML);
        //alert('mapping header html:' + mappingHeaderHTML);

        mappingDiv.innerHTML = mappingHTML;
        mappingHeaderDiv.innerHTML = mappingHeaderHTML;
      }

      function editMapping(source)
      {
        sourceEdits[source] = 1;
        drawVFSMapping();
      }

      function stopEditMapping(source)
      {
        saveValue(source);
        sourceEdits[source] = 0;
        drawVFSMapping();
      }

      function saveValue(source)
      {
        var elem = document.getElementById(source);
        vfsMapping[source] = elem.value;
      }

      function saveDelimitedMapping()
      {
        var delimited = createDelimitedFromMap(vfsMapping);
        var delimitedField = document.getElementById("destinationMap");
        delimitedField.value = delimited;
        //alert('saving delimited: ' + delimited);
      }

  </script>

  <dspel:form action="${thisPageURL}" name="addAgentForm" id="addAgentForm" 
    formid="addAgentForm" method="post">

    <dspel:input type="hidden" bean="${topologyFormHandlerName}.successURL" value="${agentsURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.failureURL" value="${thisPageFailureURL}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.targetID" value="${TARGET_DEF_ID}"/>
    <dspel:input type="hidden" bean="${topologyFormHandlerName}.addAgent" priority="-10" value="foo"/>

    <div class="contentActions">
      <table cellpadding="0" cellspacing="0" border="0" width="100%">
        <tr>
          <td class="blankSpace">&nbsp;</td>
          <td>
            <h2>
              <fmt:message key="adding-new-agent" bundle="${depBundle}">
                <fmt:param value="${targetDef.displayName}"/>
              </fmt:message>
            </h2>
          </td>
          <td width="100%" class="error rightAlign">
            <dspel:include page="../includes/formErrors.jsp"/>
          </td>   
          <%--
          <td class="toolBar_button">
            <a href="<c:out value='${thisPageURL}'/>" class="add" onmouseover="status='';return true;">
              <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_newAsset.gif'/>" 
                alt="Add Agent to Site" width="16" height="16" />&nbsp;Add Agent to Site
            </a>
          </td>
          --%>
        </tr>
      </table>
    </div> <!-- contentActions -->

    <div id="adminDeployment">
      <table cellpadding="0" cellspacing="0" border="0" class="dataTableNoGrid">
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
        <tr>
          <td class="leftAligned"> 
            <p>
              <img src="<c:url context="${initParam['atg.bizui.ContextPath']}" value='/images/icon_agent.gif'/>" 
                style="margin-right: 6px; vertical-align: middle;" width="16" height="16" border="0" />
              <dspel:input type="text" bean="${topologyFormHandlerName}.agentDisplayName" 
                iclass="formElement formElementInputText" style="margin-left: 0px;" />
            </p>
          </td>
          <td class="centerAligned">
            <p>
              <c:choose>
                <c:when test="${!empty param.add_agent_form_submitted}">
                  <dspel:input bean="${topologyFormHandlerName}.transportURL" type="text" size="60" 
                    iclass="formElement formElementInputText" style="width:300px" />
                  <br/>
                </c:when>
                <c:otherwise>
                  <dspel:input bean="${topologyFormHandlerName}.transportURL" type="text" size="60" 
                    value="rmi://<host>:<port>/atg/epub/AgentTransport" 
                    iclass="formElement formElementInputText" style="width:300px" />
                  <br/>
                </c:otherwise>
              </c:choose>
            </p>
          </td>
          <td class="centerAligned">
            <p>
              <dspel:input bean="${topologyFormHandlerName}.agentEssential" type="checkbox" default="false" iclass="checkbox"/>
            </p>
          </td>
        </tr>

        <tr>
          <td class="wrapNoBorder">
            &nbsp;
          </td>
          <td colspan="2">
            <span class="tableInfo">&nbsp;</span>
          </td>  
        </tr>

        <tr>
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
                <td> <%-- available destinations select box --%>
                  <select id="availableDestinations" class="formElement" size="8" 
                    style="width:250px;" multiple="true"/>
                </td>

                <td align="center">
                  <%-- Button Column --%>
                  <p class="multiButton">
                    <%-- Add All Button --%>
                    <input type="button" class="buttonSmall" value=">>" 
                      id="addAllButton" name="addAllButton"
                      onClick="moveAllFromTo('availableDestinations','includeDestinations');drawVFSMapping()"/>
                    <br />
                  </p>
                  <%-- Add Selected Button --%>
                  <input type="button" class="buttonSmall" 
                    onClick="move('availableDestinations','includeDestinations');drawVFSMapping()"
                    value=">" id="addSelectedButton" name="addSelectedButton"/>
                  <br />
                  <%-- Remove Selected Button --%>
                  <input type="button" class="buttonSmall" 
                    onClick="move('includeDestinations','availableDestinations');drawVFSMapping()"
                    value="<" id="removeSelectedButton" name="removeSelectedButton"/>
                  <br />
                  <p class="multiButton">
                    <%-- Remove All Button --%>
                    <input type="button" class="buttonSmall"
                      onClick="moveAllFromTo('includeDestinations','availableDestinations');drawVFSMapping()"
                      value="<<" id="removeAllButton" name="removeAllButton"/>
                  </p>
                </td>

                <td> <%-- destination select box --%>
                  <dspel:select id="includeDestinations" multiple="true" iclass="formElement" 
                    size="8" style="width:250px;" bean="${topologyFormHandlerName}.includeAssetDestinations">
                  </dspel:select>
                </td>
              </tr>
            </table>
          </td>
        </tr>

          <tr class="<c:out value="${trclass}"/>"/>
             <td colspan="3">
               <c:set var="beanFullDotName" value="${topologyFormHandlerName}.delimitedDestinationMap"/> 
               <dspel:input id="destinationMap" type="hidden" bean="${beanFullDotName}" value=""/>

               <div id="vfsMappingHeader">
                 <%-- this is populated dynamically --%>
               </div>
             </td>
          </tr>
          <tr class="<c:out value="${trclass}"/>"/>
             <td colspan="3">
               <div id="vfsMapping">
                 <%-- this is populated dynamically --%>
               </div>
             </td>
          </tr>

        <tr>
          <td class="wrapBorder">&nbsp;</td>
          <td class="wrapBorder" colspan="3">
            <span class="tableInfo">&nbsp;</span>
          </td> 
        </tr>

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
            condPopulateListBox( 'availableDestinations', availableDestinations, 
              destinationTest );
            // Deselect stuff in list boxes
            setSelection( 'availableDestinations', false );
            setSelection( 'includeDestinations', false );
          } );

          drawVFSMapping();
        </script>

      </table>
    
      <div class="contentActions">
        <table cellpadding="0" cellspacing="0" border="0">
          <tr>  
            <td class="blankSpace" width="100%">
              &nbsp;
            </td>
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
            <td class="blankSpace"></td>
          </tr>
        </table>
      </div> <!-- contentActions -->

    </div> <!-- adminDeployment -->
    
    <!-- end content -->
    
  </dspel:form>

</dspel:page>


<!-- END FILE config_add_agent.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/config/config_add_agent.jsp#2 $$Change: 651448 $--%>
