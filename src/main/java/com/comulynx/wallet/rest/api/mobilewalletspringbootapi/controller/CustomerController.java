package com.comulynx.wallet.rest.api.mobilewalletspringbootapi.controller;

import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.AppUtilities;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.model.Account;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.model.Customer;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.repository.AccountRepository;
import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.repository.CustomerRepository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.List;
import java.util.Objects;
import java.util.Random;


//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private Gson gson = new Gson();

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    /**
     * Fix Customer Login functionality
     * <p>
     * Login
     *
     * @param request
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> customerLogin(@RequestBody String request)  {
        try {
            JsonObject response = new JsonObject();

            final JsonObject req = gson.fromJson(request, JsonObject.class);
            String customerId = req.get("customerId").getAsString();
            String customerPIN = req.get("pin").getAsString();

            // TODO : Add Customer login logic here. Login using customerId and
            // PIN
            // NB: We are using plain text password for testing Customer login
            // If customerId doesn't exists throw an error "Customer does not exist"
            // If password do not match throw an error "Invalid credentials"
            List<Customer> customers = customerRepository.findAll();
            for (Customer customer : customers) {
                if (customer.getCustomerId().equals(customerId)) {
                    if (customer.getPin().equals(customerPIN)) {
                        customerRepository.save(customer);

                        //TODO : Return a JSON object with the following after successful login
                        //Customer Name, Customer ID, email and Customer Account
                        response.addProperty("Customer Name", customer.getFirstName() + " " + customer.getLastName());
                        response.addProperty("Customer ID", customer.getCustomerId());
                        response.addProperty("email", customer.getEmail());
                        response.addProperty("Customer Account", String.valueOf(accountRepository.findAccountByCustomerId(customer.getCustomerId())));
                        return ResponseEntity.ok().body(gson.toJson(response));
                    } else {
                        return new ResponseEntity<>("INVALID CREDENTIALS", HttpStatus.FORBIDDEN);
                    }

                } else {
                    return new ResponseEntity<>("Customer does not exist", HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception ex) {
            logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return ResponseEntity.status(200).body(HttpStatus.OK);

    }

    /**
     * Add required logic
     * <p>
     * Create Customer
     *
     * @param customer
     * @return
     */
    @PostMapping("/")
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {
        try {
            String customerPIN = customer.getPin();
            String email = customer.getEmail();

            // TODO : Add logic to Hash Customer PIN here
            //  : Add logic to check if Customer with provided email, or
            // customerId exists. If exists, throw a Customer with [?] exists
            // Exception.

            SecureRandom secureRandom = new SecureRandom();
            byte[] salt = new byte[16];
            secureRandom.nextBytes(salt);
            KeySpec keySpec = new PBEKeySpec(customerPIN.toCharArray(), salt, 655366, 128);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hashedPin = secretKeyFactory.generateSecret(keySpec).getEncoded();

            String accountNo = generateAccountNo(customer.getCustomerId());
            Account account = new Account();
            account.setCustomerId(customer.getCustomerId());
            account.setAccountNo(accountNo);
            account.setBalance(0.0);

            if (!Objects.equals(email, customer.getEmail()) || !Objects.equals(account.getCustomerId(), customer.getCustomerId())) {
                accountRepository.save(account);
                return ResponseEntity.ok().body("");
            } else {
                return new ResponseEntity<>("Email already taken", HttpStatus.BAD_REQUEST);
            }

        } catch (Exception ex) {
            logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    /**
     * Add required functionality
     * <p>
     * generate a random but unique Account No (NB: Account No should be unique
     * in your accounts table)
     */
    private String generateAccountNo(String customerId) {
        // TODO : Add logic here - generate a random but unique Account No (NB:
        // Account No should be unique in the accounts table)
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        if (!generatedString.equals(accountRepository.findAccountByAccountNo(customerId).toString())) {
            return generatedString;

        } else {
            return "Account Number already taken";
        }

    }
}
