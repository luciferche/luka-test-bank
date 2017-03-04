package com.luka;

import com.luka.model.User;
import com.luka.model.UserRepository;
import com.luka.model.UserRole;
import com.luka.model.UserRolesRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Arrays;


@SpringBootApplication
public class BankApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankApplication.class, args);
	}


	@Bean
	public CommandLineRunner demo(UserRepository repository, UserRolesRepository userRolesRepository) {
		return (args) -> {
			// save a couple of customers
			User newUser  = repository.save(new User("luka.matovic", "1234"));
			userRolesRepository.save(new UserRole(newUser.getId(), "ROLE_ADMIN"));
			newUser  = repository.save(new User("user", "1234"));
			userRolesRepository.save(new UserRole(newUser.getId(), "ROLE_USER"));
			newUser  = repository.save(new User("test", "1234"));
			userRolesRepository.save(new UserRole(newUser.getId(), "ROLE_USER"));
		};
	}

}
