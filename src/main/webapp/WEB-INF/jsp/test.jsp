<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>JSP 테스트</title>
</head>
<body>
    <h1>JSP 테스트 페이지</h1>
    <p>현재 시간: <%= new java.util.Date() %></p>
    <p>JSP 뷰 리졸버가 정상적으로 작동하고 있습니다!</p>
    
    <h2>다른 페이지로 이동:</h2>
    <ul>
        <li><a href="/">메인 페이지</a></li>
        <li><a href="/payment-test">결제 테스트</a></li>
        <li><a href="/webhook-test">웹훅 테스트</a></li>
        <li><a href="/api-test">API 테스트</a></li>
    </ul>
</body>
</html> 