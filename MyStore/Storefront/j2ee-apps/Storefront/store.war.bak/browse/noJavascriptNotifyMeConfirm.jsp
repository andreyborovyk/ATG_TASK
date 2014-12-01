<%-- Renders the "We'll Notify You" dialog when javascript is turned off--%>

<dsp:page> 
  <div id="atg_store_notifyMeConfirm">
    <crs:messageWithDefault key="browse_notifyMeConfirmPopup.title"/>
    <c:if test="${!empty messageText}">
      <h2 class="title">
        ${messageText}
      </h2>
    </c:if>
    <crs:messageWithDefault key="browse_notifyMeConfirmPopup.intro"/>
    <c:if test="${!empty messageText}">
      <p>
        ${messageText}
      </p>
    </c:if>
  </div>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/noJavascriptNotifyMeConfirm.jsp#3 $$Change: 635816 $--%>
