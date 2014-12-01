


--  @version $Id: //product/ARF/version/10.0.3/ARF/base/sql/xml/arf_id_generator.xml#2 $$Change: 651448 $

create table arf_id_generator (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	prefix	varchar(10)	default null,
	suffix	varchar(10)	default null
,constraint arf_id_generator_p primary key (id_space_name));


create table arf_secure_id_gen (
	id_space_name	varchar(60)	not null,
	seed	numeric(19,0)	not null,
	batch_size	integer	not null,
	ids_per_batch	integer	default null,
	prefix	varchar(10)	default null,
	suffix	varchar(10)	default null
,constraint arf_secure_id_ge_p primary key (id_space_name));

commit;





-- 
-- Data Warehouse Loader Control Tables
-- $Id: //product/ARF/version/10.0.3/ARF/base/sql/xml/arf_loader_ddl.xml#2 $
-- 

create table ARF_LOADER_QUEUE (
	LOADER_QUEUE_ID	varchar(40)	not null,
	NAME	varchar(254)	not null
,constraint ARF_LQ_P primary key (LOADER_QUEUE_ID));


create table ARF_QUEUE_ENTRY (
	QUEUE_ENTRY_ID	varchar(40)	not null,
	LOADER_QUEUE_ID	varchar(40)	not null,
	FILENAME	varchar(254)	not null,
	CREATION_DATE	timestamp	not null
,constraint ARF_QE_P primary key (QUEUE_ENTRY_ID)
,constraint ARF_QUEUE_ENTRY_F1 foreign key (LOADER_QUEUE_ID) references ARF_LOADER_QUEUE (LOADER_QUEUE_ID));

create index ARF_QUEUE_ENTRY1_X on ARF_QUEUE_ENTRY (LOADER_QUEUE_ID);

create table ARF_LQ_ENTRIES (
	LOADER_QUEUE_ID	varchar(40)	not null,
	QUEUE_ENTRY_ID	varchar(40)	not null
,constraint ARF_LQE_F1 foreign key (LOADER_QUEUE_ID) references ARF_LOADER_QUEUE (LOADER_QUEUE_ID)
,constraint ARF_LQE_F2 foreign key (QUEUE_ENTRY_ID) references ARF_QUEUE_ENTRY (QUEUE_ENTRY_ID));

create index ARF_LQE_X1 on ARF_LQ_ENTRIES (LOADER_QUEUE_ID);
create index ARF_LQE_X2 on ARF_LQ_ENTRIES (QUEUE_ENTRY_ID);

create table ARF_LOADER_PROG (
	LOADER_PROGRESS_ID	varchar(40)	not null,
	FILENAME	varchar(254)	not null,
	LOADER_QUEUE_ID	varchar(40)	not null,
	START_TIMESTAMP	timestamp	default null,
	END_TIMESTAMP	timestamp	default null,
	STATUS	integer	not null,
	STATUS_DETAIL	varchar(4096)	default null,
	OWNER	varchar(254)	default null,
	RECORDS_PROCESSED	integer	not null
,constraint ARF_LP_P primary key (LOADER_PROGRESS_ID)
,constraint ARF_LOADER_PROG_F1 foreign key (LOADER_QUEUE_ID) references ARF_LOADER_QUEUE (LOADER_QUEUE_ID));

create index ARF_LOADER_PROG1_X on ARF_LOADER_PROG (LOADER_QUEUE_ID);
commit;


