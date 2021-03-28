package de.blackforestsolutions.dravelopsboxservice.service.communicationservice;

import de.blackforestsolutions.dravelopsdatamodel.ApiToken;
import de.blackforestsolutions.dravelopsdatamodel.Box;
import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerService;
import de.blackforestsolutions.dravelopsboxservice.exceptionhandling.ExceptionHandlerServiceImpl;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallService;
import de.blackforestsolutions.dravelopsboxservice.service.communicationservice.restcalls.CallServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.ApiTokenObjectMother.getConfiguredBoxPersistenceApiToken;
import static de.blackforestsolutions.dravelopsdatamodel.objectmothers.BoxObjectMother.getBoxWithNoEmptyFields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BackendApiServiceTest {

    private final CallService callService = mock(CallServiceImpl.class);
    private final ExceptionHandlerService exceptionHandlerService = spy(ExceptionHandlerServiceImpl.class);

    private final BackendApiService classUnderTest = new BackendApiServiceImpl(callService, exceptionHandlerService);

    @BeforeEach
    void init() {
        Box mockedBox = getBoxWithNoEmptyFields();
        when((callService.getOneReactive(anyString(), any(HttpHeaders.class), eq(Box.class))))
                .thenReturn(Mono.just(mockedBox));
    }

    @Test
    void test_getOneBy_configured_apiToken_returns_box() {
        ApiToken configuredTestToken = getConfiguredBoxPersistenceApiToken();

        Mono<Box> result = classUnderTest.getOneBy(configuredTestToken, Box.class);

        StepVerifier.create(result)
                .assertNext(box -> assertThat(box).isEqualToComparingFieldByFieldRecursively(getBoxWithNoEmptyFields()))
                .verifyComplete();
    }

    @Test
    void test_getOneBy_configured_apiToken_returns_no_box_when_backend_could_not_calculate_one() {
        ApiToken configuredTestToken = getConfiguredBoxPersistenceApiToken();
        when(callService.getOneReactive(anyString(), any(HttpHeaders.class), eq(Box.class)))
                .thenReturn(Mono.empty());

        Mono<Box> result = classUnderTest.getOneBy(configuredTestToken, Box.class);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getOneBy_configured_apiToken_is_executed_correctly_with_right_arguments() {
        ApiToken configuredTestToken = getConfiguredBoxPersistenceApiToken();
        ArgumentCaptor<String> urlArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpHeaders> httpHeadersArg = ArgumentCaptor.forClass(HttpHeaders.class);

        classUnderTest.getOneBy(configuredTestToken, Box.class).block();

        verify(callService, times(1)).getOneReactive(urlArg.capture(), httpHeadersArg.capture(), eq(Box.class));
        assertThat(urlArg.getValue()).isEqualTo("http://localhost:8086/geocoding/get/operatingBox");
        assertThat(httpHeadersArg.getValue()).isEqualTo(HttpHeaders.EMPTY);
    }

    @Test
    void test_getOneBy_configured_apiToken_and_host_as_null_returns_no_box_when_exception_is_thrown() {
        ArgumentCaptor<Throwable> exceptionArg = ArgumentCaptor.forClass(Throwable.class);
        ApiToken configuredTestToken = new ApiToken(getConfiguredBoxPersistenceApiToken());
        configuredTestToken.setHost(null);

        Mono<Box> result = classUnderTest.getOneBy(configuredTestToken, Box.class);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
        verify(exceptionHandlerService, times(1)).handleException(exceptionArg.capture());
        assertThat(exceptionArg.getValue()).isInstanceOf(NullPointerException.class);
    }

    @Test
    void test_getOneBy_configured_apiToken_as_null_returns_no_box_when_expcetion_thrown_outside_of_stream() {

        Mono<Box> result = classUnderTest.getOneBy(null, null);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }

    @Test
    void test_getOneBy_configured_apiToken_and_error_by_callService_returns_no_box() {
        ApiToken configuredTestToken = getConfiguredBoxPersistenceApiToken();
        when(callService.getOneReactive(anyString(), any(HttpHeaders.class), eq(Box.class)))
                .thenReturn(Mono.error(new Exception()));

        Mono<Box> result = classUnderTest.getOneBy(configuredTestToken, Box.class);

        StepVerifier.create(result)
                .expectNextCount(0L)
                .verifyComplete();
    }
}
