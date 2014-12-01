<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>

<%--
   Document Exchange Gear
   gearmode = content 
   displaymode = full
  
   This page includes the appropriate page fragment for 
   any of the following functions based on the value of
   request parameter "dexmode":
      list documents
      create document
      edit document
      delete document
      update document status
      display search results      

   NOTE use jsp:include here because it keeps the page size small

--%>

<dsp:page>
<paf:InitializeGearEnvironment id="gearEnv">

<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" param="dexmode" />

  <dsp:oparam name="upload">
     <dsp:include page="/createDoc.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="edit">
     <dsp:include page="/editDoc.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="editStatus">
     <dsp:include page="/editStatusForm.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="delete">
    <dsp:include page="/deleteDoc.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="list">
    <dsp:include page="/fullList.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="oneItem">
    <dsp:include page="/displayDoc.jsp" flush="false"/>
  </dsp:oparam>

  <dsp:oparam name="searchResults">
    <dsp:include page="/searchResults.jsp" flush="false"/>
  </dsp:oparam>

</dsp:droplet>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/docexch/docexch.war/DocExchFull.jsp#2 $$Change: 651448 $--%>
