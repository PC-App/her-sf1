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

    	// TODO Auto-generated method stub
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        Map map = new HashMap<String, String>();
        map.put("Content-Type", "application/json");

        headers.setAll(map);

        Map req_payload = new HashMap();
        req_payload.put("name", "piyush");

        HttpEntity<?> request = new HttpEntity<>(req_payload, headers);
        String url = "http://localhost:8080/xxx/xxx/";

        ResponseEntity<?> response = new RestTemplate().postForEntity(url, request, String.class);
        ServiceResponse entityResponse = (ServiceResponse) response.getBody();
        System.out.println(entityResponse.getData());

        return force.accounts(principal);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringSalesforceApplication.class, args);

    }

}
