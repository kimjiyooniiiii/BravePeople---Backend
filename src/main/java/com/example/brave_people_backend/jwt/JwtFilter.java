package com.example.brave_people_backend.jwt;

import com.example.brave_people_backend.entity.Member;
import com.example.brave_people_backend.exception.CustomException;
import com.example.brave_people_backend.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;

    // 토큰 인증은 이 필터링 로직을 통한다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 사용자가 입력한 정보에서 token만 추출
        String jwt = resolveToken(request);

        // 토큰의 유효성을 검사하고, 권한 정보로 객체를 생성해 SecurityContext에 저장
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            Member member = memberRepository.findById(Long.parseLong(authentication.getName()))
                            .orElseThrow(() -> new CustomException("존재하지 않는 멤버ID"));

            if(member.getRefreshToken() == null) {
                throw new InsufficientAuthenticationException("잘못된 인증입니다.");
            }else{
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // 다음 Filter 실행
        filterChain.doFilter(request, response);
    }

    // 사용자가 입력한 Header에 Bearer Token이 있으면 Token만 잘라서 return
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
