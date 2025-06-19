<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
    <meta charset="UTF-8">
    <title>SimplePG - API 테스트</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        .header {
            background: linear-gradient(135deg, #20c997 0%, #17a2b8 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .content {
            padding: 30px;
        }
        .test-section {
            margin-bottom: 30px;
            padding: 25px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background: #fafafa;
        }
        .test-section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #20c997;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 15px;
        }
        .form-group label {
            display: block;
            margin-bottom: 5px;
            font-weight: 600;
            color: #555;
        }
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            box-sizing: border-box;
        }
        .btn {
            background: linear-gradient(135deg, #20c997 0%, #17a2b8 100%);
            color: white;
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            margin-right: 10px;
        }
        .result-area {
            margin-top: 20px;
            padding: 15px;
            border-radius: 5px;
            font-family: 'Courier New', monospace;
            font-size: 13px;
            white-space: pre-wrap;
            max-height: 300px;
            overflow-y: auto;
        }
        .result-success {
            background: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        .result-error {
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>SimplePG API 테스트</h1>
            <p>REST API 엔드포인트를 직접 테스트할 수 있습니다</p>
        </div>
        
        <div class="content">
            <div class="test-section">
                <h2>빠른 테스트</h2>
                
                <div class="grid">
                    <div>
                        <h3>결제 요청</h3>
                        <div class="form-group">
                            <label for="quickOrderNo">주문번호</label>
                            <input type="text" id="quickOrderNo" value="ORDER_<%= System.currentTimeMillis() %>">
                        </div>
                        <div class="form-group">
                            <label for="quickAmount">금액</label>
                            <input type="text" id="quickAmount" value="15000">
                        </div>
                        <button type="button" class="btn" onclick="quickPaymentRequest()">결제 요청</button>
                    </div>
                    
                    <div>
                        <h3>결제 상태 조회</h3>
                        <div class="form-group">
                            <label for="quickPaymentKey">Payment Key</label>
                            <input type="text" id="quickPaymentKey" placeholder="조회할 Payment Key">
                        </div>
                        <button type="button" class="btn" onclick="quickStatusCheck()">상태 조회</button>
                    </div>
                </div>
                
                <div id="quickResult" class="result-area" style="display: none;"></div>
            </div>

            <div class="test-section">
                <h2>JSON 요청 테스트</h2>
                
                <div class="form-group">
                    <label for="requestBody">요청 본문 (JSON)</label>
                    <textarea id="requestBody" rows="10">{
  "clientId": "test_client_001",
  "orderNo": "ORDER_123456789",
  "amount": "10000",
  "methodCode": "CARD",
  "productName": "테스트 상품",
  "customerName": "테스트 고객"
}</textarea>
                </div>

                <button type="button" class="btn" onclick="sendJsonRequest()">JSON 요청 전송</button>
                <button type="button" class="btn" onclick="loadExample()">예제 로드</button>
                
                <div id="jsonResult" class="result-area" style="display: none;"></div>
            </div>
        </div>
    </div>

    <script>

        function quickPaymentRequest() {
            const orderNo = document.getElementById('quickOrderNo').value;
            const amount = document.getElementById('quickAmount').value;
            
            const requestData = {
                clientId: "test_client_001",
                orderNo: orderNo,
                amount: amount,
                methodCode: "CARD",
                productName: "빠른 테스트 상품",
                customerName: "빠른 테스트 고객"
            };

            fetch('/api/protected/payment', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CLIENT-ID': 'test_client_001',
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature('POST/api/protected/payment' + JSON.stringify(requestData) + new Date().toISOString())
                },
                body: JSON.stringify(requestData)
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('quickResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.textContent = JSON.stringify(data, null, 2);
                
                if (data.paymentKey) {
                    document.getElementById('quickPaymentKey').value = data.paymentKey;
                }
            })
            .catch(error => {
                const resultDiv = document.getElementById('quickResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        function quickStatusCheck() {
            const paymentKey = document.getElementById('quickPaymentKey').value;
            if (!paymentKey) {
                alert('Payment Key를 입력해주세요.');
                return;
            }

            fetch(`/api/protected/payment/${'${paymentKey}'}`, {
                method: 'GET',
                headers: {
                    'X-CLIENT-ID': 'test_client_001',
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature('GET/api/protected/payment/' + paymentKey + new Date().toISOString())
                }
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('quickResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.textContent = JSON.stringify(data, null, 2);
            })
            .catch(error => {
                const resultDiv = document.getElementById('quickResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        function sendJsonRequest() {
            const body = document.getElementById('requestBody').value;
            
            try {
                const requestData = JSON.parse(body);
                
                fetch('/api/protected/payment', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CLIENT-ID': 'test_client_001',
                        'X-TIMESTAMP': new Date().toISOString(),
                        'X-SIGNATURE': generateSignature('POST/api/protected/payment' + body + new Date().toISOString())
                    },
                    body: body
                })
                .then(response => response.json())
                .then(data => {
                    const resultDiv = document.getElementById('jsonResult');
                    resultDiv.style.display = 'block';
                    resultDiv.className = 'result-area result-success';
                    resultDiv.textContent = JSON.stringify(data, null, 2);
                })
                .catch(error => {
                    const resultDiv = document.getElementById('jsonResult');
                    resultDiv.style.display = 'block';
                    resultDiv.className = 'result-area result-error';
                    resultDiv.textContent = 'Error: ' + error.message;
                });
            } catch (e) {
                alert('JSON 형식이 올바르지 않습니다.');
            }
        }

        function loadExample() {
            document.getElementById('requestBody').value = `{
  "clientId": "test_client_001",
  "orderNo": "ORDER_${Date.now()}",
  "amount": "10000",
  "methodCode": "CARD",
  "productName": "테스트 상품",
  "customerName": "테스트 고객"
}`;
        }

        // HMAC 서명 생성 (Mock)
        function generateSignature(data, method = 'POST', path = '/api/protected/payment') {
            const clientSecret = data.clientId || 'test_client_001';
            const body = JSON.stringify(data);
            const timestamp = Date.now();

            const dataToSign = method + path + body + timestamp;
            const signature = CryptoJS.HmacSHA256(dataToSign, clientSecret).toString(CryptoJS.enc.Base64);

            return signature; // 문자열만 반환
        }

    </script>
</body>
</html> 