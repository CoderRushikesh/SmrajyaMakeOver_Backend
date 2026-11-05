package com.Samrajya.MakeOver.Controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Samrajya.MakeOver.Entity.ContactMessage;
import com.Samrajya.MakeOver.Interfaces.ContactMessageRepository;

@RestController
@RequestMapping("/public")
public class PublicMessageController {

    private static final int MAX_WORDS = 50;

    @Autowired private ContactMessageRepository repo;

    @PostMapping("/messages")
    public ResponseEntity<?> send(@RequestBody Map<String, String> body) {
        String name = clean(body.get("name"), 100);
        String phone = clean(body.get("phone"), 20);
        String text = clean(body.get("message"), 600);
        String channel = "WHATSAPP".equalsIgnoreCase(body.get("channel")) ? "WHATSAPP" : "WEBSITE";

        if (name.isEmpty() || text.isEmpty()) {
            return ResponseEntity.badRequest().body("Name and message are required");
        }
        if (text.split("\\s+").length > MAX_WORDS) {
            return ResponseEntity.badRequest().body("Message must be " + MAX_WORDS + " words or fewer");
        }

        ContactMessage m = new ContactMessage();
        m.setName(name);
        m.setPhone(phone);
        m.setMessage(text);
        m.setChannel(channel);
        m.setSeen(false);
        repo.save(m);
        return ResponseEntity.ok(Map.of("status", "saved"));
    }

    private String clean(String v, int max) {
        if (v == null) return "";
        v = v.trim();
        return v.length() > max ? v.substring(0, max) : v;
    }
}
