
-- the source for this section is 
-- drop_versioned_site_ddl.sql



drop table site_group_shareable_types;
drop table site_group_sites;
drop table site_group;
drop table site_types;
drop table site_additional_urls;
drop table site_configuration;
drop table site_template;





-- the source for this section is 
-- drop_versioned_seo_ddl.sql



drop table das_seo_sites;
drop table das_seo_tag;





-- the source for this section is 
-- drop_sitemap_ddl.sql



drop table das_sitemap;
drop table das_siteindex;





-- the source for this section is 
-- drop_deployment_ddl.sql



drop table das_dep_fail_info;
drop table das_dd_markers;
drop table das_depl_item_ref;
drop table das_depl_repmaps;
drop table das_depl_options;
drop table das_depl_depldat;
drop table das_file_mark;
drop table das_rep_mark;
drop table das_deploy_mark;
drop table das_deploy_data;
drop table das_thread_batch;
drop table das_depl_progress;
drop table das_deployment;





-- the source for this section is 
-- drop_media.sql



drop table media_txt;
drop table media_bin;
drop table media_ext;
drop table media_base;
drop table media_folder;





-- the source for this section is 
-- drop_nucleus_security_ddl.sql



drop table das_ns_acls;
drop table das_nucl_sec;





-- the source for this section is 
-- drop_integration_data_ddl.sql



drop table if_integ_data;





-- the source for this section is 
-- drop_sds.sql



drop table das_sds;





-- the source for this section is 
-- drop_gsa_subscribers_ddl.sql



drop table das_gsa_subscriber;





-- the source for this section is 
-- drop_staff_ddl.sql



drop table das_acct_prevpwd;
drop table das_group_assoc;
drop table das_account;





-- the source for this section is 
-- drop_sql_jms_ddl.sql



drop procedure dms_topic_flag;
drop procedure dms_queue_flag;
drop table dms_msg_properties;
drop table dms_msg;
drop table dms_topic_entry;
drop table dms_topic_sub;
drop table dms_topic;
drop table dms_queue_entry;
drop table dms_queue_recv;
drop table dms_queue;
drop table dms_client;





-- the source for this section is 
-- drop_dms_limbo_ddl.sql



drop table dms_limbo_delay;
drop table dms_limbo_ptypes;
drop table dms_limbo_props;
drop table dms_limbo_body;
drop table dms_limbo_replyto;
drop table dms_limbo_msg;
drop table dms_limbo;





-- the source for this section is 
-- drop_cluster_name_ddl.sql



drop table das_cluster_name;





-- the source for this section is 
-- drop_id_generator.sql



drop table das_secure_id_gen;
drop table das_id_generator;




