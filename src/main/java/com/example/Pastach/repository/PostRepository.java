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

    @Query(value = """
    SELECT p.*
    FROM posts p
    WHERE p.id = ANY(:ids)
    ORDER BY array_position(:ids, p.id)
    """, nativeQuery = true)
    List<Post> findAllByIdInOrderByField(@Param("ids") Long[] ids);

    Page<Post> findTopNByOrderByCreatedAtDesc(Pageable pageable);
}
