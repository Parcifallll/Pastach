package com.example.Pastach.dto.mapper;

import com.example.Pastach.dto.user.UserCreateDTO;
import com.example.Pastach.dto.user.UserResponseDTO;
import com.example.Pastach.dto.user.UserUpdateDTO;
import com.example.Pastach.model.Role;
import com.example.Pastach.model.RoleEnum;
import com.example.Pastach.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// MapStruct automatically create realization on compiling
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // other fields will be mapped automatically (do not require default value + exist in DTO with the same name)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    // service: default
    User toEntity(UserCreateDTO userCreateDTO);

    @Mapping(target = "username", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "locked", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void updateFromDto(UserUpdateDTO userUpdateDTO, @MappingTarget User user);


    @Mapping(target = "roles", expression = "java(mapRoleNames(user.getRoles()))")
    UserResponseDTO toResponseDto(User user);

    default Set<String> mapRoleNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(role -> role.getName().toString())  // RoleEnum -> String
                .collect(Collectors.toSet());
    }
}