<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/JobStatus"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Job Results</title>
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>

  <body>
   <!-- bread crumbs -->
    <DIV class=breadcrumbbanner><span class=breadcrumb>
      <dsp:a href="index.jsp">Repository Loader</dsp:a>/<dsp:a href="jobdetail.jsp">Job Detail</dsp:a>/Job Results</span>
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

    <span class=title>Results for Job <dsp:valueof bean="JobStatus.jobId"/></span>
    <p>

    <!-- table listing files for this job -->
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">

      <%-- array to loop over --%>
      <dsp:param name="array" bean="JobStatus.resultList.elements"/>

      <%-- sort in asccending over file name --%>
      <%-- <dsp:param name="sortProperties" value="name"/> --%>

      <%-- message if there are no files --%>
      <dsp:oparam name="empty">
        <p>There were no results for this job.</p>
      </dsp:oparam>

      <%-- start of table --%>
      <dsp:oparam name="outputStart">
	<table border="1">
          <tr>
	    <th>Status</th>
          </tr>
      </dsp:oparam>

      <%-- table row --%>
      <dsp:oparam name="output">
          <tr>
	    <td><dsp:valueof param="element.transactionalStatus"/></td>
          </tr>
      </dsp:oparam>

      <%-- end of table --%>
      <dsp:oparam name="outputEnd">
	</table>
      </dsp:oparam>

    </dsp:droplet>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/jobresults.jsp#2 $$Change: 651448 $--%>
