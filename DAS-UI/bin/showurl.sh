#!/bin/sh

nn=${NETSCAPE:-netscape}

# If the netscape command is found in the command path, use it;
# otherwise, use mozilla.  Regardless of which browser is used,
# attempt first to use the -remote arg to open the given URL in a
# browser which is already running; if that fails, start a new
# browser.  (Alas, it seems there is a bug in netscape/mozilla
# browsers such that the -remote command does not return a non-zero
# exit status, as advertised, when no browser currently exists so the
# command for bringing up a new browser never gets called.)
if command -v ${nn} >&/dev/null; then
  ${nn} -remote "openURL(${1})" || ${nn} "${1}" &
else
  mozilla -remote "openURL(${1})" || mozilla "${1}" &
fi