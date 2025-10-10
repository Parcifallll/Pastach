package com.example.Pastach.validation;

import com.example.Pastach.exception.PostNotFoundException;
import com.example.Pastach.model.Post;

import java.util.Map;

public class PostValidation {
    public static void validatePostExists(Map<Integer, Post> posts, int postId){
        if (!posts.containsKey(postId)){
            throw new PostNotFoundException("Post with id " + postId + " is not found");
        }
    }
}
