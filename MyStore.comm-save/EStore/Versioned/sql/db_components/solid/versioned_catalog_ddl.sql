


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/Versioned/sql/db_components/solid/versioned_catalog_ddl.sql#3 $$Change: 635816 $

create table crs_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	ship_exempt	tinyint	null,
	gift_wrap_el	tinyint	null,
	admin_display	wvarchar(254)	null,
	max_quantity	integer	null
, primary key (sku_id,asset_version)
, check (ship_exempt in (0,1))
, check (gift_wrap_el in (0,1)));

alter table crs_sku set pessimistic;


create table crs_clothing_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	sku_size	wvarchar(254)	null,
	color	wvarchar(254)	null,
	color_swatch	varchar(40)	null
, primary key (sku_id,asset_version));

alter table crs_clothing_sku set pessimistic;


create table crs_furniture_sku (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	color_swatch	varchar(40)	null,
	wood_finish	wvarchar(254)	null
, primary key (sku_id,asset_version));

alter table crs_furniture_sku set pessimistic;


create table crs_promo_content (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	timestamp	null,
	promo_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	marketing_priority	integer	null,
	display_name	wvarchar(254)	null,
	description	wvarchar(1000)	null,
	long_description	long wvarchar	null,
	image_url	varchar(254)	null,
	template_id	varchar(40)	null,
	path	wvarchar(254)	null,
	parent_folder_id	varchar(40)	null,
	category_id	varchar(40)	null,
	product_id	varchar(40)	null,
	site_id	varchar(40)	null,
	link_text	wvarchar(256)	null,
	link_url	varchar(256)	null,
	promotion_id	varchar(40)	null
, primary key (promo_id,asset_version));

create index crs_promo_cont_wsx on crs_promo_content (workspace_id);
create index crs_promo_cont_cix on crs_promo_content (checkin_date);
alter table crs_promo_content set pessimistic;


create table crs_category (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	feature_promo_id	varchar(40)	null,
	title_image_id	varchar(40)	null
, primary key (category_id,asset_version));

alter table crs_category set pessimistic;


create table crs_cat_rel_prod (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	product_id	varchar(40)	not null
, primary key (category_id,sequence_num,asset_version));

alter table crs_cat_rel_prod set pessimistic;


create table crs_prod_seen_in (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	timestamp	null,
	seen_in_id	varchar(40)	not null,
	version	integer	not null,
	source_date	timestamp	null,
	display_name	wvarchar(254)	null,
	description	wvarchar(4000)	null,
	source_name	wvarchar(254)	null
, primary key (seen_in_id,asset_version));

create index crs_prod_seen__wsx on crs_prod_seen_in (workspace_id);
create index crs_prod_seen__cix on crs_prod_seen_in (checkin_date);
alter table crs_prod_seen_in set pessimistic;


create table crs_product (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	is_new	tinyint	null,
	promo_tagline	wvarchar(254)	null,
	brief_description	wvarchar(254)	null,
	title_image_id	varchar(40)	null,
	promo_image_id	varchar(40)	null,
	detail_image_id	varchar(40)	null,
	usage_instructions	wvarchar(4000)	null,
	as_seen_in	varchar(40)	null,
	preorderable	tinyint	null,
	preord_end_date	timestamp	null,
	use_inv_for_preord	tinyint	null,
	email_frnd_enabled	tinyint	null
, primary key (product_id,asset_version)
, check (is_new in (0,1))
, check (preorderable in (0,1))
, check (use_inv_for_preord in (0,1)));

alter table crs_product set pessimistic;


create table crs_discount_promo (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	qualifier	integer	not null
, primary key (promotion_id,asset_version));

alter table crs_discount_promo set pessimistic;


create table crs_feature (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	timestamp	null,
	feature_id	varchar(40)	not null,
	version	integer	not null,
	feature_name	wvarchar(254)	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	display_name	wvarchar(254)	null,
	description	wvarchar(1000)	null,
	long_description	wvarchar(4000)	null,
	title_image_id	varchar(40)	null,
	small_image_id	varchar(40)	null,
	large_image_id	varchar(40)	null,
	hidden	tinyint	null
, primary key (feature_id,asset_version)
, check (hidden in (0,1)));

create index crs_feature_wsx on crs_feature (workspace_id);
create index crs_feature_cix on crs_feature (checkin_date);
alter table crs_feature set pessimistic;


create table crs_prd_features (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	feature_id	varchar(40)	not null
, primary key (product_id,sequence_num,asset_version));

alter table crs_prd_features set pessimistic;


create table crs_prd_tips (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tip_text	wvarchar(2000)	not null
, primary key (product_id,sequence_num,asset_version));

alter table crs_prd_tips set pessimistic;


create table crs_prd_ship_cntry (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
, primary key (product_id,country,asset_version));

alter table crs_prd_ship_cntry set pessimistic;


create table crs_prd_nshp_cntry (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
, primary key (product_id,country,asset_version));

alter table crs_prd_nshp_cntry set pessimistic;


create table crs_catalog (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
, primary key (catalog_id,asset_version));

alter table crs_catalog set pessimistic;

commit work;


