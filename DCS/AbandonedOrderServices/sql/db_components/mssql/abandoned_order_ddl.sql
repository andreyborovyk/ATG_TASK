


-- $Id: //product/DCS/version/10.0.3/templates/DCS/AbandonedOrderServices/sql/abandoned_order_ddl.xml#1 $

create table dcspp_ord_abandon (
	abandonment_id	varchar(40)	not null,
	version	integer	not null,
	order_id	varchar(40)	not null,
	ord_last_updated	datetime	null,
	abandon_state	varchar(40)	null,
	abandonment_count	integer	null,
	abandonment_date	datetime	null,
	reanimation_date	datetime	null,
	convert_date	datetime	null,
	lost_date	datetime	null
,constraint dcspp_ord_abndn_p primary key (abandonment_id))

create index dcspp_ordabandn1_x on dcspp_ord_abandon (order_id)

create table dcs_user_abandoned (
	id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	profile_id	varchar(40)	not null
,constraint dcs_usr_abndnd_p primary key (id))


create table drpt_conv_order (
	order_id	varchar(40)	not null,
	converted_date	datetime	not null,
	amount	numeric(19,7)	not null,
	promo_count	integer	not null,
	promo_value	numeric(19,7)	not null
,constraint drpt_conv_order_p primary key (order_id))


create table drpt_session_ord (
	dataset_id	varchar(40)	not null,
	order_id	varchar(40)	not null,
	date_time	datetime	not null,
	amount	numeric(19,7)	not null,
	submitted	integer	not null,
	order_persistent	tinyint	null,
	session_id	varchar(40)	null,
	parent_session_id	varchar(40)	null
,constraint drpt_session_ord_p primary key (order_id))



go
