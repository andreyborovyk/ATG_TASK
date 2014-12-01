


--  @version $ $

create table caf_registry (
	registry_id	varchar(40)	not null,
	name	nvarchar(100)	not null,
	description	nvarchar(1000)	null,
	asset_type	nvarchar(100)	null
,constraint caf_registry_p primary key (registry_id));


create table caf_reg_folder (
	folder_id	varchar(40)	not null,
	name	nvarchar(100)	null,
	parent_folder_id	varchar(40)	null
,constraint caf_reg_folder_p primary key (folder_id));


create table caf_reg_rootfolder (
	registry_id	varchar(40)	not null,
	folder_id	varchar(40)	not null
,constraint caf_reg_rootf_p primary key (registry_id,folder_id));


create table caf_reg_asset (
	asset_id	varchar(40)	not null,
	name	nvarchar(100)	null,
	description	nvarchar(1000)	null,
	parent_folder_id	varchar(40)	null,
	type	integer	null,
	parent_registry_id	varchar(40)	null
,constraint caf_reg_asset_p primary key (asset_id));


create table caf_reg_repasset (
	asset_id	varchar(40)	not null,
	repository	nvarchar(100)	not null,
	item_type	nvarchar(100)	not null,
	repository_id	nvarchar(100)	not null
,constraint caf_reg_repasset_p primary key (asset_id));


create table caf_reg_pathasset (
	asset_id	varchar(40)	not null,
	asset_path	nvarchar(1000)	not null
,constraint caf_reg_path_p primary key (asset_id));

commit;


