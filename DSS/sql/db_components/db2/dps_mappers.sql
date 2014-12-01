


--  @version $Id: //product/DSS/version/10.0.3/templates/DSS/sql/dps_mappers.xml#2 $$Change: 651448 $

create table dss_dps_event (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	profileid	varchar(25)	default null,
	scenarioPathInfo	varchar(254)	default null);


create table dss_dps_page_visit (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	path	varchar(255)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_view_item (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	repositoryname	varchar(255)	default null,
	folder	varchar(255)	default null,
	itemtype	varchar(255)	default null,
	repositoryid	varchar(255)	default null,
	itemdescriptor	varchar(255)	default null,
	page	varchar(255)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_click (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	destinationpath	varchar(255)	default null,
	sourcenames	varchar(255)	default null,
	sourcepath	varchar(255)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_referrer (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	referrerpath	varchar(255)	default null,
	referrersite	varchar(255)	default null,
	referrerpage	varchar(255)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_inbound (
	id	varchar(32)	not null,
	timestamp	timestamp	default null,
	messagesubject	varchar(255)	default null,
	originalsubject	varchar(255)	default null,
	messagefrom	varchar(64)	default null,
	messageto	varchar(255)	default null,
	messagecc	varchar(255)	default null,
	messagereplyto	varchar(64)	default null,
	receiveddate	numeric(19,0)	default null,
	bounced	varchar(6)	default null,
	bounceemailaddr	varchar(255)	default null,
	bouncereplycode	varchar(10)	default null,
	bounceerrormess	varchar(255)	default null,
	bouncestatuscode	varchar(10)	default null);


create table dss_dps_admin_reg (
	id	varchar(32)	not null,
	clocktime	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	adminprofileid	varchar(25)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_property (
	id	varchar(32)	not null,
	clocktime	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	propertypath	varchar(254)	default null,
	oldvalue	varchar(254)	default null,
	newvalue	varchar(254)	default null,
	changesign	varchar(16)	default null,
	changeamount	numeric(19,7)	default null,
	changepercentage	numeric(19,7)	default null,
	elementsadded	varchar(254)	default null,
	elementsremoved	varchar(254)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_admin_prop (
	id	varchar(32)	not null,
	clocktime	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	propertypath	varchar(254)	default null,
	oldvalue	varchar(254)	default null,
	newvalue	varchar(254)	default null,
	changesign	varchar(16)	default null,
	changeamount	numeric(19,7)	default null,
	changepercentage	numeric(19,7)	default null,
	elementsadded	varchar(254)	default null,
	elementsremoved	varchar(254)	default null,
	adminprofileid	varchar(25)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_update (
	id	varchar(32)	not null,
	clocktime	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	changedproperties	varchar(4000)	default null,
	oldvalues	varchar(4000)	default null,
	newvalues	varchar(4000)	default null,
	profileid	varchar(25)	default null);


create table dss_dps_admin_up (
	id	varchar(32)	not null,
	clocktime	timestamp	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	changedproperties	varchar(4000)	default null,
	oldvalues	varchar(4000)	default null,
	newvalues	varchar(4000)	default null,
	adminprofileid	varchar(25)	default null,
	profileid	varchar(25)	default null);


create table dps_scenario_value (
	id	varchar(40)	not null,
	tag	varchar(42)	not null,
	scenario_value	varchar(100)	default null
,constraint dps_scenario_val_p primary key (id,tag)
,constraint dps_scenrvlid_f foreign key (id) references dps_user (id));

create index dps_scenval_id on dps_scenario_value (id);
commit;


