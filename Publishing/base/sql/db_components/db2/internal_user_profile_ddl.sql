


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/internal_user_profile_ddl.xml#2 $
--     The tables here are for the user profile extensions needed by EPublishing.  

create table epub_int_user (
	user_id	varchar(40)	not null,
	title	varchar(255)	default null,
	expert	numeric(1)	default null,
	def_listing	numeric(19)	default null,
	def_ip_listing	numeric(19)	default null,
	allow_applets	numeric(1)	default null
,constraint epub_int_user_pk primary key (user_id));


create table epub_int_prj_hist (
	user_id	varchar(40)	not null,
	sequence_num	numeric(19)	not null,
	project_id	varchar(40)	not null
,constraint user_i_prj_hist_pk primary key (user_id,sequence_num)
,constraint user_i_prj_hist_fk foreign key (user_id) references dpi_user (id));

commit;


