


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/solid/crs_order_ddl.sql#3 $$Change: 635816 $

create table crs_order (
	order_id	varchar(40)	not null,
	coupon_code	varchar(40)	null,
	oms_order_id	varchar(40)	null,
	status_change_date	timestamp	null,
	processing_date	timestamp	null
, primary key (order_id)
, foreign key (order_id) references dcspp_order (order_id));

alter table crs_order set pessimistic;


create table crs_hrd_ship_group (
	shipping_group_id	varchar(40)	not null,
	carrier_code	varchar(40)	null,
	weight	double precision	null
, primary key (shipping_group_id)
, foreign key (shipping_group_id) references dcspp_ship_group (shipping_group_id));

alter table crs_hrd_ship_group set pessimistic;


create table crs_order_sub_req (
	order_sub_req_id	varchar(40)	not null,
	order_ack_time_out	integer	null,
	max_order_submits	integer	null,
	client_id	varchar(40)	null
, primary key (order_sub_req_id));

alter table crs_order_sub_req set pessimistic;


create table crs_oms_segments (
	order_id	varchar(40)	not null,
	sequence_num	integer	not null,
	oms_segment	wvarchar(4000)	null
, primary key (order_id,sequence_num)
, foreign key (order_id) references dcspp_order (order_id));

alter table crs_oms_segments set pessimistic;


create table crs_item_price (
	item_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	null
, primary key (item_price_info_id)
, foreign key (item_price_info_id) references dcspp_amount_info (amount_info_id));

alter table crs_item_price set pessimistic;


create table crs_tax_price (
	amount_info_id	varchar(40)	not null,
	district_tax	double precision	null
, primary key (amount_info_id)
, foreign key (amount_info_id) references dcspp_amount_info (amount_info_id));

alter table crs_tax_price set pessimistic;


create table crs_ship_price (
	ship_price_info_id	varchar(40)	not null,
	tax_price_info_id	varchar(40)	null
, primary key (ship_price_info_id)
, foreign key (ship_price_info_id) references dcspp_amount_info (amount_info_id));

alter table crs_ship_price set pessimistic;


create table crs_tracking_info (
	tracking_info_id	varchar(40)	not null,
	shipping_method	varchar(40)	null,
	carrier_code	varchar(40)	null,
	tracking_number	varchar(40)	null,
	url	varchar(400)	null,
	weight	double precision	null
, primary key (tracking_info_id));

alter table crs_tracking_info set pessimistic;


create table crs_ord_track_info (
	order_id	varchar(40)	not null,
	sequence_num	integer	not null,
	tracking_info_id	varchar(40)	not null
, primary key (order_id,sequence_num)
, foreign key (order_id) references dcspp_order (order_id)
, foreign key (tracking_info_id) references crs_tracking_info (tracking_info_id));

create index crs_ordtrk_tkid1_x on crs_ord_track_info (tracking_info_id);
alter table crs_ord_track_info set pessimistic;

commit work;


