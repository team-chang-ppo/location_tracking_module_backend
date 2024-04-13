package org.changppo.cost_management_service.service.payment.scheduled;

//@Component
//public class PaymentProcessingJob implements Job {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @Autowired
//    private CardRepository cardRepository;
//
//    @Autowired
//    private KakaopayPaymentGatewayClient paymentGatewayClient;
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @Autowired
//    private FakePaymentInfoClient fakePaymentInfoClient;
//
//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//        List<Member> members = memberRepository.findAll();
//        LocalDateTime startDateTime = LocalDateTime.now().withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS);
//        LocalDateTime endDateTime = startDateTime.plusMonths(1).minusSeconds(1);
//
//        for (Member member : members) {
//            int amount = fakePaymentInfoClient.getPaymentAmountForPeriod(member.getId(), startDateTime, endDateTime);
//            if (amount >= 100) {
//                processPaymentForMember(member, amount);
//            }
//        }
//    }
//
//    private void processPaymentForMember(Member member, int amount) {
//        List<Card> cards = cardRepository.findAllCardByMemberId(member.getId());
//        boolean paymentSuccess = false;
//
//        for (Card card : cards) {
//            KakaopayPaymentRequest paymentRequest = createKakaopayPaymentRequest(member, card, amount);
//            KakaopayApproveResponse response = paymentGatewayClient.payment(paymentRequest);
//            PaymentCreateRequest paymentCreateRequest = createPaymentCreateRequest(response, member, card);
//            paymentService.create(paymentCreateRequest);
//            paymentSuccess = true;
//            break;
//        }
//
//        if (!paymentSuccess) {
//            member.banForPaymentFailure(LocalDateTime.now());
//            // 관련 API 키 상태 업데이트 로직 추가
//        }
//    }
//
//    private KakaopayPaymentRequest createKakaopayPaymentRequest(Member member, Card card, int amount) {
//        return new KakaopayPaymentRequest();
//    }
//    private PaymentCreateRequest createPaymentCreateRequest(KakaopayApproveResponse response, Member member, Card card) {
//        return new PaymentCreateRequest();
//    }
//}