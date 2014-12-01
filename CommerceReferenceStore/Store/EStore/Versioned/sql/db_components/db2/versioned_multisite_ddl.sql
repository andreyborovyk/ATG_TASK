


-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/sql/ddlgen/multisite_ddl.xml#2 $$Change: 651448 $

create table crs_site_attribute (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	resource_bundle	varchar(254)	default null,
	prod_threshold	integer	default null,
	page_size	integer	default null,
	css_file	varchar(254)	default null,
	large_site_icon	varchar(254)	default null,
	default_country_code	varchar(2)	default null,
	emailafriend	numeric(1)	default null,
	backinstock_addr	varchar(50)	default null,
	newpass_addr	varchar(50)	default null,
	orderconfirm_addr	varchar(50)	default null,
	ordershipped_addr	varchar(50)	default null,
	promo_addr	varchar(50)	default null
,constraint crs_site_attr_p primary key (id,asset_version));


create table crs_bill_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_bill_codes_p primary key (id,sequence_num,asset_version));


create table crs_non_bill_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_bill_c_p primary key (id,sequence_num,asset_version));


create table crs_ship_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_ship_codes_p primary key (id,sequence_num,asset_version));


create table crs_non_ship_codes (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	country_codes	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_ship_c_p primary key (id,sequence_num,asset_version));


create table crs_site_languages (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	languages	varchar(40)	not null,
	sequence_num	integer	not null
,constraint crs_site_lang_p primary key (id,sequence_num,asset_version));

commit;


