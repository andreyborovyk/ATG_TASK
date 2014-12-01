


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/contracts_ddl.xml#2 $$Change: 651448 $
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




