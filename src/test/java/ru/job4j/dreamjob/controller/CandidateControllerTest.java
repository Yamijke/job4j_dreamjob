package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.model.Vacancy;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.VacancyService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {
    private CandidateService candidateService;

    private CityService cityService;

    private CandidateController candidateController;

    private MultipartFile testFile;

    private FileService fileService;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        fileService = mock(FileService.class);
        candidateController = new CandidateController(candidateService, cityService, fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenGetAll() {
        var candidate1 = new Candidate(1, "Name1", "Level1", LocalDateTime.now(), 1, 1);
        var candidate2 = new Candidate(2, "Name2", "Level2", LocalDateTime.now(), 1, 1);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetPageWithCities() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCandidates = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCandidates).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostCandidateWithFileThenSameDataAndRedirectToCandidatesPage() throws Exception {
        var candidate1 = new Candidate(1, "Name1", "Level1", LocalDateTime.now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCaptor = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCaptor = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCaptor.capture(), fileDtoArgumentCaptor.capture())).thenReturn(candidate1);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate1, testFile, model);
        var actualCandidate = candidateArgumentCaptor.getValue();
        var actualFileDto = fileDtoArgumentCaptor.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate1);
        assertThat(fileDto).usingRecursiveComparison().isEqualTo(actualFileDto);
    }

    @Test
    public void whenSomeExceptionThrownThenGetErrorPageWithMessage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualExceptionMessage = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualExceptionMessage).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenFindCandidateById() {
        var candidate1 = new Candidate(1, "Name1", "Level1", LocalDateTime.now(), 1, 1);
        var expectedId = candidate1.getId();
        when(candidateService.findById(expectedId)).thenReturn(Optional.of(candidate1));
        var model = new ConcurrentModel();

        String viewName = candidateController.getById(model, expectedId);

        assertThat(viewName).isEqualTo("candidates/one");
        assertThat(model.containsAttribute("candidate")).isTrue();
        assertThat(model.getAttribute("candidate")).isEqualTo(candidate1);
    }

    @Test
    public void whenCantFindCandidateById() throws Exception {
        var notExistId = 10;
        when(candidateService.findById(notExistId)).thenReturn(Optional.empty());
        var model = new ConcurrentModel();

        String viewName = candidateController.getById(model, notExistId);
        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenDeleteById() {
        int nonExistId = 10;
        when(candidateService.deleteById(nonExistId)).thenReturn(false);
        var model = new ConcurrentModel();
        String viewName = candidateController.delete(model, nonExistId);
        assertThat(viewName).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("Кандидат с указанным идентификатором не найден");
    }

    @Test
    public void whenUpdateCandidateAndGetErrorPage() throws Exception {
        var candidate1 = new Candidate(1, "Name1", "Level1", LocalDateTime.now(), 1, 1);
        var expectedException = new RuntimeException("Exception message");
        when(candidateService.update(any(Candidate.class), any(FileDto.class))).thenThrow(expectedException);
        var model = new ConcurrentModel();
        var view = candidateController.update(candidate1, testFile, model);
        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo(expectedException.getMessage());
    }
}