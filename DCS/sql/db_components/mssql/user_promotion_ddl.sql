


--  @version $Id: //product/DCS/version/10.0.3/templates/DCS/sql/user_promotion_ddl.xml#2 $$Change: 651448 $
-- The promotion line was commented out to allow the profile and promotion tables to be delinked. The promotion tables are intended to be used as a "read-only" table on the production servers. The promotion (and product catalog) tables are promoted and made active on the production system through copy-switch. In doing so, the profile tables and the promotion tables cannot be in the same database, and therefore we must remove this referece. However if you are not going to use copy-switch for the promotions, then you can add this reference back in.     promotion			VARCHAR(40)		NOT NULL	REFERENCES dcs_promotion(promotion_id),

create table dcs_usr_promostat (
	status_id	varchar(40)	not null,
	profile_id	varchar(40)	not null,
	promotion	varchar(40)	not null,
	num_uses	integer	null,
	expirationDate	datetime	null,
	granted_date	datetime	null
,constraint dcs_usr_promosta_p primary key (status_id))

create index promostat_prof_idx on dcs_usr_promostat (profile_id)
create index usr_prmstat_pr_idx on dcs_usr_promostat (promotion)

create table dcs_usr_actvpromo (
	id	varchar(40)	not null,
	sequence_num	integer	not null,
	promo_status_id	varchar(40)	not null
,constraint dcs_usr_actvprom_p primary key (id,sequence_num))

create index usr_actvprm_id_idx on dcs_usr_actvpromo (id)
-- The promotion_id column was commented out to allow the profile and promotion tables to be delinked. The promotion tables are intended to be used as a "read-only" table on the production servers. The promotion (and product catalog) tables are promoted and made active on the production system through copy-switch. In doing so, the profile tables and the promotion tables cannot be in the same database, and therefore we must remove this referece. However if you are not going to use copy-switch for the promotions, then you can add this reference back in.        promotion_id                    VARCHAR(40)             NOT NULL        REFERENCES dcs_promotion(promotion_id),

create table dcs_usr_usedpromo (
	id	varchar(40)	not null,
	promotion_id	varchar(40)	not null
,constraint dcs_usr_usedprom_p primary key (id,promotion_id))

create index usr_usedprm_id_idx on dcs_usr_usedpromo (id)
create index usr_usedprm_pi_idx on dcs_usr_usedpromo (promotion_id)


go
