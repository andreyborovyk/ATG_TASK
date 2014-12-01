<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/targeting/TargetingRandom"/>
<dsp:importbean bean="/atg/targeting/TargetingFirst"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>


<%--
   Repository View Gear
   gearmode = content 
   displaymode = shared
  
   This page displays a short list of repository items
   or a "featured item" or both according to the gear 
   configuration.   
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">
<i18n:bundle baseName="<%= rpvPage.getResourceBundle() %>" localeAttribute="userLocale" changeResponseLocale="false" />
	



<%-- check that configuration is complete before continuing --%>
<core:exclusiveIf>
  <core:if value="<%= rpvPage.isConfigComplete() %>">


<%
  String highlightBGColor = rpvPage.getHighlightBGColor();
  String highlightTextColor = rpvPage.getHighlightTextColor();
  String repPath = rpvPage.getRepositoryPath();
  String itemDescName = rpvPage.getItemDescriptorName();
  String resourceBundleName = rpvPage.getResourceBundle();
  String displayShortListStr = rpvPage.getDisplayShortList();
  String displayFeaturedItem = rpvPage.getDisplayFeaturedItem();
  String displayMoreItemsLinkStr = rpvPage.getDisplayMoreItemsLink();
  String customFeaturedItemDisplayPage = rpvPage.getCustomFeaturedItemDisplayPage();

  String shortListTargeter = rpvPage.getShortListTargeter();
  String shortListSize = rpvPage.getShortListSize();

  String featuredItemTargeter = rpvPage.getFeaturedItemTargeter();

  boolean displayMoreItemsLink = Boolean.valueOf(displayMoreItemsLinkStr).booleanValue();
  boolean displayShortList = Boolean.valueOf(displayShortListStr).booleanValue();
  boolean itemsExist = false;
%>


  <%-- BEGIN FEATURED ITEM DISPLAY --%>
  <core:if value="<%= displayFeaturedItem %>">

             <dsp:droplet name="TargetingRandom">
               <dsp:param name="targeter" bean="<%= featuredItemTargeter %>"/>
               <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
               <%@ include file="featuredItem.jspf"%>
             </dsp:droplet>

  </core:if>
  <%-- END FEATURED ITEM DISPLAY --%>

  <%-- BEGIN SHORT LIST DISPLAY --%>
  <core:if value="<%= displayShortList %>">

    <dsp:droplet name="TargetingFirst">
      <dsp:param name="targeter" bean="<%= shortListTargeter %>"/>
      <dsp:param name="howMany" value="<%= shortListSize %>"/>
      <dsp:param name="transactionManager" bean="/atg/dynamo/transaction/TransactionManager"/>
      <dsp:param name="sortProperties" value="<%= rpvPage.getShortListSortProperty() %>"/>
      <dsp:param name="reverseOrder" value="<%= rpvPage.getShortListReverseSort() %>"/>
      <dsp:oparam name="outputStart">
        <%
        // If this parameter is called, then we know that there are items.
        itemsExist = true;
        %>
        <%-- Begin table header --%>
        <table border=0 width="100%">
          <core:if value="<%= rpvPage.getDisplayColumnHeaders() %>">
	  <tr>
            <core:if value="<%= rpvPage.getDisplayMainItemLink() %>">
	      <td bgcolor="<%= highlightBGColor %>">&nbsp;</td>
            </core:if>
            <rpv:propertyList id="proplist"
                              itemDescriptorName="<%= itemDescName %>"
                              displayType="shortlist"
                              gearEnv="<%= gearEnv %>">
              <core:forEach id="columnHeaders" 
                            values="<%= proplist.getDisplayProperties() %>" 
                            castClass="atg.beans.DynamicPropertyDescriptor" 
                            elementId="prop">
                <td bgcolor="<%= highlightBGColor %>"><nobr><font class="small" color="<%= highlightTextColor %>"><%= prop.getDisplayName() %></font><nobr></td>
	      </core:forEach>
            </rpv:propertyList>
          </tr>
          </core:if>
      </dsp:oparam>

      <dsp:oparam name="output">
	<dsp:getvalueof id="itemToDisplay" idtype="atg.repository.RepositoryItem" param="element">
        <tr>
          <core:if value="<%= rpvPage.getDisplayMainItemLink() %>">
          <td valign="top">
          <core:CreateUrl id="oneItemGearUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
            <core:UrlParam param="paf_dm" value="full"/>
            <core:UrlParam param="rpvmode" value="oneItem"/>
            <core:UrlParam param="itemId" value="<%= itemToDisplay.getRepositoryId() %>"/>
            <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
            <core:UrlParam param="reppath" value="<%= repPath %>"/>
            <core:UrlParam param="itemdesc" value="<%= itemDescName %>"/>
            <a href="<%= oneItemGearUrl.getNewUrl() %>" class="gear_content"><font class="small"><%= itemToDisplay.getItemDisplayName() %></a>
          </core:CreateUrl>
          </td>
          </core:if>
            <rpv:propertyList id="proplist"
                              itemDescriptorName="<%= itemDescName %>"
                              displayType="shortlist"
                              gearEnv="<%= gearEnv %>">
              <core:forEach id="properties" 
                            values="<%= proplist.getDisplayPropertyNames() %>"
                            castClass="java.lang.String"
                            elementId="propToDisplay">
		  <td valign="top"><font class="small"><%@ include file="renderProperty.jspf" %></font></td>
	       </core:forEach>
            </rpv:propertyList>
        </tr>
	</dsp:getvalueof>
      </dsp:oparam>

      <dsp:oparam name="empty">
        <i18n:message key="emptyText">
          <i18n:messageArg value="ShortList"/>
        </i18n:message></br>
      </dsp:oparam>

      <dsp:oparam name="error">
        <i18n:message key="errorText">
          <i18n:messageArg value="ShortList"/>
        </i18n:message></br>
      </dsp:oparam>

      <dsp:oparam name="outputEnd">
        </table>
      </dsp:oparam>	  
    </dsp:droplet><%-- end TargetingFirst --%>

  </core:if>
  <%-- END SHORT LIST DISPLAY --%>

  <%-- BEGIN MORE ITEMS LINK --%>
  <core:if value="<%= displayMoreItemsLink && (itemsExist || !displayShortList) %>">
     <core:createUrl id="moreItemsUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
       <core:UrlParam param="paf_dm" value="full"/>
       <core:UrlParam param="paf_gm" value="content"/>
       <core:UrlParam param="rpvmode" value="list"/>
       <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
       <a href="<%= moreItemsUrl.getNewUrl() %>" class="gear_content"><i18n:message key="moreItemsLink"/></a>
     </core:createUrl>
  </core:if>
  <%-- MORE ITEMS LINK --%>

  </core:if>
  <core:defaultCase>
    <i18n:message key="configurationIncomplete"/>
  </core:defaultCase>
</core:exclusiveIf>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/RepViewShared.jsp#2 $$Change: 651448 $--%>
