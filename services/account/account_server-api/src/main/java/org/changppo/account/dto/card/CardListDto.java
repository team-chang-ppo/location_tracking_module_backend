package org.changppo.account.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.service.dto.card.CardDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CardListDto {
    private List<CardDto> cardList;
}
