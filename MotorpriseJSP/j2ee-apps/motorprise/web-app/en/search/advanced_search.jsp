<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/commerce/catalog/AdvProductSearch"/>
<dsp:importbean bean="/atg/commerce/catalog/RepositoryValues"/>

<DECLAREPARAM NAME="noCrumbs" 
      CLASS="java.lang.String" 
      DESCRIPTION="This is for deciding what kind of breadcrumbs to display.
      If this is true, then breadcrumbs will show: Store Home|Search,
      instead of nav. history. Default is false."
      OPTIONAL>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Advanced Search"/></dsp:include>

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
        <dsp:oparam name="true"><dsp:a href="../home.jsp">Product Catalog</dsp:a> &gt; Advanced Search</dsp:oparam>
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
          <td colspan=2><span class=big>Search</span></td>
        </tr>

        <tr>
          <td width="30"><dsp:img src="../images/d.gif"/></td> 
          <td>
          
            <dsp:getvalueof id="pval0" idtype="java.util.List" bean="AdvProductSearch.searchResults">
              <dsp:include page="AdvSearchResults.jsp">
                <dsp:param name="ResultList" value="<%=pval0%>"/>
              </dsp:include>
            </dsp:getvalueof>
            
          </td> 
        </tr>

        <tr> 
          <td></td>
          <td> 
          <dsp:droplet name="Compare">
            <dsp:param bean="AdvProductSearch.resultSetSize" name="obj1"/>
            <dsp:param bean="AdvProductSearch.maxResultsPerPage" name="obj2"/>
            <dsp:oparam name="greaterthan">
              Now viewing results 
              <b><dsp:valueof bean="AdvProductSearch.startCount"/> - 
              <dsp:valueof bean="AdvProductSearch.endIndex"/></b>
              out of 
              <b><dsp:valueof bean="AdvProductSearch.resultSetSize"/></b>
            </dsp:oparam>   
          </dsp:droplet>

          <dsp:droplet name="Switch">
           <dsp:param bean="AdvProductSearch.resultPageCount" name="value"/>
           <dsp:oparam name="1">
           </dsp:oparam>
           
           <dsp:oparam name="default">
            <br>Results pages: 
            <dsp:droplet name="Switch">
               <dsp:param name="value" bean="AdvProductSearch.currentResultPageNum"/>
               <dsp:oparam name="1">
               
               </dsp:oparam>
               <dsp:oparam name="default">
                 <dsp:droplet name="For">
                   <dsp:param name="howMany" bean="AdvProductSearch.resultPageCount"/>
                   <dsp:oparam name="output">
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" bean="AdvProductSearch.currentResultPageNum"/>
                       <dsp:getvalueof id="countParam" idtype="Integer" param="count">
                         <dsp:oparam name="<%=countParam.toString()%>">
                           <dsp:a href="advanced_search.jsp" bean="AdvProductSearch.currentResultPageNum" paramvalue="index">&lt;&lt; Previous</dsp:a> &nbsp;
                         </dsp:oparam>
                       </dsp:getvalueof>
                     </dsp:droplet>
                   </dsp:oparam>
                 </dsp:droplet>
               </dsp:oparam>
             </dsp:droplet>

             <dsp:droplet name="For">
               <dsp:param bean="AdvProductSearch.resultPageCount" name="howMany"/>
               <dsp:oparam name="output">
                 <dsp:droplet name="Switch">
                   <dsp:param bean="AdvProductSearch.currentResultPageNum" name="value"/>
                   <dsp:getvalueof id="countParam" idtype="Integer" param="count">
                     <dsp:oparam name="<%=countParam.toString()%>">
                       <b><dsp:valueof param="count"/></b>
                     </dsp:oparam>
                   </dsp:getvalueof>
                   <dsp:oparam name="default">
                     <dsp:a bean="AdvProductSearch.currentResultPageNum" href="advanced_search.jsp" paramvalue="count"><dsp:valueof param="count"/></dsp:a>
                   </dsp:oparam>
                 </dsp:droplet>
               </dsp:oparam>   
             </dsp:droplet>

             &nbsp;
             <dsp:droplet name="Switch">
               <dsp:param name="value" bean="AdvProductSearch.currentResultPageNum"/>

	       <dsp:getvalueof id="pageCount" idtype="Integer" bean="AdvProductSearch.resultPageCount">
               <oparam name="<%=pageCount.toString()%>">
               </oparam>
	       </dsp:getvalueof>

               <dsp:oparam name="default">
                 <dsp:droplet name="For">
                   <dsp:param name="howMany" bean="AdvProductSearch.resultPageCount"/>
                   <dsp:oparam name="output">
                     <dsp:droplet name="Switch">
                       <dsp:param name="value" bean="AdvProductSearch.currentResultPageNum"/>
                       <dsp:getvalueof id="indexParam" idtype="Integer" param="index">
                         <dsp:oparam name="<%=indexParam.toString()%>">
                       
                           <dsp:a href="advanced_search.jsp" bean="AdvProductSearch.currentResultPageNum" paramvalue="count">Next &gt;&gt;</dsp:a>
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
                <tr><td class=box-top>&nbsp;Advanced Product Search</td></tr>
              </table>
              </td>
            </tr>

            <tr>
              <td bgcolor="#666666" width=1><dsp:img src="../images/d.gif" width="1"/></td>
              <td>
              <table width=100% bgcolor="#FFCC66" border=0 cellpadding=0 cellspacing=0>
                <tr valign=top>
                  <td width=50%>
                    <dsp:form action="advanced_search.jsp" method="POST">
                    <table width="100%" border="0" cellspacing="0" cellpadding="4" bgcolor="#FFCC66">
                      <tr><td colspan=2><dsp:img src= "../images/d.gif" height="10"/></td></tr>
                      
                      <tr valign=top>
                        <td width="15%" align="right"><span class="smallb">Text</span></td>
                        <td><dsp:input bean="AdvProductSearch.searchInput" size="25" type="text"/>&nbsp; </span>
                        </td>
                      </tr>
                      <tr>
                        <td align="right"><span class=smallb>Category</span></td>
                        <td>        
                        <dsp:select bean="AdvProductSearch.hierarchicalCategoryId">
                          <dsp:option value=""/>-- All categories --
                          <dsp:droplet name="RepositoryValues">
                            <dsp:param name="itemDescriptorName" value="category"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="ForEach">
                                <dsp:param name="array" param="values"/>
                                <dsp:param name="sortProperties" value="+displayName"/>
                                <dsp:oparam name="output">
                                  <dsp:getvalueof id="elem" idtype="atg.repository.RepositoryItem" param="element">
                                    <dsp:option value="<%=elem.getRepositoryId()%>"/>
                                    <dsp:valueof param="element.displayName"/>
                                  </dsp:getvalueof>
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                        </dsp:select>
                        </td>
                      </tr> 
                      <tr>
                        <td align="right">&nbsp;<span class=smallb>Manufacturer</span></td>
                        <td>
                        <dsp:select bean="AdvProductSearch.propertyValues.manufacturer">
                          <dsp:option value=""/>-- Any --
                          <dsp:droplet name="RepositoryValues">
                            <dsp:param name="itemDescriptorName" value="product"/>
                            <dsp:param name="propertyName" value="manufacturer"/>
                            <dsp:oparam name="output">
                              <dsp:droplet name="ForEach">
                                <dsp:param name="array" param="values"/>
                                <dsp:param name="sortProperties" value="+displayName"/>
                                <dsp:oparam name="output">
                                
                                  <dsp:getvalueof id="elemName" idtype="java.lang.String" param="element.displayName">                                
                                    <dsp:option value="<%=elemName%>"/>
                                    <dsp:valueof param="element.displayName"/>
                                  </dsp:getvalueof>
                                  
                                </dsp:oparam>
                              </dsp:droplet>
                            </dsp:oparam>
                          </dsp:droplet>
                        </dsp:select> 
                        </td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td><br>
                        <dsp:input bean="AdvProductSearch.currentResultPageNum" type="hidden" value="1"/>
                        <input name="repositoryKey" type="hidden" value="<dsp:valueof bean="/OriginatingRequest.requestLocale.locale"/>">
                        <input name="noCrumbs" type="hidden" value="<dsp:valueof param="noCrumbs"/>">
                        <dsp:input bean="AdvProductSearch.search" type="hidden" value="Search"/>
                        <dsp:input bean="AdvProductSearch.search" type="submit" value="Search"/>
                        </td>
                      </tr>
                      <tr>
                        <td colspan=2>&nbsp;</td>
                      </tr>
                      <tr>
                        <td>&nbsp;</td>
                        <td><span class=smallb><dsp:a href="simple_search.jsp">
                               <dsp:param name="noCrumbs" param="noCrumbs"/>
                               Use simple search form</dsp:a></span>
                        </td>
                      </tr> 
                        <td>&nbsp;</td>
                        <td><span class=smallb><dsp:a href="part_number_search.jsp">
                               <dsp:param name="noCrumbs" param="noCrumbs"/>
                               Use part number search form</dsp:a></span>
                        </td>
                      <tr>
                      </tr>
                    </table>
                    </dsp:form> 
                    </td>

                    <td width=50%>
                    </td>

                  </tr>
                </table>

                </td>
                <!--this column is the gray border on the right-->
                <td bgcolor="#666666" width=1><dsp:img src="../images/d.gif" width="1"/></td>
             </tr>
             <!--this row is the gray border on the bottom-->
             <tr><td bgcolor="#666666" colspan=3><dsp:img src="../images/d.gif"/></td></tr>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/search/advanced_search.jsp#2 $$Change: 651448 $--%>
