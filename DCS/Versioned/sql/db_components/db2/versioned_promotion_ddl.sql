


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/promotion_ddl.xml#3 $$Change: 655658 $
-- Add the DCS_PRM_FOLDER table, promotionFolder

create table dcs_prm_folder (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	default null
,constraint dcs_prm_folder_p primary key (folder_id,asset_version));

create index dcs_prm_folder_wsx on dcs_prm_folder (workspace_id);
create index dcs_prm_folder_cix on dcs_prm_folder (checkin_date);

create table dcs_promotion (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
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
,constraint dcs_promotion_p primary key (promotion_id,asset_version)
,constraint dcs_promotion1_c check (enabled in (0,1))
,constraint dcs_promotion2_c check (global in (0,1))
,constraint dcs_promotion3_c check (anon_profile in (0,1))
,constraint dcs_promotion4_c check (allow_multiple in (0,1))
,constraint dcs_promotion5_c check (rel_expiration in (0,1)));

create index prmo_bgin_use_idx on dcs_promotion (begin_usable);
create index prmo_end_dte_idx on dcs_promotion (end_date);
create index prmo_end_use_idx on dcs_promotion (end_usable);
create index prmo_strt_dte_idx on dcs_promotion (start_date);
create index dcs_promotion_wsx on dcs_promotion (workspace_id);
create index dcs_promotion_cix on dcs_promotion (checkin_date);

create table dcs_promo_media (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	media_id	varchar(40)	not null
,constraint dcs_promo_media_p primary key (promotion_id,tag,asset_version));

create index promo_mdia_pid_idx on dcs_promo_media (promotion_id);

create table dcs_discount_promo (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	calculator	varchar(254)	default null,
	adjuster	numeric(19,7)	default null,
	pmdl_rule	long varchar	not null,
	one_use	numeric(1,0)	default null
,constraint dcs_discount_pro_p primary key (promotion_id,asset_version)
,constraint dcs_discount_pro_c check (one_use in (0, 1)));


create table dcs_promo_upsell (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	upsell	numeric(1,0)	default null
,constraint dcs_promo_upsell_p primary key (promotion_id,asset_version)
,constraint dcs_promo_upsell_c check (upsell in (0, 1)));


create table dcs_upsell_action (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	action_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	upsell_prd_grp	long varchar	default null
,constraint dcs_upsell_actn_p primary key (action_id,asset_version));

create index dcs_upsell_act_wsx on dcs_upsell_action (workspace_id);
create index dcs_upsell_act_cix on dcs_upsell_action (checkin_date);

create table dcs_close_qualif (
	asset_version	numeric(19)	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	numeric(1)	not null,
	version_deleted	numeric(1)	not null,
	version_editable	numeric(1)	not null,
	pred_version	numeric(19)	default null,
	checkin_date	timestamp	default null,
	close_qualif_id	varchar(40)	not null,
	version	integer	not null,
	name	varchar(40)	not null,
	priority	integer	default null,
	qualifier	long varchar	default null,
	upsell_media	varchar(40)	default null,
	upsell_action	varchar(40)	default null
,constraint dcs_close_qualif_p primary key (close_qualif_id,asset_version));

create index dcs_close_qual_wsx on dcs_close_qualif (workspace_id);
create index dcs_close_qual_cix on dcs_close_qualif (checkin_date);

create table dcs_prm_cls_qlf (
	sec_asset_version	numeric(19)	not null,
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	closeness_qualif	varchar(40)	not null);


create table dcs_prm_sites (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	site_id	varchar(40)	not null);


create table dcs_prm_site_grps (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	site_group_id	varchar(40)	not null);


create table dcs_prm_tpl_vals (
	asset_version	numeric(19)	not null,
	promotion_id	varchar(40)	not null,
	placeholder	varchar(40)	not null,
	placeholderValue	long varchar	default null
,constraint dcs_prm_tpl_vals_p primary key (promotion_id,placeholder,asset_version));


create table dcs_prm_cls_vals (
	asset_version	numeric(19)	not null,
	close_qualif_id	varchar(40)	not null,
	placeholder	varchar(40)	not null,
	placeholderValue	long varchar	default null
,constraint dcs_prm_cls_vals_p primary key (close_qualif_id,placeholder,asset_version));


create table dcs_upsell_prods (
	asset_version	numeric(19)	not null,
	action_id	varchar(40)	not null,
	product_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcs_upsell_prods_p primary key (action_id,product_id,asset_version));

commit;


