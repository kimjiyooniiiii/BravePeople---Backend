package com.example.brave_people_backend.contact.controller;

import com.example.brave_people_backend.contact.dto.ContactStatusResponseDto;
import com.example.brave_people_backend.contact.dto.ReviewRequestDto;
import com.example.brave_people_backend.contact.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @GetMapping("/{roomId}")
    public ContactStatusResponseDto acceptContact(@PathVariable("roomId") Long roomId) {
        return contactService.acceptContact(roomId);
    }

    @GetMapping("/{roomId}/cancel")
    public ContactStatusResponseDto cancelContact(@PathVariable("roomId") Long roomId) {
        return contactService.cancelContact(roomId);
    }

    @GetMapping("/{roomId}/finish")
    public ContactStatusResponseDto finishContact(@PathVariable("roomId") Long roomId) {
        return contactService.finishContact(roomId);
    }

    @PostMapping("/{roomId}/review")
    public void reviewContact(@PathVariable("roomId") Long roomId,
                              @RequestBody @Valid ReviewRequestDto reviewRequestDto) {
        contactService.reviewContact(roomId, reviewRequestDto);
    }

    @GetMapping("/{roomId}/status")
    public ContactStatusResponseDto getContactStatus(@PathVariable("roomId") Long roomId) {
        return contactService.getContactStatus(roomId);
    }
}
