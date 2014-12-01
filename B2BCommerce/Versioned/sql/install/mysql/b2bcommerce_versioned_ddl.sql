
-- the source for this section is 
-- versioned_b2b_product_catalog_ddl.sql





create table dbc_manufacturer (
	asset_version	bigint	not null,
	workspace_id	varchar(40)	not null,
	branch_id	varchar(40)	not null,
	is_head	tinyint	not null,
	version_deleted	tinyint	not null,
	version_editable	tinyint	not null,
	pred_version	bigint	null,
	checkin_date	datetime	null,
	manufacturer_id	varchar(40)	not null,
	manufacturer_name	nvarchar(254)	null,
	description	nvarchar(254)	null,
	long_description	longtext charset utf8	null,
	email	varchar(255)	null
,constraint dbc_manufacturer_p primary key (manufacturer_id,asset_version));

create index dbc_man_name_idx on dbc_manufacturer (manufacturer_name);
create index dbc_manufactur_wsx on dbc_manufacturer (workspace_id);
create index dbc_manufactur_cix on dbc_manufacturer (checkin_date);

create table dbc_measurement (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	unit_of_measure	integer	null,
	quantity	numeric(19,7)	null
,constraint dbc_measurement_p primary key (sku_id,asset_version));


create table dbc_product (
	asset_version	bigint	not null,
	product_id	varchar(40)	not null,
	manufacturer	varchar(40)	null
,constraint dbc_product_p primary key (product_id,asset_version));


create table dbc_sku (
	asset_version	bigint	not null,
	sku_id	varchar(40)	not null,
	manuf_part_num	nvarchar(254)	null
,constraint dbc_sku_p primary key (sku_id,asset_version));

create index dbc_sku_prtnum_idx on dbc_sku (manuf_part_num);
commit;



-- the source for this section is 
-- b2b_order_ddl.sql





create table dbcpp_approverids (
	order_id	varchar(40)	not null,
	approver_ids	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_approverid_p primary key (order_id,sequence_num)
,constraint dbcpp_apordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_authapprids (
	order_id	varchar(40)	not null,
	auth_appr_ids	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_authapprid_p primary key (order_id,sequence_num)
,constraint dbcpp_atordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_apprsysmsgs (
	order_id	varchar(40)	not null,
	appr_sys_msgs	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dbcpp_apprsysmsg_p primary key (order_id,sequence_num)
,constraint dbcpp_sysmordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_appr_msgs (
	order_id	varchar(40)	not null,
	approver_msgs	varchar(254)	not null,
	sequence_num	integer	not null
,constraint dbcpp_appr_msgs_p primary key (order_id,sequence_num)
,constraint dbcpp_msgordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_invoice_req (
	payment_group_id	varchar(40)	not null,
	po_number	varchar(40)	null,
	pref_format	varchar(40)	null,
	pref_delivery	varchar(40)	null,
	disc_percent	numeric(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null,
	pmt_due_date	datetime	null
,constraint dbcpp_invoice_re_p primary key (payment_group_id)
,constraint dbcpp_inpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));


create table dbcpp_cost_center (
	cost_center_id	varchar(40)	not null,
	type	integer	not null,
	version	integer	not null,
	costctr_class_type	varchar(40)	null,
	identifier	varchar(40)	null,
	amount	numeric(19,7)	null,
	order_ref	varchar(40)	null
,constraint dbcpp_cost_cente_p primary key (cost_center_id));


create table dbcpp_order_cc (
	order_id	varchar(40)	not null,
	cost_centers	varchar(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_order_cc_p primary key (order_id,sequence_num)
,constraint dbcpp_orordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_cc_ordid_idx on dbcpp_order_cc (order_id);

create table dbcpp_ccitem_rel (
	relationship_id	varchar(40)	not null,
	cost_center_id	varchar(40)	null,
	commerce_item_id	varchar(40)	null,
	quantity	bigint	null,
	amount	numeric(19,7)	null
,constraint dbcpp_ccitem_rel_p primary key (relationship_id)
,constraint dbcpp_ccreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index cirel_cstctr_idx on dbcpp_ccitem_rel (cost_center_id);
create index cirel_item_idx on dbcpp_ccitem_rel (commerce_item_id);

create table dbcpp_ccship_rel (
	relationship_id	varchar(40)	not null,
	cost_center_id	varchar(40)	null,
	shipping_group_id	varchar(40)	null,
	amount	numeric(19,7)	null
,constraint dbcpp_ccship_rel_p primary key (relationship_id)
,constraint dbcpp_shreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index csrel_cstctr_idx on dbcpp_ccship_rel (cost_center_id);
create index csrel_shipgrp_idx on dbcpp_ccship_rel (shipping_group_id);

create table dbcpp_ccorder_rel (
	relationship_id	varchar(40)	not null,
	cost_center_id	varchar(40)	null,
	order_id	varchar(40)	null,
	amount	numeric(19,7)	null
,constraint dbcpp_ccorder_re_p primary key (relationship_id)
,constraint dbcpp_odreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index corel_cstctr_idx on dbcpp_ccorder_rel (cost_center_id);
create index corel_order_idx on dbcpp_ccorder_rel (order_id);

create table dbcpp_pmt_req (
	payment_group_id	varchar(40)	not null,
	req_number	varchar(40)	null
,constraint dbcpp_pmt_req_p primary key (payment_group_id)
,constraint dbcpp_pmpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index pmtreq_req_idx on dbcpp_pmt_req (req_number);
commit;



-- the source for this section is 
-- invoice_ddl.sql





create table dbc_inv_delivery (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	prefix	nvarchar(40)	null,
	first_name	nvarchar(40)	null,
	middle_name	nvarchar(40)	null,
	last_name	nvarchar(40)	null,
	suffix	nvarchar(40)	null,
	job_title	nvarchar(80)	null,
	company_name	nvarchar(40)	null,
	address1	nvarchar(80)	null,
	address2	nvarchar(80)	null,
	address3	nvarchar(80)	null,
	city	nvarchar(40)	null,
	county	nvarchar(40)	null,
	state	nvarchar(40)	null,
	postal_code	nvarchar(10)	null,
	country	nvarchar(40)	null,
	phone_number	nvarchar(40)	null,
	fax_number	nvarchar(40)	null,
	email_addr	nvarchar(255)	null,
	format	integer	null,
	delivery_mode	integer	null
,constraint dbc_inv_delivery_p primary key (id));


create table dbc_inv_pmt_terms (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	disc_percent	numeric(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_inv_pmt_term_p primary key (id));


create table dbc_invoice (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	creation_date	datetime	null,
	last_mod_date	datetime	null,
	invoice_number	varchar(40)	null,
	po_number	varchar(40)	null,
	req_number	varchar(40)	null,
	delivery_info	varchar(40)	null,
	balance_due	numeric(19,7)	null,
	pmt_due_date	datetime	null,
	pmt_terms	varchar(40)	null,
	order_id	varchar(40)	null,
	pmt_group_id	varchar(40)	null
,constraint dbc_invoice_p primary key (id)
,constraint dbc_invcdelvry_n_f foreign key (delivery_info) references dbc_inv_delivery (id)
,constraint dbc_invcpmt_term_f foreign key (pmt_terms) references dbc_inv_pmt_terms (id));

create index dbc_inv_dlivr_info on dbc_invoice (delivery_info);
create index dbc_inv_pmt_terms on dbc_invoice (pmt_terms);
create index inv_inv_idx on dbc_invoice (invoice_number);
create index inv_order_idx on dbc_invoice (order_id);
create index inv_pmt_idx on dbc_invoice (pmt_group_id);
create index inv_po_idx on dbc_invoice (po_number);
commit;



-- the source for this section is 
-- contracts_ddl.sql




-- Normally, catalog_id and price_list_id would reference the appropriate table it is possible not to use those tables though, which is why the reference is not included

create table dbc_contract (
	contract_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	creator_id	varchar(40)	null,
	negotiator_info	nvarchar(40)	null,
	price_list_id	varchar(40)	null,
	catalog_id	varchar(40)	null,
	term_id	varchar(40)	null,
	comments	nvarchar(254)	null
,constraint dbc_contract_p primary key (contract_id));


create table dbc_contract_term (
	terms_id	varchar(40)	not null,
	terms	longtext	null,
	disc_percent	numeric(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_contract_ter_p primary key (terms_id));

commit;



-- the source for this section is 
-- b2b_user_ddl.sql




-- Specific extensions for B2B user profiles

create table dbc_cost_center (
	id	varchar(40)	not null,
	identifier	nvarchar(40)	not null,
	description	nvarchar(254)	null,
	user_id	varchar(40)	null
,constraint dbc_cost_center_p primary key (id));


create table dbc_user (
	id	varchar(40)	not null,
	price_list	varchar(40)	null,
	user_catalog	varchar(40)	null,
	user_role	integer	null,
	business_addr	varchar(40)	null,
	dflt_shipping_addr	varchar(40)	null,
	dflt_billing_addr	varchar(40)	null,
	dflt_payment_type	varchar(40)	null,
	dflt_cost_center	varchar(40)	null,
	order_price_limit	numeric(19,7)	null,
	approval_required	tinyint	null
,constraint dbc_user_p primary key (id)
,constraint dbc_usrdflt_cos_f foreign key (dflt_cost_center) references dbc_cost_center (id)
,constraint dbc_usrbusnss_d_f foreign key (business_addr) references dps_contact_info (id)
,constraint dbc_usrdflt_bil_f foreign key (dflt_billing_addr) references dps_contact_info (id)
,constraint dbc_usrdflt_shi_f foreign key (dflt_shipping_addr) references dps_contact_info (id)
,constraint dbc_usrdflt_pay_f foreign key (dflt_payment_type) references dps_credit_card (id)
,constraint dbc_usrid_f foreign key (id) references dps_user (id));

create index usr_defcstctr_idx on dbc_user (dflt_cost_center);
create index dbc_usr_busnes_adr on dbc_user (business_addr);
create index dbc_usrdfltblngadr on dbc_user (dflt_billing_addr);
create index dbc_usrdfltshp_adr on dbc_user (dflt_shipping_addr);
create index dbc_usrdfltpymntty on dbc_user (dflt_payment_type);

create table dbc_buyer_costctr (
	user_id	varchar(40)	not null,
	seq	integer	not null,
	cost_center_id	varchar(40)	not null
,constraint dbc_buyer_costct_p primary key (user_id,seq)
,constraint dbc_buyrcost_cnt_f foreign key (cost_center_id) references dbc_cost_center (id));

create index dbc_byr_costctr_id on dbc_buyer_costctr (cost_center_id);
-- Multi-table associating a Buyer with one or more order approvers.  Approvers are required to be registered users of the site so they can perform online approvals.

create table dbc_buyer_approver (
	user_id	varchar(40)	not null,
	approver_id	varchar(40)	not null,
	seq	integer	not null
,constraint dbc_buyer_approv_p primary key (user_id,seq)
,constraint dbc_buyrapprvr_d_f foreign key (approver_id) references dps_user (id)
,constraint dbc_buyrusr_d_f foreign key (user_id) references dps_user (id));

create index buyer_approver_idx on dbc_buyer_approver (approver_id);

create table dbc_buyer_payment (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	payment_id	varchar(40)	not null
,constraint dbc_buyer_paymen_p primary key (user_id,tag)
,constraint dbc_buyrpaymnt_d_f foreign key (payment_id) references dps_credit_card (id)
,constraint dbc_brpymntusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_pymnt_id on dbc_buyer_payment (payment_id);

create table dbc_buyer_shipping (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	addr_id	varchar(40)	not null
,constraint dbc_buyer_shippi_p primary key (user_id,tag)
,constraint dbc_buyraddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_buyrshpusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_shpng_addr on dbc_buyer_shipping (addr_id);

create table dbc_buyer_billing (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	addr_id	varchar(40)	not null
,constraint dbc_buyer_billin_p primary key (user_id,tag)
,constraint dbc_buyrblladdr_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_buyrbllusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_biladdr_id on dbc_buyer_billing (addr_id);

create table dbc_buyer_prefvndr (
	user_id	varchar(40)	not null,
	vendor	nvarchar(100)	not null,
	seq	integer	not null
,constraint dbc_buyer_prefvn_p primary key (user_id,seq)
,constraint dbc_byrprfndusrd_f foreign key (user_id) references dps_user (id));


create table dbc_buyer_plist (
	user_id	varchar(40)	not null,
	list_id	varchar(40)	not null,
	tag	integer	not null
,constraint dbc_buyer_plist_p primary key (user_id,tag));

commit;



-- the source for this section is 
-- organization_ddl.sql





create table dbc_organization (
	id	varchar(40)	not null,
	type	integer	null,
	cust_type	integer	null,
	duns_number	varchar(20)	null,
	dflt_shipping_addr	varchar(40)	null,
	dflt_billing_addr	varchar(40)	null,
	dflt_payment_type	varchar(40)	null,
	dflt_cost_center	varchar(40)	null,
	order_price_limit	numeric(19,7)	null,
	contract_id	varchar(40)	null,
	approval_required	tinyint	null
,constraint dbc_organization_p primary key (id)
,constraint dbc_orgncontrct__f foreign key (contract_id) references dbc_contract (contract_id)
,constraint dbc_orgndflt_bil_f foreign key (dflt_billing_addr) references dps_contact_info (id)
,constraint dbc_orgndflt_shi_f foreign key (dflt_shipping_addr) references dps_contact_info (id)
,constraint dbc_orgndflt_pay_f foreign key (dflt_payment_type) references dps_credit_card (id)
,constraint dbc_orgnztnid_f foreign key (id) references dps_organization (org_id));

create index dbc_org_cntrct_id on dbc_organization (contract_id);
create index dbc_orgdfltblig_ad on dbc_organization (dflt_billing_addr);
create index dbc_orgdflt_shpadr on dbc_organization (dflt_shipping_addr);
create index dbc_orgdflt_pmttyp on dbc_organization (dflt_payment_type);
create index dbc_orgdfltcst_ctr on dbc_organization (dflt_cost_center);

create table dbc_org_contact (
	org_id	varchar(40)	not null,
	contact_id	varchar(40)	not null,
	seq	integer	not null
,constraint dbc_org_contact_p primary key (org_id,seq)
,constraint dbc_orgccontct_d_f foreign key (contact_id) references dps_contact_info (id)
,constraint dbc_orgcorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_cntct_id on dbc_org_contact (contact_id);
-- Multi-table associating an Organization with one or more order approvers.  Like administrators, approvers are required to be registered users of the site so they can perform online approvals.

create table dbc_org_approver (
	org_id	varchar(40)	not null,
	approver_id	varchar(40)	not null,
	seq	integer	not null
,constraint dbc_org_approver_p primary key (org_id,seq)
,constraint dbc_orgporg_d_f foreign key (org_id) references dps_organization (org_id)
,constraint dbc_orgpapprvr_d_f foreign key (approver_id) references dps_user (id));

create index org_approver_idx on dbc_org_approver (approver_id);
-- Multi-table associating an Organization with one or more costcenters that are pre-approved for use by members of the organization.  

create table dbc_org_costctr (
	org_id	varchar(40)	not null,
	cost_center	varchar(40)	not null,
	seq	integer	not null
,constraint dbc_org_costctr_p primary key (org_id,seq)
,constraint dbc_ocstctrorgd_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_cstctr on dbc_org_costctr (cost_center);
-- Multi-table associating an Organization with one or more payment types that are pre-apprived for use by members of the organization.Right now we're just using credit cards here, but this will needto change to support more general payment types, including invoicing and purchase orders

create table dbc_org_payment (
	org_id	varchar(40)	not null,
	tag	nvarchar(42)	not null,
	payment_id	varchar(40)	not null
,constraint dbc_org_payment_p primary key (org_id,tag)
,constraint dbc_orgppaymnt_d_f foreign key (payment_id) references dps_credit_card (id)
,constraint dbc_orgpymntorg_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_pymnt_id on dbc_org_payment (payment_id);

create table dbc_org_shipping (
	org_id	varchar(40)	not null,
	tag	nvarchar(42)	not null,
	addr_id	varchar(40)	not null
,constraint dbc_org_shipping_p primary key (org_id,tag)
,constraint dbc_orgsaddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_orgsorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_shpng_adr on dbc_org_shipping (addr_id);

create table dbc_org_billing (
	org_id	varchar(40)	not null,
	tag	nvarchar(42)	not null,
	addr_id	varchar(40)	not null
,constraint dbc_org_billing_p primary key (org_id,tag)
,constraint dbc_orgbaddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_orgborg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_billng_adr on dbc_org_billing (addr_id);

create table dbc_org_prefvndr (
	org_id	varchar(40)	not null,
	vendor	nvarchar(100)	not null,
	seq	integer	not null
,constraint dbc_org_prefvndr_p primary key (org_id,seq)
,constraint dbc_orgprfvndorg_f foreign key (org_id) references dps_organization (org_id));

commit;


