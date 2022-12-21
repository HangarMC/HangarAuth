package io.papermc.hangarauth.controller.model;

import java.util.UUID;

public record InviteHookData(String state, Traits traits, UUID id, String url) {}
