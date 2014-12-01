
-- the source for this section is 
-- b2b_product_catalog_ddl.sql





create table dbc_manufacturer (
	manufacturer_id	varchar2(40)	not null,
	manufacturer_name	varchar2(254)	null,
	description	varchar2(254)	null,
	long_description	clob	null,
	email	varchar2(255)	null
,constraint dbc_manufacturer_p primary key (manufacturer_id));

create index dbc_man_name_idx on dbc_manufacturer (manufacturer_name);

create table dbc_measurement (
	sku_id	varchar2(40)	not null,
	unit_of_measure	integer	null,
	quantity	number(19,7)	null
,constraint dbc_measurement_p primary key (sku_id));


create table dbc_product (
	product_id	varchar2(40)	not null,
	manufacturer	varchar2(40)	null
,constraint dbc_product_p primary key (product_id)
,constraint dbc_prodmanfctrr_f foreign key (manufacturer) references dbc_manufacturer (manufacturer_id)
,constraint dbc_prodprodct_d_f foreign key (product_id) references dcs_product (product_id));

create index dbc_prd_man_idx on dbc_product (manufacturer);

create table dbc_sku (
	sku_id	varchar2(40)	not null,
	manuf_part_num	varchar2(254)	null
,constraint dbc_sku_p primary key (sku_id)
,constraint dbc_skusku_d_f foreign key (sku_id) references dcs_sku (sku_id));

create index dbc_sku_prtnum_idx on dbc_sku (manuf_part_num);




-- the source for this section is 
-- b2b_order_ddl.sql





create table dbcpp_approverids (
	order_id	varchar2(40)	not null,
	approver_ids	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_approverid_p primary key (order_id,sequence_num)
,constraint dbcpp_apordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_authapprids (
	order_id	varchar2(40)	not null,
	auth_appr_ids	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_authapprid_p primary key (order_id,sequence_num)
,constraint dbcpp_atordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_apprsysmsgs (
	order_id	varchar2(40)	not null,
	appr_sys_msgs	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dbcpp_apprsysmsg_p primary key (order_id,sequence_num)
,constraint dbcpp_sysmordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_appr_msgs (
	order_id	varchar2(40)	not null,
	approver_msgs	varchar2(254)	not null,
	sequence_num	integer	not null
,constraint dbcpp_appr_msgs_p primary key (order_id,sequence_num)
,constraint dbcpp_msgordr_d_f foreign key (order_id) references dcspp_order (order_id));


create table dbcpp_invoice_req (
	payment_group_id	varchar2(40)	not null,
	po_number	varchar2(40)	null,
	pref_format	varchar2(40)	null,
	pref_delivery	varchar2(40)	null,
	disc_percent	number(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null,
	pmt_due_date	date	null
,constraint dbcpp_invoice_re_p primary key (payment_group_id)
,constraint dbcpp_inpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));


create table dbcpp_cost_center (
	cost_center_id	varchar2(40)	not null,
	type	integer	not null,
	version	integer	not null,
	costctr_class_type	varchar2(40)	null,
	identifier	varchar2(40)	null,
	amount	number(19,7)	null,
	order_ref	varchar2(40)	null
,constraint dbcpp_cost_cente_p primary key (cost_center_id));


create table dbcpp_order_cc (
	order_id	varchar2(40)	not null,
	cost_centers	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint dbcpp_order_cc_p primary key (order_id,sequence_num)
,constraint dbcpp_orordr_d_f foreign key (order_id) references dcspp_order (order_id));

create index order_cc_ordid_idx on dbcpp_order_cc (order_id);

create table dbcpp_ccitem_rel (
	relationship_id	varchar2(40)	not null,
	cost_center_id	varchar2(40)	null,
	commerce_item_id	varchar2(40)	null,
	quantity	integer	null,
	amount	number(19,7)	null
,constraint dbcpp_ccitem_rel_p primary key (relationship_id)
,constraint dbcpp_ccreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index cirel_cstctr_idx on dbcpp_ccitem_rel (cost_center_id);
create index cirel_item_idx on dbcpp_ccitem_rel (commerce_item_id);

create table dbcpp_ccship_rel (
	relationship_id	varchar2(40)	not null,
	cost_center_id	varchar2(40)	null,
	shipping_group_id	varchar2(40)	null,
	amount	number(19,7)	null
,constraint dbcpp_ccship_rel_p primary key (relationship_id)
,constraint dbcpp_shreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index csrel_cstctr_idx on dbcpp_ccship_rel (cost_center_id);
create index csrel_shipgrp_idx on dbcpp_ccship_rel (shipping_group_id);

create table dbcpp_ccorder_rel (
	relationship_id	varchar2(40)	not null,
	cost_center_id	varchar2(40)	null,
	order_id	varchar2(40)	null,
	amount	number(19,7)	null
,constraint dbcpp_ccorder_re_p primary key (relationship_id)
,constraint dbcpp_odreltnshp_f foreign key (relationship_id) references dcspp_relationship (relationship_id));

create index corel_cstctr_idx on dbcpp_ccorder_rel (cost_center_id);
create index corel_order_idx on dbcpp_ccorder_rel (order_id);

create table dbcpp_pmt_req (
	payment_group_id	varchar2(40)	not null,
	req_number	varchar2(40)	null
,constraint dbcpp_pmt_req_p primary key (payment_group_id)
,constraint dbcpp_pmpaymnt_g_f foreign key (payment_group_id) references dcspp_pay_group (payment_group_id));

create index pmtreq_req_idx on dbcpp_pmt_req (req_number);




-- the source for this section is 
-- invoice_ddl.sql





create table dbc_inv_delivery (
	id	varchar2(40)	not null,
	version	integer	not null,
	type	integer	not null,
	prefix	varchar2(40)	null,
	first_name	varchar2(40)	null,
	middle_name	varchar2(40)	null,
	last_name	varchar2(40)	null,
	suffix	varchar2(40)	null,
	job_title	varchar2(80)	null,
	company_name	varchar2(40)	null,
	address1	varchar2(80)	null,
	address2	varchar2(80)	null,
	address3	varchar2(80)	null,
	city	varchar2(40)	null,
	county	varchar2(40)	null,
	state	varchar2(40)	null,
	postal_code	varchar2(10)	null,
	country	varchar2(40)	null,
	phone_number	varchar2(40)	null,
	fax_number	varchar2(40)	null,
	email_addr	varchar2(255)	null,
	format	integer	null,
	delivery_mode	integer	null
,constraint dbc_inv_delivery_p primary key (id));


create table dbc_inv_pmt_terms (
	id	varchar2(40)	not null,
	version	integer	not null,
	type	integer	not null,
	disc_percent	number(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_inv_pmt_term_p primary key (id));


create table dbc_invoice (
	id	varchar2(40)	not null,
	version	integer	not null,
	type	integer	not null,
	creation_date	timestamp	null,
	last_mod_date	timestamp	null,
	invoice_number	varchar2(40)	null,
	po_number	varchar2(40)	null,
	req_number	varchar2(40)	null,
	delivery_info	varchar2(40)	null,
	balance_due	number(19,7)	null,
	pmt_due_date	date	null,
	pmt_terms	varchar2(40)	null,
	order_id	varchar2(40)	null,
	pmt_group_id	varchar2(40)	null
,constraint dbc_invoice_p primary key (id)
,constraint dbc_invcdelvry_n_f foreign key (delivery_info) references dbc_inv_delivery (id)
,constraint dbc_invcpmt_term_f foreign key (pmt_terms) references dbc_inv_pmt_terms (id));

create index dbc_inv_dlivr_info on dbc_invoice (delivery_info);
create index dbc_inv_pmt_terms on dbc_invoice (pmt_terms);
create index inv_inv_idx on dbc_invoice (invoice_number);
create index inv_order_idx on dbc_invoice (order_id);
create index inv_pmt_idx on dbc_invoice (pmt_group_id);
create index inv_po_idx on dbc_invoice (po_number);




-- the source for this section is 
-- contracts_ddl.sql




-- Normally, catalog_id and price_list_id would reference the appropriate table it is possible not to use those tables though, which is why the reference is not included

create table dbc_contract (
	contract_id	varchar2(40)	not null,
	display_name	varchar2(254)	null,
	creation_date	timestamp	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	creator_id	varchar2(40)	null,
	negotiator_info	varchar2(40)	null,
	price_list_id	varchar2(40)	null,
	catalog_id	varchar2(40)	null,
	term_id	varchar2(40)	null,
	comments	varchar2(254)	null
,constraint dbc_contract_p primary key (contract_id));


create table dbc_contract_term (
	terms_id	varchar2(40)	not null,
	terms	clob	null,
	disc_percent	number(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_contract_ter_p primary key (terms_id));





-- the source for this section is 
-- b2b_user_ddl.sql




-- Specific extensions for B2B user profiles

create table dbc_cost_center (
	id	varchar2(40)	not null,
	identifier	varchar2(40)	not null,
	description	varchar2(64)	null,
	user_id	varchar2(40)	null
,constraint dbc_cost_center_p primary key (id));


create table dbc_user (
	id	varchar2(40)	not null,
	price_list	varchar2(40)	null,
	user_catalog	varchar2(40)	null,
	user_role	integer	null,
	business_addr	varchar2(40)	null,
	dflt_shipping_addr	varchar2(40)	null,
	dflt_billing_addr	varchar2(40)	null,
	dflt_payment_type	varchar2(40)	null,
	dflt_cost_center	varchar2(40)	null,
	order_price_limit	number(19,7)	null,
	approval_required	number(1,0)	null
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
	user_id	varchar2(40)	not null,
	seq	integer	not null,
	cost_center_id	varchar2(40)	not null
,constraint dbc_buyer_costct_p primary key (user_id,seq)
,constraint dbc_buyrcost_cnt_f foreign key (cost_center_id) references dbc_cost_center (id));

create index dbc_byr_costctr_id on dbc_buyer_costctr (cost_center_id);
-- Multi-table associating a Buyer with one or more order approvers.  Approvers are required to be registered users of the site so they can perform online approvals.

create table dbc_buyer_approver (
	user_id	varchar2(40)	not null,
	approver_id	varchar2(40)	not null,
	seq	integer	not null
,constraint dbc_buyer_approv_p primary key (user_id,seq)
,constraint dbc_buyrapprvr_d_f foreign key (approver_id) references dps_user (id)
,constraint dbc_buyrusr_d_f foreign key (user_id) references dps_user (id));

create index buyer_approver_idx on dbc_buyer_approver (approver_id);

create table dbc_buyer_payment (
	user_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	payment_id	varchar2(40)	not null
,constraint dbc_buyer_paymen_p primary key (user_id,tag)
,constraint dbc_buyrpaymnt_d_f foreign key (payment_id) references dps_credit_card (id)
,constraint dbc_brpymntusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_pymnt_id on dbc_buyer_payment (payment_id);

create table dbc_buyer_shipping (
	user_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	addr_id	varchar2(40)	not null
,constraint dbc_buyer_shippi_p primary key (user_id,tag)
,constraint dbc_buyraddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_buyrshpusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_shpng_addr on dbc_buyer_shipping (addr_id);

create table dbc_buyer_billing (
	user_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	addr_id	varchar2(40)	not null
,constraint dbc_buyer_billin_p primary key (user_id,tag)
,constraint dbc_buyrblladdr_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_buyrbllusr_d_f foreign key (user_id) references dps_user (id));

create index dbc_byr_biladdr_id on dbc_buyer_billing (addr_id);

create table dbc_buyer_prefvndr (
	user_id	varchar2(40)	not null,
	vendor	varchar2(100)	not null,
	seq	integer	not null
,constraint dbc_buyer_prefvn_p primary key (user_id,seq)
,constraint dbc_byrprfndusrd_f foreign key (user_id) references dps_user (id));


create table dbc_buyer_plist (
	user_id	varchar2(40)	not null,
	list_id	varchar2(40)	not null,
	tag	integer	not null
,constraint dbc_buyer_plist_p primary key (user_id,tag));





-- the source for this section is 
-- organization_ddl.sql





create table dbc_organization (
	id	varchar2(40)	not null,
	type	integer	null,
	cust_type	integer	null,
	duns_number	varchar2(20)	null,
	dflt_shipping_addr	varchar2(40)	null,
	dflt_billing_addr	varchar2(40)	null,
	dflt_payment_type	varchar2(40)	null,
	dflt_cost_center	varchar2(40)	null,
	order_price_limit	number(19,7)	null,
	contract_id	varchar2(40)	null,
	approval_required	number(1,0)	null
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
	org_id	varchar2(40)	not null,
	contact_id	varchar2(40)	not null,
	seq	integer	not null
,constraint dbc_org_contact_p primary key (org_id,seq)
,constraint dbc_orgccontct_d_f foreign key (contact_id) references dps_contact_info (id)
,constraint dbc_orgcorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_cntct_id on dbc_org_contact (contact_id);
-- Multi-table associating an Organization with one or more order approvers.  Like administrators, approvers are required to be registered users of the site so they can perform online approvals.

create table dbc_org_approver (
	org_id	varchar2(40)	not null,
	approver_id	varchar2(40)	not null,
	seq	integer	not null
,constraint dbc_org_approver_p primary key (org_id,seq)
,constraint dbc_orgporg_d_f foreign key (org_id) references dps_organization (org_id)
,constraint dbc_orgpapprvr_d_f foreign key (approver_id) references dps_user (id));

create index org_approver_idx on dbc_org_approver (approver_id);
-- Multi-table associating an Organization with one or more costcenters that are pre-approved for use by members of the organization.  

create table dbc_org_costctr (
	org_id	varchar2(40)	not null,
	cost_center	varchar2(40)	not null,
	seq	integer	not null
,constraint dbc_org_costctr_p primary key (org_id,seq)
,constraint dbc_ocstctrorgd_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_cstctr on dbc_org_costctr (cost_center);
-- Multi-table associating an Organization with one or more payment types that are pre-apprived for use by members of the organization.Right now we're just using credit cards here, but this will needto change to support more general payment types, including invoicing and purchase orders

create table dbc_org_payment (
	org_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	payment_id	varchar2(40)	not null
,constraint dbc_org_payment_p primary key (org_id,tag)
,constraint dbc_orgppaymnt_d_f foreign key (payment_id) references dps_credit_card (id)
,constraint dbc_orgpymntorg_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_pymnt_id on dbc_org_payment (payment_id);

create table dbc_org_shipping (
	org_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	addr_id	varchar2(40)	not null
,constraint dbc_org_shipping_p primary key (org_id,tag)
,constraint dbc_orgsaddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_orgsorg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_shpng_adr on dbc_org_shipping (addr_id);

create table dbc_org_billing (
	org_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	addr_id	varchar2(40)	not null
,constraint dbc_org_billing_p primary key (org_id,tag)
,constraint dbc_orgbaddr_d_f foreign key (addr_id) references dps_contact_info (id)
,constraint dbc_orgborg_d_f foreign key (org_id) references dps_organization (org_id));

create index dbc_org_billng_adr on dbc_org_billing (addr_id);

create table dbc_org_prefvndr (
	org_id	varchar2(40)	not null,
	vendor	varchar2(100)	not null,
	seq	integer	not null
,constraint dbc_org_prefvndr_p primary key (org_id,seq)
,constraint dbc_orgprfvndorg_f foreign key (org_id) references dps_organization (org_id));





-- the source for this section is 
-- b2b_reporting_views.sql




create or replace view drpt_dlr_org
as
             select ORG.name as organization, round(sum(AI.amount),2) as amount
from 	dcspp_order O,
	dcspp_amount_info AI,
	dps_organization ORG,
	dps_user_org DO
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.price_info = AI.amount_info_id
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name
         ;


create or replace view drpt_dlr_byr
as
             select ORG.name as organization, DU.id as buyerid, 
   (DU.last_name || ',' || DU.first_name) as buyer,
    round(sum(AI.amount),2) as amount
from  dcspp_order O,
    dcspp_amount_info AI,
    dps_user DU,
    dps_organization ORG,
    dps_user_org DO
where              
   O.profile_id =DU.id
   and DU.id = DO.user_id
   and DO.organization = ORG.org_id
   and O.price_info = AI.amount_info_id
   and O.state = 'NO_PENDING_ACTION'
group by ORG.name,DU.id, (DU.last_name || ',' || DU.first_name)
         ;


create or replace view drpt_dlr_org_parts
as
             select org.name as organization, s.manuf_part_num as partnumber, round(sum(ai.amount),2) as amount
from  dcspp_order o, 
  dps_user_org do, 
  dcspp_item i, 
  dbc_sku s,
  dcspp_amount_info ai, 
  dps_organization org
where 
  o.profile_id = do.user_id 
  and o.order_id = i.order_ref 
  and do.organization=org.org_id 
  and i.price_info = ai.amount_info_id
  and i.catalog_ref_id = s.sku_id
  and o.state = 'NO_PENDING_ACTION'
group by org.name, s.manuf_part_num
         ;


create or replace view drpt_dlr_org_cc_i
as
             select ORG.name as organization, CC.identifier as costcenter,round(sum(CI.amount),2) as amount
from 	dcspp_order O,
	dcspp_item I,
	dbcpp_ccitem_rel CI,
	dps_organization ORG,
	dps_user_org DO,
	dbcpp_cost_center CC
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.order_id = I.order_ref
	and CI.commerce_item_id = I.commerce_item_id
	and CI.cost_center_id = CC.cost_center_id	
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name, CC.identifier
         ;


create or replace view drpt_dlr_org_cc_s
as
             select ORG.name as organization, CC.identifier as costcenter,round(sum(CS.amount),2) as amount
from 	dcspp_order O,
	dcspp_ship_group SG,
	dbcpp_ccship_rel CS,
	dps_organization ORG,
	dps_user_org DO,
	dbcpp_cost_center CC
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.order_id = SG.order_ref
	and CS.shipping_group_id = SG.shipping_group_id
	and CS.cost_center_id = CC.cost_center_id
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name, CC.identifier
         ;


create or replace view drpt_dlr_org_cc_o
as
             select ORG.name as organization, CC.identifier as costcenter,round(sum(CO.amount),2) as amount
from 	dcspp_order O,
	dbcpp_ccorder_rel CO,
	dps_organization ORG,
	dps_user_org DO,
	dbcpp_cost_center CC
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.order_id = CO.order_id
	and CO.cost_center_id = CC.cost_center_id
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name, CC.identifier
         ;


create or replace view drpt_dlr_org_cc
as
             select O.organization as organization, O.costcenter as costcenter,
(sum(I.amount) + sum(O.amount)) as amount
from drpt_dlr_org_cc_i I,
drpt_dlr_org_cc_o O
where O.organization = I.organization
and O.costcenter = I.costcenter
group by O.organization, O.costcenter
UNION
select I.organization as organization, I.costcenter as costcenter,
sum(I.amount) as amount
from drpt_dlr_org_cc_i I
where NOT EXISTS (select 1 from drpt_dlr_org_cc_o xx 
where xx.organization = I.organization
and xx.costcenter = I.costcenter)
group by I.organization, I.costcenter
UNION
select O.organization as organization, O.costcenter as costcenter,
sum(O.amount) as amount
from drpt_dlr_org_cc_o O
where NOT EXISTS (select 1 from drpt_dlr_org_cc_i xx
where xx.organization = O.organization
and xx.costcenter = O.costcenter)
group by O.organization, O.costcenter
         ;


create or replace view drpt_dlr_parts
as
             select s.manuf_part_num as partnumber, round(sum(ai.amount),2) as amount
from    
	dcspp_order o,
	dcspp_item i, 
	dbc_sku s,
	dcspp_amount_info ai
where 
  o.order_id = i.order_ref
  and o.state='NO_PENDING_ACTION'
  and i.price_info = ai.amount_info_id
  and i.catalog_ref_id = s.sku_id
group by s.manuf_part_num
         ;


create or replace view drpt_ordr_by_date
as
             select trunc (O.submitted_date) as datesubmitted,
 count(distinct O.order_id) as orders, 
 round(sum(ai.amount),2) as totalamount
from dcspp_order O,
     dcspp_item i, 
     dcspp_amount_info ai
where O.submitted_date is not null
  and O.order_id = i.order_ref
  and i.price_info = ai.amount_info_id
   and O.state = 'NO_PENDING_ACTION'
group by trunc (O.submitted_date)
         ;


create or replace view drpt_ordr_org
as
             select ORG.name as organization, count(*) as orders
from 	dcspp_order O,
	dps_organization ORG,
	dps_user_org DO
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name
         ;


create or replace view drpt_ordr_buyr
as
             select ORG.name as organization, DU.id as buyerid, (DU.last_name || ',' || DU.first_name) as buyer, count(*) as orders
from 	dcspp_order O,
	dps_user DU,
	dps_organization ORG,
	dps_user_org DO
where              
	O.profile_id =DU.id
	and DU.id = DO.user_id
	and DO.organization = ORG.org_id
	and O.state = 'NO_PENDING_ACTION'
group by ORG.name, DU.id, (DU.last_name || ',' || DU.first_name)
         ;


create or replace view drpt_ordr_org_cc
as
             select ORG.name as organization, CC.identifier as costcenter, count(*) as orders
from 	dcspp_order O,
	dps_organization ORG,
	dps_user_org DO,
	dbcpp_cost_center CC, 
	dbcpp_order_cc OCC
where              
	O.profile_id =DO.user_id
	and DO.organization = ORG.org_id
	and O.state = 'NO_PENDING_ACTION'
	and O.order_id = OCC.order_id
	and OCC.cost_centers = CC.cost_center_id
group by ORG.name, CC.identifier
         ;


create or replace view drpt_part_purchsed
as
             select Distinct S.manuf_part_num as partnumber, M.manufacturer_name as manufacturer 
from    
        dcspp_order O,
        dbc_sku S,
        dcspp_item I,
        dbc_product P,
        dbc_manufacturer M
where   
        O.state='NO_PENDING_ACTION'
        and O.order_id = I.order_ref
        and S.sku_id = I.catalog_ref_id
        and I.product_id=P.product_id
        and P.manufacturer=M.manufacturer_id
         ;



