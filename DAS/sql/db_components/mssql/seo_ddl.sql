


--     @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/seo_ddl.xml#2 $  
-- Table for seo-tag repository item

create table das_seo_tag (
	seo_tag_id	varchar(40)	not null,
	display_name	varchar(100)	null,
	title	varchar(254)	null,
	description	varchar(254)	null,
	keywords	varchar(254)	null,
	content_key	varchar(100)	null
,constraint das_seo_tag_pk primary key (seo_tag_id))


create table das_seo_sites (
	seo_tag_id	varchar(40)	not null,
	site_id	varchar(40)	not null
,constraint das_seo_site_pk primary key (seo_tag_id,site_id))



go
