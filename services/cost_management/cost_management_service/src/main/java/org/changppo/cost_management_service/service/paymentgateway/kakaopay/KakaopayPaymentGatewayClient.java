package org.changppo.cost_management_service.service.paymentgateway.kakaopay;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.*;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.subscription.KakaopaySubscriptionInactiveResponse;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.subscription.KakaopaySubscriptionStatusResponse;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.response.exception.paymentgateway.*;
import org.changppo.cost_management_service.service.paymentgateway.PaymentGatewayClient;
import org.changppo.cost_management_service.service.paymentgateway.PaymentGatewayProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayConstants.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Service
public class KakaopayPaymentGatewayClient extends PaymentGatewayClient {

    private final PaymentGatewayProperties paymentGatewayProperties;
    private final RestTemplate restTemplate;
    private final HttpSession httpSession;
    private static final PaymentGatewayType PAYMENT_GATEWAY_TYPE = PaymentGatewayType.PG_KAKAOPAY;
    @Override
    protected PaymentGatewayType getSupportedPaymentGateway() {
        return PAYMENT_GATEWAY_TYPE;
    }

    public KakaopayReadyResponse Ready(KakaopayReadyRequest req) {
        try {
            HttpEntity<Map<String, Object>> request = createReadyRequest(req);
            KakaopayReadyResponse kakaopayReadyResponse = restTemplate.postForObject(
                    KAKAOPAY_READY_URL,
                    request,
                    KakaopayReadyResponse.class
            );
            handleResponse(kakaopayReadyResponse);
            saveTid(req.getPartnerOrderId(), kakaopayReadyResponse.getTid());
            return kakaopayReadyResponse;
        } catch (Exception e) {
            throw new KakaopayPaymentGatewayReadyFailureException(e);
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

    public KakaopayApproveResponse Approve(KakaopayApproveRequest req) {
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
            return kakaopayApproveResponse;
        } catch (Exception e) {
            throw new KakaopayPaymentGatewayApproveFailureException(e);
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

    public void fail(KakaopayCancelRequest req) {
        String tid = null;
        try {
            tid = extractTid(req.getPartnerOrderId());
            removeTid(req.getPartnerOrderId());
        }catch (Exception e){
            throw new KakaopayPaymentGatewayFailFailureException(e);
        }
        throw new KakaopayPaymentGatewayFailException(
                new RuntimeException("Failed to process Kakaopay Fail for " +
                        "Partner Order ID: " + req.getPartnerOrderId() + ", " +
                        "Partner User ID: " + req.getPartnerUserId() + ", " +
                        "Tid: " + tid));
    }

    public void cancel(String partnerOrderId) {
        try {
            extractTid(partnerOrderId);
            removeTid(partnerOrderId);
        } catch (Exception e) {
            throw new KakaopayPaymentGatewayCancelFailureException(e);
        }
    }

    public KakaopaySubscriptionStatusResponse status(String sid) {
        try {
            HttpEntity<Map<String, Object>> request = createStatusAndInactiveRequest(sid);
            KakaopaySubscriptionStatusResponse kakaopaySubscriptionStatusResponse = restTemplate.postForObject(
                    KAKAOPAY_SUBSCRIPTION_STATUS_URL,
                    request,
                    KakaopaySubscriptionStatusResponse.class
            );
            handleResponse(kakaopaySubscriptionStatusResponse);
            return kakaopaySubscriptionStatusResponse;
        } catch(Exception e) {
            throw new KakaopayPaymentGatewayStatusFailureException(e);
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
            throw new KakaopayPaymentGatewayInactiveFailureException(e);
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
    public void payment(KakaopayPaymentRequest req) {
        try {
            HttpEntity<Map<String, Object>> request = createPaymentRequest(req);
            KakaopayApproveResponse kakaopayApproveResponse = restTemplate.postForObject(
                    KAKAOPAY_PAYMENT_URL,
                    request,
                    KakaopayApproveResponse.class
            );
            handleResponse(kakaopayApproveResponse);
            validateApproveResponse(kakaopayApproveResponse);
        } catch (Exception e) {
            throw new KakaopayPaymentGatewayPaymentFailureException(e);
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
