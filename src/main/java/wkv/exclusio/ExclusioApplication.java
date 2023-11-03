package wkv.exclusio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ExclusioApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ExclusioApplication.class, args);
	}
	   
    @Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
    
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200") // Specify the allowed origin(s)
                        .allowedMethods("GET", "POST", "PUT", "DELETE") // Specify the allowed HTTP methods
                        .allowedHeaders("*"); // Specify the allowed headers
            }
        };
    }

}
