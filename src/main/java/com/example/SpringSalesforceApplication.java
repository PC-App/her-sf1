package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
@EnableOAuth2Sso
public class SpringSalesforceApplication {

    @Autowired
   private Force force;

    @RequestMapping("/accounts")
    public List<Force.Account> accounts(OAuth2Authentication principal) {

        return force.accounts(principal);
    }

/*
	@RequestMapping(path = "/post", method = RequestMethod.POST)
	String post(@RequestParam String text) {
		restTemplate.postForObject(messageApi, Collections.singletonMap("text", text), Void.class);
		return "redirect:/";
	}
*/
	

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    public static void main(String[] args) {

        SpringApplication.run(SpringSalesforceApplication.class, args);

    }

}
