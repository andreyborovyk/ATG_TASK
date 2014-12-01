<%--
  Invisible iframe for invoking "tree actions", which manipluate, in response
  to user actions, the state of a server-side tree model that is accessed using
  the interface atg.web.tree.TreeState.
  
  @param  nodeId
  The ID of the tree node upon which the given action should be performed.

  @param  action
  An action to be performed on the server-side representation of the given tree
  node.  Valid actions are:
    - open: Indicate that the node has been expanded
    - close: Indicate that the node has been collapsed
    - check: Indicate that the node has been added to a selection list
    - uncheck: Indicate that the node has been removed from a selection list
    - select: Indicate that the node is now the current single selection (this
      is distinct from the selection list)
  Note that the action parameter is optional.  If unsupplied, this page does
  nothing.

  @param  treeComponent
  The path of the server-side component that manages the tree state.
  
  See tree.jsp for more details.
  
  @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/treeActionFrame.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"      uri="http://java.sun.com/jsp/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui_rt"                %>

<dspel:page>
  
  <!DOCTYPE html 
    PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
  
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>
      </title>
    </head>
    <body>
      
      <%-- Perform the tree action specified by the request parameters --%>
      <c:if test="${not empty param.action}">
        <web-ui:performTreeAction tree="${param.treeComponent}"
                                  action="${param.action}"
                                  nodeId="${param.nodeId}"/>
      </c:if>

    </body>
  </html>
</dspel:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tree/treeActionFrame.jsp#2 $$Change: 651448 $--%>
