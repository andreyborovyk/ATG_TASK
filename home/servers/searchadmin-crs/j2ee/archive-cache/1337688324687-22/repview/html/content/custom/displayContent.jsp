
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Repository View Gear
   gearmode = content 
   displaymode = full
  
   This is an example of customized featured item display.  This page will 
   be included by RepViewShared.jsp when the gear instance parameter 
   customFeaturedItemDisplayPage is /html/content/custom/displayContent.jsp
   and when displayFeaturedItem is true.

   You can create your own customized featued item display by adding pages in this
   directory and setting the customFeaturedItemDisplayPage parameter appropriately.
    
   To create the page replace the code withing the CUSTOM ITEM DISPLAY code block. 
   The parameter itemToDisplay contains the repository item for you to display 
   
--%>

<paf:includeOnly/>
<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">



<%-- ++++++++++++++++++++++++++++++ --%>
<%-- BEGIN CUSTOM ITEM DISPLAY CODE --%> 
<%-- ++++++++++++++++++++++++++++++ --%>

<dsp:getvalueof id="contentItem" param="itemToDisplay" idtype="atg.repository.RepositoryItem">

<%
  // get the values of the properties that we'd like to display  
  String url = (String)contentItem.getPropertyValue("URL");
%>
<core:exclusiveIf>
   <core:ifNull value="<%= url %>">
     <%= (String)contentItem.getPropertyValue("data") %>
   </core:ifNull>

   <core:defaultCase>
      <img src='<%= url %>'/>
   </core:defaultCase>
</core:exclusiveIf>

<P>

<%-- ++++++++++++++++++++++++++++ --%>
<%-- END CUSTOM ITEM DISPLAY CODE --%> 
<%-- ++++++++++++++++++++++++++++ --%>



</dsp:getvalueof>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/custom/displayContent.jsp#2 $$Change: 651448 $--%>
