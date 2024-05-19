package org.changppo.monitoring.security;

import jakarta.annotation.Nullable;

public interface RemoteSessionRetrieveStrategy {
    @Nullable
    RemoteSessionAuthentication retrieve(String sessionId);
}
