


--  @version $Id: //product/Ticketing/version/10.0.3/src/sql/owning_group_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with ticketing owning groups.
-- Ticketing owning group

create table tkt_owning_group (
	logical_org_id	varchar(40)	not null,
	email	varchar(254)	default null,
	deallocation_mins	integer	default null,
	inactivity_mins	integer	default null,
	availability_mins	integer	default null,
	sla_mins	integer	default null,
	def_esc_own_grp	varchar(40)	default null
,constraint tkt_own_grp_p primary key (logical_org_id)
,constraint tkt_owngrp_og_f foreign key (def_esc_own_grp) references dlo_logical_org (logical_org_id)
,constraint tkt_owngrp_lo_f foreign key (logical_org_id) references dlo_logical_org (logical_org_id));

create index tkt_owninggroup1_x on tkt_owning_group (def_esc_own_grp);

create table tkt_esc_own_group (
	logical_org_id	varchar(40)	not null,
	escalation_own_grp	varchar(40)	not null
,constraint tkt_esc_own_grp_p primary key (logical_org_id,escalation_own_grp)
,constraint tkt_escog_og_f foreign key (logical_org_id) references dlo_logical_org (logical_org_id)
,constraint tkt_escog_og2_f foreign key (escalation_own_grp) references dlo_logical_org (logical_org_id));

create index tkt_escowngroup1_x on tkt_esc_own_group (escalation_own_grp);
-- Ticket queue

create table tkt_queue (
	logical_org_id	varchar(40)	not null,
	description	varchar(254)	default null,
	accepting	numeric(1)	not null,
	deallocation_ms	integer	default null,
	inactivity_ms	integer	default null,
	availability_ms	integer	default null,
	sla_ms	integer	default null,
	def_esc_tkt_q	varchar(40)	default null,
	c_req_tmout	integer	default null,
	c_offer_tmout	integer	default null,
	c_reject_tmout	integer	default null,
	c_disp_pol	integer	default null,
	c_overflow_pol	integer	default null,
	email	varchar(254)	default null,
	email_fr_name	varchar(254)	default null,
	sms	varchar(254)	default null,
	sms_type	varchar(254)	default null,
	mms	varchar(254)	default null,
	mms_type	varchar(254)	default null
,constraint tkt_tkt_q_p primary key (logical_org_id)
,constraint tkt_q_esc_lo_f foreign key (def_esc_tkt_q) references dlo_logical_org (logical_org_id)
,constraint tkt_q_lo_f foreign key (logical_org_id) references dlo_logical_org (logical_org_id));

create index tkt_q_lo1_x on tkt_queue (def_esc_tkt_q);

create table tkt_esc_tkt_q (
	logical_org_id	varchar(40)	not null,
	escalation_tkt_q	varchar(40)	not null
,constraint tkt_q_esctktq_p primary key (logical_org_id,escalation_tkt_q)
,constraint tkt_q_esct_lo_f foreign key (logical_org_id) references dlo_logical_org (logical_org_id)
,constraint tkt_q_esct_lo2_f foreign key (escalation_tkt_q) references dlo_logical_org (logical_org_id));

create index tkt_q_esclo2_x on tkt_esc_tkt_q (escalation_tkt_q);
commit;


