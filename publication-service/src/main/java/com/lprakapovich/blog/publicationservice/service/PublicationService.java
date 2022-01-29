package com.lprakapovich.blog.publicationservice.service;

import com.lprakapovich.blog.publicationservice.repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PublicationService {

    private final PublicationRepository publicationRepository;
}
