package com.braininventory.leadsphere.user_service.service;

import com.braininventory.leadsphere.user_service.controller.InviteController;
import com.braininventory.leadsphere.user_service.dto.InviteResponseDto;
import com.braininventory.leadsphere.user_service.dto.UserRequestDto;

public interface InviteService {

    //void createInvite(String email);

    void createInvite(InviteController.InviteRequest request);

}
