<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/projects/b2bstore/purchaselists/PurchaselistFormHandler" />
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>


<DECLAREPARAM NAME="noCrumbs" 
              CLASS="java.lang.String" 
              DESCRIPTION="This is for deciding what kind of breadcrumbs to display.
                           If this is true, then breadcrumbs will show: Store Home|Checkout,
                           instead of nav history. Default is true."
                           OPTIONAL>
               
<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Einkaufslisten"/></dsp:include>


<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB" > 
    <td colspan=2 height=18> &nbsp; <span class=small>
    <!-- breadcrumbs --> 
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="noCrumbs"/>
        <dsp:oparam name="false">          
          <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/><dsp:param name="no_new_crumb" value="true"/></dsp:include> &gt; Einkaufslisten
        </dsp:oparam>
        <dsp:oparam name="default">
          <dsp:param name="noCrumbs" value="true"/>
          <dsp:a href="my_account.jsp">Mein Konto</dsp:a> &gt; Einkaufslisten
        </dsp:oparam>        
      </dsp:droplet>
    </td>
  </tr>
  

  <tr valign=top> 
    <td width=55><img src="../images/d.gif" hspace=27></td>

    <!-- main content area -->
    <td valign="top" width=745> 
    <!-- spacing image to make netscape work -->
    <table border=0 cellpadding=4 width=80%>
       <!-- vertical space -->
      <tr><td colspan=2><img src="../images/d.gif" vspace=5></td></tr>
      
      <dsp:getvalueof id="formExceptions" bean="PurchaselistFormHandler.formExceptions">
             <dsp:droplet name="ErrorMessageForEach">
               <dsp:param value="<%=formExceptions%>" name="exceptions"/>
               <dsp:oparam name="output">
                 <tr><td colspan=2>
                 <LI> <dsp:valueof param="message"/>
                 </td></tr>
               </dsp:oparam>
             </dsp:droplet>
      </dsp:getvalueof>
      <tr valign=top>
        <td colspan=2><span class=big>Mein Konto</span></td>
      </tr>
      
      <!-- vertical space -->
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>

      <tr>
       <td colspan=2>
        <table width=100% cellpadding=3 cellspacing=0 border=0>
        <tr><td class=box-top>&nbsp;Einkaufslisten</td></tr></table>
        </td>
      </tr>

      <!-- vertical space -->
      <tr><td colspan=2><img src="../images/d.gif" vspace=0></td></tr>
    <tr>
      <td>
      <dsp:droplet name="IsEmpty">
        <dsp:param bean="Profile.purchaselists" name="value"/>
        <dsp:oparam name="true">
          <span class="small">Erstellen Sie eine Einkaufsliste und fügen Sie ihr dann Artikel aus dem Produktkatalog zu.</span><p>
        </dsp:oparam>
        <dsp:oparam name="false">
          <span class="small">Sie können der Einkaufsliste Artikel hinzufügen, indem Sie auf der Seite "Produktbeschreibung" die Option "Zur Liste hinzufügen" auswählen.</span>
        </dsp:oparam>
        </dsp:droplet>
        </td>
    </tr>
 
      <tr valign=top>
           <td>
             <dsp:droplet name="/atg/dynamo/droplet/ForEach">
               <dsp:param bean="Profile.purchaselists" name="array" />
               <dsp:param name="elementName" value="list"/>
         
               <dsp:oparam name="output">
                 <dsp:a href="purchase_list.jsp">
                 <dsp:param name="listNumber" param="index"/>
                 <dsp:valueof param="list.eventName"/></dsp:a><br>
               </dsp:oparam>
             </dsp:droplet>



             <dsp:form action="purchase_lists.jsp" method="post">
             <input name="noCrumbs" type="hidden" value="<dsp:valueof param='noCrumbs'/>">
             Neue Einkaufsliste:<br>
             <dsp:input bean="PurchaselistFormHandler.listName" maxlength="20" size="20" type="text"  value=""/>
						 <dsp:input bean="PurchaselistFormHandler.savePurchaseList" type="hidden" value=""/>
             <dsp:input bean="PurchaselistFormHandler.savePurchaseList" type="submit"  value="Liste erstellen"/>
             <br>
             <span class="help">Listenname dürfen maximal aus 20 Zeichen bestehen</span>
             </dsp:form>
        </td>
      </tr>

      <!-- vertical space -->
      <tr><td><img src="../images/d.gif" vspace=0></td></tr>
    </table>

    </td>
  </tr>
</table>

</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/user/purchase_lists.jsp#2 $$Change: 651448 $--%>
