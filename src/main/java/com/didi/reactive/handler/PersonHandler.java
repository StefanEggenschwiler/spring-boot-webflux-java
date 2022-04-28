package com.didi.reactive.handler;

import com.didi.reactive.model.Person;
import com.didi.reactive.repo.PersonRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
public class PersonHandler {

    private final PersonRepository repository;

    public PersonHandler(PersonRepository repository) {
        this.repository = repository;
    }

    public Mono<ServerResponse> findAll(ServerRequest req) {
        return ServerResponse.ok().body(this.repository.findAll(), Person.class);
    }

    public Mono<ServerResponse> findById(ServerRequest req) {
        return repository.findById(Integer.valueOf(req.pathVariable("id")))
                .flatMap(person -> ServerResponse.ok().bodyValue(person))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> save(ServerRequest req) {
        return req.bodyToMono(Person.class)
                .flatMap(this.repository::save)
                .flatMap(p -> ServerResponse.created(URI.create("/v1/persons/" + p.getId())).build());
    }
    public Mono<ServerResponse> delete(ServerRequest req) {
        return ServerResponse.noContent().build(this.repository.deleteById(Integer.valueOf(req.pathVariable("id"))));
    }

    public Mono<ServerResponse> deleteAlternative(ServerRequest request) {
        return Mono.just(repository.deleteById(Integer.parseInt(request.pathVariable("id"))))
                .flatMap(val -> ServerResponse.noContent().build());
    }
}
