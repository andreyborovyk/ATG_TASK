


--  @version $Id: //app/portal/version/10.0.3/soapclient/sql/soapclient_ddl.xml#2 $$Change: 651448 $

create table soap_serv_conf (
	service_config_id	varchar(40)	not null,
	version	integer	default null,
	target_service_url	varchar(254)	default null,
	target_method_name	varchar(254)	default null,
	namespace_url	varchar(254)	default null,
	soap_action_uri	varchar(254)	default null,
	username	varchar(40)	default null,
	password	varchar(40)	default null
,constraint soap_serv_conf_p primary key (service_config_id));


create table soap_serv_param (
	serv_param_id	varchar(40)	not null,
	param_name	varchar(254)	default null,
	param_type	varchar(40)	default null,
	param_value	varchar(254)	default null,
	version	integer	default null
,constraint soap_serv_param_p primary key (serv_param_id));


create table soap_conf_param (
	service_config_id	varchar(40)	not null,
	sequence_num	integer	not null,
	service_params	varchar(40)	default null
,constraint soap_conf_param_p primary key (service_config_id,sequence_num)
,constraint soap_conf_param1_f foreign key (service_config_id) references soap_serv_conf (service_config_id)
,constraint soap_conf_param2_f foreign key (service_params) references soap_serv_param (serv_param_id));

create index soap_conf_param1_i on soap_conf_param (service_params);

create table soap_install_conf (
	install_serv_id	varchar(40)	not null,
	gear_def_id	varchar(40)	not null,
	service_config_id	varchar(40)	default null,
	version	integer	default null
,constraint soap_install_con_p primary key (install_serv_id)
,constraint soap_install_con_u unique (gear_def_id));


create table soap_instn_conf (
	instn_serv_id	varchar(40)	not null,
	gear_instance_id	varchar(40)	not null,
	service_config_id	varchar(40)	default null,
	version	integer	default null
,constraint soap_instn_conf_p primary key (instn_serv_id)
,constraint soap_instn_conf_u unique (gear_instance_id));


create table soap_user_conf (
	user_serv_id	varchar(40)	not null,
	user_id	varchar(40)	default null,
	gear_def_id	varchar(40)	default null,
	version	integer	default null,
	gear_instance_id	varchar(40)	default null,
	service_config_id	varchar(40)	default null
,constraint soap_user_conf_p primary key (user_serv_id));

create index soap_user_conf_idx on soap_user_conf (user_id,gear_instance_id);
commit;


