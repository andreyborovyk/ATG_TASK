


--  @version $Id: //product/DPS/version/10.0.3/templates/DPS/sql/personalization_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements needed to configure your database for personzalization assets.This script will create tables and indexes associated with the user segment list manager.

create table dps_seg_list (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	segment_list_id	varchar(40)	not null,
	display_name	varchar(256)	default null,
	description	varchar(1024)	default null,
	folder_id	varchar(40)	default null
,constraint dps_seg_list_p primary key (segment_list_id,asset_version));

create index dps_seg_list_wsx on dps_seg_list (workspace_id);
create index dps_seg_list_cix on dps_seg_list (checkin_date);

create table dps_seg_list_name (
	asset_version	numeric(19)	not null,
	segment_list_id	varchar(40)	not null,
	seq	integer	not null,
	group_name	varchar(256)	not null
,constraint dps_s_l_n_p primary key (segment_list_id,seq,asset_version));


create table dps_seg_list_folder (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	folder_id	varchar(40)	not null,
	display_name	varchar(256)	default null,
	description	varchar(1024)	default null,
	parent_folder_id	varchar(40)	default null
,constraint dps_s_l_f_p primary key (folder_id,asset_version));

create index dps_seg_list_f_wsx on dps_seg_list_folder (workspace_id);
create index dps_seg_list_f_cix on dps_seg_list_folder (checkin_date);
commit;


