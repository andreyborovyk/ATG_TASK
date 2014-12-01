
-- the source for this section is 
-- commerce_user.sql





create table dps_credit_card (
	id	varchar(40)	not null,
	credit_card_number	varchar(40)	default null,
	credit_card_type	varchar(40)	default null,
	expiration_month	varchar(20)	default null,
	exp_day_of_month	varchar(20)	default null,
	expiration_year	varchar(20)	default null,
	billing_addr	varchar(40)	default null
,constraint dps_credit_card_p primary key (id));

create index dps_crcdba_idx on dps_credit_card (billing_addr);

create table dcs_user (
	user_id	varchar(40)	not null,
	allow_partial_ship	numeric(1,0)	default null,
	default_creditcard	varchar(40)	default null,
	daytime_phone_num	varchar(30)	default null,
	express_checkout	numeric(1,0)	default null,
	default_carrier	varchar(256)	default null,
	price_list	varchar(40)	default null,
	sale_price_list	varchar(40)	default null
,constraint dcs_user_p primary key (user_id)
,constraint dcs_usrdeflt_cr_f foreign key (default_creditcard) references dps_credit_card (id)
,constraint dcs_user1_c check (allow_partial_ship in (0,1))
,constraint dcs_user2_c check (express_checkout in (0,1)));

create index dcs_usrdcc_idx on dcs_user (default_creditcard);

create table dps_usr_creditcard (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	credit_card_id	varchar(40)	not null
,constraint dps_usr_creditca_p primary key (user_id,tag)
,constraint dps_usrccredt_cr_f foreign key (credit_card_id) references dps_credit_card (id)
,constraint dps_usrcusr_d_f foreign key (user_id) references dps_user (id));

create index dps_ucccid_idx on dps_usr_creditcard (credit_card_id);
create index dps_uccuid_idx on dps_usr_creditcard (user_id);
commit;



-- the source for this section is 
-- custom_catalog_user_ddl.sql





create table dcs_user_catalog (
	user_id	varchar(40)	not null,
	user_catalog	varchar(40)	default null
,constraint dcs_usr_catalog_pk primary key (user_id));

commit;



-- the source for this section is 
-- product_catalog_ddl.sql





create table dcs_folder (
	folder_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	description	varchar(254)	default null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	default null
,constraint dcs_folder_p primary key (folder_id)
,constraint dcs_foldparnt_fl_f foreign key (parent_folder_id) references dcs_folder (folder_id));

create index fldr_pfldrid_idx on dcs_folder (parent_folder_id);
create index fldr_end_dte_idx on dcs_folder (end_date);
create index fldr_path_idx on dcs_folder (path);
create index fldr_strt_dte_idx on dcs_folder (start_date);

create table dcs_media (
	media_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	description	varchar(254)	default null,
	name	varchar(254)	not null,
	path	varchar(254)	not null,
	parent_folder_id	varchar(40)	not null,
	media_type	integer	default null
,constraint dcs_media_p primary key (media_id)
,constraint dcs_medparnt_fl_f foreign key (parent_folder_id) references dcs_folder (folder_id));

create index med_pfldrid_idx on dcs_media (parent_folder_id);
create index med_end_dte_idx on dcs_media (end_date);
create index med_path_idx on dcs_media (path);
create index med_strt_dte_idx on dcs_media (start_date);
create index med_type_idx on dcs_media (media_type);

create table dcs_media_ext (
	media_id	varchar(40)	not null,
	url	varchar(254)	not null
,constraint dcs_media_ext_p primary key (media_id)
,constraint dcs_medxtmed_d_f foreign key (media_id) references dcs_media (media_id));


create table dcs_media_bin (
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	timestamp	not null,
	data	blob(1048576)	not null
,constraint dcs_media_bin_p primary key (media_id)
,constraint dcs_medbnmed_d_f foreign key (media_id) references dcs_media (media_id));


create table dcs_media_txt (
	media_id	varchar(40)	not null,
	length	integer	not null,
	last_modified	timestamp	not null,
	data	varchar(20480)	not null
,constraint dcs_media_txt_p primary key (media_id)
,constraint dcs_medtxtmed_d_f foreign key (media_id) references dcs_media (media_id));


create table dcs_category (
	category_id	varchar(40)	not null,
	version	integer	not null,
	catalog_id	varchar(40)	default null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	varchar(20480)	default null,
	parent_cat_id	varchar(40)	default null,
	category_type	integer	default null,
	root_category	numeric(1,0)	default null
,constraint dcs_category_p primary key (category_id)
,constraint dcs_category_c check (root_category in (0,1)));

create index cat_end_dte_idx on dcs_category (end_date);
create index cat_pcatid_idx on dcs_category (parent_cat_id);
create index cat_strt_dte_idx on dcs_category (start_date);
create index cat_type_idx on dcs_category (category_type);

create table dcs_category_acl (
	category_id	varchar(40)	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_category_acl_p primary key (category_id));


create table dcs_product (
	product_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	long_description	varchar(20480)	default null,
	parent_cat_id	varchar(40)	default null,
	product_type	integer	default null,
	admin_display	varchar(254)	default null,
	nonreturnable	numeric(1,0)	default null,
	brand	varchar(254)	default null,
	disallow_recommend	numeric(1,0)	default null
,constraint dcs_product_p primary key (product_id)
,constraint dcs_product_c check (nonreturnable in (0,1))
,constraint dcs_product1_c check (disallow_recommend in (0,1)));

create index prd_end_dte_idx on dcs_product (end_date);
create index prd_pcatid_idx on dcs_product (parent_cat_id);
create index prd_strt_dte_idx on dcs_product (start_date);
create index prd_type_idx on dcs_product (product_type);

create table dcs_product_acl (
	product_id	varchar(40)	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_product_acl_p primary key (product_id));


create table dcs_sku (
	sku_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	sku_type	integer	default null,
	wholesale_price	double precision	default null,
	list_price	double precision	default null,
	sale_price	double precision	default null,
	on_sale	numeric(1,0)	default null,
	tax_status	integer	default null,
	fulfiller	integer	default null,
	item_acl	varchar(1024)	default null,
	nonreturnable	numeric(1,0)	default null
,constraint dcs_sku_p primary key (sku_id)
,constraint dcs_sku_c check (on_sale in (0,1))
,constraint dcs_sku1_c check (nonreturnable in (0,1)));

create index sku_end_dte_idx on dcs_sku (end_date);
create index sku_lstprice_idx on dcs_sku (list_price);
create index sku_sleprice_idx on dcs_sku (sale_price);
create index sku_strt_dte_idx on dcs_sku (start_date);
create index sku_type_idx on dcs_sku (sku_type);

create table dcs_cat_groups (
	category_id	varchar(40)	not null,
	child_prd_group	varchar(254)	default null,
	child_cat_group	varchar(254)	default null,
	related_cat_group	varchar(254)	default null
,constraint dcs_cat_groups_p primary key (category_id)
,constraint dcs_catgcatgry_d_f foreign key (category_id) references dcs_category (category_id));


create table dcs_cat_chldprd (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_prd_id	varchar(40)	not null
,constraint dcs_cat_chldprd_p primary key (category_id,sequence_num)
,constraint dcs_catccatgry_d_f foreign key (category_id) references dcs_category (category_id)
,constraint dcs_catcchild_pr_f foreign key (child_prd_id) references dcs_product (product_id));

create index ct_chldprd_cpi_idx on dcs_cat_chldprd (child_prd_id);
create index ct_chldprd_cid_idx on dcs_cat_chldprd (category_id);

create table dcs_cat_chldcat (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_cat_id	varchar(40)	not null
,constraint dcs_cat_chldcat_p primary key (category_id,sequence_num)
,constraint dcs_catcchild_ct_f foreign key (child_cat_id) references dcs_category (category_id)
,constraint dcs_chlccatgry_d_f foreign key (category_id) references dcs_category (category_id));

create index ct_chldcat_cci_idx on dcs_cat_chldcat (child_cat_id);
create index ct_chldcat_cid_idx on dcs_cat_chldcat (category_id);

create table dcs_cat_ancestors (
	category_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_cat_ancestor_p primary key (category_id,anc_cat_id));

create index dcs_ct_anc_cat_id on dcs_cat_ancestors (anc_cat_id);
create index dcs_ct_cat_id on dcs_cat_ancestors (category_id);

create table dcs_cat_rltdcat (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_cat_id	varchar(40)	not null
,constraint dcs_cat_rltdcat_p primary key (category_id,sequence_num)
,constraint dcs_catrcatgry_d_f foreign key (category_id) references dcs_category (category_id)
,constraint dcs_catrreltd_ct_f foreign key (related_cat_id) references dcs_category (category_id));

create index ct_rltdcat_rci_idx on dcs_cat_rltdcat (related_cat_id);
create index ct_rltdcat_cid_idx on dcs_cat_rltdcat (category_id);

create table dcs_cat_keywrds (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dcs_cat_keywrds_p primary key (category_id,sequence_num)
,constraint dcs_catkcatgry_d_f foreign key (category_id) references dcs_category (category_id));

create index cat_keywrds_idx on dcs_cat_keywrds (keyword);
create index ct_keywrds_cid_idx on dcs_cat_keywrds (category_id);

create table dcs_cat_media (
	category_id	varchar(40)	not null,
	template_id	varchar(40)	default null,
	thumbnail_image_id	varchar(40)	default null,
	small_image_id	varchar(40)	default null,
	large_image_id	varchar(40)	default null
,constraint dcs_cat_media_p primary key (category_id)
,constraint dcs_catmcatgry_d_f foreign key (category_id) references dcs_category (category_id)
,constraint dcs_catmlarg_mgd_f foreign key (large_image_id) references dcs_media (media_id)
,constraint dcs_catmsmall_mg_f foreign key (small_image_id) references dcs_media (media_id)
,constraint dcs_catmtemplt_d_f foreign key (template_id) references dcs_media (media_id)
,constraint dcs_catmthumbnl__f foreign key (thumbnail_image_id) references dcs_media (media_id));

create index ct_mdia_lrimid_idx on dcs_cat_media (large_image_id);
create index ct_mdia_smimid_idx on dcs_cat_media (small_image_id);
create index ct_mdia_tmplid_idx on dcs_cat_media (template_id);
create index ct_mdia_thimid_idx on dcs_cat_media (thumbnail_image_id);

create table dcs_cat_aux_media (
	category_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_cat_aux_medi_p primary key (category_id,tag)
,constraint dcs_catxcatgry_d_f foreign key (category_id) references dcs_category (category_id)
,constraint dcs_catxmdmed_d_f foreign key (media_id) references dcs_media (media_id));

create index ct_aux_mdia_mi_idx on dcs_cat_aux_media (media_id);
create index ct_aux_mdia_ci_idx on dcs_cat_aux_media (category_id);

create table dcs_prd_keywrds (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar(254)	not null
,constraint dcs_prd_keywrds_p primary key (product_id,sequence_num)
,constraint dcs_prdkprodct_d_f foreign key (product_id) references dcs_product (product_id));

create index prd_keywrds_idx on dcs_prd_keywrds (keyword);
create index pr_keywrds_pid_idx on dcs_prd_keywrds (product_id);

create table dcs_prd_media (
	product_id	varchar(40)	not null,
	template_id	varchar(40)	default null,
	thumbnail_image_id	varchar(40)	default null,
	small_image_id	varchar(40)	default null,
	large_image_id	varchar(40)	default null
,constraint dcs_prd_media_p primary key (product_id)
,constraint dcs_prdmlarg_mgd_f foreign key (large_image_id) references dcs_media (media_id)
,constraint dcs_prdmsmall_mg_f foreign key (small_image_id) references dcs_media (media_id)
,constraint dcs_prdmtemplt_d_f foreign key (template_id) references dcs_media (media_id)
,constraint dcs_prdmthumbnl__f foreign key (thumbnail_image_id) references dcs_media (media_id)
,constraint dcs_prdmprodct_d_f foreign key (product_id) references dcs_product (product_id));

create index pr_mdia_lrimid_idx on dcs_prd_media (large_image_id);
create index pr_mdia_smimid_idx on dcs_prd_media (small_image_id);
create index pr_mdia_tmplid_idx on dcs_prd_media (template_id);
create index pr_mdia_thimid_idx on dcs_prd_media (thumbnail_image_id);

create table dcs_prd_aux_media (
	product_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_prd_aux_medi_p primary key (product_id,tag)
,constraint dcs_prdaxmdmed_d_f foreign key (media_id) references dcs_media (media_id)
,constraint dcs_prdaprodct_d_f foreign key (product_id) references dcs_product (product_id));

create index pr_aux_mdia_mi_idx on dcs_prd_aux_media (media_id);
create index pr_aux_mdia_pi_idx on dcs_prd_aux_media (product_id);

create table dcs_prd_chldsku (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sku_id	varchar(40)	not null
,constraint dcs_prd_chldsku_p primary key (product_id,sequence_num)
,constraint dcs_prdcprodct_d_f foreign key (product_id) references dcs_product (product_id)
,constraint dcs_prdcsku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index pr_chldsku_sid_idx on dcs_prd_chldsku (sku_id);
create index pr_chldsku_pid_idx on dcs_prd_chldsku (product_id);

create table dcs_prd_skuattr (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	attribute_name	varchar(40)	not null
,constraint dcs_prd_skuattr_p primary key (product_id,sequence_num)
,constraint dcs_prdsprodct_d_f foreign key (product_id) references dcs_product (product_id));

create index pr_skuattr_pid_idx on dcs_prd_skuattr (product_id);

create table dcs_prd_groups (
	product_id	varchar(40)	not null,
	related_prd_group	varchar(254)	default null,
	upsl_prd_group	varchar(254)	default null
,constraint dcs_prd_groups_p primary key (product_id)
,constraint dcs_prdgprodct_d_f foreign key (product_id) references dcs_product (product_id));


create table dcs_prd_rltdprd (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_prd_id	varchar(40)	not null
,constraint dcs_prd_rltdprd_p primary key (product_id,sequence_num)
,constraint dcs_prdrprodct_d_f foreign key (product_id) references dcs_product (product_id)
,constraint dcs_prdrreltd_pr_f foreign key (related_prd_id) references dcs_product (product_id));

create index pr_rltdprd_rpi_idx on dcs_prd_rltdprd (related_prd_id);
create index pr_rltdprd_pid_idx on dcs_prd_rltdprd (product_id);

create table dcs_prd_upslprd (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	upsell_prd_id	varchar(40)	not null
,constraint dcs_prd_upslprd_p primary key (product_id,sequence_num)
,constraint dcs_prduprodct_d_f foreign key (product_id) references dcs_product (product_id)
,constraint dcs_prdureltd_pr_f foreign key (upsell_prd_id) references dcs_product (product_id));

create index pr_upslprd_upi_idx on dcs_prd_upslprd (upsell_prd_id);

create table dcs_prd_ancestors (
	product_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_prd_ancestor_p primary key (product_id,anc_cat_id));

create index dcs_prd_anc_cat_id on dcs_prd_ancestors (anc_cat_id);
create index dcs_prd_prd_id on dcs_prd_ancestors (product_id);

create table dcs_sku_attr (
	sku_id	varchar(40)	not null,
	attribute_name	varchar(42)	not null,
	attribute_value	varchar(254)	not null
,constraint dcs_sku_attr_p primary key (sku_id,attribute_name)
,constraint dcs_skuttrsku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index sku_attr_skuid_idx on dcs_sku_attr (sku_id);

create table dcs_sku_link (
	sku_link_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	quantity	integer	not null,
	bundle_item	varchar(40)	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_sku_link_p primary key (sku_link_id)
,constraint dcs_skulbundl_tm_f foreign key (bundle_item) references dcs_sku (sku_id));

create index sk_link_bndlid_idx on dcs_sku_link (bundle_item);
create index skl_end_dte_idx on dcs_sku_link (end_date);
create index skl_strt_dte_idx on dcs_sku_link (start_date);

create table dcs_sku_bndllnk (
	sku_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sku_link_id	varchar(40)	not null
,constraint dcs_sku_bndllnk_p primary key (sku_id,sequence_num)
,constraint dcs_skubsku_d_f foreign key (sku_id) references dcs_sku (sku_id)
,constraint dcs_skubsku_lnkd_f foreign key (sku_link_id) references dcs_sku_link (sku_link_id));

create index sk_bndllnk_sli_idx on dcs_sku_bndllnk (sku_link_id);
create index sk_bndllnk_sid_idx on dcs_sku_bndllnk (sku_id);

create table dcs_sku_media (
	sku_id	varchar(40)	not null,
	template_id	varchar(40)	default null,
	thumbnail_image_id	varchar(40)	default null,
	small_image_id	varchar(40)	default null,
	large_image_id	varchar(40)	default null
,constraint dcs_sku_media_p primary key (sku_id)
,constraint dcs_skumlarg_mgd_f foreign key (large_image_id) references dcs_media (media_id)
,constraint dcs_skumsmall_mg_f foreign key (small_image_id) references dcs_media (media_id)
,constraint dcs_skumtemplt_d_f foreign key (template_id) references dcs_media (media_id)
,constraint dcs_skumthumbnl__f foreign key (thumbnail_image_id) references dcs_media (media_id)
,constraint dcs_skumdsku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index sk_mdia_lrimid_idx on dcs_sku_media (large_image_id);
create index sk_mdia_smimid_idx on dcs_sku_media (small_image_id);
create index sk_mdia_tmplid_idx on dcs_sku_media (template_id);
create index sk_mdia_thimid_idx on dcs_sku_media (thumbnail_image_id);

create table dcs_sku_aux_media (
	sku_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_sku_aux_medi_p primary key (sku_id,tag)
,constraint dcs_skuxmdmed_d_f foreign key (media_id) references dcs_media (media_id)
,constraint dcs_skuxmdsku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index sk_aux_mdia_mi_idx on dcs_sku_aux_media (media_id);
create index sk_aux_mdia_si_idx on dcs_sku_aux_media (sku_id);

create table dcs_sku_replace (
	sku_id	varchar(40)	not null,
	sequence_num	integer	not null,
	replacement	varchar(40)	not null
,constraint dcs_sku_replace_p primary key (sku_id,sequence_num)
,constraint dcs_skurplcsku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index sk_replace_sid_idx on dcs_sku_replace (sku_id);

create table dcs_sku_conf (
	sku_id	varchar(40)	not null,
	config_props	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_sku_conf_p primary key (sku_id,sequence_num)
,constraint dcs_skucnfsku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table dcs_config_prop (
	config_prop_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(40)	default null,
	description	varchar(255)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_config_prop_p primary key (config_prop_id));


create table dcs_conf_options (
	config_prop_id	varchar(40)	not null,
	config_options	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_conf_options_p primary key (config_prop_id,sequence_num)
,constraint dcs_confconfg_pr_f foreign key (config_prop_id) references dcs_config_prop (config_prop_id));


create table dcs_config_opt (
	config_opt_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(40)	default null,
	description	varchar(255)	default null,
	sku	varchar(40)	default null,
	product	varchar(40)	default null,
	price	double precision	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_config_opt_p primary key (config_opt_id));

create index ct_conf_prod_idx on dcs_config_opt (product);
create index ct_conf_sku_idx on dcs_config_opt (sku);

create table dcs_foreign_cat (
	catalog_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar(100)	default null,
	description	varchar(255)	default null,
	host	varchar(100)	default null,
	port	integer	default null,
	base_url	varchar(255)	default null,
	return_url	varchar(255)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_foreign_cat_p primary key (catalog_id));

commit;



-- the source for this section is 
-- custom_catalog_ddl.sql





create table dcs_catalog (
	catalog_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(254)	default null,
	creation_date	timestamp	default null,
	last_mod_date	timestamp	default null,
	migration_status	numeric(3,0)	default null,
	migration_index	numeric(10,0)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_catalog_p primary key (catalog_id));


create table dcs_root_cats (
	catalog_id	varchar(40)	not null,
	root_cat_id	varchar(40)	not null
,constraint dcs_root_cats_p primary key (catalog_id,root_cat_id)
,constraint dcs_rotccatlg_d_f foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_rotcrot_ctd_f foreign key (root_cat_id) references dcs_category (category_id));

create index dcs_rootcatscat_id on dcs_root_cats (root_cat_id);

create table dcs_allroot_cats (
	catalog_id	varchar(40)	not null,
	root_cat_id	varchar(40)	not null
,constraint dcs_allroot_cats_p primary key (catalog_id,root_cat_id)
,constraint dcs_allrcatlg_d_f foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_allrrot_ctd_f foreign key (root_cat_id) references dcs_category (category_id));

create index dcs_allrt_cats_id on dcs_allroot_cats (root_cat_id);

create table dcs_root_subcats (
	catalog_id	varchar(40)	not null,
	sub_catalog_id	varchar(40)	not null
,constraint dcs_root_subcats_p primary key (catalog_id,sub_catalog_id)
,constraint dcs_rotscatlg_d_f foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_rotssub_ctlg_f foreign key (sub_catalog_id) references dcs_catalog (catalog_id));

create index dcs_rtsubcats_id on dcs_root_subcats (sub_catalog_id);

create table dcs_category_info (
	category_info_id	varchar(40)	not null,
	version	integer	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_category_inf_p primary key (category_info_id));


create table dcs_product_info (
	product_info_id	varchar(40)	not null,
	version	integer	not null,
	parent_cat_id	varchar(40)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_product_info_p primary key (product_info_id));


create table dcs_sku_info (
	sku_info_id	varchar(40)	not null,
	version	integer	not null,
	item_acl	varchar(1024)	default null
,constraint dcs_sku_info_p primary key (sku_info_id));


create table dcs_cat_subcats (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_cat_subcats_p primary key (category_id,sequence_num)
,constraint dcs_catscatlg_d_f foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_catscatgry_d_f foreign key (category_id) references dcs_category (category_id));

create index dcs_catsubcatlogid on dcs_cat_subcats (catalog_id);

create table dcs_cat_subroots (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	sub_category_id	varchar(40)	not null
,constraint dcs_cat_subroots_p primary key (category_id,sequence_num)
,constraint dcs_subrtscatgry_f foreign key (category_id) references dcs_category (category_id));


create table dcs_cat_catinfo (
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	category_info_id	varchar(40)	not null
,constraint dcs_cat_catinfo_p primary key (category_id,catalog_id)
,constraint dcs_infocatgry_d_f foreign key (category_id) references dcs_category (category_id));


create table dcs_catinfo_anc (
	category_info_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_catinfo_anc_p primary key (category_info_id,anc_cat_id));


create table dcs_prd_prdinfo (
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	product_info_id	varchar(40)	not null
,constraint dcs_prd_prdinfo_p primary key (product_id,catalog_id)
,constraint dcs_prdpprodct_d_f foreign key (product_id) references dcs_product (product_id));


create table dcs_prdinfo_rdprd (
	product_info_id	varchar(40)	not null,
	sequence_num	integer	not null,
	related_prd_id	varchar(40)	not null
,constraint dcs_prdinfo_rdpr_p primary key (product_info_id,sequence_num)
,constraint dcs_prdireltd_pr_f foreign key (related_prd_id) references dcs_product (product_id)
,constraint dcs_prdiprodct_n_f foreign key (product_info_id) references dcs_product_info (product_info_id));

create index dcs_prdrelatedinfo on dcs_prdinfo_rdprd (related_prd_id);

create table dcs_prdinfo_anc (
	product_info_id	varchar(40)	not null,
	anc_cat_id	varchar(40)	not null
,constraint dcs_prdinfo_anc_p primary key (product_info_id,anc_cat_id));


create table dcs_sku_skuinfo (
	sku_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	sku_info_id	varchar(40)	not null
,constraint dcs_sku_skuinfo_p primary key (sku_id,catalog_id)
,constraint dcs_skusknfsku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table dcs_skuinfo_rplc (
	sku_info_id	varchar(40)	not null,
	sequence_num	integer	not null,
	replacement	varchar(40)	not null
,constraint dcs_skuinfo_rplc_p primary key (sku_info_id,sequence_num)
,constraint dcs_skunsku_nfd_f foreign key (sku_info_id) references dcs_sku_info (sku_info_id));


create table dcs_gen_fol_cat (
	folder_id	varchar(40)	not null,
	type	integer	not null,
	name	varchar(40)	not null,
	parent	varchar(40)	default null,
	description	varchar(254)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_gen_fol_cat_p primary key (folder_id));


create table dcs_child_fol_cat (
	folder_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_folder_id	varchar(40)	not null
,constraint dcs_child_fol_ca_p primary key (folder_id,sequence_num)
,constraint dcs_catlfoldr_d_f foreign key (folder_id) references dcs_gen_fol_cat (folder_id));


create table dcs_catfol_chld (
	catfol_id	varchar(40)	not null,
	sequence_num	integer	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_catfol_chld_p primary key (catfol_id,sequence_num)
,constraint dcs_catfcatfl_d_f foreign key (catfol_id) references dcs_gen_fol_cat (folder_id));


create table dcs_catfol_sites (
	catfol_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_catfl_sites_pk primary key (catfol_id,site_id));


create table dcs_dir_anc_ctlgs (
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_catalog_id	varchar(40)	not null
,constraint dcs_dirancctlgs_pk primary key (catalog_id,sequence_num)
,constraint dcs_dirancctlgs_f1 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_dirancctlgs_f2 foreign key (anc_catalog_id) references dcs_catalog (catalog_id));

create index dcs_dirancctlg_idx on dcs_dir_anc_ctlgs (anc_catalog_id);

create table dcs_ind_anc_ctlgs (
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_catalog_id	varchar(40)	not null
,constraint dcs_indancctlgs_pk primary key (catalog_id,sequence_num)
,constraint dcs_indancctlgs_f1 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_indancctlgs_f2 foreign key (anc_catalog_id) references dcs_catalog (catalog_id));

create index dcs_indanctlg_idx on dcs_ind_anc_ctlgs (anc_catalog_id);

create table dcs_ctlg_anc_cats (
	catalog_id	varchar(40)	not null,
	sequence_num	integer	not null,
	category_id	varchar(40)	not null
,constraint dcs_ctlganccats_pk primary key (catalog_id,sequence_num)
,constraint dcs_ctlganccats_f1 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_ctlganccats_f2 foreign key (category_id) references dcs_category (category_id));

create index dcs_ctlgancat_idx on dcs_ctlg_anc_cats (category_id);

create table dcs_cat_anc_cats (
	category_id	varchar(40)	not null,
	sequence_num	integer	not null,
	anc_category_id	varchar(40)	not null
,constraint dcs_cat_anccats_pk primary key (category_id,sequence_num)
,constraint dcs_cat_anccats_f1 foreign key (category_id) references dcs_category (category_id)
,constraint dcs_cat_anccats_f2 foreign key (anc_category_id) references dcs_category (category_id));

create index dcs_catanccat_idx on dcs_cat_anc_cats (anc_category_id);

create table dcs_cat_prnt_cats (
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	parent_ctgy_id	varchar(40)	not null
,constraint dcs_catprntcats_pk primary key (category_id,catalog_id)
,constraint dcscatprntcats_fk1 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcscatprntcats_fk2 foreign key (category_id) references dcs_category (category_id)
,constraint dcscatprntcats_fk3 foreign key (parent_ctgy_id) references dcs_category (category_id));

create index dcscatprntcats_ix1 on dcs_cat_prnt_cats (catalog_id);
create index dcscatprntcats_ix2 on dcs_cat_prnt_cats (category_id);
create index dcscatprntcats_ix3 on dcs_cat_prnt_cats (parent_ctgy_id);

create table dcs_prd_prnt_cats (
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null,
	category_id	varchar(40)	not null
,constraint dcs_prdprntcats_pk primary key (product_id,catalog_id)
,constraint dcs_prdprntcats_f2 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_prdprntcats_f3 foreign key (category_id) references dcs_category (category_id)
,constraint dcs_prdprntcats_f1 foreign key (product_id) references dcs_product (product_id));

create index pr_prnt_cat_pi_idx on dcs_prd_prnt_cats (catalog_id);
create index pr_prnt_cat_ci_idx on dcs_prd_prnt_cats (category_id);

create table dcs_prd_anc_cats (
	product_id	varchar(40)	not null,
	sequence_num	integer	not null,
	category_id	varchar(40)	not null
,constraint dcs_prdanc_cats_pk primary key (product_id,sequence_num)
,constraint dcs_prdanc_cats_f2 foreign key (category_id) references dcs_category (category_id)
,constraint dcs_prdanc_cats_f1 foreign key (product_id) references dcs_product (product_id));

create index dcs_prdanccat_idx on dcs_prd_anc_cats (category_id);

create table dcs_cat_catalogs (
	category_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_cat_catalgs_pk primary key (category_id,catalog_id)
,constraint dcs_cat_catalgs_f2 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_cat_catalgs_f1 foreign key (category_id) references dcs_category (category_id));

create index dcs_catctlgs_idx on dcs_cat_catalogs (catalog_id);

create table dcs_prd_catalogs (
	product_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_prd_catalgs_pk primary key (product_id,catalog_id)
,constraint dcs_prd_catalgs_f2 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_prd_catalgs_f1 foreign key (product_id) references dcs_product (product_id));

create index dcs_prd_ctlgs_idx on dcs_prd_catalogs (catalog_id);

create table dcs_sku_catalogs (
	sku_id	varchar(40)	not null,
	catalog_id	varchar(40)	not null
,constraint dcs_sku_catalgs_pk primary key (sku_id,catalog_id)
,constraint dcs_sku_catalgs_f2 foreign key (catalog_id) references dcs_catalog (catalog_id)
,constraint dcs_sku_catalgs_f1 foreign key (sku_id) references dcs_sku (sku_id));

create index dcs_sku_ctlgs_idx on dcs_sku_catalogs (catalog_id);

create table dcs_catalog_sites (
	catalog_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_catlg_sites_pk primary key (catalog_id,site_id));

create index catlg_site_id_idx on dcs_catalog_sites (site_id);

create table dcs_category_sites (
	category_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_cat_sites_pk primary key (category_id,site_id));

create index cat_site_id_idx on dcs_category_sites (site_id);

create table dcs_product_sites (
	product_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_prod_sites_pk primary key (product_id,site_id)
,constraint dcs_prod_sites_f1 foreign key (product_id) references dcs_product (product_id));

create index prd_site_id_idx on dcs_product_sites (site_id);

create table dcs_sku_sites (
	sku_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_sku_sites_pk primary key (sku_id,site_id)
,constraint dcs_sku_sites_f1 foreign key (sku_id) references dcs_sku (sku_id));

create index sku_site_id_idx on dcs_sku_sites (site_id);
commit;



-- the source for this section is 
-- inventory_ddl.sql





create table dcs_inventory (
	inventory_id	varchar(40)	not null,
	version	integer	not null,
	inventory_lock	varchar(20)	default null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	catalog_ref_id	varchar(40)	not null,
	avail_status	integer	not null,
	availability_date	timestamp	default null,
	stock_level	integer	default null,
	backorder_level	integer	default null,
	preorder_level	integer	default null,
	stock_thresh	integer	default null,
	backorder_thresh	integer	default null,
	preorder_thresh	integer	default null
,constraint dcs_inventory_p primary key (inventory_id)
,constraint inv_catrefid_idx unique (catalog_ref_id));

create index inv_end_dte_idx on dcs_inventory (end_date);
create index inv_strt_dte_idx on dcs_inventory (start_date);
commit;



-- the source for this section is 
-- promotion_ddl.sql




-- Add the DCS_PRM_FOLDER table, promotionFolder

create table dcs_prm_folder (
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	default null
,constraint dcs_prm_folder_p primary key (folder_id)
,constraint dcs_prm_prntfol_f foreign key (parent_folder) references dcs_prm_folder (folder_id));

create index prm_prntfol_idx on dcs_prm_folder (parent_folder);

create table dcs_promotion (
	promotion_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	promotion_type	integer	default null,
	enabled	numeric(1,0)	default null,
	begin_usable	timestamp	default null,
	end_usable	timestamp	default null,
	priority	integer	default null,
	global	numeric(1,0)	default null,
	anon_profile	numeric(1,0)	default null,
	allow_multiple	numeric(1,0)	default null,
	uses	integer	default null,
	rel_expiration	numeric(1,0)	default null,
	time_until_expire	integer	default null,
	template	varchar(254)	default null,
	ui_access_lvl	integer	default null,
	parent_folder	varchar(40)	default null,
	last_modified	timestamp	default null,
	pmdl_version	integer	default null,
	qualifier	varchar(254)	default null
,constraint dcs_promotion_p primary key (promotion_id)
,constraint dcs_prm_fol_f foreign key (parent_folder) references dcs_prm_folder (folder_id)
,constraint dcs_promotion1_c check (enabled in (0,1))
,constraint dcs_promotion2_c check (global in (0,1))
,constraint dcs_promotion3_c check (anon_profile in (0,1))
,constraint dcs_promotion4_c check (allow_multiple in (0,1))
,constraint dcs_promotion5_c check (rel_expiration in (0,1)));

create index promo_folder_idx on dcs_promotion (parent_folder);
create index prmo_bgin_use_idx on dcs_promotion (begin_usable);
create index prmo_end_dte_idx on dcs_promotion (end_date);
create index prmo_end_use_idx on dcs_promotion (end_usable);
create index prmo_strt_dte_idx on dcs_promotion (start_date);

create table dcs_promo_media (
	promotion_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_promo_media_p primary key (promotion_id,tag)
,constraint dcs_prommdmed_d_f foreign key (media_id) references dcs_media (media_id)
,constraint dcs_prompromtn_d_f foreign key (promotion_id) references dcs_promotion (promotion_id));

create index promo_mdia_mid_idx on dcs_promo_media (media_id);
create index promo_mdia_pid_idx on dcs_promo_media (promotion_id);

create table dcs_discount_promo (
	promotion_id	varchar(40)	not null,
	calculator	varchar(254)	default null,
	adjuster	numeric(19,7)	default null,
	pmdl_rule	long varchar	not null,
	one_use	numeric(1,0)	default null
,constraint dcs_discount_pro_p primary key (promotion_id)
,constraint dcs_discpromtn_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_discount_pro_c check (one_use in (0, 1)));


create table dcs_promo_upsell (
	promotion_id	varchar(40)	not null,
	upsell	numeric(1,0)	default null
,constraint dcs_promo_upsell_p primary key (promotion_id)
,constraint dcs_proupsell_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_promo_upsell_c check (upsell in (0, 1)));


create table dcs_upsell_action (
	action_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	upsell_prd_grp	long varchar	default null
,constraint dcs_upsell_actn_p primary key (action_id));


create table dcs_close_qualif (
	close_qualif_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	priority	integer	default null,
	qualifier	long varchar	default null,
	upsell_media	varchar(40)	default null,
	upsell_action	varchar(40)	default null
,constraint dcs_close_qualif_p primary key (close_qualif_id)
,constraint dcs_cq_media_d_f foreign key (upsell_media) references dcs_media (media_id)
,constraint dcs_cq_action_d_f foreign key (upsell_action) references dcs_upsell_action (action_id));

create index dcs_closequalif2_x on dcs_close_qualif (upsell_media);
create index dcs_closequalif1_x on dcs_close_qualif (upsell_action);

create table dcs_prm_cls_qlf (
	promotion_id	varchar(40)	not null,
	closeness_qualif	varchar(40)	not null
,constraint dcs_promo_cq_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_prmclsqlf_d_f foreign key (closeness_qualif) references dcs_close_qualif (close_qualif_id));

create index dcs_prm_cls_qlf2_x on dcs_prm_cls_qlf (promotion_id);
create index dcs_prm_cls_qlf1_x on dcs_prm_cls_qlf (closeness_qualif);

create table dcs_prm_sites (
	promotion_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint dcs_prm_sites1_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_prm_sites2_d_f foreign key (site_id) references site_configuration (id));

create index dcs_prm_sites1_x on dcs_prm_sites (promotion_id);
create index dcs_prm_sites2_x on dcs_prm_sites (site_id);

create table dcs_prm_site_grps (
	promotion_id	varchar(40)	not null,
	site_group_id	varchar(40)	not null
,constraint dcs_prm_sgrps1_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_prm_sgrps2_d_f foreign key (site_group_id) references site_group (id));

create index dcs_prm_sgrps1_x on dcs_prm_site_grps (promotion_id);
create index dcs_prm_sgrps2_x on dcs_prm_site_grps (site_group_id);

create table dcs_prm_tpl_vals (
	promotion_id	varchar(40)	not null,
	placeholder	varchar(40)	not null,
	placeholderValue	long varchar	default null
,constraint dcs_prm_tpl_vals_p primary key (promotion_id,placeholder)
,constraint dcs_prmtplvals_d_f foreign key (promotion_id) references dcs_promotion (promotion_id));


create table dcs_prm_cls_vals (
	close_qualif_id	varchar(40)	not null,
	placeholder	varchar(40)	not null,
	placeholderValue	long varchar	default null
,constraint dcs_prm_cls_vals_p primary key (close_qualif_id,placeholder)
,constraint dcs_prmclsvals_d_f foreign key (close_qualif_id) references dcs_close_qualif (close_qualif_id));


create table dcs_upsell_prods (
	action_id	varchar(40)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_upsell_prods_p primary key (action_id,product_id)
,constraint dcs_ups_prod_d_f foreign key (product_id) references dcs_product (product_id));

create index dcs_upsellprods1_x on dcs_upsell_prods (product_id);
commit;



-- the source for this section is 
-- user_promotion_ddl.sql




-- The promotion line was commented out to allow the profile and promotion tables to be delinked. The promotion tables are intended to be used as a "read-only" table on the production servers. The promotion (and product catalog) tables are promoted and made active on the production system through copy-switch. In doing so, the profile tables and the promotion tables cannot be in the same database, and therefore we must remove this referece. However if you are not going to use copy-switch for the promotions, then you can add this reference back in.     promotion			VARCHAR(40)		NOT NULL	REFERENCES dcs_promotion(promotion_id),

create table dcs_usr_promostat (
	status_id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	promotion	varchar(40)	not null,
	num_uses	integer	default null,
	expirationDate	timestamp	default null,
	granted_date	timestamp	default null
,constraint dcs_usr_promosta_p primary key (status_id));

create index promostat_prof_idx on dcs_usr_promostat (profile_id);
create index usr_prmstat_pr_idx on dcs_usr_promostat (promotion);

create table dcs_usr_actvpromo (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	promo_status_id	varchar(40)	not null
,constraint dcs_usr_actvprom_p primary key (id,sequence_num));

create index usr_actvprm_id_idx on dcs_usr_actvpromo (id);
-- The promotion_id column was commented out to allow the profile and promotion tables to be delinked. The promotion tables are intended to be used as a "read-only" table on the production servers. The promotion (and product catalog) tables are promoted and made active on the production system through copy-switch. In doing so, the profile tables and the promotion tables cannot be in the same database, and therefore we must remove this referece. However if you are not going to use copy-switch for the promotions, then you can add this reference back in.        promotion_id                    VARCHAR(40)             NOT NULL        REFERENCES dcs_promotion(promotion_id),

create table dcs_usr_usedpromo (
	id	varchar(40)	not null,
	promotion_id	varchar(40)	not null
,constraint dcs_usr_usedprom_p primary key (id,promotion_id));

create index usr_usedprm_id_idx on dcs_usr_usedpromo (id);
create index usr_usedprm_pi_idx on dcs_usr_usedpromo (promotion_id);
commit;



-- the source for this section is 
-- user_giftlist_ddl.sql





create table dcs_giftlist (
	id	varchar(40)	not null,
	owner_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	is_public	numeric(1,0)	not null,
	is_published	numeric(1,0)	not null,
	event_name	varchar(64)	default null,
	event_type	integer	default null,
	event_date	timestamp	default null,
	comments	varchar(254)	default null,
	description	varchar(254)	default null,
	instructions	varchar(254)	default null,
	creation_date	timestamp	default null,
	last_modified_date	timestamp	default null,
	shipping_addr_id	varchar(40)	default null
,constraint dcs_giftlist_p primary key (id)
,constraint dcs_giftlist1_c check (is_public in (0,1))
,constraint dcs_giftlist2_c check (is_published in (0,1)));

create index gftlst_shpadid_idx on dcs_giftlist (shipping_addr_id);
create index gft_evnt_name_idx on dcs_giftlist (event_name);
create index gft_evnt_type_idx on dcs_giftlist (event_type);
create index gft_owner_id_idx on dcs_giftlist (owner_id);
create index gft_site_id_idx on dcs_giftlist (site_id);

create table dcs_giftinst (
	giftlist_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	default null
,constraint dcs_giftinst_p primary key (giftlist_id,tag)
,constraint dcs_giftgiftlst__f foreign key (giftlist_id) references dcs_giftlist (id));

create index giftinst_gflid_idx on dcs_giftinst (giftlist_id);

create table dcs_giftitem (
	id	varchar(40)	not null,
	catalog_ref_id	varchar(40)	default null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	quantity_desired	integer	default null,
	quantity_purchased	integer	default null
,constraint dcs_giftitem_p primary key (id));

create index giftitem_cat_idx on dcs_giftitem (catalog_ref_id);
create index giftitem_prod_idx on dcs_giftitem (product_id);
create index giftitem_site_idx on dcs_giftitem (site_id);

create table dcs_giftlist_item (
	giftlist_id	varchar(40)	not null,
	sequence_num	integer	not null,
	giftitem_id	varchar(40)	default null
,constraint dcs_giftlist_ite_p primary key (giftlist_id,sequence_num)
,constraint dcs_giftgifttm_d_f foreign key (giftitem_id) references dcs_giftitem (id)
,constraint dcs_gftlstgftlst_f foreign key (giftlist_id) references dcs_giftlist (id));

create index gftlst_itm_gii_idx on dcs_giftlist_item (giftitem_id);
create index gftlst_itm_gli_idx on dcs_giftlist_item (giftlist_id);

create table dcs_user_wishlist (
	user_id	varchar(40)	not null,
	giftlist_id	varchar(40)	default null
,constraint dcs_user_wishlis_p primary key (user_id)
,constraint dcs_usrwgiftlst__f foreign key (giftlist_id) references dcs_giftlist (id));

create index usr_wshlst_gli_idx on dcs_user_wishlist (giftlist_id);

create table dcs_user_giftlist (
	user_id	varchar(40)	not null,
	sequence_num	integer	not null,
	giftlist_id	varchar(40)	default null
,constraint dcs_user_giftlis_p primary key (user_id,sequence_num));

create index usr_gftlst_uid_idx on dcs_user_giftlist (user_id);

create table dcs_user_otherlist (
	user_id	varchar(40)	not null,
	sequence_num	integer	not null,
	giftlist_id	varchar(40)	default null
,constraint dcs_user_otherli_p primary key (user_id,sequence_num)
,constraint dcs_usrtgiftlst__f foreign key (giftlist_id) references dcs_giftlist (id));

create index usr_otrlst_gli_idx on dcs_user_otherlist (giftlist_id);
commit;



-- the source for this section is 
-- order_ddl.sql





create table dcspp_order (
	order_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	order_class_type	varchar(40)	default null,
	profile_id	varchar(40)	default null,
	description	varchar(64)	default null,
	state	varchar(40)	default null,
	state_detail	varchar(254)	default null,
	created_by_order	varchar(40)	default null,
	origin_of_order	integer	default null,
	creation_date	timestamp	default null,
	submitted_date	timestamp	default null,
	last_modified_date	timestamp	default null,
	completed_date	timestamp	default null,
	price_info	varchar(40)	default null,
	tax_price_info	varchar(40)	default null,
	explicitly_saved	numeric(1,0)	default null,
	agent_id	varchar(40)	default null,
	sales_channel	integer	default null,
	creation_site_id	varchar(40)	default null,
	site_id	varchar(40)	default null
,constraint dcspp_order_p primary key (order_id)
,constraint dcspp_order_c check (explicitly_saved IN (0,1)));

create index order_lastmod_idx on dcspp_order (last_modified_date);
create index order_profile_idx on dcspp_order (profile_id);
create index order_submit_idx on dcspp_order (submitted_date);
create index ord_creat_site_idx on dcspp_order (creation_site_id);
create index ord_site_idx on dcspp_order (site_id);

create table dcspp_ship_group (
	shipping_group_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	shipgrp_class_type	varchar(40)	default null,
	shipping_method	varchar(40)	default null,
	description	varchar(64)	default null,
	ship_on_date	timestamp	default null,
	actual_ship_date	timestamp	default null,
	state	varchar(40)	default null,
	state_detail	varchar(254)	default null,
	submitted_date	timestamp	default null,
	price_info	varchar(40)	default null,
	order_ref	varchar(40)	default null
,constraint dcspp_ship_group_p primary key (shipping_group_id));


create table dcspp_pay_group (
	payment_group_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	paygrp_class_type	varchar(40)	default null,
	payment_method	varchar(40)	default null,
	amount	numeric(19,7)	default null,
	amount_authorized	numeric(19,7)	default null,
	amount_debited	numeric(19,7)	default null,
	amount_credited	numeric(19,7)	default null,
	currency_code	varchar(10)	default null,
	state	varchar(40)	default null,
	state_detail	varchar(254)	default null,
	submitted_date	timestamp	default null,
	order_ref	varchar(40)	default null
,constraint dcspp_pay_group_p primary key (payment_group_id));


create table dcspp_item (
	commerce_item_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	item_class_type	varchar(40)	default null,
	catalog_id	varchar(40)	default null,
	catalog_ref_id	varchar(40)	default null,
	catalog_key	varchar(40)	default null,
	product_id	varchar(40)	default null,
	site_id	varchar(40)	default null,
	quantity	numeric(19,0)	default null,
	state	varchar(40)	default null,
	state_detail	varchar(254)	default null,
	price_info	varchar(40)	default null,
	order_ref	varchar(40)	default null
,constraint dcspp_item_p primary key (commerce_item_id));

create index item_catref_idx on dcspp_item (catalog_ref_id);
create index item_prodref_idx on dcspp_item (product_id);
create index item_site_idx on dcspp_item (site_id);

create table dcspp_relationship (
	relationship_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	rel_class_type	varchar(40)	default null,
	relationship_type	varchar(40)	default null,
	order_ref	varchar(40)	default null
,constraint dcspp_relationsh_p primary key (relationship_id));


create table dcspp_rel_orders (
	order_id	varchar(40)	not null,
	related_orders	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_rel_orders_p primary key (order_id,sequence_num)
,constraint dcspp_reordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dcspp_order_inst (
	order_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	default null
,constraint dcspp_order_inst_p primary key (order_id,tag)
,constraint dcspp_orordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_inst_oid_idx on dcspp_order_inst (order_id);

create table dcspp_order_sg (
	order_id	varchar(40)	not null,
	shipping_groups	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_sg_p primary key (order_id,sequence_num)
,constraint dcspp_sgordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_sg_ordid_idx on dcspp_order_sg (order_id);

create table dcspp_order_pg (
	order_id	varchar(40)	not null,
	payment_groups	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_pg_p primary key (order_id,sequence_num)
,constraint dcspp_orpgordr_f foreign key (order_id) references dcspp_order (order_id));

create index order_pg_ordid_idx on dcspp_order_pg (order_id);

create table dcspp_order_item (
	order_id	varchar(40)	not null,
	commerce_items	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_item_p primary key (order_id,sequence_num)
,constraint dcspp_oritordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_item_oid_idx on dcspp_order_item (order_id);
create index order_item_cit_idx on dcspp_order_item (commerce_items);

create table dcspp_order_rel (
	order_id	varchar(40)	not null,
	relationships	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_rel_p primary key (order_id,sequence_num)
,constraint dcspp_orlordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_rel_orid_idx on dcspp_order_rel (order_id);

create table dcspp_ship_inst (
	shipping_group_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	default null
,constraint dcspp_ship_inst_p primary key (shipping_group_id,tag)
,constraint dcspp_shshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create index ship_inst_sgid_idx on dcspp_ship_inst (shipping_group_id);

create table dcspp_hrd_ship_grp (
	shipping_group_id	varchar(40)	not null,
	tracking_number	varchar(40)	default null
,constraint dcspp_hrd_ship_g_p primary key (shipping_group_id)
,constraint dcspp_hrshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));


create table dcspp_ele_ship_grp (
	shipping_group_id	varchar(40)	not null,
	email_address	varchar(255)	default null
,constraint dcspp_ele_ship_g_p primary key (shipping_group_id)
,constraint dcspp_elshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));


create table dcspp_ship_addr (
	shipping_group_id	varchar(40)	not null,
	prefix	varchar(40)	default null,
	first_name	varchar(40)	default null,
	middle_name	varchar(40)	default null,
	last_name	varchar(40)	default null,
	suffix	varchar(40)	default null,
	job_title	varchar(40)	default null,
	company_name	varchar(40)	default null,
	address_1	varchar(50)	default null,
	address_2	varchar(50)	default null,
	address_3	varchar(50)	default null,
	city	varchar(40)	default null,
	county	varchar(40)	default null,
	state	varchar(40)	default null,
	postal_code	varchar(10)	default null,
	country	varchar(40)	default null,
	phone_number	varchar(40)	default null,
	fax_number	varchar(40)	default null,
	email	varchar(255)	default null
,constraint dcspp_ship_addr_p primary key (shipping_group_id)
,constraint dcspp_shdshippng_f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));


create table dcspp_hand_inst (
	handling_inst_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	hndinst_class_type	varchar(40)	default null,
	handling_method	varchar(40)	default null,
	shipping_group_id	varchar(40)	default null,
	commerce_item_id	varchar(40)	default null,
	quantity	integer	default null
,constraint dcspp_hand_inst_p primary key (handling_inst_id));

create index hi_item_idx on dcspp_hand_inst (commerce_item_id);
create index hi_ship_group_idx on dcspp_hand_inst (shipping_group_id);

create table dcspp_gift_inst (
	handling_inst_id	varchar(40)	not null,
	giftlist_id	varchar(40)	default null,
	giftlist_item_id	varchar(40)	default null
,constraint dcspp_gift_inst_p primary key (handling_inst_id)
,constraint dcspp_gihandlng__f foreign key (handling_inst_id) references dcspp_hand_inst (handling_inst_id));


create table dcspp_sg_hand_inst (
	shipping_group_id	varchar(40)	not null,
	handling_instrs	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_sg_hand_in_p primary key (shipping_group_id,sequence_num)
,constraint dcspp_sgshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

create index sg_hnd_ins_sgi_idx on dcspp_sg_hand_inst (shipping_group_id);

create table dcspp_pay_inst (
	payment_group_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	default null
,constraint dcspp_pay_inst_p primary key (payment_group_id,tag)
,constraint dcspp_papaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index pay_inst_pgrid_idx on dcspp_pay_inst (payment_group_id);

create table dcspp_config_item (
	config_item_id	varchar(40)	not null,
	reconfig_data	varchar(255)	default null,
	notes	varchar(255)	default null
,constraint dcspp_config_ite_p primary key (config_item_id)
,constraint dcspp_coconfg_tm_f foreign key (config_item_id) references dcspp_item (commerce_item_id));


create table dcspp_subsku_item (
	subsku_item_id	varchar(40)	not null,
	ind_quantity	integer	default null
,constraint dcspp_subsku_ite_p primary key (subsku_item_id)
,constraint dcspp_susubsk_tm_f foreign key (subsku_item_id) references dcspp_item (commerce_item_id));


create table dcspp_item_ci (
	commerce_item_id	varchar(40)	not null,
	commerce_items	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_item_ci_p primary key (commerce_item_id,sequence_num)
,constraint dcspp_itcommrc_t_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id));


create table dcspp_gift_cert (
	payment_group_id	varchar(40)	not null,
	profile_id	varchar(40)	default null,
	gift_cert_number	varchar(50)	default null
,constraint dcspp_gift_cert_p primary key (payment_group_id)
,constraint dcspp_gipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index gc_number_idx on dcspp_gift_cert (gift_cert_number);
create index gc_profile_idx on dcspp_gift_cert (profile_id);

create table dcspp_store_cred (
	payment_group_id	varchar(40)	not null,
	profile_id	varchar(40)	default null,
	store_cred_number	varchar(50)	default null
,constraint dcspp_store_cred_p primary key (payment_group_id)
,constraint dcspp_stpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index sc_number_idx on dcspp_store_cred (store_cred_number);
create index sc_profile_idx on dcspp_store_cred (profile_id);

create table dcspp_credit_card (
	payment_group_id	varchar(40)	not null,
	credit_card_number	varchar(40)	default null,
	credit_card_type	varchar(40)	default null,
	expiration_month	varchar(20)	default null,
	exp_day_of_month	varchar(20)	default null,
	expiration_year	varchar(20)	default null
,constraint dcspp_credit_car_p primary key (payment_group_id)
,constraint dcspp_crpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));


create table dcspp_bill_addr (
	payment_group_id	varchar(40)	not null,
	prefix	varchar(40)	default null,
	first_name	varchar(40)	default null,
	middle_name	varchar(40)	default null,
	last_name	varchar(40)	default null,
	suffix	varchar(40)	default null,
	job_title	varchar(40)	default null,
	company_name	varchar(40)	default null,
	address_1	varchar(50)	default null,
	address_2	varchar(50)	default null,
	address_3	varchar(50)	default null,
	city	varchar(40)	default null,
	county	varchar(40)	default null,
	state	varchar(40)	default null,
	postal_code	varchar(10)	default null,
	country	varchar(40)	default null,
	phone_number	varchar(40)	default null,
	fax_number	varchar(40)	default null,
	email	varchar(255)	default null
,constraint dcspp_bill_addr_p primary key (payment_group_id)
,constraint dcspp_bipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));


create table dcspp_pay_status (
	status_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	trans_id	varchar(50)	default null,
	amount	numeric(19,7)	default null,
	trans_success	numeric(1,0)	default null,
	error_message	varchar(254)	default null,
	trans_timestamp	timestamp	default null
,constraint dcspp_pay_status_p primary key (status_id)
,constraint dcspp_pay_status_c check (trans_success IN (0,1)));


create table dcspp_cc_status (
	status_id	varchar(40)	not null,
	auth_expiration	timestamp	default null,
	avs_code	varchar(40)	default null,
	avs_desc_result	varchar(254)	default null,
	integration_data	varchar(256)	default null
,constraint dcspp_cc_status_p primary key (status_id)
,constraint dcspp_ccstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));


create table dcspp_gc_status (
	status_id	varchar(40)	not null,
	auth_expiration	timestamp	default null
,constraint dcspp_gc_status_p primary key (status_id)
,constraint dcspp_gcstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));


create table dcspp_sc_status (
	status_id	varchar(40)	not null,
	auth_expiration	timestamp	default null
,constraint dcspp_sc_status_p primary key (status_id)
,constraint dcspp_scstats_d_f foreign key (status_id) references dcspp_pay_status (status_id));


create table dcspp_auth_status (
	payment_group_id	varchar(40)	not null,
	auth_status	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_auth_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_atpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index auth_stat_pgid_idx on dcspp_auth_status (payment_group_id);

create table dcspp_debit_status (
	payment_group_id	varchar(40)	not null,
	debit_status	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_debit_stat_p primary key (payment_group_id,sequence_num)
,constraint dcspp_depaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index debit_stat_pgi_idx on dcspp_debit_status (payment_group_id);

create table dcspp_cred_status (
	payment_group_id	varchar(40)	not null,
	credit_status	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_cred_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_crpaymntgr_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index cred_stat_pgid_idx on dcspp_cred_status (payment_group_id);

create table dcspp_shipitem_rel (
	relationship_id	varchar(40)	not null,
	shipping_group_id	varchar(40)	default null,
	commerce_item_id	varchar(40)	default null,
	quantity	numeric(19,0)	default null,
	returned_qty	numeric(19,0)	default null,
	amount	numeric(19,7)	default null,
	state	varchar(40)	default null,
	state_detail	varchar(254)	default null
,constraint dcspp_shipitem_r_p primary key (relationship_id)
,constraint dcspp_shreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index sirel_item_idx on dcspp_shipitem_rel (commerce_item_id);
create index sirel_shipgrp_idx on dcspp_shipitem_rel (shipping_group_id);

create table dcspp_rel_range (
	relationship_id	varchar(40)	not null,
	low_bound	integer	default null,
	high_bound	integer	default null
,constraint dcspp_rel_range_p primary key (relationship_id));


create table dcspp_payitem_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	default null,
	commerce_item_id	varchar(40)	default null,
	quantity	numeric(19,0)	default null,
	amount	numeric(19,7)	default null
,constraint dcspp_payitem_re_p primary key (relationship_id)
,constraint dcspp_pareltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index pirel_item_idx on dcspp_payitem_rel (commerce_item_id);
create index pirel_paygrp_idx on dcspp_payitem_rel (payment_group_id);

create table dcspp_payship_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	default null,
	shipping_group_id	varchar(40)	default null,
	amount	numeric(19,7)	default null
,constraint dcspp_payship_re_p primary key (relationship_id)
,constraint dcspp_pshrltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index psrel_paygrp_idx on dcspp_payship_rel (payment_group_id);
create index psrel_shipgrp_idx on dcspp_payship_rel (shipping_group_id);

create table dcspp_payorder_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	default null,
	order_id	varchar(40)	default null,
	amount	numeric(19,7)	default null
,constraint dcspp_payorder_r_p primary key (relationship_id)
,constraint dcspp_odreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index porel_order_idx on dcspp_payorder_rel (order_id);
create index porel_paygrp_idx on dcspp_payorder_rel (payment_group_id);

create table dcspp_amount_info (
	amount_info_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	currency_code	varchar(10)	default null,
	amount	numeric(19,7)	default null,
	discounted	numeric(1,0)	default null,
	amount_is_final	numeric(1,0)	default null,
	final_rc	integer	default null
,constraint dcspp_amount_inf_p primary key (amount_info_id)
,constraint dcspp_amount_in1_c check (discounted IN (0,1))
,constraint dcspp_amount_in2_c check (amount_is_final IN (0,1)));


create table dcspp_order_price (
	amount_info_id	varchar(40)	not null,
	raw_subtotal	numeric(19,7)	default null,
	tax	numeric(19,7)	default null,
	shipping	numeric(19,7)	default null,
	manual_adj_total	numeric(19,7)	default null
,constraint dcspp_order_pric_p primary key (amount_info_id)
,constraint dcspp_oramnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_item_price (
	amount_info_id	varchar(40)	not null,
	list_price	numeric(19,7)	default null,
	raw_total_price	numeric(19,7)	default null,
	sale_price	numeric(19,7)	default null,
	on_sale	numeric(1,0)	default null,
	order_discount	numeric(19,7)	default null,
	qty_discounted	numeric(19,0)	default null,
	qty_as_qualifier	numeric(19,0)	default null,
	price_list	varchar(40)	default null
,constraint dcspp_item_price_p primary key (amount_info_id)
,constraint dcspp_itamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id)
,constraint dcspp_item_price_c check (on_sale IN (0,1)));


create table dcspp_tax_price (
	amount_info_id	varchar(40)	not null,
	city_tax	numeric(19,7)	default null,
	county_tax	numeric(19,7)	default null,
	state_tax	numeric(19,7)	default null,
	country_tax	numeric(19,7)	default null
,constraint dcspp_tax_price_p primary key (amount_info_id)
,constraint dcspp_taamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_ship_price (
	amount_info_id	varchar(40)	not null,
	raw_shipping	numeric(19,7)	default null
,constraint dcspp_ship_price_p primary key (amount_info_id)
,constraint dcspp_shamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));


create table dcspp_amtinfo_adj (
	amount_info_id	varchar(40)	not null,
	adjustments	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_amtinfo_ad_p primary key (amount_info_id,sequence_num)
,constraint dcspp_amamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index amtinf_adj_aid_idx on dcspp_amtinfo_adj (amount_info_id);
create index amtinf_adj_adj_idx on dcspp_amtinfo_adj (adjustments);

create table dcspp_price_adjust (
	adjustment_id	varchar(40)	not null,
	version	integer	not null,
	adj_description	varchar(254)	default null,
	pricing_model	varchar(40)	default null,
	manual_adjustment	varchar(40)	default null,
	adjustment	numeric(19,7)	default null,
	qty_adjusted	integer	default null
,constraint dcspp_price_adju_p primary key (adjustment_id));


create table dcspp_shipitem_sub (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	ship_item_subtotal	varchar(40)	not null
,constraint dcspp_shipitem_s_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_sbamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ship_item_sub_idx on dcspp_shipitem_sub (amount_info_id);

create table dcspp_taxshipitem (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	tax_ship_item_sub	varchar(40)	not null
,constraint dcspp_taxshipite_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_shamntxnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index tax_ship_item_idx on dcspp_taxshipitem (amount_info_id);

create table dcspp_ntaxshipitem (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	non_tax_item_sub	varchar(40)	not null
,constraint dcspp_ntaxshipit_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_ntamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ntax_ship_item_idx on dcspp_ntaxshipitem (amount_info_id);

create table dcspp_shipitem_tax (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	ship_item_tax	varchar(40)	not null
,constraint dcspp_shipitem_t_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_txamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index ship_item_tax_idx on dcspp_shipitem_tax (amount_info_id);

create table dcspp_itmprice_det (
	amount_info_id	varchar(40)	not null,
	cur_price_details	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_itmprice_d_p primary key (amount_info_id,sequence_num)
,constraint dcspp_sbamntnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

create index itmprc_det_aii_idx on dcspp_itmprice_det (amount_info_id);

create table dcspp_det_price (
	amount_info_id	varchar(40)	not null,
	tax	numeric(19,7)	default null,
	order_discount	numeric(19,7)	default null,
	order_manual_adj	numeric(19,7)	default null,
	quantity	numeric(19,0)	default null,
	qty_as_qualifier	numeric(19,0)	default null
,constraint dcspp_det_price_p primary key (amount_info_id));


create table dcspp_det_range (
	amount_info_id	varchar(40)	not null,
	low_bound	integer	default null,
	high_bound	integer	default null
,constraint dcspp_det_range_p primary key (amount_info_id));


create table dcspp_order_adj (
	order_id	varchar(40)	not null,
	adjustment_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_adj_p primary key (order_id,sequence_num)
,constraint dcspp_oradj_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_adj_orid_idx on dcspp_order_adj (order_id);

create table dcspp_manual_adj (
	manual_adjust_id	varchar(40)	not null,
	type	integer	not null,
	adjustment_type	integer	not null,
	reason	integer	not null,
	amount	numeric(19,7)	default null,
	notes	varchar(255)	default null,
	version	integer	not null
,constraint dcspp_manual_adj_p primary key (manual_adjust_id));


create table dbcpp_sched_order (
	scheduled_order_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar(32)	default null,
	profile_id	varchar(40)	default null,
	create_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	template_order	varchar(32)	default null,
	state	integer	default null,
	next_scheduled	timestamp	default null,
	schedule	varchar(255)	default null,
	site_id	varchar(40)	default null
,constraint dbcpp_sched_orde_p primary key (scheduled_order_id));

create index sched_tmplt_idx on dbcpp_sched_order (template_order);
create index sched_profile_idx on dbcpp_sched_order (profile_id);
create index sched_site_idx on dbcpp_sched_order (site_id);

create table dbcpp_sched_clone (
	scheduled_order_id	varchar(40)	not null,
	cloned_order	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_sched_clon_p primary key (scheduled_order_id,sequence_num)
,constraint dbcpp_scschedld__f foreign key (scheduled_order_id) references dbcpp_sched_order (scheduled_order_id));


create table dcspp_scherr_aux (
	scheduled_order_id	varchar(40)	not null,
	sched_error_id	varchar(40)	not null
,constraint dcspp_scherr_aux_p primary key (scheduled_order_id));

create index sched_error_idx on dcspp_scherr_aux (sched_error_id);

create table dcspp_sched_error (
	sched_error_id	varchar(40)	not null,
	error_date	timestamp	not null
,constraint dcspp_sched_err_p primary key (sched_error_id));


create table dcspp_schd_errmsg (
	sched_error_id	varchar(40)	not null,
	error_txt	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_schd_errs_p primary key (sched_error_id,sequence_num)
,constraint dcspp_sch_errs_f foreign key (sched_error_id) references dcspp_sched_error (sched_error_id));

commit;



-- the source for this section is 
-- dcs_mappers.sql





create table dcs_cart_event (
	id	varchar(40)	not null,
	timestamp	timestamp	default null,
	orderid	varchar(40)	default null,
	itemid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	quantity	integer	default null,
	amount	numeric(19,7)	default null,
	profileid	varchar(40)	default null);


create table dcs_submt_ord_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	orderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);


create table dcs_prom_used_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	orderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	promotionid	varchar(40)	default null,
	order_amount	numeric(26,7)	default null,
	discount	numeric(26,7)	default null);


create table dcs_ord_merge_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	sourceorderid	varchar(40)	default null,
	destorderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	sourceremoved	numeric(1,0)	default null
,constraint dcs_ordmergeevt_ck check (sourceremoved in (0,1)));


create table dcs_promo_rvkd (
	id	varchar(40)	not null,
	time_stamp	timestamp	default null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);


create table dcs_promo_grntd (
	id	varchar(40)	not null,
	time_stamp	timestamp	default null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);

commit;



-- the source for this section is 
-- claimable_ddl.sql





create table dcspp_claimable (
	claimable_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	status	integer	default null,
	expiration_date	timestamp	default null,
	last_modified	timestamp	default null
,constraint dcspp_claimable_p primary key (claimable_id));


create table dcspp_giftcert (
	giftcertificate_id	varchar(40)	not null,
	amount	double precision	not null,
	amount_authorized	double precision	not null,
	amount_remaining	double precision	not null,
	purchaser_id	varchar(40)	default null,
	purchase_date	timestamp	default null,
	last_used	timestamp	default null
,constraint dcspp_giftcert_p primary key (giftcertificate_id)
,constraint dcspp_gigiftcrtf_f foreign key (giftcertificate_id) references dcspp_claimable (claimable_id));

create index claimable_user_idx on dcspp_giftcert (purchaser_id);

create table dcs_storecred_clm (
	store_credit_id	varchar(40)	not null,
	amount	numeric(19,7)	not null,
	amount_authorized	numeric(19,7)	not null,
	amount_remaining	numeric(19,7)	not null,
	owner_id	varchar(40)	default null,
	issue_date	timestamp	default null,
	expiration_date	timestamp	default null,
	last_used	timestamp	default null
,constraint dcs_storecred_cl_p primary key (store_credit_id)
,constraint dcs_storstor_crd_f foreign key (store_credit_id) references dcspp_claimable (claimable_id));

create index storecr_issue_idx on dcs_storecred_clm (issue_date);
create index storecr_owner_idx on dcs_storecred_clm (owner_id);

create table dcspp_cp_folder (
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	default null
,constraint dcspp_cp_folder_p primary key (folder_id)
,constraint dcspp_cp_prntfol_f foreign key (parent_folder) references dcspp_cp_folder (folder_id));

create index dcspp_prntfol_idx on dcspp_cp_folder (parent_folder);

create table dcspp_coupon (
	coupon_id	varchar(40)	not null,
	promotion_id	varchar(40)	not null
,constraint dcspp_coupon_p primary key (coupon_id,promotion_id)
,constraint dcspp_coupon_df foreign key (coupon_id) references dcspp_claimable (claimable_id));


create table dcspp_coupon_info (
	coupon_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	use_promo_site	integer	default null,
	parent_folder	varchar(40)	default null
,constraint dcspp_copninfo_p primary key (coupon_id)
,constraint dcspp_copninfo_d_f foreign key (coupon_id) references dcspp_claimable (claimable_id)
,constraint dcspp_cpnifol_f foreign key (parent_folder) references dcspp_cp_folder (folder_id));

create index dcspp_folder_idx on dcspp_coupon_info (parent_folder);
commit;



-- the source for this section is 
-- priceLists_ddl.sql





create table dcs_price_list (
	price_list_id	varchar(40)	not null,
	version	integer	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	creation_date	timestamp	default null,
	last_mod_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	locale	integer	default null,
	base_price_list	varchar(40)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_price_list_p primary key (price_list_id)
,constraint dcs_pricbas_prcl_f foreign key (base_price_list) references dcs_price_list (price_list_id));

create index dcs_pricelstbase_i on dcs_price_list (base_price_list);

create table dcs_complex_price (
	complex_price_id	varchar(40)	not null,
	version	integer	not null
,constraint dcs_complex_pric_p primary key (complex_price_id));


create table dcs_price (
	price_id	varchar(40)	not null,
	version	integer	not null,
	price_list	varchar(40)	not null,
	product_id	varchar(40)	default null,
	sku_id	varchar(40)	default null,
	parent_sku_id	varchar(40)	default null,
	pricing_scheme	integer	not null,
	list_price	double precision	default null,
	complex_price	varchar(40)	default null
,constraint dcs_price_p primary key (price_id)
,constraint dcs_priccomplx_p_f foreign key (complex_price) references dcs_complex_price (complex_price_id)
,constraint dcs_pricpric_lst_f foreign key (price_list) references dcs_price_list (price_list_id));

create index dcs_cmplx_prc_idx on dcs_price (complex_price);
create index dcs_price_idx1 on dcs_price (product_id);
create index dcs_price_idx2 on dcs_price (price_list,sku_id);

create table dcs_price_levels (
	complex_price_id	varchar(40)	not null,
	price_levels	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_price_levels_p primary key (complex_price_id,sequence_num)
,constraint dcs_lvlscomplx_p_f foreign key (complex_price_id) references dcs_complex_price (complex_price_id));


create table dcs_price_level (
	price_level_id	varchar(40)	not null,
	version	integer	not null,
	quantity	integer	not null,
	price	double precision	not null
,constraint dcs_price_level_p primary key (price_level_id));


create table dcs_gen_fol_pl (
	folder_id	varchar(40)	not null,
	type	integer	not null,
	name	varchar(40)	not null,
	parent	varchar(40)	default null,
	description	varchar(254)	default null,
	item_acl	varchar(1024)	default null
,constraint dcs_gen_fol_pl_p primary key (folder_id));


create table dcs_child_fol_pl (
	folder_id	varchar(40)	not null,
	sequence_num	integer	not null,
	child_folder_id	varchar(40)	not null
,constraint dcs_child_fol_pl_p primary key (folder_id,sequence_num)
,constraint dcs_chilfoldr_d_f foreign key (folder_id) references dcs_gen_fol_pl (folder_id));


create table dcs_plfol_chld (
	plfol_id	varchar(40)	not null,
	sequence_num	integer	not null,
	price_list_id	varchar(40)	not null
,constraint dcs_plfol_chld_p primary key (plfol_id,sequence_num)
,constraint dcs_plfoplfol_d_f foreign key (plfol_id) references dcs_gen_fol_pl (folder_id));

commit;



-- the source for this section is 
-- order_markers_ddl.sql





create table dcs_order_markers (
	marker_id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	default null,
	marker_data	varchar(100)	default null,
	creation_date	timestamp	default null,
	version	integer	not null,
	marker_type	integer	default null
,constraint dcsordermarkers_p primary key (marker_id,order_id)
,constraint dcsordermarkers_f foreign key (order_id) references dcspp_order (order_id));

create index dcs_ordrmarkers1_x on dcs_order_markers (order_id);

create table dcspp_commerce_item_markers (
	marker_id	varchar(40)	not null,
	commerce_item_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	default null,
	marker_data	varchar(100)	default null,
	creation_date	timestamp	default null,
	version	integer	not null,
	marker_type	integer	default null
,constraint dcscitemmarkers_p primary key (marker_id,commerce_item_id)
,constraint dcscitemmarkers_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id));

create index dcs_itemmarkers1_x on dcspp_commerce_item_markers (commerce_item_id);
commit;



-- the source for this section is 
-- commerce_site_ddl.sql




-- This file contains create table statements, which will configureyour database for use MultiSite

create table dcs_site (
	id	varchar(40)	not null,
	catalog_id	varchar(40)	default null,
	list_pricelist_id	varchar(40)	default null,
	sale_pricelist_id	varchar(40)	default null
,constraint dcs_site_p primary key (id));

commit;



-- the source for this section is 
-- reporting_views.sql




--        In the comments, the time periods indicated are calendar time, meaning that   
--        the current period should be calculated from the start of the calendar time period,   
--        rather than in real time.  For example, if it is Thursday, July 12,   
--        the most current row in a view calculating totals for a week    
--        would run from Sunday, July 8 - July 12, rather than July 5 - July 12.   
--        drpt_order gathers basic information about each order   
create view drpt_order
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	ai.amount as amount, 
	count(i.quantity) as distinct_items, 
	sum(i.quantity) as total_items,
	ba.state as state, 
	ba.country as country, 
	o.price_info as price_info
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcspp_pay_group pg, 
	dcspp_bill_addr ba
where o.order_id = i.order_ref 
	and o.price_info = ai.amount_info_id 
	and o.order_id = pg.order_ref
	and pg.payment_group_id = ba.payment_group_id
group by o.order_id, 
	o.submitted_date, 
	ai.amount, 
	ba.state, 
	ba.country, 
	o.price_info
         ;


--        drpt_cost_of_goods calculates the total wholesale cost   
--        of the items purchased in each order   
create view drpt_cost_of_goods
as
select i.order_ref as order_id, 
	sum(i.quantity * s.wholesale_price) as cost_of_goods
from dcspp_item i, 
	dcs_sku s
where i.catalog_ref_id = s.sku_id
group by i.order_ref
         ;


--        drpt_discount calculates the total amount discounted   
--        from each order via promotions   
create view drpt_discount
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	(0 - sum(pa.adjustment)) as discount
from dcspp_order o, 
	dcspp_item i, 
	dcspp_amtinfo_adj aa, 
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and i.price_info = aa.amount_info_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model is not null
group by o.order_id, 
	o.submitted_date
         ;


--        drptw_discounts calculates the number of orders that   
--        were discounted by promotions over each week   
--        The "UNION" operation allows weeks in which there   
--        were no discounts to have a row in the view   
create view drptw_discounts
as
select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) as week, 
	count(order_id) as num_of_discounts
from drpt_discount
group by DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1)
UNION
select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) as week, 
	0 as num_of_discounts
from dcspp_order 
where DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) not in (select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) from drpt_discount)
         ;


--        drptm_discounts calculates the number of orders that   
--        were discounted by promotions over each month   
--        The "UNION" operation allows months in which there   
--        were no discounts to have a row in the view   
create view drptm_discounts
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') as month,
count(order_id) as num_of_discounts
from drpt_discount
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') as month, 
0 as num_of_discounts
from dcspp_order 
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') from drpt_discount)
         ;


--        drptq_discounts calculates the number of orders that   
--        were discounted by promotions over each quarter   
--        The "UNION" operation allows quarters in which there   
--        were no discounts to have a row in the view   
create view drptq_discounts
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
count(order_id) as num_of_discounts
from drpt_discount
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
0 as num_of_discounts
from dcspp_order 
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') from drpt_discount)
         ;


--        drpta_discounts calculates the number of orders that   
--        were discounted by promotions over each year   
--        The "UNION" operation allows years in which there   
--        were no discounts to have a row in the view   
create view drpta_discounts
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') as year, 
	count(order_id) as num_of_discounts
from drpt_discount
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') as year, 
	0 as num_of_discounts
from dcspp_order 
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') from drpt_discount)
         ;


--        drpt_ordered_items comprises a list of each item purchased   
create view drpt_ordered_items
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date, 
	i.catalog_ref_id as catalog_ref_id,
	i.product_id as product_id, 
	i.quantity as quantity, 
	ai.amount as price
from dcspp_order o, 
	dcspp_item i, 
	dcspp_amount_info ai
where o.order_id = i.order_ref 
	and i.price_info = ai.amount_info_id
         ;


--        drpt_sku_stock calculates the number of unique skus in stock per product   
--        in the catalog.  The "UNION" operation exists so that a row will   
--        appear for products that have no skus in stock   
create view drpt_sku_stock
as
             select pc.product_id as product_id, 
	count(i.catalog_ref_id) as skus_in_stock
from dcs_prd_chldsku pc, 
	dcs_inventory i
where pc.sku_id = i.catalog_ref_id 
	and i.stock_level > 0
group by pc.product_id
UNION
select product_id, 
	0 as skus_in_stock 
from dcs_prd_chldsku 
where product_id not in 
(select product_id 
from dcs_prd_chldsku pc, 
	dcs_inventory i 
where pc.sku_id = i.catalog_ref_id 
	and i.stock_level > 0)
         ;


--        drptw_browses calculatess the number of times each product's page   
--        has been viewed online each week   
create view drptw_browses
as
select repositoryid as product_id, 
	DATE(DAYS(timestamp) - DAYOFWEEK(timestamp) +1) as week, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, DATE(DAYS(timestamp) - DAYOFWEEK(timestamp) +1)
         ;


--        drptm_browses calculates the number of times each product's page   
--        has been viewed online each month   
create view drptm_browses
as
select repositoryid as product_id, 
	DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(timestamp) AS CHAR(2)))||'-01') as month, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, 
	DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(timestamp) AS CHAR(2)))||'-01')
         ;


--        drptq_browses calculates the number of times each product's page   
--        has been viewed online each quarter   
create view drptq_browses
as
select repositoryid as product_id, 
DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(timestamp) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, 
DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(timestamp) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_browses calculates the number of times each product's page   
--        has been viewed online each year   
create view drpta_browses
as
select repositoryid as product_id,DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-1-1') as year, 
	count(timestamp) as browses
from dss_dps_view_item
group by repositoryid, DATE(CAST(YEAR(timestamp) AS CHAR(4))||'-1-1')
         ;


--        drptw_carts calculates the number of times each product has   
--        been added to a user's shopping cart each week   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create view drptw_carts
as
select pc.product_id as product_id, 
	DATE(DAYS(ce.timestamp) - DAYOFWEEK(ce.timestamp) +1) as week, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	DATE(DAYS(ce.timestamp) - DAYOFWEEK(ce.timestamp) +1)
         ;


--        drptm_carts calculates the number of times each product has   
--        been added to a user's shopping cart each month   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create view drptm_carts
as
select pc.product_id as product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(ce.timestamp) AS CHAR(2)))||'-01') as month, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(ce.timestamp) AS CHAR(2)))||'-01')
         ;


--        drptq_carts calculates the number of times each product has   
--        been added to a user's shopping cart each quarter   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create view drptq_carts
as
select pc.product_id as product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(ce.timestamp) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(ce.timestamp) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_carts calculates the number of times each product has   
--        been added to a user's shopping cart each year   
--        Note:  A single user adding a quantity greater than one   
--        to their cart at one time is considered a single "add to cart"   
create view drpta_carts
as
select pc.product_id as product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-1-1') as year, 
	count(ce.id) as adds_to_cart
from dcs_cart_event ce, 
	dcs_prd_chldsku pc
where ce.itemid = pc.sku_id
group by pc.product_id, 
	DATE(CAST(YEAR(ce.timestamp) AS CHAR(4))||'-1-1')
         ;


--        drpt_shipping compiles the total shipping cost for each shipping group   
create view drpt_shipping
as
select o.order_id as order_id, 
	ai.amount as shipping_cost
from dcspp_order o, 
	dcspp_ship_group sg, 
	dcspp_amount_info ai
where o.order_id = sg.order_ref 
	and sg.price_info = ai.amount_info_id
         ;


--        drpt_taxes compiles the total tax cost for each order   
create view drpt_taxes
as
select o.order_id as order_id, 
	ai.amount as tax
from dcspp_order o, 
	dcspp_amount_info ai
where o.tax_price_info = ai.amount_info_id
         ;


--        drpt_cancels compiles information about orders that failed or   
--        were cancelled   
create view drpt_cancels
as
select o.order_id as order_id, 
	o.submitted_date as submitted_date,
	ai.amount as amount, 
	si.shipping_cost as shipping_cost,
	ti.tax as tax
from dcspp_order o, 
	dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti
where (o.state = 'FAILED' or o.state = 'REMOVED') 
	and o.price_info = ai.amount_info_id
	and o.order_id = si.order_id 
	and o.order_id = ti.order_id
         ;


--        drptw_cancels calculates the number of orders that failed or   
--        were cancelled each week.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create view drptw_cancels
as
select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) as week, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1)
UNION
select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) as week, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) not in (select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) from drpt_cancels)
group by DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1)
         ;


--        drptm_cancels calculates the number of orders that failed or   
--        were cancelled each month.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create view drptm_cancels
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') as month, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') as month, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') from drpt_cancels)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01')
         ;


--        drptq_cancels calculates the number of orders that failed or   
--        were cancelled each quarter.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create view drptq_cancels
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') from drpt_cancels)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_cancels calculates the number of orders that failed or   
--        were cancelled in the past year.  It also calculates the total   
--        amount, shipping costs, and tax costs from those orders.   
create view drpta_cancels
as
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') as year, 
	count(order_id) as number_of_cancels,
	sum(amount) as amount, 
	sum(shipping_cost) as shipping_cost, 
	sum(tax) as tax
from drpt_cancels
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') as year, 
	0 as number_of_cancels, 
	0 as amount,
	0 as shipping_cost, 
	0 as tax
from dcspp_order
where DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') not in (select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') from drpt_cancels)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1')
         ;


--        drptw_gift_certs calculates the number of gift certificates   
--        that were redeemed each week   
create view drptw_gift_certs
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
UNION
select DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1) as week, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by DATE(DAYS(submitted_date) - DAYOFWEEK(submitted_date) +1)
         ;


--        drptm_gift_certs calculates the number of gift certificates   
--        that were redeemed each month   
create view drptm_gift_certs
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01') as month, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(submitted_date) AS CHAR(2)))||'-01')
         ;


--        drptq_gift_certs calculates the number of gift certificates   
--        that were redeemed each quarter   
create view drptq_gift_certs
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_gift_certs calculates the number of gift certificates   
--        that were redeemed each year   
create view drpta_gift_certs
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	count(gc.payment_group_id) as num_of_gift_certs
from dcspp_order o, 
	dcspp_pay_group pg, 
	dcspp_gift_cert gc
where o.order_id = pg.order_ref 
	and pg.payment_group_id = gc.payment_group_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
UNION
select DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1') as year, 
	0 as num_of_gift_certs
from dcspp_order
where order_id not in 
(select pg.order_ref 
from dcspp_pay_group pg, 
	dcspp_gift_cert gc 
where pg.payment_group_id = gc.payment_group_id)
group by DATE(CAST(YEAR(submitted_date) AS CHAR(4))||'-1-1')
         ;


--        drptw_orders calculates various totals from orders over each week   
create view drptw_orders
as
select DATE(DAYS(dri.submitted_date) - DAYOFWEEK(dri.submitted_date) +1) as week, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(wct.amount + wct.shipping_cost + wct.tax) as dollar_cancels,
	(sum(ai.amount) - wct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - wct.shipping_cost - wct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(wct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - wct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	wdt.num_of_discounts as num_of_discounts,
	wgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptw_cancels wct, 
	drptw_discounts wdt,
	drptw_gift_certs wgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and DATE(DAYS(dri.submitted_date) - DAYOFWEEK(dri.submitted_date) +1) = wct.week 
	and DATE(DAYS(dri.submitted_date) - DAYOFWEEK(dri.submitted_date) +1) = wdt.week 
	and DATE(DAYS(dri.submitted_date) - DAYOFWEEK(dri.submitted_date) +1) = wgct.week 
	and dri.order_id = cog.order_id
group by DATE(DAYS(dri.submitted_date) - DAYOFWEEK(dri.submitted_date) +1), 
	wct.number_of_cancels, 
	wct.amount, 
	wct.shipping_cost, 
	wct.tax, 
	wdt.num_of_discounts, 
	wgct.num_of_gift_certs
	;


--        drptm_orders calculates various totals from orders over each month   
create view drptm_orders
as
select DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(dri.submitted_date) AS CHAR(2)))||'-01') as month, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(mct.amount + mct.shipping_cost + mct.tax) as dollar_cancels,
	(sum(ai.amount) - mct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - mct.shipping_cost - mct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(mct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - mct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	mdt.num_of_discounts as num_of_discounts,
	mgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptm_cancels mct, 
	drptm_discounts mdt,
	drptm_gift_certs mgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(dri.submitted_date) AS CHAR(2)))||'-01') = mct.month 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(dri.submitted_date) AS CHAR(2)))||'-01') = mdt.month 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(dri.submitted_date) AS CHAR(2)))||'-01') = mgct.month 
	and dri.order_id = cog.order_id
group by DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(dri.submitted_date) AS CHAR(2)))||'-01'), 
	mct.number_of_cancels, mct.amount, 
	mct.shipping_cost, 
	mct.tax, 
	mdt.num_of_discounts, 
	mgct.num_of_gift_certs
         ;


--        drptq_orders calculates various totals from orders over each quarter   
create view drptq_orders
as
select DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(dri.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(qct.amount + qct.shipping_cost + qct.tax) as dollar_cancels,
	(sum(ai.amount) - qct.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - qct.shipping_cost - qct.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(qct.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - qct.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	qdt.num_of_discounts as num_of_discounts,
	qgct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drptq_cancels qct, 
	drptq_discounts qdt,
	drptq_gift_certs qgct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(dri.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') = qct.quarter 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(dri.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') = qdt.quarter 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(dri.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') = qgct.quarter 
	and dri.order_id = cog.order_id
group by DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(dri.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01'), 
	qct.number_of_cancels, 
	qct.amount, 
	qct.shipping_cost, 
	qct.tax, 
	qdt.num_of_discounts, 
	qgct.num_of_gift_certs
         ;


--        drpta_orders calculates various totals from orders over each year   
create view drpta_orders
as
select DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-1-1') as year, 
	(sum(ai.amount) + sum(si.shipping_cost) + sum(ti.tax)) as total_dollar_sales,
	(act.amount + act.shipping_cost + act.tax) as dollar_cancels,
	(sum(ai.amount) - act.amount) as merch_rev,
	(sum(si.shipping_cost) + sum(ti.tax) - act.shipping_cost - act.tax) as shipping_tax_rev,
	sum(cog.cost_of_goods) as cost_of_goods_sold,
	(count(dri.order_id)) as number_of_orders, 
	(act.number_of_cancels) as cancelled_orders,
	(count(dri.order_id) - act.number_of_cancels) as net_num_of_orders,
	sum(dri.total_items) as total_units_sold,
	adt.num_of_discounts as num_of_discounts,
	agct.num_of_gift_certs as num_of_gift_certs
from dcspp_amount_info ai, 
	drpt_shipping si, 
	drpt_taxes ti,
	drpta_cancels act, 
	drpta_discounts adt,
	drpta_gift_certs agct, 
	drpt_order dri, 
	drpt_cost_of_goods cog
where dri.price_info = ai.amount_info_id 
	and dri.order_id = si.order_id 
	and dri.order_id = ti.order_id 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-1-1') = act.year 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-1-1') = adt.year 
	and DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-1-1') = agct.year 
	and dri.order_id = cog.order_id
group by DATE(CAST(YEAR(dri.submitted_date) AS CHAR(4))||'-1-1'), 
	act.number_of_cancels, 
	act.amount, 
	act.shipping_cost, 
	act.tax, 
	adt.num_of_discounts, 
	agct.num_of_gift_certs
         ;


create view drpt_visitor
as
select distinct
        vi.profileid,
        date(vi.timestamp) as day,
        ci.state,
        ci.country
from dss_dps_view_item vi, dps_user_address ua, dps_contact_info ci
     where vi.profileid = ua.id
           and ua.billing_addr_id = ci.id
         ;


--        drpt_cart calculates the total quantity and amount of   
--        items that have been added to shopping carts over each day   
create view drpt_cart
as
select
        orderid as orderid, 
	date(timestamp) as day, 
	sum(quantity) as quantity, 
	sum(amount) as amount
from dcs_cart_event
group by orderid, 
	date(timestamp)
         ;


--        drptw_fiscal_info caclulates several revenue-related totals over each week   
create view drptw_fiscal_info
as
select wot.week as week, 
	wot.total_dollar_sales as total_dollar_sales,
	wot.dollar_cancels as dollar_cancels, 
	(wot.total_dollar_sales - wot.dollar_cancels) as net_dollar_sales,
	wot.merch_rev as merch_rev,
	wot.shipping_tax_rev as shipping_tax_rev, 
	wot.number_of_orders as number_of_orders,
	wot.cancelled_orders as cancelled_orders, 
	wot.net_num_of_orders as net_num_of_orders,
	count(ce.orderid) as num_of_carts, 
	((wot.total_dollar_sales - wot.dollar_cancels) / wot.net_num_of_orders) as avg_order_rev,
	(wot.merch_rev / wot.net_num_of_orders) as avg_order_merc_rev,
	count(vi.profileid) as number_of_shoppers,
	(wot.number_of_orders / count(vi.profileid)) as shop_to_purc_conv,
	(count(ce.orderid) / count(vi.profileid)) as shop_to_cart_conv,
	(wot.number_of_orders / count(ce.orderid)) as cart_to_purc_conv,
	wot.num_of_discounts as num_of_discounts, 
	wot.num_of_gift_certs as num_of_gift_certs
from drptw_orders wot, 
	drpt_cart ce, 
	drpt_visitor vi
where  wot.week = DATE(DAYS(ce.day) - DAYOFWEEK(ce.day) +1)
	and wot.week = DATE(DAYS(vi.day) - DAYOFWEEK(vi.day) +1)
group by wot.week, 
	wot.total_dollar_sales, 
	wot.dollar_cancels,
	wot.merch_rev, 
	wot.shipping_tax_rev, 
	wot.number_of_orders,
	wot.cancelled_orders, 
	wot.net_num_of_orders, 
	wot.num_of_discounts,
	wot.num_of_gift_certs
         ;


--        drptm_fiscal_info caclulates several revenue-related totals over each month   
create view drptm_fiscal_info
as
select mot.month as month, 
	mot.total_dollar_sales as total_dollar_sales,
	mot.dollar_cancels as dollar_cancels, 
	(mot.total_dollar_sales - mot.dollar_cancels) as net_dollar_sales,
	mot.merch_rev as merch_rev,
	mot.shipping_tax_rev as shipping_tax_rev, 
	mot.number_of_orders as number_of_orders,
	mot.cancelled_orders as cancelled_orders, 
	mot.net_num_of_orders as net_num_of_orders,
	count(ce.orderid) as num_of_carts, 
	((mot.total_dollar_sales - mot.dollar_cancels) / mot.net_num_of_orders) as avg_order_rev,
	(mot.merch_rev / mot.net_num_of_orders) as avg_order_merc_rev,
	count(vi.profileid) as number_of_shoppers,
	(mot.number_of_orders / count(vi.profileid)) as shop_to_purc_conv,
	(count(ce.orderid) / count(vi.profileid)) as shop_to_cart_conv,
	(mot.number_of_orders / count(ce.orderid)) as cart_to_purc_conv,
	mot.num_of_discounts as num_of_discounts, 
	mot.num_of_gift_certs as num_of_gift_certs
from drptm_orders mot, 
	drpt_cart ce, 
	drpt_visitor vi
where  mot.month  = DATE(CAST(YEAR(ce.day) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(ce.day) AS CHAR(2)))||'-01')
	and mot.month  = DATE(CAST(YEAR(vi.day) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(vi.day) AS CHAR(2)))||'-01')
group by mot.month, 
	mot.total_dollar_sales, 
	mot.dollar_cancels,
	mot.merch_rev, 
	mot.shipping_tax_rev, 
	mot.number_of_orders,
	mot.cancelled_orders, 
	mot.net_num_of_orders, 
	mot.num_of_discounts,
	mot.num_of_gift_certs
         ;


--        drptq_fiscal_info caclulates several revenue-related totals over each quarter   
create view drptq_fiscal_info
as
select qot.quarter as quarter, 
	qot.total_dollar_sales as total_dollar_sales,
	qot.dollar_cancels as dollar_cancels, 
	(qot.total_dollar_sales - qot.dollar_cancels) as net_dollar_sales,
	qot.merch_rev as merch_rev,
	qot.shipping_tax_rev as shipping_tax_rev, 
	qot.number_of_orders as number_of_orders,
	qot.cancelled_orders as cancelled_orders, 
	qot.net_num_of_orders as net_num_of_orders,
	count(ce.orderid) as num_of_carts, 
	((qot.total_dollar_sales - qot.dollar_cancels) / qot.net_num_of_orders) as avg_order_rev,
	(qot.merch_rev / qot.net_num_of_orders) as avg_order_merc_rev,
	count(vi.profileid) as number_of_shoppers,
	(qot.number_of_orders / count(vi.profileid)) as shop_to_purc_conv,
	(count(ce.orderid) / count(vi.profileid)) as shop_to_cart_conv,
	(qot.number_of_orders / count(ce.orderid)) as cart_to_purc_conv,
	qot.num_of_discounts as num_of_discounts, 
	qot.num_of_gift_certs as num_of_gift_certs
from drptq_orders qot, 
	drpt_cart ce, 
	drpt_visitor vi
where  qot.quarter = DATE(CAST(YEAR(ce.day) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(ce.day) - 1)*3+1) AS CHAR(2)))||'-01')
	and qot.quarter = DATE(CAST(YEAR(vi.day) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(vi.day) - 1)*3+1) AS CHAR(2)))||'-01')
group by qot.quarter, 
	qot.total_dollar_sales, 
	qot.dollar_cancels,
	qot.merch_rev, 
	qot.shipping_tax_rev, 
	qot.number_of_orders,
	qot.cancelled_orders, 
	qot.net_num_of_orders, 
	qot.num_of_discounts,
	qot.num_of_gift_certs
         ;


--        drpta_fiscal_info caclulates several revenue-related totals over each year   
create view drpta_fiscal_info
as
select aot.year as year, 
	aot.total_dollar_sales as total_dollar_sales,
	aot.dollar_cancels as dollar_cancels, 
	(aot.total_dollar_sales - aot.dollar_cancels) as net_dollar_sales,
	aot.merch_rev as merch_rev,
	aot.shipping_tax_rev as shipping_tax_rev,
	aot.number_of_orders as number_of_orders,
	aot.cancelled_orders as cancelled_orders, 
	aot.net_num_of_orders as net_num_of_orders,
	count(ce.orderid) as num_of_carts, 
	((aot.total_dollar_sales - aot.dollar_cancels) / aot.net_num_of_orders) as avg_order_rev,
	(aot.merch_rev / aot.net_num_of_orders) as avg_order_merc_rev,
	count(vi.profileid) as number_of_shoppers,
	(aot.number_of_orders / count(vi.profileid)) as shop_to_purc_conv,
	(count(ce.orderid) / count(vi.profileid)) as shop_to_cart_conv,
	(aot.number_of_orders / count(ce.orderid)) as cart_to_purc_conv,
	aot.num_of_discounts as num_of_discounts, 
	aot.num_of_gift_certs as num_of_gift_certs
from drpta_orders aot, 
	drpt_cart ce, 
	drpt_visitor vi
where  aot.year = DATE(CAST(YEAR(ce.day) AS CHAR(4))||'-1-1')
	and aot.year = DATE(CAST(YEAR(vi.day) AS CHAR(4))||'-1-1')
group by aot.year, 
	aot.total_dollar_sales, 
	aot.dollar_cancels,
	aot.merch_rev, 
	aot.shipping_tax_rev, 
	aot.number_of_orders,
	aot.cancelled_orders, 
	aot.net_num_of_orders, 
	aot.num_of_discounts,
	aot.num_of_gift_certs
         ;


--        drptw_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each week   
create view drptw_promotion
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
         ;


--        drptm_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each month   
create view drptm_promotion
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
         ;


--        drptq_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each quarter   
create view drptq_promotion
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_promotion calculates totals about orders that were discounted   
--        by the sample 'promo60003' promotion over each year   
create view drpta_promotion
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
         ;


--        drptw_male_18_25 calculates totals about orders that were placed   
--        each week by males aged 18-25   
create view drptw_male_18_25
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
         ;


--        drptm_male_18_25 calculates totals about orders that were placed   
--        each month by males aged 18-25   
create view drptm_male_18_25
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
         ;


--        drptq_male_18_25 calculates totals about orders that were placed   
--        each quarter by males aged 18-25   
create view drptq_male_18_25
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_male_18_25 calculates totals about orders that were placed   
--        each year by males aged 18-25   
create view drpta_male_18_25
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
         ;


--        drptw_male_25_39 calculates totals about orders that were placed   
--        each week by males aged 25-39   
create view drptw_male_25_39
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id
	and u.gender = 2
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
         ;


--        drptm_male_25_39 calculates totals about orders that were placed   
--        each month by males aged 25-39   
create view drptm_male_25_39
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
         ;


--        drptq_male_25_39 calculates totals about orders that were placed   
--        each quarter by males aged 25-39   
create view drptq_male_25_39
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
         ;


--        drpta_male_25_39 calculates totals about orders that were placed   
--        each year by males aged 25-39   
create view drpta_male_25_39
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	sum(ai.amount) as total_dollar_sales,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	count(o.order_id) as number_of_orders, 
	sum(i.quantity) as total_units_sold
from dcspp_order o, 
	dcspp_amount_info ai, 
	dcspp_item i, 
	dcs_sku s, 
	dps_user u
where o.price_info = ai.amount_info_id 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED' 
	and o.order_id = i.order_ref 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
         ;


commit;

-- the source for this section is 
-- custom_catalog_reporting.sql




--        new drpt_products compiles information about each product in the catalog new   
create view drpt_products
as
select p.product_id as product_id, 
	'N/A' as category_name,
	avg(s.wholesale_price) as avg_cost, 
	avg(s.list_price) as avg_list_price, 
	avg(s.sale_price) as avg_sale_price, 
	((avg(s.list_price) - avg(s.wholesale_price)) / avg(s.wholesale_price)) as avg_initial_markup, 
	sum(inv.stock_level) as units_on_hand, 
	count(s.sku_id) as number_of_skus
from dcs_product p, 
	dcs_sku s, 
	dcs_prd_chldsku pc, 
	dcs_inventory inv
where p.product_id = pc.product_id 
	and pc.sku_id = s.sku_id
	and pc.sku_id = inv.catalog_ref_id
group by p.product_id
         ;


--        new drpt_category calculates statistics about prices and costs on a per-category basis  new   
create view drpt_category
as
select ctlg.display_name as catalog_name, 
	c.display_name as category_name, 
	c.category_id as category_id,
	avg(s.wholesale_price)as avg_cost,
	avg(s.list_price) as avg_list_price,
	avg(s.sale_price) as avg_sale_price,
	((avg(s.list_price) - avg(s.wholesale_price)) / avg(s.wholesale_price)) as avg_initial_markup,
	sum(inv.stock_level) as units_on_hand, 
	count(s.sku_id) as number_of_skus
from dcs_catalog ctlg, 
	dcs_category c, 
	dcs_sku s, 
	dcs_prd_chldsku pc, 
	dcs_product_info pi,
	dcs_prd_prdinfo ppi, 
	dcs_inventory inv
where c.category_id = pi.parent_cat_id 
	and pc.product_id = ppi.product_id 
	and pc.sku_id = s.sku_id
	and ctlg.catalog_id = c.catalog_id 
	and pc.sku_id = s.sku_id 
	and ppi.catalog_id = ctlg.catalog_id 
	and ppi.product_info_id = pi.product_info_id 
	and pc.sku_id = inv.catalog_ref_id
group by c.display_name,
	ctlg.display_name, 
	c.category_id
         ;


commit;

-- the source for this section is 
-- reporting_views2.sql




--        drptw_prod_sales calculates several statistics over each week on a per-product basis    
-- drptw_prod_sales calculates several statistics over each week on a per-product basis    
create view drptw_prod_sales
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s
where o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wb.week 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and i.catalog_ref_id = s.sku_id
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold,
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock  ;


--        drptm_prod_sales calculates several statistics over each month on a per-product basis    
create view drptm_prod_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s
where o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') = mb.month 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and i.catalog_ref_id = s.sku_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold,
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drptq_prod_sales calculates several statistics over each quarter on a per-product basis    
create view drptq_prod_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
 as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s
where o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
 = qb.quarter 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
 = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and i.catalog_ref_id = s.sku_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	qb.browses, 	
	qc.adds_to_cart, 
	qot.total_units_sold,
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drpta_prod_sales calculates several statistics over each year on a per-product basis    
create view drpta_prod_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev, 
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price * i.quantity)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s
where o.order_id = i.order_ref 
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') = ab.year 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and i.catalog_ref_id = s.sku_id
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup,
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold,
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock
         ;


--        drptw_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each week   
create view drptw_promo_sales
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_promotion wpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wb.week 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and wpt.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each month   
create view drptm_promo_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_promotion mpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') = mb.month 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') = mc.month 
	and si.product_id = i.product_id 
	and mot.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and mpt.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each quarter   
create view drptq_promo_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot,
	dcs_sku s, 
	drptq_promotion qpt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') = qb.quarter 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') = qc.quarter 
	and si.product_id = i.product_id 
	and qot.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') 
	and qpt.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_promo_sales calculates totals about products that were discounted   
--        by the sample 'promo60003' promotion over each year   
create view drpta_promo_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as promo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as promo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_promotion apt, 
	dcspp_amtinfo_adj aa,
	dcspp_price_adjust pa
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') = ab.year 
	and DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') = ac.year 
	and si.product_id = i.product_id 
	and aot.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and apt.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
	and i.catalog_ref_id = s.sku_id
	and i.price_info = aa.amount_info_id 
	and aa.adjustments = pa.adjustment_id 
	and pa.pricing_model = 'promo60003'
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 
	apt.total_dollar_sales
         ;


--        drptw_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each week   
create view drptw_m18_25_sales
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_male_18_25 wpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wb.week 
	and DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) = wc.week 
	and si.product_id = i.product_id 
	and wot.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and wpt.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each month   
create view drptm_m18_25_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_male_18_25 mpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and mb.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
	and mc.month=DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')  
	and si.product_id = i.product_id 
	and mot.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and mpt.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each quarter   
create view drptq_m18_25_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')  as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s, 
	drptq_male_18_25 qpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and qb.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and qc.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and si.product_id = i.product_id 
	and qot.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and qpt.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_m_18_25_sales calculates totals about products that were purchased   
--        by males aged 18-25 each year   
create view drpta_m18_25_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_male_18_25 apt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and ab.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
	and ac.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and si.product_id = i.product_id 
	and aot.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and apt.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 180000 
	and (o.submitted_date - u.date_of_birth) < 250000 
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev,
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 
	apt.total_dollar_sales
         ;


--        drptw_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each week   
create view drptw_m25_39_sales
as
select DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) as week, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / wpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / wot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / wpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / wot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	wb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / wb.browses) as shop_to_purc_conv, 
	wc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / wc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptw_browses wb, 
	drptw_carts wc, 
	drpt_sku_stock si, 
	drptw_orders wot, 
	dcs_sku s, 
	drptw_male_25_39 wpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = wc.product_id 
	and i.product_id = wb.product_id 
	and wb.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
	and wc.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1)
	and si.product_id = i.product_id 
	and wot.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and wpt.week = DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1) 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(DAYS(o.submitted_date) - DAYOFWEEK(o.submitted_date) +1), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	wb.browses, 
	wc.adds_to_cart, 
	wot.total_units_sold, 
	wot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	wpt.total_units_sold, 
	wpt.total_dollar_sales
         ;


--        drptm_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each month   
create view drptm_m25_39_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') as month, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / mpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / mot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as months_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / mpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / mot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	mb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / mb.browses) as shop_to_purc_conv, 
	mc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / mc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptm_browses mb, 
	drptm_carts mc, 
	drpt_sku_stock si, 
	drptm_orders mot, 
	dcs_sku s, 
	drptm_male_25_39 mpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = mc.product_id 
	and i.product_id = mb.product_id 
	and mb.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
	and mc.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01')
	and si.product_id = i.product_id 
	and mot.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and mpt.month = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(MONTH(o.submitted_date) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	mb.browses, 
	mc.adds_to_cart, 
	mot.total_units_sold, 
	mot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	mpt.total_units_sold, 
	mpt.total_dollar_sales
         ;


--        drptq_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each quarter   
create view drptq_m25_39_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') as quarter, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / qpt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / qot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as quarters_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / qpt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / qot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	qb.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / qb.browses) as shop_to_purc_conv, 
	qc.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / qc.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drptq_browses qb, 
	drptq_carts qc, 
	drpt_sku_stock si, 
	drptq_orders qot, 
	dcs_sku s, 
	drptq_male_25_39 qpt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = qc.product_id 
	and i.product_id = qb.product_id 
	and qb.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') 
	and qc.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01')
	and si.product_id = i.product_id 
	and qot.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') 
	and qpt.quarter = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-'||RTRIM(CAST(((QUARTER(o.submitted_date) - 1)*3+1) AS CHAR(2)))||'-01'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	qb.browses, 
	qc.adds_to_cart, 
	qot.total_units_sold, 
	qot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	qpt.total_units_sold, 
	qpt.total_dollar_sales
         ;


--        drpta_m_25_39_sales calculates totals about products that were purchased   
--        by males aged 25-39 each year   
create view drpta_m25_39_sales
as
select DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') as year, 
	i.product_id as product_id, 
	pri.category_name as category_name, 
	pri.avg_cost as avg_cost,
	pri.avg_list_price as avg_list_price, 
	pri.avg_sale_price as avg_sale_price, 
	pri.avg_initial_markup as avg_initial_markup,
	pri.units_on_hand as units_on_hand,
	sum(i.quantity) as units_sold, 
	(sum(i.quantity) / apt.total_units_sold) as demo_units_sold_p, 
	(sum(i.quantity) / aot.total_units_sold) as total_units_sold_p, 
	(pri.units_on_hand / sum(i.quantity)) as years_on_hand,
	sum(ai.amount) as total_rev,
	sum(s.wholesale_price * i.quantity) as cost_of_goods_sold,
	((sum(ai.amount) - sum(s.wholesale_price)) / sum(ai.amount)) as maintained_markup, 
	(sum(ai.amount) / apt.total_dollar_sales) as demo_rev_p,
	(sum(ai.amount) / aot.merch_rev) as total_rev_p,
	pri.number_of_skus as number_of_skus, 
	si.skus_in_stock as skus_in_stock,
	(si.skus_in_stock / pri.number_of_skus) as skus_in_stock_p,
	ab.browses as browses, 
	count(distinct i.order_ref) as browse_conversions,
	(count(distinct i.order_ref) / ab.browses) as shop_to_purc_conv, 
	ac.adds_to_cart as adds_to_cart, 
	count(i.order_ref) as cart_conversions, 
	(count(i.order_ref) / ac.adds_to_cart) as cart_to_purc_conv
from dcspp_order o, 
	dcspp_item i, 
	drpt_products pri, 
	dcspp_amount_info ai,
	drpta_browses ab, 
	drpta_carts ac, 
	drpt_sku_stock si, 
	drpta_orders aot, 
	dcs_sku s, 
	drpta_male_25_39 apt, 
	dps_user u
where o.order_id = i.order_ref 
	and o.state != 'FAILED' 
	and o.state != 'REMOVED'
	and i.product_id = pri.product_id 
	and i.price_info = ai.amount_info_id 
	and i.product_id = ac.product_id 
	and i.product_id = ab.product_id 
	and ab.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
	and ac.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1')
	and si.product_id = i.product_id 
	and aot.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and apt.year = DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1') 
	and i.catalog_ref_id = s.sku_id 
	and o.profile_id = u.id 
	and u.gender = 2 
	and (o.submitted_date - u.date_of_birth) >= 250000 
	and (o.submitted_date - u.date_of_birth) < 390000
group by DATE(CAST(YEAR(o.submitted_date) AS CHAR(4))||'-1-1'), 
	i.product_id, 
	pri.category_name, 
	pri.avg_cost,
	pri.avg_list_price, 
	pri.avg_sale_price, 
	pri.avg_initial_markup, 
	pri.units_on_hand, 
	ab.browses, 
	ac.adds_to_cart, 
	aot.total_units_sold, 
	aot.merch_rev, 
	pri.number_of_skus, 
	si.skus_in_stock, 
	apt.total_units_sold, 	
	apt.total_dollar_sales
         ;


commit;

-- the source for this section is 
-- custom_catalog_reporting1.sql




--        new drptw_cat_sales calculates various statistics over each week on a per-category basis  new   
create view drptw_cat_sales
as
select wps.week as week, 
	cri.catalog_name as catalog_name, 
	wps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price,
	cri.avg_initial_markup as avg_initial_markup,
	sum(wps.units_sold) as units_sold, 
	sum(wps.total_rev) as total_rev,
	sum(wps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(wps.total_rev) - sum(wps.cost_of_goods_sold)) / sum(wps.total_rev)) as maintained_markup,
	(sum(wps.units_sold) / wot.total_units_sold) as total_units_sold_p,
	(sum(wps.total_rev) / wot.merch_rev) as total_rev_p, 
	cri.units_on_hand as units_on_hand,
	(cri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(wps.number_of_skus) as number_of_skus, 
	sum(wps.skus_in_stock) as skus_in_stock,
	(sum(wps.skus_in_stock) / sum(wps.number_of_skus)) as skus_in_stock_p,
	sum(wps.browses) as browses, 
	sum(wps.browse_conversions) as browse_conversions,
	(sum(wps.browse_conversions) / sum(wps.browses)) as shop_to_purc_conv,
	sum(wps.adds_to_cart) as adds_to_cart, 
	sum(wps.cart_conversions) as cart_conversions,
	(sum(wps.cart_conversions) / sum(wps.adds_to_cart)) as cart_to_purc_conv
from drptw_prod_sales wps, 
	drpt_category cri, 
	drptw_orders wot, 
	dcs_category c, 
	dcs_product_info pi, 
	dcs_prd_prdinfo ppi, 
	dcspp_item i
where wps.product_id = ppi.product_id 
	and i.product_id = ppi.product_id 
	and i.catalog_id = c.catalog_id
	and cri.category_id = c.category_id 
	and i.catalog_id = ppi.catalog_id
	and ppi.product_info_id = pi.product_info_id 
	and pi.parent_cat_id = cri.category_id 
	and wps.week = wot.week
group by wps.week, 
	cri.catalog_name, 
	wps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	wot.total_units_sold, 
	wot.merch_rev, 
	cri.units_on_hand
         ;


--        new drptm_cat_sales calculates various statistics over each month on a per-category basis    
create view drptm_cat_sales
as
select mps.month as month, 
	cri.catalog_name as catalog_name, 
	mps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(mps.units_sold) as units_sold, 
	sum(mps.total_rev) as total_rev,
	sum(mps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(mps.total_rev) - sum(mps.cost_of_goods_sold)) / sum(mps.total_rev)) as maintained_markup,
	(sum(mps.units_sold) / mot.total_units_sold) as total_units_sold_p,
	(sum(mps.total_rev) / mot.merch_rev) as total_rev_p, 
	cri.units_on_hand as units_on_hand,
	(cri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(mps.number_of_skus) as number_of_skus, 
	sum(mps.skus_in_stock) as skus_in_stock,
	(sum(mps.skus_in_stock) / sum(mps.number_of_skus)) as skus_in_stock_p,
	sum(mps.browses) as browses, 
	sum(mps.browse_conversions) as browse_conversions,
	(sum(mps.browse_conversions) / sum(mps.browses)) as shop_to_purc_conv,
	sum(mps.adds_to_cart) as adds_to_cart, 
	sum(mps.cart_conversions) as cart_conversions,
	(sum(mps.cart_conversions) / sum(mps.adds_to_cart)) as cart_to_purc_conv
from drptm_prod_sales mps, 
	drpt_category cri, 
	drptm_orders mot, 
	dcs_category c, 
	dcs_product_info pi, 
	dcs_prd_prdinfo ppi, 
	dcspp_item i
where mps.product_id = ppi.product_id 
	and i.product_id = ppi.product_id 
	and i.catalog_id = c.catalog_id 
	and cri.category_id = c.category_id 
	and i.catalog_id = ppi.catalog_id 
	and ppi.product_info_id = pi.product_info_id 
	and pi.parent_cat_id = cri.category_id 
	and mps.month = mot.month
group by mps.month, 
	cri.catalog_name, 
	mps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	mot.total_units_sold, 
	mot.merch_rev, 
	cri.units_on_hand
         ;


--        new drptq_cat_sales calculates various statistics over each quarter on a per-category basis   
create view drptq_cat_sales
as
select qps.quarter as quarter, 
	cri.catalog_name as catalog_name, 
	qps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(qps.units_sold) as units_sold, 
	sum(qps.total_rev) as total_rev,
	sum(qps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(qps.total_rev) - sum(qps.cost_of_goods_sold)) / sum(qps.total_rev)) as maintained_markup,
	(sum(qps.units_sold) / qot.total_units_sold) as total_units_sold_p,
	(sum(qps.total_rev) / qot.merch_rev) as total_rev_p, 
	cri.units_on_hand as units_on_hand,
	(cri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(qps.number_of_skus) as number_of_skus, 
	sum(qps.skus_in_stock) as skus_in_stock,
	(sum(qps.skus_in_stock) / sum(qps.number_of_skus)) as skus_in_stock_p,
	sum(qps.browses) as browses, 
	sum(qps.browse_conversions) as browse_conversions,
	(sum(qps.browse_conversions) / sum(qps.browses)) as shop_to_purc_conv,
	sum(qps.adds_to_cart) as adds_to_cart, 
	sum(qps.cart_conversions) as cart_conversions,
	(sum(qps.cart_conversions) / sum(qps.adds_to_cart)) as cart_to_purc_conv
from drptq_prod_sales qps, 
	drpt_category cri, 
	drptq_orders qot, 
	dcs_category c, 
	dcs_product_info pi, 
	dcs_prd_prdinfo ppi, 
	dcspp_item i
where qps.product_id = ppi.product_id 
	and i.product_id = ppi.product_id 
	and i.catalog_id = c.catalog_id 
	and cri.category_id = c.category_id 
	and i.catalog_id = ppi.catalog_id 
	and ppi.product_info_id = pi.product_info_id 
	and pi.parent_cat_id = cri.category_id 
	and qps.quarter = qot.quarter
group by qps.quarter, 
	cri.catalog_name, 
	qps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	qot.total_units_sold, 
	qot.merch_rev, 
	cri.units_on_hand
         ;


--        new drpta_cat_sales calculates various statistics over each year on a per-category basis   
create view drpta_cat_sales
as
select aps.year as year, 
	cri.catalog_name as catalog_name, 
	aps.category_name as category_name, 
	cri.avg_cost as avg_cost,
	cri.avg_list_price as avg_list_price, 
	cri.avg_sale_price as avg_sale_price, 
	cri.avg_initial_markup as avg_initial_markup,
	sum(aps.units_sold) as units_sold, 
	sum(aps.total_rev) as total_rev,
	sum(aps.cost_of_goods_sold) as cost_of_goods_sold,
	((sum(aps.total_rev) - sum(aps.cost_of_goods_sold)) / sum(aps.total_rev)) as maintained_markup,
	(sum(aps.units_sold) / aot.total_units_sold) as total_units_sold_p,
	(sum(aps.total_rev) / aot.merch_rev) as total_rev_p, 
	cri.units_on_hand as units_on_hand,
	(cri.units_on_hand / sum(i.quantity)) as weeks_on_hand,
	sum(aps.number_of_skus) as number_of_skus, 
	sum(aps.skus_in_stock) as skus_in_stock,
	(sum(aps.skus_in_stock) / sum(aps.number_of_skus)) as skus_in_stock_p,
	sum(aps.browses) as browses, 
	sum(aps.browse_conversions) as browse_conversions,
	(sum(aps.browse_conversions) / sum(aps.browses)) as shop_to_purc_conv,
	sum(aps.adds_to_cart) as adds_to_cart, 
	sum(aps.cart_conversions) as cart_conversions,
	(sum(aps.cart_conversions) / sum(aps.adds_to_cart)) as cart_to_purc_conv
from drpta_prod_sales aps, 
	drpt_category cri, 
	drpta_orders aot, 
	dcs_category c, 
	dcs_product_info pi, 
	dcs_prd_prdinfo ppi, 
	dcspp_item i
where aps.product_id = ppi.product_id 
	and i.product_id = ppi.product_id 
	and i.catalog_id = c.catalog_id 
	and cri.category_id = c.category_id 
	and i.catalog_id = ppi.catalog_id 
	and ppi.product_info_id = pi.product_info_id 
	and pi.parent_cat_id = cri.category_id 
	and aps.year = aot.year
group by aps.year, 
	cri.catalog_name, 
	aps.category_name, 
	cri.avg_cost, 
	cri.avg_list_price, 
	cri.avg_sale_price,
	cri.avg_initial_markup,
	aot.total_units_sold, 
	aot.merch_rev, 
	cri.units_on_hand
         ;


commit;

-- the source for this section is 
-- abandoned_order_ddl.sql




-- $Id: //product/DCS/version/10.0.3/templates/DCS/AbandonedOrderServices/sql/abandoned_order_ddl.xml#1 $

create table dcspp_ord_abandon (
	abandonment_id	varchar(40)	not null,
	version	integer	not null,
	order_id	varchar(40)	not null,
	ord_last_updated	timestamp	default null,
	abandon_state	varchar(40)	default null,
	abandonment_count	integer	default null,
	abandonment_date	timestamp	default null,
	reanimation_date	timestamp	default null,
	convert_date	timestamp	default null,
	lost_date	timestamp	default null
,constraint dcspp_ord_abndn_p primary key (abandonment_id));

create index dcspp_ordabandn1_x on dcspp_ord_abandon (order_id);

create table dcs_user_abandoned (
	id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	profile_id	varchar(40)	not null
,constraint dcs_usr_abndnd_p primary key (id));


create table drpt_conv_order (
	order_id	varchar(40)	not null,
	converted_date	timestamp	not null,
	amount	numeric(19,7)	not null,
	promo_count	integer	not null,
	promo_value	numeric(19,7)	not null
,constraint drpt_conv_order_p primary key (order_id));


create table drpt_session_ord (
	dataset_id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	date_time	timestamp	not null,
	amount	numeric(19,7)	not null,
	submitted	integer	not null,
	order_persistent	numeric(1)	default null,
	session_id	varchar(40)	default null,
	parent_session_id	varchar(40)	default null
,constraint drpt_session_ord_p primary key (order_id));

commit;



-- the source for this section is 
-- abandoned_order_views.sql




create view drpt_abandon_ord
as
      select oa.abandonment_date as abandonment_date, ai.amount as amount, case when oa.abandon_state = 'CONVERTED' then 100 else 0 end as converted from dcspp_order o, dcspp_ord_abandon oa, dcspp_amount_info ai where oa.order_id=o.order_id and o.price_info=ai.amount_info_id
         ;


create view drpt_tns_abndn_ord
as
      select date_time as abandonment_date, amount as amount from drpt_session_ord where submitted=0
         ;


commit;
