package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;
}
