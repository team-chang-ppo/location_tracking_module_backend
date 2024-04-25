package org.changppo.tracking.repository;


import org.changppo.tracking.domain.mongodb.Coordinates;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.geo.Point;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest // in-memory DB
@DirtiesContext // application context 1개로 공유
public class CoordinatesRepositoryTest {

    @Autowired
    private CoordinatesRepository coordinatesRepository;

    @DisplayName("custom 으로 만든 mongodb 쿼리문 테스트")
    @Test
    void getCoordinatesTest() {
        // given
        Coordinates coordinates1 = new Coordinates(1,2, "", "test");
        Coordinates coordinates2 = new Coordinates(1,2, "", "test");
        coordinatesRepository.save(coordinates1);
        coordinatesRepository.save(coordinates2);

        // when
        Optional<Coordinates> result = coordinatesRepository.findTopByTrackingIdOrderByCreatedAtDesc("test");

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.stream().findFirst().get().getId()).isEqualTo(coordinates2.getId());
    }
}
