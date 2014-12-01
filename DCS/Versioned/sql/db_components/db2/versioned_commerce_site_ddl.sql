


-- This file contains create table statements, which will configureyour database for use MultiSite

create table dcs_site (
	asset_version	numeric(19)	not null,
	id	varchar(40)	not null,
	catalog_id	varchar(40)	default null,
	list_pricelist_id	varchar(40)	default null,
	sale_pricelist_id	varchar(40)	default null
,constraint dcs_site_p primary key (id,asset_version));

commit;


