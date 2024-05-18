CREATE DATABASE location_tracking_module;

CREATE TABLE api_price_group (
                                 id BIGSERIAL PRIMARY KEY,
                                 group_name VARCHAR(200) NOT NULL,
                                 cost_won BIGINT NOT NULL,
                                 CONSTRAINT unique_group_name UNIQUE (group_name)
);

CREATE TABLE api_endpoint (
                              api_endpoint_id BIGSERIAL PRIMARY KEY,
                              api_price_group_id BIGINT default NULL,
                              path TEXT NOT NULL,
                              method VARCHAR(10) NOT NULL,
                              CONSTRAINT fk_api_price_group_id FOREIGN KEY (api_price_group_id) REFERENCES api_price_group(id),
                              CONSTRAINT unique_path_method UNIQUE (path, method)
);
CREATE INDEX api_endpoint_api_price_group_id_index ON api_endpoint (api_price_group_id);

CREATE TABLE hourly_api_usage (
                                  api_usage_id BIGSERIAL PRIMARY KEY,
                                  api_endpoint_id BIGINT default NULL,
                                  member_id BIGINT NOT NULL,
                                  api_key_id BIGINT NOT NULL,
                                  date DATE NOT NULL,
                                  hour SMALLINT NOT NULL,
                                  count BIGINT NOT NULL,
                                  CONSTRAINT fk_api_endpoint_id FOREIGN KEY (api_endpoint_id) REFERENCES api_endpoint(api_endpoint_id) ON DELETE SET NULL
);
CREATE INDEX hourly_api_usage_api_endpoint_id_index ON hourly_api_usage (api_endpoint_id);


CREATE VIEW hourly_api_usage_cost AS
(
WITH hourly_api_usage_count AS (SELECT member_id,
                                       api_endpoint_id,
                                       api_key_id,
                                       date,
                                       hour,
                                       sum(count) as count_per_hour
                                FROM hourly_api_usage
                                GROUP BY member_id, api_endpoint_id, api_key_id, date, hour)
SELECT hourly_api_usage_count.member_id                                 as member_id,
       hourly_api_usage_count.api_key_id                                as api_key_id,
       hourly_api_usage_count.date                                      as date,
       hourly_api_usage_count.hour                                      as hour,
       api_endpoint.api_endpoint_id                                     as api_endpoint_id,
       api_price_group.id                                               as api_price_group_id,
       api_price_group.cost_won                                         as cost_per_request,
       hourly_api_usage_count.count_per_hour                            as request_count_per_hour,
       api_price_group.cost_won * hourly_api_usage_count.count_per_hour as hourly_cost
FROM hourly_api_usage_count
         INNER JOIN api_endpoint ON hourly_api_usage_count.api_endpoint_id = api_endpoint.api_endpoint_id
         INNER JOIN api_price_group ON api_endpoint.api_price_group_id = api_price_group.id
    );
SELECT * FROM hourly_api_usage_cost;

-- test data

INSERT INTO api_price_group (group_name, cost_won) VALUES ('test_ping', 10);
INSERT INTO api_endpoint (api_price_group_id, path, method)
VALUES (1, '/api/aggregation/v1/ping', 'GET');

INSERT INTO hourly_api_usage (member_id, api_endpoint_id, api_key_id, date, hour, count)
VALUES (1, 1, 1, '2024-05-18', 1, 22),
       (1, 1, 1, '2024-05-18', 2, 33),
       (1, 1, 1, '2024-05-18', 2, 44),
       (1, Null, 1, '2024-05-17', 1, 55),
       (1, Null, 1, '2024-05-18', 6, 66),
       (1, 1, 1, '2024-05-17', 7, 55),
       (2, 1, 3, '2024-05-17', 1, 33),
       (3, NULL, 4, '2024-05-19',1,33);