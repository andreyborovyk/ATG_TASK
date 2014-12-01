


-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/sql/ddlgen/store_ddl.xml#1 $$Change: 651360 $

create table crs_store_location (
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
,constraint crs_store_locatn_p primary key (store_id))



go
