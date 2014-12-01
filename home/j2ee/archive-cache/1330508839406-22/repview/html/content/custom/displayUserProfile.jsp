
<%@ taglib uri="/rpv" prefix="rpv" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%--
   Repository View Gear
   gearmode = content 
   displaymode = full
  
   This is an example of customized item display.  This page will be included 
   by displayOneItem.jsp when the gear instance parameter customItemDisplayPage 
   is /html/content/displayUserProfile.jspf.

   You can create your own customized item display by adding pages in this
   directory and setting the customItemDisplayPage parameter appropriately.
    
   To create the page replace the code withing the CUSTOM ITEM DISPLAY code block. 
   The parameter itemToDisplay contains the repository item for you to display 
   
--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">
<rpv:repViewPage id="rpvPage" gearEnv="<%= gearEnv %>">



<%-- ++++++++++++++++++++++++++++++ --%>
<%-- BEGIN CUSTOM ITEM DISPLAY CODE --%> 
<%-- ++++++++++++++++++++++++++++++ --%>

<dsp:getvalueof id="displayUser" param="itemToDisplay" idtype="atg.repository.RepositoryItem">

<%
  String firstName = (String)displayUser.getPropertyValue("firstName");
  String lastName = (String)displayUser.getPropertyValue("lastName");
  String email = (String)displayUser.getPropertyValue("email");
  atg.repository.RepositoryItem homeAddress = (atg.repository.RepositoryItem)displayUser.getPropertyValue("homeAddress");
  String city = (String)homeAddress.getPropertyValue("city");
  String state = (String)homeAddress.getPropertyValue("state");
  String postalCode = (String)homeAddress.getPropertyValue("postalCode");
  String address1 = (String)homeAddress.getPropertyValue("address1");
  String address2 = (String)homeAddress.getPropertyValue("address2");
  String phoneNumber = (String)homeAddress.getPropertyValue("phoneNumber");
  java.util.Collection roleCollection  = (java.util.Collection)displayUser.getPropertyValue("roles");
%>

      <P>
      <%= firstName %> <%= lastName %><br>
      <%= address1 %><br>
      <core:ifNotNull value="<%= address2 %>">
        <%= address2 %><br>
      </core:ifNotNull>
      <%= city %>, <%= state %> <%= postalCode %><br>
      <p>
      <core:ifNotNull value="<%= phoneNumber %>">
        <%= phoneNumber %><br>
      </core:ifNotNull>
      <core:ifNotNull value="<%= email %>">
        <%= email %><br>
      </core:ifNotNull>

      <p>
      <core:forEach id="roleforeach" 
                    values="<%= roleCollection %>" 
                    castClass="atg.repository.RepositoryItem"
                    elementId="role">
         <core:if value="<%= roleforeach.getIndex() != 0 %>">
	 , 
         </core:if>
	 <%= role.getPropertyValue("name") %>
      </core:forEach>
      <P>


<%-- ++++++++++++++++++++++++++++ --%>
<%-- END CUSTOM ITEM DISPLAY CODE --%> 
<%-- ++++++++++++++++++++++++++++ --%>



</dsp:getvalueof>

</rpv:repViewPage>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/repview/repview.war/html/content/custom/displayUserProfile.jsp#2 $$Change: 651448 $--%>
