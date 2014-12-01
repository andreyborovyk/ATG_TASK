<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>
<tr bgcolor=#ffff99>   
  <td> &nbsp; <font size=-1><dsp:a href="fund.jsp?ElementId=%2Frepositories%2FFunds%2Fen_US%2Fshorttermbond.xml">QSSTB</dsp:a></font></td>
  <td>&nbsp;</td> 
  <td><font size=-1>150</font></td> 
  <td>&nbsp;</td> 
  <td><font size=-1>
    <dsp:setvalue param="price" value="<%=new Double(32.58)%>"/>
    <dsp:valueof param="price" converter="euro" symbol="&euro;"/>
    </font>
  </td> 
  <td>&nbsp;</td> 
  <td align=right><font size=-1>
    <dsp:setvalue param="total" value="<%=new Double(4887.00)%>"/>
    <dsp:valueof param="total" converter="euro" symbol="&euro;"/>
     </font>
  </td>
  <td>&nbsp;</td> 
</tr> 
  
<tr bgcolor=#ffff99>   
  <td> &nbsp; <font size=-1><dsp:a href="fund.jsp?ElementId=%2Frepositories%2FFunds%2Fen_US%2Fhighyieldbond.xml">QUIHB</dsp:a></font></td>
  <td>&nbsp;</td> 
  <td>
   <font size=-1>250</font>
  </td> 
  <td>&nbsp;</td> 
  <td><font size=-1>
      <dsp:setvalue param="price" value="<%=new Double(28.23)%>"/>
      <dsp:valueof param="price" converter="euro" symbol="&euro;"/>
      </font>
  </td> 
  <td>&nbsp;</td> 
  <td align=right>
   <font size=-1>
    <dsp:setvalue param="total" value="<%=new Double(7057.50)%>"/>
    <dsp:valueof param="total" converter="euro" symbol="&euro;"/>             
   </font>
  </td>
  <td>&nbsp;</td> 
</tr> 
  
<tr bgcolor=#ffff99>   
  <td> &nbsp; <font size=-1><dsp:a href="fund.jsp?ElementId=%2Frepositories%2FFunds%2Fen_US%2Fmoneymarket.xml">QUISB</dsp:a></font></td>
  <td>&nbsp;</td> 
  <td><font size=-1>320</font></td> 
  <td>&nbsp;</td> 
  <td>
   <font size=-1>
      <dsp:setvalue param="price" value="<%=new Double(19.56)%>"/>
      <dsp:valueof param="price" converter="euro" symbol="&euro;"/>
   </font>
  </td> 
  <td>&nbsp;</td> 
  <td align=right>
   <font size=-1>
    <dsp:setvalue param="total" value="<%=new Double(6259.20)%>"/>
    <dsp:valueof param="total" converter="euro" symbol="&euro;"/>
   </font>
  </td>
  <td>&nbsp;</td> 
</tr> 
  
<tr bgcolor=#ffff99>   
  <td> &nbsp; <font size=-1><dsp:a href="fund.jsp?ElementId=%2Frepositories%2FFunds%2Fen_US%2Fventure.xml">QUIVF</dsp:a></font></td>
  <td>&nbsp;</td> 
  <td><font size=-1>280</font></td> 
  <td>&nbsp;</td> 
  <td>
   <font size=-1>
      <dsp:setvalue param="price" value="<%=new Double(29.56)%>"/>
      <dsp:valueof param="price" converter="euro" symbol="&euro;"/>
   </font>
  </td> 
  <td>&nbsp;</td> 
  <td align=right>
   <font size=-1>
    <dsp:setvalue param="total" value="<%=new Double(8276.80)%>"/>
    <dsp:valueof param="total" converter="euro" symbol="&euro;"/>
   </font>
  </td>
  <td>&nbsp;</td> 
</tr> 

<tr> 
  <td bgcolor=#ffff99 colspan=6><img src="images/clear.gif" height=1></td> 
  <td colspan=2 bgcolor=#003366><img src="images/clear.gif" height=1></td>
</tr>

<tr bgcolor=#ffff99>
  <td colspan=6>&nbsp;</td>
  <td align=right>
   <font size=-1>
    <b>
    <dsp:setvalue param="total" value="<%=new Double(26480.50)%>"/>
     <dsp:valueof param="total" converter="euro" symbol="&euro;"/>
    </b>
   </font>
  </td> 
  <td>&nbsp;</td>
</tr> 
<tr bgcolor=#ffff99>
  <td colspan=8><img src="images/d.gif" vspace=2></td>
</tr>
  
</table>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/de/portfoliostatic.jsp#2 $$Change: 651448 $--%>
