


-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/International/sql/db_components/db2/promotion_i18n_ddl.sql#3 $$Change: 635816 $

create table crs_prm_xlate (
	translation_id	varchar(40)	not null,
	display_name	varchar(254)	default null,
	description	varchar(254)	default null
,constraint crs_prm_xlate_p primary key (translation_id));


create table crs_prm_prm_xlate (
	promotion_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_prm_prm_xlt_p primary key (promotion_id,locale)
,constraint crs_prm_xlate_f foreign key (translation_id) references crs_prm_xlate (translation_id));

create index crs_prm_xlt_tr_id on crs_prm_prm_xlate (translation_id);

create table crs_cq_xlate (
	translation_id	varchar(40)	not null,
	name	varchar(254)	default null
,constraint crs_cq_xlate_p primary key (translation_id));


create table crs_cq_cq_xlate (
	close_qualif_id	varchar(40)	not null,
	locale	varchar(40)	not null,
	translation_id	varchar(40)	not null
,constraint crs_cq_cq_xlt_p primary key (close_qualif_id,locale)
,constraint crs_cq_cq_xlate_f foreign key (translation_id) references crs_cq_xlate (translation_id));

create index crs_cq_xlt_tr_id on crs_cq_cq_xlate (translation_id);
commit;


