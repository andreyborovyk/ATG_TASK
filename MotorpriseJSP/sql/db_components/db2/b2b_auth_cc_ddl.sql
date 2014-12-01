


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/b2b_auth_cc_ddl.xml#2 $$Change: 651448 $

create table b2b_auth_pmnt (
	id	varchar(40)	not null,
	cc_auth	numeric(1,0)	default null,
	invoice_auth	numeric(1,0)	default null,
	store_crdt_auth	numeric(1,0)	default null,
	gift_crt_auth	numeric(1,0)	default null
,constraint b2b_auth_pmnt_p primary key (id)
,constraint b2b_athpmntid_f foreign key (id) references dps_user (id)
,constraint b2b_auth_pmnt1_c check (cc_auth in (0,1))
,constraint b2b_auth_pmnt2_c check (invoice_auth in (0,1))
,constraint b2b_auth_pmnt3_c check (store_crdt_auth in (0,1))
,constraint b2b_auth_pmnt4_c check (gift_crt_auth in (0,1)));


create table b2b_credit_card (
	id	varchar(40)	not null,
	cc_first_name	varchar(40)	default null,
	cc_middle_name	varchar(40)	default null,
	cc_last_name	varchar(40)	default null
,constraint b2b_credit_card_p primary key (id)
,constraint b2b_credtcrdid_f foreign key (id) references dps_credit_card (id));

create index b2b_crcdba_idx on b2b_credit_card (cc_last_name);
commit;


