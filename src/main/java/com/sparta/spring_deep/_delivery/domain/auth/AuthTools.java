package com.sparta.spring_deep._delivery.domain.auth;

import com.sparta.spring_deep._delivery.domain.restaurant.Restaurant;
import com.sparta.spring_deep._delivery.domain.user.details.UserDetailsImpl;
import com.sparta.spring_deep._delivery.domain.user.entity.UserRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AuthTools {

    public static void roleCheck(UserDetailsImpl userDetails, Restaurant restaurant,
        String msg) {

        if (userDetails.getUser().getRole().equals(UserRole.OWNER)) {
            ownerCheck(userDetails, restaurant, msg);
        } else if (userDetails.getUser().getRole().equals(UserRole.MANAGER)) {
//            ManagerCheck(userDetails, restaurant, msg);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "현재 매니저가 없습니다.");
        }

    }

    // 오너 이름 일치 검사
    public static void ownerCheck(UserDetailsImpl userDetails, Restaurant restaurant, String msg) {

        if (!restaurant.getOwner().getUsername().equals(userDetails.getUser().getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "(" + msg + ") 오너가 일치하지 않습니다. 요청된 Owner : (" + userDetails.getUser().getUsername()
                    + ") 등록된 Owner : (" + restaurant.getOwner().getUsername() + ")");
        }

    }

    // 매니저 이름 일치 검사.
    // 현재 레스토랑에 매니저를 사용하지 않음
//    public static void managerCheck(UserDetailsImpl userDetails, Restaurant restaurant,
//        String msg) {
//
//        if (!restaurant.getManager().getUsername().equals(userDetails.getUser().getUsername())) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
//                "(" + msg + ") 매니저가 일치하지 않습니다. 요청된 Manager : (" + userDetails.getUser()
//                    .getUsername()
//                    + ") 등록된 Manager : (" + restaurant.getManager().getUsername() + ")");
//        }
//    }

}
