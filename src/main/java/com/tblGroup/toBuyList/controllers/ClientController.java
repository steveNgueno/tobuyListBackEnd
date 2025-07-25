package com.tblGroup.toBuyList.controllers;


import com.tblGroup.toBuyList.dto.ClientDTO;
import com.tblGroup.toBuyList.models.Client;
import com.tblGroup.toBuyList.models.MoneyAccount;
import com.tblGroup.toBuyList.services.ClientService;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(path = "/client")
public class ClientController {
	private ClientService clientService;
	
	public ClientController(ClientService clientService) {
		this.clientService = clientService;
	}
	
//	------------------------------------------------------------------------------------------
// -------------------------------------------------------CLIENT MANAGEMENT-----------------------------------------------------------------
	
	@PostMapping(path = "/create", consumes = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>createClient(@RequestBody Client client){
		try {
			Client savedClient = clientService.createClient(client);
			
			return new ResponseEntity<>(savedClient, HttpStatus.CREATED);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@GetMapping(path = "/get/{clientID}", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>getClient(@PathVariable int clientID){
		try {
			Client foundClient = clientService.getClientById(clientID);
			
			return new ResponseEntity<>(foundClient, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping(path = "/get", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Client>> getAllClient(){
		List<Client>clientList = clientService.getAllClients();
		
		return new ResponseEntity<>(clientList, HttpStatus.OK);
	}
	
	@PutMapping(path = "/update/{clientID}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Client>updateClient(@PathVariable int clientID, @RequestBody ClientDTO newClient){
		try {
			Client updatedClient = clientService.updateClient(clientID, newClient);
			
			return  new ResponseEntity<>(updatedClient, HttpStatus.OK);
		}catch (IllegalArgumentException e){
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}catch (Exception e){
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
	
	@DeleteMapping(path = "/delete/{clientID}")
	public ResponseEntity<Void>deleteClient(@PathVariable int clientID){
		try {
			clientService.deleteClient(clientID);
			
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			
		} catch (IllegalArgumentException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
}
