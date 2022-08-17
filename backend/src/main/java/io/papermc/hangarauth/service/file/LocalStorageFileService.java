package io.papermc.hangarauth.service.file;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "auth.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalStorageFileService implements FileService {
}
