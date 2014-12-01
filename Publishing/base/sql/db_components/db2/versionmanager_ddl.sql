


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/versionmanager_ddl.xml#2 $
--     These tables are for the version manager  

create table avm_devline (
	id	varchar(40)	not null,
	type	numeric(19)	not null,
	name	varchar(255)	not null,
	parent	varchar(40)	default null,
	date_created	timestamp	default null
,constraint avm_devline_pk primary key (id)
,constraint avm_dl_name_unq unique (name));

create index avm_devline_pt_idx on avm_devline (parent);

create table avm_workspace (
	ws_id	varchar(40)	not null,
	checked_in	numeric(1)	not null,
	ci_time	timestamp	default null,
	userid	varchar(255)	default null,
	locked	numeric(1)	not null,
	editable	numeric(1)	not null,
	change_was	varchar(4096)	default null
,constraint avm_workspace_pk primary key (ws_id)
,constraint avm_workspace_fk foreign key (ws_id) references avm_devline (id));


create table avm_asset_lock (
	repository_name	varchar(255)	not null,
	descriptor_name	varchar(255)	not null,
	repository_id	varchar(255)	not null,
	workspace_id	varchar(40)	not null
,constraint avm_lock_pk primary key (repository_name,descriptor_name,repository_id)
,constraint avm_lock_ws_fk foreign key (workspace_id) references avm_workspace (ws_id));

create index avm_asset_lock_x on avm_asset_lock (workspace_id);
commit;


