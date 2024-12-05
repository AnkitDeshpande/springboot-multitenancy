package com.springsecurity.springsecurity.dto;

import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthRequestDto {

	private String username;

	private String password;

}
