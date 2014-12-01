


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/inventory_ddl.xml#2 $$Change: 651448 $

create table dcs_inventory (
	inventory_id	varchar(40)	not null,
	version	integer	not null,
	inventory_lock	varchar(20)	null,
	creation_date	datetime	null,
	start_date	datetime	null,
	end_date	datetime	null,
	display_name	varchar(254)	null,
	description	varchar(254)	null,
	catalog_ref_id	varchar(40)	not null,
	avail_status	integer	not null,
	availability_date	datetime	null,
	stock_level	integer	null,
	backorder_level	integer	null,
	preorder_level	integer	null,
	stock_thresh	integer	null,
	backorder_thresh	integer	null,
	preorder_thresh	integer	null
,constraint dcs_inventory_p primary key (inventory_id)
,constraint inv_catrefid_idx unique (catalog_ref_id))

create index inv_end_dte_idx on dcs_inventory (end_date)
create index inv_strt_dte_idx on dcs_inventory (start_date)


go
