<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:importbean bean="/atg/dynamo/dbsetup/module/ModuleFormHandler"/>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:page>
<br>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="tasks"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
       <dsp:param name="value" param="element.previouslyRun"/>
       <dsp:oparam name="true">
         <dsp:input bean="ModuleFormHandler.databaseTaskNames" type="checkbox" paramvalue="element.key" checked="<%=false %>"/>
         <dsp:valueof param="element.baseResourceName"/>
         (already executed)
       </dsp:oparam>
       <dsp:oparam name="false">
         <dsp:droplet name="Switch">
           <dsp:param name="value" param="element.default"/>
           <dsp:oparam name="true">
             <dsp:input bean="ModuleFormHandler.databaseTaskNames" type="checkbox" paramvalue="element.key" checked="<%=true %>"/>
             <dsp:valueof param="element.baseResourceName"/>
           </dsp:oparam>
           <dsp:oparam name="false">
             <dsp:input bean="ModuleFormHandler.databaseTaskNames" type="checkbox" paramvalue="element.key" checked="<%=false %>"/>
             <dsp:valueof param="element.baseResourceName"/>
             (not recommended for most applications)
           </dsp:oparam>
         </dsp:droplet>
       </dsp:oparam>
    </dsp:droplet>
    <br>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/ATGDBSetup/version/10.0.3/ATGDBSetupEar/src/j2ee-apps/ATGDBSetup/web-app/en/display-tasks.jsp#2 $$Change: 651448 $--%>
