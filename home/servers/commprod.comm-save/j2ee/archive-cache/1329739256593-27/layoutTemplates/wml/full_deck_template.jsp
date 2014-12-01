<%@ page contentType="text/vnd.wap.wml" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<?xml version="1.0"?>
<!DOCTYPE wml PUBLIC "-//WAPFORUM//DTD WML 1.1//EN"
			"http://www.wapforum.org/DTD/wml_1.1.xml">

<dsp:page>
<paf:InitializeEnvironment id="pafEnv">


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

<wml>

  <template> 
    <do type="prev" label="Prev">
      <prev/>
    </do>
  </template>

  <%-- Render Gear Contents --%>
  <paf:PrepareGearRenderers id="gearRenderers">

    <paf:GetGearMode id="gearMode" defaultGearMode="content">
      <paf:PrepareGearRenderer 	gearRenderers="<%= gearRenderers.getGearRenderers() %>"
	                     	gear="<%= pafEnv.getGear() %>"
                       		gearMode="<%= gearMode %>" />
    </paf:GetGearMode>
    <paf:RenderPreparedGear 	gear="<%= pafEnv.getGear() %>"
    	                   	gearRenderers="<%= gearRenderers.getGearRenderers() %>" />

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


</paf:InitializeEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/templates/layoutTemplates.war/web/wml/full_deck_template.jsp#2 $$Change: 651448 $--%>
