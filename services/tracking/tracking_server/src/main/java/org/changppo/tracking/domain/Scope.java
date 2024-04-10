package org.changppo.tracking.domain;

public enum Scope {
    READ_TRACKING_COORDINATE, // tracking 중인 최근 좌표 하나 받아오기
    WRITE_TRACKING_COORDINATE, // tracking 현재 좌표 쓰기
    ACCESS_TRACKING_HISTORY // tracking 기록 불러오기
}