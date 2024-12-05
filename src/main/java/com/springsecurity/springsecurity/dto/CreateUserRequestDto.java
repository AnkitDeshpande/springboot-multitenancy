package com.springsecurity.springsecurity.dto;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequestDto {

    private Long id;
    private String email;
    private String username;
    private String password;
    private String schemaName;
    private String roleName;
}
