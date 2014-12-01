<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/JobStatus"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Job Error Detail</title>
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>

  <body>
    <!-- bread crumbs -->
    <DIV class=breadcrumbbanner><span class=breadcrumb>
      <dsp:a href="index.jsp">Repository Loader</dsp:a>/<dsp:a href="jobdetail.jsp">Job Detail</dsp:a>/<dsp:a href="joberrors.jsp">Job Errors</dsp:a>/Job Error Detail</span>
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

    <span class=title>Error Detail for Job <dsp:valueof bean="JobStatus.jobId"/></span>

    <p><b>Error Message:</b><br>
    <dsp:valueof bean="JobStatus.currentError.message"/>
    </p>

    <p><b>Exception Message:</b><br>
    <dsp:valueof bean="JobStatus.currentError.exception.message"/>
    </p>

    <p><b>Exception Stack Trace:</b><br>
    <tt><dsp:valueof bean="JobStatus.currentExceptionStackTrace"/></tt>
    </p>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/joberrordetail.jsp#2 $$Change: 651448 $--%>
