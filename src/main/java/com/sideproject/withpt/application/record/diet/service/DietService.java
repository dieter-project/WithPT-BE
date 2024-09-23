package com.sideproject.withpt.application.record.diet.service;

import com.sideproject.withpt.application.image.ImageUploader;
import com.sideproject.withpt.application.image.repository.ImageRepository;
import com.sideproject.withpt.application.member.repository.MemberRepository;
import com.sideproject.withpt.application.record.diet.controller.request.DietFoodRequest;
import com.sideproject.withpt.application.record.diet.controller.request.EditDietInfoRequest;
import com.sideproject.withpt.application.record.diet.controller.request.SaveDietRequest;
import com.sideproject.withpt.application.record.diet.exception.DietException;
import com.sideproject.withpt.application.record.diet.repository.DietFoodRepository;
import com.sideproject.withpt.application.record.diet.repository.DietInfoRepository;
import com.sideproject.withpt.application.record.diet.repository.DietQueryRepository;
import com.sideproject.withpt.application.record.diet.repository.DietRepository;
import com.sideproject.withpt.application.record.diet.repository.response.DietInfoDto;
import com.sideproject.withpt.application.record.diet.service.response.DailyDietResponse;
import com.sideproject.withpt.application.record.diet.service.response.DietInfoResponse;
import com.sideproject.withpt.application.type.Usages;
import com.sideproject.withpt.common.exception.GlobalException;
import com.sideproject.withpt.domain.member.Member;
import com.sideproject.withpt.domain.record.Image;
import com.sideproject.withpt.domain.record.diet.DietFood;
import com.sideproject.withpt.domain.record.diet.DietInfo;
import com.sideproject.withpt.domain.record.diet.Diets;
import com.sideproject.withpt.domain.record.diet.utils.DietNutritionalStatistics;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;
    private final DietInfoRepository dietInfoRepository;
    private final DietFoodRepository dietFoodRepository;
    private final DietQueryRepository dietQueryRepository;
    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;
    private final ImageRepository imageRepository;

    @Transactional
    public void saveOrUpdateDiet(Long memberId, SaveDietRequest request, List<MultipartFile> files) {
        Member member = validateMemberId(memberId);
        dietQueryRepository.findByMemberAndUploadDate(member, request.getUploadDate())
            .ifPresentOrElse(existingDiets -> {
                    handleDietUpdate(request, files, member, existingDiets);
                },
                () -> {
                    Diets newDiets = dietRepository.save(request.toEntity(member));
                    handleDietUpdate(request, files, member, newDiets);
                });
    }

    public DailyDietResponse findDietByMemberAndUploadDate(LocalDate uploadDate, Long memberId) {
        Member member = validateMemberId(memberId);

        return dietRepository.findByMemberAndUploadDate(member, uploadDate)
            .map(diets -> {
                    List<DietInfoDto> dietInfoDtos = dietQueryRepository.findAllDietInfoAndDietFoodByDiets(member, diets);
                    return DailyDietResponse.of(diets, dietInfoDtos);
                }
            )
            .orElse(null);
    }

    public DietInfoResponse findDietInfoById(Long memberId, Long dietInfoId) {
        Member member = validateMemberId(memberId);
        Optional<DietInfo> optionalDietInfo = dietInfoRepository.findById(dietInfoId);

        if (optionalDietInfo.isEmpty()) {
            return null;
        }

        DietInfo dietInfo = optionalDietInfo.get();
        List<DietFood> dietFoods = dietFoodRepository.findAllByDietInfoOrderById(dietInfo);

        String imageUsageIdentificationId = "DIET_" + dietInfo.getDiets().getId() + "/DIETINFO_" + dietInfo.getId();
        List<Image> images = imageRepository.findAllByMemberAndUsageIdentificationId(member, imageUsageIdentificationId);

        return DietInfoResponse.of(dietInfo, dietFoods, images);
    }

    @Transactional
    public void modifyDietInfo(Long memberId, Long dietId, Long dietInfoId, EditDietInfoRequest request, List<MultipartFile> files) {
        Member member = validateMemberId(memberId);
        Diets diets = dietRepository.findById(dietId)
            .orElseThrow(() -> DietException.DIET_NOT_EXIST);

        DietInfo dietInfo = dietInfoRepository.findById(dietInfoId)
            .map(dietInfo1 -> {
                dietInfo1.updateMealTime(request.getDietDateTime());
                dietInfo1.updateMealCategory(request.getMealCategory());
                return dietInfo1;
            })
            .orElseThrow(() -> DietException.DIET_FOOD_NOT_EXIST);

        removeDietFoods(request, diets, dietInfo);
        addDietFood(request, diets, dietInfo);
        imageDeletion(request);
        imageUpload(files, member, diets, dietInfo);
    }

    @Transactional
    public void deleteDietInfo(Long memberId, Long dietId, Long dietInfoId) {
        Member member = validateMemberId(memberId);
        Diets diets = dietRepository.findById(dietId)
            .orElseThrow(() -> DietException.DIET_NOT_EXIST);

        DietInfo dietInfo = dietInfoRepository.findById(dietInfoId)
            .orElseThrow(() -> DietException.DIET_FOOD_NOT_EXIST);

        diets.subtractTotalCalorie(dietInfo.getTotalCalorie());
        diets.subtractTotalCarbohydrate(dietInfo.getTotalCarbohydrate());
        diets.subtractTotalProtein(dietInfo.getTotalProtein());
        diets.subtractTotalFat(dietInfo.getTotalFat());

        imageUploader.deleteImageByIdentificationAndMember("DIET_" + diets.getId() + "/DIETINFO_" + dietInfo.getId(), member);
        dietInfoRepository.delete(dietInfo);
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> GlobalException.TEST_ERROR);
    }

    private void handleDietUpdate(SaveDietRequest request, List<MultipartFile> files, Member member, Diets diets) {
        DietNutritionalStatistics<DietFoodRequest> statistics = DietNutritionalStatistics.getStatisticsBy(request.getDietFoods());
        diets.addTotalNutritionalStatistics(statistics);
        addDietInfo(request, files, statistics, member, diets);
    }

    private void addDietInfo(SaveDietRequest request, List<MultipartFile> files, DietNutritionalStatistics<DietFoodRequest> statistics, Member member,
        Diets diets) {
        List<DietFood> dietFoods = request.getDietFoods().stream()
            .map(DietFoodRequest::toEntity)
            .collect(Collectors.toList());

        DietInfo saveDietInfo = dietInfoRepository.save(
            createDietInfo(request, diets, statistics, dietFoods)
        );

        if (files != null) {
            imageUploader.uploadAndSaveImages(files, Usages.MEAL,
                "DIET_" + diets.getId() + "/DIETINFO_" + saveDietInfo.getId(), member);
        }
    }

    private DietInfo createDietInfo(SaveDietRequest request, Diets diets, DietNutritionalStatistics<DietFoodRequest> statistics,
        List<DietFood> dietFoods) {
        return DietInfo.builder()
            .diets(diets)
            .mealCategory(request.getMealCategory())
            .mealTime(LocalDateTime.of(request.getUploadDate(), request.getMealTime()))
            .totalCalorie(statistics.getTotalCalories())
            .totalCarbohydrate(statistics.getTotalCarbohydrate())
            .totalProtein(statistics.getTotalProtein())
            .totalFat(statistics.getTotalFat())
            .dietFoods(dietFoods)
            .build();
    }

    private void removeDietFoods(EditDietInfoRequest request, Diets diets, DietInfo dietInfo) {
        List<Long> deletedFoodIds = request.getDeletedFoodIds();

        if (!deletedFoodIds.isEmpty()) {
            List<DietFood> deletedFoods = dietFoodRepository.findAllByIdInAndDietInfo(request.getDeletedFoodIds(), dietInfo);
            DietNutritionalStatistics<DietFood> statistics = DietNutritionalStatistics.getStatisticsBy(deletedFoods);

            diets.subtractTotalNutritionalStatistics(statistics);
            dietInfo.subtractTotalNutritionalStatistics(statistics);

            dietFoodRepository.deleteAllByIdInAndDietInfo(request.getDeletedFoodIds(), dietInfo);
        }
    }

    private void addDietFood(EditDietInfoRequest request, Diets diets, DietInfo dietInfo) {
        List<DietFoodRequest> dietFoods = request.getDietFoods();
        if (!dietFoods.isEmpty()) {
            DietNutritionalStatistics<DietFoodRequest> statistics = DietNutritionalStatistics.getStatisticsBy(request.getDietFoods());

            request.getDietFoods().forEach(foodRequest -> {
                DietFood dietFood = foodRequest.toEntity();
                dietInfo.addDietFood(dietFood);
            });

            diets.addTotalNutritionalStatistics(statistics);
            dietInfo.addTotalNutritionalStatistics(statistics);
        }
    }

    private void imageUpload(List<MultipartFile> files, Member member, Diets diets, DietInfo dietInfo) {
        if (files != null && !files.isEmpty()) {
            imageUploader.uploadAndSaveImages(files, Usages.MEAL,
                "DIET_" + diets.getId() + "/DIETINFO_" + dietInfo.getId(), member);
        }
    }

    private void imageDeletion(EditDietInfoRequest request) {
        if (!request.getDeletedImageIds().isEmpty()) {
            request.getDeletedImageIds().forEach(imageUploader::deleteImage);
        }
    }
}
