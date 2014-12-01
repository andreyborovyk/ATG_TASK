


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/product_catalog_ddl.xml#2 $$Change: 651448 $

create table dcs_folder (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	folder_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	description	varchar(254)	null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	null
,constraint dcs_folder_p primary key (folder_id,asset_version))

create index fldr_end_dte_idx on dcs_folder (end_date)
create index fldr_path_idx on dcs_folder (path)
create index fldr_strt_dte_idx on dcs_folder (start_date)
create index dcs_folder_wsx on dcs_folder (workspace_id)
create index dcs_folder_cix on dcs_folder (checkin_date)

create table dcs_media (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	media_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	description	varchar(254)	null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	not null,
	media_type	integer	null
,constraint dcs_media_p primary key (media_id,asset_version))

create index med_end_dte_idx on dcs_media (end_date)
create index med_path_idx on dcs_media (path)
create index med_strt_dte_idx on dcs_media (start_date)
create index med_type_idx on dcs_media (media_type)
create index dcs_media_wsx on dcs_media (workspace_id)
create index dcs_media_cix on dcs_media (checkin_date)

create table dcs_media_ext (
	asset_version	numeric(19)	not null,
	media_id	varchar(40)	not null,
	url	varchar(254)	not null
,constraint dcs_media_ext_p primary key (media_id,asset_version))


create table dcs_media_bin (
	asset_version	numeric(19)	not null,
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	datetime	not null,
	data	image	not null
,constraint dcs_media_bin_p primary key (media_id,asset_version))


create table dcs_media_txt (
	asset_version	numeric(19)	not null,
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	datetime	not null,
	data	text	not null
,constraint dcs_media_txt_p primary key (media_id,asset_version))


create table dcs_category (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	category_id	varchar(40)	not null,
	version	integer	not null,
	catalog_id	varchar(40)	null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	text	null,
	parent_cat_id	varchar(40)	null,
	category_type	integer	null,
	root_category	numeric(1,0)	null
,constraint dcs_category_p primary key (category_id,asset_version)
,constraint dcs_category_c check (root_category in (0,1)))

create index cat_end_dte_idx on dcs_category (end_date)
create index cat_pcatid_idx on dcs_category (parent_cat_id)
create index cat_strt_dte_idx on dcs_category (start_date)
create index cat_type_idx on dcs_category (category_type)
create index dcs_category_wsx on dcs_category (workspace_id)
create index dcs_category_cix on dcs_category (checkin_date)

create table dcs_category_acl (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	item_acl	text	null
,constraint dcs_category_acl_p primary key (category_id,asset_version))


create table dcs_product (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	product_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	text	null,
	parent_cat_id	varchar(40)	null,
	product_type	integer	null,
	admin_display	varchar(254)	null,
	nonreturnable	numeric(1,0)	null,
	brand	varchar(254)	null,
	disallow_recommend	numeric(1,0)	null
,constraint dcs_product_p primary key (product_id,asset_version)
,constraint dcs_product_c check (nonreturnable in (0,1))
,constraint dcs_product1_c check (disallow_recommend in (0,1)))

create index prd_end_dte_idx on dcs_product (end_date)
create index prd_pcatid_idx on dcs_product (parent_cat_id)
create index prd_strt_dte_idx on dcs_product (start_date)
create index prd_type_idx on dcs_product (product_type)
create index dcs_product_wsx on dcs_product (workspace_id)
create index dcs_product_cix on dcs_product (checkin_date)

create table dcs_product_acl (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	item_acl	text	null
,constraint dcs_product_acl_p primary key (product_id,asset_version))


create table dcs_sku (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	sku_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	sku_type	integer	null,
	wholesale_price	numeric(19,7)	null,
	list_price	numeric(19,7)	null,
	sale_price	numeric(19,7)	null,
	on_sale	numeric(1,0)	null,
	tax_status	integer	null,
	fulfiller	integer	null,
	item_acl	text	null,
	nonreturnable	numeric(1,0)	null
,constraint dcs_sku_p primary key (sku_id,asset_version)
,constraint dcs_sku_c check (on_sale in (0,1))
,constraint dcs_sku1_c check (nonreturnable in (0,1)))

create index sku_end_dte_idx on dcs_sku (end_date)
create index sku_lstprice_idx on dcs_sku (list_price)
create index sku_sleprice_idx on dcs_sku (sale_price)
create index sku_strt_dte_idx on dcs_sku (start_date)
create index sku_type_idx on dcs_sku (sku_type)
create index dcs_sku_wsx on dcs_sku (workspace_id)
create index dcs_sku_cix on dcs_sku (checkin_date)

create table dcs_cat_groups (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	child_prd_group	varchar(254)	null,
	child_cat_group	varchar(254)	null,
	related_cat_group	varchar(254)	null
,constraint dcs_cat_groups_p primary key (category_id,asset_version))


create table dcs_cat_chldprd (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_prd_id	varchar(40)	not null
,constraint dcs_cat_chldprd_p primary key (category_id,sequence_num,asset_version,sec_asset_version))

create index ct_chldprd_cpi_idx on dcs_cat_chldprd (child_prd_id)
create index ct_chldprd_cid_idx on dcs_cat_chldprd (category_id)

create table dcs_cat_chldcat (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_cat_id	varchar(40)	not null
,constraint dcs_cat_chldcat_p primary key (category_id,sequence_num,asset_version,sec_asset_version))

create index ct_chldcat_cci_idx on dcs_cat_chldcat (child_cat_id)
create index ct_chldcat_cid_idx on dcs_cat_chldcat (category_id)

create table dcs_cat_ancestors (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_cat_ancestor_p primary key (category_id,anc_cat_id,asset_version))

create index dcs_ct_anc_cat_id on dcs_cat_ancestors (anc_cat_id)
create index dcs_ct_cat_id on dcs_cat_ancestors (category_id)

create table dcs_cat_rltdcat (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_cat_id	varchar(40)	not null
,constraint dcs_cat_rltdcat_p primary key (category_id,sequence_num,asset_version))

create index ct_rltdcat_cid_idx on dcs_cat_rltdcat (category_id)

create table dcs_cat_keywrds (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dcs_cat_keywrds_p primary key (category_id,sequence_num,asset_version))

create index cat_keywrds_idx on dcs_cat_keywrds (keyword)
create index ct_keywrds_cid_idx on dcs_cat_keywrds (category_id)

create table dcs_cat_media (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	template_id	varchar(40)	null,
	thumbnail_image_id	varchar(40)	null,
	small_image_id	varchar(40)	null,
	large_image_id	varchar(40)	null
,constraint dcs_cat_media_p primary key (category_id,asset_version))


create table dcs_cat_aux_media (
	asset_version	numeric(19)	not null,
	category_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_cat_aux_medi_p primary key (category_id,tag,asset_version))

create index ct_aux_mdia_ci_idx on dcs_cat_aux_media (category_id)

create table dcs_prd_keywrds (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dcs_prd_keywrds_p primary key (product_id,sequence_num,asset_version))

create index prd_keywrds_idx on dcs_prd_keywrds (keyword)
create index pr_keywrds_pid_idx on dcs_prd_keywrds (product_id)

create table dcs_prd_media (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	template_id	varchar(40)	null,
	thumbnail_image_id	varchar(40)	null,
	small_image_id	varchar(40)	null,
	large_image_id	varchar(40)	null
,constraint dcs_prd_media_p primary key (product_id,asset_version))


create table dcs_prd_aux_media (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_prd_aux_medi_p primary key (product_id,tag,asset_version))

create index pr_aux_mdia_pi_idx on dcs_prd_aux_media (product_id)

create table dcs_prd_chldsku (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sku_id	varchar(40)	not null
,constraint dcs_prd_chldsku_p primary key (product_id,sequence_num,asset_version,sec_asset_version))

create index pr_chldsku_sid_idx on dcs_prd_chldsku (sku_id)
create index pr_chldsku_pid_idx on dcs_prd_chldsku (product_id)

create table dcs_prd_skuattr (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	attribute_name	varchar(40)	not null
,constraint dcs_prd_skuattr_p primary key (product_id,sequence_num,asset_version))

create index pr_skuattr_pid_idx on dcs_prd_skuattr (product_id)

create table dcs_prd_groups (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	related_prd_group	varchar(254)	null,
	upsl_prd_group	varchar(254)	null
,constraint dcs_prd_groups_p primary key (product_id,asset_version))


create table dcs_prd_rltdprd (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_prd_id	varchar(40)	not null
,constraint dcs_prd_rltdprd_p primary key (product_id,sequence_num,asset_version))

create index pr_rltdprd_pid_idx on dcs_prd_rltdprd (product_id)

create table dcs_prd_upslprd (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	upsell_prd_id	varchar(40)	not null
,constraint dcs_prd_upslprd_p primary key (product_id,sequence_num,asset_version))


create table dcs_prd_ancestors (
	asset_version	numeric(19)	not null,
	product_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_prd_ancestor_p primary key (product_id,anc_cat_id,asset_version))

create index dcs_prd_anc_cat_id on dcs_prd_ancestors (anc_cat_id)
create index dcs_prd_prd_id on dcs_prd_ancestors (product_id)

create table dcs_sku_attr (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	attribute_name	varchar(42)	not null,
	attribute_value	varchar(254)	not null
,constraint dcs_sku_attr_p primary key (sku_id,attribute_name,asset_version))

create index sku_attr_skuid_idx on dcs_sku_attr (sku_id)

create table dcs_sku_link (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	sku_link_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	quantity	integer	not null,
	bundle_item	varchar(40)	not null,
	item_acl	text	null
,constraint dcs_sku_link_p primary key (sku_link_id,asset_version))

create index skl_end_dte_idx on dcs_sku_link (end_date)
create index skl_strt_dte_idx on dcs_sku_link (start_date)
create index dcs_sku_link_wsx on dcs_sku_link (workspace_id)
create index dcs_sku_link_cix on dcs_sku_link (checkin_date)

create table dcs_sku_bndllnk (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sku_link_id	varchar(40)	not null
,constraint dcs_sku_bndllnk_p primary key (sku_id,sequence_num,asset_version))

create index sk_bndllnk_sid_idx on dcs_sku_bndllnk (sku_id)

create table dcs_sku_media (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	template_id	varchar(40)	null,
	thumbnail_image_id	varchar(40)	null,
	small_image_id	varchar(40)	null,
	large_image_id	varchar(40)	null
,constraint dcs_sku_media_p primary key (sku_id,asset_version))


create table dcs_sku_aux_media (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_sku_aux_medi_p primary key (sku_id,tag,asset_version))

create index sk_aux_mdia_si_idx on dcs_sku_aux_media (sku_id)

create table dcs_sku_replace (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	sequence_num	integer	not null,
	replacement	varchar(40)	not null
,constraint dcs_sku_replace_p primary key (sku_id,sequence_num,asset_version))

create index sk_replace_sid_idx on dcs_sku_replace (sku_id)

create table dcs_sku_conf (
	asset_version	numeric(19)	not null,
	sku_id	varchar(40)	not null,
	config_props	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_sku_conf_p primary key (sku_id,sequence_num,asset_version))


create table dcs_config_prop (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	config_prop_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(40)	null,
	description	varchar(255)	null,
	item_acl	text	null
,constraint dcs_config_prop_p primary key (config_prop_id,asset_version))

create index dcs_config_pro_wsx on dcs_config_prop (workspace_id)
create index dcs_config_pro_cix on dcs_config_prop (checkin_date)

create table dcs_conf_options (
	asset_version	numeric(19)	not null,
	config_prop_id	varchar(40)	not null,
	config_options	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_conf_options_p primary key (config_prop_id,sequence_num,asset_version))


create table dcs_config_opt (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	config_opt_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(40)	null,
	description	varchar(255)	null,
	sku	varchar(40)	null,
	product	varchar(40)	null,
	price	numeric(19,7)	null,
	item_acl	text	null
,constraint dcs_config_opt_p primary key (config_opt_id,asset_version))

create index ct_conf_prod_idx on dcs_config_opt (product)
create index ct_conf_sku_idx on dcs_config_opt (sku)
create index dcs_config_opt_wsx on dcs_config_opt (workspace_id)
create index dcs_config_opt_cix on dcs_config_opt (checkin_date)

create table dcs_foreign_cat (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	null,
	checkin_date	datetime	null,
	catalog_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar(100)	null,
	description	varchar(255)	null,
	host	varchar(100)	null,
	port	integer	null,
	base_url	varchar(255)	null,
	return_url	varchar(255)	null,
	item_acl	text	null
,constraint dcs_foreign_cat_p primary key (catalog_id,asset_version))

create index dcs_foreign_ca_wsx on dcs_foreign_cat (workspace_id)
create index dcs_foreign_ca_cix on dcs_foreign_cat (checkin_date)


go
