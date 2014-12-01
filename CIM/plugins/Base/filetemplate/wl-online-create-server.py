#
# wl-online-create-server.py - A script for the online creation of servers.
#



######################
# Utility functions 

def createServer(serverName, httpPort=7001, sslPort=7002, onlyIfNotExists=1):
    cd('/')
    servers = ls('/Servers', returnMap='true')
    if not servers:
        servers = []
        pass
    if (serverName in servers) and onlyIfNotExists:
        return 0
    if not (serverName in servers):
        server = create(serverName,'Servers')
        cd('/Servers/' + serverName)        
    else:
        cd('/Servers/' + serverName)        
        server = cmo
        pass
    server.setListenAddress('')
    server.setListenPort(httpPort)
    # tunnelingEnabled appears readonly for online mode. So don't try to set it
    # setTunnelingEnabled(1)
    cd('/')
    return 1


######################
# Replacement here



username = '${wl.adminserver.username}'   # for example: 'weblogic'

password = '${wl.adminserver.password}' # for example: 'weblogic'

connect(username,password,'${wl.adminserver.url}') # example url: 't3://localhost:7001'

edit()

startEdit()

success = 0

try: 
    createServer('${wl.servername}', httpPort=${wl.http.port} , sslPort=${wl.https.port})
    success = 1
finally:
    if (success):
        save()
        activate(timeout=600000, block="true")
    else:
        cancelEdit('y')
        pass
    pass
exit()
