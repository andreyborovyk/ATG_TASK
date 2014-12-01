<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<table border=0>
  <!-- first promo -->
  <tr valign=top> 
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param name="howMany" value="1"/>
      <dsp:param bean="/atg/registry/Slots/MediaSlot" name="targeter"/>
      <dsp:param name="fireContentEvent" value="false"/>
      <dsp:param name="fireContentTypeEvent" value="false"/>
      <dsp:oparam name="output">
        <img border="1" src="<dsp:valueof param="element.url"/>">
      </dsp:oparam>  
    </dsp:droplet></td>
    <td>&nbsp;</td>
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param bean="/atg/registry/Slots/MessageSlot_de" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <span class=small><dsp:valueof valueishtml="<%=true%>" param="element.data"/>
        </span>
      </dsp:oparam>
    </dsp:droplet>
    </td>
  </tr>

  <!-- second promo -->
  <tr valign=top> 
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param name="howMany" value="1"/>
      <dsp:param bean="/atg/registry/Slots/MediaSlot2" name="targeter"/>
      <dsp:param name="fireContentEvent" value="false"/>
      <dsp:param name="fireContentTypeEvent" value="false"/>
      <dsp:oparam name="output">
        <img border="1" src="<dsp:valueof param="element.url"/>">
      </dsp:oparam>  
    </dsp:droplet></td>
    <td>&nbsp;</td>
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param bean="/atg/registry/Slots/MessageSlot2_de" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <span class=small><dsp:valueof valueishtml="<%=true%>" param="element.data"/>
        </span>
      </dsp:oparam>
    </dsp:droplet>
    </td>
  </tr>

  <!-- third promo -->
  <tr valign=top> 
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param name="howMany" value="1"/>
      <dsp:param bean="/atg/registry/Slots/MediaSlot3" name="targeter"/>
      <dsp:param name="fireContentEvent" value="false"/>
      <dsp:param name="fireContentTypeEvent" value="false"/>
      <dsp:oparam name="output">
        <img border="1" src="<dsp:valueof param="element.url"/>">
      </dsp:oparam>  
    </dsp:droplet></td>
    <td>&nbsp;</td>
    <td>
    <dsp:droplet name="/atg/targeting/TargetingFirst">
      <dsp:param bean="/atg/registry/Slots/MessageSlot3_de" name="targeter"/>
      <dsp:param name="howMany" value="1"/>
      <dsp:oparam name="output">
        <span class=small><dsp:valueof valueishtml="<%=true%>" param="element.data"/>
        </span>
      </dsp:oparam>
    </dsp:droplet>
    </td>
  </tr>

</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/DisplayPromos.jsp#2 $$Change: 651448 $--%>
