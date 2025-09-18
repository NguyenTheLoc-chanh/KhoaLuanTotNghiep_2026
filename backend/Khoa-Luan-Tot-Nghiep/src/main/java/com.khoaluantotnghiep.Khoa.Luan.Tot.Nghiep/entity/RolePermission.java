package com.khoaluantotnghiep.Khoa.Luan.Tot.Nghiep.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tblRolePermission")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // surrogate key

    @ManyToOne
    @JoinColumn(name = "sRoleId", nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "sPermissionId", nullable = false)
    private Permission permission;
}
