


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/solid/store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
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
, primary key (store_id));

alter table crs_store_location set optimistic;

commit work;


