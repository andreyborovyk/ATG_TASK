


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/dcs_mappers.xml#2 $$Change: 651448 $

create table dcs_cart_event (
	id	varchar(40)	not null,
	timestamp	datetime	null,
	orderid	varchar(40)	null,
	itemid	varchar(40)	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	quantity	integer	null,
	amount	smallmoney	null,
	profileid	varchar(40)	null)


create table dcs_submt_ord_evt (
	id	varchar(40)	not null,
	clocktime	datetime	null,
	orderid	varchar(40)	null,
	profileid	varchar(40)	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null)


create table dcs_prom_used_evt (
	id	varchar(40)	not null,
	clocktime	datetime	null,
	orderid	varchar(40)	null,
	profileid	varchar(40)	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	promotionid	varchar(40)	null,
	order_amount	numeric(26,7)	null,
	discount	numeric(26,7)	null)


create table dcs_ord_merge_evt (
	id	varchar(40)	not null,
	clocktime	datetime	null,
	sourceorderid	varchar(40)	null,
	destorderid	varchar(40)	null,
	profileid	varchar(40)	null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null,
	sourceremoved	tinyint	null
,constraint dcs_ordmergeevt_ck check (sourceremoved in (0,1)))


create table dcs_promo_rvkd (
	id	varchar(40)	not null,
	time_stamp	datetime	null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null)


create table dcs_promo_grntd (
	id	varchar(40)	not null,
	time_stamp	datetime	null,
	promotionid	varchar(254)	not null,
	profileid	varchar(254)	not null,
	sessionid	varchar(100)	null,
	parentsessionid	varchar(100)	null)



go
