<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:droplet name="Switch">
  <dsp:param param="formHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><STRONG><UL>
    <dsp:droplet name="ErrorMessageForEach">
      <dsp:param param="formHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
      </dsp:oparam>
    </dsp:droplet>
    </UL></STRONG></font><p>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/error.jsp#2 $$Change: 651448 $--%>
