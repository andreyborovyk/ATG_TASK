#
# wl-online-create-ds-prologue.py - the first segment of a list that creates on or
#  more datasources. This segment defines the createJdbcSystemResource function,
#  opens a connection connection to the admin server, and starts an edit session
# 

import os
import sys

# this is adapted from the
# wlserver_10.0/samples/server/examples/src/examples/wlst/online/wl-online-create-ds-prologue.py

# remember whether we had to put in a global redirect
isRedirecting = 0

def quieterLs(dirName=None, returnMap='true'):
    """A version of ls() that returns the results as a list, and silences the output."""

    global isRedirecting

    if (isRedirecting):
        sys.stdout.write('Redirecting for quieter ls... done. ')
        stopRedirect()
        pass
        
    if (os.sep == '/'):
        redirect("/dev/null", toStdOut='false')        
        pass
    else:
        redirect("NUL:", toStdOut='false')
        pass

    try:
        if (dirName):
            result = ls(dirName, returnMap=returnMap)
        else:
            result = ls(returnMap=returnMap)
            pass

        if result:
            return result
        # always return a list
        return []
    finally:
        stopRedirect()

        # okay, here we have to start a new redirect, or
        # the old toStdOut value sticks, and we have no output
        if (os.sep == '/'):
            redirect("/dev/null", toStdOut='true') 
            pass
        else:
            redirect("NUL:", toStdOut='true')
            pass
        isRedirecting = 1
        pass
    pass

def addInetProperties(driverProperties, propNames, serverName, databaseName, jdbcPort):
    if not 'port' in propNames:
        driverProperties.createProperty("port").setValue(jdbcPort)
    else:
        cd('Properties/port')
        cmo.setValue(jdbcPort)
        cd('../..')
        pass

    if not 'mode' in propNames:
        driverProperties.createProperty("mode").setValue("71")
    else:
        cd('Properties/mode')
        cmo.setValue("71")
        cd('../..')
        pass


    if not 'secureLevel' in propNames:
        driverProperties.createProperty("secureLevel").setValue("0")
    else:
        cd('Properties/secureLevel')
        cmo.setValue("0")
        cd('../..')
        pass

    
    if not 'serverName' in propNames:
        driverProperties.createProperty("serverName").setValue(serverName)
    else:
        cd('Properties/serverName')
        cmo.setValue(serverName)
        cd('../..')
        pass

    
    if not 'database' in propNames:
        driverProperties.createProperty("database").setValue(databaseName)
    else:
        cd('Properties/database')
        cmo.setValue(databaseName)
        cd('../..')
        pass


def createJdbcSystemResource(jndiResourceName, jdbcUrl=None, jdbcUser=None, jdbcPassword=None, jdbcDriver=None, useXaDataSource='false', serverName=None, dbServer=None, databaseName=None, jdbcPort=None, dbType=None):
    cd("/")
    serverTarget = None
    if serverName:
        cd("Servers/" + serverName)
        serverTarget = cmo
        cd("../..")
        pass

    rootNames = quieterLs(returnMap='true')
    if ('JDBCSystemResources' in rootNames):
        jdbcResources = quieterLs('/JDBCSystemResources', returnMap='true')
    elif ('JDBCSystemResource' in rootNames):
        jdbcResources = quieterLs('/JDBCSystemResource', returnMap='true')
    else:
        jdbcResources = []
        pass
    oldTargets = []
    if jndiResourceName in jdbcResources:
        # just nuke and re-create
        cd('/JDBCSystemResources/' + jndiResourceName)
        oldTargets = get('Targets')
        cd('../..')
        delete(jndiResourceName, 'JDBCSystemResource')
        print "Re-creating datasource", jndiResourceName        
    else:
        print "Creating datasource", jndiResourceName
        pass
    jdbcSR = create(jndiResourceName,"JDBCSystemResource")
    # should we do this?
    theJDBCResource = jdbcSR.getJDBCResource()
    
    theJDBCResource.setName(jndiResourceName)  # why this?
    dsParams = theJDBCResource.getJDBCDataSourceParams()
    dsParams.addJNDIName(jndiResourceName)  # and this?
    driverParams = theJDBCResource.getJDBCDriverParams()
    driverParams.setUrl(jdbcUrl)
    driverParams.setDriverName(jdbcDriver)
    driverParams.setPassword(jdbcPassword)
    
    driverProperties = driverParams.getProperties()
    oldPath = getPath(cmo)
    cd(getPath(driverProperties))
    
    propNames = quieterLs('Properties', returnMap='true')
        
    if not 'user' in propNames:
        driverProperties.createProperty("user").setValue(jdbcUser)
    else:
        cd('Properties/user')
        cmo.setValue(jdbcUser)
        cd('../..')
        pass

    if dbType == 'inet':
        addInetProperties(driverProperties, propNames, dbServer, databaseName, jdbcPort)
    else:
        if not 'DatabaseName' in propNames:
            driverProperties.createProperty("DatabaseName").setValue(jdbcUrl)
        else:
            cd('Properties/DatabaseName')
            cmo.setValue(jdbcUrl)
            cd('../..')
            pass
        
    for target in oldTargets:
        cd("/" + getPath(target))
        jdbcSR.addTarget(cmo)
        pass
    if (serverTarget):
        jdbcSR.addTarget(serverTarget)
        pass
    cd('/')
    pass



username = '${wl.adminserver.username}'   # for example: 'weblogic'

password = '${wl.adminserver.password}' # for example: 'weblogic'

connect(username,password,'${wl.adminserver.url}') # example url: 't3://localhost:7001'

edit()

startEdit()

success = 0

try: 
