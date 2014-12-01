


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/sql/db_components/oracle/multisite_ddl.sql#3 $$Change: 635816 $

create table crs_site_attribute (
	id	varchar2(40)	not null,
	resource_bundle	varchar2(254)	null,
	prod_threshold	number(10)	null,
	page_size	number(10)	null,
	css_file	varchar2(254)	null,
	large_site_icon	varchar2(254)	null,
	default_country_code	varchar2(2)	null,
	emailafriend	number(1)	null,
	backinstock_addr	varchar2(50)	null,
	newpass_addr	varchar2(50)	null,
	orderconfirm_addr	varchar2(50)	null,
	ordershipped_addr	varchar2(50)	null,
	promo_addr	varchar2(50)	null
,constraint crs_site_attr_p primary key (id));


create table crs_bill_codes (
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_bill_codes_p primary key (id,sequence_num)
,constraint crs_bill_codes_f foreign key (id) references site_configuration (id));


create table crs_non_bill_codes (
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_bill_c_p primary key (id,sequence_num)
,constraint crs_non_bill_c_f foreign key (id) references site_configuration (id));


create table crs_ship_codes (
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_ship_codes_p primary key (id,sequence_num)
,constraint crs_ship_codes_f foreign key (id) references site_configuration (id));


create table crs_non_ship_codes (
	id	varchar2(40)	not null,
	country_codes	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_non_ship_c_p primary key (id,sequence_num)
,constraint crs_non_ship_c_f foreign key (id) references site_configuration (id));


create table crs_site_languages (
	id	varchar2(40)	not null,
	languages	varchar2(40)	not null,
	sequence_num	integer	not null
,constraint crs_site_lang_p primary key (id,sequence_num)
,constraint crs_site_lang_f foreign key (id) references site_configuration (id));




