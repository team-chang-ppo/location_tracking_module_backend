package org.changppo.account.paymentgateway.kakaopay;

public class KakaopayConstants {

    public static final String MODULE_NAME = "위치추적모듈 정기결제";
    public static final String APPROVE_CALLBACK_PATH = "/api/cards/v1/kakaopay/register/approve?partner_order_id=";
    public static final String CANCEL_CALLBACK_PATH = "/api/cards/v1/kakaopay/register/cancel?partner_order_id=";
    public static final String FAIL_CALLBACK_PATH = "/api/cards/v1/kakaopay/register/fail?partner_order_id=";

    public static final String KAKAOPAY_READY_URL = "https://open-api.kakaopay.com/online/v1/payment/ready";
    public static final String KAKAOPAY_APPROVE_URL = "https://open-api.kakaopay.com/online/v1/payment/approve";
    public static final String KAKAOPAY_SUBSCRIPTION_STATUS_URL = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/status";
    public static final String KAKAOPAY_SUBSCRIPTION_INACTIVE_URL = "https://open-api.kakaopay.com/online/v1/payment/manage/subscription/inactive";
    public static final String KAKAOPAY_PAYMENT_URL = "https://open-api.kakaopay.com/online/v1/payment/subscription";
    public static final String CID = "CID";
    public static final String SID = "SID";
    public static final String CCID = "CCID";
    public static final String CARD = "CARD";
    public static final String MONEY = "MONEY";
    public static final String INACTIVE = "INACTIVE";
    public static final String MONEY_TYPE = "현금";
    public static final String MONEY_CORPORATION = "카카오페이";
    public static final String MONEY_BIN = "머니";
}

