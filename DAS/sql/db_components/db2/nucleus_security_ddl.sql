


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/nucleus_security_ddl.xml#2 $$Change: 651448 $

create table das_nucl_sec (
	func_name	varchar(254)	not null,
	policy	varchar(254)	not null
,constraint func_name_pk primary key (func_name));


create table das_ns_acls (
	id	varchar(254)	not null,
	index_num	integer	not null,
	acl	varchar(254)	not null
,constraint id_index_pk primary key (id,index_num));

commit;


