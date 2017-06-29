package com.patientConnect;

import com.patientConnect.controller.AccountController;
import jdk.nashorn.internal.ir.RuntimeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@SpringBootApplication
@RestController
public class SpringSalesforceApplication {

    @Autowired
    private Force force;

    @Autowired
    private AccountController ac;

    @RequestMapping(value="/accounts",method= RequestMethod.GET)
    public List<Force.Account> accounts(OAuth2Authentication principal) {

        return force.accounts(principal);
    }

   @RequestMapping(value="/accountops",method=RequestMethod.GET)
    public void insertAccounts(OAuth2Authentication principal)
    {
        force.insertAccountabc(principal);

    }


    @RequestMapping(value="/processaccount",method=RequestMethod.POST,consumes = "text/plain")
    public void updateaccounts(OAuth2Authentication principal)
    {

       // System.out.println("got: "+test);
        ac.create(principal);
    }



    public static void main(String[] args) {
        SpringApplication.run(SpringSalesforceApplication.class, args);
    }

   /*@EnableWebSecurity
    public class WebSecurityConfig extends
            WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .csrf().disable();
        }
    }*/
}
