package Ecommerce.usermanagement.repository;

import Ecommerce.usermanagement.document.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface IUsersRepository extends ReactiveMongoRepository<User, String> {

   Mono<User> findByUuid(String uuid);

   Mono<User> findByEmail(String email);

   Mono<User> findByUsername(String username);

   Mono<Boolean> existsByEmail(String email);

   Mono<Boolean> existsByUsername(String username);

   Mono<Boolean> existsByUuid(String uuid);

}
