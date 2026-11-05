package com.Samrajya.MakeOver.Controller;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.Samrajya.MakeOver.Entity.ContactMessage;
import com.Samrajya.MakeOver.Entity.User;
import com.Samrajya.MakeOver.Interfaces.ContactMessageRepository;
import com.Samrajya.MakeOver.Interfaces.UserRepository;

/** All endpoints here require ROLE_ADMIN (see SecurityConfig). */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserRepository users;
    @Autowired private ContactMessageRepository messages;

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        List<User> all = users.findAll();
        // createdAt is stored in UTC; bucket registrations by Indian calendar days
        ZoneId ist = ZoneId.of("Asia/Kolkata");
        LocalDate today = LocalDate.now(ist);

        // registrations per day for the last 7 days (oldest -> newest)
        Map<LocalDate, Long> perDay = new TreeMap<>();
        for (int i = 6; i >= 0; i--) perDay.put(today.minusDays(i), 0L);
        for (User u : all) {
            if (u.getCreatedAt() != null) {
                LocalDate d = u.getCreatedAt().atOffset(ZoneOffset.UTC).atZoneSameInstant(ist).toLocalDate();
                if (perDay.containsKey(d)) perDay.merge(d, 1L, Long::sum);
            }
        }
        List<Map<String, Object>> daily = new ArrayList<>();
        perDay.forEach((d, c) -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("date", d.toString());
            row.put("count", c);
            daily.add(row);
        });

        Map<String, Long> byCity = all.stream()
                .filter(u -> u.getCity() != null && !u.getCity().isBlank())
                .collect(Collectors.groupingBy(u -> u.getCity().trim(), Collectors.counting()));
        List<Map<String, Object>> topCities = byCity.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(e -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("city", e.getKey());
                    row.put("count", e.getValue());
                    return row;
                }).collect(Collectors.toList());

        long newThisWeek = daily.stream().mapToLong(r -> (Long) r.get("count")).sum();

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalUsers", (long) all.size());
        out.put("newUsersLast7Days", newThisWeek);
        out.put("totalMessages", messages.count());
        out.put("unreadMessages", messages.countBySeenFalse());
        out.put("whatsappLeads", messages.countByChannel("WHATSAPP"));
        out.put("registrationsByDay", daily);
        out.put("topCities", topCities);
        return out;
    }

    @GetMapping("/users")
    public List<Map<String, Object>> listUsers() {
        return users.findAll(Sort.by(Sort.Direction.DESC, "id")).stream().map(u -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", u.getId());
            row.put("name", u.getName());
            row.put("email", u.getEmail());
            row.put("mobile", u.getMobile());
            row.put("city", u.getCity());
            row.put("role", u.getRole() == null ? "USER" : u.getRole());
            row.put("createdAt", u.getCreatedAt());
            return row; // password is never exposed
        }).collect(Collectors.toList());
    }

    @GetMapping("/messages")
    public List<ContactMessage> listMessages() {
        return messages.findAllByOrderByIdDesc();
    }

    @PatchMapping("/messages/{id}/seen")
    public ResponseEntity<?> markSeen(@PathVariable Long id) {
        return messages.findById(id).map(m -> {
            m.setSeen(true);
            messages.save(m);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/messages/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        if (!messages.existsById(id)) return ResponseEntity.notFound().build();
        messages.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
