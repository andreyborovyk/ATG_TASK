<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/targeting/TargetingRange"/>

<%--
   Repository View Gear
   gearmode = content
   displaymode = full

   This fragment is included by RepViewFull when the value
   of request parameter rpvmode=list.  It displays a pagable
   sortable list of items from the repository. 
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">

<%
  String highlightBGColor = rpvPage.getHighlightBGColor();
  String highlightTextColor = rpvPage.getHighlightTextColor();
  String repPath = rpvPage.getRepositoryPath();
  String itemDescName = rpvPage.getItemDescriptorName();
  String fullListTargeter = rpvPage.getFullListTargeter();
  String fullListSize = rpvPage.getFullListSize();
  String resourceBundleName = rpvPage.getResourceBundle();
  String upArrow = rpvPage.getRelativeUrl("/images/up_arrow.gif");
  String downArrow = rpvPage.getRelativeUrl("/images/down_arrow.gif");

  boolean itemsExist = false;
%>
<i18n:bundle baseName="<%= resourceBundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />


<%-- Begin table header --%>
<table border="0" cellpadding="1" cellspacing="1" width="100%">
  <tr>
    <td width="90%" align=center><font class="large_bold"><i18n:message key="allItemsLinkText"/></font></td>
  </tr>
</table>

<core:urlParamValue id="sortPropertyParam" param="sortProperty">
<core:urlParamValue id="startParam" param="start">

      <dsp:droplet name="TargetingRange">
        <dsp:param name="targeter" bean="<%= fullListTargeter %>"/>
        <dsp:param name="sortProperties" value="<%= sortPropertyParam.getValue() %>"/>
        <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
        <dsp:param name="howMany" value="<%= fullListSize %>"/>
	<dsp:param name="start" value="<%= startParam.getValue() %>"/>
  	<dsp:oparam name="outputStart">

          <%
          // If this parameter is called, then we know that there are items.
          itemsExist = true;
          %>
          <table border="0" cellpadding="1" cellspacing="1" width="100%">
            <tr>
              <td nowrap><font class="small">
              <%@ include file="prevNext.jspf" %>
              </font></td>
             </tr>
           </table>

          <table border="0" cellpadding="1" cellspacing="1" width="100%">
          <core:if value="<%= rpvPage.getDisplayColumnHeaders() %>">
	  <tr>
            <core:if value="<%= rpvPage.getDisplayMainItemLink() %>">
	      <td bgcolor="<%= highlightBGColor %>">&nbsp;</td>
            </core:if>
            <rpv:propertyList id="proplist"
                              itemDescriptorName="<%= itemDescName %>"
                              displayType="fulllist"
                              gearEnv="<%= gearEnv %>">
              <core:forEach id="columnHeaders" 
                            values="<%= proplist.getDisplayProperties() %>" 
                            castClass="atg.beans.DynamicPropertyDescriptor" 
                            elementId="prop">
                <td bgcolor="<%= highlightBGColor %>" valign="center"><nobr>
<%-- 
REMOVE SORTING BECAUSE DYNAMIC SORTING DOESN'T WORK AS EXPECTED WITH TARGETING
                  <core:ifNot value="<%= prop.isMultiValued() %>">
                    <core:CreateUrl id="sortUpUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="rpvmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                      <paf:encodeUrlParam param="sortProperty" value='<%= "+" + proplist.getDisplayPropertyNames()[columnHeaders.getIndex()] %>'/>
                      <core:UrlParam param="reverseSort" value="false"/>
                      <a href="<%= sortUpUrl.getNewUrl() %>" class="gear_content"><img src="<%= upArrow %>" border=0></a>
                    </core:CreateUrl>
                  </core:ifNot>
--%>
                  <font class="small" color="<%= highlightTextColor %>">
                    <%= prop.getDisplayName() %>
                  </font>                
<%-- 
REMOVE SORTING BECAUSE DYNAMIC SORTING DOESN'T WORK AS EXPECTED WITH TARGETING
                  <core:ifNot value="<%= prop.isMultiValued() %>">
                    <core:CreateUrl id="sortDownUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
                      <core:UrlParam param="paf_dm" value="full"/>
                      <core:UrlParam param="paf_gm" value="content"/>
                      <core:UrlParam param="rpvmode" value="list"/>
                      <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                      <paf:encodeUrlParam param="sortProperty" value='<%= "-" + proplist.getDisplayPropertyNames()[columnHeaders.getIndex()] %>'/>
                      <core:UrlParam param="reverseSort" value="true"/>
                      <a href="<%= sortDownUrl.getNewUrl() %>" class="gear_content"><img src="<%= downArrow %>" border=0></a>
                    </core:CreateUrl>
                  </core:ifNot>
--%> 
                </nobr></td>
	      </core:forEach>
            </rpv:propertyList>
          </tr>
         </core:if>
      </dsp:oparam>

      <dsp:oparam name="output">
	<dsp:getvalueof id="itemToDisplay" idtype="atg.repository.RepositoryItem" param="element">
        <tr>
          <core:if value="<%= rpvPage.getDisplayMainItemLink() %>">
          <td>
          <core:CreateUrl id="oneItemGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="rpvmode" value="oneItem"/>
            <core:UrlParam param="itemId" value="<%= itemToDisplay.getRepositoryId() %>"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
            <core:UrlParam param="reppath" value="<%= repPath %>"/>
            <core:UrlParam param="itemdesc" value="<%= itemDescName %>"/>
            <a href="<%= oneItemGearUrl.getNewUrl() %>" class="gear_content"><font class="small"><%= itemToDisplay.getItemDisplayName() %></font></a>
          </core:CreateUrl>
          </td>
          </core:if>
            <rpv:propertyList id="proplist"
                              itemDescriptorName="<%= itemDescName %>"
                              displayType="fulllist"
                              gearEnv="<%= gearEnv %>">
              <core:forEach id="properties" 
                            values="<%= proplist.getDisplayPropertyNames() %>"
                            castClass="java.lang.String"
                            elementId="propToDisplay">
		  <td><font class="small"><%@ include file="renderProperty.jspf" %></font></td>
	       </core:forEach>
            </rpv:propertyList>
        </tr>
	</dsp:getvalueof>
      </dsp:oparam>

      <dsp:oparam name="empty">
          <table border="0" cellpadding="1" cellspacing="1" width="100%">
            <tr>
              <td nowrap><font class="small">
                <i18n:message key="emptyText">
                  <i18n:messageArg value="FullList"/>
                </i18n:message>
              </font></td>
             </tr>
           </table>
      </dsp:oparam>

      <dsp:oparam name="error">
          <table border="0" cellpadding="1" cellspacing="1" width="100%">
            <tr>
              <td nowrap><font class="small">
                <i18n:message key="errorText">
                  <i18n:messageArg value="FullList"/>
                </i18n:message>
              </font></td>
             </tr>
           </table>
      </dsp:oparam>

      <dsp:oparam name="outputEnd">
        </table>
      </dsp:oparam>	  


    </dsp:droplet>

</core:urlParamValue>
</core:urlParamValue>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/fullList.jsp#2 $$Change: 651448 $--%>
