package com.didi.reactive.router;

import com.didi.reactive.handler.PersonHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class PersonRouterConfig {

    //@RouterOperations({ @RouterOperation(path = "/getAllPersons", beanMethod = "getAll")})
    @Bean
    public RouterFunction<ServerResponse> personRoute(PersonHandler handler) {
        return RouterFunctions
                .route(GET("/v1/persons").and(accept(MediaType.APPLICATION_JSON)), handler::findAll)
                .andRoute(GET("/v1/persons/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::findById)
                .andRoute(POST("/v1/persons").and(accept(MediaType.APPLICATION_JSON)), handler::save)
                .andRoute(DELETE("/v1/persons/{id}").and(accept(MediaType.APPLICATION_JSON)), handler::delete);
    }
}
