<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- left panel - section navigation -->

<table border=0 cellspacing=0 cellpadding=0 width="100%">
  <tr>
    <td bgcolor="#FFFFFF" height="1" colspan=2><dsp:img src="../images/d.gif"/></td>
  </tr>
  <tr>
    <td bgcolor="#666666" height="8" colspan=2><dsp:img src="../images/d.gif"/></td>
  </tr>

    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param  name="array" bean="/atg/userprofiling/Profile.catalog.allRootCategories"/>
      <dsp:oparam name="output">
        <tr bgcolor="#FFFFFF">
          <td colspan=2><dsp:img src="../images/d.gif"/></td>
        </tr>
        <tr bgcolor="F7D774">
          <td><dsp:img src="../images/d.gif" hspace="5"/></td>
          <td><dsp:img src="../images/d.gif" vspace="1"/><br>
          
	  <dsp:droplet name="/atg/dynamo/droplet/IsNull">
	   <dsp:param name="value" param="element.template.url"/>
	   <dsp:oparam name="false">
 	    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
	     <dsp:a page="<%=urlStr%>">
	      <dsp:param name="id" param="element.repositoryId"/>
	      <dsp:param name="navAction" value="pop"/>
	      <dsp:param name="Item" param="element"/>
              <b><font size=-1 color="#555555"><dsp:valueof param="element.displayName"/></font></b>
	     </dsp:a>
	    </dsp:getvalueof>
	   </dsp:oparam>
          </dsp:droplet>
 
          <br>
          <dsp:img src="../images/d.gif" vspace="1"/><br></td>
        </tr>
      </dsp:oparam>
        
      <dsp:oparam name="empty">
        <p>No root categories found.
      </dsp:oparam>
    </dsp:droplet>
    </td>
  </tr>
  
  <tr bgcolor="#FFFFFF">
    <td height="1" colspan=2><dsp:img src="../images/d.gif"/></td>
  </tr>
  
  <tr bgcolor="#666666">
    <td height="8" colspan=2><dsp:img src="../images/d.gif"/></td>
  </tr>
  <tr bgcolor="#FFFFFF">
    <td height="1" colspan=2><dsp:img src="../images/d.gif"/></td>
  </tr>

  <tr bgcolor="#F7D774">
    <td colspan=2><dsp:img src="../images/d.gif" vspace="1"/><br>&nbsp;
        &nbsp;<dsp:a href="../catalog/compare.jsp"><b><font size=-1 color="#555555">Product 
        Comparisons</font></b></dsp:a><br>
        <dsp:img src="../images/d.gif" vspace="1"/><br></td>
  </tr>
  

</table>


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/common/CatalogNav.jsp#2 $$Change: 651448 $--%>
