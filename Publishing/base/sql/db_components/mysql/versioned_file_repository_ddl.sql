


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/file_repository_ddl.xml#2 $
--     These tables are for the storing versioned files for Publishing  

create table epub_file_folder (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	folder_id	varchar(40)	not null,
	acl	varchar(2048)	null,
	folder_name	nvarchar(255)	not null,
	parent_folder	varchar(40)	null
,constraint content_folder_pk primary key (folder_id,asset_version));

create index ff_folder_name_idx on epub_file_folder (folder_name);
create index epub_file_fold_wsx on epub_file_folder (workspace_id);
create index epub_file_fold_cix on epub_file_folder (checkin_date);

create table epub_file_asset (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	file_asset_id	varchar(40)	not null,
	type	bigint	not null,
	acl	varchar(2048)	null,
	filename	nvarchar(255)	not null,
	last_modified	datetime	null,
	size_bytes	bigint	null,
	checksum	bigint	null,
	parent_folder	varchar(40)	null
,constraint file_asset_pk primary key (file_asset_id,asset_version));

create index fa_folder_name_idx on epub_file_asset (filename);
create index fa_parent_fldr_idx on epub_file_asset (parent_folder);
create index fa_size_idx on epub_file_asset (size_bytes);
create index fa_last_mod_idx on epub_file_asset (last_modified);
create index epub_file_asse_wsx on epub_file_asset (workspace_id);
create index epub_file_asse_cix on epub_file_asset (checkin_date);

create table epub_text_file (
	asset_version	bigint	not null,
	text_file_id	varchar(40)	not null,
	text_content	longtext	null
,constraint tf_file_id_pk primary key (text_file_id,asset_version));


create table epub_binary_file (
	asset_version	bigint	not null,
	binary_file_id	varchar(40)	not null,
	binary_content	longblob	null
,constraint bf_file_id_pk primary key (binary_file_id,asset_version));

commit;


