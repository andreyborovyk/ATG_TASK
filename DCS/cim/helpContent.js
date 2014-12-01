{
  "helpItem":
	[
	/* Product and add-on selection help topics */
	{"id":"ProductSelectTask.products.b2ccommerce","content":"Configure the ATG platform (Adaptive Scenario Engine), ATG Content Administration, and ATG B2C Commerce.\n\nSee also: ATG Installation and Configuration Guide; ATG Content Administration Programming Guide, ATG Commerce Programming Guide"},
	 
	/* Add-on selection help topics */	
	{"id":"AddOnSelectTask.addOns.merch","content":"If you select Merchandising as an add-on, the appropriate module is included in your application EAR file. ATG Merchandising must be installed in order to be selected."},
	{"id":"AddOnSelectTask.addOns.customcatalog","content":"Prepares your installation for use with ATG Commerce custom catalogs."},
	{"id":"/atg/commerce/search/config/RemoteCatalogRankConfigAdapter_properties.remoteHost","content":"Enter the name of the server where your rank config adapter will run. This adapter applies rank config information after your content has been indexed. It normally runs on the publishing server."},
	{"id":"/atg/commerce/search/config/RemoteCatalogRankConfigAdapter_properties.remotePort","content":"Enter the RMI port used to connect to the server on which your rank config adapter will run (this will be either your production or staging server). This value cannot be blank. If you do not have a remote Search host, do not use your local search port for this setting, but set this value to an unused port."},
	
	{"id":"/atg/commerce/search/refinement/RemoteCatalogRefineConfigAdapter_properties.remoteHost","content":"Enter the name of the production server running your RemoteCatalogRefineConfigAdapter component."},
	{"id":"/atg/commerce/search/refinement/RemoteCatalogRefineConfigAdapter_properties.remotePort","content":"Enter the port number of the production server running your RemoteCatalogRefineConfigAdapter component."},
	{"id":"/atg/commerce/search/refinement/CatalogRefineConfigAdapter_properties.componentPath","content":"This configures the component on the production server. Enter the path to your local component:/n/n   //atg//commerce//search//refinement//LocalCatalogRefineConfigAdapter"},
	
	{"id":"/atg/commerce/search/refinement/RemoteCatalogRefineConfigAdapter_staging_properties.remoteHost","content":"Enter the name of the staging server running your RemoteCatalogRefineConfigAdapter component."},
	{"id":"/atg/commerce/search/refinement/RemoteCatalogRefineConfigAdapter_staging_properties.remotePort","content":"Enter the port number of the staging server running your RemoteCatalogRefineConfigAdapter component."},
	{"id":"/atg/commerce/search/refinement/CatalogRefineConfigAdapter_staging_properties.componentPath","content":"Enter the path to your local component:/n/n   //atg//commerce//search//refinement//LocalCatalogRefineConfigAdapter"},
			
	{"id":"/atg/commerce/search/config/CatalogRankConfigAdapter_properties.componentPath","content":"Enter the path to your local component: /n/n    //atg//commerce//search//config//LocalCatalogRankConfigAdapter"},
	{"id":"/atg/commerce/search/config/SearchUpdateAdapter_properties.componentPath","content":"Enter the path to your local component: /n/n    //atg//commerce//search//config//LocalSearchUpdateAdapter"},
	
	
	{"id":"/atg/commerce/search/config/RemoteCatalogRankConfigAdapter_staging_properties.remoteHost","content":"Enter the name of the staging server where your rank config adapter will run. This adapter applies rank config information after your content has been indexed."},
	{"id":"/atg/commerce/search/config/RemoteCatalogRankConfigAdapter_staging_properties.remotePort","content":"Enter the RMI port used to connect to the staging server on which your rank config adapter will run. This value cannot be blank. If you do not have a remote Search host, do not use your local search port for this setting, but set this value to an unused port."},
	
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_properties.remoteHost","content":"Enter the name of the production server."},
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_properties.remotePort","content":"Enter the RMI port used to communicate with the production server."},
	
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_staging_properties.remoteHost","content":"Enter host name of the staging server."},
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_staging_properties.remotePort","content":"Enter the port number the asset management server should use to communicate with the RemoteSearchUpdateAdapter component on the staging server."},
	
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_properties.remoteHost","content":"Enter the host name of the production server."},
	{"id":"/atg/commerce/search/config/RemoteSearchUpdateAdapter_properties.remotePort","content":"Enter the port number the asset management server should use to communicate with the RemoteSearchUpdateAdapter component on the production server."}				
			
	]

}