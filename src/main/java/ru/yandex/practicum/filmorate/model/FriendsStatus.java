package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendsStatus {
   private long firstUserId;
   private long secondUserId;
    boolean status;
}
