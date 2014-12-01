# wl-create-ds-command.py



  # this command is used for both online and offline datasource creation
  createJdbcSystemResource('${wl.jdbc.jndiname}',
                           jdbcUrl='${wl.jdbc.url}',
                           jdbcPassword='${wl.jdbc.password}',
                           jdbcDriver='${wl.jdbc.driver}',
                           jdbcUser='${wl.jdbc.user}',
                           serverName='${wl.servername}',
                           dbServer='${wl.jdbc.dbserver}',
                           databaseName='${wl.jdbc.dbname}',
                           jdbcPort='${wl.jdbc.port}',
                           dbType='${wl.dbtype}')

