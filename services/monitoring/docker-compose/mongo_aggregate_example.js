var api_key_id = 1;
var orAfter = ISODate("2024-03-31T00:00:00Z");
var before = ISODate("2024-04-01T23:59:59Z");

db.getSiblingDB("monitoring").getCollection("api_record").aggregate([
    {
        $match: {
            "timestamp": {
                $gte: orAfter, // 시작 timestamp 범위
                $lt: before
            },
            "api_key_id": api_key_id// 조회하고자 하는 특정 ID
        }
    },
    {
        $group: {
            _id: "route_id", // routeId를 기준으로 그룹화
            count: { $sum: 1 } // 각 routeId 별로 문서 갯수를 세어 count 필드에 저장
        }
    },
    {
        $project :{
            _id: 0,
            route_id: "$_id",
            count: 1
        }
    }
])

// 10 분 단위로 그룹화

var api_key_id = 1;
var orAfter = ISODate("2024-03-31T00:00:00Z");
var before = ISODate("2024-04-01T23:59:59Z");

db.getSiblingDB("monitoring").getCollection("api_record").aggregate([
    {
        $match: {
            "timestamp": {
                $gte: orAfter, // 시작 timestamp 범위
                $lt: before
            },
            "api_key_id": api_key_id// 조회하고자 하는 특정 ID
        }
    },
    {
        $project: {
            "route_id": 1,
            "timestamp": 1
        }
    },
    {
        $group: {
            _id: {
                "route_id": "$route_id",
                "hour": { $hour: "$timestamp" },
                "minute": {
                    $subtract: [
                        { $minute: "$timestamp" },
                        { $mod: [{ $minute: "$timestamp" }, 10] }
                    ]
                }
            },
            count: { $sum: 1 }
        }
    },
    {
        $project: {
            _id: 0,
            "route_id": "$_id.route_id",
            "time": {
                $concat: [
                    { $toString: "$_id.hour" },
                    ":",
                    { $toString: "$_id.minute" }
                ]
            },
            "count": 1
        }
    }
])