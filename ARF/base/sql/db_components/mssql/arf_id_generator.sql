


--  @version $Id: //product/ARF/version/10.0.3/ARF/base/sql/xml/arf_id_generator.xml#2 $$Change: 651448 $

create table arf_id_generator (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	prefix	varchar(10)	null,
	suffix	varchar(10)	null
,constraint arf_id_generator_p primary key (id_space_name))


create table arf_secure_id_gen (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	ids_per_batch	integer	null,
	prefix	varchar(10)	null,
	suffix	varchar(10)	null
,constraint arf_secure_id_ge_p primary key (id_space_name))



go
