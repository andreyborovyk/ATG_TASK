


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/file_repository_ddl.xml#2 $
--     These tables are for the storing versioned files for Publishing  

create table epub_file_folder (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	folder_id	varchar(40)	not null,
	acl	varchar(2048)	default null,
	folder_name	varchar(255)	not null,
	parent_folder	varchar(40)	default null
,constraint content_folder_pk primary key (folder_id,asset_version));

create index ff_folder_name_idx on epub_file_folder (folder_name);
create index epub_file_fold_wsx on epub_file_folder (workspace_id);
create index epub_file_fold_cix on epub_file_folder (checkin_date);

create table epub_file_asset (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	file_asset_id	varchar(40)	not null,
	type	numeric(19)	not null,
	acl	varchar(2048)	default null,
	filename	varchar(255)	not null,
	last_modified	timestamp	default null,
	size_bytes	numeric(19)	default null,
	checksum	bigint	default null,
	parent_folder	varchar(40)	default null
,constraint file_asset_pk primary key (file_asset_id,asset_version));

create index fa_folder_name_idx on epub_file_asset (filename);
create index fa_parent_fldr_idx on epub_file_asset (parent_folder);
create index fa_size_idx on epub_file_asset (size_bytes);
create index fa_last_mod_idx on epub_file_asset (last_modified);
create index epub_file_asse_wsx on epub_file_asset (workspace_id);
create index epub_file_asse_cix on epub_file_asset (checkin_date);

create table epub_text_file (
	asset_version	numeric(19)	not null,
	text_file_id	varchar(40)	not null,
	text_content	varchar(20480)	default null
,constraint tf_file_id_pk primary key (text_file_id,asset_version));


create table epub_binary_file (
	asset_version	numeric(19)	not null,
	binary_file_id	varchar(40)	not null,
	binary_content	blob(1048576)	default null
,constraint bf_file_id_pk primary key (binary_file_id,asset_version));

commit;


