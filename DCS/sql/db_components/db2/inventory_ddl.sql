


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/inventory_ddl.xml#2 $$Change: 651448 $

create table dcs_inventory (
	inventory_id	varchar(40)	not null,
	version	integer	not null,
	inventory_lock	varchar(20)	default null,
	creation_date	timestamp	default null,
	start_date	timestamp	default null,
	end_date	timestamp	default null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null,
	catalog_ref_id	varchar(40)	not null,
	avail_status	integer	not null,
	availability_date	timestamp	default null,
	stock_level	integer	default null,
	backorder_level	integer	default null,
	preorder_level	integer	default null,
	stock_thresh	integer	default null,
	backorder_thresh	integer	default null,
	preorder_thresh	integer	default null
,constraint dcs_inventory_p primary key (inventory_id)
,constraint inv_catrefid_idx unique (catalog_ref_id));

create index inv_end_dte_idx on dcs_inventory (end_date);
create index inv_strt_dte_idx on dcs_inventory (start_date);
commit;


