package org.changppo.gateway.context;

public sealed interface ApiRateContext permits ValidApiRateContext, InvalidApiRateContext, AbsentApiRateContext {
}
