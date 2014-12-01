


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/solid/catalog_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_sku_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	type	integer	null,
	description	varchar(254)	null
, primary key (translation_id));

alter table crs_sku_xlate set optimistic;


create table crs_clothing_xlate (
	translation_id	varchar(40)	not null,
	sku_size	varchar(254)	null,
	color	varchar(254)	null
, primary key (translation_id)
, foreign key (translation_id) references crs_sku_xlate (translation_id));

alter table crs_clothing_xlate set optimistic;


create table crs_furni_xlate (
	translation_id	varchar(40)	not null,
	wood_finish	varchar(254)	null
, primary key (translation_id)
, foreign key (translation_id) references crs_sku_xlate (translation_id));

alter table crs_furni_xlate set optimistic;


create table crs_sku_sku_xlate (
	sku_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (sku_id,locale)
, foreign key (translation_id) references crs_sku_xlate (translation_id));

create index crs_sku_xlt_tr_id on crs_sku_sku_xlate (translation_id);
alter table crs_sku_sku_xlate set optimistic;


create table crs_prd_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	wvarchar(4000)	null,
	brief_description	varchar(254)	null,
	promo_tagline	varchar(254)	null,
	usage_instructions	varchar(4000)	null
, primary key (translation_id));

alter table crs_prd_xlate set optimistic;


create table crs_prd_prd_xlate (
	product_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (product_id,locale)
, foreign key (translation_id) references crs_prd_xlate (translation_id));

create index crs_prd_xlt_tr_id on crs_prd_prd_xlate (translation_id);
alter table crs_prd_prd_xlate set optimistic;


create table crs_prd_xlate_kwr (
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
, primary key (translation_id,sequence_num)
, foreign key (translation_id) references crs_prd_xlate (translation_id));

create index crs_prd_xlt_kwr_tr on crs_prd_xlate_kwr (translation_id);
alter table crs_prd_xlate_kwr set optimistic;


create table crs_prd_xlate_tips (
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tip_text	wvarchar(2000)	not null
, primary key (translation_id,sequence_num)
, foreign key (translation_id) references crs_prd_xlate (translation_id));

create index crs_prd_xlt_tips_t on crs_prd_xlate_tips (translation_id);
alter table crs_prd_xlate_tips set optimistic;


create table crs_cat_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	wvarchar(4000)	null
, primary key (translation_id));

alter table crs_cat_xlate set optimistic;


create table crs_cat_cat_xlate (
	category_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (category_id,locale)
, foreign key (translation_id) references crs_cat_xlate (translation_id));

create index crs_cat_xlt_tr_id on crs_cat_cat_xlate (translation_id);
alter table crs_cat_cat_xlate set optimistic;


create table crs_cat_xlate_kwr (
	translation_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
, primary key (translation_id,sequence_num)
, foreign key (translation_id) references crs_cat_xlate (translation_id));

create index crs_cat_xlt_kwr_tr on crs_cat_xlate_kwr (translation_id);
alter table crs_cat_xlate_kwr set optimistic;


create table crs_fea_xlate (
	translation_id	varchar(40)	not null,
	feature_name	varchar(254)	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	wvarchar(4000)	null
, primary key (translation_id));

alter table crs_fea_xlate set optimistic;


create table crs_fea_fea_xlate (
	feature_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (feature_id,locale)
, foreign key (translation_id) references crs_fea_xlate (translation_id));

create index crs_fea_xlt_tr_id on crs_fea_fea_xlate (translation_id);
alter table crs_fea_fea_xlate set optimistic;


create table crs_asi_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	source_name	wvarchar(254)	null
, primary key (translation_id));

alter table crs_asi_xlate set optimistic;


create table crs_asi_asi_xlate (
	seen_in_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (seen_in_id,locale)
, foreign key (translation_id) references crs_asi_xlate (translation_id));

create index crs_asi_xlt_tr_id on crs_asi_asi_xlate (translation_id);
alter table crs_asi_asi_xlate set optimistic;


create table crs_prmcnt_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	description	varchar(1000)	null,
	long_description	wvarchar(4000)	null,
	link_text	wvarchar(256)	null
, primary key (translation_id));

alter table crs_prmcnt_xlate set optimistic;


create table crs_prmcnt_prmcnt_xlate (
	promo_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
, primary key (promo_id,locale)
, foreign key (translation_id) references crs_prmcnt_xlate (translation_id));

create index crs_prmcnt_xlt_tr_id on crs_prmcnt_prmcnt_xlate (translation_id);
alter table crs_prmcnt_prmcnt_xlate set optimistic;

commit work;


