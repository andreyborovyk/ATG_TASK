<%@ page import="java.io.*,java.util.*,atg.wsrp.producer.admin.WsrpProducerAdminFormHandler" errorPage="/error.jsp"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"  %>

<fmt:setBundle var="producerAdminbundle" basename="atg.wsrp.producer.admin.Resources" />

<dspel:demarcateTransaction id="demarcateXA">

<dspel:importbean bean="/atg/userprofiling/Profile"/>
<dspel:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dspel:importbean var="producerFormHandler" bean="/atg/wsrp/producer/admin/ProducerAdminFormHandler"/>

<html>
    <head>
        <title>
        <fmt:message key="title-admin-producer-admin" bundle="${producerAdminbundle}"/>
        </title>
        <link rel="STYLESHEET" type="text/css" href='<%= response.encodeURL("css/default.css") %>'>
    </head>

<!-- body tag is declared in header_main.jspf and is shared by all admin pages -->

<%
    String origURI=request.getRequestURI();
    String clearGif =  response.encodeURL("images/clear.gif");
    String mode          = "1";
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available portlets
    }
%>

    <%@ include file="nav_header_main.jspf"%>

    <br>
        <table width="98%" border="0" cellpadding="0" cellspacing="0">
        <tr>
            <td valign="top" width="20"><img src='<%=clearGif %>' height="1" width="20" alt=""></td>
            <td width="150" valign="top">
                <font class="smaller">
                <%@ include file="nav_sidebar.jspf"%><br>
                </font>
            </td>

            <td width="20"><img src='<%=clearGif %>' height="1" width="20" alt=""></td>
            <td valign="top" width="90%" align="left">

                <c:if test="${producerFormHandler.formError == true}">			
                <font class="error">
                <dspel:droplet name="ErrorMessageForEach">
                <dspel:param name="exceptions" bean="ProducerAdminFormHandler.formExceptions"/>
                <dspel:param name="resourceName" value="atg.wsrp.producer.admin.Resources"/>
                <dspel:oparam name="output">
                <img src='<%=response.encodeURL(request.getContextPath()+"/images/error.gif")%>'>&nbsp;&nbsp;
                <dspel:getvalueof param="message" var="errormsg"/>
                <c:out value="${errormsg}"/>
                </dspel:oparam>
                </dspel:droplet>
                </font>
		        </c:if>

		<c:set var="ViewMode" value= "${param['mode']}"/>

 <c:choose>

<%-- ********************************* PRODUCER OFFERED PORTLET******************************* --%>

    <c:when  test="${ViewMode == 1}">

            <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
            <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
                <font class="pageheader">
            	<fmt:message key="delete-portlet-definition-title" bundle="${producerAdminbundle}"/>
                </font>
            </td></tr></table>
            </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">
	    <fmt:message key="delete-portlet-definition-title-info" bundle="${producerAdminbundle}"/>
        </font>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" width="100%" bgcolor="#c9d1d7">
    <font class="smaller">
	    <img src="<%=clearGif%>" height="1" width="7" border="0"><br>

	        <table cellpadding="4" cellspacing="0" border="0" width="100%" bgcolor="#c9d1d7"><tr><td>

	        <!--this table generates the list-->
	        <table cellpadding="0" cellspacing="0" border="0">

            <c:url var="sucessURL" value="${origURI}">
            <c:param name="mode" value="1"/>
            </c:url>

            <dspel:setvalue bean="ProducerAdminFormHandler.successUrl" value="${sucessURL}"/>

            <dspel:getvalueof id="portlet" idtype="oasis.names.tc.wsrp.v1.types.PortletDescription[]" bean="ProducerAdminFormHandler.allProducerOfferedPortlets">
                    <c:set var="counterofPortletforDelete" value="0"/>
                    <c:forEach var="portletDefinitionDelete" items="${portlet}" varStatus="status">
                    <c:set var="counterofPortletforDelete" value="${counterofPortletforDelete+1}"/>
                    <c:if test="${status.index==0}">
                    </c:if>
                    <tr><td colspan="2"></td></tr>
                        <tr>
	                    <td nowrap width="250"><font class="smaller">&nbsp;&nbsp;
                        <c:out value="${portletDefinitionDelete.displayName.value}"/></font></td>
                        <c:url var="deletePortletDefUrl" value="/configProducer.jsp">
                            <c:param name="portletHandleID" value="${portletDefinitionDelete.portletHandle}"/>
                            <c:param name="portletDisplayName" value="${portletDefinitionDelete.displayName.value}"/>
                            <c:param name="mode" value="5"/>
		                </c:url>

                        <td nowrap width="75"><font class="smaller">&nbsp;&nbsp;
                        <dspel:a href="${deletePortletDefUrl}">
                            <fmt:message key="remote-delete-def" bundle="${producerAdminbundle}"/>
		                </dspel:a>
                        </font>
                        </td>

                        <c:url var="ViewPortletDefUrl" value="/configProducer.jsp">
	                        <c:param name="mode" value="6"/>
                        </c:url>
						<td nowrap width="75"><font class="smaller">&nbsp;&nbsp;
                        <dspel:a href="${ViewPortletDefUrl}">
		                    <fmt:message key="remote-view-def" bundle="${producerAdminbundle}"/></font>
                            <dspel:param name="portletHandleID" value="${portletDefinitionDelete.portletHandle}"/>
                            </td>
		                </dspel:a>
                      </tr>
                 </c:forEach>
                    <c:if test="${counterofPortletforDelete == 0}">
                    <font class="smaller"><fmt:message key="no-available-portlet-defination" bundle="${producerAdminbundle}"/></font>
                    </c:if>
	        </dspel:getvalueof>
          </table>
	   </td></tr></table></table>
    </c:when>

<%-- *****************************END OF PRODUCER OFFERED PORTLET**************************** --%>


<%-- *******************CHANGE  PRODUCER OFFERED PORTLET DEFINITION SUCESSS******************* --%>

    <c:when test="${ViewMode == 8}">

       <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
            <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
                <font class="pageheader">
	            <fmt:message key="portlet-definition-change-sucess" bundle="${producerAdminbundle}"/>
                </font>
            </td></tr></table>
       </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">
	    <fmt:message key="portlet-definition-change-sucess-info" bundle="${producerAdminbundle}"/>
        </font>
    </td>
    <td>
	<c:url var="ImportPortletDefUrl" value="/configProducer.jsp">
		<c:param name="mode" value="2"/>
        </c:url>
	<font class="smaller">&nbsp;&nbsp;
         <dspel:a href="${ImportPortletDefUrl}">
		back </font>
         </dspel:a>
     </td>
	</tr></table>

    <table cellspacing="0" cellpadding="4"  border="0" bgcolor="#c9d1d7" width="100%" id="table1">
        <tr><td nowrap>
            <font class="smaller">
            <img src='<%=response.encodeURL("images/info.gif")%>' >&nbsp;&nbsp;
	        <fmt:message key="update-portlet-descpription-info" bundle="${producerAdminbundle}"/>
            <br>
        </td></tr>
        <tr>
		<td width="100"><font class="smaller">&nbsp;
			<fmt:message key="remote-portlet-handle" bundle="${producerAdminbundle}"/>
		</td>
		<td>&nbsp;<font class="smaller_bold"><%--<%= portletDescription.getPortletHandle() %> --%>
        <dspel:valueof bean="ProducerAdminFormHandler.portletHandle"/></font>
        </td></tr>
    </table>
 </c:when>

<%-- *******************END OF PRODUCER OFFERED PORTLET DEFINITION SUCESSS*********************** --%>

<%-- ******************CONFIRM  PRODUCER OFFERED PORTLET DEFINITION DELETE*********************** --%>

    <c:when test="${ViewMode == 5}">
    <%--<dspel:setvalue bean="ProducerAdminFormHandler.portletHandleForDeletion" paramvalue="portletHandleID"/>--%>
    <dspel:setvalue bean="ProducerAdminFormHandler.portletHandleForDeletion" value="${param['portletHandleID']}"/>
    <dspel:form action="configProducer.jsp" formid="form1" name="form1" method="post">

    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
            <font class="pageheader">
            <fmt:message key="confirm-portlet-definition-delete" bundle="${producerAdminbundle}"/>
            </font>
        </td></tr></table>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">
        <fmt:message key="confirm-portlet-definition-delete-info" bundle="${producerAdminbundle}"/>
        </font>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

        <font class="smaller">
        <img src='<%=response.encodeURL("images/error.gif")%>' >&nbsp;&nbsp;
        <fmt:message key="remove-pop" bundle="${producerAdminbundle}"/>
        <c:out value="${param['portletDisplayName']}"/>
        <br><br>
            <c:url var="CancelUrl" value="${origURI}">
            <c:param name="mode" value="1"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.cancelUrl" value="${CancelUrl}" />

            <c:url var="SuccessUrl" value="${origURI}">
            <c:param name="mode" value="1"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.successUrl" value="${SuccessUrl}" />

            <c:url var="FailureUrl" value="${origURI}">
            <c:param name="mode" value="5"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.failureUrl" value="${FailureUrl}" />

       <dspel:input type="SUBMIT" value="Confirm" bean="ProducerAdminFormHandler.deletePortletDefinition"/>&nbsp;&nbsp;
       <dspel:input type="SUBMIT" value="Cancel" bean="ProducerAdminFormHandler.cancel"/>
       </td></tr>
     </dspel:form>
    </table>
  <dspel:setvalue bean="ProducerAdminFormHandler.reset"/>
  </c:when>

<%-- *****************END OF CONFIRM PRODUCER OFFERED PORTLET DEFINITION DELETE*************** --%>



<%-- *********************************ABOUT WSRP PRODUCER************************************* --%>

    <c:when test="${ViewMode == 4}">
		<jsp:include page="aboutWsrp.jsp"/>
    </c:when>

<%-- *************************END OF ABOUT WSRP PRODUCER************************************** --%>



<%-- ****************************DELETE PRODUCER REGISTRATION  ******************************** --%>

    <c:when test="${ViewMode == 3}">
				 	
            <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
            <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
                 <font class="pageheader">
            	 <fmt:message key="delete-producer-registration" bundle="${producerAdminbundle}"/>
                 </font>
            </td></tr></table>
            </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">
	        <fmt:message key="delete-producer-registration-title-info" bundle="${producerAdminbundle}"/>
        </font>
    </td></tr></table>

    <font class="smaller">
	    <img src="<%=clearGif%>" height="1" width="7" border="0"><br>

	        <table cellpadding="4" cellspacing="0" border="0" width="100%" bgcolor="#c9d1d7"><tr><td>

	        <!--this table generates the list-->
	        <table cellpadding="0" cellspacing="0" border="0">

            <c:url var="SuccessUrl" value="${origURI}">
                <c:param name="mode" value="3"/>
            </c:url>
            <dspel:setvalue bean="ProducerAdminFormHandler.successUrl" value="${SuccessUrl}"/>
            <dspel:getvalueof id="registration" idtype="java.util.Map" bean="ProducerAdminFormHandler.allRegistration">
	                <dspel:setvalue param="registrationDescription" paramvalue="element"/>  
                    <c:set var="counterOfRegistrationDescription" value="0"/>
                    <c:forEach var="registrationDescriptionVar" items="${registration}" varStatus="status">
                        <c:set var="counterOfRegistrationDescription" value="${counterOfRegistrationDescription+1}"/>
                        <c:if test="${status.index==0}">
                        </c:if>

                        <tr>
	                    <td nowrap width="250"><font class="smaller">&nbsp;&nbsp;
                        <c:out value="${registrationDescriptionVar.value.registrationData.consumerName}"/>
                        </font></td>
                        <td nowrap width="200"><font class="smaller">&nbsp;&nbsp;
                        <c:out value="${registrationDescriptionVar.value.lastModifiedTime}"/></font></td>

                        <c:url var="deleteRegistrationUrl" value="/configProducer.jsp">
	                        <c:param name="mode" value="9"/>
                        </c:url>
                        <td nowrap width="150"><font class="smaller">&nbsp;&nbsp;
                        <dspel:a href="${deleteRegistrationUrl}">
		                    <fmt:message key="remote-delete-def" bundle="${producerAdminbundle}"/></font>
                            <c:set var="consumerNameVar" value="${registrationDescriptionVar.value.registrationData.consumerName}">
							</c:set> 
                            <dspel:param name="consumerName" value="${consumerNameVar}"/> 
                            <c:set var="registrationHandleVar" value="${registrationDescriptionVar.key}"></c:set>
                            <dspel:param name="registrationHandle" value="${registrationHandleVar}"/></td>
		                </dspel:a>
                      </tr>
                      <tr><td colspan="4"></td></tr>
                 </c:forEach>
                    <c:if test="${counterOfRegistrationDescription == 0}">
                        <font class="smaller"><fmt:message key="no-available-registration-defination" bundle="${producerAdminbundle}"/></font>
                    </c:if>
	        </dspel:getvalueof>
          </table>
	   </td></tr></table>
    </c:when>


<%-- *************************END OF DELETE PRODUCER REGISTRATION***************************** --%>


<%-- *********************CONFIRM  REGISTRATION DEFINITION DELETE***************************** --%>

    <c:when test="${ViewMode == 9}">

    <dspel:setvalue bean="ProducerAdminFormHandler.registrationHandleForDeletion" paramvalue="registrationHandle"/>
    <dspel:form action="configProducer.jsp" formid="form3" name="form3" method="post">

    <table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="100%"><tr><td>
        <table cellpadding="4" cellspacing="0" border="0" bgcolor="#999999" width="100%"><tr><td>
            <font class="pageheader">
            <fmt:message key="confirm-registration-definition-delete" bundle="${producerAdminbundle}"/>
            </font>
        </td></tr></table>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="100%"><tr><td>
        <font class="smaller">
        <fmt:message key="confirm-registration-definition-delete-info" bundle="${producerAdminbundle}"/>
        </font>
    </td></tr></table>

    <table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>

        <font class="smaller">
        <img src='<%=response.encodeURL("images/error.gif")%>' >&nbsp;&nbsp;
        <fmt:message key="remove-registration" bundle="${producerAdminbundle}"/>
        <c:out value="${param['consumerName']}"/>
        <br><br>
            <c:url var="CancelUrl" value="${origURI}">
               <c:param name="mode" value="3"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.cancelUrl" value="${CancelUrl}" />

            <c:url var="SuccessUrl" value="${origURI}">
               <c:param name="mode" value="3"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.successUrl" value="${SuccessUrl}" />

            <c:url var="FailureUrl" value="${origURI}">
               <c:param name="mode" value="9"/>
            </c:url>
    	    <dspel:input type="HIDDEN" bean="ProducerAdminFormHandler.failureUrl" value="${FailureUrl}" />

       <dspel:input type="SUBMIT" value="Confirm" bean="ProducerAdminFormHandler.deleteRegistration"/>&nbsp;&nbsp;
       <dspel:input type="SUBMIT" value="Cancel" bean="ProducerAdminFormHandler.cancel"/>
       </td></tr>
     </dspel:form>
    </table>
  <dspel:setvalue bean="ProducerAdminFormHandler.reset"/> 
  </c:when>

<%-- *********************END OF CONFIRM REGISTRATION DEFINITION DELETE*********************** --%>


<%-- ************************CHANGE PRODUCER OFFERED PORTLET DEFINITION*********************** --%>

   <c:when test="${ViewMode == 7}">
       <%@include file="changePortletDescription.jspf"%>
   </c:when>

<%-- **********************END OF CHANGE PRODUCER OFFERED PORTLET DEFINITION****************** --%>


<%-- ***************************VIEW PORTLET DESCRIPTION************************************** --%>

   <c:when test="${ViewMode == 6}">
       <%@include file="viewPortletDescription.jspf"%>
   </c:when>

<%-- **************************END OF VIEW PORTLET DESCRIPTION******************************** --%>


<%-- ***************************LIST OF JSR 168 GEARS***************************************** --%>

   <c:when test="${ViewMode == 2}">
       <%@include file="jsr168_fragment_listing.jspf"%>
   </c:when>


<%-- **************************END OF LIST OF JSR 168 GEARS*********************************** --%>

   </c:choose>
   </td></tr></table>
  </html>
</dspel:demarcateTransaction>
<%-- @version $Id: //product/WSRP/version/10.0.3/admin/admin/admin/configProducer.jsp#2 $$Change: 651448 $--%>
