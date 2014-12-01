


--  @version $Id: //product/DPS/version/10.0.3/templates/DPS/sql/logging_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements needed to configure your database for use with the DPS logging/reporting subsystem.This script will create tables and indexes associated with the newlogging and reporting subsystem. To initialize these tables run thelogging_init.sql script.
-- Tables for storing logging data for reports

create table dps_event_type (
	id	integer	not null,
	name	varchar(32)	not null
,constraint dps_event_type_p primary key (id)
,constraint dps_event_type_u unique (name));


create table dps_user_event (
	id	bigint	not null,
	timestamp	datetime	not null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	eventtype	integer	not null,
	profileid	varchar(25)	null,
	member	tinyint	default 0 not null
,constraint dps_user_event_p primary key (id)
,constraint dps_usrvevnttyp_f foreign key (eventtype) references dps_event_type (id)
,constraint dps_user_event_c check (member in (0,1)));

create index dps_user_event_ix on dps_user_event (eventtype);
create index dps_ue_ts on dps_user_event (timestamp);

create table dps_user_event_sum (
	eventtype	integer	not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null
,constraint dps_usrsuevnttyp_f foreign key (eventtype) references dps_event_type (id));

create index dps_user_ev_sum_ix on dps_user_event_sum (eventtype);
create index dps_ues_ft on dps_user_event_sum (fromtime,totime,eventtype);

create table dps_request (
	id	bigint	not null,
	timestamp	datetime	not null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	name	varchar(255)	not null,
	member	tinyint	default 0 not null
,constraint dps_request_p primary key (id)
,constraint dps_request_c check (member in (0,1)));

create index dps_r_ts on dps_request (timestamp);

create table dps_reqname_sum (
	name	varchar(255)	not null,
	member	tinyint	default 0 not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null
,constraint dps_reqname_sum_c check (member in (0,1)));

create index dps_rns_ft on dps_reqname_sum (fromtime,totime);

create table dps_session_sum (
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	member	tinyint	default 0 not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null
,constraint dps_session_sum_c check (member in (0,1)));

create index dps_rss_ft on dps_session_sum (fromtime,totime,sessionid);

create table dps_con_req (
	id	bigint	not null,
	timestamp	datetime	not null,
	requestid	bigint	null,
	contentid	varchar(255)	not null
,constraint dps_con_req_p primary key (id));

create index dps_cr_ts on dps_con_req (timestamp);

create table dps_con_req_sum (
	contentid	varchar(255)	not null,
	member	tinyint	default 0 not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null
,constraint dps_con_req_sum_c check (member in (0,1)));

create index dps_crs_ft on dps_con_req_sum (fromtime,totime);

create table dps_pgrp_req_sum (
	groupname	nvarchar(64)	not null,
	contentname	nvarchar(255)	not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null);

create index dps_prs_ft on dps_pgrp_req_sum (fromtime,totime);

create table dps_pgrp_con_sum (
	groupname	nvarchar(64)	not null,
	contentname	nvarchar(64)	not null,
	summarycount	integer	not null,
	fromtime	datetime	not null,
	totime	datetime	not null);

create index dps_pcs_ft on dps_pgrp_con_sum (fromtime,totime);

create table dps_log_id (
	tablename	varchar(30)	not null,
	nextid	bigint	not null
,constraint dps_log_id_p primary key (tablename));

commit;


