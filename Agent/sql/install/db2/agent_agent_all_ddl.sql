
-- the source for this section is 
-- agent_audit_ddl.sql





create table agent_audit (
	id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	customer_id	varchar(40)	default null,
	organization_id	varchar(40)	default null,
	session_id	varchar(100)	default null,
	parent_session_id	varchar(100)	default null,
	ip_address	varchar(45)	default null,
	event_date	timestamp	default null,
	version	integer	not null,
	audit_type	integer	default null
,constraint agent_audit_p primary key (id));


create table agent_update_prof (
	id	varchar(40)	not null,
	updated_item_id	varchar(40)	default null
,constraint agent_upd_prof_f foreign key (id) references agent_audit (id));

create index agtupdprof1_ix on agent_update_prof (id);

create table agent_prof_props (
	id	varchar(40)	not null,
	property_name	varchar(255)	default null,
	update_type	integer	not null,
	old_value	varchar(255)	default null,
	new_value	varchar(255)	default null,
	version	integer	not null,
	profile_update_id	varchar(40)	default null
,constraint agentprofupd_p primary key (id));


create table agent_update_org (
	id	varchar(40)	not null,
	updated_item_id	varchar(40)	default null
,constraint agent_upd_org_f foreign key (id) references agent_audit (id));

create index agtupdorg1_ix on agent_update_org (id);

create table agent_org_props (
	id	varchar(40)	not null,
	property_name	varchar(100)	default null,
	update_type	integer	not null,
	old_value	varchar(255)	default null,
	new_value	varchar(255)	default null,
	version	integer	not null,
	org_update_id	varchar(40)	default null
,constraint agentorgupd_p primary key (id));


create table agent_session_end (
	id	varchar(40)	not null,
	session_create	timestamp	not null,
	duration	bigint	not null
,constraint agent_sess_end_f foreign key (id) references agent_audit (id));

create index agtsessendf1_ix on agent_session_end (id);

create table agent_call (
	id	varchar(40)	not null,
	call_id	varchar(40)	not null);

commit;


