package org.changppo.cost_management_service.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CardListDto {
    private List<CardDto> cardList;
}
