<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%
/* -------------------------------------------------
 * This jsp snippet is responsible for maintaining and displaying navigation
 * history.  Navigation history is the path from the current location in the
 * catalog up to the top of the catalog as travelled by the user.
 * ------------------------------------------------- */
%>

<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistory"/>
<dsp:importbean bean="/atg/commerce/catalog/CatalogNavHistoryCollector"/>



<DECLAREPARAM NAME="no_new_crumb" 
              CLASS="java.lang.String" 
              DESCRIPTION="If true, then the current page will be treated as a
              navigational non-entity.  The breadcrumb list will be maintained
              as it was on the previous page view and the last item in the
              breadcrumb list will be rendered as a link."
              OPTIONAL>

<DECLAREPARAM NAME="displaybreadcrumbs" 
              CLASS="java.lang.String" 
              DESCRIPTION="If true the breadcrumb list will be rendered."
              OPTIONAL>

<DECLAREPARAM NAME="element" 
              CLASS="java.lang.Object" 
              DESCRIPTION="The element to stick to the end of the breadcrumb
              list.  Usually a RepositoryItem."
              REQUIRED>

<DECLAREPARAM NAME="navAction" 
              CLASS="java.lang.String" 
              DESCRIPTION="Describes how to use the element to manipulate the
              breadcrumb stack.  Choices are push, pop, and jump.  Null treated
              as push."
              OPTIONAL>

<%
/* -------------------------------------------------
 * Use NavHistoryCollector droplet to collect breadcrumbs
 * ------------------------------------------------- */
%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" param="no_new_crumb"/>
  <dsp:oparam name="true"> 
  </dsp:oparam>
  <dsp:oparam name="default">
     <dsp:droplet name="CatalogNavHistoryCollector">
        <dsp:param name="navAction" param="navAction"/>
        <dsp:param name="item" param="element"/> 
     </dsp:droplet> 
  </dsp:oparam>
</dsp:droplet>



<%
/* -------------------------------------------------
 * use the ForEach droplet to render the navHistory array.
 * ------------------------------------------------- */
%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" param="displaybreadcrumbs"/>
  <dsp:oparam name="true"> 

    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
      <dsp:param bean="CatalogNavHistory.navHistory" name="array"/>
      <dsp:param name="elementName" value="crumb"/>
      <dsp:oparam name="output">
      
        <dsp:droplet name="/atg/dynamo/droplet/Switch"> 
          <%
          /* -------------------------------------------------
           * We want to put a separator between the items in the navHistory.  In this
           * example we put | sign between them.  We use a switch droplet to
           * identify the first item in the array because we don't want to render a
           * separator, but a link to Store Home before the first item. 
           * ------------------------------------------------- */
          %>
          <dsp:param name="value" param="count"/>

          <dsp:oparam name="1">
            &nbsp; <dsp:a href="../home.jsp">製品カタログ</dsp:a> &gt; 
          </dsp:oparam>

          <dsp:oparam name="default">      
            &gt;
          </dsp:oparam>

        </dsp:droplet>
  
        <dsp:droplet name="/atg/dynamo/droplet/IsNull"> 
          <dsp:param name="value" param="crumb"/>

          <dsp:oparam name="true">
             要素が null です
          </dsp:oparam>

          <dsp:oparam name="false">                      
            <dsp:droplet name="/atg/dynamo/droplet/Switch">
              <%
              /* -------------------------------------------------
               * Use a switch droplet to compare size to count. When
               * they are the same, then we are on the last item in 
               * array iterated by the ForEach.
               * ------------------------------------------------- */
              %>
              <dsp:param name="value" param="size"/>
              
              <dsp:getvalueof id="countParam" idtype="Integer" param="count">
              <dsp:oparam name="<%=countParam.toString()%>"> 
                <dsp:droplet name="/atg/dynamo/droplet/Switch">
                  <%
                  /* -------------------------------------------------
                   * The last item in the list is generally the item we are
                   * currently visiting and should therefore not be a link. 
                   * In some cases, when we do not want to add a new breadcrumb,              
                   * we want the last item to be a link.  We do this on the
                   * shopping cart page, search page, and others.  This is 
                   * indicated by the "no_new_crumb" parameter. 
                   * ------------------------------------------------- */
                  %>
                  <dsp:param name="value" param="no_new_crumb"/>

                  <dsp:oparam name="true">
		    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="crumb.template.url">
                    <dsp:a page="<%=urlStr%>">
                      <dsp:param name="id" param="crumb.repositoryId"/>
                      <dsp:param name="navAction" value="pop"/>
                      <dsp:param name="Item" param="crumb"/>
                      <dsp:valueof param="crumb.displayName">名前なし</dsp:valueof>
		    </dsp:a>                    
		    </dsp:getvalueof>
                  </dsp:oparam>

                  <dsp:oparam name="default">
                    <dsp:valueof param="crumb.displayName"/>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
              </dsp:getvalueof>
              
              <dsp:oparam name="default">      
	        <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="crumb.template.url">
                <dsp:a page="<%=urlStr%>">
		  <dsp:param name="id" param="crumb.repositoryId"/>
		  <dsp:param name="navAction" value="pop"/>
		  <dsp:param name="Item" param="crumb"/>
		  <dsp:valueof param="crumb.displayName">名前なし</dsp:valueof>
               </dsp:a>
               </dsp:getvalueof>
              </dsp:oparam>
            </dsp:droplet> 

          </dsp:oparam>
        </dsp:droplet>

      </dsp:oparam>
    </dsp:droplet> <%/* end ForEach */%>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/breadcrumbs.jsp#2 $$Change: 651448 $--%>
