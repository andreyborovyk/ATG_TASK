


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/dcs_mappers.xml#2 $$Change: 651448 $

create table dcs_cart_event (
	id	varchar(40)	not null,
	timestamp	timestamp	default null,
	orderid	varchar(40)	default null,
	itemid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	quantity	integer	default null,
	amount	numeric(19,7)	default null,
	profileid	varchar(40)	default null);


create table dcs_submt_ord_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	orderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);


create table dcs_prom_used_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	orderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	promotionid	varchar(40)	default null,
	order_amount	numeric(26,7)	default null,
	discount	numeric(26,7)	default null);


create table dcs_ord_merge_evt (
	id	varchar(40)	not null,
	clocktime	timestamp	default null,
	sourceorderid	varchar(40)	default null,
	destorderid	varchar(40)	default null,
	profileid	varchar(40)	default null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null,
	sourceremoved	numeric(1,0)	default null
,constraint dcs_ordmergeevt_ck check (sourceremoved in (0,1)));


create table dcs_promo_rvkd (
	id	varchar(40)	not null,
	time_stamp	timestamp	default null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);


create table dcs_promo_grntd (
	id	varchar(40)	not null,
	time_stamp	timestamp	default null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	default null,
	parentsessionid	varchar(100)	default null);

commit;


