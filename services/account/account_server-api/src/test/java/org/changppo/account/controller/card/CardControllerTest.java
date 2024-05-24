package org.changppo.account.controller.card;

import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.service.application.card.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @InjectMocks
    private CardController cardController;
    @Mock
    private CardService cardService;
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())  // Pageable 처리를 위한 설정
                .build();
    }

    @Test
    void readTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", id))
                .andExpect(status().isOk());

        verify(cardService).read(eq(id));
    }

    @Test
    void readAllTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/{id}", id))
                .andExpect(status().isOk());

        verify(cardService).readAll(id);
    }

    @Test
    void readListTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk());

        verify(cardService).readList(pageable);
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", id))
                .andExpect(status().isOk());

        verify(cardService).delete(eq(id));
    }
}
