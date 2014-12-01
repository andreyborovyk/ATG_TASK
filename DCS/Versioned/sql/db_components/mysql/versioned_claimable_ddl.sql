


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/claimable_ddl.xml#2 $$Change: 651448 $

create table dcspp_claimable (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	claimable_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	status	integer	null,
	expiration_date	datetime	null,
	last_modified	datetime	null
,constraint dcspp_claimable_p primary key (claimable_id,asset_version));

create index dcspp_claimabl_wsx on dcspp_claimable (workspace_id);
create index dcspp_claimabl_cix on dcspp_claimable (checkin_date);

create table dcspp_giftcert (
	asset_version	bigint	not null,
	giftcertificate_id	varchar(40)	not null,
	amount	numeric(19,7)	not null,
	amount_authorized	numeric(19,7)	not null,
	amount_remaining	numeric(19,7)	not null,
	purchaser_id	varchar(40)	null,
	purchase_date	datetime	null,
	last_used	datetime	null
,constraint dcspp_giftcert_p primary key (giftcertificate_id,asset_version));

create index claimable_user_idx on dcspp_giftcert (purchaser_id);

create table dcs_storecred_clm (
	asset_version	bigint	not null,
	store_credit_id	varchar(40)	not null,
	amount	numeric(19,7)	not null,
	amount_authorized	numeric(19,7)	not null,
	amount_remaining	numeric(19,7)	not null,
	owner_id	varchar(40)	null,
	issue_date	datetime	null,
	expiration_date	datetime	null,
	last_used	datetime	null
,constraint dcs_storecred_cl_p primary key (store_credit_id,asset_version));

create index storecr_issue_idx on dcs_storecred_clm (issue_date);
create index storecr_owner_idx on dcs_storecred_clm (owner_id);

create table dcspp_cp_folder (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	null
,constraint dcspp_cp_folder_p primary key (folder_id,asset_version));

create index dcspp_cp_folde_wsx on dcspp_cp_folder (workspace_id);
create index dcspp_cp_folde_cix on dcspp_cp_folder (checkin_date);

create table dcspp_coupon (
	asset_version	bigint	not null,
	coupon_id	varchar(40)	not null,
	promotion_id	varchar(40)	not null
,constraint dcspp_coupon_p primary key (coupon_id,promotion_id,asset_version));


create table dcspp_coupon_info (
	asset_version	bigint	not null,
	coupon_id	varchar(40)	not null,
	display_name	varchar(254)	null,
	use_promo_site	integer	null,
	parent_folder	varchar(40)	null
,constraint dcspp_copninfo_p primary key (coupon_id,asset_version));

commit;


