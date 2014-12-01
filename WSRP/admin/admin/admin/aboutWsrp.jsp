<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

	<fmt:setBundle var="producerAdminbundle" basename="atg.wsrp.producer.admin.Resources" />
    		
	   <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
	        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
	            <font class="pageheader">
                <fmt:message key="admin-about-wsrp-header" bundle="${producerAdminbundle}"/>
	            </font>
			</td></tr></table>
	    </td></tr></table>

    	<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
	        <font class="smaller">
            <fmt:message key="admin-helpertext-wsrpA" bundle="${producerAdminbundle}"/><br>
            <fmt:message key="admin-helpertext-wsrpB" bundle="${producerAdminbundle}"/>
            <fmt:message key="admin-helpertext-wsrpC" bundle="${producerAdminbundle}"/>
	        </font>
        </td></tr></table>
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/aboutWsrp.jsp#2 $$Change: 651448 $--%>
