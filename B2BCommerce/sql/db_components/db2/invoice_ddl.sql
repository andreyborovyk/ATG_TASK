


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/invoice_ddl.xml#2 $$Change: 651448 $

create table dbc_inv_delivery (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	prefix	varchar(40)	default null,
	first_name	varchar(40)	default null,
	middle_name	varchar(40)	default null,
	last_name	varchar(40)	default null,
	suffix	varchar(40)	default null,
	job_title	varchar(80)	default null,
	company_name	varchar(40)	default null,
	address1	varchar(80)	default null,
	address2	varchar(80)	default null,
	address3	varchar(80)	default null,
	city	varchar(40)	default null,
	county	varchar(40)	default null,
	state	varchar(40)	default null,
	postal_code	varchar(10)	default null,
	country	varchar(40)	default null,
	phone_number	varchar(40)	default null,
	fax_number	varchar(40)	default null,
	email_addr	varchar(255)	default null,
	format	integer	default null,
	delivery_mode	integer	default null
,constraint dbc_inv_delivery_p primary key (id));


create table dbc_inv_pmt_terms (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	disc_percent	numeric(19,7)	default null,
	disc_days	integer	default null,
	net_days	integer	default null
,constraint dbc_inv_pmt_term_p primary key (id));


create table dbc_invoice (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	creation_date	timestamp	default null,
	last_mod_date	timestamp	default null,
	invoice_number	varchar(40)	default null,
	po_number	varchar(40)	default null,
	req_number	varchar(40)	default null,
	delivery_info	varchar(40)	default null,
	balance_due	numeric(19,7)	default null,
	pmt_due_date	timestamp	default null,
	pmt_terms	varchar(40)	default null,
	order_id	varchar(40)	default null,
	pmt_group_id	varchar(40)	default null
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


