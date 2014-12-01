


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/solid/versioned_store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	timestamp	null,
	store_id	wvarchar(40)	not null,
	site_id	varchar(40)	not null,
	store_name	wvarchar(250)	not null,
	address_1	wvarchar(50)	null,
	address_2	wvarchar(50)	null,
	address_3	wvarchar(50)	null,
	city	wvarchar(40)	null,
	county	wvarchar(40)	null,
	state	wvarchar(40)	null,
	postal_code	varchar(10)	null,
	country	wvarchar(40)	null,
	phone_number	varchar(40)	null,
	fax_number	varchar(40)	null,
	email	varchar(255)	null
, primary key (store_id,asset_version));

create index crs_store_loca_wsx on crs_store_location (workspace_id);
create index crs_store_loca_cix on crs_store_location (checkin_date);
alter table crs_store_location set pessimistic;

commit work;


