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
import reactor.core.publisher.Mono;

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
	void testGetUserById() {
		Person person = Person.builder().id(1).surname("Hans").name("Wurst").age(23).build();
		//Person z = person.toBuilder().id(2).build();
		Mono<Person> UserMono = Mono.just(person);
		when(repository.findById(1)).thenReturn(UserMono);
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
}
