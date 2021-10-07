package com.comulynx.wallet.rest.api.mobilewalletspringbootapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    //FIXME : Add Spring boot Dev Tools in the pom.xml file
    //FIXME : Add h2 database in the pom.xml file

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
