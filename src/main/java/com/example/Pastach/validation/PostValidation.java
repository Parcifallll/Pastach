package com.example.Pastach.validation;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;

import java.util.Collection;
import java.util.Map;

public class PostValidation {
    public static void validatePostExists(Collection<Post> posts, int postId){
        if (posts.stream()
                .noneMatch(post -> post.getId() == postId)){
            throw new PostNotFoundException("Post with id " + postId + " is not found");
        }
    }
}
