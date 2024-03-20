package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.repository.CandidateRepository;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Collection;
import java.util.Optional;

@ThreadSafe
@Service
public class SimpleCandidateService implements CandidateService {

    private final CandidateRepository candidateRepository;
    private final FileService fileService;

    public SimpleCandidateService(CandidateRepository sql2oVacancyRepository, FileService fileService) {
        this.candidateRepository = sql2oVacancyRepository;
        this.fileService = fileService;
    }

    private void saveNewFile(Candidate candidate, FileDto image) {
        var file = fileService.save(image);
        candidate.setFileId(file.getId());
    }

    @Override
    public Candidate save(Candidate candidate, FileDto image) {
        saveNewFile(candidate, image);
        return candidateRepository.save(candidate);
    }

    @Override
    public boolean deleteById(int id) {
        var fileOptional = findById(id);
        if (fileOptional.isPresent()) {
            boolean deletedCandidate = candidateRepository.deleteById(id);
            if (deletedCandidate) {
                fileService.deleteById(fileOptional.get().getFileId());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(Candidate candidate, FileDto image) {
        var isNewFileEmpty = image.getContent().length == 0;
        if (isNewFileEmpty) {
            return candidateRepository.update(candidate);
        }
        var oldField = candidate.getFileId();
        saveNewFile(candidate, image);
        var isUpdated = candidateRepository.update(candidate);
        fileService.deleteById(oldField);
        return isUpdated;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return candidateRepository.findById(id);
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidateRepository.findAll();
    }
}
