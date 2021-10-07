package com.comulynx.wallet.rest.api.mobilewalletspringbootapi.repository;

import com.comulynx.wallet.rest.api.mobilewalletspringbootapi.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findAccountByCustomerId(String customerId);

	Optional<Account> findAccountByAccountNo(String customerId);

	Optional<Account> findAccountByCustomerIdOrAccountNo(String customerId, String accountNo);
	
	Optional<Account> findAccountByCustomerIdAndAccountNo(String customerId, String accountNo);
	
	

}
