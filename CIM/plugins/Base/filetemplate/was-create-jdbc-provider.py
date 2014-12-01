tempnode = '${was.nodename}'

False = 0
True = not 0

node = AdminConfig.getid('/Node:'+tempnode + '/')
print node

type = '${db.type}'
existing = AdminConfig.getid('/JDBCProvider:'+type)

newVarValue = "${db.driverpaths}"

pathList = newVarValue.split(';') #Most JDBC Providers have 1 driver path. DB2 has two. 

directoryList = []
jarList = []
for path in pathList:
  if(path.rfind('/') <> -1):
    directoryList += [path[0:path.rfind('/')]]
    jarList += [path[path.rfind('/'):len(path)]]
  else: directoryList += [path]
  
if(existing == ''):
  print type
  providerTemplate = ''
  implClassName = []
  template = False
  
  if(('oracle' == type) or ('oraclethin' == type) or ('oracleoci' == type)):
    providerTemplate=AdminConfig.listTemplates('JDBCProvider', 'Oracle JDBC Driver Provider Only (XA)')
    implClassName = ['implementationClassName', 'oracle.jdbc.xa.client.OracleXADataSource']
    varName1 = "ORACLE_JDBC_DRIVER_PATH"
    template = True
    
  if('db2' == type):
    providerTemplate=AdminConfig.listTemplates('JDBCProvider', 'DB2 Universal JDBC Driver Provider Only (XA)')
    implClassName = ['implementationClassName', 'com.ibm.db2.jcc.DB2XADataSource']
    varName1 = "DB2UNIVERSAL_JDBC_DRIVER_PATH"
    varName2 = "UNIVERSAL_JDBC_DRIVER_PATH"
    template = True
    
  if('mssql' == type):
    providerTemplate=AdminConfig.listTemplates('JDBCProvider', 'WebSphere embedded ConnectJDBC driver for MS SQL Server Provider Only (XA)')
    implClassName = ['implementationClassName', 'com.ibm.websphere.jdbcx.sqlserver.SQLServerDataSource']
    varName1 = "MSSQLSERVER_JDBC_DRIVER_PATH"
    template = True
    #Websphere and MSSQL is not supported in ATG9.0
    
  if('inet' == type):
     provideTemplate=AdminConfig.listTemplates('JDBCProvider', 'DataDirect ConnectJDBC type 4 driver for MS SQL Server (XA)')
     implClassName = ['implementationClassName', 'com.ddtek.jdbcx.sqlserver.SQLServerDataSource']
     varName1 = "CONNECTJDBC_JDBC_DRIVER_PATH"
     template = True
   
  
  if('mysql' == type):
    implClassName = ['implementationClassName', 'com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource']
    varName1 = "User-defined_JDBC_DRIVER_PATH"
    
  providerName = ['name', '${db.type}']
  if(len(jarList) == 0):
    classpathString = '${'+varName1+'}'
  else: classpathString = '${'+varName1+'}'+jarList[0]
  classpath = ['classpath', classpathString]
    
  jdbcAttrs = [providerName, implClassName, classpath] 
  
  varSubstitutions = AdminConfig.list("VariableSubstitutionEntry",node).split(java.lang.System.getProperty("line.separator"))

  for varSubst in varSubstitutions:
   getVarName = AdminConfig.showAttribute(varSubst, "symbolicName")
   if getVarName == varName1:
      AdminConfig.modify(varSubst,[["value", directoryList[0]]])
   if(type == 'db2' and len(directoryList) > 1):
    if getVarName == varName2:
      AdminConfig.modify(varSubst,[["value", directoryList[1]]])

  if(template == True):
    print 'Creating from ' + ' ${db.type} ' + 'template...'
    jdbcprovider =   AdminConfig.createUsingTemplate('JDBCProvider', node, jdbcAttrs, providerTemplate)
  else:  jdbcprovider = AdminConfig.create('JDBCProvider', node, jdbcAttrs)
  
  #Need to modify the db2 provider for the correct classpath
  if(type == 'db2' and len(jarList) > 1):
    classpathString = '[[classpath ${'+varName2+'}'+jarList[1]+']]'
    AdminConfig.modify(jdbcprovider, classpathString)
  
  
  AdminConfig.save()