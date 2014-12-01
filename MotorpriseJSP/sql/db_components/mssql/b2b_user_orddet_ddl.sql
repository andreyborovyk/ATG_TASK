


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/b2b_user_orddet_ddl.xml#2 $$Change: 651448 $

create table b2b_user_info (
	id	varchar(40)	not null,
	num_orders	integer	null,
	avg_order_amt	numeric(19,7)	null,
	use_org_approver	numeric(1,0)	null,
	use_org_costctr	numeric(1,0)	null,
	use_org_billaddr	numeric(1,0)	null,
	use_org_shipaddr	numeric(1,0)	null,
	use_org_payment	numeric(1,0)	null,
	use_org_vendors	numeric(1,0)	null,
	use_org_purchlst	numeric(1,0)	null
,constraint b2b_user_info_p primary key (id)
,constraint b2b_usrnfid_f foreign key (id) references dps_user (id)
,constraint b2b_user_info1_c check (use_org_approver in (0,1))
,constraint b2b_user_info2_c check (use_org_costctr in (0,1))
,constraint b2b_user_info3_c check (use_org_billaddr in (0,1))
,constraint b2b_user_info4_c check (use_org_shipaddr in (0,1))
,constraint b2b_user_info5_c check (use_org_payment in (0,1))
,constraint b2b_user_info6_c check (use_org_vendors in (0,1))
,constraint b2b_user_info7_c check (use_org_purchlst in (0,1)))


create table b2b_org_info (
	org_id	varchar(40)	not null,
	logo	varchar(40)	null,
	cc_auth	numeric(1,0)	null,
	invoice_auth	numeric(1,0)	null,
	store_crdt_auth	numeric(1,0)	null,
	gift_crt_auth	numeric(1,0)	null,
	use_prnt_approver	numeric(1,0)	null,
	use_prnt_costctr	numeric(1,0)	null,
	use_prnt_billaddr	numeric(1,0)	null,
	use_prnt_shipaddr	numeric(1,0)	null,
	use_prnt_payment	numeric(1,0)	null,
	use_prnt_vendors	numeric(1,0)	null,
	use_prnt_purchlst	numeric(1,0)	null
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
,constraint b2b_org_infob_c check (use_prnt_purchlst in (0,1)))



go
