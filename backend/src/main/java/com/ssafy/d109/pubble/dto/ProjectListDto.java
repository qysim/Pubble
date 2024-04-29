package com.ssafy.d109.pubble.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProjectListDto {

    private String prdId;
    private String projectTitle;
    private List<String> people;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private String status;
    private float progressRatio;
}
