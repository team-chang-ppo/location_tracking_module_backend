// create_indexes.js

// 연결할 데이터베이스 설정
var dbName = "monitoring";
var collectionName = "api";

// 인덱스 생성
db.getSiblingDB(dbName).getCollection(collectionName).createIndex({ "api_key_id": 1 });
db.getSiblingDB(dbName).getCollection(collectionName).createIndex({ "route_id": 1 });
db.getSiblingDB(dbName).getCollection(collectionName).createIndex({ "timestamp": 1 });