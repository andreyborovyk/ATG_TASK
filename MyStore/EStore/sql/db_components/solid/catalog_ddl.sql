


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/solid/catalog_ddl.sql#3 $$Change: 635816 $

create table crs_sku (
	sku_id	varchar(40)	not null,
	ship_exempt	tinyint	null,
	gift_wrap_el	tinyint	null,
	admin_display	wvarchar(254)	null,
	max_quantity	integer	null
, primary key (sku_id)
, foreign key (sku_id) references dcs_sku (sku_id)
, check (ship_exempt in (0,1))
, check (gift_wrap_el in (0,1)));

alter table crs_sku set pessimistic;


create table crs_clothing_sku (
	sku_id	varchar(40)	not null,
	sku_size	wvarchar(254)	null,
	color	wvarchar(254)	null,
	color_swatch	varchar(40)	null
, primary key (sku_id)
, foreign key (sku_id) references dcs_sku (sku_id)
, foreign key (color_swatch) references dcs_media (media_id));

create index crs_sku1_x on crs_clothing_sku (color_swatch);
alter table crs_clothing_sku set pessimistic;


create table crs_furniture_sku (
	sku_id	varchar(40)	not null,
	color_swatch	varchar(40)	null,
	wood_finish	wvarchar(254)	null
, primary key (sku_id)
, foreign key (sku_id) references dcs_sku (sku_id)
, foreign key (color_swatch) references dcs_media (media_id));

create index crs_sku2_x on crs_furniture_sku (color_swatch);
alter table crs_furniture_sku set pessimistic;


create table crs_promo_content (
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
, primary key (promo_id)
, foreign key (template_id) references dcs_media (media_id)
, foreign key (parent_folder_id) references dcs_folder (folder_id)
, foreign key (category_id) references dcs_category (category_id)
, foreign key (product_id) references dcs_product (product_id)
, foreign key (promotion_id) references dcs_promotion (promotion_id));

create index crs_promcontent1_x on crs_promo_content (template_id);
create index crs_promcontent2_x on crs_promo_content (parent_folder_id);
create index crs_promcontent3_x on crs_promo_content (category_id);
create index crs_promcontent4_x on crs_promo_content (product_id);
create index crs_promcontent5_x on crs_promo_content (promotion_id);
alter table crs_promo_content set pessimistic;


create table crs_category (
	category_id	varchar(40)	not null,
	feature_promo_id	varchar(40)	null,
	title_image_id	varchar(40)	null
, primary key (category_id)
, foreign key (category_id) references dcs_category (category_id)
, foreign key (feature_promo_id) references crs_promo_content (promo_id)
, foreign key (title_image_id) references dcs_media (media_id));

create index crs_category1_x on crs_category (feature_promo_id);
create index crs_category3_x on crs_category (title_image_id);
alter table crs_category set pessimistic;


create table crs_cat_rel_prod (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	product_id	varchar(40)	not null
, primary key (category_id,sequence_num)
, foreign key (category_id) references dcs_category (category_id)
, foreign key (product_id) references dcs_product (product_id));

create index crs_cat_relprod1_x on crs_cat_rel_prod (product_id);
alter table crs_cat_rel_prod set pessimistic;


create table crs_prod_seen_in (
	seen_in_id	varchar(40)	not null,
	version	integer	not null,
	source_date	timestamp	null,
	display_name	wvarchar(254)	null,
	description	wvarchar(4000)	null,
	source_name	wvarchar(254)	null
, primary key (seen_in_id));

alter table crs_prod_seen_in set pessimistic;


create table crs_product (
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
, primary key (product_id)
, foreign key (product_id) references dcs_product (product_id)
, foreign key (title_image_id) references dcs_media (media_id)
, foreign key (promo_image_id) references dcs_media (media_id)
, foreign key (as_seen_in) references crs_prod_seen_in (seen_in_id)
, foreign key (detail_image_id) references dcs_media (media_id)
, check (is_new in (0,1))
, check (preorderable in (0,1))
, check (use_inv_for_preord in (0,1)));

create index crs_product1_x on crs_product (title_image_id);
create index crs_product2_x on crs_product (promo_image_id);
create index crs_product3_x on crs_product (as_seen_in);
create index crs_product4_x on crs_product (detail_image_id);
alter table crs_product set pessimistic;


create table crs_discount_promo (
	promotion_id	varchar(40)	not null,
	qualifier	integer	not null
, primary key (promotion_id)
, foreign key (promotion_id) references dcs_promotion (promotion_id));

alter table crs_discount_promo set pessimistic;


create table crs_feature (
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
, primary key (feature_id)
, foreign key (large_image_id) references dcs_media (media_id)
, foreign key (small_image_id) references dcs_media (media_id)
, foreign key (title_image_id) references dcs_media (media_id)
, check (hidden in (0,1)));

create index crs_feature1_x on crs_feature (large_image_id);
create index crs_feature2_x on crs_feature (small_image_id);
create index crs_feature3_x on crs_feature (title_image_id);
alter table crs_feature set pessimistic;


create table crs_prd_features (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	feature_id	varchar(40)	not null
, primary key (product_id,sequence_num)
, foreign key (feature_id) references crs_feature (feature_id)
, foreign key (product_id) references dcs_product (product_id));

create index crs_prdfeatures1_x on crs_prd_features (feature_id);
alter table crs_prd_features set pessimistic;


create table crs_prd_tips (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tip_text	wvarchar(2000)	not null
, primary key (product_id,sequence_num)
, foreign key (product_id) references dcs_product (product_id));

alter table crs_prd_tips set pessimistic;


create table crs_prd_ship_cntry (
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
, primary key (product_id,country)
, foreign key (product_id) references dcs_product (product_id));

alter table crs_prd_ship_cntry set pessimistic;


create table crs_prd_nshp_cntry (
	product_id	varchar(40)	not null,
	country	varchar(40)	not null
, primary key (product_id,country)
, foreign key (product_id) references dcs_product (product_id));

alter table crs_prd_nshp_cntry set pessimistic;


create table crs_catalog (
	catalog_id	varchar(40)	not null,
	root_nav_cat	varchar(40)	not null
, primary key (catalog_id)
, foreign key (root_nav_cat) references dcs_category (category_id));

create index crs_ctlrtnavcat1_x on crs_catalog (root_nav_cat);
alter table crs_catalog set pessimistic;

commit work;


