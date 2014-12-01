


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/process_data_ddl.xml#2 $
--     These tables are for the ProcessDataRepository  

create table epub_process_data (
	asset_version	number(19)	not null,
	workspace_id	varchar2(40)	not null,
	branch_id	varchar2(40)	not null,
	is_head	number(1)	not null,
	version_deleted	number(1)	not null,
	version_editable	number(1)	not null,
	pred_version	number(19)	null,
	checkin_date	timestamp	null,
	process_data_id	varchar2(40)	not null,
	type	number(19)	not null
,constraint process_data_pk primary key (process_data_id,asset_version));

create index epub_process_d_wsx on epub_process_data (workspace_id);
create index epub_process_d_cix on epub_process_data (checkin_date);



