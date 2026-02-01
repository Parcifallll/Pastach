package com.example.Pastach.repository;

import com.example.Pastach.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByAuthorId(Long authorId, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    // get posts by ids (from rec system), remaining order! Order is set in Python microservice (initially by similarity score)
    @Query(value = """
    SELECT p.*
    FROM unnest(CAST(:ids AS bigint[])) WITH ORDINALITY AS input(id, ord) --create temp table id|ord, ids[] = Long[]
    INNER JOIN posts p ON p.id = input.id
    ORDER BY input.ord
    """, nativeQuery = true)
    List<Post> findAllByIdInOrderByField(@Param("ids") Long[] ids);

    Page<Post> findTopNByOrderByCreatedAtDesc(Pageable pageable);
}
