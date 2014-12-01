


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/oracle/crs_profile_ddl.sql#3 $$Change: 635816 $

      alter table dps_contact_info modify (phone_number varchar2(40));
    

create table crs_user (
	user_id	varchar2(40)	not null,
	external_id	varchar2(40)	null,
	number_of_orders	number(19,0)	null,
	last_purchase_date	date	null,
	referral_source	integer	null,
	receive_promo_email	integer	null,
	store_id	varchar2(40)	null
,constraint crs_user_p primary key (user_id)
,constraint crs_user_fk foreign key (user_id) references dps_user (id));


create table crs_items_bought (
	user_id	varchar2(40)	not null,
	sequence_id	integer	not null,
	item	varchar2(40)	not null
,constraint crs_item_bought_p primary key (user_id,sequence_id));


create table crs_email_recpient (
	email_recipient_id	varchar2(40)	not null,
	email	varchar2(40)	not null,
	user_id	varchar2(40)	null,
	source_code	varchar2(40)	null
,constraint crs_email_recpnt_p primary key (email_recipient_id));


create table crs_user_shipping (
	user_id	varchar2(40)	not null,
	tag	varchar2(42)	not null,
	addr_id	varchar2(40)	not null
,constraint crs_user_shpping_p primary key (user_id,tag)
,constraint crs_user_shppng_fk foreign key (user_id) references dps_user (id));

create index crs_u_shipaddr_idx on crs_user_shipping (addr_id);

create table crs_back_in_stock (
	id	varchar2(40)	not null,
	email	varchar2(40)	null,
	catalog_ref_id	varchar2(40)	null,
	product_id	varchar2(40)	null,
	site_id	varchar2(40)	null,
	locale	varchar2(40)	null
,constraint crs_back_in_ntfy_p primary key (id));




