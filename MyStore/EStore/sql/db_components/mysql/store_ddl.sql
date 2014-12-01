


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/mysql/store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
	store_id	nchar varchar(40)	not null,
	site_id	varchar(40)	not null,
	store_name	nchar varchar(250)	not null,
	address_1	nchar varchar(50)	null,
	address_2	nchar varchar(50)	null,
	address_3	nchar varchar(50)	null,
	city	nchar varchar(40)	null,
	county	nchar varchar(40)	null,
	state	nchar varchar(40)	null,
	postal_code	varchar(10)	null,
	country	nchar varchar(40)	null,
	phone_number	varchar(40)	null,
	fax_number	varchar(40)	null,
	email	varchar(255)	null
,constraint crs_store_locatn_p primary key (store_id));

commit;


