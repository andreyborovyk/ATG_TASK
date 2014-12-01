
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>
<html>
   <head>
     <caf:branchChildren
          parentId="${param.parentId}" 
          treeId="${param.treeId}">
          <c:forTokens var="loopVarA" items="0,1,2,3,4,5,6,7,8,9" delims=",">
          <c:forTokens var="loopVarB" items="0,1,2,3,4,5,6,7,8,9" delims=",">
            <caf:branchChild
               branchId="${param.sourceId}_${loopVarA}${loopVarB}"
               type="1OF100"
               label="Label ${loopVarA}${loopVarB}"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="show"
               hasChildren="true"
               checkboxes="true"
               loadActionUrl="/CAF/test/treeChildren.jsp"
               iconOpened="/CAF/images/tree/arrow-preview-opened.gif"
               iconClosed="/CAF/images/tree/arrow-preview-closed.gif"
               canDrag="true"
               canDrop="true"
               dropTypes="one;two"
               dynamicProperties="selectActionParam=many"
             />
        </c:forTokens>
       </c:forTokens>
     </caf:branchChildren>

   </head>
   <body >
   </body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/tree100Children.jsp#2 $$Change: 651448 $--%>
