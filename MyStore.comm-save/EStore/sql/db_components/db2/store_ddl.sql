


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/db2/store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
	store_id	varchar(40)	not null,
	site_id	varchar(40)	not null,
	store_name	varchar(250)	not null,
	address_1	varchar(50)	default null,
	address_2	varchar(50)	default null,
	address_3	varchar(50)	default null,
	city	varchar(40)	default null,
	county	varchar(40)	default null,
	state	varchar(40)	default null,
	postal_code	varchar(10)	default null,
	country	varchar(40)	default null,
	phone_number	varchar(40)	default null,
	fax_number	varchar(40)	default null,
	email	varchar(255)	default null
,constraint crs_store_locatn_p primary key (store_id));

commit;


