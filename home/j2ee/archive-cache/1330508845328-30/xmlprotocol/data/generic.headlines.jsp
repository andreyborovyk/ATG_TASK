<%@ page language="java" %><?xml version="1.0" encoding="UTF-8"?>
<headline.response status="0">
<%String theCat = request.getParameter("CATEGORIES");
//Assumes we have 0-9 categories, otherwise the simple substring search will not work, i.e. '10' will
//be picked up with 'theCat.indexOf("1")'
if (theCat != null && theCat.indexOf("1")>=0){%>
	<category>
		<category.name>ROIMax News</category.name>
		<category.info id="1" bookmark="1"/>
		<headline>
			<headline.id>1</headline.id>
			<headline.caption>ROIMax Inc. Ships the ROIMax Connector v2.0, Adding Additional Product Flexibility</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>ROIMax Connector v2.0 features industry's only truly flexible product</headline.summary>
		</headline>

                <headline>
			<headline.id>2</headline.id>
			<headline.caption>ROIMax and Zydeco Launch Joint Product</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Based On successful small business application, product for individuals launched</headline.summary>
		</headline>

                <headline>
			<headline.id>3</headline.id>
			<headline.caption>ROIMax To Adopt Krystal Communications Technology</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Move enables wireless deployment of employee portal</headline.summary>
		</headline>



		
	</category>
<%}
if (theCat != null && theCat.indexOf("2")>=0){%>
	<category>
		<category.name>Business News</category.name>
		<category.info id="2" bookmark="1"/>
		<headline>
			<headline.id>4</headline.id>
			<headline.caption>Stocks Up As Earnings Announced</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>ROIMax and Geomatrix lead the way with their earnings announcements</headline.summary>
		</headline>

                <headline>
			<headline.id>5</headline.id>
			<headline.caption>Krystal Communications to Buy Taitel for $1.2 Billion</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Acquisition marks continued consolidation in the industry</headline.summary>
		</headline>

                <headline>
			<headline.id>6</headline.id>
			<headline.caption>Zydeco Raises Revenue Expectations</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Key alliances open doors to new markets</headline.summary>
		</headline>
	</category>
<%}
if (theCat != null && theCat.indexOf("3")>=0){%>
	<category>
		<category.name>Financial News</category.name>
		<category.info id="2" bookmark="1"/>
		<headline>
			<headline.id>7</headline.id>
			<headline.caption>Reforms Boost Tokyo's Nikkei</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Comprehensive building blocks fuel investor optimism</headline.summary>
		</headline>

                <headline>
			<headline.id>8</headline.id>
			<headline.caption>Techs Rally as House Ups Chip Stock</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>NASDAQ Composite Index rises with upgrades of chip stocks</headline.summary>
		</headline>

                <headline>
			<headline.id>9</headline.id>
			<headline.caption>Dow and Nasdaq Close Up</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>The Dow Jones Industrial Average and NASDAQ Composite Index close up for the day</headline.summary>
		</headline>
	</category>
<%}
if (theCat == null){%>
	<category>
		<category.name>ROIMax News</category.name>
		<category.info id="1" bookmark="1"/>
		<headline>
			<headline.id>1</headline.id>
			<headline.caption>ROIMax Inc. Ships the ROIMax Connector v2.0, Adding Additional Product Flexibility</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>ROIMax Connector v2.0 features industry's only truly flexible product</headline.summary>
		</headline>

                <headline>
			<headline.id>2</headline.id>
			<headline.caption>ROIMax and Zydeco Launch Joint Product</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Based On successful small business application, product for individuals launched</headline.summary>
		</headline>

                <headline>
			<headline.id>3</headline.id>
			<headline.caption>ROIMax To Adopt Krystal Communications Technology</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Move enables wireless deployment of employee portal</headline.summary>
		</headline>
		
	</category>

        <category>
		<category.name>Business News</category.name>
		<category.info id="2" bookmark="1"/>
		<headline>
			<headline.id>4</headline.id>
			<headline.caption>Stocks Up As Earnings Announced</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>ROIMax and Geomatrix lead the way with their earnings announcements</headline.summary>
		</headline>

                <headline>
			<headline.id>5</headline.id>
			<headline.caption>Krystal Communications to Buy Taitel for $1.2 Billion</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Acquisition marks continued consolidation in the industry</headline.summary>
		</headline>

                <headline>
			<headline.id>6</headline.id>
			<headline.caption>Zydeco Raises Revenue Expectations</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Key alliances open doors to new markets</headline.summary>
		</headline>
	</category>    

        <category>
		<category.name>Financial News</category.name>
		<category.info id="2" bookmark="1"/>
		<headline>
			<headline.id>7</headline.id>
			<headline.caption>Reforms Boost Tokyo's Nikkei</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>Comprehensive building blocks fuel investor optimism</headline.summary>
		</headline>

                <headline>
			<headline.id>8</headline.id>
			<headline.caption>Techs Rally as House Ups Chip Stock</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>NASDAQ Composite Index rises with upgrades of chip stocks</headline.summary>
		</headline>

                <headline>
			<headline.id>9</headline.id>
			<headline.caption>Dow and Nasdaq Close Up</headline.caption>
			<headline.source>ATG Press</headline.source>
			<headline.byline>ROIMax</headline.byline>
			<headline.date>20010821</headline.date>
			<headline.summary>The Dow Jones Industrial Average and NASDAQ Composite Index close up for the day</headline.summary>
		</headline>
	</category>
        
	
<%}%>
</headline.response>
<%-- @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocol.war/data/generic.headlines.jsp#2 $$Change: 651448 $--%>
