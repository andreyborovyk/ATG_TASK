
connect('${wl.adminserver.username}', '${wl.adminserver.password}', '${wl.adminserver.url}')

cd('AppDeployments')
deployments = ls(returnMap='true')

try:
  if not deployments:
    #Recieved a dictionary, was expecting a list, but empty so moving on. 
    pass
  elif('${wl.deploy.appname}' in deployments):
      print 'CIMFOUND'
except:
    print "Unexpected error:", sys.exc_info()[0]
exit('y')