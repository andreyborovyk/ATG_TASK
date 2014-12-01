<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.ServletUtil" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/LoaderManager"/>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/JobStatus"/>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/DeleteJob"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>

<head>
  <title>Repository Loader</title>
 
  <style type="text/css">
  <!--
    /* all table data cells have a height of 2em */
    td { height: 2em; }

    /* no left border for the detail table so that it doesn't look
       like there are two borders between the two tables */
    table.detail { border-left-width: 0 }
    table.detail th { border-left-width: 0 }
    table.detail td { border-left-width: 0 }
  -->
  </style> 

    <link href="styles/main2.css" rel="stylesheet" type="text/css">
</head>

<body>

 <!-- breadcrumbs -->
<DIV class=breadcrumbbanner>&nbsp</DIV>

  <!-- header -->
  <%@ include file="header.jspf" %>
  
  <!-- top banner: Buttons-->
<TABLE class="buttonRowBg" cellpadding=0 cellspacing=0>
<TR>
    <TD valign="center" align=right><span class=buttonOnBg><dsp:a href="createjob.jsp">Create Job</dsp:a></span>
    </TD>
</TR>
</TABLE>

  
  <DIV class=column>

  <!-- Table of current jobs -->
  <span class=title>Current Jobs</span>
  <p>

  <%-- Set the sortBy parameter if it is not already set.  --%>
  <dsp:droplet name="/atg/dynamo/droplet/Switch">
    <dsp:param name="value" param="sortBy"/>
    <dsp:oparam name="unset">
      <%-- set sortBy attr which can be accessed outside of droplet --%>
      <% pageContext.setAttribute("sortBy", "-timeCreated"); %>
    </dsp:oparam>
  </dsp:droplet>
  <%
    // if sortBy attr was set before in Switch droplet then set the
    // sortBy param to the sortBy attr's value
    String sortByAttr = (String) pageContext.getAttribute("sortBy");
    if ( sortByAttr != null )
    {
      ServletUtil.getDynamoRequest( request ).setParameter(
        "sortBy", sortByAttr );
    } 
  %>

  <table cellpadding="0" cellspacing="0" border="0">
    <tr>

    <%-- first table containing all the job status information --%>
    <td valign="top">

     <%-- start of form --%>
     <dsp:form action="deletejobs.jsp" method="post" formid="deleteJobsForm"
       synchronized="/atg/dynamo/service/loader/formhandler/DeleteJob">

    <dsp:droplet name="/atg/dynamo/droplet/ForEach">

      <%-- array to loop over --%>
      <dsp:param name="array" bean="LoaderManager.jobs"/>

      <%-- sort in descending over by time stamp --%>
      <dsp:param name="sortProperties" param="sortBy"/>

      <%-- message if there are no jobs --%>
      <dsp:oparam name="empty">
        <p>There are no current jobs.</p>
      </dsp:oparam>

      <%-- start of table --%>
      <dsp:oparam name="outputStart">

        <table border="1" cellpadding="3">
          <%-- if user clicks on a heading, then the table is sorted --%>
          <%-- by that column                                        --%>
          <tr>
            <th>
              &nbsp;
            </th>
            <th>
              <dsp:a href="index.jsp">Jobs
                <dsp:param name="sortBy" value="+iD.id"/>
              </dsp:a>
            </th>
            <th>
              <dsp:a href="index.jsp">Time Added
                <dsp:param name="sortBy" value="-timeCreated"/>
              </dsp:a>
            </th>
            <th>
              <dsp:a href="index.jsp">Status
                <dsp:param name="sortBy" value="+status.status"/>
              </dsp:a>
            </th>
          </tr>
      </dsp:oparam>

      <%-- table row --%>
      <dsp:oparam name="output">
          <tr>
            <td>
              <%-- display checkbox for deleting the job if it is
                   COMPLETE or FAILED --%>
              <dsp:droplet name="/atg/dynamo/droplet/Switch">
                <dsp:param name="value" param="element.status"/>
                <dsp:oparam name="COMPLETE">
                  <dsp:input type="checkbox" bean="DeleteJob.jobIds"
                    paramvalue="element.iD"/>
                </dsp:oparam>
                <dsp:oparam name="FAILED">
                  <dsp:input type="checkbox" bean="DeleteJob.jobIds"
                    paramvalue="element.iD"/>
                </dsp:oparam>
                <dsp:oparam name="default">
                  &nbsp;
                </dsp:oparam>
              </dsp:droplet>
            </td>
            <td><dsp:valueof param="element.iD"/></td>
            <td><dsp:valueof param="element.timeCreated"
                  date="MM/dd/yyyy hh:mm:ss"/></td>
            <td><dsp:valueof param="element.status"/></td>

          </tr>
      </dsp:oparam>

      <%-- end of table --%>
      <dsp:oparam name="outputEnd">

        </table>
        <p>
          <input type="submit" value="Delete Job(s) From List"/>
        </p>

      </dsp:oparam>

    </dsp:droplet>

    <%-- end of form --%>
    </dsp:form>

    </td>
    <td valign="top">

    <%-- second table containing all the buttons for getting job detail --%>
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">

      <%-- array to loop over --%>
      <dsp:param name="array" bean="LoaderManager.jobs"/>

      <%-- sort in descending over by time stamp --%>
      <dsp:param name="sortProperties" param="sortBy"/>

      <%-- start of table --%>
      <dsp:oparam name="outputStart">
        <table class="detail" border="1" cellpadding="3">
          <tr>
            <th>Detail</th>
          </tr>
      </dsp:oparam>

      <%-- table row --%>
      <dsp:oparam name="output">
          <tr>
            <!-- form for getting job detail -->
            <dsp:form action="jobdetail.jsp" method="get" formid="jobDetailForm"
              synchronized="/atg/dynamo/service/loader/formhandler/JobStatus">

              <%-- pass JobId to JobStatus component --%>
              <dsp:input type="hidden" bean="JobStatus.jobId"
                paramvalue="element.iD"/>

              <td><dsp:input type="submit" bean="JobStatus.submit"
                value="Get Detail"/></td>

            </dsp:form>

          </tr>
      </dsp:oparam>

      <%-- end of table --%>
      <dsp:oparam name="outputEnd">
        </table>
      </dsp:oparam>

    </dsp:droplet>

  </tr>
  </table>

</DIV>


</body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/index.jsp#2 $$Change: 651448 $--%>
