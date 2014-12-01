#
# wl-create-ds-prologue.py - the first segment of a list that creates on or
#  more datasources. This segment defines the createJdbcSystemResource function,
#  and opens a domain for offline editing.
#
import os
import sys

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



def createJdbcSystemResource(jndiResourceName, jdbcUrl=None, jdbcUser=None, jdbcPassword=None, jdbcDriver=None, useXaDataSource='true', serverName=None, dbServer=None, databaseName=None, jdbcPort=None, dbType=None):
    cd('/')

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
        cd('/JDBCSystemResources/' + jndiResourceName)
        # get the old targets to restore later
        oldTargets.extend(get('Target'))
        cd('/')
        # just nuke and re-create
        delete(jndiResourceName, 'JDBCSystemResource')
        print "Re-creating datasource " + jndiResourceName        
        pass
    else:
        print "Creating datasource " + jndiResourceName
        pass
    create(jndiResourceName,'JDBCSystemResource')
    cd('/JDBCSystemResource/' + jndiResourceName)
    print 'oldTargets =', oldTargets
    if oldTargets:
        set('Target', oldTargets)
        pass
    set('DescriptorFileName', 'jdbc/' + jndiResourceName + '-3272-jdbc.xml')
    cd('/JDBCSystemResource/' + jndiResourceName + '/JdbcResource/' + jndiResourceName)
    create('MyDriverParams', 'JDBCDriverParams')
    cd('JDBCDriverParams/NO_NAME_0')
    set('DriverName', jdbcDriver)
    set('PasswordEncrypted', jdbcPassword)
    set('URL', jdbcUrl)
    set('UseXADataSourceInterface', useXaDataSource)
    create('MyDriverProps', 'Properties')
    cd('Properties/NO_NAME_0')
    create('user', 'Property')
    cd('Property/user')
    cmo.setValue(jdbcUser)
    if(dbType == 'inet'):
        cd('../..')
        create('database', 'Property')
        cd('Property/database')
        cmo.setValue(databaseName)
        cd('../..')
        create('port', 'Property')
        cd('Property/port')
        cmo.setValue(jdbcPort)
        cd('../..')
        create('mode', 'Property')
        cd('Property/mode')
        cmo.setValue('71')
        cd('../..')
        create('secureLevel', 'Property')
        cd('Property/secureLevel')
        cmo.setValue('0')
        cd('../..')
        create('serverName', 'Property')
        cd('Property/serverName')
        cmo.setValue(dbServer)
        cd('../..')
        pass
    cd('/JDBCSystemResource/' + jndiResourceName + '/JdbcResource/' + jndiResourceName)
    create('MySrcParams','JDBCDataSourceParams')
    cd('JDBCDataSourceParams/NO_NAME_0')
    set('GlobalTransactionsProtocol','TwoPhaseCommit')
    set('JNDIName', java.lang.String(jndiResourceName))
    cd('/JDBCSystemResource/' + jndiResourceName + '/JdbcResource/' + jndiResourceName)
    create('MyConPoolParams','JDBCConnectionPoolParams')
    #cd('JDBCConnectionPoolParams/NO_NAME_0')
    #set('TestTableName','SQL SELECT 1 FROM DUAL')
    cd('/')
    if serverName:
        assign('JDBCSystemResource', jndiResourceName, 'Target', serverName)
        pass
    # now we are going to construct a dictionary, based on oldTargets and
    # our currently assigned target, since sometimes it seems to lose the
    # old target on assign
    targetDict = {}
    for curServer in oldTargets:
        # targetDict[curServer.getName()] = curServer
        # print "Re-assigning " + curServer.getName()
        assign('JDBCSystemResource', jndiResourceName, 'Target', curServer.getName()) 
        pass
    cd('/')
    pass


readDomain('${wl.domain.dir}')

success = 0

try:

