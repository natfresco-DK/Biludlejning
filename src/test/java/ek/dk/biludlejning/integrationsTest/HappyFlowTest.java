package ek.dk.biludlejning.integrationsTest;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.Customer;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.ICustomerRepository;
import ek.dk.biludlejning.repository.IRentalAgreementRepository;
import ek.dk.biludlejning.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class HappyFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ICarRepository carRepository;

    @Autowired
    private ICustomerRepository customerRepository;

    @Autowired
    private IRentalAgreementRepository rentalAgreementRepository;

    @Autowired
    private IUserRepository userRepository;

    private User testUser;
    private Customer testCustomer;
    private Car testCar;

    @BeforeEach
    void setUp() {
        // Fetch an existing user with DATAREGISTRERING role
        testUser = userRepository.findByXY("role", "DATAREGISTRERING")
                .orElseThrow(() -> new IllegalStateException("No user with role DATAREGISTRERING found"));

        // Fetch an existing active customer
        testCustomer = customerRepository.findAllActive().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active customer found"));

        // Fetch an existing available car
        testCar = carRepository.findAvailableCars().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No available car found"));
    }

    @Test
    void happyFlow_createRentalAgreement_andMarkCarAsRented() throws Exception {
        // Arrange
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", testUser);

        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);

        int beforeCount = rentalAgreementRepository.getAllRentalAgreements().size();

        // Step 1: User opens create rental agreement form
        mockMvc.perform(get("/lease-create").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("lease_create"))
                .andExpect(model().attributeExists("rentalAgreement"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attributeExists("cars"));

        // Step 2: Select car/customer, enter valid dates/payment then submit
        mockMvc.perform(post("/lease-create")
                        .session(session)
                        .param("customer", String.valueOf(testCustomer.getCustomerId()))
                        .param("car", String.valueOf(testCar.getCarId()))
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .param("downpayment", "5000")
                        .param("monthly_payment", "1500")
                        .param("maxKm", "20000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/lease-agreements"));

        // Step 3: Saves Rental Agreement
        List<RentalAgreement> agreements = rentalAgreementRepository.getAllRentalAgreements();
        assertThat(agreements.size()).isEqualTo(beforeCount + 1);

        RentalAgreement savedAgreement = agreements.stream()
                .filter(agreement ->
                        agreement.getCustomer() == testCustomer.getCustomerId() &&
                                agreement.getCar() == testCar.getCarId() &&
                                startDate.equals(agreement.getStartDate()) &&
                                endDate.equals(agreement.getEndDate()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected rental agreement was not saved"));

        assertThat(savedAgreement.getDownpayment()).isEqualTo(5000.0);
        assertThat(savedAgreement.getMonthly_payment()).isEqualTo(1500.0);
        assertThat(savedAgreement.getMaxKm()).isEqualTo(20000);
        assertThat(savedAgreement.getCreatedBy()).isEqualTo(testUser.getId());
        assertThat(savedAgreement.getActive()).isTrue();

        // Step 4: Car status changes to rented
        Car updatedCar = carRepository.findByXY("car_id", testCar.getCarId())
                .orElseThrow(() -> new AssertionError("Updated car was not found"));

        assertThat(updatedCar.getStatus()).isEqualToIgnoringCase("RENTED");
    }
}