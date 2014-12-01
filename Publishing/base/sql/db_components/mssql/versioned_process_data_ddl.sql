


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/process_data_ddl.xml#2 $
--     These tables are for the ProcessDataRepository  

create table epub_process_data (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	process_data_id	varchar(40)	not null,
	type	numeric(19)	not null
,constraint process_data_pk primary key (process_data_id,asset_version))

create index epub_process_d_wsx on epub_process_data (workspace_id)
create index epub_process_d_cix on epub_process_data (checkin_date)


go
