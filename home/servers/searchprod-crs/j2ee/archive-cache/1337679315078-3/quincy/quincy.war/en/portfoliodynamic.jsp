<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page import="atg.servlet.*"%>
<dsp:page>

<% DynamoHttpServletRequest drequest = ServletUtil.getDynamoRequest(request); %>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/demo/QuincyFunds/PageValues"/>
<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="The profile id used to construct this portfolio">


<dsp:droplet name="/atg/targeting/RepositoryLookup">
  <dsp:param bean="/atg/userprofiling/ProfileAdapterRepository" name="repository"/>
  <dsp:param name="id" param="ElementId"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:oparam name="output">
    <!-- Rename the element parameter profile for easier access -->
    <dsp:setvalue paramvalue="element" param="profile"/>

    <!-- Value is initially 0.0 -->
    <%
      ServletUtil.setPropertyValue(request, response, 
                                         "/atg/demo/QuincyFunds/PageValues.portfolioValue", 
                                         new Double(0.0));
    %>
  
    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param name="array" param="profile.fundlist"/>
      <dsp:oparam name="outputEnd">
        <tr> 
          <td bgcolor=#ffff99 colspan=6><img src="images/clear.gif" height=1></td> 
          <td colspan=2 bgcolor=#003366><img src="images/clear.gif" height=1></td>
        </tr>
        <tr bgcolor=#ffff99>
          <td colspan=6>&nbsp;</td>
          <td align=right><font size=-1><b>
          <dsp:valueof bean="/atg/demo/QuincyFunds/PageValues.portfolioValue"
	               currency="true"/></b></font></td> 
          <td>&nbsp;</td>
       </tr> 
       <tr bgcolor=#ffff99>
         <td colspan=8><img src="images/d.gif" vspace=2></td>
       </tr>
       </table>

      </dsp:oparam>
      <dsp:oparam name="output">
        <tr bgcolor=#ffff99>  

	<dsp:getvalueof id="path" param="element">
	<%
		String attach=((String)path);
		drequest.setParameter("path",attach);
	%>
	</dsp:getvalueof>
 
        <dsp:droplet name="/atg/targeting/RepositoryLookup">
          <dsp:param bean="/atg/demo/QuincyFunds/repositories/Funds/Funds" name="repository"/>
	  <dsp:param name="itemDescriptor" value="fund"/>
          <dsp:param name="id" param="path"/>
          <dsp:param name="fireContentEvent" value="false"/>
          <dsp:param name="fireContentTypeEvent" value="false"/>
          <dsp:oparam name="output">
 
            <td> &nbsp; <font size=-1><dsp:a href="fund.jsp"><dsp:param name="ElementId" param="element.repositoryId"/><dsp:valueof param="element.symbol"/></dsp:a></font></td>
            <td>&nbsp;</td> 
            <td><font size=-1><dsp:valueof param="profile.fundshares[param:index]"/></font></td> 
            <td>&nbsp;</td> 
            <td><font size=-1><dsp:valueof param="element.price" 
			                   currency="true"/></font></td> 
            <td>&nbsp;</td> 
            <td align=right><font size=-1>

            <!--
              -- first put the price and # of shares into parameters for
              -- easy access from Java
              -->
            <dsp:setvalue paramvalue="element.price" param="price"/>
            <dsp:setvalue paramvalue="profile.fundshares[param:index]" param="shares"/>
                
            <!--
              -- Then define the fundValue as a new parameter as the product
              -- of the price and shares parameters
              -->
            <%drequest.setParameter("fundValue", new Double(
                              ((Number) drequest.getObjectParameter("shares")).doubleValue() * 
                              ((Number) drequest.getObjectParameter("price")).doubleValue()));%>
            <!-- Now format the fundValue parameter using the number converter -->
            <dsp:valueof param="fundValue" currency="true"/></font></td>

            <!-- Then accumulate the new portfolioValue as a page level variable -->
            <%
              ServletUtil.setPropertyValue(request, response, 
                                                 "/atg/demo/QuincyFunds/PageValues.portfolioValue",
                new Double(((Number) ServletUtil.getPropertyValue(
                                 request, "/atg/demo/QuincyFunds/PageValues.portfolioValue")).doubleValue() +
                           ((Number) drequest.getObjectParameter("fundValue")).doubleValue()));
            %>
            <td>&nbsp;</td> 
            
          </dsp:oparam>
          <dsp:oparam name="empty">
              <td colspan=8>&nbsp;</td>  
          </dsp:oparam>
        </dsp:droplet>

    </tr> 
   </dsp:oparam>
   <dsp:oparam name="empty">
    
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
       <dsp:param bean="Profile.usertype" name="value"/>
       <dsp:oparam name="default">
         <tr>
           <td colspan=8 bgcolor=ffff99><font size=-1> &nbsp; sample portfolio</font></td>
         </tr>
         <dsp:include page="portfoliostatic.jsp" ></dsp:include>
       </dsp:oparam>
       
       <dsp:oparam name="broker">
         <tr>
           <td colspan=8 bgcolor=ffff99> &nbsp; 
            Client has no holdings.<br>
           <img src="images/d.gif" vspace=4></td>
         </tr>
       </table>
       </dsp:oparam>
     </dsp:droplet>

   </dsp:oparam>
 </dsp:droplet>
 </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/en/portfoliodynamic.jsp#2 $$Change: 651448 $--%>
