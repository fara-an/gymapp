package epam.lab.gymapp.cucumber.component;

import epam.lab.gymapp.api.controller.TraineeController;
import epam.lab.gymapp.api.controller.TrainerController;
import epam.lab.gymapp.api.controller.TrainingController;
import epam.lab.gymapp.api.controller.UserController;
import epam.lab.gymapp.configuration.MetricsStubConfig;
import epam.lab.gymapp.configuration.NoSecurityConfig;
import epam.lab.gymapp.configuration.NoServiceConfig;
import epam.lab.gymapp.configuration.TrainerWorkloadClientServiceStubConfig;
import epam.lab.gymapp.controller.advice.GlobalExceptionHandler;
import epam.lab.gymapp.dao.interfaces.TrainingDao;
import epam.lab.gymapp.filter.perrequest.JwtAuthenticationFilter;
import epam.lab.gymapp.jwt.JwtService;
import epam.lab.gymapp.service.implementation.TokenBlacklistService;
import epam.lab.gymapp.service.interfaces.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@ActiveProfiles("test")
@WebMvcTest(value = {TraineeController.class, TrainerController.class, TrainingController.class, UserController.class}, excludeFilters =@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE ,classes = JwtAuthenticationFilter.class))
@Import({GlobalExceptionHandler.class,NoSecurityConfig.class, NoServiceConfig.class, TrainerWorkloadClientServiceStubConfig.class, MetricsStubConfig.class})
public class CucumberSpringConfiguration {
    @MockitoBean
    TraineeService traineeService;

    @MockitoBean
    TrainerService trainerService;

    @MockitoBean
    TrainingTypeService trainingTypeService;

    @MockitoBean
    TrainingService trainingService;

    @MockitoBean
    private TrainingDao trainingDao;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

}
