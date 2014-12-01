<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:droplet name="Switch">
  <dsp:param param="formHandler.formError" name="value"/>
  <dsp:oparam name="true">
    <STRONG><UL>
    <dsp:droplet name="ForEach">
      <dsp:param param="formHandler.formExceptions" name="array"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.errorCode"/>
          <dsp:oparam name="dbsetupDataSourceFormTestSuccess">
            <dsp:droplet name="ErrorMessageForEach">
              <dsp:param param="element" name="exceptions"/>
              <dsp:oparam name="output">
                <LI> <font color=00cc00><dsp:valueof param="message"/></font>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:droplet name="ErrorMessageForEach">
              <dsp:param param="element" name="exceptions"/>
              <dsp:oparam name="output">
                <LI> <font color=cc0000><dsp:valueof param="message"/></font>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>
    </UL></STRONG><p>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/choose-datasource-error.jsp#2 $$Change: 651448 $--%>
