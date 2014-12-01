<dsp:importbean bean="/atg/userprofiling/ProfileFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/ProfileErrorMessageForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:page>

  <crs:pageContainer divId="atg_store_optins" titleString="Mailing List Signup">    
    <jsp:body>
       <div id="atg_store_optins">






 

  <dsp:droplet name="Switch">
    <dsp:param bean="ProfileFormHandler.profile.transient" name="value"/>
    <dsp:oparam name="true">
      You are currently not logged in. Please login to change your preferences.
    </dsp:oparam>

    <dsp:oparam name="default">
    <fieldset>
      <dsp:form action="optIn.jsp" method="post">
        <dsp:input bean="ProfileFormHandler.updateSuccessURL" type="hidden" value="optIn.jsp"/>

        <legend>Please send me information on the following:</legend>
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

	
	<li><label>Fashion News</label> <dsp:input bean="ProfileFormHandler.value.emailOptIns" type="checkbox" value="Fashion_News"/> Monthly newsletter with up-to-the-minute information every fashionista needs! 
        <li><label>Weekly Bargains</label> <dsp:input bean="ProfileFormHandler.value.emailOptIns" type="checkbox" value="Weekly_Bargains"/> Weekly alerts about items that have to move fast!

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
  </cbp:pageContainer>    



</crs:page>
