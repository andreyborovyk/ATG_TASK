


--  @version $Id: //product/B2BCommerce/version/10.0.3/templates/MotorpriseJSP/sql/b2b_custom_catalog_ddl.xml#2 $$Change: 651448 $

create table b2b_belt_sku (
	sku_id	varchar(40)	not null,
	length	nvarchar(254)	null,
	top_width	nvarchar(254)	null,
	angle	nvarchar(254)	null,
	notched	tinyint	null
,constraint b2b_belt_sku_p primary key (sku_id)
,constraint b2b_beltsksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_hose_sku (
	sku_id	varchar(40)	not null,
	inner_diameter	nvarchar(254)	null,
	length	nvarchar(254)	null
,constraint b2b_hose_sku_p primary key (sku_id)
,constraint b2b_hossksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_sprkplg_sku (
	sku_id	varchar(40)	not null,
	plug_number	nvarchar(254)	null,
	thread	nvarchar(254)	null
,constraint b2b_sprkplg_sku_p primary key (sku_id)
,constraint b2b_sprksku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_oilfltr_sku (
	sku_id	varchar(40)	not null,
	thread_type	nvarchar(254)	null,
	length	nvarchar(254)	null,
	outer_diameter	nvarchar(254)	null
,constraint b2b_oilfltr_sku_p primary key (sku_id)
,constraint b2b_olflsku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_cylinder_sku (
	sku_id	varchar(40)	not null,
	diameter	nvarchar(254)	null,
	height	nvarchar(254)	null
,constraint b2b_cylinder_sku_p primary key (sku_id)
,constraint b2b_cylisku_d_f foreign key (sku_id) references dcs_sku (sku_id));


create table b2b_cube_sku (
	sku_id	varchar(40)	not null,
	height	nvarchar(254)	null,
	width	nvarchar(254)	null,
	depth	nvarchar(254)	null
,constraint b2b_cube_sku_p primary key (sku_id)
,constraint b2b_cubsksku_d_f foreign key (sku_id) references dcs_sku (sku_id));

commit;


