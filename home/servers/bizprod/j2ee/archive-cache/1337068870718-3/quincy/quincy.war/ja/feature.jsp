<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<DECLAREPARAM NAME="ElementId" CLASS="java.lang.String" DESCRIPTION="Id of the repository element to retrieve">
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<HTML> <HEAD>

<dsp:droplet name="/atg/targeting/RepositoryLookup">
  <dsp:param bean="/atg/demo/QuincyFunds/repositories/Features/Features" name="repository"/>
  <dsp:param name="itemDescriptor" value="feature"/>
  <dsp:param name="id" param="ElementId"/>
  <dsp:oparam name="output">
    <TITLE>特別提供品： <dsp:valueof param="element.title"/></TITLE>
    </HEAD>
    <body bgcolor=#ffffff text=#000000 link=#003366 vlink=#003366>

    <table border=0 cellpadding=4 cellspacing=0>
      <tr valign=top>
        <td width=120 bgcolor=#003366 rowspan=2>
        <!-- left bar navigation -->
        <dsp:include page="nav.jsp" ></dsp:include></td>
      
        <td>
        <table border=0>
          <tr>
            <td colspan=2><img src="images/banner-quincy-small.gif" hspace=20 vspace=4></td>
          </tr>
          <tr valign=top>
            <td>
            <table border=0 width=500 cellpadding=4 cellspacing=0>
              <tr>
                <td><img src="images/d.gif" hspace=4></td>
                <td>
                <h2>特別提供品</h2>
                <dsp:a href="featurelist.jsp">特別提供品リスト</dsp:a>
                <p>
                <font size=+2><dsp:valueof param="element.title"/></font><br>
                <dsp:valueof param="element.headline"/><p>
        
                <dsp:valueof param="element.author"/>
                <p>
               <!-- insert feature article main body -->    
               <%-- <dsp:getvalueof id="srcval" param="element.relativePath"><dsp:include page="<%=(String)srcval %>" staticcharset="Shift_JIS" ></dsp:include> </dsp:getvalueof> --%>
               <dsp:valueof valueishtml="true" param="element.featureContent"/>
               </td>
             </tr>
           </table>
           </td>
         </tr>
       </table>
       </td>
     </tr>
    </table>
  </dsp:oparam>
  <dsp:oparam name="empty">
    このページには、閲覧項目のリポジトリ ID を含むリンクを使用して
    アクセスする必要があります。
  </dsp:oparam>
</dsp:droplet>





</BODY> </HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/ja/feature.jsp#2 $$Change: 651448 $--%>
