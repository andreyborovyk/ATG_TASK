<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>

<% Thread.sleep(5000); %>

{updates:[{
  "id":"selectNode",
  "html":'<select id="brand_id" name="brand_id" class="small" MULTIPLE SIZE=5> <option value="-1">Show all brands</option> <option value="1" selected>- Average All Brands -</option> <option value="35">Anheuser-Busch</option><option value="57">Bacardi</option> <option value="1850">Baja Bob\'s</option> <option value="77">Bartles &amp; Jaymes</option> <option value="2610">Barton</option> <option value="2360">Black Haus</option> <option value="118">Boone\'s</option> <option value="2372">Buchannan\'s</option> <option value="2367">Bulliet</option><option value="2366">Bulloch / Ushers</option> <option value="1597">Campari</option> <option value="1218">Captain Morgan</option> <option value="2355">Ciroc</option> <option value="2374">Cragganmore</option> <option value="2358">Crown Royal</option> <option value="1219">Cruzan Island Cocktails</option> <option value="2371">Dalwhinnie</option> <option value="2354">Don Julio</option><option value="2365">George Dickel</option> <option value="2362">Goldschlagger</option> <option value="2357">Gordon\'s</option> <option value="1220">Heublein Premium Classics</option> <option value="460">Hooper\'s Hooch</option> <option value="489">Jack Daniel\'s</option> <option value="1709">Jim Beam</option> <option value="2368">Johnnie Walker</option> <option value="508">Jose Cuervo</option><option value="2373">Lagavulin</option> <option value="1208">Malibu Rum</option> <option value="638">Mike\'s</option> <option value="2352">Myer\'s</option> <option value="2369">Oban</option> <option value="2487">Peels</option> <option value="2353">Ron Anejo</option> <option value="2359">Rumpleminze</option> <option value="1221">Sauza Diablo</option><option value="2364">Scoresby</option> <option value="887">Seagrams</option> <option value="915">Smirnoff</option> <option value="1224">Stolichnaya</option> <option value="2370">Talisker</option> <option value="2356">Tanqueray</option> <option value="1011">The Club</option> <option value="1552">Two Dogs</option> </select>'
  },
  {
    "id":"checkBox",
    "html":'<input type="checkbox"><label>Enabled</label>'
    }
  ]
}

</dsp:page>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/atgFunctionsTests/xhr/select.jsp#2 $$Change: 651448 $--%>
