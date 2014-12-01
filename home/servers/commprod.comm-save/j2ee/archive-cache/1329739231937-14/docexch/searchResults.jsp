<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/docexch-taglib" prefix="dex" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:importbean bean="/atg/portal/gear/docexch/SearchFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/portal/gear/docexch/PermissionsDroplet"/>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<dex:DocExchPage id="dexpage" gearEnv="<%= gearEnv %>">



<%
  String highlightTextColor = dexpage.getHighlightTextColor();
  String highlightBGColor = dexpage.getHighlightBGColor();
  String bundleName = gearEnv.getGearInstanceParameter("resourceBundle");
%>
<i18n:bundle baseName="<%= bundleName %>" localeAttribute="userLocale" changeResponseLocale="false" />


<%-- check read permission and if search is enabled before displaying results of search --%>	
<core:if value="<%= dexpage.getEnableSearch() %>"> 

      <i18n:message id="searchButton" key="searchButton"/>
      <i18n:message id="titleColumnHeader" key="titleColumnHeader"/>
      <i18n:message id="authorColumnHeader" key="authorColumnHeader"/>
      <i18n:message id="createDateColumnHeader" key="createDateColumnHeader"/>
      <i18n:message id="statusColumnHeader" key="statusColumnHeader"/>
      <i18n:message id="noItemsFound" key="noItemsFound"/>
      <i18n:message id="configColumnHeadersPageTitle" key="configColumnHeadersPageTitle"> 
        <i18n:messageArg value="<%= gearEnv.getGear().getName(response.getLocale()) %>"/>
      </i18n:message>
      
      <%@ include file="searchForm.jspf" %>
      
      <!-- display errors if any -->
      <dsp:droplet name="/atg/dynamo/droplet/Switch">
         <dsp:param name="value" bean="SearchFormHandler.formError" />
         <dsp:oparam name="true">
            <dsp:droplet name="ForEach">
               <dsp:param name="array" bean="SearchFormHandler.formExceptions"/>
               <dsp:oparam name="output">
                  <dsp:getvalueof id="errorMsg" idtype="java.lang.String" param="element">
                  <font class="error"><img src='<%= dexpage.getRelativeUrl("/images/info.gif")%>'
                  >&nbsp;&nbsp;<i18n:message key="<%= errorMsg %>"/></font><br/>
                  </dsp:getvalueof>
               </dsp:oparam>
            </dsp:droplet>
         </dsp:oparam>
      </dsp:droplet>
      
      <p>
      
      <dsp:droplet name="/atg/dynamo/droplet/ForEach">
        <dsp:param name="array" bean="SearchFormHandler.searchResults"/>
        <dsp:oparam name="outputStart">
          <table border="0" width="99%" align="center">
          <core:if value="<%= dexpage.getDisplayColumnHeaders() %>">
            <tr bgcolor="#<%= highlightBGColor %>">
              <core:if value="<%= dexpage.getDisplayTitle() %>"> 
                <td><font id="<%= gearEnv.getGear().getId() %>" color ="#<%= highlightTextColor %>" class="small"><%= titleColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayAuthor() %>"> 
                <td><font id="<%= gearEnv.getGear().getId() %>" color ="#<%= highlightTextColor %>" class="small"><%= authorColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayCreateDate() %>"> 
                <td><font id="<%= gearEnv.getGear().getId() %>" color ="#<%= highlightTextColor %>" class="small"><%= createDateColumnHeader %></font></td>
              </core:if>
              <core:if value="<%= dexpage.getDisplayStatus() %>"> 
                <td><font id="<%= gearEnv.getGear().getId() %>" color ="#<%= highlightTextColor %>" class="small"><%= statusColumnHeader %></font></td>
              </core:if>
            </tr>
          </core:if>
        </dsp:oparam>
      
        <dsp:oparam name="outputEnd">
          </table>
        </dsp:oparam>
        
        <dsp:oparam name="output">
          <dsp:getvalueof id="docElement" param="element">
          <tr>
            <core:if value="<%= dexpage.getDisplayTitle() %>"> 
              <td><font class="small">
                <%-- view details link --%>
                <core:CreateUrl id="editDocUrl" url="<%= gearEnv.getOriginalRequestURI() %>">
                  <core:UrlParam param="prevpaf_dm" value="full"/>
                  <core:UrlParam param="paf_dm" value="full"/>
                  <core:UrlParam param="paf_gm" value="content"/>
                  <core:UrlParam param="dexmode" value="oneItem"/>
                  <core:UrlParam param="paf_gear_id" value="<%= gearEnv.getGear().getId() %>"/>
                  <core:UrlParam param="documentid" value="<%= dexpage.getDocumentId(docElement) %>"/>
                   <a href="<%= editDocUrl.getNewUrl() 
                    %>" class="gear_content"><%= dexpage.getTitle(docElement) %></a>
                </core:CreateUrl>
              </font></td>
            </core:if>
            <core:if value="<%= dexpage.getDisplayAuthor() %>"> 
              <td valign=top>
                <font class="small"><%= dexpage.getAuthorFirstName(docElement) %>
                <%= dexpage.getAuthorLastName(docElement) %></font>
              </td>
            </core:if>
            <core:if value="<%= dexpage.getDisplayCreateDate() %>"> 
              <td valign=top><font class="small">
                <core:ifNot value='<%= dexpage.getDateStyle().equals("notShown") %>'>
                  <i18n:formatDate value="<%= dexpage.getCreateDate(docElement) %>"
                                   style="<%= dexpage.getDateStyle() %>"/>
                </core:ifNot>
                <core:ifNot value='<%= dexpage.getTimeStyle().equals("notShown") %>'>
                  <i18n:formatTime value="<%= dexpage.getCreateDate(docElement) %>"
                                   style="<%= dexpage.getTimeStyle() %>"/>
                </core:ifNot>
              </font></td>
            </core:if>
            <core:if value="<%= dexpage.getDisplayStatus() %>"> 
              <td valign=top><font class="small">
                <dsp:valueof param="<%= dexpage.getStatus(docElement) %>"></dsp:valueof>
              </font></td>
            </core:if>
          </tr>
	  </dsp:getvalueof><%-- end docElement --%>
        </dsp:oparam>
      
        <dsp:oparam name="empty"><%= noItemsFound %></dsp:oparam>
      </dsp:droplet>
      <%-- End Display Search Results --%>

</core:if> <%--end enableSearch --%>
</dex:DocExchPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/searchResults.jsp#2 $$Change: 651448 $--%>
