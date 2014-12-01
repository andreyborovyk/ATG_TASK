


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/B2BCommerce/sql/b2b_product_catalog_ddl.xml#2 $$Change: 651448 $

create table dbc_manufacturer (
	manufacturer_id	varchar(40)	not null,
	manufacturer_name	varchar(254)	null,
	description	varchar(254)	null,
	long_description	text	null,
	email	varchar(255)	null
,constraint dbc_manufacturer_p primary key (manufacturer_id))

create index dbc_man_name_idx on dbc_manufacturer (manufacturer_name)

create table dbc_measurement (
	sku_id	varchar(40)	not null,
	unit_of_measure	integer	null,
	quantity	numeric(19,7)	null
,constraint dbc_measurement_p primary key (sku_id))


create table dbc_product (
	product_id	varchar(40)	not null,
	manufacturer	varchar(40)	null
,constraint dbc_product_p primary key (product_id)
,constraint dbc_prodmanfctrr_f foreign key (manufacturer) references dbc_manufacturer (manufacturer_id)
,constraint dbc_prodprodct_d_f foreign key (product_id) references dcs_product (product_id))

create index dbc_prd_man_idx on dbc_product (manufacturer)

create table dbc_sku (
	sku_id	varchar(40)	not null,
	manuf_part_num	varchar(254)	null
,constraint dbc_sku_p primary key (sku_id)
,constraint dbc_skusku_d_f foreign key (sku_id) references dcs_sku (sku_id))

create index dbc_sku_prtnum_idx on dbc_sku (manuf_part_num)


go
