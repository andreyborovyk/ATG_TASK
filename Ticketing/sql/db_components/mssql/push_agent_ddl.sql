


--  Linking table for agent's push properties @version $Id: //product/Ticketing/version/10.0.3/src/sql/push_agent_ddl.xml#2 $$Change: 651448 $

create table tkt_push_agent (
	id	varchar(40)	not null,
	push_agent	tinyint	null,
	push_load_level	integer	null,
	push_auto_in	tinyint	null
,constraint push_agent_p primary key (id)
,constraint push_agent_user_f foreign key (id) references dpi_user (id))

--     Linking table for organizations' ticketQueues property  

create table tkt_org_tktqs (
	org_id	varchar(40)	not null,
	tkt_q	varchar(40)	not null)



go
