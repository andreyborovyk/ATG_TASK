


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/solid/crs_profile_ddl.sql#3 $$Change: 635816 $

      alter table dps_contact_info modify phone_number wvarchar(40);
    

create table crs_user (
	user_id	varchar(40)	not null,
	external_id	varchar(40)	null,
	number_of_orders	integer	null,
	last_purchase_date	date	null,
	referral_source	integer	null,
	receive_promo_email	integer	null,
	store_id	varchar(40)	null
, primary key (user_id)
, foreign key (user_id) references dps_user (id));

alter table crs_user set pessimistic;


create table crs_items_bought (
	user_id	varchar(40)	not null,
	sequence_id	integer	not null,
	item	varchar(40)	not null
, primary key (user_id,sequence_id));

alter table crs_items_bought set pessimistic;


create table crs_email_recpient (
	email_recipient_id	varchar(40)	not null,
	email	varchar(40)	not null,
	user_id	varchar(40)	null,
	source_code	varchar(40)	null
, primary key (email_recipient_id));

alter table crs_email_recpient set pessimistic;


create table crs_user_shipping (
	user_id	varchar(40)	not null,
	tag	varchar(42)	not null,
	addr_id	varchar(40)	not null
, primary key (user_id,tag)
, foreign key (user_id) references dps_user (id));

create index crs_u_shipaddr_idx on crs_user_shipping (addr_id);
alter table crs_user_shipping set pessimistic;


create table crs_back_in_stock (
	id	varchar(40)	not null,
	email	varchar(40)	null,
	catalog_ref_id	varchar(40)	null,
	product_id	varchar(40)	null,
	site_id	varchar(40)	null,
	locale	varchar(40)	null
, primary key (id));

alter table crs_back_in_stock set pessimistic;

commit work;


