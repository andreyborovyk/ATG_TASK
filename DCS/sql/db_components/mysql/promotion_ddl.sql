


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/promotion_ddl.xml#3 $$Change: 655658 $
-- Add the DCS_PRM_FOLDER table, promotionFolder

create table dcs_prm_folder (
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	null
,constraint dcs_prm_folder_p primary key (folder_id)
,constraint dcs_prm_prntfol_f foreign key (parent_folder) references dcs_prm_folder (folder_id));

create index prm_prntfol_idx on dcs_prm_folder (parent_folder);

create table dcs_promotion (
	promotion_id	varchar(40)	not null,
	version	integer	not null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	promotion_type	integer	null,
	enabled	tinyint	null,
	begin_usable	datetime	null,
	end_usable	datetime	null,
	priority	integer	null,
	global	tinyint	null,
	anon_profile	tinyint	null,
	allow_multiple	tinyint	null,
	uses	integer	null,
	rel_expiration	tinyint	null,
	time_until_expire	integer	null,
	template	varchar(254)	null,
	ui_access_lvl	integer	null,
	parent_folder	varchar(40)	null,
	last_modified	datetime	null,
	pmdl_version	integer	null,
	qualifier	varchar(254)	null
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
	tag	nvarchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_promo_media_p primary key (promotion_id,tag)
,constraint dcs_prommdmed_d_f foreign key (media_id) references dcs_media (media_id)
,constraint dcs_prompromtn_d_f foreign key (promotion_id) references dcs_promotion (promotion_id));

create index promo_mdia_mid_idx on dcs_promo_media (media_id);
create index promo_mdia_pid_idx on dcs_promo_media (promotion_id);

create table dcs_discount_promo (
	promotion_id	varchar(40)	not null,
	calculator	varchar(254)	null,
	adjuster	numeric(19,7)	null,
	pmdl_rule	longtext charset utf8	not null,
	one_use	tinyint	null
,constraint dcs_discount_pro_p primary key (promotion_id)
,constraint dcs_discpromtn_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_discount_pro_c check (one_use in (0, 1)));


create table dcs_promo_upsell (
	promotion_id	varchar(40)	not null,
	upsell	tinyint	null
,constraint dcs_promo_upsell_p primary key (promotion_id)
,constraint dcs_proupsell_d_f foreign key (promotion_id) references dcs_promotion (promotion_id)
,constraint dcs_promo_upsell_c check (upsell in (0, 1)));


create table dcs_upsell_action (
	action_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	upsell_prd_grp	longtext	null
,constraint dcs_upsell_actn_p primary key (action_id));


create table dcs_close_qualif (
	close_qualif_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	priority	integer	null,
	qualifier	longtext charset utf8	null,
	upsell_media	varchar(40)	null,
	upsell_action	varchar(40)	null
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
	placeholderValue	longtext	null
,constraint dcs_prm_tpl_vals_p primary key (promotion_id,placeholder)
,constraint dcs_prmtplvals_d_f foreign key (promotion_id) references dcs_promotion (promotion_id));


create table dcs_prm_cls_vals (
	close_qualif_id	varchar(40)	not null,
	placeholder	varchar(40)	not null,
	placeholderValue	longtext	null
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


