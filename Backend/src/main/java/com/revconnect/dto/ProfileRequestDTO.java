package com.revconnect.dto;

import com.revconnect.entity.ProfilePrivacy;
import lombok.Data;

import java.util.List;

@Data
public class ProfileRequestDTO {

    private String fullName;
    private String bio;
    private String location;
    private String website;
    private String profilePictureUrl;
    private ProfilePrivacy privacy;

    private String category;
    private String contactEmail;
    private String contactPhone;
    private String businessAddress;
    private String businessHours;

    private String externalLinks;
}