
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="caf"   uri="http://www.atg.com/taglibs/caf" %>

<html>
<head>
     <caf:branchChildren
          parentId="${param.parentId}" 
          treeId="${param.treeId}">

            <caf:branchChild
               branchId="${param.parentId}_0"
               type="generic"
               label="Label A"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="testSelect"
               hasChildren="true"
               checkboxes="true"
               canDrag="true"
               canDrop="true"
               initStateChecked="true"
               dropTypes="one;two;genericDataType"
               loadActionUrl="/CAF/test/treeChildren.jsp"
               loadActionParams="MARK=asd${param.MARK};ATG=asd;"
               dynamicProperties="googleStr=Mapped Properties"
             />

            <caf:branchChild
               branchId="${param.parentId}_1"
               type="generic"
               label="Many Children"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="show"
               hasChildren="true"
               canDrag="true"
               canDrop="true"
               dropTypes="one;two;genericDataType"
               loadActionUrl="/CAF/test/tree100Children.jsp"
               loadActionParams="MARK=asd${param.MARK};ATG=asd;"
               dynamicProperties="googleStr=Mapped Properties;selectActionParam=many"
             />

            <caf:branchChild
               branchId="${param.parentId}_2"
               type="generic"
               label="Label C"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="testSelect"
               hasChildren="true"
               checkboxes="true"
               loadActionUrl="/CAF/test/treeChildren.jsp"
               canDrag="true"
               canDrop="true"
               dropTypes="one;two;genericDataType"
               loadActionParams="MARK=asd${param.MARK};ATG=asd;"
               dynamicProperties="googleStr=Object Oriented Javascript Trees"
             />


     </caf:branchChildren>

   </head>
   <body></body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/treeChildren.jsp#2 $$Change: 651448 $--%>
