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


//@ComponentScan(basePackages = "com.luka")
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
			userRolesRepository.save(new UserRole(newUser.getId(), "ADMIN"));
			newUser  = repository.save(new User("user", "1234"));
			userRolesRepository.save(new UserRole(newUser.getId(), "USER"));
		};
	}

	/*@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			System.out.println("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}

		};
	}*/
}
