package com.Samrajya.MakeOver.Interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Samrajya.MakeOver.Entity.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findAllByOrderByIdDesc();
    long countBySeenFalse();
    long countByChannel(String channel);
}
