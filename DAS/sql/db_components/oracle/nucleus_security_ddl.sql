


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/nucleus_security_ddl.xml#2 $$Change: 651448 $

create table das_nucl_sec (
	func_name	varchar2(254)	not null,
	policy	varchar2(254)	not null
,constraint func_name_pk primary key (func_name));


create table das_ns_acls (
	id	varchar2(254)	not null,
	index_num	number(10)	not null,
	acl	varchar2(254)	not null
,constraint id_index_pk primary key (id,index_num));




