


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/b2b_user_ddl.xml#2 $$Change: 651448 $
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


