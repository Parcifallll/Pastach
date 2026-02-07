package com.example.Pastach.repository;

import com.example.Pastach.dto.comment.CommentResponseDTO;
import com.example.Pastach.model.Comment;
import com.example.Pastach.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // field=Post (not column in DB)
    // for N+1 solution (use 1* LEFT JOIN instead of SELECT * (1)+ SELECT (N), N+1 problem)
    // @EntityGraph(attributePaths = {"post"})
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

}
