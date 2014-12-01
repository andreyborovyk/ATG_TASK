<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/service/loader/formhandler/DeleteJob"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Delete Jobs</title>
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

    <DIV class= column>

    <span class=title>Delete Jobs</span>

    <p>
      Do you wish to delete the following jobs?
    </p>

    <dsp:form action="index.jsp" method="post"
      synchronized="/atg/dynamo/service/loader/formhandler/DeleteJob">

      <%-- set up URL for cancel --%>
      <dsp:input type="hidden" bean="DeleteJob.cancelURL" value="index.jsp"/>

      <dsp:droplet name="/atg/dynamo/droplet/ForEach">

	<%-- array to loop over --%>
	<dsp:param name="array" bean="DeleteJob.jobIds"/>

	<%-- message if there are no jobs --%>
	<dsp:oparam name="empty">
	  <p>There are no jobs to delete.</p>
	</dsp:oparam>

	<%-- start of list --%>
	<dsp:oparam name="outputStart">
	  <ul>
	</dsp:oparam>

	<%-- list entry --%>
	<dsp:oparam name="output">
	    <li><dsp:valueof param="element"/></li>
	</dsp:oparam>

	<%-- end of list --%>
	<dsp:oparam name="outputEnd">
	  </ul>
	</dsp:oparam>

      </dsp:droplet>

      <dsp:input type="submit" bean="DeleteJob.submit" value="Yes"/>
      <dsp:input type="submit" bean="DeleteJob.cancel" value="No"/>

    </dsp:form>

    </DIV>

  </body>
</html>

</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/release/RL/j2ee-apps/rl/web-app.war/deletejobs.jsp#2 $$Change: 651448 $--%>
