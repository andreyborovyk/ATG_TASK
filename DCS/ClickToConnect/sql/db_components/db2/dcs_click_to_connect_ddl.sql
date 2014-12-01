



create table dcs_c2c_session_data (
	id	varchar(60)	not null,
	order_id	varchar(40)	not null,
	price_list_id	varchar(40)	default null,
	sale_price_list_id	varchar(40)	default null,
	catalog_id	varchar(40)	default null
,constraint dcs_c2c_id_p primary key (id)
,constraint dcs_c2c_id_f foreign key (id) references c2c_session_data (c2c_session_id));


create table dcs_c2c_active_promo (
	id	varchar(60)	not null,
	promotion_id	varchar(40)	not null
,constraint dcs_c2c_promo_id_p primary key (id,promotion_id)
,constraint dcs_c2c_promo_id_f foreign key (id) references c2c_session_data (c2c_session_id));

commit;


