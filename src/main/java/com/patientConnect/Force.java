package com.patientConnect;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Force {

    private static final String REST_VERSION = "35.0";

    @Bean
    private OAuth2RestTemplate oAuth2RestTemplate(OAuth2ProtectedResourceDetails resource, OAuth2ClientContext context) {
        return new OAuth2RestTemplate(resource, context);
    }

    @Autowired
    private OAuth2RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    private static String restUrl(OAuth2Authentication principal) {
        HashMap<String, Object> details = (HashMap<String, Object>) principal.getUserAuthentication().getDetails();
        HashMap<String, String> urls = (HashMap<String, String>) details.get("urls");
        return urls.get("rest").replace("{version}", REST_VERSION);
    }

    public List<Account> accounts(OAuth2Authentication principal) {
        String url = restUrl(principal) + "query/?q={q}";

        Map<String, String> params = new HashMap<>();
        params.put("q", "SELECT Id, Name, Type, Industry, Rating FROM Account");
        //JSONArray arr_strJson=new JSONArray(restTemplate.getForObject(url, QueryResultAccount.class, params).records);
       // JSONArray arr_strJson = new JSONArray(Arrays.asList(arr_str));

        return restTemplate.getForObject(url, QueryResultAccount.class, params).records;
       //return arr_strJson.toString();
    }

    public void insertAccountabc(OAuth2Authentication principal){
        String url = restUrl(principal) + "sobjects/Account/";

        String input = "{\"name\":\"testName\"}";
        //HttpHeaders headers = new HttpHeaders();
        //headers.setContentType(MediaType.APPLICATION_JSON);
        //headers.set("Authorization", "Bearer");
        HttpEntity<String> entity = new HttpEntity<String>(input);

        MultiValueMap<String, String> post = new LinkedMultiValueMap<String, String>();
        post.add("name", "TestAcctName");
        restTemplate.postForObject(url, post, QueryResultAccount.class,"");

        //restTemplate.postForObject(url,QueryResultAccount.class);
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

    private static class QueryResultAccount extends QueryResult<Account> {}

}
