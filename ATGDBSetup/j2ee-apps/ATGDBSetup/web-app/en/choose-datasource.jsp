<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/dbsetup/datasource/DataSourceFormHandler"/>
<dsp:importbean bean="/atg/dynamo/dbsetup/job/JobIdFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<HTML><HEAD>
<link rel="stylesheet" type="text/css" href="../css/admin.css">
<TITLE>ATG Database Initializer 1: Choose a DataSource</TITLE></HEAD>

<body bgcolor="#FFFFFF" text="#00214A" vlink="#637DA6" link="#E87F02">

<img src="../images/admin-banner.gif" alt="Dynamo Configuration" align="top" width="585" height="37" border="0"><p>

<blockquote>
<h2>Database Initializer Step 1:  Choose a DataSource</h2><p>

<!-- begin choose datasource form -->

<dsp:form action="<%=ServletUtil.getDynamoRequest(request).getRequestURI()%>" method="POST">

<dsp:input bean="DataSourceFormHandler.chooseSuccessURL" type="hidden" value="database-details.jsp"/>

<!-- save the jobId -->
<dsp:input type="hidden" bean="JobIdFormHandler.errorPage" value="start.jsp"/>
<dsp:input type="hidden" bean="JobIdFormHandler.jobId" priority="-5"/>

<!-- error handling -->

<dsp:include page="choose-datasource-error.jsp">
  <dsp:param name="formHandler" bean="DataSourceFormHandler" />
</dsp:include>

<!-- explanatory text -->

Choose or input a single DataSource from only one of the following sections:<p>


<!-- native datasources -->

<dsp:droplet name="Switch">
  <dsp:param bean="DataSourceFormHandler.hasNativeNames" name="value"/>
  <dsp:oparam name="true">
    <blockquote>
    <h3>Application Server DataSources</h3>
    <dsp:droplet name="ForEach">
      <dsp:param bean="DataSourceFormHandler.nativeNames" name="array"/>
      <dsp:oparam name="output">
        <dsp:input bean="DataSourceFormHandler.chosenName" beanvalue="DataSourceFormHandler.prefixedNativeNames[param:index]" type="radio"/>
        <dsp:valueof param="element"/>
	<br>
      </dsp:oparam>
    </dsp:droplet>
    </blockquote>
    <b>OR</b>
  </dsp:oparam>
</dsp:droplet>

<!-- nucleus pool datasources -->

<dsp:droplet name="Switch">
  <dsp:param bean="DataSourceFormHandler.hasNucleusPoolNames" name="value"/>
  <dsp:oparam name="true">
    <blockquote>
    <h3>ATG Nucleus Connection Pool DataSources</h3>
    These DataSources were found in /atg/dynamo/service/jdbc:<br><br>
    <dsp:droplet name="ForEach">
      <dsp:param bean="DataSourceFormHandler.nucleusPoolNames" name="array"/>
      <dsp:oparam name="output">
        <dsp:input bean="DataSourceFormHandler.chosenName" beanvalue="DataSourceFormHandler.prefixedNucleusPoolNames[param:index]" type="radio"/>
        <dsp:valueof param="element"/>
	<br>
      </dsp:oparam>
    </dsp:droplet>
    </blockquote>
    <b>OR</b>
  </dsp:oparam>
</dsp:droplet>

<!-- nucleus JNDIReference datasources -->

<dsp:droplet name="Switch">
  <dsp:param bean="DataSourceFormHandler.hasJndiRefNames" name="value"/>
  <dsp:oparam name="true">
    <blockquote>
    <h3>ATG Nucleus JNDI Reference DataSources</h3><br>
    <dsp:droplet name="ForEach">
      <dsp:param bean="DataSourceFormHandler.jndiRefNames" name="array"/>
      <dsp:oparam name="output">
        <dsp:input bean="DataSourceFormHandler.chosenName" beanvalue="DataSourceFormHandler.prefixedJndiRefNames[param:index]" type="radio"/>
        <dsp:valueof param="element"/>
	<br>
      </dsp:oparam>
    </dsp:droplet>
    </blockquote>
    <b>OR</b>
  </dsp:oparam>
</dsp:droplet>

<!-- freehand JNDI datasource -->

<dsp:droplet name="Switch">
  <dsp:param bean="DataSourceFormHandler.dynamoJ2EEServer" name="value"/>
  <dsp:oparam name="false">
    <blockquote>
    <h3>JNDI DataSource</h3>
    Enter JNDI Name:<br>
    <dsp:input bean="DataSourceFormHandler.jndiName" type="text" size="70"/>
    </blockquote>
    <b>OR</b>
  </dsp:oparam>
</dsp:droplet>

<!-- freehand Nucleus datasource -->

<blockquote>
<h3>Nucleus DataSource</h3>
Alternatively, enter nucleus component name:<br>
<dsp:input bean="DataSourceFormHandler.nucleusName" type="text" size="70"/><p>
</blockquote>

<!-- test and choose buttons -->

</blockquote><p>

<dsp:input bean="DataSourceFormHandler.testDataSource" type="submit" value=" Test DataSource "/>
<dsp:input bean="DataSourceFormHandler.chooseDataSource" type="submit" value=" Next "/>

</dsp:form>

<!-- end choose datasource form -->

</blockquote>

</body></html>
</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/choose-datasource.jsp#2 $$Change: 651448 $--%>
