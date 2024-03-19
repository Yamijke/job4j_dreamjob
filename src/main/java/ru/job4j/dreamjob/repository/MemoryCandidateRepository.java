package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import javax.annotation.concurrent.ThreadSafe;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {
    private AtomicInteger nextId = new AtomicInteger(0);
    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Little Boy", "Youngling", LocalDateTime.now(), 1));
        save(new Candidate(0, "Anakin Skywalker", "Padavan", LocalDateTime.now(), 1));
        save(new Candidate(0, "Obi-Wan Kenobi", "Jedi-Knight", LocalDateTime.now(), 2));
        save(new Candidate(0, "Qui-Gon Jinn", "Jedi-Master", LocalDateTime.now(), 3));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(oldCandidate.getId(),
                        candidate.getName(), candidate.getLevel(),
                        candidate.getCreationDate(), candidate.getCityId())) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
