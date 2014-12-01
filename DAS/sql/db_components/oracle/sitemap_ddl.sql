


--     @version $Id:    //product/DAF/Sitemap/main/templates/DAS/sql/deployment_ddl.xml#26 $  
-- Table for siteindex repository item

create table das_siteindex (
	siteindex_id	varchar2(40)	not null,
	lastmod	date	null,
	filename	varchar2(100)	null,
	xml	clob	null
,constraint siteindex_pk primary key (siteindex_id));

-- Table for sitemap repository item

create table das_sitemap (
	sitemap_id	varchar2(40)	not null,
	lastmod	date	null,
	filename	varchar2(100)	null,
	xml	clob	null
,constraint sitemap_pk primary key (sitemap_id));




