<%--
  Property editor for editing the properties of a content group or targeter.
  The following properties can be edited: description, repository,
  repositoryViewName, and profileRepositoryViewName (targeters only).

  @param profileRepository   The profile repository associated with the targeter.
                             This value is empty for content groups, which only
                             target content items.

  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/componentProperties.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/personalization/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="amConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="assetMgrResources" value="${amConfig.resourceBundle}"/>
  <fmt:bundle basename="${assetMgrResources}">
    <fmt:message var="requiredTitle" key='assetEditor.required.title'/>
    <fmt:message var="requiredMarker" key="assetEditor.requiredMarker"/>
  </fmt:bundle>

<script type="text/javascript">

  var assetTypesList = new Array( 0 );

  <%-- Get the list of content repositories and its item types --%>
  <asset-ui:getContentRepositories var="repList" />

   <c:forEach items="${repList}" var="result" varStatus="count">
     var assetType = new Object();
     assetType.repName = '<c:out value="${result.repositoryName}"/>';
     assetType.itemDesc = new Array(0);
     assetType.componentPath = '<c:out value="${result.componentPath}"/>';
     <c:forEach items="${result.types}" var="result1">
       var resultType = new Object();
       resultType.type = '<c:out value="${result1.type}"/>';
       resultType.displayName = '<c:out value="${result1.displayName}"/>'
       assetType.itemDesc[ assetType.itemDesc.length] = resultType;
     </c:forEach>
     assetTypesList[ assetTypesList.length ] = assetType;
  </c:forEach>

  function populateTypePullDown(componentPath)
  {
    var itemTypesList = document.getElementById("propertyValue_repositoryViewName");
    itemTypesList.options.length = 0;

    if ( assetTypesList.length > 1)
    {
      for(var x=0; x < assetTypesList.length; x++)
      {
        var assetType1 = assetTypesList[x];
        var repName = assetType1.componentPath;
        if ( repName == componentPath )
        {
          var itemDesc = new Array(0);
          itemDesc = assetType1.itemDesc;
          for(var count=0; count < itemDesc.length; count++)
          {
            itemTypesList.options[count] = new Option(itemDesc[count].displayName);
            itemTypesList.options[count].value = itemDesc[count].type;
          }
        }
      }
    }
  }

</script>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramTabName" param="tabName"/>
  <dspel:getvalueof var="paramProfileRepository" param="profileRepository"/>

  <fieldset>

  <c:if test="${not empty view.attributes.resourceBundle and not empty view.displayName}">
    <legend>
      <span>
       <fmt:setBundle var="resBundle" basename="${view.attributes.resourceBundle}"/>
       <fmt:message key="${view.displayName}" bundle="${resBundle}"/>
      </span>
    </legend>
  </c:if>

  <table class="formTable">

    <c:set var="propertyName" value="nucleusComponentName"/>
    <tr>
      <td class="formLabel">
        <span class="required" title="${requiredTitle}">
          <c:out value="${requiredMarker}"/>
        </span>
        <fmt:message key="${paramTabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:input type="text" id="propertyValue_${propertyName}"
                     iclass="formTextField" size="50" maxlength="254"
                     oninput="formFieldModified()"
                     onpropertychange="formFieldModified()"
                     bean="${requestScope.formHandlerPath}.${propertyName}"/>
      </td>
    </tr>

    <c:set var="propertyName" value="$description"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${paramTabName}.${propertyName}"/>:
      </td>
      <td>
        <dspel:input type="text" id="propertyValue_${propertyName}"
                     iclass="formTextField" size="50" maxlength="254"
                     oninput="formFieldModified()"
                     onpropertychange="formFieldModified()"
                     bean="${requestScope.formHandlerPath}.properties.${propertyName}"/>
      </td>
    </tr>

    <c:set var="propertyName" value="repository"/>
    <%-- Store the component Path of the content repository --%>
    <dspel:getvalueof var="currentValue" bean="${requestScope.formHandlerPath}.properties.${propertyName}" />

    <tr>
      <td class="formLabel">
        <fmt:message key="${paramTabName}.${propertyName}"/>:
      </td>
      <td>
        <c:choose>
          <c:when test="${currentValue eq null}">
            <dspel:select id="propertyValue_${propertyName}"
                          bean="${requestScope.formHandlerPath}.properties.${propertyName}"
                          onchange="populateTypePullDown(document.getElementById('propertyValue_${propertyName}')[document.getElementById('propertyValue_${propertyName}').options.selectedIndex].value);markAssetModified();">

               <c:forEach var="contents" items="${repList}">
                 <dspel:option value="${contents.componentPath}" selected="${selected}">
                   <c:out value="${contents.repositoryName}" />
                 </dspel:option>
               </c:forEach>
            </dspel:select>
          </c:when>
          <c:otherwise>
            <dspel:valueof bean="${requestScope.formHandlerPath}.targetedRepository" />
          </c:otherwise>
        </c:choose>
      </td>
    </tr>

    <c:set var="propertyName" value="repositoryViewName"/>
    <tr>
      <td class="formLabel">
        <fmt:message key="${paramTabName}.${propertyName}"/>:
      </td>
      <td>
        <c:choose>
          <c:when test="${currentValue eq null}">
            <dspel:select id="propertyValue_${propertyName}"
                          onchange="markAssetModified()"
                          bean="${requestScope.formHandlerPath}.properties.${propertyName}">

              <c:forEach items="${repList}" var="contents" begin="0" end="0">
                <c:forEach items="${contents.types}" var="itemTypes" >
                  <dspel:option value="${itemTypes.type}">
                    <c:out value="${itemTypes.displayName}" />
                  </dspel:option>
                </c:forEach>
              </c:forEach>
            </dspel:select>
          </c:when>
          <c:otherwise>
            <dspel:valueof bean="${requestScope.formHandlerPath}.targetedRepositoryView"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>

    <c:if test="${paramProfileRepository != null}">
      <c:set var="propertyName" value="profileRepositoryViewName"/>
      <dspel:getvalueof var="currentValue"
                        bean="${requestScope.formHandlerPath}.properties.${propertyName}" />
      <tr>
        <td class="formLabel">
          <fmt:message key="${paramTabName}.${propertyName}"/>:
        </td>
        <td>
          <c:choose>
            <c:when test="${currentValue eq null}">
              <asset-ui:getProfileTypes var="profileTypesList"
                                        repositoryPath="${paramProfileRepository}"/>

              <dspel:select id="propertyValue_${propertyName}"
                            onchange="markAssetModified()"
                            bean="${requestScope.formHandlerPath}.properties.${propertyName}">

                <c:forEach var="profileType" items="${profileTypesList}">
                  <dspel:option value="${profileType.type}">
                    <c:out value="${profileType.displayName}" escapeXml="false"/>
                  </dspel:option>
                </c:forEach>
              </dspel:select>
            </c:when>

            <c:otherwise>
              <dspel:valueof bean="${requestScope.formHandlerPath}.targetedProfileRepositoryView" />
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:if>   <%-- If paramProfileRepository != null --%>

  </table>
  </fieldset>


</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/tab/targeting/componentProperties.jsp#2 $$Change: 651448 $--%>
