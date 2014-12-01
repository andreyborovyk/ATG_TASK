<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 

<dspel:page>
<html>

<head>
<%-- Event handler for selections in the tree. --%>
<script language="JavaScript">
  function assetSelected(asset) {
    alert("Path of selected asset: " + asset.path);
  }
</script>
</head>

<body>
  <table width="100%" height="100%" cellspacing="0" cellpadding="0" border="0">
  <tr>
  <td width="50%">
    <font face="Tahoma" style="font-size:14px">Registry Tree (EL)</font><br><br>
  </td>
  <td width="50%">
    <font face="Tahoma" style="font-size:14px">Registry Tree (Tag)</font><br><br>
  </td>
  </tr>
  <tr>
  <td width="50%" height="100%">
  <%-- URL for a tree that displays the page registry. --%>
  <c:url var="treeURL" value="/components/registryTree.jsp">
    <c:param name="registry"        value="page:default"/>
    <c:param name="onSelect"        value="assetSelected"/>
    <c:param name="assetProperties" value="path"/>
  </c:url>

  <dspel:iframe src="${treeURL}" width="50%" height="100%"/>

  </td>
  <td width="50%" height="100%">
 
  <caf:assetRegistryTree
    id="registryTree1"
    css="class123"
    registryName="page:default"
    onAssetSelect="assetSelected"
    onAssetSelectProperties="path"/>
  
  </td>
  </tr>
  </table>

</body>
</html>
</dspel:page>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/registry_test.jsp#2 $$Change: 651448 $--%>
