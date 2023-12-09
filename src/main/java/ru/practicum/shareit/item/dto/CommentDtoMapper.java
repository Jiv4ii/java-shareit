package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

public class CommentDtoMapper {

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto()
                .setId(comment.getId())
                .setText(comment.getText())
                .setAuthorName(comment.getAuthor().getName())
                .setCreated(comment.getCreated());
    }

    public static Comment toComment(CommentDto commentDto) {
        return new Comment()
                .setText(commentDto.getText());


    }
}
