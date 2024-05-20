package org.changppo.monitoring.security;

import jakarta.annotation.Nullable;
import org.changppo.monioring.domain.error.RemoteSessionFetchFailedException;

public interface RemoteSessionRetrieveStrategy {
    /**
     * 세션 아이디로 세션 정보를 조회한다.
     * @param sessionId 세션 아이디
     * @return 세션 정보
     * @throws RemoteSessionFetchFailedException 세션 정보 조회 실패
     */
    @Nullable
    RemoteSessionAuthentication retrieve(String sessionId);
}
