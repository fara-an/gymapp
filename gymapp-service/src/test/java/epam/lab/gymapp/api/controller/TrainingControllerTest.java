package epam.lab.gymapp.api.controller;

import epam.lab.gymapp.dto.request.training.TrainingAddDto;
import epam.lab.gymapp.service.interfaces.TrainingService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@WebMvcTest(value = TrainingController.class)
@Import(NoSecurityConfig.class)
class TrainingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TrainingService trainingService;

    @Test
    void addTraining_ShouldReturnOk() throws Exception {
        TrainingAddDto dto = new TrainingAddDto();
        Mockito.when(trainingService.addTraining(any(TrainingAddDto.class))) .thenReturn((ResponseEntity)ResponseEntity.ok("training added"));

        mockMvc.perform(post("/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trainerUsername\":\"trainer1\",\"traineeUsername\":\"trainee1\",\"startTime\":\"2025-09-02T15:00:00\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("training added"));
    }

    @Test
    void deleteTraining_ShouldReturnOk() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2025, 9, 2, 15, 0);

        mockMvc.perform(delete("/trainings")
                        .param("trainerUsername", "trainer1")
                        .param("traineeUsername", "trainee1")
                        .param("startTime", startTime.toString()))
                .andExpect(status().isOk());

        Mockito.verify(trainingService)
               .deleteTraining(eq("trainer1"), eq("trainee1"), eq(startTime));
    }

    @Test
    void findTraining_ShouldReturnOk() throws Exception {
        LocalDateTime startTime = LocalDateTime.of(2025, 9, 2, 15, 0);

        mockMvc.perform(get("/trainings")
                        .param("trainerUsername", "trainer1")
                        .param("traineeUsername", "trainee1")
                        .param("startTime", startTime.toString()))
                .andExpect(status().isOk());

        Mockito.verify(trainingService)
               .findTraining(eq("trainer1"), eq("trainee1"), eq(startTime));
    }
}
