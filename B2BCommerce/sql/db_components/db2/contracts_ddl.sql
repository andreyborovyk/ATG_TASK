


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/contracts_ddl.xml#2 $$Change: 651448 $
-- Normally, catalog_id and price_list_id would reference the appropriate table it is possible not to use those tables though, which is why the reference is not included

create table dbc_contract (
	contract_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	creator_id	varchar(40)	default null,
	negotiator_info	varchar(40)	default null,
	price_list_id	varchar(40)	default null,
	catalog_id	varchar(40)	default null,
	term_id	varchar(40)	default null,
	comments	varchar(254)	default null
,constraint dbc_contract_p primary key (contract_id));


create table dbc_contract_term (
	terms_id	varchar(40)	not null,
	terms	varchar(20480)	default null,
	disc_percent	numeric(19,7)	default null,
	disc_days	integer	default null,
	net_days	integer	default null
,constraint dbc_contract_ter_p primary key (terms_id));

commit;


