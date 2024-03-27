package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private FileService fileService;
    private FileController fileController;
    private FileDto fileDto;

    @BeforeEach
    public void initServices() {
        fileDto = mock(FileDto.class);
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
    }

    @Test
    void whenGetFileByIdThenStatusOk() {
        int id = 1;
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.of(fileDto));
        var responseEntity = fileController.getById(id);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenGetFileByIdThenStatusNotFound() {
        int id = 1;
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.empty());
        var responseEntity = fileController.getById(id);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}