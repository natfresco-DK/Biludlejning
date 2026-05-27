package ek.dk.biludlejning.integrationsTest;

import ek.dk.biludlejning.model.Car;
import ek.dk.biludlejning.model.DamageReport;
import ek.dk.biludlejning.model.RentalAgreement;
import ek.dk.biludlejning.model.User;
import ek.dk.biludlejning.repository.ICarRepository;
import ek.dk.biludlejning.repository.IDamageReportRepository;
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
class ExceptionFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ICarRepository carRepository;

    @Autowired
    private IRentalAgreementRepository rentalAgreementRepository;

    @Autowired
    private IDamageReportRepository damageReportRepository;

    @Autowired
    private IUserRepository userRepository;

    private User damageUser;
    private RentalAgreement rentedAgreement;
    private Car rentedCar;

    @BeforeEach
    void setUp() throws Exception {
        // Fetch an existing user with SKADE/UDBEDRING role
        damageUser = userRepository.findByXY("username", "thansen")
                .orElseThrow(() -> new IllegalStateException("SKADE/UDBEDRING user 'thansen' not found"));

        // Fetch a rental agreement on car_id=3, which is EF11223 with status RENTED
        rentedAgreement = rentalAgreementRepository.findByXY("agreement_id", 1)
                .orElseThrow(() -> new IllegalStateException("rental agreement id=1 not found"));

        rentedCar = carRepository.findByXY("car_id", rentedAgreement.getCar())
                .orElseThrow(() -> new IllegalStateException("rented car not found"));

        if (!"RENTED".equalsIgnoreCase(rentedCar.getStatus())) {
            throw new IllegalStateException("Expected car to have status RENTED, but was: " + rentedCar.getStatus());
        }
    }

    @Test
    void exceptionFlow_attemptDamageReportOnActiveRental_rejected() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", damageUser);

        int beforeCount = damageReportRepository.getAllDamageReports().size();

        // Step 1: rental agreement exists and rental is still active
        mockMvc.perform(get("/damage-report-create").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("damage_report_create"))
                .andExpect(model().attributeExists("damageReport"))
                .andExpect(model().attributeExists("rentalAgreements"));

        // Step 2 user attempts to create damage report, system rejects, error shown
        mockMvc.perform(post("/damage-report-create")
                        .session(session)
                        .param("rentalAgreementId", String.valueOf(rentedAgreement.getAgreementId()))
                        .param("returnDate", LocalDate.now().toString())
                        .param("odometer", "50000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/damage-report-create"))
                .andExpect(flash().attribute("errorMessage", "Bilen skal have status 'RETURNED'"));

        // Step 3: no damage report is saved
        List<DamageReport> reportsAfter = damageReportRepository.getAllDamageReports();
        assertThat(reportsAfter).hasSize(beforeCount);
    }
}