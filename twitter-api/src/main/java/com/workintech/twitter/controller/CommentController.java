package com.workintech.twitter.controller;

import com.workintech.twitter.dto.CommentRequest;
import com.workintech.twitter.dto.CommentResponse;
import com.workintech.twitter.dto.CommentUpdateRequest;
import com.workintech.twitter.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping({"", "/"})
    public ResponseEntity<CommentResponse> create(@Valid @RequestBody CommentRequest request, Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(request, auth.getName()));
    }

    @PutMapping("/{id}")
    public CommentResponse update(@PathVariable Long id, @Valid @RequestBody CommentUpdateRequest request, Authentication auth) {
        return commentService.update(id, request, auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Authentication auth) {
        commentService.delete(id, auth.getName());
        return ResponseEntity.noContent().build();
    }
}
