
-- the source for this section is 
-- b2b_custom_catalog_ddl.sql





create table b2b_belt_sku (
	sku_id	varchar2(40)	not null,
	length	varchar2(254)	null,
	top_width	varchar2(254)	null,
	angle	varchar2(254)	null,
	notched	integer	null
,constraint b2b_belt_sku_p primary key (sku_id)
,constraint b2b_beltsksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_hose_sku (
	sku_id	varchar2(40)	not null,
	inner_diameter	varchar2(254)	null,
	length	varchar2(254)	null
,constraint b2b_hose_sku_p primary key (sku_id)
,constraint b2b_hossksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_sprkplg_sku (
	sku_id	varchar2(40)	not null,
	plug_number	varchar2(254)	null,
	thread	varchar2(254)	null
,constraint b2b_sprkplg_sku_p primary key (sku_id)
,constraint b2b_sprksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_oilfltr_sku (
	sku_id	varchar2(40)	not null,
	thread_type	varchar2(254)	null,
	length	varchar2(254)	null,
	outer_diameter	varchar2(254)	null
,constraint b2b_oilfltr_sku_p primary key (sku_id)
,constraint b2b_olflsku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_cylinder_sku (
	sku_id	varchar2(40)	not null,
	diameter	varchar2(254)	null,
	height	varchar2(254)	null
,constraint b2b_cylinder_sku_p primary key (sku_id)
,constraint b2b_cylisku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_cube_sku (
	sku_id	varchar2(40)	not null,
	height	varchar2(254)	null,
	width	varchar2(254)	null,
	depth	varchar2(254)	null
,constraint b2b_cube_sku_p primary key (sku_id)
,constraint b2b_cubsksku_d_f foreign key (sku_id) references dcs_sku (sku_id));





-- the source for this section is 
-- german_catalog_ddl.sql





create table dbc_catalog_de (
	catalog_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_catalog_de_p primary key (catalog_id));


create table dbc_category_de (
	category_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	template_id	varchar2(40)	null
,constraint dbc_category_de_p primary key (category_id));


create table dbc_product_de (
	product_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	admin_display	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_product_de_p primary key (product_id));


create table dbc_sku_de (
	sku_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_sku_de_p primary key (sku_id));


create table dbc_sku_link_de (
	sku_link_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_sku_link_de_p primary key (sku_link_id));


create table dbc_config_prop_de (
	config_prop_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_config_prop__p primary key (config_prop_id));


create table dbc_config_opt_de (
	config_opt_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	price	number(19,7)	null
,constraint dbc_config_opt_d_p primary key (config_opt_id));


create table dbc_cat_key_de (
	category_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_cat_key_de_p primary key (category_id,sequence_num));


create table dbc_prd_key_de (
	product_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_prd_key_de_p primary key (product_id,sequence_num));


create table dbc_promotion_de (
	promotion_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_promotion_de_p primary key (promotion_id));





-- the source for this section is 
-- japanese_catalog_ddl.sql





create table dbc_catalog_ja (
	catalog_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_catalog_ja_p primary key (catalog_id));


create table dbc_category_ja (
	category_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	template_id	varchar2(40)	null
,constraint dbc_category_ja_p primary key (category_id));


create table dbc_product_ja (
	product_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	admin_display	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_product_ja_p primary key (product_id));


create table dbc_sku_ja (
	sku_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	template_id	varchar2(40)	null
,constraint dbc_sku_ja_p primary key (sku_id));


create table dbc_sku_link_ja (
	sku_link_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_sku_link_ja_p primary key (sku_link_id));


create table dbc_config_prop_ja (
	config_prop_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null
,constraint dbc_configpropja_p primary key (config_prop_id));


create table dbc_config_opt_ja (
	config_opt_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	price	number(19,7)	null
,constraint dbc_config_optja_p primary key (config_opt_id));


create table dbc_cat_key_ja (
	category_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_cat_key_ja_p primary key (category_id,sequence_num));


create table dbc_prd_key_ja (
	product_id	varchar2(40)	not null,
	sequence_num	integer	not null,
	keyword	varchar2(254)	not null
,constraint dbc_prd_key_ja_p primary key (product_id,sequence_num));


create table dbc_promotion_ja (
	promotion_id	varchar2(40)	not null,
	display_name	varchar2(254)	null
,constraint dbc_promotion_ja_p primary key (promotion_id));





-- the source for this section is 
-- b2b_user_orddet_ddl.sql





create table b2b_user_info (
	id	varchar2(40)	not null,
	num_orders	integer	null,
	avg_order_amt	number(19,7)	null,
	use_org_approver	number(1,0)	null,
	use_org_costctr	number(1,0)	null,
	use_org_billaddr	number(1,0)	null,
	use_org_shipaddr	number(1,0)	null,
	use_org_payment	number(1,0)	null,
	use_org_vendors	number(1,0)	null,
	use_org_purchlst	number(1,0)	null
,constraint b2b_user_info_p primary key (id)
,constraint b2b_usrnfid_f foreign key (id) references dps_user (id)
,constraint b2b_user_info1_c check (use_org_approver in (0,1))
,constraint b2b_user_info2_c check (use_org_costctr in (0,1))
,constraint b2b_user_info3_c check (use_org_billaddr in (0,1))
,constraint b2b_user_info4_c check (use_org_shipaddr in (0,1))
,constraint b2b_user_info5_c check (use_org_payment in (0,1))
,constraint b2b_user_info6_c check (use_org_vendors in (0,1))
,constraint b2b_user_info7_c check (use_org_purchlst in (0,1)));


create table b2b_org_info (
	org_id	varchar2(40)	not null,
	logo	varchar2(40)	null,
	cc_auth	number(1,0)	null,
	invoice_auth	number(1,0)	null,
	store_crdt_auth	number(1,0)	null,
	gift_crt_auth	number(1,0)	null,
	use_prnt_approver	number(1,0)	null,
	use_prnt_costctr	number(1,0)	null,
	use_prnt_billaddr	number(1,0)	null,
	use_prnt_shipaddr	number(1,0)	null,
	use_prnt_payment	number(1,0)	null,
	use_prnt_vendors	number(1,0)	null,
	use_prnt_purchlst	number(1,0)	null
,constraint b2b_org_info_p primary key (org_id)
,constraint b2b_orgnforg_d_f foreign key (org_id) references dps_organization (org_id)
,constraint b2b_org_info1_c check (cc_auth in (0,1))
,constraint b2b_org_info2_c check (invoice_auth in (0,1))
,constraint b2b_org_info3_c check (store_crdt_auth in (0,1))
,constraint b2b_org_info4_c check (gift_crt_auth in (0,1))
,constraint b2b_org_info5_c check (use_prnt_approver in (0,1))
,constraint b2b_org_info6_c check (use_prnt_costctr in (0,1))
,constraint b2b_org_info7_c check (use_prnt_billaddr in (0,1))
,constraint b2b_org_info8_c check (use_prnt_shipaddr in (0,1))
,constraint b2b_org_info9_c check (use_prnt_payment in (0,1))
,constraint b2b_org_infoa_c check (use_prnt_vendors in (0,1))
,constraint b2b_org_infob_c check (use_prnt_purchlst in (0,1)));





-- the source for this section is 
-- b2b_auth_cc_ddl.sql





create table b2b_auth_pmnt (
	id	varchar2(40)	not null,
	cc_auth	number(1,0)	null,
	invoice_auth	number(1,0)	null,
	store_crdt_auth	number(1,0)	null,
	gift_crt_auth	number(1,0)	null
,constraint b2b_auth_pmnt_p primary key (id)
,constraint b2b_athpmntid_f foreign key (id) references dps_user (id)
,constraint b2b_auth_pmnt1_c check (cc_auth in (0,1))
,constraint b2b_auth_pmnt2_c check (invoice_auth in (0,1))
,constraint b2b_auth_pmnt3_c check (store_crdt_auth in (0,1))
,constraint b2b_auth_pmnt4_c check (gift_crt_auth in (0,1)));


create table b2b_credit_card (
	id	varchar2(40)	not null,
	cc_first_name	varchar2(40)	null,
	cc_middle_name	varchar2(40)	null,
	cc_last_name	varchar2(40)	null
,constraint b2b_credit_card_p primary key (id)
,constraint b2b_credtcrdid_f foreign key (id) references dps_credit_card (id));

create index b2b_crcdba_idx on b2b_credit_card (cc_last_name);



