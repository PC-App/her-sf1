package com.patientConnect.services;

/**
 * Created by hidhingra on 6/19/2017.
 */


        import com.fasterxml.jackson.databind.JsonNode;
        import com.fasterxml.jackson.databind.ObjectMapper;
        import java.io.IOException;
        import java.io.PrintStream;
        import java.net.URI;
        import java.net.URISyntaxException;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;
        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.NameValuePair;
        import org.apache.http.client.entity.UrlEncodedFormEntity;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpPost;
        import org.apache.http.client.utils.URIBuilder;
        import org.apache.http.entity.ContentType;
        import org.apache.http.entity.StringEntity;
        import org.apache.http.impl.client.CloseableHttpClient;
        import org.apache.http.impl.client.HttpClients;
        import org.apache.http.message.BasicNameValuePair;
        import org.apache.http.util.EntityUtils;
        import org.springframework.stereotype.Service;


@Service
public class SalesforceService
{
    private static String QUERY_ENDPOINT = "/services/data/v20.0/query";
    private static String REST_ENDPOINT = "/services/data/v20.0/sobjects/";
    private static String AUTH_ENDPOINT = "https://login.salesforce.com/services/oauth2/token";
    private String client_id;
    private String client_secret;

    public SalesforceService() {}

    private Map<String, JsonNode> tokenMap = new HashMap();
    private ObjectMapper om = new ObjectMapper();
    CloseableHttpClient client = HttpClients.createDefault();

    public void setCredentials(JsonNode entity) {
        client_id = entity.get("client_id").asText();
        client_secret = entity.get("client_secret").asText();
    }

    public JsonNode authenticate(String name, String password, String secret) throws IOException {
        HttpPost htpost = new HttpPost(AUTH_ENDPOINT);
        htpost.addHeader("content-Type", "application/x-www-form-urlencoded");
        List<NameValuePair> parmalist = new ArrayList();

        parmalist.add(new BasicNameValuePair("grant_type", "password"));
        parmalist.add(new BasicNameValuePair("client_id", client_id));
        parmalist.add(new BasicNameValuePair("client_secret", client_secret));
        parmalist.add(new BasicNameValuePair("username", name));
        parmalist.add(new BasicNameValuePair("password", password + secret));
        htpost.setEntity(new UrlEncodedFormEntity(parmalist));
        HttpResponse response = client.execute(htpost);

        JsonNode node = om.readTree(response.getEntity().getContent());
        System.out.println("auth node data --> " + node);
        tokenMap.put(name, node);
        return node;
    }

    public String executeQuery(JsonNode authNode, String query)
            throws IOException
    {
        String instanceUrl = authNode.get("instance_url").asText();
        String token = authNode.get("access_token").asText();
        URI uri = null;
        try {
            uri = new URIBuilder(instanceUrl + QUERY_ENDPOINT).setParameter("q", query).build();
        }
        catch (URISyntaxException e) {
            return "Malformed query.. URISyntaxException";
        }
        HttpGet httpGet = new HttpGet(uri);
        httpGet.addHeader("Authorization", "Bearer " + token);
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded;");
        HttpResponse httpResponse = client.execute(httpGet);
        String response = EntityUtils.toString(httpResponse.getEntity());

        System.out.println("query --> " + query + " response --> " + response);
        JsonNode resNode = om.readTree(response);
        JsonNode records = resNode.get("records");
        return records.toString();
    }

    public String getLoggedInUserInfo(JsonNode node) throws IOException {
        String id = node.get("id").asText();
        String token = node.get("access_token").asText();
        HttpPost postReq = new HttpPost(id);
        postReq.addHeader("Authorization", "Bearer " + token);
        HttpResponse httpResponse = client.execute(postReq);
        return EntityUtils.toString(httpResponse.getEntity());
    }

    public String executeInsert(String type, JsonNode authNode, JsonNode entity) throws IOException {
        String instanceUrl = authNode.get("instance_url").asText();
        String token = authNode.get("access_token").asText();
        URI uri = null;
        try {
            uri = new URIBuilder(instanceUrl + REST_ENDPOINT + type + "/").build();
        } catch (URISyntaxException e) {
            return "Malformed query... URISyntax exception";
        }
        HttpPost httpPost = new HttpPost(uri);
        httpPost.addHeader("Authorization", "Bearer " + token);
        httpPost.setEntity(new StringEntity(entity.toString(), ContentType.APPLICATION_JSON));
        HttpResponse httpResponse = client.execute(httpPost);
        String response = EntityUtils.toString(httpResponse.getEntity());
        System.out.println(" inserted into " + type + " with data --> " + entity + " got response --> " + response);
        return response;
    }
}
