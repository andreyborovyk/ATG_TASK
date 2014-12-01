# COPYRIGHT ATG 2008
 
# Was-Deploy.py
#
# Deploys the ear file using the default bindings, and then saves the configuration. 
#

print 'Installing...'
server = AdminConfig.getid('/Server:' + '${was.deploy.servername}')
appname = '${was.deploy.appname}'
AdminApp.install('${was.deploy.earpath}', '-node ' + '${was.deploy.nodename}' + ' -server '+ '${was.deploy.servername}' + ' -appname ' + appname + ' -usedefaultbindings' + ' -defaultbinding.virtual.host ${was.deploy.servername}_host' )

#We need to enable this for Web App Layering
print 'Configuring WebApp Layering'
webContainer = AdminConfig.list("WebContainer", server)
webContainerProperties = AdminConfig.list('Property', webContainer)
if (webContainerProperties != ""):
	for containerPropertyID in webContainerProperties.split():
		if(containerPropertyID != ""):
			containerPropertyName = AdminConfig.showAttribute(containerPropertyID,'name')
			if(containerPropertyName == 'com.ibm.ws.webcontainer.invokefilterscompatibility'):
				print 'We removing the old one'
				AdminConfig.remove(containerPropertyID)
				break

property = ['name', 'com.ibm.ws.webcontainer.invokefilterscompatibility']
value = ['value','true']
description = ['description','Required for Web App Layering']
containerAttrs = [property, value, description]
AdminConfig.create("Property", webContainer, containerAttrs)

#Create the URL providers, if necessary
print 'Configuring URL Providers.'
node = AdminConfig.getid('/Node:${was.deploy.nodename}')
if( not AdminConfig.getid('/Node:${was.deploy.nodename}/URLProvider:dynamosystemresource')):
    print 'Configuring dynamosystemresource'
    name = ['name', 'dynamosystemresource']
    shcn = ['streamHandlerClassName', "atg.net.www.protocol.dynamosystemresource.Handler"]
    protocol = ['protocol', "dynamosystemresource"]
    urlpAttrs = [name, shcn, protocol]
    AdminConfig.create('URLProvider', node, urlpAttrs)
else:
    print 'dynamosystemresource URL Provider previously installed, skipping...'

if( not AdminConfig.getid('/Node:${was.deploy.nodename}/URLProvider:appmoduleresource')):
    print 'Configuring appmoduleresource URL Provider.'
    name = ['name', 'appmoduleresource']
    shcn = ['streamHandlerClassName', "atg.net.www.protocol.appmoduleresource.Handler"]
    protocol = ['protocol', "appmoduleresource"]
    urlpAttrs = [name, shcn, protocol]
    AdminConfig.create('URLProvider', node, urlpAttrs)
else:
    print 'appmoduleresource URL provider already installed, skipping...'

print 'Installation Completed'
AdminConfig.save()
