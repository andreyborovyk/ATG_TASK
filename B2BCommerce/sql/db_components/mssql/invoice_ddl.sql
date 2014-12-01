


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/invoice_ddl.xml#2 $$Change: 651448 $

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
,constraint dbc_inv_delivery_p primary key (id))


create table dbc_inv_pmt_terms (
	id	varchar(40)	not null,
	version	integer	not null,
	type	integer	not null,
	disc_percent	numeric(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_inv_pmt_term_p primary key (id))


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
,constraint dbc_invcpmt_term_f foreign key (pmt_terms) references dbc_inv_pmt_terms (id))

create index dbc_inv_dlivr_info on dbc_invoice (delivery_info)
create index dbc_inv_pmt_terms on dbc_invoice (pmt_terms)
create index inv_inv_idx on dbc_invoice (invoice_number)
create index inv_order_idx on dbc_invoice (order_id)
create index inv_pmt_idx on dbc_invoice (pmt_group_id)
create index inv_po_idx on dbc_invoice (po_number)


go
