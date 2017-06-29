package com.patientConnect.controller;

/**
 * Created by hidhingra on 6/21/2017.
 */

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patientConnect.model.AuthTokenInfo;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping
public class SalesforceController {

    public static final String REST_SERVICE_URI = "https://pcfeature-dev-ed.my.salesforce.com";

    public static final String AUTH_SERVER_URI = "http://localhost:8080/SpringSecurityOAuth2Example/oauth/token";

    public static final String QPM_PASSWORD_GRANT = "?grant_type=password&username=bill&password=abc123";

    public static final String QPM_ACCESS_TOKEN = "?access_token=";
    private static String QUERY_ENDPOINT = "/services/data/v20.0/";
    private static String REST_ENDPOINT = "/services/data/v20.0/sobjects/";
    private static String AUTH_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
    private String client_id;
    private String client_secret;

    /*
     * Prepare HTTP Headers.
     */
    private static HttpHeaders getHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
       // headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
 //       headers.add("content-Type", "application/x-www-form-urlencoded");
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    /*
     * Add HTTP Authorization header, using Basic-Authentication to send client-credentials.
     */
    private static HttpHeaders getHeadersWithClientCredentials(){
        String plainClientCredentials="my-trusted-client:secret";
        String base64ClientCredentials = new String(Base64.encodeBase64(plainClientCredentials.getBytes()));

        HttpHeaders headers = getHeaders("");
        headers.add("Authorization", "Bearer " + "");
        return headers;
    }

    /*
     * Send a POST request [on /oauth/token] to get an access-token, which will then be send with each request.
     */
    @SuppressWarnings({ "unchecked"})
    private static AuthTokenInfo sendTokenRequest(){
        RestTemplate restTemplate = new RestTemplate();

        HttpEntity<String> request = new HttpEntity<String>(getHeadersWithClientCredentials());

        ResponseEntity<Object> response = restTemplate.exchange(AUTH_SERVER_URI+QPM_PASSWORD_GRANT, HttpMethod.POST, request, Object.class);

        LinkedHashMap<String, Object> map = (LinkedHashMap<String, Object>)response.getBody();
        AuthTokenInfo tokenInfo = null;

        if(map!=null){
            tokenInfo = new AuthTokenInfo();
            tokenInfo.setAccess_token((String)map.get("access_token"));
            tokenInfo.setToken_type((String)map.get("token_type"));
            tokenInfo.setRefresh_token((String)map.get("refresh_token"));
            tokenInfo.setExpires_in((int)map.get("expires_in"));
            tokenInfo.setScope((String)map.get("scope"));
            System.out.println(tokenInfo);
            //System.out.println("access_token ="+map.get("access_token")+", token_type="+map.get("token_type")+", refresh_token="+map.get("refresh_token")
            //+", expires_in="+map.get("expires_in")+", scope="+map.get("scope"));;
        }else{
            System.out.println("No user exist----------");

        }
        return tokenInfo;
    }

    @RequestMapping(value={"/rest/testaccounts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public JsonNode getAccounts(@RequestParam("token") String asserttoken) throws IOException
    {
        Assert.notNull(asserttoken, "Authenticate first please......");

        String url = REST_SERVICE_URI+QUERY_ENDPOINT + "query/?q={q}";

        Map<String, String> params = new HashMap<>();
        params.put("q", "SELECT Id, Name, Type, Industry, Rating FROM Account");

       //String url = REST_SERVICE_URI+QUERY_ENDPOINT+ "query/?q=SELECT Id, Name, Type, Industry, Rating FROM Account";
        RestTemplate restTemplate = new RestTemplate();
        System.out.println("here3: "+url);
        String url1=REST_SERVICE_URI+QUERY_ENDPOINT + "query/?q=SELECT Id, Name, Type, Industry, Rating FROM Account";

        HttpEntity<String> request = new HttpEntity<String>(getHeaders(asserttoken));

       ResponseEntity<String> response = restTemplate.exchange(url1,
                HttpMethod.GET, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
       System.out.println("here4: ");

       return root;


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
