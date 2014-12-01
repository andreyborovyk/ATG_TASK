<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/pricing/priceLists/ComplexPriceDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/For"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"/>

<dsp:droplet name="Switch">
      <dsp:param name="value" param="pricingScheme"/>
      <dsp:oparam name="bulkPrice">
        <tr>
          <td>&nbsp;</td> 
          <td valign=top><span class=smallb>ÉoÉãÉNâøäiÅF</span></td>
          <td>&nbsp;</td>
          <td>
            <table border=0 cellspacing=0 cellpadding=0 width=100%>
        
        <dsp:droplet name="ComplexPriceDroplet">
          <dsp:param name="complexPrice" param="complexPrice"/>
          <dsp:oparam name="output">
            <dsp:droplet name="For">
              <dsp:param name="howMany" param="numLevels"/>
              <dsp:param name="indexName" value="index"/>
                <dsp:oparam name="output">
                  <tr>
                    <td><span class=small>
                        <dsp:valueof param="levelMinimums[param:index]"/>
                    </span></td>
                    <td>
                        &nbsp;-&nbsp;
                    </td>
                    <td><span class=small>
                        <dsp:valueof param="levelMaximums[param:index]">à»è„</dsp:valueof>
                    </span></td>
                    <td>
                      
                      <%/*Display price for locale of user: */%>
                      <dsp:droplet name="CurrencyConversionFormatter">
                          <dsp:param name="currency" param="prices[param:index]"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="locale"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="targetLocale"/>
                          <dsp:param name="euroSymbol" value="&euro;"/>
                          <dsp:oparam name="output">
                            &nbsp;&nbsp;<span class=small><dsp:valueof valueishtml="<%=true%>" param="formattedCurrency">âøäiÇ»Çµ</dsp:valueof></span>
                          </dsp:oparam>
                        </dsp:droplet>
                       
                        <dsp:droplet name="Switch">
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="value"/>
                         
                          <dsp:oparam name="de_DE_EURO">

                        
                        <dsp:droplet name="CurrencyConversionFormatter">
                          <dsp:param name="currency" param="prices[param:index]"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="locale"/>
                          <dsp:param name="targetLocale" value="de_DE"/>
                          <dsp:oparam name="output">
                            <span class=small>(<dsp:valueof param="formattedCurrency">âøäiÇ»Çµ</dsp:valueof>)</span>
                          </dsp:oparam>
                        </dsp:droplet>
                        </dsp:oparam>
                        </dsp:droplet>
                     </td>
                  </tr>
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>
        </table>
        </td>
      </tr>  
    </dsp:oparam><!--  ***************************************************** End of Bulk Pricing -->
      
    <dsp:oparam name="tieredPrice">
       <tr>
        <td>&nbsp;</td>
        <td valign=top><span class=smallb>äKëwï âøäiÅF</span></td>
        <td>&nbsp;</td>
        <td>
          <table border=0 cellspacing=0 cellpadding=0 width=100%>
            <dsp:droplet name="ComplexPriceDroplet">
              <dsp:param name="complexPrice" param="complexPrice"/>
              <dsp:oparam name="output">
                <dsp:droplet name="For">
                  <dsp:param name="howMany" param="numLevels"/>
                  <dsp:param name="indexName" value="index"/>
                  <dsp:oparam name="output">
                    <tr>
                      <td><span class=small>
                          <dsp:valueof param="levelMinimums[param:index]"/>
                      </span> </td>
                      <td>
                          &nbsp;-&nbsp;
                      </td>
                      <td><span class=small>
                          <dsp:valueof param="levelMaximums[param:index]">à»è„</dsp:valueof>
                      </span></td>
                      <td>
                        &nbsp;&nbsp;
                        <dsp:droplet name="CurrencyConversionFormatter">
                          <dsp:param name="currency" param="prices[param:index]"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="locale"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="targetLocale"/>
                          <dsp:param name="euroSymbol" value="&euro;"/>
                          <dsp:oparam name="output">
                            <span class=small><dsp:valueof valueishtml="<%=true%>" param="formattedCurrency">âøäiÇ»Çµ</dsp:valueof></span>
                          </dsp:oparam>
                        </dsp:droplet>
                        <%/*Don't display for en directory */%>
                        <%/*
                        <dsp:droplet name="CurrencyConversionFormatter">
                          <dsp:param name="currency" param="prices[param:index]"/>
                          <dsp:param bean="/atg/userprofiling/Profile.priceList.locale" name="locale"/>
                          <dsp:param name="targetLocale" value="de_DE"/>
                          <dsp:oparam name="output">
                            <span class=small>(<dsp:valueof param="formattedCurrency">no price</dsp:valueof>)</span>
                          </dsp:oparam>
                        </dsp:droplet>*/%>
                      </td>
                  </tr>
                  </dsp:oparam>
                </dsp:droplet>
              </dsp:oparam>
            </dsp:droplet><!--  *******************************************End if Tiered Pricing -->     
          </table>
        </td>
      </tr>      
    </dsp:oparam>  
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/catalog/DisplayComplexPrice.jsp#2 $$Change: 651448 $--%>
