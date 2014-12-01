<%@ page language="java" %>
<%
/*<ATGCOPYRIGHT>
 
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/
 
 /** 
  * Dispatcher for handling different instance configuration tasks
  **/
 
%> 
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>

<dsp:page>
                        
<core:IfNotNull value='<%= request.getParameter("config_page")%>'>

   <core:ExclusiveIf>

      <core:If value='<%= request.getParameter("config_page").equals("DisplayText")%>'>
	<jsp:include page="/admin/editInstanceConfig.jsp" flush="true" />
      </core:If>
      <core:If value='<%= request.getParameter("config_page").equals("DefaultConfig")%>'>
	<jsp:include page="/admin/editDefaults.jsp" flush="true" />
      </core:If>

      <core:DefaultCase>
        <jsp:include page="/admin/displayInstanceConfig.jsp" flush="true" />
      </core:DefaultCase>
 
  </core:ExclusiveIf>

</core:IfNotNull>

<core:IfNull value='<%= request.getParameter("config_page")%>'>
      <jsp:include page="/admin/displayInstanceConfig.jsp" flush="true" />
</core:IfNull>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/admin/instanceConfig.jsp#2 $$Change: 651448 $--%>
