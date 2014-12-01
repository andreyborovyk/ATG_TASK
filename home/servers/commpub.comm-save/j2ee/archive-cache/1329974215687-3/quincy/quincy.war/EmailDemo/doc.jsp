<%@ taglib uri="/dspTaglib" prefix="dsp" %>
<dsp:page>

<HTML>
<HEAD>
<TITLE>Targeted Email Demo</TITLE>
</HEAD>

<BODY BGCOLOR="#FFFFFF" TEXT="#00214A" VLINK="#637DA6" LINK="#E87F02">
<dsp:include page="header.jsp" />
      
<br>
<p>

<h2>Documentation for the Targeted Email Demo</h2>

<h3>Template Email</h3>

The <b>atg.userprofiling.email</b> package provides the ability to
generate and send email based on jsp templates.  This means that 
droplet tags can be harnessed to format and target email messages.  
The key features of Template Email API are:

<ul>
<li> <b>TemplateEmailInfo</b> - contains the definition
of a template email message, including which jsp template to use
and the sender's email address, etc.
<li> <b>TemplateEmailSender</b> - can be used to send
a mailing to a number of profiles.
<li> <b>MessageContentProcessor</b> - is used to determine how the template
is processed when it is being turned into an email message.
</ul>

Please see the Targeted Email chapter of the DPS Programmer's Guide
for more information on the atg.userprofiling.email package.

<h3>About this demo</h3>

This demo builds upon the Template Email API to provide the following
features:

<ul>
<li> A MailingRequest component which combines a Profile Group, 
a TemplateEmailSender and a TemplateEmailInfo in one component; 
<li> The ability to manage a group of mailings from a single component
(a MailingApplication);
<li> The ability to schedule a mailing (a SchedulableMailingRequest);
<li> The ability to define a mailing that will be driven by events in Dynamo
(an EventDrivenMailingRequest);
<li> A simple jsp user interface for creating new mailings, canceling
mailings and viewing the results of mailings. 
</ul>

<h3>How to use this demo</h3>

The demo UI consists mainly of two pages: Summary of Mailings and
Creating a New Mailing.

<h4>Summary of Mailings</h4>

The Summary of Mailings page lists all the mailings known to the
MailingApplication.  The top table summarizes mailings that have been
scheduled to run in the future, or periodically.  The demo comes with
an example scheduled mailing, Weekly News, which runs every 7 days;
the first weekly mailing is sent out when the MailingApplication
service is first started.  As you can see from the Scheduled Mailings
table, the weekly news are sent out to all investors, with the content
provided by the template in news.jsp.  You can view other mailing
details, such as the message Subject and From fields, by clicking on
the name of the mailing.

<p>

Similarly, the second table summarizes all the event-driven mailings.
The example event-driven mailing, Investment Opportunities, is
triggered whenever a user browses the page fundlist.jsp on the
Quincy Funds site.  When this happens, the user receives an email
message based on the template in funds.jsp.

<p> 

The bottom two tables are initially empty, and are populated by the
system as the attempted mailings complete or fail.  For example, when
the MailingApplication is started, a Weekly News mailing is run for
the first time; if everything is configured correctly, you should see
an entry in the Completed Mailings table corresponding to the mailing
result.  Note that the Completed Mailings table lists both the number
of messages that were sent and the number of messages that failed -
due to a null or invalid email address, for instance.  You can view
the result details by following the link in the Result column; this
shows the error messages for any unsuccessful message sends.

<p>

Any mailings that could not be completed at all are listed in the
Failed Mailings table, together with the exception that caused the
failure.

<p>

Note that as with any HTML interface, the Summary of Mailings page
does not get updated automatically as mailings are performed.  You
must reload the page to see the latest information.

<h4>Creating a New Mailing</h4>

The Create a New Mailing page allows you to create your own mailings
via a series of easy steps.  Steps 1-6 ask you straightforward
questions about the new mailing, such as the jsp template to be used
when creating the message, the email From and Subject fields, the
profile group to which the mailing should be directed, and so on.  In
step 7, you specify whether the mailing should be run immediately,
scheduled to be run in the future, or be driven by user events.  For
scheduled mailings, you must enter the schedule according to which the
mailings will be sent out.  For event-driven mailings, you specify the
type of event that should trigger the mailing; for page events, you
must also specify the path to the triggering page.  

<p>

When you hit the CreateMailing button, a new mailing will be created
and added to the list of demo mailings.  If you indicate that the
mailing is to be run immediately, it will be started, and in a few
seconds you will be able to see the result on the summary page.  If
your mailing is a scheduled or event-driven mailing, it will be added
to the corresponding table in the Summary of Mailings page.  A
scheduled mailing will then be scheduled to run according to its
schedule; for an event-driven mailing, an event channel handler
will be created to watch for the specified events and trigger the
mailings.  This event channel handler will be given the name you specify
in step 7 above.

<p>

Note that the mailings you create using the Create a New Mailing page
do not persist across Dynamo restarts.  To create persistent mailings,
you must modify the demo configuration, as described below.

<h3>Extending this demo</h3>

If you want a specific mailing to persist between restarts of the server you
will have to change the <b>initialMailings</b> property of the <b>MailingApplication</b>
component to point to additional MailingRequests.  You can do this
by using the Dynamo ACC.

<p>

If you add new profile groups through the Dynamo Personalization
Control Center (PCC) they will be available to be used when creating
a new mailing.

<p>

If you want to change the default templates that are available
from the Create a New Mailing page you can add the path to
that template to <b>demoTemplates</b> property of the 
<b>MailingRequestForm</b> component. You can do this by using the Dynamo ACC.


<h3>Java sources for this demo</h3>

The Java code sources for this demo
can be found in the /demo/src/maildemo directory of your DPS 
install.
<p>
The Java classes show how a simple mailing application can be built
around the atg.userprofiling.email package. 

<dsp:include page="../footer.jsp" />
</BODY>
</HTML>
</dsp:page>
<%-- @version $Id: //product/DSS/version/10.0.3/release/DSSJ2EEDemo/j2ee-apps/QuincyFunds/web-app/EmailDemo/doc.jsp#2 $$Change: 651448 $--%>
