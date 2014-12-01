<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/service/loader/FileSystemMonitorService"/>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/CreateJob"/>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/DateEditor"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Create Job</title>
    
    <link href="styles/main2.css" rel="stylesheet" type="text/css">
  </head>
  <body>
  
  <!-- bread crumbs -->
  <DIV class=breadcrumbbanner><span class=breadcrumb><dsp:a href="index.jsp">Repository Loader</dsp:a>/Create Job</span></DIV>
  
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

    <span class=title>Create Job</span>

    <p>
      FileSystemMonitorService root path: <dsp:valueof bean="FileSystemMonitorService.rootPath"/>
    </p>

    <dsp:form action="confirmjob.jsp" method="post"
      synchronized="/atg/dynamo/service/loader/formhandler/CreateJob">

      <%-- set up URL's for success, failure, and cancel outcomes --%>
      <dsp:input type="hidden" bean="CreateJob.successURL" value="jobstatus.jsp"/>
      <dsp:input type="hidden" bean="CreateJob.failureURL" value="createjob.jsp"/>
      <dsp:input type="hidden" bean="CreateJob.cancelURL" value="index.jsp"/>

      <dsp:input type="hidden" bean="CreateJob.loggingDebug" value="true"/>

      <p>
        <!-- text input for Subdirectory -->
        Subdirectory under <dsp:valueof bean="FileSystemMonitorService.rootPath"/>: 
        <dsp:input type="text" bean="CreateJob.subdirectory" size="32" maxsize="256"
          required="<%=false%>"/>

        <%-- check if there were any errors with subdirectory property --%>
	<dsp:droplet name="Switch">
	  <dsp:param bean="CreateJob.propertyExceptions.subdirectory.errorCode"
	    name="value"/>
	  <dsp:oparam name="missingRequiredValue">
	    <br><font color="red">You must provide a subdirectory.</font>
	  </dsp:oparam>
	  <dsp:oparam name="invalidSubdirectory">
	    <br><font color="red">The subdirectory provided does not
	    exist under the FileSystemMonitorService's root path.</font>
	  </dsp:oparam>
	  <dsp:oparam name="noFileSystemMonitor">
            <br><font color="red">The FileSystemMonitor property in the
            CreateJob component must be set.</font>
	  </dsp:oparam>
	  <dsp:oparam name="noFileSystemMonitor">
	    <font color="red">The FileSystemMonitor property in the
	    CreateJob component must be set.</font>
	  </dsp:oparam>
	  <dsp:oparam name="noPath">
	    <font color="red">The Path property in the CreateJob component
	    must be set.</font>
	  </dsp:oparam>
	</dsp:droplet>

      <p>
        <!-- radio input for Recurse  -->
        Recurse: <dsp:input type="radio" bean="CreateJob.recursive" value="true"/> yes
                 <dsp:input type="radio" bean="CreateJob.recursive" value="false"/> no
      </p>

      <!-- date threshold input -->
      <p>
        Date Threshold:<br>
        For an update, remove, or reload job, only files newer than the
        date threshold will be consider for the job.  Note that for
        add jobs all files are considered for the job regardless of
        their age.<br>

        <%-- date input using tag converter --%>
        <dsp:input bean="CreateJob.dateThreshold" priority="99"
          date="M/d/yyyy HH:mm:ss"/>
        <br>format M/d/yyyy HH:mm:ss, e.g. 5/31/2002 13:05:00 

        <%-- check that date input is valid --%>
	<dsp:droplet name="Switch">
	  <dsp:param bean="CreateJob.propertyExceptions.dateThreshold.errorCode"
	    name="value"/>
	  <dsp:oparam name="invalidDate">
	    <br><font color="red">Please enter a valid date.</font>
	  </dsp:oparam>
	</dsp:droplet>
      </p>

      <p>
        <!-- submit buttons  -->
        <dsp:input type="submit" bean="CreateJob.add" value="Add Files"/>
        <dsp:input type="submit" bean="CreateJob.update" value="Update Files"/>
        <dsp:input type="submit" bean="CreateJob.delete" value="Remove Files"/>
        <dsp:input type="submit" bean="CreateJob.cancel" value="Cancel"/>

      </p>
    </dsp:form>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/createjob.jsp#2 $$Change: 651448 $--%>
