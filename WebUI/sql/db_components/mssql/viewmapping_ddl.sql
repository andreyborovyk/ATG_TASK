


--     @version $Id: //product/WebUI/version/10.0.3/src/sql/viewmapping_ddl.xml#2 $$Change: 651448 $  
--     ItemMapping    Mapps a component path + item name to that item's views.  

create table vmap_im (
	id	varchar(40)	not null,
	name	varchar(64)	null,
	description	varchar(1024)	null,
	item_path	varchar(255)	not null,
	item_name	varchar(64)	not null,
	is_readonly	numeric(1)	null,
	form_handler	varchar(40)	null,
	mode_id	varchar(40)	not null
,constraint vmap_im_pk primary key (id))

create index vmap_im_item on vmap_im (item_path,item_name,name)
--     FormHandler  

create table vmap_fh (
	id	varchar(40)	not null,
	name	varchar(64)	null,
	description	varchar(2048)	null,
	component_path	varchar(1024)	null
,constraint vmap_fh_pk primary key (id))

--     MapMode  

create table vmap_mode (
	id	varchar(40)	not null,
	name	varchar(64)	not null,
	description	varchar(1024)	null,
	fallback_id	varchar(40)	null)

--     ItemViewMapping  

create table vmap_ivm (
	id	varchar(40)	not null,
	name	varchar(64)	not null,
	display_name	varchar(64)	not null,
	description	varchar(1024)	null,
	view_id	varchar(40)	null
,constraint vmap_ivm_pk primary key (id))

--     ItemMapping -> view ItemViewMapping relation  

create table vmap_im2ivm_rel (
	item_id	varchar(40)	not null,
	sequence_num	integer	not null,
	view_id	varchar(40)	not null
,constraint vmap_im2ivm_pk primary key (item_id,sequence_num))

--     ItemView  

create table vmap_iv (
	id	varchar(40)	not null,
	name	varchar(64)	not null,
	description	varchar(1024)	not null,
	uri	varchar(255)	null,
	view_component	varchar(255)	null,
	app_name	varchar(255)	null,
	mode_id	varchar(40)	not null
,constraint vmap_iv_pk primary key (id))

--     PropertyView  

create table vmap_pv (
	id	varchar(40)	not null,
	name	varchar(64)	not null,
	type	varchar(128)	not null,
	component_type	varchar(128)	null,
	description	varchar(1024)	not null,
	uri	varchar(255)	null,
	view_component	varchar(255)	null,
	app_name	varchar(255)	null,
	mode_id	varchar(40)	not null,
	container_family	varchar(128)	null,
	is_default	numeric(1)	null,
	is_readonly	numeric(1)	null,
	is_component	numeric(1)	null
,constraint vmap_pv_pk primary key (id))

create index vmap_pv_unique on vmap_pv (type,name,mode_id,is_component)
--     The relationship between an ItemViewMapping and its    PropertyViewMapping items  

create table vmap_ivm2pvm_rel (
	ivm_id	varchar(40)	not null,
	pvm_id	varchar(40)	not null,
	name	varchar(64)	not null)

--     PropertyViewMapping  

create table vmap_pvm (
	id	varchar(40)	not null,
	description	varchar(1024)	null,
	pview_id	varchar(40)	null,
	cpview_id	varchar(40)	null
,constraint vmap_pvm_pk primary key (id))

--     AttributeValue  

create table vmap_attrval (
	id	varchar(40)	not null,
	attr_value	varchar(2048)	null
,constraint vmap_attrval_pk primary key (id))

--     A table that provides the mapping between mapping items    and AttributeValue items  

create table vmap_attrval_rel (
	mapper_id	varchar(40)	not null,
	attribute_id	varchar(40)	not null,
	name	varchar(64)	not null
,constraint vmap_attvl_rel_pk primary key (mapper_id,attribute_id))

--     A table that provides the mapping between mapping items    and component AttributeValue items  

create table vmap_cattrval_rel (
	mapper_id	varchar(40)	not null,
	attribute_id	varchar(40)	not null,
	name	varchar(64)	not null
,constraint vmap_ctvl_rel_pk primary key (mapper_id,attribute_id))

--     ItemView -> ItemViewAttributeDefinition relation  

create table vmap_iv2ivad_rel (
	view_id	varchar(40)	not null,
	attr_id	varchar(40)	not null,
	name	varchar(64)	not null
,constraint vmap_iv2iva_pk primary key (view_id,name))

--     ItemViewAttributeDefinition  

create table vmap_ivattrdef (
	id	varchar(40)	not null,
	description	varchar(1024)	null,
	default_value	varchar(1024)	null
,constraint vmap_ivatdef_pk primary key (id))

--     PropertyView -> PropertyViewAttributeDefinition relation  

create table vmap_pv2pvad_rel (
	view_id	varchar(40)	not null,
	attr_id	varchar(40)	not null,
	name	varchar(64)	not null)

--     PropertyViewAttributeDefinition  

create table vmap_pvattrdef (
	id	varchar(40)	not null,
	description	varchar(1024)	null,
	default_value	varchar(1024)	null
,constraint vmap_pvatdef_pk primary key (id))



go
