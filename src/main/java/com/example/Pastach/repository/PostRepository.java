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


    @Query("SELECT p FROM Post p WHERE p.id IN :ids ORDER BY FIELD(p.id, :ids)")
    List<Post> findAllByIdInOrderByField(@Param("ids") List<Long> ids);

    List<Post> findTopByOrderByCreatedAtDesc(int limit);  // for fallback: @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC LIMIT :limit")
}
