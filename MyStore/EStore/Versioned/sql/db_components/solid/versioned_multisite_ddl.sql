


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/solid/versioned_multisite_ddl.sql#3 $$Change: 635816 $

create table crs_site_attribute (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	resource_bundle	varchar(254)	null,
	prod_threshold	integer	null,
	page_size	integer	null,
	css_file	varchar(254)	null,
	large_site_icon	varchar(254)	null,
	default_country_code	varchar(2)	null,
	emailafriend	tinyint	null,
	backinstock_addr	varchar(50)	null,
	newpass_addr	varchar(50)	null,
	orderconfirm_addr	varchar(50)	null,
	ordershipped_addr	varchar(50)	null,
	promo_addr	varchar(50)	null
, primary key (id,asset_version));

alter table crs_site_attribute set pessimistic;


create table crs_bill_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
, primary key (id,sequence_num,asset_version));

alter table crs_bill_codes set pessimistic;


create table crs_non_bill_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
, primary key (id,sequence_num,asset_version));

alter table crs_non_bill_codes set pessimistic;


create table crs_ship_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
, primary key (id,sequence_num,asset_version));

alter table crs_ship_codes set pessimistic;


create table crs_non_ship_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
, primary key (id,sequence_num,asset_version));

alter table crs_non_ship_codes set pessimistic;


create table crs_site_languages (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	languages	varchar(40)	not null,
	sequence_num	integer	not null
, primary key (id,sequence_num,asset_version));

alter table crs_site_languages set pessimistic;

commit work;


