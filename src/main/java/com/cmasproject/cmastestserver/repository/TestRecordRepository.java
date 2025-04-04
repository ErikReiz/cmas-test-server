package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestRecordRepository extends JpaRepository<TestRecord, UUID> {
    Optional<TestRecord> findTestRecordById(UUID id);

    List<TestRecord> findTestRecordsByPatient(Patient patient);
}
