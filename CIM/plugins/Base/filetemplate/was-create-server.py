# COPYRIGHT ATG 2008
 
# Was-Deploy.py
#
# Deploys the ear file using the default bindings, and then saves the configuration. 
#

print 'Creating New Server...' + '${was.deploy.servername}'

node='${was.deploy.nodename}'
nodeId=AdminConfig.getid('/Node:'+ '${was.deploy.nodename}')
cellId=AdminConfig.list('Cell')
serverName='${was.deploy.servername}'
coregroup='DefaultCoreGroup'

try:
    AdminTask.createApplicationServer(node, '-name ' + serverName )
except:
    print 'Application Server Creation failed. IBM WebSphere reports the following error: ', sys.exc_info()[0]
    raise
else:
    print 'Server created successfully.'

try:
    AdminTask.modifyServerPort(serverName, ['-nodeName', node, '-endPointName', 'WC_defaulthost', '-port', '${was.deploy.httpport}', '-modifyShared'])
    AdminTask.modifyServerPort(serverName, ['-nodeName', node, '-endPointName', 'WC_defaulthost_secure', '-port', '${was.deploy.httpsport}', '-modifyShared'])
except:
    print 'Unable to modify the server HTTP and HTTPS ports. IBM WebSphere reports the following error: ', sys.exc_info()[0]
else:
    print 'Set the HTTP and HTTPS port successfully.'

try:
    AdminConfig.create('VirtualHost', cellId, [['aliases', [[['hostname', '*'],['port','${was.deploy.httpport}']], [['hostname', '*'],['port', '${was.deploy.httpsport}']]]], ['name', serverName+'_host']])
    pass
except:
    print 'Virtual Host creation failed, possibly because it already exists.'
    print 'IBM WebSphere reports the following error: ', sys.exc_info()[0]
    pass

AdminConfig.save()
