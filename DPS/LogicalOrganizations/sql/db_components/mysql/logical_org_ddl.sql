


--  @version $Id: //product/DPS/version/10.0.3/templates/DPS/LogicalOrganizations/sql/logical_org_ddl.xml#2 $$Change: 651448 $
-- This file contains create table statements, which will configure your database for use with logical organizations.
-- Add the logical organization definition. This is anamed "virtual" organization which refers to a normal(internal user) organization. It's defined in a separatenamespace so that is can be accessed from an external users, soat least the logical organization name can be displayed.

create table dlo_logical_org (
	logical_org_id	varchar(40)	not null,
	type	integer	null,
	phys_org_id	varchar(40)	null,
	name	nvarchar(254)	not null,
	phys_org_name	nvarchar(254)	not null
,constraint dlo_logical_org_p primary key (logical_org_id));

commit;


