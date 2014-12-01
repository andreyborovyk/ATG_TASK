


--  @version $Id: //product/DAS/version/10.0.3/templates/DAS/sql/create_staff_ddl.xml#2 $$Change: 651448 $
-- SQL for creating the Dynamo Staff Repository for the GSA
-- Primary account table.
--  Type=1 is a login account.  The first_name, last_name, and password         fields are valid for this type of account.
-- Type=2 is a group account.  The description field is valid for        this type of account.
-- Type=4 is a privilege account.  The description field is valid for        this type of account.

create table das_account (
	account_name	varchar(254)	not null,
	type	integer	not null,
	first_name	varchar(254)	null,
	last_name	varchar(254)	null,
	password	varchar(254)	null,
	description	varchar(254)	null,
	lastpwdupdate	datetime	null
,constraint das_account_p primary key (account_name))


create table das_group_assoc (
	account_name	varchar(254)	not null,
	sequence_num	integer	not null,
	group_name	varchar(254)	not null
,constraint das_grp_asc_p primary key (account_name,sequence_num))

-- Adding the previous password information

create table das_acct_prevpwd (
	account_name	varchar(254)	not null,
	seq_num	integer	not null,
	prevpwd	varchar(35)	null
,constraint das_prevpwd_p primary key (account_name,seq_num)
,constraint das_prvpwd_d_f foreign key (account_name) references das_account (account_name))



go
