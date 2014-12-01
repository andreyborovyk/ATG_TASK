You should use this directory to store any changes that you need to make
to your Dynamo configuration for the "original" server.

Any property files stored here will be used when you run startDynamo without
any arguments.  They will override all other .properties files in the system.

The most commonly configured entries are stored in the file:

  <ATGdir>/home/localconfig/atg/dynamo/Configuration.properties

This file is created when you install Dynamo.  Although you can also
just modify values in the ../config directory itself, these changes will be
lost when you download a new release of Dynamo.  Any changes made in this
directory will be preserved.

See the Nucleus chapter in the Dynamo Programmer's Guide for more information
about setting values in .properties files.


