


-- @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/sql/ddlgen/storetext_ddl.xml#2 $$Change: 651448 $

create table crs_store_text (
	text_id	varchar(40)	not null,
	key_id	varchar(254)	not null,
	text_template	varchar(4000)	null,
	tag	varchar(40)	null,
	text_type	integer	null
,constraint crs_txt_key_p primary key (text_id,key_id))

create index crs_txt_key_id on crs_store_text (key_id)

create table crs_store_long_txt (
	text_id	varchar(40)	not null,
	text_template	text	null
,constraint crs_lng_txt_key_p primary key (text_id))



go
