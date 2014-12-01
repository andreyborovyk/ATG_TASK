This archive contains the source code and workflows for creating a programatic import. This archive
is provided as is and is not supported. In the archive you will find the workflows, a class, and a
properties file. 

There are two workflows in the package. One if you are only using a production server, that is called
import.wdl. The other if you are using a staging and production server, that is called import-staging.wdl.
Choose the one that is appropriate for your environment's configuration.

In order to use this package, you should extract it into your module and subclass the
ProgramaticImportService class. This is an abstract class. In your subclass you should override
the importUserData(Process, TransactionDemarcation) method. It is in this method that you will place
your logic for importing the data. No need to worry about creating projects and advancing the workflow.
The base class will do that for you.

This class is designed to work with the provided workflows. If the workflow is modified, then you may
need to change the taskOutcomeId property in order to have the workflow advance correctly.

In order to start execution, another service must call the executeImport() method in the base class.

Note that the class is designed to use a single transaction. This is fine for small imports. However,
if a large import is required, then the user is responsible for transaction batching. This is the
reason the TransactionDemarcation object is passed to the importUserData() method.
