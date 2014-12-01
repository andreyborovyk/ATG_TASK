<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<i18n:bundle baseName="atg.gears.helloworld.helloworld" localeAttribute="userLocale" changeResponseLocale="false" />

<h3><i18n:message key="helloworld-full"/></h3>
 
<p>
 <i18n:message key="helloworld-description"/>
</p>

</dsp:page>


<%-- @version $Id: //app/portal/version/10.0.3/helloworld/helloworld.war/chtml/fullHelloWorld.jsp#1 $$Change: 651360 $--%>
