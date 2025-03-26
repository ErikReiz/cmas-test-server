package com.cmasproject.cmastestserver.repository;

import com.cmasproject.cmastestserver.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepository extends JpaRepository<File, UUID> {
}
