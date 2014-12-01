<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>

  <crs:pageContainer divId="atg_store_optouts" titleString="Mailing List Removal">    
    <jsp:body>
       <div id="atg_store_optouts">






 

  <dsp:droplet name="Switch">
    <dsp:param bean="ProfileFormHandler.profile.transient" name="value"/>
    <dsp:oparam name="true">
      You are currently not logged in. Please login to change your preferences.
    </dsp:oparam>

    <dsp:oparam name="default">
    <fieldset>
      <dsp:form action="optOut.jsp" method="post">
        <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="hidden" value="optIn.jsp"/>

        <legend>Please remove me from the following lists:</legend>
	<ul class="atg_store_basicForm">
        <dsp:droplet name="Switch">
          <dsp:param bean="ProfileFormHandler.formError" name="value"/>
          <dsp:oparam name="true">
              <dsp:droplet name="ProfileErrorMessageForEach">
                <dsp:param bean="ProfileFormHandler.formExceptions" name="exceptions"/>
                <dsp:oparam name="output">
                  <li> <dsp:valueof param="message"/>
                </dsp:oparam>
              </dsp:droplet>
            </ul></strong></font>
          </dsp:oparam>
        </dsp:droplet>

	
	<li><label>Special Deals</label> <dsp:input bean="ProfileFormHandler.value.emailOptOuts" type="checkbox" value="Special_Deals"/> I don't want email about special offers from ATG Store
        <li><label>Site Notices</label> <dsp:input bean="ProfileFormHandler.value.emailOptOuts" type="checkbox" value="Site_Notices"/> I don't want to hear about system outages, major updates, etc.

	</ul>
	<p>
	<div class="atg_store_basicButton">
	   <dsp:input bean="ProfileFormHandler.update" type="submit" value="Save Changes"/>
	</div>
	
      </dsp:form>
      </fieldset>
    </dsp:oparam>
  </dsp:droplet>
  
  
	</div>
    </jsp:body>
  </crs:pageContainer>    



</dsp:page>
