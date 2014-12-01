


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/japanese_catalog_ddl.xml#2 $$Change: 651448 $

create table dbc_catalog_ja (
	catalog_id	varchar(40)	not null,
	display_name	varchar(254)	default null
,constraint dbc_catalog_ja_p primary key (catalog_id));


create table dbc_category_ja (
	category_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	long varchar	default null,
	template_id	varchar(40)	default null
,constraint dbc_category_ja_p primary key (category_id));


create table dbc_product_ja (
	product_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	long varchar	default null,
	admin_display	varchar(254)	default null,
	template_id	varchar(40)	default null
,constraint dbc_product_ja_p primary key (product_id));


create table dbc_sku_ja (
	sku_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	template_id	varchar(40)	default null
,constraint dbc_sku_ja_p primary key (sku_id));


create table dbc_sku_link_ja (
	sku_link_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null
,constraint dbc_sku_link_ja_p primary key (sku_link_id));


create table dbc_config_prop_ja (
	config_prop_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null
,constraint dbc_configpropja_p primary key (config_prop_id));


create table dbc_config_opt_ja (
	config_opt_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	price	double precision	default null
,constraint dbc_config_optja_p primary key (config_opt_id));


create table dbc_cat_key_ja (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dbc_cat_key_ja_p primary key (category_id,sequence_num));


create table dbc_prd_key_ja (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dbc_prd_key_ja_p primary key (product_id,sequence_num));


create table dbc_promotion_ja (
	promotion_id	varchar(40)	not null,
	display_name	varchar(254)	default null
,constraint dbc_promotion_ja_p primary key (promotion_id));

commit;


