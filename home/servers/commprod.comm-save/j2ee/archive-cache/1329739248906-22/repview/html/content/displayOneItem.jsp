<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Repository View Gear
   gearmode = content 
   displaymode = full
  
   This page displays the properties of a single item. 
   It is included by RepViewFull.jsp when the value of
   rpvmode=oneItem.  
   
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">


<%
  String repPath = rpvPage.getRepositoryPath();
  String origItemDescName = rpvPage.getItemDescriptorName();
  String resourceBundleName = rpvPage.getResourceBundle();
%>

<i18n:bundle baseName="<%= resourceBundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />

<core:urlParamValue id="itemid" param="itemId">
<core:urlParamValue id="currentItemDescName" param="itemdesc">

<%
  //The customItemDisplayPage is only useful for the main item type used
  //by this gear.  If the current item type does not match the main item type,
  //then we set the customItemDisplayPage to null so that the default display
  //will be used. 
  String customItemDisplayPage = null;
  if (currentItemDescName == null || 
      currentItemDescName.getValue().equals(origItemDescName))
    customItemDisplayPage = rpvPage.getCustomItemDisplayPage();
%>

<rpv:getItem id="itemGetter"
             repositoryPath="<%= repPath %>"
             itemDescriptorName="<%= currentItemDescName.getValue() %>"
             itemId="<%= itemid.getValue() %>">

    <core:cast id="itemToDisplay" 
               value="<%= itemGetter.getItem() %>" 
               castClass="atg.repository.RepositoryItem">	



	<core:exclusiveIf>
	    <core:ifNotNull value="<%= customItemDisplayPage %>">
		<dsp:include page="<%= customItemDisplayPage %>" flush="false">
		  <dsp:param name="itemToDisplay" value="<%= itemToDisplay %>"/>
                </dsp:include>
	    </core:ifNotNull>
	    <core:defaultCase>

                <table>
                  <tr>
                    <td colspan=2 align="left"><font class="large_bold">	 
           	    <%= itemToDisplay.getItemDisplayName() %>	
                    </font></td>
                  </tr>    
           
                  <rpv:propertyList id="proplist"
                                    displayType="oneItem"
                                    item="<%= itemToDisplay %>"
                                    itemDescriptorName="<%= currentItemDescName.getValue() %>" 
                                    gearEnv="<%= gearEnv %>">
                     <core:forEach id="properties"
                                   values="<%= proplist.getDisplayProperties() %>"
                                   castClass="atg.beans.DynamicPropertyDescriptor"
                                   elementId="prop">
                       <% String propToDisplay = proplist.getDisplayPropertyNames()[properties.getIndex()]; %>
                       <tr>
                         <td align="right" valign="top"><font class="small"><%= prop.getDisplayName() 
				%><i18n:message key="keyValSeparator">:</i18n:message></td>
                         <td valign="top"><font class="small"><%@ include file="renderProperty.jspf" %></font></td>
                       </tr>
                     </core:forEach>
                   </rpv:propertyList>
           
                </table>

	    </core:defaultCase>
	</core:exclusiveIf>

    </core:cast>

</rpv:getItem>

</core:urlParamValue>
</core:urlParamValue>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/displayOneItem.jsp#2 $$Change: 651448 $--%>
