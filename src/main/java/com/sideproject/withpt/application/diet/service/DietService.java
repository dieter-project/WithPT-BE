package com.sideproject.withpt.application.diet.service;

import com.sideproject.withpt.application.diet.repository.DietRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DietService {

    private final DietRepository dietRepository;

}
