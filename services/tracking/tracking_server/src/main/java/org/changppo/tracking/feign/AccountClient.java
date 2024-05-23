package org.changppo.tracking.feign;

import org.changppo.commons.ResponseBody;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "accountClient", url = "${external.account-service.host}")
public interface AccountClient {

    @GetMapping(value = "/api/apikeys/v1/validate/{apikeyId}")
    ResponseBody<ApikeyValidResponsePayload> isApikeyIdValid(@PathVariable Long apikeyId);
}
