<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/portal/gear/docexch/SearchFormHandler"/>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">
<%
  // Define a boolean to ask if documents exist in the repository.
  boolean documentsExist = false;
  
  String dateStyle = dexpage.getDateStyle();
  String timeStyle = dexpage.getTimeStyle();
  String bundleName = dexpage.getResourceBundle();
%>
<i18n:bundle baseName="<%= bundleName %>" changeResponseLocale="false" />
<i18n:message id="emptyText" key="emptyText"/>
<i18n:message id="titleColumnHeader" key="titleColumnHeader"/>
<i18n:message id="authorColumnHeader" key="authorColumnHeader"/>
<i18n:message id="createDateColumnHeader" key="createDateColumnHeader"/>
<i18n:message id="statusColumnHeader" key="statusColumnHeader"/>
<i18n:message id="allItemsLinkText" key="allItemsLinkText"/>
<i18n:message id="newItemLinkText" key="newItemLinkText"/>
<i18n:message id="searchButton" key="searchButton"/>
<i18n:message id="genericError" key="genericError"/>

<P>
<P>
 
<core:if value="<%= dexpage.getEnableSearch() %>"> 
        <%@ include file="searchForm.jspf" %>
</core:if>

<core:demarcateTransaction id="docExchSharedPageXA">

<dsp:droplet name="/atg/portal/gear/docexch/RQLQueryBuilder">
  <dsp:param name="repository" value="<%= dexpage.getRepositoryPath() %>"/>
  <dsp:param name="itemDescriptor" value="<%= dexpage.getItemDescriptorName() %>"/>
  <dsp:param name="gearInstanceRef" value="<%= gearEnv.getGear().getId() %>"/>
  <dsp:param name="gearIdPropertyName" value="<%= dexpage.getGearIdPropertyName() %>"/>
  <dsp:oparam name="query">

    <dsp:droplet name="/atg/dynamo/droplet/RQLQueryRange">
      <dsp:param name="repository" value="<%= dexpage.getRepositoryPath() %>"/>
      <dsp:param name="itemDescriptor" value="<%= dexpage.getItemDescriptorName() %>"/>
      <dsp:param name="gearInstanceRef" value="<%= gearEnv.getGear().getId() %>"/>
      <dsp:param name="queryRQL" param="queryRQL"/>
      <dsp:param name="sortProperties" value='<%= "-" + dexpage.getCreateDatePropertyName() %>'/>
      <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
      <dsp:param name="howMany" value="<%= dexpage.getShortListSize() %>"/>

      <dsp:oparam name="output">
	<dsp:getvalueof id="docElement" param="element">
        <tr>
          <core:if value="<%= dexpage.getDisplayTitle() %>"> 
          <td valign=top><font class="smaller">
            <%-- view details link --%>
            <core:createUrl id="detailsUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
              <core:urlParam param="prevpaf_dm" value="shared"/>
              <core:urlParam param="paf_dm" value="full"/>
              <core:urlParam param="paf_gm" value="content"/>
              <core:urlParam param="dexmode" value="oneItem"/>
              <core:urlParam param="paf_gear_id" 
              value="<%= gearEnv.getGear().getId() %>"/>
              <core:urlParam param="documentid" value="<%= dexpage.getDocumentId(docElement) %>"/>
              <a href="<%= detailsUrl.getNewUrl() 
              %>" class="gear_content"><%= dexpage.getTitle(docElement) %></a>
            </core:createUrl></font></td>
          </core:if>
          <core:if value="<%= dexpage.getDisplayAuthor() %>"> 
          <td valign=top><font class="smaller">
            <%= dexpage.getAuthorFirstName(docElement) %>
            <%= dexpage.getAuthorLastName(docElement) %>
          </font></td>
          </core:if>
          <core:if value="<%= dexpage.getDisplayCreateDate() %>"> 
          <td valign=top><font class="smaller">

          <core:ifNot value='<%= dateStyle.equals("notShown") %>'>
              <i18n:formatDate value="<%= (java.util.Date) dexpage.getCreateDate(docElement) %>" 
                               style="<%= dateStyle %>" 
                               defaultText="illegalDateFormat"/>
          </core:ifNot>
          <core:ifNot value='<%= timeStyle.equals("notShown") %>'>
              <i18n:formatTime value="<%= (java.util.Date) dexpage.getCreateDate(docElement) %>" 
                   style="<%= timeStyle %>" 
                   defaultText="illegalTimeFormat"/>
          </core:ifNot>
          </font></td>
          </core:if>
          <core:if value="<%= dexpage.getDisplayStatus() %>"> 
          <td valign=top><font class="smaller">
            <%= dexpage.getStatus(docElement) %>
          </font></td>
          </core:if>
        </tr>
	</dsp:getvalueof>
      </dsp:oparam>

      <dsp:oparam name="empty">
        <table width="99%" align=center><tr>
        <td align=left><font class="small"><%= emptyText %></font></td>
        </tr></table>
      </dsp:oparam>

      <dsp:oparam name="error">
        <table width="99%" align=center><tr>
        <td align=left><font class="small"><%= genericError %></font></td>
        </tr></table>
      </dsp:oparam>

      <dsp:oparam name="outputStart">
        <%
        // If this parameter is called, then we know that there are documents.
        documentsExist = true;
        %>
        <table border="0" width="99%" align=center>
        <core:if value="<%= dexpage.getDisplayColumnHeaders() %>">
            <tr bgcolor="#<%= dexpage.getHighlightBGColor() %>">
              <core:if value="<%= dexpage.getDisplayTitle() %>"> 
                <td><font color ="#<%= dexpage.getHighlightTextColor() %>" 
		  class="small"><%= titleColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayAuthor() %>"> 
                <td><font color ="#<%= dexpage.getHighlightTextColor() %>" 
                   class="small"><%= authorColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayCreateDate() %>"> 
                <td><font color ="#<%= dexpage.getHighlightTextColor() %>"
		class="small"><%= createDateColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayStatus() %>"> 
                <td><font color ="#<%= dexpage.getHighlightTextColor() %>"
		class="small"><%= statusColumnHeader %></font></td>
              </core:if>
            </tr>
        </core:if>
      </dsp:oparam>

      <dsp:oparam name="outputEnd">
        </table>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<table width="99%" align=center>
  <tr>
    <td width="100%" align="left"><font class="small_bold">
      <core:if value="<%= documentsExist %>"><%-- don't display unless there's docs to display. --%>
        <core:createUrl id="fullGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
          <core:urlParam param="paf_dm" value="full"/>
          <core:urlParam param="paf_gm" value="content"/>
          <core:urlParam param="dexmode" value="list"/>
          <core:urlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
	  <%-- a - in front of the property name requests a reverse sort --%>
          <paf:encodeUrlParam param="sortProperty" value='<%= "-" + dexpage.getCreateDatePropertyName() %>'/>
          <a href="<%= fullGearUrl.getNewUrl() %>" class="gear_nav"><%= allItemsLinkText %></a>
        </core:createUrl> 
      </core:if>
      <dsp:droplet name="/atg/portal/gear/docexch/PermissionsDroplet">
        <dsp:param name="pafEnv" value="<%= gearEnv %>"/>
        <dsp:oparam name="mayWrite"><%-- 
           If documents exist (and mayWrite is assumed here), then display pipe. 
          --%><core:if value="<%= documentsExist %>">|</core:if>
          <font class="small_bold">
          <core:createUrl id="uploadGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
            <core:urlParam param="paf_dm" value="full"/>
            <core:urlParam param="paf_gm" value="content"/>
            <core:urlParam param="dexmode" value="upload"/>
            <core:urlParam param="prevpaf_dm" value="shared"/>
            <core:urlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
            <a href="<%= uploadGearUrl.getNewUrl() %>" class="gear_nav"><%= newItemLinkText %></a>
        </core:createUrl></font>
        </dsp:oparam>
      </dsp:droplet>
      &nbsp;
    </td>
  </tr>
</table>

</core:demarcateTransaction>


</dex:DocExchPage>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/DocExchShared.jsp#2 $$Change: 651448 $--%>
