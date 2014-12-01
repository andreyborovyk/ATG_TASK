


--  @version $Id: //app/portal/version/10.0.3/paf/sql/alert_ddl.xml#2 $$Change: 651448 $

create table alt_user (
	id	nvarchar(40)	not null,
	version	integer	default 0 not null,
	target_id	nvarchar(40)	not null,
	target_type	varchar(40)	not null,
	source_id	nvarchar(40)	null,
	msg_type	nvarchar(255)	null,
	message_bean	longblob	null,
	creation_date	datetime	null,
	end_date	datetime	null,
	start_date	datetime	null,
	delete_flag	tinyint	not null
,constraint alt_userpk primary key (id));

create index alt_user_idx on alt_user (target_id,target_type,source_id);

create table alt_group (
	id	nvarchar(40)	not null,
	version	integer	not null,
	target_id	nvarchar(40)	not null,
	target_type	varchar(40)	not null,
	source_id	nvarchar(40)	null,
	msg_type	nvarchar(255)	null,
	message_bean	longblob	null,
	creation_date	datetime	null,
	end_date	datetime	null,
	start_date	datetime	null,
	delete_flag	tinyint	not null
,constraint alt_grouppk primary key (id));

create index alt_group_idx on alt_group (target_id,target_type,source_id);

create table alt_user_alert_rel (
	id	nvarchar(40)	not null,
	alert_id	nvarchar(40)	not null
,constraint alt_useralertrel_p primary key (id,alert_id));


create table alt_user_pref (
	id	nvarchar(40)	not null,
	source_id	nvarchar(40)	not null,
	source_type	nvarchar(40)	not null,
	version	integer	not null,
	event_type	nvarchar(255)	null,
	name	nvarchar(40)	not null,
	value	nvarchar(40)	not null
,constraint alt_user_prefpk primary key (id));


create table alt_userpref_rel (
	id	nvarchar(40)	not null,
	alert_user_pref_id	nvarchar(40)	not null
,constraint alt_user_relpk primary key (id,alert_user_pref_id));

create index alt_userpref_idx on alt_userpref_rel (alert_user_pref_id);

create table alt_gear (
	id	nvarchar(40)	not null,
	source_id	nvarchar(40)	not null,
	source_type	nvarchar(40)	not null,
	version	integer	not null,
	message_type	nvarchar(255)	not null,
	name	nvarchar(40)	not null,
	value	nvarchar(40)	not null,
	resource_bundle	nvarchar(255)	not null
,constraint alt_gearpk primary key (id));


create table alt_gear_rel (
	id	nvarchar(40)	not null,
	name	nvarchar(40)	not null,
	gear_id	nvarchar(40)	not null
,constraint alt_gear_relpk primary key (id,gear_id));

create index alt_gear_idx on alt_gear_rel (gear_id);

create table alt_gear_def (
	id	nvarchar(40)	not null,
	version	integer	not null,
	message_type	nvarchar(255)	not null,
	name	nvarchar(40)	not null,
	value	nvarchar(40)	not null,
	resource_bundle	nvarchar(255)	not null
,constraint alt_gear_defpk primary key (id));


create table alt_gear_def_rel (
	id	nvarchar(40)	not null,
	name	nvarchar(40)	not null,
	gear_def_id	nvarchar(40)	not null
,constraint alt_def_relpk primary key (id,gear_def_id));

create index alt_gear_def_idx on alt_gear_def_rel (gear_def_id);

create table alt_channel (
	channel_id	nvarchar(40)	not null,
	version	integer	not null,
	component_name	nvarchar(255)	not null,
	display_name	nvarchar(255)	not null
,constraint alt_channel_pk primary key (channel_id));


create table alt_chan_usr_rel (
	alt_user_pref_id	nvarchar(40)	not null,
	channel_id	nvarchar(40)	not null
,constraint alt_chnusr_relpk primary key (channel_id,alt_user_pref_id));

commit;


