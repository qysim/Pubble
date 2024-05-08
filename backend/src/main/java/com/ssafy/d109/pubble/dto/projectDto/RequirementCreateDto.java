package com.ssafy.d109.pubble.dto.projectDto;

import com.ssafy.d109.pubble.entity.Project;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class RequirementCreateDto {

//    private Integer orderIndex;
    private String code;
    private String requirementName;
    private String detail;
    private Integer managerId; // 일단은 userid로 받는중
    private Integer authorId; // 접속자로 그대로 만들어버릴지? userId로 받을지? employeeId로 받을지?
    private String targetUser;
//    private String latest_version;
//    private LocalDateTime createAt;
//    private LocalDateTime updatedAt;
//    private Integer projectId;
}