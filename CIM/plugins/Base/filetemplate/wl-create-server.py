#
# wl-create-server.py - A script for the offline creation of servers.
#



######################
# Utility functions 

def createServer(serverName, httpPort=7001, sslPort=7002, onlyIfNotExists=1):
    cd('/')
    servers = ls('/Server', returnMap='true')
    if not servers:
        servers = []
        pass
    if (serverName in servers) and onlyIfNotExists:
        return 0
    if not (serverName in servers):
        create(serverName,'Server')
        pass
    cd('/Server/' + serverName)
    set('ListenAddress', '')
    set('ListenPort', httpPort)
    set('TunnelingEnabled', 'True')
    cd('/')
    return 1



######################
# Replacement here



readDomain('${wl.domain.dir}')

if (createServer('${wl.servername}', httpPort=${wl.http.port} , sslPort=${wl.https.port})):
    updateDomain()
    pass
closeDomain()
exit()
