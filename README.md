## 🛠️ Artive Auth Backend - 작업 목록 (Progress Tracker)

### ✅ 완료된 작업
- [x] 회원가입 API (`/auth/signup`)
  - 썸네일 이미지 업로드 (S3 연동)
  - 이메일 인증 여부 확인 포함
- [x] 로그인 API (`/auth/login`)
  - JWT 발급
  - 이메일 인증된 사용자만 가능
- [x] 이메일 인증 기능
  - 인증 토큰 발송
  - 인증 링크 클릭 시 `isVerified` true 처리
- [x] 프로필 조회 API (`/auth/me`)
  - JWT 인증 사용자 정보 조회
- [x] S3 이미지 업로드 기능 (`/upload`)
  - Swagger에서 테스트 완료 (200 OK)

### 🔧 진행 중인 작업
- [ ] 프로필 수정 API (`/auth/edit-profile`)
  - 이름, 닉네임, 썸네일, bio 수정
- [ ] Swagger 문서 정비
  - 응답 예시 및 설명 추가

### ⏳ 예정된 작업
- [ ] 프론트엔드 폼 연동 (React 또는 기타)
  - 회원가입/로그인/프로필수정 테스트
- [ ] 관리자 인증 (ROLE_ADMIN)
- [ ] 비밀번호 암호화/변경 API
- [ ] 소셜 로그인 (카카오 연동 예정)
- [ ] 금융인증서 연동 검토

---

## 📝 주의사항
- `application.yml`에 AWS 및 구글 OAuth client ID/secret 입력 필수
- 이메일 인증을 위해 `Google App Password` 필요
- 썸네일 저장 경로: `user-thumbnail/` (S3 버킷 내 디렉토리)

