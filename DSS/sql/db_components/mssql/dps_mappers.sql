


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/dps_mappers.xml#2 $$Change: 651448 $

create table dss_dps_event (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	profileid	varchar(25)	null,
	scenarioPathInfo	varchar(254)	null)


create table dss_dps_page_visit (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	path	varchar(255)	null,
	profileid	varchar(25)	null)


create table dss_dps_view_item (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	repositoryname	varchar(255)	null,
	folder	varchar(255)	null,
	itemtype	varchar(255)	null,
	repositoryid	varchar(255)	null,
	itemdescriptor	varchar(255)	null,
	page	varchar(255)	null,
	profileid	varchar(25)	null)


create table dss_dps_click (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	destinationpath	varchar(255)	null,
	sourcenames	varchar(255)	null,
	sourcepath	varchar(255)	null,
	profileid	varchar(25)	null)


create table dss_dps_referrer (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	referrerpath	varchar(255)	null,
	referrersite	varchar(255)	null,
	referrerpage	varchar(255)	null,
	profileid	varchar(25)	null)


create table dss_dps_inbound (
	id	varchar(32)	not null,
	timestamp	datetime	null,
	messagesubject	varchar(255)	null,
	originalsubject	varchar(255)	null,
	messagefrom	varchar(64)	null,
	messageto	varchar(255)	null,
	messagecc	varchar(255)	null,
	messagereplyto	varchar(64)	null,
	receiveddate	numeric(19,0)	null,
	bounced	varchar(6)	null,
	bounceemailaddr	varchar(255)	null,
	bouncereplycode	varchar(10)	null,
	bounceerrormess	varchar(255)	null,
	bouncestatuscode	varchar(10)	null)


create table dss_dps_admin_reg (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	adminprofileid	varchar(25)	null,
	profileid	varchar(25)	null)


create table dss_dps_property (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	propertypath	varchar(254)	null,
	oldvalue	varchar(254)	null,
	newvalue	varchar(254)	null,
	changesign	varchar(16)	null,
	changeamount	numeric(19,7)	null,
	changepercentage	numeric(19,7)	null,
	elementsadded	varchar(254)	null,
	elementsremoved	varchar(254)	null,
	profileid	varchar(25)	null)


create table dss_dps_admin_prop (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	propertypath	varchar(254)	null,
	oldvalue	varchar(254)	null,
	newvalue	varchar(254)	null,
	changesign	varchar(16)	null,
	changeamount	numeric(19,7)	null,
	changepercentage	numeric(19,7)	null,
	elementsadded	varchar(254)	null,
	elementsremoved	varchar(254)	null,
	adminprofileid	varchar(25)	null,
	profileid	varchar(25)	null)


create table dss_dps_update (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	changedproperties	text	null,
	oldvalues	text	null,
	newvalues	text	null,
	profileid	varchar(25)	null)


create table dss_dps_admin_up (
	id	varchar(32)	not null,
	clocktime	datetime	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	changedproperties	text	null,
	oldvalues	text	null,
	newvalues	text	null,
	adminprofileid	varchar(25)	null,
	profileid	varchar(25)	null)


create table dps_scenario_value (
	id	varchar(40)	not null,
	tag	varchar(42)	not null,
	scenario_value	varchar(100)	null
,constraint dps_scenario_val_p primary key (id,tag)
,constraint dps_scenrvlid_f foreign key (id) references dps_user (id))

create index dps_scenval_id on dps_scenario_value (id)


go
