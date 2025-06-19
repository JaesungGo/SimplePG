<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>SimplePG - 결제 시스템 테스트 (간단 버전)</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }
        .form-group input, .form-group select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            box-sizing: border-box;
        }
        .btn {
            background: #007bff;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        .btn:hover {
            background: #0056b3;
        }
        .result {
            margin-top: 20px;
            padding: 15px;
            border-radius: 4px;
            background: #f8f9fa;
            border: 1px solid #dee2e6;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>💳 SimplePG 결제 시스템 테스트</h1>
        <p>간단한 결제 요청 테스트</p>
        
        <form id="paymentForm">
            <div class="form-group">
                <label for="clientId">가맹점 ID</label>
                <input type="text" id="clientId" name="clientId" value="test_client_001" required>
            </div>
            <div class="form-group">
                <label for="orderNo">주문 번호</label>
                <input type="text" id="orderNo" name="orderNo" value="ORDER_" required>
            </div>
            <div class="form-group">
                <label for="amount">결제 금액</label>
                <input type="text" id="amount" name="amount" value="10000" required>
            </div>
            <div class="form-group">
                <label for="methodCode">결제 수단</label>
                <select id="methodCode" name="methodCode" required>
                    <option value="CARD">신용카드</option>
                    <option value="BANK">계좌이체</option>
                    <option value="MOBILE">휴대폰결제</option>
                </select>
            </div>
            <div class="form-group">
                <label for="productName">상품명</label>
                <input type="text" id="productName" name="productName" value="테스트 상품">
            </div>
            <div class="form-group">
                <label for="customerName">고객명</label>
                <input type="text" id="customerName" name="customerName" value="테스트 고객">
            </div>
            <button type="button" class="btn" onclick="submitPayment()">결제 요청</button>
        </form>
        
        <div id="result" class="result" style="display: none;"></div>
    </div>

    <script>
        // 페이지 로드 시 주문번호 생성
        window.onload = function() {
            document.getElementById('orderNo').value = 'ORDER_' + Date.now();
        };

        // 결제 요청
        function submitPayment() {
            const formData = new FormData(document.getElementById('paymentForm'));
            const paymentData = {
                clientId: formData.get('clientId'),
                orderNo: formData.get('orderNo'),
                amount: formData.get('amount'),
                methodCode: formData.get('methodCode'),
                productName: formData.get('productName'),
                customerName: formData.get('customerName')
            };

            fetch('/api/protected/payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CLIENT-ID': paymentData.clientId,
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature(paymentData)
                },
                body: JSON.stringify(paymentData)
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('result');
                resultDiv.style.display = 'block';
                resultDiv.textContent = JSON.stringify(data, null, 2);
            })
            .catch(error => {
                const resultDiv = document.getElementById('result');
                resultDiv.style.display = 'block';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        // HMAC 서명 생성 (Mock)
        function generateSignature(data) {
            const dataString = JSON.stringify(data);
            return btoa(dataString + '_' + Date.now()).replace(/[^a-zA-Z0-9]/g, '');
        }
    </script>
</body>
</html> 