package org.changppo.gateway.ratelimit.context;

public sealed interface ApiRateContext permits ValidApiRateContext, InvalidApiRateContext, AbsentApiRateContext {
}
