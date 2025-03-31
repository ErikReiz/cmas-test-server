package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.Patient;
import com.cmasproject.cmastestserver.entities.TestRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TestRecordRepository extends JpaRepository<TestRecord, UUID> {
    TestRecord getTestRecordById(UUID id);

    List<TestRecord> getTestRecordsByPatient(Patient patient);
}
