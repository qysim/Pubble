package com.ssafy.d109.pubble.service;

import com.ssafy.d109.pubble.dto.projectDto.*;
import com.ssafy.d109.pubble.entity.Project;
import com.ssafy.d109.pubble.entity.Requirement;
import com.ssafy.d109.pubble.entity.User;
import com.ssafy.d109.pubble.exception.Requirement.RequirementNotFoundException;
import com.ssafy.d109.pubble.exception.User.UserNotFoundException;
import com.ssafy.d109.pubble.repository.ProjectRepository;
import com.ssafy.d109.pubble.repository.RequirementRepository;
import com.ssafy.d109.pubble.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public float getApprovalRatio(Integer projectId) {

        float approvalRatio;

        int approved = 0;
        int unapproved = 0;

        List<Requirement> requirements = requirementRepository.findLatestRequirementsForProjectByProjectId(projectId);

        for(Requirement requirement : requirements) {
            if (requirement.getApproval().equals("a")) {
                approved += 1;
            } else {
                unapproved += 1;
            }
        }
        if (unapproved == 0) {
            if (approved == 0) {
                approvalRatio = 0;
            } else {
                approvalRatio = 1;
            }
        } else {
            approvalRatio = (float) approved / unapproved;
        }
        return approvalRatio;
    }

    public ProgressRatio getProgressRatio(Integer projectId) {
        float lockRatio;
        float approvalRatio;
        float changeRatio;

        int locked = 0;
        int unlocked = 0;

        int approved = 0;
        int unapproved = 0;

        int unchanged = 0;
        int changed = 0;

        List<Requirement> requirements = requirementRepository.findLatestRequirementsForProjectByProjectId(projectId);

        for(Requirement requirement : requirements) {
            if (requirement.getIsLock().equals("l")) {
                locked += 1;
            } else {
                unlocked += 1;
            }

            if (requirement.getApproval().equals("a")) {
                approved += 1;
            } else {
                unapproved += 1;
            }

            if (requirement.getVersion().equals("V.1.0.")) {
                unchanged += 1;
            } else {
                changed += 1;
            }
        }

        if (unlocked == 0) {
            if (locked == 0) {
                lockRatio = 0;
            } else {
                lockRatio = 1;
            }
        } else {
            lockRatio = (float) locked / unlocked;
        }

        if (unapproved == 0) {
            if (approved == 0) {
                approvalRatio = 0;
            } else {
                approvalRatio = 1;
            }
        } else {
            approvalRatio = (float) approved / unapproved;
        }

        if (unchanged == 0) {
            if (changed == 0) {
                changeRatio = 0;
            } else {
                changeRatio = 1;
            }
        } else {
            changeRatio = (float) changed / unchanged;
        }

        return ProgressRatio.builder().lockRatio(lockRatio).approvalRatio(approvalRatio).changeRatio(changeRatio).build();
    }

    // requirement 생성
    public void createRequirement(Integer projectId, RequirementCreateDto requirementCreateDto) {
        Optional<Project> optionalProject = projectRepository.findByProjectId(projectId);
        Optional<User> optionalAuthor = userRepository.findByUserId(requirementCreateDto.getAuthorId());
        Optional<User> optionalManager = userRepository.findByUserId(requirementCreateDto.getManagerId());

        if (optionalProject.isPresent() && optionalAuthor.isPresent() && optionalManager.isPresent()) {
            Project project = optionalProject.get();
            User author = optionalAuthor.get();
            User manager = optionalManager.get();

            // orderIndex
            Integer orderIndex = requirementRepository.findMaxOrderIndexByProjectId(project.getProjectId());
            if (orderIndex == null) {
                orderIndex = 0; // 해당 프로젝트에 요구사항이 없다면 0으로 시작
            } else {
                orderIndex += 1;
            }

            Requirement requirement = Requirement.builder()
                    .isLock("u")
                    .approval("u")
                    .code(requirementCreateDto.getCode())
                    .requirementName(requirementCreateDto.getRequirementName())
                    .detail(requirementCreateDto.getDetail())
                    .manager(manager)
                    .author(author)
                    .targetUser(requirementCreateDto.getTargetUser())
                    .version("V.1.0.")
                    .orderIndex(orderIndex)
                    .project(project)
                    .build();

            requirementRepository.save(requirement);
        }
    }

    private DashboardUserInfo getDashboardUserInfo(User user) {
        return DashboardUserInfo.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .position(user.getPosition())
                .role(user.getRole())
                .isApprovable(user.getIsApprovable())
                .profileColor(user.getProfileColor())
                .build();
    }

    public RequirementSummaryDto getRequirement(Integer requirementId) {
        Requirement requirement = requirementRepository.findByRequirementId(requirementId).orElseThrow(RequirementNotFoundException::new);

        DashboardUserInfo managerInfo = getDashboardUserInfo(requirement.getManager());
        DashboardUserInfo authorInfo = getDashboardUserInfo(requirement.getAuthor());

        RequirementSummaryDto requirementSummaryDto = RequirementSummaryDto.builder()
                .requirementId(requirement.getRequirementId())
                .orderIndex(requirement.getOrderIndex())
                .version(requirement.getVersion())
                .isLock(requirement.getIsLock())
                .approval(requirement.getApproval())
                .approvalComment(requirement.getApprovalComment())
                .code(requirement.getCode())
                .requirementName(requirement.getRequirementName())
                .detail(requirement.getDetail())
                .manager(managerInfo)
                .targetUser(requirement.getTargetUser())
                .createdAt(requirement.getCreatedAt())
                .author(authorInfo)
                .build();

        return requirementSummaryDto;
    }

    @Transactional
    public void updateRequirement(Integer requirementId, RequirementUpdateDto udto) {
        Requirement requirement = requirementRepository.findByRequirementId(requirementId).orElseThrow(RequirementNotFoundException::new);

        if (udto.getIsApproval() != null) {
            requirement.setApproval(udto.getIsApproval());
        }
        if (udto.getCode() != null) {
            requirement.setCode(udto.getCode());
        }
        if (udto.getRequirementName() != null) {
            requirement.setRequirementName(udto.getRequirementName());
        }
        if (udto.getDetail() != null) {
            requirement.setDetail(udto.getDetail());
        }
        if (udto.getManagerId() != null) {
            User manager = userRepository.findByUserId(udto.getManagerId()).orElseThrow(UserNotFoundException::new);
            requirement.setManager(manager);
        }
        if (udto.getAuthorId() != null) {
            User author = userRepository.findByUserId(udto.getAuthorId()).orElseThrow(UserNotFoundException::new);
            requirement.setAuthor(author);
        }
        if (udto.getVersion() != null) {
            requirement.setVersion(udto.getVersion());
        }

        requirementRepository.save(requirement);
    }

    // update version by command(h:hold / r:restore)
    @Transactional
    public void updateVersion(Integer requirementId, String command) {
        Optional<Requirement> optionalRequirement = requirementRepository.findByRequirementId(requirementId);

        if (optionalRequirement.isPresent()) {
            Requirement requirement = optionalRequirement.get();

            int holdCommand = 0;
            int restoreCommand = 0;
            String approval = "h";

            if ("h".equals(command)) {
                holdCommand += 1;
            } else if ("r".equals(command)) {
                restoreCommand += 1;
            }

            String[] parts = requirement.getVersion().split("\\.");
            Integer front = Integer.parseInt(parts[1]) + holdCommand;
            Integer back = Integer.parseInt(parts[2]) + restoreCommand;

            String newVersion = String.format("V.%d.%d", front, back);

            // tobuild로 id, version 제외, 복제 저장
            requirementRepository.save(requirement.toBuilder().requirementId(null).version(newVersion).approval(approval).build());

            // orderIndex는 차후 논의
        }
    }

    @Transactional
    public void updateRequirementLock(Integer requirementId, String lock) {
        Optional<Requirement> optionalRequirement = requirementRepository.findByRequirementId(requirementId);

        if (optionalRequirement.isPresent()) {
            Requirement requirement = optionalRequirement.get();
            if ("u".equals(lock)){
                requirement.setIsLock("u");
            } else if ("l".equals(lock)) {
                requirement.setIsLock("l");
            }

            requirementRepository.save(requirement);
        }
    }

    @Transactional
    public void updateRequirementApproval(Integer requirementId, ApprovalDto approvalDto) {
        Optional<Requirement> optionalRequirement = requirementRepository.findByRequirementId(requirementId);

        if (optionalRequirement.isPresent()) {
            Requirement requirement = optionalRequirement.get();
            switch (approvalDto.getApproval()) {
                case "u" -> requirement.setApproval("u");
                case "h" -> requirement.setApproval("h");
                case "a" -> requirement.setApproval("a");
            }

            requirement.setApprovalComment(approvalDto.getApprovalComment());
            requirementRepository.save(requirement);
        }
    }

    public List<RequirementSummaryDto> getRequirementsByCode(Integer projectId, String code) {
        List<Requirement> requirements = requirementRepository.findByProject_ProjectIdAndCode(projectId, code);
        List<RequirementSummaryDto> requirementSummaryDtos = new ArrayList<>();

        for(Requirement requirement : requirements) {
            RequirementSummaryDto requirementSummaryDto = getRequirement(requirement.getRequirementId());
            requirementSummaryDtos.add(requirementSummaryDto);
        }

        return requirementSummaryDtos;
    }
}