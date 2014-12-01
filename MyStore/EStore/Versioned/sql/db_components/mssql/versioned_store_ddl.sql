


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/mssql/versioned_store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	store_id	varchar(40)	not null,
	site_id	varchar(40)	not null,
	store_name	varchar(250)	not null,
	address_1	varchar(50)	null,
	address_2	varchar(50)	null,
	address_3	varchar(50)	null,
	city	varchar(40)	null,
	county	varchar(40)	null,
	state	varchar(40)	null,
	postal_code	varchar(10)	null,
	country	varchar(40)	null,
	phone_number	varchar(40)	null,
	fax_number	varchar(40)	null,
	email	varchar(255)	null
,constraint crs_store_locatn_p primary key (store_id,asset_version))

create index crs_store_loca_wsx on crs_store_location (workspace_id)
create index crs_store_loca_cix on crs_store_location (checkin_date)


go
