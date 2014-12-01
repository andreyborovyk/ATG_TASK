


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/oracle/store_ddl.sql#3 $$Change: 635816 $

create table crs_store_location (
	store_id	varchar2(40)	not null,
	site_id	varchar2(40)	not null,
	store_name	varchar2(250)	not null,
	address_1	varchar2(50)	null,
	address_2	varchar2(50)	null,
	address_3	varchar2(50)	null,
	city	varchar2(40)	null,
	county	varchar2(40)	null,
	state	varchar2(40)	null,
	postal_code	varchar2(10)	null,
	country	varchar2(40)	null,
	phone_number	varchar2(40)	null,
	fax_number	varchar2(40)	null,
	email	varchar2(255)	null
,constraint crs_store_locatn_p primary key (store_id));




