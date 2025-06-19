<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SimplePG - 결제 시스템 테스트 대시보드</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 0;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 40px 20px;
        }
        .header {
            text-align: center;
            color: white;
            margin-bottom: 50px;
        }
        .header h1 {
            font-size: 3.5em;
            font-weight: 300;
            margin: 0;
            text-shadow: 0 2px 4px rgba(0,0,0,0.3);
        }
        .header p {
            font-size: 1.2em;
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        .dashboard {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
            gap: 30px;
            margin-bottom: 50px;
        }
        .card {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            transition: all 0.3s ease;
            text-decoration: none;
            color: inherit;
            display: block;
        }
        .card:hover {
            transform: translateY(-10px);
            box-shadow: 0 20px 40px rgba(0,0,0,0.3);
        }
        .card-icon {
            font-size: 3em;
            margin-bottom: 20px;
            text-align: center;
        }
        .card h2 {
            margin: 0 0 15px 0;
            color: #333;
            font-size: 1.5em;
            font-weight: 600;
        }
        .card p {
            color: #666;
            line-height: 1.6;
            margin: 0 0 20px 0;
        }
        .card-features {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        .card-features li {
            padding: 8px 0;
            color: #555;
            position: relative;
            padding-left: 25px;
        }
        .card-features li:before {
            content: "✓";
            position: absolute;
            left: 0;
            color: #28a745;
            font-weight: bold;
        }
        .status-section {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
            margin-bottom: 30px;
        }
        .status-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 20px;
            margin-top: 20px;
        }
        .status-item {
            text-align: center;
            padding: 20px;
            border-radius: 10px;
            background: #f8f9fa;
        }
        .status-number {
            font-size: 2.5em;
            font-weight: bold;
            color: #667eea;
            margin-bottom: 10px;
        }
        .status-label {
            color: #666;
            font-size: 0.9em;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
        .footer {
            text-align: center;
            color: white;
            opacity: 0.8;
            margin-top: 50px;
        }
        .tech-stack {
            background: white;
            border-radius: 15px;
            padding: 30px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.2);
        }
        .tech-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin-top: 20px;
        }
        .tech-item {
            text-align: center;
            padding: 15px;
            border-radius: 8px;
            background: #f8f9fa;
            border: 1px solid #e9ecef;
        }
        .tech-name {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }
        .tech-desc {
            font-size: 0.8em;
            color: #666;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>💳 SimplePG</h1>
        <p>결제 시스템 테스트 대시보드</p>
    </div>

    <!-- 시스템 상태 -->
    <div class="status-section">
        <h2>📊 시스템 상태</h2>
        <div class="status-grid">
            <div class="status-item">
                <div class="status-number">🟢</div>
                <div class="status-label">서버 상태</div>
            </div>
            <div class="status-item">
                <div class="status-number">8080</div>
                <div class="status-label">포트</div>
            </div>
            <div class="status-item">
                <div class="status-number">Spring</div>
                <div class="status-label">프레임워크</div>
            </div>
            <div class="status-item">
                <div class="status-number">WebClient</div>
                <div class="status-label">HTTP 클라이언트</div>
            </div>
        </div>
    </div>

    <!-- 테스트 카드들 -->
    <div class="dashboard">
        <a href="${pageContext.request.contextPath}/payment-test" class="card">
            <div class="card-icon">💳</div>
            <h2>결제 시스템 테스트</h2>
            <p>결제 요청, 상태 조회, 취소, 완료 기능을 테스트할 수 있습니다.</p>
            <ul class="card-features">
                <li>결제 요청 생성</li>
                <li>결제 상태 조회</li>
                <li>결제 취소 처리</li>
                <li>결제 완료 처리</li>
                <li>최근 결제 내역</li>
            </ul>
        </a>

        <a href="${pageContext.request.contextPath}/webhook-test" class="card">
        <div class="card-icon">🔄</div>
            <h2>웹훅 테스트</h2>
            <p>외부 결제 시스템에서 SimplePG로 웹훅을 보내는 시뮬레이션입니다.</p>
            <ul class="card-features">
                <li>성공 웹훅 전송</li>
                <li>실패 웹훅 전송</li>
                <li>Mock 서버 요청</li>
                <li>웹훅 로그 확인</li>
                <li>실시간 모니터링</li>
            </ul>
        </a>

        <a href="${pageContext.request.contextPath}/api-test" class="card">
        <div class="card-icon">📚</div>
            <h2>API 테스트</h2>
            <p>SimplePG API의 상세한 사용법과 예제를 확인할 수 있습니다.</p>
            <ul class="card-features">
                <li>REST API 명세</li>
                <li>요청/응답 예제</li>
                <li>에러 코드 설명</li>
                <li>인증 방법</li>
                <li>웹훅 가이드</li>
            </ul>
        </a>

        <a href="#" class="card" onclick="alert('모니터링 대시보드는 별도로 제공됩니다.')">
            <div class="card-icon">📈</div>
            <h2>모니터링</h2>
            <p>실시간 결제 처리 현황과 시스템 성능을 모니터링합니다.</p>
            <ul class="card-features">
                <li>실시간 트랜잭션</li>
                <li>성공/실패율</li>
                <li>응답 시간</li>
                <li>시스템 리소스</li>
                <li>알림 설정</li>
            </ul>
        </a>
    </div>

    <!-- 기술 스택 -->
    <div class="tech-stack">
        <h2>🛠️ 기술 스택</h2>
        <div class="tech-grid">
            <div class="tech-item">
                <div class="tech-name">Spring 5.3.37</div>
                <div class="tech-desc">웹 프레임워크</div>
            </div>
            <div class="tech-item">
                <div class="tech-name">WebClient</div>
                <div class="tech-desc">HTTP 클라이언트</div>
            </div>
            <div class="tech-item">
                <div class="tech-name">MyBatis</div>
                <div class="tech-desc">ORM 프레임워크</div>
            </div>
            <div class="tech-item">
                <div class="tech-name">MySQL Database</div>
                <div class="tech-desc">DBMS</div>
            </div>
            <div class="tech-item">
                <div class="tech-name">Gradle</div>
                <div class="tech-desc">빌드 도구</div>
            </div>
            <div class="tech-item">
                <div class="tech-name">JSP</div>
                <div class="tech-desc">뷰 템플릿</div>
            </div>
        </div>
    </div>

    <div class="footer">
        <p>SimplePG 결제 시스템 - 테스트 환경</p>
        <p>© 2025 SimplePG. All rights reserved.</p>
    </div>
</div>

<script>
    // 페이지 로드 시 애니메이션 효과
    window.onload = function() {
        const cards = document.querySelectorAll('.card');
        cards.forEach((card, index) => {
            setTimeout(() => {
                card.style.opacity = '0';
                card.style.transform = 'translateY(20px)';
                card.style.transition = 'all 0.5s ease';

                setTimeout(() => {
                    card.style.opacity = '1';
                    card.style.transform = 'translateY(0)';
                }, 100);
            }, index * 100);
        });
    };

    // 시스템 상태 업데이트 (Mock)
    function updateSystemStatus() {
        const statusNumbers = document.querySelectorAll('.status-number');
        statusNumbers.forEach((status, index) => {
            if (index === 0) {
                // 서버 상태는 항상 온라인
                status.textContent = '🟢';
            }
        });
    }

    // 5초마다 상태 업데이트
    setInterval(updateSystemStatus, 5000);
</script>
</body>
</html>