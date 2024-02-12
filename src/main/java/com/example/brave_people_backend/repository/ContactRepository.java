package com.example.brave_people_backend.repository;

import com.example.brave_people_backend.entity.Contact;
import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.enumclass.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    //두 사람 사이에 의뢰가 있는지 조회
    boolean existsByClientAndHelper(@Param("client")Member client, @Param("helper")Member helper);

    //두 사람 사이의 의뢰 중 contactStatus가 진행중인 의뢰가 있는지 조회
    boolean existsByContactStatusAndClientAndHelper(@Param("contactStatus") ContactStatus contactStatus,
                                                    @Param("client")Member client,
                                                    @Param("helper")Member helper);

}
