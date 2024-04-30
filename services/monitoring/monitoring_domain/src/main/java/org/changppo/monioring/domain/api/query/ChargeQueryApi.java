package org.changppo.monioring.domain.api.query;

import org.changppo.monioring.domain.request.query.ApikeyTotalChargeRequest;
import org.changppo.monioring.domain.request.query.MemberTotalChargeRequest;
import org.changppo.monioring.domain.view.ApiKeyDayChargeView;
import org.changppo.monioring.domain.view.ChargeGraphView;
import org.changppo.monioring.domain.view.TotalChargeView;

public interface ChargeQueryApi {

    /**
     * 특정 구간에서 API Key 의 총 요금을 조회한다.
     * @param request 조회 요청
     * @return 총 요금
     */
    TotalChargeView getTotalChargeByApiKeyId(ApikeyTotalChargeRequest request);

    /**
     * 특정 구간에서 회원의 총 요금을 조회한다.
     * @param request 조회 요청
     * @return 총 요금
     */
    TotalChargeView getTotalCharge(MemberTotalChargeRequest request);

    /**
     * 특정 구간에서 API Key 의 일별 요금을 조회한다.
     * @param request 조회 요청
     * @return 일별 요금
     */
    ApiKeyDayChargeView getDayChargeByApiKeyId(ApikeyTotalChargeRequest request);

    /**
     * 특정 구간에서 회원의 api key 별 일별 요금을 조회한다.
     * @param request 조회 요청
     * @return api key 별 일별 요금
     */
    ChargeGraphView getChargeGraphByMemberId(MemberTotalChargeRequest request);

}
