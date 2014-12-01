<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>

<paf:hasCommunityRole roles="leader,gear-manager">
<dsp:page>

   <core:ExclusiveIf>

    <core:DefaultCase>
	<jsp:include page="/admin/configDisplayText.jsp" flush="true" />
    </core:DefaultCase>
<%--
    <core:DefaultCase>
      <jsp:include page="/admin/displayInstanceConfig.jsp" flush="true" />
    </core:DefaultCase>
--%> 
  </core:ExclusiveIf>



</dsp:page>
</paf:hasCommunityRole>
<%-- @version $Id: //app/portal/version/10.0.3/user_registration/user_registration.war/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
