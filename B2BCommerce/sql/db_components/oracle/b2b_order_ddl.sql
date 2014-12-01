


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/b2b_order_ddl.xml#2 $$Change: 651448 $

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



