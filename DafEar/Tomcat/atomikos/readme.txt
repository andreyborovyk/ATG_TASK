This directory contains the minimal jars and configuration for
Atomikos Transactions Essentials version 3.6.5. See:

  http://www.atomikos.com/Main/TransactionsEssentials

Please see the license.txt and notice.txt files for licensing details.

This directory also contains the tomcat integration classes
(version 1) downloaded from:

   http://atomikos.com/Documentation/Tomcat6Integration33

With modifications to BeanFactory (contained in
atomikos-tomcat-beanfactory.jar) to allow it to work with both XA and
non-XA datasources. The modified is source available in the included
atomikos/java/com/atomikos/tomcat/BeanFactory.java file.

Additionally, the following additions were made to jta.properties:

  com.atomikos.icatch.enable_logging=false
  com.atomikos.icatch.threaded_2pc=false
  com.atomikos.icatch.output_dir = ${catalina.base}/logs
  com.atomikos.icatch.log_base_dir = ${catalina.base}/logs  
  com.atomikos.icatch.default_jta_timeout = 300000  
  com.atomikos.icatch.max_timeout = 1800000
  com.atomikos.icatch.serial_jta_transactions=${atg.notUsingAtomikosWithMySQL}  
where atg.notUsingAtomikosWithMySQL is to false by ATG's TomcatClassLoader
  when it appears that we are using MySQL, and true when we are not
  using MySQL.


