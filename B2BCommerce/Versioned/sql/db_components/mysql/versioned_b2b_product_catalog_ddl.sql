


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/b2b_product_catalog_ddl.xml#2 $$Change: 651448 $

create table dbc_manufacturer (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	manufacturer_id	varchar(40)	not null,
	manufacturer_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	long_description	longtext charset utf8	null,
	email	varchar(255)	null
,constraint dbc_manufacturer_p primary key (manufacturer_id,asset_version));

create index dbc_man_name_idx on dbc_manufacturer (manufacturer_name);
create index dbc_manufactur_wsx on dbc_manufacturer (workspace_id);
create index dbc_manufactur_cix on dbc_manufacturer (checkin_date);

create table dbc_measurement (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	unit_of_measure	integer	null,
	quantity	numeric(19,7)	null
,constraint dbc_measurement_p primary key (sku_id,asset_version));


create table dbc_product (
	asset_version	bigint	not null,
	product_id	varchar(40)	not null,
	manufacturer	varchar(40)	null
,constraint dbc_product_p primary key (product_id,asset_version));


create table dbc_sku (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	manuf_part_num	nvarchar(254)	null
,constraint dbc_sku_p primary key (sku_id,asset_version));

create index dbc_sku_prtnum_idx on dbc_sku (manuf_part_num);
commit;


