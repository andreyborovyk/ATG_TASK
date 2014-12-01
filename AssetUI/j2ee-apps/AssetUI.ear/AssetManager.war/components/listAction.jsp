<%--
  Invisible iframe for invoking "list actions", which manipluate, in response
  to user actions, the state of a server-side list model that is accessed via
  the component and property name properties. 

  
  @param  item
  The item that needs to be added or removed from the list. (or unset if the action is addAll, removeAll)

  @param  action
  An action to be performed on the server-side representation of the given tree
  node.  Valid actions are:
    - add: Add to the list
    - remove: Remove from the list
    - addAll: Add all candidates to the list
    - removeAll: clear out the list completely

  @param listManager 
  The nucleus path to a component of type ListManager that contains the list to be managed.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/listAction.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <c:set var="debug" value="false"/>
  
  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>
      </title>
    </head>
    <body>

      <%-- get the sessioninfo --%>
      <dspel:importbean var="sessionInfo" bean="${param.listManager}"/>

      <c:if test="${debug}">        
        list manager = <c:out value="${param.listManager}"/><br />
        list action = <c:out value="${param.action}"/><br />
        list item = <c:out value="${param.item}"/><br />
        <c:if test="${sessionInfo ne null}">
          <dspel:test var="checkedItemsInfo" value="${sessionInfo.checkedItems}"/>
          numChecked items before = <c:out value="${checkedItemsInfo.size}"/><br />
        </c:if>
      </c:if>

      <%-- Perform the list action specified by the request parameters --%>
      <c:if test="${not empty param.action}">
        <asset-ui:performListAction listManager="${param.listManager}"
                                    action="${param.action}"
                                    item="${param.item}"/>
      </c:if>

      <c:if test="${debug}">        
        <c:if test="${sessionInfo ne null}">
          <dspel:test var="checkedItemsInfo" value="${sessionInfo.checkedItems}"/>
          numChecked items after = <c:out value="${checkedItemsInfo.size}"/><br />
        </c:if>
      </c:if>
 
      <script type="text/javascript" > 
        //
        // initialize the numChecks var from the appropriate server side component  
        //
        <c:set var="numChecks" value="0"/>
        <c:if test="${sessionInfo ne null}">
          <c:set var="numChecks" value="${sessionInfo.checkedItemCount}"/>
        </c:if>
        parent.atg.assetmanager.checkboxes.setNumChecks("<c:out value='${numChecks}'/>");
      </script>
    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/listAction.jsp#2 $$Change: 651448 $--%>
