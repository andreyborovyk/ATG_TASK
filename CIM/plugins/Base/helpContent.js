{
  "helpItem":
	[
		{"id":"DisplayMainMenu","content":"Welcome to CIM!
		
		\n\nATG Configuration and Installation Manager (CIM) is a text-based application that simplifies configuration for ATG products. CIM dynamically generates menus for each part of the configuration process, so that you are presented with relevant options for your installed products. For each step, CIM identifies a default option with an asterisk (*) and\\or a \"default\" note. In the main menu, this default identifies the next step to perform; in other menus, it shows the standard selection for configuration choices.
		
		\n\nCIM also identifies steps that you have already completed with a \"Done\" note. Information that you enter is validated to ensure that it is correct and in the appropriate format, if applicable. If at any time you need to stop working in CIM and resume configuation later, CIM stores previous selections and entries, such as your application server location, and allows you to pick up where you stopped. Users who frequently configure many ATG installations have the option to create a batch file based on their CIM use to automate those configuration steps.
		
		\n\nBEFORE YOU BEGIN:\nBefore you start using CIM, make the sure the following steps have been done:\n\n -Install your application server.\n -Download and install ATG products.\n -Check the ATG supported environments page to ensure that your application server, database version, etc. are supported. See http:\\\\www.atg.com\\en\\ecommerce-suite\\platform\\supported-environments\\index.jhtml. \n -Get your database access information ready (login, password, driver location, URL, and database name).\n -Understand which ATG servers your products will require (asset management, production, etc.). 
		
		\n\nUSING CIM\n\n\To use CIM, enter the number or letter of your menu selection and press ENTER. Default selections are identified with an asterisk (*); default values are displayed in double brackets [[example]]. To select or enter a default, press Enter. 
		
		\n\nConfiguration steps are usually performed in the following order:\n  1. Select the product you want to configure. Your product selection affects other options throughout CIM. You can configure only one product at a time, and changing product selection usually involves re-running other configuration tasks. After selecting the product, you can select add-ons, such as ACI or Search, and also whether you want to use switching data sources. The list of available add-ons depends on the product selected.\n  2. Identify the application server you are using. When configuration is finished, CIM can deploy your assembled EAR file to the application server, and can also perform some application server tasks, such as creating JBoss servers. 
		
		\n\nAfter you have selected a product, chosen add-ons, and specified an application server, additional Main Menu options become available.\n  3. Configure your database. CIM guides you through data source configuration, database table creation, and data import for your selected product and its add-ons.\n  4. Configure server instances. In this step, specify what types of ATG servers you want to create; for instance, if you are configuring an installation that includes Content Administration, you can configure the management (publishing) and production servers, and a data warehouse loader server for use with ACI. CIM creates the ATG server using makeDynamoServer, creates necessary properties files, and configures the server's ports.\n\n  5. Assemble and deploy the ATG servers to your application server. Select a server instance (ATG server) and generate the appropriate EAR file. CIM supports all standard runAssembler options.
		
		\n\nAdditional information: See the ATG Programming Guide for runAssembler documentation. See the ATG Installation and Configuration Guide for general installation and database configuration information, and your product's Installation Guide for product-specific details.
		
		\n\nYour current menu options are:"},
		{"id":"DisplayMainMenu.database","content":"Configure data sources, create schemas, and import application data for the product and add-ons you selected."},
		{"id":"DisplayMainMenu.serverinstance","content":"Identify server instance types to create for your selected product and add-ons. Server instance types include Commerce Production, Management, and Datawarehouse Loader."},
		{"id":"DisplayMainMenu.appassembly","content":"Create an EAR file and deploy it to your application server."},
		{"id":"DisplayMainMenu.productselector","content":"Select the product to configure. You can configure only one product and its add-ons at a time."},
		{"id":"DisplayMainMenu.appselector","content":"Specify the application server on which your ATG products will be deployed. ATG  supports JBoss, WebLogic, and WebSphere. See www.atg.com for details on supported platforms."},
		{"id":"DisplayMainMenu.customPluginLauncher","content":"Run custom CIM plugins."},
		{"id":"DisplayMainMenu.batchsave","content":"As you use CIM, you can save the selections you make into a batch file. This is useful if you need to repeat a given configuration in precisely the same way multiple times. To record a batch file, start CIM using the -record flag:\n\n     cim - record\n\nTo run the saved file, use the command:\n\n     cim.bat -batch \\path\\to\\batchfile.cim"},

		/* Custom plug-in*/

		{"id":"CustomPluginSelectionStep","content":"If you have created custom CIM plug-ins, they are listed below. Select a plug-in to run it."},

	]

}