


--  @version $Id: //product/Publishing/version/10.0.3/pws/sql/xml/internal_user_profile_ddl.xml#2 $
--     The tables here are for the user profile extensions needed by EPublishing.  

create table epub_int_user (
	user_id	varchar2(40)	not null,
	title	varchar2(255)	null,
	expert	number(1)	null,
	def_listing	number(19)	null,
	def_ip_listing	number(19)	null,
	allow_applets	number(1)	null
,constraint epub_int_user_pk primary key (user_id));


create table epub_int_prj_hist (
	user_id	varchar2(40)	not null,
	sequence_num	number(19)	not null,
	project_id	varchar2(40)	not null
,constraint user_i_prj_hist_pk primary key (user_id,sequence_num)
,constraint user_i_prj_hist_fk foreign key (user_id) references dpi_user (id));




