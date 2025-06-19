<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.1.1/crypto-js.min.js"></script>
    <meta charset="UTF-8">
    <title>SimplePG - 웹훅 테스트</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .header {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
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
            margin-bottom: 30px;
            padding: 25px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background: #fafafa;
        }

        .test-section h2 {
            margin-top: 0;
            color: #333;
            border-bottom: 2px solid #ff6b6b;
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
            border-color: #ff6b6b;
            box-shadow: 0 0 5px rgba(255, 107, 107, 0.3);
        }

        .btn {
            background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
            color: white;
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
            transition: all 0.3s ease;
            margin-right: 10px;
        }

        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(255, 107, 107, 0.4);
        }

        .btn-success {
            background: linear-gradient(135deg, #28a745 0%, #20c997 100%);
        }

        .btn-danger {
            background: linear-gradient(135deg, #dc3545 0%, #c82333 100%);
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
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;
        }

        .webhook-flow {
            background: #e8f4fd;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .flow-step {
            display: flex;
            align-items: center;
            margin-bottom: 15px;
            padding: 10px;
            background: white;
            border-radius: 5px;
            border-left: 4px solid #ff6b6b;
        }

        .flow-step:last-child {
            margin-bottom: 0;
        }

        .step-number {
            background: #ff6b6b;
            color: white;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            margin-right: 15px;
        }

        .step-content {
            flex: 1;
        }

        .step-title {
            font-weight: 600;
            color: #333;
            margin-bottom: 5px;
        }

        .step-desc {
            color: #666;
            font-size: 14px;
        }

        .arrow {
            color: #ff6b6b;
            font-size: 20px;
            margin: 0 10px;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>🔄 SimplePG 웹훅 테스트</h1>
        <p>외부 결제 시스템 → SimplePG 웹훅 시뮬레이션</p>
    </div>

    <div class="content">
        <!-- 웹훅 플로우 설명 -->
        <div class="webhook-flow">
            <h3>📋 웹훅 처리 플로우</h3>
            <div class="flow-step">
                <div class="step-number">1</div>
                <div class="step-content">
                    <div class="step-title">결제 요청 생성</div>
                    <div class="step-desc">SimplePG에서 결제 요청을 생성하고 Payment Key 발급</div>
                </div>
            </div>
            <div class="flow-step">
                <div class="step-number">2</div>
                <div class="step-content">
                    <div class="step-title">외부 결제 시스템으로 요청 전송</div>
                    <div class="step-desc">WebClient를 통해 외부 결제 시스템으로 비동기 요청</div>
                </div>
            </div>
            <div class="flow-step">
                <div class="step-number">3</div>
                <div class="step-content">
                    <div class="step-title">외부 시스템에서 웹훅 응답</div>
                    <div class="step-desc">결제 처리 완료 후 SimplePG로 웹훅 전송</div>
                </div>
            </div>
            <div class="flow-step">
                <div class="step-number">4</div>
                <div class="step-content">
                    <div class="step-title">SimplePG에서 웹훅 처리</div>
                    <div class="step-desc">결제 상태 업데이트 및 가맹점 서버로 결과 전송</div>
                </div>
            </div>
        </div>

        <!-- 웹훅 시뮬레이션 -->
        <div class="test-section">
            <h2>🎯 웹훅 시뮬레이션</h2>
            <p>외부 결제 시스템에서 SimplePG로 웹훅을 보내는 상황을 시뮬레이션합니다.</p>

            <div class="grid">
                <div>
                    <div class="form-group">
                        <label for="webhookPaymentKey">Payment Key</label>
                        <input type="text" id="webhookPaymentKey" placeholder="웹훅을 보낼 Payment Key를 입력하세요">
                    </div>
                    <div class="form-group">
                        <label for="webhookStatus">웹훅 상태</label>
                        <select id="webhookStatus">
                            <option value="success">성공 (APPROVED)</option>
                            <option value="failure">실패 (FAILED)</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label for="transactionId">Transaction ID (자동 생성)</label>
                        <input type="text" id="transactionId" readonly>
                    </div>
                </div>
                <div>
                    <div class="form-group">
                        <label for="approvedAt">승인 시간</label>
                        <input type="text" id="approvedAt" readonly>
                    </div>
                    <div class="form-group">
                        <label for="delayTime">지연 시간 (초)</label>
                        <input type="number" id="delayTime" value="2" min="0" max="10">
                    </div>
                    <div class="form-group">
                        <label for="webhookUrl">웹훅 URL</label>
                        <input type="text" id="webhookUrl" readonly>
                    </div>
                </div>
            </div>

            <button type="button" class="btn btn-success" onclick="sendSuccessWebhook()">성공 웹훅 전송</button>
            <button type="button" class="btn btn-danger" onclick="sendFailureWebhook()">실패 웹훅 전송</button>
            <button type="button" class="btn" onclick="sendMockWebhook()">Mock 서버로 요청</button>

            <div id="webhookResult" class="result-area" style="display: none;"></div>
        </div>

        <!-- 웹훅 로그 -->
        <div class="test-section">
            <h2>📊 웹훅 로그</h2>
            <button type="button" class="btn" onclick="clearWebhookLog()">로그 지우기</button>
            <div id="webhookLog" class="result-area result-info">
                웹훅 로그가 여기에 표시됩니다...
            </div>
        </div>

        <!-- 테스트 시나리오 -->
        <div class="test-section">
            <h2>🧪 테스트 시나리오</h2>
            <div class="grid">
                <div>
                    <h4>시나리오 1: 정상 결제 플로우</h4>
                    <ol>
                        <li>결제 요청 생성 (Payment Key 발급)</li>
                        <li>성공 웹훅 전송</li>
                        <li>결제 상태 확인 (APPROVED)</li>
                        <li>결제 완료 처리</li>
                    </ol>
                </div>
                <div>
                    <h4>시나리오 2: 결제 실패 플로우</h4>
                    <ol>
                        <li>결제 요청 생성 (Payment Key 발급)</li>
                        <li>실패 웹훅 전송</li>
                        <li>결제 상태 확인 (FAILED)</li>
                        <li>재시도 또는 취소</li>
                    </ol>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    // 페이지 로드 시 초기화
    window.onload = function() {
        generateTransactionId();
        updateApprovedAt();
        updateWebhookUrl();

        // Payment Key 입력 시 URL 업데이트
        document.getElementById('webhookPaymentKey').addEventListener('input', updateWebhookUrl);
        document.getElementById('webhookStatus').addEventListener('change', updateWebhookUrl);
    };

    // Transaction ID 생성
    function generateTransactionId() {
        const timestamp = Date.now();
        const random = Math.random().toString(36).substring(2, 15);
        const transactionId = 'txn_' + timestamp + '_' + random;
        console.log('Generated Transaction ID:', transactionId);
        document.getElementById('transactionId').value = transactionId;
    }

    // 승인 시간 업데이트
    function updateApprovedAt() {
        const now = new Date().toISOString();
        document.getElementById('approvedAt').value = now;
    }

    // 웹훅 URL 업데이트
    function updateWebhookUrl() {
        const paymentKey = document.getElementById('webhookPaymentKey').value;
        const status = document.getElementById('webhookStatus').value;

        console.log('PaymentKey:', paymentKey);
        console.log('Status:', status);

        if (paymentKey) {
            const url = '/api/protected/webhook/' + paymentKey + '/' + status;
            console.log('Generated URL:', url);
            document.getElementById('webhookUrl').value = url;
        } else {
            document.getElementById('webhookUrl').value = '';
        }
    }

    // 성공 웹훅 전송
    function sendSuccessWebhook() {
        const paymentKey = document.getElementById('webhookPaymentKey').value;
        if (!paymentKey) {
            alert('Payment Key를 입력해주세요.');
            return;
        }

        const webhookData = {
            transactionId: document.getElementById('transactionId').value,
            paymentStatus: "APPROVED",
            approvedAt: document.getElementById('approvedAt').value
        };

        sendWebhook(paymentKey, 'success', webhookData);
    }

    // 실패 웹훅 전송
    function sendFailureWebhook() {
        const paymentKey = document.getElementById('webhookPaymentKey').value;
        if (!paymentKey) {
            alert('Payment Key를 입력해주세요.');
            return;
        }

        const webhookData = {
            transactionId: document.getElementById('transactionId').value,
            paymentStatus: "FAILED",
            approvedAt: null
        };

        sendWebhook(paymentKey, 'failure', webhookData);
    }

    // 웹훅 전송
    function sendWebhook(paymentKey, status, webhookData) {
        const delayTime = document.getElementById('delayTime').value * 1000;

        addWebhookLog('🔄 웹훅 전송 시작: ' + paymentKey + ' (' + status + ')');

        setTimeout(() => {
            const webhookUrl = '/api/protected/webhook/' + paymentKey + '/' + status;
            console.log('Sending webhook to:', webhookUrl);

            fetch(webhookUrl, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(webhookData)
            })
                .then(response => {
                    if (response.ok) {
                        addWebhookLog('✅ 웹훅 전송 성공: ' + paymentKey + ' (' + status + ')');
                        addWebhookLog('📊 응답 데이터: ' + JSON.stringify(webhookData, null, 2));

                        const resultDiv = document.getElementById('webhookResult');
                        resultDiv.style.display = 'block';
                        resultDiv.className = 'result-area result-success';
                        resultDiv.textContent = '웹훅 전송 성공!\n\nPayment Key: ' + paymentKey + '\nStatus: ' + status + '\nTransaction ID: ' + webhookData.transactionId + '\nApproved At: ' + (webhookData.approvedAt || 'N/A');
                    } else {
                        throw new Error('HTTP ' + response.status + ': ' + response.statusText);
                    }
                })
                .catch(error => {
                    addWebhookLog('❌ 웹훅 전송 실패: ' + paymentKey + ' (' + status + ') - ' + error.message);

                    const resultDiv = document.getElementById('webhookResult');
                    resultDiv.style.display = 'block';
                    resultDiv.className = 'result-area result-error';
                    resultDiv.textContent = '웹훅 전송 실패: ' + error.message;
                });
        }, delayTime);
    }

    // Mock 서버로 요청 전송
    function sendMockWebhook() {
        const paymentKey = document.getElementById('webhookPaymentKey').value;
        if (!paymentKey) {
            alert('Payment Key를 입력해주세요.');
            return;
        }

        const orderNo = 'ORDER_' + Date.now();
        const successUrl = '/api/protected/webhook/' + paymentKey + '/success';
        const failureUrl = '/api/protected/webhook/' + paymentKey + '/failure';

        const mockData = {
            paymentKey: paymentKey,
            amount: "10000",
            orderNo: orderNo,
            customerName: "테스트 고객",
            methodCode: "CARD",
            successUrl: successUrl,
            failureUrl: failureUrl
        };

        addWebhookLog('🔄 Mock 서버로 결제 요청 전송: ' + paymentKey);

        fetch('/mock/request', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(mockData)
        })
            .then(response => response.json())
            .then(data => {
                addWebhookLog('✅ Mock 서버 응답: ' + JSON.stringify(data, null, 2));

                const resultDiv = document.getElementById('webhookResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-success';
                resultDiv.textContent = 'Mock 서버 요청 성공!\n\n' + JSON.stringify(data, null, 2);
            })
            .catch(error => {
                addWebhookLog('❌ Mock 서버 요청 실패: ' + error.message);

                const resultDiv = document.getElementById('webhookResult');
                resultDiv.style.display = 'block';
                resultDiv.className = 'result-area result-error';
                resultDiv.textContent = 'Mock 서버 요청 실패: ' + error.message;
            });
    }

    // 웹훅 로그 추가
    function addWebhookLog(message) {
        const logDiv = document.getElementById('webhookLog');
        const timestamp = new Date().toLocaleTimeString();
        const logEntry = '[' + timestamp + '] ' + message + '\n\n';

        logDiv.textContent += logEntry;
        logDiv.scrollTop = logDiv.scrollHeight;
    }

    // 웹훅 로그 지우기
    function clearWebhookLog() {
        document.getElementById('webhookLog').textContent = '웹훅 로그가 여기에 표시됩니다...\n';
    }

    // 1초마다 승인 시간 업데이트
    setInterval(updateApprovedAt, 1000);
</script>

</body>
</html> 