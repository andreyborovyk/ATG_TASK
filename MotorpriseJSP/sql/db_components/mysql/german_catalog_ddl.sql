


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/german_catalog_ddl.xml#2 $$Change: 651448 $

create table dbc_catalog_de (
	catalog_id	varchar(40)	not null,
	display_name	nvarchar(254)	null
,constraint dbc_catalog_de_p primary key (catalog_id));


create table dbc_category_de (
	category_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	long_description	longtext charset utf8	null,
	template_id	varchar(40)	null
,constraint dbc_category_de_p primary key (category_id));


create table dbc_product_de (
	product_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	long_description	longtext charset utf8	null,
	admin_display	nvarchar(254)	null,
	template_id	varchar(40)	null
,constraint dbc_product_de_p primary key (product_id));


create table dbc_sku_de (
	sku_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	template_id	varchar(40)	null
,constraint dbc_sku_de_p primary key (sku_id));


create table dbc_sku_link_de (
	sku_link_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null
,constraint dbc_sku_link_de_p primary key (sku_link_id));


create table dbc_config_prop_de (
	config_prop_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null
,constraint dbc_config_prop__p primary key (config_prop_id));


create table dbc_config_opt_de (
	config_opt_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	price	numeric(19,7)	null
,constraint dbc_config_opt_d_p primary key (config_opt_id));


create table dbc_cat_key_de (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	nvarchar(254)	not null
,constraint dbc_cat_key_de_p primary key (category_id,sequence_num));


create table dbc_prd_key_de (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	nvarchar(254)	not null
,constraint dbc_prd_key_de_p primary key (product_id,sequence_num));


create table dbc_promotion_de (
	promotion_id	varchar(40)	not null,
	display_name	nvarchar(254)	null
,constraint dbc_promotion_de_p primary key (promotion_id));

commit;


