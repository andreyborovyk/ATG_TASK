<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/commerce/catalog/AdvProductSearch"/>
<dsp:importbean bean="/atg/commerce/catalog/RepositoryValues"/>
<dsp:importbean bean="/atg/projects/b2bstore/catalog/PartNumberSearchFormHandler"/>
<DECLAREPARAM NAME="noCrumbs" 
      CLASS="java.lang.String" 
      DESCRIPTION="This is for deciding what kind of breadcrumbs to display.
      If this is true, then breadcrumbs will show: Store Home|Search,
      instead of nav. history. Default is false."
      OPTIONAL>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" 詳細検索"/></dsp:include>


<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18> &nbsp; 
      <!-- breadcrumbs --> 
      <span class="small">
 <dsp:droplet name="Switch">
        <dsp:param name="value" param="noCrumbs"/>        
        <dsp:oparam name="true"><dsp:a href="../home.jsp">製品カタログ</dsp:a> &gt; 詳細検索</dsp:oparam>
        <dsp:oparam name="default"><dsp:param name="noCrumbs" value="false"/>
          <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/><dsp:param name="no_new_crumb" value="true"/></dsp:include>
        </dsp:oparam>
      </dsp:droplet>
    </span>  
    </td>
  </tr>
  <tr valign=top> 
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <!-- main content area -->
    <td valign="top" width=745> 
      <table border=0 cellpadding=4 width=80%>
        <tr><td colspan=2><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

        <tr valign=top>
          <td colspan=2><span class=big>検索</span>
          
          
          </td>
        </tr> 

        <tr>
          <td width="30"><dsp:img src="../images/d.gif"/></td> 
          <td>
            
            <dsp:getvalueof id="pval0" idtype="java.util.List" bean="PartNumberSearchFormHandler.searchResults">
            
              <dsp:include page="PartNumberSearchResults.jsp">
                <dsp:param name="Skus" value="<%=pval0%>"/> 
              </dsp:include>
            
            </dsp:getvalueof> 
            
          </td> 
        </tr>

        <tr> 
          <td></td>
          <td> 
          <dsp:droplet name="Compare">
            <dsp:param bean="PartNumberSearchFormHandler.resultSetSize" name="obj1"/>
            <dsp:param bean="PartNumberSearchFormHandler.maxResultsPerPage" name="obj2"/>
            <dsp:oparam name="greaterthan">
              次の検索結果の一部を<b><dsp:valueof bean="PartNumberSearchFormHandler.startCount"/> - <dsp:valueof bean="PartNumberSearchFormHandler.endIndex"/></b>を表示しています 
              <b><dsp:valueof bean="PartNumberSearchFormHandler.resultSetSize"/></b>
            </dsp:oparam>   
          </dsp:droplet>

          <dsp:droplet name="Switch">
           <dsp:param bean="PartNumberSearchFormHandler.resultPageCount" name="value"/>
           <dsp:oparam name="0">
           </dsp:oparam>           
           <dsp:oparam name="1">
           </dsp:oparam>
           <dsp:oparam name="default"><br>
             結果ページ： 

             <dsp:droplet name="Switch">
               <dsp:param name="value" bean="PartNumberSearchFormHandler.currentResultPageNum"/>
               <dsp:oparam name="1">
               
               </dsp:oparam>
               <dsp:oparam name="default">
                 <dsp:droplet name="For">
                   <dsp:param name="howMany" bean="PartNumberSearchFormHandler.resultPageCount"/>
                   <dsp:oparam name="output">
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" bean="PartNumberSearchFormHandler.currentResultPageNum"/>
                       <dsp:getvalueof id="countParam" idtype="Integer" param="count">
                         <dsp:oparam name="<%=countParam.toString()%>">
                           <dsp:a href="part_number_search.jsp" bean="PartNumberSearchFormHandler.currentResultPageNum" paramvalue="index">&lt;&lt; 戻る</dsp:a> &nbsp;
                         </dsp:oparam>
                       </dsp:getvalueof>
                     </dsp:droplet>
                   </dsp:oparam>
                 </dsp:droplet>
               </dsp:oparam>
             </dsp:droplet>

             <dsp:droplet name="For">
               <dsp:param bean="PartNumberSearchFormHandler.resultPageCount" name="howMany"/>
               <dsp:oparam name="output">
                 <dsp:droplet name="Switch">
                   <dsp:param bean="PartNumberSearchFormHandler.currentResultPageNum" name="value"/>
                   <dsp:getvalueof id="countParam" idtype="Integer" param="count">
                     <dsp:oparam name="<%=countParam.toString()%>">
                       <b><dsp:valueof param="count"/></b>
                     </dsp:oparam>
                   </dsp:getvalueof>
                   <dsp:oparam name="default">
                     <dsp:a href="part_number_search.jsp" bean="PartNumberSearchFormHandler.currentResultPageNum" paramvalue="count"><dsp:valueof param="count"/></dsp:a>
                   </dsp:oparam>
                 </dsp:droplet>
               </dsp:oparam>   
             </dsp:droplet>
             &nbsp;
             
             <dsp:droplet name="Switch">
               <dsp:param name="value" bean="PartNumberSearchFormHandler.currentResultPageNum"/>

	       <dsp:getvalueof id="pageCount" idtype="Integer" bean="PartNumberSearchFormHandler.resultPageCount">
               <oparam name="<%=pageCount.toString()%>">
               </oparam>
	       </dsp:getvalueof>

               <dsp:oparam name="default">
                 <dsp:droplet name="For">
                   <dsp:param name="howMany" bean="PartNumberSearchFormHandler.resultPageCount"/>
                   <dsp:oparam name="output">
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" bean="PartNumberSearchFormHandler.currentResultPageNum"/>
                       <dsp:getvalueof id="indexParam" idtype="Integer" param="index">
                         <dsp:oparam name="<%=indexParam.toString()%>">
                           <dsp:a href="part_number_search.jsp" bean="PartNumberSearchFormHandler.currentResultPageNum" paramvalue="count">次へ &gt;&gt;</dsp:a>
                         </dsp:oparam>
                       </dsp:getvalueof>
                     </dsp:droplet>
                   </dsp:oparam>
                 </dsp:droplet>
               </dsp:oparam>
             </dsp:droplet>
           </dsp:oparam>   
         </dsp:droplet></td>
        </tr>

        <tr>
          <td colspan=2>
          <!--search box starts here-->
          <table width=100% bgcolor="#FFCC66" border=0 cellpadding=0 cellspacing=0>
            <tr bgcolor="#666666">
              <td bgcolor="#666666" width=1><dsp:img src="../images/d.gif" width="1"/></td>
              <td colspan=2>
              <table cellpadding=3 cellspacing=0 border=0>
                <tr><td class=box-top>&nbsp;部品番号検索</td></tr>
              </table>
              </td>
            </tr>
            <tr bgcolor="#666666">
              <td bgcolor="#666666"><dsp:img src="../images/d.gif" width="1"/></td>
              <td>
                <dsp:form action="part_number_search.jsp" method="POST">
                <table width="100%" border="0" cellspacing="0" cellpadding="4" bgcolor="#FFCC66">
                  <tr><td colspan=2><dsp:img src= "../images/d.gif" height="10"/></td></tr>
                  <tr valign="top">
                     <td width="15%" align="right">
                      <span class="smallb">部品番号</span>
                     </td>
                     <td>
                       <dsp:input bean="PartNumberSearchFormHandler.searchInput" size="25" type="text"/>&nbsp;
                        <br> <span class="small">部品番号の一部で検索するには <b>*</b> を使用してください。</span>
                      </td>
                   </tr>
                   <tr>
                     <td>&nbsp;</td>
                     <td>
                        <input name="repositoryKey" type="hidden" value="<dsp:valueof bean="/OriginatingRequest.requestLocale.locale"/>">
                        <input name="noCrumbs" type="hidden" value="<dsp:valueof param="noCrumbs"/>"><br>
                        <dsp:input bean="PartNumberSearchFormHandler.search" type="hidden" value="Search"/>
                        <dsp:input bean="PartNumberSearchFormHandler.search" type="submit" value="検索"/>
                       </dsp:form></td>
                    </tr>
                    <tr>
                      <td>&nbsp;</td>
                      <td><span class=smallb><dsp:a href="simple_search.jsp">
                               <dsp:param name="noCrumbs" param="noCrumbs"/>
                               簡易検索フォームを使用する</dsp:a></span>
                      </td>
                     </tr> 
                     <tr>
                      <td>&nbsp;</td>
                      <td><span class=smallb><dsp:a href="advanced_search.jsp">
                               <dsp:param name="noCrumbs" param="noCrumbs"/>
                               製品詳細検索フォームを使用する</dsp:a></span>
                       </td>
                    <tr>
                    <td colspan=2><dsp:img src="../images/d.gif"/>
                  </table></td>
                <!-- this column is the gray border on the right-->
                <td bgcolor="#666666" width=1><dsp:img src="../images/d.gif" width="1"/></td>
             </tr>
             <!--this row is the gray border on the bottom-->
             <tr><td bgcolor="#666666" colspan=3><dsp:img src="../images/d.gif"/></td></tr>
              
             </table>

                </td>
             </tr>
          </table>
          <!--search box ends here-->



          </td>
        </tr>
      </table>
    </td>
  </tr>
  
 
</table>
</td>
</tr>

</body>
</html>


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/search/part_number_search.jsp#2 $$Change: 651448 $--%>
