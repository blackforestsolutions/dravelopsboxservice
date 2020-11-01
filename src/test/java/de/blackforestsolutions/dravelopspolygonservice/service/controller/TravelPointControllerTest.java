package de.blackforestsolutions.dravelopspolygonservice.service.controller;

import de.blackforestsolutions.dravelopspolygonservice.controller.TravelPointController;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiService;
import de.blackforestsolutions.dravelopspolygonservice.service.communicationservice.TravelPointApiServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getPolygonApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.TravelPointObjectMother.getGermanyTravelPoint;
import static de.blackforestsolutions.dravelopsdatamodel.testutil.TestUtils.toJson;
import static org.apache.commons.lang.StringUtils.deleteWhitespace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class TravelPointControllerTest {

    private final TravelPointApiService travelPointApiService = mock(TravelPointApiServiceImpl.class);

    private final WebTestClient classUnderTest = WebTestClient.bindToController(new TravelPointController(travelPointApiService)).build();

    @BeforeEach
    void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void test_if_call_is_executed_correctly_and_return_travelPoints() {
        ArgumentCaptor<String> requestToken = ArgumentCaptor.forClass(String.class);
        when(travelPointApiService.retrieveTravelPointsFromApiService(anyString())).thenReturn(Flux.just(toJson(getGermanyTravelPoint())));

        Flux<String> result = classUnderTest
                .post()
                .uri("/pelias/travelpoints/get")
                .body(Mono.just(toJson(getPolygonApiToken())), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        verify(travelPointApiService, times(1)).retrieveTravelPointsFromApiService(requestToken.capture());
        assertThat(requestToken.getValue()).isEqualTo(toJson(getPolygonApiToken()));
        StepVerifier.create(result)
                .assertNext(travelPoint -> assertThat(deleteWhitespace(travelPoint)).isEqualTo(deleteWhitespace(toJson(getGermanyTravelPoint()))))
                .verifyComplete();
    }

    @Test
    void test_if_call_is_executed_correctly_when_no_results_are_available() {
        when(travelPointApiService.retrieveTravelPointsFromApiService(anyString())).thenReturn(Flux.empty());

        Flux<String> result = classUnderTest
                .post()
                .uri("/pelias/travelpoints/get")
                .body(Mono.just(toJson(getPolygonApiToken())), String.class)
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(String.class)
                .getResponseBody();

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

}
