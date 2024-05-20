package jackson_test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.commons.*;
import org.changppo.commons.old.Response;
import org.changppo.commons.old.Result;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class DynamicMappingTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @ParameterizedTest
    @DisplayName("기존 방식대로 하면, 예외 발생함")
    @MethodSource("dynamicJsonResponseWithPayloadFormat1")
    void failed(TestCase<String, Class<?>> testCase) throws JsonProcessingException {
        String json = testCase.input();
        Assertions.assertThrows(Exception.class, () -> {
            Response response = objectMapper.readValue(json, Response.class);
            Result result = response.getResult();
            System.out.println(result.getClass().getSimpleName());
        });
    }


    @ParameterizedTest
    @DisplayName("payload format 1")
    @MethodSource("dynamicJsonResponseWithPayloadFormat1")
    void advancedTestDynamicMapping(TestCase<String, Consumer<Object>> testCase) throws JsonProcessingException {

        String json = testCase.input();
        TypeReference<ResponseBody<PayloadFormat1>> typeReference = new TypeReference<>() {
        };
        ResponseBody<PayloadFormat1> payloadFormatResponseBody = objectMapper.readValue(json, typeReference);

        // Then
        testCase.expected().accept(payloadFormatResponseBody);
        String s = objectMapper.writeValueAsString(payloadFormatResponseBody);
        System.out.println(s);
    }

    @ParameterizedTest
    @DisplayName("payload format 2")
    @MethodSource("dynamicJsonResponseWithPayloadFormat2")
    void advancedTestDynamicMapping2(TestCase<String, Consumer<Object>> testCase) throws JsonProcessingException {

        String json = testCase.input();
        TypeReference<ResponseBody<PayloadFormat2>> typeReference = new TypeReference<>() {
        };
        ResponseBody<PayloadFormat2> payloadFormatResponseBody = objectMapper.readValue(json, typeReference);

        // Then
        testCase.expected().accept(payloadFormatResponseBody);
        String s = objectMapper.writeValueAsString(payloadFormatResponseBody);
        System.out.println(s);
    }





    public record TestCase<I, E>(
            I input,
            E expected
    ) {
    }

    record PayloadFormat1(
            String data1,
            String data2
    ) {
    }

    record PayloadFormat2(
            List<PayloadFormat1> data
    ) {
    }

    private static Stream<TestCase<String, Consumer<Object>>> dynamicJsonResponseWithPayloadFormat1() {

        String successResponse1 = """
                {
                    "success": true,
                    "result": {
                        "data1": "value1",
                        "data2": "value2"
                    }
                }
                """;

        Consumer<Object> consumer1 = o -> {
            if (!(o instanceof SuccessResponseBody successResponseBody)) {
                fail();
                return;
            }
            Object result = successResponseBody.getResult();
            if (!(result instanceof PayloadFormat1 payloadFormat1)) {
                fail();
                return;
            }
            assertEquals("value1", payloadFormat1.data1());
            assertEquals("value2", payloadFormat1.data2());
        };
        String successResponse2 = """
                {
                    "success": true,
                    "code": "0",
                    "result": {
                        "data2": "value2"
                    }
                }
                """;
        Consumer<Object> consumer2 = o -> {
            if (!(o instanceof SuccessResponseBody successResponseBody)) {
                fail();
                return;
            }
            Object result = successResponseBody.getResult();
            if (!(result instanceof PayloadFormat1 payloadFormat1)) {
                fail();
                return;
            }
            assertEquals("value2", payloadFormat1.data2());
        };
        String failureResponse = """
                {
                    "success": false,
                    "code": "1",
                    "result": {
                        "msg": "some error message"
                    }
                }
                """;
        Consumer<Object> consumer3 = o -> {
            if (!(o instanceof FailedResponseBody failedResponseBody)) {
                fail();
                return;
            }
            assertEquals("1", failedResponseBody.getCode());
            FailedResponseBody.ErrorPayload result = failedResponseBody.getResult();
            assertEquals("some error message", result.getMsg());
        };

        return Stream.of(
                new TestCase<>(successResponse1, consumer1),
                new TestCase<>(successResponse2, consumer2),
                new TestCase<>(failureResponse, consumer3)
        );
    }

    private static Stream<TestCase<String, Consumer<Object>>> dynamicJsonResponseWithPayloadFormat2() {
        String successResponse = """
                {
                    "success": true,
                    "result": {
                        "data": [
                            {
                                "data1": "value1",
                                "data2": "value2"
                            },
                            {
                                "data1": "value3",
                                "data2": "value4"
                            }
                        ]
                    }
                }
                """;
        Consumer<Object> consumer = o -> {
            if (!(o instanceof SuccessResponseBody successResponseBody)) {
                fail();
                return;
            }
            Object result = successResponseBody.getResult();
            if (!(result instanceof PayloadFormat2 payloadFormat2)) {
                fail();
                return;
            }
            List<PayloadFormat1> data = payloadFormat2.data();
            assertEquals(2, data.size());
            assertEquals("value1", data.get(0).data1());
            assertEquals("value2", data.get(0).data2());
            assertEquals("value3", data.get(1).data1());
            assertEquals("value4", data.get(1).data2());
        };

        String failureResponse = """
                {
                    "success": false,
                    "code": "1",
                    "result": {
                        "msg": "some error message"
                    }
                }
                """;
        Consumer<Object> consumer2 = o -> {
            if (!(o instanceof FailedResponseBody failedResponseBody)) {
                fail();
                return;
            }
            assertEquals("1", failedResponseBody.getCode());
            FailedResponseBody.ErrorPayload result = failedResponseBody.getResult();
            assertEquals("some error message", result.getMsg());
        };

        return Stream.of(
                new TestCase<>(successResponse, consumer),
                new TestCase<>(failureResponse, consumer2)
        );

    }


}
