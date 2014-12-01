
# wl-online-deploy-ear.py - wlst script to deploy an ear file to via a running adminserver


username = '${wl.adminserver.username}'   # for example: 'weblogic'

password = '${wl.adminserver.password}' # for example: 'weblogic'

connect(username,password,'${wl.adminserver.url}') # example url: 't3://localhost:7001'

print 'Deploying ${wl.deploy.earpath} to ${wl.deploy.servername}....' # example servername: "PubServer"
edit()
startEdit()

cd('AppDeployments')
deployments = ls(returnMap='true')
if not deployments:
  deployments = []
if('${wl.deploy.appname}' in deployments):
  undeploy(appName="${wl.deploy.appname}")

# example servername: "PubServer"
deploy(appName="${wl.deploy.appname}", path="${wl.deploy.earpath}", targets="${wl.deploy.servername}")

save()
activate(block="true")
exit('y')
