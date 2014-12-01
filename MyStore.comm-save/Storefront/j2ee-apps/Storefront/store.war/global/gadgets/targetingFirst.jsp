<dsp:page>

  <%--
    This gadget invokes the TargetingRandom droplet, using the slot provided, and including the
    rendering page.

    Parameters:
     - targeter - The targeter (or slot) to be used by the targeting droplet
     - renderer - The JSP page used to render the output
     - elementName - The name of the parameter that the targeting content should be placed into
  --%>

  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>

  <dsp:getvalueof var="renderer" vartype="java.lang.String" param="renderer"/>

  <dsp:droplet name="TargetingFirst">
    <dsp:param name="howMany" value="1"/>
    <dsp:param name="targeter" param="targeter"/>
    <dsp:param name="fireViewItemEvent" value="false"/>
    <dsp:param name="elementName" param="elementName"/>
    <dsp:oparam name="output">
      <dsp:include page="${renderer}"/>
    </dsp:oparam>
  </dsp:droplet>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/gadgets/targetingFirst.jsp#3 $$Change: 635816 $--%>
