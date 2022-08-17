package io.papermc.hangarauth.service.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "auth.storage.type", havingValue = "object")
public class S3FileService implements FileService {
}
