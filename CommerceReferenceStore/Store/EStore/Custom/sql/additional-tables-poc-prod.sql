-- additional profile repository tables

CREATE TABLE mys_user_subscription (
        id                      VARCHAR(254)    NOT NULL REFERENCES dps_user(id)
,
        subscription_id         VARCHAR(254)    NOT NULL REFERENCES mys_magz_subscription(subscription_id),
        PRIMARY KEY(id, subscription_id));

CREATE TABLE mys_subscription (
        subscription_id         VARCHAR(254)    NOT NULL,
        product_Id              VARCHAR(254)    NULL REFERENCES dcs_product(product_id),
        sku_id                  VARCHAR(254)    NULL REFERENCES dcs_product(product_id),
        start_date              DATE    NULL,
        end_date                DATE    NULL,
        PRIMARY KEY(subscription_id));


-- additional product catalog tables

CREATE TABLE mys_subs_product (
        id                      VARCHAR(254)    NOT NULL REFERENCES dcs_product(product_id),
        frequency               INTEGER NULL,
        no_of_issues            INTEGER NULL,
        PRIMARY KEY(id));


CREATE TABLE mys_subs_sku (
        id                      VARCHAR(254)    NOT NULL REFERENCES dcs_sku(sku_id),
        duration                INTEGER NULL,        PRIMARY KEY(id));