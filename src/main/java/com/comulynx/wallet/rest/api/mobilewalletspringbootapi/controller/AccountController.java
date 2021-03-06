package com.comulynx.wallet.rest.api.mobilewalletspringbootapi.controller;


import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.AppUtilities;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.exception.ResourceNotFoundException;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.model.Account;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.repository.AccountRepository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    private Gson gson = new Gson();

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public List<Account> getAllAccount() {
        return accountRepository.findAll();
    }


    @PostMapping("/balance")
    public ResponseEntity<?> getAccountBalanceByCustomerIdAndAccountNo(@RequestBody String request)
            throws ResourceNotFoundException {
        try {
            JsonObject response = new JsonObject();

            final JsonObject balanceRequest = gson.fromJson(request, JsonObject.class);
            String customerId = balanceRequest.get("customerId").getAsString();
//            String accountNo = balanceRequest.get("accountNo").getAsString();

            // TODO : Add logic to find Account balance by CustomerId
            Account account = accountRepository.findAccountByCustomerId(customerId).orElse(null);

            response.addProperty("balance", account.getBalance());
            response.addProperty("accountNo", account.getAccountNo());
            return ResponseEntity.ok().body(gson.toJson(response));

        } catch (
                Exception ex) {
            logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));

            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }
}
