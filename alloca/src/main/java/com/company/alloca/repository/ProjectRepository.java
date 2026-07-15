package com.company.alloca.repository;

import com.company.alloca.entity.Project;
import com.company.alloca.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectCode(String projectCode);

    List<Project> findByStatus(ProjectStatus status);

    boolean existsByProjectCode(String projectCode);
}