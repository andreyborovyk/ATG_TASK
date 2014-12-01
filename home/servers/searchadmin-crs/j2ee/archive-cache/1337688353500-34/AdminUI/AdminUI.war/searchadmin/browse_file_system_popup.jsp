<%--
  This Page is used for displaying and choosing server-side folder (e.g. to configure file system content).

  @author Alexander Lutarevich
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_file_system_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$$
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="destFieldId" var="destFieldId"/>
  <d:getvalueof param="title" var="title"/>

  <div class="content" id="browseTreePane" dojoType="dijit.layout.LayoutContainer"
       style="width: 100%; height: 100%; padding: 0; margin: 0; border: 0;">
    <div dojoType="dojox.layout.ContentPane" layoutAlign="top" style="padding:3px 15px;">
      <b><fmt:message key="browse_file_system_popup.select_folder.${title}"/></b>
    </div>

    <div id="browseTreeContainer" style="overflow:auto; padding:0 15px;" dojoType="dojox.layout.ContentPane" layoutAlign="client">
      <div dojoType="atg.searchadmin.tree.BrowseLazyStore"
           jsId="file_system_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
      <div dojoType="atg.searchadmin.tree.BrowseDojoTree"
           id="file_system_dojo_Tree"
           store="file_system_store"
           class="atg"
           expandUrl="/searchadmin/browse_folder_nodes.jsp"
           persist="false">
      </div>
    </div>

    <div dojoType="dojox.layout.ContentPane" layoutAlign="bottom" style="padding:3px 15px;">
      <b><fmt:message key="browse_file_system_popup.view_subfolder"/></b>
    </div>
  </div>

  <div class="footer" id="popupFooter">
    <input type="button" value="<fmt:message key="browse_file_system_popup.button.ok"/>"
           onclick="getPath();"
           title="<fmt:message key="browse_file_system_popup.button.ok"/>"/>

    <input type="button" value="<fmt:message key="browse_file_system_popup.button.cancel"/>" onclick="closePopUp();"
           title="<fmt:message key="browse_file_system_popup.button.cancel"/>"/>
  </div>
  <script type="text/javascript">
    function getPath() {
      var tree = dijit.byId("file_system_dojo_Tree");
      var selectedNode = dijit.getEnclosingWidget(tree.selectedNode);
      if (selectedNode == null) {
        return null;
      }
      var store = tree.store;
      document.getElementById('<c:out value="${destFieldId}"/>').value = 
          store.getIdFromIdentity(store.getIdentity(selectedNode.item));
      closePopUp();
    }
    function customLoad() {
      var browseTreePane = dijit.byId("browseTreePane");
      if (browseTreePane != null) {
        browseTreePane.layout();
        browseTreePane.resize();
      }
      new atg.searchadmin.tree.DojoTreeTooltip({connectId:["file_system_dojo_Tree"]});
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_file_system_popup.jsp#2 $$Change: 651448 $--%>
