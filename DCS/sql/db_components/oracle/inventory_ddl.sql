


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/inventory_ddl.xml#2 $$Change: 651448 $

create table dcs_inventory (
	inventory_id	varchar2(40)	not null,
	version	integer	not null,
	inventory_lock	varchar2(20)	null,
	creation_date	timestamp	null,
	start_date	timestamp	null,
	end_date	timestamp	null,
	display_name	varchar2(254)	null,
	description	varchar2(254)	null,
	catalog_ref_id	varchar2(40)	not null,
	avail_status	integer	not null,
	availability_date	timestamp	null,
	stock_level	integer	null,
	backorder_level	integer	null,
	preorder_level	integer	null,
	stock_thresh	integer	null,
	backorder_thresh	integer	null,
	preorder_thresh	integer	null
,constraint dcs_inventory_p primary key (inventory_id)
,constraint inv_catrefid_idx unique (catalog_ref_id));

create index inv_end_dte_idx on dcs_inventory (end_date);
create index inv_strt_dte_idx on dcs_inventory (start_date);



