<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/CreateJob"/>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/JobStatus"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Job Status</title>
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>

  <body>
    
    
    <!-- bread crumbs -->
    <DIV class=breadcrumbbanner><span class=breadcrumb>
      <dsp:a href="index.jsp">Repository Loader</dsp:a>/<dsp:a href="createjob.jsp">Create Job</dsp:a>/Job Status</span>
    </DIV>
    
    
    <!-- header -->
    <%@ include file="header.jspf" %>
    
    <!-- top banner: Buttons-->
<TABLE class="buttonRowBg" cellpadding=0 cellspacing=0>
<TR>
    <TD valign="center" align=right><span class=buttonOnBg><dsp:a href="createjob.jsp">Create Another Job</dsp:a></span>
    </TD>
</TR>
</TABLE>
    
   
     <DIV class=column>
     
    <span class=title>Job Status</span>
<p>
    <dsp:form action="jobdetail.jsp" method="post"
      synchronized="/atg/dynamo/service/loader/formhandler/JobStatus">

      <%-- pass JobId to JobStatus component --%>
      <dsp:input type="hidden" bean="JobStatus.jobId" beanvalue="CreateJob.jobId"/>

      <table cellpadding=5 cellspacing=0 border=1>
	<tr><td>Job ID</td><td><dsp:valueof bean="CreateJob.jobId"/></td></tr>

	<tr><td>Path</td><td><dsp:valueof bean="CreateJob.path"/></td></tr>

	<tr><td>Recurse</td><td><dsp:valueof bean="CreateJob.recursive"/></td></tr>

	<tr><td>Date Threshold</td><td><dsp:valueof bean="CreateJob.dateThreshold" date="M/dd/yyyy hh:mm:ss"/></td></tr>

        <tr>
          <td>Detail</td>
          <td><dsp:input type="submit" bean="JobStatus.submit" value="Get Detail"/></td>
        </tr>

      </table>

    </dsp:form>

    
    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/jobstatus.jsp#2 $$Change: 651448 $--%>
