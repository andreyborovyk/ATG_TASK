<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/JobStatus"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param name="value" bean="JobStatus.status"/>
      <dsp:oparam name="COMPLETE">
      </dsp:oparam>
      <dsp:oparam name="default">
       <meta http-equiv=Refresh content="60; url=/rl/jobdetail.jsp">
      </dsp:oparam>
    </dsp:droplet>
    <title>Job Detail</title>
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>

  <body>
  <!-- bread crumbs -->
    <DIV class=breadcrumbbanner><span class=breadcrumb>
      <dsp:a href="index.jsp">Repository Loader</dsp:a>/Job Detail</span>
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

    <span class=title>Job Detail</span>
    <p>

      <!-- table listing details of this job -->
      <table cellpadding="5" cellspacing=0 border="1">
        <tr><td>Job ID</td><td><dsp:valueof bean="JobStatus.jobId"/></td></tr>

        <tr><td>Status</td><td><dsp:valueof bean="JobStatus.status"/></td></tr>

        <tr>
          <td>Time Created</td>
          <td><dsp:valueof bean="JobStatus.timeCreated" date="M/dd/yyyy hh:mm:ss"/></td>
        </tr>

        <tr>
          <td>Time Started</td>
          <td><dsp:valueof bean="JobStatus.timeStarted" date="M/dd/yyyy hh:mm:ss"/></td>
        </tr>

        <tr>
          <td>Time Stopped</td>
          <td><dsp:valueof bean="JobStatus.timeStopped" date="M/dd/yyyy hh:mm:ss"/></td>
        </tr>

        <tr>
          <td>Files</td>
          <td>
            <%-- display number of files --%>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param name="value" bean="JobStatus.fileElementsSize"/>
              <dsp:oparam name="0">
                0
              </dsp:oparam>
             <dsp:oparam name="-1">
             	<dsp:a href="jobfiles.jsp">
             		             Unspecified Size
             	</dsp:a>
              </dsp:oparam>
              <dsp:oparam name="default">
                <dsp:a href="jobfiles.jsp">
                  <dsp:valueof bean="JobStatus.fileElementsSize"/>
                </dsp:a>
              </dsp:oparam>
            </dsp:droplet>
          </td>
        </tr>

<!--
        <tr>
          <td>Results</td>
            <%-- display number of results --%>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param name="value" bean="JobStatus.resultListSize"/>
              <dsp:oparam name="0">
                0
              </dsp:oparam>
              <dsp:oparam name="default">
                <dsp:a href="jobresults.jsp">
                  <dsp:valueof bean="JobStatus.resultListSize"/>
                </dsp:a>
              </dsp:oparam>
            </dsp:droplet>
          </td>
        </tr>
-->

        <tr>
          <td>Errors</td>
          <td>
            <%-- display number of errors --%>
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <dsp:param name="value" bean="JobStatus.errorListSize"/>
              <dsp:oparam name="0">
                0
              </dsp:oparam>
              <dsp:oparam name="default">
                <dsp:a href="joberrors.jsp">
                  <dsp:valueof bean="JobStatus.errorListSize"/>
                </dsp:a>
              </dsp:oparam>
            </dsp:droplet>
          </td>
        </tr>

      </table>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/jobdetail.jsp#2 $$Change: 651448 $--%>
