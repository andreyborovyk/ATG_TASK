<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<br>Should see this second 
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" value='<%= "true" %>'/>
  <dsp:oparam name="true">
<br>Should see this third
    <dsp:droplet name="/atg/dynamo/droplet/Switch">
      <dsp:param name="value" value='<%= "true" %>'/>
      <dsp:oparam name="true">
<br>Should see this fourth
        <dsp:include page="inc3.jsp"/>
<br>Should see this sixth
      </dsp:oparam>
    </dsp:droplet>
<br>Should see this seventh
  </dsp:oparam>
</dsp:droplet>
<br>Should see this eighth
</dsp:page>
<%-- @version $Id: //product/DAS/version/10.0.3/templates/DAF/j2ee-apps/dyn/inc1d11.jsp#2 $$Change: 651448 $--%>
