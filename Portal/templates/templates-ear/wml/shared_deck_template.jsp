<%@ page buffer="64kb" %>

<%@ page contentType="text/vnd.wap.wml" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>

<?xml version="1.0"?> 
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN" "http://www.wapforum.org/DTD/wml_1.1.xml"> 
 
<!-- If WML 1.2 or 1.3 features are required, then use one of the following DOCTYPE instead: 
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.2//EN" "http://www.wapforum.org/DTD/wml12.dtd"> 
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.3//EN" "http://www.wapforum.org/DTD/wml13.dtd"> 
  --> 

<paf:InitializeEnvironment id="pafEnv"> 

<dsp:page>

<core:transactionStatus id="beginXAStatus">
  <core:if value="<%= beginXAStatus.isNoTransaction() %>">
    <core:beginTransaction id="beginSharedPageXA">
      <core:ifNot value="<%= beginSharedPageXA.isSuccess() %>">
        <paf:log message="Error: could not create transaction"
                 throwable="<%= beginSharedPageXA.getException() %>"/>
      </core:ifNot>
    </core:beginTransaction>
  </core:if>
</core:transactionStatus>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<wml> 
 
  <card id="init" title="<%= atg.servlet.ServletUtil.escapeHtmlString(pafEnv.getCommunity().getName()) %>">
    <onevent type="ontimer">
      <go href="#page<%= pafEnv.getPage().getId() %>"/>
    </onevent>
    <timer value="25"/>
 
   <p align="center">
      <big><b><%= atg.servlet.ServletUtil.escapeHtmlString(pafEnv.getCommunity().getName()) %></b></big><br/>
      <b><%= atg.servlet.ServletUtil.escapeHtmlString(pafEnv.getPage().getName()) %></b>
    </p>

  </card>

  <%-- Render Toplevel Page --%>
  <card id="page<%= pafEnv.getPage().getId() %>" title="<%= atg.servlet.ServletUtil.escapeHtmlString(pafEnv.getPage().getName()) %>"> 
 
      <%-- Render Authentication --%>
      <p>
        <core:exclusiveIf>
          <core:IfNot value="<%= pafEnv.isRegisteredUser() %>">

            <core:CreateUrl id="loginSuccessURL" url='<%= pafEnv.getOriginalRequestURI() + "#page" + pafEnv.getPage().getId() %>'>
             <core:CreateUrl id="loginURL" url="<%= pafEnv.getLoginURI(false) %>">
               <core:UrlParam param="successURL" value="<%= atg.servlet.ServletUtil.escapeURLString( loginSuccessURL.getNewUrl() ) %>"/>        
                 <a href="<%= loginURL.getNewUrl() %>">Log In</a><br/>

             </core:CreateUrl>
           </core:CreateUrl>
             
          </core:IfNot>
          <core:DefaultCase>
             
            <core:CreateUrl id="logoutURL" url="<%= pafEnv.getLogoutURI() %>">
              <paf:encodeUrlParam param="successURL" value="<%= pafEnv.getCommunityURI(pafEnv.getCommunity()) %>"/>
           
                <a href="<%= logoutURL.getNewUrl() %>">Log Out</a><br/>

            </core:CreateUrl>
           
          </core:DefaultCase>
        </core:exclusiveIf>
      </p>

						
      <%-- Render Gear Titles --%>
      <p>
          <core:ForEach	id="regionsForEach"
			values="<%= pafEnv.getPage().getRegions()%>"
			castClass="atg.portal.framework.Region"
			keyCastClass="java.lang.String"
			elementId="region"
			keyId="regionName">

            <paf:GetGears 	id="gears"
				regionName="<%= regionName %>"
				page="<%= pafEnv.getPage() %>">  
              <core:ForEach	id="gearsForEach"
				values="<%= gears.getGears() %>"
				castClass="atg.portal.framework.Gear"
				elementId="gear">
		
			<a href="#gear<%= gear.getId() %>"><%= atg.servlet.ServletUtil.escapeHtmlString(gear.getName()) %></a><br/>

              </core:ForEach>
            </paf:GetGears>
          </core:ForEach>
      </p>
    </card>

    <%-- Render Gears --%>
    <paf:PrepareGearRenderers id="gearRenderers">
      <core:ForEach	id="regionsForEach"
			values="<%= pafEnv.getPage().getRegions()%>"
			castClass="atg.portal.framework.Region"
			keyCastClass="java.lang.String"
			elementId="region"
			keyId="regionName">
        <paf:GetGears 	id="gears" 
			regionName="<%= regionName %>" 
			page="<%= pafEnv.getPage() %>">	
	
          <core:ForEach 	id="gearsForEach"
				values="<%= gears.getGears() %>"
				castClass="atg.portal.framework.Gear"
				elementId="gear">
            <paf:GetGearMode 	id="gearMode"
			 	defaultGearMode="content"
				onlyForGear="<%= gear %>">	

              <%-- Prepare the GearRenderer, register it with the GearRenderers --%>
	      <paf:PrepareGearRenderer 	gearRenderers="<%= gearRenderers.getGearRenderers() %>"
					gear="<%= gear %>"
					displayMode="shared"
					gearMode="<%= gearMode %>"
					regionName="<%= regionName %>" />
            </paf:GetGearMode>
          </core:ForEach>

          <core:ForEach 	id="gearsForEach"
				values="<%= gears.getGears() %>"
				castClass="atg.portal.framework.Gear"
				elementId="gear"> 

            <card id="gear<%= gear.getId() %>" title="<%= atg.servlet.ServletUtil.escapeHtmlString(gear.getName()) %>">	
	      <do type="prev" label="Prev">
                <prev/>
              </do>
										
              <%-- Render Gear Contents --%>
              <paf:RenderPreparedGear	gear="<%= gear %>"
					gearRenderers="<%= gearRenderers.getGearRenderers() %>" />
              
						
            </card>

          </core:ForEach>
        </paf:GetGears>
      </core:ForEach>
    </paf:PrepareGearRenderers>


</wml>

<core:transactionStatus id="sharedPageXAStatus">
  <core:exclusiveIf>
    <%-- if we couldn't get the transaction status successfully, then rollback --%>
    <core:ifNot value="<%= sharedPageXAStatus.isSuccess() %>">
      <core:rollbackTransaction id="failedXAStatusRollback"/>
    </core:ifNot>

    <%-- if the transaction is marked for rollback, then roll it back --%>
    <core:if value="<%= sharedPageXAStatus.isMarkedRollback() %>">
      <core:rollbackTransaction id="sharedPageRollbackXA">
        <core:ifNot value="<%= sharedPageRollbackXA.isSuccess() %>">
          <paf:log message="Error: could not rollback transaction"
                   throwable="<%= sharedPageRollbackXA.getException() %>"/>
	</core:ifNot>
      </core:rollbackTransaction>
    </core:if>

    <%-- if the transaction is marked as active, then commit it. if that fails, then rollback --%>
    <core:if value="<%= sharedPageXAStatus.isActive() %>">
      <core:commitTransaction id="sharedPageCommitXA">
        <core:ifNot value="<%= sharedPageCommitXA.isSuccess() %>">
          <paf:log message="Error: could not commit transaction"
                   throwable="<%= sharedPageCommitXA.getException() %>"/>
	  <core:rollbackTransaction id="secondTryRollbackXA"/>
	</core:ifNot>
      </core:commitTransaction>
    </core:if>    
  </core:exclusiveIf>
</core:transactionStatus>

</dsp:page>

</paf:InitializeEnvironment>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/wml/shared_deck_template.jsp#2 $$Change: 651448 $--%>
