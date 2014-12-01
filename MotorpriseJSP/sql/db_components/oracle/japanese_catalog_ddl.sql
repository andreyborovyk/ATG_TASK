


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/japanese_catalog_ddl.xml#2 $$Change: 651448 $

create table dbc_catalog_ja (
	catalog_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_catalog_ja_p primary key (catalog_id));


create table dbc_category_ja (
	category_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	template_id	varchar2(40)	null
,constraint dbc_category_ja_p primary key (category_id));


create table dbc_product_ja (
	product_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	admin_display	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_product_ja_p primary key (product_id));


create table dbc_sku_ja (
	sku_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_sku_ja_p primary key (sku_id));


create table dbc_sku_link_ja (
	sku_link_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_sku_link_ja_p primary key (sku_link_id));


create table dbc_config_prop_ja (
	config_prop_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_configpropja_p primary key (config_prop_id));


create table dbc_config_opt_ja (
	config_opt_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	price	number(19,7)	null
,constraint dbc_config_optja_p primary key (config_opt_id));


create table dbc_cat_key_ja (
	category_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_cat_key_ja_p primary key (category_id,sequence_num));


create table dbc_prd_key_ja (
	product_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_prd_key_ja_p primary key (product_id,sequence_num));


create table dbc_promotion_ja (
	promotion_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_promotion_ja_p primary key (promotion_id));




