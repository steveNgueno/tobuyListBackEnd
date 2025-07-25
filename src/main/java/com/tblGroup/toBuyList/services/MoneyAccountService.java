package com.tblGroup.toBuyList.services;


import com.tblGroup.toBuyList.dto.AmountDTO;
import com.tblGroup.toBuyList.dto.MoneyAccountDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.repositories.ClientRepository;
import com.tblGroup.toBuyList.repositories.MoneyAccountRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MoneyAccountService {
	private MoneyAccountRepository moneyAccountRepository;
	private ClientRepository clientRepository;

	public MoneyAccountService(MoneyAccountRepository moneyAccountRepository, ClientRepository clientRepository) {
		this.moneyAccountRepository = moneyAccountRepository;
		this.clientRepository = clientRepository;
	}
	
	
	//	---------------------------------------------------------------------------------------------------------------------------------------
//	---------------------MoneyAccountManagement------------------------------------------------
	public MoneyAccount createAccount(int clientID, MoneyAccountDTO moneyAccount){
		Optional<Client>optionalClient= clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with this id not found");
		}
		
		MoneyAccount moneyAccountAdded = new MoneyAccount();
		moneyAccountAdded.setName(moneyAccount.name());
		moneyAccountAdded.setPhone(moneyAccount.phone());
		moneyAccountAdded.setPassword(moneyAccount.password());
		
		return moneyAccountRepository.save(moneyAccountAdded);
	}
	
	public MoneyAccount getAccountByID(int clientID, int mAccountID){
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("Money account with ID: " + mAccountID + " not found for client ID: " + clientID + ".");
			}
			
			Hibernate.initialize(moneyAccount.getClient());
			
			return moneyAccount;
		}
		
	public List<MoneyAccount>getAllAccounts(int clientID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			Client clientSaved = optionalClient.get();
			
			List<MoneyAccount>moneyAccountList = moneyAccountRepository.findAllByClientId(clientID);
			
			for (MoneyAccount ma: moneyAccountList){
				Hibernate.initialize(ma.getClient());
			}
			
			return moneyAccountList;
		}
		
	public MoneyAccount updateAccount(int clientID, int mAccountID,  MoneyAccountDTO newMoneyAccount){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccountFound = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccountFound != null){
				moneyAccountFound.setPassword(newMoneyAccount.password());
				
				moneyAccountRepository.save(moneyAccountFound);
				
				return moneyAccountFound;
			}
			
			throw new IllegalArgumentException("No money account found at this ID.");
		}
		
	public Boolean deleteAccount(int clientID, int mAccountID){
			Optional<Client>optionalClient = clientRepository.findById(clientID);
			
			if (optionalClient.isEmpty()){
				throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
			}
			
			MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
			
			if (moneyAccount == null){
				throw new IllegalArgumentException("This client has no account at this ID, You could create him.");
			}
			
			moneyAccountRepository.delete(moneyAccount);
			
			return true;
	}
	
	
//	----------------------------------TRANSACTIONS MANAGEMENT----------------------------------------------------------
	
	public MoneyAccount makeDeposit(int clientID, AmountDTO amountDTO, int mAccountID) throws Exception {
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		
		
		if (moneyAccount != null) {
			if (amountDTO.amount() <= 0){
				throw new Exception("This amount is invalid, please try again  with amount up to 0 ");
			}
			moneyAccount.setAmount(moneyAccount.getAmount() + amountDTO.amount());
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
	
	public MoneyAccount makeRetrieve(int clientID, AmountDTO amountDTO, int mAccountID) throws Exception {
		Optional<Client>optionalClient = clientRepository.findById(clientID);
		
		if (optionalClient.isEmpty()){
			throw new IllegalArgumentException("Client with the ID: "+clientID+" not found.");
		}
		
		MoneyAccount moneyAccount = moneyAccountRepository.findByClient_IdAndId(clientID, mAccountID);
		
		if (moneyAccount != null){
			if (amountDTO.amount() <= 0 || amountDTO.amount() > moneyAccount.getAmount()){
				throw new Exception("This amount is invalid, please try again  with amount up to 0 & down to: "+moneyAccount.getAmount());
			}
			
			moneyAccount.setAmount(moneyAccount.getAmount() - amountDTO.amount());
			
			moneyAccountRepository.save(moneyAccount);
			
			return moneyAccount;
		}
		
		throw new IllegalArgumentException("Not account found at this ID: "+mAccountID);
	}
}
