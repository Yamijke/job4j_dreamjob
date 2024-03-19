package ru.job4j.dreamjob.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private String level;
    private LocalDateTime creationDate;
    private int cityId;

    public Candidate() {
    }

    public Candidate(int id, String name, String level, LocalDateTime creationDate, int cityId) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.creationDate = creationDate;
        this.cityId = cityId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return id == candidate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
