
-- the source for this section is 
-- logical_org_ddl.sql




-- This file contains create table statements, which will configure your database for use with logical organizations.
-- Add the logical organization definition. This is anamed "virtual" organization which refers to a normal(internal user) organization. It's defined in a separatenamespace so that is can be accessed from an external users, soat least the logical organization name can be displayed.

create table dlo_logical_org (
	logical_org_id	varchar2(40)	not null,
	type	number(10)	null,
	phys_org_id	varchar2(40)	null,
	name	varchar2(254)	not null,
	phys_org_name	varchar2(254)	not null
,constraint dlo_logical_org_p primary key (logical_org_id));




