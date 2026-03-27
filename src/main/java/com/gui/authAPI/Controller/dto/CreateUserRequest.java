package com.gui.authAPI.Controller.dto;

public record CreateUserRequest (String userName, String password, String role ) {
}
