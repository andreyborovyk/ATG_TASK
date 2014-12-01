authAliasName="${db.alias}"
user="${db.user}"
password="${db.password}"
tempnode = '${was.nodename}'
tempcell = '${was.cellname}'

type="${db.type}"
helpername = ''

if(type == 'oracle' or type == 'oraclethin' or type == 'oracleoci'):
  helpername = "com.ibm.websphere.rsadapter.Oracle10gDataStoreHelper"
if(type == 'mssql' or type == 'inet'):
 helpername = "com.ibm.websphere.rsadapter.WSConnectJDBCDataStoreHelper"
# Websphere and MSSQL is not supported in ATG9.0
if(type == 'db2'):
  helpername = "com.ibm.websphere.rsadapter.DB2DataStoreHelper"
#MySQL and MSSQL are dev-only supported

provider = AdminConfig.getid('/Node:' + tempnode + '/JDBCProvider:${db.type}')
node = AdminConfig.getid('/Node:' + tempnode)
dsName = '${was.jndiname}'
dsJNDIName = '${was.jndiname}'
dsDescription = 'Data source for ' + '${db.alias}'

dataSourceEntries=AdminConfig.list("DataSource", node)

if (dataSourceEntries != ""):
  dataSourceEntryList=dataSourceEntries.split(lineSeparator)
  for dataSourceEntry in dataSourceEntryList:
     if (dsName == dataSourceEntry.split("(")[0]):
       print "- " + dsName + " already exists"
       ds=dataSourceEntry
       break
     else:
      ds="None"
else:
  ds="None"
 
#---------------------------------------------------
# If the DataSource exists, delete it
#---------------------------------------------------

if (ds != "None"):
  print "- DataSource found. Removing Datasource: " + dsName
  AdminConfig.remove(ds)
  AdminConfig.save()
else:
  print "- DataSource not found."
# Create The Datasource

print "- Creating new DataSource: " + dsName
dsAttrs=[["name", dsName], ["description", dsDescription], ]
datasource=AdminConfig.create("DataSource", provider, dsAttrs)

dsPropSetId = AdminConfig.create("J2EEResourcePropertySet", datasource, [] )

if(type=='oracle' or type=='oraclethin' or type=='oracleoci'): #Only Oracle needs the URL
  dsPropAttrib = [["name", "URL"], ["type", "java.lang.String"], ["value", "${db.url}"]]
  AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib )
  
dsPropAttrib2 = [["name", "databaseName"], ["type", "java.lang.String"], ["value", "${db.name}"]]
dsPropAttrib3 = [["name", "serverName"], ["type", "java.lang.String"], ["value", "${db.hostname}"]]
dsPropAttrib4 = [["name", "portNumber"], ["type", "java.lang.Integer"], ["value", "${db.port}"]]
dsPropAttrib5 = [["name", "user"], ["type", "java.lang.String"], ["value", user]]
dsPropAttrib6 = [["name", "password"], ["type", "java.lang.String"], ["value", password]]
dsPropAttrib7 = [["name", "webSphereDefaultIsolationLevel"], ["type", "java.lang.Integer"], ["value", "2"]]


AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib2 )
AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib3 )
AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib4 )
AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib5 )
AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib6 )
AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib7 )
  
if(type=='db2'): #Only DB2 needs the driverType.
  dsPropAttrib8 = [["name", "driverType"], ["type", "java.lang.Integer"], ["value", "4"]]
  AdminConfig.create("J2EEResourceProperty", dsPropSetId, dsPropAttrib8)

map_auth_attr=["authDataAlias", authAliasName]
map_configalias_attr=["mappingConfigAlias", "DefaultPrincipalMapping"]
map_attrs=[map_auth_attr, map_configalias_attr]
mapping_attr=["mapping", map_attrs]

dsAttrs=[]
dsAttrs.append(["jndiName", dsJNDIName])
dsAttrs.append(["authDataAlias", authAliasName])
dsAttrs.append(["datasourceHelperClassname", helpername])
dsAttrs.append(mapping_attr)

AdminConfig.modify(datasource, dsAttrs)

AdminConfig.save()
