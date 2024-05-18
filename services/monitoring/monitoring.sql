CREATE DATABASE location_tracking_module;

CREATE TABLE api_price_group (
                                 id BIGSERIAL PRIMARY KEY,
                                 group_name VARCHAR(200) NOT NULL,
                                 CONSTRAINT unique_group_name UNIQUE (group_name)
);

CREATE TABLE api_price (
                           api_price_id BIGSERIAL PRIMARY KEY,
                           start_date DATE default NULL,
                           end_date DATE default NULL,
                           cost_won BIGINT NOT NULL,
                           priority SMALLINT NOT NULL,
                           api_price_group_id BIGINT NOT NULL,
                           CONSTRAINT fk_api_price_group FOREIGN KEY (api_price_group_id) REFERENCES api_price_group(id)
);

CREATE TABLE api_endpoint (
                              api_endpoint_id BIGSERIAL PRIMARY KEY,
                              api_price_group_id BIGINT default NULL,
                              path TEXT NOT NULL,
                              method VARCHAR(10) NOT NULL,
                              CONSTRAINT fk_api_price_group_id FOREIGN KEY (api_price_group_id) REFERENCES api_price_group(id),
                              CONSTRAINT unique_path_method UNIQUE (path, method)
);

CREATE TABLE hourly_api_usage (
                                  api_usage_id BIGSERIAL PRIMARY KEY,
                                  api_endpoint_id BIGINT NOT NULL,
                                  member_id BIGINT NOT NULL,
                                  api_key_id BIGINT NOT NULL,
                                  date DATE NOT NULL,
                                  hour SMALLINT NOT NULL,
                                  count BIGINT NOT NULL,
                                  CONSTRAINT fk_api_endpoint_id FOREIGN KEY (api_endpoint_id) REFERENCES api_endpoint(api_endpoint_id)
);

INSERT INTO api_price_group (group_name) VALUES ('test_ping');
INSERT INTO api_price (cost_won, priority, api_price_group_id)
VALUES (1, 1, 1);
INSERT INTO api_endpoint (api_price_group_id, path, method)
VALUES (1, '/api/aggregation/v1/ping', 'GET');

SELECT * FROM hourly_api_usage;