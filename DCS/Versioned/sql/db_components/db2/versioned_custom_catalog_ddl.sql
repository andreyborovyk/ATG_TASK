


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/custom_catalog_ddl.xml#1 $$Change: 651360 $

create table dcs_catalog (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	catalog_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(254)	default null,
	creation_date	timestamp	default null,
	last_mod_date	timestamp	default null,
	migration_status	numeric(3,0)	default null,
	migration_index	numeric(10,0)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_catalog_p primary key (catalog_id,asset_version));

create index dcs_catalog_wsx on dcs_catalog (workspace_id);
create index dcs_catalog_cix on dcs_catalog (checkin_date);

create table dcs_root_cats (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	root_cat_id	varchar(40)	not null
,constraint dcs_root_cats_p primary key (catalog_id,root_cat_id,asset_version,sec_asset_version));

create index dcs_rootcatscat_id on dcs_root_cats (root_cat_id);

create table dcs_allroot_cats (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	root_cat_id	varchar(40)	not null
,constraint dcs_allroot_cats_p primary key (catalog_id,root_cat_id,asset_version));


create table dcs_root_subcats (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	sub_catalog_id	varchar(40)	not null
,constraint dcs_root_subcats_p primary key (catalog_id,sub_catalog_id,asset_version,sec_asset_version));


create table dcs_category_info (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	category_info_id	varchar(40)	not null,
	version	integer	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_category_inf_p primary key (category_info_id,asset_version));

create index dcs_category_i_wsx on dcs_category_info (workspace_id);
create index dcs_category_i_cix on dcs_category_info (checkin_date);

create table dcs_product_info (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	product_info_id	varchar(40)	not null,
	version	integer	not null,
	parent_cat_id	varchar(40)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_product_info_p primary key (product_info_id,asset_version));

create index dcs_product_in_wsx on dcs_product_info (workspace_id);
create index dcs_product_in_cix on dcs_product_info (checkin_date);

create table dcs_sku_info (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	sku_info_id	varchar(40)	not null,
	version	integer	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_sku_info_p primary key (sku_info_id,asset_version));

create index dcs_sku_info_wsx on dcs_sku_info (workspace_id);
create index dcs_sku_info_cix on dcs_sku_info (checkin_date);

create table dcs_cat_subcats (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_cat_subcats_p primary key (category_id,sequence_num,asset_version,sec_asset_version));

create index dcs_catsubcatlogid on dcs_cat_subcats (catalog_id);

create table dcs_cat_subroots (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sub_category_id	varchar(40)	not null
,constraint dcs_cat_subroots_p primary key (category_id,sequence_num,asset_version));


create table dcs_cat_catinfo (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	category_info_id	varchar(40)	not null
,constraint dcs_cat_catinfo_p primary key (category_id,catalog_id,asset_version,sec_asset_version));


create table dcs_catinfo_anc (
	asset_version	numeric(19)	not null,
	category_info_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_catinfo_anc_p primary key (category_info_id,anc_cat_id,asset_version));


create table dcs_prd_prdinfo (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	product_info_id	varchar(40)	not null
,constraint dcs_prd_prdinfo_p primary key (product_id,catalog_id,asset_version,sec_asset_version));


create table dcs_prdinfo_rdprd (
	asset_version	numeric(19)	not null,
	product_info_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_prd_id	varchar(40)	not null
,constraint dcs_prdinfo_rdpr_p primary key (product_info_id,sequence_num,asset_version));


create table dcs_prdinfo_anc (
	asset_version	numeric(19)	not null,
	product_info_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_prdinfo_anc_p primary key (product_info_id,anc_cat_id,asset_version));


create table dcs_sku_skuinfo (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	sku_info_id	varchar(40)	not null
,constraint dcs_sku_skuinfo_p primary key (sku_id,catalog_id,asset_version,sec_asset_version));


create table dcs_skuinfo_rplc (
	asset_version	numeric(19)	not null,
	sku_info_id	varchar(40)	not null,
	sequence_num	integer	not null,
	replacement	varchar(40)	not null
,constraint dcs_skuinfo_rplc_p primary key (sku_info_id,sequence_num,asset_version));


create table dcs_gen_fol_cat (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	folder_id	varchar(40)	not null,
	type	integer	not null,
	name	varchar(40)	not null,
	parent	varchar(40)	default null,
	description	varchar(254)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_gen_fol_cat_p primary key (folder_id,asset_version));

create index dcs_gen_fol_ca_wsx on dcs_gen_fol_cat (workspace_id);
create index dcs_gen_fol_ca_cix on dcs_gen_fol_cat (checkin_date);

create table dcs_child_fol_cat (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	folder_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_folder_id	varchar(40)	not null
,constraint dcs_child_fol_ca_p primary key (folder_id,sequence_num,asset_version,sec_asset_version));


create table dcs_catfol_chld (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	catfol_id	varchar(40)	not null,
	sequence_num	integer	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_catfol_chld_p primary key (catfol_id,sequence_num,asset_version,sec_asset_version));


create table dcs_catfol_sites (
	asset_version	numeric(19)	not null,
	catfol_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_catfl_sites_pk primary key (catfol_id,site_id,asset_version));


create table dcs_dir_anc_ctlgs (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_catalog_id	varchar(40)	not null
,constraint dcs_dirancctlgs_pk primary key (catalog_id,sequence_num,asset_version));


create table dcs_ind_anc_ctlgs (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_catalog_id	varchar(40)	not null
,constraint dcs_indancctlgs_pk primary key (catalog_id,sequence_num,asset_version));


create table dcs_ctlg_anc_cats (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	category_id	varchar(40)	not null
,constraint dcs_ctlganccats_pk primary key (catalog_id,sequence_num,asset_version));


create table dcs_cat_anc_cats (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_category_id	varchar(40)	not null
,constraint dcs_cat_anccats_pk primary key (category_id,sequence_num,asset_version));


create table dcs_cat_prnt_cats (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	parent_ctgy_id	varchar(40)	not null
,constraint dcs_catprntcats_pk primary key (category_id,catalog_id,asset_version));


create table dcs_prd_prnt_cats (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	category_id	varchar(40)	not null
,constraint dcs_prdprntcats_pk primary key (product_id,catalog_id,asset_version));


create table dcs_prd_anc_cats (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	category_id	varchar(40)	not null
,constraint dcs_prdanc_cats_pk primary key (product_id,sequence_num,asset_version));


create table dcs_cat_catalogs (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_cat_catalgs_pk primary key (category_id,catalog_id,asset_version));


create table dcs_prd_catalogs (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_prd_catalgs_pk primary key (product_id,catalog_id,asset_version));


create table dcs_sku_catalogs (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_sku_catalgs_pk primary key (sku_id,catalog_id,asset_version));


create table dcs_catalog_sites (
	asset_version	numeric(19)	not null,
	catalog_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_catlg_sites_pk primary key (catalog_id,site_id,asset_version));

create index catlg_site_id_idx on dcs_catalog_sites (site_id);

create table dcs_category_sites (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_cat_sites_pk primary key (category_id,site_id,asset_version));

create index cat_site_id_idx on dcs_category_sites (site_id);

create table dcs_product_sites (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_prod_sites_pk primary key (product_id,site_id,asset_version));

create index prd_site_id_idx on dcs_product_sites (site_id);

create table dcs_sku_sites (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_sku_sites_pk primary key (sku_id,site_id,asset_version));

create index sku_site_id_idx on dcs_sku_sites (site_id);
commit;


