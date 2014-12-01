


--  @version $Id: //app/portal/version/10.0.3/calendar/sql/calendar_ddl.xml#2 $$Change: 651448 $

create table cal_base_event (
	id	varchar2(40)	not null,
	name	varchar2(60)	not null,
	description	varchar2(255)	null,
	ignore_time	number(1,0)	null,
	gear_id	varchar2(40)	null,
	owner	varchar2(40)	null,
	item_acl	varchar2(1024)	null,
	version	number(10)	default 0 not null,
	start_date	timestamp	null,
	start_time	timestamp	not null,
	end_date	timestamp	null,
	end_time	timestamp	not null,
	local_start_time	timestamp	null,
	local_end_time	timestamp	null,
	time_zone	varchar2(5)	null,
	event_type	number(10)	not null,
	public_event	number(1,0)	not null
,constraint cal_base_event_pk primary key (id)
,constraint cal_base_event_c1 check (ignore_time in (0,1))
,constraint cal_base_event_c2 check (public_event in (0,1)));


create table cal_detail_event (
	event_id	varchar2(40)	not null,
	address1	varchar2(80)	null,
	address2	varchar2(80)	null,
	address3	varchar2(80)	null,
	city	varchar2(80)	null,
	state	varchar2(32)	null,
	country	varchar2(80)	null,
	postal_code	varchar2(10)	null,
	contact_name	varchar2(80)	null,
	contact_phone	varchar2(80)	null,
	contact_email	varchar2(255)	null,
	url	varchar2(254)	null
,constraint cal_detail_eventpk primary key (event_id)
,constraint cal_detail_eventfk foreign key (event_id) references cal_base_event (id));


create table cal_event_mapper (
	id	varchar2(40)	not null,
	tstamp	date	null,
	event_name	varchar2(255)	null,
	community_name	varchar2(255)	null,
	start_date	varchar2(80)	null,
	city	varchar2(80)	null,
	state	varchar2(32)	null,
	gear_id	varchar2(40)	null,
	community_id	varchar2(40)	null,
	user_id	varchar2(40)	null,
	msg_type	varchar2(255)	null);

create index cal_evtmap_comid on cal_event_mapper (community_id);
create index cal_evtmap_gearid on cal_event_mapper (gear_id);
create index cal_evtmap_usrid on cal_event_mapper (user_id);



