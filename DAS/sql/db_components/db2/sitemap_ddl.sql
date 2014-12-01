


--     @version $Id:    //product/DAF/Sitemap/main/templates/DAS/sql/deployment_ddl.xml#26 $  
-- Table for siteindex repository item

create table das_siteindex (
	siteindex_id	varchar(40)	not null,
	lastmod	timestamp	default null,
	filename	varchar(100)	default null,
	xml	clob	default null
,constraint siteindex_pk primary key (siteindex_id));

-- Table for sitemap repository item

create table das_sitemap (
	sitemap_id	varchar(40)	not null,
	lastmod	timestamp	default null,
	filename	varchar(100)	default null,
	xml	clob	default null
,constraint sitemap_pk primary key (sitemap_id));

commit;


