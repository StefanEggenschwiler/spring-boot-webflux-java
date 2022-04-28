package com.didi.reactive.controller;

import com.didi.reactive.model.Person;
import com.didi.reactive.repo.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/persons")
public class PersonController {

    @Autowired
    PersonRepository repository;

    @GetMapping
    public Flux<Person> getPersons() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Person> getPerson(@PathVariable Integer id) {
        return repository.findById(id);
    }

    @PostMapping
    public Mono<Person> createPerson(@RequestBody Person person) {
        return repository.save(person);
    }

    @PutMapping("/{id}")
    public Mono<Person> updatePerson(@RequestBody Person person, @PathVariable Integer id) {
        return repository.findById(id)
                .map(p -> person)
                .doOnNext(e -> e.setId(id))
                .flatMap(c -> repository.save(c));
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deletePerson(@PathVariable Integer id) {
        return repository.deleteById(id);
    }
}
