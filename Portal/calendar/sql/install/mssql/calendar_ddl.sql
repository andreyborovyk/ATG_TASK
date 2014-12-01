


--  @version $Id: //app/portal/version/10.0.3/calendar/sql/calendar_ddl.xml#2 $$Change: 651448 $

create table cal_base_event (
	id	varchar(40)	not null,
	name	varchar(60)	not null,
	description	varchar(255)	null,
	ignore_time	tinyint	null,
	gear_id	varchar(40)	null,
	owner	varchar(40)	null,
	item_acl	varchar(1024)	null,
	version	integer	default 0 not null,
	start_date	datetime	null,
	start_time	datetime	not null,
	end_date	datetime	null,
	end_time	datetime	not null,
	local_start_time	datetime	null,
	local_end_time	datetime	null,
	time_zone	varchar(5)	null,
	event_type	integer	not null,
	public_event	tinyint	not null
,constraint cal_base_event_pk primary key (id)
,constraint cal_base_event_c1 check (ignore_time in (0,1))
,constraint cal_base_event_c2 check (public_event in (0,1)))


create table cal_detail_event (
	event_id	varchar(40)	not null,
	address1	varchar(80)	null,
	address2	varchar(80)	null,
	address3	varchar(80)	null,
	city	varchar(80)	null,
	state	varchar(32)	null,
	country	varchar(80)	null,
	postal_code	varchar(10)	null,
	contact_name	varchar(80)	null,
	contact_phone	varchar(80)	null,
	contact_email	varchar(255)	null,
	url	varchar(254)	null
,constraint cal_detail_eventpk primary key (event_id)
,constraint cal_detail_eventfk foreign key (event_id) references cal_base_event (id))


create table cal_event_mapper (
	id	varchar(40)	not null,
	tstamp	datetime	null,
	event_name	varchar(255)	null,
	community_name	varchar(255)	null,
	start_date	varchar(80)	null,
	city	varchar(80)	null,
	state	varchar(32)	null,
	gear_id	varchar(40)	null,
	community_id	varchar(40)	null,
	user_id	varchar(40)	null,
	msg_type	varchar(255)	null)

create index cal_evtmap_comid on cal_event_mapper (community_id)
create index cal_evtmap_gearid on cal_event_mapper (gear_id)
create index cal_evtmap_usrid on cal_event_mapper (user_id)


go
