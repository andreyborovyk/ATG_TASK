


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/b2b_user_orddet_ddl.xml#2 $$Change: 651448 $

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




