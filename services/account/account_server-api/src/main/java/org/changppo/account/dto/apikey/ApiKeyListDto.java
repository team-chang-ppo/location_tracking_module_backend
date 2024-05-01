package org.changppo.account.dto.apikey;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiKeyListDto {
    private int numberOfElements;

    private boolean hasNext;

    private List<ApiKeyDto> apiKeyList;
}
