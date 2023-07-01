package pet.store.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreCustomer;
import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	
	//Create Store
	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
		copyPetStoreFields(petStore, petStoreData);
		PetStore savedPetStore = petStoreDao.save(petStore);
		return new PetStoreData(savedPetStore);
	}
	
	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setPetStoreName(petStoreData.getPetStoreName());
		petStore.setPetStoreAddress(petStoreData.getPetStoreAddress());
		petStore.setPetStoreCity(petStoreData.getPetStoreCity());
		petStore.setPetStoreState(petStoreData.getPetStoreState());
		petStore.setPetStoreZip(petStoreData.getPetStoreZip());
		petStore.setPetStorePhone(petStoreData.getPetStorePhone());
	}
	
//Get
	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		
		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
			return petStore;
	}
	private PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID="
		+ petStoreId + " does not exist."));
	}
	
	//Create Employee
	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		copyEmployeeFields(petStore, employee, petStoreEmployee);
		employee.setPetStore(petStore);
		
		Employee savedEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(savedEmployee);
	}

	private void copyEmployeeFields(PetStore petStore, Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		employee.setEmployeeFirstName(petStoreEmployee.getEmployeeFirstName());
		employee.setEmployeeLastName(petStoreEmployee.getEmployeeLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setEmployeeJobTitle(petStoreEmployee.getEmployeeJobTitle());
		petStore.getEmployees().add(employee);
	}
			
	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		if(Objects.isNull(employeeId)) {
			return new Employee();
		} else {
			return findEmployeeById(petStoreId, employeeId);
		}
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId)
				.orElseThrow(() -> new NoSuchElementException(
						"Employee with ID= " + employeeId + " was not found."));
		
		if(employee.getPetStore().getPetStoreId() == petStoreId) {
			return employee;
		} else {
			throw new IllegalArgumentException("Please enter correct data in the appropriate format");
		}
	}
	
	//Create Customer
	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer (Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Customer customer = findOrCreateCustomer(petStore, petStoreCustomer.getCustomerId());
		copyCustomerFields(petStore, customer, petStoreCustomer);
		
		customer.getPetStores().add(petStore);
		Customer savedCustomer = customerDao.save(customer);
		savedCustomer.getPetStores().add(petStore);
		return new PetStoreCustomer(savedCustomer);
	}

	private void copyCustomerFields(PetStore petStore, Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setCustomerFirstName(petStoreCustomer.getCustomerFirstName());
		customer.setCustomerLastName(petStoreCustomer.getCustomerLastName());
		customer.setCustomerEmail(petStoreCustomer.getCustomerEmail());
		petStore.getCustomers().add(customer);
	}

	private Customer findOrCreateCustomer(PetStore petStore, Long customerId) {
		Customer customer;
		if(customerId != null) {
			customer = customerDao.findById(customerId)
					.orElseThrow(() -> new NoSuchElementException(
							"Customer with ID= " + customerId + " was not found."));
					for(PetStore pet : customer.getPetStores()) {
				if(pet.getPetStoreId() == petStore.getPetStoreId()) {
					customer.getPetStores().add(pet);
				}
			}
		} else {
			customer = new Customer();
			customer.setPetStores(listOfPetStoresById(petStore.getPetStoreId()));
		}
		return customer;
	}

	//Get Pet Stores
	private Set<PetStore> listOfPetStoresById(Long petStoreId) {
		List<PetStore> petStores = petStoreDao.findAll();
		Set<PetStore> setPetStores = new HashSet<>();
		
		for(PetStore tempStore : petStores) {
			if(tempStore.getPetStoreId() == petStoreId) {
				setPetStores.add(tempStore);
			}
		}
		return setPetStores;
	}
	
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStoreData> result = new LinkedList<>();
		List<PetStore> petStores = petStoreDao.findAll();
		
		for(PetStore petStore : petStores) {
			PetStoreData psd = new PetStoreData(petStore);
			psd.getCustomers().clear();
			psd.getEmployees().clear();
			
			result.add(psd);
		}
		return result;
	}
	//Delete Pet Store by ID
	public void deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);

}
	//Get Pet Store by ID
	public PetStoreData getPetStoreById(Long petStoreId) {
		PetStore petStore = petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet Store with ID=" + petStoreId + " was not found."));
		return new PetStoreData(petStore);
}
}



	

