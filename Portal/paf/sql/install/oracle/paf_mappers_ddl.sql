


--  @version $Id: //app/portal/version/10.0.3/paf/sql/paf_mappers_ddl.xml#2 $$Change: 651448 $

create table paf_page_visit (
	id	varchar2(40)	not null,
	tstamp	date	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	page_path	varchar2(255)	null,
	msg_type	varchar2(255)	null);

create index paf_pgvst_comid on paf_page_visit (community_id);
create index paf_pgvst_gearid on paf_page_visit (gear_id);
create index paf_pgvst_usrid on paf_page_visit (user_id);

create table comm_gear_add (
	id	varchar2(40)	not null,
	tstamp	date	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index comm_gearaddcom_id on comm_gear_add (community_id);
create index comm_gearaddgearid on comm_gear_add (gear_id);
create index comm_gearaddusr_id on comm_gear_add (user_id);

create table comm_gear_rem (
	id	varchar2(40)	not null,
	tstamp	date	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index comm_gearremcom_id on comm_gear_rem (community_id);
create index comm_gearremgearid on comm_gear_rem (gear_id);
create index comm_gearremusr_id on comm_gear_rem (user_id);

create table page_gear_add (
	id	varchar2(40)	not null,
	tstamp	date	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	page_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index page_gearaddcom_id on page_gear_add (community_id);
create index page_gearaddgearid on page_gear_add (gear_id);
create index page_gearaddpageid on page_gear_add (page_id);
create index page_gearaddusr_id on page_gear_add (user_id);

create table page_gear_rem (
	id	varchar2(40)	not null,
	tstamp	date	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	page_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index page_gearremcom_id on page_gear_rem (community_id);
create index page_gearremgearid on page_gear_rem (gear_id);
create index page_gearrempageid on page_gear_rem (page_id);
create index page_gearremusr_id on page_gear_rem (user_id);



