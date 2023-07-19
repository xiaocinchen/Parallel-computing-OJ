package com.offer.oj.domain.dto;

import com.offer.oj.domain.enums.CodeTypeEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class SubmitCodeDTO implements Serializable {

    @NotNull(message = "QuestionId cannot be null.")
    private Integer questionId;

    @NotNull(message = "Code content cannot be empty.")
    private String content;

    @NotNull(message = "Code type cannot be empty.")
    private CodeTypeEnum type;

    @Null(message = "Why you submit author?")
    private Integer authorId;

    @Null(message = "Why you submit fileName?")
    private String fileName;

    @Null
    private Boolean isResult;

    @Serial
    private static final long serialVersionUID = 1L;

    public void clearContent() {
        this.setContent("");
    }

}
