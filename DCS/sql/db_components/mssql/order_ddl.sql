


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/order_ddl.xml#2 $$Change: 651448 $

create table dcspp_order (
	order_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	order_class_type	varchar(40)	null,
	profile_id	varchar(40)	null,
	description	varchar(64)	null,
	state	varchar(40)	null,
	state_detail	varchar(254)	null,
	created_by_order	varchar(40)	null,
	origin_of_order	integer	null,
	creation_date	datetime	null,
	submitted_date	datetime	null,
	last_modified_date	datetime	null,
	completed_date	datetime	null,
	price_info	varchar(40)	null,
	tax_price_info	varchar(40)	null,
	explicitly_saved	numeric(1,0)	null,
	agent_id	varchar(40)	null,
	sales_channel	integer	null,
	creation_site_id	varchar(40)	null,
	site_id	varchar(40)	null
,constraint dcspp_order_p primary key (order_id)
,constraint dcspp_order_c check (explicitly_saved IN (0,1)))

create index order_lastmod_idx on dcspp_order (last_modified_date)
create index order_profile_idx on dcspp_order (profile_id)
create index order_submit_idx on dcspp_order (submitted_date)
create index ord_creat_site_idx on dcspp_order (creation_site_id)
create index ord_site_idx on dcspp_order (site_id)

create table dcspp_ship_group (
	shipping_group_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	shipgrp_class_type	varchar(40)	null,
	shipping_method	varchar(40)	null,
	description	varchar(64)	null,
	ship_on_date	datetime	null,
	actual_ship_date	datetime	null,
	state	varchar(40)	null,
	state_detail	varchar(254)	null,
	submitted_date	datetime	null,
	price_info	varchar(40)	null,
	order_ref	varchar(40)	null
,constraint dcspp_ship_group_p primary key (shipping_group_id))


create table dcspp_pay_group (
	payment_group_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	paygrp_class_type	varchar(40)	null,
	payment_method	varchar(40)	null,
	amount	numeric(19,7)	null,
	amount_authorized	numeric(19,7)	null,
	amount_debited	numeric(19,7)	null,
	amount_credited	numeric(19,7)	null,
	currency_code	varchar(10)	null,
	state	varchar(40)	null,
	state_detail	varchar(254)	null,
	submitted_date	datetime	null,
	order_ref	varchar(40)	null
,constraint dcspp_pay_group_p primary key (payment_group_id))


create table dcspp_item (
	commerce_item_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	item_class_type	varchar(40)	null,
	catalog_id	varchar(40)	null,
	catalog_ref_id	varchar(40)	null,
	catalog_key	varchar(40)	null,
	product_id	varchar(40)	null,
	site_id	varchar(40)	null,
	quantity	numeric(19,0)	null,
	state	varchar(40)	null,
	state_detail	varchar(254)	null,
	price_info	varchar(40)	null,
	order_ref	varchar(40)	null
,constraint dcspp_item_p primary key (commerce_item_id))

create index item_catref_idx on dcspp_item (catalog_ref_id)
create index item_prodref_idx on dcspp_item (product_id)
create index item_site_idx on dcspp_item (site_id)

create table dcspp_relationship (
	relationship_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	rel_class_type	varchar(40)	null,
	relationship_type	varchar(40)	null,
	order_ref	varchar(40)	null
,constraint dcspp_relationsh_p primary key (relationship_id))


create table dcspp_rel_orders (
	order_id	varchar(40)	not null,
	related_orders	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_rel_orders_p primary key (order_id,sequence_num)
,constraint dcspp_reordr_d_f foreign key (order_id) references dcspp_order (order_id))


create table dcspp_order_inst (
	order_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	null
,constraint dcspp_order_inst_p primary key (order_id,tag)
,constraint dcspp_orordr_d_f foreign key (order_id) references dcspp_order (order_id))

create index order_inst_oid_idx on dcspp_order_inst (order_id)

create table dcspp_order_sg (
	order_id	varchar(40)	not null,
	shipping_groups	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_sg_p primary key (order_id,sequence_num)
,constraint dcspp_sgordr_d_f foreign key (order_id) references dcspp_order (order_id))

create index order_sg_ordid_idx on dcspp_order_sg (order_id)

create table dcspp_order_pg (
	order_id	varchar(40)	not null,
	payment_groups	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_pg_p primary key (order_id,sequence_num)
,constraint dcspp_orpgordr_f foreign key (order_id) references dcspp_order (order_id))

create index order_pg_ordid_idx on dcspp_order_pg (order_id)

create table dcspp_order_item (
	order_id	varchar(40)	not null,
	commerce_items	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_item_p primary key (order_id,sequence_num)
,constraint dcspp_oritordr_d_f foreign key (order_id) references dcspp_order (order_id))

create index order_item_oid_idx on dcspp_order_item (order_id)
create index order_item_cit_idx on dcspp_order_item (commerce_items)

create table dcspp_order_rel (
	order_id	varchar(40)	not null,
	relationships	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_rel_p primary key (order_id,sequence_num)
,constraint dcspp_orlordr_d_f foreign key (order_id) references dcspp_order (order_id))

create index order_rel_orid_idx on dcspp_order_rel (order_id)

create table dcspp_ship_inst (
	shipping_group_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	null
,constraint dcspp_ship_inst_p primary key (shipping_group_id,tag)
,constraint dcspp_shshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id))

create index ship_inst_sgid_idx on dcspp_ship_inst (shipping_group_id)

create table dcspp_hrd_ship_grp (
	shipping_group_id	varchar(40)	not null,
	tracking_number	varchar(40)	null
,constraint dcspp_hrd_ship_g_p primary key (shipping_group_id)
,constraint dcspp_hrshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id))


create table dcspp_ele_ship_grp (
	shipping_group_id	varchar(40)	not null,
	email_address	varchar(255)	null
,constraint dcspp_ele_ship_g_p primary key (shipping_group_id)
,constraint dcspp_elshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id))


create table dcspp_ship_addr (
	shipping_group_id	varchar(40)	not null,
	prefix	nvarchar(40)	null,
	first_name	nvarchar(40)	null,
	middle_name	nvarchar(40)	null,
	last_name	nvarchar(40)	null,
	suffix	nvarchar(40)	null,
	job_title	nvarchar(40)	null,
	company_name	nvarchar(40)	null,
	address_1	nvarchar(50)	null,
	address_2	nvarchar(50)	null,
	address_3	nvarchar(50)	null,
	city	nvarchar(40)	null,
	county	nvarchar(40)	null,
	state	nvarchar(40)	null,
	postal_code	nvarchar(10)	null,
	country	nvarchar(40)	null,
	phone_number	nvarchar(40)	null,
	fax_number	nvarchar(40)	null,
	email	nvarchar(255)	null
,constraint dcspp_ship_addr_p primary key (shipping_group_id)
,constraint dcspp_shdshippng_f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id))


create table dcspp_hand_inst (
	handling_inst_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	hndinst_class_type	varchar(40)	null,
	handling_method	varchar(40)	null,
	shipping_group_id	varchar(40)	null,
	commerce_item_id	varchar(40)	null,
	quantity	integer	null
,constraint dcspp_hand_inst_p primary key (handling_inst_id))

create index hi_item_idx on dcspp_hand_inst (commerce_item_id)
create index hi_ship_group_idx on dcspp_hand_inst (shipping_group_id)

create table dcspp_gift_inst (
	handling_inst_id	varchar(40)	not null,
	giftlist_id	varchar(40)	null,
	giftlist_item_id	varchar(40)	null
,constraint dcspp_gift_inst_p primary key (handling_inst_id)
,constraint dcspp_gihandlng__f foreign key (handling_inst_id) references dcspp_hand_inst (handling_inst_id))


create table dcspp_sg_hand_inst (
	shipping_group_id	varchar(40)	not null,
	handling_instrs	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_sg_hand_in_p primary key (shipping_group_id,sequence_num)
,constraint dcspp_sgshippng__f foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id))

create index sg_hnd_ins_sgi_idx on dcspp_sg_hand_inst (shipping_group_id)

create table dcspp_pay_inst (
	payment_group_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	special_inst	varchar(254)	null
,constraint dcspp_pay_inst_p primary key (payment_group_id,tag)
,constraint dcspp_papaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index pay_inst_pgrid_idx on dcspp_pay_inst (payment_group_id)

create table dcspp_config_item (
	config_item_id	varchar(40)	not null,
	reconfig_data	varchar(255)	null,
	notes	varchar(255)	null
,constraint dcspp_config_ite_p primary key (config_item_id)
,constraint dcspp_coconfg_tm_f foreign key (config_item_id) references dcspp_item (commerce_item_id))


create table dcspp_subsku_item (
	subsku_item_id	varchar(40)	not null,
	ind_quantity	integer	null
,constraint dcspp_subsku_ite_p primary key (subsku_item_id)
,constraint dcspp_susubsk_tm_f foreign key (subsku_item_id) references dcspp_item (commerce_item_id))


create table dcspp_item_ci (
	commerce_item_id	varchar(40)	not null,
	commerce_items	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_item_ci_p primary key (commerce_item_id,sequence_num)
,constraint dcspp_itcommrc_t_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id))


create table dcspp_gift_cert (
	payment_group_id	varchar(40)	not null,
	profile_id	varchar(40)	null,
	gift_cert_number	varchar(50)	null
,constraint dcspp_gift_cert_p primary key (payment_group_id)
,constraint dcspp_gipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index gc_number_idx on dcspp_gift_cert (gift_cert_number)
create index gc_profile_idx on dcspp_gift_cert (profile_id)

create table dcspp_store_cred (
	payment_group_id	varchar(40)	not null,
	profile_id	varchar(40)	null,
	store_cred_number	varchar(50)	null
,constraint dcspp_store_cred_p primary key (payment_group_id)
,constraint dcspp_stpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index sc_number_idx on dcspp_store_cred (store_cred_number)
create index sc_profile_idx on dcspp_store_cred (profile_id)

create table dcspp_credit_card (
	payment_group_id	varchar(40)	not null,
	credit_card_number	varchar(40)	null,
	credit_card_type	varchar(40)	null,
	expiration_month	varchar(20)	null,
	exp_day_of_month	varchar(20)	null,
	expiration_year	varchar(20)	null
,constraint dcspp_credit_car_p primary key (payment_group_id)
,constraint dcspp_crpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))


create table dcspp_bill_addr (
	payment_group_id	varchar(40)	not null,
	prefix	nvarchar(40)	null,
	first_name	nvarchar(40)	null,
	middle_name	nvarchar(40)	null,
	last_name	nvarchar(40)	null,
	suffix	nvarchar(40)	null,
	job_title	nvarchar(40)	null,
	company_name	nvarchar(40)	null,
	address_1	nvarchar(50)	null,
	address_2	nvarchar(50)	null,
	address_3	nvarchar(50)	null,
	city	nvarchar(40)	null,
	county	nvarchar(40)	null,
	state	nvarchar(40)	null,
	postal_code	nvarchar(10)	null,
	country	nvarchar(40)	null,
	phone_number	nvarchar(40)	null,
	fax_number	nvarchar(40)	null,
	email	nvarchar(255)	null
,constraint dcspp_bill_addr_p primary key (payment_group_id)
,constraint dcspp_bipaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))


create table dcspp_pay_status (
	status_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	trans_id	varchar(50)	null,
	amount	numeric(19,7)	null,
	trans_success	numeric(1,0)	null,
	error_message	varchar(254)	null,
	trans_timestamp	datetime	null
,constraint dcspp_pay_status_p primary key (status_id)
,constraint dcspp_pay_status_c check (trans_success IN (0,1)))


create table dcspp_cc_status (
	status_id	varchar(40)	not null,
	auth_expiration	datetime	null,
	avs_code	varchar(40)	null,
	avs_desc_result	varchar(254)	null,
	integration_data	varchar(256)	null
,constraint dcspp_cc_status_p primary key (status_id)
,constraint dcspp_ccstats_d_f foreign key (status_id) references dcspp_pay_status (status_id))


create table dcspp_gc_status (
	status_id	varchar(40)	not null,
	auth_expiration	datetime	null
,constraint dcspp_gc_status_p primary key (status_id)
,constraint dcspp_gcstats_d_f foreign key (status_id) references dcspp_pay_status (status_id))


create table dcspp_sc_status (
	status_id	varchar(40)	not null,
	auth_expiration	datetime	null
,constraint dcspp_sc_status_p primary key (status_id)
,constraint dcspp_scstats_d_f foreign key (status_id) references dcspp_pay_status (status_id))


create table dcspp_auth_status (
	payment_group_id	varchar(40)	not null,
	auth_status	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_auth_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_atpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index auth_stat_pgid_idx on dcspp_auth_status (payment_group_id)

create table dcspp_debit_status (
	payment_group_id	varchar(40)	not null,
	debit_status	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_debit_stat_p primary key (payment_group_id,sequence_num)
,constraint dcspp_depaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index debit_stat_pgi_idx on dcspp_debit_status (payment_group_id)

create table dcspp_cred_status (
	payment_group_id	varchar(40)	not null,
	credit_status	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_cred_statu_p primary key (payment_group_id,sequence_num)
,constraint dcspp_crpaymntgr_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id))

create index cred_stat_pgid_idx on dcspp_cred_status (payment_group_id)

create table dcspp_shipitem_rel (
	relationship_id	varchar(40)	not null,
	shipping_group_id	varchar(40)	null,
	commerce_item_id	varchar(40)	null,
	quantity	numeric(19,0)	null,
	returned_qty	numeric(19,0)	null,
	amount	numeric(19,7)	null,
	state	varchar(40)	null,
	state_detail	varchar(254)	null
,constraint dcspp_shipitem_r_p primary key (relationship_id)
,constraint dcspp_shreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id))

create index sirel_item_idx on dcspp_shipitem_rel (commerce_item_id)
create index sirel_shipgrp_idx on dcspp_shipitem_rel (shipping_group_id)

create table dcspp_rel_range (
	relationship_id	varchar(40)	not null,
	low_bound	integer	null,
	high_bound	integer	null
,constraint dcspp_rel_range_p primary key (relationship_id))


create table dcspp_payitem_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	null,
	commerce_item_id	varchar(40)	null,
	quantity	numeric(19,0)	null,
	amount	numeric(19,7)	null
,constraint dcspp_payitem_re_p primary key (relationship_id)
,constraint dcspp_pareltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id))

create index pirel_item_idx on dcspp_payitem_rel (commerce_item_id)
create index pirel_paygrp_idx on dcspp_payitem_rel (payment_group_id)

create table dcspp_payship_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	null,
	shipping_group_id	varchar(40)	null,
	amount	numeric(19,7)	null
,constraint dcspp_payship_re_p primary key (relationship_id)
,constraint dcspp_pshrltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id))

create index psrel_paygrp_idx on dcspp_payship_rel (payment_group_id)
create index psrel_shipgrp_idx on dcspp_payship_rel (shipping_group_id)

create table dcspp_payorder_rel (
	relationship_id	varchar(40)	not null,
	payment_group_id	varchar(40)	null,
	order_id	varchar(40)	null,
	amount	numeric(19,7)	null
,constraint dcspp_payorder_r_p primary key (relationship_id)
,constraint dcspp_odreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id))

create index porel_order_idx on dcspp_payorder_rel (order_id)
create index porel_paygrp_idx on dcspp_payorder_rel (payment_group_id)

create table dcspp_amount_info (
	amount_info_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	currency_code	varchar(10)	null,
	amount	numeric(19,7)	null,
	discounted	numeric(1,0)	null,
	amount_is_final	numeric(1,0)	null,
	final_rc	integer	null
,constraint dcspp_amount_inf_p primary key (amount_info_id)
,constraint dcspp_amount_in1_c check (discounted IN (0,1))
,constraint dcspp_amount_in2_c check (amount_is_final IN (0,1)))


create table dcspp_order_price (
	amount_info_id	varchar(40)	not null,
	raw_subtotal	numeric(19,7)	null,
	tax	numeric(19,7)	null,
	shipping	numeric(19,7)	null,
	manual_adj_total	numeric(19,7)	null
,constraint dcspp_order_pric_p primary key (amount_info_id)
,constraint dcspp_oramnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))


create table dcspp_item_price (
	amount_info_id	varchar(40)	not null,
	list_price	numeric(19,7)	null,
	raw_total_price	numeric(19,7)	null,
	sale_price	numeric(19,7)	null,
	on_sale	numeric(1,0)	null,
	order_discount	numeric(19,7)	null,
	qty_discounted	numeric(19,0)	null,
	qty_as_qualifier	numeric(19,0)	null,
	price_list	varchar(40)	null
,constraint dcspp_item_price_p primary key (amount_info_id)
,constraint dcspp_itamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id)
,constraint dcspp_item_price_c check (on_sale IN (0,1)))


create table dcspp_tax_price (
	amount_info_id	varchar(40)	not null,
	city_tax	numeric(19,7)	null,
	county_tax	numeric(19,7)	null,
	state_tax	numeric(19,7)	null,
	country_tax	numeric(19,7)	null
,constraint dcspp_tax_price_p primary key (amount_info_id)
,constraint dcspp_taamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))


create table dcspp_ship_price (
	amount_info_id	varchar(40)	not null,
	raw_shipping	numeric(19,7)	null
,constraint dcspp_ship_price_p primary key (amount_info_id)
,constraint dcspp_shamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))


create table dcspp_amtinfo_adj (
	amount_info_id	varchar(40)	not null,
	adjustments	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_amtinfo_ad_p primary key (amount_info_id,sequence_num)
,constraint dcspp_amamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index amtinf_adj_aid_idx on dcspp_amtinfo_adj (amount_info_id)
create index amtinf_adj_adj_idx on dcspp_amtinfo_adj (adjustments)

create table dcspp_price_adjust (
	adjustment_id	varchar(40)	not null,
	version	integer	not null,
	adj_description	varchar(254)	null,
	pricing_model	varchar(40)	null,
	manual_adjustment	varchar(40)	null,
	adjustment	numeric(19,7)	null,
	qty_adjusted	integer	null
,constraint dcspp_price_adju_p primary key (adjustment_id))


create table dcspp_shipitem_sub (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	ship_item_subtotal	varchar(40)	not null
,constraint dcspp_shipitem_s_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_sbamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index ship_item_sub_idx on dcspp_shipitem_sub (amount_info_id)

create table dcspp_taxshipitem (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	tax_ship_item_sub	varchar(40)	not null
,constraint dcspp_taxshipite_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_shamntxnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index tax_ship_item_idx on dcspp_taxshipitem (amount_info_id)

create table dcspp_ntaxshipitem (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	non_tax_item_sub	varchar(40)	not null
,constraint dcspp_ntaxshipit_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_ntamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index ntax_ship_item_idx on dcspp_ntaxshipitem (amount_info_id)

create table dcspp_shipitem_tax (
	amount_info_id	varchar(40)	not null,
	shipping_group_id	varchar(42)	not null,
	ship_item_tax	varchar(40)	not null
,constraint dcspp_shipitem_t_p primary key (amount_info_id,shipping_group_id)
,constraint dcspp_txamnt_nfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index ship_item_tax_idx on dcspp_shipitem_tax (amount_info_id)

create table dcspp_itmprice_det (
	amount_info_id	varchar(40)	not null,
	cur_price_details	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_itmprice_d_p primary key (amount_info_id,sequence_num)
,constraint dcspp_sbamntnfd_f foreign key (amount_info_id) references dcspp_amount_info (amount_info_id))

create index itmprc_det_aii_idx on dcspp_itmprice_det (amount_info_id)

create table dcspp_det_price (
	amount_info_id	varchar(40)	not null,
	tax	numeric(19,7)	null,
	order_discount	numeric(19,7)	null,
	order_manual_adj	numeric(19,7)	null,
	quantity	numeric(19,0)	null,
	qty_as_qualifier	numeric(19,0)	null
,constraint dcspp_det_price_p primary key (amount_info_id))


create table dcspp_det_range (
	amount_info_id	varchar(40)	not null,
	low_bound	integer	null,
	high_bound	integer	null
,constraint dcspp_det_range_p primary key (amount_info_id))


create table dcspp_order_adj (
	order_id	varchar(40)	not null,
	adjustment_id	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dcspp_order_adj_p primary key (order_id,sequence_num)
,constraint dcspp_oradj_d_f foreign key (order_id) references dcspp_order (order_id))

create index order_adj_orid_idx on dcspp_order_adj (order_id)

create table dcspp_manual_adj (
	manual_adjust_id	varchar(40)	not null,
	type	integer	not null,
	adjustment_type	integer	not null,
	reason	integer	not null,
	amount	numeric(19,7)	null,
	notes	varchar(255)	null,
	version	integer	not null
,constraint dcspp_manual_adj_p primary key (manual_adjust_id))


create table dbcpp_sched_order (
	scheduled_order_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	name	varchar(32)	null,
	profile_id	varchar(40)	null,
	create_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	template_order	varchar(32)	null,
	state	integer	null,
	next_scheduled	datetime	null,
	schedule	varchar(255)	null,
	site_id	varchar(40)	null
,constraint dbcpp_sched_orde_p primary key (scheduled_order_id))

create index sched_tmplt_idx on dbcpp_sched_order (template_order)
create index sched_profile_idx on dbcpp_sched_order (profile_id)
create index sched_site_idx on dbcpp_sched_order (site_id)

create table dbcpp_sched_clone (
	scheduled_order_id	varchar(40)	not null,
	cloned_order	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_sched_clon_p primary key (scheduled_order_id,sequence_num)
,constraint dbcpp_scschedld__f foreign key (scheduled_order_id) references dbcpp_sched_order (scheduled_order_id))


create table dcspp_scherr_aux (
	scheduled_order_id	varchar(40)	not null,
	sched_error_id	varchar(40)	not null
,constraint dcspp_scherr_aux_p primary key (scheduled_order_id))

create index sched_error_idx on dcspp_scherr_aux (sched_error_id)

create table dcspp_sched_error (
	sched_error_id	varchar(40)	not null,
	error_date	datetime	not null
,constraint dcspp_sched_err_p primary key (sched_error_id))


create table dcspp_schd_errmsg (
	sched_error_id	varchar(40)	not null,
	error_txt	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dcspp_schd_errs_p primary key (sched_error_id,sequence_num)
,constraint dcspp_sch_errs_f foreign key (sched_error_id) references dcspp_sched_error (sched_error_id))



go
