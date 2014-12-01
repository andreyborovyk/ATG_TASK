


--  @version $ $

create table dcs_order_markers (
	marker_id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	default null,
	marker_data	varchar(100)	default null,
	creation_date	timestamp	default null,
	version	integer	not null,
	marker_type	integer	default null
,constraint dcsordermarkers_p primary key (marker_id,order_id)
,constraint dcsordermarkers_f foreign key (order_id) references dcspp_order (order_id));

create index dcs_ordrmarkers1_x on dcs_order_markers (order_id);

create table dcspp_commerce_item_markers (
	marker_id	varchar(40)	not null,
	commerce_item_id	varchar(40)	not null,
	marker_key	varchar(100)	not null,
	marker_value	varchar(100)	default null,
	marker_data	varchar(100)	default null,
	creation_date	timestamp	default null,
	version	integer	not null,
	marker_type	integer	default null
,constraint dcscitemmarkers_p primary key (marker_id,commerce_item_id)
,constraint dcscitemmarkers_f foreign key (commerce_item_id) references dcspp_item (commerce_item_id));

create index dcs_itemmarkers1_x on dcspp_commerce_item_markers (commerce_item_id);
commit;


