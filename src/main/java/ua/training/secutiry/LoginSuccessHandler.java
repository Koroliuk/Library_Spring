package ua.training.secutiry;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ua.training.model.enums.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String redirectURL = request.getContextPath();
        if (userDetails.isUserBlocked()) {
            redirectURL += "/user/blocked";
        } else {
            if (userDetails.hasRole(Role.READER)) {
                redirectURL += "/reader/home?tab=1&page=1";
            } else if (userDetails.hasRole(Role.LIBRARIAN)) {
                redirectURL += "/librarian/home?tab=1&page=1";
            } else if (userDetails.hasRole(Role.ADMIN)) {
                redirectURL += "/admin/home?tab=1&page=1";
            } else {
                response.sendError(404);
            }
        }
        response.sendRedirect(redirectURL);
    }
}
