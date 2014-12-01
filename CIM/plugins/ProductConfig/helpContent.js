{
  "helpItem":
	[
/* Custom plug-in*/

		{"id":"CustomPluginSelectionStep","content":"If you have created custom CIM plug-ins, they are listed below. Select a plug-in to run it."},

	
/* Product and add-on selection help topics */
	
		{"id":"ProductSelectTask","content":"Before you perform any configuration tasks, select the product you want to configure. Only products you have installed appear in the Product Selection menu. \n\nEach product has a set of available add-ons, such as reporting (ATG Customer Intelligence) and ATG Search, and optional features such as custom catalogs and switching datasources.\n\nTo select a product, type the product number and press the ENTER key. You can select more than one product if necessary. If you change your product selection later, you may have to make changes in other configuration areas as well.\n\nFor additional information on product configuration, see the product documentation available from the ATG Customer Care site."},

		{"id":"ProductSelectTask.products","content":""},
		{"id":"ProductSelectTask.products.searchAdmin","content":"Configure ATG Search as part of your installation. See also: ATG Search Installation and Configuration Guide."},
		{"id":"ProductSelectTask.products.store","content":"See ATG internal documentation for Commerce Reference Store configuration information."},
		{"id":"ProductSelectTask.products.outreach","content":"Configure the ATG platform, ATG Content Administration, and ATG Outreach. If you want to use Outreach with ATG Commerce-specific extensions, also select the applicable ATG Commerce option at this prompt. See also: ATG Customer Outreach Administration and Development Guide"},
		{"id":"ProductSelectTask.products.abtest","content":"Configure the ATG platform, ATG Content Administration, and ATG Campaign Optimizer. If you want to use Campaign Optimizer with ATG Commerce-specific extensions, select ATG Campaign Optimizer with Commerce, instead.\n\nSee also: ATG Campaign Optimizer Guide"},
		{"id":"ProductSelectTask.products.dcs-abtest","content":"Configure the ATG platform, ATG Content Administration with Commerce extensions, and ATG Campaign Optimizer.\n\nSee also: ATG Campaign Optimizer Guide"},
		{"id":"ProductSelectTask.products.merch","content":"See also: ATG Merchandising Administration and Configuration Guide"},
		{"id":"ProductSelectTask.products.commerceservicecenter","content":"See also: ATG Commerce Service Center Installation Guide and ATG Commerce Service Center User Guide"},
		{"id":"ProductSelectTask.products.knowledge","content":"See also: ATG Service Installation and Configuration Guide"},
		{"id":"ProductSelectTask.products.siteadmin","content":"See also: ATG Multsite Administration Guide"},
		{"id":"ProductSelectTask.products.b2bcommerce","content":"Configure the ATG platform (Adaptive Scenario Engine), ATG Content Administration, and ATG B2B Commerce."},

		/* Add-on selection help topics */	
		
		{"id":"AddOnSelectTask.addOns","content":"Select one or more of the listed add-ons as part of your installation."},
		{"id":"AddOnSelectTask.addOns.reporting","content":"If you select the Reporting add-on as part of your product selection, CIM handles the following reporting configuration tasks:\n -Configures data sources for communication with the data warehouse\n Creates data warehouse database tables required for your products.\n -Includes modules required for reporting in your EAR files during assembly and deployment. \n\nNote: CIM does not create tables for products that CIM does not configure, such as Knowledge. Also, you must still manually configure the Web server and content store, import metadata and reports, and perform other tasks as described in the ATG Customer Intelligence Installation and Configuration Guide."},
		{"id":"AddOnSelectTask.addOns.staging","content":"Prepares your installation for use with a Content Administration staging environment. A staging environment functions in the Content Administration workflow as a place to which you can deploy files for testing before moving them to your live environment. When assembling your EAR files for deployment, you will be prompted to create a staging server, which CIM automatically assembles using the correct -layer flag in runAssembler.\n\n   CAUTION: CIM does not configure switching datasources for use with a staging environment. To use switching for staging, manually configure the datasources and repositories involved. See the ATG Installation and Configuration Guide for information on switching datasources."},

		{"id":"AddOnSelectTask.addOns.preview","content":"Prepares your installation for use with the Content Administration preview feature by adding a configuration layer to use when assembling your servers. Preview allows an ATG Business Control Center user to preview assets in a Web page or template. For information on Preview and other Content Administration features, see the Content Administration Programming Guide. Note that ATG Outreach does not use the Content Administration preview feature. If you are configuring ATG Outreach, select this option only if you are also configuring the platform and want to use the Preview feature with other applications."},

		{"id":"AddOnSelectTask.addOns.publock","content":"Your asset management server instance will be configured to serve as the lock manager. This configuration is supported for small sites and for testing; production sites should include a dedicated lock manager. See the Locked Caching section of the ATG Repository Guide for information on lock management."},
		{"id":"AddOnSelectTask.addOns.prodlock","content":"Your production server instance will be configured to serve as the lock manager. This configuration is supported for small sites and for testing; production sites should include a dedicated lock manager. See the Locked Caching section of the ATG Repository Guide for information on lock management."},
		{"id":"AddOnSelectTask.addOns.lock","content":"Your installation will include a separate ATG server dedicated to lock management. This configuration is recommended for production sites. See the Locked Caching section of the ATG Repository Guide for information on lock management."},

/*these next two might no longer be in use - ID changes */
		{"id":"AddOnSelectTask.addOns.productionLock","content":"Select this option if your installation will include a lock management server. See the Locked Caching section of the ATG Repository Guide for information on lock management."},
		{"id":"AddOnSelectTask.addOns.agentLock","content":"Select this option if your installation will include a dedicated lock agent server. See the Locked Caching section of the ATG Repository Guide for information on lock management."},
		
		
		{"id":"AddOnSelectTask.addOns.outreachPreview","content":"Prepares your installation for use with the ATG Outreach e-mail preview feature, which allows you to preview the content of messages you send as part of an e-mail campaign."},	
		{"id":"AddOnSelectTask.addOns.previewOnManagement","content":"Prepares your installation for use with the Content Administration preview feature by adding a configuration layer to use when assembling your management server. Preview allows an ATG Business Control Center user to preview assets in a Web page or template. For information on Preview and other Content Administration features, see the Content Administration Programming Guide."},
		{"id":"AddOnSelectTask.addOns.externalPreviewServer","content":"Prepares your installation for use with the Content Administration preview feature by adding a configuration layer to use when assembling your servers. Preview allows an ATG Business Control Center user to preview assets in a Web page or template. For information on Preview and other Content Administration features, see the Content Administration Programming Guide."},

		{"id":"AddOnSelectTask.addOns.ABTestReportingOnManagement","content":"Prepares your installation for use with the Campaign Optimizer reporting feature by adding a configuration layer to use when assembling your management server. Reporting for Campaign Optimizer is a separate feature and does not use ATG Customer Intelligence. For more information, see the ATG Campaign Optimizer Guide."},
		{"id":"AddOnSelectTask.addOns.ABTestReportingOnSeparate","content":"Prepares your installation for use with the Campaign Optimizer reporting feature by adding a configuration layer to use when assembling your reporting server. Reporting for Campaign Optimizer is a separate feature and does not use ATG Customer Intelligence. For more information, see the ATG Campaign Optimizer Guide."},
		
		
		{"id":"AddOnSelectTask.addOns.indexByProduct","content":"The ProductCatalogOutputConfig component is configured to create an XHTML document for each product in the product catalog. This means that each document indexed by ATG Search corresponds to a product, so that when a site visitor searches the catalog, each individual result returned represents a product. This is the default."},
		{"id":"AddOnSelectTask.addOns.indexBySku","content":"By default, the ProductCatalogOutputConfig component is configured to create an XHTML document for each SKU in the product catalog. This means that each document indexed by ATG Search corresponds to a SKU, so that when a site visitor searches the catalog, each individual result returned represents a SKU."},
		{"id":"AddOnSelectTask.addOns.queryConsoleOnManagement","content":"Includes the Search Query Console testing tool as part of your management server. The Query Console is a sample Web application that demonstrates the search form handler classes. This application has search form pages for most of the ATG Search query types. You can use the application for submitting test queries, or use the JSPs as starting points for building your own search pages. Although the Query Console is typically run as part of the production server, you might want to use it on the management server to ensure that it is never exposed to your production site users. For more information, see the Sample Application section of the Commerce Search Guide."},
		{"id":"AddOnSelectTask.addOns.queryConsoleOnProduction","content":"Includes the Search Query Console testing tool as part of your production server, which is the usual configuration. The Query Console is a sample Web application that demonstrates the search form handler classes. This application has search form pages for most of the ATG Search query types. You can use the application for submitting test queries, or use the JSPs as starting points for building your own search pages. For more information, see the Sample Application section of the Commerce Search Guide."},
		{"id":"AddOnSelectTask.addOns.queryConsoleOnAgent","content":"Includes the Search Query Console testing tool as part of your agent server. The Query Console is a sample Web application that demonstrates the search form handler classes. This application has search form pages for most of the ATG Search query types. You can use the application for submitting test queries, or use the JSPs as starting points for building your own search pages. For more information, see the Sample Application section of the Commerce Search Guide."},

		{"id":"AddOnSelectTask.addOns.switchingdatasource","content":"Your installation will be configured to use a switching datasource. Two identical copies of your production database are created. This allows you to update an offline copy of your database, then switch to that copy while updating the previously live copy, to ensure a consistent end-user experience. See the Copying and Switching Databases section of the ATG Installation and Configuration Guide for information on datasource configuration."},
		{"id":"AddOnSelectTask.addOns.nonswitchingdatasource","content":"Your installation will be configured to use a non-switching datasource to access the production database. See the Copying and Switching Databases section of the ATG Installation and Configuration Guide for information on datasource configuration."},

		{"id":"AddOnSelectTask.addOns.fulfillment_using_atg","content":"Fulfillment is handled by ATG components. See the Configuring the Fulfillment Process chapter of the ATG Commerce Programming Guide for information on this feature."},
		{"id":"AddOnSelectTask.addOns.fufillment_using_integrations","content":"Fulfillment is handled using integrated third-party components. See the Configuring the Fulfillment Process chapter of the ATG Commerce Programming Guide for information on this feature."},

		{"id":"AddOnSelectTask.addOns.clicktoconnect","content":"Your installation will be configured to use ATG eStara Click2Connect."},
		{"id":"AddOnSelectTask.addOns.priceLists","content":"Your installation will be configured to use customized price lists."},
		{"id":"AddOnSelectTask.addOns.scheduledOrders","content":"Your installation will be configured to enable scheduled orders."},	
	
/*app server selection help topics */	
	
		{"id":"AppServerTaskId","content":"ATG products can be used with JBoss, WebSphere, or WebLogic application servers. See the ATG Installation and Configuration Guide for detailed information on application servers and ATG products.\n\n CIM also includes a Raw Ear Output Mode, which creates an EAR file in the location you specify, but does not deploy it to an application server.\n\n\Note: You will have to create your WebLogic domain before deploying ATG applications."},
		{"id":"AppServerPathTaskId","content":"Enter the absolute path to your application server. For example:\n\n   c:\\jboss-eap-4.2\\jboss-as\n\nSee the ATG Installation and Configuration Guide for detailed information on ATG products and application servers."},

		{"id":"HostnameId.hostname","content":"Enter the hostname of the WebSphere deployment manager server."},
		{"id":"PortTaskId.port","content":"Enter the port number used to contact the WebSphere deployment manager server."},
		{"id":"WasSecurityTaskId","content":"Specify whether or not WebSphere Security is enabled."},	

		/* WebSphere configuration help topics*/		
		
		{"id":"UsernameTaskId.username","content":"If WebSphere security is enabled, enter your WebSphere Admin username."},
		{"id":"PasswordTaskId.password","content":"If WebSphere security is enabled, enter your WebSphere Admin password."},
		{"id":"WebsphereValidation.gotoValidate","content":"Verify that you can connect to your WebSphere Admin Server (admin server must be running)."},
		

		/* Server instance type configuration help topics*/		

		{"id":"PerformTypeConfig","content":"Before you can create a server instance of this type, CIM requires some additional properties to be configured. Enter C to continue, and provide the requested configuration information."},

		
		{"id":"ServerInstanceTypeSelectionStep","content":"Select a server instance type to configure. Server Instance Types allow you to create multiple, slightly different, versions of a particular server type. For example, you may have multiple production servers in your installation, but each of them will need differently configured ports. The server type options depend on the ATG products and add-ons you have selected; examples include Publishing (also known  as asset management), Production, and Staging. Types that are optional for your installation are marked as such."},
		
		{"id":"serverInstanceTypeConfigurationMenuStep","content":"Choose a menu option to provide configuration information for servers of this type."},
		
		{"id":"serverInstanceTypeConfigurationMenuStep.generalConfiguration","content":"Provide general configuration information for all servers of this type. Type H at any prompt for information on a specific property."},
		{"id":"serverInstanceTypeConfigurationMenuStep.instancemanager","content":"Add more server instances of this type, or delete existing instances."},
		{"id":"serverInstanceTypeConfigurationMenuStep.editOpts","content":"Add a custom module to the module list for this server type, or remove a custom module you have added."},
		{"id":"serverInstanceTypeConfigurationMenuStep.scenario-OptionalConfigDir","content":"Configure a scenario manager server. See the ATG Personalization Programming Guide for information on scenario management."},

		{"id":"serverInstanceConfigurationMenuStep","content":"You can add or remove server instances of the type you are currently configuring. Adding an instance may involve providing additional configuration information. Type H at any prompt for information on a particular property.\n\nIf you did not select the Dedicated Lock Manager add-in as part of product selection, you may be prompted to select whether your new instance includes lock management."},

		{"id":"ServerInstanceNameTaskproduction.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskmanagement.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskpublishing.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskagent.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},		
		{"id":"ServerInstanceNameTaskstaging.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskpreview.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskexternalpreview.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskPubLock.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		{"id":"ServerInstanceNameTaskdatawarehouse_loader.serverInstanceName","content":"Enter a name for this server instance, or accept the default."},
		
		{"id":"ServerInstanceRemoveTaskmanagement","content":"Type the number of the server instance you want to remove. Removing a server instance deletes all associated configuration files, and cannot be undone."},

		

/* ServerInstancePatternSelectionTaskproduction */

		{"id":"ServerInstancePatternSelectionTask","content":"Select a type for the server instance."},
		{"id":"ServerInstancePatternSelectionTask.serverInstancePatternId.management_lock","content":"Select this option to create an asset management server instance that includes a lock manager server."},
		{"id":"ServerInstancePatternSelectionTask.serverInstancePatternId.basic_management","content":"Select this option to create an asset management server instance without a lock manager server."},
		{"id":"ServerInstancePatternSelectionTask.serverInstancePatternId.production_lock","content":"Select this option to create a production server instance that includes a lock manager server."},
		{"id":"ServerInstancePatternSelectionTask.serverInstancePatternId.basic_production","content":"Select this option to create a production server instance without a lock manager server."},
		
		{"id":"JBossPortBindingSelectTask","content":"If you are running multiple JBoss servers on one physical machine, each instance requires different port bindings to prevent conflicts. You can use the default ports, or set custom ports. See your JBoss documentation for further information."},
		
		
/* Server instance assembly help topics*/		

		
		{"id":"ServerInstanceSelectionStep","content":"Select the server instances to assemble and deploy.\n\nNote: If you are using CIM to configure and deploy your EAR files, do not use the startDynamoOnJBoss script to start your servers. You may need to edit the default ATG startup scripts.\n\nServer instances represent common product configurations, and ensure that your assembled application includes the correct top-level configuration. Server instances are created as ATG servers in your installation's ATG9dir\\home\\servers directory. See the Creating Additional Dynamo Server Instances section of the ATG Installation and Configuration Guide for more information."},

		 {"id":"/atg/dynamo/Configuration_properties.httpPort","content":"Enter the HTTP port to use for this server instance, or accept the default."},
		 {"id":"/atg/dynamo/Configuration_properties.httpsPort","content":"Enter the HTTPS port to use for this server instance, or accept the default."},
		 {"id":"/atg/dynamo/Configuration_properties.rmiPort","content":"Enter the RMI port to use for this server instance, or accept the default."},
		 {"id":"/atg/dynamo/Configuration_properties.drpPort","content":"Enter the DRP port to use for this server instance, or accept the default."},
		 {"id":"/atg/dynamo/Configuration_properties.fileDeploymentPort","content":"The port on which a Content Administration target listens to accept file deployments. This port is required even if you do not intend to perform file deployments on a given target, and must be different for each server instance running on the same physical machine."},
		 {"id":"/atg/dynamo/service/ClusterName_properties.clusterName","content":"The name of the Knowledge cluster. The default value is knowledge"},
		 {"id":"/atg/dynamo/Configuration_properties.siteHttpServerPort","content":"If you have a single application server, this setting is the same as the httpPort. If you are setting up a cluster, set siteHttpServerPort to the HTTP port of your load balancer."},
		{"id":"/atg/dynamo/Configuration_properties.fileSychronizationDeploymentPort","content":"Enter the port used by the FileSynchronizationDeployServer component. This component coordinates file asset storage and access in environments that have clustered ATG Content Administration servers distributed across different host machines. For more information on the FileSynchronizationDeployServer component, see the ATG Content Administration Programming Guide."},

		

		{"id":"ServerInstanceNameTaskbasic_management","content":"The name you enter is used as the name of the management ATG server in the <ATG9dir>\\home\\servers directory. \n\nSee the Creating Additional Dynamo Server Instances section of the ATG Installation and Configuration Guide for more information on ATG servers."},
		{"id":"ServerInstanceNameTaskbasic_lock","content":"The name you enter is used as the name of the lock manager ATG server in the <ATG9dir>\\home\\servers directory. \n\nSee the Creating Additional Dynamo Server Instances section of the ATG Installation and Configuration Guide for more information on ATG servers."},
		{"id":"ServerInstanceNameTaskbasic_production","content":"The name you enter is used as the name of the production ATG server in the <ATG9dir>\\home\\servers directory. \n\nSee the Creating Additional Dynamo Server Instances section of the ATG Installation and Configuration Guide for more information on ATG servers."},

		
		{"id":"serverInstanceConfigurationMenuStep","content":"Select from the following server instance configuration tasks:"},
		{"id":"serverInstanceConfigurationMenuStep.instanceConfiguration","content":"Configure the specified server instance."},
		{"id":"serverInstanceConfigurationMenuStep.typeConfiguration","content":"Configure another server instance of this type."},
		{"id":"serverInstanceConfigurationMenuStep.editOpts","content":"Add modules to the module list for this server instance."},
		{"id":"serverInstanceConfigurationMenuStep.scenario-OptionConfigDir","content":"Configure an optional server instance."},

				
/* Module list help topics*/		
		
		{"id":"ModuleListStep","content":"You can add a custom module to the default list CIM calculates for your product selections."},
		{"id":"ModuleNameTask.moduleName","content":"Enter the name of the custom module to add to this server instance's module list. If CIM cannot find the module you are adding, it will still be added to the list, but assembly will fail if the module cannot be found at that time."},
		{"id":"ModuleNameTask.location","content":"Select a module. Your custom module is added to the list before the module you select. To change the location, remove and re-add the module."},
		{"id":"RemoveModuleTask","content":"Enter the number of a module to select it for removal. You can remove only one module at a time. Only modules that you have added to the calculated list can be removed; this ensures that you do not accidentally remove any required modules."},
		


		
		{"id":"DynamoPortConfigTask.httpPort","content":"Enter the HTTP port to use for this server, or accept the default."},
		{"id":"DynamoPortConfigTask.rmiPort","content":"Enter the RMI port to use for this server, or accept the default."},
		{"id":"DynamoPortConfigTask.drpPort","content":"Enter the DRP port to use for this server, or accept the default."},
		{"id":"DynamoPortConfigTask.httpsPort","content":"Enter the HTTPS port to use for this server, or accept the default."},
				

/* EAR File deployment topics */				


		{"id":"WebsphereServerSelectionStep","content":"Select a WebSphere server to which CIM will deploy the EAR file."},

		{"id":"nameTaskWasServerTaskId","content":"Enter the name to use for the WebSphere server CIM will create."},
		{"id":"WasNodeTaskId","content":"Select the WebSphere node to which CIM will deploy the EAR file."},
		


		
		{"id":"JbossServerSelectionStep_JBossTemplate","content":"Select a JBoss server template to use as the basis for the new server. See the JBoss documentation for information on server templates."},
		{"id":"nameJbossServerTaskId","content":"Enter a name for the new JBoss server."},

		{"id":"EarFileNameTask","content":"Enter the name to use for the EAR file."},

		{"id":"JbossServerSelectionStep","content":"Select an existing JBoss server, or create a new server using CIM."},
		
		{"id":"JbossDeployChoiceStep","content":"Select an assembly and deployment option."},
		{"id":"JbossDeployChoiceStep.jdbcDriver","content":"Add the database driver to your application server classpath."},
		{"id":"JbossDeployChoiceStep.deploy","content":"Creates the specified EAR file and deploys it to your application server.\n WARNING: JBoss should not be running when you deploy the EAR. If JBoss is running, it will try to load the Web application before files have been fully copied, resulting in errors."},
		{"id":"JbossDeployChoiceStep.datasource","content":"For each data source you have configured, creates a [data_source_name]-ds.xml file required by the application server."},
		{"id":"JbossDeployChoiceStep.editOpts","content":"Specify the runAssembler arguments to use when creating and deploying your EAR file. These arguments are persisted as the defaults next time you assemble an EAR."},
		{"id":"JbossDeployChoiceStep.done","content":"Configure another server instance for deployment."},
		{"id":"JbossDeployChoiceStep.optimizeJvm","content":"Optimize your JVM for ATG applications. Optimization updates your run.conf|bat file with the following settings:\n\n -Xms512m\n -Xmx1152m\n -XX:MaxPermSize=256m\n -XX:MaxNewSize=256m\n -Dsun.rmi.dgc.server.gcInterval=3600000\n\nThe changed lines are marked with a comment, in case you want to reverse the changes later."},
		{"id":"JbossDeployChoiceStep.postDeploy","content":"Select from post-deployment options."},
		
		{"id":"OptimizeJvmChoiceStep.optimizeJVM","content":"Optimize your JVM for ATG applications. Optimization updates your run.conf|bat file with the following settings:\n\n -Xms512m\n -Xmx1152m\n -XX:MaxPermSize=256m\n -XX:MaxNewSize=256m\n -Dsun.rmi.dgc.server.gcInterval=3600000\n\nThe changed lines are marked with a comment, in case you want to reverse the changes later."},

		
		{"id":"JbossRegisterDatasourceSelectTaskbasic","content":"For each data source you have configured, creates a [data_source_name]-ds.xml file required by the application server."},

		{"id":"JbossPostDeploymentActionsChoiceStep","content":"Select a post-deployment option."},
		{"id":"JbossCopyJdbcDriverChoiceStep","content":"Copies the JDBC driver .jar file to the application server and includes it in the classpath."},
		{"id":"JbossPostDeploymentActionsChoiceStep.optimizeJvm","content":"Optimize your JVM for ATG applications. Optimization updates your run.conf|bat file with the following settings:\n\n -Xms512m\n -Xmx1152m\n -XX:MaxPermSize=256m\n -XX:MaxNewSize=256m\n -Dsun.rmi.dgc.server.gcInterval=3600000\n\nThe changed lines are marked with a comment, in case you want to reverse the changes later."},

		
		
		{"id":"JbossRegisterDatasourceDatasourcesTaskbasic_productionswitchingA","content":"Enter the JNDI name for the data source."},



		{"id":"WebsphereDeployChoiceStep","content":"Select an assembly and deployment option:"},
		{"id":"WebsphereRegisterDatasourceSelectTaskbasic","content":"DOES THIS EXIST?"},
		{"id":"WebspherePostDeploymentActionsChoiceStep","content":"Perform post-deployment configuration for your application server."},
		{"id":"WebsphereCopyJdbcDriverChoiceStep","content":"Copies the JDBC driver .jar file to the application server and includes it in the classpath."},


		{"id":"WeblogicDeployChoiceStep","content":"Select an assembly and deployment option:"},
		{"id":"WeblogicRegisterDatasourceSelectTaskbasic","content":""},
		{"id":"WeblogicPostDeploymentActionsChoiceStep","content":"Perform post-deployment configuration for your application server."},
		{"id":"WeblogicCopyJdbcDriverChoiceStep","content":"Copies the JDBC driver .jar file to the application server and includes it in the classpath."},

		{"id":"BigEarRawOutputChoiceStep","content":"Enter the target directory to which you want the EAR file output."},
		{"id":"UpdateEarOutputPathTask","content":"Enter the directory to which you want the EAR file output."},

		{"id":"AppAssemblyOptionsTask","content":"To assemble your application to run on your application server, use the runAssembler command-line script. This script takes a set of ATG application modules and assembles them into an EAR file. The syntax of the command is:\n\n     runAssembler [options] output-file-name -m module-list\n\nFor example, if you develop your application as an application module named MyApp, and you want to assemble an EAR file that includes your application plus the DSS and DPS modules, you would use the following command:\n\n     runAssembler MyApp.ear -m MyApp DSS\n\nThe runAssembler options are:
\n\n     -pack	Packs the assembled EAR file into the archived J2EE enterprise archive format. By default, the EAR is assembled in an exploded, open-directory format.
\n\n     -standalone	Configures the assembled EAR in standalone mode, so that it contains all application resources, including Nucleus configuration files, and does not refer to the ATG installation directory. By default, the EAR is assembled in development mode, in which only classes, libraries, and J2EE modules are imported to the EAR file, and Nucleus configuration and other resources are used directly from the ATG install directory.
\n\n     -overwrite	Overwrites all resources in the existing EAR file. By default, resources in the assembled EAR are only overwritten if the source file is newer, to reduce assembly time.
\n\n     -collapse-class-path	Collapses all JAR files and directories in the CLASSPATH into a single JAR file in the assembled EAR file. By default, these JAR files and directories are copied separately to the EAR file lib directory, and placed in the EAR file CLASSPATH.
\n\n     -jardirs   Collapse all classpath entries into individual jar files.
\n\n     -verbose   Display extra logging information.

\n\n     -classesonly	Instead of assembling a complete EAR file, creates a JAR file that collapses the JAR files and directories in the CLASSPATH into a single library.
\n\n     -displayname [name]	Specifies the value for setting the display name in the application.xml file for the assembled EAR file.
\n\n     -liveconfig	Enables the liveconfig configuration layer for the application. For more information, see the ATG Installation and Configuration Guide.
\n\n     -distributable   Adds the distributable tag to the web.xml file for clustering.
\n\n     -layer	Enables one or more named configuration layers for the application. This option must immediately precede the -m switch.
\n\n     -omit-licenses	If assembling a standalone EAR file, omits license files from the EAR file. This is useful if your ATG installation includes development licenses that should be excluded from a standalone EAR file being assembled for production deployment.
\n\n     -add-ear-file filename	Includes the contents from an existing EAR file in the assembled EAR file.
\n\n     -context-roots-file filename	Specifies a Java properties file whose settings are used to override the default context root values for any Web applications included in the assembled EAR file. To specify the context root for a web application in this properties file, add a line with the following format: module-uri=context-root. Module-uri is the module URI of the web application, and context-root specifies the context root to use.
\n\n      -dynamo-env-properties    Specifies a properties file, which can be used to add values to the dynamo.env file in the assembled EAR file.  Values in this file can modify runtime behavior of the ATG application EAR file.
\n\n      -exclude-acc-resources   Excludes jar files used by the ACC. Use only if you never intend to connect from the standalone ACC (see the ATG Installation and Configuration Guide).
\n\n      -nofix	Instructs runAssembler not to fix servlet mappings that do not begin with a leading backslash. By default the runAssembler command attempts to fix any servlet mappings defined in a web.xml that do not start with a leading /. JBoss does not allow servlet mappings without starting slashes, so runAssembler converts <url pattern>foo.bar.baz</url pattern> to <url pattern>/foo.bar.baz</url pattern>. The runAssembler command does ignore mappings that begin with * or with white space. For example, it does not change <url pattern>*.jsp</url pattern>.
\n\n     -run-in-place	When assembling the EAR file, does not copy classes.jar or any WAR files included in the application, but refers to the ATG installation for these resources. If during development you make changes to classes.jar or to WAR files within your ATG installation, you then do not need to reassemble the EAR in order to see the changes.
Note: This switch can only be used with JBoss. It should only be used during development, and not on a production site. Any EAR or WAR files referred to in your ATG installation must be exploded.
\n\n      -prependJars	Includes the comma separated list of jar files on the classpath. This attribute is useful for applying hotfixes. For example: runAssembler -prependJars hotfix1.jar,hotfix2.jar myEarFile.ear -m DCS. Note: Special characters appearing in jar file names might cause that file to be ignored. When naming files, use only alphanumeric characters and the underscore."},

		{"id":"ValidationStep","content":"CIM ensures that you have installed the selected ATG products and their licenses before configuring."},

		
/* Datasource configuration topics */		
		
		{"id":"SelectNamedDatasourceStep","content":"Select a datasource to configure. The data sources listed vary depending on the product you are configuring."},

		{"id":"SelectNamedDatasourceStep.management","content":"This datasource connects to the database that contains tables used by ATG administrative applications."},
		{"id":"SelectNamedDatasourceStep.nonswitchingCore","content":"This datasource connects to the database that contains the production tables. In a non-switching environment, this includes the Commerce catalog."},
		{"id":"SelectNamedDatasourceStep.stagingnonswitchingCore","content":"This datasource connects to the database that contains the production tables for the staging server."},
		{"id":"SelectNamedDatasourceStep.datawarehouse_loader","content":"This datasource connects to the database that contains the tables used by the ATG Customer Intelligence data warehouse data loaders."},
		{"id":"SelectNamedDatasourceStep.datawarehouse","content":"This datasource connects to the database that contains the tables used by the ATG Customer Intelligence data warehouse. This database should be optimized for data warehousing."},
		{"id":"SelectNamedDatasourceStep.switchingCore","content":"This datasource connects to the database containing your non-switching content in a switching environment."},
		{"id":"SelectNamedDatasourceStep.switchingA","content":"This datasource connects to one of the two databases used to contain your switched database tables."},
		{"id":"SelectNamedDatasourceStep.switchingB","content":"This datasource connects to one of the two databases used to contain your switched database tables."},

		{"id":"ExistingDatasourcePropTask.existingDatasourceName","content":"CIM stores the five most recent datasource connection detail sets you provide. If you select one of these sets, CIM uses those values to populate the current datasource. You can review these values and change any that are necessary. Select Use Existing/None to keep the existing configuration for the datasource, or if no values have been set, do not prepopulate them."},

		
		
		
		{"id":"ConfigDSStep.conDetailsNavOpt","content":"Configure connection information for this datasource."},
		{"id":"ConfigDSStep.testConNavOpt","content":"Test your database connection using this datasource."},
		{"id":"ConfigDSStep.createSchemaNavOpt","content":"Create database schemas for the product combination you are configuring. After you create the tables, you can import data."},
		{"id":"ConfigDSStep.dataImportNavOpt","content":"After you have created schemas, you can import starting data for the applications you are configuring."},
		{"id":"ConfigDSStep.dropSchemaNavOpt","content":"Drop database tables for the product combination you are configuring."},
		{"id":"ConfigDSStep.otherDSNavOpt","content":"Select another datasource to configure."},

		
		{"id":"DatasourcePropTask.databaseType","content":"Select your database vendor."},
		{"id":"DatasourcePropTask.driverPath","content":"Enter the path to your database driver."},
		{"id":"DatasourcePropTask.driverClassName","content":"Enter the name of the driver class this data source will use to connect to the database."},
		{"id":"DatasourcePropTask.username","content":"Enter the user name this data source will use to connect to the database."},
		{"id":"DatasourcePropTask.password","content":"Enter the password this data source will use to connect to the database."},
		{"id":"DatasourcePropTask.hostname","content":"Enter the host name that will be used to compose the JDBC URL."},
		{"id":"DatasourcePropTask.port","content":"Enter the port number that will be used to compose the JDBC URL."},
		{"id":"DatasourcePropTask.databaseName","content":"Enter the database name that will be used to compose the JDBC URL."},
		{"id":"DatasourcePropTask.url","content":"Enter the JDBC URL components."},
		{"id":"DatasourcePropTask.jndiName","content":"Enter the JNDI name used for this data source."},
		{"id":"DatasourcePropTask.driverPath","content":"Enter the path to the database driver for this data source."},

		{"id":"DB2LicenseTask.licenseDriverPath","content":"Enter the path to the DB2 database driver license for this data source."},
		
		{"id":"TestConnectionStep","content":"Tests your database connection using the configuration values you entered for the data source. If connection fails, you can re-enter values for the data source."},
		{"id":"CreateSchemaStep","content":"Runs the SQL scripts to create the database schema for the product you are configuring. You can view the details of the scripts that were run in the cim.log file."},
		{"id":"ImportDataStep","content":"Imports initial data for the product you are configuring."},
		{"id":"DropSchemaStep","content":"Runs the SQL scripts to drop database schema for the product you are configuring."},

		{"id":"CustomPluginSelectionStep","content":"Select a custom wizard from the list. Custom wizards can be created by your administrators to perform configuration tasks particular to your site. See the CIM Developer Guide for information on creating custom wizards."},

		{"id":"JbossPostDeploymentActionsChoiceStep.copyStaticStore","content":"Select this option to copy the Sketch-specific docroot to JBOSS_HOME\\server\\SERVER_NAME\\deploy\\jboss-web.deployer\\ROOT.war."},

		{"id":"/atg/dynamo/service/jdbc/JTDataSource_properties.dataSource","content":"The name of the direct data source."},
		{"id":"/atg/dynamo/service/jdbc/DirectJTDataSource_properties.className","content":"The class name of the JNDI data source."},
		{"id":"/atg/dynamo/service/jdbc/JTDataSource_agent_properties.dataSource","content":"The name of the agent data source."},
		{"id":"/atg/dynamo/service/jdbc/DirectJTDataSource_agent_properties.className","content":"The class name of the agent data source."},
		{"id":"/atg/dynamo/service/jdbc/DirectJTDataSource_agent_properties.JNDIName","content":"The JNDI name of the agent data source."},
		{"id":"/atg/dynamo/service/jdbc/DirectJTDataSource_production_properties.className","content":"The class name of the production data source."},
		{"id":"/atg/dynamo/service/jdbc/DirectJTDataSource_production_properties.JNDIName","content":"The JNDI name of the production data source."},

		{"id":"atg/dynamo/service/jdbc/DirectJTDataSource_publishing_properties.JNDIName","content":"The JNDI name of the publishing data source."},
		{"id":"atg/dynamo/service/jdbc/DirectJTDataSource_publishing_properties.className","content":"The class name of the publishing data source."},	
		{"id":"atg/dynamo/service/jdbc/JTDataSource_publishing_properties.dataSource","content":"The name of the publishing data source."},
		{"id":"atg/dynamo/service/jdbc/JTDataSource_publishing_properties.className","content":"The class name of the publishing data source."},
		
		
/* Lock management configuration */
		
		{"id":"/atg/dynamo/service/ClientLockManager_properties.lockServerPort","content":"Enter the port number to use for lock management on your production server."},
		{"id":"/atg/dynamo/service/ClientLockManager_properties.lockServerAddress","content":"Enter the hostname of the lock management client."},

		{"id":"/atg/dynamo/service/ClientLockManager_production_properties.lockServerPort","content":"Enter the port number to use for lock management on your production server."},
		{"id":"/atg/dynamo/service/ClientLockManager_production_properties.lockServerAddress","content":"Enter the host name of the lock management client on the production server."},

		{"id":"/atg/dynamo/service/ClientLockManager_agent_properties.lockServerPort","content":"Enter the port number to use for lock management on your agent server."},
		{"id":"/atg/dynamo/service/ClientLockManager_agent_properties.lockServerAddress","content":"Enter the host name of the lock management client on the agent server."},

		{"id":"/atg/dynamo/service/ServerLockManager_properties.port","content":"Enter the port number to use for lock management on the lock manager."},

		
/* Search property file settings */		

		{"id":"SearchIndexSelectTask","content":"Select whether you want ATG Search to index your catalog by product or by SKU.\n\nBy default, the ProductCatalogOutputConfig component is configured to create an XHTML document for each product in the product catalog. This means that each document indexed by ATG Search corresponds to a product, so that when a site visitor searches the catalog, each individual result returned represents a product.\n\nYou may prefer to create a separate XHTML document for each SKU, so that each search result represents a SKU rather than a product. See the ATG Commerce Search Guide > Indexing Commerce Catalog Data chapter for detailed information on catalog indexing."},
		{"id":"LaunchingServiceConfigTask","content":"Enter the ATG Search deployment share you specified during Search installation.\n\nThe deployment share is a single, scalable, shared directory where master copies of indexes are stored. Your installation can have only one deployment share, which should be located on a dedicated high-performance disk, separate from the Search Administration installation. The Search Administration installation and all Search engines must have access to this directory.\n\n\After you enter the deployment share directory, CIM creates a number of properties files required by Search."},
		
		{"id":"/atg/dynamo/service/SearchClientLockManager_properties.componentPath","content":"Enter the path to the lock management component:\n\n   \\atg\\dynamo\\service\\ClientLockManager on the production server\n   \\atg\\dynamo\\service\\ClientLockManager_production on the asset management server:\n   \\atg\\dynamo\\service\\ClientLockManager_production on the staging server (if using)"},
		{"id":"/atg/search/service/SearchSQLRepositoryEventServer_properties.componentPath","content":"The SearchSQLRepositoryEventServer component is used by the subscriber table needed for GSA distributed cache invalidation, and is located in the DAF.Search.Base module, in the atg\\search\\service component directory. By default, it points to the local, default JDBC connection and the related services. If your environment has both production and internal-facing ATG instances, you must change this components to point to the _production version on the internally-facing server:\n\n   \\atg\\dynamo\\server\\SQLRepositoryEventServer_production"},
		{"id":"/atg/search/service/SearchIdGenerator_properties.componentPath","content":"On the asset management and agent servers, by default the /atg/search/service/SearchIdGenerator component points to the /atg/dynamo/service/IdGenerator_production component, which is the ID generator for the production server. This ensures that Search Administration and all Search client applications use the same ID generator for the Search repositories. If you are adding a staging server to your configuration, you may need to configure this component as shown:\n\n   \\atg\\search\\service\\SearchIdGenerator\\componentPath=\\atg\\dynamo\\service\\IdGenerator_production"},
		{"id":"/atg/search/config/LanguageDimensionService_properties.locales","content":"TBD"},

		{"id":"/atg/search/routing/LaunchingService_properties.deployShare","content":"Enter the deployment share directory for Search. Search Administration and all Search engines must have access to the deployment share directory in order to deploy and search indexes. This is a single, scalable, shared directory where master copies of indexes are stored. Your installation can have only one deployment share. This directory should ideally be located on a high-performance drive separate from the Search Administration machine. For all machines running Search engines, the deployshare directory must be mounted at the same location, and the path must be exactly the same for all components accessing this directory."},
		
		{"id":"/atg/search/testing/StagingEnvironment.searchEnvironmentName","content":"TBD"},

		{"id":"/atg/search/repository/IndexingDeploymentListener_properties.indexingOutputConfig","content":"If you have created additional IndexingOutputConfig components in addition to the production catalog IndexingOutputConfig, enter them here in order to use the deployment and indexing synchronization feature of Search. See the ATG Search Installation and Configuration Guide for information."},
		
/* Reporting property file settings */		
		
		{"id":"/atg/reporting/datawarehouse/TimeRepositoryLoader_properties.dateTimeZoneId","content":"Enter the time zone ID to use for reporting in your installation."},
		{"id":"/atg/reporting/datawarehouse/loaders/TimeRepositoryLoader_properties.dateTimeZoneID","content":"Enter the time zone ID to use for reporting in your installation."},
		{"id":"/atg/reporting/datawarehouse/CommerceWarehouseConfiguration_properties.standardCurrencyCode","content":"Enter the ISO currency code that represents the standard currency to use for reporting in your installation."},
		{"id":"/atg/dynamo/service/DWDataCollectionConfig_properties.defaultRoot","content":"Provide a location in which to store ACI log files that will be loaded into the data warehouse. This location must be accessible to both the data logging and loading servers."},
		{"id":"/atg/dynamo/service/DeploymentDWDataCollectionConfig_properties.defaultRoot","content":"Provide a location in which to store ACI log files that will be loaded into the data warehouse. This location must be accessible to both the data logging and loading servers."},

/* Service Center property file settings */	
		
		{"id":"/atg/dynamo/service/SMTPEmail_properties.emailHanderHostName","content":"The name of your SMTP Email Host. You must provide a host name."},
		{"id":"/atg/dynamo/service/SMTPEmail_properties.emailHanderPort","content":"The port number of your SMTP Email Host."},
		{"id":"/atg/search/routing/LaunchingService_properties.engineDir","content":"Provide the path to your Search Engine."},
		{"id":"/atg/epub/Configuration_properties.queryWorkflowTargetByID","content":"This setting allows you to look for workflows by name rather than by ID, which is the default. Modify this parameter to true to enable searching for workflows by name."},	
			
		
/* Knowledge property file settings */		
		
		{"id":"/atg/userprofiling/search/ProfileOutputConfig_properties.incrementalUpdateSeconds","content":"How frequently to check for customer profile changes to live index.  Set this to -1 on any server that is not the indexing server.  It's recommended that the indexing server is a non-agent facing server"},
		{"id":"/atg/cognos/Configuration_properties.gatewayURLPath","content":"Enter the gateway URL of the Cognos component of your ATG Customer Intelligence server. Find the URL in the Environment section of the Cognos Configuration interface. For example, http://servername:80/arc/cgi-bin/cognos.cgi. This value is entered in the /atg/cognos/Configuration.gatewayURLPath property."},
		{"id":"/atg/searchadmin/adapter/customization/RemoteSolutionAuxiliaryDataAdapter_properties.remoteHost","content":"Enter name of the server that you will use as a remote host for solutions."},
		{"id":"/atg/searchadmin/adapter/customization/RemoteSolutionAuxiliaryDataAdapter_properties.remotePort","content":"Enter port of the server that you will use as a remote host for solutions."},
		{"id":"/atg/search/SynchronizationInvoker_properties.host","content":"Enter name of the server that you will use as a publishing host."},
		{"id":"/atg/search/SynchronizationInvoker_properties.port","content":"Enter port of the server that you will use as a publishing host."},		
		
/* CSC property file settings */		
		
		{"id":"/atg/commerce/search/OrderOutputConfig_properties.incrementalUpdateSeconds","content":"How frequently to check for order changes to live index. Set this to -1 on any server that is not the indexing server.  It's recommended that the indexing server is a non-agent facing server"},
		{"id":"/atg/commerce/custsvc/util/CSRConfigurator_properties.defaultCatalogId","content":"Enter the ID of your default catalog."},
		{"id":"/atg/commerce/custsvc/util/CSRConfigurator_properties.usingPriceLists","content":"This property enables custom price lists."},
		{"id":"/atg/commerce/custsvc/util/CSRConfigurator_properties.usingSalePriceLists","content":"This property enables sales price lists."},
		{"id":"/atg/commerce/custsvc/util/CSRConfigurator_properties.usingScheduledOrders","content":"This property enables the creation of scheduled orders."},		
		
/* eStara ClicktoConnect property file settings */	
	
		{"id":"/atg/clicktoconnect/Configuration_properties.accountId","content":"Enter the ID of your eStara account."},
		{"id":"/atg/clicktoconnect/Configuration_properties.username","content":"Enter the user name associated with your eStara account."},
		{"id":"/atg/clicktoconnect/Configuration_properties.password","content":"Enter the password used for your eStara account."},
		{"id":"/atg/svc/clicktoconnect/C2CTools_properties.secretKeyForHashCompare","content":"Enter the secret key that was made when you created your eStara account. This key is used for authentication."},
	
/* Outreach property file settings */		
			
		{"id":"/atg/campaign/list/imports/ImportRMIClient_properties.importMachineHostName","content":"Enter the name of the import server, which is a dedicated ATG Outreach Production/Staging instance you want to use to handle list import operations. Make sure the format of the name is resolvable by the network. If necessary, specify the fully qualified domain name, for example: my-server.us.example.com."},
		{"id":"/atg/campaign/list/imports/ImportRMIClient_properties.importMachineRMIPort","content":"Enter the port number that the import server uses for RMI (the default is 1099)."},
		{"id":"/atg/dynamo/service/POP3Service_properties.host","content":"Enter the name of the POP3 server used by ATG Outreach to handle bounced e-mails. Make sure the format of the name is resolvable by the network. If necessary, specify the fully qualified domain name, for example mail-serv6.us.example.com."},
		{"id":"/atg/dynamo/service/POP3Service_properties.username","content":"Enter the user name of a valid e-mail account on the POP3 server, for example jsmith."},
		{"id":"/atg/dynamo/service/POP3Service_properties.password","content":"Enter the password for a valid e-mail account on the POP3 server."},
		{"id":"/atg/dynamo/service/POP3Service_properties.port","content":"Enter the port number used by the POP3 server (typically 110)."},
		{"id":"/atg/dynamo/service/SMTPEmail_properties.emailHandlerHostName","content":"Enter the name of the SMTP server used by ATG Outreach to send campaign e-mails. Make sure the format of the name is resolvable by the network. If necessary, specify the fully qualified domain name, for example: mail-serv33.us.example.com."},
		{"id":"/atg/dynamo/service/SMTPEmail_properties.emailHandlerPort","content":"Enter the port number used by the SMTP server (typically 25)."},
		{"id":"/atg/epub/file/OutreachWebAppFileSystem_properties.localDirectory","content":"Enter the location of the .ear file to which ATG Outreach file assets (such as e-mail JSPs) will be deployed. Example: C:\\jboss4.2\\server\\atg-runtime\\deploy\\ATG.ear. Note that this file typically does not exist yet; CIM will create it during application assembly."},


/* Campaign Optimizer property file settings */

		{"id":"//atg/abtest/reporting/ABTestReportRepository_properties.dataSource","content":"The name of the data source that stores report data that results when raw log data produced by the recorders in Campaign Optimizer tests is evaluated."},
	{"id":"//atg/abtest/reporting/ABTestLogRepository_properties.dataSource","content":"The name of the data source that stores raw log data produced by the recorders in Campaign Optimizer tests."},

		{"id":"/atg/scenario/ScenarioManager_properties.editOnlyMode","content":"This property specifies whether this scenario server should not run any scenarios or process any events. The default value is true. For more information, see the ATG Personalization Programming Guide."},
		{"id":"atg/abtest/ABTestRepository_properties.dataSource","content":"The name of the data source that stores the tracking and test tables."},

	]

}