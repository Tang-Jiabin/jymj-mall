package com.jymj.mall.gateway.swagger.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import springfox.documentation.swagger.web.UiConfigurationBuilder;



@Component
public class SwaggerUiHandler implements HandlerFunction<ServerResponse> {




    @Override
    public Mono<ServerResponse> handle(ServerRequest request) {
        Mono<ServerResponse> responseMono = ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters
                        .fromValue(UiConfigurationBuilder.builder().build()));
        return responseMono;
    }

}
