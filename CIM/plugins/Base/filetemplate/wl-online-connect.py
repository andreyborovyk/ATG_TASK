#
# wl-online-connect.py - Just open a connection to the weblogic adminserver. Used to
#   test to make sure connection parameters are correct
# 


username = '${wl.adminserver.username}'   # for example: 'weblogic'

password = '${wl.adminserver.password}' # for example: 'weblogic'

connect(username,password,'${wl.adminserver.url}') # example url: 't3://localhost:7001'
