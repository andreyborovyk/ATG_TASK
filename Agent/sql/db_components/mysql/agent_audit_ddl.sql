


--  @version $ $

create table agent_audit (
	id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	customer_id	varchar(40)	null,
	organization_id	varchar(40)	null,
	session_id	varchar(100)	null,
	parent_session_id	varchar(100)	null,
	ip_address	varchar(45)	null,
	event_date	datetime	null,
	version	integer	not null,
	audit_type	integer	null
,constraint agent_audit_p primary key (id));


create table agent_update_prof (
	id	varchar(40)	not null,
	updated_item_id	varchar(40)	null
,constraint agent_upd_prof_f foreign key (id) references agent_audit (id));

create index agtupdprof1_ix on agent_update_prof (id);

create table agent_prof_props (
	id	varchar(40)	not null,
	property_name	varchar(255)	null,
	update_type	integer	not null,
	old_value	varchar(255)	null,
	new_value	varchar(255)	null,
	version	integer	not null,
	profile_update_id	varchar(40)	null
,constraint agentprofupd_p primary key (id));


create table agent_update_org (
	id	varchar(40)	not null,
	updated_item_id	varchar(40)	null
,constraint agent_upd_org_f foreign key (id) references agent_audit (id));

create index agtupdorg1_ix on agent_update_org (id);

create table agent_org_props (
	id	varchar(40)	not null,
	property_name	varchar(100)	null,
	update_type	integer	not null,
	old_value	varchar(255)	null,
	new_value	varchar(255)	null,
	version	integer	not null,
	org_update_id	varchar(40)	null
,constraint agentorgupd_p primary key (id));


create table agent_session_end (
	id	varchar(40)	not null,
	session_create	datetime	not null,
	duration	bigint	not null
,constraint agent_sess_end_f foreign key (id) references agent_audit (id));

create index agtsessendf1_ix on agent_session_end (id);

create table agent_call (
	id	varchar(40)	not null,
	call_id	varchar(40)	not null);

commit;


