package org.changppo.account.paymentgateway.kakaopay;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.paymentgateway.PaymentGatewayProperties;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.*;
import org.changppo.account.paymentgateway.kakaopay.dto.subscription.KakaopaySubscriptionInactiveResponse;
import org.changppo.account.paymentgateway.kakaopay.dto.subscription.KakaopaySubscriptionStatusResponse;
import org.changppo.account.response.ClientResponse;
import org.changppo.account.type.PaymentGatewayType;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Service
@Slf4j
public class KakaopayPaymentGatewayClient extends PaymentGatewayClient {

    private final PaymentGatewayProperties paymentGatewayProperties;
    private final RestTemplate restTemplate;
    private final HttpSession httpSession;
    private static final PaymentGatewayType PAYMENT_GATEWAY_TYPE = PaymentGatewayType.PG_KAKAOPAY;

    @Override
    protected PaymentGatewayType getSupportedPaymentGateway() {
        return PAYMENT_GATEWAY_TYPE;
    }

    public ClientResponse<KakaopayReadyResponse> Ready(KakaopayReadyRequest req) {
        try {
            HttpEntity<Map<String, Object>> request = createReadyRequest(req);
            KakaopayReadyResponse kakaopayReadyResponse = restTemplate.postForObject(
                    KAKAOPAY_READY_URL,
                    request,
                    KakaopayReadyResponse.class
            );
            handleResponse(kakaopayReadyResponse);
            saveTid(req.getPartnerOrderId(), kakaopayReadyResponse.getTid());
            return ClientResponse.success(kakaopayReadyResponse);
        } catch (Exception e) {
            log.error("Failed to process Kakaopay Ready for User ID: {}", req.getPartnerUserId(), e);
            return ClientResponse.failure();
        }
    }

    private void saveTid(String partnerOrderId, String tid) {
        httpSession.setAttribute(partnerOrderId, tid);
    }

    private HttpEntity<Map<String, Object>> createReadyRequest(KakaopayReadyRequest req) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID.equalsIgnoreCase(req.getCid()) ? paymentGatewayProperties.getKakaopay().getCid() : paymentGatewayProperties.getKakaopay().getCcid());
        parameters.put("partner_order_id", req.getPartnerOrderId());
        parameters.put("partner_user_id", req.getPartnerUserId());
        parameters.put("item_name", req.getItemName());
        parameters.put("quantity", req.getQuantity());
        parameters.put("total_amount", req.getTotalAmount());
        parameters.put("tax_free_amount", req.getTaxFreeAmount());
        parameters.put("approval_url", req.getApprovalUrl());
        parameters.put("cancel_url", req.getCancelUrl());
        parameters.put("fail_url", req.getFailUrl());
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    public ClientResponse<KakaopayApproveResponse> Approve(KakaopayApproveRequest req) {
        try {
            String tid = extractTid(req.getPartnerOrderId());
            removeTid(req.getPartnerOrderId());
            HttpEntity<Map<String, Object>> request = createApproveRequest(req, tid);
            KakaopayApproveResponse kakaopayApproveResponse = restTemplate.postForObject(
                    KAKAOPAY_APPROVE_URL,
                    request,
                    KakaopayApproveResponse.class
            );
            handleResponse(kakaopayApproveResponse);
            validateApproveResponse(kakaopayApproveResponse);
            return ClientResponse.success(kakaopayApproveResponse);
        } catch (Exception e) {
            return ClientResponse.failure();
        }
    }

    private HttpEntity<Map<String, Object>> createApproveRequest(KakaopayApproveRequest req, String tid) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", CID.equalsIgnoreCase(req.getCid()) ? paymentGatewayProperties.getKakaopay().getCid() : paymentGatewayProperties.getKakaopay().getCcid());
        parameters.put("tid", tid);
        parameters.put("partner_order_id", req.getPartnerOrderId());
        parameters.put("partner_user_id", req.getPartnerUserId());
        parameters.put("pg_token", req.getPgToken());
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    public void fail(KakaopayFailRequest req) {
        try {
            extractTid(req.getPartnerOrderId());
            removeTid(req.getPartnerOrderId());
        }catch (Exception e){
            log.error("Failed to fail Kakaopay for User ID: {}", req.getPartnerUserId(), e);
        }
    }

    public void cancel(KakaopayCancelRequest req) {
        try {
            extractTid(req.getPartnerOrderId());
            removeTid(req.getPartnerOrderId());
        } catch (Exception e) {
            log.error("Failed to cancel Kakaopay for User ID: {}", req.getPartnerUserId(), e);
        }
    }

    public ClientResponse<KakaopaySubscriptionStatusResponse> status(String sid) {
        try {
            HttpEntity<Map<String, Object>> request = createStatusAndInactiveRequest(sid);
            KakaopaySubscriptionStatusResponse kakaopaySubscriptionStatusResponse = restTemplate.postForObject(
                    KAKAOPAY_SUBSCRIPTION_STATUS_URL,
                    request,
                    KakaopaySubscriptionStatusResponse.class
            );
            handleResponse(kakaopaySubscriptionStatusResponse);
            return ClientResponse.success(kakaopaySubscriptionStatusResponse);
        } catch(Exception e) {
            log.error("Failed to get subscription status. Sid: {}", sid, e);
            return ClientResponse.failure();
        }
    }

    @Override
    public void inactive(String sid) {
        try {
            HttpEntity<Map<String, Object>> request = createStatusAndInactiveRequest(sid);
            KakaopaySubscriptionInactiveResponse kakaopaySubscriptionInactiveResponse = restTemplate.postForObject(
                    KAKAOPAY_SUBSCRIPTION_INACTIVE_URL,
                    request,
                    KakaopaySubscriptionInactiveResponse.class
            );
            handleResponse(kakaopaySubscriptionInactiveResponse);
            validateInactiveStatus(kakaopaySubscriptionInactiveResponse);
        } catch (Exception e) {
            log.error("Failed to deactivate subscription. Sid: {}", sid, e);
        }
    }

    private HttpEntity<Map<String, Object>> createStatusAndInactiveRequest(String sid) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", paymentGatewayProperties.getKakaopay().getCcid());
        parameters.put("sid", sid);
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    private void validateInactiveStatus(KakaopaySubscriptionInactiveResponse kakaopaySubscriptionInactiveResponse) {
        if (!INACTIVE.equalsIgnoreCase(kakaopaySubscriptionInactiveResponse.getStatus())) {
            throw new RuntimeException("Failed to deactivate subscription. Sid: " + kakaopaySubscriptionInactiveResponse.getSid() + ", Status: " + kakaopaySubscriptionInactiveResponse.getStatus());
        }
    }

    private String extractTid(String partnerOrderId) {
        String tid = (String) httpSession.getAttribute(partnerOrderId);
        if (tid == null) {
            throw new IllegalStateException("Failed to extract Kakaopay Tid for partnerOrderId: " + partnerOrderId);
        }
        return tid;
    }

    public void removeTid(String partnerOrderId) {
        httpSession.removeAttribute(partnerOrderId);
    }

    @Override
    public ClientResponse<KakaopayApproveResponse> payment(KakaopayPaymentRequest req) {
        try {
            HttpEntity<Map<String, Object>> request = createPaymentRequest(req);
            KakaopayApproveResponse kakaopayApproveResponse = restTemplate.postForObject(
                    KAKAOPAY_PAYMENT_URL,
                    request,
                    KakaopayApproveResponse.class
            );
            handleResponse(kakaopayApproveResponse);
            validateApproveResponse(kakaopayApproveResponse);
            return ClientResponse.success(kakaopayApproveResponse);
        } catch (Exception e) {
            log.error("Failed to process Kakaopay Payment for User ID: {}", req.getPartnerUserId(), e);
            return ClientResponse.failure();
        }
    }

    private HttpEntity<Map<String, Object>> createPaymentRequest(KakaopayPaymentRequest req) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cid", paymentGatewayProperties.getKakaopay().getCcid());
        parameters.put("sid", req.getSid());
        parameters.put("partner_order_id", req.getPartnerOrderId());
        parameters.put("partner_user_id", req.getPartnerUserId());
        parameters.put("item_name", req.getItemName());
        parameters.put("quantity", req.getQuantity());
        parameters.put("total_amount", req.getTotalAmount());
        parameters.put("tax_free_amount", req.getTaxFreeAmount());
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    public static <T> void handleResponse(T response) {
        if (response == null) {
            throw new RuntimeException("Failed to process Kakaopay Response: Response is null");
        }
    }

    private void validateApproveResponse(KakaopayApproveResponse response) {
        if (response.getAmount() == null) {
            throw new IllegalStateException("Amount cannot be null.");
        }
        if ("CARD".equalsIgnoreCase(response.getPayment_method_type())) {
            if (response.getCard_info() == null) {
                throw new IllegalStateException("Card info cannot be null");
            }
        } else if (!"MONEY".equalsIgnoreCase(response.getPayment_method_type())) {
            throw new IllegalStateException("Unsupported payment method type: " + response.getPayment_method_type());
        }
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION, paymentGatewayProperties.getKakaopay().getSecretKey());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }
}
