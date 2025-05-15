# UniLife
### 2025.05.15 업데이트
- 메뉴 다섯가지 중 일정, 시간표, 포트폴리오 연결
- 친구, 학점계산기는 도저히 연결 불가 (파라미터 전달 문제)
- 헤더 html을 따로 나눠 놓음
- tailwind를 쓴 html과 아닌 html이 있는데 디자인 충돌로 인해 안 쓴 html에 tailwind를 추가하지는 않았음
- 대신 header.html을 tailwind를 쓴 버전과 아닌 버전으로 나눔
- 로그인 기능은 Spring Security로 처리
- 로그인 성공 시 사용자의 id(users.id)를 세션에 저장 -> 전역적으로 사용자 정보 활용 가능
- @ControllerAdvice(GlobalControllerAdvice.java 파일)을 통해 사용자 정보를 모든 페이지에서 공통 전달
- 기존 컨트롤러에서 사용자 정보 전달하는 부분은 지우지 않음(@ControllerAdvice와 같이 사용해도 충돌이 일어나지 않는다 하여)
