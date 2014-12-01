


--  @version $Id: //product/DAF/version/10.0.3/Search/Base/src/sql/search_site_ddl.xml#2 $$Change: 651448 $
-- Table containing site to search content set assocaitions

create table srch_site_cntnt_pri (
	id	varchar(40)	not null,
	content_id	varchar(40)	not null
,constraint srch_site_cntnt_pp primary key (id))


create table srch_site_cntnt (
	cntn_id	varchar(40)	not null,
	site_id	varchar(40)	not null,
	content_label	varchar(40)	not null
,constraint srch_site_cntnt_p primary key (cntn_id))



go
