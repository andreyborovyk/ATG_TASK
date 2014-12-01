


--  @version $ $

create table agent_audit (
	id	varchar2(40)	not null,
	profile_id	varchar2(40)	not null,
	customer_id	varchar2(40)	null,
	organization_id	varchar2(40)	null,
	session_id	varchar2(100)	null,
	parent_session_id	varchar2(100)	null,
	ip_address	varchar2(45)	null,
	event_date	timestamp	null,
	version	number(10)	not null,
	audit_type	number(10)	null
,constraint agent_audit_p primary key (id));


create table agent_update_prof (
	id	varchar2(40)	not null,
	updated_item_id	varchar2(40)	null
,constraint agent_upd_prof_f foreign key (id) references agent_audit (id));

create index agtupdprof1_ix on agent_update_prof (id);

create table agent_prof_props (
	id	varchar2(40)	not null,
	property_name	varchar2(255)	null,
	update_type	number(10)	not null,
	old_value	varchar2(255)	null,
	new_value	varchar2(255)	null,
	version	number(10)	not null,
	profile_update_id	varchar2(40)	null
,constraint agentprofupd_p primary key (id));


create table agent_update_org (
	id	varchar2(40)	not null,
	updated_item_id	varchar2(40)	null
,constraint agent_upd_org_f foreign key (id) references agent_audit (id));

create index agtupdorg1_ix on agent_update_org (id);

create table agent_org_props (
	id	varchar2(40)	not null,
	property_name	varchar2(100)	null,
	update_type	number(10)	not null,
	old_value	varchar2(255)	null,
	new_value	varchar2(255)	null,
	version	number(10)	not null,
	org_update_id	varchar2(40)	null
,constraint agentorgupd_p primary key (id));


create table agent_session_end (
	id	varchar2(40)	not null,
	session_create	timestamp	not null,
	duration	number(19)	not null
,constraint agent_sess_end_f foreign key (id) references agent_audit (id));

create index agtsessendf1_ix on agent_session_end (id);

create table agent_call (
	id	varchar2(40)	not null,
	call_id	varchar2(40)	not null);




