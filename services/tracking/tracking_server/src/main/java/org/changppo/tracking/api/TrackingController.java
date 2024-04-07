package org.changppo.tracking.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.tracking.api.request.ConnectRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.ConnectResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.service.TrackingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tracking/v1")
public class TrackingController {

    private final TrackingService trackingService;

    /**
     * 연결 API
     * 해당 정보를 저장한 token 을 반환 200
     * 이미 사용자가 존재하면 409
     * @param request ConnectRequest
     * @return response ConnectResponse
     */
    @PostMapping("/connect")
    public ResponseEntity<ConnectResponse> connect(@RequestBody @Valid ConnectRequest request) {
        ConnectResponse response = trackingService.connect(request);

        return ResponseEntity.ok().body(response);
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
    @PostMapping("/tracking")
    public ResponseEntity<Void> tracking(@RequestBody @Valid TrackingRequest request,
                                         @AuthenticationPrincipal TrackingContext context) {
        trackingService.tracking(request, context.trackingId());

        return ResponseEntity.ok().build();
    }

    /**
     * tracking 종료 API
     * 존재 하지 않는 tracking 404
     * 종료 처리된 tracking 400
     * @param context TrackingContext
     * @return TODO 반환처리
     */
    @DeleteMapping("/tracking")
    public ResponseEntity<Void> finish(@AuthenticationPrincipal TrackingContext context) {
        trackingService.finish(context.trackingId());

        return ResponseEntity.ok().build();
    }

    /**
     * tracking 요청 API - 가장 최근 좌표 1개를 반환
     * 존재 하지 않는 tracking 404
     * 종료 처리된 tracking 400
     * @param context TrackingContext
     * @return TrackingResponse
     */
    @GetMapping("/tracking")
    public ResponseEntity<TrackingResponse> getTracking(@AuthenticationPrincipal TrackingContext context) {
        TrackingResponse response = trackingService.getTracking(context.trackingId());

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
