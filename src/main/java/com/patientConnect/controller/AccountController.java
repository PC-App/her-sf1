package com.patientConnect.controller;

/**
 * Created by hidhingra on 6/13/2017.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.patientConnect.Force;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@EnableOAuth2Sso
@RequestMapping("/aiccountopsiii")
public class AccountController {
    public static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private static final String REST_VERSION = "35.0";
    @Autowired
    private OAuth2RestTemplate restTemplate;


    // -------------------Retrieve Single Account------------------------------------------

    @SuppressWarnings("unchecked")
    private static String restUrl(OAuth2Authentication principal) {
        HashMap<String, Object> details = (HashMap<String, Object>) principal.getUserAuthentication().getDetails();
        HashMap<String, String> urls = (HashMap<String, String>) details.get("urls");
        return urls.get("rest").replace("{version}", REST_VERSION);
    }

    @RequestMapping(method=RequestMethod.GET)
    public List<Force.Account> getAccounts(OAuth2Authentication principal) {
        String a="0014100000M7OKQAA3";
        String url = restUrl(principal) + "query/?q={q}";

        Map<String, String> params = new HashMap<>();
        params.put("q", "SELECT Id, Name, Type, Industry, Rating FROM Account="+a);
        //JSONArray arr_strJson=new JSONArray(restTemplate.getForObject(url, QueryResultAccount.class, params).records);
        // JSONArray arr_strJson = new JSONArray(Arrays.asList(arr_str));

        return restTemplate.getForObject(url, QueryResultAccount.class, params).records;
    }

    @RequestMapping(method=RequestMethod.POST)
    public void create(OAuth2Authentication principal) {
        System.out.println("came here");
        String url = restUrl(principal) + "sobjects/Account/0014100000LvmxiAAB";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer 00D41000000TgZI!AQYAQGl3H0Ufs2QI_4pRGM6nbdLYIgoEI_yHDbQhS1lVSCLB32fv7enhe.7xgLRksa.4fts8suUFWeMGX2scB.8jLBKApeYn");


        MultiValueMap<String, String> post = new LinkedMultiValueMap<String, String>();
        post.add("PCFeature31__PC_Gender__c", "Male");
        HttpEntity<?> request = new HttpEntity<>(post, headers);
        ResponseEntity<?> response = new RestTemplate().postForEntity(url, request, String.class);
        HttpStatus statusCode = response.getStatusCode();
        if (statusCode == HttpStatus.ACCEPTED) {
            System.out.println("Got success");
        }

        //return null;
    }




    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Account {
        public String Id;
        public String Name;
        public String Industry;
        public String Rating;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class QueryResult<T> {
        public List<T> records;
    }

    private static class QueryResultAccount extends QueryResult<Force.Account> {}

}
