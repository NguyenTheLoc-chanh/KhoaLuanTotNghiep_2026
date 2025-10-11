package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.controller.publicapi;

import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.dto.ProvinceDto;
import com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/address")
@RequiredArgsConstructor
@Tag(name = "Address", description = "API lấy dữ liệu Tỉnh/Thành phố, Quận/Huyện, Xã/Phường từ open-api.vn")
public class AddressAPIController {

    private final AddressService addressService;

    @Operation(
            summary = "Lấy danh sách tất cả tỉnh/thành",
            description = "API gọi open-api.vn để lấy toàn bộ danh sách tỉnh/thành phố.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách tỉnh/thành thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProvinceDto.class)
                            )
                    )
            }
    )
    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceDto>> getProvinces() {
        List<ProvinceDto> provinces = addressService.getProvinces();
        return ResponseEntity.ok(provinces);
    }

    @Operation(
            summary = "Lấy danh sách quận/huyện theo tỉnh/thành",
            description = "API gọi open-api.vn để lấy danh sách quận/huyện + xã/phường theo mã tỉnh.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách quận/huyện thành công",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProvinceDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Không tìm thấy tỉnh với mã code đã cung cấp")
            }
    )
    @GetMapping("/provinces/{provinceCode}/districts")
    public ResponseEntity<List<ProvinceDto>> getDistrictsByProvince(
            @PathVariable int provinceCode
    ) {
        List<ProvinceDto> districts = addressService.getDistrictsByProvince(provinceCode);
        return ResponseEntity.ok(districts);
    }
}
