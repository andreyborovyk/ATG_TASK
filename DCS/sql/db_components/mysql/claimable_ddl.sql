


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/claimable_ddl.xml#2 $$Change: 651448 $

create table dcspp_claimable (
	claimable_id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	status	integer	null,
	expiration_date	datetime	null,
	last_modified	datetime	null
,constraint dcspp_claimable_p primary key (claimable_id));


create table dcspp_giftcert (
	giftcertificate_id	varchar(40)	not null,
	amount	numeric(19,7)	not null,
	amount_authorized	numeric(19,7)	not null,
	amount_remaining	numeric(19,7)	not null,
	purchaser_id	varchar(40)	null,
	purchase_date	datetime	null,
	last_used	datetime	null
,constraint dcspp_giftcert_p primary key (giftcertificate_id)
,constraint dcspp_gigiftcrtf_f foreign key (giftcertificate_id) references dcspp_claimable (claimable_id));

create index claimable_user_idx on dcspp_giftcert (purchaser_id);

create table dcs_storecred_clm (
	store_credit_id	varchar(40)	not null,
	amount	numeric(19,7)	not null,
	amount_authorized	numeric(19,7)	not null,
	amount_remaining	numeric(19,7)	not null,
	owner_id	varchar(40)	null,
	issue_date	datetime	null,
	expiration_date	datetime	null,
	last_used	datetime	null
,constraint dcs_storecred_cl_p primary key (store_credit_id)
,constraint dcs_storstor_crd_f foreign key (store_credit_id) references dcspp_claimable (claimable_id));

create index storecr_issue_idx on dcs_storecred_clm (issue_date);
create index storecr_owner_idx on dcs_storecred_clm (owner_id);

create table dcspp_cp_folder (
	folder_id	varchar(40)	not null,
	name	varchar(254)	not null,
	parent_folder	varchar(40)	null
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
	display_name	varchar(254)	null,
	use_promo_site	integer	null,
	parent_folder	varchar(40)	null
,constraint dcspp_copninfo_p primary key (coupon_id)
,constraint dcspp_copninfo_d_f foreign key (coupon_id) references dcspp_claimable (claimable_id)
,constraint dcspp_cpnifol_f foreign key (parent_folder) references dcspp_cp_folder (folder_id));

create index dcspp_folder_idx on dcspp_coupon_info (parent_folder);
commit;


