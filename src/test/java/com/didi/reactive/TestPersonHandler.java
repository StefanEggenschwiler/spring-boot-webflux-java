package com.didi.reactive;

import com.didi.reactive.model.Person;
import com.didi.reactive.repo.PersonRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestPersonHandler {

	@Autowired
	private ApplicationContext context;

	@MockBean
	private PersonRepository repository;

	private WebTestClient webTestClient;

	@BeforeEach
	public void setUp() {
		webTestClient = WebTestClient.bindToApplicationContext(context).build();
	}

	@Test
	void testGetAll() {
		Person person1 = Person.builder().id(1).surname("Hans").name("Wurst").age(23).build();
		Person person2 = person1.toBuilder().id(2).surname("Rolf").name("Tester").age(39).build();
		Flux<Person> userListFlux = Flux.fromIterable(Arrays.asList(person1, person2));
		when(repository.findAll()).thenReturn(userListFlux);
		webTestClient.get()
				.uri("/v1/persons")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Person.class)
				.contains(person1, person2);
		verify(repository, times(1)).findAll();
	}

	@Test
	void testGetUserById() {
		Person person = Person.builder().id(1).surname("Hans").name("Wurst").age(23).build();
		Mono<Person> userMono = Mono.just(person);
		when(repository.findById(1)).thenReturn(userMono);
		webTestClient.get()
				.uri("/v1/persons/1")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Person.class)
				.value(personResponse -> {
							Assertions.assertThat(personResponse.getId()).isEqualTo(1);
							Assertions.assertThat(personResponse.getSurname()).isEqualTo("Hans");
							Assertions.assertThat(personResponse.getName()).isEqualTo("Wurst");
							Assertions.assertThat(personResponse.getAge()).isEqualTo(23);
						}
				);
		verify(repository, times(1)).findById(1);
	}

	@Test
	void testGetUserByIdNotFound() {
		when(repository.findById(1)).thenReturn(Mono.empty());
		webTestClient.get()
				.uri("/v1/persons/1")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody().isEmpty();
		verify(repository, times(1)).findById(1);
	}

	@Test
	public void createUser() {
		Person person = Person.builder().id(1).surname("Hans").name("Wurst").age(23).build();
		when(repository.save(person)).thenReturn(Mono.just(person));
		webTestClient.post()
				.uri("/v1/persons")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(person))
				.exchange()
				.expectStatus().isCreated()
				.expectHeader().location("/v1/persons/1")
				.expectBody().isEmpty();
		verify(repository, times(1)).save(person);
	}

	@Test
	public void deleteUser() {
		when(repository.deleteById(1)).thenReturn(Mono.empty());
		webTestClient.delete()
				.uri("/v1/persons/1")
				.exchange()
				.expectStatus().isNoContent()
				.expectBody().isEmpty();
		verify(repository, times(1)).deleteById(1);
	}
}
