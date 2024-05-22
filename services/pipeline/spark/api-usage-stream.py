from pyspark.sql import SparkSession
from pyspark.sql.types import *
from pyspark.sql.functions import *

packages = [
    'org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.1',
    'org.apache.kafka:kafka-clients:2.8.1'
]

# SparkSession
spark = SparkSession.builder \
    .master("yarn") \
    .appName("apiUsageKafkaConsumer") \
    .config("spark.submit.deployMode", "client") \
    .config("spark.jars.packages", ",".join(packages)) \
    .getOrCreate()

spark.conf.set("spark.sql.session.timezone", "UTC")

spark.sparkContext.setLogLevel("ERROR")

api_usage_trace_schema = StructType([
    StructField("eventId", StringType(), False),
    StructField("requestTime", TimestampType(), False),
    StructField("responseTime", TimestampType(), False),
    StructField("requestProtocol", StringType(), True),
    StructField("requestMethod", StringType(), True),
    StructField("requestUri", StringType(), False),
    StructField("responseStatus", IntegerType(), True),
    StructField("clientIp", StringType(), True),
    StructField("clientAgent", StringType(), True),
    StructField("apiKey", StringType(), True),
    StructField("traceId", StringType(), False),
    StructField("apiKeyId", IntegerType(), False),
    StructField("memberId", IntegerType(), False),
    StructField("memberGrade", StringType(), False)
])


# INPUT SOURCE
# Kafka api-usage Stream
kafka_topic = "api-usage-trace"
kafka_servers = "localhost:29092"
consumer_group = "api-usage-trace-spark"
offset_reset = "latest"

parsed_column = from_json(col("value").cast("string"), api_usage_trace_schema,
                          {"timestampFormat": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"})

kafka_input_select = [
    "json_object.*",
    "offset as kafka_offset",
    "partition as kafka_partition",
    "timestamp as kafka_timestamp",
    "to_date(json_object.requestTime) as date",
]

kafka_input_stream = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", kafka_servers) \
    .option("subscribe", kafka_topic) \
    .option("includeHeaders", "true") \
    .option("startingOffsets", offset_reset) \
    .load() \
    .withColumn("json_object", parsed_column) \
    .selectExpr(*kafka_input_select) \

kafka_input_stream.printSchema()

# load api_price_endpoint from db
url = "jdbc:postgresql://localhost:5432/location_tracking_module"
dbtable = "api_endpoint"
username = "username"
password = "password"

api_endpoint_df = spark.read \
    .format("jdbc") \
    .option("driver", "org.postgresql.Driver") \
    .option("url", url) \
    .option("dbtable", dbtable) \
    .option("user", username) \
    .option("password", password) \
    .load()

api_endpoint_df.printSchema()

# WINDOWED AGGREGATION
not_null = col("memberId").isNotNull() & col("apiKeyId") & col("requestUri").isNotNull() & col("requestTime").isNotNull()
not_5xx = (col("responseStatus") >= 500) & (col("responseStatus") < 600)

select_columns = [
    "requestTime",
    'split(regexp_replace(requestUri, r"https?://[^/]+", ""), r"\?.*")[0] as requestUri',
    "requestMethod",
    "memberId",
    "apiKeyId",
    "date"
]

time_division_columns = {
    "hour": hour("requestTime")
}

window_interval = "1 hours"
watermark_interval = "5 minutes"

group_by_columns = ["requestUri", "requestMethod", "memberId", "apiKeyId", "date", window(col("requestTime"), window_interval)]

# windowing write
windowed_stream = kafka_input_stream \
    .where(not_null & not_5xx) \
    .selectExpr(*select_columns) \
    .withWatermark("requestTime", watermark_interval) \
    .groupBy(*group_by_columns) \
    .count()
windowed_stream.printSchema()

# JOIN

join_expression = (windowed_stream["requestUri"] == api_endpoint_df["path"]) & (windowed_stream["requestMethod"] == api_endpoint_df["method"])
join_type = "left_outer"
hourly_api_usage_select = [
    "api_endpoint_id as apiEndpointId",
    "memberId",
    "apiKeyId",
    "date",
    "window",
    "count"
]
hourly_api_usage_df = windowed_stream.join(api_endpoint_df, join_expression, join_type).selectExpr(*hourly_api_usage_select)
hourly_api_usage_df.printSchema()

# SEND TO KAFKA
to_json_column = to_json(struct("*"),{"timestampFormat": "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"}).alias("value")

kafka_output_schema = hourly_api_usage_df.select(to_json_column)
kafka_output_schema.printSchema()

output_kafka_topic = "hourly-api-usage"
output_kafka_checkpoint_location = "/tracking/windowed/kafka_check_point"

kafka_write_stream = kafka_output_schema.writeStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", kafka_servers) \
    .option("topic", output_kafka_topic) \
    .option("checkpointLocation", output_kafka_checkpoint_location) \
    .start()

# SEND TO HDFS
# # hdf write stream
full_write_stream = kafka_input_stream \
    .writeStream \
    .format("parquet") \
    .outputMode("append") \
    .partitionBy("date") \
    .trigger(processingTime='5 seconds') \
    .option("path", "/tracking/api_usage") \
    .option("checkpointLocation", "/tracking/api_usage/check_point") \
    .start()

# SEND TO CONSOLE
# console write stream for debug
kafka_input_stream.writeStream.format("console").start()

# await
full_write_stream.awaitTermination()