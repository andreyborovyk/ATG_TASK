<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/CreateJob"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Confirm Jobs</title>
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>
  <body>
 
  <!-- bread crumbs -->
    <DIV class=breadcrumbbanner><span class=breadcrumb>
      <dsp:a href="index.jsp">Repository Loader</dsp:a>/Delete Jobs</span>
    </DIV>
  
    <!-- header -->
    <%@ include file="header.jspf" %>

    <!-- top banner: Buttons-->
<TABLE class="buttonRowBg" cellpadding=0 cellspacing=0>
<TR>
    <TD valign="center" align=right>&nbsp
    </TD>
</TR>
</TABLE>

    <DIV class=column>

    <span class=title>Confirm Job</span>

    <p>
      The job path is set to:  <dsp:valueof bean="CreateJob.path"/>
    </p>
    <p>
      Recursive is set to: <dsp:valueof bean="CreateJob.recursive"/>
    </p>

    <dsp:form action="jobstatus.jsp" method="post"
      synchronized="/atg/dynamo/service/loader/formhandler/CreateJob">

      <%-- set up URL for cancel/failure --%>
      <dsp:input type="hidden" bean="CreateJob.cancelURL" value="createjob.jsp"/>
      <dsp:input type="hidden" bean="CreateJob.failureURL" value="createjob.jsp"/>
  
      <dsp:input type="submit" bean="CreateJob.confirm" value="Confirm"/>
      <dsp:input type="submit" bean="CreateJob.cancel" value="Cancel"/>
      
    </dsp:form>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/confirmjob.jsp#2 $$Change: 651448 $--%>
