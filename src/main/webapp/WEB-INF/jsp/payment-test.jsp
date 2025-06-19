<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
    <meta charset="UTF-8">
    <title>SimplePG - 결제 시스템 테스트</title>
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
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 2.5em;
            font-weight: 300;
        }
        .header p {
            margin: 10px 0 0 0;
            opacity: 0.9;
        }
        .content {
            padding: 30px;
        }
        .test-section {
            margin-bottom: 40px;
            padding: 25px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background: #fafafa;
        }
        .test-section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #667eea;
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
        .form-group input, .form-group select, .form-group textarea {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            box-sizing: border-box;
        }
        .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 5px rgba(102, 126, 234, 0.3);
        }
        .btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        .btn-secondary {
            background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
        }
        .btn-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
        }
        .btn-success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
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
        .result-info {
            background: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
        }
        .grid {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
        }
        .grid > div {
            flex: 1;
            min-width: 300px;
        }
        .status-badge {
            display: inline-block;
            padding: 4px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            text-transform: uppercase;
        }
        .status-ready { background: #ffeaa7; color: #d63031; }
        .status-approved { background: #a8e6cf; color: #2d3436; }
        .status-completed { background: #74b9ff; color: white; }
        .status-canceled { background: #fd79a8; color: white; }
        .status-failed { background: #fab1a0; color: white; }
        .payment-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 5px;
            margin-top: 15px;
        }
        .payment-info h4 {
            margin-top: 0;
            color: #495057;
        }
        .info-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            padding: 5px 0;
            border-bottom: 1px solid #e9ecef;
        }
        .info-label {
            font-weight: 600;
            color: #6c757d;
        }
        .info-value {
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>💳 SimplePG 결제 시스템</h1>
            <p>결제 요청, 상태 조회, 취소, 완료 기능 테스트</p>
        </div>
        
        <div class="content">
            <!-- 결제 요청 테스트 -->
            <div class="test-section">
                <h2>🔵 1. 결제 요청 테스트</h2>
                <form id="paymentForm" onsubmit="return false;">
                    <div class="grid">
                        <div>
                            <div class="form-group">
                                <label for="clientId">가맹점 ID (Client ID)</label>
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
                        </div>
                        <div>
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
                        </div>
                    </div>
                    <button type="button" class="btn" onclick="submitPayment()">결제 요청</button>
                </form>
                <div id="paymentResult" class="result-area" style="display: none;"></div>
            </div>

            <!-- 결제 상태 조회 테스트 -->
            <div class="test-section">
                <h2>🔍 2. 결제 상태 조회 테스트</h2>
                <div class="form-group">
                    <label for="statusPaymentKey">Payment Key</label>
                    <input type="text" id="statusPaymentKey" placeholder="조회할 Payment Key를 입력하세요">
                </div>
                <button type="button" class="btn btn-secondary" onclick="getPaymentStatus()">상태 조회</button>
                <div id="statusResult" class="result-area" style="display: none;"></div>
            </div>

            <!-- 결제 취소 테스트 -->
            <div class="test-section">
                <h2>❌ 3. 결제 취소 테스트</h2>
                <div class="form-group">
                    <label for="cancelPaymentKey">Payment Key</label>
                    <input type="text" id="cancelPaymentKey" placeholder="취소할 Payment Key를 입력하세요">
                </div>
                <div class="form-group">
                    <label for="cancelReason">취소 사유 (선택사항)</label>
                    <textarea id="cancelReason" rows="3" placeholder="취소 사유를 입력하세요"></textarea>
                </div>
                <button type="button" class="btn btn-danger" onclick="cancelPayment()">결제 취소</button>
                <div id="cancelResult" class="result-area" style="display: none;"></div>
            </div>

            <!-- 결제 완료 테스트 -->
            <div class="test-section">
                <h2>✅ 4. 결제 완료 테스트</h2>
                <div class="form-group">
                    <label for="completePaymentKey">Payment Key</label>
                    <input type="text" id="completePaymentKey" placeholder="완료할 Payment Key를 입력하세요">
                </div>
                <button type="button" class="btn btn-success" onclick="completePayment()">결제 완료</button>
                <div id="completeResult" class="result-area" style="display: none;"></div>
            </div>

            <!-- 최근 결제 내역 -->
            <div class="test-section">
                <h2>📋 5. 최근 결제 내역</h2>
                <button type="button" class="btn btn-secondary" onclick="loadRecentPayments()">최근 결제 내역 조회</button>
                <div id="recentPayments" class="result-area" style="display: none;"></div>
            </div>
        </div>
    </div>

    <script>
        // 페이지 로드 시 자동으로 주문번호 생성
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
                const resultDiv = document.getElementById('paymentResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.textContent = JSON.stringify(data, null, 2);
                
                // Payment Key를 다른 입력 필드에 자동 설정
                if (data.paymentKey) {
                    document.getElementById('statusPaymentKey').value = data.paymentKey;
                    document.getElementById('cancelPaymentKey').value = data.paymentKey;
                    document.getElementById('completePaymentKey').value = data.paymentKey;
                }
            })
            .catch(error => {
                const resultDiv = document.getElementById('paymentResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        // 결제 상태 조회
        function getPaymentStatus() {
            const paymentKey = document.getElementById('statusPaymentKey').value;
            if (!paymentKey) {
                alert('Payment Key를 입력해주세요.');
                return;
            }

            fetch('/api/protected/payment/' + paymentKey, {
                method: 'GET',
                headers: {
                    'X-CLIENT-ID': document.getElementById('paymentForm').clientId,
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature({paymentKey: paymentKey})
                }
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('statusResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-info';
                resultDiv.innerHTML = formatPaymentInfo(data);
            })
            .catch(error => {
                const resultDiv = document.getElementById('statusResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        // 결제 취소
        function cancelPayment() {
            const paymentKey = document.getElementById('cancelPaymentKey').value;
            const cancelReason = document.getElementById('cancelReason').value;
            
            if (!paymentKey) {
                alert('Payment Key를 입력해주세요.');
                return;
            }

            let url = '/api/protected/payment/' + paymentKey + '/cancel';
            if (cancelReason) {
                url += '?cancelReason=' + encodeURIComponent(cancelReason);
            }

            fetch(url, {
                method: 'POST',
                headers: {
                    'X-CLIENT-ID': document.getElementById('paymentForm').clientId,
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature({paymentKey: paymentKey, cancelReason: cancelReason})
                }
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('cancelResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.innerHTML = formatPaymentInfo(data);
            })
            .catch(error => {
                const resultDiv = document.getElementById('cancelResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        // 결제 완료
        function completePayment() {
            const paymentKey = document.getElementById('completePaymentKey').value;
            
            if (!paymentKey) {
                alert('Payment Key를 입력해주세요.');
                return;
            }

            fetch('/api/protected/payment/' + paymentKey + '/complete', {
                method: 'POST',
                headers: {
                    'X-CLIENT-ID': document.getElementById('paymentForm').clientId,
                    'X-TIMESTAMP': new Date().toISOString(),
                    'X-SIGNATURE': generateSignature({paymentKey: paymentKey})
                }
            })
            .then(response => response.json())
            .then(data => {
                const resultDiv = document.getElementById('completeResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.innerHTML = formatPaymentInfo(data);
            })
            .catch(error => {
                const resultDiv = document.getElementById('completeResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Error: ' + error.message;
            });
        }

        // 최근 결제 내역 조회 (Mock 데이터)
        function loadRecentPayments() {
            const resultDiv = document.getElementById('recentPayments');
            resultDiv.style.display = 'block';
            resultDiv.className = 'result-area result-info';
            resultDiv.innerHTML = 
                '<h4>📊 최근 결제 내역 (Mock 데이터)</h4>' +
                '<div class="payment-info">' +
                '<div class="info-row">' +
                '<span class="info-label">Payment Key:</span>' +
                '<span class="info-value">pk_test_1234567890abcdef</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">상태:</span>' +
                '<span class="info-value"><span class="status-badge status-approved">APPROVED</span></span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">금액:</span>' +
                '<span class="info-value">15,000원</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">결제수단:</span>' +
                '<span class="info-value">신용카드</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">생성일시:</span>' +
                '<span class="info-value">2024-01-15 14:30:25</span>' +
                '</div>' +
                '</div>' +
                '<div class="payment-info" style="margin-top: 15px;">' +
                '<div class="info-row">' +
                '<span class="info-label">Payment Key:</span>' +
                '<span class="info-value">pk_test_0987654321fedcba</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">상태:</span>' +
                '<span class="info-value"><span class="status-badge status-completed">COMPLETED</span></span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">금액:</span>' +
                '<span class="info-value">25,000원</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">결제수단:</span>' +
                '<span class="info-value">계좌이체</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">생성일시:</span>' +
                '<span class="info-value">2024-01-15 13:15:10</span>' +
                '</div>' +
                '</div>';
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

        // 결제 정보 포맷팅
        function formatPaymentInfo(data) {
            const statusClass = getStatusClass(data.status);
            let approvedAtHtml = '';
            if (data.approvedAt) {
                approvedAtHtml = '<div class="info-row">' +
                    '<span class="info-label">승인일시:</span>' +
                    '<span class="info-value">' + data.approvedAt + '</span>' +
                    '</div>';
            }
            
            return '<div class="payment-info">' +
                '<h4>💳 결제 정보</h4>' +
                '<div class="info-row">' +
                '<span class="info-label">Payment Key:</span>' +
                '<span class="info-value">' + (data.paymentKey || 'N/A') + '</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">상태:</span>' +
                '<span class="info-value"><span class="status-badge ' + statusClass + '">' + (data.status || 'N/A') + '</span></span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">주문번호:</span>' +
                '<span class="info-value">' + (data.orderNo || 'N/A') + '</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">금액:</span>' +
                '<span class="info-value">' + (data.amount ? data.amount + '원' : 'N/A') + '</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">결제수단:</span>' +
                '<span class="info-value">' + (data.methodCode || 'N/A') + '</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">고객명:</span>' +
                '<span class="info-value">' + (data.customerName || 'N/A') + '</span>' +
                '</div>' +
                '<div class="info-row">' +
                '<span class="info-label">생성일시:</span>' +
                '<span class="info-value">' + (data.createdAt || 'N/A') + '</span>' +
                '</div>' +
                approvedAtHtml +
                '</div>';
        }

        // 상태별 CSS 클래스 반환
        function getStatusClass(status) {
            switch(status) {
                case 'READY': return 'status-ready';
                case 'APPROVED': return 'status-approved';
                case 'COMPLETED': return 'status-completed';
                case 'CANCELED': return 'status-canceled';
                case 'FAILED': return 'status-failed';
                default: return 'status-ready';
            }
        }
    </script>
</body>
</html> 