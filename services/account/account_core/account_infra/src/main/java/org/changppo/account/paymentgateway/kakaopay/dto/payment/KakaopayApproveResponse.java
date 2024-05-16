package org.changppo.account.paymentgateway.kakaopay.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.account.paymentgateway.dto.PaymentResponse;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayApproveResponse implements PaymentResponse {
    private String aid;
    private String tid;
    private String cid;
    private String sid;
    private String partner_order_id;
    private String partner_user_id;
    private String payment_method_type;
    private Amount amount;
    private CardInfo card_info;
    private String item_name;
    private String item_code;
    private int quantity;
    private String created_at;
    private String approved_at;
    private String payload;

    @Override
    public String getKey() {
        return tid;
    }

    @Override
    public String getCardType() {
        return card_info.getCard_type();
    }

    @Override
    public String getCardIssuerCorporation() {
        return card_info.getKakaopay_issuer_corp();
    }

    @Override
    public String getCardBin() {
        return card_info.getBin();
    }

    @Override
    public int getTotalAmount() {
        return amount.getTotal();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Amount {
        private int total;
        private int tax_free;
        private int tax;
        private int point;
        private int discount;
        private int green_deposit;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CardInfo {
        private String kakaopay_purchase_corp;
        private String kakaopay_purchase_corp_code;
        private String kakaopay_issuer_corp;
        private String kakaopay_issuer_corp_code;
        private String bin;
        private String card_type;
        private String install_month;
        private String approved_id;
        private String card_mid;
        private String interest_free_install;
        private String installment_type;
        private String card_item_code;
    }
}
