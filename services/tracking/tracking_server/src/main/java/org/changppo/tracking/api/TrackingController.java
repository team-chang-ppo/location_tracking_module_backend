
package org.changppo.tracking.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.tracking.aop.TrackingContextParam;
import org.changppo.tracking.api.request.GenerateTokenRequest;
import org.changppo.tracking.api.request.StartTrackingRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.GenerateTokenResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tracking/v1")
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * 토큰 생성 API
     * 해당 정보를 저장한 token 을 반환 200
     * @param request ConnectRequest
     * @return response ConnectResponse
     * TODO : 토큰 헤더의 키 값은 상의 필요
     */
    @PostMapping("/generate-token")
    public ResponseEntity<GenerateTokenResponse> generateToken(@RequestHeader("api-key-id") String apiKeyId,
                                                               @RequestBody @Valid GenerateTokenRequest request) {
        GenerateTokenResponse response = trackingService.generateToken(apiKeyId, request);

        return ResponseEntity.ok().body(response);
    }

    /**
     * 배달 시작시 정적인 정보 받아오기
     * 성공 시 200
     * 이미 사용자가 존재하면 409
     * @param request StartTrackingRequest
     * @param context TrackingContext
     * @return void
     */
    @TrackingContextParam
    @PostMapping("/start")
    public ResponseEntity<GenerateTokenResponse> startTracking(@RequestBody @Valid StartTrackingRequest request,
                                                               TrackingContext context) {
        trackingService.startTracking(request, context);

        return ResponseEntity.ok().build();
    }

    /**
     * Tracking API
     * 200
     * 존재 하지 않는 tracking 404
     * 종료 처리된 tracking 400
     * @param request TrackingRequest
     * @param context TrackingContext
     * @return TODO 반환처리
     */
    @TrackingContextParam
    @PostMapping("/tracking")
    public ResponseEntity<Void> tracking(@RequestBody @Valid TrackingRequest request,
                                         TrackingContext context) {
        trackingService.tracking(request, context);

        return ResponseEntity.ok().build();
    }

    /**
     * tracking 종료 API
     * 존재 하지 않는 tracking 404
     * 종료 처리된 tracking 400
     * @param context TrackingContext
     * @return TODO 반환처리
     */
    @TrackingContextParam
    @DeleteMapping("/tracking")
    public ResponseEntity<Void> finish(TrackingContext context) {
        trackingService.finish(context);

        return ResponseEntity.ok().build();
    }

    /**
     * tracking 요청 API - 가장 최근 좌표 1개를 반환
     * 존재 하지 않는 tracking 404
     * 종료 처리된 tracking 400
     * @param context TrackingContext
     * @return TrackingResponse
     */
    @TrackingContextParam
    @GetMapping("/tracking")
    public ResponseEntity<TrackingResponse> getTracking(TrackingContext context) {
        TrackingResponse response = trackingService.getTracking(context);

        return ResponseEntity.ok().body(response);
    }


    /**
     * TODO : 해당 배달원의 모든 정보 반환
     * 필수 X
     */

    /**
     * TODO : 추가 실시간성이 필요한 API 에 대해서 연결 요청할 API (추후 생각)
     * 토큰에 대해 권한을 다르게 주고, 권한이 다른 토큰을 부여해야 할듯
     * 고민 : API 를 따로 구성해야할지 or 권한 정보를 헤더나 바디로 받아서 처리해야할지
     * API 를 구성하거나, 권한 정보를 헤더로 넣으면 API GATEWAY 에서 거르기 쉬울듯?
     */

}