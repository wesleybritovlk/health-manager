package com.github.wesleybritovlk.healthmanager.handler;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

public interface GlobalHandlerService {
    GlobalHandler create(GlobalHandlerDTO dto);
}

@Service
@Transactional
@RequiredArgsConstructor
class GlobalHandlerServiceImpl implements GlobalHandlerService {
    private final GlobalHandlerRepository repository;

    @Override
    public GlobalHandler create(GlobalHandlerDTO dto) {
        return repository.save(GlobalHandler.builder().createdAt(dto.timestamp()).status(dto.status())
                .error(String.valueOf(dto.error())).message(String.valueOf(dto.message()))
                .requestPath(dto.request_path()).build());
    }
}
