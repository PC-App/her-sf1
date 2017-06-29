package com.patientConnect.controller;

/**
 * Created by hidhingra on 6/19/2017.
 */
import com.patientConnect.services.SalesforceService;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;






@RestController
@RequestMapping
public class AccessController
{
    @Autowired
    private SalesforceService sfService;
    private Map<String, JsonNode> tokenMap = new HashMap();

    public AccessController() {}

    @RequestMapping(value={"/rest/login"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String loginTest(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret) throws IOException {
        JsonNode node = (JsonNode)tokenMap.get(name);
        if (node == null) {
            node = sfService.authenticate(name, password, secret);
        }
        return sfService.getLoggedInUserInfo(node);
    }

    @RequestMapping(value={"/rest/credentials"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"application/json"})
    public String setCredentials(@RequestBody JsonNode entity) throws IOException {
        sfService.setCredentials(entity);
        return "Successfully set the SF credentials you can start interacting with SalesForce now";
    }

    @RequestMapping(value={"/rest/add/account"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"application/json"})
    public String addAccount(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret, @RequestBody JsonNode entity) throws IOException
    {
        if ((!entity.has("name")) && (!entity.has("Name"))) {
            return "invalid request.. Missing mandatory attirbute 'name'";
        }
        return insert(name, password, secret, "Account", entity);
    }

    @RequestMapping(value={"/rest/add/contact"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"application/json"})
    public String addContact(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret, @RequestBody JsonNode entity) throws IOException
    {
        if ((!entity.has("lastname")) && (!entity.has("LastName"))) {
            return "invalid request.. Missing mandatory attirbute 'lastname'";
        }
        return insert(name, password, secret, "Contact", entity);
    }


    @RequestMapping(value={"/rest/insert"}, method={org.springframework.web.bind.annotation.RequestMethod.POST}, consumes={"application/json"})
    public String insert(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret, @RequestParam("type") String type, @RequestBody JsonNode entity)
            throws IOException
    {
        JsonNode authNode = (JsonNode)tokenMap.get(name);
        if (authNode == null) {
            authNode = sfService.authenticate(name, password, secret);
        }
        return sfService.executeInsert(type, authNode, entity);
    }

    @RequestMapping(value={"/rest/accounts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String getAccounts(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret) throws IOException
    {
        return executeQuery(name, password, secret, "SELECT Name, id, OwnerId, Industry FROM Account");
    }

    @RequestMapping(value={"/rest/contacts"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String getContacts(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret) throws IOException
    {
        return executeQuery(name, password, secret, "SELECT Name, id, Email, AccountId, Department FROM Contact");
    }

    @RequestMapping(value={"/rest/assets"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String getAssets(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret) throws IOException
    {
        return executeQuery(name, password, secret, "SELECT Name, id, ContactId, AccountId, Price FROM Asset");
    }

    @RequestMapping(value={"/rest/orders"}, method={org.springframework.web.bind.annotation.RequestMethod.GET})
    public String getOrders(@RequestParam("name") String name, @RequestParam("sname") String password, @RequestParam("token") String secret) throws IOException
    {
        return executeQuery(name, password, secret, "SELECT Name, id, orderNumber, Description FROM Order");
    }

    private String executeQuery(String name, String password, String secret, String query) throws IOException {
        JsonNode authNode = (JsonNode)tokenMap.get(name);
        if (authNode == null) {
            authNode = sfService.authenticate(name, password, secret);
        }
        return sfService.executeQuery(authNode, query);
    }
}
