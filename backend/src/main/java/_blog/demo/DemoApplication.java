package _blog.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

 
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}


	// @Bean
	// CommandLineRunner commandLineRunner(UserRepository userRepository) {
	// 	return args -> {
	// 		User user = new User(null,"momo","1234");
	// 		userRepository.save(user);
	// 	};
	// }

}
