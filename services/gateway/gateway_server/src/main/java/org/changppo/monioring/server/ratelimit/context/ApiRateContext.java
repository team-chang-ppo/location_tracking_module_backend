package org.changppo.monioring.server.ratelimit.context;

public sealed interface ApiRateContext permits ValidApiRateContext, InvalidApiRateContext, AbsentApiRateContext {
}
