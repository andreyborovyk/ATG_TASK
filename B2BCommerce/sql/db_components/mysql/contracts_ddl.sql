


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/contracts_ddl.xml#2 $$Change: 651448 $
-- Normally, catalog_id and price_list_id would reference the appropriate table it is possible not to use those tables though, which is why the reference is not included

create table dbc_contract (
	contract_id	varchar(40)	not null,
	display_name	nvarchar(254)	null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	creator_id	varchar(40)	null,
	negotiator_info	nvarchar(40)	null,
	price_list_id	varchar(40)	null,
	catalog_id	varchar(40)	null,
	term_id	varchar(40)	null,
	comments	nvarchar(254)	null
,constraint dbc_contract_p primary key (contract_id));


create table dbc_contract_term (
	terms_id	varchar(40)	not null,
	terms	longtext	null,
	disc_percent	numeric(19,7)	null,
	disc_days	integer	null,
	net_days	integer	null
,constraint dbc_contract_ter_p primary key (terms_id));

commit;


