


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/versionmanager_ddl.xml#2 $
--     These tables are for the version manager  

create table avm_devline (
	id	varchar2(40)	not null,
	type	number(19)	not null,
	name	varchar2(255)	not null,
	parent	varchar2(40)	null,
	date_created	timestamp	null
,constraint avm_devline_pk primary key (id)
,constraint avm_dl_name_unq unique (name));

create index avm_devline_pt_idx on avm_devline (parent);

create table avm_workspace (
	ws_id	varchar2(40)	not null,
	checked_in	number(1)	not null,
	ci_time	timestamp	null,
	userid	varchar2(255)	null,
	locked	number(1)	not null,
	editable	number(1)	not null,
	change_was	clob	null
,constraint avm_workspace_pk primary key (ws_id)
,constraint avm_workspace_fk foreign key (ws_id) references avm_devline (id));


create table avm_asset_lock (
	repository_name	varchar2(255)	not null,
	descriptor_name	varchar2(255)	not null,
	repository_id	varchar2(255)	not null,
	workspace_id	varchar2(40)	not null
,constraint avm_lock_pk primary key (repository_name,descriptor_name,repository_id)
,constraint avm_lock_ws_fk foreign key (workspace_id) references avm_workspace (ws_id));

create index avm_asset_lock_x on avm_asset_lock (workspace_id);



