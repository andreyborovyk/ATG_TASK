


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/b2b_user_orddet_ddl.xml#2 $$Change: 651448 $

create table b2b_user_info (
	id	varchar(40)	not null,
	num_orders	integer	null,
	avg_order_amt	numeric(19,7)	null,
	use_org_approver	tinyint	null,
	use_org_costctr	tinyint	null,
	use_org_billaddr	tinyint	null,
	use_org_shipaddr	tinyint	null,
	use_org_payment	tinyint	null,
	use_org_vendors	tinyint	null,
	use_org_purchlst	tinyint	null
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
	org_id	varchar(40)	not null,
	logo	varchar(40)	null,
	cc_auth	tinyint	null,
	invoice_auth	tinyint	null,
	store_crdt_auth	tinyint	null,
	gift_crt_auth	tinyint	null,
	use_prnt_approver	tinyint	null,
	use_prnt_costctr	tinyint	null,
	use_prnt_billaddr	tinyint	null,
	use_prnt_shipaddr	tinyint	null,
	use_prnt_payment	tinyint	null,
	use_prnt_vendors	tinyint	null,
	use_prnt_purchlst	tinyint	null
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

commit;


