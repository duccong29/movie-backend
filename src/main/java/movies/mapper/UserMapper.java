package movies.mapper;

import movies.dto.request.user.UserCreationRequest;
import movies.dto.request.user.UserUpdateRequest;
import movies.dto.response.user.UserResponse;
import movies.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);
    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request );
}
