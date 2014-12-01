<%--
  This page displays the children of a tree node whose ID is given by the
  ${requestScope.nodePath} variable.  It also recursively displays the children
  of any child nodes that are currently expanded.

  See tree.jsp for more information.
  
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/treeBranch.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"                %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jsp/jstl/fmt"                     %>

<dspel:page>

  <dspel:importbean var="config"    bean="/atg/web/Configuration"/>
  <dspel:importbean var="treeState" bean="${param.treeComponent}"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get the contents of the node whose path is given by the "nodePath"
       request attribute. --%>
  <web-ui:getTreeChildren var="children"
                          tree="${param.treeComponent}"
                          nodeId="${requestScope.nodePath}"/>
  <c:if test="${empty children and empty requestScope.nodePath}" >
    <c:choose>
      <c:when test="${empty param.emptyTreeView}" >
        <!--jsp:include page="emptyTreeView.jsp" /-->
      </c:when>
      <c:otherwise>
	<c:set var="emptyTreeView" value="${param.emptyTreeView}" />
	<c:set var="emptyTreeViewContextRoot" value="${param.emptyTreeViewContextRoot}" />
	<dspel:include otherContext="${emptyTreeViewContextRoot}" page="${emptyTreeView}" />
      </c:otherwise>
    </c:choose>
  </c:if>

  <%-- Display each of the node's children --%>
  <!-- Begin display of child nodes -->
  <c:forEach var="node" items="${children}">
    <!-- Begin display of child node "<c:out value='${node.path}'/>" -->
    
    <%-- get this value once because this is an expensive calcuation --%>
    <c:set var="nodeHasChildren" value="${node.hasChildren}"/>

    <%-- Determine the plus/minus icon class --%>
    <c:choose>
      <c:when test="${node.isOpen}">
        <c:set var="openCloseClass" value="closeIcon"/>
      </c:when>
      <c:otherwise>
        <c:set var="openCloseClass" value="openIcon"/>
      </c:otherwise>
    </c:choose>

    <%-- Determine the checkbox or radio button state and onclick handler --%>
    <c:choose>
      <c:when test="${param.selectorControl eq 'checkbox'}">
        <c:choose>
          <c:when test="${node.isChecked}">
            <c:set var="checked" value="checked"/>
          </c:when>
          <c:otherwise>
            <c:set var="checked" value=""/>
          </c:otherwise>
        </c:choose>
        <c:set var="checkboxFunction" value="tree.checkNode"/>
      </c:when>
      <c:when test="${param.selectorControl eq 'radio'}">
        <c:choose>
          <c:when test="${node.isChecked}">
            <c:set var="checked" value="checked"/>
          </c:when>
          <c:otherwise>
            <c:set var="checked" value=""/>
          </c:otherwise>
        </c:choose>
        <c:set var="checkboxFunction" value="tree.clearChecksAndCheckNode"/>
      </c:when>
    </c:choose>
    
    <%-- For the selected node, specify the path, so the onload function can
         highlight the node. --%>
    <c:if test="${node.isSelected}">
      <script type="text/javascript">
        tree.selectedPath = "<c:out value='${node.path}'/>";
      </script>
    </c:if>

    <%-- For any highlighted nodes, add this path to the highlighted nodes 
         list (comma delimted), so the onload function can highlight the node. --%>
    <c:if test="${node.isHighlighted}">
      <script type="text/javascript">
        if (tree.highlightedPaths == null)
          tree.highlightedPaths = "<c:out value='${node.path}'/>";
        else
          tree.highlightedPaths += ",<c:out value='${node.path}'/>";
      </script>
    </c:if>

    <%-- Display the node as a single-row non-wrapping table --%>
    <div id="<c:out value='nodeDisplay_${node.path}'/>">
      <table border="0" width="100%" cellpadding="0" cellspacing="0">
        <tr> 
          <%-- set path to the StyleFinder component --%>
          <c:choose>
          	<c:when test="${not empty treeState.treeDefinition.styleFinderPath}">
          	  <c:set var="styleFinderPath" value="${treeState.treeDefinition.styleFinderPath}"/>
          	</c:when>
          	<c:otherwise>
          	  <%-- default styleFinder component --%>
          	  <c:set var="styleFinderPath" value="/atg/web/service/CustomStyleFinder"/>
          	</c:otherwise>
          </c:choose>
                       
          <%-- find any custom styles that need to be applied to this table cell --%>             
          <web-ui:invoke var="customStyle"
            componentPath="${styleFinderPath}"
            method="getStyle">
            <web-ui:parameter value="${node}"/>
            <web-ui:parameter value="StyleFinder_treeCellClass"/>
          </web-ui:invoke>

          <td class="treeCell <c:out value='${customStyle}'/>">
          
            <%-- amitj: BUGS-FIXED: 126223 --%>
            <%-- Determine the JavaScript function signature for handling
                 selection of the node. --%>
            <c:choose>
              <c:when test="${not empty param.onSelectProperties}">

                <c:forTokens var="propertyName" items="${param.onSelectProperties}" delims="," varStatus="loop">
                  <c:if test="${loop.first}">
                    <c:set var="infoVar" value="{"/>
                  </c:if>
                  <c:set var="infoVar" value="${infoVar}${propertyName}: '${node[propertyName]}'"/>
                  <c:choose>
                    <c:when test="${loop.last}">
                      <c:set var="infoVar" value="${infoVar}}"/>
                    </c:when>
                    <c:otherwise>
                      <c:set var="infoVar" value="${infoVar},"/>
                    </c:otherwise>
                  </c:choose>
                </c:forTokens>

                <c:set var="selectFunction"
                       value="tree.nodeSelected('${node.path}', ${infoVar})"/>
                <c:set var="toogleFunction"
                       value="tree.toggleNode('${node.path}', ${infoVar})"/>

              </c:when>
              <c:otherwise>
                <c:set var="selectFunction"
                       value="tree.nodeSelected('${node.path}', '')"/>
                <c:set var="toogleFunction"
                       value="tree.toggleNode('${node.path}', '')"/>
              </c:otherwise>
            </c:choose>
        
            <%-- Display the open/close icon, if applicable --%>
            <c:choose>
              <c:when test="${nodeHasChildren}">
                <a id="openCloseIcon_<c:out value='${node.path}'/>"
                   onclick="<c:out value='${toogleFunction}'/>"
                   class="<c:out value='${openCloseClass}'/>"></a>
              </c:when>
              <c:otherwise>
                <span class="noOpenCloseIcon"></span>
              </c:otherwise>
            </c:choose>


           <%-- determine whether to display tooltips --%>
           <c:set var="tooltip" value=""/>
           <c:if test="${param.showToolTips}"> 
             <c:set var="tooltip" value="${node.toolTipText}"/>
           </c:if>
            
            <c:choose>
              <c:when test="${node.isBucket}">
                <%-- For a bucket node, clicking on it should toggle it open/closed --%>
                <a id="<c:out value='node_${node.path}'/>"
                   onclick="<c:out value='${toogleFunction}'/>"
                   title="<c:out value='${tooltip}'/>">
                   <c:out value="${node.label}"/>
                </a>

              </c:when>
              <c:otherwise>
                
                <%-- Determine the JavaScript function signature for handling
                     checking of the node. --%>
                <c:choose>
                  <c:when test="${not empty param.onCheckProperties}">

                    <c:forTokens var="propertyName" items="${param.onCheckProperties}" delims="," varStatus="loop">
                      <c:if test="${loop.first}">
                        <c:set var="infoVar" value="{"/>
                      </c:if>
                      <c:set var="infoVarValue" value="${node[propertyName]}"/>
                      <%
                        String infoVarVal = (String)pageContext.getAttribute("infoVarValue");
                        if (infoVarVal != null){
                          infoVarVal = infoVarVal.replaceAll("'","\\\\'");
                          infoVarVal = infoVarVal.replaceAll("\"","\\\\\"");
                        }
                        pageContext.setAttribute("infoVarValue", infoVarVal);
                      %>
                      <c:set var="infoVar" value='${infoVar}${propertyName}: "${infoVarValue}"'/>
                      <c:choose>
                        <c:when test="${loop.last}">
                          <c:set var="infoVar" value="${infoVar}}"/>
                        </c:when>
                        <c:otherwise>
                          <c:set var="infoVar" value="${infoVar},"/>
                        </c:otherwise>
                      </c:choose>
                    </c:forTokens>

                    <c:set var="checkboxFunction"
                           value="${checkboxFunction}('${node.path}', ${infoVar})"/>

                  </c:when>
                  <c:otherwise>
                    <c:set var="checkboxFunction"
                           value="${checkboxFunction}('${node.path}', '')"/>
                  </c:otherwise>
                </c:choose>
    
                <%-- Display the checkbox or radio button--%>
                <c:if test="${node.isCheckable}">
                  <c:choose>
                    <c:when test="${param.selectorControl eq 'checkbox'}">
                      <input type="checkbox" name="treeCheckbox" 
                        id="<c:out value='check_${node.path}'/>"
                        onclick="<c:out value='${checkboxFunction}'/>"
                        <c:out value="${checked}"/>/>
                    </c:when>
                    <c:when test="${param.selectorControl eq 'radio'}">
                      <input type="radio"
                        name="treeRadio"
                        id="<c:out value='check_${node.path}'/>"
                        onclick="<c:out value='${checkboxFunction}'/>"
                        <c:out value="${checked}"/>/>
                    </c:when>
                  </c:choose>
                 </c:if>

                <%-- Display the node icon and label --%>
                <c:choose>
                  <c:when test="${node.isSelectable}">
                    <dspel:img otherContext="${node.iconName.contextRoot}" 
                               src="${node.iconName.relativeUrl}" border="0" align="absmiddle"
                           onclick="${selectFunction}"/>
                    <a id="<c:out value='node_${node.path}'/>"
                       class="treeNode"
                       onclick="<c:out value='${selectFunction}'/>"
                       title="<c:out value='${tooltip}'/>">
                      <c:out value="${node.label}"/>          
                    </a>
                  </c:when>
                  <c:otherwise>
                    <dspel:img otherContext="${node.iconName.contextRoot}" 
                               src="${node.iconName.relativeUrl}" border="0" align="absmiddle"/>
                    <c:out value="${node.label}"/>
                  </c:otherwise>
                </c:choose>

              </c:otherwise>  <%-- isBucket --%>
            </c:choose>
            <span id="<c:out value='expanding_${node.path}'/>" style="display:none">
              <fmt:message key="tree.opening"/>
            </span>
          </td>
        </tr>
      </table>
    </div>

    <!-- Begin open-node display for "<c:out value='${node.path}'/>" -->
    <div id="<c:out value='subtreeContainer_${node.path}'/>">

      <%-- If the node is open, display its contents in an indented table.
           Note that we "push and pop" the ${requestScope.nodePath} variable.
           The "pop" may not be strictly necessary... --%>
      <c:if test="${node.isOpen and nodeHasChildren}">
        <c:set var="originalNodePath" value="${requestScope.nodePath}"/>
        <c:set scope="request" var="nodePath" value="${node.path}"/>
        <dspel:include page="/tree/treeBranchIndented.jsp"/>
        <c:set scope="request" var="nodePath" value="${originalNodePath}"/>
      </c:if>

    </div>
    <!-- End open-node display for "<c:out value='${node.path}'/>" -->

    <!-- End display of child node "<c:out value='${node.path}'/>" -->
  </c:forEach>
  <!-- End display of child nodes -->

</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/treeBranch.jsp#2 $$Change: 651448 $--%>
