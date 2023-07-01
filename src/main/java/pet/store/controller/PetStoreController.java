package pet.store.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.service.PetStoreService;

@RestController 
@RequestMapping
@Slf4j
public class PetStoreController {
	
	@Autowired
	private PetStoreService petStoreService;
	
	//Create
	@PostMapping("/pet_store")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreData createPetStore(
			@RequestBody PetStoreData petStoreData) {
		log.info("Creating Pet Store {}", petStoreData);
		
		return petStoreService.savePetStore(petStoreData);
	}
	//Create Pet Store Employee
	@PostMapping("/pet_store/{petStoreId}/employee")
	@ResponseStatus(code = HttpStatus.CREATED)
	public PetStoreEmployee savePetStoreEmployee(@PathVariable Long petStoreId,
			@RequestBody PetStoreEmployee petStoreEmployee) {
		log.info("Creating Employee with ID={} ", petStoreEmployee);
		return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
	}
	
	
	//Update
	@PutMapping("/pet_store/{petStoreId}")
	public PetStoreData updatePetStore(
			@PathVariable Long petStoreId, @RequestBody PetStoreData petStoreData) {
		petStoreData.setPetStoreId(petStoreId);
		log.info("Updating pet store {}", petStoreData);
		return petStoreService.savePetStore(petStoreData);
	}
	
	//Update Pet Store Employee
	@PutMapping("/pet_store/{petStoreId}/employee")
	public PetStoreEmployee updatePetStoreEmployee(@PathVariable Long petStoreId, 
			@RequestBody PetStoreEmployee petStoreEmployee,
			@RequestBody PetStoreData petStoreData) {
		petStoreData.setPetStoreId(petStoreId);
		return petStoreService.saveEmployee(petStoreId, petStoreEmployee);
	}
	
	//Get list of Pet Stores
	@GetMapping("/pet_store")
	public List <PetStoreData> listAllPetStores(){
		log.info("Retrieve all pet store.");
		return petStoreService.retrieveAllPetStores();
	}
	
	//Get Store by ID
	@GetMapping("/pet_store/{petStoreId}")
	public PetStoreData getPetStoreById(
			@PathVariable Long petStoreId) {
				log.info("Retrieving pet store by ID={}", petStoreId);
		return petStoreService.getPetStoreById(petStoreId);
	
}
	//Delete Store By ID
	@DeleteMapping("/pet_store/{petStoreId}/delete")
	public Map<String, String> deletePetStoreById(@PathVariable Long petStoreId) {
		log.info("Deleting store with ID={}", petStoreId);
				
		petStoreService.deletePetStoreById(petStoreId);
	
		Map<String, String> msg = new HashMap<>();
		msg.put("message", "Deleted Pet Store");
		return msg;
	}
	
	@DeleteMapping("/pet_store")
	public void deleteAllPetStores() {
		log.info("Attempting to delete all stores.");
		throw new UnsupportedOperationException("Deleting all pet stores isn't allowed.");	
	}
	
	//Create Pet Store Customer
		@PostMapping("/pet_store/{petStoreId}/customer")
		@ResponseStatus(code = HttpStatus.CREATED)
		public PetStoreCustomer savePetStoreCustomer(@PathVariable Long petStoreId,
				@RequestBody PetStoreCustomer petStoreCustomer) {
			log.info("Creating Customer with ID={} ", petStoreCustomer);
			return petStoreService.saveCustomer(petStoreId, petStoreCustomer);
		}
}


