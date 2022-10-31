package com.argusoft.who.emcare.web.commonapi.dao;

import com.argusoft.who.emcare.web.commonapi.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OTPRepository extends JpaRepository<OTP, Long> {

    @Modifying
    void deleteByEmailId(String emailId);

    @Query(value = "select * from otp where email_id = :emailId and verified is false and expiry > now() ;", nativeQuery = true)
    Optional<OTP> retrievOTP(@Param("emailId") String emailId);

    @Modifying
    @Query(value = "update otp set count = :count where email_id = :emailId ;", nativeQuery = true)
    void updateOTPCount(@Param("count") Integer count, @Param("emailId") String emailId);

    @Modifying
    @Query(value = "update otp set verified = true where email_id = :emailId and otp = :otp ;", nativeQuery = true)
    void invalidateOtp(@Param("otp") String otp, @Param("emailId") String emailId);

}
