
This directory contains the needed jars for using Bitronix
Transacation Manager (BTM) version 2.0.0beta2 with Tomcat. For more
information on the Bitronix Transaction Manager, see: http://docs.codehaus.org/display/BTM/Home

The btm-2.0.0b2-modified.jar in this directory contains a modified version of
bitronix.tm.recovery.RecoveryHelper.recover() where the line:

        Xid[] xids = resourceHolderState.getXAResource().recover(flags);

has been replaced with:

        Xid[] xids = null;
        try {
          xids = resourceHolderState.getXAResource().recover(flags);
        }
        catch (XAException e) {
          // ignore
          if ((sRecoveryFailureWarningCount++) < 10) {
            log.warn("Failed to recover "+ resourceHolderState + ". Ignoring...");
          }
        }

In addition, the following member variable was added to RecoveryHelper:

    static volatile int sRecoveryFailureWarningCount = 0; 

This allows start-up to continue with even without necessary user
permissions for a full recovery on Oracle. For production, please
follow the Oracle grant instructions found at:

  http://docs.codehaus.org/display/BTM/FAQ

To avoid the "Failed to recover" warning and possibly inconsistent
database state.


And additionally, in bitronix.tm.internal.XAResourceManager.enlist(),
modified:
            if (alwaysLastResources != null && alwaysLastResources.size() > 0) {
              throw new BitronixSystemException("cannot enlist more than one non-XA resource, tried enlisting " + xaResourceHolderState + ", already enlisted: " + alwaysLastResources.get(0));
            }

to read:

            if ((alwaysLastResources != null && alwaysLastResources.size() > 0)  &&
                false) {
              throw new BitronixSystemException("cannot enlist more than one non-XA resource, tried enlisting " + xaResourceHolderState + ", already enlisted: " + alwaysLastResources.get(0));
            }

in order to allow multiple non-XA DataSources to be enlisted (to enable
mysql to work).


The java source for the above changes are included in the
btm-2.0.0b2-modified.jar file.
